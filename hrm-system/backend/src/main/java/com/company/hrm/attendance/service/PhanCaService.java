package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.PhanCaAssignResult;
import com.company.hrm.attendance.dto.PhanCaRequest;
import com.company.hrm.attendance.dto.PhanCaResponse;
import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.entity.PhanCa;
import com.company.hrm.attendance.repository.CaLamViecRepository;
import com.company.hrm.attendance.repository.ChamCongChiTietRepository;
import com.company.hrm.attendance.repository.PhanCaRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Phân ca làm việc cho nhân viên. Hỗ trợ 3 mode:
 * <ul>
 *   <li>Single: 1 NV + 1 ngày.</li>
 *   <li>Bulk: nhiều NV + 1 ca + khoảng ngày.</li>
 *   <li>Rotating: nhiều NV + nhiều ca xoay vòng + khoảng ngày.</li>
 * </ul>
 * Phân ca kíp xoay vòng: với mỗi NV và mỗi ngày trong khoảng,
 * chọn ca bằng công thức {@code caIds.get((dayOffset % chuKy) % caIds.size())}.
 */
@Service
public class PhanCaService {

    private static final Logger log = LoggerFactory.getLogger(PhanCaService.class);

    /** Trạng thái NV được phép phân ca. */
    private static final Set<TrangThaiNv> ALLOWED_STATUS = EnumSet.of(
            TrangThaiNv.UNG_VIEN, TrangThaiNv.THU_VIEC, TrangThaiNv.CHINH_THUC, TrangThaiNv.TAM_HOAN_HDLD);

    private final PhanCaRepository repo;
    private final CaLamViecRepository caRepo;
    private final NhanVienRepository nvRepo;
    private final ChamCongChiTietRepository chamCongRepo;

    public PhanCaService(PhanCaRepository repo,
                         CaLamViecRepository caRepo,
                         NhanVienRepository nvRepo,
                         ChamCongChiTietRepository chamCongRepo) {
        this.repo = repo;
        this.caRepo = caRepo;
        this.nvRepo = nvRepo;
        this.chamCongRepo = chamCongRepo;
    }

    /**
     * Dispatch theo mode của request.
     */
    @Transactional
    public Object assign(PhanCaRequest req) {
        Mode mode = detectMode(req);
        log.info("PhanCaService.assign mode={}", mode);
        return switch (mode) {
            case SINGLE -> assignSingle(req.getNhanVienId(), req.getCaId(), req.getNgayApDung(),
                    req.getGhiChu(), Boolean.TRUE.equals(req.getOverride()));
            case BULK -> assignBulk(req.getNhanVienIds(), req.getCaIds().get(0),
                    req.getFromDate(), req.getToDate(), req.getGhiChu());
            case ROTATING -> assignRotating(req.getNhanVienIds(), req.getCaIds(),
                    req.getChuKy(), req.getFromDate(), req.getToDate(), req.getGhiChu());
        };
    }

    private enum Mode { SINGLE, BULK, ROTATING }

    private Mode detectMode(PhanCaRequest req) {
        boolean hasSingle = req.getNhanVienId() != null
                && req.getCaId() != null
                && req.getNgayApDung() != null;
        boolean hasRotating = req.getNhanVienIds() != null && !req.getNhanVienIds().isEmpty()
                && req.getCaIds() != null && req.getCaIds().size() > 1
                && req.getFromDate() != null && req.getToDate() != null;
        boolean hasBulk = req.getNhanVienIds() != null && !req.getNhanVienIds().isEmpty()
                && req.getCaIds() != null && !req.getCaIds().isEmpty()
                && req.getFromDate() != null && req.getToDate() != null;
        if (hasRotating) return Mode.ROTATING;
        if (hasBulk) return Mode.BULK;
        if (hasSingle) return Mode.SINGLE;
        throw new BusinessException("PHAN_CA_REQUEST_INVALID",
                "Request không hợp lệ: cần (nhanVienId+caId+ngayApDung) HOẶC (nhanVienIds+caIds+fromDate+toDate)");
    }

    // ---------------- single ----------------

