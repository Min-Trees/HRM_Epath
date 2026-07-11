-- =====================================================================
-- HỆ THỐNG QUẢN LÝ NHÂN SỰ - CHẤM CÔNG - BHXH - TÍNH LƯƠNG
-- PostgreSQL DDL Schema
-- =====================================================================
-- Nguyên tắc thiết kế:
--   1. NHAN_VIEN là hub trung tâm — mọi bảng nghiệp vụ FK tới nhan_vien_id
--   2. Biến động (BIEN_DONG_NHAN_SU, BHXH_BIEN_DONG) là APPEND-ONLY,
--      không UPDATE/DELETE, trạng thái nhân viên là trường phái sinh
--   3. Bảng công tháng khi đã "chốt" (khóa) là bất biến (immutable)
--   4. Mọi bảng nghiệp vụ có audit columns (created_at, updated_at, created_by...)
--   5. Dùng schema riêng theo từng module để rõ ràng ranh giới bounded-context
-- =====================================================================

CREATE SCHEMA IF NOT EXISTS hr;         -- Core HR
CREATE SCHEMA IF NOT EXISTS timekeeping; -- Chấm công
CREATE SCHEMA IF NOT EXISTS social_ins;  -- BHXH
CREATE SCHEMA IF NOT EXISTS payroll;     -- Tính lương

-- Dùng cho sinh UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================================
-- ENUM TYPES (dùng chung)
-- =====================================================================

CREATE TYPE hr.gioi_tinh AS ENUM ('NAM', 'NU', 'KHAC');

CREATE TYPE hr.trang_thai_nv AS ENUM (
    'UNG_VIEN',        -- Trúng tuyển, chưa nhận việc
    'THU_VIEC',
    'CHINH_THUC',
    'TAM_HOAN_HDLD',   -- Thai sản / ốm dài / nghỉ không lương dài hạn
    'DA_NGHI_VIEC',
    'DA_NGHI_HUU',
    'LUU_TRU'          -- Archive, giữ liên kết lịch sử
);

CREATE TYPE hr.loai_hop_dong AS ENUM (
    'THU_VIEC',
    'XAC_DINH_THOI_HAN',
    'KHONG_XAC_DINH_THOI_HAN',
    'PHU_LUC'
);

CREATE TYPE hr.trang_thai_hop_dong AS ENUM (
    'HIEU_LUC', 'HET_HIEU_LUC', 'DA_THANH_LY', 'HUY'
);

CREATE TYPE hr.loai_bien_dong AS ENUM (
    'TUYEN_DUNG', 'BO_NHIEM', 'DIEU_CHUYEN', 'THANG_CHUC',
    'DIEU_CHINH_LUONG', 'KY_LUAT', 'TAM_HOAN_HDLD', 'CHAM_DUT_HDLD', 'NGHI_HUU'
);

CREATE TYPE timekeeping.loai_ca AS ENUM ('HANH_CHINH', 'CA_KIP', 'FLEXIBLE');

CREATE TYPE timekeeping.nguon_cham_cong AS ENUM (
    'VAN_TAY', 'KHUON_MAT', 'GPS_MOBILE', 'THU_CONG'
);

CREATE TYPE timekeeping.loai_ngoai_le AS ENUM (
    'DI_TRE', 'VE_SOM', 'THIEU_CONG', 'LAM_NGOAI_CA', 'KHONG_NGOAI_LE'
);

CREATE TYPE timekeeping.loai_nghi_phep AS ENUM (
    'PHEP_NAM', 'OM', 'VIEC_RIENG_CO_LUONG', 'VIEC_RIENG_KHONG_LUONG', 'THAI_SAN', 'KHAC'
);

CREATE TYPE timekeeping.trang_thai_don AS ENUM (
    'CHO_DUYET', 'DUYET_CAP_1', 'DA_DUYET', 'TU_CHOI', 'HUY'
);

