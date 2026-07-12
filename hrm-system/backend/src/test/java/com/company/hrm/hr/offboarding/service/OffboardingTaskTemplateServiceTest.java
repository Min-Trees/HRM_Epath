package com.company.hrm.hr.offboarding.service;

import com.company.hrm.hr.offboarding.entity.LyDoNghiViec;
import com.company.hrm.hr.offboarding.entity.LoaiTaskOffboarding;
import com.company.hrm.hr.offboarding.entity.OffboardingCase;
import com.company.hrm.hr.offboarding.entity.OffboardingTask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T14 - Unit test cho OffboardingTaskTemplateService.
 *
 * <p>Kiem tra checklist mac dinh co it nhat cac task chung, va co them
 * task dac thu theo tung ly do.
 */
class OffboardingTaskTemplateServiceTest {

    private final OffboardingTaskTemplateService svc = new OffboardingTaskTemplateService();

    @Test
    void nghiTuNguyen_coTaskChungVaKhongCoTaskSaThai() {
        OffboardingCase c = caseMoi(LyDoNghiViec.NGHI_VIEC_TU_NGUYEN);
        List<OffboardingTask> tasks = svc.defaultChecklistFor(c);

        assertThat(tasks).isNotEmpty();
        assertThat(tasks).extracting(OffboardingTask::getLoaiTask)
                .contains(LoaiTaskOffboarding.TRA_TAI_SAN)
                .contains(LoaiTaskOffboarding.BAO_GIAM_BHXH)
                .contains(LoaiTaskOffboarding.QUYET_TOAN_THUE_TNCN);
    }

    @Test
    void saThai_coTaskTraCCCD() {
        OffboardingCase c = caseMoi(LyDoNghiViec.SA_THAI);
        List<OffboardingTask> tasks = svc.defaultChecklistFor(c);

        assertThat(tasks).extracting(OffboardingTask::getLoaiTask)
                .contains(LoaiTaskOffboarding.TRA_CCCD);
    }

    @Test
    void nghiHuu_coTaskChotSoBHXHSomHon() {
        OffboardingCase c = caseMoi(LyDoNghiViec.NGHI_HUU);
        List<OffboardingTask> tasks = svc.defaultChecklistFor(c);

        long soLuongChotSo = tasks.stream()
                .filter(t -> t.getLoaiTask() == LoaiTaskOffboarding.CHOT_SO_BHXH_D07)
                .count();
        assertThat(soLuongChotSo).isEqualTo(2);
    }

    @Test
    void taskDuocGanCaseIdVaThuTu() {
        OffboardingCase c = caseMoi(LyDoNghiViec.NGHI_VIEC_TU_NGUYEN);
        List<OffboardingTask> tasks = svc.defaultChecklistFor(c);

        assertThat(tasks).allMatch(t -> t.getCaseId().equals(c.getCaseId()));
        assertThat(tasks).allMatch(t -> t.getThuTu() != null && t.getThuTu() > 0);
    }

    private OffboardingCase caseMoi(LyDoNghiViec lyDo) {
        OffboardingCase c = new OffboardingCase();
        c.setCaseId(UUID.randomUUID());
        c.setLyDo(lyDo);
        return c;
    }
}
