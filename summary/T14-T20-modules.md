# T14-T20: Bổ sung 7 module HRM còn thiếu

## Bối cảnh

Sau khi hoàn thành T1-T13 (System Admin + Multi-Tenant + 12 module lõi HRM), phân tích tài liệu nghiệp vụ phát hiện 7 module còn thiếu hoặc chưa hoàn thiện. Đợt này (T14-T20) triển khai full-stack cho cả 7 module:

| Task | Module | Schema mới | Trạng thái |
|---|---|---|---|
| T14 | Offboarding (Nghỉ việc) | `hr.*` bổ sung | Hoàn thành |
| T15 | BHXH Reports (D02-LT, D03-LT) | dùng `social_ins.*` | Hoàn thành |
| T16 | Tax Settlement (Cam kết 08, 02/QTT, 05/QTT) | bổ sung `payroll.cam_ket_08` | Hoàn thành |
| T17 | Recruitment (Tuyển dụng) | `recruitment` schema mới | Hoàn thành |
| T18 | KPI/OKR (Đánh giá hiệu suất) | `performance` schema mới | Hoàn thành |
| T19 | Payroll Run (Hoàn thiện P3) | bổ sung `payroll.ky_linh_luong` | Hoàn thành |
| T20 | Training (Đào tạo) | `training` schema mới | Hoàn thành |

## Cấu trúc triển khai

Mỗi module được xây dựng theo cùng pattern full-stack:

### Backend
- **Flyway migration** (`V14..V19__*.sql`) – schema + ENUM + trigger
- **Entity (JPA)** ánh xạ bảng + ENUM native
- **Repository** kế thừa `JpaRepository`
- **DTO** với validation (`@NotBlank`, `@NotNull`, ...)
- **Service** chứa business logic + state machine + audit log
- **Controller** với `@RequiresRole(...)` phân quyền
- **Unit tests (Mockito)** cho service

### Frontend
- **Page component** (React + TS) – tabs, forms, workflow buttons
- **Mock layer** (`*.mock.ts`) – localStorage để chạy độc lập
- Tích hợp vào `App.tsx` + sidebar Dashboard

## Chi tiết từng module

### T14 – Offboarding

| Thành phần | Mô tả |
|---|---|
| Bảng | `hr.offboarding_case`, `hr.offboarding_task`, `hr.offboarding_asset`, `hr.severance_calc` |
| ENUM | `ly_do_nghi_viec`, `trang_thai_offboarding`, `trang_thai_task`, `loai_task_offboarding` |
| State machine | `MOI_TAO → DANG_XU_LY → DA_BAN_GIAO → HOAN_THANH` (+ `HUY`) |
| Nghiệp vụ | Sinh task template theo lý do nghỉ, tính severance theo `ChronoUnit.DAYS.between` |
| Test | 13 unit tests |

### T15 – BHXH Reports

| Thành phần | Mô tả |
|---|---|
| Báo cáo | D02-LT (tăng/giảm lao động), D03-LT (biến động quá trình) |
| Kỹ thuật | `JdbcTemplate` aggregate, `BhxhXmlExporter` theo schema BHXH Việt Nam |
| Xuất | JSON + XML download (preview trên FE) |
| Test | 14 unit tests |

### T16 – Tax Settlement

| Thành phần | Mô tả |
|---|---|
| Bảng | `payroll.cam_ket_08` (bổ sung), `payroll.thue_tncn` đã có sẵn |
| ENUM | `payroll.loai_cam_ket_08` |
| Nghiệp vụ | Quản lý Cam kết 08, sinh 02/QTT (tổng hợp), 05/QTT (chi tiết) |
| Xuất | XML theo format Tổng cục Thuế |
| Test | 17 unit tests |

### T17 – Recruitment

