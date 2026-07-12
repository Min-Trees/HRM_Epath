package com.company.hrm.payroll.run.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.payroll.run.dto.KyLinhLuongDto;
import com.company.hrm.payroll.run.dto.PayslipDto;
import com.company.hrm.payroll.run.entity.AuditKyLuong;
import com.company.hrm.payroll.run.entity.KyLinhLuong;
import com.company.hrm.payroll.run.entity.TrangThaiKyLuong;
import com.company.hrm.payroll.run.repository.AuditKyLuongRepository;
import com.company.hrm.payroll.run.repository.KyLinhLuongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * T19 - Service cho Ky linh luong (payroll run workflow).
 *
 * <p>Workflow:
 * <pre>
 *   CHO_CHAY -> DANG_CHAY -> DA_CHAY -> DA_DUYET_CAP_1 -> DA_DUYET_CAP_2 -> DA_CHI_TRA
 *   (HUY o cac step truoc khi DA_CHI_TRA)
 * </pre>
 */
@Service
public class KyLinhLuongService {

    private final KyLinhLuongRepository repo;
    private final AuditKyLuongRepository auditRepo;
    private final PayslipGenerator payslipGenerator;
    private final AtomicLong maCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public KyLinhLuongService(KyLinhLuongRepository repo,
                               AuditKyLuongRepository auditRepo,
                               PayslipGenerator payslipGenerator) {
        this.repo = repo;
        this.auditRepo = auditRepo;
        this.payslipGenerator = payslipGenerator;
    }

    @Transactional(readOnly = true)
    public List<KyLinhLuongDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public KyLinhLuongDto findById(UUID id) {
        return toDto(repo.findById(id).orElseThrow(() ->
                new BusinessException("KY_LUONG_NOT_FOUND", "Khong tim thay ky luong")));
    }

    @Transactional
    public KyLinhLuongDto create(KyLinhLuongDto dto) {
        if (dto.getThang() == null || dto.getThang() < 1 || dto.getThang() > 12) {
            throw new BusinessException("INVALID_THANG", "Thang phai trong [1, 12]");
        }
        if (repo.findByThangAndNam(dto.getThang(), dto.getNam()).isPresent()) {
            throw new BusinessException("KY_LUONG_EXIST",
                    "Ky luong " + dto.getThang() + "/" + dto.getNam() + " da ton tai");
        }
        KyLinhLuong e = new KyLinhLuong();
        e.setThang(dto.getThang());
        e.setNam(dto.getNam());
        e.setNgayChotCong(dto.getNgayChotCong());
        e.setNgayChiTra(dto.getNgayChiTra());
        e.setGhiChu(dto.getGhiChu());
        e.setLoaiHinhChiTra(dto.getLoaiHinhChiTra());
        e.setMaKyLinh(generateMaKy());
        e.setTrangThai(TrangThaiKyLuong.CHO_CHAY);
        return toDto(repo.save(e));
    }

    @Transactional
    public KyLinhLuongDto startRun(UUID id, UUID nguoiChayId) {
        KyLinhLuong e = requireAndGet(id);
        if (e.getTrangThai() != TrangThaiKyLuong.CHO_CHAY) {
            throw new BusinessException("INVALID_STATE", "Ky luong phai CHO_CHAY");
        }
        e.setTrangThai(TrangThaiKyLuong.DANG_CHAY);
        e.setNguoiChayId(nguoiChayId);
        e.setNgayChay(LocalDateTime.now());

        // Gia dinh job chay xong -> DA_CHAY
        e.setTrangThai(TrangThaiKyLuong.DA_CHAY);
        // Cap nhat tong so NV / tong thuc linh (gia tri placeholder, thuc te tu payroll service)
        e.setTongNhanVien(0);
        e.setTongThucLinh(java.math.BigDecimal.ZERO);
        e.setTongBhxhNld(java.math.BigDecimal.ZERO);
        e.setTongThueTncn(java.math.BigDecimal.ZERO);

        writeAudit(e, nguoiChayId, "CHAY_KY_LUONG", "Chay thanh cong");
        return toDto(repo.save(e));
    }

    @Transactional
    public KyLinhLuongDto approveCap1(UUID id, UUID nguoiDuyetId) {
        KyLinhLuong e = requireAndGet(id);
        if (e.getTrangThai() != TrangThaiKyLuong.DA_CHAY) {
            throw new BusinessException("INVALID_STATE", "Ky luong phai DA_CHAY truoc");
        }
        e.setTrangThai(TrangThaiKyLuong.DA_DUYET_CAP_1);
        e.setNguoiDuyetCap1Id(nguoiDuyetId);
        e.setNgayDuyetCap1(LocalDateTime.now());
        writeAudit(e, nguoiDuyetId, "DUYET_CAP_1", "Duyet cap 1");
        return toDto(repo.save(e));
    }

