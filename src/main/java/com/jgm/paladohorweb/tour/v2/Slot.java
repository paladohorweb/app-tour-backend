package com.jgm.paladohorweb.tour.v2;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "v2_slots")
@Getter
@Setter
@NoArgsConstructor
public class Slot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long experienceId;

  @Column(nullable = false)
  private java.time.LocalDate date;

  private String time;
  private int capacity;
  @Version private Long version;
}
