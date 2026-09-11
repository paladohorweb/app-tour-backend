package com.jgm.paladohorweb.tour.v2;

import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class V2Controller {
  final V2Service s;

  @GetMapping("/experiences")
  public Object catalog() {
    return s.catalog();
  }

  @GetMapping("/experiences/{id}")
  public Object experience(@PathVariable Long id) {
    return s.publicExperience(id);
  }

  @GetMapping("/experiences/{id}/slots")
  public Object slots(@PathVariable Long id) {
    return s.availability(id);
  }

  @GetMapping("/provider/experiences")
  public Object mine() {
    return s.mine();
  }

  @PostMapping("/provider/experiences")
  public Object create(@Valid @RequestBody Contracts.ExperienceInput d) {
    return s.create(d);
  }

  @PutMapping("/provider/experiences/{id}")
  public Object edit(@PathVariable Long id, @Valid @RequestBody Contracts.ExperienceInput d) {
    return s.edit(id, d);
  }

  @PatchMapping("/provider/experiences/{id}/active")
  public void active(@PathVariable Long id, @RequestBody Map<String, Boolean> d) {
    s.active(id, Boolean.TRUE.equals(d.get("active")));
  }

  @PostMapping("/provider/experiences/{id}/slots")
  public Object slot(@PathVariable Long id, @Valid @RequestBody Contracts.SlotInput d) {
    return s.addSlot(id, d);
  }

  @PostMapping("/bookings")
  public Object book(@Valid @RequestBody Contracts.BookingInput d) {
    return s.book(d);
  }

  @GetMapping("/bookings")
  public Object bookings() {
    return s.myBookings();
  }

  @GetMapping("/provider/bookings")
  public Object providerBookings() {
    return s.providerBookings();
  }

  @PatchMapping("/bookings/{id}/{action}")
  public Object change(@PathVariable Long id, @PathVariable String action) {
    return s.transition(id, action);
  }

  @GetMapping("/notifications")
  public Object notifications() {
    return s.myNotices();
  }

  @PatchMapping("/notifications/{id}/read")
  public void read(@PathVariable Long id) {
    s.read(id);
  }

  @PostMapping("/admin/notifications")
  public Object send(@Valid @RequestBody Contracts.NoticeInput d) {
    return Map.of("sent", s.send(d));
  }

  @GetMapping("/preferences")
  public Object preferences() {
    return Map.of("marketing", s.me().isMarketing());
  }

  @PutMapping("/preferences")
  public void preferences(@RequestBody Map<String, Boolean> d) {
    s.marketing(Boolean.TRUE.equals(d.get("marketing")));
  }

  @PostMapping("/privacy-requests")
  public Object privacy(@Valid @RequestBody Contracts.PrivacyInput d) {
    return s.requestPrivacy(d);
  }

  @GetMapping("/privacy-requests")
  public Object privacy() {
    return s.myPrivacy();
  }

  @GetMapping("/admin/privacy-requests")
  public Object adminPrivacy() {
    return s.adminPrivacy();
  }

  @PatchMapping("/admin/privacy-requests/{id}/close")
  public void closePrivacy(@PathVariable Long id) {
    s.closePrivacy(id);
  }
}
