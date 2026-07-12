package com.company.hrm.payroll.tax.repository;

import com.company.hrm.payroll.tax.entity.CamKet08;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CamKet08Repository extends JpaRepository<CamKet08, UUID> {
    Optional<CamKet08> findByNhanVienIdAndNam(UUID nhanVienId, Integer nam);
    List<CamKet08> findByNam(Integer nam);
}
