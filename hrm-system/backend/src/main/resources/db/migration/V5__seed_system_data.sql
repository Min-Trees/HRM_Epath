-- =====================================================================
-- V5__seed_system_data.sql
-- Seed 6 role mặc định + ~50 permission cho 5 module (HR, timekeeping,
-- social_ins, payroll, system). Đồng thời tạo 1 default company.
-- V6 sẽ backfill company_id cho dữ liệu cũ và seed user SYSTEM_ADMIN.
-- =====================================================================

-- Default company (V6 sẽ dùng cố định UUID này để backfill)
INSERT INTO system.company (company_id, ten_cong_ty, ma_so_thue, dia_chi, so_dien_thoai,
                            email, nguoi_dai_dien_phap_luat, ngay_dang_ky, trang_thai)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Default Company',
    '0000000000',
    'HCM City',
    '0900000000',
    'admin@default-company.local',
    'Người đại diện mặc định',
    CURRENT_DATE,
    'HOAT_DONG'
);

-- =====================================================================
-- ROLE
-- =====================================================================
INSERT INTO system.role (code, ten_role, mo_ta, is_system) VALUES
    ('SYSTEM_ADMIN',   'Quản trị hệ thống',  'Tất cả quyền',                                  TRUE),
    ('COMPANY_ADMIN',  'Admin doanh nghiệp',  'Quản lý user, NV, phòng ban, phân quyền nội bộ', TRUE),
    ('HR_MANAGER',     'HR Manager',          'Hồ sơ NV, hợp đồng, biến động',                TRUE),
    ('ACCOUNTANT',     'Kế toán lương',       'Tính lương, thuế, BHXH, báo cáo',              TRUE),
    ('MANAGER',        'Quản lý',             'Duyệt nghỉ phép, OT cho phòng ban',            TRUE),
    ('EMPLOYEE',       'Nhân viên',           'Xem hồ sơ cá nhân, gửi yêu cầu',              TRUE);

-- =====================================================================
-- PERMISSION (resource.action format)
-- =====================================================================

-- module 'hr'
INSERT INTO system.permission (code, module, mo_ta) VALUES
    ('nhan_vien.create',          'hr',          'Tạo hồ sơ nhân viên'),
    ('nhan_vien.read',            'hr',          'Xem hồ sơ nhân viên'),
    ('nhan_vien.update',          'hr',          'Cập nhật hồ sơ nhân viên'),
    ('nhan_vien.delete',          'hr',          'Xoá hồ sơ nhân viên'),
    ('hop_dong.create',           'hr',          'Tạo hợp đồng'),
    ('hop_dong.update',           'hr',          'Cập nhật hợp đồng'),
    ('hop_dong.terminate',        'hr',          'Thanh lý/chấm dứt hợp đồng'),
    ('bien_dong.create',          'hr',          'Tạo biến động nhân sự'),
    ('phong_ban.create',          'hr',          'Tạo phòng ban'),
    ('phong_ban.read',            'hr',          'Xem phòng ban'),
    ('phong_ban.update',          'hr',          'Cập nhật phòng ban'),
    ('phong_ban.delete',          'hr',          'Xoá phòng ban'),
    ('ngach_bac.read',            'hr',          'Xem ngạch/bậc lương'),
    ('nguoi_phu_thuoc.create',    'hr',          'Thêm người phụ thuộc'),
    ('nguoi_phu_thuoc.update',    'hr',          'Cập nhật người phụ thuộc'),
    ('nguoi_phu_thuoc.delete',    'hr',          'Xoá người phụ thuộc');

