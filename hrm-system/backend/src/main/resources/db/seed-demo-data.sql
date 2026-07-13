-- =====================================================================
-- SEED DATA - Dựa trên schema thực tế của DB
-- =====================================================================

-- Phong ban
INSERT INTO hr.phong_ban (ma_phong_ban, ten_phong_ban, phong_ban_cha_id, dinh_bien, cap_do) VALUES
    ('PGD', 'Ban Giam doc', NULL, 3, 1),
    ('PNS', 'Phong Nhan su', NULL, 5, 1),
    ('PKT', 'Phong Ky thuat', NULL, 8, 1),
    ('PIT', 'Phong IT', NULL, 4, 1),
    ('PKD', 'Phong Kinh doanh', NULL, 10, 1)
ON CONFLICT (ma_phong_ban) DO NOTHING;

-- Nhan vien (5 NV + 1 NV cu)
INSERT INTO hr.nhan_vien (ma_nv, ho_ten, so_cccd, ngay_cap_cccd, noi_cap_cccd, ngay_sinh, gioi_tinh,
    dia_chi_thuong_tru, dia_chi_lien_lac, so_dien_thoai, email,
    phong_ban_id, ngay_vao_lam, trang_thai, company_id, ma_so_thue)
VALUES
    ('NV001', 'Nguyen Van A', '079201001234', '2020-01-15', 'TP.HCM', '1988-03-15', 'NAM',
     '123 Nguyen Trai, Q1, TP.HCM', '123 Nguyen Trai, Q1, TP.HCM',
     '0901123456', 'a.nguyen@company.vn',
     (SELECT phong_ban_id FROM hr.phong_ban WHERE ma_phong_ban='PNS' LIMIT 1),
     '2020-01-01', 'CHINH_THUC',
     '11111111-1111-1111-1111-111111111111', '1234567890'),
    ('NV002', 'Tran Thi B', '079202003456', '2019-06-20', 'Ha Noi', '1992-07-22', 'NU',
     '456 Le loi, Q3, TP.HCM', '456 Le loi, Q3, TP.HCM',
     '0902123456', 'b.tran@company.vn',
     (SELECT phong_ban_id FROM hr.phong_ban WHERE ma_phong_ban='PIT' LIMIT 1),
     '2021-03-15', 'CHINH_THUC',
     '11111111-1111-1111-1111-111111111111', '2345678901'),
    ('NV003', 'Le Van C', '079203005678', '2018-09-10', 'TP.HCM', '1990-11-30', 'NAM',
     '789 Truong Chinh, Q.Tan Binh, TP.HCM', '789 Truong Chinh, Q.Tan Binh, TP.HCM',
     '0903123456', 'c.le@company.vn',
     (SELECT phong_ban_id FROM hr.phong_ban WHERE ma_phong_ban='PKT' LIMIT 1),
     '2019-07-01', 'CHINH_THUC',
     '11111111-1111-1111-1111-111111111111', '3456789012'),
    ('NV004', 'Pham Thi D', '079204007890', '2021-02-28', 'TP.HCM', '1995-05-18', 'NU',
     '321 Vo Van Tien, Q.10, TP.HCM', '321 Vo Van Tien, Q.10, TP.HCM',
     '0904123456', 'd.pham@company.vn',
     (SELECT phong_ban_id FROM hr.phong_ban WHERE ma_phong_ban='PKD' LIMIT 1),
     '2022-01-10', 'CHINH_THUC',
     '11111111-1111-1111-1111-111111111111', '4567890123'),
    ('NV005', 'Hoang Van E', '079205009012', '2022-04-15', 'TP.HCM', '1985-12-25', 'NAM',
     '654 Dien Bien Phu, Q.Binh Thanh, TP.HCM', '654 Dien Bien Phu, Q.Binh Thanh, TP.HCM',
     '0905123456', 'e.hoang@company.vn',
     (SELECT phong_ban_id FROM hr.phong_ban WHERE ma_phong_ban='PGD' LIMIT 1),
     '2020-06-01', 'CHINH_THUC',
     '11111111-1111-1111-1111-111111111111', '5678901234')
