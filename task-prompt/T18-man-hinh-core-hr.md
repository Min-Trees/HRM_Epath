# T18 — Màn hình Core HR (nhân viên, hợp đồng, biến động)

- Nhóm: Frontend
- Blocked by: T17, T07
- Đầu vào cần đọc: `00-CONVENTIONS.md`, API của T04–T07

## Mục tiêu
Các màn hình quản lý tổ chức, hồ sơ nhân viên, hợp đồng và timeline biến động.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T18 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và dùng khung frontend + API client của T17. Gọi các endpoint /api/v1/hr do T04–T07 cung cấp (nếu backend chưa sẵn, dùng mock của T17). Chỉ làm trong frontend/ (khu vực module Core HR).

Nhiệm vụ: xây các màn hình Core HR.

Yêu cầu:
1. Cơ cấu tổ chức: cây phòng ban (xem/tạo/sửa/đóng), hiển thị định biên và số nhân sự; quản lý ngạch/bậc lương. Gán trưởng bộ phận.
2. Danh sách nhân viên: bảng có tìm kiếm (tên/mã), lọc theo phòng ban và trạng thái, phân trang (dùng PageResponse). Nút thêm nhân viên (form validate CCCD, phòng ban, quản lý).
3. Hồ sơ nhân viên (chi tiết): thông tin cá nhân/liên hệ/học vấn; tab người phụ thuộc (CRUD, phục vụ giảm trừ) và quá trình công tác. Sửa thông tin cá nhân — KHÔNG cho sửa trực tiếp trạng thái/phòng ban/chức danh/lương (các trường này chỉ đổi qua biến động).
4. Hợp đồng: danh sách hợp đồng của nhân viên, tạo hợp đồng/phụ lục, hiển thị hiệu lực; màn hình cảnh báo hợp đồng sắp hết hạn (30–45 ngày) với số ngày còn lại.
5. Biến động nhân sự: form tạo biến động (loại, số/ngày quyết định, ngày hiệu lực, thay đổi phòng ban/chức danh/lương, trạng thái sau); timeline biến động theo thời gian trên hồ sơ; tra cứu trạng thái tại một ngày. Nhấn mạnh append-only: không có nút sửa/xóa biến động.
6. Phân quyền hiển thị theo vai trò (HR ghi; vai trò khác chỉ đọc phần liên quan). Trạng thái nhân viên/hợp đồng hiển thị bằng StatusPill.

Tiêu chí hoàn thành:
- `npm run build` không lỗi type; các màn hình render với mock và (nếu có) API thật.
- Kiểm tra: không tồn tại nút sửa trực tiếp trạng thái/lương ở hồ sơ; timeline biến động hiển thị đúng thứ tự; cảnh báo hết hạn lọc đúng khoảng.
- Bàn giao: danh sách route/màn hình, endpoint đã nối, các component tái sử dụng, khoảng trống dữ liệu còn mock.
```
