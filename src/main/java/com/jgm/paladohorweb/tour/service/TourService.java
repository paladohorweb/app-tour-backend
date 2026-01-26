package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.TourRequestDTO;
import com.jgm.paladohorweb.tour.dto.TourResponseDTO;


import java.util.List;

public interface TourService {

    List<TourResponseDTO> listar();

    TourResponseDTO obtenerPorId(Long id);

    TourResponseDTO crear(TourRequestDTO dto);
}
