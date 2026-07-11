package com.company.hrm.attendance.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Tổng hợp chấm công theo nhân viên / tháng — cung cấp cho T11 (tính lương).
 *
 * <p>{@code chiTiet} liệt kê từng ngày có chấm công trong tháng (kèm số giờ,
 * trạng thái ngoại lệ), giúp payroll debug nhanh khi sai lệch.
 */
public class MonthlySummary {

    private final UUID nhanVienId;
    private final int thang;
    private final int nam;
    private final int soNgayCong;
    private final int soNgayNghi;
    private final int soNgayNgoaiLe;
    private final java.math.BigDecimal tongGioCong;
    private final List<TimeLogResponse> chiTiet;

    public MonthlySummary(UUID nhanVienId, int thang, int nam, int soNgayCong, int soNgayNghi,
                          int soNgayNgoaiLe, java.math.BigDecimal tongGioCong,
                          List<TimeLogResponse> chiTiet) {
        this.nhanVienId = nhanVienId;
        this.thang = thang;
        this.nam = nam;
        this.soNgayCong = soNgayCong;
        this.soNgayNghi = soNgayNghi;
        this.soNgayNgoaiLe = soNgayNgoaiLe;
        this.tongGioCong = tongGioCong;
        this.chiTiet = chiTiet;
    }

    public UUID getNhanVienId() { return nhanVienId; }
    public int getThang() { return thang; }
    public int getNam() { return nam; }
    public int getSoNgayCong() { return soNgayCong; }
    public int getSoNgayNghi() { return soNgayNghi; }
    public int getSoNgayNgoaiLe() { return soNgayNgoaiLe; }
    public java.math.BigDecimal getTongGioCong() { return tongGioCong; }
    public List<TimeLogResponse> getChiTiet() { return chiTiet; }

    /** Tính số ngày từ {@code from} đến {@code to} (inclusive). */
    public static int daysInRange(LocalDate from, LocalDate to) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
    }
}