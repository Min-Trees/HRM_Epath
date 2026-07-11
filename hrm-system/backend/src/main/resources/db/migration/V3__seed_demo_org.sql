-- =====================================================================
-- V3__seed_demo_org.sql
-- Dữ liệu DEMO — chỉ chạy ở môi trường dev/test. Production KHÔNG dùng.
-- Tạo vài phòng ban gốc và ngạch/bậc lương mẫu để có sẵn cây tổ chức.
-- =====================================================================

-- Phòng ban gốc (cha_id = NULL)
INSERT INTO hr.phong_ban (ma_phong_ban, ten_phong_ban, phong_ban_cha_id, dinh_bien, cap_do) VALUES
    ('PGD',   'Ban Giám đốc',                 NULL, 5,  1),
    ('PNS',   'Phòng Nhân sự',                NULL, 8,  1),
    ('PKT',   'Phòng Kế toán',                NULL, 6,  1),
    ('PKD',   'Phòng Kinh doanh',             NULL, 15, 1),
    ('PKTCT', 'Phòng Kỹ thuật Công trình',    NULL, 20, 1);

-- Đơn vị con (ví dụ dưới PGD)
INSERT INTO hr.phong_ban (ma_phong_ban, ten_phong_ban, phong_ban_cha_id, dinh_bien, cap_do)
SELECT 'PIT', 'Phòng IT', phong_ban_id, 6, 2 FROM hr.phong_ban WHERE ma_phong_ban = 'PGD'
UNION ALL
SELECT 'PNS_TD', 'Tổ Tuyển dụng', phong_ban_id, 3, 2 FROM hr.phong_ban WHERE ma_phong_ban = 'PNS';

-- Ngạch/bậc lương mẫu (chuẩn Nhà nước tham khảo, có thể điều chỉnh theo doanh nghiệp)
INSERT INTO hr.ngach_bac_luong (ma_ngach, ten_chuc_danh, bac_luong, he_so_luong, luong_co_ban_toi_thieu) VALUES
    ('NV01', 'Nhân viên văn phòng bậc 1',   1, 2.34, 4960000.00),
    ('NV02', 'Nhân viên văn phòng bậc 2',   2, 2.67, 4960000.00),
    ('NV03', 'Nhân viên văn phòng bậc 3',   3, 3.00, 4960000.00),
    ('KTV01','Kỹ thuật viên bậc 1',         1, 3.00, 4960000.00),
    ('KTV02','Kỹ thuật viên bậc 2',         2, 3.33, 4960000.00),
    ('TP',   'Trưởng phòng',                5, 4.50, 4960000.00),
    ('PP',   'Phó phòng',                   4, 4.00, 4960000.00),
    ('GD',   'Giám đốc',                    7, 6.50, 4960000.00);