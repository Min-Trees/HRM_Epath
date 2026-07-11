package com.company.hrm.attendance.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity cho bảng {@code timekeeping.nghi_phep}.
 *
 * <p>Đơn nghỉ phép được duyệt theo quy trình 2 cấp:
 * {@code CHO_DUYET → DUYET_CAP_1 (MANAGER) → DA_DUYET (HR)}, hoặc
 * {@code TU_CHOI} ở bất kỳ cấp nào. Với {@link LoaiNghiPhep#PHEP_NAM},
 * quỹ phép chỉ bị trừ khi đạt {@code DA_DUYET}; khi hủy sau duyệt thì hoàn lại.
 *
 * <p>{@code soNgayNghi} được tính theo lịch phân ca của NV (xem {@link com.company.hrm.attendance.service.NghiPhepService}).
 */
@Entity
@Table(name = "nghi_phep", schema = "timekeeping")
public class NghiPhep extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "nghi_phep_id")
    private UUID nghiPhepId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_nghi_phep", nullable = false)
    private LoaiNghiPhep loaiNghiPhep;

    @Column(name = "tu_ngay", nullable = false)
    private LocalDate tuNgay;

    @Column(name = "den_ngay", nullable = false)
    private LocalDate denNgay;

    @Column(name = "so_ngay_nghi", nullable = false, precision = 4, scale = 1)
    private BigDecimal soNgayNghi;

    @Column(name = "ly_do", length = 500)
    private String lyDo;

    @Column(name = "file_dinh_kem_url", length = 500)
    private String fileDinhKemUrl;

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

    public UUID getNghiPhepId() { return nghiPhepId; }
    public void setNghiPhepId(UUID nghiPhepId) { this.nghiPhepId = nghiPhepId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public LoaiNghiPhep getLoaiNghiPhep() { return loaiNghiPhep; }
    public void setLoaiNghiPhep(LoaiNghiPhep loaiNghiPhep) { this.loaiNghiPhep = loaiNghiPhep; }
    public LocalDate getTuNgay() { return tuNgay; }
    public void setTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public void setDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }
    public BigDecimal getSoNgayNghi() { return soNgayNghi; }
    public void setSoNgayNghi(BigDecimal soNgayNghi) { this.soNgayNghi = soNgayNghi; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public void setFileDinhKemUrl(String fileDinhKemUrl) { this.fileDinhKemUrl = fileDinhKemUrl; }
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