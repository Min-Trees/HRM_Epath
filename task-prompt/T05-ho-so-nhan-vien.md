# T05 — Hồ sơ nhân viên & người phụ thuộc

- Nhóm: Core HR
- Blocked by: T04
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`hr.nhan_vien`, `hr.nguoi_phu_thuoc`, `hr.qua_trinh_cong_tac`)

## Mục tiêu
Quản lý hồ sơ nhân viên (hub trung tâm), người phụ thuộc phục vụ giảm trừ gia cảnh, và quá trình công tác.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T05 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và các bảng hr.nhan_vien, hr.nguoi_phu_thuoc, hr.qua_trinh_cong_tac trong database/schema.sql. Chỉ làm trong package com.company.hrm.hr.

Nhiệm vụ: xây API quản lý hồ sơ nhân viên và dữ liệu liên quan.

Yêu cầu:
1. Entity NhanVien ánh xạ hr.nhan_vien: định danh (ma_nv, ho_ten, ngay_sinh, gioi_tinh, so_cccd...), liên hệ, học vấn, ngay_vao_lam, FK phong_ban_id, ngach_bac_id, quan_ly_truc_tiep_id, trang_thai (enum), tai_khoan_cham_cong_id. Lưu ý trang_thai là trường PHÁI SINH: task này chỉ khởi tạo mặc định UNG_VIEN/THU_VIEC khi tạo mới; việc chuyển trạng thái do T07 (biến động) đảm nhiệm — KHÔNG cho API sửa trực tiếp trang_thai ở đây.
2. Entity NguoiPhuThuoc và QuaTrinhCongTac + repository.
3. Sinh ma_nv tự động theo quy tắc có tiền tố + số tăng dần (ví dụ NV0001); đảm bảo duy nhất.
4. Service NhanVienService:
   - Tạo hồ sơ (validate CCCD duy nhất, phòng ban tồn tại và đang active, quản lý trực tiếp tồn tại nếu có).
   - Cập nhật thông tin cá nhân/liên hệ/học vấn (KHÔNG gồm trang_thai, phong_ban, chuc_danh, luong — những trường này chỉ đổi qua biến động T07).
   - Tìm kiếm/lọc: theo tên, ma_nv, phòng ban, trạng thái; phân trang (dùng PageResponse của T03).
   - Khi tạo hồ sơ, sinh tai_khoan_cham_cong_id (đồng bộ danh nghĩa sang chấm công) và ghi chú điểm mở rộng để phát event cho module khác (T07 sẽ hoàn thiện event).
5. Người phụ thuộc: CRUD gắn nhan_vien_id, có tu_ngay_giam_tru/den_ngay_giam_tru phục vụ tính thuế; validate không chồng lấn vô lý.
6. Controller /api/v1/hr:
   - POST /employees, GET /employees (lọc+phân trang), GET /employees/{id} (kèm người phụ thuộc & quá trình công tác), PUT /employees/{id} (thông tin cá nhân).
   - CRUD /employees/{id}/dependents, /employees/{id}/work-history.
   - Ghi bảo vệ vai trò HR.
7. Cập nhật liên kết trưởng bộ phận với T04 nếu cần (gán manager hợp lệ).

Tiêu chí hoàn thành:
- Test: tạo hồ sơ sinh ma_nv duy nhất; chặn sửa trực tiếp trang_thai/phong_ban qua PUT; CRUD người phụ thuộc; tìm kiếm phân trang.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, quy tắc sinh mã, ranh giới trường "chỉ đổi qua biến động".
```
