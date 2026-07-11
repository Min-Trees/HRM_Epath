# T11 — Tổng hợp & chốt bảng công tháng (khóa)

- Nhóm: Chấm công
- Blocked by: T09, T10
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`timekeeping.bang_cong_thang` + trigger `prevent_edit_locked_bangcong`)

## Mục tiêu
Tổng hợp toàn bộ dữ liệu công/phép/OT thành bảng công tháng và chốt (khóa) làm đầu vào duy nhất cho tính lương.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T11 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và bảng timekeeping.bang_cong_thang (kèm trigger prevent_edit_locked_bangcong) trong database/schema.sql. Chỉ làm trong package com.company.hrm.attendance. Dùng truy vấn tổng hợp do T09 và T10 cung cấp.

Nhiệm vụ: xây nghiệp vụ tổng hợp và chốt/khóa bảng công tháng.

Yêu cầu:
1. Entity BangCongThang ánh xạ timekeeping.bang_cong_thang: thang, nam, so_ngay_cong_thuc_te, so_gio_ot_150/200/300, so_ngay_phep_nam, so_ngay_nghi_om, so_ngay_nghi_khong_luong, so_ngay_thieu_cong, trang_thai (MO, DA_CHOT, DA_HUY_CHOT), chot_boi, chot_luc. UNIQUE (nhan_vien_id, thang, nam).
2. Service BangCongService:
   - Tổng hợp (generate/refresh) bảng công cho 1 nhân viên hoặc toàn bộ theo tháng/năm: lấy số ngày công thực tế và giờ OT đã duyệt (từ T09/T10), quy giờ OT về 3 nhóm hệ số (150/200/300), đếm ngày phép năm/ốm/nghỉ không lương/thiếu công. Chỉ tổng hợp lại được khi trạng thái MO.
   - Chốt bảng công (MO → DA_CHOT): kiểm tra không còn ngoại lệ chấm công chưa xử lý và không còn đơn nghỉ/OT đang CHO_DUYET trong tháng; đặt chot_boi, chot_luc. Sau khi DA_CHOT, cấm mọi cập nhật số liệu (tôn trọng trigger DB; ở tầng service cũng chặn và ném BusinessException).
   - Hủy chốt (DA_CHOT → DA_HUY_CHOT) chỉ cho vai trò HR có lý do, ghi audit; sau khi hủy chốt phải tổng hợp lại rồi chốt lại. Không cho hủy chốt nếu đã có bảng lương tham chiếu (phối hợp T15: nếu payroll đã dùng, chặn).
   - Idempotent: chốt lại bảng đã DA_CHOT không đổi dữ liệu, trả về trạng thái hiện tại.
3. Controller /api/v1/attendance:
   - POST /timesheets/generate?month=&year=&employeeId= (bỏ employeeId để chạy toàn bộ), GET /timesheets?month=&year=&status=, GET /timesheets/{id}.
   - POST /timesheets/{id}/lock (chốt), POST /timesheets/{id}/unlock (hủy chốt, chỉ HR).
   - Bảo vệ vai trò HR.
4. Cung cấp API/service method để T15 lấy bảng công DA_CHOT theo nhân viên/tháng; nếu chưa chốt phải để T15 từ chối tính lương.

Tiêu chí hoàn thành:
- Test: tổng hợp đúng số liệu từ chấm công/phép/OT; chặn chốt khi còn ngoại lệ/đơn chờ duyệt; sau DA_CHOT không sửa được (ném BusinessException); hủy chốt cần HR và ghi audit; chốt idempotent.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, máy trạng thái bảng công, điều kiện chốt, method cung cấp cho T15.
```
