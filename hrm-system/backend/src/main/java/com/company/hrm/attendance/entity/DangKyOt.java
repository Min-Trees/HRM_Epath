package com.company.hrm.attendance.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity cho bảng {@code timekeeping.dang_ky_ot}.
 *
 * <p>Đăng ký tăng ca được duyệt theo quy trình 2 cấp giống nghỉ phép:
 * {@code CHO_DUYET → DUYET_CAP_1 (MANAGER) → DA_DUYET (HR)}, hoặc {@code TU_CHOI}.
 * OT chỉ được tính lương khi đạt {@code DA_DUYET}.
 *
 * <p>{@code heSoOt} do client (HR/Manager) truyền — backend không validate với ngày
 * vì có thể OT ngày lễ xin 150% vì lý do đặc biệt.
 */
@Entity
@Table(name = "dang_ky_ot", schema = "timekeeping")
public class DangKyOt extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "ot_id")
    private UUID otId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "ngay_lam_ot", nullable = false)
    private LocalDate ngayLamOt;

    @Column(name = "gio_bat_dau", nullable = false)
    private OffsetDateTime gioBatDau;

    @Column(name = "gio_ket_thuc", nullable = false)
    private OffsetDateTime gioKetThuc;

    @Column(name = "so_gio_ot", nullable = false, precision = 4, scale = 2)
    private BigDecimal soGioOt;

    @Enumerated(EnumType.STRING)
    @Column(name = "he_so_ot", nullable = false)
    private HeSoOt heSoOt;

    @Column(name = "lam_dem", nullable = false)
    private boolean lamDem = false;

    @Column(name = "ly_do", length = 500)
    private String lyDo;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiDon trangThai = TrangThaiDon.CHO_DUYET;

    @Column(name = "duyet_cap_1_boi")
    private UUID duyetCap1Boi;

    @Column(name = "duyet_cap_1_luc")
    private OffsetDateTime duyetCap1Luc;

    @Column(name = "duyet_cap_2_boi")
    private UUID duyetCap2Boi;

    @Column(name = "duyet_cap_2_luc")
    private OffsetDateTime duyetCap2Luc;

    @Column(name = "ghi_chu_duyet", length = 500)
    private String ghiChuDuyet;

    public UUID getOtId() { return otId; }
    public void setOtId(UUID otId) { this.otId = otId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public LocalDate getNgayLamOt() { return ngayLamOt; }
    public void setNgayLamOt(LocalDate ngayLamOt) { this.ngayLamOt = ngayLamOt; }
    public OffsetDateTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(OffsetDateTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public OffsetDateTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(OffsetDateTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public BigDecimal getSoGioOt() { return soGioOt; }
    public void setSoGioOt(BigDecimal soGioOt) { this.soGioOt = soGioOt; }
    public HeSoOt getHeSoOt() { return heSoOt; }
    public void setHeSoOt(HeSoOt heSoOt) { this.heSoOt = heSoOt; }
    public boolean isLamDem() { return lamDem; }
    public void setLamDem(boolean lamDem) { this.lamDem = lamDem; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public TrangThaiDon getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDon trangThai) { this.trangThai = trangThai; }
    public UUID getDuyetCap1Boi() { return duyetCap1Boi; }
    public void setDuyetCap1Boi(UUID duyetCap1Boi) { this.duyetCap1Boi = duyetCap1Boi; }
    public OffsetDateTime getDuyetCap1Luc() { return duyetCap1Luc; }
    public void setDuyetCap1Luc(OffsetDateTime duyetCap1Luc) { this.duyetCap1Luc = duyetCap1Luc; }
    public UUID getDuyetCap2Boi() { return duyetCap2Boi; }
    public void setDuyetCap2Boi(UUID duyetCap2Boi) { this.duyetCap2Boi = duyetCap2Boi; }
    public OffsetDateTime getDuyetCap2Luc() { return duyetCap2Luc; }
    public void setDuyetCap2Luc(OffsetDateTime duyetCap2Luc) { this.duyetCap2Luc = duyetCap2Luc; }
    public String getGhiChuDuyet() { return ghiChuDuyet; }
    public void setGhiChuDuyet(String ghiChuDuyet) { this.ghiChuDuyet = ghiChuDuyet; }
}