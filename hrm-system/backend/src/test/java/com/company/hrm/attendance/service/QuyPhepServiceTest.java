package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.LeaveBalanceInitResult;
import com.company.hrm.attendance.dto.QuyPhepNamResponse;
import com.company.hrm.attendance.entity.QuyPhepNam;
import com.company.hrm.attendance.repository.QuyPhepNamRepository;
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
import static org.mockito.Mockito.*;

class QuyPhepServiceTest {

    private QuyPhepNamRepository repo;
    private NhanVienRepository nvRepo;
    private QuyPhepService service;

    @BeforeEach
    void setUp() {
        repo = mock(QuyPhepNamRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        service = new QuyPhepService(repo, nvRepo);
        when(repo.save(any(QuyPhepNam.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private NhanVien nv(UUID id, LocalDate ngayVaoLam) {
        NhanVien n = new NhanVien();
        n.setNhanVienId(id);
        n.setMaNv("NV0001");
        n.setTrangThai(TrangThaiNv.CHINH_THUC);
        n.setNgayVaoLam(ngayVaoLam);
        return n;
    }

    // ---------------- init ----------------

    @Test
    void init_3years_thamNien_3_nhan_12_ngay() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, LocalDate.of(2023, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvId, 2026)).thenReturn(false);

        QuyPhepNamResponse r = service.init(nvId, 2026);
        // 2026 - 2023 = 3 năm; floor(3/5) = 0 → 12 ngày
        assertEquals(new BigDecimal("12.0"), r.getSoNgayDuocHuong());
        assertEquals(new BigDecimal("0.0"), r.getSoNgayDaDung());
    }

    @Test
    void init_5yearsExact_nhan_13_ngay() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, LocalDate.of(2021, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvId, 2026)).thenReturn(false);

        QuyPhepNamResponse r = service.init(nvId, 2026);
        assertEquals(new BigDecimal("13.0"), r.getSoNgayDuocHuong());
    }

    @Test
    void init_4years_thamNien_4_nhan_12_ngay() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, LocalDate.of(2022, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvId, 2026)).thenReturn(false);

