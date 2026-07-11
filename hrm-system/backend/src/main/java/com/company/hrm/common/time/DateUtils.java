package com.company.hrm.common.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tiện ích thời gian — đặc biệt phục vụ các quy tắc nghiệp vụ:
 *   - đếm "ngày làm việc" giữa hai mốc (bỏ T7, CN) — dùng cho nghỉ không lương ≥ 14 ngày làm việc,
 *     báo giảm BHXH, v.v.
 *   - chuyển đổi giữa LocalDate / Instant.
 *
 * Ngày lễ/Tết hiện chưa cấu hình — sẽ thêm bảng ngày lễ trong task sau nếu cần.
 */
public final class DateUtils {

    private DateUtils() {}

    public static long daysBetween(LocalDate from, LocalDate to) {
        if (from == null || to == null) return 0;
        return ChronoUnit.DAYS.between(from, to);
    }

    /** Đếm ngày làm việc (T2..T6) giữa from..to inclusive. */
    public static long workingDaysBetween(LocalDate from, LocalDate to) {
        if (from == null || to == null) return 0;
        if (to.isBefore(from)) return 0;
        long count = 0;
        LocalDate cur = from;
        while (!cur.isAfter(to)) {
            DayOfWeek dow = cur.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                count++;
            }
            cur = cur.plusDays(1);
        }
        return count;
    }

    public static boolean isWorkingDay(LocalDate date) {
        if (date == null) return false;
        DayOfWeek dow = date.getDayOfWeek();
        return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
    }
}