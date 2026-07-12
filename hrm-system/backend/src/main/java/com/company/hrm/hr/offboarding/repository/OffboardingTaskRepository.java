package com.company.hrm.hr.offboarding.repository;

import com.company.hrm.hr.offboarding.entity.OffboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OffboardingTaskRepository extends JpaRepository<OffboardingTask, UUID> {
    List<OffboardingTask> findByCaseIdOrderByThuTuAscCreatedAtAsc(UUID caseId);
    long countByCaseId(UUID caseId);
}
