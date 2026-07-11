package com.company.hrm.hr.entity;

import com.company.hrm.common.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bien_dong_nhan_su", schema = "hr")
public class BienDongNhanSu extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "bien_dong_id")
    private UUID bienDongId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_bien_dong", nullable = false)
    private LoaiBienDong loaiBienDong;

    @Column(name = "so_quyet_dinh", nullable = false, length = 50)
    private String soQuyetDinh;

    @Column(name = "ngay_quyet_dinh", nullable = false)
    private LocalDate ngayQuyetDinh;

    @Column(name = "ngay_hieu_luc", nullable = false)
    private LocalDate ngayHieuLuc;

    @Column(name = "phong_ban_truoc_id")
    private UUID phongBanTruocId;

    @Column(name = "phong_ban_sau_id")
    private UUID phongBanSauId;

    @Column(name = "chuc_danh_truoc", length = 200)
    private String chucDanhTruoc;

    @Column(name = "chuc_danh_sau", length = 200)
    private String chucDanhSau;

    @Column(name = "luong_truoc", precision = 14, scale = 2)
    private BigDecimal luongTruoc;

    @Column(name = "luong_sau", precision = 14, scale = 2)
    private BigDecimal luongSau;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_nv_sau", nullable = false)
    private TrangThaiNvSau trangThaiNvSau;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;

    @Column(name = "file_quyet_dinh_url", length = 500)
    private String fileQuyetDinhUrl;

    public enum LoaiBienDong {
        TUYEN_DUNG, BO_NHIEM, DIEU_CHUYEN, THANG_CHUC,
        DIEU_CHINH_LUONG, KY_LUAT, TAM_HOAN_HDLD, CHAM_DUT_HDLD, NGHI_HUU
    }

    /** Trạng thái NV sẽ chuyển thành sau biến động (mirror enum NhanVien.TrangThaiNv). */
    public enum TrangThaiNvSau {
        UNG_VIEN, THU_VIEC, CHINH_THUC, TAM_HOAN_HDLD,
        DA_NGHI_VIEC, DA_NGHI_HUU, LUU_TRU
    }

    public UUID getBienDongId() { return bienDongId; }
    public void setBienDongId(UUID bienDongId) { this.bienDongId = bienDongId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public LoaiBienDong getLoaiBienDong() { return loaiBienDong; }
    public void setLoaiBienDong(LoaiBienDong loaiBienDong) { this.loaiBienDong = loaiBienDong; }
    public String getSoQuyetDinh() { return soQuyetDinh; }
    public void setSoQuyetDinh(String soQuyetDinh) { this.soQuyetDinh = soQuyetDinh; }
    public LocalDate getNgayQuyetDinh() { return ngayQuyetDinh; }
    public void setNgayQuyetDinh(LocalDate ngayQuyetDinh) { this.ngayQuyetDinh = ngayQuyetDinh; }
    public LocalDate getNgayHieuLuc() { return ngayHieuLuc; }
    public void setNgayHieuLuc(LocalDate ngayHieuLuc) { this.ngayHieuLuc = ngayHieuLuc; }
    public UUID getPhongBanTruocId() { return phongBanTruocId; }
    public void setPhongBanTruocId(UUID phongBanTruocId) { this.phongBanTruocId = phongBanTruocId; }
    public UUID getPhongBanSauId() { return phongBanSauId; }
    public void setPhongBanSauId(UUID phongBanSauId) { this.phongBanSauId = phongBanSauId; }
    public String getChucDanhTruoc() { return chucDanhTruoc; }
    public void setChucDanhTruoc(String chucDanhTruoc) { this.chucDanhTruoc = chucDanhTruoc; }
    public String getChucDanhSau() { return chucDanhSau; }
    public void setChucDanhSau(String chucDanhSau) { this.chucDanhSau = chucDanhSau; }
    public BigDecimal getLuongTruoc() { return luongTruoc; }
    public void setLuongTruoc(BigDecimal luongTruoc) { this.luongTruoc = luongTruoc; }
    public BigDecimal getLuongSau() { return luongSau; }
    public void setLuongSau(BigDecimal luongSau) { this.luongSau = luongSau; }
    public TrangThaiNvSau getTrangThaiNvSau() { return trangThaiNvSau; }
    public void setTrangThaiNvSau(TrangThaiNvSau trangThaiNvSau) { this.trangThaiNvSau = trangThaiNvSau; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public String getFileQuyetDinhUrl() { return fileQuyetDinhUrl; }
    public void setFileQuyetDinhUrl(String fileQuyetDinhUrl) { this.fileQuyetDinhUrl = fileQuyetDinhUrl; }
}
