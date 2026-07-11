package com.company.hrm.attendance.entity;

/**
 * Phân loại nghỉ phép. Mirror enum {@code timekeeping.loai_nghi_phep} trong schema.
 *
 * <ul>
 *   <li>{@link #PHEP_NAM}: phép năm — trừ quỹ khi DA_DUYET.</li>
 *   <li>{@link #OM}: nghỉ ốm — bắt buộc có {@code fileDinhKemUrl} (chứng từ y tế).</li>
 *   <li>{@link #VIEC_RIENG_CO_LUONG}: việc riêng có lương.</li>
 *   <li>{@link #VIEC_RIENG_KHONG_LUONG}: việc riêng không lương — tính vào BHXH nếu ≥ 14 ngày/tháng.</li>
 *   <li>{@link #THAI_SAN}: thai sản — bắt buộc có {@code fileDinhKemUrl}.</li>
 *   <li>{@link #KHAC}: khác.</li>
 * </ul>
 */
public enum LoaiNghiPhep {
    PHEP_NAM,
    OM,
    VIEC_RIENG_CO_LUONG,
    VIEC_RIENG_KHONG_LUONG,
    THAI_SAN,
    KHAC
}