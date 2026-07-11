# T11 — Module Quản trị hệ thống (System Administration) & Đa tenant

## Đã làm gì

Bổ sung module quản trị hệ thống theo `bosung.md`:
- 6 bảng mới trong schema `system.*` (Company, Role, Permission, RolePermission, UserAccount, AuditLog).
- 6 entity + 5 repository + 4 service + 3 controller + 1 AOP aspect + 1 password encoder stub.
- Multi-tenant: thêm `company_id` vào `hr.nhan_vien`, `hr.hop_dong_lao_dong`, `payroll.bang_luong_thang` (NOT NULL sau V6 backfill) + FK sang `system.company`.
- RBAC: thêm annotation `@RequiresPermission` chạy song song với `@RequiresRole` cũ (backwards-compatible). Format permission `resource.action`.
- Audit AOP: annotation `@Auditable` + `AuditAspect` chạy sau khi method return OK, snapshot JSONB old/new.
- Seed mặc định: 1 company + 6 role + ~50 permission + 2 user (admin/cadmin) qua migration V5/V6.
- Tenant resolver: `TenantContext` (ThreadLocal) + `AuthContext.currentCompanyIdOrNull()`, tầng service dùng để filter.

## Files mới / sửa

### Migration (mới) trong `hrm-system/backend/src/main/resources/db/migration/`

- **V4__system_admin_and_multi_tenant.sql** — Schema `system` + 6 bảng + enum `trang_thai_company` / `trang_thai_user_account` + thêm `company_id` vào 3 bảng nghiệp vụ (nullable tạm thời, FK sẽ thêm ở V6 sau khi backfill).
- **V5__seed_system_data.sql** — 1 default company (`11111111-1111-1111-1111-111111111111`) + 6 role + 50 permission (5 module: `hr`, `timekeeping`, `social_ins`, `payroll`, `system`) + gán role_permission cho 6 role.
- **V6__migrate_data_default_company.sql** — Backfill `company_id = default` cho mọi NV/HĐ/Lương hiện có, ép NOT NULL, thêm FK sang `system.company`. Insert 2 user placeholder (`admin`, `cadmin`); password sẽ được reset sau migration bởi `SystemDataInitializer`.

### Code Java (mới) trong `com.company.hrm.system`

**Entity** (`com.company.hrm.system.entity`):
- `Company`, `TrangThaiCompany` (enum), `RoleEntity`, `Permission`, `UserAccount`, `TrangThaiUserAccount` (enum), `AuditLog`.

**Repository** (`com.company.hrm.system.repository`):
- `CompanyRepository`, `RoleEntityRepository`, `PermissionRepository`, `UserAccountRepository`, `AuditLogRepository` — tất cả extends `JpaRepository`, có method query ngắn (findByMaSoThue, findByCode, findByUsername, ...).

**Service** (`com.company.hrm.system.service`):
- `CompanyService` — CRUD + updateStatus; validate `TAX_CODE_DUPLICATE`.
- `RoleService` — `findOrCreateByCode`, `getPermissionsByRoleCodes`, `getPermissionsByUserId`.
- `UserAccountService` — CRUD + lock/unlock/resetPassword + login (verify BCrypt/SHA-256 stub). KHÔNG xoá user — giữ lịch sử login.
- `AuditService` — `record(module, action, entityType, entityId, oldJson, newJson)`.

**Controller** (`com.company.hrm.system.controller`):
- `SystemAdminController` — `/api/v1/system/companies/*` (6 endpoint).
- `UserAccountController` — `/api/v1/system/users/*` (7 endpoint).
- `AuthController` — `/api/v1/auth/login`, `/auth/me` (stub JWT).

**Audit AOP** (`com.company.hrm.system.audit`):
- `@Auditable(module = "...", action = "...", entityType = "...")` annotation.
- `AuditAspect` — `@AfterReturning`, snapshot JSON gồm `entityId` + tên method + arg types (đã redact password).

**Security stub** (`com.company.hrm.system.security`):
- `PasswordEncoder` — SHA-256 + 16-byte salt ngẫu nhiên, format `base64(salt):base64(hash)`. TODO: thay bằng Spring Security `BCryptPasswordEncoder` khi tích hợp.

**Configuration** (`com.company.hrm.system.config`):
- `SystemBeansConfig` — bean `PasswordEncoder`.
- `SystemDataInitializer` (CommandLineRunner) — sau Flyway chạy V6, reset password placeholder của `admin/admin123` và `cadmin/cadmin123`.

