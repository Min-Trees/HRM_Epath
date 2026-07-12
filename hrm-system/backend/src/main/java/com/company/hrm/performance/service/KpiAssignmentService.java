package com.company.hrm.performance.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.performance.dto.KpiAssignmentDto;
import com.company.hrm.performance.dto.KpiFinalRatingDto;
import com.company.hrm.performance.dto.KpiReviewDto;
import com.company.hrm.performance.dto.KpiSelfAssessmentDto;
import com.company.hrm.performance.entity.*;
import com.company.hrm.performance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * T18 - Service cho Assignment (gan KPI cho NV).
 */
@Service
public class KpiAssignmentService {

    private final KpiAssignmentRepository repo;
    private final KpiCycleRepository cycleRepo;
    private final KpiSelfAssessmentRepository selfRepo;
    private final KpiReviewRepository reviewRepo;
    private final KpiFinalRatingRepository finalRepo;
    private final NhanVienRepository nhanVienRepo;

    public KpiAssignmentService(KpiAssignmentRepository repo,
                                 KpiCycleRepository cycleRepo,
                                 KpiSelfAssessmentRepository selfRepo,
                                 KpiReviewRepository reviewRepo,
                                 KpiFinalRatingRepository finalRepo,
                                 NhanVienRepository nhanVienRepo) {
        this.repo = repo;
        this.cycleRepo = cycleRepo;
        this.selfRepo = selfRepo;
        this.reviewRepo = reviewRepo;
        this.finalRepo = finalRepo;
        this.nhanVienRepo = nhanVienRepo;
    }

