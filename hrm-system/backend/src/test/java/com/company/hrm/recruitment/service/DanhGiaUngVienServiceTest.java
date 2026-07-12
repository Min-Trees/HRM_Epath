package com.company.hrm.recruitment.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.recruitment.dto.DanhGiaUngVienDto;
import com.company.hrm.recruitment.entity.DanhGiaUngVien;
import com.company.hrm.recruitment.entity.KetQuaPhongVan;
import com.company.hrm.recruitment.entity.LichPhongVan;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.entity.UngVien;
import com.company.hrm.recruitment.repository.DanhGiaUngVienRepository;
import com.company.hrm.recruitment.repository.LichPhongVanRepository;
import com.company.hrm.recruitment.repository.UngVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DanhGiaUngVienServiceTest {

    @Mock DanhGiaUngVienRepository repo;
    @Mock LichPhongVanRepository lichPvRepo;
    @Mock UngVienRepository uvRepo;
    DanhGiaUngVienService service;

    @BeforeEach
    void setUp() {
        service = new DanhGiaUngVienService(repo, lichPvRepo, uvRepo);
    }

    @Test
    void submit_diemVuotMuc_throwException() {
        // LichPv can ton tai de validation diem chay truoc validate lichPv
        UUID lichPvId = UUID.randomUUID();
        LichPhongVan lichPv = new LichPhongVan();
        lichPv.setLichPvId(lichPvId);
        lichPv.setUngVienId(UUID.randomUUID());
        when(lichPvRepo.findById(lichPvId)).thenReturn(Optional.of(lichPv));

        DanhGiaUngVienDto dto = new DanhGiaUngVienDto();
        dto.setLichPvId(lichPvId);
        dto.setNguoiDanhGiaId(UUID.randomUUID());
        dto.setDiemKyThuat(11);
        dto.setKetQua(KetQuaPhongVan.DAT);
        assertThatThrownBy(() -> service.submit(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_DIEM"));
    }

    @Test
    void submit_lichPvKhongTonTai_throwException() {
        DanhGiaUngVienDto dto = new DanhGiaUngVienDto();
        dto.setLichPvId(UUID.randomUUID());
        dto.setNguoiDanhGiaId(UUID.randomUUID());
        dto.setDiemKyThuat(8);
        dto.setKetQua(KetQuaPhongVan.DAT);
        when(lichPvRepo.findById(dto.getLichPvId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.submit(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("LICH_PV_NOT_FOUND"));
    }

    @Test
    void submit_DAT_chuyenUngVienSangDeNghiTuyen() {
        UUID uvId = UUID.randomUUID();
        LichPhongVan lichPv = new LichPhongVan();
        lichPv.setLichPvId(UUID.randomUUID());
        lichPv.setUngVienId(uvId);
        when(lichPvRepo.findById(lichPv.getLichPvId())).thenReturn(Optional.of(lichPv));
        when(lichPvRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UngVien uv = new UngVien();
        uv.setUngVienId(uvId);
        uv.setTrangThai(TrangThaiUngVien.CHO_PHONG_VAN_VONG_1);
        when(uvRepo.findById(uvId)).thenReturn(Optional.of(uv));
        when(uvRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        when(repo.save(any())).thenAnswer(inv -> {
            DanhGiaUngVien d = inv.getArgument(0);
            d.setDanhGiaId(UUID.randomUUID());
            return d;
        });

        DanhGiaUngVienDto dto = new DanhGiaUngVienDto();
        dto.setLichPvId(lichPv.getLichPvId());
        dto.setNguoiDanhGiaId(UUID.randomUUID());
        dto.setDiemKyThuat(8);
        dto.setDiemGiaoTiep(9);
        dto.setDiemThaiDo(9);
        dto.setDiemKetQua(8);
        dto.setKetQua(KetQuaPhongVan.DAT);

        DanhGiaUngVienDto out = service.submit(dto);
        assertThat(out.getDanhGiaId()).isNotNull();
        // diemTrungBinh duoc DB generated, mock return null trong save
        assertThat(uv.getTrangThai()).isEqualTo(TrangThaiUngVien.DE_NGHI_TUYEN);
    }

    @Test
    void submit_KHONG_DAT_tuChoiUngVien() {
        UUID uvId = UUID.randomUUID();
        LichPhongVan lichPv = new LichPhongVan();
        lichPv.setLichPvId(UUID.randomUUID());
        lichPv.setUngVienId(uvId);
        when(lichPvRepo.findById(lichPv.getLichPvId())).thenReturn(Optional.of(lichPv));
        when(lichPvRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UngVien uv = new UngVien();
        uv.setUngVienId(uvId);
        when(uvRepo.findById(uvId)).thenReturn(Optional.of(uv));
        when(uvRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repo.save(any())).thenAnswer(inv -> {
            DanhGiaUngVien d = inv.getArgument(0);
            d.setDanhGiaId(UUID.randomUUID());
            return d;
        });

        DanhGiaUngVienDto dto = new DanhGiaUngVienDto();
        dto.setLichPvId(lichPv.getLichPvId());
        dto.setNguoiDanhGiaId(UUID.randomUUID());
        dto.setDiemKyThuat(4);
        dto.setKetQua(KetQuaPhongVan.KHONG_DAT);
        service.submit(dto);
        assertThat(uv.getTrangThai()).isEqualTo(TrangThaiUngVien.TU_CHOI);
    }
}
