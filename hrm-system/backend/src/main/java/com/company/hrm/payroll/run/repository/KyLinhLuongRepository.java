package com.company.hrm.payroll.run.repository;

import com.company.hrm.payroll.run.entity.KyLinhLuong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KyLinhLuongRepository extends JpaRepository<KyLinhLuong, UUID> {
    Optional<KyLinhLuong> findByThangAndNam(Integer thang, Integer nam);
}
