package com.company.hrm.payroll.run.repository;

import com.company.hrm.payroll.run.entity.AuditKyLuong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditKyLuongRepository extends JpaRepository<AuditKyLuong, UUID> {
    List<AuditKyLuong> findByKyLinhIdOrderByCreatedAtDesc(UUID kyLinhId);
}
