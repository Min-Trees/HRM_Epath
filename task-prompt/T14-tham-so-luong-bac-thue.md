# T14 — Tham số lương & bậc thuế TNCN

- Nhóm: Tính lương
- Blocked by: T03
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`payroll.tham_so_luong`, `payroll.bac_thue_tncn`)

## Mục tiêu
Quản lý tham số hệ thống lương (lương cơ sở, lương tối thiểu vùng, mức giảm trừ) và biểu thuế TNCN lũy tiến theo thời điểm hiệu lực.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T14 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và các bảng payroll.tham_so_luong, payroll.bac_thue_tncn trong database/schema.sql. Chỉ làm trong package com.company.hrm.payroll (phần tham số).

Nhiệm vụ: xây kho tham số lương và biểu thuế TNCN, có hiệu lực theo thời gian.

Yêu cầu:
1. Entity ThamSoLuong ánh xạ payroll.tham_so_luong: ten_tham_so (ví dụ luong_co_so, luong_toi_thieu_vung_1..4, giam_tru_ban_than, giam_tru_nguoi_phu_thuoc), gia_tri, hieu_luc_tu_ngay, hieu_luc_den_ngay. Entity BacThueTncn ánh xạ payroll.bac_thue_tncn: bac, thu_nhap_tu, thu_nhap_den (null = bậc cao nhất), thue_suat, hieu_luc_tu_ngay.
2. Service ThamSoService:
   - Lấy giá trị tham số theo tên tại một ngày (chọn bản ghi có hiệu lực bao ngày đó). Cập nhật tham số bằng cách MỞ bản ghi mới có hiệu lực từ ngày, đóng bản ghi cũ (không sửa lịch sử).
   - Cung cấp method tiện dụng: lươngCoSo(date), lươngToiThieuVung(vung, date), giamTruBanThan(date), giamTruNguoiPhuThuoc(date).
3. Service ThueTncnService:
   - Nạp biểu thuế lũy tiến theo ngày; hàm tính thuế TNCN từ thu nhập tính thuế (lũy tiến từng phần) trả về số thuế và bảng phân rã theo bậc. Dùng BigDecimal HALF_UP.
   - Seed biểu thuế 7 bậc hiện hành và các tham số mặc định (giảm trừ bản thân 11.000.000, người phụ thuộc 4.400.000) qua migration/CommandLineRunner seed (không ghi đè nếu đã có).
4. Controller /api/v1/payroll:
   - GET/POST /parameters, GET /parameters/effective?name=&date=.
   - GET/POST /tax-brackets, POST /tax/preview (nhập thu nhập tính thuế → trả thuế + phân rã).
   - Bảo vệ vai trò PAYROLL_ACCOUNTANT (đọc cho HR).
5. Cung cấp service method public cho T12 (trần đóng cần lương cơ sở/tối thiểu vùng) và T15 (giảm trừ, biểu thuế).

Tiêu chí hoàn thành:
- Test: lấy tham số theo ngày đúng bản hiệu lực; tính thuế lũy tiến đúng ở vài mốc thu nhập (kiểm tra biên giữa các bậc); phân rã theo bậc cộng lại bằng tổng thuế.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, danh mục tham số + biểu thuế seed, method cung cấp cho T12/T15.
```
