package com.jgm.paladohorweb.tour.mapper;

import com.jgm.paladohorweb.tour.dto.response.ReservaResponse;
import com.jgm.paladohorweb.tour.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {
    @Mapping(source = "tour.titulo", target = "tour")
    ReservaResponse toResponse(Reserva entity);
}
