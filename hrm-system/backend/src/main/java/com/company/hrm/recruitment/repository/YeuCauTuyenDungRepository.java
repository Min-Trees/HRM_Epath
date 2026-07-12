package com.company.hrm.recruitment.repository;

import com.company.hrm.recruitment.entity.TrangThaiYeuCau;
import com.company.hrm.recruitment.entity.YeuCauTuyenDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface YeuCauTuyenDungRepository extends JpaRepository<YeuCauTuyenDung, UUID> {
    List<YeuCauTuyenDung> findByTrangThai(TrangThaiYeuCau trangThai);
    List<YeuCauTuyenDung> findByPhongBanId(UUID phongBanId);
}
