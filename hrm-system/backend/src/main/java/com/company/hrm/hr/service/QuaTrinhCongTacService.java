package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.QuaTrinhCongTacRequest;
import com.company.hrm.hr.dto.QuaTrinhCongTacResponse;
import com.company.hrm.hr.entity.QuaTrinhCongTac;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.hr.repository.QuaTrinhCongTacRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class QuaTrinhCongTacService {

    private final QuaTrinhCongTacRepository repo;
    private final NhanVienRepository nvRepo;

    public QuaTrinhCongTacService(QuaTrinhCongTacRepository repo, NhanVienRepository nvRepo) {
        this.repo = repo;
        this.nvRepo = nvRepo;
    }

    @Transactional(readOnly = true)
    public List<QuaTrinhCongTacResponse> list(UUID nhanVienId) {
        return repo.findByNhanVienIdOrderByTuNgayDesc(nhanVienId).stream()
                .map(QuaTrinhCongTacResponse::from).toList();
    }

    @Transactional
    public QuaTrinhCongTacResponse add(UUID nhanVienId, QuaTrinhCongTacRequest req) {
        requireNhanVien(nhanVienId);
        validate(req);
        QuaTrinhCongTac e = new QuaTrinhCongTac();
        e.setNhanVienId(nhanVienId);
        apply(e, req);
        return QuaTrinhCongTacResponse.from(repo.save(e));
    }

    @Transactional
    public QuaTrinhCongTacResponse update(UUID nhanVienId, UUID id, QuaTrinhCongTacRequest req) {
        QuaTrinhCongTac e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "WORK_HISTORY_NOT_FOUND", "Không tìm thấy quá trình công tác"));
        if (!e.getNhanVienId().equals(nhanVienId)) {
            throw new BusinessException("WORK_HISTORY_OWNER_MISMATCH",
                    "Bản ghi không thuộc nhân viên này");
        }
        validate(req);
        apply(e, req);
        return QuaTrinhCongTacResponse.from(repo.save(e));
    }

    @Transactional
    public void delete(UUID nhanVienId, UUID id) {
        QuaTrinhCongTac e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "WORK_HISTORY_NOT_FOUND", "Không tìm thấy quá trình công tác"));
        if (!e.getNhanVienId().equals(nhanVienId)) {
            throw new BusinessException("WORK_HISTORY_OWNER_MISMATCH",
                    "Bản ghi không thuộc nhân viên này");
        }
        repo.delete(e);
    }

    private void requireNhanVien(UUID id) {
        if (!nvRepo.existsById(id)) {
            throw new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên");
        }
    }

    private void validate(QuaTrinhCongTacRequest req) {
        if (req.getDenNgay() != null && req.getDenNgay().isBefore(req.getTuNgay())) {
            throw new BusinessException("WORK_HISTORY_DATE_INVALID",
                    "Đến ngày phải >= từ ngày");
        }
    }

    private void apply(QuaTrinhCongTac e, QuaTrinhCongTacRequest req) {
        e.setDonVi(req.getDonVi());
        e.setChucDanh(req.getChucDanh());
        e.setTuNgay(req.getTuNgay());
        e.setDenNgay(req.getDenNgay());
        e.setMoTa(req.getMoTa());
    }
}