package com.company.hrm.recruitment.dto;

import com.company.hrm.recruitment.entity.LoaiHopDongDeNghi;
import com.company.hrm.recruitment.entity.TrangThaiQuyetDinh;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class QuyetDinhTuyenDto {

    private UUID quyetDinhId;

    @NotNull
    private UUID ungVienId;

    @NotNull
    private UUID nguoiQuyetDinhId;

    @NotNull
    private LoaiHopDongDeNghi loaiHopDong;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal mucLuongDeNghi;

    @NotNull
    private LocalDate ngayVaoLamDeNghi;

    @NotNull
    private UUID phongBanId;

    private String chucDanh;
    private Integer thoiHanThuViecThang;
    private String ghiChu;
    private TrangThaiQuyetDinh trangThai;
    private LocalDate ngayUngVienPhanHoi;
    private UUID nhanVienMoiId;
    private LocalDateTime createdAt;

    private String tenUngVien;
    private String tenPhongBan;

    public UUID getQuyetDinhId() { return quyetDinhId; }
    public void setQuyetDinhId(UUID quyetDinhId) { this.quyetDinhId = quyetDinhId; }
    public UUID getUngVienId() { return ungVienId; }
    public void setUngVienId(UUID ungVienId) { this.ungVienId = ungVienId; }
    public UUID getNguoiQuyetDinhId() { return nguoiQuyetDinhId; }
    public void setNguoiQuyetDinhId(UUID nguoiQuyetDinhId) { this.nguoiQuyetDinhId = nguoiQuyetDinhId; }
    public LoaiHopDongDeNghi getLoaiHopDong() { return loaiHopDong; }
    public void setLoaiHopDong(LoaiHopDongDeNghi loaiHopDong) { this.loaiHopDong = loaiHopDong; }
    public BigDecimal getMucLuongDeNghi() { return mucLuongDeNghi; }
    public void setMucLuongDeNghi(BigDecimal mucLuongDeNghi) { this.mucLuongDeNghi = mucLuongDeNghi; }
    public LocalDate getNgayVaoLamDeNghi() { return ngayVaoLamDeNghi; }
    public void setNgayVaoLamDeNghi(LocalDate ngayVaoLamDeNghi) { this.ngayVaoLamDeNghi = ngayVaoLamDeNghi; }
    public UUID getPhongBanId() { return phongBanId; }
    public void setPhongBanId(UUID phongBanId) { this.phongBanId = phongBanId; }
    public String getChucDanh() { return chucDanh; }
    public void setChucDanh(String chucDanh) { this.chucDanh = chucDanh; }
    public Integer getThoiHanThuViecThang() { return thoiHanThuViecThang; }
    public void setThoiHanThuViecThang(Integer thoiHanThuViecThang) { this.thoiHanThuViecThang = thoiHanThuViecThang; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public TrangThaiQuyetDinh getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiQuyetDinh trangThai) { this.trangThai = trangThai; }
    public LocalDate getNgayUngVienPhanHoi() { return ngayUngVienPhanHoi; }
    public void setNgayUngVienPhanHoi(LocalDate ngayUngVienPhanHoi) { this.ngayUngVienPhanHoi = ngayUngVienPhanHoi; }
    public UUID getNhanVienMoiId() { return nhanVienMoiId; }
    public void setNhanVienMoiId(UUID nhanVienMoiId) { this.nhanVienMoiId = nhanVienMoiId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getTenUngVien() { return tenUngVien; }
    public void setTenUngVien(String tenUngVien) { this.tenUngVien = tenUngVien; }
    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }
}
