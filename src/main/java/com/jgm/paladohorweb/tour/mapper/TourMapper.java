package com.jgm.paladohorweb.tour.mapper;


import com.jgm.paladohorweb.tour.dto.request.TourRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.TourResponseDTO;
import com.jgm.paladohorweb.tour.entity.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TourMapper {


    Tour toEntity(TourRequestDTO dto);



    TourResponseDTO toResponseDTO(Tour entity);

    List<TourResponseDTO> toResponseList(List<Tour> entities);
}
