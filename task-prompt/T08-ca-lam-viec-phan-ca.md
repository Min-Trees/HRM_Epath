# T08 — Ca làm việc & phân ca

- Nhóm: Chấm công
- Blocked by: T05
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`timekeeping.ca_lam_viec`, `timekeeping.phan_ca`)

## Mục tiêu
Định nghĩa ca làm việc (hành chính, ca kíp, linh hoạt) và phân ca cho nhân viên theo ngày/tuần/tháng — nền để đối chiếu dữ liệu chấm công.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T08 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và các bảng timekeeping.ca_lam_viec, timekeeping.phan_ca trong database/schema.sql. Chỉ làm trong package com.company.hrm.attendance.

Nhiệm vụ: xây API quản lý ca làm việc và phân ca.

Yêu cầu:
1. Entity CaLamViec ánh xạ timekeeping.ca_lam_viec: ma_ca, ten_ca, loai_ca (HANH_CHINH, CA_KIP, FLEXIBLE), gio_bat_dau, gio_ket_thuc, so_gio_chuan, qua_ngay (ca đêm kết thúc hôm sau), active. Entity PhanCa ánh xạ timekeeping.phan_ca (nhan_vien_id, ca_id, ngay_ap_dung, ghi_chu) với ràng buộc UNIQUE (nhan_vien_id, ngay_ap_dung).
2. Repository + service CaLamViecService: CRUD ca; không trùng ma_ca; đóng bằng active=false thay vì xóa khi ca đã được dùng để phân.
3. Service PhanCaService:
   - Phân ca cho 1 nhân viên theo 1 ngày; phân theo khoảng ngày (tuần/tháng); phân theo nhóm (danh sách nhan_vien_id) trong một khoảng.
   - Chặn phân trùng: mỗi nhân viên chỉ 1 ca/ngày (nếu phân lại thì ghi đè có kiểm soát, ghi chú lý do).
   - Hỗ trợ ca kíp xoay vòng: sinh phân ca theo mẫu luân phiên (ví dụ 3 ca xoay theo chu kỳ) cho một nhóm và khoảng ngày.
   - Validate nhân viên tồn tại và đang làm việc (không TAM_HOAN/DA_NGHI).
4. Controller /api/v1/attendance:
   - GET/POST/PUT /shifts, PATCH /shifts/{id}/close.
   - POST /shift-assignments (một hoặc nhiều), GET /shift-assignments?employeeId=&from=&to= (lịch làm việc), DELETE /shift-assignments/{id} (chỉ khi chưa có chấm công phát sinh).
   - Bảo vệ ghi bằng vai trò HR/MANAGER.
5. Cung cấp truy vấn "ca chuẩn của nhân viên tại một ngày" (dùng cho T09 đối chiếu chấm công) qua service method public.

Tiêu chí hoàn thành:
- Test: CRUD ca; phân ca theo khoảng không tạo trùng; sinh ca kíp xoay vòng đúng chu kỳ; lấy ca chuẩn theo ngày đúng.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, mô hình ca/phân ca, method lấy ca chuẩn cho T09.
```
