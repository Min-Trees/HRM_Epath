package com.company.hrm.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request đồng bộ chấm công hàng loạt từ máy chấm công / app mobile.
 *
 * <p>Service sẽ duyệt từng bản ghi: trùng {@code (nhan_vien_id, ngay_cham_cong)}
 * sẽ bị bỏ qua (giữ bản ghi đầu tiên). Kết quả trả về qua {@link BatchTimeLogResult}.
 */
public class BatchTimeLogRequest {

    @NotEmpty
    @Valid
    private List<TimeLogRequest> records;

    public List<TimeLogRequest> getRecords() { return records; }
    public void setRecords(List<TimeLogRequest> records) { this.records = records; }
}