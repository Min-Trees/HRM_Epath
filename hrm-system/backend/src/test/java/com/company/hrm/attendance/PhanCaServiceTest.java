package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.PhanCaAssignResult;
import com.company.hrm.attendance.dto.PhanCaRequest;
import com.company.hrm.attendance.dto.PhanCaResponse;
import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.entity.CaLamViec.LoaiCa;
import com.company.hrm.attendance.entity.PhanCa;
import com.company.hrm.attendance.repository.CaLamViecRepository;
import com.company.hrm.attendance.repository.ChamCongChiTietRepository;
import com.company.hrm.attendance.repository.PhanCaRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PhanCaServiceTest {

    private PhanCaRepository phanCaRepo;
    private CaLamViecRepository caRepo;
    private NhanVienRepository nvRepo;
    private ChamCongChiTietRepository chamCongRepo;
    private PhanCaService service;

    @BeforeEach
    void setUp() {
        phanCaRepo = mock(PhanCaRepository.class);
        caRepo = mock(CaLamViecRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        chamCongRepo = mock(ChamCongChiTietRepository.class);
        service = new PhanCaService(phanCaRepo, caRepo, nvRepo, chamCongRepo);
        when(phanCaRepo.save(any(PhanCa.class))).thenAnswer(inv -> inv.getArgument(0));
        when(chamCongRepo.existsByPhanCaId(any())).thenReturn(false);
    }

    private NhanVien nv(UUID id, TrangThaiNv status) {
        NhanVien n = new NhanVien();
        n.setNhanVienId(id);
        n.setMaNv("NV0001");
        n.setTrangThai(status);
        return n;
    }

    private CaLamViec ca(UUID id, String ma) {
        CaLamViec c = new CaLamViec();
        c.setCaId(id);
        c.setMaCa(ma);
        c.setTenCa(ma);
        c.setLoaiCa(LoaiCa.HANH_CHINH);
        c.setGioBatDau(LocalTime.of(8, 0));
        c.setGioKetThuc(LocalTime.of(17, 0));
        c.setSoGioChuan(new BigDecimal("8.00"));
        c.setQuaNgay(false);
        c.setActive(true);
        return c;
    }

    // ---------------- single ----------------

    @Test
    void assignSingle_succeeds() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any())).thenReturn(false);

        PhanCaResponse r = service.assignSingle(nvId, caId, LocalDate.of(2026, 7, 1), "Ca HC", false);
        assertEquals(nvId, r.getNhanVienId());
        assertEquals(caId, r.getCaId());
        assertEquals("CA-HC", r.getMaCa());
        assertEquals("Ca HC", r.getGhiChu());
    }

    @Test
    void assignSingle_duplicate_throws() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assignSingle(nvId, caId, LocalDate.of(2026, 7, 1), null, false));
        assertEquals("PHAN_CA_DUPLICATE", ex.getCode());
    }

    @Test
    void assignSingle_duplicate_withOverride_succeeds() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any())).thenReturn(true);

        PhanCa old = new PhanCa();
        old.setPhanCaId(UUID.randomUUID());
        old.setCaId(UUID.randomUUID());
        old.setNhanVienId(nvId);
        old.setNgayApDung(LocalDate.of(2026, 7, 1));
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(nvId, LocalDate.of(2026, 7, 1)))
                .thenReturn(Optional.of(old));

        PhanCaResponse r = service.assignSingle(nvId, caId, LocalDate.of(2026, 7, 1), "ghi đè", true);
        verify(phanCaRepo).delete(old);
        ArgumentCaptor<PhanCa> captor = ArgumentCaptor.forClass(PhanCa.class);
        verify(phanCaRepo).save(captor.capture());
        assertEquals(caId, captor.getValue().getCaId());
        assertTrue(captor.getValue().getGhiChu().contains("ghi đè"));
    }

    @Test
    void assignSingle_employeeNotFound_throws() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.assignSingle(nvId, caId, LocalDate.of(2026, 7, 1), null, false));
    }

    @Test
    void assignSingle_employeeDaNghi_throws() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.DA_NGHI_VIEC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assignSingle(nvId, caId, LocalDate.of(2026, 7, 1), null, false));
        assertEquals("EMPLOYEE_NOT_WORKING", ex.getCode());
    }

    @Test
    void assignSingle_inactiveCa_throws() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        CaLamViec ca = ca(caId, "CA-HC");
        ca.setActive(false);
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assignSingle(nvId, caId, LocalDate.of(2026, 7, 1), null, false));
        assertEquals("CA_LAM_VIEC_INACTIVE", ex.getCode());
    }

    // ---------------- bulk ----------------

    @Test
    void assignBulk_createsForAllDays() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any())).thenReturn(false);

        PhanCaAssignResult r = service.assignBulk(
                List.of(nvId), caId,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3), "tuần 1");
        assertEquals(3, r.getCreated());
        assertEquals(0, r.getSkipped());
        assertEquals(3, r.getAssignments().size());
    }

    @Test
    void assignBulk_skipsDuplicateDays() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        // Ngày 2026-07-02 đã có phân ca
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any()))
                .thenAnswer(inv -> {
                    LocalDate d = inv.getArgument(1);
                    return d.equals(LocalDate.of(2026, 7, 2));
                });

        PhanCaAssignResult r = service.assignBulk(
                List.of(nvId), caId,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3), null);
        assertEquals(2, r.getCreated());
        assertEquals(1, r.getSkipped());
    }

    @Test
    void assignBulk_invalidRange_throws() {
        UUID caId = UUID.randomUUID();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.assignBulk(List.of(UUID.randomUUID()), caId,
                        LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 1), null));
        assertEquals("PHAN_CA_RANGE_INVALID", ex.getCode());
    }

    // ---------------- rotating ----------------

    @Test
    void assignRotating_cyclesCorrectly() {
        UUID nv1 = UUID.randomUUID();
        UUID nv2 = UUID.randomUUID();
        UUID caA = UUID.randomUUID();
        UUID caB = UUID.randomUUID();
        UUID caC = UUID.randomUUID();
        when(nvRepo.findById(any())).thenAnswer(inv -> {
            UUID id = inv.getArgument(0);
            return Optional.of(nv(id, TrangThaiNv.CHINH_THUC));
        });
        when(caRepo.findById(any())).thenAnswer(inv -> {
            UUID id = inv.getArgument(0);
            if (id.equals(caA)) return Optional.of(ca(caA, "CA-A"));
            if (id.equals(caB)) return Optional.of(ca(caB, "CA-B"));
            if (id.equals(caC)) return Optional.of(ca(caC, "CA-C"));
            return Optional.empty();
        });
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(any(), any())).thenReturn(false);

        // Khoảng 6 ngày, 3 ca, cycle=3 → A, B, C, A, B, C
        PhanCaAssignResult r = service.assignRotating(
                List.of(nv1, nv2), List.of(caA, caB, caC), 3,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 6), null);
        assertEquals(12, r.getCreated()); // 2 NV × 6 ngày

        ArgumentCaptor<PhanCa> captor = ArgumentCaptor.forClass(PhanCa.class);
        verify(phanCaRepo, times(12)).save(captor.capture());
        List<PhanCa> saved = captor.getAllValues();
        // NV1 ngày 1 -> A
        assertEquals(caA, saved.get(0).getCaId());
        // NV1 ngày 2 -> B
        assertEquals(caB, saved.get(1).getCaId());
        // NV1 ngày 3 -> C
        assertEquals(caC, saved.get(2).getCaId());
        // NV1 ngày 4 -> A (chu kỳ lặp lại)
        assertEquals(caA, saved.get(3).getCaId());
        // NV1 ngày 5 -> B
        assertEquals(caB, saved.get(4).getCaId());
        // NV1 ngày 6 -> C
        assertEquals(caC, saved.get(5).getCaId());
        // NV2 ngày 1 -> A (mỗi NV xoay độc lập)
        assertEquals(caA, saved.get(6).getCaId());
    }

    @Test
    void assignRotating_skipsDuplicates() {
        UUID nvId = UUID.randomUUID();
        UUID caA = UUID.randomUUID();
        UUID caB = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caA)).thenReturn(Optional.of(ca(caA, "CA-A")));
        when(caRepo.findById(caB)).thenReturn(Optional.of(ca(caB, "CA-B")));
        // ngày 2 đã có
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any()))
                .thenAnswer(inv -> {
                    LocalDate d = inv.getArgument(1);
                    return d.equals(LocalDate.of(2026, 7, 2));
                });

        PhanCaAssignResult r = service.assignRotating(
                List.of(nvId), List.of(caA, caB), 2,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3), null);
        assertEquals(2, r.getCreated());
        assertEquals(1, r.getSkipped());
    }

    // ---------------- getStandardShift ----------------

    @Test
    void getStandardShift_returnsCa() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        PhanCa pc = new PhanCa();
        pc.setPhanCaId(UUID.randomUUID());
        pc.setCaId(caId);
        pc.setNhanVienId(nvId);
        pc.setNgayApDung(LocalDate.of(2026, 7, 1));
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(nvId, LocalDate.of(2026, 7, 1)))
                .thenReturn(Optional.of(pc));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));

        CaLamViec result = service.getStandardShift(nvId, LocalDate.of(2026, 7, 1));
        assertNotNull(result);
        assertEquals("CA-HC", result.getMaCa());
    }

    @Test
    void getStandardShift_returnsNullWhenNoAssignment() {
        UUID nvId = UUID.randomUUID();
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(eq(nvId), any())).thenReturn(Optional.empty());
        CaLamViec result = service.getStandardShift(nvId, LocalDate.of(2026, 7, 1));
        assertNull(result);
    }

    // ---------------- delete ----------------

    @Test
    void delete_ok() {
        UUID id = UUID.randomUUID();
        PhanCa pc = new PhanCa();
        pc.setPhanCaId(id);
        when(phanCaRepo.findById(id)).thenReturn(Optional.of(pc));

        service.delete(id);
        verify(phanCaRepo).delete(pc);
    }

    @Test
    void delete_hasChamCong_throws() {
        UUID id = UUID.randomUUID();
        PhanCa pc = new PhanCa();
        pc.setPhanCaId(id);
        when(phanCaRepo.findById(id)).thenReturn(Optional.of(pc));
        when(chamCongRepo.existsByPhanCaId(id)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(id));
        assertEquals("PHAN_CA_HAS_CHAM_CONG", ex.getCode());
    }

    @Test
    void delete_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(phanCaRepo.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    }

    // ---------------- dispatch ----------------

    @Test
    void assign_dispatchesToSingle() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any())).thenReturn(false);

        PhanCaRequest req = new PhanCaRequest();
        req.setNhanVienId(nvId);
        req.setCaId(caId);
        req.setNgayApDung(LocalDate.of(2026, 7, 1));

        Object result = service.assign(req);
        assertInstanceOf(PhanCaResponse.class, result);
    }

    @Test
    void assign_dispatchesToBulk() {
        UUID nvId = UUID.randomUUID();
        UUID caId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, TrangThaiNv.CHINH_THUC)));
        when(caRepo.findById(caId)).thenReturn(Optional.of(ca(caId, "CA-HC")));
        when(phanCaRepo.existsByNhanVienIdAndNgayApDung(eq(nvId), any())).thenReturn(false);

        PhanCaRequest req = new PhanCaRequest();
        req.setNhanVienIds(List.of(nvId));
        req.setCaIds(List.of(caId));
        req.setFromDate(LocalDate.of(2026, 7, 1));
        req.setToDate(LocalDate.of(2026, 7, 1));

        Object result = service.assign(req);
        assertInstanceOf(PhanCaAssignResult.class, result);
        assertEquals(1, ((PhanCaAssignResult) result).getCreated());
    }

    @Test
    void assign_invalid_throws() {
        PhanCaRequest req = new PhanCaRequest();
        BusinessException ex = assertThrows(BusinessException.class, () -> service.assign(req));
        assertEquals("PHAN_CA_REQUEST_INVALID", ex.getCode());
    }
}
