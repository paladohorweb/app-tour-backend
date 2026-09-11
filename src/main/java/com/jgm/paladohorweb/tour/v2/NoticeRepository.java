package com.jgm.paladohorweb.tour.v2;

public interface NoticeRepository
    extends org.springframework.data.jpa.repository.JpaRepository<Notice, Long> {
  java.util.List<Notice> findTop100ByUserIdOrderByIdDesc(Long id);
}
