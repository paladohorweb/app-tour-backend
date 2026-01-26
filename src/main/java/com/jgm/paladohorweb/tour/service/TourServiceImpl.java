package com.jgm.paladohorweb.tour.service;


import com.jgm.paladohorweb.tour.dto.TourRequestDTO;
import com.jgm.paladohorweb.tour.dto.TourResponseDTO;
import com.jgm.paladohorweb.tour.entity.Tour;
import com.jgm.paladohorweb.tour.exception.ResourceNotFoundException;
import com.jgm.paladohorweb.tour.mapper.TourMapper;
import com.jgm.paladohorweb.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TourServiceImpl implements TourService {

    private final TourRepository repository;
    private final TourMapper mapper;

    @Override
    public List<TourResponseDTO> listar() {
        return mapper.toResponseList(repository.findByActivoTrue());
    }

    @Override
    public TourResponseDTO obtenerPorId(Long id) {
        Tour tour = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));

        return mapper.toResponseDTO(tour);
    }

    @Override
    public TourResponseDTO crear(TourRequestDTO dto) {
        Tour tour = mapper.toEntity(dto);
        Tour guardado = repository.save(tour);
        return mapper.toResponseDTO(guardado);
    }
}
