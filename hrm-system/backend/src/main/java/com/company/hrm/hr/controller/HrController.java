package com.company.hrm.hr.controller;

import com.company.hrm.common.page.PageResponse;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.hr.dto.*;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr")
public class HrController {

    private final PhongBanService phongBanService;
    private final NgachBacLuongService ngachBacLuongService;
    private final NhanVienService nhanVienService;
    private final NguoiPhuThuocService nguoiPhuThuocService;
    private final QuaTrinhCongTacService quaTrinhCongTacService;
    private final HopDongService hopDongService;
    private final BienDongService bienDongService;

    public HrController(PhongBanService phongBanService,
                        NgachBacLuongService ngachBacLuongService,
                        NhanVienService nhanVienService,
                        NguoiPhuThuocService nguoiPhuThuocService,
                        QuaTrinhCongTacService quaTrinhCongTacService,
                        HopDongService hopDongService,
                        BienDongService bienDongService) {
        this.phongBanService = phongBanService;
        this.ngachBacLuongService = ngachBacLuongService;
        this.nhanVienService = nhanVienService;
        this.nguoiPhuThuocService = nguoiPhuThuocService;
        this.quaTrinhCongTacService = quaTrinhCongTacService;
        this.hopDongService = hopDongService;
        this.bienDongService = bienDongService;
    }

    // ---------------- Phòng ban ----------------

