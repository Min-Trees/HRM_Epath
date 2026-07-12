package com.company.hrm.hr.offboarding.controller;

import com.company.hrm.common.page.PageResponse;
import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.hr.offboarding.dto.OffboardingCaseDto;
import com.company.hrm.hr.offboarding.dto.OffboardingTaskDto;
import com.company.hrm.hr.offboarding.dto.SeveranceCalcDto;
import com.company.hrm.hr.offboarding.entity.LyDoNghiViec;
import com.company.hrm.hr.offboarding.entity.TrangThaiOffboarding;
import com.company.hrm.hr.offboarding.entity.TrangThaiTask;
import com.company.hrm.hr.offboarding.service.OffboardingCaseService;
import com.company.hrm.hr.offboarding.service.SeveranceCalculatorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * T14 - REST controller cho module Offboarding.
 *
 * <p>Base path: {@code /api/v1/hr/offboarding}
 */
@RestController
@RequestMapping("/api/v1/hr/offboarding")
public class OffboardingController {

    private final OffboardingCaseService caseService;
    private final SeveranceCalculatorService severanceService;

    public OffboardingController(OffboardingCaseService caseService,
                                 SeveranceCalculatorService severanceService) {
        this.caseService = caseService;
        this.severanceService = severanceService;
    }

    @GetMapping("/cases")
    @RequiresRole({Role.HR, Role.MANAGER})
    public PageResponse<OffboardingCaseDto> list(Pageable pageable) {
        return caseService.list(pageable);
    }

    @GetMapping("/cases/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT})
    public OffboardingCaseDto get(@PathVariable UUID id) {
        return caseService.get(id);
    }

    @PostMapping("/cases")
    @RequiresRole(Role.HR)
    public ResponseEntity<OffboardingCaseDto> create(@Valid @RequestBody OffboardingCaseDto dto) {
        UUID nguoiTaoId = AuthContext.currentUserIdOrNull();
        OffboardingCaseDto r = caseService.create(dto, nguoiTaoId);
        return ResponseEntity.created(URI.create("/api/v1/hr/offboarding/cases/" + r.getCaseId())).body(r);
    }

    @PatchMapping("/cases/{id}/status")
    @RequiresRole({Role.HR, Role.MANAGER})
    public OffboardingCaseDto updateStatus(@PathVariable UUID id, @RequestParam TrangThaiOffboarding status) {
        UUID nguoiDuyetId = AuthContext.currentUserIdOrNull();
        return caseService.updateStatus(id, status, nguoiDuyetId);
    }

    @GetMapping("/cases/{id}/tasks")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT})
    public List<OffboardingTaskDto> listTasks(@PathVariable UUID id) {
        return caseService.listTasks(id);
    }

    @PatchMapping("/tasks/{taskId}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT})
    public OffboardingTaskDto updateTask(@PathVariable UUID taskId,
                                         @RequestParam TrangThaiTask trangThai,
                                         @RequestParam(required = false) String fileDinhKemUrl,
                                         @RequestParam(required = false) String ghiChu) {
        UUID nguoiHoanThanhId = AuthContext.currentUserIdOrNull();
        return caseService.updateTask(taskId, trangThai, nguoiHoanThanhId, fileDinhKemUrl, ghiChu);
    }

    @PostMapping("/severance/preview")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public SeveranceCalcDto previewSeverance(@RequestParam UUID nhanVienId,
                                             @RequestParam LocalDate ngayNghiViec,
                                             @RequestParam BigDecimal luongBinhQuan6Thang,
                                             @RequestParam LyDoNghiViec lyDo) {
        return severanceService.preview(nhanVienId, ngayNghiViec, luongBinhQuan6Thang, lyDo);
    }

    @PostMapping("/cases/{caseId}/severance")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public SeveranceCalcDto calculateSeverance(@PathVariable UUID caseId,
                                               @RequestParam BigDecimal luongBinhQuan6Thang) {
        UUID nguoiTinhId = AuthContext.currentUserIdOrNull();
        return severanceService.calculateAndPersist(caseId, luongBinhQuan6Thang, nguoiTinhId);
    }

    @GetMapping("/cases/{caseId}/severance")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public SeveranceCalcDto getSeverance(@PathVariable UUID caseId) {
        return severanceService.getByCase(caseId);
    }
}
