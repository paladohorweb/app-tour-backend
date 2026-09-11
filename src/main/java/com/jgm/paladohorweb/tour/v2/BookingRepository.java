package com.jgm.paladohorweb.tour.v2;

public interface BookingRepository
    extends org.springframework.data.jpa.repository.JpaRepository<Booking, Long> {
  java.util.List<Booking> findByUserIdOrderByIdDesc(Long id);

  java.util.List<Booking> findByOwnerIdOrderByIdDesc(Long id);

  java.util.Optional<Booking> findByRequestKey(String key);

  @org.springframework.data.jpa.repository.Query(
      "select coalesce(sum(b.people),0) from Booking b where b.experienceId=:experience and"
          + " b.date<=:day and b.endDate>:day and b.status in ('CONFIRMED','COMPLETED')")
  long occupiedNight(
      @org.springframework.data.repository.query.Param("experience") Long experience,
      @org.springframework.data.repository.query.Param("day") java.time.LocalDate day);

  @org.springframework.data.jpa.repository.Query(
      "select coalesce(sum(b.people),0) from Booking b where b.slotId=:id and b.status in"
          + " ('CONFIRMED','COMPLETED')")
  long confirmed(@org.springframework.data.repository.query.Param("id") Long id);
}
