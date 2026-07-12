package com.company.hrm.hr.offboarding.repository;

import com.company.hrm.hr.offboarding.entity.OffboardingAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OffboardingAssetRepository extends JpaRepository<OffboardingAsset, UUID> {
    List<OffboardingAsset> findByCaseIdOrderByCreatedAtAsc(UUID caseId);
}
