package com.company.hrm.performance.repository;

import com.company.hrm.performance.entity.KpiSelfAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KpiSelfAssessmentRepository extends JpaRepository<KpiSelfAssessment, UUID> {
    Optional<KpiSelfAssessment> findByAssignmentId(UUID assignmentId);
}
