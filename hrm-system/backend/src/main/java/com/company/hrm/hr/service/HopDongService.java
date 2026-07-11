package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.AddendumRequest;
import com.company.hrm.hr.dto.HopDongExpiringItem;
import com.company.hrm.hr.dto.HopDongRequest;
import com.company.hrm.hr.dto.HopDongResponse;
import com.company.hrm.hr.entity.HopDongLaoDong;
import com.company.hrm.hr.entity.HopDongLaoDong.LoaiHopDong;
import com.company.hrm.hr.entity.HopDongLaoDong.TrangThaiHopDong;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.HopDongLaoDongRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.common.time.DateUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class HopDongService {

    private static final List<LoaiHopDong> HOP_DONG_CHINH = List.of(
            LoaiHopDong.THU_VIEC, LoaiHopDong.XAC_DINH_THOI_HAN, LoaiHopDong.KHONG_XAC_DINH_THOI_HAN);

    private final HopDongLaoDongRepository repo;
    private final NhanVienRepository nvRepo;
    private final ApplicationEventPublisher events;

    public HopDongService(HopDongLaoDongRepository repo,
                          NhanVienRepository nvRepo,
                          ApplicationEventPublisher events) {
        this.repo = repo;
        this.nvRepo = nvRepo;
        this.events = events;
    }

    @Transactional
    public HopDongResponse create(UUID nhanVienId, HopDongRequest req) {
        NhanVien nv = nvRepo.findById(nhanVienId).orElseThrow(
                () -> new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));
        validate(req, null);
        if (repo.existsBySoHopDong(req.getSoHopDong())) {
            throw new BusinessException("HOP_DONG_SO_DUPLICATE",
                    "Số hợp đồng '" + req.getSoHopDong() + "' đã tồn tại");
        }
        if (req.getLoaiHopDong() == LoaiHopDong.PHU_LUC) {
            throw new BusinessException("HOP_DONG_USE_ADDENDUM_API",
                    "Hợp đồng loại PHU_LUC phải tạo qua endpoint /contracts/{id}/addendum");
        }
        // Kiểm tra đã có HĐ chính HIEU_LUC chưa
        repo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(
                nhanVienId, TrangThaiHopDong.HIEU_LUC, HOP_DONG_CHINH).ifPresent(existing -> {
                    throw new BusinessException("EMPLOYEE_HAS_ACTIVE_CONTRACT",
                            "Nhân viên đã có hợp đồng chính hiệu lực (số " + existing.getSoHopDong()
                                    + "), hãy tạo phụ lục hoặc kết thúc hợp đồng cũ trước");
                });

        HopDongLaoDong e = new HopDongLaoDong();
        e.setNhanVienId(nhanVienId);
        apply(e, req);
        e.setPhuCapCoDinh(req.getPhuCapCoDinh() == null ? new HashMap<>() : req.getPhuCapCoDinh());
        e.setTrangThai(TrangThaiHopDong.HIEU_LUC);
        // T11: gán companyId theo NV (đảm bảo tenant bound).
        e.setCompanyId(nv.getCompanyId() != null ? nv.getCompanyId()
                : com.company.hrm.system.SystemConstants.DEFAULT_COMPANY_ID);
        HopDongLaoDong saved = repo.save(e);

        // Hook: phát tín hiệu để T07/T13 bắt (biến động trạng thái NV + báo tăng BHXH)
        events.publishEvent(new ContractSignedEvent(saved.getHopDongId(), nhanVienId,
                saved.getLoaiHopDong(), saved.getNgayHieuLuc()));
        return HopDongResponse.from(saved);
    }

    @Transactional
    public HopDongResponse addendum(UUID gocId, AddendumRequest req) {
        HopDongLaoDong goc = repo.findById(gocId).orElseThrow(() ->
                new ResourceNotFoundException("HOP_DONG_NOT_FOUND", "Không tìm thấy hợp đồng gốc"));
        if (goc.getLoaiHopDong() == LoaiHopDong.PHU_LUC) {
            throw new BusinessException("HOP_DONG_ADDENDUM_OF_ADDENDUM",
                    "Không thể tạo phụ lục của phụ lục; hãy tạo phụ lục cho hợp đồng chính");
        }
        if (repo.existsBySoHopDong(req.getSoHopDong())) {
            throw new BusinessException("HOP_DONG_SO_DUPLICATE",
                    "Số hợp đồng '" + req.getSoHopDong() + "' đã tồn tại");
        }
        if (req.getNgayHetHieuLucMoi() != null && req.getNgayHetHieuLucMoi().isBefore(req.getNgayHieuLuc())) {
            throw new BusinessException("HOP_DONG_DATE_INVALID",
                    "Ngày hết hiệu lực phụ lục phải >= ngày hiệu lực");
        }

        HopDongLaoDong e = new HopDongLaoDong();
        e.setNhanVienId(goc.getNhanVienId());
        e.setSoHopDong(req.getSoHopDong());
        e.setLoaiHopDong(LoaiHopDong.PHU_LUC);
        e.setHopDongGocId(goc.getHopDongId());
        e.setNgayKy(req.getNgayKy());
        e.setNgayHieuLuc(req.getNgayHieuLuc());
        e.setNgayHetHieuLuc(req.getNgayHetHieuLucMoi());
        // Phụ lục giữ nguyên lương/phụ cấp của HĐ gốc
        e.setMucLuongThoaThuan(goc.getMucLuongThoaThuan());
        e.setPhuCapCoDinh(goc.getPhuCapCoDinh());
        e.setFileDinhKemUrl(req.getFileDinhKemUrl());
        e.setTrangThai(TrangThaiHopDong.HIEU_LUC);
        return HopDongResponse.from(repo.save(e));
    }

    @Transactional
    public HopDongResponse update(UUID id, HopDongRequest req) {
        HopDongLaoDong e = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("HOP_DONG_NOT_FOUND", "Không tìm thấy hợp đồng"));
        // Chỉ sửa khi chưa kích hoạt HOẶC chỉ cập nhật file đính kèm
        if (e.getTrangThai() == TrangThaiHopDong.HIEU_LUC) {
            // Cho phép cập nhật file đính kèm
            if (req.getFileDinhKemUrl() != null) {
                e.setFileDinhKemUrl(req.getFileDinhKemUrl());
            } else {
                throw new BusinessException("HOP_DONG_ACTIVE_UPDATE_FORBIDDEN",
                        "Hợp đồng đang hiệu lực, chỉ được cập nhật file đính kèm; muốn sửa nội dung hãy tạo phụ lục");
            }
            return HopDongResponse.from(repo.save(e));
        }
        validate(req, e);
        if (!e.getSoHopDong().equals(req.getSoHopDong()) && repo.existsBySoHopDong(req.getSoHopDong())) {
            throw new BusinessException("HOP_DONG_SO_DUPLICATE", "Số hợp đồng đã tồn tại");
        }
        apply(e, req);
        return HopDongResponse.from(repo.save(e));
    }

    @Transactional(readOnly = true)
    public HopDongResponse get(UUID id) {
        return repo.findById(id)
                .map(HopDongResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("HOP_DONG_NOT_FOUND",
                        "Không tìm thấy hợp đồng"));
    }

    @Transactional(readOnly = true)
    public List<HopDongResponse> listByNhanVien(UUID nhanVienId) {
        return repo.findByNhanVienIdOrderByNgayHieuLucDesc(nhanVienId).stream()
                .map(HopDongResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<HopDongExpiringItem> expiring(int fromDays, int toDays) {
        if (fromDays > toDays || fromDays < 0) {
            throw new BusinessException("HOP_DONG_EXPIRY_RANGE_INVALID",
                    "Khoảng ngày không hợp lệ (fromDays=" + fromDays + ", toDays=" + toDays + ")");
        }
        LocalDate today = LocalDate.now();
        LocalDate from = today.plusDays(fromDays);
        LocalDate to = today.plusDays(toDays);
        List<HopDongLaoDong> list = repo.findByTrangThaiAndNgayHetHieuLucBetween(
                TrangThaiHopDong.HIEU_LUC, from, to);
        return list.stream().map(h -> {
            HopDongExpiringItem i = new HopDongExpiringItem();
            i.setHopDongId(h.getHopDongId());
            i.setNhanVienId(h.getNhanVienId());
            i.setSoHopDong(h.getSoHopDong());
            i.setNgayHetHieuLuc(h.getNgayHetHieuLuc());
            i.setSoNgayConLai(DateUtils.daysBetween(today, h.getNgayHetHieuLuc()));
            nvRepo.findById(h.getNhanVienId()).ifPresent(nv -> {
                i.setMaNv(nv.getMaNv());
                i.setHoTen(nv.getHoTen());
            });
            return i;
        }).toList();
    }

    private void validate(HopDongRequest req, HopDongLaoDong current) {
        if (req.getNgayHetHieuLuc() != null
                && req.getNgayHetHieuLuc().isBefore(req.getNgayHieuLuc())) {
            throw new BusinessException("HOP_DONG_DATE_INVALID",
                    "Ngày hết hiệu lực phải >= ngày hiệu lực");
        }
        if (req.getLoaiHopDong() == LoaiHopDong.KHONG_XAC_DINH_THOI_HAN
                && req.getNgayHetHieuLuc() != null) {
            throw new BusinessException("HOP_DONG_INDEFINITE_MUST_HAVE_NULL_END",
                    "Hợp đồng không xác định thời hạn phải có ngày hết hiệu lực = null");
        }
        if (req.getLoaiHopDong() != LoaiHopDong.KHONG_XAC_DINH_THOI_HAN
                && req.getNgayHetHieuLuc() == null) {
            throw new BusinessException("HOP_DONG_END_REQUIRED",
                    "Hợp đồng có thời hạn (xác định / thử việc) phải có ngày hết hiệu lực");
        }
    }

    private void apply(HopDongLaoDong e, HopDongRequest req) {
        e.setSoHopDong(req.getSoHopDong());
        e.setLoaiHopDong(req.getLoaiHopDong());
        e.setHopDongGocId(req.getHopDongGocId());
        e.setNgayKy(req.getNgayKy());
        e.setNgayHieuLuc(req.getNgayHieuLuc());
        e.setNgayHetHieuLuc(req.getNgayHetHieuLuc());
        e.setMucLuongThoaThuan(req.getMucLuongThoaThuan());
        if (req.getPhuCapCoDinh() != null) e.setPhuCapCoDinh(req.getPhuCapCoDinh());
        e.setFileDinhKemUrl(req.getFileDinhKemUrl());
    }

    /** Event để T07 / T13 bắt — placeholder, chưa có listener. */
    public record ContractSignedEvent(UUID hopDongId, UUID nhanVienId,
                                      LoaiHopDong loaiHopDong, LocalDate ngayHieuLuc) {}
}