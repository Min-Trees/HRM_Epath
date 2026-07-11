# T07 — Biến động nhân sự (append-only) & trạng thái phái sinh

- Nhóm: Core HR
- Blocked by: T05, T06
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`hr.bien_dong_nhan_su`, `hr.nhan_vien.trang_thai`)

## Mục tiêu
Ghi nhận mọi thay đổi nhân sự dưới dạng bản ghi append-only, suy ra trạng thái nhân viên, và phát event đồng bộ sang chấm công/BHXH/lương.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T07 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và bảng hr.bien_dong_nhan_su trong database/schema.sql. Chỉ làm trong package com.company.hrm.hr và cơ chế event dùng chung nếu cần (phối hợp common). Đây là task cốt lõi giữ audit trail — tuân thủ nghiêm quy tắc append-only.

Nhiệm vụ: xây nghiệp vụ biến động nhân sự và cơ chế suy ra trạng thái + phát event.

Yêu cầu:
1. Entity BienDongNhanSu ánh xạ hr.bien_dong_nhan_su: loai_bien_dong (TUYEN_DUNG, BO_NHIEM, DIEU_CHUYEN, THANG_CHUC, DIEU_CHINH_LUONG, KY_LUAT, TAM_HOAN_HDLD, CHAM_DUT_HDLD, NGHI_HUU), so_quyet_dinh, ngay_quyet_dinh, ngay_hieu_luc, phong_ban_truoc/sau, chuc_danh_truoc/sau, luong_truoc/sau, trang_thai_nv_sau, ly_do, file_quyet_dinh_url.
2. Repository chỉ hỗ trợ INSERT và truy vấn; KHÔNG expose update/delete. Trả lịch sử theo nhan_vien_id sắp xếp ngay_hieu_luc giảm dần.
3. Service BienDongService:
   - Tạo biến động: validate quyết định (số hiệu, ngày), tính hợp lệ chuyển trạng thái (ví dụ chỉ NGHI_HUU/CHAM_DUT_HDLD mới đưa về DA_NGHI_*; TAM_HOAN_HDLD → TAM_HOAN_HDLD; đạt thử việc → CHINH_THUC...).
   - Sau khi ghi biến động: cập nhật các trường phái sinh trên NhanVien (trang_thai, phong_ban_id, chuc_danh/ngach nếu có, và mức lương hiện hành phản chiếu từ luong_sau) — đây là NƠI DUY NHẤT được phép đổi các trường này.
   - Suy ra trạng thái nhân viên tại một ngày bất kỳ từ chuỗi biến động (hàm trangThaiTaiNgay), phục vụ tra cứu lịch sử.
4. Cơ chế event nội bộ (Spring ApplicationEventPublisher): phát các event khi biến động hiệu lực:
   - CHINH_THUC / ký HĐLĐ chính thức → sự kiện gợi ý báo tăng BHXH (T13 lắng nghe).
   - CHAM_DUT_HDLD / NGHI_HUU → gợi ý báo giảm BHXH + chốt sổ.
   - DIEU_CHINH_LUONG → gợi ý điều chỉnh mức đóng BHXH và áp lương mới cho payroll từ ngày hiệu lực.
   - DIEU_CHUYEN → cập nhật phòng ban cho chấm công.
   Định nghĩa các lớp event trong common hoặc hr.event; module khác chỉ cần lắng nghe.
5. Controller /api/v1/hr:
   - POST /movements (tạo biến động), GET /employees/{id}/movements (timeline), GET /employees/{id}/status?date=YYYY-MM-DD (trạng thái tại ngày).
   - Bảo vệ vai trò HR.

Ràng buộc bất biến:
- Tuyệt đối không cho sửa/xóa bản ghi biến động.
- Trạng thái và phòng ban/chức danh/lương của nhân viên chỉ thay đổi thông qua biến động, không qua API hồ sơ (T05).

Tiêu chí hoàn thành:
- Test: tạo chuỗi biến động (tuyển → chính thức → thăng chức → nghỉ việc) và kiểm tra trang_thai phái sinh đúng; trangThaiTaiNgay đúng cho các mốc; repository không có API update/delete; event được phát (dùng ApplicationEvents test).
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: danh sách event và ý nghĩa, quy tắc chuyển trạng thái, cách module khác subscribe.
```
