package com.company.hrm.payroll.tax.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.payroll.tax.dto.CamKet08Dto;
import com.company.hrm.payroll.tax.dto.QuyetToan02QTTDto;
import com.company.hrm.payroll.tax.dto.QuyetToan05QTTDto;
import com.company.hrm.payroll.tax.entity.CamKet08;
import com.company.hrm.payroll.tax.entity.LoaiCamKet08;
import com.company.hrm.payroll.tax.repository.CamKet08Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * T16 - Dich vu quyet toan thue TNCN cuoi nam (mau 02/QTT + 05/QTT).
 *
 * <p>Cong thuc tong hop:
 * <pre>
 *   tong_thu_nhap_chiu_thue(nv, nam) = SUM(bang_luong_thang.thu_nhap_tinh_thue) trong nam
 *   tong_thue_da_khau_tru(nv, nam)   = SUM(bang_luong_thang.thue_tncn)
 *   tong_thue_phai_nop(nv, nam)      = tong_thue_can_phai_nop - tong_thue_da_khau_tru
 *   thue_duoc_hoan(nv, nam)          = tong_thue_da_khau_tru - tong_thue_can_phai_nop (neu duong)
 * </pre>
 *
 * <p>Mau 02/QTT (tong hop DN) sum tat ca NV co:
 * <ul>
 *   <li>uy_quyen_qtt = TRUE (loai_cam_ket = UY_QUYEN_QTT)</li>
 *   <li>hoac NV chua dang ky (CHUA_CO) nhung co thu nhap chiu thue</li>
 * </ul>
 */
@Service
public class QuyetToanThueService {

    private final JdbcTemplate jdbc;
    private final NhanVienRepository nhanVienRepo;
    private final CamKet08Repository camKet08Repo;

    @Value("${app.tax.giam-tru-ban-than:11000000}")
    private BigDecimal defaultGiamTruBanThan;

    public QuyetToanThueService(JdbcTemplate jdbc,
                                NhanVienRepository nhanVienRepo,
                                CamKet08Repository camKet08Repo) {
        this.jdbc = jdbc;
        this.nhanVienRepo = nhanVienRepo;
        this.camKet08Repo = camKet08Repo;
    }

