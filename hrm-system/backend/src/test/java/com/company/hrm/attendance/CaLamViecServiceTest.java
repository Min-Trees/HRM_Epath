package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.CaLamViecRequest;
import com.company.hrm.attendance.dto.CaLamViecResponse;
import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.entity.CaLamViec.LoaiCa;
import com.company.hrm.attendance.repository.CaLamViecRepository;
import com.company.hrm.attendance.repository.PhanCaRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CaLamViecServiceTest {

    private CaLamViecRepository caRepo;
    private PhanCaRepository phanCaRepo;
    private CaLamViecService service;

    @BeforeEach
    void setUp() {
        caRepo = mock(CaLamViecRepository.class);
        phanCaRepo = mock(PhanCaRepository.class);
        service = new CaLamViecService(caRepo, phanCaRepo);
        when(caRepo.save(any(CaLamViec.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private CaLamViecRequest req(boolean quaNgay) {
        CaLamViecRequest r = new CaLamViecRequest();
        r.setMaCa("CA-HC");
        r.setTenCa("Hành chính");
        r.setLoaiCa(LoaiCa.HANH_CHINH);
        r.setGioBatDau(LocalTime.of(8, 0));
        r.setGioKetThuc(LocalTime.of(17, 0));
        r.setSoGioChuan(new BigDecimal("8.00"));
        r.setQuaNgay(quaNgay);
        return r;
    }

    @Test
    void create_succeeds_hanhChinh() {
        when(caRepo.existsByMaCa("CA-HC")).thenReturn(false);
        CaLamViecResponse r = service.create(req(false));
        assertEquals("CA-HC", r.getMaCa());
        assertEquals(LoaiCa.HANH_CHINH, r.getLoaiCa());
        assertTrue(r.isActive());
        assertFalse(r.isQuaNgay());
    }

    @Test
    void create_succeeds_quaDem() {
        when(caRepo.existsByMaCa("CA-DEM")).thenReturn(false);
        CaLamViecRequest r = req(true);
        r.setMaCa("CA-DEM");
        r.setGioBatDau(LocalTime.of(22, 0));
        r.setGioKetThuc(LocalTime.of(6, 0));
        CaLamViecResponse resp = service.create(r);
        assertTrue(resp.isQuaNgay());
    }

    @Test
    void create_duplicateMa_throws() {
        when(caRepo.existsByMaCa("CA-HC")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(req(false)));
        assertEquals("CA_LAM_VIEC_MA_DUPLICATE", ex.getCode());
    }

    @Test
    void create_timeInvalid_throws() {
        when(caRepo.existsByMaCa("CA-HC")).thenReturn(false);
        CaLamViecRequest r = req(false);
        r.setGioKetThuc(LocalTime.of(7, 0)); // trước gioBatDau
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("CA_LAM_VIEC_TIME_INVALID", ex.getCode());
    }

    @Test
    void create_quaDem_equalTimes_throws() {
        when(caRepo.existsByMaCa("CA-DEM")).thenReturn(false);
        CaLamViecRequest r = req(true);
        r.setGioBatDau(LocalTime.of(22, 0));
        r.setGioKetThuc(LocalTime.of(22, 0));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("CA_LAM_VIEC_TIME_INVALID", ex.getCode());
    }

    @Test
    void update_succeeds() {
        UUID id = UUID.randomUUID();
        CaLamViec existing = new CaLamViec();
        existing.setCaId(id);
        existing.setMaCa("CA-HC");
        when(caRepo.findById(id)).thenReturn(Optional.of(existing));

        CaLamViecRequest r = req(false);
        r.setTenCa("Hành chính cập nhật");
        r.setSoGioChuan(new BigDecimal("7.50"));
        CaLamViecResponse resp = service.update(id, r);
        assertEquals("Hành chính cập nhật", resp.getTenCa());
        assertEquals(new BigDecimal("7.50"), resp.getSoGioChuan());
    }

    @Test
    void close_notUsed_succeeds() {
        UUID id = UUID.randomUUID();
        CaLamViec e = new CaLamViec();
        e.setCaId(id);
        e.setMaCa("CA-HC");
        e.setActive(true);
        when(caRepo.findById(id)).thenReturn(Optional.of(e));
        when(phanCaRepo.existsByCaId(id)).thenReturn(false);

        CaLamViecResponse r = service.close(id);
        assertFalse(r.isActive());
    }

    @Test
    void close_inUse_throws() {
        UUID id = UUID.randomUUID();
        CaLamViec e = new CaLamViec();
        e.setCaId(id);
        e.setActive(true);
        when(caRepo.findById(id)).thenReturn(Optional.of(e));
        when(phanCaRepo.existsByCaId(id)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.close(id));
        assertEquals("CA_LAM_VIEC_IN_USE", ex.getCode());
    }

    @Test
    void close_alreadyClosed_idempotent() {
        UUID id = UUID.randomUUID();
        CaLamViec e = new CaLamViec();
        e.setCaId(id);
        e.setActive(false);
        when(caRepo.findById(id)).thenReturn(Optional.of(e));

        CaLamViecResponse r = service.close(id);
        assertFalse(r.isActive());
        verify(caRepo, never()).save(any());
    }

    @Test
    void get_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(caRepo.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }

    @Test
    void findAll_withActiveFilter() {
        CaLamViec e = new CaLamViec();
        e.setMaCa("CA-A");
        e.setActive(true);
        when(caRepo.findAllByActiveOrderByMaCaAsc(true)).thenReturn(List.of(e));
        List<CaLamViecResponse> r = service.findAll(true);
        assertEquals(1, r.size());
        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        verify(caRepo).findAllByActiveOrderByMaCaAsc(captor.capture());
        assertTrue(captor.getValue());
    }
}
