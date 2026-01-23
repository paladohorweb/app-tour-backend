package com.jgm.paladohorweb.tour.mapper;


import com.jgm.paladohorweb.tour.dto.LugarRequestDTO;
import com.jgm.paladohorweb.tour.dto.LugarResponseDTO;
import com.jgm.paladohorweb.tour.entity.Lugar;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface LugarMapper {
    Lugar toEntity(LugarRequestDTO dto);

    LugarResponseDTO toResponseDTO(Lugar entity);

    List<LugarResponseDTO> toResponseList(List<Lugar> entities);
}
