package com.company.hrm.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class AddendumRequest {
    @NotBlank
    private String soHopDong;

    @NotNull
    private LocalDate ngayKy;

    @NotNull
    private LocalDate ngayHieuLuc;

    /** null = phụ lục không kéo dài hạn hợp đồng. */
    private LocalDate ngayHetHieuLucMoi;

    private String noiDung;
    private String fileDinhKemUrl;

    public String getSoHopDong() { return soHopDong; }
    public void setSoHopDong(String soHopDong) { this.soHopDong = soHopDong; }
    public LocalDate getNgayKy() { return ngayKy; }
    public void setNgayKy(LocalDate ngayKy) { this.ngayKy = ngayKy; }
    public LocalDate getNgayHieuLuc() { return ngayHieuLuc; }
    public void setNgayHieuLuc(LocalDate ngayHieuLuc) { this.ngayHieuLuc = ngayHieuLuc; }
    public LocalDate getNgayHetHieuLucMoi() { return ngayHetHieuLucMoi; }
    public void setNgayHetHieuLucMoi(LocalDate ngayHetHieuLucMoi) { this.ngayHetHieuLucMoi = ngayHetHieuLucMoi; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public void setFileDinhKemUrl(String fileDinhKemUrl) { this.fileDinhKemUrl = fileDinhKemUrl; }
}