ON CONFLICT (ma_nv) DO NOTHING;

-- Users
INSERT INTO system.user_account (company_id, employee_id, username, password_hash, email, role_codes, trang_thai)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1),
     'a.nguyen', '$2a$10$N9qo8uLOickgx2ZMRZoMye.PvZRRK1d5u5u5u5u5u5u5u5u5u5',
     'a.nguyen@company.vn', 'HR,HR_MANAGER', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;
INSERT INTO system.user_account (company_id, employee_id, username, password_hash, email, role_codes, trang_thai)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV002' LIMIT 1),
     'b.tran', '$2a$10$N9qo8uLOickgx2ZMRZoMye.PvZRRK1d5u5u5u5u5u5u5u5u5u5',
     'b.tran@company.vn', 'EMPLOYEE', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;
INSERT INTO system.user_account (company_id, employee_id, username, password_hash, email, role_codes, trang_thai)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV003' LIMIT 1),
     'c.le', '$2a$10$N9qo8uLOickgx2ZMRZoMye.PvZRRK1d5u5u5u5u5u5u5u5u5u5',
     'c.le@company.vn', 'MANAGER', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;
INSERT INTO system.user_account (company_id, employee_id, username, password_hash, email, role_codes, trang_thai)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV004' LIMIT 1),
     'd.pham', '$2a$10$N9qo8uLOickgx2ZMRZoMye.PvZRRK1d5u5u5u5u5u5u5u5u5u5',
     'd.pham@company.vn', 'ACCOUNTANT,PAYROLL_ACCOUNTANT', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;
INSERT INTO system.user_account (company_id, employee_id, username, password_hash, email, role_codes, trang_thai)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV005' LIMIT 1),
     'e.hoang', '$2a$10$N9qo8uLOickgx2ZMRZoMye.PvZRRK1d5u5u5u5u5u5u5u5u5u5',
     'e.hoang@company.vn', 'COMPANY_ADMIN,HR_MANAGER', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Bang cong thang 5/2026
INSERT INTO timekeeping.bang_cong_thang (nhan_vien_id, thang, nam, so_ngay_cong_thuc_te,
    so_gio_ot_150, so_gio_ot_200, so_gio_ot_300, so_ngay_phep_nam,
    so_ngay_nghi_om, so_ngay_nghi_khong_luong, so_ngay_thieu_cong, trang_thai, company_id)
SELECT nhan_vien_id, 5, 2026,
    22, 1, 0, 0, 1, 0, 0, 0,
    CASE WHEN ma_nv = 'NV001' THEN 'DA_CHOT'::timekeeping.trang_thai_bang_cong ELSE 'DA_CHOT'::timekeeping.trang_thai_bang_cong END,
    '11111111-1111-1111-1111-111111111111'
FROM hr.nhan_vien WHERE ma_nv IN ('NV001','NV002','NV003','NV004','NV005')
ON CONFLICT (nhan_vien_id, thang, nam) DO NOTHING;

-- Bang luong thang 5/2026
INSERT INTO payroll.bang_luong_thang (nhan_vien_id, bang_cong_id, thang, nam,
    luong_co_ban, phu_cap, tien_ot, luong_gop, muc_luong_dong_bhxh,
    bhxh_nld, bhxh_dn, thu_nhap_truoc_thue,
    giam_tru_ban_than, giam_tru_nguoi_phu_thuoc, thu_nhap_tinh_thue, thue_tncn,
    tam_ung, khau_tru_khac, thuc_linh, trang_thai, company_id)
