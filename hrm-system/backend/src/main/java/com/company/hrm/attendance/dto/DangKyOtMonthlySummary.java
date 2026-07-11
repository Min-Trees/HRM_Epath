package com.company.hrm.attendance.dto;

import java.math.BigDecimal;

/**
 * Tổng hợp OT đã duyệt theo NV và tháng — cung cấp cho T11 (tính lương).
 *
 * <p>3 trường tương ứng 3 mức hệ số OT (150% / 200% / 300%).
 */
public class DangKyOtMonthlySummary {

    private final java.util.UUID nhanVienId;
    private final int thang;
    private final int nam;
    private final BigDecimal soGioOt150;
    private final BigDecimal soGioOt200;
    private final BigDecimal soGioOt300;

    public DangKyOtMonthlySummary(java.util.UUID nhanVienId, int thang, int nam,
                                  BigDecimal soGioOt150, BigDecimal soGioOt200, BigDecimal soGioOt300) {
        this.nhanVienId = nhanVienId;
        this.thang = thang;
        this.nam = nam;
        this.soGioOt150 = soGioOt150;
        this.soGioOt200 = soGioOt200;
        this.soGioOt300 = soGioOt300;
    }

    public java.util.UUID getNhanVienId() { return nhanVienId; }
    public int getThang() { return thang; }
    public int getNam() { return nam; }
    public BigDecimal getSoGioOt150() { return soGioOt150; }
    public BigDecimal getSoGioOt200() { return soGioOt200; }
    public BigDecimal getSoGioOt300() { return soGioOt300; }
}