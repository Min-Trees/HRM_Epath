package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.NgachBacLuongRequest;
import com.company.hrm.hr.entity.NgachBacLuong;
import com.company.hrm.hr.repository.NgachBacLuongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NgachBacLuongServiceTest {

    private NgachBacLuongRepository repo;
    private NgachBacLuongService service;

    @BeforeEach
    void setUp() {
        repo = mock(NgachBacLuongRepository.class);
        service = new NgachBacLuongService(repo);
        when(repo.save(any(NgachBacLuong.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private NgachBacLuongRequest req(String ma, String ten, double heSo) {
        NgachBacLuongRequest r = new NgachBacLuongRequest();
        r.setMaNgach(ma);
        r.setTenChucDanh(ten);
        r.setBacLuong(1);
        r.setHeSoLuong(BigDecimal.valueOf(heSo));
        r.setLuongCoBanToiThieu(new BigDecimal("4960000.00"));
        return r;
    }

    @Test
    void create_duplicateMa_throws() {
        when(repo.existsByMaNgach("NV01")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("NV01", "NV1", 2.34)));
        assertEquals("MA_NGACH_DUPLICATE", ex.getCode());
    }

    @Test
    void create_succeeds() {
        when(repo.existsByMaNgach("NV01")).thenReturn(false);
        var resp = service.create(req("NV01", "Nhân viên 1", 2.34));
        assertEquals("NV01", resp.getMaNgach());
        assertTrue(resp.isActive());
    }

    @Test
    void get_notFound_throws() {
        when(repo.findById(any())).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.get(UUID.randomUUID()));
        assertEquals("NGACH_BAC_NOT_FOUND", ex.getCode());
    }

    @Test
    void close_marksInactive() {
        UUID id = UUID.randomUUID();
        NgachBacLuong e = new NgachBacLuong();
        e.setNgachBacId(id);
        e.setActive(true);
        when(repo.findById(id)).thenReturn(Optional.of(e));
        var resp = service.close(id);
        assertFalse(resp.isActive());
    }
}