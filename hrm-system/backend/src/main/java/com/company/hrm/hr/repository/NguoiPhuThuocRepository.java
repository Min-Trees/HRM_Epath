package com.company.hrm.hr.repository;

import com.company.hrm.hr.entity.NguoiPhuThuoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NguoiPhuThuocRepository extends JpaRepository<NguoiPhuThuoc, UUID> {
    List<NguoiPhuThuoc> findByNhanVienIdAndActiveTrue(UUID nhanVienId);
    List<NguoiPhuThuoc> findByNhanVienId(UUID nhanVienId);
}