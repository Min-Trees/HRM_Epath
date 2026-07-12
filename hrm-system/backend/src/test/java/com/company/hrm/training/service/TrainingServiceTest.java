package com.company.hrm.training.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.training.dto.*;
import com.company.hrm.training.entity.*;
import com.company.hrm.training.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock ChuongTrinhDaoTaoRepository ctRepo;
    @Mock LopHocRepository lopRepo;
    @Mock DangKyDaoTaoRepository dkRepo;
    @Mock DiemDanhDaoTaoRepository ddRepo;
    @Mock DanhGiaSauDaoTaoRepository dgRepo;
    TrainingService service;

    @BeforeEach
    void setUp() {
        service = new TrainingService(ctRepo, lopRepo, dkRepo, ddRepo, dgRepo);
    }

    // ============ CHUONG TRINH ============

    @Test
    void createChuongTrinh_maTrung_throwException() {
        ChuongTrinhDaoTao existing = new ChuongTrinhDaoTao();
        existing.setMaChuongTrinh("CT001");
        when(ctRepo.findAll()).thenReturn(List.of(existing));

        ChuongTrinhDaoTaoDto dto = new ChuongTrinhDaoTaoDto();
        dto.setMaChuongTrinh("CT001");
        dto.setTenChuongTrinh("Dao tao noi quy");
        dto.setLoaiChuongTrinh(LoaiChuongTrinh.AN_TOAN_LAO_DONG);
        dto.setThoiLuongGio(new BigDecimal("8"));
        assertThatThrownBy(() -> service.createChuongTrinh(dto, UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("MA_TRUNG"));
    }

    @Test
    void createChuongTrinh_thanhCong() {
        when(ctRepo.findAll()).thenReturn(List.of());
        when(ctRepo.save(any())).thenAnswer(inv -> {
            ChuongTrinhDaoTao c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        ChuongTrinhDaoTaoDto dto = new ChuongTrinhDaoTaoDto();
        dto.setMaChuongTrinh("CT-NEW");
        dto.setTenChuongTrinh("Excel nang cao");
        dto.setLoaiChuongTrinh(LoaiChuongTrinh.KY_NANG_CHUYEN_MON);
        dto.setThoiLuongGio(new BigDecimal("16"));
        dto.setDiemDanhGiaToiThieu(new BigDecimal("70"));

        ChuongTrinhDaoTaoDto out = service.createChuongTrinh(dto, UUID.randomUUID());
        assertThat(out.getMaChuongTrinh()).isEqualTo("CT-NEW");
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiChuongTrinh.NHAP);
        assertThat(out.getDiemDanhGiaToiThieu()).isEqualByComparingTo("70");
    }

    @Test
    void congBo_tuNHAP_thanhCong() {
        ChuongTrinhDaoTao c = new ChuongTrinhDaoTao();
        c.setId(UUID.randomUUID());
        c.setTrangThai(TrangThaiChuongTrinh.NHAP);
        when(ctRepo.findById(c.getId())).thenReturn(Optional.of(c));
        when(ctRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ChuongTrinhDaoTaoDto out = service.congBoChuongTrinh(c.getId());
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiChuongTrinh.CONG_BO);
    }

    @Test
    void congBo_tuCONG_BO_throwException() {
        ChuongTrinhDaoTao c = new ChuongTrinhDaoTao();
        c.setId(UUID.randomUUID());
        c.setTrangThai(TrangThaiChuongTrinh.CONG_BO);
        when(ctRepo.findById(c.getId())).thenReturn(Optional.of(c));
        assertThatThrownBy(() -> service.congBoChuongTrinh(c.getId()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("INVALID_STATE"));
    }

    // ============ LOP HOC ============

    @Test
    void createLop_ngayKetThucTruocBatDau_throwException() {
        LopHocDto dto = new LopHocDto();
        dto.setMaLop("L01");
        dto.setChuongTrinhId(UUID.randomUUID());
        dto.setTenLop("Lop 1");
        dto.setNgayBatDau(LocalDate.of(2026, 6, 15));
        dto.setNgayKetThuc(LocalDate.of(2026, 6, 10));
        dto.setSoBuoi(5);
        when(ctRepo.existsById(dto.getChuongTrinhId())).thenReturn(true);
        assertThatThrownBy(() -> service.createLop(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("INVALID_DATES"));
    }

    @Test
    void createLop_thanhCong() {
        when(ctRepo.existsById(any())).thenReturn(true);
        when(lopRepo.save(any())).thenAnswer(inv -> {
            LopHoc l = inv.getArgument(0);
            l.setId(UUID.randomUUID());
            return l;
        });
        LopHocDto dto = new LopHocDto();
        dto.setMaLop("L-NEW");
        dto.setChuongTrinhId(UUID.randomUUID());
        dto.setTenLop("Lop moi");
        dto.setNgayBatDau(LocalDate.of(2026, 6, 15));
        dto.setNgayKetThuc(LocalDate.of(2026, 6, 20));
        dto.setSoBuoi(5);
        LopHocDto out = service.createLop(dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiLop.MO_DANG_KY);
    }

    @Test
    void lopTransition_hopLe() {
        LopHoc l = new LopHoc();
        l.setId(UUID.randomUUID());
        l.setTrangThai(TrangThaiLop.MO_DANG_KY);
        when(lopRepo.findById(l.getId())).thenReturn(Optional.of(l));
        when(lopRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        LopHocDto out = service.changeLopState(l.getId(), TrangThaiLop.DONG_DANG_KY);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiLop.DONG_DANG_KY);
    }

    @Test
    void lopTransition_sai_throwException() {
        LopHoc l = new LopHoc();
        l.setId(UUID.randomUUID());
        l.setTrangThai(TrangThaiLop.HOAN_THANH);
        when(lopRepo.findById(l.getId())).thenReturn(Optional.of(l));
        assertThatThrownBy(() -> service.changeLopState(l.getId(), TrangThaiLop.MO_DANG_KY))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("INVALID_TRANSITION"));
    }

    // ============ DANG KY ============

    @Test
    void dangKy_lopDong_throwException() {
        LopHoc l = new LopHoc();
        l.setId(UUID.randomUUID());
        l.setSoChoToiDa(30);
        l.setTrangThai(TrangThaiLop.DONG_DANG_KY);
        when(lopRepo.findById(l.getId())).thenReturn(Optional.of(l));
        DangKyDaoTaoDto dto = new DangKyDaoTaoDto();
        dto.setLopHocId(l.getId());
        dto.setNhanVienId(UUID.randomUUID());
        assertThatThrownBy(() -> service.dangKy(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("LOP_KHONG_MO"));
    }

    @Test
    void dangKy_lopDay_throwException() {
        LopHoc l = new LopHoc();
        l.setId(UUID.randomUUID());
        l.setSoChoToiDa(1);
        l.setTrangThai(TrangThaiLop.MO_DANG_KY);
        when(lopRepo.findById(l.getId())).thenReturn(Optional.of(l));
        DangKyDaoTao existing = new DangKyDaoTao();
        existing.setTrangThai(TrangThaiDangKy.DA_CHAP_NHAN);
        when(dkRepo.findByLopHocId(l.getId())).thenReturn(List.of(existing));

        DangKyDaoTaoDto dto = new DangKyDaoTaoDto();
        dto.setLopHocId(l.getId());
        dto.setNhanVienId(UUID.randomUUID());
        assertThatThrownBy(() -> service.dangKy(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("LOP_DAY"));
    }

    @Test
    void dangKy_trungNV_throwException() {
        UUID nvId = UUID.randomUUID();
        LopHoc l = new LopHoc();
        l.setId(UUID.randomUUID());
        l.setSoChoToiDa(30);
        l.setTrangThai(TrangThaiLop.MO_DANG_KY);
        when(lopRepo.findById(l.getId())).thenReturn(Optional.of(l));
        DangKyDaoTao existing = new DangKyDaoTao();
        existing.setNhanVienId(nvId);
        existing.setTrangThai(TrangThaiDangKy.DA_CHAP_NHAN);
        when(dkRepo.findByLopHocId(l.getId())).thenReturn(List.of(existing));

        DangKyDaoTaoDto dto = new DangKyDaoTaoDto();
        dto.setLopHocId(l.getId());
        dto.setNhanVienId(nvId);
        assertThatThrownBy(() -> service.dangKy(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("DA_DANG_KY"));
    }

    @Test
    void dangKy_thanhCong() {
        UUID nvId = UUID.randomUUID();
        LopHoc l = new LopHoc();
        l.setId(UUID.randomUUID());
        l.setSoChoToiDa(30);
        l.setTrangThai(TrangThaiLop.MO_DANG_KY);
        when(lopRepo.findById(l.getId())).thenReturn(Optional.of(l));
        when(dkRepo.findByLopHocId(l.getId())).thenReturn(List.of());
        when(dkRepo.save(any())).thenAnswer(inv -> {
            DangKyDaoTao d = inv.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        DangKyDaoTaoDto dto = new DangKyDaoTaoDto();
        dto.setLopHocId(l.getId());
        dto.setNhanVienId(nvId);
        dto.setLyDoDangKy("Nang cao ky nang");
        DangKyDaoTaoDto out = service.dangKy(dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiDangKy.CHO_DUYET);
        assertThat(out.getNhanVienId()).isEqualTo(nvId);
    }

    @Test
    void duyet_thanhCong() {
        DangKyDaoTao dk = new DangKyDaoTao();
        dk.setId(UUID.randomUUID());
        dk.setTrangThai(TrangThaiDangKy.CHO_DUYET);
        when(dkRepo.findById(dk.getId())).thenReturn(Optional.of(dk));
        when(dkRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DangKyDaoTaoDto out = service.duyetDangKy(dk.getId(), UUID.randomUUID(),
                TrangThaiDangKy.DA_CHAP_NHAN, "OK");
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiDangKy.DA_CHAP_NHAN);
        assertThat(out.getNgayDuyet()).isNotNull();
    }

    @Test
    void duyet_tuTrangThaiDaChapNhan_throwException() {
        DangKyDaoTao dk = new DangKyDaoTao();
        dk.setId(UUID.randomUUID());
        dk.setTrangThai(TrangThaiDangKy.DA_CHAP_NHAN);
        when(dkRepo.findById(dk.getId())).thenReturn(Optional.of(dk));
        assertThatThrownBy(() -> service.duyetDangKy(dk.getId(), UUID.randomUUID(),
                TrangThaiDangKy.TU_CHOI, "test"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode()).isEqualTo("INVALID_STATE"));
    }

    // ============ DANH GIA ============

    @Test
    void classifyDiem_xuatSac() {
        assertThat(service.classifyDiem(new BigDecimal("95"), new BigDecimal("60")))
                .isEqualTo(KetQuaDanhGia.XUAT_SAC);
    }

    @Test
    void classifyDiem_tot() {
        assertThat(service.classifyDiem(new BigDecimal("80"), new BigDecimal("60")))
                .isEqualTo(KetQuaDanhGia.TOT);
    }

    @Test
    void classifyDiem_trungBinh_datNguong() {
        assertThat(service.classifyDiem(new BigDecimal("65"), new BigDecimal("60")))
                .isEqualTo(KetQuaDanhGia.TRUNG_BINH);
    }

    @Test
    void classifyDiem_yeu() {
        assertThat(service.classifyDiem(new BigDecimal("40"), new BigDecimal("60")))
                .isEqualTo(KetQuaDanhGia.YEU);
    }

    @Test
    void classifyDiem_null() {
        assertThat(service.classifyDiem(null, new BigDecimal("60")))
                .isEqualTo(KetQuaDanhGia.KHONG_DANH_GIA);
    }

    @Test
    void danhGia_thanhCong_capChungChi() {
        UUID ctId = UUID.randomUUID();
        UUID lopId = UUID.randomUUID();
        UUID dkId = UUID.randomUUID();

        ChuongTrinhDaoTao ct = new ChuongTrinhDaoTao();
        ct.setId(ctId);
        ct.setChungChi("IOSH");
        ct.setDiemDanhGiaToiThieu(new BigDecimal("60"));

        LopHoc lop = new LopHoc();
        lop.setId(lopId);
        lop.setChuongTrinhId(ctId);

        DangKyDaoTao dk = new DangKyDaoTao();
        dk.setId(dkId);
        dk.setLopHocId(lopId);
        dk.setTrangThai(TrangThaiDangKy.DA_CHAP_NHAN);

        when(dkRepo.findById(dkId)).thenReturn(Optional.of(dk));
        when(lopRepo.findById(lopId)).thenReturn(Optional.of(lop));
        when(ctRepo.findById(ctId)).thenReturn(Optional.of(ct));
        when(dgRepo.save(any())).thenAnswer(inv -> {
            DanhGiaSauDaoTao d = inv.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });
        when(dkRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DanhGiaSauDto dto = new DanhGiaSauDto();
        dto.setDangKyId(dkId);
        dto.setDiemNoiDung(new BigDecimal("85"));
        dto.setDiemGiangVien(new BigDecimal("80"));
        dto.setDiemThucHanh(new BigDecimal("90"));

        DanhGiaSauDto out = service.danhGia(dto, UUID.randomUUID());
        // 85*0.4 + 80*0.3 + 90*0.3 = 34 + 24 + 27 = 85
        assertThat(out.getDiemTrungBinh()).isEqualByComparingTo("85.0");
        assertThat(out.getKetQua()).isEqualTo(KetQuaDanhGia.TOT);
        assertThat(dk.getChungChiCap()).isEqualTo("IOSH");
    }
}
