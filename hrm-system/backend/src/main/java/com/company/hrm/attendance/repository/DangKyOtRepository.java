package com.company.hrm.attendance.repository;

import com.company.hrm.attendance.entity.DangKyOt;
import com.company.hrm.attendance.entity.HeSoOt;
import com.company.hrm.attendance.entity.TrangThaiDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository cho bảng {@code timekeeping.dang_ky_ot}.
 */
public interface DangKyOtRepository extends JpaRepository<DangKyOt, UUID> {

    /** Query theo NV + khoảng ngày OT. */
    List<DangKyOt> findByNhanVienIdAndNgayLamOtBetweenOrderByNgayLamOtAsc(
            UUID nhanVienId, LocalDate from, LocalDate to);

    /** Query theo NV + trạng thái. */
    List<DangKyOt> findByNhanVienIdAndTrangThaiInOrderByNgayLamOtDesc(
            UUID nhanVienId, List<TrangThaiDon> trangThais);

    /**
     * Tổng giờ OT đã duyệt theo NV + tháng + hệ số. Dùng cho T11.
     * JPQL COALESCE trả {@code 0} khi không có bản ghi.
     */
    @Query("SELECT COALESCE(SUM(o.soGioOt), 0) FROM DangKyOt o " +
           "WHERE o.nhanVienId = :nhanVienId " +
           "AND o.trangThai = :trangThai " +
           "AND o.ngayLamOt BETWEEN :from AND :to " +
           "AND o.heSoOt = :heSoOt")
    java.math.BigDecimal sumApprovedByMonthAndHeSo(@Param("nhanVienId") UUID nhanVienId,
                                                   @Param("trangThai") TrangThaiDon trangThai,
                                                   @Param("from") LocalDate from,
                                                   @Param("to") LocalDate to,
                                                   @Param("heSoOt") HeSoOt heSoOt);
}