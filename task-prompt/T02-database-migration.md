# T02 — Thiết lập database & migration (Flyway)

- Nhóm: Nền tảng
- Blocked by: T01
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql`

## Mục tiêu
Biến `schema.sql` thành migration Flyway có phiên bản, thêm seed dữ liệu tham chiếu, để DB có thể dựng lại tự động và JPA validate được.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T02 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và database/schema.sql trước khi bắt đầu. Chỉ làm trong phạm vi task này. KHÔNG sửa nội dung logic của schema.sql.

Nhiệm vụ: thiết lập cơ chế migration cơ sở dữ liệu PostgreSQL bằng Flyway và chuẩn bị dữ liệu tham chiếu.

Việc cần làm:
1. Tạo backend/src/main/resources/db/migration/V1__init_schema.sql: nội dung là toàn bộ DDL từ database/schema.sql (4 schema hr, timekeeping, social_ins, payroll; enum; bảng; index; view; trigger). Giữ nguyên thứ tự để chạy được một lần từ DB trống.
2. Tạo V2__seed_reference_data.sql: seed dữ liệu tham chiếu không phụ thuộc nghiệp vụ:
   - payroll.bac_thue_tncn: 7 bậc thuế TNCN lũy tiến từng phần theo quy định hiện hành (5,10,15,20,25,30,35%) với ngưỡng thu nhập tính thuế/tháng.
   - payroll.tham_so_luong: lương cơ sở, lương tối thiểu vùng (vùng 1-4) với ngày hiệu lực.
   - Một số ca làm việc mẫu trong timekeeping.ca_lam_viec (hành chính 8h, 3 ca kíp, flexible).
3. Tạo V3__seed_demo_org.sql (tùy chọn, đánh dấu rõ là dữ liệu demo): vài phòng ban gốc và ngạch/bậc lương mẫu để môi trường dev có sẵn cây tổ chức.
4. Cấu hình Flyway trong application.yml nếu T01 chưa hoàn chỉnh: locations classpath:db/migration, baseline-on-migrate.
5. Viết database/README ngắn: cách chạy migration (Flyway tự chạy khi khởi động app, hoặc mvn flyway:migrate), biến môi trường kết nối, cách reset DB dev.

Ràng buộc:
- Bậc thuế và tham số lương phải chính xác theo biểu lũy tiến từng phần; ghi comment nguồn/ngày hiệu lực trong file seed.
- Seed nghiệp vụ (nhân viên, hợp đồng...) KHÔNG thuộc task này — để cho T22.

Tiêu chí hoàn thành:
- Nếu có PostgreSQL: chạy migration từ DB trống thành công, không lỗi. Nếu không có, validate cú pháp SQL tĩnh và nêu rõ.
- Ghi mục bàn giao: danh sách file migration, thứ tự phiên bản, và cách verify.
```
