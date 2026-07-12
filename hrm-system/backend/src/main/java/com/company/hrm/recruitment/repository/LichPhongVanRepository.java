package com.company.hrm.recruitment.repository;

import com.company.hrm.recruitment.entity.LichPhongVan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LichPhongVanRepository extends JpaRepository<LichPhongVan, UUID> {
    List<LichPhongVan> findByUngVienIdOrderByVongPhongVanAsc(UUID ungVienId);
    List<LichPhongVan> findByThoiGianBatDauBetween(LocalDateTime tu, LocalDateTime den);
}
