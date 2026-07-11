# T13 — Báo tăng/giảm & chế độ hưởng BHXH

- Nhóm: BHXH
- Blocked by: T07, T12
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`social_ins.bhxh_bien_dong`, `social_ins.che_do_huong`, view `social_ins.v_bien_dong_qua_han`)

## Mục tiêu
Lập hồ sơ báo tăng/giảm lao động (append-only) theo sự kiện HR, và quản lý hồ sơ chế độ hưởng (ốm, thai sản, TNLĐ-BNN...).

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T13 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và các bảng social_ins.bhxh_bien_dong, social_ins.che_do_huong (kèm view v_bien_dong_qua_han) trong database/schema.sql. Chỉ làm trong package com.company.hrm.bhxh. Lắng nghe event do T07 phát.

Nhiệm vụ: xây nghiệp vụ báo tăng/giảm BHXH (append-only) và chế độ hưởng.

Yêu cầu:
1. Entity BhxhBienDong ánh xạ social_ins.bhxh_bien_dong: loai_bao (BAO_TANG, BAO_GIAM, DIEU_CHINH_MUC_DONG), ly_do (TUYEN_MOI, HET_THU_VIEC, DIEU_CHINH_LUONG, NGHI_VIEC, NGHI_KHONG_LUONG_14_NGAY, THAI_SAN, OM_DAI_NGAY, KHAC), ngay_phat_sinh, ngay_nop_ho_so, han_nop_ho_so, mau_to_khai (D02-LT, TK1-TS...), file_ho_so_url, da_nop. Entity CheDoHuong ánh xạ social_ins.che_do_huong: loai_che_do (OM_DAU, THAI_SAN, TAI_NAN_LAO_DONG_BENH_NGHE_NGHIEP, HUU_TRI, TU_TUAT), tu_ngay, den_ngay, so_ngay_huong, ho_so_y_te_url, so_tien_de_nghi, so_tien_duoc_chi_tra, trang_thai (MOI_TAO, DA_NOP_CO_QUAN_BHXH, DA_DUOC_CHI_TRA, TU_CHOI).
2. Repository biến động BHXH là APPEND-ONLY: chỉ INSERT + truy vấn; không update/delete (được phép cập nhật cờ da_nop/ngay_nop qua thao tác "đánh dấu đã nộp" riêng, ghi rõ là ngoại lệ có kiểm soát — hoặc mô hình hóa bằng bản ghi trạng thái nộp tách biệt). Ưu tiên giữ bản ghi biến động bất biến.
3. Tự động hóa theo event T07:
   - CHINH_THUC / ký HĐLĐ chính thức (HĐ ≥ 1 tháng) → tạo hồ sơ BAO_TANG (ly_do TUYEN_MOI/HET_THU_VIEC), tính han_nop_ho_so = ngay_phat_sinh + 30 ngày, gợi ý mau_to_khai.
   - CHAM_DUT_HDLD / NGHI_HUU → tạo BAO_GIAM (ly_do NGHI_VIEC) và kích hoạt chốt sổ (phối hợp T12).
   - DIEU_CHINH_LUONG → tạo DIEU_CHINH_MUC_DONG.
   - Nghỉ không lương ≥ 14 ngày làm việc/tháng, thai sản, ốm dài ngày → tạo BAO_GIAM với ly_do tương ứng (nhận tín hiệu từ chấm công/HR).
   Cho phép cả tạo thủ công.
4. Service:
   - Tạo/liệt kê hồ sơ biến động theo nhân viên; đánh dấu đã nộp (da_nop, ngay_nop_ho_so); truy vấn hồ sơ quá hạn nộp (dùng view v_bien_dong_qua_han).
   - Kết xuất tờ khai: sinh nội dung tờ khai theo mẫu (D02-LT, TK1-TS) ở dạng cấu trúc/CSV mô phỏng để nộp cổng BHXH (không cần định dạng cơ quan thật, ghi chú điểm tích hợp).
   - Chế độ hưởng: tạo hồ sơ chế độ với chứng từ; máy trạng thái MOI_TAO → DA_NOP_CO_QUAN_BHXH → DA_DUOC_CHI_TRA/TU_CHOI; ghi số tiền đề nghị và được chi trả.
5. Controller /api/v1/social-insurance:
   - POST /movements, GET /employees/{id}/movements, POST /movements/{id}/submit (đánh dấu đã nộp), GET /movements/overdue, GET /movements/{id}/declaration (kết xuất tờ khai).
   - POST /benefits, PUT /benefits/{id}/status, GET /employees/{id}/benefits.
   - Bảo vệ vai trò BHXH_OFFICER.

Ràng buộc bất biến:
- Bản ghi báo tăng/giảm không được xóa; lịch sử phải truy vết đầy đủ.

Tiêu chí hoàn thành:
- Test: event chính thức → sinh BAO_TANG đúng, han_nop = +30 ngày; nghỉ việc → BAO_GIAM + chốt sổ; truy vấn quá hạn; máy trạng thái chế độ; repository không expose update/delete bản ghi biến động.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, ánh xạ event→hồ sơ, mẫu tờ khai kết xuất, máy trạng thái chế độ.
```
