package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.request.TourRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.TourResponseDTO;


import java.util.List;

public interface TourService {

    List<TourResponseDTO> listar();

    TourResponseDTO obtenerPorId(Long id);

    TourResponseDTO crear(TourRequestDTO dto);
}
