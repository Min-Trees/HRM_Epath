package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.LeaveBalanceInitResult;
import com.company.hrm.attendance.dto.QuyPhepNamResponse;
import com.company.hrm.attendance.entity.QuyPhepNam;
import com.company.hrm.attendance.repository.QuyPhepNamRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

/**
 * Quản lý quỹ phép năm (T10).
 *
 * <p>Công thức số ngày được hưởng (theo Bộ luật Lao động VN + task prompt):
 * <pre>
 *   thamNien    = Period.between(nv.ngayVaoLam, ngayTraCuu(1/1/năm)).getYears()
 *   soNgayDuocHuong = 12 + max(0, floor(thamNien / 5))
 * </pre>
 * Mỗi 5 năm thâm niên +1 ngày; thâm niên &lt; 5 năm → 12 ngày.
 *
 * <p>Trừ quỹ: thực hiện ở {@link NghiPhepService} khi đơn {@code PHEP_NAM} đạt {@code DA_DUYET}.
 * Hoàn quỹ khi hủy đơn sau duyệt.
 */
@Service
public class QuyPhepService {

    private static final Logger log = LoggerFactory.getLogger(QuyPhepService.class);

    private final QuyPhepNamRepository repo;
    private final NhanVienRepository nvRepo;

    public QuyPhepService(QuyPhepNamRepository repo, NhanVienRepository nvRepo) {
        this.repo = repo;
        this.nvRepo = nvRepo;
    }

