# T21 — Màn hình Tính lương (bảng lương, phiếu lương, duyệt)

- Nhóm: Frontend
- Blocked by: T17, T16
- Đầu vào cần đọc: `00-CONVENTIONS.md`, API của T14–T16

## Mục tiêu
Các màn hình chạy/duyệt bảng lương tháng, xem phiếu lương phân rã, kết xuất chi trả và báo cáo kỳ lương.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T21 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và dùng khung frontend + API client của T17. Gọi endpoint /api/v1/payroll của T14–T16 (fallback mock). Chỉ làm trong frontend/ (khu vực module Tính lương).

Nhiệm vụ: xây các màn hình Tính lương.

Yêu cầu:
1. Tham số & thuế: màn hình xem/cập nhật tham số lương (lương cơ sở, tối thiểu vùng, giảm trừ) và biểu thuế TNCN; công cụ preview thuế từ thu nhập tính thuế.
2. Chạy lương: chọn kỳ (tháng/năm) và phạm vi (nhân viên/phòng ban/toàn công ty), nút Chạy lương; hiển thị điều kiện tiên quyết (bảng công đã chốt, BHXH hợp lệ) và thông báo rõ khi bị từ chối (mã lỗi 409). Không cho chạy khi chưa đủ điều kiện.
3. Bảng lương tháng: bảng với các cột thu nhập, phụ cấp, OT, BHXH NLĐ/DN, thuế TNCN, tạm ứng/khấu trừ, thực lĩnh; StatusPill (DA_TINH/DA_DUYET/DA_CHI_TRA/HUY); tổng quỹ lương kỳ. Hành động: duyệt (đơn/lô), yêu cầu điều chỉnh (reject → tính lại), kết xuất lệnh chi ngân hàng, đánh dấu đã chi trả.
4. Phiếu lương: xem phiếu lương điện tử của từng nhân viên với phân rã đầy đủ khớp bảng lương; nút tải/ in.
5. Báo cáo: tổng quỹ lương, tổng BHXH NLĐ/DN, tổng thuế TNCN theo phòng ban và toàn công ty cho kỳ.
6. Phân quyền: PAYROLL_ACCOUNTANT ghi/duyệt; vai trò khác chỉ đọc. Sau DA_CHI_TRA hiển thị chỉ đọc.

Tiêu chí hoàn thành:
- `npm run build` không lỗi type; render với mock và (nếu có) API thật.
- Kiểm tra: nút Chạy lương bị chặn khi chưa đủ điều kiện và hiển thị lý do; phiếu lương phân rã khớp tổng; máy trạng thái phản ánh đúng (không thao tác sau chi trả).
- Bàn giao: route/màn hình, endpoint đã nối, component tái sử dụng, phần còn mock.
```
