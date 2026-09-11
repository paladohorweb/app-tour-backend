package com.jgm.paladohorweb.tour.v2;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "v2_experiences")
@Getter
@Setter
@NoArgsConstructor
public class Experience {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long ownerId;

  @Column(nullable = false, length = 120)
  private String title;

  @Column(nullable = false, length = 5000)
  private String description;

  @Column(nullable = false)
  private String kind;

  private String department;
  private String municipality;
  private String address;
  private Double latitude;
  private Double longitude;
  private Double meetingLatitude;
  private Double meetingLongitude;

  @Column(length = 1500)
  private String meetingInstructions;

  private String whatsapp;
  private String imageUrl;
  private String rnt;

  @Column(length = 3000)
  private String cancellationPolicy;

  @Column(precision = 12, scale = 2)
  private java.math.BigDecimal price;

  private boolean active = true;
}
