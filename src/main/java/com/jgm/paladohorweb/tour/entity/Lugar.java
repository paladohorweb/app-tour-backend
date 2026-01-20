package com.jgm.paladohorweb.tour.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "lugares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    private String ciudad;
    private String pais;

    private String imagenUrl;

    private Double latitud;
    private Double longitud;

    private Boolean activo = true;
}
