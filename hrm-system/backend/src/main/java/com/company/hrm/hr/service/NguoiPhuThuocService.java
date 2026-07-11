package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.NguoiPhuThuocRequest;
import com.company.hrm.hr.dto.NguoiPhuThuocResponse;
import com.company.hrm.hr.entity.NguoiPhuThuoc;
import com.company.hrm.hr.repository.NguoiPhuThuocRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NguoiPhuThuocService {

    private final NguoiPhuThuocRepository repo;
    private final NhanVienRepository nvRepo;

    public NguoiPhuThuocService(NguoiPhuThuocRepository repo, NhanVienRepository nvRepo) {
        this.repo = repo;
        this.nvRepo = nvRepo;
    }

    @Transactional(readOnly = true)
    public List<NguoiPhuThuocResponse> list(UUID nhanVienId) {
        return repo.findByNhanVienIdAndActiveTrue(nhanVienId).stream()
                .map(NguoiPhuThuocResponse::from).toList();
    }

    @Transactional
    public NguoiPhuThuocResponse add(UUID nhanVienId, NguoiPhuThuocRequest req) {
        requireNhanVien(nhanVienId);
        validateGiamTru(req);
        NguoiPhuThuoc e = new NguoiPhuThuoc();
        e.setNhanVienId(nhanVienId);
        apply(e, req);
        e.setActive(true);
        return NguoiPhuThuocResponse.from(repo.save(e));
    }

    @Transactional
    public NguoiPhuThuocResponse update(UUID nhanVienId, UUID id, NguoiPhuThuocRequest req) {
        requireNhanVien(nhanVienId);
        NguoiPhuThuoc e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "DEPENDENT_NOT_FOUND", "Không tìm thấy người phụ thuộc"));
        if (!e.getNhanVienId().equals(nhanVienId)) {
            throw new BusinessException("DEPENDENT_OWNER_MISMATCH",
                    "Người phụ thuộc không thuộc nhân viên này");
        }
        validateGiamTru(req);
        apply(e, req);
        return NguoiPhuThuocResponse.from(repo.save(e));
    }

    @Transactional
    public void delete(UUID nhanVienId, UUID id) {
        NguoiPhuThuoc e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "DEPENDENT_NOT_FOUND", "Không tìm thấy người phụ thuộc"));
        if (!e.getNhanVienId().equals(nhanVienId)) {
            throw new BusinessException("DEPENDENT_OWNER_MISMATCH",
                    "Người phụ thuộc không thuộc nhân viên này");
        }
        e.setActive(false);
        repo.save(e);
    }

    private void requireNhanVien(UUID nhanVienId) {
        if (!nvRepo.existsById(nhanVienId)) {
            throw new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên");
        }
    }

    private void validateGiamTru(NguoiPhuThuocRequest req) {
        if (req.getDenNgayGiamTru() != null && req.getDenNgayGiamTru().isBefore(req.getTuNgayGiamTru())) {
            throw new BusinessException("DEPENDENT_DATE_INVALID",
                    "Đến ngày giảm trừ phải >= từ ngày giảm trừ");
        }
    }

    private void apply(NguoiPhuThuoc e, NguoiPhuThuocRequest req) {
        e.setHoTen(req.getHoTen());
        e.setNgaySinh(req.getNgaySinh());
        e.setQuanHe(req.getQuanHe());
        e.setSoCccdHoacKhaiSinh(req.getSoCccdHoacKhaiSinh());
        e.setMaSoThuePhuThuoc(req.getMaSoThuePhuThuoc());
        e.setTuNgayGiamTru(req.getTuNgayGiamTru());
        e.setDenNgayGiamTru(req.getDenNgayGiamTru());
    }
}