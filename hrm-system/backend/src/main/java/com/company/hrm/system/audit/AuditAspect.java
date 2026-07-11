package com.company.hrm.system.audit;

import com.company.hrm.system.service.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Aspect bắt method có annotation {@link Auditable} và ghi log sau khi method return OK.
 *
 * <p>Snapshot {@code oldValue}/{@code newValue} ở dạng JSON ngắn gồm:
 * <ul>
 *   <li>{@code entityId}: nếu lấy được từ arg đầu tiên (UUID hoặc có method {@code getId()}).</li>
 *   <li>{@code method}: tên method.</li>
 *   <li>{@code args}: một số arg đầu (để debug) — đã redact các field nhạy cảm (password).</li>
 * </ul>
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditService auditService;
    private final ObjectMapper mapper;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @AfterReturning(
            pointcut = "@annotation(auditable)",
            returning = "result")
    public void afterAuditable(JoinPoint jp, Auditable auditable, Object result) {
        try {
            Map<String, Object> snap = new LinkedHashMap<>();
            UUID entityId = extractEntityId(jp.getArgs(), result);
            if (entityId != null) snap.put("entityId", entityId.toString());
            snap.put("method", jp.getSignature().getName());

            String argsJson = safeToJson(safeArgs(jp.getArgs()));
            String newJson = safeToJson(snap);

            auditService.record(
                    auditable.module(),
                    auditable.action(),
                    auditable.entityType(),
                    entityId,
                    null,
                    argsJson + (newJson == null ? "" : "\nresult:" + newJson));
        } catch (Exception ex) {
            // Không để audit lỗi làm ảnh hưởng nghiệp vụ chính
            log.warn("AuditAspect ghi log thất bại: {}", ex.getMessage());
        }
    }

    /** Cố gắng lấy entityId từ args[0] (UUID) hoặc kết quả trả về. */
    private UUID extractEntityId(Object[] args, Object result) {
        if (args != null && args.length > 0 && args[0] instanceof UUID u) return u;
        if (result instanceof UUID u) return u;
        // Thử gọi getId() nếu có (reflection nhẹ, không tốn)
        if (result != null) {
            try {
                Object id = result.getClass().getMethod("getId").invoke(result);
                if (id instanceof UUID u) return u;
                if (id instanceof java.util.UUID u2) return u2;
            } catch (NoSuchMethodException ignored) {
                // không sao
            } catch (Exception ex) {
                log.debug("extractEntityId reflect lỗi: {}", ex.getMessage());
            }
        }
        return null;
    }

    /** Loại bỏ arg có vẻ là password / token. */
    private Map<String, Object> safeArgs(Object[] args) {
        if (args == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object a = args[i];
            if (a == null) {
                m.put("arg" + i, null);
                continue;
            }
            String cname = a.getClass().getSimpleName().toLowerCase();
            if (cname.contains("password") || cname.contains("login")
                    || cname.contains("token") || cname.contains("credential")) {
                m.put("arg" + i, "<redacted>");
                continue;
            }
            // Chỉ ghi tên class + toString() ngắn
            m.put("arg" + i, a.getClass().getSimpleName());
        }
        return m;
    }

    private String safeToJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }
}