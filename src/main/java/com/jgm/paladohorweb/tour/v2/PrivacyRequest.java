package com.jgm.paladohorweb.tour.v2;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "v2_privacy_requests")
@Getter
@Setter
@NoArgsConstructor
public class PrivacyRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;
  private String kind;

  @Column(length = 2000)
  private String detail;

  private String status;
  private java.time.Instant createdAt;
}
