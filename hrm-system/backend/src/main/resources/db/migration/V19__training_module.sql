-- =====================================================================
-- V19: Training Module (Dao tao)
--
-- T20 - Bo sung:
--   1. Bang chuong_trinh_dao_tao - course templates
--   2. Bang lop_hoc - class instances linked to course
--   3. Bang dang_ky_dao_tao - employee registration
--   4. Bang diem_danh_dao_tao - attendance per session
--   5. Bang danh_gia_sau_dao_tao - post-training evaluation
--   ENUMs: trang_thai_chuong_trinh, loai_chuong_trinh, trang_thai_lop,
--          trang_thai_dang_ky, trang_thai_tham_du, ket_qua_danh_gia
-- =====================================================================

CREATE SCHEMA IF NOT EXISTS training;

CREATE TYPE training.trang_thai_chuong_trinh AS ENUM (
    'NHAP',
    'CONG_BO',
    'NGUNG'
);

CREATE TYPE training.loai_chuong_trinh AS ENUM (
    'KY_NANG_MEMM',
    'KY_NANG_CHUYEN_MON',
    'AN_TOAN_LAO_DONG',
    'LANG_DAO_VAN_HOA',
    'QUAN_LY',
    'CHUNG_CHI_BAT_BUOC',
    'KHAC'
);

CREATE TYPE training.trang_thai_lop AS ENUM (
    'MO_DANG_KY',
    'DONG_DANG_KY',
    'DANG_DIEN_RA',
    'HOAN_THANH',
    'HUY'
);

CREATE TYPE training.trang_thai_dang_ky AS ENUM (
    'CHO_DUYET',
    'DA_CHAP_NHAN',
    'TU_CHOI',
    'HUY'
);

CREATE TYPE training.trang_thai_tham_du AS ENUM (
    'CHUA_DI_HOC',
    'CO_MAT',
    'VANG',
    'VANG_CO_PHEP'
);

CREATE TYPE training.ket_qua_danh_gia AS ENUM (
    'XUAT_SAC',
    'TOT',
    'TRUNG_BINH',
    'YEU',
    'KHONG_DANH_GIA'
);

