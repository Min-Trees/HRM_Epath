**TÀI LIỆU PHÂN TÍCH NGHIỆP VỤ**

**HỆ THỐNG QUẢN LÝ NHÂN SỰ -- CHẤM CÔNG -- BẢO HIỂM XÃ HỘI**

*Mô tả nghiệp vụ, quy trình vận hành, mô hình chức năng, sơ đồ ERD và
vòng đời nhân sự*

1\. Tổng quan hệ thống

Hệ thống gồm **3 module nghiệp vụ lõi** liên kết chặt chẽ theo dòng dữ
liệu một chiều xuyên suốt vòng đời nhân sự: **Quản lý nhân sự (Core
HR)** cung cấp dữ liệu gốc về con người và tổ chức; **Chấm công
(Timekeeping)** ghi nhận thời gian làm việc thực tế; **Bảo hiểm xã hội
(BHXH)** quản lý nghĩa vụ đóng và quyền lợi hưởng theo quy định pháp
luật. Cả ba module đều đổ dữ liệu vào **module Tính lương** -- nơi tổng
hợp và cho ra kết quả cuối cùng là bảng lương, số tiền trích nộp
BHXH/BHYT/BHTN, và thuế TNCN phải khấu trừ.

Nguyên tắc thiết kế xuyên suốt: **một nguồn dữ liệu gốc -- nhiều nơi sử
dụng**. Ví dụ hồ sơ nhân viên chỉ tạo một lần ở Core HR, nhưng được tham
chiếu (không sao chép) ở cả module Chấm công, BHXH và Tính lương thông
qua khóa ngoại \`nhan\_vien\_id\`.

2\. Mô tả chi tiết nghiệp vụ và quy trình vận hành theo module

2.1. Module Quản lý nhân sự (Core HR)

Chức năng chính:

-   **Quản lý cơ cấu tổ chức**: xây dựng cây phòng ban/bộ phận đa cấp,
    gán trưởng bộ phận, định biên nhân sự theo từng đơn vị.

-   **Quản lý hồ sơ nhân viên**: thông tin định danh (CCCD, ngày sinh,
    quê quán), thông tin liên hệ, trình độ học vấn, quá trình công tác,
    người phụ thuộc (phục vụ giảm trừ gia cảnh thuế TNCN).

-   **Quản lý hợp đồng lao động**: theo dõi loại hợp đồng (thử việc, xác
    định thời hạn 12--36 tháng, không xác định thời hạn), mức lương thỏa
    thuận, phụ lục hợp đồng, cảnh báo tự động trước 30--45 ngày khi hợp
    đồng sắp hết hạn để tái ký hoặc chấm dứt.

-   **Quản lý biến động nhân sự**: ra quyết định tuyển dụng, bổ nhiệm,
    điều chuyển, thăng chức, kỷ luật, chấm dứt hợp đồng --- mỗi biến
    động đều gắn với một quyết định (số hiệu, ngày hiệu lực) và tự động
    cập nhật trạng thái nhân viên.

-   **Quản lý ngạch/bậc lương**: khung lương theo chức danh, hệ số
    lương, làm cơ sở tính lương cơ bản.

Quy trình vận hành điển hình:

-   Phòng Nhân sự tạo hồ sơ ứng viên trúng tuyển → sinh mã nhân viên
    (ma\_nv) tự động → thiết lập tài khoản chấm công (đồng bộ sang
    module Chấm công) → soạn và ký hợp đồng thử việc.

-   Hết thời gian thử việc, quản lý trực tiếp đánh giá → nếu đạt, hệ
    thống chuyển trạng thái sang **Chính thức**, sinh hợp đồng lao động
    chính thức, đồng thời sinh yêu cầu **báo tăng lao động** gửi sang
    module BHXH.

-   Mọi thay đổi chức danh/lương/phòng ban đều phải đi qua nghiệp vụ
    **biến động nhân sự** để đảm bảo có vết kiểm toán (audit trail),
    không sửa trực tiếp trên hồ sơ gốc.

2.2. Module Chấm công (Timekeeping)

Chức năng chính:

-   **Quản lý ca làm việc**: hành chính (8h/ngày), ca kíp xoay vòng (3
    ca), làm việc linh hoạt (flexible), phân ca theo tuần/tháng cho từng
    nhân viên hoặc theo nhóm.

-   **Thu thập dữ liệu chấm công**: đồng bộ từ máy chấm công vân
    tay/khuôn mặt, ứng dụng di động có định vị GPS (cho nhân viên làm
    việc ngoài văn phòng/công trường), hoặc chấm công thủ công có phê
    duyệt.

-   **Xử lý ngoại lệ**: tự động phát hiện đi trễ, về sớm, quên chấm công
    (thiếu công), làm việc ngoài ca; nhân viên/quản lý giải trình và
    duyệt bổ sung.

-   **Quản lý tăng ca (OT)**: đăng ký OT trước, tính hệ số theo quy định
    Bộ luật Lao động --- 150% ngày thường, 200% ngày nghỉ hằng tuần,
    300% ngày lễ/Tết có hưởng lương; OT làm việc ban đêm cộng thêm
    20--30%.

-   **Quản lý nghỉ phép**: phép năm (tính theo thâm niên, tối thiểu 12
    ngày/năm + 1 ngày/5 năm thâm niên), nghỉ ốm, nghỉ việc riêng có
    lương/không lương, nghỉ thai sản --- có quy trình xin nghỉ → duyệt
    theo cấp bậc → trừ vào quỹ phép còn lại.

-   **Tổng hợp bảng công**: cuối tháng hệ thống tự động tổng hợp số ngày
    công thực tế, giờ OT, ngày phép, ngày nghỉ không lương cho từng nhân
    viên, làm đầu vào duy nhất cho module Tính lương.

Quy trình vận hành điển hình:

-   Hằng ngày: nhân viên chấm công vào/ra → dữ liệu đổ về hệ thống theo
    thời gian thực → hệ thống tự đối chiếu với ca đã phân để gắn cờ
    ngoại lệ (trễ/sớm/thiếu công).

-   Trong tháng: nhân viên gửi yêu cầu nghỉ phép/OT/giải trình công →
    quản lý trực tiếp phê duyệt theo quy trình duyệt đa cấp (1--2 cấp
    tùy chính sách công ty).

-   Cuối tháng (thường ngày làm việc cuối cùng hoặc đầu tháng sau): chốt
    bảng công, phòng Nhân sự rà soát lần cuối, khóa bảng công (không cho
    sửa) và chuyển sang module Tính lương.

2.3. Module Bảo hiểm xã hội (BHXH)

Chức năng chính:

-   **Quản lý quá trình tham gia BHXH/BHYT/BHTN**: mã số BHXH, đơn vị
    tham gia, mức lương đóng theo từng giai đoạn (có trần đóng BHXH/BHYT
    tối đa = 20 lần lương cơ sở, trần BHTN = 20 lần lương tối thiểu
    vùng).

-   **Nghiệp vụ báo tăng/báo giảm lao động**: báo tăng khi ký HĐLĐ từ đủ
    1 tháng trở lên (tuyển mới, hết thử việc chuyển chính thức); báo
    giảm khi nghỉ việc, nghỉ không lương từ 14 ngày làm việc/tháng trở
    lên, nghỉ thai sản, nghỉ ốm dài ngày. Kết xuất file/tờ khai theo mẫu
    chuẩn của cơ quan BHXH (mẫu D02-LT, TK1-TS\...).

-   **Tỷ lệ trích đóng hiện hành**: doanh nghiệp đóng 17,5% BHXH + 3%
    BHYT + 1% BHTN = 21,5%; người lao động đóng 8% BHXH + 1,5% BHYT + 1%
    BHTN = 10,5% trên mức lương đóng.

-   **Quản lý chế độ hưởng**: ốm đau (tối đa 30--70 ngày/năm tùy thâm
    niên và điều kiện lao động), thai sản (6 tháng), tai nạn lao động --
    bệnh nghề nghiệp, hưu trí, tử tuất --- mỗi hồ sơ chế độ có hồ sơ y
    tế/chứng từ đính kèm và số tiền được cơ quan BHXH chi trả hoặc hoàn
    ứng.

-   **Quản lý sổ BHXH**: theo dõi quá trình đóng liên tục qua nhiều đơn
    vị, chốt sổ khi nghỉ việc, xác nhận thời gian đóng chưa hưởng để làm
    căn cứ hưởng chế độ hưu trí/thất nghiệp sau này.

Quy trình vận hành điển hình:

-   Khi có biến động tăng/giảm lao động từ module Nhân sự → cán bộ BHXH
    lập hồ sơ báo tăng/giảm → nộp qua phần mềm giao dịch điện tử BHXH
    (thường trong vòng 30 ngày kể từ ngày phát sinh, riêng báo giảm cần
    nộp kịp thời để tránh truy thu).

-   Hằng tháng: đối chiếu mức lương đóng BHXH với mức lương ghi trên hợp
    đồng/bảng lương, đảm bảo không thấp hơn mức tối thiểu vùng và tuân
    thủ nguyên tắc đóng trên tổng thu nhập có tính chất lương ổn định.

-   Khi nhân viên phát sinh sự kiện (ốm, thai sản, tai nạn lao động):
    tiếp nhận hồ sơ chứng từ → lập hồ sơ đề nghị giải quyết chế độ → nộp
    cơ quan BHXH → nhận kết quả chi trả → đối chiếu, thông báo cho nhân
    viên.

-   Khi nghỉ việc: lập thủ tục báo giảm + chốt sổ BHXH, bàn giao sổ/tra
    cứu quá trình đóng cho người lao động.

2.4. Module Tính lương (điểm hội tụ dữ liệu)

Đây là module tổng hợp, nhận đầu vào từ cả ba module trên:

-   Từ **Nhân sự**: lương cơ bản theo hợp đồng, hệ số lương, phụ cấp cố
    định (chức vụ, ăn trưa, xăng xe, điện thoại\...).

-   Từ **Chấm công**: số ngày công thực tế, giờ OT theo hệ số, số ngày
    nghỉ không lương (bị trừ lương).

-   Từ **BHXH**: mức lương đóng làm căn cứ trích 10,5% phần người lao
    động chịu, đồng thời tính 21,5% phần doanh nghiệp chịu (chi phí,
    không trừ vào lương nhân viên).

-   Tính thêm **thuế TNCN** theo biểu lũy tiến từng phần sau khi trừ
    giảm trừ gia cảnh (bản thân + người phụ thuộc) và các khoản bảo hiểm
    bắt buộc.

-   Kết quả cuối: **bảng lương chi tiết** (lương thực lĩnh = lương gộp −
    BHXH/BHYT/BHTN NLĐ − thuế TNCN − tạm ứng/khấu trừ khác) → duyệt →
    chi trả qua ngân hàng → xuất phiếu lương điện tử.

3\. Sơ đồ phân tích ERD --- mối quan hệ giữa các module

Sơ đồ dưới đây thể hiện các thực thể dữ liệu chính và quan hệ khóa
chính/khóa ngoại giữa 4 nhóm module (màu xanh dương: Nhân sự, xanh lá:
Chấm công, đỏ nâu: BHXH, tím: Tính lương). Thực thể **NHAN\_VIEN** đóng
vai trò trung tâm (hub), gần như mọi thực thể khác đều tham chiếu tới nó
qua khóa ngoại \`nhan\_vien\_id\`.

![](media/9db6ff67e32ddd428a24b17b525f0a7968214c4a.png){width="6.458333333333333in"
height="4.791666666666667in"}

*Hình 1 -- Sơ đồ ERD hệ thống Nhân sự -- Chấm công -- BHXH -- Tính
lương*

Diễn giải quan hệ chính

  ------------------------------------------------------------------ ------------------- ----------------------------------------------------------------------------------------
  **Quan hệ**                                                        **Bản chất**        **Ý nghĩa nghiệp vụ**
  PHONG\_BAN → NHAN\_VIEN                                            1 -- n              Một phòng ban có nhiều nhân viên; nhân viên chỉ thuộc 1 phòng ban tại 1 thời điểm.
  NHAN\_VIEN → HOP\_DONG\_LAO\_DONG                                  1 -- n              Một nhân viên có nhiều hợp đồng theo thời gian (thử việc, chính thức, phụ lục).
  NHAN\_VIEN → PHAN\_CA → CHAM\_CONG\_CHI\_TIET                      1 -- n -- n         Ca làm việc quyết định khung giờ chuẩn để đối chiếu dữ liệu chấm công thực tế.
  CHAM\_CONG\_CHI\_TIET, NGHI\_PHEP → BANG\_CONG\_THANG              n -- 1 (tổng hợp)   Bảng công tháng là kết quả tổng hợp từ chấm công chi tiết và nghỉ phép đã duyệt.
  NHAN\_VIEN → BHXH\_QUA\_TRINH / BHXH\_BIEN\_DONG / BHXH\_CHE\_DO   1 -- n              Theo dõi toàn bộ lịch sử tham gia, biến động và quyền lợi BHXH của từng nhân viên.
  BANG\_CONG\_THANG, BHXH\_QUA\_TRINH → BANG\_LUONG\_THANG           n -- 1 (đầu vào)    Bảng lương tháng chỉ được tính khi đã có bảng công chốt và mức lương đóng BHXH hợp lệ.
  ------------------------------------------------------------------ ------------------- ----------------------------------------------------------------------------------------

4\. Quy trình vận hành tổng hợp (end-to-end)

Sơ đồ dưới thể hiện dòng chảy dữ liệu và trách nhiệm xử lý xuyên suốt 4
module, từ lúc tiếp nhận nhân sự mới đến khi ra bảng lương và báo cáo
cuối cùng.

![](media/7d995e4ee31bf6761dd497d57037d77b0060c415.png){width="5.833333333333333in"
height="5.416666666666667in"}

*Hình 2 -- Quy trình vận hành tổng hợp giữa các module*

Diễn giải các bước chính:

-   **Bước 1 -- Khởi tạo**: Nhân sự tạo hồ sơ, ký HĐLĐ → đồng thời kích
    hoạt phân ca (Chấm công) và báo tăng BHXH (BHXH).

-   **Bước 2 -- Vận hành hằng ngày/hằng tháng**: Chấm công ghi nhận,
    duyệt phép/OT, tổng hợp bảng công; song song BHXH xác định mức đóng
    và xử lý các chế độ phát sinh (nếu có).

-   **Bước 3 -- Tính lương**: nhận bảng công đã chốt + mức đóng BHXH →
    tính lương gộp, trích các khoản bảo hiểm, tính thuế TNCN.

-   **Bước 4 -- Hoàn tất**: duyệt và chi trả lương, đồng thời xuất báo
    cáo nộp cơ quan BHXH/thuế và lưu trữ chứng từ theo quy định.

-   **Vòng lặp biến động**: bất kỳ thay đổi nhân sự nào (thăng chức,
    thuyên chuyển, nghỉ việc) đều quay lại **Bước 1** dưới dạng biến
    động, kéo theo cập nhật tương ứng ở cả 3 module còn lại.

5\. Vòng đời nhân sự (Employee Lifecycle)

Vòng đời nhân sự là trục thời gian xuyên suốt toàn bộ hệ thống --- mỗi
trạng thái trong vòng đời đều kéo theo một tập nghiệp vụ tương ứng ở cả
3 module.

![](media/9daa2da6f43db9426d2f0d1a9d4d8892930a6267.png){width="6.666666666666667in"
height="2.7083333333333335in"}

*Hình 3 -- Vòng đời nhân sự và các nghiệp vụ liên đới*

  ------------------------ ----------------------------------------------- -----------------------------------------------------------------------------------------------------------------------------------------------------
  **Giai đoạn**            **Sự kiện/Trạng thái**                          **Nghiệp vụ liên đới ở các module**
  1\. Tuyển dụng           Ứng viên trúng tuyển                            HR: tạo hồ sơ, mã nhân viên. Chấm công: chưa kích hoạt.
  2\. Thử việc             Ký HĐ thử việc (≤ 60 ngày)                      HR: HĐ thử việc. Chấm công: bắt đầu chấm công, phân ca. BHXH: thường chưa đóng (thử việc dưới 1 tháng) hoặc đóng nếu ký HĐLĐ chính thức ngay.
  3\. Chính thức           Đạt thử việc, ký HĐLĐ chính thức                HR: HĐLĐ chính thức. BHXH: báo tăng lao động, xác định mức đóng. Lương: bắt đầu tính lương đầy đủ.
  4\. Biến động            Thăng chức/thuyên chuyển/điều chỉnh lương       HR: quyết định biến động. BHXH: điều chỉnh mức đóng nếu lương thay đổi. Lương: áp dụng mức lương mới từ ngày hiệu lực.
  5\. Tạm hoãn HĐLĐ        Thai sản/ốm dài ngày/nghỉ không lương dài hạn   Chấm công: ghi nhận nghỉ dài hạn. BHXH: báo giảm tạm thời, giải quyết chế độ (thai sản/ốm đau). Lương: tạm ngưng hoặc giảm lương tương ứng.
  6\. Nghỉ việc/Nghỉ hưu   Chấm dứt HĐLĐ                                   HR: quyết định thôi việc, chốt hồ sơ. BHXH: báo giảm, chốt sổ BHXH. Lương: tính lương tháng cuối, trợ cấp thôi việc/thất nghiệp (nếu đủ điều kiện).
  7\. Lưu trữ              Đã nghỉ                                         Toàn bộ dữ liệu chuyển trạng thái lưu trữ (archive), vẫn giữ liên kết lịch sử phục vụ tra cứu, xác nhận quá trình công tác/đóng BHXH khi cần.
  ------------------------ ----------------------------------------------- -----------------------------------------------------------------------------------------------------------------------------------------------------

Nhận xét thiết kế

-   Mỗi **giai đoạn vòng đời** tương ứng với một hoặc nhiều **bản ghi
    biến động** (BIEN\_DONG\_NHAN\_SU, BHXH\_BIEN\_DONG) --- không bao
    giờ ghi đè, chỉ thêm mới, đảm bảo có thể truy vết lịch sử đầy đủ
    (audit trail) phục vụ thanh tra lao động, thanh tra BHXH và quyết
    toán thuế.

-   Trạng thái nhân viên (trang\_thai trong bảng NHAN\_VIEN) nên là một
    **trường tính toán/phái sinh** được cập nhật tự động từ bản ghi biến
    động mới nhất, tránh sai lệch dữ liệu giữa các module.

-   Thiết kế theo hướng **event-driven**: mỗi sự kiện nghiệp vụ (ký HĐ,
    nghỉ việc, thay đổi lương) nên phát sinh một \"event\" để các module
    liên quan (Chấm công, BHXH, Lương) tự động đồng bộ, thay vì phải
    nhập liệu thủ công nhiều lần.
