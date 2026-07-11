package com.company.hrm.hr.repository;

import com.company.hrm.hr.entity.BienDongNhanSu;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho bảng append-only {@code hr.bien_dong_nhan_su}.
 *
 * Chỉ expose method INSERT + truy vấn — KHÔNG có update/delete ở tầng ứng dụng.
 * Kế thừa {@link Repository} (marker interface rỗng) thay vì {@code JpaRepository}
 * để loại bỏ hoàn toàn {@code saveAll}, {@code delete*}, {@code flush}, ...
 *
 * Quy ước: KHÔNG ĐƯỢC thêm method delete/update vào đây. Khi cần sửa dữ liệu biến động
 * → sai nghiệp vụ, phải ghi biến động bù (movement of correction).
 */
public interface BienDongNhanSuRepository extends Repository<BienDongNhanSu, UUID> {

    /** INSERT bản ghi mới. */
    BienDongNhanSu save(BienDongNhanSu entity);

    Optional<BienDongNhanSu> findById(UUID id);

    /** Timeline theo NV, mới nhất trước. */
    List<BienDongNhanSu> findByNhanVienIdOrderByNgayHieuLucDesc(UUID nhanVienId);

    /** Biến động mới nhất có {@code ngay_hieu_luc <= date} cho NV (phục vụ truy vấn trạng thái tại ngày). */
    Optional<BienDongNhanSu> findFirstByNhanVienIdAndNgayHieuLucLessThanEqualOrderByNgayHieuLucDesc(
            UUID nhanVienId, java.time.LocalDate date);

    boolean existsByNhanVienIdAndSoQuyetDinh(UUID nhanVienId, String soQuyetDinh);

    boolean existsById(UUID id);
}
