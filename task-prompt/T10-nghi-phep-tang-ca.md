# T10 — Nghỉ phép & tăng ca (duyệt 2 cấp)

- Nhóm: Chấm công
- Blocked by: T05
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`timekeeping.nghi_phep`, `timekeeping.dang_ky_ot`, `timekeeping.quy_phep_nam`)

## Mục tiêu
Xử lý đơn nghỉ phép và đăng ký tăng ca theo quy trình duyệt 2 cấp, quản lý quỹ phép năm theo thâm niên và hệ số OT theo luật.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T10 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và các bảng timekeeping.nghi_phep, timekeeping.dang_ky_ot, timekeeping.quy_phep_nam trong database/schema.sql. Chỉ làm trong package com.company.hrm.attendance.

Nhiệm vụ: xây nghiệp vụ nghỉ phép, tăng ca và quỹ phép năm, với duyệt 2 cấp.

Yêu cầu:
1. Entity NghiPhep ánh xạ timekeeping.nghi_phep: loai_nghi_phep (PHEP_NAM, OM, VIEC_RIENG_CO_LUONG, VIEC_RIENG_KHONG_LUONG, THAI_SAN, KHAC), tu_ngay, den_ngay, so_ngay_nghi, ly_do, file_dinh_kem_url, trang_thai (CHO_DUYET, DUYET_CAP_1, DA_DUYET, TU_CHOI, HUY), duyet_cap_1_boi/luc, duyet_cap_2_boi/luc, ghi_chu_duyet. Entity DangKyOt ánh xạ timekeeping.dang_ky_ot: ngay_lam_ot, gio_bat_dau, gio_ket_thuc, so_gio_ot, he_so_ot (NGAY_THUONG_150, NGAY_NGHI_TUAN_200, NGAY_LE_300), lam_dem, cùng bộ trường duyệt 2 cấp. Entity QuyPhepNam ánh xạ timekeeping.quy_phep_nam (nam, so_ngay_duoc_huong, so_ngay_da_dung, so_ngay_con_lai là cột generated).
2. Quỹ phép năm: hàm khởi tạo/tính so_ngay_duoc_huong theo thâm niên = 12 + floor(số năm thâm niên / 5), tính từ ngay_vao_lam của nhân viên (lấy từ module hr). Cho phép khởi tạo quỹ đầu năm cho một hoặc nhiều nhân viên.
3. Quy trình duyệt 2 cấp DÙNG CHUNG cho cả nghỉ phép và OT: CHO_DUYET → DUYET_CAP_1 (quản lý trực tiếp) → DA_DUYET (HR/cấp 2); từ chối ở bất kỳ cấp nào → TU_CHOI; người tạo có thể HUY khi chưa DA_DUYET. Không cho cùng một người duyệt cả 2 cấp.
4. Service:
   - Tạo đơn nghỉ: tính so_ngay_nghi (loại trừ cuối tuần/lễ nếu cần, nêu rõ giả định); với PHEP_NAM kiểm tra quỹ còn đủ, chỉ TRỪ quỹ khi DA_DUYET; hoàn quỹ khi HUY sau duyệt. Nghỉ ốm/thai sản yêu cầu file_dinh_kem_url.
   - Tạo đăng ký OT: validate khoảng giờ, tính so_gio_ot; xác định he_so_ot theo ngày (thường/nghỉ tuần/lễ); lam_dem cộng thêm 20–30% (ghi cấu hình tỷ lệ đêm). OT chỉ hợp lệ để tính lương khi DA_DUYET.
   - Kiểm tra chồng lấn: một nhân viên không có 2 đơn nghỉ trùng ngày đang hiệu lực.
5. Controller /api/v1/attendance:
   - POST /leave-requests, POST /leave-requests/{id}/approve (body có cấp duyệt/approverId/quyết định), GET /leave-requests?employeeId=&status=.
   - POST /overtime-requests, POST /overtime-requests/{id}/approve, GET /overtime-requests?employeeId=&status=.
   - GET /leave-balance?employeeId=&year= (quỹ phép), POST /leave-balance/init (khởi tạo quỹ).
   - Bảo vệ vai trò: EMPLOYEE tạo, MANAGER duyệt cấp 1, HR duyệt cấp 2.
6. Cung cấp truy vấn cho T11: số ngày phép (theo loại), số giờ OT đã duyệt theo hệ số, theo nhân viên/tháng.

Tiêu chí hoàn thành:
- Test: duyệt 2 cấp đúng thứ tự và chặn cùng người duyệt 2 cấp; trừ/hoàn quỹ phép; tính so_ngay_duoc_huong theo thâm niên; xác định he_so_ot theo ngày; OT/nghỉ chưa duyệt không tính.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, máy trạng thái duyệt 2 cấp, công thức quỹ phép và hệ số OT, truy vấn cho T11.
```
