package com.company.hrm.social_ins.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.social_ins.dto.BhxhReportD02LTDto;
import com.company.hrm.social_ins.dto.BhxhReportD03LTDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * T15 - Dich vu bao cao BHXH mau D02-LT (bao tang/giam/dieu chinh) va
 * mau D03-LT (bao cap so BHXH).
 *
 * <p>Du lieu duoc trich tu cac bang:
 * <ul>
 *   <li>{@code social_ins.bhxh_bien_dong} - moi record = 1 dong trong D02-LT</li>
 *   <li>{@code social_ins.so_bhxh} - moi record co ngay_cap gan day = 1 dong D03-LT</li>
 *   <li>{@code hr.nhan_vien}, {@code social_ins.qua_trinh_tham_gia} - join tham chieu</li>
 * </ul>
 *
 * <p>Cac tham so (ty le dong BHXH 10.5/21.5, ...) duoc lay tu {@code payroll.tham_so_luong}.
 */
@Service
public class BhxhReportService {

    private static final BigDecimal DEFAULT_TY_LE_NLD = new BigDecimal("10.5");
    private static final BigDecimal DEFAULT_TY_LE_DN = new BigDecimal("21.5");

    private final JdbcTemplate jdbc;

    public BhxhReportService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Sinh bao cao D02-LT tu cac bien dong BHXH trong [tuNgay, denNgay].
     */
    @Transactional(readOnly = true)
    public BhxhReportD02LTDto generateD02LT(LocalDate tuNgay, LocalDate denNgay, String maDonViBHXH,
                                            String tenDonVi, String maSoThueDonVi, String nguoiLap) {
        validateRange(tuNgay, denNgay);
        if (maDonViBHXH == null || maDonViBHXH.isBlank()) {
            throw new BusinessException("MISSING_MA_DON_VI", "Ma don vi BHXH khong duoc de trong");
        }

        String sql = "SELECT bd.bien_dong_id, bd.nhan_vien_id, nv.ma_nv, nv.ho_ten, " +
                "       nv.so_cmnd, nv.ngay_sinh, nv.gioi_tinh, nv.quoc_tich, " +
                "       nv.dia_chi_lien_lac, " +
                "       COALESCE(qt.ma_so_bhxh, sb.ma_so_bhxh) AS ma_so_bhxh, " +
                "       bd.loai_bao, bd.ly_do, bd.ngay_phat_sinh, bd.da_nop, " +
                "       qt.muc_luong_dong, qt.ty_le_dong_nld, qt.ty_le_dong_dn " +
                "  FROM social_ins.bhxh_bien_dong bd " +
                "  JOIN hr.nhan_vien nv ON nv.nhan_vien_id = bd.nhan_vien_id " +
                "  LEFT JOIN social_ins.qua_trinh_tham_gia qt " +
                "    ON qt.nhan_vien_id = bd.nhan_vien_id " +
                "   AND qt.tu_ngay <= bd.ngay_phat_sinh " +
                "   AND (qt.den_ngay IS NULL OR qt.den_ngay >= bd.ngay_phat_sinh) " +
                "  LEFT JOIN social_ins.so_bhxh sb ON sb.nhan_vien_id = bd.nhan_vien_id " +
                " WHERE bd.ngay_phat_sinh BETWEEN ? AND ? " +
                " ORDER BY bd.ngay_phat_sinh, nv.ma_nv";

        List<Map<String, Object>> rawRows = jdbc.queryForList(sql, Date.valueOf(tuNgay), Date.valueOf(denNgay));

        List<BhxhReportD02LTDto.Row> rows = new ArrayList<>(rawRows.size());
        for (Map<String, Object> r : rawRows) {
            BhxhReportD02LTDto.Row row = new BhxhReportD02LTDto.Row();
            row.setNhanVienId(toUuid(r.get("nhan_vien_id")));
            row.setMaNv(toStr(r.get("ma_nv")));
            row.setHoTen(toStr(r.get("ho_ten")));
            row.setSoCmnd(toStr(r.get("so_cmnd")));
            row.setNgaySinh(toLocalDate(r.get("ngay_sinh")));
            row.setGioiTinh(toStr(r.get("gioi_tinh")));
            row.setQuocTich(toStr(r.get("quoc_tich")));
            row.setDiaChi(toStr(r.get("dia_chi_lien_lac")));
            row.setMaSoBhxh(toStr(r.get("ma_so_bhxh")));
            row.setLoaiBienDong(toStr(r.get("loai_bao")));
            row.setLyDoBienDong(toStr(r.get("ly_do")));
            row.setNgayPhatSinh(toLocalDate(r.get("ngay_phat_sinh")));
            row.setMucLuongDong(toBigDecimal(r.get("muc_luong_dong")));
            row.setTyLeNld(toBigDecimal(r.get("ty_le_dong_nld")));
            row.setTyLeDn(toBigDecimal(r.get("ty_le_dong_dn")));
            if (row.getTyLeNld() == null) row.setTyLeNld(DEFAULT_TY_LE_NLD);
            if (row.getTyLeDn() == null) row.setTyLeDn(DEFAULT_TY_LE_DN);
            row.setTrangThaiNop(((Boolean) r.get("da_nop")) == null
                    || Boolean.FALSE.equals(r.get("da_nop")) ? "CHUA_NOP" : "DA_NOP");
            row.setNoiDung(buildNoiDung(row));
            rows.add(row);
        }

        BhxhReportD02LTDto out = new BhxhReportD02LTDto();
        out.setTuNgay(tuNgay);
        out.setDenNgay(denNgay);
        out.setMaDonViBHXH(maDonViBHXH);
        out.setTenDonVi(tenDonVi);
        out.setMaSoThueDonVi(maSoThueDonVi);
        out.setNguoiLap(nguoiLap != null ? nguoiLap : "HR-Admin");
        out.setNgayLap(LocalDateTime.now());
        out.setRows(rows);
        out.setTongSoDong(rows.size());
        return out;
    }

