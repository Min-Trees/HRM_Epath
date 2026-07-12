-- =====================================================================
-- V16: Module Tuyen dung (Recruitment)
--
-- T17 - Quy trinh tuyen dung:
--   1. Yeu cau tuyen dung (yeu_cau_tuyen_dung) - nguoi quan ly / HR tao
--   2. Vi tri tuyen dung (vi_tri_tuyen_dung) - chi tiet cho moi yeu cau
--   3. Ung vien (ung_vien) - ho so ung vien
--   4. Lich phong van (lich_phong_van) - dat lich PV
--   5. Danh gia ung vien (danh_gia_ung_vien) - ket qua PV
--   6. Quyet dinh tuyen (quyet_dinh_tuyen) - offer / tu choi
-- =====================================================================

CREATE SCHEMA IF NOT EXISTS recruitment;

-- ENUM types
CREATE TYPE recruitment.trang_thai_yeu_cau AS ENUM (
    'MOI_TAO',
    'CHO_PHE_DUYET',
    'DA_PHE_DUYET',
    'DANG_TUYEN',
    'DA_DONG',
    'HUY'
);

CREATE TYPE recruitment.trang_thai_ung_vien AS ENUM (
    'MOI_NOP_HO_SO',
    'CHO_PHONG_VAN_VONG_1',
    'CHO_PHONG_VAN_VONG_2',
    'CHO_DANH_GIA',
    'DE_NGHI_TUYEN',
    'TU_CHOI',
    'RUT_HO_SO',
    'DA_TUYEN'
);

CREATE TYPE recruitment.ket_qua_phong_van AS ENUM (
    'DAT',
    'KHA',
    'TRUNG_BINH',
    'KHONG_DAT',
    'HOAN_THANH'
);

CREATE TYPE recruitment.loai_hop_dong_de_nghi AS ENUM (
    'THU_VIEC',
    'CHINH_THUC',
    'HOP_DONG_XAC_DINH_THOI_HAN',
    'CONG_TAC_VIEN'
);