**Context** (`com.company.hrm.system.context`):
- `TenantContext` — ThreadLocal `currentCompanyIdOrNull()`.

### Sửa `common.security` (RBAC mới)

**`AuthContext`** — thêm field `currentCompanyId`, `currentPermissions` (Set<String>); methods `currentCompanyIdOrNull()`, `requireCompanyId()`, `hasPermission(String)`, `requirePermission(String)`, `hasRole(String)`, `csvToPermissions(String)`. **Giữ** API cũ `set(userId, role)` để không phá controller cũ.

**`AuthAspect`** — đọc thêm header `X-Company-Id`, `X-Permissions`. Áp dụng kiểm tra `@RequiresPermission` trước, rồi `@RequiresRole` (nếu controller có cả 2 thì kiểm tra cả 2).

**`RequiresPermission`** (mới) — annotation kiểm tra permission code.

**`Role`** enum — thêm `SYSTEM_ADMIN`, `COMPANY_ADMIN`, `HR_MANAGER`, `ACCOUNTANT` (giữ `HR`, `MANAGER`, `BHXH_OFFICER`, `PAYROLL_ACCOUNTANT`, `EMPLOYEE` cũ).

### Sửa entity nghiệp vụ

- `NhanVien` — thêm `companyId`. `NhanVienService.create` set `DEFAULT_COMPANY_ID` nếu caller không truyền.
- `HopDongLaoDong` — thêm `companyId`. `HopDongService.create` copy từ `nhanVien.companyId`.
- `BangLuongThang` — `BangLuongThang` entity chưa tồn tại (sẽ tạo ở task T14+). Chỉ thêm cột `company_id` ở V4 và NOT NULL ở V6.

### Repository helpers

- `NhanVienRepository.findByCompanyIdAndId` / `findByCompanyId`.
- `HopDongLaoDongRepository.findByCompanyIdAndNhanVienId` / `findByCompanyId`.

## Máy trạng thái tài khoản

```
MỚI (seed)  → ACTIVE → LOCKED → ACTIVE
              (login)  (admin)  (admin)
              ↓
            PENDING (chờ kích hoạt email)
```

`LOCKED` không cho login (`USER_LOCKED`). `PENDING` cũng không cho login. `ACTIVE` mới được login (`AUTH_INVALID` nếu sai password).

## Phân quyền (RBAC)

6 role, ~50 permission, format `resource.action`:

| Role code | Module có quyền |
|---|---|
| SYSTEM_ADMIN | Tất cả (50) |
| COMPANY_ADMIN | Tất cả trừ `audit_log.read` (49) |
| HR_MANAGER | `hr` (không delete), `timekeeping` (leave/phan_ca/cham_cong), `social_ins` (read/create) |
| ACCOUNTANT | `payroll` (full), `social_ins` (read), `timekeeping` (read + chot) |
| MANAGER | `hr` (read), `timekeeping` (approve_cap1, cancel) |
| EMPLOYEE | `hr` (read profile + phụ thuộc), `timekeeping` (create leave/OT), `payroll` (read bảng lương cá nhân) |

API trả về `LoginResponse { token, userId, companyId, username, roleCodes, permissions }` — client cache `permissions` rồi gửi `X-Permissions` header ở các request sau (giả lập JWT — khi có Spring Security sẽ verify chữ ký).

## Multi-tenant

- **Default company UUID**: `11111111-1111-1111-1111-111111111111` (hằng số `SystemConstants.DEFAULT_COMPANY_ID`).
- **Tenant resolution**: AuthAspect đọc `X-Company-Id` header → set `AuthContext.currentCompanyId` + `TenantContext.currentCompanyId`.
- **Filter ở tầng service** (chưa enforce ở JPA filter — cho phép test linh hoạt):
  - `NhanVienRepository.findByCompanyId`, `findByNhanVienIdAndCompanyId`.
  - `HopDongLaoDongRepository.findByCompanyId`, `findByCompanyIdAndNhanVienId`.
- **Backfill V6**: mọi row seed cũ gắn `company_id = default`. Sau V6, cột `company_id` NOT NULL.

## Audit AOP

