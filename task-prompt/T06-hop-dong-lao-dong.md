# T06 — Hợp đồng lao động & cảnh báo hết hạn

- Nhóm: Core HR
- Blocked by: T05
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`hr.hop_dong_lao_dong`, view `hr.v_hop_dong_sap_het_han`)

## Mục tiêu
Quản lý hợp đồng lao động, phụ lục, và cảnh báo hết hạn trước 30–45 ngày.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T06 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và bảng hr.hop_dong_lao_dong + view hr.v_hop_dong_sap_het_han trong database/schema.sql. Chỉ làm trong package com.company.hrm.hr.

Nhiệm vụ: xây API quản lý hợp đồng lao động và cảnh báo hết hạn.

Yêu cầu:
1. Entity HopDongLaoDong ánh xạ hr.hop_dong_lao_dong: loai_hop_dong (THU_VIEC, XAC_DINH_THOI_HAN, KHONG_XAC_DINH_THOI_HAN, PHU_LUC), hop_dong_goc_id (khi là phụ lục), ngay_ky, ngay_hieu_luc, ngay_het_hieu_luc (null nếu không thời hạn), muc_luong_thoa_thuan, phu_cap_co_dinh (JSONB), trang_thai.
2. Repository + service HopDongService:
   - Tạo hợp đồng cho nhân viên; validate: ngày hết hạn >= ngày hiệu lực; loại KHONG_XAC_DINH_THOI_HAN thì ngay_het_hieu_luc null; phụ lục phải trỏ hop_dong_goc_id hợp lệ.
   - Quy tắc: tại một thời điểm mỗi nhân viên chỉ có 1 hợp đồng chính đang HIEU_LUC (phụ lục không tính là hợp đồng chính). Khi kích hoạt hợp đồng mới, hợp đồng cũ chuyển HET_HIEU_LUC.
   - Ánh xạ phu_cap_co_dinh dạng map {chuc_vu, an_trua, xang_xe, dien_thoai...} <-> JSONB.
   - Truy vấn hợp đồng sắp hết hạn: dùng view v_hop_dong_sap_het_han hoặc query khoảng 30–45 ngày; cho phép tham số ngưỡng ngày.
3. Controller /api/v1/hr:
   - POST /contracts, GET /employees/{id}/contracts, GET /contracts/{id}, PUT /contracts/{id} (chỉ khi chưa kích hoạt hoặc cập nhật file đính kèm), POST /contracts/{id}/addendum (tạo phụ lục).
   - GET /contracts/expiring?fromDays=30&toDays=45 — danh sách cảnh báo, kèm số ngày còn lại.
   - Ghi bảo vệ vai trò HR.
4. Điểm mở rộng: khi ký HĐLĐ chính thức, cần phát tín hiệu để T07/T13 báo tăng BHXH — để lại hook rõ ràng (interface/event) nhưng không tự cập nhật trạng thái nhân viên (đó là việc của T07).

Tiêu chí hoàn thành:
- Test: validate ngày hợp đồng; chỉ 1 HĐ chính HIEU_LUC; tạo phụ lục; truy vấn sắp hết hạn trả đúng khoảng.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, quy tắc hiệu lực hợp đồng, cấu trúc phu_cap_co_dinh.
```
