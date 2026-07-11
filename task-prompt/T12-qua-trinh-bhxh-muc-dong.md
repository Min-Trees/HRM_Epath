# T12 — Quá trình tham gia BHXH & mức đóng

- Nhóm: BHXH
- Blocked by: T05
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`social_ins.qua_trinh_tham_gia`, `social_ins.so_bhxh`)

## Mục tiêu
Quản lý quá trình tham gia BHXH/BHYT/BHTN theo giai đoạn, mức đóng, trần đóng và sổ BHXH.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T12 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và các bảng social_ins.qua_trinh_tham_gia, social_ins.so_bhxh trong database/schema.sql. Chỉ làm trong package com.company.hrm.bhxh.

Nhiệm vụ: xây nghiệp vụ quá trình tham gia BHXH, mức đóng và sổ BHXH.

Yêu cầu:
1. Entity QuaTrinhThamGia ánh xạ social_ins.qua_trinh_tham_gia: ma_so_bhxh, don_vi_tham_gia, muc_luong_dong, tu_ngay, den_ngay (null = đang áp dụng), ty_le_dong_dn (mặc định 21.5), ty_le_dong_nld (mặc định 10.5), tran_dong_bhxh_bhyt, tran_dong_bhtn. Entity SoBhxh ánh xạ social_ins.so_bhxh (ma_so_bhxh unique, da_chot_so, ngay_chot_so, tong_thoi_gian_da_dong_thang).
2. Ràng buộc: tại một thời điểm mỗi nhân viên chỉ có tối đa 1 giai đoạn đang áp dụng (den_ngay IS NULL) — tôn trọng unique index uq_qttg_dang_ap_dung. Khi mở giai đoạn mới phải đóng giai đoạn cũ (đặt den_ngay = ngày trước ngày hiệu lực mới).
3. Trần đóng: cung cấp tham số lương cơ sở và lương tối thiểu vùng (lấy từ tham_so_luong của T14 nếu sẵn, nếu chưa thì hằng số cấu hình tạm + ghi chú). Tính tran_dong_bhxh_bhyt = 20 × lương cơ sở, tran_dong_bhtn = 20 × lương tối thiểu vùng. Khi muc_luong_dong vượt trần, mức đóng thực tế bị giới hạn ở trần tương ứng — cung cấp method tính "mức đóng hợp lệ theo từng quỹ".
4. Service:
   - Mở/điều chỉnh/đóng giai đoạn tham gia; tính và lưu trần theo thời điểm hiệu lực.
   - Truy vấn quá trình theo nhân viên (sắp xếp tu_ngay giảm dần) và "giai đoạn hiệu lực tại một kỳ lương" (tháng/năm) — phục vụ T15.
   - Quản lý sổ BHXH: tạo/cấp mã số, chốt sổ khi nghỉ việc (đặt da_chot_so, tính tong_thoi_gian_da_dong_thang từ các giai đoạn).
5. Controller /api/v1/social-insurance:
   - POST /participations, PUT /participations/{id}, GET /employees/{id}/participations, GET /employees/{id}/participations/effective?month=&year=.
   - GET/POST /employees/{id}/insurance-book, POST /employees/{id}/insurance-book/close.
   - Bảo vệ vai trò BHXH_OFFICER (đọc cho HR/PAYROLL_ACCOUNTANT).
6. Cung cấp service method cho T15: lấy mức đóng hợp lệ (đã áp trần) và tỷ lệ NLĐ/DN tại kỳ lương; nếu không có giai đoạn hiệu lực → báo không hợp lệ để T15 từ chối tính lương.

Tiêu chí hoàn thành:
- Test: chỉ 1 giai đoạn đang áp dụng; mở giai đoạn mới đóng giai đoạn cũ; áp trần đúng khi vượt; lấy giai đoạn hiệu lực theo kỳ; chốt sổ tính đúng số tháng.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, quy tắc giai đoạn/trần đóng, method cung cấp mức đóng cho T15.
```
