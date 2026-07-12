package com.company.hrm.hr.offboarding.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.page.PageResponse;
import com.company.hrm.hr.entity.BienDongNhanSu;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.offboarding.dto.OffboardingCaseDto;
import com.company.hrm.hr.offboarding.dto.OffboardingTaskDto;
import com.company.hrm.hr.offboarding.entity.OffboardingCase;
import com.company.hrm.hr.offboarding.entity.OffboardingTask;
import com.company.hrm.hr.offboarding.entity.TrangThaiOffboarding;
import com.company.hrm.hr.offboarding.entity.TrangThaiTask;
import com.company.hrm.hr.offboarding.repository.OffboardingCaseRepository;
import com.company.hrm.hr.offboarding.repository.OffboardingTaskRepository;
import com.company.hrm.hr.repository.BienDongNhanSuRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * T14 - Service chinh cho Offboarding: tao case, phe duyet, hoan thanh checklist,
 * dong bo voi bien dong nhan su (CHAM_DUT_HDLD) khi hoan thanh.
 */
@Service
public class OffboardingCaseService {

    private final OffboardingCaseRepository caseRepo;
    private final OffboardingTaskRepository taskRepo;
    private final NhanVienRepository nhanVienRepo;
    private final BienDongNhanSuRepository bienDongRepo;
    private final ApplicationEventPublisher events;
    private final OffboardingTaskTemplateService templateService;

    public OffboardingCaseService(OffboardingCaseRepository caseRepo,
                                  OffboardingTaskRepository taskRepo,
                                  NhanVienRepository nhanVienRepo,
                                  BienDongNhanSuRepository bienDongRepo,
                                  ApplicationEventPublisher events,
                                  OffboardingTaskTemplateService templateService) {
        this.caseRepo = caseRepo;
        this.taskRepo = taskRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.bienDongRepo = bienDongRepo;
        this.events = events;
        this.templateService = templateService;
    }

    /** Tao case offboarding moi, kem checklist mac dinh theo template. */
    @Transactional
    public OffboardingCaseDto create(OffboardingCaseDto dto, UUID nguoiTaoId) {
        NhanVien nv = nhanVienRepo.findById(dto.getNhanVienId())
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Khong tim thay nhan vien"));

        // Kiem tra nhan vien chua co case active
        caseRepo.findByNhanVienIdAndTrangThaiNotIn(dto.getNhanVienId(),
                List.of(TrangThaiOffboarding.HOAN_THANH, TrangThaiOffboarding.HUY))
                .ifPresent(c -> {
                    throw new BusinessException("OFFBOARDING_ACTIVE_EXISTS",
                            "Nhan vien dang co ho so offboarding chua hoan thanh");
                });

        OffboardingCase c = new OffboardingCase();
        c.setNhanVienId(dto.getNhanVienId());
        c.setSoQuyetDinh(dto.getSoQuyetDinh());
        c.setNgayQuyetDinh(dto.getNgayQuyetDinh());
        c.setNgayNghiViecCuoi(dto.getNgayNghiViecCuoi());
        c.setNgayChinhThucNghi(dto.getNgayChinhThucNghi());
        c.setLyDo(dto.getLyDo());
        c.setLyDoChiTiet(dto.getLyDoChiTiet());
        c.setTrangThai(TrangThaiOffboarding.MOI_TAO);
        c.setNguoiTaoId(nguoiTaoId);
        c.setGhiChu(dto.getGhiChu());
        OffboardingCase saved = caseRepo.save(c);

        // Sinh checklist mac dinh theo template theo ly do
        List<OffboardingTask> defaults = templateService.defaultChecklistFor(saved);
        for (OffboardingTask t : defaults) {
            t.setCaseId(saved.getCaseId());
            taskRepo.save(t);
        }

        return toDto(saved, nv);
    }

    @Transactional(readOnly = true)
    public OffboardingCaseDto get(UUID caseId) {
        OffboardingCase c = caseRepo.findById(caseId)
                .orElseThrow(() -> new BusinessException("OFFBOARDING_NOT_FOUND", "Khong tim thay ho so"));
        NhanVien nv = nhanVienRepo.findById(c.getNhanVienId())
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Khong tim thay nhan vien"));
        return toDto(c, nv);
    }

