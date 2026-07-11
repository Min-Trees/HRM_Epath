# T16 — Duyệt bảng lương, chi trả & phiếu lương

- Nhóm: Tính lương
- Blocked by: T15
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`payroll.bang_luong_thang`)

## Mục tiêu
Quy trình duyệt bảng lương, tạo lệnh chi ngân hàng, khóa kỳ lương và phát hành phiếu lương điện tử.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T16 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và bảng payroll.bang_luong_thang trong database/schema.sql. Chỉ làm trong package com.company.hrm.payroll. Dùng bảng lương DA_TINH do T15 sinh ra.

Nhiệm vụ: xây quy trình duyệt/chi trả và phiếu lương.

Yêu cầu:
1. Máy trạng thái bảng lương: DA_TINH → DA_DUYET → DA_CHI_TRA; hoặc HUY (chỉ khi chưa DA_CHI_TRA). Sau DA_DUYET không cho tính lại (phối hợp T15). Sau DA_CHI_TRA là bất biến.
2. Service:
   - Duyệt: duyệt theo lô một kỳ (toàn bộ bảng DA_TINH của tháng) hoặc từng bảng; ghi duyet_boi, duyet_luc. Chỉ PAYROLL_ACCOUNTANT/quyền cao được duyệt; có thể yêu cầu điều chỉnh (trả về HUY để T15 tính lại).
   - Khóa kỳ lương: sau khi duyệt, khóa để không phát sinh thay đổi; đồng thời đây là tín hiệu chặn hủy chốt bảng công tương ứng (phối hợp T11: bảng công đã bị payroll tham chiếu thì không cho unlock).
   - Tạo lệnh chi ngân hàng: kết xuất danh sách chi trả (so_tai_khoan_nhan, số tiền thực lĩnh) dạng file/CSV mô phỏng; đánh dấu DA_CHI_TRA và chi_tra_luc khi xác nhận.
   - Phiếu lương: sinh phiếu lương điện tử cho từng nhân viên từ dữ liệu phân rã (thu nhập, phụ cấp, OT, BHXH NLĐ, thuế, khấu trừ, thực lĩnh) — trả về cấu trúc dữ liệu + HTML đơn giản; lưu phieu_luong_url (mô phỏng).
   - Báo cáo kỳ: tổng quỹ lương, tổng BHXH NLĐ/DN, tổng thuế TNCN theo phòng ban và toàn công ty.
3. Controller /api/v1/payroll:
   - POST /payslips/{id}/approve, POST /runs/{month}/{year}/approve (duyệt lô), POST /payslips/{id}/reject.
   - POST /runs/{month}/{year}/bank-file (kết xuất lệnh chi), POST /runs/{month}/{year}/pay (đánh dấu đã chi trả).
   - GET /payslips/{id}/document (phiếu lương), GET /reports/payroll-summary?month=&year=.
   - Bảo vệ vai trò PAYROLL_ACCOUNTANT.

Tiêu chí hoàn thành:
- Test: máy trạng thái đúng (không chi trả khi chưa duyệt; không sửa sau chi trả); duyệt lô; kết xuất lệnh chi đúng tổng; phiếu lương phân rã khớp bảng lương; báo cáo tổng cộng đúng; chặn unlock bảng công đã tham chiếu.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, máy trạng thái bảng lương, cấu trúc phiếu lương và file chi trả, các báo cáo.
```
