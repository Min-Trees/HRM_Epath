package com.company.hrm.system.controller;

import com.company.hrm.common.security.RequiresPermission;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.system.dto.UserAccountRequest;
import com.company.hrm.system.dto.UserAccountResponse;
import com.company.hrm.system.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/system/users")
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @PostMapping
    @RequiresPermission("user_account.create")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public ResponseEntity<UserAccountResponse> createUser(@Valid @RequestBody UserAccountRequest req) {
        UserAccountResponse r = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1/system/users/" + r.getUserId())).body(r);
    }

    @GetMapping("/{id}")
    @RequiresPermission("user_account.read")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public UserAccountResponse getUser(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("user_account.update")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public UserAccountResponse updateUser(@PathVariable UUID id, @RequestBody UserAccountRequest req) {
        return service.update(id, req);
    }

    @PostMapping("/{id}/lock")
    @RequiresPermission("user_account.lock")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public UserAccountResponse lock(@PathVariable UUID id) {
        return service.lock(id);
    }

    @PostMapping("/{id}/unlock")
    @RequiresPermission("user_account.unlock")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public UserAccountResponse unlock(@PathVariable UUID id) {
        return service.unlock(id);
    }

    @PostMapping("/{id}/reset-password")
    @RequiresPermission("user_account.reset_password")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public UserAccountResponse resetPassword(@PathVariable UUID id,
                                              @RequestParam("newPassword") String newPassword) {
        return service.resetPassword(id, newPassword);
    }

    @GetMapping
    @RequiresPermission("user_account.read")
    @RequiresRole({Role.SYSTEM_ADMIN, Role.COMPANY_ADMIN})
    public List<UserAccountResponse> listByCompany(@RequestParam("companyId") UUID companyId) {
        return service.listByCompany(companyId);
    }
}