-- module 'timekeeping'
INSERT INTO system.permission (code, module, mo_ta) VALUES
    ('ca_lam_viec.create',         'timekeeping', 'Tạo ca làm việc'),
    ('ca_lam_viec.read',           'timekeeping', 'Xem ca làm việc'),
    ('ca_lam_viec.update',         'timekeeping', 'Cập nhật ca làm việc'),
    ('ca_lam_viec.delete',         'timekeeping', 'Xoá ca làm việc'),
    ('phan_ca.create',             'timekeeping', 'Phân ca cho nhân viên'),
    ('phan_ca.update',             'timekeeping', 'Cập nhật phân ca'),
    ('phan_ca.delete',             'timekeeping', 'Xoá phân ca'),
    ('cham_cong.create',           'timekeeping', 'Ghi nhận chấm công'),
    ('cham_cong.read',             'timekeeping', 'Xem bảng chấm công'),
    ('cham_cong.chot',             'timekeeping', 'Chốt bảng công tháng'),
    ('leave.create',               'timekeeping', 'Tạo đơn nghỉ phép'),
    ('leave.approve_cap1',         'timekeeping', 'Duyệt cấp 1 đơn nghỉ phép'),
    ('leave.approve_cap2',         'timekeeping', 'Duyệt cấp 2 đơn nghỉ phép'),
    ('leave.cancel',               'timekeeping', 'Hủy đơn nghỉ phép'),
    ('leave.read',                 'timekeeping', 'Xem đơn nghỉ phép'),
    ('overtime.create',            'timekeeping', 'Tạo đơn tăng ca'),
    ('overtime.approve_cap1',      'timekeeping', 'Duyệt cấp 1 đơn OT'),
    ('overtime.approve_cap2',      'timekeeping', 'Duyệt cấp 2 đơn OT'),
    ('overtime.cancel',            'timekeeping', 'Hủy đơn OT'),
    ('overtime.read',              'timekeeping', 'Xem đơn OT'),
    ('leave_balance.init',         'timekeeping', 'Khởi tạo quỹ phép'),
    ('leave_balance.read',         'timekeeping', 'Xem quỹ phép');

-- module 'social_ins'
INSERT INTO system.permission (code, module, mo_ta) VALUES
    ('so_bhxh.create',             'social_ins',  'Tạo sổ BHXH'),
    ('so_bhxh.read',               'social_ins',  'Xem sổ BHXH'),
    ('so_bhxh.update',             'social_ins',  'Cập nhật sổ BHXH'),
    ('muc_dong.read',              'social_ins',  'Xem mức đóng BHXH'),
    ('bien_dong_bhxh.create',      'social_ins',  'Tạo biến động BHXH'),
    ('bien_dong_bhxh.approve',     'social_ins',  'Duyệt biến động BHXH'),
    ('thu_tuc_bhxh.create',        'social_ins',  'Tạo thủ tục BHXH'),
    ('thu_tuc_bhxh.approve',       'social_ins',  'Duyệt thủ tục BHXH');

-- module 'payroll'
INSERT INTO system.permission (code, module, mo_ta) VALUES
    ('bang_tham_so.create',        'payroll',     'Tạo bảng tham số lương'),
    ('bang_tham_so.update',        'payroll',     'Cập nhật bảng tham số'),
    ('bang_luong.create',          'payroll',     'Tạo bảng lương'),
    ('bang_luong.read',            'payroll',     'Xem bảng lương'),
    ('bang_luong.chot',            'payroll',     'Chốt bảng lương'),
    ('bang_luong.chinh_sua_dong',  'payroll',     'Chỉnh sửa dòng bảng lương'),
    ('bang_luong.chinh_sua_hang',  'payroll',     'Chỉnh sửa hạng bảng lương'),
    ('thue_tncn.tinh',             'payroll',     'Tính thuế TNCN');