CREATE TYPE timekeeping.he_so_ot AS ENUM ('NGAY_THUONG_150', 'NGAY_NGHI_TUAN_200', 'NGAY_LE_300');

CREATE TYPE timekeeping.trang_thai_bang_cong AS ENUM ('MO', 'DA_CHOT', 'DA_HUY_CHOT');

CREATE TYPE social_ins.loai_bao_bien_dong AS ENUM ('BAO_TANG', 'BAO_GIAM', 'DIEU_CHINH_MUC_DONG');

CREATE TYPE social_ins.ly_do_bien_dong AS ENUM (
    'TUYEN_MOI', 'HET_THU_VIEC', 'DIEU_CHINH_LUONG', 'NGHI_VIEC',
    'NGHI_KHONG_LUONG_14_NGAY', 'THAI_SAN', 'OM_DAI_NGAY', 'KHAC'
);

CREATE TYPE social_ins.loai_che_do AS ENUM (
    'OM_DAU', 'THAI_SAN', 'TAI_NAN_LAO_DONG_BENH_NGHE_NGHIEP', 'HUU_TRI', 'TU_TUAT'
);

CREATE TYPE social_ins.trang_thai_ho_so AS ENUM (
    'MOI_TAO', 'DA_NOP_CO_QUAN_BHXH', 'DA_DUOC_CHI_TRA', 'TU_CHOI'
);

CREATE TYPE payroll.trang_thai_bang_luong AS ENUM (
    'DA_TINH', 'DA_DUYET', 'DA_CHI_TRA', 'HUY'
);

-- =====================================================================
-- MODULE 1: CORE HR (schema hr)
-- =====================================================================

