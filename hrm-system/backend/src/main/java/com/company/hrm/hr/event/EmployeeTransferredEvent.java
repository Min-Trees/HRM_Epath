package com.company.hrm.hr.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Phát khi NV được điều chuyển sang phòng ban khác.
 * Module chấm công lắng nghe để cập nhật cấu hình chấm công theo PB mới.
 */
public record EmployeeTransferredEvent(
        UUID nhanVienId,
        UUID phongBanCu,
        UUID phongBanMoi,
        LocalDate ngayHieuLuc) {
}
