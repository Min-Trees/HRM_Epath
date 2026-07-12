package com.company.hrm.hr.offboarding.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T14 - Ho so nghi viec cua nhan vien.
 *
 * <p>Workflow:
 * <pre>
 *   MOI_TAO -> CHO_DUYET -> DANG_THUC_HIEN -> CHO_QUYET_TOAN -> HOAN_THANH
 *      \-> HUY
 * </pre>
 *
 * <p>Khi HOAN_THANH:
 * <ul>
 *   <li>Set trang_thai cua nhan_vien = DA_NGHI_VIEC (hoac DA_NGHI_HUU)</li>
 *   <li>Tao bien dong BHXH loai BAO_GIAM (ly_do = NGHI_VIEC)</li>
 *   <li>Chot so BHXH (neu co)</li>
 *   <li>Quyet toan thue TNCN cuoi cung</li>
 * </ul>
 */
@Entity
@Table(name = "offboarding_case", schema = "hr")
public class OffboardingCase extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "so_quyet_dinh", nullable = false, length = 50)
    private String soQuyetDinh;

    @Column(name = "ngay_quyet_dinh", nullable = false)
    private LocalDate ngayQuyetDinh;

    @Column(name = "ngay_nghi_viec_cuoi", nullable = false)
    private LocalDate ngayNghiViecCuoi;

    @Column(name = "ngay_chinh_thuc_nghi", nullable = false)
    private LocalDate ngayChinhThucNghi;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ly_do", nullable = false, columnDefinition = "hr.ly_do_nghi_viec")
    private LyDoNghiViec lyDo;

    @Column(name = "ly_do_chi_tiet", columnDefinition = "TEXT")
    private String lyDoChiTiet;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "hr.trang_thai_offboarding")
    private TrangThaiOffboarding trangThai = TrangThaiOffboarding.MOI_TAO;

    @Column(name = "nguoi_tao_id")
    private UUID nguoiTaoId;

    @Column(name = "nguoi_duyet_id")
    private UUID nguoiDuyetId;

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "bhxh_bien_dong_giam_id")
    private UUID bhxhBienDongGiamId;

    @Column(name = "quyet_toan_thue_id")
    private UUID quyetToanThueId;

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getSoQuyetDinh() { return soQuyetDinh; }
    public void setSoQuyetDinh(String soQuyetDinh) { this.soQuyetDinh = soQuyetDinh; }
    public LocalDate getNgayQuyetDinh() { return ngayQuyetDinh; }
    public void setNgayQuyetDinh(LocalDate ngayQuyetDinh) { this.ngayQuyetDinh = ngayQuyetDinh; }
    public LocalDate getNgayNghiViecCuoi() { return ngayNghiViecCuoi; }
    public void setNgayNghiViecCuoi(LocalDate ngayNghiViecCuoi) { this.ngayNghiViecCuoi = ngayNghiViecCuoi; }
    public LocalDate getNgayChinhThucNghi() { return ngayChinhThucNghi; }
    public void setNgayChinhThucNghi(LocalDate ngayChinhThucNghi) { this.ngayChinhThucNghi = ngayChinhThucNghi; }
    public LyDoNghiViec getLyDo() { return lyDo; }
    public void setLyDo(LyDoNghiViec lyDo) { this.lyDo = lyDo; }
    public String getLyDoChiTiet() { return lyDoChiTiet; }
    public void setLyDoChiTiet(String lyDoChiTiet) { this.lyDoChiTiet = lyDoChiTiet; }
    public TrangThaiOffboarding getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiOffboarding trangThai) { this.trangThai = trangThai; }
    public UUID getNguoiTaoId() { return nguoiTaoId; }
    public void setNguoiTaoId(UUID nguoiTaoId) { this.nguoiTaoId = nguoiTaoId; }
    public UUID getNguoiDuyetId() { return nguoiDuyetId; }
    public void setNguoiDuyetId(UUID nguoiDuyetId) { this.nguoiDuyetId = nguoiDuyetId; }
    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public void setNgayDuyet(LocalDateTime ngayDuyet) { this.ngayDuyet = ngayDuyet; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getBhxhBienDongGiamId() { return bhxhBienDongGiamId; }
    public void setBhxhBienDongGiamId(UUID bhxhBienDongGiamId) { this.bhxhBienDongGiamId = bhxhBienDongGiamId; }
    public UUID getQuyetToanThueId() { return quyetToanThueId; }
    public void setQuyetToanThueId(UUID quyetToanThueId) { this.quyetToanThueId = quyetToanThueId; }
}
