package com.company.hrm.system.audit;

import com.company.hrm.system.entity.AuditLog;
import com.company.hrm.system.repository.AuditLogRepository;
import com.company.hrm.system.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditAspectTest {

    private AuditService auditService;
    private AuditLogRepository repo;
    private AuditAspect aspect;

    @BeforeEach
    void setUp() {
        repo = mock(AuditLogRepository.class);
        auditService = new AuditService(repo);
        aspect = new AuditAspect(auditService);
        when(repo.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private JoinPoint jp(String methodName, Object[] args) {
        JoinPoint pjp = mock(JoinPoint.class);
        Signature sig = mock(Signature.class);
        when(sig.getName()).thenReturn(methodName);
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.getArgs()).thenReturn(args);
        return pjp;
    }

    private Auditable sampleAnn() throws NoSuchMethodException {
        Method m = SampleService.class.getMethod("doAudited");
        return m.getAnnotation(Auditable.class);
    }

    static class SampleService {
        @Auditable(module = "timekeeping", action = "APPROVE_CAP2", entityType = "NghiPhep")
        public void doAudited() {}
    }

    @Test
    void afterAuditable_ghiLog() throws NoSuchMethodException {
        UUID leaveId = UUID.randomUUID();
        JoinPoint pjp = jp("doAudited", new Object[]{leaveId});
        aspect.afterAuditable(pjp, sampleAnn(), leaveId);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repo).save(captor.capture());
        AuditLog saved = captor.getValue();
        assertEquals("timekeeping", saved.getModule());
        assertEquals("APPROVE_CAP2", saved.getAction());
        assertEquals("NghiPhep", saved.getEntityType());
        assertEquals(leaveId, saved.getEntityId());
    }

    @Test
    void afterAuditable_auditServiceLoi_khongNem() {
        // AuditAspect phải swallow exception để không ảnh hưởng nghiệp vụ chính
        AuditService badSvc = mock(AuditService.class);
        AuditAspect a = new AuditAspect(badSvc);
        when(badSvc.record(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("boom"));
        UUID id = UUID.randomUUID();
        JoinPoint pjp = jp("create", new Object[]{id});

        Auditable ann = newSample("hr", "CREATE", "Company");
        assertDoesNotThrow(() -> a.afterAuditable(pjp, ann, id));
    }

    private static Auditable newSample(String module, String action, String entityType) {
        return new Auditable() {
            @Override public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Auditable.class;
            }
            @Override public String module() { return module; }
            @Override public String action() { return action; }
            @Override public String entityType() { return entityType; }
        };
    }
}