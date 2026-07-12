package com.company.hrm.hr.offboarding.repository;

import com.company.hrm.hr.offboarding.entity.SeveranceCalc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SeveranceCalcRepository extends JpaRepository<SeveranceCalc, UUID> {
    Optional<SeveranceCalc> findByCaseId(UUID caseId);
    Optional<SeveranceCalc> findByNhanVienId(UUID nhanVienId);
}
