package com.company.hrm.training.controller;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.training.dto.*;
import com.company.hrm.training.entity.TrangThaiDangKy;
import com.company.hrm.training.entity.TrangThaiLop;
import com.company.hrm.training.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * T20 - REST controller cho Module Dao tao.
 */
@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

    private final TrainingService service;

    public TrainingController(TrainingService service) {
        this.service = service;
    }

    // Chuong trinh
    @GetMapping("/chuong-trinh")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public List<ChuongTrinhDaoTaoDto> listChuongTrinh() {
        return service.findAllChuongTrinh();
    }

    @PostMapping("/chuong-trinh")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public ChuongTrinhDaoTaoDto createChuongTrinh(@Valid @RequestBody ChuongTrinhDaoTaoDto dto) {
        return service.createChuongTrinh(dto, AuthContext.currentUserIdOrNull());
    }

    @PostMapping("/chuong-trinh/{id}/cong-bo")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public ChuongTrinhDaoTaoDto congBo(@PathVariable UUID id) {
        return service.congBoChuongTrinh(id);
    }

    // Lop hoc
    @GetMapping("/lop")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public List<LopHocDto> listLop() {
        return service.findAllLop();
    }

    @PostMapping("/lop")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public LopHocDto createLop(@Valid @RequestBody LopHocDto dto) {
        return service.createLop(dto);
    }

    @PostMapping("/lop/{id}/transition")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public LopHocDto transitionLop(@PathVariable UUID id, @RequestParam TrangThaiLop newState) {
        return service.changeLopState(id, newState);
    }

    // Dang ky
    @PostMapping("/dang-ky")
    @RequiresRole({Role.HR, Role.HR_MANAGER, Role.EMPLOYEE})
    public DangKyDaoTaoDto dangKy(@Valid @RequestBody DangKyDaoTaoDto dto) {
        return service.dangKy(dto);
    }

    @PostMapping("/dang-ky/{id}/duyet")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public DangKyDaoTaoDto duyet(@PathVariable UUID id,
                                  @RequestParam TrangThaiDangKy quyetDinh,
                                  @RequestParam(required = false) String ghiChu) {
        return service.duyetDangKy(id, AuthContext.currentUserIdOrNull(), quyetDinh, ghiChu);
    }

    @GetMapping("/dang-ky/by-lop/{lopId}")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public List<DangKyDaoTaoDto> findByLop(@PathVariable UUID lopId) {
        return service.findDangKyByLop(lopId);
    }

    @GetMapping("/dang-ky/by-nv/{nvId}")
    @RequiresRole({Role.HR, Role.HR_MANAGER, Role.EMPLOYEE})
    public List<DangKyDaoTaoDto> findByNv(@PathVariable UUID nvId) {
        return service.findDangKyByNhanVien(nvId);
    }

    // Diem danh
    @PostMapping("/diem-danh")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public DiemDanhDto diemDanh(@Valid @RequestBody DiemDanhDto dto) {
        return service.diemDanh(dto, AuthContext.currentUserIdOrNull());
    }

    // Danh gia
    @PostMapping("/danh-gia")
    @RequiresRole({Role.HR, Role.HR_MANAGER})
    public DanhGiaSauDto danhGia(@Valid @RequestBody DanhGiaSauDto dto) {
        return service.danhGia(dto, AuthContext.currentUserIdOrNull());
    }
}