    @Transactional(readOnly = true)
    public PageResponse<OffboardingCaseDto> list(Pageable pageable) {
        Page<OffboardingCase> page = caseRepo.findAll(pageable);
        List<OffboardingCaseDto> dtos = new ArrayList<>();
        for (OffboardingCase c : page.getContent()) {
            NhanVien nv = nhanVienRepo.findById(c.getNhanVienId()).orElse(null);
            dtos.add(toDto(c, nv));
        }
        return new PageResponse<>(dtos, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @Transactional
    public OffboardingCaseDto updateStatus(UUID caseId, TrangThaiOffboarding newStatus, UUID nguoiDuyetId) {
        OffboardingCase c = caseRepo.findById(caseId)
                .orElseThrow(() -> new BusinessException("OFFBOARDING_NOT_FOUND", "Khong tim thay ho so"));
        validateTransition(c.getTrangThai(), newStatus);

        c.setTrangThai(newStatus);
        if (newStatus == TrangThaiOffboarding.DANG_THUC_HIEN && nguoiDuyetId != null) {
            c.setNguoiDuyetId(nguoiDuyetId);
            c.setNgayDuyet(LocalDateTime.now());
        }
        caseRepo.save(c);

        // Khi HOAN_THANH -> tu dong tao bien dong nhan su CHAM_DUT_HDLD + cap nhat trang_thai NV
        if (newStatus == TrangThaiOffboarding.HOAN_THANH) {
            finalizeCase(c);
        }
        NhanVien nv = nhanVienRepo.findById(c.getNhanVienId()).orElse(null);
        return toDto(c, nv);
    }

    private void validateTransition(TrangThaiOffboarding from, TrangThaiOffboarding to) {
        boolean valid = switch (from) {
            case MOI_TAO -> to == TrangThaiOffboarding.CHO_DUYET || to == TrangThaiOffboarding.HUY;
            case CHO_DUYET -> to == TrangThaiOffboarding.DANG_THUC_HIEN || to == TrangThaiOffboarding.HUY;
            case DANG_THUC_HIEN -> to == TrangThaiOffboarding.CHO_QUYET_TOAN || to == TrangThaiOffboarding.HUY;
            case CHO_QUYET_TOAN -> to == TrangThaiOffboarding.HOAN_THANH || to == TrangThaiOffboarding.HUY;
            case HOAN_THANH, HUY -> false;
        };
        if (!valid) {
            throw new BusinessException("OFFBOARDING_INVALID_TRANSITION",
                    "Khong the chuyen trang thai tu " + from + " sang " + to);
        }
    }

    private void finalizeCase(OffboardingCase c) {
        NhanVien nv = nhanVienRepo.findById(c.getNhanVienId()).orElseThrow();
        // Cap nhat trang_thai nhan vien
        NhanVien.TrangThaiNv newStatus = (c.getLyDo() == com.company.hrm.hr.offboarding.entity.LyDoNghiViec.NGHI_HUU)
                ? NhanVien.TrangThaiNv.DA_NGHI_HUU
                : NhanVien.TrangThaiNv.DA_NGHI_VIEC;
        nv.setTrangThai(newStatus);
        nhanVienRepo.save(nv);

        // Tao bien dong nhan su CHAM_DUT_HDLD (append-only)
        BienDongNhanSu bd = new BienDongNhanSu();
        bd.setNhanVienId(c.getNhanVienId());
        bd.setLoaiBienDong(BienDongNhanSu.LoaiBienDong.CHAM_DUT_HDLD);
        bd.setSoQuyetDinh(c.getSoQuyetDinh());
        bd.setNgayQuyetDinh(c.getNgayQuyetDinh());
        bd.setNgayHieuLuc(c.getNgayChinhThucNghi());
        bd.setTrangThaiNvSau(BienDongNhanSu.TrangThaiNvSau.valueOf(newStatus.name()));
        bd.setLyDo(c.getLyDoChiTiet() != null ? c.getLyDoChiTiet() : c.getLyDo().name());
        bienDongRepo.save(bd);
    }

    /** Lay tat ca task theo case. */
    @Transactional(readOnly = true)
    public List<OffboardingTaskDto> listTasks(UUID caseId) {
        List<OffboardingTask> tasks = taskRepo.findByCaseIdOrderByThuTuAscCreatedAtAsc(caseId);
        return tasks.stream().map(this::toTaskDto).toList();
    }

    /** Cap nhat trang thai mot task (nhan vien phu trach danh dau hoan thanh). */
    @Transactional
    public OffboardingTaskDto updateTask(UUID taskId, TrangThaiTask trangThai, UUID nguoiHoanThanhId,
                                         String fileDinhKemUrl, String ghiChu) {
        OffboardingTask t = taskRepo.findById(taskId)
                .orElseThrow(() -> new BusinessException("TASK_NOT_FOUND", "Khong tim thay task"));
        t.setTrangThai(trangThai);
        if (trangThai == TrangThaiTask.HOAN_THANH) {
            t.setNgayHoanThanh(LocalDateTime.now());
            t.setNguoiHoanThanhId(nguoiHoanThanhId);
        }
        if (fileDinhKemUrl != null) t.setFileDinhKemUrl(fileDinhKemUrl);
        if (ghiChu != null) t.setGhiChu(ghiChu);
        taskRepo.save(t);
        return toTaskDto(t);
    }

    private OffboardingCaseDto toDto(OffboardingCase c, NhanVien nv) {
        OffboardingCaseDto d = new OffboardingCaseDto();
        d.setCaseId(c.getCaseId());
        d.setNhanVienId(c.getNhanVienId());
        if (nv != null) {
            d.setMaNv(nv.getMaNv());
            d.setHoTen(nv.getHoTen());
        }
        d.setSoQuyetDinh(c.getSoQuyetDinh());
        d.setNgayQuyetDinh(c.getNgayQuyetDinh());
        d.setNgayNghiViecCuoi(c.getNgayNghiViecCuoi());
        d.setNgayChinhThucNghi(c.getNgayChinhThucNghi());
        d.setLyDo(c.getLyDo());
        d.setLyDoChiTiet(c.getLyDoChiTiet());
        d.setTrangThai(c.getTrangThai());
        d.setNguoiTaoId(c.getNguoiTaoId());
        d.setNguoiDuyetId(c.getNguoiDuyetId());
        d.setNgayDuyet(c.getNgayDuyet());
        d.setGhiChu(c.getGhiChu());
        d.setBhxhBienDongGiamId(c.getBhxhBienDongGiamId());
        d.setQuyetToanThueId(c.getQuyetToanThueId());

        // Thong ke task
        List<OffboardingTask> tasks = taskRepo.findByCaseIdOrderByThuTuAscCreatedAtAsc(c.getCaseId());
        long total = tasks.size();
        long done = tasks.stream().filter(t ->
                t.getTrangThai() == TrangThaiTask.HOAN_THANH ||
                t.getTrangThai() == TrangThaiTask.KHONG_AP_DUNG).count();
        d.setTongTask(total);
        d.setTaskHoanThanh(done);
        d.setTienDoPhanTram(total == 0 ? 0.0 : Math.round(done * 10000.0 / total) / 100.0);
        return d;
    }

    private OffboardingTaskDto toTaskDto(OffboardingTask t) {
        OffboardingTaskDto d = new OffboardingTaskDto();
        d.setTaskId(t.getTaskId());
        d.setCaseId(t.getCaseId());
        d.setLoaiTask(t.getLoaiTask());
        d.setMoTa(t.getMoTa());
        d.setNguoiPhuTrachId(t.getNguoiPhuTrachId());
        d.setHanHoanThanh(t.getHanHoanThanh());
        d.setTrangThai(t.getTrangThai());
        d.setNgayHoanThanh(t.getNgayHoanThanh());
        d.setNguoiHoanThanhId(t.getNguoiHoanThanhId());
        d.setFileDinhKemUrl(t.getFileDinhKemUrl());
        d.setGhiChu(t.getGhiChu());
        d.setThuTu(t.getThuTu());
        return d;
    }
}
