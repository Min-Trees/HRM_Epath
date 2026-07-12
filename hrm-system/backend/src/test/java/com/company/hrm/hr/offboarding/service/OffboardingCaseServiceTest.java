package com.company.hrm.hr.offboarding.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.offboarding.dto.OffboardingCaseDto;
import com.company.hrm.hr.offboarding.entity.LyDoNghiViec;
import com.company.hrm.hr.offboarding.entity.OffboardingCase;
import com.company.hrm.hr.offboarding.entity.TrangThaiOffboarding;
import com.company.hrm.hr.offboarding.repository.OffboardingCaseRepository;
import com.company.hrm.hr.offboarding.repository.OffboardingTaskRepository;
import com.company.hrm.hr.repository.BienDongNhanSuRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * T14 - Unit test cho OffboardingCaseService.
 *
 * <p>Focus vao state machine + finalize side-effects.
 */
@ExtendWith(MockitoExtension.class)
class OffboardingCaseServiceTest {

    @Mock OffboardingCaseRepository caseRepo;
    @Mock OffboardingTaskRepository taskRepo;
    @Mock NhanVienRepository nhanVienRepo;
    @Mock BienDongNhanSuRepository bienDongRepo;
    @Mock ApplicationEventPublisher events;
    @Mock OffboardingTaskTemplateService templateService;

    @InjectMocks OffboardingCaseService service;

    private NhanVien nv;
    private OffboardingCase activeCase;

    @BeforeEach
    void setUp() {
        nv = new NhanVien();
        nv.setNhanVienId(UUID.randomUUID());
        nv.setMaNv("NV-001");
        nv.setHoTen("Nguyen Van A");
        nv.setTrangThai(NhanVien.TrangThaiNv.CHINH_THUC);

        activeCase = new OffboardingCase();
        activeCase.setCaseId(UUID.randomUUID());
        activeCase.setNhanVienId(nv.getNhanVienId());
        activeCase.setSoQuyetDinh("QDD-2026-001");
        activeCase.setNgayQuyetDinh(LocalDate.of(2026, 6, 1));
        activeCase.setNgayNghiViecCuoi(LocalDate.of(2026, 6, 30));
        activeCase.setNgayChinhThucNghi(LocalDate.of(2026, 7, 1));
        activeCase.setLyDo(LyDoNghiViec.NGHI_VIEC_TU_NGUYEN);
        activeCase.setTrangThai(TrangThaiOffboarding.MOI_TAO);
    }

    @Test
    void createCase_taoMoiVaSinhChecklist() {
        when(caseRepo.findByNhanVienIdAndTrangThaiNotIn(any(), any())).thenReturn(Optional.empty());
        when(nhanVienRepo.findById(nv.getNhanVienId())).thenReturn(Optional.of(nv));
        when(caseRepo.save(any(OffboardingCase.class))).thenAnswer(inv -> {
            OffboardingCase c = inv.getArgument(0);
            if (c.getCaseId() == null) c.setCaseId(UUID.randomUUID());
            return c;
        });
        when(templateService.defaultChecklistFor(any())).thenReturn(java.util.List.of());

        OffboardingCaseDto dto = new OffboardingCaseDto();
        dto.setNhanVienId(nv.getNhanVienId());
        dto.setSoQuyetDinh("QDD-2026-001");
        dto.setNgayQuyetDinh(LocalDate.of(2026, 6, 1));
        dto.setNgayNghiViecCuoi(LocalDate.of(2026, 6, 30));
        dto.setNgayChinhThucNghi(LocalDate.of(2026, 7, 1));
        dto.setLyDo(LyDoNghiViec.NGHI_VIEC_TU_NGUYEN);

        OffboardingCaseDto result = service.create(dto, UUID.randomUUID());

        assertThat(result.getCaseId()).isNotNull();
        assertThat(result.getTrangThai()).isEqualTo(TrangThaiOffboarding.MOI_TAO);
        verify(templateService).defaultChecklistFor(any());
    }

    @Test
    void createCase_nhanVienDaCoCaseActive_throw() {
        when(caseRepo.findByNhanVienIdAndTrangThaiNotIn(any(), any())).thenReturn(Optional.of(activeCase));
        when(nhanVienRepo.findById(nv.getNhanVienId())).thenReturn(Optional.of(nv));

        OffboardingCaseDto dto = new OffboardingCaseDto();
        dto.setNhanVienId(nv.getNhanVienId());

        assertThatThrownBy(() -> service.create(dto, UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("OFFBOARDING_ACTIVE_EXISTS"));
        verify(caseRepo, never()).save(any());
    }

    @Test
    void finalizeCase_hoanThanh_capNhatTrangThaiNvVaTaoBienDong() {
        activeCase.setTrangThai(TrangThaiOffboarding.CHO_QUYET_TOAN);
        when(caseRepo.findById(activeCase.getCaseId())).thenReturn(Optional.of(activeCase));
        when(caseRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(taskRepo.findByCaseIdOrderByThuTuAscCreatedAtAsc(any())).thenReturn(java.util.List.of());
        when(nhanVienRepo.findById(nv.getNhanVienId())).thenReturn(Optional.of(nv));

        service.updateStatus(activeCase.getCaseId(), TrangThaiOffboarding.HOAN_THANH, UUID.randomUUID());

        assertThat(nv.getTrangThai()).isEqualTo(NhanVien.TrangThaiNv.DA_NGHI_VIEC);
        verify(nhanVienRepo).save(nv);

        ArgumentCaptor<com.company.hrm.hr.entity.BienDongNhanSu> bdCaptor =
                ArgumentCaptor.forClass(com.company.hrm.hr.entity.BienDongNhanSu.class);
        verify(bienDongRepo).save(bdCaptor.capture());
        assertThat(bdCaptor.getValue().getLoaiBienDong())
                .isEqualTo(com.company.hrm.hr.entity.BienDongNhanSu.LoaiBienDong.CHAM_DUT_HDLD);
    }

    @Test
    void finalizeCase_nghiHuu_setTrangThaiDaNghiHuu() {
        activeCase.setLyDo(LyDoNghiViec.NGHI_HUU);
        activeCase.setTrangThai(TrangThaiOffboarding.CHO_QUYET_TOAN);
        when(caseRepo.findById(activeCase.getCaseId())).thenReturn(Optional.of(activeCase));
        when(caseRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(taskRepo.findByCaseIdOrderByThuTuAscCreatedAtAsc(any())).thenReturn(java.util.List.of());
        when(nhanVienRepo.findById(nv.getNhanVienId())).thenReturn(Optional.of(nv));

        service.updateStatus(activeCase.getCaseId(), TrangThaiOffboarding.HOAN_THANH, UUID.randomUUID());

        assertThat(nv.getTrangThai()).isEqualTo(NhanVien.TrangThaiNv.DA_NGHI_HUU);
    }

    @Test
    void updateStatus_chuyenTrangThaiKhongHopLe_throw() {
        activeCase.setTrangThai(TrangThaiOffboarding.HOAN_THANH);
        when(caseRepo.findById(activeCase.getCaseId())).thenReturn(Optional.of(activeCase));

        assertThatThrownBy(() ->
                service.updateStatus(activeCase.getCaseId(), TrangThaiOffboarding.DANG_THUC_HIEN, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("OFFBOARDING_INVALID_TRANSITION"));
    }
}
