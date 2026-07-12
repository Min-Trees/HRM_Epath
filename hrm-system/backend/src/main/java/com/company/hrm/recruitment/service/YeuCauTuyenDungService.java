package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.UngVienDto;
import com.company.hrm.recruitment.dto.YeuCauTuyenDungDto;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.TrangThaiYeuCau;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.entity.YeuCauTuyenDung;
import com.company.hrm.recruitment.repository.UngVienRepository;
import com.company.hrm.recruitment.repository.YeuCauTuyenDungRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * T17 - Service cho Yeu cau tuyen dung.
 */
@Service
public class YeuCauTuyenDungService {

    private final YeuCauTuyenDungRepository repo;
    private final UngVienRepository ungVienRepo;
    private final AtomicLong maCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public YeuCauTuyenDungService(YeuCauTuyenDungRepository repo, UngVienRepository ungVienRepo) {
        this.repo = repo;
        this.ungVienRepo = ungVienRepo;
    }

    @Transactional(readOnly = true)
    public List<YeuCauTuyenDungDto> findAll() {
        return repo.findAll().stream().map(this::toDtoWithCount).toList();
    }

    @Transactional(readOnly = true)
    public YeuCauTuyenDungDto findById(UUID id) {
        YeuCauTuyenDung e = repo.findById(id)
                .orElseThrow(() -> new BusinessException("YC_NOT_FOUND", "Khong tim thay yeu cau"));
        return toDtoWithCount(e);
    }

    @Transactional
    public YeuCauTuyenDungDto create(YeuCauTuyenDungDto dto) {
        if (dto.getSoLuongCan() == null || dto.getSoLuongCan() < 1) {
            throw new BusinessException("INVALID_SO_LUONG", "So luong can phai >= 1");
        }
        YeuCauTuyenDung e = new YeuCauTuyenDung();
        e.setTieuDe(dto.getTieuDe());
        e.setPhongBanId(dto.getPhongBanId());
        e.setNguoiYeuCauId(dto.getNguoiYeuCauId());
        e.setSoLuongCan(dto.getSoLuongCan());
        e.setLyDo(dto.getLyDo());
        e.setMucLuongDeXuat(dto.getMucLuongDeXuat());
        e.setNgayCanTuyen(dto.getNgayCanTuyen());
        e.setTrangThai(TrangThaiYeuCau.MOI_TAO);
        e.setMaYeuCau(generateMaYC());
        YeuCauTuyenDung saved = repo.save(e);
        return toDtoWithCount(saved);
    }

    @Transactional
    public YeuCauTuyenDungDto submitForApproval(UUID id) {
        YeuCauTuyenDung e = repo.findById(id).orElseThrow(() ->
                new BusinessException("YC_NOT_FOUND", "Khong tim thay yeu cau"));
        if (e.getTrangThai() != TrangThaiYeuCau.MOI_TAO) {
            throw new BusinessException("INVALID_STATE",
                    "Chi yeu cau o trang thai MOI_TAO moi co the gui phe duyet");
        }
        e.setTrangThai(TrangThaiYeuCau.CHO_PHE_DUYET);
        return toDtoWithCount(repo.save(e));
    }

    @Transactional
    public YeuCauTuyenDungDto approve(UUID id, UUID nguoiPheDuyetId) {
        YeuCauTuyenDung e = repo.findById(id).orElseThrow(() ->
                new BusinessException("YC_NOT_FOUND", "Khong tim thay yeu cau"));
        if (e.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new BusinessException("INVALID_STATE",
                    "Chi yeu cau CHO_PHE_DUYET moi co the duyet");
        }
        e.setTrangThai(TrangThaiYeuCau.DA_PHE_DUYET);
        e.setNguoiPheDuyetId(nguoiPheDuyetId);
        e.setNgayPheDuyet(LocalDateTime.now());
        return toDtoWithCount(repo.save(e));
    }

    @Transactional
    public YeuCauTuyenDungDto startRecruiting(UUID id) {
        YeuCauTuyenDung e = repo.findById(id).orElseThrow(() ->
                new BusinessException("YC_NOT_FOUND", "Khong tim thay yeu cau"));
        if (e.getTrangThai() != TrangThaiYeuCau.DA_PHE_DUYET) {
            throw new BusinessException("INVALID_STATE", "Can phai DA_PHE_DUYET truoc");
        }
        e.setTrangThai(TrangThaiYeuCau.DANG_TUYEN);
        return toDtoWithCount(repo.save(e));
    }

    @Transactional
    public YeuCauTuyenDungDto close(UUID id) {
        YeuCauTuyenDung e = repo.findById(id).orElseThrow(() ->
                new BusinessException("YC_NOT_FOUND", "Khong tim thay yeu cau"));
        e.setTrangThai(TrangThaiYeuCau.DA_DONG);
        e.setNgayDongYeuCau(LocalDate.now());
        return toDtoWithCount(repo.save(e));
    }

    private String generateMaYC() {
        return String.format("YC-%05d", maCounter.incrementAndGet());
    }

    private YeuCauTuyenDungDto toDtoWithCount(YeuCauTuyenDung e) {
        YeuCauTuyenDungDto d = new YeuCauTuyenDungDto();
        d.setYeuCauId(e.getYeuCauId());
        d.setMaYeuCau(e.getMaYeuCau());
        d.setTieuDe(e.getTieuDe());
        d.setPhongBanId(e.getPhongBanId());
        d.setNguoiYeuCauId(e.getNguoiYeuCauId());
        d.setSoLuongCan(e.getSoLuongCan());
        d.setLyDo(e.getLyDo());
        d.setMucLuongDeXuat(e.getMucLuongDeXuat());
        d.setTrangThai(e.getTrangThai());
        d.setNguoiPheDuyetId(e.getNguoiPheDuyetId());
        d.setNgayPheDuyet(e.getNgayPheDuyet());
        d.setNgayCanTuyen(e.getNgayCanTuyen());
        d.setNgayDongYeuCau(e.getNgayDongYeuCau());
        d.setSoUngVien(ungVienRepo.findByYeuCauId(e.getYeuCauId()).size());
        return d;
    }
}
