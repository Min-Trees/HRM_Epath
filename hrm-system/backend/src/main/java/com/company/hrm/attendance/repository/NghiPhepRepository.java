package com.company.hrm.attendance.repository;

import com.company.hrm.attendance.entity.NghiPhep;
import com.company.hrm.attendance.entity.TrangThaiDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository cho bảng {@code timekeeping.nghi_phep}.
 */
public interface NghiPhepRepository extends JpaRepository<NghiPhep, UUID> {

    /**
     * Kiểm tra NV đã có đơn hiệu lực (trạng thái trong {@code activeStatuses}) mà
     * khoảng nghỉ trùng với {@code [from, to]} (inclusive cả 2 đầu) hay không.
     * Hai khoảng [a1, b1] và [a2, b2] overlap khi a1 ≤ b2 AND a2 ≤ b1.
     */
    boolean existsByNhanVienIdAndTrangThaiInAndTuNgayLessThanEqualAndDenNgayGreaterThanEqual(
            UUID nhanVienId, List<TrangThaiDon> trangThais, LocalDate to, LocalDate from);

    /** Query theo NV + trạng thái (1 hoặc nhiều trạng thái). */
    List<NghiPhep> findByNhanVienIdAndTrangThaiInOrderByTuNgayDesc(
            UUID nhanVienId, List<TrangThaiDon> trangThais);

    /** Query theo NV + tu_ngay ∈ [from, to]. Dùng cho tổng hợp tháng (T11). */
    List<NghiPhep> findByNhanVienIdAndTuNgayBetweenOrderByTuNgayAsc(
            UUID nhanVienId, LocalDate from, LocalDate to);
}