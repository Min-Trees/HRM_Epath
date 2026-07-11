package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;

import java.time.LocalDate;

/**
 * Trạng thái phái sinh của NV tại một ngày bất kỳ, suy ra từ chuỗi biến động.
 */
public class BienDongTimelineItem {

    private LocalDate ngay;
    private TrangThaiNv trangThai;
    private String lyDo;

    public BienDongTimelineItem() {}

    public BienDongTimelineItem(LocalDate ngay, TrangThaiNv trangThai, String lyDo) {
        this.ngay = ngay;
        this.trangThai = trangThai;
        this.lyDo = lyDo;
    }

    public LocalDate getNgay() { return ngay; }
    public void setNgay(LocalDate ngay) { this.ngay = ngay; }
    public TrangThaiNv getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNv trangThai) { this.trangThai = trangThai; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}
