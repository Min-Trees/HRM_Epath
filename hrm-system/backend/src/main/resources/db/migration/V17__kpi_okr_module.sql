-- =====================================================================
-- V17: Module Đánh giá hiệu suất (KPI / OKR)
--
-- T18 - Quy trinh danh gia hieu suat theo chu ky (quy / nam):
--   1. Chu ky danh gia (kpi_cycle): Q1/2026, Q2/2026, nam 2026...
--   2. Mau KPI (kpi_template): HR tao san cac mau theo chuc danh
--   3. Gan KPI cho NV (kpi_assignment): manager gan cho NV trong chu ky
--   4. Ket qua KPI (kpi_result): NV tu danh gia (self-assessment)
--   5. Review cua quan ly (kpi_review): manager cham diem + nhan xet
--   6. Phe duyet HR (kpi_final_rating): HR chot xep loai (A/B/C/D)
-- =====================================================================

CREATE SCHEMA IF NOT EXISTS performance;

-- ENUM types
CREATE TYPE performance.trang_thai_chu_ky AS ENUM (
    'MOI_TAO',
    'DANG_DANH_GIA',
    'DA_DONG',
    'HUY'
);

CREATE TYPE performance.loai_muc_tieu AS ENUM (
    'KPI',            -- Key Performance Indicator (do luong bang so)
    'OKR',            -- Objective + Key Result (dinh huong + ket qua)
    'NHIEM_VU'        -- Nhiem vu thuong (khong co chi so do luong)
);

CREATE TYPE performance.trang_thai_assignment AS ENUM (
    'MOI_GAN',
    'NV_DA_TU_DANH_GIA',
    'MANAGER_DA_REVIEW',
    'HR_DA_PHE_DUYET',
    'TU_CHOI',
    'HUY'
);

CREATE TYPE performance.xep_loai AS ENUM (
    'A',     -- Xuat sac (90-100)
    'B',     -- Tot (75-89)
    'C',     -- Trung binh (50-74)
    'D'      -- Yeu (<50)
);

-- Chu ky danh gia (VD: Q1-2026, Nam 2026)
CREATE TABLE performance.kpi_cycle (
    cycle_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ten_chu_ky        VARCHAR(100) NOT NULL,
    loai_chu_ky       VARCHAR(20) NOT NULL,         -- QUARTER / HALF_YEAR / YEAR
    ngay_bat_dau      DATE NOT NULL,
    ngay_ket_thuc     DATE NOT NULL,
    han_nv_tu_danh_gia DATE,                        -- deadline NV tu danh gia
    han_manager_review DATE,                        -- deadline Manager review
    han_hr_phe_duyet  DATE,                         -- deadline HR phe duyet
    trang_thai        performance.trang_thai_chu_ky NOT NULL DEFAULT 'MOI_TAO',
    nguoi_tao_id      UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    mo_ta             TEXT,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_cycle_ngay CHECK (ngay_ket_thuc > ngay_bat_dau)
);

CREATE INDEX idx_cycle_status ON performance.kpi_cycle(trang_thai);
CREATE INDEX idx_cycle_ngay ON performance.kpi_cycle(ngay_bat_dau DESC);

-- Mau KPI (template)
CREATE TABLE performance.kpi_template (
    template_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ten_mau           VARCHAR(200) NOT NULL,
    mo_ta             TEXT,
    ap_dung_chuc_danh VARCHAR(200),                -- phong ban / chuc danh ap dung
    nguoi_tao_id      UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    is_active         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_template_active ON performance.kpi_template(is_active) WHERE is_active = TRUE;

-- Muc tieu KPI gan cho NV (trong 1 cycle)
CREATE TABLE performance.kpi_assignment (
    assignment_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cycle_id          UUID NOT NULL REFERENCES performance.kpi_cycle(cycle_id),
    nhan_vien_id      UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    template_id       UUID REFERENCES performance.kpi_template(template_id),
    ten_muc_tieu      VARCHAR(300) NOT NULL,
    loai_muc_tieu     performance.loai_muc_tieu NOT NULL DEFAULT 'KPI',
    don_vi_do         VARCHAR(50),                 -- %, VND, so luong, lan...
    target_value      NUMERIC(18,2) NOT NULL,
    trong_so          NUMERIC(5,2) NOT NULL DEFAULT 1.0,    -- trong so 0-100
    mo_ta_chi_tiet    TEXT,
    nguoi_gan_id      UUID REFERENCES hr.nhan_vien(nhan_vien_id),  -- manager gan
    trang_thai        performance.trang_thai_assignment NOT NULL DEFAULT 'MOI_GAN',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_assign_nv_cycle ON performance.kpi_assignment(nhan_vien_id, cycle_id);
CREATE INDEX idx_assign_cycle ON performance.kpi_assignment(cycle_id);
CREATE INDEX idx_assign_status ON performance.kpi_assignment(trang_thai);

-- Tu danh gia cua NV
CREATE TABLE performance.kpi_self_assessment (
    self_assessment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id      UUID NOT NULL UNIQUE REFERENCES performance.kpi_assignment(assignment_id),
    actual_value       NUMERIC(18,2),
    ty_le_hoan_thanh   NUMERIC(5,2),                 -- % hoan thanh target
    diem_tu_danh_gia   NUMERIC(5,2),                  -- 0-100
    nhan_xet_nv        TEXT,
    minh_chung_url     VARCHAR(500),
    ngay_tu_danh_gia   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Manager review
CREATE TABLE performance.kpi_review (
    review_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id     UUID NOT NULL UNIQUE REFERENCES performance.kpi_assignment(assignment_id),
    nguoi_review_id   UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    actual_value      NUMERIC(18,2),                   -- manager xac nhan
    diem_manager      NUMERIC(5,2) NOT NULL CHECK (diem_manager BETWEEN 0 AND 100),
    diem_trung_binh   NUMERIC(5,2),  -- tinh o service (60% manager + 40% self_assessment)
    nhan_xet_manager  TEXT,
    diem_manh         TEXT,
    diem_yeu          TEXT,
    de_xuat_xep_loai  performance.xep_loai,
    ngay_review       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- HR phe duyet va chot xep loai
CREATE TABLE performance.kpi_final_rating (
    final_rating_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id     UUID NOT NULL UNIQUE REFERENCES performance.kpi_assignment(assignment_id),
    nguoi_phe_duyet_id UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    xep_loai_cuoi     performance.xep_loai NOT NULL,
    diem_cuoi         NUMERIC(5,2) NOT NULL CHECK (diem_cuoi BETWEEN 0 AND 100),
    nhan_xet_hr       TEXT,
    he_so_thuong      NUMERIC(4,2),                 -- 1.0 = thuong 100% (he so)
    ngay_phe_duyet    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Triggers
CREATE TRIGGER trg_cycle_updated_at BEFORE UPDATE ON performance.kpi_cycle
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_template_updated_at BEFORE UPDATE ON performance.kpi_template
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_assign_updated_at BEFORE UPDATE ON performance.kpi_assignment
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();

COMMENT ON SCHEMA performance IS 'T18 - Module danh gia hieu suat (KPI/OKR)';