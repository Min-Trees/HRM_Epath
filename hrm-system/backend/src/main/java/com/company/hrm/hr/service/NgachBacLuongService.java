package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.NgachBacLuongRequest;
import com.company.hrm.hr.dto.NgachBacLuongResponse;
import com.company.hrm.hr.entity.NgachBacLuong;
import com.company.hrm.hr.repository.NgachBacLuongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NgachBacLuongService {

    private final NgachBacLuongRepository repo;

    public NgachBacLuongService(NgachBacLuongRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<NgachBacLuongResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(NgachBacLuongResponse::from);
    }

    @Transactional(readOnly = true)
    public NgachBacLuongResponse get(UUID id) {
        return repo.findById(id)
                .map(NgachBacLuongResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NGACH_BAC_NOT_FOUND", "Không tìm thấy ngạch/bậc lương"));
    }

    @Transactional
    public NgachBacLuongResponse create(NgachBacLuongRequest req) {
        if (repo.existsByMaNgach(req.getMaNgach())) {
            throw new BusinessException("MA_NGACH_DUPLICATE",
                    "Mã ngạch '" + req.getMaNgach() + "' đã tồn tại");
        }
        NgachBacLuong e = new NgachBacLuong();
        apply(e, req);
        e.setActive(true);
        return NgachBacLuongResponse.from(repo.save(e));
    }

    @Transactional
    public NgachBacLuongResponse update(UUID id, NgachBacLuongRequest req) {
        NgachBacLuong e = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("NGACH_BAC_NOT_FOUND", "Không tìm thấy ngạch/bậc lương"));
        if (!e.getMaNgach().equals(req.getMaNgach()) && repo.existsByMaNgach(req.getMaNgach())) {
            throw new BusinessException("MA_NGACH_DUPLICATE",
                    "Mã ngạch '" + req.getMaNgach() + "' đã tồn tại");
        }
        apply(e, req);
        return NgachBacLuongResponse.from(repo.save(e));
    }

    @Transactional
    public NgachBacLuongResponse close(UUID id) {
        NgachBacLuong e = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("NGACH_BAC_NOT_FOUND", "Không tìm thấy ngạch/bậc lương"));
        e.setActive(false);
        return NgachBacLuongResponse.from(repo.save(e));
    }

    private void apply(NgachBacLuong e, NgachBacLuongRequest req) {
        e.setMaNgach(req.getMaNgach());
        e.setTenChucDanh(req.getTenChucDanh());
        e.setBacLuong(req.getBacLuong());
        e.setHeSoLuong(req.getHeSoLuong());
        e.setLuongCoBanToiThieu(req.getLuongCoBanToiThieu());
    }
}