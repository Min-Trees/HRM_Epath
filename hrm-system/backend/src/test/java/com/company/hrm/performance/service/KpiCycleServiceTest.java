package com.company.hrm.performance.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.performance.dto.KpiCycleDto;
import com.company.hrm.performance.entity.KpiCycle;
import com.company.hrm.performance.entity.TrangThaiChuKy;
import com.company.hrm.performance.repository.KpiAssignmentRepository;
import com.company.hrm.performance.repository.KpiCycleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KpiCycleServiceTest {

    @Mock KpiCycleRepository repo;
    @Mock KpiAssignmentRepository assignRepo;
    KpiCycleService service;

    @BeforeEach
    void setUp() {
        service = new KpiCycleService(repo, assignRepo);
    }

    @Test
    void create_ngaySai_throwException() {
        KpiCycleDto dto = new KpiCycleDto();
        dto.setTenChuKy("Q1-2026");
        dto.setLoaiChuKy("QUARTER");
        dto.setNgayBatDau(LocalDate.of(2026, 4, 1));
        dto.setNgayKetThuc(LocalDate.of(2026, 1, 1));
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_NGAY"));
    }

    @Test
    void create_thanhCong() {
        when(repo.save(any())).thenAnswer(inv -> {
            KpiCycle c = inv.getArgument(0);
            c.setCycleId(UUID.randomUUID());
            return c;
        });
        when(assignRepo.findByCycleId(any())).thenReturn(List.of());

        KpiCycleDto dto = new KpiCycleDto();
        dto.setTenChuKy("Q1-2026");
        dto.setLoaiChuKy("QUARTER");
        dto.setNgayBatDau(LocalDate.of(2026, 1, 1));
        dto.setNgayKetThuc(LocalDate.of(2026, 3, 31));
        KpiCycleDto out = service.create(dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiChuKy.MOI_TAO);
        assertThat(out.getSoMucTieu()).isZero();
    }

    @Test
    void startCycle_canChuyenTuMoiTao() {
        KpiCycle c = new KpiCycle();
        c.setCycleId(UUID.randomUUID());
        c.setTrangThai(TrangThaiChuKy.MOI_TAO);
        when(repo.findById(c.getCycleId())).thenReturn(Optional.of(c));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(assignRepo.findByCycleId(any())).thenReturn(List.of());

        KpiCycleDto out = service.startCycle(c.getCycleId());
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiChuKy.DANG_DANH_GIA);
    }

    @Test
    void startCycle_tuDangDanhGia_throwException() {
        KpiCycle c = new KpiCycle();
        c.setCycleId(UUID.randomUUID());
        c.setTrangThai(TrangThaiChuKy.DANG_DANH_GIA);
        when(repo.findById(c.getCycleId())).thenReturn(Optional.of(c));
        assertThatThrownBy(() -> service.startCycle(c.getCycleId()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_STATE"));
    }

    @Test
    void closeCycle_thanhCong() {
        KpiCycle c = new KpiCycle();
        c.setCycleId(UUID.randomUUID());
        c.setTrangThai(TrangThaiChuKy.DANG_DANH_GIA);
        when(repo.findById(c.getCycleId())).thenReturn(Optional.of(c));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(assignRepo.findByCycleId(any())).thenReturn(List.of());
        KpiCycleDto out = service.closeCycle(c.getCycleId());
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiChuKy.DA_DONG);
    }
}
