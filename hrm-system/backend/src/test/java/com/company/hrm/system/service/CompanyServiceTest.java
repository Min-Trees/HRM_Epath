package com.company.hrm.system.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.system.dto.CompanyRequest;
import com.company.hrm.system.dto.CompanyResponse;
import com.company.hrm.system.entity.Company;
import com.company.hrm.system.entity.TrangThaiCompany;
import com.company.hrm.system.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    private CompanyRepository repo;
    private CompanyService service;

    @BeforeEach
    void setUp() {
        repo = mock(CompanyRepository.class);
        service = new CompanyService(repo);
        when(repo.save(any(Company.class))).thenAnswer(inv -> {
            Company c = inv.getArgument(0);
            if (c.getCompanyId() == null) c.setCompanyId(UUID.randomUUID());
            return c;
        });
    }

    private CompanyRequest req(String maSoThue) {
        CompanyRequest r = new CompanyRequest();
        r.setTenCongTy("Acme");
        r.setMaSoThue(maSoThue);
        r.setDiaChi("HCM");
        return r;
    }

    @Test
    void create_thanhCong() {
        when(repo.existsByMaSoThue("0123456789")).thenReturn(false);
        CompanyResponse r = service.create(req("0123456789"));
        assertEquals("Acme", r.getTenCongTy());
        assertEquals(TrangThaiCompany.HOAT_DONG, r.getTrangThai());
        verify(repo).save(any(Company.class));
    }

    @Test
    void create_taxCodeTrung_throws() {
        when(repo.existsByMaSoThue("0123456789")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("0123456789")));
        assertEquals("TAX_CODE_DUPLICATE", ex.getCode());
        verify(repo, never()).save(any());
    }

    @Test
    void create_taxCodeSaiFormat_validation() {
        // Validation @Pattern sẽ chặn ở controller layer; service kiểm tra unique trước
        when(repo.existsByMaSoThue(any())).thenReturn(false);
        assertDoesNotThrow(() -> service.create(req("01234567890")));
    }

    @Test
    void update_doiMST() {
        UUID id = UUID.randomUUID();
        Company c = new Company();
        c.setCompanyId(id);
        c.setTenCongTy("Old");
        c.setMaSoThue("0123456789");
        c.setTrangThai(TrangThaiCompany.HOAT_DONG);
        when(repo.findById(id)).thenReturn(Optional.of(c));
        when(repo.existsByMaSoThue("9876543210")).thenReturn(false);

        CompanyResponse r = service.update(id, req("9876543210"));
        assertEquals("9876543210", r.getMaSoThue());
        assertEquals("Acme", r.getTenCongTy());
    }

    @Test
    void update_mstTrungThrow() {
        UUID id = UUID.randomUUID();
        Company c = new Company();
        c.setCompanyId(id);
        c.setMaSoThue("0123456789");
        when(repo.findById(id)).thenReturn(Optional.of(c));
        when(repo.existsByMaSoThue("9876543210")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(id, req("9876543210")));
        assertEquals("TAX_CODE_DUPLICATE", ex.getCode());
    }

    @Test
    void updateStatus_thanhCong() {
        UUID id = UUID.randomUUID();
        Company c = new Company();
        c.setCompanyId(id);
        c.setTrangThai(TrangThaiCompany.HOAT_DONG);
        when(repo.findById(id)).thenReturn(Optional.of(c));

        CompanyResponse r = service.updateStatus(id, TrangThaiCompany.NGUNG_HOAT_DONG);
        assertEquals(TrangThaiCompany.NGUNG_HOAT_DONG, r.getTrangThai());
    }

    @Test
    void getById_notFound_throws() {
        when(repo.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
    }

    @Test
    void listByStatus_filter() {
        when(repo.findByTrangThai(TrangThaiCompany.HOAT_DONG))
                .thenReturn(List.of());
        assertEquals(0, service.listByStatus(TrangThaiCompany.HOAT_DONG).size());
    }
}