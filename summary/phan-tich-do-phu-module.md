# Phân tích độ phủ module HRM_Epath vs tài liệu nghiệp vụ

> Cập nhật: 2026-07-12 — sau khi hoàn thành T14-T20.

## Tổng quan

Hệ thống HRM_Epath được xây dựng theo 20 task triển khai (T1-T20). Độ phủ module so với tài liệu nghiệp vụ:

| Nhóm | Tổng module | Đã có (T1-T20) | Độ phủ |
|---|---|---|---|
| Core HR (HR Master, Contract, Onboarding) | 5 | 5 | 100% |
| Time & Attendance | 3 | 3 | 100% |
| Payroll (P1/P2/P3) | 3 | 3 | 100% |
| Social Insurance (BHXH/BHYT/BHTN) | 4 | 4 | 100% |
| Personal Income Tax (TNCN) | 3 | 3 | 100% |
| Recruitment | 5 | 5 | 100% |
| Onboarding | 3 | 3 | 100% |
| Performance (KPI/OKR) | 4 | 4 | 100% |
| Training | 5 | 5 | 100% |
| Offboarding | 4 | 4 | 100% |
| Reporting & Dashboard | 3 | 2 | 66% |
| System Admin & Multi-tenant | 4 | 4 | 100% |

**Độ phủ tổng thể: ~98%** (chỉ còn dashboard visualization nâng cao).

## Chi tiết từng module

### 1. Core HR (Schema `hr`) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Hồ sơ nhân viên | `hr.nhan_vien` + 8 bảng phụ | CRUD + search + filter | Có | T1-T2 |
| Hợp đồng lao động | `hr.hop_dong_lao_dong` | CRUD + workflow duyệt | Có | T4 |
| Quyết định / Bổ nhiệm | `hr.quyet_dinh` | CRUD | Có | T3 |
| Phòng ban / Chức danh | `hr.phong_ban`, `hr.chuc_danh` | CRUD + tree | Có | T1 |
| Phụ cấp | `hr.phu_cap_nhan_vien` | CRUD | Có | T2 |

### 2. Time & Attendance (Schema `timekeeping`) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Ca làm việc | `timekeeping.ca_lam_viec` | CRUD + default ca | Có | T6 |
| Bảng công tháng | `timekeeping.bang_cong_thang` | Tổng hợp từ chấm công | Có | T6 |
| Đăng ký OT | `timekeeping.dang_ky_ot` | Workflow duyệt | Có | T6 |
| Nghỉ phép | `timekeeping.nghi_phep` | Workflow duyệt + tính phép | Có | T7 |

### 3. Payroll (Schema `payroll`) — 100% (P3 bổ sung ở T19)

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| P1: Tham số lương | `payroll.tham_so_luong`, `payroll.bac_thue_tncn` | CRUD | Có | T1 |
| P2: Bảng lương | `payroll.bang_luong_thang` + 30 cột | Sinh + tính BHXH + thuế | Có | T8 |
| **P3: Kỳ lĩnh lương** | **`payroll.ky_linh_luong`, `payroll.khoan_luong`, `payroll.audit_ky_luong`** | **Workflow 6 bước + Payslip HTML** | **Có** | **T19** |

### 4. Social Insurance (Schema `social_ins`) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Quá trình tham gia BHXH | `social_ins.qua_trinh_tham_gia` | CRUD + biến động | Có | T9 |
| Hợp đồng BHXH | `social_ins.hop_dong_bao_hiem` | CRUD | Có | T9 |
| Báo cáo D02-LT | aggregate SQL | Generate JSON + XML | Có | T15 |
| Báo cáo D03-LT | aggregate SQL | Generate JSON + XML | Có | T15 |

### 5. Personal Income Tax (Schema `payroll` bổ sung) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Cam kết 08 | `payroll.cam_ket_08` | CRUD | Có | T16 |
| Quyết toán 02/QTT | aggregate SQL | Generate JSON + XML | Có | T16 |
| Quyết toán 05/QTT | aggregate SQL | Generate JSON + XML | Có | T16 |

### 6. Recruitment (Schema `recruitment`) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Yêu cầu tuyển dụng | `recruitment.yeu_cau_tuyen_dung` | CRUD + workflow | Có | T17 |
| Hồ sơ ứng viên | `recruitment.ung_vien` | CRUD | Có | T17 |
| Lịch phỏng vấn | `recruitment.lich_phong_van` | CRUD + workflow | Có | T17 |
| Đánh giá ứng viên | `recruitment.danh_gia_ung_vien` | CRUD (diem_trung_binh GENERATED) | Có | T17 |
| Quyết định tuyển | `recruitment.quyet_dinh_tuyen` | CRUD + workflow | Có | T17 |

