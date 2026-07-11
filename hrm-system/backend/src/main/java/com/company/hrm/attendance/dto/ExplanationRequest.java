package com.company.hrm.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Nội dung giải trình cho 1 bản ghi chấm công có ngoại lệ.
 * Sau khi NV gửi, bản ghi chuyển sang trạng thái {@code CHO_DUYET}.
 */
public class ExplanationRequest {

    @NotBlank
    @Size(max = 1000)
    private String noiDung;

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
}