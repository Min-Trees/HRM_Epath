package com.company.hrm.system.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Doanh nghiệp / Tenant trong hệ thống HRM.
 *
 * <p>Toàn bộ dữ liệu nghiệp vụ (nhân viên, hợp đồng, bảng lương, ...) đều FK về
 * {@link #companyId} để đảm bảo phân tách dữ liệu giữa các tenant.
 */
@Entity
@Table(name = "company", schema = "system")
public class Company extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "ten_cong_ty", nullable = false, length = 200)
    private String tenCongTy;

    @Column(name = "ma_so_thue", nullable = false, unique = true, length = 20)
    private String maSoThue;

    @Column(name = "ma_so_dkkd", length = 50)
    private String maSoDkkd;

    @Column(name = "dia_chi", length = 500)
    private String diaChi;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "nguoi_dai_dien_phap_luat", length = 200)
    private String nguoiDaiDienPhapLuat;

    @Column(name = "ngay_dang_ky")
    private LocalDate ngayDangKy = LocalDate.now();

    @Column(name = "goi_dich_vu", length = 50)
    private String goiDichVu;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "system.trang_thai_company")
    private TrangThaiCompany trangThai = TrangThaiCompany.HOAT_DONG;

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
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