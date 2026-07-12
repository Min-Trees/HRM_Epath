package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.LichPhongVanDto;
import com.company.hrm.recruitment.entity.LichPhongVan;
import com.company.hrm.recruitment.entity.TrangThaiLichPV;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.repository.LichPhongVanRepository;
import com.company.hrm.recruitment.repository.UngVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LichPhongVanServiceTest {

    @Mock LichPhongVanRepository repo;
    @Mock UngVienRepository uvRepo;
    LichPhongVanService service;

    @BeforeEach
    void setUp() {
        service = new LichPhongVanService(repo, uvRepo);
    }

    @Test
    void schedule_thoiGianSai_throwException() {
        LichPhongVanDto dto = new LichPhongVanDto();
        dto.setUngVienId(UUID.randomUUID());
        dto.setVongPhongVan(1);
        dto.setThoiGianBatDau(LocalDateTime.of(2026, 5, 1, 10, 0));
        dto.setThoiGianKetThuc(LocalDateTime.of(2026, 5, 1, 9, 0));
        assertThatThrownBy(() -> service.schedule(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_TIME"));
    }

    @Test
    void schedule_ungVienKhongTonTai_throwException() {
        LichPhongVanDto dto = new LichPhongVanDto();
        dto.setUngVienId(UUID.randomUUID());
        dto.setVongPhongVan(1);
        dto.setThoiGianBatDau(LocalDateTime.of(2026, 5, 1, 10, 0));
        dto.setThoiGianKetThuc(LocalDateTime.of(2026, 5, 1, 11, 0));
        when(uvRepo.findById(dto.getUngVienId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.schedule(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("UV_NOT_FOUND"));
    }

    @Test
    void schedule_vong1_thanhCong() {
        UngVien uv = new UngVien();
        uv.setUngVienId(UUID.randomUUID());
        when(uvRepo.findById(any())).thenReturn(Optional.of(uv));
        when(repo.save(any())).thenAnswer(inv -> {
            LichPhongVan l = inv.getArgument(0);
            l.setLichPvId(UUID.randomUUID());
            return l;
        });
        when(uvRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LichPhongVanDto dto = new LichPhongVanDto();
        dto.setUngVienId(uv.getUngVienId());
        dto.setVongPhongVan(1);
        dto.setThoiGianBatDau(LocalDateTime.of(2026, 5, 1, 10, 0));
        dto.setThoiGianKetThuc(LocalDateTime.of(2026, 5, 1, 11, 0));
        dto.setDiaDiem("Phong hop A");

        LichPhongVanDto out = service.schedule(dto);
        assertThat(out.getLichPvId()).isNotNull();
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiLichPV.CHUA_DIEN_RA);
        assertThat(out.getTenUngVien()).isNull();  // vi ta stub lazy findById = null
    }
}
