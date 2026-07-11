package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.NghiPhepRequest;
import com.company.hrm.attendance.dto.NghiPhepResponse;
import com.company.hrm.attendance.dto.QuyPhepNamResponse;
import com.company.hrm.attendance.entity.LoaiNghiPhep;
import com.company.hrm.attendance.entity.NghiPhep;
import com.company.hrm.attendance.entity.PhanCa;
import com.company.hrm.attendance.entity.TrangThaiDon;
import com.company.hrm.attendance.repository.NghiPhepRepository;
import com.company.hrm.attendance.repository.PhanCaRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NghiPhepServiceTest {

    private NghiPhepRepository repo;
    private NhanVienRepository nvRepo;
    private PhanCaRepository phanCaRepo;
    private QuyPhepService quyPhepService;
    private NghiPhepService service;

    private final UUID NV_ID = UUID.randomUUID();
    private final UUID LEAVE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repo = mock(NghiPhepRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        phanCaRepo = mock(PhanCaRepository.class);
        quyPhepService = mock(QuyPhepService.class);
        service = new NghiPhepService(repo, nvRepo, phanCaRepo, quyPhepService);

        when(repo.save(any(NghiPhep.class))).thenAnswer(inv -> inv.getArgument(0));

        NhanVien nv = new NhanVien();
        nv.setNhanVienId(NV_ID);
        nv.setMaNv("NV0001");
        nv.setTrangThai(TrangThaiNv.CHINH_THUC);
        when(nvRepo.findById(NV_ID)).thenReturn(Optional.of(nv));
    }

    private NhanVien nv(UUID id, TrangThaiNv st) {
        NhanVien n = new NhanVien();
        n.setNhanVienId(id);
        n.setTrangThai(st);
        return n;
    }

    private NghiPhepRequest req(LocalDate tu, LocalDate den, LoaiNghiPhep loai) {
        NghiPhepRequest r = new NghiPhepRequest();
        r.setNhanVienId(NV_ID);
        r.setTuNgay(tu);
        r.setDenNgay(den);
        r.setLoaiNghiPhep(loai);
        r.setLyDo("test");
        return r;
    }

    private PhanCa phanCa(LocalDate ngay) {
        PhanCa p = new PhanCa();
        p.setPhanCaId(UUID.randomUUID());
        p.setNhanVienId(NV_ID);
        p.setCaId(UUID.randomUUID());
        p.setNgayApDung(ngay);
        return p;
    }

    // ---------------- create ----------------

    @Test
    void create_phepNam_3ngayCoCa_thanhCong() {
        LocalDate tu = LocalDate.now().plusDays(7);
        LocalDate den = tu.plusDays(4); // T2→T6 = 5 ngày nhưng mock chỉ có 3 ca
        when(phanCaRepo.findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(NV_ID, tu, den))
                .thenReturn(List.of(phanCa(tu), phanCa(tu.plusDays(1)), phanCa(tu.plusDays(2))));

        NghiPhepResponse resp = service.create(req(tu, den, LoaiNghiPhep.PHEP_NAM));
        assertEquals(new BigDecimal("3.0"), resp.getSoNgayNghi());
        assertEquals(TrangThaiDon.CHO_DUYET, resp.getTrangThai());
        assertEquals(NV_ID, resp.getNhanVienId());
    }

    @Test
    void create_khongCoCa_trongKhoang_throws() {
        LocalDate tu = LocalDate.now().plusDays(7);
        LocalDate den = tu.plusDays(2);
        when(phanCaRepo.findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(NV_ID, tu, den))
                .thenReturn(List.of());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req(tu, den, LoaiNghiPhep.PHEP_NAM)));
        assertEquals("NGHI_PHEP_NO_SHIFT", ex.getCode());
    }

    @Test
    void create_om_khongFileDinhKem_throws() {
        LocalDate tu = LocalDate.now().plusDays(7);
        LocalDate den = tu.plusDays(1);
        when(phanCaRepo.findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(NV_ID, tu, den))
                .thenReturn(List.of(phanCa(tu)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req(tu, den, LoaiNghiPhep.OM)));
        assertEquals("NGHI_PHEP_REQUIRED_ATTACHMENT", ex.getCode());
    }

    @Test
    void create_om_coFileDinhKem_thanhCong() {
        LocalDate tu = LocalDate.now().plusDays(7);
        LocalDate den = tu.plusDays(1);
        when(phanCaRepo.findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(NV_ID, tu, den))
                .thenReturn(List.of(phanCa(tu)));
        NghiPhepRequest r = req(tu, den, LoaiNghiPhep.OM);
        r.setFileDinhKemUrl("https://example.com/giay-kham.pdf");

        NghiPhepResponse resp = service.create(r);
        assertEquals("https://example.com/giay-kham.pdf", resp.getFileDinhKemUrl());
    }

    @Test
    void create_nhanVienNghiViec_throws() {
        UUID badId = UUID.randomUUID();
        when(nvRepo.findById(badId)).thenReturn(Optional.of(nv(badId, TrangThaiNv.DA_NGHI_VIEC)));
        NghiPhepRequest r = req(LocalDate.now().plusDays(7), LocalDate.now().plusDays(8), LoaiNghiPhep.PHEP_NAM);
        r.setNhanVienId(badId);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("EMPLOYEE_NOT_WORKING", ex.getCode());
    }

    @Test
    void create_quyPhepKhongDu_throws() {
        LocalDate tu = LocalDate.now().plusDays(7);
        LocalDate den = tu.plusDays(4);
        when(phanCaRepo.findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(NV_ID, tu, den))
                .thenReturn(List.of(phanCa(tu), phanCa(tu.plusDays(1)), phanCa(tu.plusDays(2)),
                        phanCa(tu.plusDays(3)), phanCa(tu.plusDays(4))));
        QuyPhepNamResponse bal = new QuyPhepNamResponse(null, NV_ID, tu.getYear(),
                new BigDecimal("12.0"), new BigDecimal("11.0"), new BigDecimal("1.0"));
        when(quyPhepService.getBalance(eq(NV_ID), anyInt())).thenReturn(bal);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req(tu, den, LoaiNghiPhep.PHEP_NAM)));
        assertEquals("QUY_PHEP_NOT_ENOUGH_PRECHECK", ex.getCode());
    }

    @Test
    void create_denNgayTruocTuNgay_throws() {
        LocalDate tu = LocalDate.now().plusDays(7);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req(tu, tu.minusDays(1), LoaiNghiPhep.PHEP_NAM)));
        assertEquals("NGHI_PHEP_NGAY_INVALID", ex.getCode());
    }

    @Test
    void create_trungDonHieuLuc_throws() {
        LocalDate tu = LocalDate.now().plusDays(7);
        LocalDate den = tu.plusDays(2);
        when(phanCaRepo.findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(NV_ID, tu, den))
                .thenReturn(List.of(phanCa(tu), phanCa(tu.plusDays(1)), phanCa(tu.plusDays(2))));
        when(repo.existsByNhanVienIdAndTrangThaiInAndTuNgayLessThanEqualAndDenNgayGreaterThanEqual(
                eq(NV_ID), any(), eq(den), eq(tu))).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req(tu, den, LoaiNghiPhep.PHEP_NAM)));
        assertEquals("NGHI_PHEP_TRUNG", ex.getCode());
    }

    // ---------------- duyệt 2 cấp ----------------

    private NghiPhep leaveWith(TrangThaiDon st, LoaiNghiPhep loai, BigDecimal soNgay) {
        NghiPhep e = new NghiPhep();
        e.setNghiPhepId(LEAVE_ID);
        e.setNhanVienId(NV_ID);
        e.setLoaiNghiPhep(loai);
        e.setTuNgay(LocalDate.now().plusDays(7));
        e.setDenNgay(LocalDate.now().plusDays(9));
        e.setSoNgayNghi(soNgay);
        e.setTrangThai(st);
        return e;
    }

    @Test
    void approveCap1_choDuyet_duyetCap1() {
        NghiPhep e = leaveWith(TrangThaiDon.CHO_DUYET, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        UUID approver = UUID.randomUUID();
        NghiPhepResponse resp = service.approveCap1(LEAVE_ID, approver, true, "ok");
        assertEquals(TrangThaiDon.DUYET_CAP_1, resp.getTrangThai());
        assertEquals(approver, resp.getDuyetCap1Boi());
        assertNotNull(resp.getDuyetCap1Luc());
        // CHƯA trừ quỹ ở cấp 1
        verify(quyPhepService, never()).congDaDung(any(), anyInt(), any());
    }

    @Test
    void approveCap2_sauCap1_phepNam_truQuy() {
        UUID cap1 = UUID.randomUUID();
        UUID cap2 = UUID.randomUUID();
        NghiPhep e = leaveWith(TrangThaiDon.DUYET_CAP_1, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        e.setDuyetCap1Boi(cap1);
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        NghiPhepResponse resp = service.approveCap2(LEAVE_ID, cap2, true, "ok");
        assertEquals(TrangThaiDon.DA_DUYET, resp.getTrangThai());
        verify(quyPhepService).congDaDung(eq(NV_ID), anyInt(), eq(new BigDecimal("3.0")));
    }

    @Test
    void approveCap2_viecRiengCoLuong_khongTruQuy() {
        UUID cap1 = UUID.randomUUID();
        UUID cap2 = UUID.randomUUID();
        NghiPhep e = leaveWith(TrangThaiDon.DUYET_CAP_1,
                LoaiNghiPhep.VIEC_RIENG_CO_LUONG, new BigDecimal("2.0"));
        e.setDuyetCap1Boi(cap1);
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        NghiPhepResponse resp = service.approveCap2(LEAVE_ID, cap2, true, "ok");
        assertEquals(TrangThaiDon.DA_DUYET, resp.getTrangThai());
        verify(quyPhepService, never()).congDaDung(any(), anyInt(), any());
    }

    @Test
    void approveCap2_cungNguoi_throws() {
        UUID same = UUID.randomUUID();
        NghiPhep e = leaveWith(TrangThaiDon.DUYET_CAP_1, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        e.setDuyetCap1Boi(same);
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approveCap2(LEAVE_ID, same, true, null));
        assertEquals("APPROVER_DUPLICATE", ex.getCode());
    }

    @Test
    void approveCap1_tuChoi_khongTruQuy() {
        NghiPhep e = leaveWith(TrangThaiDon.CHO_DUYET, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        NghiPhepResponse resp = service.approveCap1(LEAVE_ID, UUID.randomUUID(), false, "nghỉ nhiều quá");
        assertEquals(TrangThaiDon.TU_CHOI, resp.getTrangThai());
        verify(quyPhepService, never()).congDaDung(any(), anyInt(), any());
    }

    @Test
    void approveCap2_saiTrangThai_throws() {
        NghiPhep e = leaveWith(TrangThaiDon.CHO_DUYET, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approveCap2(LEAVE_ID, UUID.randomUUID(), true, null));
        assertEquals("NGHI_PHEP_INVALID_STATE", ex.getCode());
    }

    // ---------------- cancel ----------------

    @Test
    void cancel_choDuyet_thanhCong_khongHoan() {
        NghiPhep e = leaveWith(TrangThaiDon.CHO_DUYET, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        NghiPhepResponse resp = service.cancel(LEAVE_ID);
        assertEquals(TrangThaiDon.HUY, resp.getTrangThai());
        verify(quyPhepService, never()).hoanDaDung(any(), anyInt(), any());
    }

    @Test
    void cancel_daDuyet_phepNam_hoanQuy() {
        NghiPhep e = leaveWith(TrangThaiDon.DA_DUYET, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        NghiPhepResponse resp = service.cancel(LEAVE_ID);
        assertEquals(TrangThaiDon.HUY, resp.getTrangThai());
        verify(quyPhepService).hoanDaDung(eq(NV_ID), anyInt(), eq(new BigDecimal("3.0")));
    }

    @Test
    void cancel_daDuyet_viecRieng_khongHoanQuy() {
        NghiPhep e = leaveWith(TrangThaiDon.DA_DUYET,
                LoaiNghiPhep.VIEC_RIENG_CO_LUONG, new BigDecimal("2.0"));
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        service.cancel(LEAVE_ID);
        verify(quyPhepService, never()).hoanDaDung(any(), anyInt(), any());
    }

    @Test
    void cancel_daHuy_throws() {
        NghiPhep e = leaveWith(TrangThaiDon.HUY, LoaiNghiPhep.PHEP_NAM, new BigDecimal("3.0"));
        when(repo.findById(LEAVE_ID)).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.cancel(LEAVE_ID));
        assertEquals("NGHI_PHEP_DA_HUY", ex.getCode());
    }

    // ---------------- list ----------------

    @Test
    void list_withStatuses() {
        when(repo.findByNhanVienIdAndTrangThaiInOrderByTuNgayDesc(eq(NV_ID), any()))
                .thenReturn(List.of());
        List<NghiPhepResponse> r = service.list(NV_ID, List.of(TrangThaiDon.CHO_DUYET));
        assertEquals(0, r.size());
    }

    @Test
    void list_notFound_throws() {
        when(repo.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(UUID.randomUUID()));
    }

    // ----- helper for anyInt -----
    private static int anyInt() { return org.mockito.ArgumentMatchers.anyInt(); }
}