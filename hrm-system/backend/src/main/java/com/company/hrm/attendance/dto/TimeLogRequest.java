package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.NguonChamCong;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Request ghi nhận 1 bản ghi chấm công cho (NV, ngày).
 *
 * <p>Quy tắc đối chiếu tự động (T09 service):
 * <ul>
 *   <li>Không có phân ca ngày đó → {@code LAM_NGOAI_CA}.</li>
 *   <li>Thiếu {@code gioVao} hoặc {@code gioRa} → {@code THIEU_CONG}.</li>
 *   <li>Vào muộn / ra sớm quá ngưỡng → {@code DI_TRE} / {@code VE_SOM}.</li>
 * </ul>
 *
 * <p>Nếu {@link #nguon} = {@code THU_CONG} thì bản ghi tạo ở trạng thái
 * {@code CHO_DUYET}, chờ HR/MANAGER duyệt mới coi là hợp lệ.
 */
public class TimeLogRequest {

    @NotNull
    private UUID nhanVienId;

    @NotNull
    private LocalDate ngayChamCong;

    /** Nullable: nếu chỉ log vào thì truyền, ra=null; ngược lại. */
    private OffsetDateTime gioVao;

    private OffsetDateTime gioRa;

    @NotNull
    private NguonChamCong nguon;

    /** Bắt buộc khi {@link #nguon} = {@code GPS_MOBILE}. */
    private Double viTriGpsLat;
    private Double viTriGpsLng;

    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
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
}