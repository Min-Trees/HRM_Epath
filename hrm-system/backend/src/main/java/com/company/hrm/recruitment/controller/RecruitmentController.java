package com.company.hrm.recruitment.controller;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.recruitment.dto.DanhGiaUngVienDto;
import com.company.hrm.recruitment.dto.LichPhongVanDto;
import com.company.hrm.recruitment.dto.QuyetDinhTuyenDto;
import com.company.hrm.recruitment.dto.UngVienDto;
import com.company.hrm.recruitment.dto.YeuCauTuyenDungDto;
import com.company.hrm.recruitment.entity.TrangThaiLichPV;
import com.company.hrm.recruitment.entity.TrangThaiQuyetDinh;
import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import com.company.hrm.recruitment.service.DanhGiaUngVienService;
import com.company.hrm.recruitment.service.LichPhongVanService;
import com.company.hrm.recruitment.service.QuyetDinhTuyenService;
import com.company.hrm.recruitment.service.UngVienService;
import com.company.hrm.recruitment.service.YeuCauTuyenDungService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * T17 - REST controller cho Module Tuyen dung.
 *
 * <p>Cac nhom endpoint:
 * <ul>
 *   <li>/api/v1/recruitment/yeu-cau: quan ly yeu cau tuyen dung</li>
 *   <li>/api/v1/recruitment/ung-vien: ho so ung vien</li>
 *   <li>/api/v1/recruitment/phong-van: lich phong van + danh gia</li>
 *   <li>/api/v1/recruitment/quyet-dinh: offer tuyen dung</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/recruitment")
public class RecruitmentController {

    private final YeuCauTuyenDungService ycService;
    private final UngVienService uvService;
    private final LichPhongVanService lichPvService;
    private final DanhGiaUngVienService danhGiaService;
    private final QuyetDinhTuyenService qdtService;

    public RecruitmentController(YeuCauTuyenDungService ycService,
                                  UngVienService uvService,
                                  LichPhongVanService lichPvService,
                                  DanhGiaUngVienService danhGiaService,
                                  QuyetDinhTuyenService qdtService) {
        this.ycService = ycService;
        this.uvService = uvService;
        this.lichPvService = lichPvService;
        this.danhGiaService = danhGiaService;
        this.qdtService = qdtService;
    }

    // ==================== Yeu cau tuyen dung ====================
    @GetMapping("/yeu-cau")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<YeuCauTuyenDungDto> listYeuCau() {
        return ycService.findAll();
    }

    @GetMapping("/yeu-cau/{id}")
    @RequiresRole({Role.HR, Role.MANAGER})
    public YeuCauTuyenDungDto getYeuCau(@PathVariable UUID id) {
        return ycService.findById(id);
    }

    @PostMapping("/yeu-cau")
    @RequiresRole({Role.MANAGER, Role.HR})
    public YeuCauTuyenDungDto createYeuCau(@Valid @RequestBody YeuCauTuyenDungDto dto) {
        if (dto.getNguoiYeuCauId() == null) {
            dto.setNguoiYeuCauId(AuthContext.currentUserIdOrNull());
        }
        return ycService.create(dto);
    }

    @PostMapping("/yeu-cau/{id}/submit")
    @RequiresRole({Role.MANAGER, Role.HR})
    public YeuCauTuyenDungDto submitYeuCau(@PathVariable UUID id) {
        return ycService.submitForApproval(id);
    }

    @PostMapping("/yeu-cau/{id}/approve")
    @RequiresRole({Role.HR})
    public YeuCauTuyenDungDto approveYeuCau(@PathVariable UUID id) {
        return ycService.approve(id, AuthContext.currentUserIdOrNull());
    }

    @PostMapping("/yeu-cau/{id}/start-recruiting")
    @RequiresRole({Role.HR})
    public YeuCauTuyenDungDto startRecruiting(@PathVariable UUID id) {
        return ycService.startRecruiting(id);
    }

