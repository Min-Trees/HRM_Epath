package com.company.hrm.payroll.run.repository;

import com.company.hrm.payroll.run.entity.KhoanLuong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KhoanLuongRepository extends JpaRepository<KhoanLuong, UUID> {
    List<KhoanLuong> findByKyLinhId(UUID kyLinhId);
    List<KhoanLuong> findByBangLuongId(UUID bangLuongId);
}
