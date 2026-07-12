package com.company.hrm.hr.offboarding.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T14 - Tinh tro cap thoi viec (severance) theo BLLD 2019, Dieu 44-46.
 *
 * <p>Cong thuc:
 * <pre>
 *   So nam tham nien = thoi_gian_lam_viec_thang / 12 (lam tron 2 chu so)
 *   So tien = so_nam_tham_nien * he_so * luong_binh_quan_6_thang
 * </pre>
 *
 * <p>He so:
 * <ul>
 *   <li>0.5 - nghi tu nguyen / het han HD</li>
 *   <li>1.0 - bi sa thai (boi thuong them 1 thang luong)</li>
 * </ul>
 */
@Entity
@Table(name = "severance_calc", schema = "hr")
public class SeveranceCalc {

    @Id
    @GeneratedValue
    @Column(name = "severance_id")
    private UUID severanceId;

    @Column(name = "case_id", nullable = false, unique = true)
    private UUID caseId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "thoi_gian_lam_viec_thang", nullable = false)
    private Integer thoiGianLamViecThang;

    @Column(name = "so_nam_tham_nien", nullable = false, precision = 6, scale = 2)
    private BigDecimal soNamThamNien;

    @Column(name = "luong_thoi_viec_binh_quan", nullable = false, precision = 14, scale = 2)
    private BigDecimal luongThoiViecBinhQuan;

    @Column(name = "he_so", nullable = false, precision = 4, scale = 2)
    private BigDecimal heSo;

    @Column(name = "so_tien_tro_cap", nullable = false, precision = 14, scale = 2)
    private BigDecimal soTienTroCap;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "nguoi_tinh_id")
    private UUID nguoiTinhId;

    @Column(name = "ngay_tinh", nullable = false)
    private LocalDateTime ngayTinh = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getSeveranceId() { return severanceId; }
    public void setSeveranceId(UUID severanceId) { this.severanceId = severanceId; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public Integer getThoiGianLamViecThang() { return thoiGianLamViecThang; }
    public void setThoiGianLamViecThang(Integer thoiGianLamViecThang) { this.thoiGianLamViecThang = thoiGianLamViecThang; }
    public BigDecimal getSoNamThamNien() { return soNamThamNien; }
    public void setSoNamThamNien(BigDecimal soNamThamNien) { this.soNamThamNien = soNamThamNien; }
    public BigDecimal getLuongThoiViecBinhQuan() { return luongThoiViecBinhQuan; }
    public void setLuongThoiViecBinhQuan(BigDecimal luongThoiViecBinhQuan) { this.luongThoiViecBinhQuan = luongThoiViecBinhQuan; }
    public BigDecimal getHeSo() { return heSo; }
    public void setHeSo(BigDecimal heSo) { this.heSo = heSo; }
    public BigDecimal getSoTienTroCap() { return soTienTroCap; }
    public void setSoTienTroCap(BigDecimal soTienTroCap) { this.soTienTroCap = soTienTroCap; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getNguoiTinhId() { return nguoiTinhId; }
    public void setNguoiTinhId(UUID nguoiTinhId) { this.nguoiTinhId = nguoiTinhId; }
    public LocalDateTime getNgayTinh() { return ngayTinh; }
    public void setNgayTinh(LocalDateTime ngayTinh) { this.ngayTinh = ngayTinh; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
