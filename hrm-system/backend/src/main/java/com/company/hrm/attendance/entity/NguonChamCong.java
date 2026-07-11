package com.company.hrm.attendance.entity;

/**
 * Nguồn dữ liệu chấm công. Mirror enum {@code timekeeping.nguon_cham_cong} trong schema.
 *
 * <ul>
 *   <li>{@link #VAN_TAY}: máy chấm công vân tay.</li>
 *   <li>{@link #KHUON_MAT}: máy chấm công khuôn mặt.</li>
 *   <li>{@link #GPS_MOBILE}: app mobile có GPS.</li>
 *   <li>{@link #THU_CONG}: nhập tay (bắt buộc duyệt).</li>
 * </ul>
 */
public enum NguonChamCong {
    VAN_TAY,
    KHUON_MAT,
    GPS_MOBILE,
    THU_CONG
}