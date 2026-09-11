package com.jgm.paladohorweb.tour.v2;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "v2_notices")
@Getter
@Setter
@NoArgsConstructor
public class Notice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  private String title;

  @Column(length = 1500)
  private String message;

  private boolean seen;
  private boolean promotional;
  private java.time.Instant createdAt;
}
