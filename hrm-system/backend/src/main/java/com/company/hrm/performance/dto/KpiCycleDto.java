package com.company.hrm.performance.dto;

import com.company.hrm.performance.entity.TrangThaiChuKy;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class KpiCycleDto {

    private UUID cycleId;

    @NotBlank
    @Size(max = 100)
    private String tenChuKy;

    @NotBlank
    @Pattern(regexp = "QUARTER|HALF_YEAR|YEAR")
    private String loaiChuKy;

    @NotNull
    private LocalDate ngayBatDau;

    @NotNull
    private LocalDate ngayKetThuc;

    private LocalDate hanNvTuDanhGia;
    private LocalDate hanManagerReview;
    private LocalDate hanHrPheDuyet;
    private TrangThaiChuKy trangThai;
    private UUID nguoiTaoId;
    private String moTa;

    private Integer soMucTieu;
    private Integer soNvThamGia;

    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public String getTenChuKy() { return tenChuKy; }
    public void setTenChuKy(String tenChuKy) { this.tenChuKy = tenChuKy; }
    public String getLoaiChuKy() { return loaiChuKy; }
    public void setLoaiChuKy(String loaiChuKy) { this.loaiChuKy = loaiChuKy; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public LocalDate getHanNvTuDanhGia() { return hanNvTuDanhGia; }
    public void setHanNvTuDanhGia(LocalDate hanNvTuDanhGia) { this.hanNvTuDanhGia = hanNvTuDanhGia; }
    public LocalDate getHanManagerReview() { return hanManagerReview; }
    public void setHanManagerReview(LocalDate hanManagerReview) { this.hanManagerReview = hanManagerReview; }
    public LocalDate getHanHrPheDuyet() { return hanHrPheDuyet; }
    public void setHanHrPheDuyet(LocalDate hanHrPheDuyet) { this.hanHrPheDuyet = hanHrPheDuyet; }
    public TrangThaiChuKy getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiChuKy trangThai) { this.trangThai = trangThai; }
    public UUID getNguoiTaoId() { return nguoiTaoId; }
    public void setNguoiTaoId(UUID nguoiTaoId) { this.nguoiTaoId = nguoiTaoId; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public Integer getSoMucTieu() { return soMucTieu; }
    public void setSoMucTieu(Integer soMucTieu) { this.soMucTieu = soMucTieu; }
    public Integer getSoNvThamGia() { return soNvThamGia; }
    public void setSoNvThamGia(Integer soNvThamGia) { this.soNvThamGia = soNvThamGia; }
}