    /**
     * Khởi tạo quỹ phép cho 1 NV tại 1 năm. Idempotent — skip nếu đã tồn tại.
     */
    @Transactional
    public QuyPhepNamResponse init(UUID nhanVienId, int nam) {
        validateNam(nam);
        NhanVien nv = nvRepo.findById(nhanVienId).orElseThrow(() ->
                new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));
        if (repo.existsByNhanVienIdAndNam(nhanVienId, nam)) {
            log.info("Quỹ phép NV {} năm {} đã tồn tại, bỏ qua init", nhanVienId, nam);
            return QuyPhepNamResponse.from(
                    repo.findByNhanVienIdAndNam(nhanVienId, nam).orElseThrow());
        }
        BigDecimal soNgay = tinhSoNgayDuocHuong(nv, nam);
        QuyPhepNam qp = new QuyPhepNam();
        qp.setNhanVienId(nhanVienId);
        qp.setNam(nam);
        qp.setSoNgayDuocHuong(soNgay);
        qp.setSoNgayDaDung(new BigDecimal("0.0"));
        QuyPhepNam saved = repo.save(qp);
        log.info("Khởi tạo quỹ phép NV {} năm {} = {} ngày", nhanVienId, nam, soNgay);
        return QuyPhepNamResponse.from(saved);
    }

    /**
     * Khởi tạo quỹ phép cho nhiều NV. NV đã có quỹ trong năm được skip.
     * Trả về {@link LeaveBalanceInitResult} để client thấy bao nhiêu NV thành công.
     */
    @Transactional
    public LeaveBalanceInitResult initBatch(List<UUID> nhanVienIds, int nam) {
        validateNam(nam);
        if (nhanVienIds == null || nhanVienIds.isEmpty()) {
            return new LeaveBalanceInitResult(0, 0, 0);
        }
        int created = 0, skipped = 0;
        for (UUID nvId : nhanVienIds) {
            try {
                if (repo.existsByNhanVienIdAndNam(nvId, nam)) {
                    skipped++;
                    log.info("Quỹ phép NV {} năm {} đã tồn tại, skip batch init", nvId, nam);
                    continue;
                }
                init(nvId, nam);
                created++;
            } catch (BusinessException ex) {
                log.warn("Skip quỹ phép NV {} năm {}: {}", nvId, nam, ex.getMessage());
                skipped++;
            } catch (ResourceNotFoundException ex) {
                log.warn("Skip quỹ phép NV {} năm {}: {}", nvId, nam, ex.getMessage());
                skipped++;
            }
        }
        return new LeaveBalanceInitResult(nhanVienIds.size(), created, skipped);
    }

    /**
     * Lấy quỹ phép hiện tại của NV theo năm. Nếu chưa có → trả về null (không tự init).
     */
    @Transactional(readOnly = true)
    public QuyPhepNamResponse getBalance(UUID nhanVienId, int nam) {
        validateNam(nam);
        return repo.findByNhanVienIdAndNam(nhanVienId, nam)
                .map(QuyPhepNamResponse::from)
                .orElse(null);
    }

    /**
     * Cộng dồn số ngày đã dùng — được gọi từ {@link NghiPhepService} khi đơn PHEP_NAM đạt DA_DUYET.
     * Đảm bảo quỹ đã được khởi tạo trước.
     *
     * @throws BusinessException nếu quỹ chưa được init hoặc không đủ ngày.
     */
    @Transactional
    public void congDaDung(UUID nhanVienId, int nam, BigDecimal soNgay) {
        QuyPhepNam qp = repo.findByNhanVienIdAndNam(nhanVienId, nam).orElseThrow(() ->
                new BusinessException("QUY_PHEP_NOT_INIT",
                        "Chưa khởi tạo quỹ phép cho NV " + nhanVienId + " năm " + nam
                                + "; hãy gọi POST /leave-balance/init"));
        BigDecimal newDaDung = qp.getSoNgayDaDung().add(soNgay).setScale(1, RoundingMode.HALF_UP);
        if (newDaDung.compareTo(qp.getSoNgayDuocHuong()) > 0) {
            throw new BusinessException("QUY_PHEP_NOT_ENOUGH",
                    "Quỹ phép không đủ: được hưởng " + qp.getSoNgayDuocHuong()
                            + ", đã dùng " + qp.getSoNgayDaDung()
                            + ", yêu cầu thêm " + soNgay);
        }
        qp.setSoNgayDaDung(newDaDung);
        repo.save(qp);
        log.info("NV {} năm {} dùng thêm {} ngày phép; tổng đã dùng = {}",
                nhanVienId, nam, soNgay, newDaDung);
    }

    /** Hoàn quỹ — dùng khi hủy đơn sau DA_DUYET. Không về âm. */
    @Transactional
    public void hoanDaDung(UUID nhanVienId, int nam, BigDecimal soNgay) {
        QuyPhepNam qp = repo.findByNhanVienIdAndNam(nhanVienId, nam).orElseThrow(() ->
                new BusinessException("QUY_PHEP_NOT_INIT",
                        "Chưa khởi tạo quỹ phép cho NV " + nhanVienId + " năm " + nam));
        BigDecimal newDaDung = qp.getSoNgayDaDung().subtract(soNgay).setScale(1, RoundingMode.HALF_UP);
        if (newDaDung.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Hoàn quỹ phép NV {} năm {}: tổng đã dùng < 0; đặt về 0", nhanVienId, nam);
            newDaDung = BigDecimal.ZERO;
        }
        qp.setSoNgayDaDung(newDaDung);
        repo.save(qp);
        log.info("NV {} năm {} hoàn {} ngày phép; tổng đã dùng = {}",
                nhanVienId, nam, soNgay, newDaDung);
    }

    // ---------------- helpers ----------------

    /** Tính số ngày phép được hưởng theo thâm niên tới đầu năm {@code nam}. */
    static BigDecimal tinhSoNgayDuocHuong(NhanVien nv, int nam) {
        LocalDate mocTinh = LocalDate.of(nam, 1, 1);
        Period p = Period.between(nv.getNgayVaoLam(), mocTinh);
        int thamNien = Math.max(0, p.getYears());
        int bonus = thamNien / 5; // floor: mỗi 5 năm +1
        return new BigDecimal(12 + bonus).setScale(1, RoundingMode.HALF_UP);
    }

    private void validateNam(int nam) {
        if (nam < 1970 || nam > 9999) {
            throw new BusinessException("YEAR_INVALID", "Năm không hợp lệ: " + nam);
        }
    }
}