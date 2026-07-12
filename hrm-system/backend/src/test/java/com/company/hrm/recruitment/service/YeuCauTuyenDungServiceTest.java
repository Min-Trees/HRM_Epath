package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.YeuCauTuyenDungDto;
import com.company.hrm.recruitment.entity.TrangThaiYeuCau;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.entity.YeuCauTuyenDung;
import com.company.hrm.recruitment.repository.UngVienRepository;
import com.company.hrm.recruitment.repository.YeuCauTuyenDungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YeuCauTuyenDungServiceTest {

    @Mock YeuCauTuyenDungRepository repo;
    @Mock UngVienRepository uvRepo;
    YeuCauTuyenDungService service;

    @BeforeEach
    void setUp() {
        service = new YeuCauTuyenDungService(repo, uvRepo);
    }

    @Test
    void create_soLuong0_throwException() {
        YeuCauTuyenDungDto dto = new YeuCauTuyenDungDto();
        dto.setTieuDe("Tuyen dev");
        dto.setPhongBanId(UUID.randomUUID());
        dto.setNguoiYeuCauId(UUID.randomUUID());
        dto.setSoLuongCan(0);
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_SO_LUONG"));
    }

    @Test
    void create_taoMoi_thanhCong() {
        when(repo.save(any())).thenAnswer(inv -> {
            YeuCauTuyenDung yc = inv.getArgument(0);
            yc.setYeuCauId(UUID.randomUUID());
            return yc;
        });
        when(uvRepo.findByYeuCauId(any())).thenReturn(List.of());

        YeuCauTuyenDungDto dto = new YeuCauTuyenDungDto();
        dto.setTieuDe("Tuyen dev");
        dto.setPhongBanId(UUID.randomUUID());
        dto.setNguoiYeuCauId(UUID.randomUUID());
        dto.setSoLuongCan(2);

        YeuCauTuyenDungDto out = service.create(dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiYeuCau.MOI_TAO);
        assertThat(out.getMaYeuCau()).startsWith("YC-");
    }

    @Test
    void submitFromInvalidState_throwException() {
        YeuCauTuyenDung yc = new YeuCauTuyenDung();
        yc.setYeuCauId(UUID.randomUUID());
        yc.setTrangThai(TrangThaiYeuCau.DA_PHE_DUYET);
        when(repo.findById(yc.getYeuCauId())).thenReturn(Optional.of(yc));
        assertThatThrownBy(() -> service.submitForApproval(yc.getYeuCauId()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_STATE"));
    }

    @Test
    void submit_flow() {
        YeuCauTuyenDung yc = new YeuCauTuyenDung();
        yc.setYeuCauId(UUID.randomUUID());
        yc.setTrangThai(TrangThaiYeuCau.MOI_TAO);
        when(repo.findById(yc.getYeuCauId())).thenReturn(Optional.of(yc));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uvRepo.findByYeuCauId(any())).thenReturn(List.of());

        YeuCauTuyenDungDto out = service.submitForApproval(yc.getYeuCauId());
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiYeuCau.CHO_PHE_DUYET);
    }

    @Test
    void approve_setNguoiPheDuyet() {
        YeuCauTuyenDung yc = new YeuCauTuyenDung();
        yc.setYeuCauId(UUID.randomUUID());
        yc.setTrangThai(TrangThaiYeuCau.CHO_PHE_DUYET);
        when(repo.findById(yc.getYeuCauId())).thenReturn(Optional.of(yc));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uvRepo.findByYeuCauId(any())).thenReturn(List.of());

        UUID approver = UUID.randomUUID();
        YeuCauTuyenDungDto out = service.approve(yc.getYeuCauId(), approver);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiYeuCau.DA_PHE_DUYET);
        assertThat(out.getNguoiPheDuyetId()).isEqualTo(approver);
        assertThat(out.getNgayPheDuyet()).isNotNull();
    }

    @Test
    void startRecruiting_canChuyenTuDaPheDuyet() {
        YeuCauTuyenDung yc = new YeuCauTuyenDung();
        yc.setYeuCauId(UUID.randomUUID());
        yc.setTrangThai(TrangThaiYeuCau.DA_PHE_DUYET);
        when(repo.findById(yc.getYeuCauId())).thenReturn(Optional.of(yc));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uvRepo.findByYeuCauId(any())).thenReturn(List.of());

        YeuCauTuyenDungDto out = service.startRecruiting(yc.getYeuCauId());
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiYeuCau.DANG_TUYEN);
    }

    @Test
    void findById_khongTonTai_throwException() {
        when(repo.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("YC_NOT_FOUND"));
    }

    @Test
    void findAll_soUngVien_demTheoYeuCau() {
        YeuCauTuyenDung yc = new YeuCauTuyenDung();
        yc.setYeuCauId(UUID.randomUUID());
        yc.setSoLuongCan(2);
        yc.setTrangThai(TrangThaiYeuCau.DANG_TUYEN);
        yc.setTieuDe("Tuyen dev");
        when(repo.findAll()).thenReturn(List.of(yc));

        List<UngVien> uvs = new ArrayList<>();
        for (int i = 0; i < 3; i++) uvs.add(new UngVien());
        when(uvRepo.findByYeuCauId(yc.getYeuCauId())).thenReturn(uvs);

        List<YeuCauTuyenDungDto> out = service.findAll();
        assertThat(out).hasSize(1);
        assertThat(out.get(0).getSoUngVien()).isEqualTo(3);
    }
}
