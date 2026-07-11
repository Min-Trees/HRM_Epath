-- =====================================================================
-- V2__seed_reference_data.sql
-- Dữ liệu tham chiếu KHÔNG phụ thuộc nghiệp vụ.
-- Áp dụng cho cả môi trường dev, test và prod (tham số pháp lý).
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) Biểu thuế TNCN lũy tiến từng phần — áp dụng cho thu nhập tính thuế / tháng
--    Nguồn: Luật Thuế TNCN sửa đổi bổ sung; biểu 7 bậc 5/10/15/20/25/30/35%.
--    Lưu ý: ngưỡng "thu_nhap_tu" / "thu_nhap_den" tính trên thu nhập tính thuế
--    (sau khi đã trừ giảm trừ bản thân + người phụ thuộc + BHXH NLĐ).
--    Mốc phổ biến (triệu VND/tháng): 0–5; 5–10; 10–18; 18–32; 32–52; 52–80; >80.
-- ---------------------------------------------------------------------
INSERT INTO payroll.bac_thue_tncn (bac, thu_nhap_tu, thu_nhap_den, thue_suat, hieu_luc_tu_ngay) VALUES
    (1,        0.00,  5000000.00,  5.00, DATE '2009-01-01'),
    (2,  5000000.00, 10000000.00, 10.00, DATE '2009-01-01'),
    (3, 10000000.00, 18000000.00, 15.00, DATE '2009-01-01'),
    (4, 18000000.00, 32000000.00, 20.00, DATE '2009-01-01'),
    (5, 32000000.00, 52000000.00, 25.00, DATE '2009-01-01'),
    (6, 52000000.00, 80000000.00, 30.00, DATE '2009-01-01'),
    (7, 80000000.00,        NULL, 35.00, DATE '2009-01-01');

-- ---------------------------------------------------------------------
-- 2) Tham số lương cơ sở & lương tối thiểu vùng
--    Mặc định lương cơ sở = 2,340,000 VND (Nghị định 73/2024/NĐ-CP, từ 01/07/2024)
--    Lương tối thiểu vùng theo Nghị định 74/2024/NĐ-CP từ 01/07/2024:
--      Vùng I: 4,960,000 | Vùng II: 4,410,000 | Vùng III: 3,860,000 | Vùng IV: 3,450,000
--    Có thể override bằng migration mới khi có nghị định mới.
-- ---------------------------------------------------------------------
INSERT INTO payroll.tham_so_luong (ten_tham_so, gia_tri, hieu_luc_tu_ngay, hieu_luc_den_ngay) VALUES
    ('luong_co_so',         2340000.00, DATE '2024-07-01', NULL),
    ('luong_toi_thieu_vung_1', 4960000.00, DATE '2024-07-01', NULL),
    ('luong_toi_thieu_vung_2', 4410000.00, DATE '2024-07-01', NULL),
    ('luong_toi_thieu_vung_3', 3860000.00, DATE '2024-07-01', NULL),
    ('luong_toi_thieu_vung_4', 3450000.00, DATE '2024-07-01', NULL),
    ('giam_tru_ban_than',   11000000.00, DATE '2020-07-01', NULL);

-- ---------------------------------------------------------------------
-- 3) Ca làm việc mẫu (tham chiếu cho phân ca)
--    - HC1: Hành chính 8h–17h (nghỉ trưa 12–13)
--    - HC_FLEX: Flexible 8h chuẩn
--    - CA1: Ca sáng (6h–14h)
--    - CA2: Ca chiều (14h–22h)
--    - CA3: Ca đêm (22h–6h hôm sau, qua_ngay=true)
-- ---------------------------------------------------------------------
INSERT INTO timekeeping.ca_lam_viec (ma_ca, ten_ca, loai_ca, gio_bat_dau, gio_ket_thuc, so_gio_chuan, qua_ngay) VALUES
    ('HC1',     'Hành chính chuẩn',     'HANH_CHINH', TIME '08:00:00', TIME '17:00:00', 8.00, FALSE),
    ('HC_FLEX', 'Hành chính linh hoạt', 'FLEXIBLE',   TIME '08:00:00', TIME '17:00:00', 8.00, FALSE),
    ('CA1',     'Ca sáng',              'CA_KIP',     TIME '06:00:00', TIME '14:00:00', 8.00, FALSE),
    ('CA2',     'Ca chiều',             'CA_KIP',     TIME '14:00:00', TIME '22:00:00', 8.00, FALSE),
    ('CA3',     'Ca đêm',               'CA_KIP',     TIME '22:00:00', TIME '06:00:00', 8.00, TRUE);