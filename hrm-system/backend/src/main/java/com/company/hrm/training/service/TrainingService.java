package com.company.hrm.training.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.training.dto.*;
import com.company.hrm.training.entity.*;
import com.company.hrm.training.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * T20 - Service chinh cho module Dao tao.
 */
@Service
public class TrainingService {

    private final ChuongTrinhDaoTaoRepository ctRepo;
    private final LopHocRepository lopRepo;
    private final DangKyDaoTaoRepository dkRepo;
    private final DiemDanhDaoTaoRepository ddRepo;
    private final DanhGiaSauDaoTaoRepository dgRepo;

    public TrainingService(ChuongTrinhDaoTaoRepository ctRepo, LopHocRepository lopRepo,
                           DangKyDaoTaoRepository dkRepo, DiemDanhDaoTaoRepository ddRepo,
                           DanhGiaSauDaoTaoRepository dgRepo) {
        this.ctRepo = ctRepo;
        this.lopRepo = lopRepo;
        this.dkRepo = dkRepo;
        this.ddRepo = ddRepo;
        this.dgRepo = dgRepo;
    }

    // ============ CHUONG TRINH ============

    @Transactional(readOnly = true)
    public List<ChuongTrinhDaoTaoDto> findAllChuongTrinh() {
        return ctRepo.findAll().stream().map(this::toCtDto).toList();
    }

    @Transactional
    public ChuongTrinhDaoTaoDto createChuongTrinh(ChuongTrinhDaoTaoDto dto, UUID nguoiTaoId) {
        if (ctRepo.findAll().stream().anyMatch(c -> c.getMaChuongTrinh().equalsIgnoreCase(dto.getMaChuongTrinh()))) {
            throw new BusinessException("MA_TRUNG", "Ma chuong trinh da ton tai");
        }
        ChuongTrinhDaoTao e = new ChuongTrinhDaoTao();
        copyCt(dto, e);
        e.setNguoiTaoId(nguoiTaoId);
        if (e.getTrangThai() == null) e.setTrangThai(TrangThaiChuongTrinh.NHAP);
        if (e.getDiemDanhGiaToiThieu() == null) e.setDiemDanhGiaToiThieu(new BigDecimal("60.0"));
        return toCtDto(ctRepo.save(e));
    }

    @Transactional
    public ChuongTrinhDaoTaoDto congBoChuongTrinh(UUID id) {
        ChuongTrinhDaoTao e = ctRepo.findById(id).orElseThrow(() ->
                new BusinessException("CT_NOT_FOUND", "Chuong trinh khong ton tai"));
        if (e.getTrangThai() != TrangThaiChuongTrinh.NHAP) {
            throw new BusinessException("INVALID_STATE", "Chi cong bo duoc tu trang thai NHAP");
        }
        e.setTrangThai(TrangThaiChuongTrinh.CONG_BO);
        return toCtDto(ctRepo.save(e));
    }

    // ============ LOP HOC ============

    @Transactional(readOnly = true)
    public List<LopHocDto> findAllLop() {
        return lopRepo.findAll().stream().map(this::toLopDto).toList();
    }

    @Transactional
    public LopHocDto createLop(LopHocDto dto) {
        if (!ctRepo.existsById(dto.getChuongTrinhId())) {
            throw new BusinessException("CT_NOT_FOUND", "Chuong trinh khong ton tai");
        }
        if (dto.getNgayKetThuc().isBefore(dto.getNgayBatDau())) {
            throw new BusinessException("INVALID_DATES", "Ngay ket thuc phai sau ngay bat dau");
        }
        LopHoc e = new LopHoc();
        copyLop(dto, e);
        if (e.getTrangThai() == null) e.setTrangThai(TrangThaiLop.MO_DANG_KY);
        if (e.getSoChoToiDa() == null || e.getSoChoToiDa() < 1) e.setSoChoToiDa(30);
        return toLopDto(lopRepo.save(e));
    }

    @Transactional
    public LopHocDto changeLopState(UUID id, TrangThaiLop newState) {
        LopHoc e = lopRepo.findById(id).orElseThrow(() ->
                new BusinessException("LOP_NOT_FOUND", "Lop khong ton tai"));
        validateLopTransition(e.getTrangThai(), newState);
        e.setTrangThai(newState);
        return toLopDto(lopRepo.save(e));
    }