        QuyPhepNamResponse r = service.init(nvId, 2026);
        // Period 2022-01-01 → 2026-01-01 = 4 năm đúng; floor(4/5) = 0 → 12
        assertEquals(new BigDecimal("12.0"), r.getSoNgayDuocHuong());
    }

    @Test
    void init_7years_nhan_13_ngay() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, LocalDate.of(2019, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvId, 2026)).thenReturn(false);

        QuyPhepNamResponse r = service.init(nvId, 2026);
        // floor(7/5) = 1 → 13
        assertEquals(new BigDecimal("13.0"), r.getSoNgayDuocHuong());
    }

    @Test
    void init_10years_nhan_14_ngay() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, LocalDate.of(2016, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvId, 2026)).thenReturn(false);

        QuyPhepNamResponse r = service.init(nvId, 2026);
        // floor(10/5) = 2 → 14
        assertEquals(new BigDecimal("14.0"), r.getSoNgayDuocHuong());
    }

    @Test
    void init_idempotent_skipNeuDaCo() {
        UUID nvId = UUID.randomUUID();
        QuyPhepNam existing = new QuyPhepNam();
        existing.setQuyPhepId(UUID.randomUUID());
        existing.setNhanVienId(nvId);
        existing.setNam(2026);
        existing.setSoNgayDuocHuong(new BigDecimal("12.0"));
        existing.setSoNgayDaDung(new BigDecimal("3.0"));

        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(nvId, LocalDate.of(2023, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvId, 2026)).thenReturn(true);
        when(repo.findByNhanVienIdAndNam(nvId, 2026)).thenReturn(Optional.of(existing));

        QuyPhepNamResponse r = service.init(nvId, 2026);
        assertEquals(new BigDecimal("3.0"), r.getSoNgayDaDung());
        verify(repo, never()).save(any());
    }

    @Test
    void init_nhanVienNotFound_throws() {
        UUID badId = UUID.randomUUID();
        when(nvRepo.findById(badId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.init(badId, 2026));
    }

    @Test
    void init_namKhongHopLe_throws() {
        assertThrows(BusinessException.class, () -> service.init(UUID.randomUUID(), 1500));
        assertThrows(BusinessException.class, () -> service.init(UUID.randomUUID(), 99999));
    }

    // ---------------- initBatch ----------------

    @Test
    void initBatch_mixed_skipExisting() {
        UUID nvA = UUID.randomUUID();
        UUID nvB = UUID.randomUUID();
        UUID nvC = UUID.randomUUID();

        when(nvRepo.findById(nvA)).thenReturn(Optional.of(nv(nvA, LocalDate.of(2023, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvA, 2026)).thenReturn(false);

        QuyPhepNam existingB = new QuyPhepNam();
        existingB.setQuyPhepId(UUID.randomUUID());
        existingB.setNhanVienId(nvB);
        existingB.setNam(2026);
        existingB.setSoNgayDuocHuong(new BigDecimal("12.0"));
        existingB.setSoNgayDaDung(new BigDecimal("0.0"));
        when(nvRepo.findById(nvB)).thenReturn(Optional.of(nv(nvB, LocalDate.of(2023, 1, 1))));
        when(repo.existsByNhanVienIdAndNam(nvB, 2026)).thenReturn(true);
        when(repo.findByNhanVienIdAndNam(nvB, 2026)).thenReturn(Optional.of(existingB));

        when(nvRepo.findById(nvC)).thenReturn(Optional.empty()); // skip

        LeaveBalanceInitResult r = service.initBatch(List.of(nvA, nvB, nvC), 2026);
        assertEquals(3, r.getTotal());
        assertEquals(1, r.getCreated());
        assertEquals(2, r.getSkipped());
    }

    // ---------------- getBalance ----------------

    @Test
    void getBalance_returnsExisting() {
        UUID nvId = UUID.randomUUID();
        QuyPhepNam existing = new QuyPhepNam();
        existing.setQuyPhepId(UUID.randomUUID());
        existing.setNhanVienId(nvId);
        existing.setNam(2026);
        existing.setSoNgayDuocHuong(new BigDecimal("12.0"));
        existing.setSoNgayDaDung(new BigDecimal("2.0"));
        when(repo.findByNhanVienIdAndNam(nvId, 2026)).thenReturn(Optional.of(existing));

        QuyPhepNamResponse r = service.getBalance(nvId, 2026);
        // DB-generated column; mock service trả existing.as-is (soNgayConLai = null khi chưa set).
        // Thực tế JPA load về sẽ có giá trị từ DB. Ở đây chỉ verify mapping pass-through.
        assertEquals(new BigDecimal("12.0"), r.getSoNgayDuocHuong());
        assertEquals(new BigDecimal("2.0"), r.getSoNgayDaDung());
    }

    @Test
    void getBalance_notFound_returnsNull() {
        when(repo.findByNhanVienIdAndNam(any(), eq(2026))).thenReturn(Optional.empty());
        assertNull(service.getBalance(UUID.randomUUID(), 2026));
    }

    // ---------------- congDaDung / hoanDaDung ----------------

    @Test
    void congDaDung_truQuy() {
        UUID nvId = UUID.randomUUID();
        QuyPhepNam qp = new QuyPhepNam();
        qp.setQuyPhepId(UUID.randomUUID());
        qp.setNhanVienId(nvId);
        qp.setNam(2026);
        qp.setSoNgayDuocHuong(new BigDecimal("12.0"));
        qp.setSoNgayDaDung(new BigDecimal("2.0"));
        when(repo.findByNhanVienIdAndNam(nvId, 2026)).thenReturn(Optional.of(qp));

        service.congDaDung(nvId, 2026, new BigDecimal("3.0"));
        assertEquals(new BigDecimal("5.0"), qp.getSoNgayDaDung());
    }

    @Test
    void congDaDung_quaTai_throws() {
        UUID nvId = UUID.randomUUID();
        QuyPhepNam qp = new QuyPhepNam();
        qp.setQuyPhepId(UUID.randomUUID());
        qp.setNhanVienId(nvId);
        qp.setNam(2026);
        qp.setSoNgayDuocHuong(new BigDecimal("12.0"));
        qp.setSoNgayDaDung(new BigDecimal("11.0"));
        when(repo.findByNhanVienIdAndNam(nvId, 2026)).thenReturn(Optional.of(qp));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.congDaDung(nvId, 2026, new BigDecimal("2.0")));
        assertEquals("QUY_PHEP_NOT_ENOUGH", ex.getCode());
    }

    @Test
    void congDaDung_quyChuaInit_throws() {
        UUID nvId = UUID.randomUUID();
        when(repo.findByNhanVienIdAndNam(nvId, 2026)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.congDaDung(nvId, 2026, new BigDecimal("1.0")));
        assertEquals("QUY_PHEP_NOT_INIT", ex.getCode());
    }

    @Test
    void hoanDaDung_giamQuy() {
        UUID nvId = UUID.randomUUID();
        QuyPhepNam qp = new QuyPhepNam();
        qp.setQuyPhepId(UUID.randomUUID());
        qp.setNhanVienId(nvId);
        qp.setNam(2026);
        qp.setSoNgayDuocHuong(new BigDecimal("12.0"));
        qp.setSoNgayDaDung(new BigDecimal("5.0"));
        when(repo.findByNhanVienIdAndNam(nvId, 2026)).thenReturn(Optional.of(qp));

        service.hoanDaDung(nvId, 2026, new BigDecimal("2.0"));
        assertEquals(new BigDecimal("3.0"), qp.getSoNgayDaDung());
    }

    @Test
    void hoanDaDung_amDatVe0() {
        UUID nvId = UUID.randomUUID();
        QuyPhepNam qp = new QuyPhepNam();
        qp.setQuyPhepId(UUID.randomUUID());
        qp.setNhanVienId(nvId);
        qp.setNam(2026);
        qp.setSoNgayDuocHuong(new BigDecimal("12.0"));
        qp.setSoNgayDaDung(new BigDecimal("1.0"));
        when(repo.findByNhanVienIdAndNam(nvId, 2026)).thenReturn(Optional.of(qp));

        service.hoanDaDung(nvId, 2026, new BigDecimal("3.0"));
        assertEquals(0, qp.getSoNgayDaDung().compareTo(new BigDecimal("0.0")));
    }
}