    @Transactional(readOnly = true)
    public List<KpiAssignmentDto> findByNvAndCycle(UUID nvId, UUID cycleId) {
        return repo.findByNhanVienIdAndCycleId(nvId, cycleId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<KpiAssignmentDto> findByCycle(UUID cycleId) {
        return repo.findByCycleId(cycleId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public KpiAssignmentDto findById(UUID id) {
        return toDto(repo.findById(id).orElseThrow(() ->
                new BusinessException("ASSIGN_NOT_FOUND", "Khong tim thay assignment")));
    }

    @Transactional
    public KpiAssignmentDto create(KpiAssignmentDto dto) {
        KpiCycle cycle = cycleRepo.findById(dto.getCycleId()).orElseThrow(() ->
                new BusinessException("CYCLE_NOT_FOUND", "Cycle khong ton tai"));
        if (cycle.getTrangThai() == TrangThaiChuKy.DA_DONG || cycle.getTrangThai() == TrangThaiChuKy.HUY) {
            throw new BusinessException("CYCLE_CLOSED", "Cycle da dong, khong the gan KPI");
        }
        if (dto.getTrongSo() == null) dto.setTrongSo(java.math.BigDecimal.ONE);
        KpiAssignment e = new KpiAssignment();
        e.setCycleId(dto.getCycleId());
        e.setNhanVienId(dto.getNhanVienId());
        e.setTemplateId(dto.getTemplateId());
        e.setTenMucTieu(dto.getTenMucTieu());
        e.setLoaiMucTieu(dto.getLoaiMucTieu() != null ? dto.getLoaiMucTieu() : LoaiMucTieu.KPI);
        e.setDonViDo(dto.getDonViDo());
        e.setTargetValue(dto.getTargetValue());
        e.setTrongSo(dto.getTrongSo());
        e.setMoTaChiTiet(dto.getMoTaChiTiet());
        e.setNguoiGanId(dto.getNguoiGanId());
        e.setTrangThai(TrangThaiAssignment.MOI_GAN);
        return toDto(repo.save(e));
    }

    @Transactional
    public KpiAssignmentDto selfAssess(UUID assignmentId, KpiSelfAssessmentDto dto) {
        KpiAssignment a = repo.findById(assignmentId).orElseThrow(() ->
                new BusinessException("ASSIGN_NOT_FOUND", "Khong tim thay assignment"));
        if (a.getTrangThai() != TrangThaiAssignment.MOI_GAN) {
            throw new BusinessException("INVALID_STATE", "Chi assignment MOI_GAN moi co the tu danh gia");
        }
        if (dto.getDiemTuDanhGia() != null &&
            (dto.getDiemTuDanhGia().doubleValue() < 0 || dto.getDiemTuDanhGia().doubleValue() > 100)) {
            throw new BusinessException("INVALID_DIEM", "Diem tu danh gia phai trong [0, 100]");
        }

        KpiSelfAssessment sa = new KpiSelfAssessment();
        sa.setAssignmentId(assignmentId);
        sa.setActualValue(dto.getActualValue());
        sa.setTyLeHoanThanh(dto.getTyLeHoanThanh());
        sa.setDiemTuDanhGia(dto.getDiemTuDanhGia());
        sa.setNhanXetNv(dto.getNhanXetNv());
        sa.setMinhChungUrl(dto.getMinhChungUrl());
        selfRepo.save(sa);

        a.setTrangThai(TrangThaiAssignment.NV_DA_TU_DANH_GIA);
        return toDto(repo.save(a));
    }

    @Transactional
    public KpiAssignmentDto managerReview(UUID assignmentId, KpiReviewDto dto) {
        KpiAssignment a = repo.findById(assignmentId).orElseThrow(() ->
                new BusinessException("ASSIGN_NOT_FOUND", "Khong tim thay assignment"));
        if (a.getTrangThai() != TrangThaiAssignment.NV_DA_TU_DANH_GIA) {
            throw new BusinessException("INVALID_STATE", "Can NV tu danh gia truoc");
        }
        if (dto.getDiemManager() == null ||
            dto.getDiemManager().doubleValue() < 0 || dto.getDiemManager().doubleValue() > 100) {
            throw new BusinessException("INVALID_DIEM", "Diem manager phai trong [0, 100]");
        }

        KpiReview r = new KpiReview();
        r.setAssignmentId(assignmentId);
        r.setNguoiReviewId(dto.getNguoiReviewId());
        r.setActualValue(dto.getActualValue());
        r.setDiemManager(dto.getDiemManager());
        r.setNhanXetManager(dto.getNhanXetManager());
        r.setDiemManh(dto.getDiemManh());
        r.setDiemYeu(dto.getDiemYeu());
        r.setDeXuatXepLoai(dto.getDeXuatXepLoai());
        reviewRepo.save(r);

        a.setTrangThai(TrangThaiAssignment.MANAGER_DA_REVIEW);
        return toDto(repo.save(a));
    }

    @Transactional
    public KpiAssignmentDto hrApprove(UUID assignmentId, KpiFinalRatingDto dto) {
        KpiAssignment a = repo.findById(assignmentId).orElseThrow(() ->
                new BusinessException("ASSIGN_NOT_FOUND", "Khong tim thay assignment"));
        if (a.getTrangThai() != TrangThaiAssignment.MANAGER_DA_REVIEW) {
            throw new BusinessException("INVALID_STATE", "Can manager review truoc");
        }
        if (dto.getDiemCuoi() == null ||
            dto.getDiemCuoi().doubleValue() < 0 || dto.getDiemCuoi().doubleValue() > 100) {
            throw new BusinessException("INVALID_DIEM", "Diem cuoi phai trong [0, 100]");
        }

        KpiFinalRating fr = new KpiFinalRating();
        fr.setAssignmentId(assignmentId);
        fr.setNguoiPheDuyetId(dto.getNguoiPheDuyetId());
        fr.setXepLoaiCuoi(dto.getXepLoaiCuoi());
        fr.setDiemCuoi(dto.getDiemCuoi());
        fr.setNhanXetHr(dto.getNhanXetHr());
        fr.setHeSoThuong(dto.getHeSoThuong());
        finalRepo.save(fr);

        a.setTrangThai(TrangThaiAssignment.HR_DA_PHE_DUYET);
        return toDto(repo.save(a));
    }

    private KpiAssignmentDto toDto(KpiAssignment e) {
        KpiAssignmentDto d = new KpiAssignmentDto();
        d.setAssignmentId(e.getAssignmentId());
        d.setCycleId(e.getCycleId());
        d.setNhanVienId(e.getNhanVienId());
        d.setTemplateId(e.getTemplateId());
        d.setTenMucTieu(e.getTenMucTieu());
        d.setLoaiMucTieu(e.getLoaiMucTieu());
        d.setDonViDo(e.getDonViDo());
        d.setTargetValue(e.getTargetValue());
        d.setTrongSo(e.getTrongSo());
        d.setMoTaChiTiet(e.getMoTaChiTiet());
        d.setNguoiGanId(e.getNguoiGanId());
        d.setTrangThai(e.getTrangThai());
        d.setCreatedAt(e.getCreatedAt());

        selfRepo.findByAssignmentId(e.getAssignmentId()).ifPresent(sa -> d.setDiemTuDanhGia(sa.getDiemTuDanhGia()));
        reviewRepo.findByAssignmentId(e.getAssignmentId()).ifPresent(rv -> {
            d.setDiemManager(rv.getDiemManager());
            d.setDiemTrungBinh(rv.getDiemTrungBinh());
        });
        finalRepo.findByAssignmentId(e.getAssignmentId()).ifPresent(fr ->
                d.setXepLoaiCuoi(fr.getXepLoaiCuoi().name()));

        nhanVienRepo.findById(e.getNhanVienId()).ifPresent(nv -> {
            d.setHoTen(nv.getHoTen());
            d.setMaNv(nv.getMaNv());
        });
        return d;
    }
}
