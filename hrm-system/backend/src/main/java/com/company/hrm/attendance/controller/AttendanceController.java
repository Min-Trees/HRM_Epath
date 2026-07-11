package com.company.hrm.attendance.controller;

import com.company.hrm.attendance.dto.ApprovalRequest;
import com.company.hrm.attendance.dto.BatchTimeLogRequest;
import com.company.hrm.attendance.dto.BatchTimeLogResult;
import com.company.hrm.attendance.dto.CaLamViecRequest;
import com.company.hrm.attendance.dto.CaLamViecResponse;
import com.company.hrm.attendance.dto.DangKyOtMonthlySummary;
import com.company.hrm.attendance.dto.DangKyOtRequest;
import com.company.hrm.attendance.dto.DangKyOtResponse;
import com.company.hrm.attendance.dto.ExplanationRequest;
import com.company.hrm.attendance.dto.LeaveBalanceInitRequest;
import com.company.hrm.attendance.dto.LeaveBalanceInitResult;
import com.company.hrm.attendance.dto.MonthlySummary;
import com.company.hrm.attendance.dto.NghiPhepRequest;
import com.company.hrm.attendance.dto.NghiPhepResponse;
import com.company.hrm.attendance.dto.PhanCaRequest;
import com.company.hrm.attendance.dto.PhanCaResponse;
import com.company.hrm.attendance.dto.QuyPhepNamResponse;
import com.company.hrm.attendance.dto.TimeLogRequest;
import com.company.hrm.attendance.dto.TimeLogResponse;
import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.entity.TrangThaiDon;
import com.company.hrm.attendance.service.CaLamViecService;
import com.company.hrm.attendance.service.ChamCongService;
import com.company.hrm.attendance.service.DangKyOtService;
import com.company.hrm.attendance.service.NghiPhepService;
import com.company.hrm.attendance.service.PhanCaService;
import com.company.hrm.attendance.service.QuyPhepService;
import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final CaLamViecService caLamViecService;
    private final PhanCaService phanCaService;
    private final ChamCongService chamCongService;
    private final NghiPhepService nghiPhepService;
    private final DangKyOtService dangKyOtService;
    private final QuyPhepService quyPhepService;

    public AttendanceController(CaLamViecService caLamViecService,
                                PhanCaService phanCaService,
                                ChamCongService chamCongService,
                                NghiPhepService nghiPhepService,
                                DangKyOtService dangKyOtService,
                                QuyPhepService quyPhepService) {
        this.caLamViecService = caLamViecService;
        this.phanCaService = phanCaService;
        this.chamCongService = chamCongService;
        this.nghiPhepService = nghiPhepService;
        this.dangKyOtService = dangKyOtService;
        this.quyPhepService = quyPhepService;
    }

    // ---------------- Ca làm việc ----------------

    @GetMapping("/shifts")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER})
    public List<CaLamViecResponse> listShifts(
            @RequestParam(name = "active", required = false) Boolean active) {
        return caLamViecService.findAll(active);
    }

    @GetMapping("/shifts/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER})
    public CaLamViecResponse getShift(@PathVariable UUID id) {
        return caLamViecService.get(id);
    }

    @PostMapping("/shifts")
    @RequiresRole({Role.HR, Role.MANAGER})
    public ResponseEntity<CaLamViecResponse> createShift(@Valid @RequestBody CaLamViecRequest req) {
        CaLamViecResponse r = caLamViecService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/attendance/shifts/" + r.getCaId())).body(r);
    }

    @PutMapping("/shifts/{id}")
    @RequiresRole({Role.HR, Role.MANAGER})
    public CaLamViecResponse updateShift(@PathVariable UUID id,
                                          @Valid @RequestBody CaLamViecRequest req) {
        return caLamViecService.update(id, req);
    }

    @PatchMapping("/shifts/{id}/close")
    @RequiresRole(Role.HR)
    public CaLamViecResponse closeShift(@PathVariable UUID id) {
        return caLamViecService.close(id);
    }

    // ---------------- Phân ca ----------------

    @PostMapping("/shift-assignments")
    @RequiresRole({Role.HR, Role.MANAGER})
    public ResponseEntity<Object> assignShift(@Valid @RequestBody PhanCaRequest req) {
        Object result = phanCaService.assign(req);
        if (result instanceof PhanCaResponse r) {
            return ResponseEntity.created(URI.create("/api/v1/attendance/shift-assignments/" + r.getPhanCaId())).body(r);
        }
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/shift-assignments/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public PhanCaResponse getShiftAssignment(@PathVariable UUID id) {
        return phanCaService.get(id);
    }

    @GetMapping("/shift-assignments")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public List<PhanCaResponse> listShiftAssignments(
            @RequestParam(name = "employeeId") UUID employeeId,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return phanCaService.list(employeeId, from, to);
    }

    @GetMapping("/employees/{employeeId}/standard-shift")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public ResponseEntity<CaLamViec> standardShift(
            @PathVariable UUID employeeId,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        CaLamViec ca = phanCaService.getStandardShift(employeeId,
                date == null ? LocalDate.now() : date);
        return ca == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(ca);
    }

    @DeleteMapping("/shift-assignments/{id}")
    @RequiresRole({Role.HR, Role.MANAGER})
    public ResponseEntity<Void> deleteShiftAssignment(@PathVariable UUID id) {
        phanCaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Chấm công (T09) ----------------

    /**
     * Ghi 1 bản ghi chấm công (NV tự chấm hoặc HR/MANAGER nhập hộ).
     * Service tự đối chiếu ca chuẩn và gán ngoại lệ.
     */
    @PostMapping("/time-logs")
    @RequiresRole({Role.HR, Role.MANAGER, Role.EMPLOYEE})
    public ResponseEntity<TimeLogResponse> recordTimeLog(@Valid @RequestBody TimeLogRequest req) {
        TimeLogResponse r = chamCongService.record(req);
        return ResponseEntity.created(URI.create("/api/v1/attendance/time-logs/" + r.getChamCongId())).body(r);
    }

    /**
     * Đồng bộ lô từ máy chấm công / app mobile. Chỉ HR/MANAGER.
     * Bỏ qua trùng ngày; trả về kết quả {total, created, skipped}.
     */
    @PostMapping("/time-logs/batch")
    @RequiresRole({Role.HR, Role.MANAGER})
    public BatchTimeLogResult recordBatchTimeLogs(@Valid @RequestBody BatchTimeLogRequest req) {
        return chamCongService.recordBatch(req.getRecords());
    }

    @GetMapping("/time-logs/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public TimeLogResponse getTimeLog(@PathVariable UUID id) {
        return chamCongService.get(id);
    }

    @GetMapping("/time-logs")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public List<TimeLogResponse> listTimeLogs(
            @RequestParam(name = "employeeId") UUID employeeId,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return chamCongService.list(employeeId, from, to);
    }

    /**
     * Danh sách ngoại lệ chấm công cần xử lý. Lọc theo trạng thái duyệt (nếu có).
     */
    @GetMapping("/exceptions")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<TimeLogResponse> listExceptions(
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "status", required = false) TrangThaiDon status) {
        return chamCongService.listExceptions(from, to, status);
    }

    /**
     * HR gửi giải trình hộ NV (theo quyết định T09: stub auth chưa biết current user là NV nào).
     */
    @PostMapping("/time-logs/{id}/explanation")
    @RequiresRole({Role.HR, Role.MANAGER})
    public TimeLogResponse submitExplanation(@PathVariable UUID id,
                                              @Valid @RequestBody ExplanationRequest req) {
        return chamCongService.submitExplanation(id, req.getNoiDung());
    }

    /** MANAGER/HR duyệt hoặc từ chối giải trình / bản ghi thủ công. */
    @PostMapping("/time-logs/{id}/approve")
    @RequiresRole({Role.HR, Role.MANAGER})
    public TimeLogResponse approve(@PathVariable UUID id,
                                   @Valid @RequestBody ApprovalRequest req) {
        boolean approve = Boolean.TRUE.equals(req.getApprove());
        return chamCongService.approve(id, approve, AuthContext.currentUserIdOrNull(), req.getGhiChu());
    }

    /**
     * Tổng hợp công theo NV và tháng — T11 sẽ gọi.
     */
    @GetMapping("/employees/{employeeId}/summary")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public MonthlySummary monthlySummary(
            @PathVariable UUID employeeId,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        return chamCongService.summary(employeeId, month, year);
    }

    // ---------------- Nghỉ phép (T10) ----------------

    @PostMapping("/leave-requests")
    @RequiresRole({Role.HR, Role.MANAGER, Role.EMPLOYEE})
    public ResponseEntity<NghiPhepResponse> createLeave(@Valid @RequestBody NghiPhepRequest req) {
        NghiPhepResponse r = nghiPhepService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/attendance/leave-requests/" + r.getNghiPhepId())).body(r);
    }

    @GetMapping("/leave-requests/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public NghiPhepResponse getLeave(@PathVariable UUID id) {
        return nghiPhepService.get(id);
    }

    @GetMapping("/leave-requests")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public List<NghiPhepResponse> listLeaves(
            @RequestParam(name = "employeeId") UUID employeeId,
            @RequestParam(name = "status", required = false) List<TrangThaiDon> status) {
        return nghiPhepService.list(employeeId, status);
    }

    /** MANAGER duyệt cấp 1. */
    @PostMapping("/leave-requests/{id}/approve-cap1")
    @RequiresRole(Role.MANAGER)
    public NghiPhepResponse approveLeaveCap1(@PathVariable UUID id,
                                              @Valid @RequestBody ApprovalRequest req) {
        return nghiPhepService.approveCap1(id, AuthContext.currentUserIdOrNull(),
                Boolean.TRUE.equals(req.getApprove()), req.getGhiChu());
    }

    /** HR duyệt cấp 2 (sau khi đã duyệt cấp 1). */
    @PostMapping("/leave-requests/{id}/approve-cap2")
    @RequiresRole(Role.HR)
    public NghiPhepResponse approveLeaveCap2(@PathVariable UUID id,
                                              @Valid @RequestBody ApprovalRequest req) {
        return nghiPhepService.approveCap2(id, AuthContext.currentUserIdOrNull(),
                Boolean.TRUE.equals(req.getApprove()), req.getGhiChu());
    }

    /** NV tạo hoặc HR hủy đơn. */
    @PostMapping("/leave-requests/{id}/cancel")
    @RequiresRole({Role.HR, Role.EMPLOYEE})
    public NghiPhepResponse cancelLeave(@PathVariable UUID id) {
        return nghiPhepService.cancel(id);
    }

    // ---------------- Tăng ca (T10) ----------------

    @PostMapping("/overtime-requests")
    @RequiresRole({Role.HR, Role.MANAGER, Role.EMPLOYEE})
    public ResponseEntity<DangKyOtResponse> createOvertime(@Valid @RequestBody DangKyOtRequest req) {
        DangKyOtResponse r = dangKyOtService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/attendance/overtime-requests/" + r.getOtId())).body(r);
    }

    @GetMapping("/overtime-requests/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public DangKyOtResponse getOvertime(@PathVariable UUID id) {
        return dangKyOtService.get(id);
    }

    @GetMapping("/overtime-requests")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public List<DangKyOtResponse> listOvertime(
            @RequestParam(name = "employeeId") UUID employeeId,
            @RequestParam(name = "status", required = false) List<TrangThaiDon> status) {
        return dangKyOtService.list(employeeId, status);
    }

    @PostMapping("/overtime-requests/{id}/approve-cap1")
    @RequiresRole(Role.MANAGER)
    public DangKyOtResponse approveOvertimeCap1(@PathVariable UUID id,
                                                 @Valid @RequestBody ApprovalRequest req) {
        return dangKyOtService.approveCap1(id, AuthContext.currentUserIdOrNull(),
                Boolean.TRUE.equals(req.getApprove()), req.getGhiChu());
    }

    @PostMapping("/overtime-requests/{id}/approve-cap2")
    @RequiresRole(Role.HR)
    public DangKyOtResponse approveOvertimeCap2(@PathVariable UUID id,
                                                 @Valid @RequestBody ApprovalRequest req) {
        return dangKyOtService.approveCap2(id, AuthContext.currentUserIdOrNull(),
                Boolean.TRUE.equals(req.getApprove()), req.getGhiChu());
    }

    @PostMapping("/overtime-requests/{id}/cancel")
    @RequiresRole({Role.HR, Role.EMPLOYEE})
    public DangKyOtResponse cancelOvertime(@PathVariable UUID id) {
        return dangKyOtService.cancel(id);
    }

    /** Tổng hợp OT đã duyệt theo tháng — T11. */
    @GetMapping("/overtime/employees/{employeeId}/monthly")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT})
    public DangKyOtMonthlySummary monthlyOvertime(
            @PathVariable UUID employeeId,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        return dangKyOtService.monthlyApprovedOT(employeeId, month, year);
    }

    // ---------------- Quỹ phép năm (T10) ----------------

    @GetMapping("/leave-balance")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public QuyPhepNamResponse getLeaveBalance(
            @RequestParam(name = "employeeId") UUID employeeId,
            @RequestParam(name = "year") int year) {
        return quyPhepService.getBalance(employeeId, year);
    }

    /** Khởi tạo quỹ phép cho nhiều NV — HR dùng đầu năm. */
    @PostMapping("/leave-balance/init")
    @RequiresRole(Role.HR)
    public LeaveBalanceInitResult initLeaveBalance(@Valid @RequestBody LeaveBalanceInitRequest req) {
        return quyPhepService.initBatch(req.getNhanVienIds(), req.getNam());
    }
}