package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.CaLamViecRequest;
import com.company.hrm.attendance.dto.CaLamViecResponse;
import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.repository.CaLamViecRepository;
import com.company.hrm.attendance.repository.PhanCaRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CaLamViecService {

    private final CaLamViecRepository repo;
    private final PhanCaRepository phanCaRepo;

    public CaLamViecService(CaLamViecRepository repo, PhanCaRepository phanCaRepo) {
        this.repo = repo;
        this.phanCaRepo = phanCaRepo;
    }

    @Transactional
    public CaLamViecResponse create(CaLamViecRequest req) {
        if (repo.existsByMaCa(req.getMaCa())) {
            throw new BusinessException("CA_LAM_VIEC_MA_DUPLICATE",
                    "Mã ca '" + req.getMaCa() + "' đã tồn tại");
        }
        validateTime(req);
        CaLamViec e = new CaLamViec();
        apply(e, req);
        e.setActive(true);
        return CaLamViecResponse.from(repo.save(e));
    }

    @Transactional
    public CaLamViecResponse update(UUID id, CaLamViecRequest req) {
        CaLamViec e = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("CA_LAM_VIEC_NOT_FOUND", "Không tìm thấy ca làm việc"));
        if (!e.getMaCa().equals(req.getMaCa()) && repo.existsByMaCa(req.getMaCa())) {
            throw new BusinessException("CA_LAM_VIEC_MA_DUPLICATE",
                    "Mã ca '" + req.getMaCa() + "' đã tồn tại");
        }
        validateTime(req);
        apply(e, req);
        return CaLamViecResponse.from(repo.save(e));
    }

    @Transactional(readOnly = true)
    public CaLamViecResponse get(UUID id) {
        return repo.findById(id)
                .map(CaLamViecResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("CA_LAM_VIEC_NOT_FOUND",
                        "Không tìm thấy ca làm việc"));
    }

    @Transactional(readOnly = true)
    public List<CaLamViecResponse> findAll(Boolean activeOnly) {
        List<CaLamViec> list = (activeOnly != null && activeOnly)
                ? repo.findAllByActiveOrderByMaCaAsc(true)
                : repo.findAllByOrderByMaCaAsc();
        return list.stream().map(CaLamViecResponse::from).toList();
    }

    /** Đóng ca (active=false). Không cho đóng nếu ca đã được dùng để phân. */
    @Transactional
    public CaLamViecResponse close(UUID id) {
        CaLamViec e = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("CA_LAM_VIEC_NOT_FOUND", "Không tìm thấy ca làm việc"));
        if (!e.isActive()) {
            return CaLamViecResponse.from(e); // đã đóng rồi → idempotent
        }
        if (phanCaRepo.existsByCaId(id)) {
            throw new BusinessException("CA_LAM_VIEC_IN_USE",
                    "Ca đã được dùng để phân cho nhân viên, không thể đóng");
        }
        e.setActive(false);
        return CaLamViecResponse.from(repo.save(e));
    }

    private void validateTime(CaLamViecRequest req) {
        boolean quaNgay = Boolean.TRUE.equals(req.getQuaNgay());
        if (!quaNgay && !req.getGioKetThuc().isAfter(req.getGioBatDau())) {
            throw new BusinessException("CA_LAM_VIEC_TIME_INVALID",
                    "Giờ kết thúc phải sau giờ bắt đầu (hoặc bật qua_ngay nếu là ca đêm)");
        }
        if (quaNgay && req.getGioKetThuc().equals(req.getGioBatDau())) {
            throw new BusinessException("CA_LAM_VIEC_TIME_INVALID",
                    "Ca qua đêm phải có giờ kết thúc khác giờ bắt đầu");
        }
    }

    private void apply(CaLamViec e, CaLamViecRequest req) {
        e.setMaCa(req.getMaCa());
        e.setTenCa(req.getTenCa());
        e.setLoaiCa(req.getLoaiCa());
        e.setGioBatDau(req.getGioBatDau());
        e.setGioKetThuc(req.getGioKetThuc());
        e.setSoGioChuan(req.getSoGioChuan());
        e.setQuaNgay(Boolean.TRUE.equals(req.getQuaNgay()));
    }
}