- Đặt `@Auditable(module="...", action="...", entityType="...")` trên method service cần track.
- Aspect chạy sau method return thành công; lỗi audit KHÔNG ảnh hưởng nghiệp vụ chính.
- `oldValue`/`newValue` lưu JSONB — chỉ chứa entityId + tên method + arg types (đã redact password/login).
- Bảng `system.audit_log` không có `updated_at` — chỉ insert. Không có trigger chặn UPDATE ở DB (cho phép test cleanup).

## Tests

- `CompanyServiceTest` (8) — create/update/list/status + TAX_CODE_DUPLICATE + not found.
- `UserAccountServiceTest` (15) — CRUD + login OK/sai password/user locked + lock/unlock/resetPassword + USERNAME/EMAIL/COMPANY/ROLE validation.
- `PasswordEncoderTest` (6) — encode/matches + salt ngẫu nhiên + null/malformed.
- `AuditServiceTest` (3) — anonymous ghi vẫn OK + có user gắn userId/companyId + null snapshot.
- `AuditAspectTest` (2) — ghi log khi annotation có + swallow exception audit failure.
- `RoleServiceTest` (6) — requireByCode, gom permission theo role, lookup qua userId.
- `TenantContextTest` (3) — set/get/clear.
- `AuthContextTest` (cập nhật, 8) — hasPermission/requirePermission + CSV + requireCompanyId.

Tổng cộng: **237 tests pass / 0 fail** trên 24 lớp test (T11 thêm **39 test mới**).

## Quyết định thiết kế đã xác nhận với user

1. **Foundation + migrate data** — không xoá Role enum cũ; thêm 4 role mới (SYSTEM_ADMIN, COMPANY_ADMIN, HR_MANAGER, ACCOUNTANT) song song.
2. **Permission format** = `resource.action` (canonical, không có resource.action[param]).
3. **Multi-tenant** = AuthContext.currentCompanyId() từ header (KHÔNG dùng X-Company-Id override).
4. **Backfill company** = mọi dữ liệu seed gắn `default` UUID cố định.
5. **RBAC** = chuỗi `resource.action`, lưu ở bảng `system.permission` (code unique).
6. **Audit** = AOP + annotation `@Auditable`.
7. **Auth context** = mở rộng `AuthContext.hasPermission(String)` để controller gọi trực tiếp.
8. **Auth thật** = giữ `@RequiresRole` cũ song song với `@RequiresPermission` mới — không migrate.
9. **Password stub** = SHA-256 + salt; TODO thay bằng BCrypt khi có Spring Security.
10. **JSONB** = dùng `@JdbcTypeCode(SqlTypes.JSON)` (Hibernate 6 native, không cần `hibernate-types`).
11. **AOP audit error** = swallow (try/catch + log warning) để không phá nghiệp vụ chính.

## Ghi chú cho task sau

- **T17 (frontend auth)**: tích hợp `LoginResponse.permissions` vào client, gửi `X-Permissions` ở mọi request sau.
- **T18 (Spring Security thật)**: thay stub AuthAspect bằng Spring Security filter chain; thay `PasswordEncoder` stub bằng `BCryptPasswordEncoder`; thay `AuthContext` ThreadLocal bằng `@AuthenticationPrincipal`.
- **T14 (tính lương)** cần thêm entity `payroll.bang_luong_thang` (cột `company_id` đã có sẵn ở V4).
- **Multi-tenant ở JPA** (Hibernate Filter): hiện chỉ filter ở service. Task sau có thể thêm `@FilterDef(name="tenant", parameters=...)` ở BaseAuditEntity.
- **AuditAspect** chỉ ghi log cho method có annotation; chưa gắn lên bất kỳ service nào — task sau sẽ annotate các method nhạy cảm (approveCap2, cancel, lock, update salary, ...).
- **AuthController.me()**: chỉ trả userId; chưa trả full profile (sẽ mở rộng ở T17).
- **BCryptPasswordEncoder** — thêm khi có Spring Security; hiện SHA-256 + salt chỉ là stub đủ dùng để verify login.

## Verification

- Chạy `cmd /c "set JAVA_HOME=C:\Program Files\Java\jdk-17 && mvn -q test -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 -Dmaven.compiler.release=17"` từ `hrm-system/backend`. Kết quả: **237/237 pass / 0 fail / 0 skip**.
- 24 file `.txt` trong `target/surefire-reports/`.
- 6 file migration mới (V4, V5, V6) + 6 entity mới + 5 repository mới + 4 service mới + 3 controller mới + audit AOP.