package com.company.hrm.hr.service;

import com.company.hrm.hr.entity.BienDongNhanSu;
import com.company.hrm.hr.entity.HopDongLaoDong.LoaiHopDong;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.event.EmployeeStatusChangedEvent;
import com.company.hrm.hr.repository.BienDongNhanSuRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BienDongContractListenerTest {

    private BienDongNhanSuRepository bdRepo;
    private NhanVienRepository nvRepo;
    private ApplicationEventPublisher events;
    private BienDongContractListener listener;

    private UUID nvId;

    @BeforeEach
    void setUp() {
        bdRepo = mock(BienDongNhanSuRepository.class);
        nvRepo = mock(NhanVienRepository.class);
        events = mock(ApplicationEventPublisher.class);
        listener = new BienDongContractListener(bdRepo, nvRepo, events);
        when(bdRepo.save(any(BienDongNhanSu.class))).thenAnswer(inv -> inv.getArgument(0));
        when(nvRepo.save(any(NhanVien.class))).thenAnswer(inv -> inv.getArgument(0));
        nvId = UUID.randomUUID();
    }

    private NhanVien nv(TrangThaiNv status) {
        NhanVien n = new NhanVien();
        n.setNhanVienId(nvId);
        n.setPhongBanId(UUID.randomUUID());
        n.setTrangThai(status);
        return n;
    }

    private HopDongService.ContractSignedEvent event(LoaiHopDong loai) {
        return new HopDongService.ContractSignedEvent(UUID.randomUUID(), nvId, loai, LocalDate.of(2026, 1, 1));
    }

    @Test
    void thuViec_fromUngVien_createsMovementToThuViec() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(TrangThaiNv.UNG_VIEN)));
        listener.onContractSigned(event(LoaiHopDong.THU_VIEC));

        ArgumentCaptor<BienDongNhanSu> captor = ArgumentCaptor.forClass(BienDongNhanSu.class);
        verify(bdRepo).save(captor.capture());
        BienDongNhanSu bd = captor.getValue();
        assertEquals(BienDongNhanSu.LoaiBienDong.TUYEN_DUNG, bd.getLoaiBienDong());
        assertEquals(BienDongNhanSu.TrangThaiNvSau.THU_VIEC, bd.getTrangThaiNvSau());
        assertNotNull(bd.getSoQuyetDinh());

        ArgumentCaptor<NhanVien> nvCaptor = ArgumentCaptor.forClass(NhanVien.class);
        verify(nvRepo).save(nvCaptor.capture());
        assertEquals(TrangThaiNv.THU_VIEC, nvCaptor.getValue().getTrangThai());

        verify(events).publishEvent(any(EmployeeStatusChangedEvent.class));
    }

    @Test
    void xacDinhThoiHan_fromThuViec_createsMovementToChinhThuc() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(TrangThaiNv.THU_VIEC)));
        listener.onContractSigned(event(LoaiHopDong.XAC_DINH_THOI_HAN));

        ArgumentCaptor<BienDongNhanSu> captor = ArgumentCaptor.forClass(BienDongNhanSu.class);
        verify(bdRepo).save(captor.capture());
        assertEquals(BienDongNhanSu.TrangThaiNvSau.CHINH_THUC, captor.getValue().getTrangThaiNvSau());
    }

    @Test
    void khongXacDinhThoiHan_fromUngVien_createsMovementToChinhThuc() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(TrangThaiNv.UNG_VIEN)));
        listener.onContractSigned(event(LoaiHopDong.KHONG_XAC_DINH_THOI_HAN));

        ArgumentCaptor<BienDongNhanSu> captor = ArgumentCaptor.forClass(BienDongNhanSu.class);
        verify(bdRepo).save(captor.capture());
        assertEquals(BienDongNhanSu.TrangThaiNvSau.CHINH_THUC, captor.getValue().getTrangThaiNvSau());
    }

    @Test
    void addendum_isIgnored() {
        listener.onContractSigned(event(LoaiHopDong.PHU_LUC));
        verify(bdRepo, never()).save(any());
        verify(nvRepo, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void alreadyChinhThuc_isIgnored() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(TrangThaiNv.CHINH_THUC)));
        listener.onContractSigned(event(LoaiHopDong.KHONG_XAC_DINH_THOI_HAN));
        verify(bdRepo, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void daNghiViec_isIgnored() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv(TrangThaiNv.DA_NGHI_VIEC)));
        listener.onContractSigned(event(LoaiHopDong.THU_VIEC));
        verify(bdRepo, never()).save(any());
    }

    @Test
    void employeeMissing_logsAndReturns() {
        when(nvRepo.findById(nvId)).thenReturn(Optional.empty());
        listener.onContractSigned(event(LoaiHopDong.THU_VIEC));
        verify(bdRepo, never()).save(any());
    }
}
