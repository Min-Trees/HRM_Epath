package com.company.hrm.hr.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Phát khi NV có biến động lương (DIEU_CHINH_LUONG, hoặc biến động khác có {@code luong_sau}).
 * T13 (BHXH) dùng để điều chỉnh mức đóng BHXH;
 * T15 (payroll) dùng để áp lương mới từ {@code ngayHieuLuc}.
 */
public record EmployeeSalaryChangedEvent(
        UUID nhanVienId,
        BigDecimal luongTruoc,
        BigDecimal luongSau,
        LocalDate ngayHieuLuc) {
}