CREATE TABLE training.chuong_trinh_dao_tao (
    chuong_trinh_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_chuong_trinh    VARCHAR(30) UNIQUE NOT NULL,
    ten_chuong_trinh   VARCHAR(300) NOT NULL,
    loai_chuong_trinh  training.loai_chuong_trinh NOT NULL,
    mo_ta              TEXT,
    muc_tieu           TEXT,
    thoi_luong_gio     NUMERIC(5,1) NOT NULL CHECK (thoi_luong_gio > 0),
    diem_danh_gia_toi_thieu NUMERIC(4,2) NOT NULL DEFAULT 60.0,  -- diem toi thieu de dat
    chung_chi          VARCHAR(100),                                -- CCC, IOSH...
    trang_thai         training.trang_thai_chuong_trinh NOT NULL DEFAULT 'NHAP',
    nguoi_tao_id       UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_chuong_trinh_loai ON training.chuong_trinh_dao_tao(loai_chuong_trinh);
CREATE INDEX idx_chuong_trinh_trang_thai ON training.chuong_trinh_dao_tao(trang_thai);

CREATE TABLE training.lop_hoc (
    lop_hoc_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_lop            VARCHAR(30) UNIQUE NOT NULL,
    chuong_trinh_id   UUID NOT NULL REFERENCES training.chuong_trinh_dao_tao(chuong_trinh_id),
    ten_lop           VARCHAR(300) NOT NULL,
    ngay_bat_dau      DATE NOT NULL,
    ngay_ket_thuc     DATE NOT NULL,
    so_buoi           INTEGER NOT NULL CHECK (so_buoi > 0),
    so_cho_toi_da     INTEGER NOT NULL DEFAULT 30,
    dia_diem          VARCHAR(200),
    giang_vien        VARCHAR(200),
    chi_phi_moi_nv    NUMERIC(14,2) DEFAULT 0,
    trang_thai        training.trang_thai_lop NOT NULL DEFAULT 'MO_DANG_KY',
    ghi_chu           TEXT,
    nguoi_phu_trach_id UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_lop_hoc_dates CHECK (ngay_ket_thuc >= ngay_bat_dau)
);

CREATE INDEX idx_lop_hoc_chuong_trinh ON training.lop_hoc(chuong_trinh_id);
CREATE INDEX idx_lop_hoc_trang_thai ON training.lop_hoc(trang_thai);
CREATE INDEX idx_lop_hoc_dates ON training.lop_hoc(ngay_bat_dau DESC);

CREATE TABLE training.dang_ky_dao_tao (
    dang_ky_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lop_hoc_id        UUID NOT NULL REFERENCES training.lop_hoc(lop_hoc_id),
    nhan_vien_id      UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    ngay_dang_ky      TIMESTAMPTZ NOT NULL DEFAULT now(),
    trang_thai        training.trang_thai_dang_ky NOT NULL DEFAULT 'CHO_DUYET',
    ly_do_dang_ky     TEXT,
    nguoi_duyet_id    UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    ngay_duyet        TIMESTAMPTZ,
    ghi_chu_duyet     TEXT,
    diem_tong_ket     NUMERIC(4,2),                 -- tong ket sau khi diem danh + danh gia
    chung_chi_cap     VARCHAR(100),                 -- cap luc nao
    ngay_cap          DATE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_dang_ky_lop_nv UNIQUE (lop_hoc_id, nhan_vien_id)
);

CREATE INDEX idx_dang_ky_nv ON training.dang_ky_dao_tao(nhan_vien_id);
CREATE INDEX idx_dang_ky_lop ON training.dang_ky_dao_tao(lop_hoc_id);
CREATE INDEX idx_dang_ky_trang_thai ON training.dang_ky_dao_tao(trang_thai);

CREATE TABLE training.diem_danh_dao_tao (
    diem_danh_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dang_ky_id        UUID NOT NULL REFERENCES training.dang_ky_dao_tao(dang_ky_id),
    buoi_so           INTEGER NOT NULL CHECK (buoi_so > 0),
    ngay_hoc          DATE NOT NULL,
    trang_thai        training.trang_thai_tham_du NOT NULL DEFAULT 'CHUA_DI_HOC',
    diem_bai_tap      NUMERIC(4,2),
    ghi_chu           VARCHAR(300),
    nguoi_diem_danh_id UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_diem_danh_buoi UNIQUE (dang_ky_id, buoi_so)
);

CREATE INDEX idx_diem_danh_dang_ky ON training.diem_danh_dao_tao(dang_ky_id);

CREATE TABLE training.danh_gia_sau_dao_tao (
    danh_gia_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dang_ky_id        UUID NOT NULL UNIQUE REFERENCES training.dang_ky_dao_tao(dang_ky_id),
    diem_noi_dung     NUMERIC(4,2) CHECK (diem_noi_dung BETWEEN 0 AND 100),
    diem_giang_vien   NUMERIC(4,2) CHECK (diem_giang_vien BETWEEN 0 AND 100),
    diem_thuc_hanh    NUMERIC(4,2) CHECK (diem_thuc_hanh BETWEEN 0 AND 100),
    diem_trung_binh   NUMERIC(4,2),  -- tinh o service (40% noi dung + 30% GV + 30% thuc hanh)
    ket_qua           training.ket_qua_danh_gia NOT NULL DEFAULT 'TRUNG_BINH',
    y_kien_nguoi_hoc  TEXT,
    y_kien_gv         TEXT,
    ngay_danh_gia     TIMESTAMPTZ NOT NULL DEFAULT now(),
    nguoi_danh_gia_id UUID REFERENCES hr.nhan_vien(nhan_vien_id)
);

CREATE INDEX idx_danh_gia_ket_qua ON training.danh_gia_sau_dao_tao(ket_qua);

-- Triggers
CREATE TRIGGER trg_chuong_trinh_updated_at BEFORE UPDATE ON training.chuong_trinh_dao_tao
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_lop_hoc_updated_at BEFORE UPDATE ON training.lop_hoc
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_dang_ky_updated_at BEFORE UPDATE ON training.dang_ky_dao_tao
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_diem_danh_updated_at BEFORE UPDATE ON training.diem_danh_dao_tao
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();

COMMENT ON SCHEMA training IS 'T20 - Module Dao tao (training, enrollment, attendance, evaluation)';