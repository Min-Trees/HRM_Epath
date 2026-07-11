package com.company.hrm.hr.repository;

import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NhanVienRepository extends JpaRepository<NhanVien, UUID> {

    Optional<NhanVien> findByMaNv(String maNv);
    boolean existsByMaNv(String maNv);
    boolean existsBySoCccd(String soCccd);

    @Query("""
        SELECT n FROM NhanVien n
        WHERE (:q IS NULL OR LOWER(n.hoTen) LIKE LOWER(CONCAT('%', :q, '%'))
                          OR LOWER(n.maNv)  LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:phongBanId IS NULL OR n.phongBanId = :phongBanId)
          AND (:trangThai IS NULL OR n.trangThai = :trangThai)
        ORDER BY n.maNv
        """)
    org.springframework.data.domain.Page<NhanVien> search(
            @Param("q") String q,
            @Param("phongBanId") UUID phongBanId,
            @Param("trangThai") TrangThaiNv trangThai,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(n.maNv, :prefixLen + 1) AS long)), 0) FROM NhanVien n WHERE n.maNv LIKE CONCAT(:prefix, '%')")
    long maxNumericSuffix(@Param("prefix") String prefix, @Param("prefixLen") int prefixLen);

    long countByPhongBanId(UUID phongBanId);

    List<NhanVien> findByPhongBanId(UUID phongBanId);

    /** Tìm NV trong 1 tenant. Dùng cho multi-tenant filter (T11). */
    Optional<NhanVien> findByNhanVienIdAndCompanyId(UUID nhanVienId, UUID companyId);

    List<NhanVien> findByCompanyId(UUID companyId);
}