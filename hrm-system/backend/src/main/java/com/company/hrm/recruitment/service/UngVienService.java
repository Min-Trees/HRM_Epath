package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.UngVienDto;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.entity.YeuCauTuyenDung;
import com.company.hrm.recruitment.repository.UngVienRepository;
import com.company.hrm.recruitment.repository.YeuCauTuyenDungRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * T17 - Service cho Ung vien.
 */
@Service
public class UngVienService {

    private final UngVienRepository repo;
    private final YeuCauTuyenDungRepository ycRepo;
    private final AtomicLong maCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public UngVienService(UngVienRepository repo, YeuCauTuyenDungRepository ycRepo) {
        this.repo = repo;
        this.ycRepo = ycRepo;
    }

    @Transactional(readOnly = true)
    public List<UngVienDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UngVienDto findById(UUID id) {
        return toDto(repo.findById(id).orElseThrow(() ->
                new BusinessException("UV_NOT_FOUND", "Khong tim thay ung vien")));
    }

    @Transactional(readOnly = true)
    public List<UngVienDto> findByYeuCau(UUID yeuCauId) {
        return repo.findByYeuCauId(yeuCauId).stream().map(this::toDto).toList();
    }

    @Transactional
    public UngVienDto create(UngVienDto dto) {
        if (dto.getHoTen() == null || dto.getHoTen().isBlank()) {
            throw new BusinessException("INVALID_HO_TEN", "Ho ten khong duoc de trong");
        }
        if (dto.getYeuCauId() != null) {
            YeuCauTuyenDung yc = ycRepo.findById(dto.getYeuCauId()).orElse(null);
            if (yc == null) {
                throw new BusinessException("YC_NOT_FOUND", "Yeu cau tuyen dung khong ton tai");
            }
        }
        UngVien e = new UngVien();
        e.setMaUngVien(generateMaUV());
        copyFields(e, dto);
        e.setTrangThai(TrangThaiUngVien.MOI_NOP_HO_SO);
        UngVien saved = repo.save(e);
        return toDto(saved);
    }

    @Transactional
    public UngVienDto updateStatus(UUID id, TrangThaiUngVien trangThai) {
        UngVien e = repo.findById(id).orElseThrow(() ->
                new BusinessException("UV_NOT_FOUND", "Khong tim thay ung vien"));
        e.setTrangThai(trangThai);
        return toDto(repo.save(e));
    }

    private String generateMaUV() {
        return String.format("UV-%05d", maCounter.incrementAndGet());
    }

    private void copyFields(UngVien e, UngVienDto dto) {
        e.setHoTen(dto.getHoTen());
        e.setNgaySinh(dto.getNgaySinh());
        e.setGioiTinh(dto.getGioiTinh());
        e.setEmail(dto.getEmail());
        e.setSoDienThoai(dto.getSoDienThoai());
        e.setDiaChi(dto.getDiaChi());
        e.setCmnd(dto.getCmnd());
        e.setNgayCapCmnd(dto.getNgayCapCmnd());
        e.setNoiCapCmnd(dto.getNoiCapCmnd());
        e.setTrinhDo(dto.getTrinhDo());
        e.setTruongDaoTao(dto.getTruongDaoTao());
        e.setChuyenNganh(dto.getChuyenNganh());
        e.setNamTotNghiep(dto.getNamTotNghiep());
        e.setSoNamKinhNghiem(dto.getSoNamKinhNghiem());
        e.setCongTyCu(dto.getCongTyCu());
        e.setChucDanhCu(dto.getChucDanhCu());
        e.setCvUrl(dto.getCvUrl());
        e.setThuXinViecUrl(dto.getThuXinViecUrl());
        e.setKyNang(dto.getKyNang());
        e.setGhiChu(dto.getGhiChu());
        e.setYeuCauId(dto.getYeuCauId());
        e.setNguoiGioiThieuId(dto.getNguoiGioiThieuId());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
    }

    private UngVienDto toDto(UngVien e) {
        UngVienDto d = new UngVienDto();
        d.setUngVienId(e.getUngVienId());
        d.setMaUngVien(e.getMaUngVien());
        d.setHoTen(e.getHoTen());
        d.setNgaySinh(e.getNgaySinh());
        d.setGioiTinh(e.getGioiTinh());
        d.setEmail(e.getEmail());
        d.setSoDienThoai(e.getSoDienThoai());
        d.setDiaChi(e.getDiaChi());
        d.setCmnd(e.getCmnd());
        d.setNgayCapCmnd(e.getNgayCapCmnd());
        d.setNoiCapCmnd(e.getNoiCapCmnd());
        d.setTrinhDo(e.getTrinhDo());
        d.setTruongDaoTao(e.getTruongDaoTao());
        d.setChuyenNganh(e.getChuyenNganh());
        d.setNamTotNghiep(e.getNamTotNghiep());
        d.setSoNamKinhNghiem(e.getSoNamKinhNghiem());
        d.setCongTyCu(e.getCongTyCu());
        d.setChucDanhCu(e.getChucDanhCu());
        d.setCvUrl(e.getCvUrl());
        d.setThuXinViecUrl(e.getThuXinViecUrl());
        d.setKyNang(e.getKyNang());
        d.setGhiChu(e.getGhiChu());
        d.setTrangThai(e.getTrangThai());
        d.setYeuCauId(e.getYeuCauId());
        d.setNguoiGioiThieuId(e.getNguoiGioiThieuId());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }
}
