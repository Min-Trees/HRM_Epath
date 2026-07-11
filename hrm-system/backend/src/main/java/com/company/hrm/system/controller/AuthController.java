package com.company.hrm.system.controller;

import com.company.hrm.system.dto.LoginRequest;
import com.company.hrm.system.dto.LoginResponse;
import com.company.hrm.system.dto.UserAccountResponse;
import com.company.hrm.system.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Stub auth controller — chỉ trả thông tin cơ bản cho client.
 * Task sau sẽ thay bằng Spring Security + JWT thật.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAccountService service;

    public AuthController(UserAccountService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    @GetMapping("/me")
    public String me() {
        var userId = com.company.hrm.common.security.AuthContext.currentUserIdOrNull();
        return userId == null ? "anonymous" : userId.toString();
    }
}