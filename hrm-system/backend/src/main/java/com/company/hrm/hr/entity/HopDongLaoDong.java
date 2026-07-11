package com.company.hrm.hr.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "hop_dong_lao_dong", schema = "hr",
        uniqueConstraints = @UniqueConstraint(name = "hop_dong_lao_dong_so_hop_dong_key", columnNames = "so_hop_dong"))
public class HopDongLaoDong extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "hop_dong_id")
    private UUID hopDongId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "so_hop_dong", nullable = false, length = 50)
    private String soHopDong;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hop_dong", nullable = false)
    private LoaiHopDong loaiHopDong;

    @Column(name = "hop_dong_goc_id")
    private UUID hopDongGocId;

    @Column(name = "ngay_ky", nullable = false)
    private LocalDate ngayKy;

    @Column(name = "ngay_hieu_luc", nullable = false)
    private LocalDate ngayHieuLuc;

    @Column(name = "ngay_het_hieu_luc")
    private LocalDate ngayHetHieuLuc;

    @Column(name = "muc_luong_thoa_thuan", nullable = false, precision = 14, scale = 2)
    private BigDecimal mucLuongThoaThuan;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "phu_cap_co_dinh", columnDefinition = "jsonb")
    private Map<String, BigDecimal> phuCapCoDinh = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiHopDong trangThai = TrangThaiHopDong.HIEU_LUC;

    @Column(name = "file_dinh_kem_url", length = 500)
    private String fileDinhKemUrl;

    /** Tenant ID — multi-tenant (T11). */
    @Column(name = "company_id")
    private UUID companyId;

    public enum LoaiHopDong {
        THU_VIEC, XAC_DINH_THOI_HAN, KHONG_XAC_DINH_THOI_HAN, PHU_LUC
    }

    public enum TrangThaiHopDong {
        HIEU_LUC, HET_HIEU_LUC, DA_THANH_LY, HUY
    }

    public UUID getHopDongId() { return hopDongId; }
    public void setHopDongId(UUID hopDongId) { this.hopDongId = hopDongId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
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
    public TrangThaiHopDong getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiHopDong trangThai) { this.trangThai = trangThai; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public void setFileDinhKemUrl(String fileDinhKemUrl) { this.fileDinhKemUrl = fileDinhKemUrl; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
}