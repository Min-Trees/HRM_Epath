# T15 — Engine tính lương (từ bảng công chốt + BHXH)

- Nhóm: Tính lương
- Blocked by: T11, T13, T14
- Đầu vào cần đọc: `00-CONVENTIONS.md`, `database/schema.sql` (`payroll.bang_luong_thang`, `payroll.chi_tiet_khoan_khau_tru`)

## Mục tiêu
Tính bảng lương tháng từ bảng công đã chốt, mức đóng BHXH hợp lệ, phụ cấp hợp đồng, OT và thuế TNCN — điểm hội tụ dữ liệu 3 module.

## Khối PROMPT (giao cho agent fable)
```
Bạn là agent thực thi task T15 của dự án HRM. Đọc task-prompt/00-CONVENTIONS.md và bảng payroll.bang_luong_thang, payroll.chi_tiet_khoan_khau_tru trong database/schema.sql. Chỉ làm trong package com.company.hrm.payroll. Dùng dữ liệu từ T11 (bảng công), T12/T13 (BHXH), T14 (tham số + thuế), T06 (lương/phụ cấp hợp đồng).

Nhiệm vụ: xây engine tính lương tháng.

Yêu cầu:
1. Entity BangLuongThang ánh xạ payroll.bang_luong_thang: liên kết bang_cong_id, thang, nam, luong_co_ban, phu_cap, tien_ot, tien_khau_tru_nghi_khong_luong, luong_gop, muc_luong_dong_bhxh, bhxh_nld, bhxh_dn, thu_nhap_truoc_thue, giam_tru_ban_than, giam_tru_nguoi_phu_thuoc, thu_nhap_tinh_thue, thue_tncn, tam_ung, khau_tru_khac, thuc_linh, trang_thai (DA_TINH, DA_DUYET, DA_CHI_TRA, HUY). Entity ChiTietKhoanKhauTru ánh xạ payroll.chi_tiet_khoan_khau_tru. UNIQUE (nhan_vien_id, thang, nam).
2. TIỀN ĐIỀU KIỆN (bắt buộc kiểm tra, ném BusinessException nếu vi phạm):
   - Bảng công tháng của nhân viên phải ở trạng thái DA_CHOT (lấy qua service T11). Nếu chưa chốt → từ chối.
   - Phải có giai đoạn BHXH hiệu lực tại kỳ (lấy qua service T12). Nếu không → từ chối.
   - Không tính trùng: nếu đã có bảng lương kỳ này (khác HUY) → từ chối.
3. Công thức (dùng BigDecimal HALF_UP, mọi tỷ lệ/tham số lấy theo ngày trong kỳ):
   - Lương theo công: lương cơ bản hợp đồng quy theo số ngày công thực tế / công chuẩn tháng; cộng phụ cấp cố định (JSONB hợp đồng); trừ ngày nghỉ không lương.
   - Tiền OT: theo số giờ OT từng nhóm × đơn giá giờ × hệ số 150/200/300%, cộng phụ trội đêm nếu có.
   - Lương gộp = lương theo công + phụ cấp + tiền OT − khấu trừ nghỉ không lương.
   - BHXH: mức đóng hợp lệ (đã áp trần, từ T12) × 10,5% (NLĐ, trừ vào lương) và × 21,5% (DN, chi phí — thể hiện nhưng không trừ lương NLĐ).
   - Thu nhập trước thuế = lương gộp − BHXH phần NLĐ.
   - Giảm trừ = bản thân + (số người phụ thuộc hợp lệ trong kỳ × mức/người) (từ T14 + danh sách người phụ thuộc T05).
   - Thu nhập tính thuế = max(0, thu nhập trước thuế − giảm trừ); thuế TNCN theo biểu lũy tiến (T14).
   - Thực lĩnh = thu nhập trước thuế − thuế TNCN − tạm ứng − khấu trừ khác.
4. Service PayrollEngine:
   - Chạy lương cho 1 nhân viên hoặc theo lô (toàn phòng ban/toàn công ty) một kỳ; ghi bảng lương trạng thái DA_TINH và chi tiết khấu trừ.
   - Tính lại (recalculate) chỉ khi bảng lương chưa DA_DUYET (xóa/ghi đè bản DA_TINH có kiểm soát).
   - Lưu snapshot các tham số đã dùng (tỷ lệ, giảm trừ) để phiếu lương tái hiện được.
5. Controller /api/v1/payroll:
   - POST /runs (body: month, year, scope=employee|department|all, id?) → chạy lương, trả tóm tắt.
   - GET /payslips?month=&year=&employeeId=&status=, GET /payslips/{id} (chi tiết phân rã).
   - Bảo vệ vai trò PAYROLL_ACCOUNTANT.

Tiêu chí hoàn thành:
- Test: từ chối tính khi bảng công chưa chốt và khi thiếu BHXH; tính đúng end-to-end một ca mẫu (đối chiếu số học từng bước); OT theo hệ số; thuế lũy tiến; chặn tính trùng; recalculate chỉ khi chưa duyệt.
- `mvn -q test` xanh (hoặc nêu rõ hạn chế).
- Bàn giao: endpoint, công thức từng bước, danh sách tiền điều kiện, snapshot tham số cho phiếu lương.
```
