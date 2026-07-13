# HRM_Epath - Huong Dan Khoi Dong Nhanh

## Lan dau tien (First Run)

```powershell
cd "D:\LP & EP IT\HRM"
.\start-hrm.ps1 -FirstRun
```

Script se:
1. Drop + recreate database `hrm`
2. Chay Flyway migrations (V1–V19)
3. Seed du lieu mau toan bo modules
4. Seed 5 tai khoan (password = `123456`)
5. Khoi dong Backend (port 8080)
6. Khoi dong Frontend (port 5173)

## Lan sau (chi khoi dong)

```powershell
cd "D:\LP & EP IT\HRM"
.\start-hrm.ps1
```

## Chi khoi dong khong seed

```powershell
cd "D:\LP & EP IT\HRM"
.\start-hrm.ps1 -SkipSeed
```

## Xem trang thai

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:5173

## Tai khoan test

| Username | Password | Roles |
|----------|----------|-------|
| a.nguyen | 123456 | HR_MANAGER + HR |
| b.tran | 123456 | EMPLOYEE |
| c.le | 123456 | MANAGER |
| d.pham | 123456 | ACCOUNTANT + PAYROLL_ACCOUNTANT |
| e.hoang | 123456 | COMPANY_ADMIN + HR_MANAGER |

## Modules

| Module | URL | Task |
|--------|-----|------|
| Ho so nghi viec (Offboarding) | http://localhost:5173/#/offboarding | T14 |
| Bao cao BHXH D02/D03 | http://localhost:5173/#/bhxh/reports | T15 |
| Quyet toan thue TNCN | http://localhost:5173/#/tax/qtt | T16 |
| Tuyen dung | http://localhost:5173/#/recruitment | T17 |
| KPI/OKR | http://localhost:5173/#/performance/kpi | T18 |
| Payroll Run | http://localhost:5173/#/payroll/run | T19 |
| Dao tao | http://localhost:5173/#/training | T20 |

## Seed data hien co

- 5 nhan vien (NV001–NV005) + 1 NV da nghi (NV006)
- 5 phong ban (PGD, PNS, PKT, PIT, PKD)
- Bang cong thang 5/2026 (5 NV, trang thai DA_CHOT)
- Bang luong thang 5/2026 (5 NV, trang thai DA_DUYET)
- 2 ky linh luong (1 DA_CHI_TRA, 1 DA_DUYET_CAP_2)
- 5 qua trinh BHXH (DANG_DONG)
- 2 yeu cau tuyen dung (MOI_TAO, DANG_TUYEN)
- 1 chu ky KPI Q2/2026 voi 4 assignments
- 3 chuong trinh dao tao, 2 lop hoc, 6 dang ky
- 1 ho so offboarding NV006 (HOAN_THANH)

## Xu ly loi thuong gap

### Backend khong khoi dong
- Kiem tra PostgreSQL dang chay: `Get-Service postgresql*`
- Kiem tra log: `Get-Content $env:TEMP\hrm-backend-run.log -Tail 20`

### Frontend khong khoi dong
- Kiem tra port 5173: `Get-NetTCPConnection -LocalPort 5173`
- Kiem tra log: `Get-Content $env:TEMP\hrm-frontend.log`

### Login khong duoc
- Chay lai seed users: `node hrm-system\backend\seed-users.js`
- Hoac reset DB: `.\start-hrm.ps1 -FirstRun`

### Migration loi
- Xoa history: `psql -U postgres -d hrm -c "DELETE FROM flyway_schema_history WHERE version > '10';"`
- Reset DB: `.\start-hrm.ps1 -FirstRun`

## Cau hinh

| File | Muc dich |
|------|----------|
| `backend/src/main/resources/application.yml` | DB URL, credentials |
| `backend/src/main/resources/db/seed-demo-data.sql` | SQL seed data |
| `backend/seed-users.js` | Seed users (Node.js) |
| `frontend/vite.config.ts` | Proxy API, port |
| `frontend/src/mock/*.mock.ts` | Mock API cho frontend |

## Phat trien tiep

Frontend su dung mock API (localStorage). De ket noi backend that:
1. Bo comment hoac sua proxy trong `vite.config.ts`
2. Hoac implement thuc su cac API endpoints
