package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.DanhGiaUngVienDto;
import com.company.hrm.recruitment.entity.DanhGiaUngVien;
import com.company.hrm.recruitment.entity.KetQuaPhongVan;
import com.company.hrm.recruitment.entity.LichPhongVan;
import com.company.hrm.recruitment.entity.TrangThaiLichPV;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.repository.DanhGiaUngVienRepository;
import com.company.hrm.recruitment.repository.LichPhongVanRepository;
import com.company.hrm.recruitment.repository.UngVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * T17 - Service cho Danh gia phong van.
 */
@Service
public class DanhGiaUngVienService {

    private final DanhGiaUngVienRepository repo;
    private final LichPhongVanRepository lichPvRepo;
    private final UngVienRepository ungVienRepo;

    public DanhGiaUngVienService(DanhGiaUngVienRepository repo,
                                  LichPhongVanRepository lichPvRepo,
                                  UngVienRepository ungVienRepo) {
        this.repo = repo;
        this.lichPvRepo = lichPvRepo;
        this.ungVienRepo = ungVienRepo;
    }

    @Transactional(readOnly = true)
    public List<DanhGiaUngVienDto> findByLichPv(UUID lichPvId) {
        return repo.findByLichPvId(lichPvId).stream().map(this::toDto).toList();
    }

    @Transactional
    public DanhGiaUngVienDto submit(DanhGiaUngVienDto dto) {
        LichPhongVan lichPv = lichPvRepo.findById(dto.getLichPvId()).orElseThrow(() ->
                new BusinessException("LICH_PV_NOT_FOUND", "Lich phong van khong ton tai"));

        // Validate diem (0-10)
        validateDiem(dto.getDiemKyThuat(), "diemKyThuat");
        validateDiem(dto.getDiemGiaoTiep(), "diemGiaoTiep");
        validateDiem(dto.getDiemThaiDo(), "diemThaiDo");
        validateDiem(dto.getDiemKetQua(), "diemKetQua");

        DanhGiaUngVien e = new DanhGiaUngVien();
        e.setLichPvId(dto.getLichPvId());
        e.setNguoiDanhGiaId(dto.getNguoiDanhGiaId());
        e.setDiemKyThuat(dto.getDiemKyThuat());
        e.setDiemGiaoTiep(dto.getDiemGiaoTiep());
        e.setDiemThaiDo(dto.getDiemThaiDo());
        e.setDiemKetQua(dto.getDiemKetQua());
        e.setKetQua(dto.getKetQua());
        e.setDiemManh(dto.getDiemManh());
        e.setDiemYeu(dto.getDiemYeu());
        e.setNhanXet(dto.getNhanXet());
        DanhGiaUngVien saved = repo.save(e);

        // Cap nhat trang thai lich PV + ung vien
        lichPv.setTrangThai(TrangThaiLichPV.HOAN_THANH);
        lichPvRepo.save(lichPv);

        UngVien uv = ungVienRepo.findById(lichPv.getUngVienId()).orElse(null);
        if (uv != null) {
            if (dto.getKetQua() == KetQuaPhongVan.DAT || dto.getKetQua() == KetQuaPhongVan.KHA) {
                uv.setTrangThai(TrangThaiUngVien.DE_NGHI_TUYEN);
            } else {
                uv.setTrangThai(TrangThaiUngVien.TU_CHOI);
            }
            ungVienRepo.save(uv);
        }

        return toDto(saved);
    }

    private void validateDiem(Integer d, String field) {
        if (d != null && (d < 0 || d > 10)) {
            throw new BusinessException("INVALID_DIEM",
                    field + " phai trong khoang [0, 10]");
        }
    }

    private DanhGiaUngVienDto toDto(DanhGiaUngVien e) {
        DanhGiaUngVienDto d = new DanhGiaUngVienDto();
        d.setDanhGiaId(e.getDanhGiaId());
        d.setLichPvId(e.getLichPvId());
        d.setNguoiDanhGiaId(e.getNguoiDanhGiaId());
        d.setDiemKyThuat(e.getDiemKyThuat());
        d.setDiemGiaoTiep(e.getDiemGiaoTiep());
        d.setDiemThaiDo(e.getDiemThaiDo());
        d.setDiemKetQua(e.getDiemKetQua());
        d.setDiemTrungBinh(e.getDiemTrungBinh());
        d.setKetQua(e.getKetQua());
        d.setDiemManh(e.getDiemManh());
        d.setDiemYeu(e.getDiemYeu());
        d.setNhanXet(e.getNhanXet());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }
}
