# Bản đồ task — Hệ thống HRM (Nhân sự · Chấm công · BHXH · Tính lương)

Mỗi file trong thư mục này là một task độc lập, chứa sẵn một prompt hoàn chỉnh để giao cho một agent (chạy bằng model **fable**) thực thi. Agent chỉ cần đọc đúng file task của mình cộng với hai file nền (`00-CONVENTIONS.md`, `../database/schema.sql`) là đủ ngữ cảnh để làm việc.

> Trạng thái: đã có đủ prompt cho toàn bộ 22 task (T01–T22).

## Cách dùng

1. Đọc `00-CONVENTIONS.md` trước — quy ước chung mọi task phải tuân theo.
2. Giao từng file `Txx-*.md` cho một agent fable. Nội dung bên trong ô `PROMPT` chính là prompt truyền vào agent.
3. Tôn trọng thứ tự phụ thuộc bên dưới: không khởi động task khi task chặn nó (Blocked by) chưa xong.
4. Sau mỗi task, agent phải chạy verify (build/test) theo phần "Tiêu chí hoàn thành" của task đó.

## Stack cố định

Backend Spring Boot 3 (Java 21, Maven) · Frontend React + TypeScript (Vite) · Database PostgreSQL 15+. DDL nguồn: `../database/schema.sql`.

## Danh sách task và phụ thuộc

| Task | Tên | Nhóm | Blocked by |
|------|-----|------|-----------|
| T01 | Khởi tạo monorepo & công cụ build | Nền tảng | — |
| T02 | Thiết lập database & migration (Flyway) | Nền tảng | T01 |
| T03 | Lớp chung backend (lỗi, phân trang, audit, bảo mật cơ bản) | Nền tảng | T01 |
| T04 | Cơ cấu tổ chức & ngạch bậc lương | Core HR | T02, T03 |
| T05 | Hồ sơ nhân viên & người phụ thuộc | Core HR | T04 |
| T06 | Hợp đồng lao động & cảnh báo hết hạn | Core HR | T05 |
| T07 | Biến động nhân sự (append-only) & trạng thái phái sinh | Core HR | T05, T06 |
| T08 | Ca làm việc & phân ca | Chấm công | T05 |
| T09 | Thu thập chấm công & xử lý ngoại lệ | Chấm công | T08 |
| T10 | Nghỉ phép & tăng ca (duyệt 2 cấp) | Chấm công | T05 |
| T11 | Tổng hợp & chốt bảng công tháng (khóa) | Chấm công | T09, T10 |
| T12 | Quá trình tham gia BHXH & mức đóng | BHXH | T05 |
| T13 | Báo tăng/giảm & chế độ hưởng BHXH | BHXH | T07, T12 |
| T14 | Tham số lương & bậc thuế TNCN | Tính lương | T03 |
| T15 | Engine tính lương (từ bảng công chốt + BHXH) | Tính lương | T11, T13, T14 |
| T16 | Duyệt bảng lương, chi trả & phiếu lương | Tính lương | T15 |
| T17 | Khung frontend (routing, layout, API client, auth mock) | Frontend | T03 |
| T18 | Màn hình Core HR (nhân viên, hợp đồng, biến động) | Frontend | T17, T07 |
| T19 | Màn hình Chấm công (phân ca, ngoại lệ, phép/OT, bảng công) | Frontend | T17, T11 |
| T20 | Màn hình BHXH (quá trình, báo tăng/giảm, chế độ) | Frontend | T17, T13 |
| T21 | Màn hình Tính lương (bảng lương, phiếu lương, duyệt) | Frontend | T17, T16 |
| T22 | Kiểm thử tích hợp end-to-end & seed dữ liệu demo | QA | T16, T21 |

## Sơ đồ phụ thuộc (rút gọn)

```
T01 ─┬─ T02 ─┬─ T04 ── T05 ─┬─ T06 ── T07 ─┬────────────── T13 ─┐
     │       │              │              │                    │
     ├─ T03  │              ├─ T08 ── T09 ─┤                    │
     │       │              ├─ T10 ────────┤                    │
     │       │              └─ T12 ────────┴─ T11 ──┐           │
     │       └──────────────────────────────────────┤           │
     │                                        T14 ──┴─ T15 ── T16 ─┐
     └─ T03 ── T17 ─┬─ T18 (cần T07)                                │
                    ├─ T19 (cần T11)                                │
                    ├─ T20 (cần T13)                                │
                    └─ T21 (cần T16) ──────────────────────── T22 ─┘
```

## Gợi ý song song hóa

- Đợt 1: T01.
- Đợt 2 (song song): T02, T03.
- Đợt 3 (song song sau T02+T03): T04, T14, T17.
- Đợt 4: T05 → rồi song song T06, T08, T10, T12.
- Đợt 5: T07, T09, T11, T13, và các màn hình frontend tương ứng khi backend sẵn sàng.
- Đợt 6: T15 → T16 → T21 → T22.