SELECT
    nv.nhan_vien_id,
    bc.bang_cong_id,
    5, 2026,
    CASE nv.ma_nv
        WHEN 'NV001' THEN 7020000
        WHEN 'NV002' THEN 6247800
        WHEN 'NV003' THEN 7792200
        WHEN 'NV004' THEN 5473800
        WHEN 'NV005' THEN 10530000
    END,
    CASE nv.ma_nv WHEN 'NV001' THEN 1500000 WHEN 'NV002' THEN 800000 WHEN 'NV003' THEN 2000000 WHEN 'NV004' THEN 500000 WHEN 'NV005' THEN 3000000 ELSE 0 END,
    CASE nv.ma_nv WHEN 'NV001' THEN 1500000 WHEN 'NV002' THEN 0 WHEN 'NV003' THEN 2000000 WHEN 'NV004' THEN 0 WHEN 'NV005' THEN 0 ELSE 0 END,
    CASE nv.ma_nv WHEN 'NV001' THEN 10020000 WHEN 'NV002' THEN 7047800 WHEN 'NV003' THEN 11792200 WHEN 'NV004' THEN 5973800 WHEN 'NV005' THEN 13530000 END,
    CASE nv.ma_nv WHEN 'NV001' THEN 7020000 WHEN 'NV002' THEN 6247800 WHEN 'NV003' THEN 7792200 WHEN 'NV004' THEN 5473800 WHEN 'NV005' THEN 10530000 END,
    CASE nv.ma_nv WHEN 'NV001' THEN 737100 WHEN 'NV002' THEN 656019 WHEN 'NV003' THEN 818181 WHEN 'NV004' THEN 574749 WHEN 'NV005' THEN 1105650 END,
    CASE nv.ma_nv WHEN 'NV001' THEN 1509300 WHEN 'NV002' THEN 1344300 WHEN 'NV003' THEN 1677300 WHEN 'NV004' THEN 1178100 WHEN 'NV005' THEN 2268900 END,
    CASE nv.ma_nv WHEN 'NV001' THEN 9273000 WHEN 'NV002' THEN 6391781 WHEN 'NV003' THEN 10974019 WHEN 'NV004' THEN 5399051 WHEN 'NV005' THEN 12424350 END,
    11000000, 0, 0, 0, 0, 0,
    CASE nv.ma_nv WHEN 'NV001' THEN 9273000 WHEN 'NV002' THEN 5391781 WHEN 'NV003' THEN 10474019 WHEN 'NV004' THEN 5399051 WHEN 'NV005' THEN 12424350 END,
    'DA_DUYET',
    '11111111-1111-1111-1111-111111111111'
FROM hr.nhan_vien nv
JOIN timekeeping.bang_cong_thang bc ON nv.nhan_vien_id = bc.nhan_vien_id AND bc.thang=5 AND bc.nam=2026
WHERE nv.ma_nv IN ('NV001','NV002','NV003','NV004','NV005')
ON CONFLICT (nhan_vien_id, thang, nam) DO NOTHING;

-- Ky linh luong
INSERT INTO payroll.ky_linh_luong (thang, nam, ma_ky_linh, ngay_chot_cong, ngay_chi_tra,
    trang_thai, loai_hinh_chi_tra, tong_nhan_vien, tong_thuc_linh, tong_bhxh_nld, tong_thue_tncn,
    nguoi_chay_id, ngay_chay, nguoi_duyet_cap_1_id, ngay_duyet_cap_1, nguoi_duyet_cap_2_id, ngay_duyet_cap_2)
VALUES
    (4, 2026, 'KL-00001', '2026-04-30', '2026-04-30', 'DA_CHI_TRA', 'BANK_TRANSFER',
     5, 42580151, 3889699, 0,
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1), '2026-04-25T10:00',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV005' LIMIT 1), '2026-04-26T09:00',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV005' LIMIT 1), '2026-04-27T11:00')
ON CONFLICT (thang, nam) DO NOTHING;

