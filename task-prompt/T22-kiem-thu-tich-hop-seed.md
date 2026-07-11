# T22 — Kiểm thử tích hợp end-to-end & seed dữ liệu demo

- Nhóm: QA
- Blocked by: T16, T21
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql`, bàn giao của tất cả task backend

## Mục tiêu
Chứng minh toàn hệ thống chạy thông suốt: từ tạo nhân viên đến ra bảng lương; seed dữ liệu demo cho toàn bộ vòng đời.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T22 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và database/schema.sql. Được phép chạm nhiều module ở mức test và seed; KHÔNG sửa logic nghiệp vụ đã có (nếu phát hiện lỗi, ghi lại và đề xuất, chỉ sửa khi cần cho test chạy được và nêu rõ).

Nhiệm vụ: viết kiểm thử tích hợp end-to-end và seed dữ liệu demo.

Yêu cầu:
1. Seed dữ liệu demo (script SQL trong database/ hoặc CommandLineRunner có cờ bật, idempotent): 1 cây phòng ban vài cấp, ngạch bậc, ~8–10 nhân viên trải các trạng thái (thử việc, chính thức, tạm hoãn, đã nghỉ), hợp đồng (gồm sắp hết hạn), phân ca + chấm công một tháng có ngoại lệ, đơn phép/OT đã duyệt, quá trình BHXH + vài hồ sơ báo tăng/giảm, và một kỳ lương mẫu. Dữ liệu tiếng Việt hợp lý.
2. Test tích hợp backend (SpringBootTest, có thể dùng Testcontainers PostgreSQL nếu môi trường cho phép; nếu không, nêu rõ và dùng H2/profile test tương thích): kịch bản xuyên suốt
   - Tạo nhân viên → ký HĐ thử việc → biến động lên chính thức → kiểm tra trạng thái phái sinh + event báo tăng BHXH.
   - Phân ca + chấm công (gồm ngoại lệ đã giải trình) → tạo và duyệt 2 cấp đơn phép/OT → tổng hợp và chốt bảng công.
   - Xác nhận không chạy được lương khi bảng công chưa chốt; sau khi chốt + có BHXH hợp lệ thì chạy lương thành công.
   - Tính lương → kiểm tra số học thực lĩnh = gộp − BHXH NLĐ − TNCN − tạm ứng/khấu trừ; duyệt → chi trả; xác nhận không sửa được sau chi trả và không hủy chốt được bảng công đã tham chiếu.
   - Nghỉ việc → báo giảm + chốt sổ.
3. Kiểm tra các bất biến (viết assert rõ ràng): append-only biến động HR/BHXH; bảng công DA_CHOT bất biến; duyệt 2 cấp; công thức OT/BHXH/thuế.
4. (Tùy chọn nếu môi trường frontend chạy được) smoke test frontend với dữ liệu seed qua mock/API; nếu không, mô tả kịch bản kiểm thử thủ công theo màn hình.
5. Tài liệu chạy: README ngắn ở database/ hoặc thư mục test mô tả cách seed + chạy test, và checklist nghiệm thu theo từng quy tắc nghiệp vụ.

Tiêu chí hoàn thành:
- `mvn -q verify` (hoặc test) chạy kịch bản e2e xanh (hoặc nêu rõ hạn chế môi trường + phần đã verify tĩnh).
- Seed chạy idempotent, tạo dữ liệu demo đầy đủ vòng đời.
- Bàn giao: danh sách kịch bản e2e + bất biến đã kiểm, hướng dẫn seed/chạy, checklist nghiệm thu, và danh sách lỗi/đề xuất (nếu có).
```
