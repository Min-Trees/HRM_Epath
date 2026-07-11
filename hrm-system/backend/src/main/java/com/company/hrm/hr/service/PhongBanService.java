package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.PhongBanRequest;
import com.company.hrm.hr.dto.PhongBanResponse;
import com.company.hrm.hr.dto.PhongBanTreeNode;
import com.company.hrm.hr.entity.PhongBan;
import com.company.hrm.hr.repository.PhongBanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PhongBanService {

    private final PhongBanRepository repo;

    public PhongBanService(PhongBanRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<PhongBanResponse> findAllFlat() {
        return repo.findAllByOrderByCapDoAscMaPhongBanAsc().stream()
                .map(PhongBanResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PhongBanTreeNode> findTree() {
        List<PhongBan> all = repo.findAllByOrderByCapDoAscMaPhongBanAsc();
        Map<UUID, PhongBanTreeNode> nodes = new LinkedHashMap<>();
        for (PhongBan e : all) {
            PhongBanTreeNode n = new PhongBanTreeNode();
            n.setPhongBanId(e.getPhongBanId());
            n.setMaPhongBan(e.getMaPhongBan());
            n.setTenPhongBan(e.getTenPhongBan());
            n.setPhongBanChaId(e.getPhongBanChaId());
            n.setDinhBien(e.getDinhBien());
            n.setCapDo(e.getCapDo());
            n.setActive(e.isActive());
            nodes.put(e.getPhongBanId(), n);
        }
        List<PhongBanTreeNode> roots = new ArrayList<>();
        for (PhongBanTreeNode n : nodes.values()) {
            if (n.getPhongBanChaId() == null) {
                roots.add(n);
            } else {
                PhongBanTreeNode parent = nodes.get(n.getPhongBanChaId());
                if (parent != null) parent.getChildren().add(n);
                else roots.add(n); // mồ côi → đẩy lên root để không mất
            }
        }
        return roots;
    }

    @Transactional(readOnly = true)
    public PhongBanResponse get(UUID id) {
        PhongBan e = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PHONG_BAN_NOT_FOUND", "Không tìm thấy phòng ban"));
        return PhongBanResponse.from(e);
    }

    @Transactional
    public PhongBanResponse create(PhongBanRequest req) {
        if (repo.existsById(req.getPhongBanChaId() != null ? req.getPhongBanChaId() : new UUID(0, 0))
                || req.getPhongBanChaId() == null) {
            // existsById với null không dùng được; check ma
        }
        if (repo.findByMaPhongBan(req.getMaPhongBan()).isPresent()) {
            throw new BusinessException("MA_PHONG_BAN_DUPLICATE",
                    "Mã phòng ban '" + req.getMaPhongBan() + "' đã tồn tại");
        }
        PhongBan e = new PhongBan();
        e.setMaPhongBan(req.getMaPhongBan());
        e.setTenPhongBan(req.getTenPhongBan());
        e.setPhongBanChaId(req.getPhongBanChaId());
        e.setTruongBoPhanId(req.getTruongBoPhanId());
        e.setDinhBien(req.getDinhBien());
        e.setActive(true);
        e.setCapDo(computeCapDo(req.getPhongBanChaId()));
        return PhongBanResponse.from(repo.save(e));
    }

    @Transactional
    public PhongBanResponse update(UUID id, PhongBanRequest req) {
        PhongBan e = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PHONG_BAN_NOT_FOUND", "Không tìm thấy phòng ban"));

        if (!e.getMaPhongBan().equals(req.getMaPhongBan())
                && repo.findByMaPhongBan(req.getMaPhongBan()).isPresent()) {
            throw new BusinessException("MA_PHONG_BAN_DUPLICATE",
                    "Mã phòng ban '" + req.getMaPhongBan() + "' đã tồn tại");
        }
        if (req.getPhongBanChaId() != null && req.getPhongBanChaId().equals(id)) {
            throw new BusinessException("DEPARTMENT_PARENT_CYCLE",
                    "Phòng ban không thể là cha của chính nó");
        }
        if (req.getPhongBanChaId() != null && isAncestor(id, req.getPhongBanChaId())) {
            throw new BusinessException("DEPARTMENT_CYCLE_DETECTED",
                    "Phòng ban cha mới sẽ tạo vòng lặp trong cây tổ chức");
        }

        e.setMaPhongBan(req.getMaPhongBan());
        e.setTenPhongBan(req.getTenPhongBan());
        e.setPhongBanChaId(req.getPhongBanChaId());
        e.setTruongBoPhanId(req.getTruongBoPhanId());
        e.setDinhBien(req.getDinhBien());
        e.setCapDo(computeCapDo(req.getPhongBanChaId()));
        return PhongBanResponse.from(repo.save(e));
    }

    @Transactional
    public PhongBanResponse assignManager(UUID id, UUID nhanVienId) {
        PhongBan e = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PHONG_BAN_NOT_FOUND", "Không tìm thấy phòng ban"));
        e.setTruongBoPhanId(nhanVienId);
        return PhongBanResponse.from(repo.save(e));
    }

    @Transactional
    public PhongBanResponse close(UUID id) {
        PhongBan e = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PHONG_BAN_NOT_FOUND", "Không tìm thấy phòng ban"));
        long children = repo.countByPhongBanChaId(id);
        if (children > 0) {
            throw new BusinessException("DEPARTMENT_HAS_CHILDREN",
                    "Không thể đóng phòng ban còn " + children + " phòng ban con");
        }
        e.setActive(false);
        return PhongBanResponse.from(repo.save(e));
    }

    /** Kiểm tra nhanVienId hiện đang là trưởng phòng ban khác (trừ chính id này). */
    @Transactional(readOnly = true)
    public boolean isManagerOfOther(UUID nhanVienId, UUID excludeDeptId) {
        if (nhanVienId == null) return false;
        return repo.findAll().stream()
                .anyMatch(p -> p.getTruongBoPhanId() != null
                        && p.getTruongBoPhanId().equals(nhanVienId)
                        && !p.getPhongBanId().equals(excludeDeptId));
    }

    // ---------- private helpers ----------

    private int computeCapDo(UUID parentId) {
        if (parentId == null) return 1;
        return repo.findById(parentId)
                .map(p -> p.getCapDo() + 1)
                .orElseThrow(() -> new BusinessException("PHONG_BAN_CHA_NOT_FOUND",
                        "Phòng ban cha không tồn tại"));
    }

    /** true nếu candidateId là con/cháu của deptId. */
    private boolean isAncestor(UUID deptId, UUID candidateId) {
        UUID cur = candidateId;
        Set<UUID> seen = new HashSet<>();
        while (cur != null) {
            if (!seen.add(cur)) return true; // phòng vệ vòng lặp DB
            if (cur.equals(deptId)) return true;
            PhongBan p = repo.findById(cur).orElse(null);
            if (p == null) return false;
            cur = p.getPhongBanChaId();
        }
        return false;
    }
}