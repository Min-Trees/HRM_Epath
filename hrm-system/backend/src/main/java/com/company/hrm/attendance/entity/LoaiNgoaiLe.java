package com.company.hrm.attendance.entity;

/**
 * Phân loại ngoại lệ chấm công. Mirror enum {@code timekeeping.loai_ngoai_le} trong schema.
 *
 * <ul>
 *   <li>{@link #DI_TRE}: vào muộn quá ngưỡng so với giờ bắt đầu ca.</li>
 *   <li>{@link #VE_SOM}: ra sớm quá ngưỡng so với giờ kết thúc ca.</li>
 *   <li>{@link #THIEU_CONG}: thiếu giờ vào hoặc giờ ra so với ca chuẩn.</li>
 *   <li>{@link #LAM_NGOAI_CA}: không có phân ca cho ngày đó nhưng có log.</li>
 *   <li>{@link #KHONG_NGOAI_LE}: đúng ca, không có ngoại lệ.</li>
 * </ul>
 */
public enum LoaiNgoaiLe {
    DI_TRE,
    VE_SOM,
    THIEU_CONG,
    LAM_NGOAI_CA,
    KHONG_NGOAI_LE
}