# Quy ước chung — mọi agent phải tuân theo

Đọc file này trước khi thực thi bất kỳ task nào. Prompt trong từng task giả định các quy ước dưới đây.

## 1. Phạm vi & kỷ luật thư mục

- Chỉ tạo/sửa file trong phạm vi task nêu rõ. Không đụng file của module khác trừ khi task cho phép.
- Không sửa `database/schema.sql` (nguồn DDL chuẩn). Nếu cần thay đổi schema, tạo migration mới ở T02 hoặc ghi chú vào phần bàn giao.
- Cấu trúc thư mục dự án:
  ```
  hrm-system/
  ├── database/            # schema.sql + migration Flyway (V1__, V2__...)
  ├── backend/             # Spring Boot (Maven, package com.company.hrm)
  └── frontend/            # React + TypeScript (Vite)
  ```

## 2. Backend

- Java 21, Spring Boot 3.3.x, Maven. Package gốc `com.company.hrm`, mỗi module một package con: `hr`, `attendance`, `bhxh`, `payroll`, dùng chung `common`.
- Kiến trúc mỗi module: `controller` → `service` → `repository` (Spring Data JPA) → `entity`. DTO tách khỏi entity; map bằng phương thức tĩnh hoặc MapStruct.
- Entity ánh xạ đúng bảng/enum trong `schema.sql` (chú ý schema-qualified: `hr.nhan_vien`, `timekeeping.bang_cong_thang`...). Dùng `@Table(schema=..., name=...)`.
- Tiền tệ/định lượng: `BigDecimal` với `RoundingMode.HALF_UP`, không dùng `double`.
- Validation bằng `jakarta.validation` trên DTO request.
- REST: prefix `/api/v1/<module>`. HTTP status: 200 OK, 201 Created, 400 lỗi validation, 404 not found, 409 vi phạm quy tắc nghiệp vụ (`BusinessException`).
- Trả lỗi theo cấu trúc thống nhất `{ "timestamp", "code", "message", "details" }` (xem T03).

## 3. Frontend

- React 18/19 + TypeScript + Vite. State phía UI dùng hook; gọi API qua client tập trung (T17). KHÔNG dùng localStorage/sessionStorage.
- Mỗi màn hình là một route; component tách nhỏ, tái sử dụng (Table, Badge, StatusPill, FormField...).
- Tiếng Việt cho nhãn hiển thị; tên biến/hàm tiếng Anh hoặc không dấu.
- Nếu backend chưa sẵn sàng, dùng mock data tách riêng trong `src/mocks/` để dễ thay bằng call API thật.

## 4. Quy tắc nghiệp vụ bất biến (áp dụng xuyên suốt)

1. `NHAN_VIEN` là hub — mọi bản ghi nghiệp vụ tham chiếu `nhan_vien_id`, không sao chép hồ sơ.
2. Biến động nhân sự và biến động BHXH là **append-only**: chỉ INSERT, không UPDATE/DELETE. Trạng thái nhân viên là trường **phái sinh** từ biến động mới nhất.
3. **Bảng công đã chốt (`DA_CHOT`) là bất biến** — không sửa; muốn sửa phải hủy chốt (`DA_HUY_CHOT`) rồi tạo lại, có audit.
4. **Không chạy lương** khi bảng công tháng chưa `DA_CHOT` hoặc mức đóng BHXH không hợp lệ.
5. Nghỉ phép và OT phải qua **2 cấp duyệt**: `CHO_DUYET → DUYET_CAP_1 → DA_DUYET` (hoặc `TU_CHOI` ở bất kỳ cấp nào).
6. OT theo hệ số 150% (ngày thường), 200% (ngày nghỉ tuần), 300% (lễ/Tết); làm đêm cộng thêm 20–30%.
7. BHXH: NLĐ 10,5%, DN 21,5% trên mức lương đóng; trần BHXH/BHYT = 20× lương cơ sở, trần BHTN = 20× lương tối thiểu vùng.
8. Lương thực lĩnh = lương gộp − BH phần NLĐ − thuế TNCN − tạm ứng − khấu trừ khác.
9. Báo tăng BHXH khi HĐLĐ từ đủ 1 tháng; báo giảm khi nghỉ việc / nghỉ không lương ≥ 14 ngày làm việc/tháng / thai sản / ốm dài ngày.

## 5. Kiểm thử & bàn giao

- Mỗi task backend phải có unit/slice test cho quy tắc nghiệp vụ chính và chạy được `mvn -q test`.
- Mỗi task frontend phải `npm run build` không lỗi type.
- Kết thúc task, ghi mục bàn giao gồm: file đã tạo/sửa, endpoint hoặc route mới, quyết định thiết kế, và lệnh verify đã chạy.
- Nếu môi trường thiếu công cụ (JDK/Maven/registry npm bị chặn), nêu rõ đã verify tĩnh tới đâu và lệnh cần chạy ở máy có mạng.

## 6. Định dạng prompt trong mỗi task

Mỗi file `Txx-*.md` gồm: mục tiêu, phụ thuộc, đầu vào cần đọc, và một khối `PROMPT` — copy nguyên khối đó làm prompt cho agent fable. Trong prompt luôn nhắc agent: đọc `00-CONVENTIONS.md` + `database/schema.sql`, giữ đúng phạm vi, và chạy verify cuối cùng.
