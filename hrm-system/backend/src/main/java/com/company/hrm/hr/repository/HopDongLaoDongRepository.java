package com.company.hrm.hr.repository;

import com.company.hrm.hr.entity.HopDongLaoDong;
import com.company.hrm.hr.entity.HopDongLaoDong.TrangThaiHopDong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HopDongLaoDongRepository extends JpaRepository<HopDongLaoDong, UUID> {
    Optional<HopDongLaoDong> findBySoHopDong(String soHopDong);
    boolean existsBySoHopDong(String soHopDong);

    List<HopDongLaoDong> findByNhanVienIdOrderByNgayHieuLucDesc(UUID nhanVienId);

    Optional<HopDongLaoDong> findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongNot(
            UUID nhanVienId, TrangThaiHopDong trangThai, HopDongLaoDong.LoaiHopDong loai);

    /** HĐ chính (không phải phụ lục) đang HIEU_LUC của NV. */
    Optional<HopDongLaoDong> findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(
            UUID nhanVienId, TrangThaiHopDong trangThai, List<HopDongLaoDong.LoaiHopDong> loaiHopDong);

    /** HĐ chính sắp hết hạn trong [from, to] ngày tới. */
    List<HopDongLaoDong> findByTrangThaiAndNgayHetHieuLucBetween(
            TrangThaiHopDong trangThai, LocalDate from, LocalDate to);

    // ---- Multi-tenant (T11) ----

    Optional<HopDongLaoDong> findByHopDongIdAndCompanyId(UUID hopDongId, UUID companyId);

    List<HopDongLaoDong> findByCompanyId(UUID companyId);

    List<HopDongLaoDong> findByCompanyIdAndNhanVienId(UUID companyId, UUID nhanVienId);
}