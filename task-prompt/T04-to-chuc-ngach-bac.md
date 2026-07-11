# T04 — Cơ cấu tổ chức & ngạch bậc lương

- Nhóm: Core HR
- Blocked by: T02, T03
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (bảng `hr.phong_ban`, `hr.ngach_bac_luong`)

## Mục tiêu
Quản lý cây phòng ban đa cấp, định biên và khung ngạch/bậc lương — nền tổ chức cho hồ sơ nhân viên.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T04 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và phần bảng hr.phong_ban, hr.ngach_bac_luong trong database/schema.sql. Chỉ làm trong package com.company.hrm.hr (phần tổ chức) và test tương ứng.

Nhiệm vụ: xây API quản lý cơ cấu tổ chức và ngạch/bậc lương.

Yêu cầu:
1. Entity PhongBan ánh xạ hr.phong_ban (có quan hệ tự tham chiếu phong_ban_cha_id, trường truong_bo_phan_id, dinh_bien, cap_do, active). Entity NgachBacLuong ánh xạ hr.ngach_bac_luong.
2. Repository (Spring Data JPA) cho cả hai.
3. Service PhongBanService:
   - CRUD phòng ban; khi tạo/di chuyển phải tính lại cap_do theo cha; chặn tạo vòng lặp (một phòng ban không thể là con cháu của chính nó).
   - Trả cây tổ chức (dạng cây lồng nhau) và danh sách phẳng có đường dẫn.
   - Không cho xóa cứng phòng ban còn nhân viên hoặc còn phòng ban con; dùng active=false (đóng) thay cho xóa.
4. Service NgachBacLuongService: CRUD, không cho trùng ma_ngach, đóng bằng active=false.
5. Controller REST /api/v1/hr:
   - GET /departments (cây hoặc phẳng qua query ?view=tree|flat), GET /departments/{id}, POST, PUT, PATCH /departments/{id}/close.
   - GET/POST/PUT /salary-grades.
   - Gán trưởng bộ phận: PUT /departments/{id}/manager (nhận nhan_vien_id) — cho phép để trống ở giai đoạn này nếu chưa có nhân viên; validate khi T05 sẵn sàng.
   - Bảo vệ ghi bằng vai trò HR (dùng cơ chế @RequiresRole của T03).
6. DTO request/response tách khỏi entity, có validation (mã, tên bắt buộc; dinh_bien >= 0).

Tiêu chí hoàn thành:
- Test: tạo cây 3 cấp, chặn vòng lặp, chặn đóng phòng ban còn con; CRUD ngạch bậc.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế môi trường).
- Bàn giao: endpoint, quy tắc cây tổ chức, và cách frontend lấy cây.
```
