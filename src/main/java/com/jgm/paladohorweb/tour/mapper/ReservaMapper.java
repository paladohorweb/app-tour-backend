package com.jgm.paladohorweb.tour.mapper;

import com.jgm.paladohorweb.tour.dto.request.CreateReservaRequest;
import com.jgm.paladohorweb.tour.dto.response.ReservaResponse;
import com.jgm.paladohorweb.tour.entity.Reserva;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface ReservaMapper {

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "tour", ignore = true)
//    @Mapping(target = "estado", ignore = true)
    Reserva toEntity(CreateReservaRequest dto);

    ReservaResponse toResponse(Reserva reserva);
}
