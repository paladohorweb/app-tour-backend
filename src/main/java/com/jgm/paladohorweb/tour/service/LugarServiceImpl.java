package com.jgm.paladohorweb.tour.service;


import com.jgm.paladohorweb.tour.dto.LugarRequestDTO;
import com.jgm.paladohorweb.tour.dto.LugarResponseDTO;
import com.jgm.paladohorweb.tour.entity.Lugar;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.mapper.LugarMapper;
import com.jgm.paladohorweb.tour.repository.LugarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LugarServiceImpl  implements LugarService{

    private final LugarRepository repository;
    private final LugarMapper mapper;

    @Override
    public List<LugarResponseDTO> listar() {
        return mapper.toResponseList(repository.findByActivoTrue());
    }

    @Override
    public LugarResponseDTO obtenerPorId(Long id) {
        Lugar lugar = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));

        return mapper.toResponseDTO(lugar);
    }

    @Override
    public LugarResponseDTO crear(LugarRequestDTO dto) {
        Lugar lugar = mapper.toEntity(dto);
        Lugar guardado = repository.save(lugar);
        return mapper.toResponseDTO(guardado);
    }
}
