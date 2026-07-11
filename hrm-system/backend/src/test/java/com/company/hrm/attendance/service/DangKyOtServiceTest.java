package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.DangKyOtMonthlySummary;
import com.company.hrm.attendance.dto.DangKyOtRequest;
import com.company.hrm.attendance.dto.DangKyOtResponse;
import com.company.hrm.attendance.entity.DangKyOt;
import com.company.hrm.attendance.entity.HeSoOt;
import com.company.hrm.attendance.entity.TrangThaiDon;
import com.company.hrm.attendance.repository.DangKyOtRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DangKyOtServiceTest {

    private DangKyOtRepository repo;
    private NhanVienRepository nvRepo;
    private DangKyOtService service;

    private final UUID NV_ID = UUID.randomUUID();
    private final UUID OT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repo = mock(DangKyOtRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        service = new DangKyOtService(repo, nvRepo);
        when(repo.save(any(DangKyOt.class))).thenAnswer(inv -> inv.getArgument(0));
        when(nvRepo.existsById(NV_ID)).thenReturn(true);
    }

    private OffsetDateTime at(LocalDate d, int h, int m) {
        return OffsetDateTime.of(d, java.time.LocalTime.of(h, m), ZoneOffset.UTC);
    }

    private DangKyOtRequest req(LocalDate ngay, OffsetDateTime bd, OffsetDateTime kt, HeSoOt heSo) {
        DangKyOtRequest r = new DangKyOtRequest();
        r.setNhanVienId(NV_ID);
        r.setNgayLamOt(ngay);
        r.setGioBatDau(bd);
        r.setGioKetThuc(kt);
        r.setHeSoOt(heSo);
        r.setLamDem(false);
        r.setLyDo("test");
        return r;
    }

    private DangKyOt otWith(TrangThaiDon st) {
        DangKyOt o = new DangKyOt();
        o.setOtId(OT_ID);
        o.setNhanVienId(NV_ID);
        o.setNgayLamOt(LocalDate.of(2026, 1, 15));
        o.setGioBatDau(at(LocalDate.of(2026, 1, 15), 18, 0));
        o.setGioKetThuc(at(LocalDate.of(2026, 1, 15), 21, 0));
        o.setSoGioOt(new BigDecimal("3.00"));
        o.setHeSoOt(HeSoOt.NGAY_THUONG_150);
        o.setTrangThai(st);
        return o;
    }

    // ---------------- create ----------------

    @Test
    void create_thanhCong_tinhSoGio() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        DangKyOtResponse resp = service.create(req(ngay,
                at(ngay, 18, 0), at(ngay, 21, 30), HeSoOt.NGAY_THUONG_150));
        // 3.5 giờ
        assertEquals(new BigDecimal("3.50"), resp.getSoGioOt());
        assertEquals(TrangThaiDon.CHO_DUYET, resp.getTrangThai());
        assertEquals(HeSoOt.NGAY_THUONG_150, resp.getHeSoOt());
        assertFalse(resp.isLamDem());
    }

    @Test
    void create_gioKetThucTruoc_throws() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req(ngay,
                        at(ngay, 21, 0), at(ngay, 18, 0), HeSoOt.NGAY_THUONG_150)));
        assertEquals("OT_GIO_INVALID", ex.getCode());
    }

    @Test
    void create_gioKetBangGioBat_throws() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req(ngay,
                        at(ngay, 18, 0), at(ngay, 18, 0), HeSoOt.NGAY_THUONG_150)));
        assertEquals("OT_GIO_INVALID", ex.getCode());
    }

    @Test
    void create_employeeNotFound_throws() {
        when(nvRepo.existsById(NV_ID)).thenReturn(false);
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(req(ngay,
                        at(ngay, 18, 0), at(ngay, 21, 0), HeSoOt.NGAY_THUONG_150)));
    }

    @Test
    void create_lamDem_true() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        DangKyOtRequest r = req(ngay, at(ngay, 22, 0), at(ngay.plusDays(1), 2, 0), HeSoOt.NGAY_NGHI_TUAN_200);
        r.setLamDem(true);
        DangKyOtResponse resp = service.create(r);
        assertTrue(resp.isLamDem());
        assertEquals(new BigDecimal("4.00"), resp.getSoGioOt());
    }

    // ---------------- duyệt 2 cấp ----------------

    @Test
    void approveCap1_choDuyet_duyetCap1() {
        DangKyOt e = otWith(TrangThaiDon.CHO_DUYET);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        UUID approver = UUID.randomUUID();
        DangKyOtResponse resp = service.approveCap1(OT_ID, approver, true, "ok");
        assertEquals(TrangThaiDon.DUYET_CAP_1, resp.getTrangThai());
        assertEquals(approver, resp.getDuyetCap1Boi());
    }

    @Test
    void approveCap2_sauCap1_daDuyet() {
        UUID cap1 = UUID.randomUUID();
        UUID cap2 = UUID.randomUUID();
        DangKyOt e = otWith(TrangThaiDon.DUYET_CAP_1);
        e.setDuyetCap1Boi(cap1);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        DangKyOtResponse resp = service.approveCap2(OT_ID, cap2, true, null);
        assertEquals(TrangThaiDon.DA_DUYET, resp.getTrangThai());
        assertEquals(cap2, resp.getDuyetCap2Boi());
    }

    @Test
    void approveCap2_cungNguoi_throws() {
        UUID same = UUID.randomUUID();
        DangKyOt e = otWith(TrangThaiDon.DUYET_CAP_1);
        e.setDuyetCap1Boi(same);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approveCap2(OT_ID, same, true, null));
        assertEquals("APPROVER_DUPLICATE", ex.getCode());
    }

    @Test
    void approveCap1_tuChoi() {
        DangKyOt e = otWith(TrangThaiDon.CHO_DUYET);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        DangKyOtResponse resp = service.approveCap1(OT_ID, UUID.randomUUID(), false, "no");
        assertEquals(TrangThaiDon.TU_CHOI, resp.getTrangThai());
    }

    @Test
    void approveCap2_saiTrangThai_throws() {
        DangKyOt e = otWith(TrangThaiDon.CHO_DUYET);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approveCap2(OT_ID, UUID.randomUUID(), true, null));
        assertEquals("OT_INVALID_STATE", ex.getCode());
    }

    // ---------------- cancel ----------------

    @Test
    void cancel_choDuyet() {
        DangKyOt e = otWith(TrangThaiDon.CHO_DUYET);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        assertEquals(TrangThaiDon.HUY, service.cancel(OT_ID).getTrangThai());
    }

    @Test
    void cancel_daDuyet_choHuy() {
        // OT đã duyệt vẫn cho hủy (HR audit)
        DangKyOt e = otWith(TrangThaiDon.DA_DUYET);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        assertEquals(TrangThaiDon.HUY, service.cancel(OT_ID).getTrangThai());
    }

    @Test
    void cancel_daHuy_throws() {
        DangKyOt e = otWith(TrangThaiDon.HUY);
        when(repo.findById(OT_ID)).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.cancel(OT_ID));
        assertEquals("OT_DA_HUY", ex.getCode());
    }

    // ---------------- monthly summary ----------------

    @Test
    void monthlyApprovedOT_gopTheoHeSo() {
        when(repo.sumApprovedByMonthAndHeSo(eq(NV_ID), eq(TrangThaiDon.DA_DUYET),
                any(), any(), eq(HeSoOt.NGAY_THUONG_150))).thenReturn(new BigDecimal("10.00"));
        when(repo.sumApprovedByMonthAndHeSo(eq(NV_ID), eq(TrangThaiDon.DA_DUYET),
                any(), any(), eq(HeSoOt.NGAY_NGHI_TUAN_200))).thenReturn(new BigDecimal("5.50"));
        when(repo.sumApprovedByMonthAndHeSo(eq(NV_ID), eq(TrangThaiDon.DA_DUYET),
                any(), any(), eq(HeSoOt.NGAY_LE_300))).thenReturn(new BigDecimal("3.00"));

        DangKyOtMonthlySummary r = service.monthlyApprovedOT(NV_ID, 1, 2026);
        assertEquals(new BigDecimal("10.00"), r.getSoGioOt150());
        assertEquals(new BigDecimal("5.50"), r.getSoGioOt200());
        assertEquals(new BigDecimal("3.00"), r.getSoGioOt300());
    }

    @Test
    void monthlyApprovedOT_thangInvalid_throws() {
        assertThrows(BusinessException.class,
                () -> service.monthlyApprovedOT(NV_ID, 13, 2026));
    }

    @Test
    void monthlyApprovedOT_nullTraZero() {
        when(repo.sumApprovedByMonthAndHeSo(any(), any(), any(), any(), any())).thenReturn(null);
        DangKyOtMonthlySummary r = service.monthlyApprovedOT(NV_ID, 2, 2026);
        assertEquals(0, r.getSoGioOt150().compareTo(BigDecimal.ZERO));
        assertEquals(0, r.getSoGioOt200().compareTo(BigDecimal.ZERO));
        assertEquals(0, r.getSoGioOt300().compareTo(BigDecimal.ZERO));
    }

    @Test
    void get_notFound_throws() {
        when(repo.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(UUID.randomUUID()));
    }

    // ---------------- list ----------------

    @Test
    void list_withStatuses() {
        when(repo.findByNhanVienIdAndTrangThaiInOrderByNgayLamOtDesc(eq(NV_ID), any()))
                .thenReturn(List.of());
        assertEquals(0, service.list(NV_ID, List.of(TrangThaiDon.CHO_DUYET)).size());
    }
}