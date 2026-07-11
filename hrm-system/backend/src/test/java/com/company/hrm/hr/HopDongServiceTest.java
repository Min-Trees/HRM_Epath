package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.AddendumRequest;
import com.company.hrm.hr.dto.HopDongExpiringItem;
import com.company.hrm.hr.dto.HopDongRequest;
import com.company.hrm.hr.entity.HopDongLaoDong;
import com.company.hrm.hr.entity.HopDongLaoDong.LoaiHopDong;
import com.company.hrm.hr.entity.HopDongLaoDong.TrangThaiHopDong;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.HopDongLaoDongRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HopDongServiceTest {

    private HopDongLaoDongRepository repo;
    private NhanVienRepository nvRepo;
    private ApplicationEventPublisher events;
    private HopDongService service;

    @BeforeEach
    void setUp() {
        repo = mock(HopDongLaoDongRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        events = mock(ApplicationEventPublisher.class);
        service = new HopDongService(repo, nvRepo, events);
        when(repo.save(any(HopDongLaoDong.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private NhanVien nv(UUID id) {
        NhanVien n = new NhanVien();
        n.setNhanVienId(id);
        n.setMaNv("NV0001");
        n.setHoTen("Nguyễn Văn A");
        return n;
    }

    private HopDongRequest req(LoaiHopDong loai) {
        HopDongRequest r = new HopDongRequest();
        r.setSoHopDong("HD-001");
        r.setLoaiHopDong(loai);
        r.setNgayKy(LocalDate.of(2026, 1, 1));
        r.setNgayHieuLuc(LocalDate.of(2026, 1, 1));
        r.setNgayHetHieuLuc(LocalDate.of(2027, 1, 1));
        r.setMucLuongThoaThuan(new BigDecimal("15000000"));
        Map<String, BigDecimal> phuCap = new HashMap<>();
        phuCap.put("an_trua", new BigDecimal("1000000"));
        phuCap.put("xang_xe", new BigDecimal("500000"));
        r.setPhuCapCoDinh(phuCap);
        return r;
    }

    // ---------------- validate ----------------

    @Test
    void create_endBeforeStart_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));

        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        r.setNgayHieuLuc(LocalDate.of(2026, 6, 1));
        r.setNgayHetHieuLuc(LocalDate.of(2026, 1, 1));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(nvId, r));
        assertEquals("HOP_DONG_DATE_INVALID", ex.getCode());
    }

    @Test
    void create_indefiniteWithEndDate_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));

        HopDongRequest r = req(LoaiHopDong.KHONG_XAC_DINH_THOI_HAN);
        // ngayHetHieuLuc đã set ở helper — đây là case vi phạm
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(nvId, r));
        assertEquals("HOP_DONG_INDEFINITE_MUST_HAVE_NULL_END", ex.getCode());
    }

    @Test
    void create_definiteWithoutEndDate_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));

        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        r.setNgayHetHieuLuc(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(nvId, r));
        assertEquals("HOP_DONG_END_REQUIRED", ex.getCode());
    }

    @Test
    void create_thuViecWithoutEndDate_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));

        HopDongRequest r = req(LoaiHopDong.THU_VIEC);
        r.setNgayHetHieuLuc(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(nvId, r));
        assertEquals("HOP_DONG_END_REQUIRED", ex.getCode());
    }

    @Test
    void create_phuLucDirect_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));

        HopDongRequest r = req(LoaiHopDong.PHU_LUC);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(nvId, r));
        assertEquals("HOP_DONG_USE_ADDENDUM_API", ex.getCode());
    }

    @Test
    void create_duplicateSoHopDong_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));
        when(repo.existsBySoHopDong("HD-001")).thenReturn(true);

        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(nvId, r));
        assertEquals("HOP_DONG_SO_DUPLICATE", ex.getCode());
    }

    @Test
    void create_employeeNotFound_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.empty());

        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        assertThrows(ResourceNotFoundException.class, () -> service.create(nvId, r));
    }

    // ---------------- rule "1 HĐ chính HIEU_LUC" ----------------

    @Test
    void create_alreadyHasActiveContract_throws() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));
        HopDongLaoDong existing = new HopDongLaoDong();
        existing.setHopDongId(UUID.randomUUID());
        existing.setSoHopDong("HD-OLD");
        when(repo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(
                eq(nvId), eq(TrangThaiHopDong.HIEU_LUC), anyList()))
                .thenReturn(Optional.of(existing));

        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(nvId, r));
        assertEquals("EMPLOYEE_HAS_ACTIVE_CONTRACT", ex.getCode());
    }

    // ---------------- create ok ----------------

    @Test
    void create_xacDinhThoiHan_succeeds() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));
        when(repo.existsBySoHopDong("HD-001")).thenReturn(false);
        when(repo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(
                eq(nvId), eq(TrangThaiHopDong.HIEU_LUC), anyList()))
                .thenReturn(Optional.empty());

        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        var resp = service.create(nvId, r);

        ArgumentCaptor<HopDongLaoDong> captor = ArgumentCaptor.forClass(HopDongLaoDong.class);
        verify(repo).save(captor.capture());
        HopDongLaoDong saved = captor.getValue();

        assertEquals(nvId, saved.getNhanVienId());
        assertEquals("HD-001", saved.getSoHopDong());
        assertEquals(LoaiHopDong.XAC_DINH_THOI_HAN, saved.getLoaiHopDong());
        assertNull(saved.getHopDongGocId());
        assertEquals(LocalDate.of(2027, 1, 1), saved.getNgayHetHieuLuc());
        assertEquals(TrangThaiHopDong.HIEU_LUC, saved.getTrangThai());
        assertEquals(new BigDecimal("15000000"), saved.getMucLuongThoaThuan());
        assertEquals(new BigDecimal("1000000"), saved.getPhuCapCoDinh().get("an_trua"));

        // Event được phát
        verify(events).publishEvent(any(HopDongService.ContractSignedEvent.class));
    }

    @Test
    void create_khongXacDinhThoiHan_succeeds_nullEndDate() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId)));
        when(repo.existsBySoHopDong("HD-002")).thenReturn(false);
        when(repo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(
                eq(nvId), eq(TrangThaiHopDong.HIEU_LUC), anyList()))
                .thenReturn(Optional.empty());

        HopDongRequest r = req(LoaiHopDong.KHONG_XAC_DINH_THOI_HAN);
        r.setSoHopDong("HD-002");
        r.setNgayHetHieuLuc(null);

        var resp = service.create(nvId, r);
        ArgumentCaptor<HopDongLaoDong> captor = ArgumentCaptor.forClass(HopDongLaoDong.class);
        verify(repo).save(captor.capture());
        assertNull(captor.getValue().getNgayHetHieuLuc());
        assertEquals(LoaiHopDong.KHONG_XAC_DINH_THOI_HAN, captor.getValue().getLoaiHopDong());
    }

    // ---------------- addendum ----------------

    @Test
    void addendum_ok_setsHopDongGocIdAndInheritsLuong() {
        UUID gocId = UUID.randomUUID();
        UUID nvId = UUID.randomUUID();
        HopDongLaoDong goc = new HopDongLaoDong();
        goc.setHopDongId(gocId);
        goc.setNhanVienId(nvId);
        goc.setLoaiHopDong(LoaiHopDong.XAC_DINH_THOI_HAN);
        goc.setMucLuongThoaThuan(new BigDecimal("15000000"));
        goc.setPhuCapCoDinh(Map.of("an_trua", new BigDecimal("1000000")));
        when(repo.findById(gocId)).thenReturn(Optional.of(goc));
        when(repo.existsBySoHopDong("PL-001")).thenReturn(false);

        AddendumRequest req = new AddendumRequest();
        req.setSoHopDong("PL-001");
        req.setNgayKy(LocalDate.of(2026, 6, 1));
        req.setNgayHieuLuc(LocalDate.of(2026, 6, 1));
        req.setNgayHetHieuLucMoi(LocalDate.of(2027, 6, 1));
        req.setFileDinhKemUrl("https://docs/pl-001.pdf");

        service.addendum(gocId, req);

        ArgumentCaptor<HopDongLaoDong> captor = ArgumentCaptor.forClass(HopDongLaoDong.class);
        verify(repo).save(captor.capture());
        HopDongLaoDong saved = captor.getValue();
        assertEquals(LoaiHopDong.PHU_LUC, saved.getLoaiHopDong());
        assertEquals(gocId, saved.getHopDongGocId());
        assertEquals(nvId, saved.getNhanVienId());
        assertEquals(LocalDate.of(2027, 6, 1), saved.getNgayHetHieuLuc());
        // Kế thừa lương + phụ cấp từ HĐ gốc
        assertEquals(new BigDecimal("15000000"), saved.getMucLuongThoaThuan());
        assertEquals(new BigDecimal("1000000"), saved.getPhuCapCoDinh().get("an_trua"));
    }

    @Test
    void addendum_ofAddendum_throws() {
        UUID gocId = UUID.randomUUID();
        HopDongLaoDong pl = new HopDongLaoDong();
        pl.setHopDongId(gocId);
        pl.setLoaiHopDong(LoaiHopDong.PHU_LUC);
        when(repo.findById(gocId)).thenReturn(Optional.of(pl));

        AddendumRequest req = new AddendumRequest();
        req.setSoHopDong("PL-002");
        req.setNgayKy(LocalDate.of(2026, 6, 1));
        req.setNgayHieuLuc(LocalDate.of(2026, 6, 1));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.addendum(gocId, req));
        assertEquals("HOP_DONG_ADDENDUM_OF_ADDENDUM", ex.getCode());
    }

    @Test
    void addendum_endBeforeStart_throws() {
        UUID gocId = UUID.randomUUID();
        HopDongLaoDong goc = new HopDongLaoDong();
        goc.setHopDongId(gocId);
        goc.setNhanVienId(UUID.randomUUID());
        goc.setLoaiHopDong(LoaiHopDong.XAC_DINH_THOI_HAN);
        when(repo.findById(gocId)).thenReturn(Optional.of(goc));

        AddendumRequest req = new AddendumRequest();
        req.setSoHopDong("PL-003");
        req.setNgayKy(LocalDate.of(2026, 6, 1));
        req.setNgayHieuLuc(LocalDate.of(2026, 6, 1));
        req.setNgayHetHieuLucMoi(LocalDate.of(2026, 1, 1));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.addendum(gocId, req));
        assertEquals("HOP_DONG_DATE_INVALID", ex.getCode());
    }

    // ---------------- update ----------------

    @Test
    void update_activeContract_onlyFileAllowed() {
        UUID id = UUID.randomUUID();
        HopDongLaoDong e = new HopDongLaoDong();
        e.setHopDongId(id);
        e.setSoHopDong("HD-001");
        e.setTrangThai(TrangThaiHopDong.HIEU_LUC);
        e.setNgayHieuLuc(LocalDate.of(2026, 1, 1));
        when(repo.findById(id)).thenReturn(Optional.of(e));

        // Đổi lương khi đang HIEU_LUC → cấm
        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.update(id, r));
        assertEquals("HOP_DONG_ACTIVE_UPDATE_FORBIDDEN", ex.getCode());
    }

    @Test
    void update_activeContract_updateFileOnly_ok() {
        UUID id = UUID.randomUUID();
        HopDongLaoDong e = new HopDongLaoDong();
        e.setHopDongId(id);
        e.setSoHopDong("HD-001");
        e.setTrangThai(TrangThaiHopDong.HIEU_LUC);
        e.setNgayHieuLuc(LocalDate.of(2026, 1, 1));
        e.setFileDinhKemUrl("https://old");
        when(repo.findById(id)).thenReturn(Optional.of(e));

        HopDongRequest r = req(LoaiHopDong.XAC_DINH_THOI_HAN);
        r.setFileDinhKemUrl("https://new");

        service.update(id, r);
        assertEquals("https://new", e.getFileDinhKemUrl());
    }

    // ---------------- expiring ----------------

    @Test
    void expiring_filtersCorrectRange_andIncludesEmployeeInfo() {
        LocalDate today = LocalDate.now();
        HopDongLaoDong h1 = new HopDongLaoDong();
        h1.setHopDongId(UUID.randomUUID());
        h1.setNhanVienId(UUID.randomUUID());
        h1.setSoHopDong("HD-A");
        h1.setNgayHetHieuLuc(today.plusDays(35));
        when(repo.findByTrangThaiAndNgayHetHieuLucBetween(
                eq(TrangThaiHopDong.HIEU_LUC), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(h1));
        when(nvRepo.findById(h1.getNhanVienId())).thenReturn(Optional.of(nv(h1.getNhanVienId())));

        List<HopDongExpiringItem> result = service.expiring(30, 45);

        assertEquals(1, result.size());
        HopDongExpiringItem item = result.get(0);
        assertEquals("HD-A", item.getSoHopDong());
        assertEquals(today.plusDays(35), item.getNgayHetHieuLuc());
        assertEquals(35, item.getSoNgayConLai());
        assertEquals("NV0001", item.getMaNv());
        assertEquals("Nguyễn Văn A", item.getHoTen());
    }

    @Test
    void expiring_invalidRange_throws() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.expiring(45, 30));
        assertEquals("HOP_DONG_EXPIRY_RANGE_INVALID", ex.getCode());

        BusinessException ex2 = assertThrows(BusinessException.class, () -> service.expiring(-1, 30));
        assertEquals("HOP_DONG_EXPIRY_RANGE_INVALID", ex2.getCode());
    }

    @Test
    void expiring_emptyRange_returnsEmpty() {
        when(repo.findByTrangThaiAndNgayHetHieuLucBetween(
                eq(TrangThaiHopDong.HIEU_LUC), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        List<HopDongExpiringItem> result = service.expiring(30, 45);
        assertTrue(result.isEmpty());
    }
}
