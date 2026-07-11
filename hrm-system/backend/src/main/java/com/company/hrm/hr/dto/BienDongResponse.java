package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.BienDongNhanSu;
import com.company.hrm.hr.entity.BienDongNhanSu.LoaiBienDong;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class BienDongResponse {

    private UUID bienDongId;
    private UUID nhanVienId;
    private LoaiBienDong loaiBienDong;
    private String soQuyetDinh;
    private LocalDate ngayQuyetDinh;
    private LocalDate ngayHieuLuc;
    private UUID phongBanTruocId;
    private UUID phongBanSauId;
    private String chucDanhTruoc;
    private String chucDanhSau;
    private BigDecimal luongTruoc;
    private BigDecimal luongSau;
    private TrangThaiNv trangThaiNvSau;
    private String lyDo;
    private String fileQuyetDinhUrl;
    private Instant createdAt;

    public static BienDongResponse from(BienDongNhanSu e) {
        BienDongResponse r = new BienDongResponse();
        r.bienDongId = e.getBienDongId();
        r.nhanVienId = e.getNhanVienId();
        r.loaiBienDong = e.getLoaiBienDong();
        r.soQuyetDinh = e.getSoQuyetDinh();
        r.ngayQuyetDinh = e.getNgayQuyetDinh();
        r.ngayHieuLuc = e.getNgayHieuLuc();
        r.phongBanTruocId = e.getPhongBanTruocId();
        r.phongBanSauId = e.getPhongBanSauId();
        r.chucDanhTruoc = e.getChucDanhTruoc();
        r.chucDanhSau = e.getChucDanhSau();
        r.luongTruoc = e.getLuongTruoc();
        r.luongSau = e.getLuongSau();
        r.trangThaiNvSau = mapStatus(e.getTrangThaiNvSau());
        r.lyDo = e.getLyDo();
        r.fileQuyetDinhUrl = e.getFileQuyetDinhUrl();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    private static TrangThaiNv mapStatus(BienDongNhanSu.TrangThaiNvSau s) {
        if (s == null) return null;
        return TrangThaiNv.valueOf(s.name());
    }

    public UUID getBienDongId() { return bienDongId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public LoaiBienDong getLoaiBienDong() { return loaiBienDong; }
    public String getSoQuyetDinh() { return soQuyetDinh; }
    public LocalDate getNgayQuyetDinh() { return ngayQuyetDinh; }
    public LocalDate getNgayHieuLuc() { return ngayHieuLuc; }
    public UUID getPhongBanTruocId() { return phongBanTruocId; }
    public UUID getPhongBanSauId() { return phongBanSauId; }
    public String getChucDanhTruoc() { return chucDanhTruoc; }
    public String getChucDanhSau() { return chucDanhSau; }
    public BigDecimal getLuongTruoc() { return luongTruoc; }
    public BigDecimal getLuongSau() { return luongSau; }
    public TrangThaiNv getTrangThaiNvSau() { return trangThaiNvSau; }
    public String getLyDo() { return lyDo; }
    public String getFileQuyetDinhUrl() { return fileQuyetDinhUrl; }
    public Instant getCreatedAt() { return createdAt; }
}