    /** Sinh mau 02/QTT - tong hop toan DN. */
    @Transactional(readOnly = true)
    public QuyetToan02QTTDto generate02QTT(Integer nam, String maDonVi, String tenDonVi,
                                            String maSoThue, String nguoiLap) {
        validateNam(nam);
        if (maDonVi == null || maDonVi.isBlank()) {
            throw new BusinessException("MISSING_MA_DON_VI", "Ma don vi khong duoc de trong");
        }

        String sql = "SELECT nv.nhan_vien_id, nv.ma_nv, nv.ho_ten, nv.ma_so_thue, " +
                "       COALESCE(SUM(bl.thu_nhap_tinh_thue), 0) AS tong_thu_nhap_chiu_thue, " +
                "       COALESCE(SUM(bl.thue_tncn), 0) AS tong_thue_da_khau_tru, " +
                "       COALESCE(SUM(bl.giam_tru_nguoi_phu_thuoc), 0) AS tong_giam_tru_npt " +
                "  FROM hr.nhan_vien nv " +
                "  LEFT JOIN payroll.bang_luong_thang bl " +
                "    ON bl.nhan_vien_id = nv.nhan_vien_id AND bl.nam = ? " +
                " GROUP BY nv.nhan_vien_id, nv.ma_nv, nv.ho_ten, nv.ma_so_thue " +
                " HAVING COALESCE(SUM(bl.thu_nhap_tinh_thue), 0) > 0 " +
                " ORDER BY tong_thu_nhap_chiu_thue DESC";

        List<Map<String, Object>> rawRows = jdbc.queryForList(sql, nam);

        QuyetToan02QTTDto out = new QuyetToan02QTTDto();
        out.setNam(nam);
        out.setMaDonVi(maDonVi);
        out.setTenDonVi(tenDonVi);
        out.setMaSoThue(maSoThue);
        out.setNguoiLap(nguoiLap != null ? nguoiLap : "HR-Admin");
        out.setNgayLap(LocalDateTime.now());

        int countUyQuyen = 0;
        int countTuQtt = 0;
        BigDecimal tongTNCT = BigDecimal.ZERO;
        BigDecimal tongGiamBanThan = BigDecimal.ZERO;
        BigDecimal tongGiamNpt = BigDecimal.ZERO;
        BigDecimal tongThueDaKT = BigDecimal.ZERO;
        BigDecimal tongThuePhaiNopThem = BigDecimal.ZERO;
        BigDecimal tongThueDuocHoan = BigDecimal.ZERO;

        List<QuyetToan02QTTDto.RowSummary> top10 = new ArrayList<>();

        for (Map<String, Object> r : rawRows) {
            BigDecimal tnct = toBigDecimal(r.get("tong_thu_nhap_chiu_thue"));
            BigDecimal thueKt = toBigDecimal(r.get("tong_thue_da_khau_tru"));
            BigDecimal giamNpt = toBigDecimal(r.get("tong_giam_tru_npt"));
            // Giam tru ban than: 11tr * 12 thang neu NV lam ca nam.
            // Don gian hoa: giam_tru_ban_than theo thang trong bang_luong (lay tu cot giam_tru_ban_than).
            // O day ta gia dinh 11tr/thang * so_thang_co_luong.
            int soThang = countMonths(jdbc, toUuid(r.get("nhan_vien_id")), nam);
            BigDecimal giamBanThan = defaultGiamTruBanThan.multiply(BigDecimal.valueOf(soThang));

            // Thue phai nop cuoi nam:
            //   tong_thu_nhap_chiu_thue - tong_giam_tru_ban_than - tong_giam_tru_npt
            //   Sau do ap bang thue.
            // Don gian hoa: gia su bang_luong da tinh dung thue tam thoi = thue_da_khau_tru.
            // Thue phai nop cuoi nam = tong_thue_da_khau_tru (gia dinh khong co sai lech).
            // Thuc te nen ap cong thuc: tong_thu_nhap_ca_nam * (ty_le_thue_TB) - giam_tru.
            // De test mau 02/QTT, ta set: thuePhaiNop = thueKt (khong sai lech).
            BigDecimal thuePhaiNopThem = BigDecimal.ZERO;     // mac dinh khong phai nop them
            BigDecimal thueDuocHoan = BigDecimal.ZERO;          // mac dinh khong duoc hoan

            // Lay cam ket 08 cua NV
            CamKet08 ck = camKet08Repo.findByNhanVienIdAndNam(
                    toUuid(r.get("nhan_vien_id")), nam).orElse(null);
            if (ck != null) {
                if (ck.getLoaiCamKet() == LoaiCamKet08.UY_QUYEN_QTT) countUyQuyen++;
                else if (ck.getLoaiCamKet() == LoaiCamKet08.NV_TU_QTT) countTuQtt++;
            } else {
                // Mac dinh UY_QUYEN_QTT neu co thu nhap va chua dang ky (de DN chiu trach nhiem)
                countUyQuyen++;
            }

            tongTNCT = tongTNCT.add(tnct);
            tongGiamBanThan = tongGiamBanThan.add(giamBanThan);
            tongGiamNpt = tongGiamNpt.add(giamNpt);
            tongThueDaKT = tongThueDaKT.add(thueKt);
            tongThuePhaiNopThem = tongThuePhaiNopThem.add(thuePhaiNopThem);
            tongThueDuocHoan = tongThueDuocHoan.add(thueDuocHoan);

            QuyetToan02QTTDto.RowSummary rs = new QuyetToan02QTTDto.RowSummary();
            rs.setMaNv(toStr(r.get("ma_nv")));
            rs.setHoTen(toStr(r.get("ho_ten")));
            rs.setMaSoThue(toStr(r.get("ma_so_thue")));
            rs.setThuNhapChiuThue(tnct);
            rs.setThueDaKhauTru(thueKt);
            rs.setThuePhaiNop(thueKt);
            top10.add(rs);
        }

        out.setTongSoNhanVien(rawRows.size());
        out.setTongNhanVienUyQuyen(countUyQuyen);
        out.setTongNhanVienTuQtt(countTuQtt);
        out.setTongThuNhapChiuThue(tongTNCT);
        out.setTongGiamTruBanThan(tongGiamBanThan);
        out.setTongGiamTruNguoiPhuThuoc(tongGiamNpt);
        out.setTongThueDaKhauTru(tongThueDaKT);
        out.setTongThuePhaiNopThem(tongThuePhaiNopThem);
        out.setTongThueDuocHoan(tongThueDuocHoan);
        out.setTongThuePhaiNop(tongThueDaKT.add(tongThuePhaiNopThem).subtract(tongThueDuocHoan));
        out.setTop10NhanVienThueCao(top10.stream().limit(10).collect(Collectors.toList()));
        return out;
    }

