package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.NhanVienRequest;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.entity.PhongBan;
import com.company.hrm.hr.repository.NguoiPhuThuocRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.hr.repository.PhongBanRepository;
import com.company.hrm.hr.repository.QuaTrinhCongTacRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NhanVienServiceTest {

    private NhanVienRepository repo;
    private PhongBanRepository pbRepo;
    private NguoiPhuThuocRepository nptRepo;
    private QuaTrinhCongTacRepository qttRepo;
    private NhanVienService service;

    @BeforeEach
    void setUp() {
        repo = mock(NhanVienRepository.class);
        pbRepo = mock(PhongBanRepository.class);
        nptRepo = mock(NguoiPhuThuocRepository.class);
        qttRepo = mock(QuaTrinhCongTacRepository.class);
        service = new NhanVienService(repo, pbRepo, nptRepo, qttRepo);
        when(repo.save(any(NhanVien.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private NhanVienRequest req() {
        NhanVienRequest r = new NhanVienRequest();
        r.setHoTen("Nguyễn Văn A");
        r.setNgaySinh(LocalDate.of(1990, 1, 1));
        r.setSoCccd("012345678901");
        r.setNgayVaoLam(LocalDate.of(2026, 1, 1));
        r.setPhongBanId(UUID.randomUUID());
        return r;
    }

    private PhongBan pb(UUID id, boolean active) {
        PhongBan p = new PhongBan();
        p.setPhongBanId(id);
        p.setMaPhongBan("PB");
        p.setActive(active);
        return p;
    }

    @Test
    void create_succeeds_generatesMaNv() {
        NhanVienRequest r = req();
        UUID pbId = r.getPhongBanId();
        when(pbRepo.findById(pbId)).thenReturn(Optional.of(pb(pbId, true)));
        when(repo.existsBySoCccd(r.getSoCccd())).thenReturn(false);
        when(repo.maxNumericSuffix("NV", 2)).thenReturn(5L);

        var resp = service.create(r);
        ArgumentCaptor<NhanVien> captor = ArgumentCaptor.forClass(NhanVien.class);
        verify(repo).save(captor.capture());
        assertEquals("NV0006", captor.getValue().getMaNv());
        assertEquals(TrangThaiNv.UNG_VIEN, captor.getValue().getTrangThai());
        assertEquals("CC-NV0006", captor.getValue().getTaiKhoanChamCongId());
    }

    @Test
    void create_duplicateCccd_throws() {
        NhanVienRequest r = req();
        when(repo.existsBySoCccd(r.getSoCccd())).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("CCCD_DUPLICATE", ex.getCode());
    }

    @Test
    void create_departmentInactive_throws() {
        NhanVienRequest r = req();
        UUID pbId = r.getPhongBanId();
        when(pbRepo.findById(pbId)).thenReturn(Optional.of(pb(pbId, false)));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("PHONG_BAN_INACTIVE", ex.getCode());
    }

    @Test
    void create_departmentMissing_throws() {
        NhanVienRequest r = req();
        when(pbRepo.findById(r.getPhongBanId())).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("PHONG_BAN_NOT_FOUND", ex.getCode());
    }

    @Test
    void create_quanLyNotFound_throws() {
        NhanVienRequest r = req();
        UUID pbId = r.getPhongBanId();
        r.setQuanLyTrucTiepId(UUID.randomUUID());
        when(pbRepo.findById(pbId)).thenReturn(Optional.of(pb(pbId, true)));
        when(repo.existsBySoCccd(any())).thenReturn(false);
        when(repo.existsById(r.getQuanLyTrucTiepId())).thenReturn(false);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("QUAN_LY_NOT_FOUND", ex.getCode());
    }

    @Test
    void updateInfo_changingTrangThai_throws() {
        UUID id = UUID.randomUUID();
        NhanVien nv = new NhanVien();
        nv.setNhanVienId(id);
        nv.setTrangThai(TrangThaiNv.UNG_VIEN);
        nv.setPhongBanId(UUID.randomUUID());
        nv.setSoCccd("012345678901");
        when(repo.findById(id)).thenReturn(Optional.of(nv));

        NhanVienRequest r = new NhanVienRequest();
        r.setHoTen("X");
        r.setNgayVaoLam(LocalDate.now());
        r.setPhongBanId(nv.getPhongBanId());
        r.setTrangThai(TrangThaiNv.CHINH_THUC);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updateInfo(id, r));
        assertEquals("TRANG_THAI_IMMUTABLE", ex.getCode());
    }

    @Test
    void updateInfo_changingPhongBan_throws() {
        UUID id = UUID.randomUUID();
        NhanVien nv = new NhanVien();
        nv.setNhanVienId(id);
        nv.setPhongBanId(UUID.randomUUID());
        nv.setSoCccd("012");
        when(repo.findById(id)).thenReturn(Optional.of(nv));

        NhanVienRequest r = new NhanVienRequest();
        r.setHoTen("X");
        r.setNgayVaoLam(LocalDate.now());
        r.setPhongBanId(UUID.randomUUID()); // đổi phòng ban
        r.setTrangThai(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updateInfo(id, r));
        assertEquals("PHONG_BAN_IMMUTABLE", ex.getCode());
    }

    @Test
    void updateInfo_succeeds() {
        UUID id = UUID.randomUUID();
        UUID pbId = UUID.randomUUID();
        NhanVien nv = new NhanVien();
        nv.setNhanVienId(id);
        nv.setPhongBanId(pbId);
        nv.setSoCccd("012");
        when(repo.findById(id)).thenReturn(Optional.of(nv));

        NhanVienRequest r = new NhanVienRequest();
        r.setHoTen("Nguyễn Văn B");
        r.setNgaySinh(LocalDate.of(1992, 5, 5));
        r.setNgayVaoLam(LocalDate.of(2026, 1, 1));
        r.setPhongBanId(pbId);
        r.setSoCccd("012");

        var resp = service.updateInfo(id, r);
        assertEquals("Nguyễn Văn B", resp.getHoTen());
    }

    @Test
    void get_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }
}