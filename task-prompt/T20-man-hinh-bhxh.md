# T20 — Màn hình BHXH (quá trình, báo tăng/giảm, chế độ)

- Nhóm: Frontend
- Blocked by: T17, T13
- Đầu vào cần đọc: `00-CONVENTIONS.md`, API của T12–T13

## Mục tiêu
Các màn hình quản lý quá trình đóng BHXH, hồ sơ báo tăng/giảm và chế độ hưởng.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T20 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và dùng khung frontend + API client của T17. Gọi endpoint /api/v1/social-insurance của T12–T13 (fallback mock). Chỉ làm trong frontend/ (khu vực module BHXH).

Nhiệm vụ: xây các màn hình BHXH.

Yêu cầu:
1. Tổng quan BHXH: thẻ chỉ số (số đang tham gia, hồ sơ báo tăng/giảm chờ nộp, hồ sơ quá hạn); danh sách hồ sơ cần xử lý.
2. Quá trình tham gia: theo nhân viên, hiển thị các giai đoạn đóng (mức đóng, tỷ lệ NLĐ 10,5% / DN 21,5%, trần đóng, khoảng thời gian); mở/điều chỉnh/đóng giai đoạn; quản lý sổ BHXH và chốt sổ.
3. Báo tăng/giảm: danh sách hồ sơ biến động (append-only, không sửa/xóa), tạo hồ sơ thủ công và xem hồ sơ tự sinh từ biến động HR; hiển thị hạn nộp và cảnh báo quá hạn; nút đánh dấu đã nộp; xem/kết xuất tờ khai (D02-LT, TK1-TS).
4. Chế độ hưởng: form tạo hồ sơ chế độ (ốm/thai sản/TNLĐ-BNN...) kèm chứng từ; máy trạng thái MOI_TAO → DA_NOP_CO_QUAN_BHXH → DA_DUOC_CHI_TRA/TU_CHOI; hiển thị số tiền đề nghị và được chi trả.
5. Phân quyền: BHXH_OFFICER ghi; HR/PAYROLL_ACCOUNTANT chỉ đọc phần liên quan. Trạng thái hồ sơ dùng StatusPill; nhấn mạnh tính bất biến của bản ghi báo tăng/giảm.

Tiêu chí hoàn thành:
- `npm run build` không lỗi type; render với mock và (nếu có) API thật.
- Kiểm tra: hồ sơ biến động không có nút sửa/xóa; cảnh báo quá hạn hiển thị đúng; máy trạng thái chế độ chuyển đúng.
- Bàn giao: route/màn hình, endpoint đã nối, component tái sử dụng, phần còn mock.
```
