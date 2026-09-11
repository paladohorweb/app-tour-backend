package com.jgm.paladohorweb.tour.v2;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "v2_audit")
@Getter
@Setter
@NoArgsConstructor
public class AuditEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long actorId;
  private String action;
  private Long resourceId;
  private java.time.Instant createdAt;
}
