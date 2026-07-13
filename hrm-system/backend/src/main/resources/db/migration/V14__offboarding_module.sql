-- =====================================================================
-- V14__offboarding_module.sql
-- T14: Module Offboarding - Quy trinh nghi viec
--
-- Bo sung:
--   1. Bang offboarding_case     - ho so nghi viec cua NV
--   2. Bang offboarding_task     - checklist task (HR/IT/Manager/Payroll)
--   3. Bang severance_calc       - tinh tro cap thoi viec
--   4. Bang offboarding_asset    - thu hoi tai san (laptop, the,...)
--   5. ENUM types
--   6. View ho tro dashboard offboarding
-- =====================================================================

-- =====================================================================
-- ENUM TYPES
-- =====================================================================

CREATE TYPE hr.ly_do_nghi_viec AS ENUM (
    'NGHI_VIEC_TU_NGUYEN',
    'SA_THAI',
    'HET_HAN_HDLD',
    'NGHI_HUU',
    'THOI_VIEC',
    'KHAC'
);

CREATE TYPE hr.trang_thai_offboarding AS ENUM (
    'MOI_TAO',           -- HR vua tao case
    'CHO_DUYET',         -- cho NV/Manager xac nhan
    'DANG_THUC_HIEN',     -- dang lam checklist
    'CHO_QUYET_TOAN',     -- cho payroll quyet toan
    'HOAN_THANH',         -- da xong
    'HUY'
);

CREATE TYPE hr.trang_thai_task AS ENUM (
    'CHUA_LAM',
    'DANG_LAM',
    'HOAN_THANH',
    'KHONG_AP_DUNG'
);

CREATE TYPE hr.loai_task_offboarding AS ENUM (
    'TRA_TAI_SAN',           -- laptop, the, CMND ban sao
    'BAN_GIAO_CONG_VIEC',    -- hand-over task
    'THU_HOI_QUYEN_TRUY_CAP', -- email, tai khoan
    'PHEP_NAM_CON_DU',       -- tinh phep nam con du
    'KY_VAN_BANG_LUONG',     -- ky phieu luong cuoi
    'QUYET_TOAN_THUE_TNCN',  -- quyet toan thue TNCN cuoi cung
    'BAO_GIAM_BHXH',         -- bao giam BHXH
    'CHOT_SO_BHXH_D07',      -- chot so BHXH (mau D07)
    'TRA_CCCD',              -- tra CMND/CCCD ban goc (neu giu)
    'PHONG_VAN_THAM_PHONG',  -- exit interview
    'XAC_NHAN_KHONG_NO',     -- xac nhan khong con khoan no
    'KHAC'
);

-- =====================================================================
-- BANG 1: OFFBOARDING_CASE (ho so nghi viec)
-- =====================================================================

CREATE TABLE hr.offboarding_case (
    case_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    so_quyet_dinh        VARCHAR(50) NOT NULL,
    ngay_quyet_dinh      DATE NOT NULL,
    ngay_nghi_viec_cuoi  DATE NOT NULL,                     -- ngay cuoi cung lam viec
    ngay_chinh_thuc_nghi DATE NOT NULL,                     -- ngay chinh thuc nghi (thuong = ngay_nghi_viec_cuoi + 1)
    ly_do                hr.ly_do_nghi_viec NOT NULL,
    ly_do_chi_tiet       TEXT,
    trang_thai           hr.trang_thai_offboarding NOT NULL DEFAULT 'MOI_TAO',
    nguoi_tao_id         UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    nguoi_duyet_id       UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    ngay_duyet           TIMESTAMPTZ,
    ghi_chu              TEXT,
    -- Lien ket sang BHXH & Payroll
    bhxh_bien_dong_giam_id UUID,                            -- FK sang bhxh_bien_dong (giam) khi phat sinh
    quyet_toan_thue_id     UUID,                            -- FK sang quyet_toan_thue khi phat sinh
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by           UUID,
    updated_by           UUID,
    CONSTRAINT chk_ngay_nghi CHECK (ngay_chinh_thuc_nghi >= ngay_nghi_viec_cuoi)
);

CREATE INDEX idx_offb_nv ON hr.offboarding_case(nhan_vien_id);
CREATE INDEX idx_offb_trangthai ON hr.offboarding_case(trang_thai);
CREATE INDEX idx_offb_ngay_nghi ON hr.offboarding_case(ngay_chinh_thuc_nghi DESC);

-- Moi nhan vien chi co 1 case offboarding "dang hoat dong" (chua HOAN_THANH/HUY)
CREATE UNIQUE INDEX uq_offb_active_per_nv ON hr.offboarding_case(nhan_vien_id)
    WHERE trang_thai NOT IN ('HOAN_THANH', 'HUY');

-- =====================================================================
-- BANG 2: OFFBOARDING_TASK (checklist)
-- =====================================================================