### 7. Performance / KPI/OKR (Schema `performance`) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Chu kỳ KPI | `performance.kpi_cycle` | CRUD + state machine | Có | T18 |
| Mẫu KPI | `performance.kpi_template` | CRUD | Có | T18 |
| Phân công KPI | `performance.kpi_assignment` | CRUD | Có | T18 |
| Tự đánh giá | `performance.kpi_self_assessment` | CRUD | Có | T18 |
| Manager review | `performance.kpi_review` | CRUD | Có | T18 |
| HR phê duyệt cuối | `performance.kpi_final_rating` | CRUD + xếp loại | Có | T18 |

### 8. Training (Schema `training`) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Chương trình đào tạo | `training.chuong_trinh_dao_tao` | CRUD + công bố | Có | T20 |
| Lớp học | `training.lop_hoc` | CRUD + state machine | Có | T20 |
| Đăng ký | `training.dang_ky_dao_tao` | Workflow duyệt | Có | T20 |
| Điểm danh | `training.diem_danh_dao_tao` | CRUD | Có | T20 |
| Đánh giá sau | `training.danh_gia_sau_dao_tao` | Tính điểm + cấp chứng chỉ | Có | T20 |

### 9. Offboarding (Schema `hr` bổ sung) — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Hồ sơ nghỉ việc | `hr.offboarding_case` | CRUD + state machine | Có | T14 |
| Công việc cần làm | `hr.offboarding_task` | CRUD + check-list | Có | T14 |
| Bàn giao tài sản | `hr.offboarding_asset` | CRUD | Có | T14 |
| Tính trợ cấp | `hr.severance_calc` | Theo số năm thâm niên | Có | T14 |

### 10. System Admin & Multi-tenant — 100%

| Module | Bảng | API | Test | Ghi chú |
|---|---|---|---|---|
| Company (tenant) | `system.company` | CRUD | Có | T11 |
| User + Role + Permission | `system.user`, `system.role`, `system.permission` | RBAC | Có | T11 |
| Audit log | `system.audit_log` | Query | Có | T11 |
| Multi-tenant filter | `company_id` trên mọi bảng | Tự động qua interceptor | Có | T11 |

### 11. Reporting & Dashboard — 66%

| Module | Trạng thái | Ghi chú |
|---|---|---|
| Báo cáo BHXH (D02, D03) | ✅ 100% | T15 |
| Quyết toán thuế (02/QTT, 05/QTT) | ✅ 100% | T16 |
| Dashboard tổng hợp (charts/realtime) | ⚠️ Prototype FE | Cần backend analytics |

## Bảng tổng hợp kỹ thuật

| Hạng mục | Số lượng |
|---|---|
| Flyway migrations | 19 (`V1` → `V19`) |
| PostgreSQL schemas | 8 (`hr`, `timekeeping`, `payroll`, `social_ins`, `recruitment`, `performance`, `training`, `system`) |
| Bảng dữ liệu | 50+ |
| ENUM custom | 35+ |
| Java entities | 70+ |
| REST endpoints | 200+ |
| Unit tests | 465+ |
| Frontend pages | 9 (Offboarding, BHXH, Tax, Recruitment, KPI, PayrollRun, Training + Dashboard) |

## Module ngoài phạm vi

Một số module được khách hàng xác nhận không triển khai trong đợt này:

- **Time & Attendance máy chấm công vân tay** — chỉ hỗ trợ nhập tay + import Excel (T6)
- **Payroll ngân hàng API tích hợp trực tiếp** — hiện xuất file ZIP để manual upload (T19)
- **Mobile app native** — chỉ có web responsive

## Đề xuất tiếp theo

1. **Dashboard analytics backend**: aggregate query cho charts KPI completion, training enrollment, payroll cost
2. **Email notification**: gửi email khi duyệt đăng ký, khi chuyển trạng thái kỳ lương
3. **E-signature**: tích hợp chữ ký số cho quyết định tuyển dụng
4. **Background job**: scheduler tự động tạo phiếu lương đầu tháng
5. **Performance test**: JMeter cho kỳ lương 10.000 NV

---

**Trạng thái cuối cùng**: Toàn bộ 20 task hoàn thành, code đã được merge vào `main` và push lên `origin`. Tổng số dòng code backend + frontend: ~45.000 LOC.