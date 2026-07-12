package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.UngVienDto;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.repository.UngVienRepository;
import com.company.hrm.recruitment.repository.YeuCauTuyenDungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UngVienServiceTest {

    @Mock UngVienRepository repo;
    @Mock YeuCauTuyenDungRepository ycRepo;
    UngVienService service;

    @BeforeEach
    void setUp() {
        service = new UngVienService(repo, ycRepo);
    }

    @Test
    void create_hoTenTrong_throwException() {
        UngVienDto dto = new UngVienDto();
        dto.setHoTen("");
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_HO_TEN"));
    }

    @Test
    void create_yeuCauKhongTonTai_throwException() {
        UngVienDto dto = new UngVienDto();
        dto.setHoTen("Test");
        dto.setYeuCauId(UUID.randomUUID());
        when(ycRepo.findById(dto.getYeuCauId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("YC_NOT_FOUND"));
    }

    @Test
    void create_thanhCong() {
        when(repo.save(any())).thenAnswer(inv -> {
            UngVien u = inv.getArgument(0);
            u.setUngVienId(UUID.randomUUID());
            return u;
        });

        UngVienDto dto = new UngVienDto();
        dto.setHoTen("Nguyen Van A");
        dto.setEmail("a@test.com");

        UngVienDto out = service.create(dto);
        assertThat(out.getUngVienId()).isNotNull();
        assertThat(out.getMaUngVien()).startsWith("UV-");
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiUngVien.MOI_NOP_HO_SO);
    }

    @Test
    void updateStatus_thanhCong() {
        UngVien uv = new UngVien();
        uv.setUngVienId(UUID.randomUUID());
        uv.setTrangThai(TrangThaiUngVien.MOI_NOP_HO_SO);
        when(repo.findById(uv.getUngVienId())).thenReturn(Optional.of(uv));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UngVienDto out = service.updateStatus(uv.getUngVienId(), TrangThaiUngVien.CHO_PHONG_VAN_VONG_1);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiUngVien.CHO_PHONG_VAN_VONG_1);
    }

    @Test
    void findById_khongTonTai_throwException() {
        when(repo.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("UV_NOT_FOUND"));
    }
}
