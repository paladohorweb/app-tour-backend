package com.jgm.paladohorweb.tour.v2;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "v2_bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long experienceId;

  @Column(nullable = false)
  private Long slotId;

  @Column(nullable = false)
  private Long userId;

  private Long ownerId;
  private int people;

  @Column(precision = 12, scale = 2)
  private java.math.BigDecimal total;

  private String status;
  private String paymentStatus;

  @Column(length = 120)
  private String title;

  private String reference;

  @Column(unique = true, nullable = false)
  private String requestKey;

  private java.time.Instant createdAt;
  private java.time.LocalDate date;
  private java.time.LocalDate endDate;
  private String legalVersion;
  private Double meetingLatitude;
  private Double meetingLongitude;
  private String whatsapp;

  @Column(length = 1500)
  private String meetingInstructions;

  @Column(length = 3000)
  private String cancellationSnapshot;

  @Version private Long version;
}