    @PostMapping("/yeu-cau/{id}/close")
    @RequiresRole({Role.HR})
    public YeuCauTuyenDungDto closeYeuCau(@PathVariable UUID id) {
        return ycService.close(id);
    }

    // ==================== Ung vien ====================
    @GetMapping("/ung-vien")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<UngVienDto> listUngVien() {
        return uvService.findAll();
    }

    @GetMapping("/ung-vien/{id}")
    @RequiresRole({Role.HR, Role.MANAGER})
    public UngVienDto getUngVien(@PathVariable UUID id) {
        return uvService.findById(id);
    }

    @GetMapping("/yeu-cau/{yeuCauId}/ung-vien")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<UngVienDto> listUngVienTheoYeuCau(@PathVariable UUID yeuCauId) {
        return uvService.findByYeuCau(yeuCauId);
    }

    @PostMapping("/ung-vien")
    @RequiresRole({Role.HR, Role.MANAGER})
    public UngVienDto createUngVien(@Valid @RequestBody UngVienDto dto) {
        return uvService.create(dto);
    }

    @PostMapping("/ung-vien/{id}/status")
    @RequiresRole({Role.HR, Role.MANAGER})
    public UngVienDto updateTrangThaiUV(@PathVariable UUID id, @RequestParam TrangThaiUngVien status) {
        return uvService.updateStatus(id, status);
    }

    // ==================== Phong van ====================
    @GetMapping("/ung-vien/{ungVienId}/lich-phong-van")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<LichPhongVanDto> listLichPV(@PathVariable UUID ungVienId) {
        return lichPvService.findByUngVien(ungVienId);
    }

    @PostMapping("/lich-phong-van")
    @RequiresRole({Role.HR, Role.MANAGER})
    public LichPhongVanDto schedule(@Valid @RequestBody LichPhongVanDto dto) {
        if (dto.getNguoiToChucId() == null) {
            dto.setNguoiToChucId(AuthContext.currentUserIdOrNull());
        }
        return lichPvService.schedule(dto);
    }

    @PostMapping("/lich-phong-van/{id}/status")
    @RequiresRole({Role.HR, Role.MANAGER})
    public LichPhongVanDto updateLichPvStatus(@PathVariable UUID id, @RequestParam TrangThaiLichPV status) {
        return lichPvService.updateStatus(id, status);
    }

    @GetMapping("/lich-phong-van/{lichPvId}/danh-gia")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<DanhGiaUngVienDto> listDanhGia(@PathVariable UUID lichPvId) {
        return danhGiaService.findByLichPv(lichPvId);
    }

    @PostMapping("/danh-gia")
    @RequiresRole({Role.HR, Role.MANAGER})
    public DanhGiaUngVienDto submitDanhGia(@Valid @RequestBody DanhGiaUngVienDto dto) {
        if (dto.getNguoiDanhGiaId() == null) {
            dto.setNguoiDanhGiaId(AuthContext.currentUserIdOrNull());
        }
        return danhGiaService.submit(dto);
    }

    // ==================== Quyet dinh tuyen ====================
    @GetMapping("/ung-vien/{ungVienId}/quyet-dinh")
    @RequiresRole({Role.HR, Role.MANAGER})
    public List<QuyetDinhTuyenDto> listQuyetDinh(@PathVariable UUID ungVienId) {
        return qdtService.findByUngVien(ungVienId);
    }

    @PostMapping("/quyet-dinh")
    @RequiresRole({Role.HR})
    public QuyetDinhTuyenDto createQuyetDinh(@Valid @RequestBody QuyetDinhTuyenDto dto) {
        if (dto.getNguoiQuyetDinhId() == null) {
            dto.setNguoiQuyetDinhId(AuthContext.currentUserIdOrNull());
        }
        return qdtService.create(dto);
    }

    @PostMapping("/quyet-dinh/{id}/ung-vien-phan-hoi")
    @RequiresRole({Role.HR})
    public QuyetDinhTuyenDto uvPhanHoi(@PathVariable UUID id, @RequestParam boolean dongY) {
        return qdtService.ungVienPhanHoi(id, dongY);
    }
}
