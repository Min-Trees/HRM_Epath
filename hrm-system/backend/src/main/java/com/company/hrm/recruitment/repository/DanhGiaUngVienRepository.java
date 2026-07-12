package com.company.hrm.recruitment.repository;

import com.company.hrm.recruitment.entity.DanhGiaUngVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DanhGiaUngVienRepository extends JpaRepository<DanhGiaUngVien, UUID> {
    List<DanhGiaUngVien> findByLichPvId(UUID lichPvId);
}
