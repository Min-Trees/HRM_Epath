# Hệ thống HRM

Quản lý nhân sự · Chấm công · BHXH · Tính lương.

## Yêu cầu môi trường

- JDK 21
- Maven 3.9+
- Node.js 20+ và npm 10+
- PostgreSQL 15+

## Cấu trúc

```
hrm-system/
├── database/      # DDL nguồn + migration Flyway (thêm ở T02)
├── backend/       # Spring Boot 3 (Java 21, Maven)
└── frontend/      # React + TypeScript + Vite
```

## Chạy backend

```bash
cd hrm-system/backend
# Tạo DB (Postgres 15+)
createdb hrm
# Biến môi trường tùy chọn; mặc định trỏ localhost:5432/hrm
export DB_URL=jdbc:postgresql://localhost:5432/hrm
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
mvn spring-boot:run
```

Backend mặc định chạy ở `http://localhost:8080`.

## Chạy frontend

```bash
cd hrm-system/frontend
npm install
npm run dev      # chế độ phát triển ở http://localhost:5173
npm run build    # build production ra dist/
```

Proxy `/api` → `http://localhost:8080` đã cấu hình trong `vite.config.ts`.

## Công cụ build

- Backend: `mvn -q test` (test), `mvn spring-boot:run` (dev), `mvn package` (jar)
- Frontend: `npm run build` (type-check + vite build), `npm run dev` (HMR)