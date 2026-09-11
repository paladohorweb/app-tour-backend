package com.jgm.paladohorweb.tour.v2;

public interface PrivacyRequestRepository
    extends org.springframework.data.jpa.repository.JpaRepository<PrivacyRequest, Long> {
  java.util.List<PrivacyRequest> findByUserIdOrderByIdDesc(Long id);
}
