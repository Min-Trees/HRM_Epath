-- =====================================================================
-- V6__migrate_data_default_company.sql
-- Backfill company_id cho dữ liệu nghiệp vụ hiện có (T1-T10 seed) về
-- default company. Đồng thời thêm FK tới system.company và seed
-- SYSTEM_ADMIN + COMPANY_ADMIN user.
-- =====================================================================

-- Default company UUID cố định (do V5 đã insert).
-- Backfill company_id cho các bảng nghiệp vụ:
UPDATE hr.nhan_vien
   SET company_id = '11111111-1111-1111-1111-111111111111'
 WHERE company_id IS NULL;

UPDATE hr.hop_dong_lao_dong h
   SET company_id = nv.company_id
  FROM hr.nhan_vien nv
 WHERE h.nhan_vien_id = nv.nhan_vien_id
   AND h.company_id IS NULL;

UPDATE payroll.bang_luong_thang bl
   SET company_id = nv.company_id
  FROM hr.nhan_vien nv
 WHERE bl.nhan_vien_id = nv.nhan_vien_id
   AND bl.company_id IS NULL;

-- Sau khi backfill, ép NOT NULL
ALTER TABLE hr.nhan_vien ALTER COLUMN company_id SET NOT NULL;
ALTER TABLE hr.hop_dong_lao_dong ALTER COLUMN company_id SET NOT NULL;
ALTER TABLE payroll.bang_luong_thang ALTER COLUMN company_id SET NOT NULL;

-- FK tới system.company (chỉ thêm sau khi backfill, tránh vi phạm ở row cũ)
ALTER TABLE hr.nhan_vien
    ADD CONSTRAINT fk_nhan_vien_company
    FOREIGN KEY (company_id) REFERENCES system.company(company_id);

ALTER TABLE hr.hop_dong_lao_dong
    ADD CONSTRAINT fk_hop_dong_company
    FOREIGN KEY (company_id) REFERENCES system.company(company_id);

ALTER TABLE payroll.bang_luong_thang
    ADD CONSTRAINT fk_bang_luong_company
    FOREIGN KEY (company_id) REFERENCES system.company(company_id);

ALTER TABLE system.user_account
    ADD CONSTRAINT fk_user_account_company
    FOREIGN KEY (company_id) REFERENCES system.company(company_id);

ALTER TABLE system.user_account
    ADD CONSTRAINT fk_user_account_employee
    FOREIGN KEY (employee_id) REFERENCES hr.nhan_vien(nhan_vien_id);

-- =====================================================================
-- SEED USER mặc định
-- Password hash dùng SHA-256 + salt (stub của T11). Khi chuyển sang Spring
-- Security + BCrypt sẽ regenerate.
-- Mật khẩu raw:
--   admin   -> 'admin123'  (SYSTEM_ADMIN)
--   cadmin  -> 'cadmin123' (COMPANY_ADMIN)
-- Hash được tính sẵn ở PasswordEncoder.encode; dùng Base64(salt):Base64(hash).
-- Salt ngẫu nhiên nên hash dưới đây chỉ là ví dụ — trong thực tế cần generate
-- động khi chạy. V6 chỉ chèn user; password sẽ được reset bằng
-- UserAccountService.resetPassword sau migration (hoặc bỏ qua — dev dùng
-- plaintext stub login).
-- =====================================================================

-- Vì SHA-256 + salt của PasswordEncoder là deterministic-after-salt-only và
-- không có đầu vào cố định, ta sẽ chèn user với password_hash placeholder.
-- Password sẽ được reset ngay sau migration qua Java code (xem
-- SystemDataInitializer @PostConstruct).

-- SYSTEM_ADMIN
INSERT INTO system.user_account (user_id, company_id, username, password_hash, email,
                                role_codes, trang_thai)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'admin',
    '__PLACEHOLDER_RESET_BY_INITIALIZER__',
    'admin@default-company.local',
    'SYSTEM_ADMIN',
    'ACTIVE'
);

-- COMPANY_ADMIN
INSERT INTO system.user_account (user_id, company_id, username, password_hash, email,
                                role_codes, trang_thai)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'cadmin',
    '__PLACEHOLDER_RESET_BY_INITIALIZER__',
    'cadmin@default-company.local',
    'COMPANY_ADMIN',
    'ACTIVE'
);