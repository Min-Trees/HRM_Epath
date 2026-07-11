package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.HopDongLaoDong;
import com.company.hrm.hr.entity.HopDongLaoDong.LoaiHopDong;
import com.company.hrm.hr.entity.HopDongLaoDong.TrangThaiHopDong;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class HopDongResponse {
    private UUID hopDongId;
    private UUID nhanVienId;
    private String soHopDong;
    private LoaiHopDong loaiHopDong;
    private UUID hopDongGocId;
    private LocalDate ngayKy;
    private LocalDate ngayHieuLuc;
    private LocalDate ngayHetHieuLuc;
    private BigDecimal mucLuongThoaThuan;
    private Map<String, BigDecimal> phuCapCoDinh;
    private TrangThaiHopDong trangThai;
    private String fileDinhKemUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public static HopDongResponse from(HopDongLaoDong e) {
        HopDongResponse r = new HopDongResponse();
        r.hopDongId = e.getHopDongId();
        r.nhanVienId = e.getNhanVienId();
        r.soHopDong = e.getSoHopDong();
        r.loaiHopDong = e.getLoaiHopDong();
        r.hopDongGocId = e.getHopDongGocId();
        r.ngayKy = e.getNgayKy();
        r.ngayHieuLuc = e.getNgayHieuLuc();
        r.ngayHetHieuLuc = e.getNgayHetHieuLuc();
        r.mucLuongThoaThuan = e.getMucLuongThoaThuan();
        r.phuCapCoDinh = e.getPhuCapCoDinh();
        r.trangThai = e.getTrangThai();
        r.fileDinhKemUrl = e.getFileDinhKemUrl();
        r.createdAt = e.getCreatedAt();
        r.updatedAt = e.getUpdatedAt();
        return r;
    }

    public UUID getHopDongId() { return hopDongId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public String getSoHopDong() { return soHopDong; }
    public LoaiHopDong getLoaiHopDong() { return loaiHopDong; }
    public UUID getHopDongGocId() { return hopDongGocId; }
    public LocalDate getNgayKy() { return ngayKy; }
    public LocalDate getNgayHieuLuc() { return ngayHieuLuc; }
    public LocalDate getNgayHetHieuLuc() { return ngayHetHieuLuc; }
    public BigDecimal getMucLuongThoaThuan() { return mucLuongThoaThuan; }
    public Map<String, BigDecimal> getPhuCapCoDinh() { return phuCapCoDinh; }
    public TrangThaiHopDong getTrangThai() { return trangThai; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}