package com.jgm.paladohorweb.tour.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    private String ciudad;
    private String pais;

    private String imagenUrl;

    private Long precio;

    private Double latitud;
    private Double longitud;


    private Boolean activo = true;
}
