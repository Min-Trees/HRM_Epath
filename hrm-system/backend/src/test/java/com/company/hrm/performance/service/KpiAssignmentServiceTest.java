package com.company.hrm.performance.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.performance.dto.KpiAssignmentDto;
import com.company.hrm.performance.dto.KpiFinalRatingDto;
import com.company.hrm.performance.dto.KpiReviewDto;
import com.company.hrm.performance.dto.KpiSelfAssessmentDto;
import com.company.hrm.performance.entity.*;
import com.company.hrm.performance.repository.*;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KpiAssignmentServiceTest {

    @Mock KpiAssignmentRepository repo;
    @Mock KpiCycleRepository cycleRepo;
    @Mock KpiSelfAssessmentRepository selfRepo;
    @Mock KpiReviewRepository reviewRepo;
    @Mock KpiFinalRatingRepository finalRepo;
    @Mock NhanVienRepository nhanVienRepo;
    KpiAssignmentService service;

    @BeforeEach
    void setUp() {
        service = new KpiAssignmentService(repo, cycleRepo, selfRepo, reviewRepo, finalRepo, nhanVienRepo);
    }

    @Test
    void create_cycleDong_throwException() {
        KpiCycle cycle = new KpiCycle();
        cycle.setCycleId(UUID.randomUUID());
        cycle.setTrangThai(TrangThaiChuKy.DA_DONG);
        when(cycleRepo.findById(cycle.getCycleId())).thenReturn(Optional.of(cycle));

        KpiAssignmentDto dto = new KpiAssignmentDto();
        dto.setCycleId(cycle.getCycleId());
        dto.setNhanVienId(UUID.randomUUID());
        dto.setTenMucTieu("Tang doanh thu 20%");
        dto.setTargetValue(new BigDecimal("20"));
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("CYCLE_CLOSED"));
    }

    @Test
    void create_thanhCong() {
        KpiCycle cycle = new KpiCycle();
        cycle.setCycleId(UUID.randomUUID());
        cycle.setTrangThai(TrangThaiChuKy.DANG_DANH_GIA);
        when(cycleRepo.findById(cycle.getCycleId())).thenReturn(Optional.of(cycle));
        when(repo.save(any())).thenAnswer(inv -> {
            KpiAssignment a = inv.getArgument(0);
            a.setAssignmentId(UUID.randomUUID());
            return a;
        });
        when(selfRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(reviewRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(finalRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(nhanVienRepo.findById(any())).thenReturn(Optional.empty());

        KpiAssignmentDto dto = new KpiAssignmentDto();
        dto.setCycleId(cycle.getCycleId());
        dto.setNhanVienId(UUID.randomUUID());
        dto.setTenMucTieu("Tang doanh thu 20%");
        dto.setTargetValue(new BigDecimal("20"));
        dto.setDonViDo("%");
        KpiAssignmentDto out = service.create(dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiAssignment.MOI_GAN);
        assertThat(out.getTrongSo()).isEqualByComparingTo("1");
    }

    @Test
    void selfAssess_diemVuotMuc_throwException() {
        KpiAssignment a = new KpiAssignment();
        a.setAssignmentId(UUID.randomUUID());
        a.setTrangThai(TrangThaiAssignment.MOI_GAN);
        when(repo.findById(a.getAssignmentId())).thenReturn(Optional.of(a));

        KpiSelfAssessmentDto sa = new KpiSelfAssessmentDto();
        sa.setDiemTuDanhGia(new BigDecimal("150"));
        assertThatThrownBy(() -> service.selfAssess(a.getAssignmentId(), sa))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_DIEM"));
    }

    @Test
    void selfAssess_chuyenTrangThai() {
        KpiAssignment a = new KpiAssignment();
        a.setAssignmentId(UUID.randomUUID());
        a.setTrangThai(TrangThaiAssignment.MOI_GAN);
        when(repo.findById(a.getAssignmentId())).thenReturn(Optional.of(a));
        when(selfRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(selfRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(reviewRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(finalRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(nhanVienRepo.findById(any())).thenReturn(Optional.empty());

        KpiSelfAssessmentDto sa = new KpiSelfAssessmentDto();
        sa.setDiemTuDanhGia(new BigDecimal("85"));
        sa.setTyLeHoanThanh(new BigDecimal("95"));
        KpiAssignmentDto out = service.selfAssess(a.getAssignmentId(), sa);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiAssignment.NV_DA_TU_DANH_GIA);
    }

    @Test
    void managerReview_chuaTuDanhGia_throwException() {
        KpiAssignment a = new KpiAssignment();
        a.setAssignmentId(UUID.randomUUID());
        a.setTrangThai(TrangThaiAssignment.MOI_GAN);
        when(repo.findById(a.getAssignmentId())).thenReturn(Optional.of(a));

        KpiReviewDto dto = new KpiReviewDto();
        dto.setDiemManager(new BigDecimal("80"));
        assertThatThrownBy(() -> service.managerReview(a.getAssignmentId(), dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_STATE"));
    }

    @Test
    void managerReview_chuyenTrangThai() {
        KpiAssignment a = new KpiAssignment();
        a.setAssignmentId(UUID.randomUUID());
        a.setTrangThai(TrangThaiAssignment.NV_DA_TU_DANH_GIA);
        when(repo.findById(a.getAssignmentId())).thenReturn(Optional.of(a));
        when(reviewRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(selfRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(reviewRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(finalRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(nhanVienRepo.findById(any())).thenReturn(Optional.empty());

        KpiReviewDto dto = new KpiReviewDto();
        dto.setNguoiReviewId(UUID.randomUUID());
        dto.setDiemManager(new BigDecimal("90"));
        dto.setDeXuatXepLoai(XepLoai.A);
        KpiAssignmentDto out = service.managerReview(a.getAssignmentId(), dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiAssignment.MANAGER_DA_REVIEW);
    }

    @Test
    void hrApprove_thanhCong() {
        KpiAssignment a = new KpiAssignment();
        a.setAssignmentId(UUID.randomUUID());
        a.setTrangThai(TrangThaiAssignment.MANAGER_DA_REVIEW);
        when(repo.findById(a.getAssignmentId())).thenReturn(Optional.of(a));
        when(finalRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(selfRepo.findByAssignmentId(any())).thenReturn(Optional.empty());
        when(reviewRepo.findByAssignmentId(any())).thenReturn(Optional.empty());

        // Sau khi hrApprove save, final rating da ton tai -> tra Optional.of
        KpiFinalRating fr = new KpiFinalRating();
        fr.setAssignmentId(a.getAssignmentId());
        fr.setXepLoaiCuoi(XepLoai.A);
        fr.setDiemCuoi(new BigDecimal("92"));
        when(finalRepo.findByAssignmentId(a.getAssignmentId())).thenReturn(Optional.of(fr));
        when(nhanVienRepo.findById(any())).thenReturn(Optional.empty());

        KpiFinalRatingDto dto = new KpiFinalRatingDto();
        dto.setNguoiPheDuyetId(UUID.randomUUID());
        dto.setXepLoaiCuoi(XepLoai.A);
        dto.setDiemCuoi(new BigDecimal("92"));
        dto.setHeSoThuong(new BigDecimal("1.5"));
        KpiAssignmentDto out = service.hrApprove(a.getAssignmentId(), dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiAssignment.HR_DA_PHE_DUYET);
        assertThat(out.getXepLoaiCuoi()).isEqualTo("A");
    }
}
