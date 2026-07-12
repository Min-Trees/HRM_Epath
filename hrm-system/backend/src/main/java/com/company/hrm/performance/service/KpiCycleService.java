package com.company.hrm.performance.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.performance.dto.KpiCycleDto;
import com.company.hrm.performance.entity.KpiCycle;
import com.company.hrm.performance.entity.TrangThaiChuKy;
import com.company.hrm.performance.repository.KpiAssignmentRepository;
import com.company.hrm.performance.repository.KpiCycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * T18 - Service cho Chu ky danh gia (cycle).
 */
@Service
public class KpiCycleService {

    private final KpiCycleRepository repo;
    private final KpiAssignmentRepository assignRepo;

    public KpiCycleService(KpiCycleRepository repo, KpiAssignmentRepository assignRepo) {
        this.repo = repo;
        this.assignRepo = assignRepo;
    }

    @Transactional(readOnly = true)
    public List<KpiCycleDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public KpiCycleDto findById(UUID id) {
        return toDto(repo.findById(id).orElseThrow(() ->
                new BusinessException("CYCLE_NOT_FOUND", "Khong tim thay chu ky")));
    }

    @Transactional
    public KpiCycleDto create(KpiCycleDto dto) {
        if (dto.getNgayKetThuc().isBefore(dto.getNgayBatDau()) ||
            dto.getNgayKetThuc().isEqual(dto.getNgayBatDau())) {
            throw new BusinessException("INVALID_NGAY", "Ngay ket thuc phai sau ngay bat dau");
        }
        KpiCycle e = new KpiCycle();
        e.setTenChuKy(dto.getTenChuKy());
        e.setLoaiChuKy(dto.getLoaiChuKy());
        e.setNgayBatDau(dto.getNgayBatDau());
        e.setNgayKetThuc(dto.getNgayKetThuc());
        e.setHanNvTuDanhGia(dto.getHanNvTuDanhGia());
        e.setHanManagerReview(dto.getHanManagerReview());
        e.setHanHrPheDuyet(dto.getHanHrPheDuyet());
        e.setMoTa(dto.getMoTa());
        e.setTrangThai(TrangThaiChuKy.MOI_TAO);
        return toDto(repo.save(e));
    }

    @Transactional
    public KpiCycleDto startCycle(UUID id) {
        KpiCycle e = repo.findById(id).orElseThrow(() ->
                new BusinessException("CYCLE_NOT_FOUND", "Khong tim thay chu ky"));
        if (e.getTrangThai() != TrangThaiChuKy.MOI_TAO) {
            throw new BusinessException("INVALID_STATE", "Chi cycle MOI_TAO moi co the bat dau");
        }
        e.setTrangThai(TrangThaiChuKy.DANG_DANH_GIA);
        return toDto(repo.save(e));
    }

    @Transactional
    public KpiCycleDto closeCycle(UUID id) {
        KpiCycle e = repo.findById(id).orElseThrow(() ->
                new BusinessException("CYCLE_NOT_FOUND", "Khong tim thay chu ky"));
        e.setTrangThai(TrangThaiChuKy.DA_DONG);
        return toDto(repo.save(e));
    }

    private KpiCycleDto toDto(KpiCycle e) {
        KpiCycleDto d = new KpiCycleDto();
        d.setCycleId(e.getCycleId());
        d.setTenChuKy(e.getTenChuKy());
        d.setLoaiChuKy(e.getLoaiChuKy());
        d.setNgayBatDau(e.getNgayBatDau());
        d.setNgayKetThuc(e.getNgayKetThuc());
        d.setHanNvTuDanhGia(e.getHanNvTuDanhGia());
        d.setHanManagerReview(e.getHanManagerReview());
        d.setHanHrPheDuyet(e.getHanHrPheDuyet());
        d.setTrangThai(e.getTrangThai());
        d.setNguoiTaoId(e.getNguoiTaoId());
        d.setMoTa(e.getMoTa());
        d.setSoMucTieu(assignRepo.findByCycleId(e.getCycleId()).size());
        d.setSoNvThamGia((int) assignRepo.findByCycleId(e.getCycleId()).stream()
                .map(a -> a.getNhanVienId()).distinct().count());
        return d;
    }
}
