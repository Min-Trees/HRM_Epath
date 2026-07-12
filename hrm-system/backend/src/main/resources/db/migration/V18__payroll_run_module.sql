-- =====================================================================
-- V18: Payroll Run (Ky linh luong) - Hoan thien Payroll P3
--
-- T19 - Bo sung:
--   1. Bang ky_linh_luong (payroll run) - workflow cua 1 ky luong
--      CHO_CHAY -> DANG_CHAY -> DA_CHAY -> DA_DUYET_CAP_1 -> DA_DUYET_CAP_2
--      -> DA_CHI_TRA -> HUY
--   2. Bang chi_tiet_khoan_thuong (bonus/allowance items)
--   3. Bang audit_payroll_run - log thay doi trang thai ky luong
--   4. ENUM trang_thai_ky_luong + loai_khoan_luong
-- =====================================================================

CREATE TYPE payroll.trang_thai_ky_luong AS ENUM (
    'CHO_CHAY',
    'DANG_CHAY',
    'DA_CHAY',
    'DA_DUYET_CAP_1',
    'DA_DUYET_CAP_2',
    'DA_CHI_TRA',
    'HUY'
);

CREATE TYPE payroll.loai_khoan_luong AS ENUM (
    'PHU_CAP',
    'THUONG',
    'TET',
    'COMISSION',
    'PHAT',
    'TAM_UNG',
    'BHXH_NLD',
    'THUE_TNCN',
    'KHAC'
);

CREATE TYPE payroll.loai_hinh_chuyen AS ENUM (
    'BANK_TRANSFER',
    'CASH',
    'CHEQUE'
);

-- Bang ky linh luong
CREATE TABLE payroll.ky_linh_luong (
    ky_linh_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_ky_linh        VARCHAR(20) UNIQUE,
    thang             INTEGER NOT NULL CHECK (thang BETWEEN 1 AND 12),
    nam               INTEGER NOT NULL,
    ngay_chot_cong    DATE,
    ngay_chi_tra      DATE,
    trang_thai        payroll.trang_thai_ky_luong NOT NULL DEFAULT 'CHO_CHAY',
    loai_hinh_chi_tra payroll.loai_hinh_chuyen NOT NULL DEFAULT 'BANK_TRANSFER',
    tong_nhan_vien    INTEGER NOT NULL DEFAULT 0,
    tong_thuc_linh    NUMERIC(18,2) NOT NULL DEFAULT 0,
    tong_bhxh_nld     NUMERIC(18,2) NOT NULL DEFAULT 0,
    tong_thue_tncn    NUMERIC(18,2) NOT NULL DEFAULT 0,
    ghi_chu           TEXT,
    nguoi_chay_id     UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    nguoi_duyet_cap_1_id UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    nguoi_duyet_cap_2_id UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    ngay_chay         TIMESTAMPTZ,
    ngay_duyet_cap_1  TIMESTAMPTZ,
    ngay_duyet_cap_2  TIMESTAMPTZ,
    ngay_chi_tra_thuc_te TIMESTAMPTZ,
    file_zip_url      VARCHAR(500),                  -- link file zip transfer bank
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_ky_linh_thang_nam UNIQUE (thang, nam)
);

CREATE INDEX idx_ky_luong_trangthai ON payroll.ky_linh_luong(trang_thai);
CREATE INDEX idx_ky_luong_thang_nam ON payroll.ky_linh_luong(nam DESC, thang DESC);

-- Khoan thuong/phu cap theo NV trong 1 ky
CREATE TABLE payroll.khoan_luong (
    khoan_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bang_luong_id     UUID NOT NULL REFERENCES payroll.bang_luong_thang(bang_luong_id),
    ky_linh_id        UUID NOT NULL REFERENCES payroll.ky_linh_luong(ky_linh_id),
    loai_khoan        payroll.loai_khoan_luong NOT NULL,
    so_tien           NUMERIC(14,2) NOT NULL,
    mo_ta             VARCHAR(300),
    nguoi_them_id     UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_khoan_luong_sotien CHECK (so_tien >= 0)
);

CREATE INDEX idx_khoan_luong_bang ON payroll.khoan_luong(bang_luong_id);
CREATE INDEX idx_khoan_luong_ky ON payroll.khoan_luong(ky_linh_id);

-- Audit log
CREATE TABLE payroll.audit_ky_luong (
    audit_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ky_linh_id        UUID NOT NULL REFERENCES payroll.ky_linh_luong(ky_linh_id),
    trang_thai_cu     payroll.trang_thai_ky_luong,
    trang_thai_moi    payroll.trang_thai_ky_luong NOT NULL,
    nguoi_thuc_hien_id UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    hanh_dong         VARCHAR(50) NOT NULL,
    ghi_chu           TEXT,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_ky_luong ON payroll.audit_ky_luong(ky_linh_id, created_at DESC);

-- Triggers
CREATE TRIGGER trg_ky_luong_updated_at BEFORE UPDATE ON payroll.ky_linh_luong
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();

COMMENT ON TABLE payroll.ky_linh_luong IS 'T19 - Workflow ky linh luong';
COMMENT ON TABLE payroll.khoan_luong IS 'T19 - Khoan thuong/phu cap theo NV trong ky';