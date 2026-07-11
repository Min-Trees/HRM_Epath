package com.company.hrm.attendance.service;

import com.company.hrm.attendance.config.AttendanceProperties;
import com.company.hrm.attendance.dto.BatchTimeLogResult;
import com.company.hrm.attendance.dto.MonthlySummary;
import com.company.hrm.attendance.dto.TimeLogRequest;
import com.company.hrm.attendance.dto.TimeLogResponse;
import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.entity.CaLamViec.LoaiCa;
import com.company.hrm.attendance.entity.ChamCongChiTiet;
import com.company.hrm.attendance.entity.LoaiNgoaiLe;
import com.company.hrm.attendance.entity.NguonChamCong;
import com.company.hrm.attendance.entity.PhanCa;
import com.company.hrm.attendance.entity.TrangThaiDon;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChamCongServiceTest {

    private ChamCongChiTietRepository repo;
    private PhanCaRepository phanCaRepo;
    private NhanVienRepository nvRepo;
    private PhanCaService phanCaService;
    private AttendanceProperties props;
    private ChamCongService service;

    private final UUID NV_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID PHAN_CA_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private final UUID CA_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    @BeforeEach
    void setUp() {
        repo = mock(ChamCongChiTietRepository.class);
        phanCaRepo = mock(PhanCaRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        phanCaService = mock(PhanCaService.class);
        props = new AttendanceProperties();
        // default thresholds: 5 phút
        service = new ChamCongService(repo, phanCaRepo, nvRepo, phanCaService, props);
        when(repo.save(any(ChamCongChiTiet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(nvRepo.existsById(NV_ID)).thenReturn(true);
    }

    private NhanVien nv() {
        NhanVien n = new NhanVien();
        n.setNhanVienId(NV_ID);
        n.setMaNv("NV0001");
        n.setTrangThai(TrangThaiNv.CHINH_THUC);
        return n;
    }

    private CaLamViec caHanhChinh() {
        CaLamViec c = new CaLamViec();
        c.setCaId(CA_ID);
        c.setMaCa("CA-HC");
        c.setTenCa("Hành chính");
        c.setLoaiCa(LoaiCa.HANH_CHINH);
        c.setGioBatDau(LocalTime.of(8, 0));
        c.setGioKetThuc(LocalTime.of(17, 0));
        c.setSoGioChuan(new BigDecimal("8.00"));
        c.setQuaNgay(false);
        c.setActive(true);
        return c;
    }

    private CaLamViec caQuaDem() {
        CaLamViec c = new CaLamViec();
        c.setCaId(CA_ID);
        c.setMaCa("CA-KIP");
        c.setTenCa("Ca kíp đêm");
        c.setLoaiCa(LoaiCa.CA_KIP);
        c.setGioBatDau(LocalTime.of(22, 0));
        c.setGioKetThuc(LocalTime.of(6, 0));
        c.setSoGioChuan(new BigDecimal("8.00"));
        c.setQuaNgay(true);
        c.setActive(true);
        return c;
    }

    private PhanCa phanCa(UUID caId, LocalDate ngay) {
        PhanCa p = new PhanCa();
        p.setPhanCaId(PHAN_CA_ID);
        p.setNhanVienId(NV_ID);
        p.setCaId(caId);
        p.setNgayApDung(ngay);
        return p;
    }

    private TimeLogRequest req(LocalDate ngay, OffsetDateTime vao, OffsetDateTime ra, NguonChamCong nguon) {
        TimeLogRequest r = new TimeLogRequest();
        r.setNhanVienId(NV_ID);
        r.setNgayChamCong(ngay);
        r.setGioVao(vao);
        r.setGioRa(ra);
        r.setNguon(nguon);
        return r;
    }

    private OffsetDateTime at(LocalDate d, int h, int m) {
        return OffsetDateTime.of(d, LocalTime.of(h, m), ZoneOffset.UTC);
    }

    // ---------------- record / exception detection ----------------

    @Test
    void record_noShift_lamNgoaiCa() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(null);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay)).thenReturn(Optional.empty());

        TimeLogResponse resp = service.record(req(ngay,
                at(ngay, 8, 0), at(ngay, 17, 0), NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        ChamCongChiTiet saved = cap.getValue();
        assertEquals(LoaiNgoaiLe.LAM_NGOAI_CA, saved.getLoaiNgoaiLe());
        assertTrue(saved.isCanGiaiTrinh());
        assertEquals(new BigDecimal("9.00"), saved.getSoGioCong());
        assertEquals(NguonChamCong.VAN_TAY, saved.getNguon());
        assertEquals(NV_ID, resp.getNhanVienId());
    }

    @Test
    void record_dungCa_khongNgoaiLe() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caHanhChinh();
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        service.record(req(ngay, at(ngay, 8, 0), at(ngay, 17, 0), NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(LoaiNgoaiLe.KHONG_NGOAI_LE, cap.getValue().getLoaiNgoaiLe());
        assertFalse(cap.getValue().isCanGiaiTrinh());
        assertEquals(PHAN_CA_ID, cap.getValue().getPhanCaId());
    }

    @Test
    void record_vaoTre_diTre() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caHanhChinh();
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        // vào lúc 8:10 = trễ 10 phút, ngưỡng 5
        service.record(req(ngay, at(ngay, 8, 10), at(ngay, 17, 0), NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(LoaiNgoaiLe.DI_TRE, cap.getValue().getLoaiNgoaiLe());
        assertTrue(cap.getValue().isCanGiaiTrinh());
    }

    @Test
    void record_vaoDungNguong_khongNgoaiLe() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caHanhChinh();
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        // vào lúc 8:05 = trễ 5 phút, ngưỡng 5 → KHÔNG ngoại lệ (chỉ > mới tính)
        service.record(req(ngay, at(ngay, 8, 5), at(ngay, 17, 0), NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(LoaiNgoaiLe.KHONG_NGOAI_LE, cap.getValue().getLoaiNgoaiLe());
        assertFalse(cap.getValue().isCanGiaiTrinh());
    }

    @Test
    void record_raSom_veSom() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caHanhChinh();
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        // ra lúc 16:50 = sớm 10 phút
        service.record(req(ngay, at(ngay, 8, 0), at(ngay, 16, 50), NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(LoaiNgoaiLe.VE_SOM, cap.getValue().getLoaiNgoaiLe());
        assertTrue(cap.getValue().isCanGiaiTrinh());
    }

    @Test
    void record_thieuGioRa_thieuCong() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caHanhChinh();
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        // chỉ log vào, không có giờ ra
        service.record(req(ngay, at(ngay, 8, 0), null, NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(LoaiNgoaiLe.THIEU_CONG, cap.getValue().getLoaiNgoaiLe());
        assertNull(cap.getValue().getSoGioCong()); // không tính khi thiếu
    }

    @Test
    void record_quaDem_raSomVanDungCa() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caQuaDem(); // 22:00 -> 06:00 (qua đêm)
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        // ra lúc 05:55 hôm sau = sớm 5 phút so với 06:00 ngày kế → KHÔNG ngoại lệ (chỉ > mới tính)
        service.record(req(ngay,
                at(ngay, 22, 0),
                at(ngay.plusDays(1), 5, 55),
                NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(LoaiNgoaiLe.KHONG_NGOAI_LE, cap.getValue().getLoaiNgoaiLe());
    }

    @Test
    void record_thuCong_choDuyet() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caHanhChinh();
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        service.record(req(ngay, at(ngay, 8, 0), at(ngay, 17, 0), NguonChamCong.THU_CONG));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(TrangThaiDon.CHO_DUYET, cap.getValue().getGiaiTrinhTrangThai());
    }

    @Test
    void record_gpsWithoutCoords_throws() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);

        TimeLogRequest r = req(ngay, at(ngay, 8, 0), at(ngay, 17, 0), NguonChamCong.GPS_MOBILE);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.record(r));
        assertEquals("CHAM_CONG_GPS_REQUIRED", ex.getCode());
    }

    @Test
    void record_duplicate_throws() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.record(req(ngay, at(ngay, 8, 0), at(ngay, 17, 0), NguonChamCong.VAN_TAY)));
        assertEquals("CHAM_CONG_DUPLICATE", ex.getCode());
        verify(repo, never()).save(any());
    }

    @Test
    void record_employeeNotFound_throws() {
        UUID badId = UUID.randomUUID();
        when(nvRepo.existsById(badId)).thenReturn(false);
        TimeLogRequest r = req(LocalDate.of(2026, 1, 15),
                at(LocalDate.of(2026, 1, 15), 8, 0),
                at(LocalDate.of(2026, 1, 15), 17, 0),
                NguonChamCong.VAN_TAY);
        r.setNhanVienId(badId);
        assertThrows(ResourceNotFoundException.class, () -> service.record(r));
    }

    @Test
    void record_gioRaTruocGioVao_throws() {
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.record(req(ngay, at(ngay, 17, 0), at(ngay, 8, 0), NguonChamCong.VAN_TAY)));
        assertEquals("CHAM_CONG_GIO_RA_TRUOC_GIO_VAO", ex.getCode());
    }

    // ---------------- batch ----------------

    @Test
    void recordBatch_skipsDuplicates() {
        LocalDate ngay1 = LocalDate.of(2026, 1, 15);
        LocalDate ngay2 = LocalDate.of(2026, 1, 16);
        // ngày 1 đã có
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay1)).thenReturn(true);
        // ngày 2 chưa
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay2)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay2)).thenReturn(null);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay2)).thenReturn(Optional.empty());

        BatchTimeLogResult result = service.recordBatch(List.of(
                req(ngay1, at(ngay1, 8, 0), at(ngay1, 17, 0), NguonChamCong.VAN_TAY),
                req(ngay2, at(ngay2, 8, 0), at(ngay2, 17, 0), NguonChamCong.VAN_TAY)
        ));

        assertEquals(2, result.getTotal());
        assertEquals(1, result.getCreated());
        assertEquals(1, result.getSkipped());
    }

    // ---------------- explanation / approval ----------------

    @Test
    void submitExplanation_canGiaiTrinh_succeeds() {
        ChamCongChiTiet e = new ChamCongChiTiet();
        e.setChamCongId(UUID.randomUUID());
        e.setNhanVienId(NV_ID);
        e.setNgayChamCong(LocalDate.of(2026, 1, 15));
        e.setLoaiNgoaiLe(LoaiNgoaiLe.DI_TRE);
        e.setCanGiaiTrinh(true);
        when(repo.findById(e.getChamCongId())).thenReturn(Optional.of(e));

        TimeLogResponse resp = service.submitExplanation(e.getChamCongId(), "Kẹt xe");
        assertEquals("Kẹt xe", resp.getGiaiTrinhNoiDung());
        assertEquals(TrangThaiDon.CHO_DUYET, resp.getGiaiTrinhTrangThai());
        assertNull(resp.getDuyetBoi());
    }

    @Test
    void submitExplanation_khongCanGiaiTrinh_throws() {
        ChamCongChiTiet e = new ChamCongChiTiet();
        e.setChamCongId(UUID.randomUUID());
        e.setCanGiaiTrinh(false);
        when(repo.findById(e.getChamCongId())).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.submitExplanation(e.getChamCongId(), "test"));
        assertEquals("CHAM_CONG_KHONG_CAN_GIAI_TRINH", ex.getCode());
    }

    @Test
    void submitExplanation_daDuyet_throws() {
        ChamCongChiTiet e = new ChamCongChiTiet();
        e.setChamCongId(UUID.randomUUID());
        e.setCanGiaiTrinh(true);
        e.setGiaiTrinhTrangThai(TrangThaiDon.DA_DUYET);
        when(repo.findById(e.getChamCongId())).thenReturn(Optional.of(e));

        assertThrows(BusinessException.class,
                () -> service.submitExplanation(e.getChamCongId(), "test"));
    }

    @Test
    void approve_choDuyet_duyet() {
        ChamCongChiTiet e = new ChamCongChiTiet();
        e.setChamCongId(UUID.randomUUID());
        e.setCanGiaiTrinh(true);
        e.setGiaiTrinhTrangThai(TrangThaiDon.CHO_DUYET);
        e.setGiaiTrinhNoiDung("Kẹt xe");
        when(repo.findById(e.getChamCongId())).thenReturn(Optional.of(e));

        UUID approver = UUID.randomUUID();
        TimeLogResponse resp = service.approve(e.getChamCongId(), true, approver, "OK");
        assertEquals(TrangThaiDon.DA_DUYET, resp.getGiaiTrinhTrangThai());
        assertEquals(approver, resp.getDuyetBoi());
        assertNotNull(resp.getDuyetLuc());
        assertTrue(resp.getGiaiTrinhNoiDung().contains("Kẹt xe"));
        assertTrue(resp.getGiaiTrinhNoiDung().contains("[Đã duyệt]"));
    }

    @Test
    void approve_choDuyet_tuChoi() {
        ChamCongChiTiet e = new ChamCongChiTiet();
        e.setChamCongId(UUID.randomUUID());
        e.setGiaiTrinhTrangThai(TrangThaiDon.CHO_DUYET);
        when(repo.findById(e.getChamCongId())).thenReturn(Optional.of(e));

        TimeLogResponse resp = service.approve(e.getChamCongId(), false, UUID.randomUUID(), null);
        assertEquals(TrangThaiDon.TU_CHOI, resp.getGiaiTrinhTrangThai());
    }

    @Test
    void approve_saiTrangThai_throws() {
        ChamCongChiTiet e = new ChamCongChiTiet();
        e.setChamCongId(UUID.randomUUID());
        e.setGiaiTrinhTrangThai(TrangThaiDon.HUY);
        when(repo.findById(e.getChamCongId())).thenReturn(Optional.of(e));

        assertThrows(BusinessException.class,
                () -> service.approve(e.getChamCongId(), true, UUID.randomUUID(), null));
    }

    // ---------------- summary ----------------

    @Test
    void summary_monthly() {
        UUID empId = UUID.randomUUID();
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        ChamCongChiTiet log1 = new ChamCongChiTiet();
        log1.setNhanVienId(empId);
        log1.setNgayChamCong(LocalDate.of(2026, 1, 5));
        log1.setLoaiNgoaiLe(LoaiNgoaiLe.KHONG_NGOAI_LE);
        log1.setSoGioCong(new BigDecimal("8.00"));

        ChamCongChiTiet log2 = new ChamCongChiTiet();
        log2.setNhanVienId(empId);
        log2.setNgayChamCong(LocalDate.of(2026, 1, 15));
        log2.setLoaiNgoaiLe(LoaiNgoaiLe.DI_TRE);
        log2.setSoGioCong(new BigDecimal("7.50"));

        ChamCongChiTiet log3 = new ChamCongChiTiet();
        log3.setNhanVienId(empId);
        log3.setNgayChamCong(LocalDate.of(2026, 1, 20));
        log3.setLoaiNgoaiLe(LoaiNgoaiLe.LAM_NGOAI_CA);
        log3.setSoGioCong(new BigDecimal("9.00"));

        when(nvRepo.existsById(empId)).thenReturn(true);
        when(repo.findByNhanVienIdAndNgayChamCongBetweenOrderByNgayChamCongAsc(empId, from, to))
                .thenReturn(List.of(log1, log2, log3));

        MonthlySummary s = service.summary(empId, 1, 2026);
        assertEquals(3, s.getSoNgayCong());
        assertEquals(2, s.getSoNgayNgoaiLe());
        assertEquals(31 - 3, s.getSoNgayNghi());
        assertEquals(0, new BigDecimal("24.50").compareTo(s.getTongGioCong()));
        assertEquals(3, s.getChiTiet().size());
    }

    @Test
    void summary_thangKhongHopLe_throws() {
        assertThrows(BusinessException.class,
                () -> service.summary(UUID.randomUUID(), 13, 2026));
    }

    @Test
    void summary_nhanVienNotFound_throws() {
        UUID badId = UUID.randomUUID();
        when(nvRepo.existsById(badId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> service.summary(badId, 1, 2026));
    }

    // ---------------- custom threshold ----------------

    @Test
    void customThreshold_diTre_tinhLai() {
        props.setNguongDiTrePhut(15); // nới lên 15
        LocalDate ngay = LocalDate.of(2026, 1, 15);
        CaLamViec ca = caHanhChinh();
        when(repo.existsByNhanVienIdAndNgayChamCong(NV_ID, ngay)).thenReturn(false);
        when(phanCaService.getStandardShift(NV_ID, ngay)).thenReturn(ca);
        when(phanCaRepo.findByNhanVienIdAndNgayApDung(NV_ID, ngay))
                .thenReturn(Optional.of(phanCa(ca.getCaId(), ngay)));

        // vào trễ 10 phút, với ngưỡng 15 → không phải ngoại lệ
        service.record(req(ngay, at(ngay, 8, 10), at(ngay, 17, 0), NguonChamCong.VAN_TAY));

        ArgumentCaptor<ChamCongChiTiet> cap = ArgumentCaptor.forClass(ChamCongChiTiet.class);
        verify(repo).save(cap.capture());
        assertEquals(LoaiNgoaiLe.KHONG_NGOAI_LE, cap.getValue().getLoaiNgoaiLe());
    }
}