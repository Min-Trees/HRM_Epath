package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.NguoiPhuThuocRequest;
import com.company.hrm.hr.entity.NguoiPhuThuoc;
import com.company.hrm.hr.repository.NguoiPhuThuocRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NguoiPhuThuocServiceTest {

    private NguoiPhuThuocRepository repo;
    private NhanVienRepository nvRepo;
    private NguoiPhuThuocService service;

    @BeforeEach
    void setUp() {
        repo = mock(NguoiPhuThuocRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        service = new NguoiPhuThuocService(repo, nvRepo);
        when(repo.save(any(NguoiPhuThuoc.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private NguoiPhuThuocRequest req() {
        NguoiPhuThuocRequest r = new NguoiPhuThuocRequest();
        r.setHoTen("Người phụ thuộc");
        r.setQuanHe("con");
        r.setTuNgayGiamTru(LocalDate.of(2026, 1, 1));
        return r;
    }

    @Test
    void add_employeeNotFound_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.existsById(nvId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.add(nvId, req()));
    }

    @Test
    void add_denNgayBeforeTuNgay_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.existsById(nvId)).thenReturn(true);
        NguoiPhuThuocRequest r = req();
        r.setDenNgayGiamTru(LocalDate.of(2025, 1, 1)); // trước tu_ngay
        BusinessException ex = assertThrows(BusinessException.class, () -> service.add(nvId, r));
        assertEquals("DEPENDENT_DATE_INVALID", ex.getCode());
    }

    @Test
    void add_succeeds() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.existsById(nvId)).thenReturn(true);
        var resp = service.add(nvId, req());
        assertEquals(nvId, resp.getNhanVienId());
        assertTrue(resp.isActive());
    }

    @Test
    void delete_marksInactive() {
        UUID nvId = UUID.randomUUID();
        UUID depId = UUID.randomUUID();
        NguoiPhuThuoc e = new NguoiPhuThuoc();
        e.setNguoiPhuThuocId(depId);
        e.setNhanVienId(nvId);
        e.setActive(true);
        when(repo.findById(depId)).thenReturn(Optional.of(e));

        service.delete(nvId, depId);
        assertFalse(e.isActive());
    }

    @Test
    void delete_ownerMismatch_throws() {
        UUID nvId = UUID.randomUUID();
        UUID depId = UUID.randomUUID();
        NguoiPhuThuoc e = new NguoiPhuThuoc();
        e.setNguoiPhuThuocId(depId);
        e.setNhanVienId(UUID.randomUUID()); // khác nvId
        when(repo.findById(depId)).thenReturn(Optional.of(e));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(nvId, depId));
        assertEquals("DEPENDENT_OWNER_MISMATCH", ex.getCode());
    }
}