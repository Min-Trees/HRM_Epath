package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.NhanVienRequest;
import com.company.hrm.hr.dto.NhanVienResponse;
import com.company.hrm.hr.dto.NguoiPhuThuocResponse;
import com.company.hrm.hr.dto.QuaTrinhCongTacResponse;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.entity.PhongBan;
import com.company.hrm.hr.repository.NguoiPhuThuocRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.hr.repository.PhongBanRepository;
import com.company.hrm.hr.repository.QuaTrinhCongTacRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NhanVienService {

    private static final String MA_NV_PREFIX = "NV";

    private final NhanVienRepository repo;
    private final PhongBanRepository phongBanRepo;
    private final NguoiPhuThuocRepository nptRepo;
    private final QuaTrinhCongTacRepository qttRepo;

    public NhanVienService(NhanVienRepository repo,
                          PhongBanRepository phongBanRepo,
                          NguoiPhuThuocRepository nptRepo,
                          QuaTrinhCongTacRepository qttRepo) {
        this.repo = repo;
        this.phongBanRepo = phongBanRepo;
        this.nptRepo = nptRepo;
        this.qttRepo = qttRepo;
    }

    @Transactional
    public NhanVienResponse create(NhanVienRequest req) {
        if (req.getSoCccd() != null && !req.getSoCccd().isBlank()
                && repo.existsBySoCccd(req.getSoCccd())) {
            throw new BusinessException("CCCD_DUPLICATE",
                    "Số CCCD '" + req.getSoCccd() + "' đã tồn tại trên hồ sơ khác");
        }
        PhongBan pb = phongBanRepo.findById(req.getPhongBanId())
                .orElseThrow(() -> new BusinessException("PHONG_BAN_NOT_FOUND",
                        "Phòng ban không tồn tại"));
        if (!pb.isActive()) {
            throw new BusinessException("PHONG_BAN_INACTIVE",
                    "Phòng ban '" + pb.getMaPhongBan() + "' đã đóng, không thể gán nhân viên");
        }
        if (req.getQuanLyTrucTiepId() != null && !repo.existsById(req.getQuanLyTrucTiepId())) {
            throw new BusinessException("QUAN_LY_NOT_FOUND",
                    "Quản lý trực tiếp không tồn tại");
        }

        NhanVien nv = new NhanVien();
        nv.setMaNv(generateMaNv());
        nv.setHoTen(req.getHoTen());
        nv.setNgaySinh(req.getNgaySinh());
        nv.setGioiTinh(req.getGioiTinh());
        nv.setSoCccd(req.getSoCccd());
        nv.setNgayCapCccd(req.getNgayCapCccd());
        nv.setNoiCapCccd(req.getNoiCapCccd());
        nv.setQueQuan(req.getQueQuan());
        nv.setDiaChiThuongTru(req.getDiaChiThuongTru());
        nv.setDiaChiLienLac(req.getDiaChiLienLac());
        nv.setSoDienThoai(req.getSoDienThoai());
        nv.setEmail(req.getEmail());
        nv.setTrinhDoHocVan(req.getTrinhDoHocVan());
        nv.setNgayVaoLam(req.getNgayVaoLam());
        nv.setPhongBanId(req.getPhongBanId());
        nv.setNgachBacId(req.getNgachBacId());
        nv.setQuanLyTrucTiepId(req.getQuanLyTrucTiepId());
        // Mặc định UNG_VIEN; cho phép tạo với THU_VIEC nếu đã ký HĐ thử việc
        nv.setTrangThai(req.getTrangThai() == null ? TrangThaiNv.UNG_VIEN : req.getTrangThai());
        nv.setTaiKhoanChamCongId("CC-" + nv.getMaNv());
        // T11: gán tenant mặc định nếu caller không chỉ định.
        if (nv.getCompanyId() == null) {
            nv.setCompanyId(com.company.hrm.system.SystemConstants.DEFAULT_COMPANY_ID);
        }
        // TODO (T07): phát event TUYEN_DUNG cho module chấm công & BHXH
        return NhanVienResponse.from(repo.save(nv));
    }

    @Transactional(readOnly = true)
    public Page<NhanVienResponse> search(String q, UUID phongBanId, TrangThaiNv trangThai,
                                          Pageable pageable) {
        return repo.search(q, phongBanId, trangThai, pageable).map(NhanVienResponse::from);
    }

    @Transactional(readOnly = true)
    public NhanVienResponse get(UUID id) {
        NhanVien e = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));
        NhanVienResponse r = NhanVienResponse.from(e);
        r.setDependents(nptRepo.findByNhanVienIdAndActiveTrue(id).stream()
                .map(NguoiPhuThuocResponse::from).toList());
        r.setWorkHistory(qttRepo.findByNhanVienIdOrderByTuNgayDesc(id).stream()
                .map(QuaTrinhCongTacResponse::from).toList());
        return r;
    }

    /**
     * Cập nhật thông tin cá nhân / liên hệ / học vấn.
     * KHÔNG thay đổi: trang_thai, phong_ban_id, chuc_danh, luong (các trường này chỉ đổi qua biến động T07).
     */
    @Transactional
    public NhanVienResponse updateInfo(UUID id, NhanVienRequest req) {
        NhanVien nv = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));
        // Không cho thay đổi trang_thai, phong_ban_id, ngach_bac_id, quan_ly_truc_tiep_id ở endpoint này
        if (req.getTrangThai() != null && req.getTrangThai() != nv.getTrangThai()) {
            throw new BusinessException("TRANG_THAI_IMMUTABLE",
                    "Không thể thay đổi trạng thái nhân viên qua API này; hãy dùng biến động nhân sự (T07)");
        }
        if (req.getPhongBanId() != null && !req.getPhongBanId().equals(nv.getPhongBanId())) {
            throw new BusinessException("PHONG_BAN_IMMUTABLE",
                    "Không thể thay đổi phòng ban qua API này; hãy dùng biến động nhân sự (T07)");
        }
        if (req.getSoCccd() != null && !req.getSoCccd().isBlank()
                && !req.getSoCccd().equals(nv.getSoCccd())
                && repo.existsBySoCccd(req.getSoCccd())) {
            throw new BusinessException("CCCD_DUPLICATE", "Số CCCD đã tồn tại");
        }

        nv.setHoTen(req.getHoTen());
        nv.setNgaySinh(req.getNgaySinh());
        nv.setGioiTinh(req.getGioiTinh());
        nv.setSoCccd(req.getSoCccd());
        nv.setNgayCapCccd(req.getNgayCapCccd());
        nv.setNoiCapCccd(req.getNoiCapCccd());
        nv.setQueQuan(req.getQueQuan());
        nv.setDiaChiThuongTru(req.getDiaChiThuongTru());
        nv.setDiaChiLienLac(req.getDiaChiLienLac());
        nv.setSoDienThoai(req.getSoDienThoai());
        nv.setEmail(req.getEmail());
        nv.setTrinhDoHocVan(req.getTrinhDoHocVan());
        return NhanVienResponse.from(repo.save(nv));
    }

    /** Sinh mã NV0001, NV0002... tăng dần. */
    private String generateMaNv() {
        long max = repo.maxNumericSuffix(MA_NV_PREFIX, MA_NV_PREFIX.length());
        return String.format("%s%04d", MA_NV_PREFIX, max + 1);
    }

    public void requireExists(UUID id) {
        if (!repo.existsById(id)) {
            throw new com.company.hrm.common.error.ResourceNotFoundException(
                    "EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên");
        }
    }
}