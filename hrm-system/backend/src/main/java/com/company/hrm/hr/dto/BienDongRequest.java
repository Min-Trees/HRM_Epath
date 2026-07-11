package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.BienDongNhanSu.LoaiBienDong;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BienDongRequest {

    @NotNull
    private UUID nhanVienId;

    @NotNull
    private LoaiBienDong loaiBienDong;

    @NotBlank
    @Size(max = 50)
    private String soQuyetDinh;

    @NotNull
    private LocalDate ngayQuyetDinh;

    @NotNull
    private LocalDate ngayHieuLuc;

    /** Bắt buộc nếu biến động làm đổi phòng ban (DIEU_CHUYEN, BO_NHIEM, ...). */
    private UUID phongBanSauId;

    /** Bắt buộc nếu biến động đổi chức danh / ngạch bậc / quan_ly_truc_tiep. */
    private UUID ngachBacSauId;
    private UUID quanLyTrucTiepSauId;

    /** Bắt buộc nếu biến động đổi lương (DIEU_CHINH_LUONG, THANG_CHUC có luong_sau). */
    private BigDecimal luongSau;

    @NotNull
    private TrangThaiNv trangThaiNvSau;

    private String chucDanhSau;
    private String lyDo;
    private String fileQuyetDinhUrl;

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
    public UUID getPhongBanSauId() { return phongBanSauId; }
    public void setPhongBanSauId(UUID phongBanSauId) { this.phongBanSauId = phongBanSauId; }
    public UUID getNgachBacSauId() { return ngachBacSauId; }
    public void setNgachBacSauId(UUID ngachBacSauId) { this.ngachBacSauId = ngachBacSauId; }
    public UUID getQuanLyTrucTiepSauId() { return quanLyTrucTiepSauId; }
    public void setQuanLyTrucTiepSauId(UUID quanLyTrucTiepSauId) { this.quanLyTrucTiepSauId = quanLyTrucTiepSauId; }
    public BigDecimal getLuongSau() { return luongSau; }
    public void setLuongSau(BigDecimal luongSau) { this.luongSau = luongSau; }
    public TrangThaiNv getTrangThaiNvSau() { return trangThaiNvSau; }
    public void setTrangThaiNvSau(TrangThaiNv trangThaiNvSau) { this.trangThaiNvSau = trangThaiNvSau; }
    public String getChucDanhSau() { return chucDanhSau; }
    public void setChucDanhSau(String chucDanhSau) { this.chucDanhSau = chucDanhSau; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public String getFileQuyetDinhUrl() { return fileQuyetDinhUrl; }
    public void setFileQuyetDinhUrl(String fileQuyetDinhUrl) { this.fileQuyetDinhUrl = fileQuyetDinhUrl; }
}
