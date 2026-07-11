package com.company.hrm.attendance.util;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Helper ngày lễ Việt Nam (T10 dùng cho gợi ý OT ở frontend; không enforce ở backend).
 *
 * <p>Hardcode các ngày lễ cố định theo dương lịch:
 * <ul>
 *   <li>1/1 — Tết dương lịch</li>
 *   <li>30/4 — Ngày Thống nhất</li>
 *   <li>1/5 — Quốc tế lao động</li>
 *   <li>2/9 — Quốc khánh</li>
 * </ul>
 *
 * <p>Tết âm lịch (mùng 1, 2, 3 tháng Giêng + 30 Tết) — bảng lookup cố định 2024–2030.
 * Năm ngoài khoảng này trả {@code false} (HR có thể bổ sung khi cần).
 */
public final class VietnamHoliday {

    private VietnamHoliday() {}

    /** Các ngày lễ cố định theo dương lịch (month-day). */
    private static final Set<String> FIXED_SOLAR = Set.of(
            "01-01", "04-30", "05-01", "09-02"
    );

    /**
     * Tết âm lịch theo dương lịch 2024–2030 (gồm 30 Tết + mùng 1, 2, 3 tháng Giêng).
     * Lưu ở dạng ISO date string để lookup nhanh.
     */
    private static final Map<Integer, Set<LocalDate>> LUNAR_TET_BY_YEAR = Map.of(
            2024, dates("2024-02-08", "2024-02-09", "2024-02-10", "2024-02-11", "2024-02-12"),
            2025, dates("2025-01-28", "2025-01-29", "2025-01-30", "2025-01-31", "2025-02-01"),
            2026, dates("2026-02-16", "2026-02-17", "2026-02-18", "2026-02-19", "2026-02-20"),
            2027, dates("2027-02-05", "2027-02-06", "2027-02-07", "2027-02-08", "2027-02-09"),
            2028, dates("2028-01-25", "2028-01-26", "2028-01-27", "2028-01-28", "2028-01-29"),
            2029, dates("2029-02-12", "2029-02-13", "2029-02-14", "2029-02-15", "2029-02-16"),
            2030, dates("2030-02-02", "2030-02-03", "2030-02-04", "2030-02-05", "2030-02-06")
    );

    private static Set<LocalDate> dates(String... iso) {
        Set<LocalDate> s = new java.util.HashSet<>();
        for (String d : iso) s.add(LocalDate.parse(d));
        return s;
    }

    /**
     * Kiểm tra {@code date} có phải ngày lễ VN hay không.
     */
    public static boolean isHoliday(LocalDate date) {
        if (date == null) return false;
        String mmdd = String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth());
        if (FIXED_SOLAR.contains(mmdd)) return true;
        Set<LocalDate> tet = LUNAR_TET_BY_YEAR.get(date.getYear());
        return tet != null && tet.contains(date);
    }
}