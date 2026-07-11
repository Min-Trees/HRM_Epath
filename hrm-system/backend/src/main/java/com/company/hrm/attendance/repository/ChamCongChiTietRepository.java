package com.company.hrm.attendance.repository;

import com.company.hrm.attendance.entity.ChamCongChiTiet;
import com.company.hrm.attendance.entity.LoaiNgoaiLe;
import com.company.hrm.attendance.entity.TrangThaiDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho bảng {@code timekeeping.cham_cong_chi_tiet}.
 *
 * <p>Dùng bởi:
 * <ul>
 *   <li>T08: {@link #existsByPhanCaId(UUID)} chặn xóa phân ca đã phát sinh chấm công.</li>
 *   <li>T09: ghi log, truy vấn lịch sử, ngoại lệ, tổng hợp tháng.</li>
 * </ul>
 */
public interface ChamCongChiTietRepository extends JpaRepository<ChamCongChiTiet, UUID> {

    boolean existsByPhanCaId(UUID phanCaId);

    boolean existsByNhanVienIdAndNgayChamCong(UUID nhanVienId, LocalDate ngayChamCong);

    Optional<ChamCongChiTiet> findByNhanVienIdAndNgayChamCong(UUID nhanVienId, LocalDate ngayChamCong);

    /** Lịch sử chấm công của NV trong khoảng ngày (mặc định 1 tháng). */
    List<ChamCongChiTiet> findByNhanVienIdAndNgayChamCongBetweenOrderByNgayChamCongAsc(
            UUID nhanVienId, LocalDate from, LocalDate to);

    /**
     * Danh sách ngoại lệ theo khoảng ngày (loại trừ {@link LoaiNgoaiLe#KHONG_NGOAI_LE}).
     * Dùng cho màn hình HR/MANAGER.
     */
    List<ChamCongChiTiet> findByLoaiNgoaiLeNotAndNgayChamCongBetweenOrderByNgayChamCongAsc(
            LoaiNgoaiLe loaiNgoaiLe, LocalDate from, LocalDate to);

    /**
     * Danh sách bản ghi theo trạng thái giải trình — dùng để lọc ngoại lệ cần duyệt.
     * Kết hợp với khoảng ngày ở tầng service.
     */
    List<ChamCongChiTiet> findByGiaiTrinhTrangThaiAndNgayChamCongBetweenOrderByNgayChamCongAsc(
            TrangThaiDon trangThai, LocalDate from, LocalDate to);
}