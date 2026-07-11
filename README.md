# HRM_Epath

Hệ thống Quản trị Nhân sự (HRM) cho doanh nghiệp — phát triển theo từng task (T01–T…), Spring Boot 3 + Java 17 + PostgreSQL + Flyway.

## Cấu trúc

```
HRM_Epath/
├── hrm_doc.md              # Đặc tả tổng thể HRM
├── bosung.md               # Phần bổ sung (system admin, đa tenant, …)
├── task-prompt/            # Prompt từng task (T01.md … Tnn.md)
├── summary/                # Tổng kết sau mỗi task
├── database/               # SQL tham khảo, dữ liệu mẫu
└── hrm-system/
    └── backend/            # Spring Boot module (Java 17, Maven)
```

## Tech stack

| Layer | Công nghệ |
|---|---|
| Ngôn ngữ | Java 17 |
| Build | Maven (wrapper) |
| Framework | Spring Boot 3.x (web, data-jpa, validation, security) |
| DB | PostgreSQL 15+, schema-per-domain (`hr`, `attendance`, `social_ins`, `payroll`, `system`, …) |
| Migration | Flyway |
| Test | JUnit 5, Mockito |
| Logging | SLF4J + Logback |

## Chạy nhanh

```bash
# Yêu cầu JDK 17 + Maven
cd hrm-system/backend

# Test (không cần DB)
set JAVA_HOME=C:\Program Files\Java\jdk-17
mvn test

# Chạy app (cần Postgres ở localhost:5432/hr, user/pass theo application.yml)
mvn spring-boot:run
```

## Quy ước làm việc

- **Mỗi task = 1 nhánh git**. Ví dụ: T11 → `feature/t11-system-admin-multi-tenant`.
- **Done task = merge vào `main` + push origin**.
- Commit message theo Conventional Commits (`feat: …`, `chore: …`, `docs: …`).
- Xem chi tiết ở `.cursor/rules/git-workflow.mdc`.

## Trạng thái các task

| Task | Module | Trạng thái |
|---|---|---|
| T01 | (nền tảng) | done |
| T02 – T09 | hr, attendance | done |
| T10 | nghiệp vụ cuối | done |
| **T11 (bổ sung)** | **system admin + multi-tenant** | **done** |
| T12+ | tiếp theo | theo `task-prompt/` |
