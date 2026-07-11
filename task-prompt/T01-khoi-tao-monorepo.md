# T01 — Khởi tạo monorepo & công cụ build

- Nhóm: Nền tảng
- Blocked by: —
- Đầu vào cần đọc: `00-CONVENTIONS.md`

## Mục tiêu
Dựng khung repo cho cả backend và frontend, chuẩn hóa công cụ build, để các task sau chỉ việc thêm code.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T01 của dự án HRM. Đọc file task-prompt/00-CONVENTIONS.md trước khi bắt đầu và tuân thủ mọi quy ước trong đó. Chỉ làm trong phạm vi task này.

Nhiệm vụ: khởi tạo cấu trúc monorepo hrm-system với hai dự án con backend (Spring Boot 3, Java 21, Maven) và frontend (React + TypeScript + Vite), cùng công cụ build và tài liệu chạy.

Yêu cầu backend:
- Tạo backend/pom.xml: Spring Boot 3.3.x parent, Java 21, dependencies: spring-boot-starter-web, spring-boot-starter-validation, spring-boot-starter-data-jpa, postgresql driver (runtime), flyway-core, spring-boot-starter-test, và testcontainers postgresql (test scope). Cấu hình spring-boot-maven-plugin.
- Tạo lớp khởi động com.company.hrm.HrmApplication với @SpringBootApplication.
- Tạo backend/src/main/resources/application.yml: cấu hình datasource PostgreSQL đọc từ biến môi trường (URL, user, pass) với default localhost:5432/hrm; JPA ddl-auto=validate; Flyway enabled; server.port=8080. Thêm profile "local".
- Tạo .gitignore phù hợp (target/, node_modules/, .env...).

Yêu cầu frontend:
- Khởi tạo frontend bằng cấu hình Vite + React + TypeScript: package.json (scripts dev/build/preview/lint), vite.config.ts, tsconfig*.json, index.html, src/main.tsx, src/App.tsx tối giản hiển thị "HRM" và một layout rỗng.
- Ghim phiên bản cụ thể cho react, react-dom, vite, typescript, @vitejs/plugin-react, @types/react, @types/react-dom.

Tài liệu:
- Tạo hrm-system/README dev-run tối thiểu nếu chưa có mục hướng dẫn chạy: lệnh chạy backend (mvn spring-boot:run) và frontend (npm install && npm run dev), yêu cầu JDK 21, Node 20+, PostgreSQL 15+.

Tiêu chí hoàn thành:
- Cấu trúc thư mục đúng như 00-CONVENTIONS.md.
- Chạy được `mvn -q -DskipTests package` (hoặc nêu rõ nếu môi trường thiếu Maven/JDK 21, kèm kết quả kiểm tra tĩnh: pom hợp lệ, cây thư mục đúng).
- `npm install && npm run build` chạy được (hoặc nêu rõ nếu registry npm bị chặn; khi đó kiểm tra tĩnh cấu hình).
- Ghi mục bàn giao: danh sách file tạo, lệnh verify đã chạy và kết quả.
```
