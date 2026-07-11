# T09 — Thu thập chấm công & xử lý ngoại lệ

- Nhóm: Chấm công
- Blocked by: T08
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`timekeeping.cham_cong_chi_tiet`)

## Mục tiêu
Ghi nhận dữ liệu vào/ra từ nhiều nguồn, đối chiếu ca chuẩn, tự phát hiện ngoại lệ và cho phép giải trình/duyệt.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T09 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và bảng timekeeping.cham_cong_chi_tiet trong database/schema.sql. Chỉ làm trong package com.company.hrm.attendance. Dùng method "ca chuẩn theo ngày" do T08 cung cấp.

Nhiệm vụ: xây nghiệp vụ thu thập chấm công và xử lý ngoại lệ.

Yêu cầu:
1. Entity ChamCongChiTiet ánh xạ timekeeping.cham_cong_chi_tiet: nhan_vien_id, phan_ca_id, ngay_cham_cong, gio_vao, gio_ra, nguon (VAN_TAY, KHUON_MAT, GPS_MOBILE, THU_CONG), vi_tri_gps, loai_ngoai_le (DI_TRE, VE_SOM, THIEU_CONG, LAM_NGOAI_CA, KHONG_NGOAI_LE), so_gio_cong, can_giai_trinh, giai_trinh_noi_dung, giai_trinh_trang_thai, duyet_boi, duyet_luc. UNIQUE (nhan_vien_id, ngay_cham_cong).
2. Service ChamCongService:
   - Nhận bản ghi vào/ra (một điểm hoặc theo lô, mô phỏng đồng bộ từ máy/app). Với THU_CONG yêu cầu phê duyệt.
   - Đối chiếu ca chuẩn của nhân viên (T08): tính so_gio_cong, và gán loai_ngoai_le tự động (đi trễ nếu gio_vao muộn hơn giờ bắt đầu ca quá ngưỡng; về sớm; thiếu công nếu thiếu vào hoặc ra; làm ngoài ca nếu không có phân ca). Ngưỡng phút cấu hình được.
   - Đặt can_giai_trinh=true cho ngoại lệ cần giải trình.
   - Nhân viên gửi giải trình; quản lý duyệt/từ chối (cập nhật giai_trinh_trang_thai, duyet_boi, duyet_luc). Chỉ MANAGER/HR được duyệt.
3. Controller /api/v1/attendance:
   - POST /time-logs (một điểm), POST /time-logs/batch (đồng bộ lô), GET /time-logs?employeeId=&from=&to=.
   - GET /exceptions?from=&to=&status= (danh sách ngoại lệ cần xử lý).
   - POST /time-logs/{id}/explanation (nhân viên gửi giải trình), POST /time-logs/{id}/approve (duyệt/từ chối).
4. GPS: lưu vi_tri_gps khi nguồn GPS_MOBILE; để chỗ mở rộng kiểm tra vùng cho phép (geofence) nhưng không bắt buộc hiện thực đầy đủ.
5. Cung cấp truy vấn tổng hợp công theo nhân viên/tháng (số ngày công, giờ theo ngày) để T11 dùng.

Tiêu chí hoàn thành:
- Test: gán ngoại lệ đúng (đi trễ/về sớm/thiếu công/ngoài ca) theo ca chuẩn; luồng giải trình→duyệt cập nhật trạng thái; batch không tạo trùng ngày.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, quy tắc phát hiện ngoại lệ + ngưỡng cấu hình, truy vấn tổng hợp cho T11.
```