    @Transactional
    public PhanCaResponse assignSingle(UUID nhanVienId, UUID caId, LocalDate ngayApDung,
                                       String ghiChu, boolean override) {
        NhanVien nv = requireActiveEmployee(nhanVienId);
        CaLamViec ca = requireActiveShift(caId);

        if (repo.existsByNhanVienIdAndNgayApDung(nhanVienId, ngayApDung)) {
            if (!override) {
                throw new BusinessException("PHAN_CA_DUPLICATE",
                        "Nhân viên đã được phân ca cho ngày " + ngayApDung
                                + "; truyền override=true để ghi đè");
            }
            PhanCa old = repo.findByNhanVienIdAndNgayApDung(nhanVienId, ngayApDung).orElseThrow();
            repo.delete(old);
            log.info("Override phân ca cũ {} cho nv {} ngày {}", old.getPhanCaId(), nhanVienId, ngayApDung);
        }

        PhanCa pc = new PhanCa();
        pc.setNhanVienId(nhanVienId);
        pc.setCaId(ca.getCaId());
        pc.setNgayApDung(ngayApDung);
        pc.setGhiChu(override ? buildOverrideNote(ghiChu) : ghiChu);
        return PhanCaResponse.from(repo.save(pc), ca.getMaCa(), ca.getTenCa());
    }

    private String buildOverrideNote(String original) {
        if (original == null || original.isBlank()) return "Ghi đè bởi HR lúc " + LocalDate.now();
        return original + " (ghi đè lúc " + LocalDate.now() + ")";
    }

    // ---------------- bulk ----------------

