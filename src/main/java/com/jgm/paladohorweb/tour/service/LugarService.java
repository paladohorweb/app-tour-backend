package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.dto.LugarRequestDTO;
import com.jgm.paladohorweb.tour.dto.LugarResponseDTO;


import java.util.List;

public interface LugarService {

    List<LugarResponseDTO> listar();

    LugarResponseDTO obtenerPorId(Long id);

    LugarResponseDTO crear(LugarRequestDTO dto);
}
