-- =====================================================================
-- T16: Quyet toan thue TNCN cuoi nam (mau 02/QTT, 05/QTT)
--
-- Theo Thong tu 92/2015/TT-BTC va 111/2013/TT-BTC (sua doi boi 25/2018).
-- Cam ket 08: NV uy quyen DN quyet toan thay (hoac khong).
-- Mau 02/QTT: tong hop toan DN.
-- Mau 05/QTT: chi tiet tung NV.
-- =====================================================================

CREATE TYPE payroll.loai_cam_ket_08 AS ENUM (
    'UY_QUYEN_QTT',     -- NV uy quyen cho DN quyet toan thay (mau 02/QTT)
    'NV_TU_QTT',        -- NV tu quyet toan voi co quan thue
    'CHUA_CO'           -- NV chua dang ky (mac dinh truoc 31/3 nam sau)
);

-- Bang luu cac cam ket 08 dang ky voi nguoi phu thuoc (Appendix)
CREATE TABLE payroll.cam_ket_08 (
    cam_ket_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nhan_vien_id    UUID NOT NULL REFERENCES hr.nhan_vien(nhan_vien_id),
    nam             INTEGER NOT NULL CHECK (nam BETWEEN 2000 AND 2100),
    loai_cam_ket    payroll.loai_cam_ket_08 NOT NULL,
    -- Chi tiet nguoi phu thuoc ma NV dang ky (neu co)
    ngay_dang_ky    DATE NOT NULL,
    hieu_luc_tu_ngay DATE NOT NULL,
    hieu_luc_den_ngay DATE,
    -- Quyet dinh dang ky: NV uy quyen cho DN quyet toan thay
    uy_quyen_qtt    BOOLEAN NOT NULL DEFAULT FALSE,
    ghi_chu         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_camket_nv_nam UNIQUE (nhan_vien_id, nam)
);

CREATE INDEX idx_camket_nv ON payroll.cam_ket_08(nhan_vien_id, nam DESC);

CREATE TRIGGER trg_camket_updated_at BEFORE UPDATE ON payroll.cam_ket_08
    FOR EACH ROW EXECUTE FUNCTION common_set_updated_at();

COMMENT ON TABLE payroll.cam_ket_08 IS 'T16 - Cam ket 08 giua NV va DN ve quyet toan thue TNCN cuoi nam';

-- Them cot ma_so_thue cho nhan_vien (QTT thue can theo doi MST ca nhan)
ALTER TABLE hr.nhan_vien ADD COLUMN IF NOT EXISTS ma_so_thue VARCHAR(20);