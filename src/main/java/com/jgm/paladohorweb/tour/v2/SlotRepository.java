package com.jgm.paladohorweb.tour.v2;

public interface SlotRepository
    extends org.springframework.data.jpa.repository.JpaRepository<Slot, Long> {
  java.util.List<Slot> findByExperienceIdOrderByDateAscTimeAsc(Long id);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select s from Slot s where s.id=:id")
  java.util.Optional<Slot> lockById(@org.springframework.data.repository.query.Param("id") Long id);
}
