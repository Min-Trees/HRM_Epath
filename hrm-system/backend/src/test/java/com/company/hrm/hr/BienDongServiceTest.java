package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.BienDongRequest;
import com.company.hrm.hr.dto.BienDongResponse;
import com.company.hrm.hr.dto.BienDongTimelineItem;
import com.company.hrm.hr.entity.BienDongNhanSu;
import com.company.hrm.hr.entity.BienDongNhanSu.LoaiBienDong;
import com.company.hrm.hr.entity.HopDongLaoDong;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.event.EmployeeSalaryChangedEvent;
import com.company.hrm.hr.event.EmployeeStatusChangedEvent;
import com.company.hrm.hr.event.EmployeeTransferredEvent;
import com.company.hrm.hr.repository.BienDongNhanSuRepository;
import com.company.hrm.hr.repository.HopDongLaoDongRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class BienDongServiceTest {

    private BienDongNhanSuRepository bdRepo;
    private NhanVienRepository nvRepo;
    private HopDongLaoDongRepository hdRepo;
    private ApplicationEventPublisher events;
    private BienDongService service;

    private UUID nvId;
    private UUID pbId;
    private NhanVien nv;

    @BeforeEach
    void setUp() {
        bdRepo = mock(BienDongNhanSuRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        hdRepo = mock(HopDongLaoDongRepository.class);
        events = mock(ApplicationEventPublisher.class);
        service = new BienDongService(bdRepo, nvRepo, hdRepo, events);
        when(bdRepo.save(any(BienDongNhanSu.class))).thenAnswer(inv -> inv.getArgument(0));
        when(nvRepo.save(any(NhanVien.class))).thenAnswer(inv -> inv.getArgument(0));
        when(hdRepo.save(any(HopDongLaoDong.class))).thenAnswer(inv -> inv.getArgument(0));

        nvId = UUID.randomUUID();
        pbId = UUID.randomUUID();
        nv = new NhanVien();
        nv.setNhanVienId(nvId);
        nv.setMaNv("NV0001");
        nv.setHoTen("Nguyễn Văn A");
        nv.setNgayVaoLam(LocalDate.of(2026, 1, 1));
        nv.setPhongBanId(pbId);
        nv.setTrangThai(TrangThaiNv.UNG_VIEN);
    }

    private BienDongRequest req(LoaiBienDong loai, TrangThaiNv target) {
        BienDongRequest r = new BienDongRequest();
        r.setNhanVienId(nvId);
        r.setLoaiBienDong(loai);
        r.setSoQuyetDinh("QD-001");
        r.setNgayQuyetDinh(LocalDate.of(2026, 1, 15));
        r.setNgayHieuLuc(LocalDate.of(2026, 1, 20));
        r.setTrangThaiNvSau(target);
        return r;
    }

    private HopDongLaoDong hd(UUID nvId, BigDecimal luong) {
        HopDongLaoDong h = new HopDongLaoDong();
        h.setHopDongId(UUID.randomUUID());
        h.setNhanVienId(nvId);
        h.setMucLuongThoaThuan(luong);
        return h;
    }

    // ---------------- employee / validate ----------------

    @Test
    void create_employeeNotFound_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.empty());
        BienDongRequest r = req(LoaiBienDong.TUYEN_DUNG, TrangThaiNv.THU_VIEC);
        assertThrows(ResourceNotFoundException.class, () -> service.create(r));
    }

    @Test
    void create_ngayHieuLucBeforeNgayQuyetDinh_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        BienDongRequest r = req(LoaiBienDong.TUYEN_DUNG, TrangThaiNv.THU_VIEC);
        r.setNgayQuyetDinh(LocalDate.of(2026, 2, 1));
        r.setNgayHieuLuc(LocalDate.of(2026, 1, 20));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("BIEN_DONG_NGAY_HIEU_LUC_INVALID", ex.getCode());
    }

    @Test
    void create_ngayHieuLucBeforeNgayVaoLam_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        BienDongRequest r = req(LoaiBienDong.TUYEN_DUNG, TrangThaiNv.THU_VIEC);
        r.setNgayQuyetDinh(LocalDate.of(2025, 12, 15)); // trước ngayVaoLam
        r.setNgayHieuLuc(LocalDate.of(2025, 12, 31));    // trước ngayVaoLam=2026-01-01
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("BIEN_DONG_BEFORE_JOIN_DATE", ex.getCode());
    }

    @Test
    void create_duplicateSoQuyetDinh_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        when(bdRepo.existsByNhanVienIdAndSoQuyetDinh(nvId, "QD-001")).thenReturn(true);
        BienDongRequest r = req(LoaiBienDong.TUYEN_DUNG, TrangThaiNv.THU_VIEC);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("BIEN_DONG_SO_QUYET_DINH_DUPLICATE", ex.getCode());
    }

    // ---------------- status transition matrix ----------------

    @Test
    void create_invalidStatusTransition_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        // UNG_VIEN -> DA_NGHI_HUU không hợp lệ
        BienDongRequest r = req(LoaiBienDong.NGHI_HUU, TrangThaiNv.DA_NGHI_HUU);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("INVALID_STATUS_TRANSITION", ex.getCode());
    }

    @Test
    void create_statusUnchangedForTuyenDung_throws() {
        nv.setTrangThai(TrangThaiNv.THU_VIEC);
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        BienDongRequest r = req(LoaiBienDong.TUYEN_DUNG, TrangThaiNv.THU_VIEC);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("BIEN_DONG_STATUS_UNCHANGED", ex.getCode());
    }

    @Test
    void create_dieuChuyen_statusUnchanged_ok() {
        nv.setTrangThai(TrangThaiNv.CHINH_THUC);
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        when(hdRepo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(eq(nvId), any(), anyList()))
                .thenReturn(Optional.empty());

        BienDongRequest r = req(LoaiBienDong.DIEU_CHUYEN, TrangThaiNv.CHINH_THUC);
        UUID newPbId = UUID.randomUUID();
        r.setPhongBanSauId(newPbId);

        service.create(r);

        ArgumentCaptor<NhanVien> captor = ArgumentCaptor.forClass(NhanVien.class);
        verify(nvRepo).save(captor.capture());
        assertEquals(newPbId, captor.getValue().getPhongBanId());
        // Event transfer
        verify(events).publishEvent(any(EmployeeTransferredEvent.class));
    }

    // ---------------- happy path: full chain -----------------------

    @Test
    void create_fullChain_updatesStatusAndSalaryAndPublishesEvents() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        when(hdRepo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(eq(nvId), any(), anyList()))
                .thenReturn(Optional.of(hd(nvId, new BigDecimal("10000000"))));

        BienDongRequest r = req(LoaiBienDong.TUYEN_DUNG, TrangThaiNv.CHINH_THUC);
        r.setLuongSau(new BigDecimal("15000000"));
        UUID newPbId = UUID.randomUUID();
        r.setPhongBanSauId(newPbId);

        service.create(r);

        ArgumentCaptor<BienDongNhanSu> bdCaptor = ArgumentCaptor.forClass(BienDongNhanSu.class);
        verify(bdRepo).save(bdCaptor.capture());
        BienDongNhanSu saved = bdCaptor.getValue();
        assertEquals(LoaiBienDong.TUYEN_DUNG, saved.getLoaiBienDong());
        assertEquals(pbId, saved.getPhongBanTruocId());
        assertEquals(newPbId, saved.getPhongBanSauId());
        assertEquals(new BigDecimal("10000000"), saved.getLuongTruoc());
        assertEquals(new BigDecimal("15000000"), saved.getLuongSau());

        // NV được cập nhật
        ArgumentCaptor<NhanVien> nvCaptor = ArgumentCaptor.forClass(NhanVien.class);
        verify(nvRepo).save(nvCaptor.capture());
        assertEquals(TrangThaiNv.CHINH_THUC, nvCaptor.getValue().getTrangThai());
        assertEquals(newPbId, nvCaptor.getValue().getPhongBanId());

        // HĐ được cập nhật lương
        ArgumentCaptor<HopDongLaoDong> hdCaptor = ArgumentCaptor.forClass(HopDongLaoDong.class);
        verify(hdRepo).save(hdCaptor.capture());
        assertEquals(new BigDecimal("15000000"), hdCaptor.getValue().getMucLuongThoaThuan());

        // 3 event: status + transfer + salary
        verify(events).publishEvent(any(EmployeeStatusChangedEvent.class));
        verify(events).publishEvent(any(EmployeeTransferredEvent.class));
        verify(events).publishEvent(any(EmployeeSalaryChangedEvent.class));
    }

    @Test
    void create_dieuChinhLuong_noActiveContract_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        when(hdRepo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(eq(nvId), any(), anyList()))
                .thenReturn(Optional.empty());
        BienDongRequest r = req(LoaiBienDong.DIEU_CHINH_LUONG, TrangThaiNv.CHINH_THUC);
        r.setLuongSau(new BigDecimal("20000000"));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("NO_ACTIVE_CONTRACT_TO_ADJUST_SALARY", ex.getCode());
    }

    @Test
    void create_dieuChinhLuong_missingLuongSau_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        BienDongRequest r = req(LoaiBienDong.DIEU_CHINH_LUONG, TrangThaiNv.CHINH_THUC);
        // không set luongSau
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("BIEN_DONG_LUONG_REQUIRED", ex.getCode());
    }

    @Test
    void create_dieuChinhLuong_negativeLuong_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        BienDongRequest r = req(LoaiBienDong.DIEU_CHINH_LUONG, TrangThaiNv.CHINH_THUC);
        r.setLuongSau(new BigDecimal("-1"));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("BIEN_DONG_LUONG_INVALID", ex.getCode());
    }

    @Test
    void create_dieuChuyen_missingPhongBanSau_throws() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));
        when(hdRepo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(eq(nvId), any(), anyList()))
                .thenReturn(Optional.empty());
        BienDongRequest r = req(LoaiBienDong.DIEU_CHUYEN, TrangThaiNv.CHINH_THUC);
        // không set phongBanSauId
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("BIEN_DONG_PB_REQUIRED", ex.getCode());
    }

    // ---------------- trangThaiTaiNgay ----------------

    @Test
    void trangThaiTaiNgay_returnsLatestBeforeDate() {
        BienDongNhanSu bd = new BienDongNhanSu();
        bd.setNhanVienId(nvId);
        bd.setNgayHieuLuc(LocalDate.of(2026, 6, 1));
        bd.setTrangThaiNvSau(BienDongNhanSu.TrangThaiNvSau.CHINH_THUC);
        bd.setLyDo("Ký HĐ chính thức");
        when(bdRepo.findFirstByNhanVienIdAndNgayHieuLucLessThanEqualOrderByNgayHieuLucDesc(
                nvId, LocalDate.of(2026, 7, 1))).thenReturn(Optional.of(bd));

        BienDongTimelineItem item = service.trangThaiTaiNgay(nvId, LocalDate.of(2026, 7, 1));
        assertEquals(LocalDate.of(2026, 6, 1), item.getNgay());
        assertEquals(TrangThaiNv.CHINH_THUC, item.getTrangThai());
    }

    @Test
    void trangThaiTaiNgay_noMovement_returnsUngVien() {
        when(bdRepo.findFirstByNhanVienIdAndNgayHieuLucLessThanEqualOrderByNgayHieuLucDesc(
                any(UUID.class), any(LocalDate.class))).thenReturn(Optional.empty());
        BienDongTimelineItem item = service.trangThaiTaiNgay(nvId, LocalDate.of(2026, 1, 1));
        assertEquals(TrangThaiNv.UNG_VIEN, item.getTrangThai());
    }

    @Test
    void trangThaiTaiNgay_queryingBeforeAnyMovement_returnsUngVien() {
        when(bdRepo.findFirstByNhanVienIdAndNgayHieuLucLessThanEqualOrderByNgayHieuLucDesc(
                any(UUID.class), any(LocalDate.class))).thenReturn(Optional.empty());
        // NV chưa có biến động nào → trạng thái khởi tạo UNG_VIEN
        BienDongTimelineItem item = service.trangThaiTaiNgay(nvId, LocalDate.of(2025, 12, 31));
        assertEquals(TrangThaiNv.UNG_VIEN, item.getTrangThai());
    }

    // ---------------- list ----------------

    @Test
    void listByNhanVien_returnsDescendingTimeline() {
        BienDongNhanSu bd1 = new BienDongNhanSu();
        bd1.setBienDongId(UUID.randomUUID());
        bd1.setNhanVienId(nvId);
        bd1.setTrangThaiNvSau(BienDongNhanSu.TrangThaiNvSau.THU_VIEC);
        when(bdRepo.findByNhanVienIdOrderByNgayHieuLucDesc(nvId)).thenReturn(List.of(bd1));

        List<BienDongResponse> list = service.listByNhanVien(nvId);
        assertEquals(1, list.size());
        assertEquals(TrangThaiNv.THU_VIEC, list.get(0).getTrangThaiNvSau());
    }
}