-- module 'system'
INSERT INTO system.permission (code, module, mo_ta) VALUES
    ('company.create',             'system',      'Tạo doanh nghiệp'),
    ('company.read',               'system',      'Xem doanh nghiệp'),
    ('company.update',             'system',      'Cập nhật doanh nghiệp'),
    ('user_account.create',        'system',      'Tạo tài khoản'),
    ('user_account.read',          'system',      'Xem tài khoản'),
    ('user_account.update',        'system',      'Cập nhật tài khoản'),
    ('user_account.lock',          'system',      'Khóa tài khoản'),
    ('user_account.unlock',        'system',      'Mở khóa tài khoản'),
    ('user_account.reset_password','system',      'Reset mật khẩu'),
    ('role.read',                  'system',      'Xem role'),
    ('permission.read',            'system',      'Xem permission'),
    ('audit_log.read',             'system',      'Xem audit log');

-- =====================================================================
-- ROLE_PERMISSION — gán permission cho role
-- =====================================================================

-- SYSTEM_ADMIN: tất cả
INSERT INTO system.role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM system.role r CROSS JOIN system.permission p
WHERE r.code = 'SYSTEM_ADMIN';

-- COMPANY_ADMIN: tất cả trừ system.audit_log.read
INSERT INTO system.role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM system.role r, system.permission p
WHERE r.code = 'COMPANY_ADMIN'
  AND NOT (p.module = 'system' AND p.code = 'audit_log.read');

-- HR_MANAGER: hr (read/create/update, không delete), timekeeping (read/leave/leave_balance),
-- social_ins (read/create/update)
INSERT INTO system.role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM system.role r, system.permission p
WHERE r.code = 'HR_MANAGER'
  AND (
        (p.module = 'hr'        AND p.code NOT LIKE '%.delete')
     OR (p.module = 'timekeeping' AND p.code IN (
            'ca_lam_viec.read','phan_ca.create','phan_ca.update','phan_ca.delete',
            'cham_cong.read','cham_cong.chot',
            'leave.create','leave.approve_cap1','leave.approve_cap2','leave.cancel','leave.read',
            'overtime.read',
            'leave_balance.init','leave_balance.read'))
     OR (p.module = 'social_ins' AND p.code IN (
            'so_bhxh.create','so_bhxh.read','so_bhxh.update','muc_dong.read',
            'bien_dong_bhxh.create','thu_tuc_bhxh.create'))
  );

-- ACCOUNTANT: payroll toàn quyền + social_ins.read + timekeeping.read
INSERT INTO system.role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM system.role r, system.permission p
WHERE r.code = 'ACCOUNTANT'
  AND (
        (p.module = 'payroll')
     OR (p.module = 'social_ins' AND p.code IN ('so_bhxh.read','muc_dong.read'))
     OR (p.module = 'timekeeping' AND p.code IN (
            'cham_cong.read','cham_cong.chot','leave.read','overtime.read','leave_balance.read'))
  );

-- MANAGER: timekeeping (leave/overtime duyệt + read HR)
INSERT INTO system.role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM system.role r, system.permission p
WHERE r.code = 'MANAGER'
  AND (
        (p.module = 'hr' AND p.code IN ('nhan_vien.read','phong_ban.read'))
     OR (p.module = 'timekeeping' AND p.code IN (
            'ca_lam_viec.read','phan_ca.read',
            'cham_cong.read',
            'leave.read','leave.approve_cap1','leave.cancel',
            'overtime.read','overtime.approve_cap1','overtime.cancel',
            'leave_balance.read'))
  );

-- EMPLOYEE: read-only profile + tạo đơn của mình
INSERT INTO system.role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM system.role r, system.permission p
WHERE r.code = 'EMPLOYEE'
  AND (
        (p.module = 'hr'         AND p.code IN ('nhan_vien.read','nguoi_phu_thuoc.create','nguoi_phu_thuoc.update','nguoi_phu_thuoc.delete'))
     OR (p.module = 'timekeeping' AND p.code IN (
            'ca_lam_viec.read',
            'leave.create','leave.cancel','leave.read',
            'overtime.create','overtime.cancel','overtime.read',
            'leave_balance.read'))
     OR (p.module = 'payroll'    AND p.code IN ('bang_luong.read'))
  );

-- (phan_ca.read chưa có trong danh sách permission seed; nếu cần thêm sau)