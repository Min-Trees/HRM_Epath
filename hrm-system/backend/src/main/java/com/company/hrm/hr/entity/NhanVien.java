package com.company.hrm.hr.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "nhan_vien", schema = "hr",
        uniqueConstraints = {
                @UniqueConstraint(name = "nhan_vien_ma_nv_key", columnNames = "ma_nv"),
                @UniqueConstraint(name = "nhan_vien_so_cccd_key", columnNames = "so_cccd")
        })
public class NhanVien extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "nhan_vien_id")
    private UUID nhanVienId;

    @Column(name = "ma_nv", nullable = false, length = 20)
    private String maNv;

    @Column(name = "ho_ten", nullable = false, length = 200)
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh")
    private GioiTinh gioiTinh;

    @Column(name = "so_cccd", length = 20)
    private String soCccd;

    @Column(name = "ngay_cap_cccd")
    private LocalDate ngayCapCccd;

    @Column(name = "noi_cap_cccd", length = 200)
    private String noiCapCccd;

    @Column(name = "que_quan", length = 300)
    private String queQuan;

    @Column(name = "dia_chi_thuong_tru", length = 300)
    private String diaChiThuongTru;

    @Column(name = "dia_chi_lien_lac", length = 300)
    private String diaChiLienLac;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "trinh_do_hoc_van", length = 200)
    private String trinhDoHocVan;

    @Column(name = "ngay_vao_lam", nullable = false)
    private LocalDate ngayVaoLam;

    @Column(name = "phong_ban_id", nullable = false)
    private UUID phongBanId;

    @Column(name = "ngach_bac_id")
    private UUID ngachBacId;

    @Column(name = "quan_ly_truc_tiep_id")
    private UUID quanLyTrucTiepId;

    /** Trường phái sinh — chỉ T07 (biến động nhân sự) được phép thay đổi. */
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiNv trangThai = TrangThaiNv.UNG_VIEN;

    @Column(name = "tai_khoan_cham_cong_id", length = 50)
    private String taiKhoanChamCongId;

    /** Tenant ID — multi-tenant (T11). Null nếu chưa được seed V6 backfill. */
    @Column(name = "company_id")
    private UUID companyId;

    /** Ma so thue ca nhan (T16 - QTT thue TNCN). */
    @Column(name = "ma_so_thue", length = 20)
    private String maSoThue;

    public enum GioiTinh { NAM, NU, KHAC }

    public enum TrangThaiNv {
        UNG_VIEN, THU_VIEC, CHINH_THUC, TAM_HOAN_HDLD,
        DA_NGHI_VIEC, DA_NGHI_HUU, LUU_TRU
    }

    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public GioiTinh getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(GioiTinh gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getSoCccd() { return soCccd; }
    public void setSoCccd(String soCccd) { this.soCccd = soCccd; }
    public LocalDate getNgayCapCccd() { return ngayCapCccd; }
    public void setNgayCapCccd(LocalDate ngayCapCccd) { this.ngayCapCccd = ngayCapCccd; }
    public String getNoiCapCccd() { return noiCapCccd; }
    public void setNoiCapCccd(String noiCapCccd) { this.noiCapCccd = noiCapCccd; }
    public String getQueQuan() { return queQuan; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }
    public String getDiaChiThuongTru() { return diaChiThuongTru; }
    public void setDiaChiThuongTru(String diaChiThuongTru) { this.diaChiThuongTru = diaChiThuongTru; }
    public String getDiaChiLienLac() { return diaChiLienLac; }
    public void setDiaChiLienLac(String diaChiLienLac) { this.diaChiLienLac = diaChiLienLac; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTrinhDoHocVan() { return trinhDoHocVan; }
    public void setTrinhDoHocVan(String trinhDoHocVan) { this.trinhDoHocVan = trinhDoHocVan; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public UUID getPhongBanId() { return phongBanId; }
    public void setPhongBanId(UUID phongBanId) { this.phongBanId = phongBanId; }
    public UUID getNgachBacId() { return ngachBacId; }
    public void setNgachBacId(UUID ngachBacId) { this.ngachBacId = ngachBacId; }
    public UUID getQuanLyTrucTiepId() { return quanLyTrucTiepId; }
    public void setQuanLyTrucTiepId(UUID quanLyTrucTiepId) { this.quanLyTrucTiepId = quanLyTrucTiepId; }
    public TrangThaiNv getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNv trangThai) { this.trangThai = trangThai; }
    public String getTaiKhoanChamCongId() { return taiKhoanChamCongId; }
    public void setTaiKhoanChamCongId(String taiKhoanChamCongId) { this.taiKhoanChamCongId = taiKhoanChamCongId; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
}