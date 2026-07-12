package com.company.hrm.hr.offboarding.service;

import com.company.hrm.hr.offboarding.entity.LoaiTaskOffboarding;
import com.company.hrm.hr.offboarding.entity.OffboardingCase;
import com.company.hrm.hr.offboarding.entity.OffboardingTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Tao checklist mac dinh cho offboarding case theo tung ly do nghi.
 *
 * <p>Moi ly do co 1 bo checklist rieng de dam bao khong bo sot task quan trong
 * (vi du: nghi huu -> KHONG co tro cap thoi viec, nhung phai chot so BHXH som hon).
 */
@Component
public class OffboardingTaskTemplateService {

    public List<OffboardingTask> defaultChecklistFor(OffboardingCase c) {
        List<OffboardingTask> tasks = new ArrayList<>();

        // Tasks chung (cho moi ly do)
        addTask(tasks, c, LoaiTaskOffboarding.TRA_TAI_SAN, "Thu hoi laptop, the ra vao, CMND ban sao", 1);
        addTask(tasks, c, LoaiTaskOffboarding.BAN_GIAO_CONG_VIEC, "Ban giao cong viec cho nguoi ke nhiem", 2);
        addTask(tasks, c, LoaiTaskOffboarding.THU_HOI_QUYEN_TRUY_CAP, "Vo hieu email, tai khoan he thong, VPN", 3);
        addTask(tasks, c, LoaiTaskOffboarding.PHEP_NAM_CON_DU, "Kiem tra phep nam con du (neu co)", 4);
        addTask(tasks, c, LoaiTaskOffboarding.KY_VAN_BANG_LUONG, "Ky van bang luong cuoi cung", 5);
        addTask(tasks, c, LoaiTaskOffboarding.QUYET_TOAN_THUE_TNCN, "Quyet toan thue TNCN cuoi cung (neu can)", 6);
        addTask(tasks, c, LoaiTaskOffboarding.BAO_GIAM_BHXH, "Bao giam BHXH (mau D02-G)", 7);
        addTask(tasks, c, LoaiTaskOffboarding.CHOT_SO_BHXH_D07, "Chot so BHXH (mau D07)", 8);
        addTask(tasks, c, LoaiTaskOffboarding.PHONG_VAN_THAM_PHONG, "Phong van tham phong (exit interview)", 9);
        addTask(tasks, c, LoaiTaskOffboarding.XAC_NHAN_KHONG_NO, "Xac nhan khong con khoan no cong ty", 10);

        // Tasks theo ly do cu the
        switch (c.getLyDo()) {
            case NGHI_VIEC_TU_NGUYEN, HET_HAN_HDLD, THOI_VIEC -> {
                addTask(tasks, c, LoaiTaskOffboarding.TRA_CCCD,
                        "Tra CMND/CCCD ban goc (neu phong ban dang giu)", 11);
            }
            case SA_THAI -> {
                addTask(tasks, c, LoaiTaskOffboarding.TRA_CCCD,
                        "Tra CMND/CCCD ban goc va cac giay to lien quan", 11);
            }
            case NGHI_HUU -> {
                addTask(tasks, c, LoaiTaskOffboarding.CHOT_SO_BHXH_D07,
                        "Ho so huu tri - chot so som hon 6 thang", 7);
            }
            default -> {
                addTask(tasks, c, LoaiTaskOffboarding.KHAC, "Tham khao HR de biet them task phu hop", 99);
            }
        }
        return tasks;
    }

    private void addTask(List<OffboardingTask> out, OffboardingCase c, LoaiTaskOffboarding loai,
                         String moTa, int thuTu) {
        OffboardingTask t = new OffboardingTask();
        t.setCaseId(c.getCaseId());
        t.setLoaiTask(loai);
        t.setMoTa(moTa);
        t.setThuTu(thuTu);
        out.add(t);
    }
}
