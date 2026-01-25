package com.jgm.paladohorweb.tour.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "toures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    private String ciudad;
    private String pais;

    private String imagenUrl;

    private BigDecimal precio;

    @Builder.Default
    private Boolean activo = true;
}
