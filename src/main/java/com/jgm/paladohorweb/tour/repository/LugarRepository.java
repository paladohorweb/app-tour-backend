package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LugarRepository  extends JpaRepository<Lugar,Long> {

    List<Lugar> findByActivoTrue();






}