    @Transactional
    public PhanCaAssignResult assignBulk(List<UUID> nhanVienIds, UUID caId,
                                          LocalDate fromDate, LocalDate toDate, String ghiChu) {
        validateRange(fromDate, toDate);
        CaLamViec ca = requireActiveShift(caId);
        int created = 0, skipped = 0;
        List<PhanCaResponse> results = new ArrayList<>();
        for (UUID nvId : nhanVienIds) {
            try {
                NhanVien nv = requireActiveEmployee(nvId);
                for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
                    if (repo.existsByNhanVienIdAndNgayApDung(nvId, d)) {
                        skipped++;
                        log.info("Skip phân ca trùng ngày {} cho nv {}", d, nvId);
                        continue;
                    }
                    PhanCa pc = new PhanCa();
                    pc.setNhanVienId(nvId);
                    pc.setCaId(ca.getCaId());
                    pc.setNgayApDung(d);
                    pc.setGhiChu(ghiChu);
                    PhanCa saved = repo.save(pc);
                    results.add(PhanCaResponse.from(saved, ca.getMaCa(), ca.getTenCa()));
                    created++;
                }
            } catch (BusinessException ex) {
                log.warn("Bỏ qua nv {} khi phân bulk: {}", nvId, ex.getMessage());
                skipped++;
            }
        }
        return new PhanCaAssignResult(created, skipped, results);
    }

    // ---------------- rotating ----------------

    @Transactional
    public PhanCaAssignResult assignRotating(List<UUID> nhanVienIds, List<UUID> caIds,
                                              Integer chuKy, LocalDate fromDate, LocalDate toDate,
                                              String ghiChu) {
        validateRange(fromDate, toDate);
        if (caIds == null || caIds.isEmpty()) {
            throw new BusinessException("PHAN_CA_CA_IDS_REQUIRED",
                    "Phân ca xoay vòng cần ít nhất 1 ca");
        }
        int cycle = (chuKy == null || chuKy < caIds.size()) ? caIds.size() : chuKy;
        Map<UUID, CaLamViec> caById = new HashMap<>();
        for (UUID caId : caIds) {
            caById.put(caId, requireActiveShift(caId));
        }
        int created = 0, skipped = 0;
        List<PhanCaResponse> results = new ArrayList<>();
        for (UUID nvId : nhanVienIds) {
            try {
                NhanVien nv = requireActiveEmployee(nvId);
                for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
                    long dayOffset = ChronoUnit.DAYS.between(fromDate, d);
                    int caIndex = (int) ((dayOffset % cycle) % caIds.size());
                    UUID caId = caIds.get(caIndex);
                    CaLamViec ca = caById.get(caId);
                    if (repo.existsByNhanVienIdAndNgayApDung(nvId, d)) {
                        skipped++;
                        log.info("Skip phân ca xoay vòng trùng ngày {} cho nv {}", d, nvId);
                        continue;
                    }
                    PhanCa pc = new PhanCa();
                    pc.setNhanVienId(nvId);
                    pc.setCaId(ca.getCaId());
                    pc.setNgayApDung(d);
                    pc.setGhiChu(appendRotatingNote(ghiChu, caIndex + 1, caIds.size()));
                    PhanCa saved = repo.save(pc);
                    results.add(PhanCaResponse.from(saved, ca.getMaCa(), ca.getTenCa()));
                    created++;
                }
            } catch (BusinessException ex) {
                log.warn("Bỏ qua nv {} khi phân xoay vòng: {}", nvId, ex.getMessage());
                skipped++;
            }
        }
        return new PhanCaAssignResult(created, skipped, results);
    }

    private String appendRotatingNote(String original, int caIndex, int totalCa) {
        String tag = "Ca xoay vòng " + caIndex + "/" + totalCa;
        if (original == null || original.isBlank()) return tag;
        return original + " — " + tag;
    }

    // ---------------- queries ----------------

    @Transactional(readOnly = true)
    public PhanCaResponse get(UUID id) {
        PhanCa pc = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("PHAN_CA_NOT_FOUND", "Không tìm thấy phân ca"));
        CaLamViec ca = caRepo.findById(pc.getCaId()).orElse(null);
        return PhanCaResponse.from(pc, ca != null ? ca.getMaCa() : null, ca != null ? ca.getTenCa() : null);
    }

    @Transactional(readOnly = true)
    public List<PhanCaResponse> list(UUID nhanVienId, LocalDate from, LocalDate to) {
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to == null) to = LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1);
        Map<UUID, CaLamViec> caById = new HashMap<>();
        return repo.findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(nhanVienId, from, to)
                .stream()
                .map(pc -> {
                    CaLamViec ca = caById.computeIfAbsent(pc.getCaId(), id ->
                            caRepo.findById(id).orElse(null));
                    return PhanCaResponse.from(pc, ca != null ? ca.getMaCa() : null,
                            ca != null ? ca.getTenCa() : null);
                })
                .toList();
    }

    /** Lấy ca chuẩn của NV tại ngày — dùng cho T09 đối chiếu chấm công. */
    @Transactional(readOnly = true)
    public CaLamViec getStandardShift(UUID nhanVienId, LocalDate ngay) {
        return repo.findByNhanVienIdAndNgayApDung(nhanVienId, ngay)
                .flatMap(pc -> caRepo.findById(pc.getCaId()))
                .orElse(null);
    }

    // ---------------- delete ----------------

    @Transactional
    public void delete(UUID id) {
        PhanCa pc = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("PHAN_CA_NOT_FOUND", "Không tìm thấy phân ca"));
        if (chamCongRepo.existsByPhanCaId(id)) {
            throw new BusinessException("PHAN_CA_HAS_CHAM_CONG",
                    "Phân ca đã phát sinh chấm công, không thể xóa; hãy điều chỉnh hoặc thêm phân ca thay thế");
        }
        repo.delete(pc);
    }

    // ---------------- helpers ----------------

    private NhanVien requireActiveEmployee(UUID nhanVienId) {
        NhanVien nv = nvRepo.findById(nhanVienId).orElseThrow(() ->
                new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));
        if (!ALLOWED_STATUS.contains(nv.getTrangThai())) {
            throw new BusinessException("EMPLOYEE_NOT_WORKING",
                    "Nhân viên đang ở trạng thái " + nv.getTrangThai()
                            + ", không thể phân ca");
        }
        return nv;
    }

    private CaLamViec requireActiveShift(UUID caId) {
        CaLamViec ca = caRepo.findById(caId).orElseThrow(() ->
                new ResourceNotFoundException("CA_LAM_VIEC_NOT_FOUND", "Không tìm thấy ca làm việc"));
        if (!ca.isActive()) {
            throw new BusinessException("CA_LAM_VIEC_INACTIVE",
                    "Ca '" + ca.getMaCa() + "' đã đóng, không thể phân");
        }
        return ca;
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from)) {
            throw new BusinessException("PHAN_CA_RANGE_INVALID",
                    "Khoảng ngày không hợp lệ");
        }
    }
}
