package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.LichPhongVanDto;
import com.company.hrm.recruitment.entity.LichPhongVan;
import com.company.hrm.recruitment.entity.TrangThaiLichPV;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.repository.LichPhongVanRepository;
import com.company.hrm.recruitment.repository.UngVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * T17 - Service cho Lich phong van.
 */
@Service
public class LichPhongVanService {

    private final LichPhongVanRepository repo;
    private final UngVienRepository ungVienRepo;

    public LichPhongVanService(LichPhongVanRepository repo, UngVienRepository ungVienRepo) {
        this.repo = repo;
        this.ungVienRepo = ungVienRepo;
    }

    @Transactional(readOnly = true)
    public List<LichPhongVanDto> findByUngVien(UUID ungVienId) {
        return repo.findByUngVienIdOrderByVongPhongVanAsc(ungVienId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public LichPhongVanDto findById(UUID id) {
        return toDto(repo.findById(id).orElseThrow(() ->
                new BusinessException("LICH_PV_NOT_FOUND", "Khong tim thay lich phong van")));
    }

    @Transactional
    public LichPhongVanDto schedule(LichPhongVanDto dto) {
        if (dto.getThoiGianKetThuc().isBefore(dto.getThoiGianBatDau()) ||
            dto.getThoiGianKetThuc().isEqual(dto.getThoiGianBatDau())) {
            throw new BusinessException("INVALID_TIME",
                    "Thoi gian ket thuc phai sau bat dau");
        }
        UngVien uv = ungVienRepo.findById(dto.getUngVienId()).orElseThrow(() ->
                new BusinessException("UV_NOT_FOUND", "Ung vien khong ton tai"));
        LichPhongVan e = new LichPhongVan();
        e.setUngVienId(dto.getUngVienId());
        e.setVongPhongVan(dto.getVongPhongVan());
        e.setThoiGianBatDau(dto.getThoiGianBatDau());
        e.setThoiGianKetThuc(dto.getThoiGianKetThuc());
        e.setDiaDiem(dto.getDiaDiem());
        e.setHinhThuc(dto.getHinhThuc() != null ? dto.getHinhThuc() : "TRUC_TIEP");
        e.setLinkOnline(dto.getLinkOnline());
        if (dto.getNguoiPhongVanIds() != null && !dto.getNguoiPhongVanIds().isEmpty()) {
            e.setNguoiPhongVanIds(dto.getNguoiPhongVanIds().toArray(new UUID[0]));
        }
        e.setNguoiToChucId(dto.getNguoiToChucId());
        e.setGhiChu(dto.getGhiChu());
        e.setTrangThai(TrangThaiLichPV.CHUA_DIEN_RA);
        LichPhongVan saved = repo.save(e);

        // Cap nhat trang thai ung vien
        if (dto.getVongPhongVan() != null && dto.getVongPhongVan() >= 2) {
            uv.setTrangThai(TrangThaiUngVien.CHO_PHONG_VAN_VONG_2);
        } else {
            uv.setTrangThai(TrangThaiUngVien.CHO_PHONG_VAN_VONG_1);
        }
        ungVienRepo.save(uv);

        return toDto(saved);
    }

    @Transactional
    public LichPhongVanDto updateStatus(UUID id, TrangThaiLichPV trangThai) {
        LichPhongVan e = repo.findById(id).orElseThrow(() ->
                new BusinessException("LICH_PV_NOT_FOUND", "Khong tim thay lich phong van"));
        e.setTrangThai(trangThai);
        return toDto(repo.save(e));
    }

    private LichPhongVanDto toDto(LichPhongVan e) {
        LichPhongVanDto d = new LichPhongVanDto();
        d.setLichPvId(e.getLichPvId());
        d.setUngVienId(e.getUngVienId());
        d.setVongPhongVan(e.getVongPhongVan());
        d.setThoiGianBatDau(e.getThoiGianBatDau());
        d.setThoiGianKetThuc(e.getThoiGianKetThuc());
        d.setDiaDiem(e.getDiaDiem());
        d.setHinhThuc(e.getHinhThuc());
        d.setLinkOnline(e.getLinkOnline());
        d.setNguoiPhongVanIds(e.getNguoiPhongVanIds() == null
                ? List.of() : List.of(e.getNguoiPhongVanIds()));
        d.setNguoiToChucId(e.getNguoiToChucId());
        d.setTrangThai(e.getTrangThai());
        d.setGhiChu(e.getGhiChu());
        // Lay ten ung vien (lazy)
        ungVienRepo.findById(e.getUngVienId()).ifPresent(uv -> {
            d.setTenUngVien(uv.getHoTen());
            d.setMaUngVien(uv.getMaUngVien());
        });
        return d;
    }
}
