package com.company.hrm.hr.event;

import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Phát khi biến động làm thay đổi {@code trang_thai} của NV.
 * T13 (BHXH), T15 (payroll), và UI có thể lắng nghe.
 *
 * @param nhanVienId NV bị ảnh hưởng.
 * @param trangThaiCu Trạng thái trước biến động.
 * @param trangThaiMoi Trạng thái sau biến động (== {@code bien_dong.trang_thai_nv_sau}).
 * @param ngayHieuLuc Ngày biến động có hiệu lực.
 * @param loaiBienDong Loại biến động (TUYEN_DUNG, CHAM_DUT_HDLD, NGHI_HUU, ...).
 */
public record EmployeeStatusChangedEvent(
        UUID nhanVienId,
        TrangThaiNv trangThaiCu,
        TrangThaiNv trangThaiMoi,
        LocalDate ngayHieuLuc,
        String loaiBienDong) {
}
