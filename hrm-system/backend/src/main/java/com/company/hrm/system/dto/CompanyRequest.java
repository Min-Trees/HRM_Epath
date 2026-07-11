package com.company.hrm.system.dto;

import com.company.hrm.system.entity.TrangThaiCompany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request tạo / cập nhật doanh nghiệp.
 * {@code maSoThue} dùng regex đơn giản 10–13 chữ số (Việt Nam MST = 10 hoặc 13 số).
 */
public class CompanyRequest {

    @NotBlank
    @Size(max = 200)
    private String tenCongTy;

    @NotBlank
    @Pattern(regexp = "\\d{10,13}", message = "Mã số thuế phải là 10–13 chữ số")
    private String maSoThue;

    @Size(max = 50)
    private String maSoDkkd;

    @Size(max = 500)
    private String diaChi;

    @Size(max = 20)
    private String soDienThoai;

    @Size(max = 200)
    private String email;

    @Size(max = 200)
    private String nguoiDaiDienPhapLuat;

    private LocalDate ngayDangKy;

    @Size(max = 50)
    private String goiDichVu;

    private TrangThaiCompany trangThai;

    public String getTenCongTy() { return tenCongTy; }
    public void setTenCongTy(String tenCongTy) { this.tenCongTy = tenCongTy; }
    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
    public String getMaSoDkkd() { return maSoDkkd; }
    public void setMaSoDkkd(String maSoDkkd) { this.maSoDkkd = maSoDkkd; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNguoiDaiDienPhapLuat() { return nguoiDaiDienPhapLuat; }
    public void setNguoiDaiDienPhapLuat(String nguoiDaiDienPhapLuat) { this.nguoiDaiDienPhapLuat = nguoiDaiDienPhapLuat; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }
    public String getGoiDichVu() { return goiDichVu; }
    public void setGoiDichVu(String goiDichVu) { this.goiDichVu = goiDichVu; }
    public TrangThaiCompany getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiCompany trangThai) { this.trangThai = trangThai; }
}