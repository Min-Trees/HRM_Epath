package com.company.hrm.recruitment.repository;

import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.UngVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UngVienRepository extends JpaRepository<UngVien, UUID> {
    List<UngVien> findByTrangThai(TrangThaiUngVien trangThai);
    List<UngVien> findByYeuCauId(UUID yeuCauId);
}
