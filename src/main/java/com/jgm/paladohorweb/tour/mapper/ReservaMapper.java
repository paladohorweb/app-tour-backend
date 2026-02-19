package com.jgm.paladohorweb.tour.mapper;

import com.jgm.paladohorweb.tour.dto.request.CreateReservaRequest;
import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.entity.Reserva;
import com.jgm.paladohorweb.tour.entity.Tour;
import com.jgm.paladohorweb.tour.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    /**
     * CreateReservaRequest solo trae tourId/email/nombre.
     * Las relaciones (tour, usuario) se setean en el Service.
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "tour", ignore = true),
            @Mapping(target = "usuario", ignore = true),
            @Mapping(target = "monto", ignore = true),
            @Mapping(target = "estado", ignore = true),
            @Mapping(target = "stripePaymentIntentId", ignore = true),
            @Mapping(target = "fechaCreacion", ignore = true),

            // Estos sí pueden mapearse directo por nombre
            @Mapping(target = "emailCliente", source = "emailCliente"),
            @Mapping(target = "nombreCliente", source = "nombreCliente")
    })
    Reserva toEntity(CreateReservaRequest dto);

    @Mappings({
            @Mapping(target = "tourId", source = "tour", qualifiedByName = "tourToId"),
            @Mapping(target = "tourNombre", source = "tour", qualifiedByName = "tourToNombre"),
            @Mapping(target = "usuarioId", source = "usuario", qualifiedByName = "usuarioToId")
    })
    ReservaResponseDTO toResponse(Reserva reserva);

    // ===== Helpers para mapear relaciones =====

    @Named("tourToId")
    default Long tourToId(Tour tour) {
        return tour != null ? tour.getId() : null;
    }

    @Named("tourToNombre")
    default String tourToNombre(Tour tour) {
        return tour != null ? tour.getNombre() : null;
    }

    @Named("usuarioToId")
    default Long usuarioToId(Usuario usuario) {
        return usuario != null ? usuario.getId() : null;
    }
}

