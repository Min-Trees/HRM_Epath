package com.company.hrm.common.security;

import com.company.hrm.common.error.ForbiddenException;
import com.company.hrm.system.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.UUID;

/**
 * Filter giả lập auth: đọc header X-User-Id / X-User-Role / X-Company-Id / X-Permissions
 * và set vào {@link AuthContext} cho mỗi request; đồng thời kiểm tra
 * {@link RequiresRole} (T09) và {@link RequiresPermission} (T11 mới).
 *
 * <p>CẢNH BÁO: đây là stub dev — khi tích hợp thật sẽ thay bằng filter chain
 * Spring Security + JWT.
 */
@Aspect
@Component
public class AuthAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Controller *)")
    public Object aroundController(ProceedingJoinPoint pjp) throws Throwable {
        try {
            HttpServletRequest req = currentRequest();
            if (req != null) {
                AuthContext.clear();
                TenantContext.clear();

                UUID userId = parseUuid(req.getHeader(AuthContext.HDR_USER_ID));
                String role = trimToNull(req.getHeader(AuthContext.HDR_USER_ROLE));
                UUID companyId = parseUuid(req.getHeader(AuthContext.HDR_COMPANY_ID));
                Set<String> perms = AuthContext.csvToPermissions(req.getHeader(AuthContext.HDR_PERMISSIONS));

                AuthContext.set(userId, role, companyId, perms);
                TenantContext.set(companyId);

                // Thứ tự: permission cụ thể trước, rồi role-based fallback.
                checkRequiresPermission(pjp);
                checkRequiresRole(pjp, role);
            }
            return pjp.proceed();
        } finally {
            AuthContext.clear();
            TenantContext.clear();
        }
    }

    private void checkRequiresRole(ProceedingJoinPoint pjp, String currentRole) {
        RequiresRole methodAnnotation = pjp.getSignature() instanceof MethodSignature ms
                ? ms.getMethod().getAnnotation(RequiresRole.class) : null;
        RequiresRole classAnnotation = pjp.getTarget().getClass().getAnnotation(RequiresRole.class);
        RequiresRole effective = methodAnnotation != null ? methodAnnotation : classAnnotation;
        if (effective == null) return;

        if (currentRole == null) {
            throw new ForbiddenException("AUTH_REQUIRED", "Cần truyền header " + AuthContext.HDR_USER_ROLE);
        }
        for (Role r : effective.value()) {
            if (r.name().equals(currentRole)) return;
        }
        throw new ForbiddenException("FORBIDDEN",
                "Vai trò '" + currentRole + "' không được phép gọi endpoint này");
    }

    private void checkRequiresPermission(ProceedingJoinPoint pjp) {
        RequiresPermission methodAnnotation = pjp.getSignature() instanceof MethodSignature ms
                ? ms.getMethod().getAnnotation(RequiresPermission.class) : null;
        RequiresPermission classAnnotation = pjp.getTarget().getClass().getAnnotation(RequiresPermission.class);
        RequiresPermission effective = methodAnnotation != null ? methodAnnotation : classAnnotation;
        if (effective == null) return;

        for (String code : effective.value()) {
            if (AuthContext.hasPermission(code)) return;
        }
        throw new ForbiddenException("PERMISSION_DENIED",
                "Thiếu quyền '" + String.join(",", effective.value())
                        + "' để gọi endpoint này");
    }

    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest();
        }
        return null;
    }

    private static UUID parseUuid(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return UUID.fromString(s.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}