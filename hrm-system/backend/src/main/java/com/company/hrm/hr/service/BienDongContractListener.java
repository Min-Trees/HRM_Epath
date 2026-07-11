package com.company.hrm.hr.service;

import com.company.hrm.hr.entity.BienDongNhanSu;
import com.company.hrm.hr.entity.BienDongNhanSu.LoaiBienDong;
import com.company.hrm.hr.entity.BienDongNhanSu.TrangThaiNvSau;
import com.company.hrm.hr.entity.HopDongLaoDong.LoaiHopDong;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.event.EmployeeStatusChangedEvent;
import com.company.hrm.hr.repository.BienDongNhanSuRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Lắng nghe {@link com.company.hrm.hr.service.HopDongService.ContractSignedEvent}
 * để tự động sinh biến động {@code TUYEN_DUNG} khi HR tạo hợp đồng qua API.
 *
 * <p>Quy tắc:
 * <ul>
 *   <li>{@code THU_VIEC} → chuyển NV từ {@code UNG_VIEN} sang {@code THU_VIEC}.</li>
 *   <li>{@code XAC_DINH_THOI_HAN} / {@code KHONG_XAC_DINH_THOI_HAN} → chuyển NV từ
 *       {@code UNG_VIEN} hoặc {@code THU_VIEC} sang {@code CHINH_THUC} (đạt thử việc).</li>
 *   <li>Phụ lục ({@code PHU_LUC}) → bỏ qua.</li>
 *   <li>NV đã ở {@code CHINH_THUC} / {@code TAM_HOAN_HDLD} / đã nghỉ → bỏ qua
 *       (HR tự tạo biến động DIEU_CHINH_LUONG / DIEU_CHUYEN nếu cần).</li>
 * </ul>
 *
 * <p>Listener chạy {@code AFTER_COMMIT} để chỉ ghi biến động khi transaction
 * của {@link HopDongService} đã commit thành công (không sinh biến động "ma"
 * khi HĐ bị rollback).
 */
@Component
public class BienDongContractListener {

    private static final Logger log = LoggerFactory.getLogger(BienDongContractListener.class);

    private final BienDongNhanSuRepository repo;
    private final NhanVienRepository nvRepo;
    private final ApplicationEventPublisher events;

    public BienDongContractListener(BienDongNhanSuRepository repo,
                                    NhanVienRepository nvRepo,
                                    ApplicationEventPublisher events) {
        this.repo = repo;
        this.nvRepo = nvRepo;
        this.events = events;
    }

    @EventListener
    @Transactional
    public void onContractSigned(HopDongService.ContractSignedEvent ev) {
        if (ev.loaiHopDong() == LoaiHopDong.PHU_LUC) {
            return; // Phụ lục không đổi trạng thái NV
        }
        Optional<NhanVien> opt = nvRepo.findById(ev.nhanVienId());
        if (opt.isEmpty()) {
            log.warn("ContractSignedEvent for missing nhan_vien_id={}", ev.nhanVienId());
            return;
        }
        NhanVien nv = opt.get();
        TrangThaiNv target = decideTarget(ev.loaiHopDong(), nv.getTrangThai());
        if (target == null || target == nv.getTrangThai()) {
            log.info("Skip auto-create TUYEN_DUNG: nv={} current={} target={}",
                    nv.getNhanVienId(), nv.getTrangThai(), target);
            return;
        }
        TrangThaiNv oldStatus = nv.getTrangThai();

        BienDongNhanSu bd = new BienDongNhanSu();
        bd.setNhanVienId(nv.getNhanVienId());
        bd.setLoaiBienDong(LoaiBienDong.TUYEN_DUNG);
        bd.setSoQuyetDinh("AUTO-" + ev.hopDongId().toString().substring(0, 8).toUpperCase());
        bd.setNgayQuyetDinh(LocalDate.now());
        bd.setNgayHieuLuc(ev.ngayHieuLuc());
        bd.setPhongBanTruocId(nv.getPhongBanId());
        bd.setTrangThaiNvSau(TrangThaiNvSau.valueOf(target.name()));
        bd.setLyDo("Tự động sinh từ ký hợp đồng " + ev.loaiHopDong());
        repo.save(bd);

        nv.setTrangThai(target);
        nvRepo.save(nv);

        events.publishEvent(new EmployeeStatusChangedEvent(
                nv.getNhanVienId(), oldStatus, target, ev.ngayHieuLuc(), LoaiBienDong.TUYEN_DUNG.name()));
        log.info("Auto-created TUYEN_DUNG movement for nv={} {} -> {}",
                nv.getNhanVienId(), oldStatus, target);
    }

    /** Quyết định trạng thái đích dựa trên loại HĐ và trạng thái hiện tại của NV. Trả về null nếu không nên auto-sinh. */
    private TrangThaiNv decideTarget(LoaiHopDong loai, TrangThaiNv current) {
        if (current == TrangThaiNv.DA_NGHI_VIEC
                || current == TrangThaiNv.DA_NGHI_HUU
                || current == TrangThaiNv.LUU_TRU) {
            return null; // NV đã nghỉ, không auto-sinh
        }
        return switch (loai) {
            case THU_VIEC -> (current == TrangThaiNv.UNG_VIEN) ? TrangThaiNv.THU_VIEC : null;
            case XAC_DINH_THOI_HAN, KHONG_XAC_DINH_THOI_HAN ->
                    (current == TrangThaiNv.UNG_VIEN || current == TrangThaiNv.THU_VIEC)
                            ? TrangThaiNv.CHINH_THUC : null;
            case PHU_LUC -> null;
        };
    }
}
