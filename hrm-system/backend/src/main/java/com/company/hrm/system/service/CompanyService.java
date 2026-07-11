package com.company.hrm.system.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.system.dto.CompanyRequest;
import com.company.hrm.system.dto.CompanyResponse;
import com.company.hrm.system.entity.Company;
import com.company.hrm.system.entity.TrangThaiCompany;
import com.company.hrm.system.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Nghiệp vụ quản lý doanh nghiệp (tenant).
 *
 * <p>Validate:
 * <ul>
 *   <li>{@code maSoThue} unique (throw {@code TAX_CODE_DUPLICATE} — HTTP 409).</li>
 *   <li>Khi {@code NGUNG_HOAT_DONG}, không cho phép tạo user mới (kiểm ở tầng UserAccountService).</li>
 * </ul>
 */
@Service
public class CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository repo;

    public CompanyService(CompanyRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public CompanyResponse create(CompanyRequest req) {
        if (repo.existsByMaSoThue(req.getMaSoThue())) {
            throw new BusinessException("TAX_CODE_DUPLICATE",
                    "Mã số thuế '" + req.getMaSoThue() + "' đã tồn tại");
        }
        Company c = new Company();
        applyRequest(c, req);
        if (c.getTrangThai() == null) c.setTrangThai(TrangThaiCompany.HOAT_DONG);
        Company saved = repo.save(c);
        log.info("Tạo company {} (MST {})", saved.getCompanyId(), saved.getMaSoThue());
        return CompanyResponse.from(saved);
    }

    @Transactional
    public CompanyResponse update(UUID companyId, CompanyRequest req) {
        Company c = requireById(companyId);
        if (!c.getMaSoThue().equals(req.getMaSoThue()) && repo.existsByMaSoThue(req.getMaSoThue())) {
            throw new BusinessException("TAX_CODE_DUPLICATE",
                    "Mã số thuế '" + req.getMaSoThue() + "' đã tồn tại");
        }
        applyRequest(c, req);
        if (req.getTrangThai() != null) c.setTrangThai(req.getTrangThai());
        log.info("Cập nhật company {} (MST {})", companyId, c.getMaSoThue());
        return CompanyResponse.from(repo.save(c));
    }

    @Transactional
    public CompanyResponse updateStatus(UUID companyId, TrangThaiCompany status) {
        Company c = requireById(companyId);
        c.setTrangThai(status);
        log.info("Đổi trạng thái company {} → {}", companyId, status);
        return CompanyResponse.from(repo.save(c));
    }

    @Transactional(readOnly = true)
    public CompanyResponse getById(UUID companyId) {
        return CompanyResponse.from(requireById(companyId));
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> listByStatus(TrangThaiCompany status) {
        List<Company> list = status == null ? repo.findAll() : repo.findByTrangThai(status);
        return list.stream().map(CompanyResponse::from).toList();
    }

    public Company requireById(UUID companyId) {
        return repo.findById(companyId).orElseThrow(() ->
                new ResourceNotFoundException("COMPANY_NOT_FOUND",
                        "Không tìm thấy doanh nghiệp " + companyId));
    }

    private void applyRequest(Company c, CompanyRequest req) {
        c.setTenCongTy(req.getTenCongTy());
        c.setMaSoThue(req.getMaSoThue());
        c.setMaSoDkkd(req.getMaSoDkkd());
        c.setDiaChi(req.getDiaChi());
        c.setSoDienThoai(req.getSoDienThoai());
        c.setEmail(req.getEmail());
        c.setNguoiDaiDienPhapLuat(req.getNguoiDaiDienPhapLuat());
        if (req.getNgayDangKy() != null) c.setNgayDangKy(req.getNgayDangKy());
        c.setGoiDichVu(req.getGoiDichVu());
    }
}