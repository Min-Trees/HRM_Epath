package com.company.hrm.attendance.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity cho bảng {@code timekeeping.cham_cong_chi_tiet}.
 *
 * <p>Mỗi bản ghi đại diện cho dữ liệu vào/ra của một nhân viên trong một ngày.
 * Hệ thống tự động đối chiếu với ca chuẩn (do T08 cung cấp) để gán
 * {@link #loaiNgoaiLe} và {@link #canGiaiTrinh}.
 *
 * <p><b>GPS deviation:</b> schema khai báo {@code vi_tri_gps POINT} (PostGIS).
 * Tầng JPA lưu thành 2 cột {@code vi_tri_gps_lat} / {@code vi_tri_gps_lng}
 * để thuận tiện cho query; khi schema thật dùng PostGIS có thể đổi sang
 * custom type mà không ảnh hưởng API.
 *
 * <p>Unique constraint {@code (nhan_vien_id, ngay_cham_cong)} đảm bảo mỗi NV chỉ có
 * 1 bản ghi/ngày; service xử lý trùng lặp ở tầng batch bằng cách bỏ qua.
 */
@Entity
@Table(name = "cham_cong_chi_tiet", schema = "timekeeping",
        uniqueConstraints = @UniqueConstraint(name = "uq_ccct_nv_ngay",
                columnNames = {"nhan_vien_id", "ngay_cham_cong"}))
public class ChamCongChiTiet extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "cham_cong_id")
    private UUID chamCongId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "phan_ca_id")
    private UUID phanCaId;

    @Column(name = "ngay_cham_cong", nullable = false)
    private LocalDate ngayChamCong;

    @Column(name = "gio_vao")
    private OffsetDateTime gioVao;

    @Column(name = "gio_ra")
    private OffsetDateTime gioRa;

    @Enumerated(EnumType.STRING)
    @Column(name = "nguon", nullable = false)
    private NguonChamCong nguon;

    @Column(name = "vi_tri_gps_lat")
    private Double viTriGpsLat;

    @Column(name = "vi_tri_gps_lng")
    private Double viTriGpsLng;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_ngoai_le", nullable = false)
    private LoaiNgoaiLe loaiNgoaiLe = LoaiNgoaiLe.KHONG_NGOAI_LE;

    @Column(name = "so_gio_cong", precision = 4, scale = 2)
    private BigDecimal soGioCong;

    @Column(name = "can_giai_trinh", nullable = false)
    private boolean canGiaiTrinh = false;

    @Column(name = "giai_trinh_noi_dung")
    private String giaiTrinhNoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "giai_trinh_trang_thai")
    private TrangThaiDon giaiTrinhTrangThai;

    @Column(name = "duyet_boi")
    private UUID duyetBoi;

    @Column(name = "duyet_luc")
    private OffsetDateTime duyetLuc;

    public UUID getChamCongId() { return chamCongId; }
    public void setChamCongId(UUID chamCongId) { this.chamCongId = chamCongId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public UUID getPhanCaId() { return phanCaId; }
    public void setPhanCaId(UUID phanCaId) { this.phanCaId = phanCaId; }
    public LocalDate getNgayChamCong() { return ngayChamCong; }
    public void setNgayChamCong(LocalDate ngayChamCong) { this.ngayChamCong = ngayChamCong; }
    public OffsetDateTime getGioVao() { return gioVao; }
    public void setGioVao(OffsetDateTime gioVao) { this.gioVao = gioVao; }
    public OffsetDateTime getGioRa() { return gioRa; }
    public void setGioRa(OffsetDateTime gioRa) { this.gioRa = gioRa; }
    public NguonChamCong getNguon() { return nguon; }
    public void setNguon(NguonChamCong nguon) { this.nguon = nguon; }
    public Double getViTriGpsLat() { return viTriGpsLat; }
    public void setViTriGpsLat(Double viTriGpsLat) { this.viTriGpsLat = viTriGpsLat; }
    public Double getViTriGpsLng() { return viTriGpsLng; }
    public void setViTriGpsLng(Double viTriGpsLng) { this.viTriGpsLng = viTriGpsLng; }
    public LoaiNgoaiLe getLoaiNgoaiLe() { return loaiNgoaiLe; }
    public void setLoaiNgoaiLe(LoaiNgoaiLe loaiNgoaiLe) { this.loaiNgoaiLe = loaiNgoaiLe; }
    public BigDecimal getSoGioCong() { return soGioCong; }
    public void setSoGioCong(BigDecimal soGioCong) { this.soGioCong = soGioCong; }
    public boolean isCanGiaiTrinh() { return canGiaiTrinh; }
    public void setCanGiaiTrinh(boolean canGiaiTrinh) { this.canGiaiTrinh = canGiaiTrinh; }
    public String getGiaiTrinhNoiDung() { return giaiTrinhNoiDung; }
    public void setGiaiTrinhNoiDung(String giaiTrinhNoiDung) { this.giaiTrinhNoiDung = giaiTrinhNoiDung; }
    public TrangThaiDon getGiaiTrinhTrangThai() { return giaiTrinhTrangThai; }
    public void setGiaiTrinhTrangThai(TrangThaiDon giaiTrinhTrangThai) { this.giaiTrinhTrangThai = giaiTrinhTrangThai; }
    public UUID getDuyetBoi() { return duyetBoi; }
    public void setDuyetBoi(UUID duyetBoi) { this.duyetBoi = duyetBoi; }
    public OffsetDateTime getDuyetLuc() { return duyetLuc; }
    public void setDuyetLuc(OffsetDateTime duyetLuc) { this.duyetLuc = duyetLuc; }
}