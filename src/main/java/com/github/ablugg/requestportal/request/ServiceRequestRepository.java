package com.github.ablugg.requestportal.request;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access. There is no implementation class: Spring Data generates one at startup
 * and writes the SQL for each method from its name (findByReference becomes
 * "select ... where reference = ?").
 */
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    Optional<ServiceRequest> findByReference(String reference);

    boolean existsByReference(String reference);

    List<ServiceRequest> findAllByOrderByCreatedAtDesc();

    List<ServiceRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);
}
