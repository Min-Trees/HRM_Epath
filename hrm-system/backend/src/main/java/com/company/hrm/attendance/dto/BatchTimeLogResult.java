package com.company.hrm.attendance.dto;

/**
 * Kết quả đồng bộ chấm công lô:
 * <ul>
 *   <li>{@code created}: số bản ghi tạo mới thành công.</li>
 *   <li>{@code skipped}: số bản ghi bị bỏ qua (trùng ngày, validation lỗi).</li>
 *   <li>{@code total}: tổng input.</li>
 * </ul>
 */
public class BatchTimeLogResult {
    private final int total;
    private final int created;
    private final int skipped;

    public BatchTimeLogResult(int total, int created, int skipped) {
        this.total = total;
        this.created = created;
        this.skipped = skipped;
    }
    public int getTotal() { return total; }
    public int getCreated() { return created; }
    public int getSkipped() { return skipped; }
}