package com.jgm.paladohorweb.tour.mapper;


import com.jgm.paladohorweb.tour.dto.LugarRequestDTO;
import com.jgm.paladohorweb.tour.dto.LugarResponseDTO;
import com.jgm.paladohorweb.tour.entity.Tour;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface LugarMapper {
    Tour toEntity(LugarRequestDTO dto);

    LugarResponseDTO toResponseDTO(Tour entity);

    List<LugarResponseDTO> toResponseList(List<Tour> entities);
}
