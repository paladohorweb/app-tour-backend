package com.jgm.paladohorweb.tour.v2;

import com.jgm.paladohorweb.tour.entity.*;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
public class V2Service {
  final ExperienceRepository experiences;
  final SlotRepository slots;
  final BookingRepository bookings;
  final NoticeRepository notices;
  final UsuarioRepository users;
  final AuditEntryRepository audit;
  final PrivacyRequestRepository privacy;

  public List<Experience> catalog() {
    return experiences.findByActiveTrueOrderByIdDesc();
  }

  public List<Notice> myNotices() {
    return notices.findTop100ByUserIdOrderByIdDesc(me().getId());
  }

  public List<PrivacyRequest> myPrivacy() {
    return privacy.findByUserIdOrderByIdDesc(me().getId());
  }

  public List<PrivacyRequest> adminPrivacy() {
    if (!admin(me())) throw error(403, "Solo administración");
    return privacy.findAll();
  }

  public void closePrivacy(Long id) {
    var u = me();
    if (!admin(u)) throw error(403, "Solo administración");
    var r = privacy.findById(id).orElseThrow(() -> error(404, "Solicitud no encontrada"));
    r.setStatus("CLOSED");
    log(u, "PRIVACY_CLOSED", id);
  }

  public Usuario me() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null) throw error(401, "Inicia sesión");
    return users
        .findByEmail(a.getName())
        .filter(u -> Boolean.TRUE.equals(u.getActivo()))
        .orElseThrow(() -> error(401, "Sesión inválida"));
  }

  public boolean admin(Usuario u) {
    return u.getRol() == Rol.ROLE_ADMIN;
  }

  void provider(Usuario u) {
    if (!admin(u) && u.getRol() != Rol.ROLE_GUIA) throw error(403, "Solo prestadores");
  }

  void owner(Experience e, Usuario u) {
    if (!admin(u) && !e.getOwnerId().equals(u.getId()))
      throw error(404, "Experiencia no encontrada");
  }

  Experience experience(Long id) {
    return experiences.findById(id).orElseThrow(() -> error(404, "Experiencia no encontrada"));
  }

  public Experience publicExperience(Long id) {
    var e = experience(id);
    if (!e.isActive()) throw error(404, "Experiencia no disponible");
    return e;
  }

  public Experience create(Contracts.ExperienceInput d) {
    var u = me();
    provider(u);
    Experience e = new Experience();
    e.setOwnerId(u.getId());
    copy(e, d);
    experiences.save(e);
    log(u, "EXPERIENCE_CREATED", e.getId());
    return e;
  }

  public Experience edit(Long id, Contracts.ExperienceInput d) {
    var u = me();
    var e = experience(id);
    owner(e, u);
    copy(e, d);
    log(u, "EXPERIENCE_UPDATED", id);
    return e;
  }

  void copy(Experience e, Contracts.ExperienceInput d) {
    e.setTitle(d.title());
    e.setDescription(d.description());
    e.setKind(d.kind());
    e.setDepartment(d.department());
    e.setMunicipality(d.municipality());
    e.setAddress(d.address());
    e.setLatitude(d.latitude());
    e.setLongitude(d.longitude());
    e.setMeetingLatitude(d.meetingLatitude());
    e.setMeetingLongitude(d.meetingLongitude());
    e.setMeetingInstructions(d.meetingInstructions());
    e.setWhatsapp(d.whatsapp());
    e.setImageUrl(d.imageUrl());
    e.setRnt(d.rnt());
    e.setCancellationPolicy(d.cancellationPolicy());
    e.setPrice(d.price());
  }

  public List<Experience> mine() {
    var u = me();
    provider(u);
    return admin(u) ? experiences.findAll() : experiences.findByOwnerIdOrderByIdDesc(u.getId());
  }

  public void active(Long id, boolean active) {
    var u = me();
    var e = experience(id);
    owner(e, u);
    e.setActive(active);
    log(u, "EXPERIENCE_ACTIVE_" + active, id);
  }

  public Slot addSlot(Long id, Contracts.SlotInput d) {
    var u = me();
    owner(experience(id), u);
    if (slots.findByExperienceIdOrderByDateAscTimeAsc(id).stream()
        .anyMatch(
            s ->
                s.getDate().equals(d.date())
                    && (experience(id).getKind().equals("STAY") || s.getTime().equals(d.time()))))
      throw error(409, "Ya existe ese horario");
    Slot s = new Slot();
    s.setExperienceId(id);
    s.setDate(d.date());
    s.setTime(d.time());
    s.setCapacity(d.capacity());
    slots.save(s);
    log(u, "SLOT_CREATED", s.getId());
    return s;
  }

  public List<Map<String, Object>> availability(Long id) {
    publicExperience(id);
    return slots.findByExperienceIdOrderByDateAscTimeAsc(id).stream()
        .filter(s -> !s.getDate().isBefore(LocalDate.now(ZoneId.of("America/Bogota"))))
        .map(
            s -> {
              Map<String, Object> m = new LinkedHashMap<>();
              m.put("id", s.getId());
              m.put("date", s.getDate());
              m.put("time", s.getTime());
              m.put("capacity", s.getCapacity());
              m.put("available", Math.max(0, s.getCapacity() - used(s)));
              return m;
            })
        .toList();
  }

  public Booking book(Contracts.BookingInput d) {
    var u = me();
    var old = bookings.findByRequestKey(u.getId() + ":" + d.requestKey());
    if (old.isPresent()) return old.get();
    var s = slots.lockById(d.slotId()).orElseThrow(() -> error(404, "Horario no encontrado"));
    var e = publicExperience(s.getExperienceId());
    if (s.getDate().isBefore(LocalDate.now(ZoneId.of("America/Bogota"))))
      throw error(400, "La fecha ya pasó");
    if (d.people() > s.getCapacity() - used(s)) throw error(409, "No hay cupos suficientes");
    long units = d.people();
    if (e.getKind().equals("STAY")) {
      if (d.endDate() == null
          || !d.endDate().isAfter(s.getDate())
          || d.endDate().isAfter(s.getDate().plusDays(30)))
        throw error(400, "Selecciona una estancia de 1 a 30 noches");
      units *= java.time.temporal.ChronoUnit.DAYS.between(s.getDate(), d.endDate());
      checkNights(e.getId(), s.getDate(), d.endDate(), d.people(), false);
    } else if (d.endDate() != null) throw error(400, "La fecha de salida solo aplica a hospedajes");
    Booking b = new Booking();
    b.setExperienceId(e.getId());
    b.setSlotId(s.getId());
    b.setUserId(u.getId());
    b.setOwnerId(e.getOwnerId());
    b.setPeople(d.people());
    b.setTotal(e.getPrice().multiply(BigDecimal.valueOf(units)));
    b.setStatus("REQUESTED");
    b.setPaymentStatus("PENDING");
    b.setTitle(e.getTitle());
    b.setReference("TG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    b.setRequestKey(u.getId() + ":" + d.requestKey());
    b.setCreatedAt(Instant.now());
    b.setDate(s.getDate());
    b.setEndDate(d.endDate());
    b.setLegalVersion(d.legalVersion());
    b.setMeetingLatitude(e.getMeetingLatitude());
    b.setMeetingLongitude(e.getMeetingLongitude());
    b.setMeetingInstructions(e.getMeetingInstructions());
    b.setWhatsapp(e.getWhatsapp());
    b.setCancellationSnapshot(e.getCancellationPolicy());
    bookings.save(b);
    notify(
        u.getId(),
        "Solicitud registrada",
        b.getReference() + " pendiente de confirmación del prestador",
        false);
    notify(e.getOwnerId(), "Nueva solicitud", b.getReference() + " — " + e.getTitle(), false);
    log(u, "BOOKING_REQUESTED", b.getId());
    return b;
  }

  long used(Slot s) {
    return experience(s.getExperienceId()).getKind().equals("STAY")
        ? bookings.occupiedNight(s.getExperienceId(), s.getDate())
        : bookings.confirmed(s.getId());
  }

  void checkNights(Long experience, LocalDate start, LocalDate end, int people, boolean lock) {
    var calendar = slots.findByExperienceIdOrderByDateAscTimeAsc(experience);
    for (LocalDate day = start; day.isBefore(end); day = day.plusDays(1)) {
      final LocalDate date = day;
      var found =
          calendar.stream()
              .filter(x -> x.getDate().equals(date))
              .findFirst()
              .orElseThrow(() -> error(409, "Falta disponibilidad para la noche " + date));
      var s = lock ? slots.lockById(found.getId()).orElseThrow() : found;
      if (bookings.occupiedNight(experience, day) + people > s.getCapacity())
        throw error(409, "No hay cupos para la noche " + day);
    }
  }

  public List<Booking> myBookings() {
    return bookings.findByUserIdOrderByIdDesc(me().getId());
  }

  public List<Booking> providerBookings() {
    var u = me();
    provider(u);
    return admin(u) ? bookings.findAll() : bookings.findByOwnerIdOrderByIdDesc(u.getId());
  }

  public Booking transition(Long id, String action) {
    var u = me();
    var b = bookings.findById(id).orElseThrow(() -> error(404, "Reserva no encontrada"));
    var s = slots.lockById(b.getSlotId()).orElseThrow();
    boolean manager = admin(u) || b.getOwnerId().equals(u.getId());
    if (action.equals("cancel")) {
      if (!manager && !b.getUserId().equals(u.getId())) throw error(404, "Reserva no encontrada");
      if (!List.of("REQUESTED", "CONFIRMED").contains(b.getStatus()))
        throw error(409, "No se puede cancelar este estado");
      b.setStatus("CANCELLED");
      if (b.getPaymentStatus().equals("VERIFIED")) b.setPaymentStatus("REFUND_REVIEW");
    } else {
      if (!manager) throw error(404, "Reserva no encontrada");
      switch (action) {
        case "confirm":
          if (!b.getStatus().equals("REQUESTED")) throw error(409, "La solicitud ya fue atendida");
          if (s.getDate().isBefore(LocalDate.now(ZoneId.of("America/Bogota"))))
            throw error(409, "La fecha ya pasó");
          if (!experience(b.getExperienceId()).isActive()) throw error(409, "Experiencia inactiva");
          if (b.getEndDate() != null)
            checkNights(b.getExperienceId(), b.getDate(), b.getEndDate(), b.getPeople(), true);
          else if (bookings.confirmed(s.getId()) + b.getPeople() > s.getCapacity())
            throw error(409, "No hay cupos suficientes");
          b.setStatus("CONFIRMED");
          break;
        case "verify-payment":
          if (!b.getStatus().equals("CONFIRMED") || !b.getPaymentStatus().equals("PENDING"))
            throw error(409, "Confirma primero la reserva o revisa el estado de pago");
          b.setPaymentStatus("VERIFIED");
          break;
        case "complete":
          if (!b.getStatus().equals("CONFIRMED")) throw error(409, "Confirma primero la reserva");
          b.setStatus("COMPLETED");
          break;
        default:
          throw error(400, "Acción inválida");
      }
    }
    notify(
        b.getUserId(),
        "Actualización de reserva",
        b.getReference() + " · " + b.getStatus() + " · pago " + b.getPaymentStatus(),
        false);
    log(u, "BOOKING_" + action, id);
    return b;
  }

  public void notify(Long user, String title, String message, boolean promo) {
    Notice n = new Notice();
    n.setUserId(user);
    n.setTitle(title);
    n.setMessage(message);
    n.setPromotional(promo);
    n.setCreatedAt(Instant.now());
    notices.save(n);
  }

  public int send(Contracts.NoticeInput d) {
    var u = me();
    if (!admin(u)) throw error(403, "Solo administración");
    var targets =
        d.userId() == null
            ? users.findAll()
            : List.of(
                users.findById(d.userId()).orElseThrow(() -> error(404, "Usuario no encontrado")));
    int sent = 0;
    for (var target : targets) {
      if (Boolean.TRUE.equals(target.getActivo()) && (!d.promotional() || target.isMarketing())) {
        notify(target.getId(), d.title(), d.message(), d.promotional());
        sent++;
      }
    }
    log(u, "NOTIFICATION_SENT", (long) sent);
    return sent;
  }

  public void read(Long id) {
    var n = notices.findById(id).orElseThrow(() -> error(404, "Aviso no encontrado"));
    if (!n.getUserId().equals(me().getId())) throw error(404, "Aviso no encontrado");
    n.setSeen(true);
  }

  public void marketing(boolean value) {
    var u = me();
    u.setMarketing(value);
    log(u, "MARKETING_" + value, u.getId());
  }

  public PrivacyRequest requestPrivacy(Contracts.PrivacyInput d) {
    var u = me();
    PrivacyRequest r = new PrivacyRequest();
    r.setUserId(u.getId());
    r.setKind(d.kind());
    r.setDetail(d.detail());
    r.setStatus("OPEN");
    r.setCreatedAt(Instant.now());
    privacy.save(r);
    if (d.kind().equals("REVOKE")) u.setMarketing(false);
    log(u, "PRIVACY_REQUEST", r.getId());
    return r;
  }

  public void log(Usuario u, String action, Long id) {
    AuditEntry a = new AuditEntry();
    a.setActorId(u.getId());
    a.setAction(action);
    a.setResourceId(id);
    a.setCreatedAt(Instant.now());
    audit.save(a);
  }

  static ResponseStatusException error(int status, String message) {
    return new ResponseStatusException(HttpStatus.valueOf(status), message);
  }
}
