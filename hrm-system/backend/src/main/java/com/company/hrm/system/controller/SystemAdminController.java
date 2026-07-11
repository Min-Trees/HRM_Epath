package com.company.hrm.system.controller;

import com.company.hrm.common.security.RequiresPermission;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.system.dto.CompanyRequest;
import com.company.hrm.system.dto.CompanyResponse;
import com.company.hrm.system.entity.TrangThaiCompany;
import com.company.hrm.system.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Quản trị doanh nghiệp + user + audit log. Route gốc {@code /api/v1/system}.
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemAdminController {

    private final CompanyService companyService;

    public SystemAdminController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // ---------------- Company ----------------

    @PostMapping("/companies")
    @RequiresPermission("company.create")
    @RequiresRole(Role.SYSTEM_ADMIN)
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest req) {
        CompanyResponse r = companyService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/system/companies/" + r.getCompanyId())).body(r);
    }

    @GetMapping("/companies/{id}")
    @RequiresPermission("company.read")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public CompanyResponse getCompany(@PathVariable UUID id) {
        return companyService.getById(id);
    }

    @PutMapping("/companies/{id}")
    @RequiresPermission("company.update")
    @RequiresRole(Role.SYSTEM_ADMIN)
    public CompanyResponse updateCompany(@PathVariable UUID id, @Valid @RequestBody CompanyRequest req) {
        return companyService.update(id, req);
    }

    @PostMapping("/companies/{id}/status")
    @RequiresPermission("company.update")
    @RequiresRole(Role.SYSTEM_ADMIN)
    public CompanyResponse updateCompanyStatus(@PathVariable UUID id,
                                               @RequestParam TrangThaiCompany status) {
        return companyService.updateStatus(id, status);
    }

    @GetMapping("/companies")
    @RequiresPermission("company.read")
    @RequiresRole(Role.SYSTEM_ADMIN)
    public List<CompanyResponse> listCompanies(@RequestParam(required = false) TrangThaiCompany status) {
        return companyService.listByStatus(status);
    }
}