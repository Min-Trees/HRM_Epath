package com.company.hrm.training.repository;

import com.company.hrm.training.entity.ChuongTrinhDaoTao;
import com.company.hrm.training.entity.LoaiChuongTrinh;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, UUID> {
    List<ChuongTrinhDaoTao> findByLoaiChuongTrinh(LoaiChuongTrinh loaiChuongTrinh);
}