-- Yeu cau tuyen dung
CREATE TABLE recruitment.yeu_cau_tuyen_dung (
    yeu_cau_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_yeu_cau          VARCHAR(20) UNIQUE,
    tieu_de             VARCHAR(200) NOT NULL,
    phong_ban_id        UUID NOT NULL REFERENCES hr.phong_ban(phong_ban_id),
    nguoi_yeu_cau_id    UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    so_luong_can        INTEGER NOT NULL CHECK (so_luong_can > 0),
    ly_do               TEXT,
    muc_luong_de_xuat   NUMERIC(14,2),
    trang_thai          recruitment.trang_thai_yeu_cau NOT NULL DEFAULT 'MOI_TAO',
    nguoi_phe_duyet_id  UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    ngay_phe_duyet      TIMESTAMPTZ,
    ngay_can_tuyen      DATE,
    ngay_dong_yeu_cau   DATE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_yc_td_phongban ON recruitment.yeu_cau_tuyen_dung(phong_ban_id);
CREATE INDEX idx_yc_td_trangthai ON recruitment.yeu_cau_tuyen_dung(trang_thai);
CREATE INDEX idx_yc_td_ngay_can ON recruitment.yeu_cau_tuyen_dung(ngay_can_tuyen);

-- Ung vien (ho so)
CREATE TABLE recruitment.ung_vien (
    ung_vien_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_ung_vien         VARCHAR(20) UNIQUE,
    ho_ten              VARCHAR(200) NOT NULL,
    ngay_sinh           DATE,
    gioi_tinh           VARCHAR(10),
    email               VARCHAR(200),
    so_dien_thoai       VARCHAR(20),
    dia_chi             VARCHAR(300),
    cmnd                VARCHAR(20),
    ngay_cap_cmnd       DATE,
    noi_cap_cmnd        VARCHAR(200),
    trinh_do            VARCHAR(100),
    truong_dao_tao      VARCHAR(300),
    chuyen_nganh        VARCHAR(200),
    nam_tot_nghiep      INTEGER,
    so_nam_kinh_nghiem  INTEGER,
    cong_ty_cu          VARCHAR(300),
    chuc_danh_cu        VARCHAR(200),
    cv_url              VARCHAR(500),
    thu_xin_viec_url    VARCHAR(500),
    ky_nang             TEXT,
    ghi_chu             TEXT,
    trang_thai          recruitment.trang_thai_ung_vien NOT NULL DEFAULT 'MOI_NOP_HO_SO',
    yeu_cau_id          UUID REFERENCES recruitment.yeu_cau_tuyen_dung(yeu_cau_id),
    nguoi_gioi_thieu_id UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_uv_trangthai ON recruitment.ung_vien(trang_thai);
CREATE INDEX idx_uv_yeucau ON recruitment.ung_vien(yeu_cau_id);
CREATE INDEX idx_uv_email ON recruitment.ung_vien(email);
CREATE INDEX idx_uv_phone ON recruitment.ung_vien(so_dien_thoai);

-- Lich phong van
CREATE TABLE recruitment.lich_phong_van (
    lich_pv_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ung_vien_id         UUID NOT NULL REFERENCES recruitment.ung_vien(ung_vien_id),
    vong_phong_van      INTEGER NOT NULL CHECK (vong_phong_van >= 1),
    thoi_gian_bat_dau   TIMESTAMPTZ NOT NULL,
    thoi_gian_ket_thuc  TIMESTAMPTZ NOT NULL,
    dia_diem            VARCHAR(300),
    hinh_thuc           VARCHAR(50) DEFAULT 'TRUC_TIEP',     -- TRUC_TIEP / ONLINE / HYBRID
    link_online         VARCHAR(500),
    nguoi_phong_van_ids UUID[] NOT NULL DEFAULT '{}',         -- danh sach NVD
    nguoi_to_chuc_id    UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    trang_thai          VARCHAR(30) DEFAULT 'CHUA_DIEN_RA',   -- CHUA_DIEN_RA / DANG_DIEN_RA / HOAN_THANH / HUY
    ghi_chu             TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_lich_pv_thoigian CHECK (thoi_gian_ket_thuc > thoi_gian_bat_dau)
);

CREATE INDEX idx_lichpv_uv ON recruitment.lich_phong_van(ung_vien_id, vong_phong_van);
CREATE INDEX idx_lichpv_thoigian ON recruitment.lich_phong_van(thoi_gian_bat_dau);

-- Danh gia phong van
CREATE TABLE recruitment.danh_gia_ung_vien (
    danh_gia_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lich_pv_id          UUID NOT NULL REFERENCES recruitment.lich_phong_van(lich_pv_id),
    nguoi_danh_gia_id   UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    diem_ky_thuat       INTEGER CHECK (diem_ky_thuat BETWEEN 0 AND 10),
    diem_giao_tiep      INTEGER CHECK (diem_giao_tiep BETWEEN 0 AND 10),
    diem_thai_do        INTEGER CHECK (diem_thai_do BETWEEN 0 AND 10),
    diem_ket_qua        INTEGER CHECK (diem_ket_qua BETWEEN 0 AND 10),
    diem_trung_binh     NUMERIC(4,2) GENERATED ALWAYS AS (
        (COALESCE(diem_ky_thuat, 0) + COALESCE(diem_giao_tiep, 0)
         + COALESCE(diem_thai_do, 0) + COALESCE(diem_ket_qua, 0))
        / NULLIF(
            (CASE WHEN diem_ky_thuat IS NOT NULL THEN 1 ELSE 0 END) +
            (CASE WHEN diem_giao_tiep IS NOT NULL THEN 1 ELSE 0 END) +
            (CASE WHEN diem_thai_do IS NOT NULL THEN 1 ELSE 0 END) +
            (CASE WHEN diem_ket_qua IS NOT NULL THEN 1 ELSE 0 END),
          0)
    ) STORED,
    ket_qua             recruitment.ket_qua_phong_van NOT NULL,
    diem_manh           TEXT,
    diem_yeu            TEXT,
    nhan_xet            TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_danhgia_lichpv ON recruitment.danh_gia_ung_vien(lich_pv_id);
CREATE INDEX idx_danhgia_nguoi ON recruitment.danh_gia_ung_vien(nguoi_danh_gia_id);
CREATE UNIQUE INDEX uq_danhgia_uv_lan ON recruitment.danh_gia_ung_vien(lich_pv_id, nguoi_danh_gia_id);

-- Quyet dinh tuyen (offer)
CREATE TABLE recruitment.quyet_dinh_tuyen (
    quyet_dinh_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ung_vien_id         UUID NOT NULL REFERENCES recruitment.ung_vien(ung_vien_id),
    nguoi_quyet_dinh_id UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    loai_hop_dong       recruitment.loai_hop_dong_de_nghi NOT NULL,
    muc_luong_de_nghi   NUMERIC(14,2) NOT NULL CHECK (muc_luong_de_nghi > 0),
    ngay_vao_lam_de_nghi DATE NOT NULL,
    phong_ban_id        UUID NOT NULL REFERENCES hr.phong_ban(phong_ban_id),
    chuc_danh           VARCHAR(200),
    thoi_han_thu_viec_thang INTEGER,
    ghi_chu             TEXT,
    trang_thai          VARCHAR(30) NOT NULL DEFAULT 'CHO_PHAN_HOI', -- CHO_PHAN_HOI / DA_DONG_Y / TU_CHOI / HET_HAN
    ngay_ung_vien_phan_hoi DATE,
    nhan_vien_moi_id    UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_qdt_uv ON recruitment.quyet_dinh_tuyen(ung_vien_id);
CREATE INDEX idx_qdt_trangthai ON recruitment.quyet_dinh_tuyen(trang_thai);

-- Triggers
CREATE TRIGGER trg_yctd_updated_at BEFORE UPDATE ON recruitment.yeu_cau_tuyen_dung
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_uv_updated_at BEFORE UPDATE ON recruitment.ung_vien
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_lichpv_updated_at BEFORE UPDATE ON recruitment.lich_phong_van
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_qdt_updated_at BEFORE UPDATE ON recruitment.quyet_dinh_tuyen
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();

COMMENT ON SCHEMA recruitment IS 'T17 - Module tuyen dung (Recruitment)';