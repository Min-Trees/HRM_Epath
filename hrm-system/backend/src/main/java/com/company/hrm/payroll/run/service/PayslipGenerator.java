package com.company.hrm.payroll.run.service;

import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.payroll.run.dto.PayslipDto;
import com.company.hrm.payroll.run.entity.KyLinhLuong;
import com.company.hrm.payroll.run.entity.LoaiKhoanLuong;
import com.company.hrm.payroll.run.repository.KhoanLuongRepository;
import com.company.hrm.payroll.run.repository.KyLinhLuongRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * T19 - Tao phieu luong cho NV trong 1 ky linh luong.
 *
 * <p>Doc payroll.bang_luong_thang + payroll.khoan_luong de tong hop
 * thanh PayslipDto (HTML/PDF sau).
 */
@Component
public class PayslipGenerator {

    private final JdbcTemplate jdbc;
    private final KyLinhLuongRepository kyRepo;
    private final KhoanLuongRepository khoanRepo;
    private final NhanVienRepository nvRepo;

    public PayslipGenerator(JdbcTemplate jdbc, KyLinhLuongRepository kyRepo,
                            KhoanLuongRepository khoanRepo, NhanVienRepository nvRepo) {
        this.jdbc = jdbc;
        this.kyRepo = kyRepo;
        this.khoanRepo = khoanRepo;
        this.nvRepo = nvRepo;
    }

    /**
     * Tao phieu luong cho 1 NV trong ky.
     */
    public PayslipDto generateForNhanVien(KyLinhLuong ky, UUID nhanVienId) {
        NhanVien nv = nvRepo.findById(nhanVienId).orElseThrow(() ->
                new IllegalArgumentException("NV not found: " + nhanVienId));

        String sql = "SELECT COALESCE(luong_co_ban, 0) AS luong_co_ban, " +
                "       COALESCE(phu_cap, 0) AS phu_cap, " +
                "       COALESCE(tien_ot, 0) AS tien_ot, " +
                "       COALESCE(muc_luong_dong_bhxh, 0) AS muc_bhxh, " +
                "       COALESCE(bhxh_nld, 0) AS bhxh_nld, " +
                "       COALESCE(thue_tncn, 0) AS thue_tncn, " +
                "       COALESCE(tam_ung, 0) AS tam_ung, " +
                "       COALESCE(khau_tru_khac, 0) AS khau_tru_khac, " +
                "       COALESCE(thuc_linh, 0) AS thuc_linh " +
                "  FROM payroll.bang_luong_thang " +
                " WHERE nhan_vien_id = ? AND thang = ? AND nam = ?";

        List<java.util.Map<String, Object>> rows = jdbc.queryForList(sql, nhanVienId,
                ky.getThang(), ky.getNam());
        if (rows.isEmpty()) {
            throw new IllegalStateException("Chua co bang luong cho NV " + nhanVienId +
                    " trong ky " + ky.getThang() + "/" + ky.getNam());
        }

        java.util.Map<String, Object> r = rows.get(0);
        PayslipDto p = new PayslipDto();
        p.setThang(ky.getThang());
        p.setNam(ky.getNam());
        p.setNhanVienId(nhanVienId);
        p.setMaNv(nv.getMaNv());
        p.setHoTen(nv.getHoTen());

        BigDecimal luongCoBan = toB(r.get("luong_co_ban"));
        BigDecimal phuCap = toB(r.get("phu_cap"));
        BigDecimal tienOt = toB(r.get("tien_ot"));

        p.setLuongCoBan(luongCoBan);
        p.setPhuCap(phuCap);
        p.setTienOt(tienOt);
        p.setMucDongBhxh(toB(r.get("muc_bhxh")));
        p.setBhxhNld(toB(r.get("bhxh_nld")));
        p.setThueTncn(toB(r.get("thue_tncn")));
        p.setTamUng(toB(r.get("tam_ung")));
        p.setKhauTruKhac(toB(r.get("khau_tru_khac")));

        BigDecimal tongThu = luongCoBan.add(phuCap).add(tienOt);
        BigDecimal tongTru = p.getBhxhNld().add(p.getThueTncn())
                .add(p.getTamUng()).add(p.getKhauTruKhac());

        // Lay cac khoan thuong/phu cap khac
        List<PayslipDto.LineItem> chiTietThuong = new ArrayList<>();
        chiTietThuong.add(new PayslipDto.LineItem("Luong co ban", luongCoBan));
        if (phuCap.signum() > 0) chiTietThuong.add(new PayslipDto.LineItem("Phu cap", phuCap));
        if (tienOt.signum() > 0) chiTietThuong.add(new PayslipDto.LineItem("OT", tienOt));

        for (var k : khoanRepo.findByBangLuongId(findBangLuongId(nhanVienId, ky))) {
            chiTietThuong.add(new PayslipDto.LineItem(k.getMoTa() != null ? k.getMoTa() : k.getLoaiKhoan().name(),
                    k.getSoTien()));
            tongThu = tongThu.add(k.getSoTien());
        }

        List<PayslipDto.LineItem> chiTietKhauTru = new ArrayList<>();
        if (p.getBhxhNld().signum() > 0) chiTietKhauTru.add(new PayslipDto.LineItem("BHXH NLD (10.5%)", p.getBhxhNld()));
        if (p.getThueTncn().signum() > 0) chiTietKhauTru.add(new PayslipDto.LineItem("Thue TNCN", p.getThueTncn()));
        if (p.getTamUng().signum() > 0) chiTietKhauTru.add(new PayslipDto.LineItem("Tam ung", p.getTamUng()));
        if (p.getKhauTruKhac().signum() > 0) chiTietKhauTru.add(new PayslipDto.LineItem("Khau tru khac", p.getKhauTruKhac()));

        p.setTongKhoanThu(tongThu);
        p.setTongKhoanTru(tongTru);
        p.setThucLinh(tongThu.subtract(tongTru));
        p.setChiTietThuong(chiTietThuong);
        p.setChiTietKhauTru(chiTietKhauTru);
        return p;
    }

    private UUID findBangLuongId(UUID nvId, KyLinhLuong ky) {
        String sql = "SELECT bang_luong_id FROM payroll.bang_luong_thang " +
                " WHERE nhan_vien_id = ? AND thang = ? AND nam = ?";
        return jdbc.queryForObject(sql, UUID.class, nvId, ky.getThang(), ky.getNam());
    }

    private static BigDecimal toB(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        return new BigDecimal(o.toString());
    }
}