    private void validateLopTransition(TrangThaiLop from, TrangThaiLop to) {
        // MO_DANG_KY -> DONG_DANG_KY -> DANG_DIEN_RA -> HOAN_THANH (HUY o cac step truoc)
        boolean ok = switch (from) {
            case MO_DANG_KY -> to == TrangThaiLop.DONG_DANG_KY || to == TrangThaiLop.HUY;
            case DONG_DANG_KY -> to == TrangThaiLop.DANG_DIEN_RA || to == TrangThaiLop.HUY;
            case DANG_DIEN_RA -> to == TrangThaiLop.HOAN_THANH || to == TrangThaiLop.HUY;
            default -> false;
        };
        if (!ok) {
            throw new BusinessException("INVALID_TRANSITION",
                    "Khong the chuyen tu " + from + " -> " + to);
        }
    }

    // ============ DANG KY ============

    @Transactional
    public DangKyDaoTaoDto dangKy(DangKyDaoTaoDto dto) {
        LopHoc lop = lopRepo.findById(dto.getLopHocId()).orElseThrow(() ->
                new BusinessException("LOP_NOT_FOUND", "Lop khong ton tai"));
        if (lop.getTrangThai() != TrangThaiLop.MO_DANG_KY) {
            throw new BusinessException("LOP_KHONG_MO", "Lop khong con nhan dang ky");
        }
        long current = dkRepo.findByLopHocId(lop.getId()).stream()
                .filter(d -> d.getTrangThai() == TrangThaiDangKy.CHO_DUYET ||
                             d.getTrangThai() == TrangThaiDangKy.DA_CHAP_NHAN)
                .count();
        if (current >= lop.getSoChoToiDa()) {
            throw new BusinessException("LOP_DAY", "Lop da du " + lop.getSoChoToiDa() + " cho");
        }
        if (dkRepo.findByLopHocId(lop.getId()).stream()
                .anyMatch(d -> d.getNhanVienId().equals(dto.getNhanVienId()))) {
            throw new BusinessException("DA_DANG_KY", "NV da dang ky lop nay");
        }
        DangKyDaoTao e = new DangKyDaoTao();
        e.setLopHocId(dto.getLopHocId());
        e.setNhanVienId(dto.getNhanVienId());
        e.setLyDoDangKy(dto.getLyDoDangKy());
        e.setNgayDangKy(LocalDateTime.now());
        e.setTrangThai(TrangThaiDangKy.CHO_DUYET);
        return toDkDto(dkRepo.save(e));
    }

    @Transactional
    public DangKyDaoTaoDto duyetDangKy(UUID id, UUID nguoiDuyetId, TrangThaiDangKy quyetDinh, String ghiChu) {
        DangKyDaoTao e = dkRepo.findById(id).orElseThrow(() ->
                new BusinessException("DK_NOT_FOUND", "Dang ky khong ton tai"));
        if (e.getTrangThai() != TrangThaiDangKy.CHO_DUYET) {
            throw new BusinessException("INVALID_STATE", "Chi duyet dang ky o trang thai CHO_DUYET");
        }
        if (quyetDinh == TrangThaiDangKy.CHO_DUYET) {
            throw new BusinessException("INVALID_DECISION", "Khong the duyet thanh CHO_DUYET");
        }
        e.setTrangThai(quyetDinh);
        e.setNguoiDuyetId(nguoiDuyetId);
        e.setNgayDuyet(LocalDateTime.now());
        e.setGhiChuDuyet(ghiChu);
        return toDkDto(dkRepo.save(e));
    }

    @Transactional(readOnly = true)
    public List<DangKyDaoTaoDto> findDangKyByLop(UUID lopId) {
        return dkRepo.findByLopHocId(lopId).stream().map(this::toDkDto).toList();
    }

    @Transactional(readOnly = true)
    public List<DangKyDaoTaoDto> findDangKyByNhanVien(UUID nvId) {
        return dkRepo.findByNhanVienId(nvId).stream().map(this::toDkDto).toList();
    }

    // ============ DIEM DANH ============

