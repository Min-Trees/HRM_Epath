package com.company.hrm.payroll.run.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.payroll.run.dto.KyLinhLuongDto;
import com.company.hrm.payroll.run.entity.AuditKyLuong;
import com.company.hrm.payroll.run.entity.KhoanLuong;
import com.company.hrm.payroll.run.entity.KyLinhLuong;
import com.company.hrm.payroll.run.entity.TrangThaiKyLuong;
import com.company.hrm.payroll.run.repository.AuditKyLuongRepository;
import com.company.hrm.payroll.run.repository.KhoanLuongRepository;
import com.company.hrm.payroll.run.repository.KyLinhLuongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KyLinhLuongServiceTest {

    @Mock KyLinhLuongRepository repo;
    @Mock AuditKyLuongRepository auditRepo;
    @Mock PayslipGenerator payslipGenerator;
    @Mock KhoanLuongRepository khoanRepo;
    @Mock NhanVienRepository nvRepo;
    KyLinhLuongService service;

    @BeforeEach
    void setUp() {
        service = new KyLinhLuongService(repo, auditRepo, payslipGenerator);
    }

    @Test
    void create_thangKhongHopLe_throwException() {
        KyLinhLuongDto dto = new KyLinhLuongDto();
        dto.setThang(13);
        dto.setNam(2026);
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_THANG"));
    }

    @Test
    void create_kyLinhTrungNam_throwException() {
        KyLinhLuongDto dto = new KyLinhLuongDto();
        dto.setThang(5);
        dto.setNam(2026);
        when(repo.findByThangAndNam(5, 2026)).thenReturn(Optional.of(new KyLinhLuong()));
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("KY_LUONG_EXIST"));
    }

    @Test
    void create_thanhCong() {
        when(repo.findByThangAndNam(any(), any())).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> {
            KyLinhLuong k = inv.getArgument(0);
            k.setKyLinhId(UUID.randomUUID());
            return k;
        });

        KyLinhLuongDto dto = new KyLinhLuongDto();
        dto.setThang(5);
        dto.setNam(2026);
        KyLinhLuongDto out = service.create(dto);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiKyLuong.CHO_CHAY);
        assertThat(out.getMaKyLinh()).startsWith("KL-");
    }

    @Test
    void startRun_chuyenQuaDA_CHAY_trongMotLan() {
        KyLinhLuong e = new KyLinhLuong();
        e.setKyLinhId(UUID.randomUUID());
        e.setTrangThai(TrangThaiKyLuong.CHO_CHAY);
        when(repo.findById(e.getKyLinhId())).thenReturn(Optional.of(e));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        KyLinhLuongDto out = service.startRun(e.getKyLinhId(), UUID.randomUUID());
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiKyLuong.DA_CHAY);
        assertThat(out.getNgayChay()).isNotNull();
    }

    @Test
    void startRun_tuDangChay_throwException() {
        KyLinhLuong e = new KyLinhLuong();
        e.setKyLinhId(UUID.randomUUID());
        e.setTrangThai(TrangThaiKyLuong.DANG_CHAY);
        when(repo.findById(e.getKyLinhId())).thenReturn(Optional.of(e));
        assertThatThrownBy(() -> service.startRun(e.getKyLinhId(), UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_STATE"));
    }

    @Test
    void approveCap1_canChuyenTuDA_CHAY() {
        KyLinhLuong e = new KyLinhLuong();
        e.setKyLinhId(UUID.randomUUID());
        e.setTrangThai(TrangThaiKyLuong.DA_CHAY);
        when(repo.findById(e.getKyLinhId())).thenReturn(Optional.of(e));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID approver = UUID.randomUUID();
        KyLinhLuongDto out = service.approveCap1(e.getKyLinhId(), approver);
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiKyLuong.DA_DUYET_CAP_1);
        assertThat(out.getNguoiDuyetCap1Id()).isEqualTo(approver);
    }

    @Test
    void approveCap2_canChuyenTuDA_DUYET_CAP_1() {
        KyLinhLuong e = new KyLinhLuong();
        e.setKyLinhId(UUID.randomUUID());
        e.setTrangThai(TrangThaiKyLuong.DA_DUYET_CAP_1);
        when(repo.findById(e.getKyLinhId())).thenReturn(Optional.of(e));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        KyLinhLuongDto out = service.approveCap2(e.getKyLinhId(), UUID.randomUUID());
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiKyLuong.DA_DUYET_CAP_2);
    }

    @Test
    void payPaid_canChuyenTuDA_DUYET_CAP_2() {
        KyLinhLuong e = new KyLinhLuong();
        e.setKyLinhId(UUID.randomUUID());
        e.setTrangThai(TrangThaiKyLuong.DA_DUYET_CAP_2);
        when(repo.findById(e.getKyLinhId())).thenReturn(Optional.of(e));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        KyLinhLuongDto out = service.payrollPaid(e.getKyLinhId(), UUID.randomUUID(),
                "https://example.com/transfer.zip");
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiKyLuong.DA_CHI_TRA);
        assertThat(out.getFileZipUrl()).contains(".zip");
        assertThat(out.getNgayChiTraThucTe()).isNotNull();
    }

    @Test
    void cancel_tuDA_CHI_TRA_throwException() {
        KyLinhLuong e = new KyLinhLuong();
        e.setKyLinhId(UUID.randomUUID());
        e.setTrangThai(TrangThaiKyLuong.DA_CHI_TRA);
        when(repo.findById(e.getKyLinhId())).thenReturn(Optional.of(e));
        assertThatThrownBy(() -> service.cancel(e.getKyLinhId(), UUID.randomUUID(), "test"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("CANNOT_CANCEL"));
    }

    @Test
    void cancel_thanhCong() {
        KyLinhLuong e = new KyLinhLuong();
        e.setKyLinhId(UUID.randomUUID());
        e.setTrangThai(TrangThaiKyLuong.DA_CHAY);
        when(repo.findById(e.getKyLinhId())).thenReturn(Optional.of(e));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        KyLinhLuongDto out = service.cancel(e.getKyLinhId(), UUID.randomUUID(), "Sai so lieu");
        assertThat(out.getTrangThai()).isEqualTo(TrangThaiKyLuong.HUY);
    }
}
