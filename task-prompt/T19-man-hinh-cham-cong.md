# T19 — Màn hình Chấm công (phân ca, ngoại lệ, phép/OT, bảng công)

- Nhóm: Frontend
- Blocked by: T17, T11
- Đầu vào cần đọc: `00-CONVENTIONS.md`, API của T08–T11

## Mục tiêu
Các màn hình quản lý ca/phân ca, xem chấm công và ngoại lệ, xin–duyệt phép/OT, và chốt bảng công tháng.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T19 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và dùng khung frontend + API client của T17. Gọi endpoint /api/v1/attendance của T08–T11 (fallback mock). Chỉ làm trong frontend/ (khu vực module Chấm công).

Nhiệm vụ: xây các màn hình Chấm công.

Yêu cầu:
1. Ca & phân ca: danh sách ca; lịch phân ca dạng lưới tuần/tháng theo nhân viên; phân ca cho cá nhân/nhóm và theo khoảng ngày; hỗ trợ mẫu ca kíp xoay vòng.
2. Chấm công & ngoại lệ: bảng dữ liệu vào/ra theo ngày, đánh dấu ngoại lệ (đi trễ/về sớm/thiếu công/ngoài ca) bằng màu; màn hình danh sách ngoại lệ cần xử lý; luồng nhân viên gửi giải trình và quản lý duyệt/từ chối.
3. Nghỉ phép & OT: form tạo đơn (nghỉ phép theo loại, đăng ký OT với hệ số/đêm); danh sách đơn với trạng thái; giao diện duyệt 2 cấp thể hiện rõ cấp 1 (quản lý) và cấp 2 (HR), chặn cùng người duyệt 2 cấp; hiển thị quỹ phép năm còn lại.
4. Bảng công tháng: màn hình tổng hợp theo tháng với các cột (ngày công, OT theo hệ số, phép, nghỉ không lương, thiếu công) và StatusPill (MO/DA_CHOT/DA_HUY_CHOT); nút Tổng hợp lại, Chốt (khóa) và Hủy chốt (chỉ HR). Khi DA_CHOT hiển thị chỉ đọc, có chỉ dẫn muốn sửa phải hủy chốt.
5. Phân quyền: EMPLOYEE tạo đơn; MANAGER duyệt cấp 1; HR duyệt cấp 2, phân ca và chốt bảng công. Ẩn/hiện hành động theo vai trò.

Tiêu chí hoàn thành:
- `npm run build` không lỗi type; render với mock và (nếu có) API thật.
- Kiểm tra: quy trình duyệt 2 cấp hiển thị đúng; bảng công DA_CHOT ở chế độ chỉ đọc; ngoại lệ tô màu đúng loại.
- Bàn giao: route/màn hình, endpoint đã nối, component tái sử dụng, phần còn mock.
```
