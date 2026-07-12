package com.company.hrm.payroll.run.dto;

import com.company.hrm.payroll.run.entity.LoaiHinhChuyen;
import com.company.hrm.payroll.run.entity.TrangThaiKyLuong;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class KyLinhLuongDto {

    private UUID kyLinhId;
    private String maKyLinh;

    @NotNull
    @Min(1) @Max(12)
    private Integer thang;

    @NotNull
    @Min(2000) @Max(2100)
    private Integer nam;

    private LocalDate ngayChotCong;
    private LocalDate ngayChiTra;
    private TrangThaiKyLuong trangThai;
    private LoaiHinhChuyen loaiHinhChiTra;
    private Integer tongNhanVien;
    private BigDecimal tongThucLinh;
    private BigDecimal tongBhxhNld;
    private BigDecimal tongThueTncn;
    private String ghiChu;
    private UUID nguoiChayId;
    private UUID nguoiDuyetCap1Id;
    private UUID nguoiDuyetCap2Id;
    private LocalDateTime ngayChay;
    private LocalDateTime ngayDuyetCap1;
    private LocalDateTime ngayDuyetCap2;
    private LocalDateTime ngayChiTraThucTe;
    private String fileZipUrl;

    public UUID getKyLinhId() { return kyLinhId; }
    public void setKyLinhId(UUID kyLinhId) { this.kyLinhId = kyLinhId; }
    public String getMaKyLinh() { return maKyLinh; }
    public void setMaKyLinh(String maKyLinh) { this.maKyLinh = maKyLinh; }
    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { this.thang = thang; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public LocalDate getNgayChotCong() { return ngayChotCong; }
    public void setNgayChotCong(LocalDate ngayChotCong) { this.ngayChotCong = ngayChotCong; }
    public LocalDate getNgayChiTra() { return ngayChiTra; }
    public void setNgayChiTra(LocalDate ngayChiTra) { this.ngayChiTra = ngayChiTra; }
    public TrangThaiKyLuong getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiKyLuong trangThai) { this.trangThai = trangThai; }
    public LoaiHinhChuyen getLoaiHinhChiTra() { return loaiHinhChiTra; }
    public void setLoaiHinhChiTra(LoaiHinhChuyen loaiHinhChiTra) { this.loaiHinhChiTra = loaiHinhChiTra; }
    public Integer getTongNhanVien() { return tongNhanVien; }
    public void setTongNhanVien(Integer tongNhanVien) { this.tongNhanVien = tongNhanVien; }
    public BigDecimal getTongThucLinh() { return tongThucLinh; }
    public void setTongThucLinh(BigDecimal tongThucLinh) { this.tongThucLinh = tongThucLinh; }
    public BigDecimal getTongBhxhNld() { return tongBhxhNld; }
    public void setTongBhxhNld(BigDecimal tongBhxhNld) { this.tongBhxhNld = tongBhxhNld; }
    public BigDecimal getTongThueTncn() { return tongThueTncn; }
    public void setTongThueTncn(BigDecimal tongThueTncn) { this.tongThueTncn = tongThueTncn; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getNguoiChayId() { return nguoiChayId; }
    public void setNguoiChayId(UUID nguoiChayId) { this.nguoiChayId = nguoiChayId; }
    public UUID getNguoiDuyetCap1Id() { return nguoiDuyetCap1Id; }
    public void setNguoiDuyetCap1Id(UUID nguoiDuyetCap1Id) { this.nguoiDuyetCap1Id = nguoiDuyetCap1Id; }
    public UUID getNguoiDuyetCap2Id() { return nguoiDuyetCap2Id; }
    public void setNguoiDuyetCap2Id(UUID nguoiDuyetCap2Id) { this.nguoiDuyetCap2Id = nguoiDuyetCap2Id; }
    public LocalDateTime getNgayChay() { return ngayChay; }
    public void setNgayChay(LocalDateTime ngayChay) { this.ngayChay = ngayChay; }
    public LocalDateTime getNgayDuyetCap1() { return ngayDuyetCap1; }
    public void setNgayDuyetCap1(LocalDateTime ngayDuyetCap1) { this.ngayDuyetCap1 = ngayDuyetCap1; }
    public LocalDateTime getNgayDuyetCap2() { return ngayDuyetCap2; }
    public void setNgayDuyetCap2(LocalDateTime ngayDuyetCap2) { this.ngayDuyetCap2 = ngayDuyetCap2; }
    public LocalDateTime getNgayChiTraThucTe() { return ngayChiTraThucTe; }
    public void setNgayChiTraThucTe(LocalDateTime ngayChiTraThucTe) { this.ngayChiTraThucTe = ngayChiTraThucTe; }
    public String getFileZipUrl() { return fileZipUrl; }
    public void setFileZipUrl(String fileZipUrl) { this.fileZipUrl = fileZipUrl; }
}
