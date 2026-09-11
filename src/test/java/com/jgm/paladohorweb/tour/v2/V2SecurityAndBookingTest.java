package com.jgm.paladohorweb.tour.v2;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jgm.paladohorweb.tour.entity.*;
import com.jgm.paladohorweb.tour.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
class V2SecurityAndBookingTest {
  @Autowired NoticeRepository notices;
  @Autowired AuditEntryRepository audit;
  @Autowired PrivacyRequestRepository privacy;
  @Autowired BookingRepository bookings;
  @Autowired SlotRepository slots;
  @Autowired ExperienceRepository experiences;
  @Autowired V2Service s;
  @Autowired UsuarioRepository users;
  @Autowired MockMvc mvc;
  @Autowired org.springframework.security.crypto.password.PasswordEncoder encoder;
  Usuario guide, traveler, other, admin;
  Experience experience;
  Slot slot;

  Usuario create(String name, Rol role) {
    Usuario u = new Usuario();
    u.setNombre(name);
    u.setEmail(name + "@example.test");
    u.setPassword(encoder.encode("Test-password-12345"));
    u.setRol(role);
    u.setActivo(true);
    return users.save(u);
  }

  void as(Usuario u) {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                u.getEmail(), null, List.of(new SimpleGrantedAuthority(u.getRol().name()))));
  }

  Contracts.ExperienceInput input(String kind) {
    return new Contracts.ExperienceInput(
        "Jardín",
        "Experiencia de prueba",
        kind,
        "Antioquia",
        "Jardín",
        "Parque",
        0.0,
        -75.0,
        0.0,
        -75.0,
        "Entrada norte",
        "573001234567",
        "https://example.test/image.jpg",
        "RNT-TEST",
        "Consultar cancelaciones",
        BigDecimal.valueOf(100));
  }

  Contracts.BookingInput request(Slot sl, int people) {
    return new Contracts.BookingInput(
        sl.getId(), people, null, UUID.randomUUID().toString(), true, true, "2026-09-v2");
  }

  @BeforeEach
  void setup() {
    notices.deleteAll();
    audit.deleteAll();
    privacy.deleteAll();
    bookings.deleteAll();
    slots.deleteAll();
    experiences.deleteAll();
    users.deleteAll();
    guide = create("guide", Rol.ROLE_GUIA);
    traveler = create("traveler", Rol.ROLE_USER);
    other = create("other", Rol.ROLE_USER);
    admin = create("admin", Rol.ROLE_ADMIN);
    as(guide);
    experience = s.create(input("TOUR"));
    slot =
        s.addSlot(
            experience.getId(), new Contracts.SlotInput(LocalDate.now().plusDays(3), "09:00", 2));
  }

  @AfterEach
  void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void unauthenticatedRequestsCannotBook() throws Exception {
    SecurityContextHolder.clearContext();
    mvc.perform(post("/api/v2/bookings").contentType("application/json").content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void travelerCannotPublishOrReadProviderBookings() {
    as(traveler);
    assertThrows(ResponseStatusException.class, () -> s.create(input("TOUR")));
    assertThrows(ResponseStatusException.class, () -> s.providerBookings());
  }

  @Test
  void foreignGuideCannotModifyExperience() {
    var second = create("second-guide", Rol.ROLE_GUIA);
    as(second);
    assertThrows(ResponseStatusException.class, () -> s.edit(experience.getId(), input("TOUR")));
  }

  @Test
  void duplicateRequestIsIdempotent() {
    as(traveler);
    var req = request(slot, 1);
    var b = s.book(req);
    assertEquals(b.getId(), s.book(req).getId());
    assertEquals(1, bookings.count());
    assertEquals("PENDING", b.getPaymentStatus());
  }

  @Test
  void onlyOwnerCanConfirmOrVerifyPayment() {
    as(traveler);
    var b = s.book(request(slot, 1));
    as(other);
    assertThrows(ResponseStatusException.class, () -> s.transition(b.getId(), "confirm"));
    assertThrows(ResponseStatusException.class, () -> s.transition(b.getId(), "verify-payment"));
    assertTrue(s.myBookings().isEmpty());
  }

  @Test
  void capacityIsCheckedAtConfirmation() {
    as(traveler);
    var a = s.book(request(slot, 2));
    as(other);
    var b = s.book(request(slot, 1));
    as(guide);
    s.transition(a.getId(), "confirm");
    assertThrows(ResponseStatusException.class, () -> s.transition(b.getId(), "confirm"));
    assertEquals(2, bookings.confirmed(slot.getId()));
  }

  @Test
  void paidCancellationRequiresRefundReview() {
    as(traveler);
    var b = s.book(request(slot, 1));
    as(guide);
    s.transition(b.getId(), "confirm");
    s.transition(b.getId(), "verify-payment");
    as(traveler);
    var canceled = s.transition(b.getId(), "cancel");
    assertEquals("REFUND_REVIEW", canceled.getPaymentStatus());
    assertEquals(0, bookings.confirmed(slot.getId()));
  }

  @Test
  void promotionalNotificationsRespectWithdrawal() {
    as(traveler);
    s.marketing(true);
    as(admin);
    assertEquals(1, s.send(new Contracts.NoticeInput(null, "Promo", "Oferta", true)));
    as(traveler);
    s.marketing(false);
    as(admin);
    assertEquals(0, s.send(new Contracts.NoticeInput(null, "Promo", "Oferta", true)));
  }

  @Test
  void foreignUserCannotMarkNoticeRead() {
    as(traveler);
    s.book(request(slot, 1));
    var n = notices.findTop100ByUserIdOrderByIdDesc(traveler.getId()).get(0);
    as(other);
    assertThrows(ResponseStatusException.class, () -> s.read(n.getId()));
  }

  @Test
  void missingConsentIsRejectedByApi() throws Exception {
    mvc.perform(
            post("/api/v2/bookings")
                .with(user(traveler.getEmail()).roles("USER"))
                .contentType("application/json")
                .content(
                    "{\"slotId\":"
                        + slot.getId()
                        + ",\"people\":1,\"requestKey\":\"1234567890123456\",\"legalVersion\":\"2026-09-v2\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void oldPaymentEndpointIsDisabled() throws Exception {
    mvc.perform(
            post("/api/pagos/crear-intent")
                .with(user(traveler.getEmail()).roles("USER"))
                .contentType("application/json")
                .content("{}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void concurrentConfirmationsCannotOverbook() throws Exception {
    as(traveler);
    var a = s.book(request(slot, 2));
    as(other);
    var b = s.book(request(slot, 2));
    var pool = Executors.newFixedThreadPool(2);
    CountDownLatch start = new CountDownLatch(1);
    try {
      List<Future<Boolean>> futures = new ArrayList<>();
      for (var booking : List.of(a, b))
        futures.add(
            pool.submit(
                () -> {
                  as(guide);
                  start.await();
                  try {
                    s.transition(booking.getId(), "confirm");
                    return true;
                  } catch (ResponseStatusException e) {
                    return false;
                  } finally {
                    SecurityContextHolder.clearContext();
                  }
                }));
      start.countDown();
      int ok = 0;
      for (var f : futures) if (f.get(15, TimeUnit.SECONDS)) ok++;
      assertEquals(1, ok);
      assertEquals(2, bookings.confirmed(slot.getId()));
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void stayChecksEveryNight() {
    as(guide);
    var stay = s.create(input("STAY"));
    var d = LocalDate.now().plusDays(4);
    var first = s.addSlot(stay.getId(), new Contracts.SlotInput(d, "15:00", 2));
    s.addSlot(stay.getId(), new Contracts.SlotInput(d.plusDays(1), "15:00", 1));
    as(traveler);
    var bad =
        new Contracts.BookingInput(
            first.getId(),
            2,
            d.plusDays(2),
            UUID.randomUUID().toString(),
            true,
            true,
            "2026-09-v2");
    assertThrows(ResponseStatusException.class, () -> s.book(bad));
    var good =
        new Contracts.BookingInput(
            first.getId(),
            1,
            d.plusDays(2),
            UUID.randomUUID().toString(),
            true,
            true,
            "2026-09-v2");
    assertEquals(0, BigDecimal.valueOf(200).compareTo(s.book(good).getTotal()));
  }

  @Test
  void adminCannotBeCreatedThroughSignup() throws Exception {
    mvc.perform(
            post("/api/auth/register")
                .contentType("application/json")
                .content(
                    "{\"nombre\":\"Intruso\",\"email\":\"intruso@example.test\",\"password\":\"Long-password-12345\",\"rol\":\"ROLE_ADMIN\",\"acceptedTerms\":true,\"acceptedPrivacy\":true,\"legalVersion\":\"2026-09-v2\"}"))
        .andExpect(status().isBadRequest());
  }
}