    @GetMapping("/departments")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER})
    public List<?> listDepartments(@RequestParam(name = "view", defaultValue = "flat") String view) {
        return "tree".equalsIgnoreCase(view)
                ? phongBanService.findTree()
                : phongBanService.findAllFlat();
    }

    @GetMapping("/departments/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER})
    public PhongBanResponse getDepartment(@PathVariable UUID id) {
        return phongBanService.get(id);
    }

    @PostMapping("/departments")
    @RequiresRole(Role.HR)
    public ResponseEntity<PhongBanResponse> createDepartment(@Valid @RequestBody PhongBanRequest req) {
        PhongBanResponse r = phongBanService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/hr/departments/" + r.getPhongBanId())).body(r);
    }

    @PutMapping("/departments/{id}")
    @RequiresRole(Role.HR)
    public PhongBanResponse updateDepartment(@PathVariable UUID id,
                                             @Valid @RequestBody PhongBanRequest req) {
        return phongBanService.update(id, req);
    }

    @PatchMapping("/departments/{id}/close")
    @RequiresRole(Role.HR)
    public PhongBanResponse closeDepartment(@PathVariable UUID id) {
        return phongBanService.close(id);
    }

    @PutMapping("/departments/{id}/manager")
    @RequiresRole(Role.HR)
    public PhongBanResponse assignManager(@PathVariable UUID id,
                                           @Valid @RequestBody AssignManagerRequest req) {
        if (req.getNhanVienId() != null) {
            // Validate nhân viên tồn tại + không đang quản lý phòng ban khác
            nhanVienService.requireExists(req.getNhanVienId());
            if (phongBanService.isManagerOfOther(req.getNhanVienId(), id)) {
                throw new com.company.hrm.common.error.BusinessException(
                        "EMPLOYEE_ALREADY_MANAGER",
                        "Nhân viên đang là trưởng bộ phận khác, không thể gán thêm");
            }
        }
        return phongBanService.assignManager(id, req.getNhanVienId());
    }

    // ---------------- Ngạch bậc lương ----------------

    @GetMapping("/salary-grades")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT, Role.MANAGER})
    public PageResponse<NgachBacLuongResponse> listSalaryGrades(Pageable pageable) {
        Page<NgachBacLuongResponse> page = ngachBacLuongService.findAll(pageable);
        return PageResponse.from(page, x -> x);
    }

    @PostMapping("/salary-grades")
    @RequiresRole(Role.HR)
    public ResponseEntity<NgachBacLuongResponse> createSalaryGrade(@Valid @RequestBody NgachBacLuongRequest req) {
        NgachBacLuongResponse r = ngachBacLuongService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/hr/salary-grades/" + r.getNgachBacId())).body(r);
    }

    @PutMapping("/salary-grades/{id}")
    @RequiresRole(Role.HR)
    public NgachBacLuongResponse updateSalaryGrade(@PathVariable UUID id,
                                                   @Valid @RequestBody NgachBacLuongRequest req) {
        return ngachBacLuongService.update(id, req);
    }

    @PatchMapping("/salary-grades/{id}/close")
    @RequiresRole(Role.HR)
    public NgachBacLuongResponse closeSalaryGrade(@PathVariable UUID id) {
        return ngachBacLuongService.close(id);
    }

    // ---------------- Nhân viên ----------------

    @PostMapping("/employees")
    @RequiresRole(Role.HR)
    public ResponseEntity<NhanVienResponse> createEmployee(@Valid @RequestBody NhanVienRequest req) {
        NhanVienResponse r = nhanVienService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/hr/employees/" + r.getNhanVienId())).body(r);
    }

    @GetMapping("/employees")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER})
    public PageResponse<NhanVienResponse> listEmployees(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID phongBanId,
            @RequestParam(required = false) TrangThaiNv trangThai,
            Pageable pageable) {
        return PageResponse.from(nhanVienService.search(q, phongBanId, trangThai, pageable), x -> x);
    }

    @GetMapping("/employees/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER, Role.EMPLOYEE})
    public NhanVienResponse getEmployee(@PathVariable UUID id) {
        return nhanVienService.get(id);
    }

    @PutMapping("/employees/{id}")
    @RequiresRole(Role.HR)
    public NhanVienResponse updateEmployee(@PathVariable UUID id,
                                           @Valid @RequestBody NhanVienRequest req) {
        return nhanVienService.updateInfo(id, req);
    }

    // ---------------- Người phụ thuộc ----------------

    @GetMapping("/employees/{id}/dependents")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public List<NguoiPhuThuocResponse> listDependents(@PathVariable UUID id) {
        return nguoiPhuThuocService.list(id);
    }

    @PostMapping("/employees/{id}/dependents")
    @RequiresRole(Role.HR)
    public ResponseEntity<NguoiPhuThuocResponse> addDependent(@PathVariable UUID id,
                                                               @Valid @RequestBody NguoiPhuThuocRequest req) {
        NguoiPhuThuocResponse r = nguoiPhuThuocService.add(id, req);
        return ResponseEntity.status(201).body(r);
    }

    @PutMapping("/employees/{id}/dependents/{depId}")
    @RequiresRole(Role.HR)
    public NguoiPhuThuocResponse updateDependent(@PathVariable UUID id,
                                                   @PathVariable UUID depId,
                                                   @Valid @RequestBody NguoiPhuThuocRequest req) {
        return nguoiPhuThuocService.update(id, depId, req);
    }

    @DeleteMapping("/employees/{id}/dependents/{depId}")
    @RequiresRole(Role.HR)
    public ResponseEntity<Void> deleteDependent(@PathVariable UUID id, @PathVariable UUID depId) {
        nguoiPhuThuocService.delete(id, depId);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Quá trình công tác ----------------

    @GetMapping("/employees/{id}/work-history")
    @RequiresRole({Role.HR, Role.MANAGER, Role.EMPLOYEE})
    public List<QuaTrinhCongTacResponse> listWorkHistory(@PathVariable UUID id) {
        return quaTrinhCongTacService.list(id);
    }

    @PostMapping("/employees/{id}/work-history")
    @RequiresRole(Role.HR)
    public ResponseEntity<QuaTrinhCongTacResponse> addWorkHistory(@PathVariable UUID id,
                                                                  @Valid @RequestBody QuaTrinhCongTacRequest req) {
        QuaTrinhCongTacResponse r = quaTrinhCongTacService.add(id, req);
        return ResponseEntity.status(201).body(r);
    }

    @PutMapping("/employees/{id}/work-history/{wid}")
    @RequiresRole(Role.HR)
    public QuaTrinhCongTacResponse updateWorkHistory(@PathVariable UUID id,
                                                     @PathVariable UUID wid,
                                                     @Valid @RequestBody QuaTrinhCongTacRequest req) {
        return quaTrinhCongTacService.update(id, wid, req);
    }

    @DeleteMapping("/employees/{id}/work-history/{wid}")
    @RequiresRole(Role.HR)
    public ResponseEntity<Void> deleteWorkHistory(@PathVariable UUID id, @PathVariable UUID wid) {
        quaTrinhCongTacService.delete(id, wid);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Hợp đồng lao động ----------------

    @PostMapping("/employees/{id}/contracts")
    @RequiresRole(Role.HR)
    public ResponseEntity<HopDongResponse> createContract(@PathVariable UUID id,
                                                          @Valid @RequestBody HopDongRequest req) {
        HopDongResponse r = hopDongService.create(id, req);
        return ResponseEntity.created(URI.create("/api/v1/hr/contracts/" + r.getHopDongId())).body(r);
    }

    @GetMapping("/employees/{id}/contracts")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public List<HopDongResponse> listContracts(@PathVariable UUID id) {
        return hopDongService.listByNhanVien(id);
    }

    @GetMapping("/contracts/{id}")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public HopDongResponse getContract(@PathVariable UUID id) {
        return hopDongService.get(id);
    }

    @PutMapping("/contracts/{id}")
    @RequiresRole(Role.HR)
    public HopDongResponse updateContract(@PathVariable UUID id,
                                          @Valid @RequestBody HopDongRequest req) {
        return hopDongService.update(id, req);
    }

    @PostMapping("/contracts/{id}/addendum")
    @RequiresRole(Role.HR)
    public ResponseEntity<HopDongResponse> addAddendum(@PathVariable UUID id,
                                                       @Valid @RequestBody AddendumRequest req) {
        HopDongResponse r = hopDongService.addendum(id, req);
        return ResponseEntity.status(201).body(r);
    }

    @GetMapping("/contracts/expiring")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<HopDongExpiringItem> expiringContracts(
            @RequestParam(name = "fromDays", defaultValue = "30") int fromDays,
            @RequestParam(name = "toDays", defaultValue = "45") int toDays) {
        return hopDongService.expiring(fromDays, toDays);
    }

    // ---------------- Biến động nhân sự ----------------

    @PostMapping("/movements")
    @RequiresRole(Role.HR)
    public ResponseEntity<BienDongResponse> createMovement(@Valid @RequestBody BienDongRequest req) {
        BienDongResponse r = bienDongService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/hr/movements/" + r.getBienDongId())).body(r);
    }

    @GetMapping("/employees/{id}/movements")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER})
    public List<BienDongResponse> listMovements(@PathVariable UUID id) {
        return bienDongService.listByNhanVien(id);
    }

    @GetMapping("/employees/{id}/status")
    @RequiresRole({Role.HR, Role.MANAGER, Role.PAYROLL_ACCOUNTANT, Role.BHXH_OFFICER})
    public BienDongTimelineItem employeeStatusAt(
            @PathVariable UUID id,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bienDongService.trangThaiTaiNgay(id, date == null ? LocalDate.now() : date);
    }
}