    @Transactional
    public DiemDanhDto diemDanh(DiemDanhDto dto, UUID nguoiDiemDanhId) {
        DangKyDaoTao dk = dkRepo.findById(dto.getDangKyId()).orElseThrow(() ->
                new BusinessException("DK_NOT_FOUND", "Dang ky khong ton tai"));
        if (dk.getTrangThai() != TrangThaiDangKy.DA_CHAP_NHAN) {
            throw new BusinessException("DK_INVALID", "Dang ky chua duoc chap nhan");
        }
        DiemDanhDaoTao e = new DiemDanhDaoTao();
        e.setDangKyId(dto.getDangKyId());
        e.setBuoiSo(dto.getBuoiSo());
        e.setNgayHoc(dto.getNgayHoc());
        e.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : TrangThaiThamDu.CO_MAT);
        e.setDiemBaiTap(dto.getDiemBaiTap());
        e.setGhiChu(dto.getGhiChu());
        e.setNguoiDiemDanhId(nguoiDiemDanhId);
        return toDdDto(ddRepo.save(e));
    }

    // ============ DANH GIA ============

    @Transactional
    public DanhGiaSauDto danhGia(DanhGiaSauDto dto, UUID nguoiDanhGiaId) {
        DangKyDaoTao dk = dkRepo.findById(dto.getDangKyId()).orElseThrow(() ->
                new BusinessException("DK_NOT_FOUND", "Dang ky khong ton tai"));
        if (dk.getTrangThai() != TrangThaiDangKy.DA_CHAP_NHAN) {
            throw new BusinessException("DK_INVALID", "Chi danh gia NV da duoc chap nhan");
        }
        // tinh diem tong ket
        BigDecimal diemTB = BigDecimal.ZERO;
        if (dto.getDiemNoiDung() != null) diemTB = diemTB.add(dto.getDiemNoiDung().multiply(new BigDecimal("0.4")));
        if (dto.getDiemGiangVien() != null) diemTB = diemTB.add(dto.getDiemGiangVien().multiply(new BigDecimal("0.3")));
        if (dto.getDiemThucHanh() != null) diemTB = diemTB.add(dto.getDiemThucHanh().multiply(new BigDecimal("0.3")));

        ChuongTrinhDaoTao ct = ctRepo.findById(dk.getLopHocId() != null
                ? lopRepo.findById(dk.getLopHocId()).orElseThrow().getChuongTrinhId()
                : null).orElse(null);

        KetQuaDanhGia ketQua = classifyDiem(diemTB, ct != null ? ct.getDiemDanhGiaToiThieu() : new BigDecimal("60.0"));

        DanhGiaSauDaoTao e = new DanhGiaSauDaoTao();
        e.setDangKyId(dto.getDangKyId());
        e.setDiemNoiDung(dto.getDiemNoiDung());
        e.setDiemGiangVien(dto.getDiemGiangVien());
        e.setDiemThucHanh(dto.getDiemThucHanh());
        e.setKetQua(ketQua);
        e.setYKienNguoiHoc(dto.getYKienNguoiHoc());
        e.setYKienGv(dto.getYKienGv());
        e.setNgayDanhGia(LocalDateTime.now());
        e.setNguoiDanhGiaId(nguoiDanhGiaId);

        DanhGiaSauDaoTao saved = dgRepo.save(e);

        // cap nhat diem + cap chung chi neu dat
        dk.setDiemTongKet(diemTB);
        if (ct != null && diemTB.compareTo(ct.getDiemDanhGiaToiThieu()) >= 0) {
            dk.setChungChiCap(ct.getChungChi());
        }
        dkRepo.save(dk);

        DanhGiaSauDto out = toDgDto(saved);
        out.setDiemTrungBinh(diemTB);
        out.setKetQua(ketQua);
        return out;
    }

    public KetQuaDanhGia classifyDiem(BigDecimal diem, BigDecimal nguong) {
        if (diem == null) return KetQuaDanhGia.KHONG_DANH_GIA;
        if (diem.compareTo(new BigDecimal("90")) >= 0) return KetQuaDanhGia.XUAT_SAC;
        if (diem.compareTo(new BigDecimal("75")) >= 0) return KetQuaDanhGia.TOT;
        if (diem.compareTo(nguong != null ? nguong : new BigDecimal("60")) >= 0) return KetQuaDanhGia.TRUNG_BINH;
        return KetQuaDanhGia.YEU;
    }

    // ============ Mappers ============

    private ChuongTrinhDaoTaoDto toCtDto(ChuongTrinhDaoTao e) {
        ChuongTrinhDaoTaoDto d = new ChuongTrinhDaoTaoDto();
        d.setId(e.getId());
        d.setMaChuongTrinh(e.getMaChuongTrinh());
        d.setTenChuongTrinh(e.getTenChuongTrinh());
        d.setLoaiChuongTrinh(e.getLoaiChuongTrinh());
        d.setMoTa(e.getMoTa());
        d.setMucTieu(e.getMucTieu());
        d.setThoiLuongGio(e.getThoiLuongGio());
        d.setDiemDanhGiaToiThieu(e.getDiemDanhGiaToiThieu());
        d.setChungChi(e.getChungChi());
        d.setTrangThai(e.getTrangThai());
        d.setNguoiTaoId(e.getNguoiTaoId());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    private void copyCt(ChuongTrinhDaoTaoDto d, ChuongTrinhDaoTao e) {
        e.setMaChuongTrinh(d.getMaChuongTrinh());
        e.setTenChuongTrinh(d.getTenChuongTrinh());
        e.setLoaiChuongTrinh(d.getLoaiChuongTrinh());
        e.setMoTa(d.getMoTa());
        e.setMucTieu(d.getMucTieu());
        e.setThoiLuongGio(d.getThoiLuongGio());
        e.setDiemDanhGiaToiThieu(d.getDiemDanhGiaToiThieu());
        e.setChungChi(d.getChungChi());
        e.setTrangThai(d.getTrangThai());
    }

    private LopHocDto toLopDto(LopHoc e) {
        LopHocDto d = new LopHocDto();
        d.setId(e.getId());
        d.setMaLop(e.getMaLop());
        d.setChuongTrinhId(e.getChuongTrinhId());
        d.setTenLop(e.getTenLop());
        d.setNgayBatDau(e.getNgayBatDau());
        d.setNgayKetThuc(e.getNgayKetThuc());
        d.setSoBuoi(e.getSoBuoi());
        d.setSoChoToiDa(e.getSoChoToiDa());
        d.setDiaDiem(e.getDiaDiem());
        d.setGiangVien(e.getGiangVien());
        d.setChiPhiMoiNv(e.getChiPhiMoiNv());
        d.setTrangThai(e.getTrangThai());
        d.setGhiChu(e.getGhiChu());
        d.setNguoiPhuTrachId(e.getNguoiPhuTrachId());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    private void copyLop(LopHocDto d, LopHoc e) {
        e.setMaLop(d.getMaLop());
        e.setChuongTrinhId(d.getChuongTrinhId());
        e.setTenLop(d.getTenLop());
        e.setNgayBatDau(d.getNgayBatDau());
        e.setNgayKetThuc(d.getNgayKetThuc());
        e.setSoBuoi(d.getSoBuoi());
        e.setSoChoToiDa(d.getSoChoToiDa());
        e.setDiaDiem(d.getDiaDiem());
        e.setGiangVien(d.getGiangVien());
        e.setChiPhiMoiNv(d.getChiPhiMoiNv());
        e.setTrangThai(d.getTrangThai());
        e.setGhiChu(d.getGhiChu());
        e.setNguoiPhuTrachId(d.getNguoiPhuTrachId());
    }

    private DangKyDaoTaoDto toDkDto(DangKyDaoTao e) {
        DangKyDaoTaoDto d = new DangKyDaoTaoDto();
        d.setId(e.getId());
        d.setLopHocId(e.getLopHocId());
        d.setNhanVienId(e.getNhanVienId());
        d.setNgayDangKy(e.getNgayDangKy());
        d.setTrangThai(e.getTrangThai());
        d.setLyDoDangKy(e.getLyDoDangKy());
        d.setNguoiDuyetId(e.getNguoiDuyetId());
        d.setNgayDuyet(e.getNgayDuyet());
        d.setGhiChuDuyet(e.getGhiChuDuyet());
        d.setDiemTongKet(e.getDiemTongKet());
        d.setChungChiCap(e.getChungChiCap());
        d.setNgayCap(e.getNgayCap());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    private DiemDanhDto toDdDto(DiemDanhDaoTao e) {
        DiemDanhDto d = new DiemDanhDto();
        d.setId(e.getId());
        d.setDangKyId(e.getDangKyId());
        d.setBuoiSo(e.getBuoiSo());
        d.setNgayHoc(e.getNgayHoc());
        d.setTrangThai(e.getTrangThai());
        d.setDiemBaiTap(e.getDiemBaiTap());
        d.setGhiChu(e.getGhiChu());
        d.setNguoiDiemDanhId(e.getNguoiDiemDanhId());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    private DanhGiaSauDto toDgDto(DanhGiaSauDaoTao e) {
        DanhGiaSauDto d = new DanhGiaSauDto();
        d.setId(e.getId());
        d.setDangKyId(e.getDangKyId());
        d.setDiemNoiDung(e.getDiemNoiDung());
        d.setDiemGiangVien(e.getDiemGiangVien());
        d.setDiemThucHanh(e.getDiemThucHanh());
        d.setKetQua(e.getKetQua());
        d.setYKienNguoiHoc(e.getYKienNguoiHoc());
        d.setYKienGv(e.getYKienGv());
        d.setNgayDanhGia(e.getNgayDanhGia());
        d.setNguoiDanhGiaId(e.getNguoiDanhGiaId());
        return d;
    }
}