CREATE TABLE hr.phong_ban (
    phong_ban_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_phong_ban         VARCHAR(20) NOT NULL UNIQUE,
    ten_phong_ban        VARCHAR(200) NOT NULL,
    phong_ban_cha_id     UUID REFERENCES hr.phong_ban(phong_ban_id),
    truong_bo_phan_id    UUID,                       -- FK tới nhan_vien, thêm constraint sau khi tạo bảng nhan_vien
    dinh_bien            INTEGER NOT NULL DEFAULT 0 CHECK (dinh_bien >= 0),
    cap_do               INTEGER NOT NULL DEFAULT 1, -- cấp trong cây tổ chức
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_phong_ban_cha ON hr.phong_ban(phong_ban_cha_id);

CREATE TABLE hr.ngach_bac_luong (
    ngach_bac_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_ngach             VARCHAR(20) NOT NULL UNIQUE,
    ten_chuc_danh        VARCHAR(200) NOT NULL,
    bac_luong            INTEGER NOT NULL DEFAULT 1,
    he_so_luong          NUMERIC(6,2) NOT NULL,
    luong_co_ban_toi_thieu NUMERIC(14,2),
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE hr.nhan_vien (
    nhan_vien_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_nv                VARCHAR(20) NOT NULL UNIQUE,
    ho_ten               VARCHAR(200) NOT NULL,
    ngay_sinh            DATE,
    gioi_tinh            hr.gioi_tinh,
    so_cccd              VARCHAR(20) UNIQUE,
    ngay_cap_cccd        DATE,
    noi_cap_cccd         VARCHAR(200),
    que_quan             VARCHAR(300),
    dia_chi_thuong_tru   VARCHAR(300),
    dia_chi_lien_lac     VARCHAR(300),
    so_dien_thoai        VARCHAR(20),
    email                VARCHAR(200),
    trinh_do_hoc_van     VARCHAR(200),
    ngay_vao_lam         DATE NOT NULL,
    phong_ban_id         UUID NOT NULL REFERENCES hr.phong_ban(phong_ban_id),
    ngach_bac_id         UUID REFERENCES hr.ngach_bac_luong(ngach_bac_id),
    quan_ly_truc_tiep_id UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    -- trang_thai là trường PHÁI SINH, được cập nhật bởi trigger/service
    -- dựa trên bản ghi bien_dong_nhan_su mới nhất — KHÔNG sửa tay trực tiếp
    trang_thai           hr.trang_thai_nv NOT NULL DEFAULT 'UNG_VIEN',
    tai_khoan_cham_cong_id VARCHAR(50),  -- id đồng bộ sang máy chấm công / app
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_nv_phong_ban ON hr.nhan_vien(phong_ban_id);
CREATE INDEX idx_nv_trang_thai ON hr.nhan_vien(trang_thai);
CREATE INDEX idx_nv_quan_ly ON hr.nhan_vien(quan_ly_truc_tiep_id);

ALTER TABLE hr.phong_ban
    ADD CONSTRAINT fk_phong_ban_truong_bp
    FOREIGN KEY (truong_bo_phan_id) REFERENCES hr.nhan_vien(nhan_vien_id);

CREATE TABLE hr.nguoi_phu_thuoc (
    nguoi_phu_thuoc_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    ho_ten               VARCHAR(200) NOT NULL,
    ngay_sinh            DATE,
    quan_he              VARCHAR(50) NOT NULL,     -- con, vợ/chồng, cha mẹ...
    so_cccd_hoac_khai_sinh VARCHAR(30),
    ma_so_thue_phu_thuoc VARCHAR(20),
    tu_ngay_giam_tru     DATE NOT NULL,
    den_ngay_giam_tru    DATE,
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_npt_nv ON hr.nguoi_phu_thuoc(nhan_vien_id);

CREATE TABLE hr.qua_trinh_cong_tac (
    qua_trinh_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    don_vi               VARCHAR(300) NOT NULL,
    chuc_danh            VARCHAR(200),
    tu_ngay              DATE NOT NULL,
    den_ngay             DATE,
    mo_ta                TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE hr.hop_dong_lao_dong (
    hop_dong_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    so_hop_dong          VARCHAR(50) NOT NULL UNIQUE,
    loai_hop_dong        hr.loai_hop_dong NOT NULL,
    hop_dong_goc_id       UUID REFERENCES hr.hop_dong_lao_dong(hop_dong_id), -- nếu là phụ lục
    ngay_ky              DATE NOT NULL,
    ngay_hieu_luc        DATE NOT NULL,
    ngay_het_hieu_luc    DATE,                      -- NULL nếu không xác định thời hạn
    muc_luong_thoa_thuan NUMERIC(14,2) NOT NULL,
    phu_cap_co_dinh      JSONB DEFAULT '{}'::jsonb, -- {"chuc_vu":..,"an_trua":..,"xang_xe":..,"dien_thoai":..}
    trang_thai           hr.trang_thai_hop_dong NOT NULL DEFAULT 'HIEU_LUC',
    file_dinh_kem_url    VARCHAR(500),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_ngay_hd CHECK (ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc >= ngay_hieu_luc)
);

CREATE INDEX idx_hd_nv ON hr.hop_dong_lao_dong(nhan_vien_id);
CREATE INDEX idx_hd_het_hieu_luc ON hr.hop_dong_lao_dong(ngay_het_hieu_luc)
    WHERE trang_thai = 'HIEU_LUC';

-- Biến động nhân sự: APPEND-ONLY — không UPDATE/DELETE ở tầng ứng dụng
CREATE TABLE hr.bien_dong_nhan_su (
    bien_dong_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    loai_bien_dong       hr.loai_bien_dong NOT NULL,
    so_quyet_dinh        VARCHAR(50) NOT NULL,
    ngay_quyet_dinh      DATE NOT NULL,
    ngay_hieu_luc        DATE NOT NULL,
    phong_ban_truoc_id   UUID REFERENCES hr.phong_ban(phong_ban_id),
    phong_ban_sau_id     UUID REFERENCES hr.phong_ban(phong_ban_id),
    chuc_danh_truoc      VARCHAR(200),
    chuc_danh_sau        VARCHAR(200),
    luong_truoc          NUMERIC(14,2),
    luong_sau            NUMERIC(14,2),
    trang_thai_nv_sau    hr.trang_thai_nv NOT NULL, -- trạng thái NV sẽ chuyển thành sau biến động này
    ly_do                TEXT,
    file_quyet_dinh_url  VARCHAR(500),
    created_by           UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_bdns_nv ON hr.bien_dong_nhan_su(nhan_vien_id, ngay_hieu_luc DESC);

-- =====================================================================
-- MODULE 2: CHẤM CÔNG (schema timekeeping)
-- =====================================================================

CREATE TABLE timekeeping.ca_lam_viec (
    ca_id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ma_ca                VARCHAR(20) NOT NULL UNIQUE,
    ten_ca               VARCHAR(100) NOT NULL,
    loai_ca              timekeeping.loai_ca NOT NULL,
    gio_bat_dau          TIME NOT NULL,
    gio_ket_thuc         TIME NOT NULL,
    so_gio_chuan         NUMERIC(4,2) NOT NULL DEFAULT 8,
    qua_ngay             BOOLEAN NOT NULL DEFAULT FALSE, -- ca đêm kết thúc ngày sau
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE timekeeping.phan_ca (
    phan_ca_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    ca_id                UUID NOT NULL REFERENCES timekeeping.ca_lam_viec(ca_id),
    ngay_ap_dung         DATE NOT NULL,
    ghi_chu              VARCHAR(300),
    created_by           UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_phan_ca UNIQUE (nhan_vien_id, ngay_ap_dung)
);

CREATE INDEX idx_phanca_nv_ngay ON timekeeping.phan_ca(nhan_vien_id, ngay_ap_dung);

CREATE TABLE timekeeping.cham_cong_chi_tiet (
    cham_cong_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    phan_ca_id           UUID REFERENCES timekeeping.phan_ca(phan_ca_id),
    ngay_cham_cong       DATE NOT NULL,
    gio_vao              TIMESTAMPTZ,
    gio_ra               TIMESTAMPTZ,
    nguon                timekeeping.nguon_cham_cong NOT NULL,
    vi_tri_gps           POINT,                     -- dùng cho GPS mobile
    loai_ngoai_le        timekeeping.loai_ngoai_le NOT NULL DEFAULT 'KHONG_NGOAI_LE',
    so_gio_cong          NUMERIC(4,2),               -- tổng giờ công thực tế trong ngày
    can_giai_trinh       BOOLEAN NOT NULL DEFAULT FALSE,
    giai_trinh_noi_dung  TEXT,
    giai_trinh_trang_thai timekeeping.trang_thai_don,
    duyet_boi            UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    duyet_luc            TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_ccct_nv_ngay ON timekeeping.cham_cong_chi_tiet(nhan_vien_id, ngay_cham_cong);
CREATE UNIQUE INDEX uq_ccct_nv_ngay ON timekeeping.cham_cong_chi_tiet(nhan_vien_id, ngay_cham_cong);

CREATE TABLE timekeeping.dang_ky_ot (
    ot_id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    ngay_lam_ot          DATE NOT NULL,
    gio_bat_dau          TIMESTAMPTZ NOT NULL,
    gio_ket_thuc         TIMESTAMPTZ NOT NULL,
    so_gio_ot            NUMERIC(4,2) NOT NULL CHECK (so_gio_ot > 0),
    he_so_ot             timekeeping.he_so_ot NOT NULL,
    lam_dem              BOOLEAN NOT NULL DEFAULT FALSE, -- +20-30% nếu true
    ly_do                VARCHAR(500),
    trang_thai           timekeeping.trang_thai_don NOT NULL DEFAULT 'CHO_DUYET',
    duyet_cap_1_boi      UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    duyet_cap_1_luc      TIMESTAMPTZ,
    duyet_cap_2_boi      UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    duyet_cap_2_luc      TIMESTAMPTZ,
    ghi_chu_duyet        VARCHAR(500),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_ot_nv_ngay ON timekeeping.dang_ky_ot(nhan_vien_id, ngay_lam_ot);

CREATE TABLE timekeeping.quy_phep_nam (
    quy_phep_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    nam                  INTEGER NOT NULL,
    so_ngay_duoc_huong   NUMERIC(4,1) NOT NULL,      -- 12 + 1/5 năm thâm niên
    so_ngay_da_dung      NUMERIC(4,1) NOT NULL DEFAULT 0,
    so_ngay_con_lai      NUMERIC(4,1) GENERATED ALWAYS AS (so_ngay_duoc_huong - so_ngay_da_dung) STORED,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_quyphep_nv_nam UNIQUE (nhan_vien_id, nam)
);

CREATE TABLE timekeeping.nghi_phep (
    nghi_phep_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    loai_nghi_phep       timekeeping.loai_nghi_phep NOT NULL,
    tu_ngay              DATE NOT NULL,
    den_ngay             DATE NOT NULL,
    so_ngay_nghi         NUMERIC(4,1) NOT NULL CHECK (so_ngay_nghi > 0),
    ly_do                VARCHAR(500),
    file_dinh_kem_url    VARCHAR(500),               -- chứng từ y tế nếu nghỉ ốm/thai sản
    trang_thai           timekeeping.trang_thai_don NOT NULL DEFAULT 'CHO_DUYET',
    duyet_cap_1_boi      UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    duyet_cap_1_luc      TIMESTAMPTZ,
    duyet_cap_2_boi      UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    duyet_cap_2_luc      TIMESTAMPTZ,
    ghi_chu_duyet        VARCHAR(500),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_nghiphep_ngay CHECK (den_ngay >= tu_ngay)
);

CREATE INDEX idx_nghiphep_nv ON timekeeping.nghi_phep(nhan_vien_id, tu_ngay);

-- Bảng công tháng: khi trang_thai = 'DA_CHOT' thì BẤT BIẾN (immutable),
-- việc chỉnh sửa phải "hủy chốt" (DA_HUY_CHOT) rồi tạo lại — có audit riêng
CREATE TABLE timekeeping.bang_cong_thang (
    bang_cong_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    thang                INTEGER NOT NULL CHECK (thang BETWEEN 1 AND 12),
    nam                  INTEGER NOT NULL,
    so_ngay_cong_thuc_te NUMERIC(4,1) NOT NULL DEFAULT 0,
    so_gio_ot_150        NUMERIC(6,2) NOT NULL DEFAULT 0,
    so_gio_ot_200        NUMERIC(6,2) NOT NULL DEFAULT 0,
    so_gio_ot_300        NUMERIC(6,2) NOT NULL DEFAULT 0,
    so_ngay_phep_nam     NUMERIC(4,1) NOT NULL DEFAULT 0,
    so_ngay_nghi_om      NUMERIC(4,1) NOT NULL DEFAULT 0,
    so_ngay_nghi_khong_luong NUMERIC(4,1) NOT NULL DEFAULT 0,
    so_ngay_thieu_cong   NUMERIC(4,1) NOT NULL DEFAULT 0,
    trang_thai           timekeeping.trang_thai_bang_cong NOT NULL DEFAULT 'MO',
    chot_boi             UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    chot_luc             TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_bangcong_nv_thang UNIQUE (nhan_vien_id, thang, nam)
);

CREATE INDEX idx_bangcong_thang_nam ON timekeeping.bang_cong_thang(nam, thang);
CREATE INDEX idx_bangcong_trangthai ON timekeeping.bang_cong_thang(trang_thai);

-- =====================================================================
-- MODULE 3: BHXH (schema social_ins)
-- =====================================================================

CREATE TABLE social_ins.qua_trinh_tham_gia (
    qua_trinh_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    ma_so_bhxh           VARCHAR(20) NOT NULL,
    don_vi_tham_gia      VARCHAR(300) NOT NULL,
    muc_luong_dong       NUMERIC(14,2) NOT NULL CHECK (muc_luong_dong > 0),
    tu_ngay              DATE NOT NULL,
    den_ngay             DATE,                       -- NULL = đang áp dụng
    ty_le_dong_dn        NUMERIC(5,2) NOT NULL DEFAULT 21.5,
    ty_le_dong_nld       NUMERIC(5,2) NOT NULL DEFAULT 10.5,
    tran_dong_bhxh_bhyt  NUMERIC(14,2),               -- 20x lương cơ sở tại thời điểm
    tran_dong_bhtn       NUMERIC(14,2),               -- 20x lương tối thiểu vùng tại thời điểm
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_qttg_ngay CHECK (den_ngay IS NULL OR den_ngay >= tu_ngay)
);

CREATE INDEX idx_qttg_nv ON social_ins.qua_trinh_tham_gia(nhan_vien_id, tu_ngay DESC);
-- Đảm bảo tại một thời điểm chỉ có tối đa 1 giai đoạn "đang áp dụng" (den_ngay IS NULL) cho mỗi NV
CREATE UNIQUE INDEX uq_qttg_dang_ap_dung ON social_ins.qua_trinh_tham_gia(nhan_vien_id)
    WHERE den_ngay IS NULL;

-- Báo tăng/giảm/điều chỉnh — APPEND-ONLY
CREATE TABLE social_ins.bhxh_bien_dong (
    bien_dong_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    loai_bao             social_ins.loai_bao_bien_dong NOT NULL,
    ly_do                social_ins.ly_do_bien_dong NOT NULL,
    ngay_phat_sinh       DATE NOT NULL,
    ngay_nop_ho_so       DATE,
    han_nop_ho_so        DATE,                        -- ngay_phat_sinh + 30 ngày (tính ở service)
    mau_to_khai          VARCHAR(20),                 -- D02-LT, TK1-TS...
    file_ho_so_url       VARCHAR(500),
    da_nop               BOOLEAN NOT NULL DEFAULT FALSE,
    ghi_chu              TEXT,
    created_by           UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_bhxhbd_nv ON social_ins.bhxh_bien_dong(nhan_vien_id, ngay_phat_sinh DESC);
CREATE INDEX idx_bhxhbd_han_nop ON social_ins.bhxh_bien_dong(han_nop_ho_so) WHERE da_nop = FALSE;

CREATE TABLE social_ins.che_do_huong (
    che_do_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    loai_che_do          social_ins.loai_che_do NOT NULL,
    tu_ngay              DATE NOT NULL,
    den_ngay             DATE,
    so_ngay_huong        NUMERIC(5,1),
    ho_so_y_te_url        VARCHAR(500),
    so_tien_de_nghi       NUMERIC(14,2),
    so_tien_duoc_chi_tra  NUMERIC(14,2),
    trang_thai            social_ins.trang_thai_ho_so NOT NULL DEFAULT 'MOI_TAO',
    ngay_nop_co_quan_bhxh DATE,
    ngay_nhan_ket_qua     DATE,
    ghi_chu               TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_chedo_nv ON social_ins.che_do_huong(nhan_vien_id, tu_ngay DESC);

CREATE TABLE social_ins.so_bhxh (
    so_bhxh_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id         UUID NOT NULL UNIQUE REFERENCES hr.nhan_vien(nhan_vien_id),
    ma_so_bhxh           VARCHAR(20) NOT NULL UNIQUE,
    ngay_cap             DATE,
    da_chot_so           BOOLEAN NOT NULL DEFAULT FALSE,
    ngay_chot_so         DATE,
    tong_thoi_gian_da_dong_thang INTEGER,             -- số tháng đã đóng, xác nhận khi chốt sổ
    ghi_chu              TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =====================================================================
-- MODULE 4: TÍNH LƯƠNG (schema payroll)
-- =====================================================================

CREATE TABLE payroll.bac_thue_tncn (
    bac_thue_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bac                  INTEGER NOT NULL UNIQUE,
    thu_nhap_tu          NUMERIC(14,2) NOT NULL,
    thu_nhap_den         NUMERIC(14,2),               -- NULL = không giới hạn (bậc cao nhất)
    thue_suat            NUMERIC(5,2) NOT NULL,       -- %
    hieu_luc_tu_ngay     DATE NOT NULL DEFAULT '2009-01-01'
);

CREATE TABLE payroll.tham_so_luong (
    tham_so_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ten_tham_so          VARCHAR(100) NOT NULL,       -- luong_co_so, luong_toi_thieu_vung_1...
    gia_tri               NUMERIC(14,2) NOT NULL,
    hieu_luc_tu_ngay      DATE NOT NULL,
    hieu_luc_den_ngay     DATE,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Bảng lương tháng: chỉ được tạo khi bang_cong_thang.trang_thai = 'DA_CHOT'
-- và social_ins.qua_trinh_tham_gia hợp lệ tại kỳ lương — ràng buộc ở tầng service
CREATE TABLE payroll.bang_luong_thang (
    bang_luong_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id          UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    bang_cong_id          UUID NOT NULL REFERENCES timekeeping.bang_cong_thang(bang_cong_id),
    thang                 INTEGER NOT NULL CHECK (thang BETWEEN 1 AND 12),
    nam                   INTEGER NOT NULL,
    luong_co_ban          NUMERIC(14,2) NOT NULL,
    phu_cap                NUMERIC(14,2) NOT NULL DEFAULT 0,
    tien_ot                NUMERIC(14,2) NOT NULL DEFAULT 0,
    tien_khau_tru_nghi_khong_luong NUMERIC(14,2) NOT NULL DEFAULT 0,
    luong_gop              NUMERIC(14,2) NOT NULL,
    muc_luong_dong_bhxh    NUMERIC(14,2) NOT NULL,
    bhxh_nld               NUMERIC(14,2) NOT NULL DEFAULT 0,  -- 10.5% NLĐ chịu, trừ vào lương
    bhxh_dn                NUMERIC(14,2) NOT NULL DEFAULT 0,  -- 21.5% DN chịu, chi phí DN (không trừ lương)
    thu_nhap_truoc_thue    NUMERIC(14,2) NOT NULL,
    giam_tru_ban_than       NUMERIC(14,2) NOT NULL DEFAULT 11000000,
    giam_tru_nguoi_phu_thuoc NUMERIC(14,2) NOT NULL DEFAULT 0,
    thu_nhap_tinh_thue       NUMERIC(14,2) NOT NULL DEFAULT 0,
    thue_tncn                NUMERIC(14,2) NOT NULL DEFAULT 0,
    tam_ung                  NUMERIC(14,2) NOT NULL DEFAULT 0,
    khau_tru_khac             NUMERIC(14,2) NOT NULL DEFAULT 0,
    thuc_linh                 NUMERIC(14,2) NOT NULL,
    trang_thai                 payroll.trang_thai_bang_luong NOT NULL DEFAULT 'DA_TINH',
    duyet_boi                  UUID REFERENCES hr.nhan_vien(nhan_vien_id),
    duyet_luc                  TIMESTAMPTZ,
    chi_tra_luc                TIMESTAMPTZ,
    so_tai_khoan_nhan          VARCHAR(30),
    phieu_luong_url             VARCHAR(500),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_luong_nv_thang UNIQUE (nhan_vien_id, thang, nam)
);

CREATE INDEX idx_luong_thang_nam ON payroll.bang_luong_thang(nam, thang);
CREATE INDEX idx_luong_trangthai ON payroll.bang_luong_thang(trang_thai);

CREATE TABLE payroll.chi_tiet_khoan_khau_tru (
    khoan_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bang_luong_id          UUID NOT NULL REFERENCES payroll.bang_luong_thang(bang_luong_id),
    loai_khoan             VARCHAR(50) NOT NULL,     -- TAM_UNG, PHAT, BOI_THUONG...
    so_tien                 NUMERIC(14,2) NOT NULL,
    ghi_chu                 VARCHAR(300),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_khoan_khautru_luong ON payroll.chi_tiet_khoan_khau_tru(bang_luong_id);

-- =====================================================================
-- TRIGGERS: auto-update updated_at
-- =====================================================================

CREATE OR REPLACE FUNCTION common_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_nv_updated_at BEFORE UPDATE ON hr.nhan_vien
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_hd_updated_at BEFORE UPDATE ON hr.hop_dong_lao_dong
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_ccct_updated_at BEFORE UPDATE ON timekeeping.cham_cong_chi_tiet
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_ot_updated_at BEFORE UPDATE ON timekeeping.dang_ky_ot
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_nghiphep_updated_at BEFORE UPDATE ON timekeeping.nghi_phep
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_bangcong_updated_at BEFORE UPDATE ON timekeeping.bang_cong_thang
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_qttg_updated_at BEFORE UPDATE ON social_ins.qua_trinh_tham_gia
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_chedo_updated_at BEFORE UPDATE ON social_ins.che_do_huong
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_sobhxh_updated_at BEFORE UPDATE ON social_ins.so_bhxh
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();
CREATE TRIGGER trg_luong_updated_at BEFORE UPDATE ON payroll.bang_luong_thang
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();

-- =====================================================================
-- TRIGGER: ngăn sửa/xóa bảng công đã chốt (immutability)
-- =====================================================================

CREATE OR REPLACE FUNCTION timekeeping.prevent_edit_locked_bangcong()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.trang_thai = 'DA_CHOT' AND NEW.trang_thai = 'DA_CHOT' THEN
        RAISE EXCEPTION 'Bảng công tháng % / % của nhân viên % đã chốt, không thể sửa. Phải hủy chốt trước.',
            OLD.thang, OLD.nam, OLD.nhan_vien_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bangcong_immutable BEFORE UPDATE ON timekeeping.bang_cong_thang
    FOR EACH ROW EXECUTE FUNCTION timekeeping.prevent_edit_locked_bangcong();

-- =====================================================================
-- VIEW hỗ trợ: hợp đồng sắp hết hạn (30-45 ngày)
-- =====================================================================

CREATE OR REPLACE VIEW hr.v_hop_dong_sap_het_han AS
SELECT hd.*, nv.ho_ten, nv.ma_nv, nv.phong_ban_id
FROM hr.hop_dong_lao_dong hd
JOIN hr.nhan_vien nv ON nv.nhan_vien_id = hd.nhan_vien_id
WHERE hd.trang_thai = 'HIEU_LUC'
  AND hd.ngay_het_hieu_luc IS NOT NULL
  AND hd.ngay_het_hieu_luc BETWEEN CURRENT_DATE + INTERVAL '30 days'
                                AND CURRENT_DATE + INTERVAL '45 days';

-- =====================================================================
-- VIEW hỗ trợ: hồ sơ BHXH quá hạn nộp
-- =====================================================================

CREATE OR REPLACE VIEW social_ins.v_bien_dong_qua_han AS
SELECT bd.*, nv.ho_ten, nv.ma_nv
FROM social_ins.bhxh_bien_dong bd
JOIN hr.nhan_vien nv ON nv.nhan_vien_id = bd.nhan_vien_id
WHERE bd.da_nop = FALSE
  AND bd.han_nop_ho_so IS NOT NULL
  AND bd.han_nop_ho_so < CURRENT_DATE;

COMMENT ON SCHEMA hr IS 'Module Quản lý nhân sự (Core HR)';
COMMENT ON SCHEMA timekeeping IS 'Module Chấm công';
COMMENT ON SCHEMA social_ins IS 'Module Bảo hiểm xã hội (BHXH)';
COMMENT ON SCHEMA payroll IS 'Module Tính lương';
