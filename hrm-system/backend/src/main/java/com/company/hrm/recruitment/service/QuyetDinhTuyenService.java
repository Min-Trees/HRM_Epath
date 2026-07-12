package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.QuyetDinhTuyenDto;
import com.company.hrm.recruitment.entity.QuyetDinhTuyen;
import com.company.hrm.recruitment.entity.TrangThaiQuyetDinh;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.repository.QuyetDinhTuyenRepository;
import com.company.hrm.recruitment.repository.UngVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * T17 - Service cho Quyet dinh tuyen (offer).
 *
 * <p>Khi quyet dinh o trang thai DA_DONG_Y, HR co the chuyen ung vien
 * thanh nhan vien chinh thuc (set nhan_vien_moi_id sau khi tao NV moi).
 */
@Service
public class QuyetDinhTuyenService {

    private final QuyetDinhTuyenRepository repo;
    private final UngVienRepository ungVienRepo;

    public QuyetDinhTuyenService(QuyetDinhTuyenRepository repo, UngVienRepository ungVienRepo) {
        this.repo = repo;
        this.ungVienRepo = ungVienRepo;
    }

    @Transactional(readOnly = true)
    public List<QuyetDinhTuyenDto> findByUngVien(UUID ungVienId) {
        return repo.findByUngVienId(ungVienId).stream().map(this::toDto).toList();
    }

    @Transactional
    public QuyetDinhTuyenDto create(QuyetDinhTuyenDto dto) {
        UngVien uv = ungVienRepo.findById(dto.getUngVienId()).orElseThrow(() ->
                new BusinessException("UV_NOT_FOUND", "Ung vien khong ton tai"));
        if (uv.getTrangThai() != TrangThaiUngVien.DE_NGHI_TUYEN) {
            throw new BusinessException("INVALID_STATE",
                    "Ung vien phai o trang thai DE_NGHI_TUYEN moi co the tao quyet dinh");
        }
        QuyetDinhTuyen e = new QuyetDinhTuyen();
        e.setUngVienId(dto.getUngVienId());
        e.setNguoiQuyetDinhId(dto.getNguoiQuyetDinhId());
        e.setLoaiHopDong(dto.getLoaiHopDong());
        e.setMucLuongDeNghi(dto.getMucLuongDeNghi());
        e.setNgayVaoLamDeNghi(dto.getNgayVaoLamDeNghi());
        e.setPhongBanId(dto.getPhongBanId());
        e.setChucDanh(dto.getChucDanh());
        e.setThoiHanThuViecThang(dto.getThoiHanThuViecThang());
        e.setGhiChu(dto.getGhiChu());
        e.setTrangThai(TrangThaiQuyetDinh.CHO_PHAN_HOI);
        QuyetDinhTuyen saved = repo.save(e);
        return toDto(saved);
    }

    @Transactional
    public QuyetDinhTuyenDto ungVienPhanHoi(UUID id, boolean dongY) {
        QuyetDinhTuyen e = repo.findById(id).orElseThrow(() ->
                new BusinessException("QDT_NOT_FOUND", "Quyet dinh khong ton tai"));
        if (e.getTrangThai() != TrangThaiQuyetDinh.CHO_PHAN_HOI) {
            throw new BusinessException("INVALID_STATE",
                    "Quyet dinh khong o trang thai CHO_PHAN_HOI");
        }
        e.setTrangThai(dongY ? TrangThaiQuyetDinh.DA_DONG_Y : TrangThaiQuyetDinh.TU_CHOI);
        e.setNgayUngVienPhanHoi(LocalDate.now());

        // Cap nhat trang thai ung vien
        UngVien uv = ungVienRepo.findById(e.getUngVienId()).orElse(null);
        if (uv != null) {
            uv.setTrangThai(dongY ? TrangThaiUngVien.DA_TUYEN : TrangThaiUngVien.TU_CHOI);
            ungVienRepo.save(uv);
        }
        return toDto(repo.save(e));
    }

    private QuyetDinhTuyenDto toDto(QuyetDinhTuyen e) {
        QuyetDinhTuyenDto d = new QuyetDinhTuyenDto();
        d.setQuyetDinhId(e.getQuyetDinhId());
        d.setUngVienId(e.getUngVienId());
        d.setNguoiQuyetDinhId(e.getNguoiQuyetDinhId());
        d.setLoaiHopDong(e.getLoaiHopDong());
        d.setMucLuongDeNghi(e.getMucLuongDeNghi());
        d.setNgayVaoLamDeNghi(e.getNgayVaoLamDeNghi());
        d.setPhongBanId(e.getPhongBanId());
        d.setChucDanh(e.getChucDanh());
        d.setThoiHanThuViecThang(e.getThoiHanThuViecThang());
        d.setGhiChu(e.getGhiChu());
        d.setTrangThai(e.getTrangThai());
        d.setNgayUngVienPhanHoi(e.getNgayUngVienPhanHoi());
        d.setNhanVienMoiId(e.getNhanVienMoiId());
        d.setCreatedAt(e.getCreatedAt());
        ungVienRepo.findById(e.getUngVienId()).ifPresent(uv -> d.setTenUngVien(uv.getHoTen()));
        return d;
    }
}
