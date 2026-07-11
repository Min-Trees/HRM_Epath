package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.HopDongLaoDong.LoaiHopDong;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class HopDongRequest {
    @NotBlank
    private String soHopDong;

    @NotNull
    private LoaiHopDong loaiHopDong;

    private UUID hopDongGocId; // bắt buộc nếu loaiHopDong = PHU_LUC

    @NotNull
    private LocalDate ngayKy;

    @NotNull
    private LocalDate ngayHieuLuc;

    private LocalDate ngayHetHieuLuc; // null nếu KHONG_XAC_DINH_THOI_HAN

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal mucLuongThoaThuan;

    private Map<String, BigDecimal> phuCapCoDinh;
    private String fileDinhKemUrl;

    public String getSoHopDong() { return soHopDong; }
    public void setSoHopDong(String soHopDong) { this.soHopDong = soHopDong; }
    public LoaiHopDong getLoaiHopDong() { return loaiHopDong; }
    public void setLoaiHopDong(LoaiHopDong loaiHopDong) { this.loaiHopDong = loaiHopDong; }
    public UUID getHopDongGocId() { return hopDongGocId; }
    public void setHopDongGocId(UUID hopDongGocId) { this.hopDongGocId = hopDongGocId; }
    public LocalDate getNgayKy() { return ngayKy; }
    public void setNgayKy(LocalDate ngayKy) { this.ngayKy = ngayKy; }
    public LocalDate getNgayHieuLuc() { return ngayHieuLuc; }
    public void setNgayHieuLuc(LocalDate ngayHieuLuc) { this.ngayHieuLuc = ngayHieuLuc; }
    public LocalDate getNgayHetHieuLuc() { return ngayHetHieuLuc; }
    public void setNgayHetHieuLuc(LocalDate ngayHetHieuLuc) { this.ngayHetHieuLuc = ngayHetHieuLuc; }
    public BigDecimal getMucLuongThoaThuan() { return mucLuongThoaThuan; }
    public void setMucLuongThoaThuan(BigDecimal mucLuongThoaThuan) { this.mucLuongThoaThuan = mucLuongThoaThuan; }
    public Map<String, BigDecimal> getPhuCapCoDinh() { return phuCapCoDinh; }
    public void setPhuCapCoDinh(Map<String, BigDecimal> phuCapCoDinh) { this.phuCapCoDinh = phuCapCoDinh; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public void setFileDinhKemUrl(String fileDinhKemUrl) { this.fileDinhKemUrl = fileDinhKemUrl; }
}