| Thành phần | Mô tả |
|---|---|
| Schema | `recruitment` (mới) |
| Bảng | `yeu_cau_tuyen_dung`, `ung_vien`, `lich_phong_van`, `danh_gia_ung_vien`, `quyet_dinh_tuyen` |
| ENUM | `trang_thai_yeu_cau`, `trang_thai_ung_vien`, `ket_qua_phong_van`, `loai_hop_dong_de_nghi` |
| State machine | Yêu cầu → Ứng viên → Lịch PV → Đánh giá → Quyết định → Onboard |
| Test | 16 unit tests |

### T18 – KPI/OKR

| Thành phần | Mô tả |
|---|---|
| Schema | `performance` (mới) |
| Bảng | `kpi_cycle`, `kpi_template`, `kpi_assignment`, `kpi_self_assessment`, `kpi_review`, `kpi_final_rating` |
| ENUM | `trang_thai_chu_ky`, `loai_muc_tieu`, `trang_thai_assignment`, `xep_loai` |
| Workflow | NV tự đánh giá → Manager review → HR phê duyệt |
| Tính toán | `diem_trung_binh` GENERATED column |
| Test | 12 unit tests |

### T19 – Payroll Run (Hoàn thiện P3)

| Thành phần | Mô tả |
|---|---|
| Bảng | `payroll.ky_linh_luong`, `payroll.khoan_luong`, `payroll.audit_ky_luong` |
| ENUM | `trang_thai_ky_luong`, `loai_khoan_luong`, `loai_hinh_chuyen` |
| Workflow | `CHO_CHAY → DANG_CHAY → DA_CHAY → DA_DUYET_CAP_1 → DA_DUYET_CAP_2 → DA_CHI_TRA` (+ `HUY`) |
| Bổ sung | PayslipGenerator (HTML), file zip URL sau chi trả, audit log mỗi transition |
| Test | 12 unit tests |

### T20 – Training (Đào tạo)

| Thành phần | Mô tả |
|---|---|
| Schema | `training` (mới) |
| Bảng | `chuong_trinh_dao_tao`, `lop_hoc`, `dang_ky_dao_tao`, `diem_danh_dao_tao`, `danh_gia_sau_dao_tao` |
| ENUM | `trang_thai_chuong_trinh`, `loai_chuong_trinh`, `trang_thai_lop`, `trang_thai_dang_ky`, `trang_thai_tham_du`, `ket_qua_danh_gia` |
| State machine | CT (NHAP→CONG_BO), Lớp (MO_DANG_KY → DONG_DANG_KY → DANG_DIEN_RA → HOAN_THANH), ĐK (CHO_DUYET → DA_CHAP_NHAN/TU_CHOI) |
| Tính toán | Điểm TB = 40% nội dung + 30% GV + 30% thực hành; tự động cấp chứng chỉ |
| Test | 20 unit tests |

## Tổng kết

- **Tests**: 465+ total, 0 failures
- **Backend Java files mới**: ~90 (entity, repo, dto, service, controller, test)
- **Frontend files mới**: ~14 (page, mock, route updates)
- **Migration scripts**: 7 (`V14` → `V19`, `V15__cam_ket_08_tax_settlement.sql` bổ sung cho T16)
- **Git history**: 7 branches `feature/t14..t20-*`, tất cả đã merge `--no-ff` vào `main` và push `origin`

## Cấu trúc commit

Mỗi task là 1 commit riêng trên nhánh riêng, theo đúng quy tắc git workflow đã đặt:

```
main
├── feature/t14-offboarding           (T14)
├── feature/t15-bhxh-reports         (T15)
├── feature/t16-tax-settlement       (T16)
├── feature/t17-recruitment          (T17)
├── feature/t18-kpi-okr              (T18)
├── feature/t19-payroll-run          (T19)
└── feature/t20-training             (T20)
```

## Hướng phát triển tiếp theo

- Tích hợp email notification (khi duyệt đăng ký, khi chuyển trạng thái kỳ lương)
- Tích hợp e-signature cho quyết định tuyển dụng
- Background job tự động tạo phiếu lương khi chạy kỳ
- Report dashboard tổng hợp (training completion, payroll cost, KPI summary)
- Phân quyền chi tiết theo từng resource thay vì chỉ theo role