CREATE TABLE hr.offboarding_task (
    task_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id              UUID NOT NULL REFERENCES hr.offboarding_case(case_id) ON DELETE CASCADE,
    loai_task            hr.loai_task_offboarding NOT NULL,
    mo_ta                TEXT,
    nguoi_phu_trach_id   UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    han_hoan_thanh       DATE,
    trang_thai           hr.trang_thai_task NOT NULL DEFAULT 'CHUA_LAM',
    ngay_hoan_thanh      TIMESTAMPTZ,
    nguoi_hoan_thanh_id  UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    file_dinh_kem_url    VARCHAR(500),
    ghi_chu              TEXT,
    thu_tu               INTEGER NOT NULL DEFAULT 0,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_offbtask_case ON hr.offboarding_task(case_id);
CREATE INDEX idx_offbtask_trangthai ON hr.offboarding_task(trang_thai);
CREATE INDEX idx_offbtask_phutrach ON hr.offboarding_task(nguoi_phu_trach_id);

-- =====================================================================
-- BANG 3: OFFBOARDING_ASSET (thu hoi tai san)
-- =====================================================================

CREATE TABLE hr.offboarding_asset (
    asset_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id              UUID NOT NULL REFERENCES hr.offboarding_case(case_id) ON DELETE CASCADE,
    ten_tai_san          VARCHAR(200) NOT NULL,
    ma_tai_san           VARCHAR(50),
    tinh_trang           VARCHAR(200),
    da_thu_hoi           BOOLEAN NOT NULL DEFAULT FALSE,
    ngay_thu_hoi         DATE,
    ghi_chu              TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_offbasset_case ON hr.offboarding_asset(case_id);

-- =====================================================================
-- BANG 4: SEVERANCE_CALC (tinh tro cap thoi viec)
-- =====================================================================
-- Theo BLLD 2019 (Dieu 44-46):
--   * Nghi viec tu nguyen, het han HD: 1/2 thang luong / nam (tham nien >=12 thang)
--   * Don phuong sa thai khong dung: thuong them 1 thang luong (BHXH)
--   * Nghi huu: khong co (da huong luong huu)

CREATE TABLE hr.severance_calc (
    severance_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id              UUID NOT NULL UNIQUE REFERENCES hr.offboarding_case(case_id) ON DELETE CASCADE,
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    thoi_gian_lam_viec_thang INTEGER NOT NULL,             -- tong so thang lam viec
    so_nam_tham_nien     NUMERIC(6,2) NOT NULL,           -- so nam (lam tron)
    luong_thoi_viec_binh_quan NUMERIC(14,2) NOT NULL,     -- luong binh quan 6 thang cuoi (theo BLLD)
    he_so                NUMERIC(4,2) NOT NULL,           -- 0.5 cho tu nguyen, 1.0 cho sa thai
    so_tien_tro_cap      NUMERIC(14,2) NOT NULL,          -- = so_nam_tham_nien * he_so * luong_binh_quan
    ghi_chu              TEXT,
    nguoi_tinh_id        UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    ngay_tinh            TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_severance_he_so CHECK (he_so IN (0.5, 1.0)),
    CONSTRAINT chk_severance_thoigian CHECK (thoi_gian_lam_viec_thang >= 0),
    CONSTRAINT chk_severance_tien CHECK (so_tien_tro_cap >= 0)
);

CREATE INDEX idx_severance_nv ON hr.severance_calc(nhan_vien_id);

-- =====================================================================
-- TRIGGER: updated_at tu dong
-- =====================================================================

CREATE TRIGGER trg_offbcase_updated_at BEFORE UPDATE ON hr.offboarding_case
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_offbtask_updated_at BEFORE UPDATE ON hr.offboarding_task
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();

-- =====================================================================
-- VIEW: tien do offboarding (so task hoan thanh / tong)
-- =====================================================================

CREATE OR REPLACE VIEW hr.v_offboarding_progress AS
SELECT
    c.case_id,
    c.nhan_vien_id,
    nv.ho_ten,
    nv.ma_nv,
    c.ly_do,
    c.ngay_chinh_thuc_nghi,
    c.trang_thai,
    COUNT(t.*) AS tong_task,
    COUNT(t.*) FILTER (WHERE t.trang_thai = 'HOAN_THANH') AS task_hoan_thanh,
    COUNT(t.*) FILTER (WHERE t.trang_thai = 'KHONG_AP_DUNG') AS task_khong_ap_dung,
    CASE
        WHEN COUNT(t.*) = 0 THEN 0
        ELSE ROUND(
            100.0 * COUNT(t.*) FILTER (WHERE t.trang_thai IN ('HOAN_THANH', 'KHONG_AP_DUNG'))::numeric
                  / COUNT(t.*), 2
        )
    END AS tien_do_phan_tram
FROM hr.offboarding_case c
JOIN hr.nhan_vien nv ON nv.nhan_vien_id = c.nhan_vien_id
LEFT JOIN hr.offboarding_task t ON t.case_id = c.case_id
WHERE c.trang_thai NOT IN ('HUY')
GROUP BY c.case_id, nv.ho_ten, nv.ma_nv;

COMMENT ON TABLE hr.offboarding_case IS 'T14: Ho so nghi viec cua nhan vien';
COMMENT ON TABLE hr.offboarding_task IS 'T14: Checklist task theo case';
COMMENT ON TABLE hr.offboarding_asset IS 'T14: Thu hoi tai san khi nghi viec';
COMMENT ON TABLE hr.severance_calc IS 'T14: Tinh tro cap thoi viec theo BLLD 2019 D44-46';