    @Transactional
    public KyLinhLuongDto approveCap2(UUID id, UUID nguoiDuyetId) {
        KyLinhLuong e = requireAndGet(id);
        if (e.getTrangThai() != TrangThaiKyLuong.DA_DUYET_CAP_1) {
            throw new BusinessException("INVALID_STATE", "Can duyet cap 1 truoc");
        }
        e.setTrangThai(TrangThaiKyLuong.DA_DUYET_CAP_2);
        e.setNguoiDuyetCap2Id(nguoiDuyetId);
        e.setNgayDuyetCap2(LocalDateTime.now());
        writeAudit(e, nguoiDuyetId, "DUYET_CAP_2", "Duyet cap 2");
        return toDto(repo.save(e));
    }

    @Transactional
    public KyLinhLuongDto payrollPaid(UUID id, UUID nguoiTra, String fileZipUrl) {
        KyLinhLuong e = requireAndGet(id);
        if (e.getTrangThai() != TrangThaiKyLuong.DA_DUYET_CAP_2) {
            throw new BusinessException("INVALID_STATE", "Can duyet cap 2 truoc");
        }
        e.setTrangThai(TrangThaiKyLuong.DA_CHI_TRA);
        e.setNguoiDuyetCap2Id(nguoiTra);
        e.setNgayChiTraThucTe(LocalDateTime.now());
        e.setFileZipUrl(fileZipUrl);
        writeAudit(e, nguoiTra, "CHI_TRA", "Da chi tra luong");
        return toDto(repo.save(e));
    }

    @Transactional
    public KyLinhLuongDto cancel(UUID id, UUID nguoiHuy, String lyDo) {
        KyLinhLuong e = requireAndGet(id);
        if (e.getTrangThai() == TrangThaiKyLuong.DA_CHI_TRA) {
            throw new BusinessException("CANNOT_CANCEL", "Ky luong da chi tra, khong the huy");
        }
        e.setTrangThai(TrangThaiKyLuong.HUY);
        writeAudit(e, nguoiHuy, "HUY", lyDo);
        return toDto(repo.save(e));
    }

    @Transactional(readOnly = true)
    public PayslipDto getPayslip(UUID kyLinhId, UUID nhanVienId) {
        KyLinhLuong ky = repo.findById(kyLinhId).orElseThrow(() ->
                new BusinessException("KY_LUONG_NOT_FOUND", "Ky luong khong ton tai"));
        return payslipGenerator.generateForNhanVien(ky, nhanVienId);
    }

    private KyLinhLuong requireAndGet(UUID id) {
        return repo.findById(id).orElseThrow(() ->
                new BusinessException("KY_LUONG_NOT_FOUND", "Khong tim thay ky luong"));
    }

    private String generateMaKy() {
        return String.format("KL-%05d", maCounter.incrementAndGet());
    }

    private void writeAudit(KyLinhLuong e, UUID nguoiThucHien, String hanhDong, String ghiChu) {
        AuditKyLuong a = new AuditKyLuong();
        a.setKyLinhId(e.getKyLinhId());
        a.setTrangThaiCu(null);
        a.setTrangThaiMoi(e.getTrangThai());
        a.setNguoiThucHienId(nguoiThucHien);
        a.setHanhDong(hanhDong);
        a.setGhiChu(ghiChu);
        auditRepo.save(a);
    }

    private KyLinhLuongDto toDto(KyLinhLuong e) {
        KyLinhLuongDto d = new KyLinhLuongDto();
        d.setKyLinhId(e.getKyLinhId());
        d.setMaKyLinh(e.getMaKyLinh());
        d.setThang(e.getThang());
        d.setNam(e.getNam());
        d.setNgayChotCong(e.getNgayChotCong());
        d.setNgayChiTra(e.getNgayChiTra());
        d.setTrangThai(e.getTrangThai());
        d.setLoaiHinhChiTra(e.getLoaiHinhChiTra());
        d.setTongNhanVien(e.getTongNhanVien());
        d.setTongThucLinh(e.getTongThucLinh());
        d.setTongBhxhNld(e.getTongBhxhNld());
        d.setTongThueTncn(e.getTongThueTncn());
        d.setGhiChu(e.getGhiChu());
        d.setNguoiChayId(e.getNguoiChayId());
        d.setNguoiDuyetCap1Id(e.getNguoiDuyetCap1Id());
        d.setNguoiDuyetCap2Id(e.getNguoiDuyetCap2Id());
        d.setNgayChay(e.getNgayChay());
        d.setNgayDuyetCap1(e.getNgayDuyetCap1());
        d.setNgayDuyetCap2(e.getNgayDuyetCap2());
        d.setNgayChiTraThucTe(e.getNgayChiTraThucTe());
        d.setFileZipUrl(e.getFileZipUrl());
        return d;
    }
}
