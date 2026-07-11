package com.company.hrm.attendance.dto;

/**
 * Kết quả khởi tạo quỹ phép hàng loạt.
 */
public class LeaveBalanceInitResult {
    private final int total;
    private final int created;
    private final int skipped;

    public LeaveBalanceInitResult(int total, int created, int skipped) {
        this.total = total;
        this.created = created;
        this.skipped = skipped;
    }
    public int getTotal() { return total; }
    public int getCreated() { return created; }
    public int getSkipped() { return skipped; }
}