INSERT INTO payroll.ky_linh_luong (thang, nam, ma_ky_linh, trang_thai, loai_hinh_chi_tra, tong_nhan_vien, tong_thuc_linh)
VALUES (5, 2026, 'KL-00002', 'DA_DUYET_CAP_2', 'BANK_TRANSFER', 5, 32580151)
ON CONFLICT (thang, nam) DO NOTHING;

-- BHXH qua trinh tham gia
INSERT INTO social_ins.qua_trinh_tham_gia (nhan_vien_id, ma_so_bhxh, don_vi_tham_gia, muc_luong_dong,
    tu_ngay, den_ngay, ty_le_dong_dn, ty_le_dong_nld, tran_dong_bhxh_bhyt, tran_dong_bhtn,
    created_by, updated_by)
SELECT
    nv.nhan_vien_id,
    'BHXH-' || nv.ma_nv,
    'Cty ' || nv.ma_nv,
    CASE nv.ma_nv WHEN 'NV001' THEN 7020000 WHEN 'NV002' THEN 6247800 WHEN 'NV003' THEN 7792200 WHEN 'NV004' THEN 5473800 WHEN 'NV005' THEN 10530000 END,
    CASE nv.ma_nv WHEN 'NV001' THEN '2020-01-01'::date WHEN 'NV002' THEN '2021-03-15'::date WHEN 'NV003' THEN '2019-07-01'::date WHEN 'NV004' THEN '2022-01-10'::date WHEN 'NV005' THEN '2020-06-01'::date END,
    NULL,
    0.215, 0.105,
    CASE nv.ma_nv WHEN 'NV001' THEN 151430 WHEN 'NV002' THEN 134730 WHEN 'NV003' THEN 168090 WHEN 'NV004' THEN 118050 WHEN 'NV005' THEN 227280 END,
    0,
    (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1),
    (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1)
FROM hr.nhan_vien nv
WHERE nv.ma_nv IN ('NV001','NV002','NV003','NV004','NV005')
ON CONFLICT DO NOTHING;