    /**
     * Sinh bao cao D03-LT (yeu cau cap so BHXH moi) tu cac NV co ngay cap so gan day.
     */
    @Transactional(readOnly = true)
    public BhxhReportD03LTDto generateD03LT(LocalDate tuNgay, LocalDate denNgay, String maDonViBHXH,
                                            String tenDonVi) {
        validateRange(tuNgay, denNgay);
        if (maDonViBHXH == null || maDonViBHXH.isBlank()) {
            throw new BusinessException("MISSING_MA_DON_VI", "Ma don vi BHXH khong duoc de trong");
        }

        String sql = "SELECT sb.so_bhxh_id, sb.nhan_vien_id, sb.ma_so_bhxh, sb.ngay_cap, " +
                "       nv.ma_nv, nv.ho_ten, nv.ngay_sinh, nv.so_cmnd " +
                "  FROM social_ins.so_bhxh sb " +
                "  JOIN hr.nhan_vien nv ON nv.nhan_vien_id = sb.nhan_vien_id " +
                " WHERE sb.ngay_cap BETWEEN ? AND ? " +
                " ORDER BY sb.ngay_cap, nv.ma_nv";

        List<Map<String, Object>> rawRows = jdbc.queryForList(sql, Date.valueOf(tuNgay), Date.valueOf(denNgay));

        List<BhxhReportD03LTDto.Row> rows = new ArrayList<>(rawRows.size());
        for (Map<String, Object> r : rawRows) {
            BhxhReportD03LTDto.Row row = new BhxhReportD03LTDto.Row();
            row.setNhanVienId(toUuid(r.get("nhan_vien_id")));
            row.setMaNv(toStr(r.get("ma_nv")));
            row.setHoTen(toStr(r.get("ho_ten")));
            row.setNgaySinh(toLocalDate(r.get("ngay_sinh")));
            row.setSoCmnd(toStr(r.get("so_cmnd")));
            row.setMaSoBhxh(toStr(r.get("ma_so_bhxh")));
            row.setNgayCapSo(toLocalDate(r.get("ngay_cap")));
            row.setLoaiDeNghi(detectLoaiDeNghi(row));
            row.setLyDo(row.getLoaiDeNghi().equals("CAP_MOI") ? "Tuyen dung moi" : "Theo yeu cau");
            row.setViTriLuuTru("Phong HCNS - Ban HR");
            row.setTrangThaiNop("CHUA_NOP");
            rows.add(row);
        }

        BhxhReportD03LTDto out = new BhxhReportD03LTDto();
        out.setTuNgay(tuNgay);
        out.setDenNgay(denNgay);
        out.setMaDonViBHXH(maDonViBHXH);
        out.setTenDonVi(tenDonVi);
        out.setNgayLap(LocalDateTime.now());
        out.setTongSoDong(rows.size());
        out.setRows(rows);
        return out;
    }

    private String detectLoaiDeNghi(BhxhReportD03LTDto.Row row) {
        // Logic simple: neu so_bhxh ton tai, nhung ngay_cap gan day -> CAP_MOI.
        // Sau nay co the them flag CAP_LAI / SUA_DOI / MAT neu co truong mo rong.
        return "CAP_MOI";
    }

    private String buildNoiDung(BhxhReportD02LTDto.Row r) {
        if (r.getNgayPhatSinh() == null) return r.getLoaiBienDong();
        return String.format("%s - %s (%s)", r.getLoaiBienDong(),
                r.getLyDoBienDong() != null ? r.getLyDoBienDong() : "",
                r.getNgayPhatSinh());
    }

    private void validateRange(LocalDate tu, LocalDate den) {
        if (tu == null || den == null) {
            throw new BusinessException("INVALID_DATE_RANGE", "tuNgay va denNgay khong duoc de trong");
        }
        if (den.isBefore(tu)) {
            throw new BusinessException("INVALID_DATE_RANGE",
                    "denNgay (" + den + ") phai >= tuNgay (" + tu + ")");
        }
    }

    private static UUID toUuid(Object o) {
        if (o == null) return null;
        return (o instanceof UUID u) ? u : UUID.fromString(o.toString());
    }
    private static String toStr(Object o) { return o == null ? null : o.toString(); }
    private static LocalDate toLocalDate(Object o) {
        if (o == null) return null;
        if (o instanceof Date d) return d.toLocalDate();
        if (o instanceof Timestamp t) return t.toLocalDateTime().toLocalDate();
        return LocalDate.parse(o.toString());
    }
    private static BigDecimal toBigDecimal(Object o) {
        if (o == null) return null;
        if (o instanceof BigDecimal b) return b;
        return new BigDecimal(o.toString());
    }
}