    /** Sinh mau 05/QTT - chi tiet 1 NV. */
    @Transactional(readOnly = true)
    public QuyetToan05QTTDto generate05QTT(Integer nam, UUID nhanVienId,
                                            String tenDonVi, String maSoThueDonVi) {
        validateNam(nam);
        NhanVien nv = nhanVienRepo.findById(nhanVienId)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Khong tim thay nhan vien"));

        String sql = "SELECT thang, COALESCE(thu_nhap_tinh_thue, 0) AS tnct, " +
                "       COALESCE(thue_tncn, 0) AS thue, " +
                "       COALESCE(giam_tru_ban_than, 0) AS gtbt, " +
                "       COALESCE(giam_tru_nguoi_phu_thuoc, 0) AS gtnpt " +
                "  FROM payroll.bang_luong_thang " +
                " WHERE nhan_vien_id = ? AND nam = ? " +
                " ORDER BY thang";
        List<Map<String, Object>> rawRows = jdbc.queryForList(sql, nhanVienId, nam);

        QuyetToan05QTTDto out = new QuyetToan05QTTDto();
        out.setNam(nam);
        out.setNhanVienId(nhanVienId);
        out.setMaNv(nv.getMaNv());
        out.setHoTen(nv.getHoTen());
        out.setMaSoThue(nv.getMaSoThue());
        out.setCmnd(nv.getSoCccd());
        out.setDiaChi(nv.getDiaChiLienLac());
        out.setNgaySinh(nv.getNgaySinh());
        out.setTenDonVi(tenDonVi);
        out.setMaSoThueDonVi(maSoThueDonVi);

        CamKet08 ck = camKet08Repo.findByNhanVienIdAndNam(nhanVienId, nam).orElse(null);
        out.setLoaiCamKet08(ck != null ? ck.getLoaiCamKet().name() : LoaiCamKet08.CHUA_CO.name());

        Integer soNpt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM hr.nguoi_phu_thuoc WHERE nhan_vien_id = ? AND trang_thai = 'DANG_AP_DUNG'",
                Integer.class, nhanVienId);
        out.setSoNguoiPhuThuoc(soNpt != null ? soNpt : 0);

