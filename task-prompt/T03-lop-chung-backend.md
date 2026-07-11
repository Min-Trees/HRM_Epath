# T03 — Lớp chung backend (lỗi, phân trang, audit, bảo mật cơ bản)

- Nhóm: Nền tảng
- Blocked by: T01
- Đầu vào cần đọc: `00-CONVENTIONS.md`

## Mục tiêu
Xây các thành phần dùng chung cho mọi module backend để tránh lặp và đảm bảo hành vi thống nhất.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T03 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md trước. Chỉ tạo file trong package com.company.hrm.common và cấu hình chung; không viết code nghiệp vụ của module cụ thể.

Nhiệm vụ: xây lớp nền dùng chung cho backend.

Thành phần cần tạo (package com.company.hrm.common):
1. Ngoại lệ: BusinessException (vi phạm quy tắc nghiệp vụ → HTTP 409), ResourceNotFoundException (404), ValidationException nếu cần. Mỗi ngoại lệ mang mã lỗi (code) dạng chuỗi.
2. GlobalExceptionHandler (@RestControllerAdvice): map các ngoại lệ và MethodArgumentNotValidException về response lỗi thống nhất ErrorResponse { timestamp, code, message, details }. details liệt kê lỗi field khi validation.
3. Phân trang & lọc: lớp PageResponse<T> { content, page, size, totalElements, totalPages } và tiện ích chuyển từ Spring Data Page.
4. Audit: lớp cơ sở BaseAuditEntity với created_at/updated_at (@CreationTimestamp/@UpdateTimestamp) để entity kế thừa; cấu hình JPA auditing nếu dùng created_by/updated_by.
5. Kiểu tiền tệ/tiện ích: helper làm tròn BigDecimal (HALF_UP, scale 2) và định dạng.
6. Bảo mật cơ bản (mức prototype, không cần provider thật): SecurityContextHolder giả lập vai trò qua header X-User-Role (HR, MANAGER, BHXH_OFFICER, PAYROLL_ACCOUNTANT, EMPLOYEE) và một annotation/aspect @RequiresRole để bảo vệ endpoint. Ghi chú rõ đây là stub để thay bằng Spring Security/JWT khi tích hợp thật.
7. Cấu hình chung: OpenAPI/springdoc (nếu thêm dependency, cập nhật pom), CORS cho phép origin của frontend dev, Jackson cấu hình ngày/giờ ISO-8601 và BigDecimal không mất scale.
8. Lớp tiện ích thời gian: chuyển đổi LocalDate/Instant, tính "ngày làm việc" cơ bản (phục vụ các quy tắc như nghỉ không lương ≥ 14 ngày làm việc).

Tiêu chí hoàn thành:
- Có unit test cho GlobalExceptionHandler (mapping status/code) và helper làm tròn tiền.
- `mvn -q test` chạy được (hoặc nêu rõ hạn chế môi trường + kiểm tra tĩnh).
- Ghi mục bàn giao: danh sách lớp chung, cách các module khác sử dụng, và các quyết định thiết kế (đặc biệt về cơ chế vai trò stub).
```
