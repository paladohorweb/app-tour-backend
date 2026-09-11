package com.jgm.paladohorweb.tour.v2;

public interface ExperienceRepository
    extends org.springframework.data.jpa.repository.JpaRepository<Experience, Long> {
  java.util.List<Experience> findByActiveTrueOrderByIdDesc();

  java.util.List<Experience> findByOwnerIdOrderByIdDesc(Long id);
}
