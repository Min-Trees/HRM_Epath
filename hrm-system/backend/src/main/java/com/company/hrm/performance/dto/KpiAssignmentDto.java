package com.company.hrm.performance.dto;

import com.company.hrm.performance.entity.LoaiMucTieu;
import com.company.hrm.performance.entity.TrangThaiAssignment;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class KpiAssignmentDto {

    private UUID assignmentId;

    @NotNull
    private UUID cycleId;

    @NotNull
    private UUID nhanVienId;

    private UUID templateId;

    @NotBlank
    @Size(max = 300)
    private String tenMucTieu;

    private LoaiMucTieu loaiMucTieu;

    private String donViDo;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal targetValue;

    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal trongSo;

    private String moTaChiTiet;
    private UUID nguoiGanId;
    private TrangThaiAssignment trangThai;
    private LocalDateTime createdAt;

    private BigDecimal diemTuDanhGia;
    private BigDecimal diemManager;
    private BigDecimal diemTrungBinh;
    private String xepLoaiCuoi;
    private String hoTen;
    private String maNv;

    public UUID getAssignmentId() { return assignmentId; }
    public void setAssignmentId(UUID assignmentId) { this.assignmentId = assignmentId; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public UUID getTemplateId() { return templateId; }
    public void setTemplateId(UUID templateId) { this.templateId = templateId; }
    public String getTenMucTieu() { return tenMucTieu; }
    public void setTenMucTieu(String tenMucTieu) { this.tenMucTieu = tenMucTieu; }
    public LoaiMucTieu getLoaiMucTieu() { return loaiMucTieu; }
    public void setLoaiMucTieu(LoaiMucTieu loaiMucTieu) { this.loaiMucTieu = loaiMucTieu; }
    public String getDonViDo() { return donViDo; }
    public void setDonViDo(String donViDo) { this.donViDo = donViDo; }
    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }
    public BigDecimal getTrongSo() { return trongSo; }
    public void setTrongSo(BigDecimal trongSo) { this.trongSo = trongSo; }
    public String getMoTaChiTiet() { return moTaChiTiet; }
    public void setMoTaChiTiet(String moTaChiTiet) { this.moTaChiTiet = moTaChiTiet; }
    public UUID getNguoiGanId() { return nguoiGanId; }
    public void setNguoiGanId(UUID nguoiGanId) { this.nguoiGanId = nguoiGanId; }
    public TrangThaiAssignment getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiAssignment trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public BigDecimal getDiemTuDanhGia() { return diemTuDanhGia; }
    public void setDiemTuDanhGia(BigDecimal diemTuDanhGia) { this.diemTuDanhGia = diemTuDanhGia; }
    public BigDecimal getDiemManager() { return diemManager; }
    public void setDiemManager(BigDecimal diemManager) { this.diemManager = diemManager; }
    public BigDecimal getDiemTrungBinh() { return diemTrungBinh; }
    public void setDiemTrungBinh(BigDecimal diemTrungBinh) { this.diemTrungBinh = diemTrungBinh; }
    public String getXepLoaiCuoi() { return xepLoaiCuoi; }
    public void setXepLoaiCuoi(String xepLoaiCuoi) { this.xepLoaiCuoi = xepLoaiCuoi; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
}
