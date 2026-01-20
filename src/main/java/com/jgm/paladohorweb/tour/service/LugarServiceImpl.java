package com.jgm.paladohorweb.tour.service;


import com.jgm.paladohorweb.tour.entity.Lugar;
import com.jgm.paladohorweb.tour.repository.LugarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LugarServiceImpl  implements LugarService{

    private final LugarRepository repository;

    @Override
    public List<Lugar> listar() {
        return repository.findByActivoTrue();
    }

    @Override
    public Lugar obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Lugar No encontrado"));
    }

    @Override
    public Lugar crear(Lugar lugar) {
        return repository.save(lugar);
    }
}
