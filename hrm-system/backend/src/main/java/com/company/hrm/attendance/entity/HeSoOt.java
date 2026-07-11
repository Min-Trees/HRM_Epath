package com.company.hrm.attendance.entity;

/**
 * Hệ số OT. Mirror enum {@code timekeeping.he_so_ot} trong schema.
 *
 * <ul>
 *   <li>{@link #NGAY_THUONG_150}: ngày thường — 150% lương giờ.</li>
 *   <li>{@link #NGAY_NGHI_TUAN_200}: ngày nghỉ tuần (T7/CN) — 200%.</li>
 *   <li>{@link #NGAY_LE_300}: ngày lễ/Tết — 300%.</li>
 * </ul>
 *
 * <p>Chưa bao gồm phụ cấp làm đêm (20–30%); được cộng thêm ở tầng tính lương (T15).
 */
public enum HeSoOt {
    NGAY_THUONG_150,
    NGAY_NGHI_TUAN_200,
    NGAY_LE_300;

    /** Hệ số dạng số thực — dùng cho engine tính lương (T15). */
    public double toMultiplier() {
        return switch (this) {
            case NGAY_THUONG_150 -> 1.5;
            case NGAY_NGHI_TUAN_200 -> 2.0;
            case NGAY_LE_300 -> 3.0;
        };
    }
}