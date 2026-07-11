package com.company.hrm.attendance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cấu hình ngưỡng phát hiện ngoại lệ chấm công.
 *
 * <pre>
 * attendance:
 *   nguong-di-tre-phut: 5        # vào muộn quá X phút → DI_TRE
 *   nguong-ve-som-phut: 5        # ra sớm quá X phút → VE_SOM
 *   giam-cua-gps-cho-phep: false  # có cho phép chấm công GPS ngoài vùng (geofence) — mặc định false (chỉ lưu log)
 * </pre>
 */
@ConfigurationProperties(prefix = "attendance")
public class AttendanceProperties {

    /** Ngưỡng đi trễ (phút). Vào sau giờ bắt đầu ca quá số phút này → {@code DI_TRE}. */
    private int nguongDiTrePhut = 5;

    /** Ngưỡng về sớm (phút). Ra trước giờ kết thúc ca quá số phút này → {@code VE_SOM}. */
    private int nguongVeSomPhut = 5;

    /**
     * (Dành cho geofence sau này.) Có cho phép chấm công GPS khi vị trí ngoài vùng cty?
     * T09 chỉ lưu tọa độ, không validate — giá trị này tồn tại để service nâng cấp sau.
     */
    private boolean giamCuaGpsChoPhep = false;

    public int getNguongDiTrePhut() { return nguongDiTrePhut; }
    public void setNguongDiTrePhut(int nguongDiTrePhut) { this.nguongDiTrePhut = nguongDiTrePhut; }
    public int getNguongVeSomPhut() { return nguongVeSomPhut; }
    public void setNguongVeSomPhut(int nguongVeSomPhut) { this.nguongVeSomPhut = nguongVeSomPhut; }
    public boolean isGiamCuaGpsChoPhep() { return giamCuaGpsChoPhep; }
    public void setGiamCuaGpsChoPhep(boolean giamCuaGpsChoPhep) { this.giamCuaGpsChoPhep = giamCuaGpsChoPhep; }
}