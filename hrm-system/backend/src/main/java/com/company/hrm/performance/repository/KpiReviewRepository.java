package com.company.hrm.performance.repository;

import com.company.hrm.performance.entity.KpiReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KpiReviewRepository extends JpaRepository<KpiReview, UUID> {
    Optional<KpiReview> findByAssignmentId(UUID assignmentId);
}
