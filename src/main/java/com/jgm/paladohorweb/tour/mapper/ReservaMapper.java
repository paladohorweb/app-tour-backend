package com.jgm.paladohorweb.tour.mapper;

import com.jgm.paladohorweb.tour.dto.request.CreateReservaRequest;
import com.jgm.paladohorweb.tour.dto.response.ReservaResponseDTO;
import com.jgm.paladohorweb.tour.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReservaMapper {

    // ============================
    // REQUEST -> ENTITY
    // ============================
    // Solo mapeamos lo que realmente viene del request.
    // Lo demás se completa en ReservaService.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "guia", ignore = true)
    @Mapping(target = "monto", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "stripePaymentIntentId", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Reserva toEntity(CreateReservaRequest dto);

    // ============================
    // ENTITY -> RESPONSE DTO
    // ============================
    @Mapping(target = "tourId", source = "tour.id")
    @Mapping(target = "tourNombre", source = "tour.nombre")
    @Mapping(target = "usuarioId", source = "usuario.id")

    @Mapping(target = "guiaId", source = "guia.id")
    @Mapping(target = "guiaNombre", source = "guia.nombre")
    @Mapping(target = "guiaEmail", source = "guia.email")

    ReservaResponseDTO toResponse(Reserva reserva);
}