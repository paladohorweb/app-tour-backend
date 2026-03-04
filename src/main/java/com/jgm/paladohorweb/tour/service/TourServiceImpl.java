package com.jgm.paladohorweb.tour.service;


import com.jgm.paladohorweb.tour.dto.request.TourRequestDTO;
import com.jgm.paladohorweb.tour.dto.response.TourResponseDTO;
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
    @Transactional(readOnly = true)
    public List<TourResponseDTO> listar() {
        return mapper.toResponseList(repository.findByActivoTrue());
    }

    @Override
    @Transactional(readOnly = true)
    public TourResponseDTO obtenerPorId(Long id) {
        Tour tour = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour no encontrado"));
        return mapper.toResponseDTO(tour);
    }

    @Override
    public TourResponseDTO crear(TourRequestDTO dto) {
        Tour tour = mapper.toEntity(dto);
        tour.setActivo(true);

        Tour saved = repository.save(tour);
        return mapper.toResponseDTO(saved);
    }

    public TourResponseDTO actualizar(Long id, TourRequestDTO dto) {
        Tour tour = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour no encontrado"));

        mapper.updateEntityFromDto(dto, tour);
        return mapper.toResponseDTO(repository.save(tour));
    }

    public void cambiarActivo(Long id, boolean activo) {
        Tour tour = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour no encontrado"));
        tour.setActivo(activo);
        repository.save(tour);
    }

    public void eliminar(Long id) {
        Tour tour = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour no encontrado"));
        repository.delete(tour);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TourResponseDTO> listarAdmin() {
        return mapper.toResponseList(repository.findAllByOrderByIdDesc());
    }

}