-- Cam ket 08
INSERT INTO payroll.cam_ket_08 (nhan_vien_id, nam, loai_cam_ket, ngay_dang_ky, hieu_luc_tu_ngay, uy_quyen_qtt)
SELECT
    nv.nhan_vien_id, 2026, 'UY_QUYEN_QTT'::payroll.loai_cam_ket_08, '2026-01-15',
    CASE nv.ma_nv WHEN 'NV001' THEN '2026-01-01'::date WHEN 'NV002' THEN '2026-01-01'::date WHEN 'NV003' THEN '2026-01-01'::date WHEN 'NV004' THEN '2026-01-01'::date WHEN 'NV005' THEN '2026-01-01'::date END,
    (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV005' LIMIT 1)
FROM hr.nhan_vien nv
WHERE nv.ma_nv IN ('NV001','NV002','NV003','NV004','NV005')
ON CONFLICT DO NOTHING;

-- Recruitment: yeu cau tuyen dung
INSERT INTO recruitment.yeu_cau_tuyen_dung (ma_yeu_cau, tieu_de, phong_ban_id, nguoi_yeu_cau_id,
    so_luong_can, ly_do, muc_luong_de_xuat, trang_thai)
VALUES
    ('YC-IT-01', 'Tuyen nhan vien IT',
     (SELECT phong_ban_id FROM hr.phong_ban WHERE ma_phong_ban='PIT' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV005' LIMIT 1),
     1, 'Moi nhan vien IT them de dam bao hau tan', 12000000, 'MOI_TAO'::recruitment.trang_thai_yeu_cau),
    ('YC-KD-01', 'Tuyen 2 nhan vien kinh doanh',
     (SELECT phong_ban_id FROM hr.phong_ban WHERE ma_phong_ban='PKD' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV005' LIMIT 1),
     2, 'Mo rong team kinh doanh', 10000000, 'DANG_TUYEN'::recruitment.trang_thai_yeu_cau)
ON CONFLICT DO NOTHING;

-- KPI: chu ky Q2/2026
INSERT INTO performance.kpi_cycle (ten_chu_ky, loai_chu_ky, ngay_bat_dau, ngay_ket_thuc,
    trang_thai, nguoi_tao_id, mo_ta)
VALUES
    ('KPI Q2/2026', 'QUARTER', '2026-04-01', '2026-06-30', 'MOI_TAO'::performance.trang_thai_chu_ky,
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1),
     'Chu ky KPI quy 2 nam 2026 cho toan cong ty')
ON CONFLICT DO NOTHING;

-- KPI: assignments
INSERT INTO performance.kpi_assignment (cycle_id, nhan_vien_id, ten_muc_tieu, loai_muc_tieu,
    don_vi_do, target_value, trong_so, nguoi_gan_id, trang_thai)
SELECT
    (SELECT cycle_id FROM performance.kpi_cycle WHERE ten_chu_ky='KPI Q2/2026' LIMIT 1),
    nv.nhan_vien_id,
    'Muc tieu ' || nv.ma_nv || ' Q2/2026',
    'KPI',
    '%',
    100,
    1.0,
    (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV005' LIMIT 1),
    CASE nv.ma_nv WHEN 'NV001' THEN 'NV_DA_TU_DANH_GIA'::performance.trang_thai_assignment ELSE 'MOI_GAN'::performance.trang_thai_assignment END
FROM hr.nhan_vien nv
WHERE nv.ma_nv IN ('NV001','NV002','NV003','NV004')
ON CONFLICT DO NOTHING;

-- Training: chuong trinh
INSERT INTO training.chuong_trinh_dao_tao (ma_chuong_trinh, ten_chuong_trinh, loai_chuong_trinh,
    thoi_luong_gio, diem_danh_gia_toi_thieu, chung_chi, trang_thai, nguoi_tao_id)
VALUES
    ('CT-ATLD', 'An toan lao dong co ban', 'AN_TOAN_LAO_DONG', 8, 60, 'Chung chi ATLD', 'CONG_BO',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1)),
    ('CT-EXCEL', 'Excel nang cao cho nhan su', 'KY_NANG_CHUYEN_MON', 16, 70, NULL, 'CONG_BO',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1)),
    ('CT-LD', 'Ky nang lanh dao cap trung', 'QUAN_LY', 24, 75, NULL, 'NHAP',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1))
ON CONFLICT (ma_chuong_trinh) DO NOTHING;

-- Training: lop hoc
INSERT INTO training.lop_hoc (ma_lop, chuong_trinh_id, ten_lop, ngay_bat_dau, ngay_ket_thuc,
    so_buoi, so_cho_toi_da, dia_diem, giang_vien, chi_phi_moi_nv, trang_thai, nguoi_phu_trach_id)
VALUES
    ('L-ATLD-05',
     (SELECT chuong_trinh_id FROM training.chuong_trinh_dao_tao WHERE ma_chuong_trinh='CT-ATLD' LIMIT 1),
     'ATLD Khoi 05/2026', '2026-05-15', '2026-05-16', 2, 30, 'Phong 301', 'TS. Nguyen Van G', 500000, 'DANG_DIEN_RA',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1)),
    ('L-EXCEL-06',
     (SELECT chuong_trinh_id FROM training.chuong_trinh_dao_tao WHERE ma_chuong_trinh='CT-EXCEL' LIMIT 1),
     'Excel Nang cao 06/2026', '2026-06-01', '2026-06-05', 5, 20, 'Phong 202', 'ThS. Tran Thi H', 1500000, 'MO_DANG_KY',
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1))
ON CONFLICT (ma_lop) DO NOTHING;

