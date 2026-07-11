package com.company.hrm.system.service;

import com.company.hrm.system.entity.Permission;
import com.company.hrm.system.entity.RoleEntity;
import com.company.hrm.system.repository.RoleEntityRepository;
import com.company.hrm.system.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Truy vấn role + permission. Hỗ trợ login flow: từ danh sách role code của user,
 * tính ra tập permission codes hợp lệ.
 */
@Service
public class RoleService {

    private final RoleEntityRepository roleRepo;
    private final UserAccountRepository userRepo;

    public RoleService(RoleEntityRepository roleRepo, UserAccountRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public RoleEntity requireByCode(String code) {
        return roleRepo.findByCode(code).orElseThrow(() ->
                new IllegalStateException("Role '" + code + "' chưa được seed — chạy V5__seed_system_data"));
    }

    @Transactional(readOnly = true)
    public Set<String> getPermissionsByRoleCodes(Collection<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) return Set.of();
        List<RoleEntity> roles = roleRepo.findAllByCodeIn(roleCodes);
        Set<String> out = new HashSet<>();
        for (RoleEntity r : roles) {
            for (Permission p : r.getPermissions()) {
                out.add(p.getCode());
            }
        }
        return out;
    }

    /** Helper resolve permission codes cho user theo userId. */
    @Transactional(readOnly = true)
    public Set<String> getPermissionsByUserId(java.util.UUID userId) {
        return userRepo.findById(userId)
                .map(u -> getPermissionsByRoleCodes(u.getRoleCodeList()))
                .orElse(Set.of());
    }

    /** Helper tiện ích: gom role codes → role entities. */
    @Transactional(readOnly = true)
    public List<RoleEntity> findAllByCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) return List.of();
        return roleRepo.findAllByCodeIn(codes).stream()
                .collect(Collectors.toList());
    }
}