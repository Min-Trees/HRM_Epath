package com.company.hrm.hr.repository;

import com.company.hrm.hr.entity.QuaTrinhCongTac;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuaTrinhCongTacRepository extends JpaRepository<QuaTrinhCongTac, UUID> {
    List<QuaTrinhCongTac> findByNhanVienIdOrderByTuNgayDesc(UUID nhanVienId);
}