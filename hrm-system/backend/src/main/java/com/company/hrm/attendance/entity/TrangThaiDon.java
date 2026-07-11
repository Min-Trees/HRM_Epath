package com.company.hrm.attendance.entity;

/**
 * Trạng thái đơn giải trình / duyệt chấm công.
 * Mirror enum {@code timekeeping.trang_thai_don} trong schema.
 *
 * <ul>
 *   <li>{@link #CHO_DUYET}: chờ HR/MANAGER duyệt (mặc định cho giải trình và nguồn THU_CONG).</li>
 *   <li>{@link #DUYET_CAP_1}: cấp 1 đã duyệt (2-step approval — dùng cho OT/nghỉ phép).</li>
 *   <li>{@link #DA_DUYET}: đã duyệt xong.</li>
 *   <li>{@link #TU_CHOI}: bị từ chối.</li>
 *   <li>{@link #HUY}: NV hủy đơn.</li>
 * </ul>
 *
 * <p>Giải trình chấm công chỉ dùng {@link #CHO_DUYET}, {@link #DA_DUYET}, {@link #TU_CHOI}.
 * Các giá trị còn lại kế thừa để đồng bộ với OT/nghỉ phép sau này.
 */
public enum TrangThaiDon {
    CHO_DUYET,
    DUYET_CAP_1,
    DA_DUYET,
    TU_CHOI,
    HUY
}