-- Training: dang ky
INSERT INTO training.dang_ky_dao_tao (lop_hoc_id, nhan_vien_id, trang_thai, ly_do_dang_ky)
VALUES
    ((SELECT lop_hoc_id FROM training.lop_hoc WHERE ma_lop='L-ATLD-05' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1), 'DA_CHAP_NHAN', 'Bat buoc ATLD'),
    ((SELECT lop_hoc_id FROM training.lop_hoc WHERE ma_lop='L-ATLD-05' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV002' LIMIT 1), 'DA_CHAP_NHAN', 'Nang cao ATLD'),
    ((SELECT lop_hoc_id FROM training.lop_hoc WHERE ma_lop='L-ATLD-05' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV003' LIMIT 1), 'CHO_DUYET', 'Huong loi CNVC'),
    ((SELECT lop_hoc_id FROM training.lop_hoc WHERE ma_lop='L-ATLD-05' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV004' LIMIT 1), 'CHO_DUYET', 'Bo tro ATLD'),
    ((SELECT lop_hoc_id FROM training.lop_hoc WHERE ma_lop='L-EXCEL-06' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV001' LIMIT 1), 'DA_CHAP_NHAN', 'Nang cao Excel'),
    ((SELECT lop_hoc_id FROM training.lop_hoc WHERE ma_lop='L-EXCEL-06' LIMIT 1),
     (SELECT nhan_vien_id FROM hr.nhan_vien WHERE ma_nv='NV002' LIMIT 1), 'CHO_DUYET', 'Ky nang phu hop CV')
ON CONFLICT (lop_hoc_id, nhan_vien_id) DO NOTHING;

-- Training: diem danh buoi 1
INSERT INTO training.diem_danh_dao_tao (dang_ky_id, buoi_so, ngay_hoc, trang_thai)
SELECT dk.dang_ky_id, 1, '2026-05-15', 'CO_MAT'
FROM training.dang_ky_dao_tao dk
JOIN training.lop_hoc lh ON dk.lop_hoc_id = lh.lop_hoc_id
WHERE lh.ma_lop = 'L-ATLD-05'
ON CONFLICT (dang_ky_id, buoi_so) DO NOTHING;

-- Report
DO $$
BEGIN
    RAISE NOTICE '=== SEED REPORT ===';
    RAISE NOTICE 'Phong ban: %', (SELECT COUNT(*) FROM hr.phong_ban);
    RAISE NOTICE 'Nhan vien: %', (SELECT COUNT(*) FROM hr.nhan_vien);
    RAISE NOTICE 'Users: %', (SELECT COUNT(*) FROM system.user_account);
    RAISE NOTICE 'Bang cong: %', (SELECT COUNT(*) FROM timekeeping.bang_cong_thang);
    RAISE NOTICE 'Bang luong: %', (SELECT COUNT(*) FROM payroll.bang_luong_thang);
    RAISE NOTICE 'Ky linh luong: %', (SELECT COUNT(*) FROM payroll.ky_linh_luong);
    RAISE NOTICE 'BHXH qua trinh: %', (SELECT COUNT(*) FROM social_ins.qua_trinh_tham_gia);
    RAISE NOTICE 'Cam ket 08: %', (SELECT COUNT(*) FROM payroll.cam_ket_08);
    RAISE NOTICE 'YC tuyen dung: %', (SELECT COUNT(*) FROM recruitment.yeu_cau_tuyen_dung);
    RAISE NOTICE 'KPI cycle: %', (SELECT COUNT(*) FROM performance.kpi_cycle);
    RAISE NOTICE 'KPI assignment: %', (SELECT COUNT(*) FROM performance.kpi_assignment);
    RAISE NOTICE 'CT dao tao: %', (SELECT COUNT(*) FROM training.chuong_trinh_dao_tao);
    RAISE NOTICE 'Lop hoc: %', (SELECT COUNT(*) FROM training.lop_hoc);
    RAISE NOTICE 'Dang ky DT: %', (SELECT COUNT(*) FROM training.dang_ky_dao_tao);
    RAISE NOTICE 'Diem danh: %', (SELECT COUNT(*) FROM training.diem_danh_dao_tao);
    RAISE NOTICE '=== DONE ===';
END $$;