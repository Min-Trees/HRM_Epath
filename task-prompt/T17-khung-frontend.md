# T17 — Khung frontend (routing, layout, API client, auth mock)

- Nhóm: Frontend
- Blocked by: T03
- Đầu vào cần đọc: `00-CONVENTIONS.md`

## Mục tiêu
Dựng nền frontend: layout + sidebar, routing, API client tập trung, quản lý vai trò mock, và bộ component UI dùng chung.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T17 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md. Chỉ làm trong thư mục frontend/ (React + TypeScript + Vite). KHÔNG dùng localStorage/sessionStorage — giữ state trong bộ nhớ (React state/context).

Nhiệm vụ: xây khung ứng dụng frontend để các màn hình nghiệp vụ (T18–T21) cắm vào.

Yêu cầu:
1. Routing: dùng react-router. Layout chính gồm sidebar điều hướng theo nhóm module (Core HR, Chấm công, BHXH, Tính lương) + header (breadcrumb, vai trò hiện tại, thông báo). Route lồng, có trang 404.
2. API client tập trung (src/api/): wrapper fetch/axios đọc base URL từ biến môi trường Vite (VITE_API_BASE_URL), tự gắn header X-User-Role (khớp cơ chế vai trò stub của T03), xử lý lỗi thống nhất theo ErrorResponse { timestamp, code, message, details } và ném lỗi có mã để UI hiển thị. Có lớp typed cho từng module (hrApi, attendanceApi, bhxhApi, payrollApi) — khai báo hàm gọi theo endpoint đã có, kể cả khi backend chưa chạy.
3. Auth/vai trò mock: RoleContext cho phép chuyển vai trò (HR, MANAGER, BHXH_OFFICER, PAYROLL_ACCOUNTANT, EMPLOYEE) để demo phân quyền hiển thị; menu và nút hành động ẩn/hiện theo vai trò. Ghi rõ đây là mock, thay bằng auth thật sau.
4. Bộ component UI dùng chung (src/components/): Table (sắp xếp, phân trang), Badge/StatusPill (map trạng thái → màu), FormField (input/select/date + hiển thị lỗi validation), Modal, Toast/Notification, EmptyState, Card, Tabs, ConfirmDialog. Style CSS thuần hoặc CSS-in-JS nhẹ, tông chuyên nghiệp, responsive; nhãn tiếng Việt.
5. Lớp mock data (src/mocks/) và cơ chế bật/tắt mock (khi VITE_USE_MOCK=true dùng dữ liệu giả, ngược lại gọi API thật) để các màn hình phát triển song song backend.
6. Tiện ích: định dạng tiền VND, ngày giờ, phần trăm; hook usePagedQuery, useAsync xử lý loading/error.

Tiêu chí hoàn thành:
- `npm run build` không lỗi type.
- Chạy dev hiển thị layout + điều hướng rỗng cho 4 nhóm module; đổi vai trò thay đổi menu.
- Bàn giao: cấu trúc thư mục frontend, cách gọi API + bật mock, danh sách component dùng chung và props chính, quy ước thêm màn hình mới cho T18–T21.
```