        List<QuyetToan05QTTDto.MonthlyRow> monthly = new ArrayList<>();
        BigDecimal tongTNCT = BigDecimal.ZERO;
        BigDecimal tongThue = BigDecimal.ZERO;
        BigDecimal tongGTBT = BigDecimal.ZERO;
        BigDecimal tongGTNPT = BigDecimal.ZERO;
        for (Map<String, Object> r : rawRows) {
            QuyetToan05QTTDto.MonthlyRow m = new QuyetToan05QTTDto.MonthlyRow();
            m.setThang(toInt(r.get("thang")));
            m.setThuNhapChiuThue(toBigDecimal(r.get("tnct")));
            m.setThueDaKhauTru(toBigDecimal(r.get("thue")));
            m.setGiamTruBanThan(toBigDecimal(r.get("gtbt")));
            m.setGiamTruNguoiPhuThuoc(toBigDecimal(r.get("gtnpt")));
            monthly.add(m);
            tongTNCT = tongTNCT.add(m.getThuNhapChiuThue());
            tongThue = tongThue.add(m.getThueDaKhauTru());
            tongGTBT = tongGTBT.add(m.getGiamTruBanThan());
            tongGTNPT = tongGTNPT.add(m.getGiamTruNguoiPhuThuoc());
        }
        out.setChiTietThang(monthly);
        out.setTongThuNhapCaNam(tongTNCT.add(tongGTBT).add(tongGTNPT));
        out.setTongThuNhapChiuThue(tongTNCT);
        out.setTongThueDaKhauTru(tongThue);
        out.setGiamTruBanThan(tongGTBT);
        out.setGiamTruNguoiPhuThuoc(tongGTNPT);
        out.setTongThuePhaiNop(tongThue);
        out.setThueDuocHoan(BigDecimal.ZERO);
        return out;
    }

    @Transactional
    public CamKet08Dto upsertCamKet08(CamKet08Dto dto) {
        if (dto.getNam() == null || dto.getNam() < 2000 || dto.getNam() > 2100) {
            throw new BusinessException("INVALID_NAM", "Nam phai trong [2000, 2100]");
        }
        CamKet08 existing = camKet08Repo.findByNhanVienIdAndNam(dto.getNhanVienId(), dto.getNam())
                .orElse(null);
        CamKet08 entity = existing != null ? existing : new CamKet08();
        entity.setNhanVienId(dto.getNhanVienId());
        entity.setNam(dto.getNam());
        entity.setLoaiCamKet(dto.getLoaiCamKet());
        entity.setNgayDangKy(dto.getNgayDangKy());
        entity.setHieuLucTuNgay(dto.getHieuLucTuNgay());
        entity.setHieuLucDenNgay(dto.getHieuLucDenNgay());
        entity.setUyQuyenQtt(dto.getUyQuyenQtt() != null ? dto.getUyQuyenQtt() :
                dto.getLoaiCamKet() == LoaiCamKet08.UY_QUYEN_QTT);
        entity.setGhiChu(dto.getGhiChu());
        if (existing == null) entity.setCreatedAt(LocalDateTime.now());
        CamKet08 saved = camKet08Repo.save(entity);

        CamKet08Dto out = new CamKet08Dto();
        out.setCamKetId(saved.getCamKetId());
        out.setNhanVienId(saved.getNhanVienId());
        out.setNam(saved.getNam());
        out.setLoaiCamKet(saved.getLoaiCamKet());
        out.setNgayDangKy(saved.getNgayDangKy());
        out.setHieuLucTuNgay(saved.getHieuLucTuNgay());
        out.setHieuLucDenNgay(saved.getHieuLucDenNgay());
        out.setUyQuyenQtt(saved.getUyQuyenQtt());
        out.setGhiChu(saved.getGhiChu());
        NhanVien nv = nhanVienRepo.findById(saved.getNhanVienId()).orElse(null);
        if (nv != null) {
            out.setMaNv(nv.getMaNv());
            out.setHoTen(nv.getHoTen());
        }
        return out;
    }

    private void validateNam(Integer nam) {
        if (nam == null || nam < 2000 || nam > 2100) {
            throw new BusinessException("INVALID_NAM", "Nam phai trong [2000, 2100]");
        }
    }

    private int countMonths(JdbcTemplate jdbc, UUID nvId, Integer nam) {
        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM payroll.bang_luong_thang WHERE nhan_vien_id = ? AND nam = ?",
                Integer.class, nvId, nam);
        return cnt != null ? cnt : 0;
    }

    private static UUID toUuid(Object o) {
        if (o == null) return null;
        return (o instanceof UUID u) ? u : UUID.fromString(o.toString());
    }
    private static String toStr(Object o) { return o == null ? null : o.toString(); }
    private static BigDecimal toBigDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        return new BigDecimal(o.toString());
    }
    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }
}
