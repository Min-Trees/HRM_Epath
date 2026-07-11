-- =====================================================================
-- V4__system_admin_and_multi_tenant.sql
-- Module quản trị hệ thống + đa tenant + RBAC + audit log.
-- T11 (bổ sung theo bosung.md): company, user_account, role, permission,
-- role_permission, audit_log.
-- =====================================================================

CREATE SCHEMA IF NOT EXISTS system;

-- =====================================================================
-- ENUM TYPES (system)
-- =====================================================================

CREATE TYPE system.trang_thai_company AS ENUM (
    'HOAT_DONG',     -- Đang hoạt động
    'TAM_DUNG',      -- Tạm dừng (vd: vì chưa gia hạn)
    'NGUNG_HOAT_DONG' -- Ngừng vĩnh viễn
);

CREATE TYPE system.trang_thai_user_account AS ENUM (
    'ACTIVE',
    'LOCKED',
    'PENDING'        -- Mới tạo, chưa kích hoạt email
);

-- =====================================================================
-- BẢNG: system.company
-- =====================================================================

CREATE TABLE system.company (
    company_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ten_cong_ty             VARCHAR(200) NOT NULL,
    ma_so_thue              VARCHAR(20) NOT NULL UNIQUE,
    ma_so_dkkd              VARCHAR(50),
    dia_chi                 VARCHAR(500),
    so_dien_thoai           VARCHAR(20),
    email                   VARCHAR(200),
    nguoi_dai_dien_phap_luat VARCHAR(200),
    ngay_dang_ky            DATE NOT NULL DEFAULT CURRENT_DATE,
    goi_dich_vu             VARCHAR(50),
    trang_thai              system.trang_thai_company NOT NULL DEFAULT 'HOAT_DONG',
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_company_trangthai ON system.company(trang_thai);

-- =====================================================================
-- BẢNG: system.role
-- =====================================================================

CREATE TABLE system.role (
    role_id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                    VARCHAR(50) NOT NULL UNIQUE,
    ten_role                VARCHAR(200) NOT NULL,
    mo_ta                   VARCHAR(500),
    is_system               BOOLEAN NOT NULL DEFAULT FALSE, -- role mặc định, không được xoá
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =====================================================================
-- BẢNG: system.permission
-- =====================================================================

CREATE TABLE system.permission (
    permission_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                    VARCHAR(100) NOT NULL UNIQUE, -- vd 'nhan_vien.create', 'leave.approve_cap1'
    module                  VARCHAR(50) NOT NULL,         -- 'hr', 'timekeeping', 'social_ins', 'payroll', 'system'
    mo_ta                   VARCHAR(500)
);

CREATE INDEX idx_permission_module ON system.permission(module);

-- =====================================================================
-- BẢNG: system.role_permission (N-N)
-- =====================================================================

CREATE TABLE system.role_permission (
    role_id                 UUID NOT NULL REFERENCES system.role(role_id) ON DELETE CASCADE,
    permission_id           UUID NOT NULL REFERENCES system.permission(permission_id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permission_perm ON system.role_permission(permission_id);

-- =====================================================================
-- BẢNG: system.user_account
-- =====================================================================

CREATE TABLE system.user_account (
    user_id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id              UUID NOT NULL REFERENCES system.company(company_id),
    employee_id             UUID REFERENCES hr.nhan_vien(nhan_vien_id), -- nullable: user system chưa gắn NV
    username                VARCHAR(100) NOT NULL UNIQUE,
    password_hash           VARCHAR(200) NOT NULL,
    email                   VARCHAR(200) UNIQUE,
    role_codes              VARCHAR(500) NOT NULL DEFAULT '', -- CSV 'SYSTEM_ADMIN,COMPANY_ADMIN'
    trang_thai              system.trang_thai_user_account NOT NULL DEFAULT 'ACTIVE',
    last_login_at           TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_user_account_company ON system.user_account(company_id);
CREATE INDEX idx_user_account_employee ON system.user_account(employee_id);
CREATE INDEX idx_user_account_trangthai ON system.user_account(trang_thai);

-- =====================================================================
-- BẢNG: system.audit_log (append-only)
-- =====================================================================

CREATE TABLE system.audit_log (
    log_id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID REFERENCES system.user_account(user_id),
    company_id              UUID REFERENCES system.company(company_id),
    action                  VARCHAR(100) NOT NULL,    -- 'CREATE', 'UPDATE', 'DELETE', 'APPROVE'...
    module                  VARCHAR(50) NOT NULL,     -- 'hr', 'timekeeping', 'system'...
    entity_type             VARCHAR(50),              -- 'NghiPhep', 'Company'...
    entity_id               UUID,                     -- id của bản ghi bị tác động
    old_value               JSONB,
    new_value               JSONB,
    ip_address              VARCHAR(45),              -- IPv4/IPv6
    user_agent              VARCHAR(500),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_log_user ON system.audit_log(user_id);
CREATE INDEX idx_audit_log_company_module ON system.audit_log(company_id, module);
CREATE INDEX idx_audit_log_created_at ON system.audit_log(created_at);

-- Audit log KHÔNG cần updated_at; chỉ insert. Không tạo trigger chặn UPDATE
-- vì một số test có thể cleanup dữ liệu.

-- =====================================================================
-- CỘT company_id CHO BẢNG NGHIỆP VỤ ĐA TENANT
-- (V6 sẽ backfill giá trị mặc định)
-- =====================================================================

ALTER TABLE hr.nhan_vien ADD COLUMN company_id UUID;
ALTER TABLE hr.hop_dong_lao_dong ADD COLUMN company_id UUID;
ALTER TABLE payroll.bang_luong_thang ADD COLUMN company_id UUID;

CREATE INDEX idx_nhan_vien_company ON hr.nhan_vien(company_id);
CREATE INDEX idx_hop_dong_company ON hr.hop_dong_lao_dong(company_id);
CREATE INDEX idx_bang_luong_company ON payroll.bang_luong_thang(company_id);

-- FK tới system.company sẽ được thêm SAU khi V5 đã tạo default company.