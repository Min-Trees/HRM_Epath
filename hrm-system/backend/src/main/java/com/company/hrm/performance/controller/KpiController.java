package com.company.hrm.performance.controller;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.performance.dto.*;
import com.company.hrm.performance.service.KpiAssignmentService;
import com.company.hrm.performance.service.KpiCycleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * T18 - REST controller cho Module KPI/OKR.
 *
 * <p>Workflow: HR tao cycle -> manager gan KPI -> NV tu danh gia ->
 * manager review -> HR phe duyet.
 */
@RestController
@RequestMapping("/api/v1/performance")
public class KpiController {

    private final KpiCycleService cycleService;
    private final KpiAssignmentService assignService;

    public KpiController(KpiCycleService cycleService, KpiAssignmentService assignService) {
        this.cycleService = cycleService;
        this.assignService = assignService;
    }

    // ===== Cycle =====
    @GetMapping("/cycles")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<KpiCycleDto> listCycles() {
        return cycleService.findAll();
    }

    @PostMapping("/cycles")
    @RequiresRole({Role.HR})
    public KpiCycleDto createCycle(@Valid @RequestBody KpiCycleDto dto) {
        dto.setNguoiTaoId(AuthContext.currentUserIdOrNull());
        return cycleService.create(dto);
    }

    @PostMapping("/cycles/{id}/start")
    @RequiresRole({Role.HR})
    public KpiCycleDto startCycle(@PathVariable UUID id) {
        return cycleService.startCycle(id);
    }

    @PostMapping("/cycles/{id}/close")
    @RequiresRole({Role.HR})
    public KpiCycleDto closeCycle(@PathVariable UUID id) {
        return cycleService.closeCycle(id);
    }

    // ===== Assignment =====
    @GetMapping("/assignments")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<KpiAssignmentDto> listAssignments(@RequestParam UUID cycleId) {
        return assignService.findByCycle(cycleId);
    }

    @GetMapping("/nhan-vien/{nvId}/assignments")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public List<KpiAssignmentDto> listAssignmentsOfNv(@PathVariable UUID nvId,
                                                        @RequestParam UUID cycleId) {
        return assignService.findByNvAndCycle(nvId, cycleId);
    }

    @PostMapping("/assignments")
    @RequiresRole({Role.MANAGER, Role.HR})
    public KpiAssignmentDto createAssignment(@Valid @RequestBody KpiAssignmentDto dto) {
        if (dto.getNguoiGanId() == null) {
            dto.setNguoiGanId(AuthContext.currentUserIdOrNull());
        }
        return assignService.create(dto);
    }

    @PostMapping("/assignments/{id}/self-assess")
    @RequiresRole({Role.EMPLOYEE, Role.MANAGER, Role.HR})
    public KpiAssignmentDto selfAssess(@PathVariable UUID id,
                                        @Valid @RequestBody KpiSelfAssessmentDto dto) {
        dto.setAssignmentId(id);
        return assignService.selfAssess(id, dto);
    }

    @PostMapping("/assignments/{id}/manager-review")
    @RequiresRole({Role.MANAGER, Role.HR})
    public KpiAssignmentDto managerReview(@PathVariable UUID id,
                                           @Valid @RequestBody KpiReviewDto dto) {
        dto.setAssignmentId(id);
        if (dto.getNguoiReviewId() == null) {
            dto.setNguoiReviewId(AuthContext.currentUserIdOrNull());
        }
        return assignService.managerReview(id, dto);
    }

    @PostMapping("/assignments/{id}/hr-approve")
    @RequiresRole({Role.HR})
    public KpiAssignmentDto hrApprove(@PathVariable UUID id,
                                       @Valid @RequestBody KpiFinalRatingDto dto) {
        dto.setAssignmentId(id);
        if (dto.getNguoiPheDuyetId() == null) {
            dto.setNguoiPheDuyetId(AuthContext.currentUserIdOrNull());
        }
        return assignService.hrApprove(id, dto);
    }
}
