package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour,Long> {

    List<Tour> findByActivoTrue();






}


