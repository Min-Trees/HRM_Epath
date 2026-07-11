package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.ChamCongChiTiet;
import com.company.hrm.attendance.entity.LoaiNgoaiLe;
import com.company.hrm.attendance.entity.NguonChamCong;
import com.company.hrm.attendance.entity.TrangThaiDon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response trả về chi tiết bản ghi chấm công.
 * Dùng cho cả {@code GET /time-logs}, {@code POST /time-logs}, {@code /exceptions}, v.v.
 */
public class TimeLogResponse {

    private UUID chamCongId;
    private UUID nhanVienId;
    private UUID phanCaId;
    private LocalDate ngayChamCong;
    private OffsetDateTime gioVao;
    private OffsetDateTime gioRa;
    private NguonChamCong nguon;
    private Double viTriGpsLat;
    private Double viTriGpsLng;
    private LoaiNgoaiLe loaiNgoaiLe;
    private BigDecimal soGioCong;
    private boolean canGiaiTrinh;
    private String giaiTrinhNoiDung;
    private TrangThaiDon giaiTrinhTrangThai;
    private UUID duyetBoi;
    private OffsetDateTime duyetLuc;

    public static TimeLogResponse from(ChamCongChiTiet e) {
        TimeLogResponse r = new TimeLogResponse();
        r.chamCongId = e.getChamCongId();
        r.nhanVienId = e.getNhanVienId();
        r.phanCaId = e.getPhanCaId();
        r.ngayChamCong = e.getNgayChamCong();
        r.gioVao = e.getGioVao();
        r.gioRa = e.getGioRa();
        r.nguon = e.getNguon();
        r.viTriGpsLat = e.getViTriGpsLat();
        r.viTriGpsLng = e.getViTriGpsLng();
        r.loaiNgoaiLe = e.getLoaiNgoaiLe();
        r.soGioCong = e.getSoGioCong();
        r.canGiaiTrinh = e.isCanGiaiTrinh();
        r.giaiTrinhNoiDung = e.getGiaiTrinhNoiDung();
        r.giaiTrinhTrangThai = e.getGiaiTrinhTrangThai();
        r.duyetBoi = e.getDuyetBoi();
        r.duyetLuc = e.getDuyetLuc();
        return r;
    }

    public UUID getChamCongId() { return chamCongId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public UUID getPhanCaId() { return phanCaId; }
    public LocalDate getNgayChamCong() { return ngayChamCong; }
    public OffsetDateTime getGioVao() { return gioVao; }
    public OffsetDateTime getGioRa() { return gioRa; }
    public NguonChamCong getNguon() { return nguon; }
    public Double getViTriGpsLat() { return viTriGpsLat; }
    public Double getViTriGpsLng() { return viTriGpsLng; }
    public LoaiNgoaiLe getLoaiNgoaiLe() { return loaiNgoaiLe; }
    public BigDecimal getSoGioCong() { return soGioCong; }
    public boolean isCanGiaiTrinh() { return canGiaiTrinh; }
    public String getGiaiTrinhNoiDung() { return giaiTrinhNoiDung; }
    public TrangThaiDon getGiaiTrinhTrangThai() { return giaiTrinhTrangThai; }
    public UUID getDuyetBoi() { return duyetBoi; }
    public OffsetDateTime getDuyetLuc() { return duyetLuc; }
}