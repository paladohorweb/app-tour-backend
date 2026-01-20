package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.entity.Lugar;

import java.util.List;

public interface LugarService {
    List<Lugar> listar();
    Lugar obtenerPorId(Long id);
    Lugar crear(Lugar lugar);



}
