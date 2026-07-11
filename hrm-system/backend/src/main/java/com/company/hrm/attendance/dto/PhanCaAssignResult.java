package com.company.hrm.attendance.dto;

import java.util.List;

/**
 * Kết quả phân ca (bulk / rotating): tổng hợp số bản ghi tạo / bỏ qua / lỗi.
 */
public class PhanCaAssignResult {

    private int created;
    private int skipped;
    private List<PhanCaResponse> assignments;

    public PhanCaAssignResult() {}

    public PhanCaAssignResult(int created, int skipped, List<PhanCaResponse> assignments) {
        this.created = created;
        this.skipped = skipped;
        this.assignments = assignments;
    }

    public int getCreated() { return created; }
    public void setCreated(int created) { this.created = created; }
    public int getSkipped() { return skipped; }
    public void setSkipped(int skipped) { this.skipped = skipped; }
    public List<PhanCaResponse> getAssignments() { return assignments; }
    public void setAssignments(List<PhanCaResponse> assignments) { this.assignments = assignments; }
}
