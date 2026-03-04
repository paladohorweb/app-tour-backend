package com.jgm.paladohorweb.tour.mapper;


import com.jgm.paladohorweb.tour.dto.request.TourRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.TourResponseDTO;
import com.jgm.paladohorweb.tour.entity.Tour;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TourMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Tour toEntity(TourRequestDTO dto);

    TourResponseDTO toResponseDTO(Tour entity);

    List<TourResponseDTO> toResponseList(List<Tour> entities);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntityFromDto(TourRequestDTO dto, @MappingTarget Tour entity);
}
