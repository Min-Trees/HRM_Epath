# Database

## Nguồn DDL chuẩn

`schema.sql` là **nguồn DDL chuẩn duy nhất** (single source of truth). Mọi thay đổi schema phải qua migration Flyway (`hrm-system/backend/src/main/resources/db/migration/`), KHÔNG sửa trực tiếp `schema.sql`.

## Cách chạy migration

### Cách 1 — Tự động khi khởi động backend

Mặc định Spring Boot + Flyway sẽ tự chạy mọi migration khi app start:

```bash
cd hrm-system/backend
mvn spring-boot:run
```

Log sẽ hiện các dòng như:

```
Flyway Community Edition X.Y.Z by Redgate
Successfully validated X migrations
Migrating schema "hrm" to version "1 - init schema"
Migrating schema "hrm" to version "2 - seed reference data"
Migrating schema "hrm" to version "3 - seed demo org"
Successfully applied X migrations
```

### Cách 2 — Dùng Flyway Maven plugin (chạy tay)

```bash
cd hrm-system/backend
mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/hrm \
  -Dflyway.user=postgres \
  -Dflyway.password=postgres
```

Lệnh khác: `mvn flyway:info`, `mvn flyway:clean` (XÓA TOÀN BỘ schema — chỉ dùng dev!), `mvn flyway:validate`.

## Biến môi trường kết nối

| Biến           | Mặc định                                  | Ghi chú |
|----------------|--------------------------------------------|---------|
| `DB_URL`       | `jdbc:postgresql://localhost:5432/hrm`     | JDBC URL |
| `DB_USERNAME`  | `postgres`                                 | user |
| `DB_PASSWORD`  | `postgres`                                 | password |

## Danh sách migration

| Phiên bản | File | Nội dung |
|-----------|------|---------|
| V1 | `V1__init_schema.sql` | Toàn bộ DDL: 4 schema, enum, bảng, index, view, trigger (copy từ `schema.sql`). |
| V2 | `V2__seed_reference_data.sql` | Tham số pháp lý: 7 bậc thuế TNCN (5/10/15/20/25/30/35%), lương cơ sở, lương tối thiểu 4 vùng, 5 ca làm việc mẫu. |
| V3 | `V3__seed_demo_org.sql` | Phòng ban + ngạch bậc lương demo (CHỈ dev/test — không chạy ở prod). |

## Reset DB ở môi trường dev

```bash
# Xóa toàn bộ DB và tạo lại
dropdb hrm
createdb hrm
# Lần khởi động backend kế tiếp sẽ tự chạy lại migrations
mvn spring-boot:run
```

Hoặc dùng Flyway clean (chỉ dev):

```bash
mvn flyway:clean flyway:migrate
```

## Quy tắc schema

1. **Mỗi module 1 schema** (`hr`, `timekeeping`, `social_ins`, `payroll`) — bám sát bounded context.
2. **Bảng append-only**: `hr.bien_dong_nhan_su`, `social_ins.bhxh_bien_dong`. KHÔNG UPDATE/DELETE ở tầng ứng dụng.
3. **Bảng công đã chốt** (`timekeeping.bang_cong_thang.trang_thai = 'DA_CHOT'`) là **bất biến** — trigger `trg_bangcong_immutable` chặn UPDATE khi vẫn ở trạng thái chốt. Phải chuyển sang `DA_HUY_CHOT` rồi sửa.
4. **Trạng thái nhân viên** là trường phái sinh, cập nhật từ biến động mới nhất (xử lý ở T07).
5. **Mọi bảng có audit columns**: `created_at`, `updated_at`, `created_by`.
6. **UUID khóa chính** (`gen_random_uuid()`), không dùng bigserial.

## Thêm migration mới

Đặt tên `V<n>__<mo_ta>.sql` (snake_case, tiếng Anh hoặc không dấu). KHÔNG sửa file đã chạy. Test trên DB dev trước khi merge.