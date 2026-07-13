import { useEffect, useState } from "react";
import { recruitmentApi } from "../api";

export default function RecruitmentListPage() {
  const [tab, setTab] = useState<"yeu-cau" | "ung-vien" | "phong-van" | "quyet-dinh">("yeu-cau");
  const [yeuCauList, setYeuCauList] = useState<any[]>([]);
  const [ungVienList, setUngVienList] = useState<any[]>([]);
  const [selectedYC, setSelectedYC] = useState<any>(null);
  const [selectedUV, setSelectedUV] = useState<any>(null);
  const [lichPVList, setLichPVList] = useState<any[]>([]);
  const [quyetDinhList, setQuyetDinhList] = useState<any[]>([]);

  useEffect(() => {
    recruitmentApi.listYeuCau().then((r) => setYeuCauList(Array.isArray(r) ? r : r.content || []));
    recruitmentApi.listUngVien().then((r) => setUngVienList(Array.isArray(r) ? r : r.content || []));
  }, []);

  const onSelectYC = async (yc: any) => {
    setSelectedYC(yc);
    const uvs = await recruitmentApi.listUngVienByYeuCau(yc.yeuCauId);
    const freshUVs = Array.isArray(uvs) ? uvs : uvs.content || [];
    setUngVienList((prev) => {
      const others = prev.filter((u) => !freshUVs.find((x: any) => x.ungVienId === u.ungVienId));
      return [...others, ...freshUVs];
    });
  };

  const onSelectUV = async (uv: any) => {
    setSelectedUV(uv);
    const lichs = await recruitmentApi.listLichPV(uv.ungVienId);
    setLichPVList(Array.isArray(lichs) ? lichs : lichs.content || []);
    const qdts = await recruitmentApi.listQuyetDinh(uv.ungVienId);
    setQuyetDinhList(Array.isArray(qdts) ? qdts : qdts.content || []);
  };

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <h2>Phân hệ Tuyển dụng (T17)</h2>
      <p style={{ color: "#64748b" }}>Yêu cầu tuyển dụng · Ứng viên · Phỏng vấn · Quyết định tuyển dụng</p>

      <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
        <TabBtn active={tab === "yeu-cau"} onClick={() => setTab("yeu-cau")}>Yêu cầu tuyển dụng ({yeuCauList.length})</TabBtn>
        <TabBtn active={tab === "ung-vien"} onClick={() => setTab("ung-vien")}>Ứng viên ({ungVienList.length})</TabBtn>
        <TabBtn active={tab === "phong-van"} onClick={() => setTab("phong-van")}>Phỏng vấn ({lichPVList.length})</TabBtn>
        <TabBtn active={tab === "quyet-dinh"} onClick={() => setTab("quyet-dinh")}>Quyết định ({quyetDinhList.length})</TabBtn>
      </div>

      {tab === "yeu-cau" && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8 }}>
          <h3>Yêu cầu tuyển dụng</h3>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                <th style={th}>Mã</th>
                <th style={th}>Tiêu đề</th>
                <th style={th}>Số lượng cần tuyển</th>
                <th style={th}>Trạng thái</th>
                <th style={th}>Số ứng viên</th>
                <th style={th}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {yeuCauList.map((yc) => (
                <tr key={yc.yeuCauId} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td}>{yc.maYeuCau}</td>
                  <td style={td}>{yc.tieuDe}</td>
                  <td style={td}>{yc.soLuongCan}</td>
                  <td style={td}><StatusChip status={yc.trangThai} /></td>
                  <td style={td}>{yc.soUngVien}</td>
                  <td style={td}>
                    <button onClick={() => onSelectYC(yc)} style={btn}>Xem ứng viên</button>
                  </td>
                </tr>
              ))}
              {yeuCauList.length === 0 && (
                <tr><td colSpan={6} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chưa có yêu cầu nào</td></tr>
              )}
            </tbody>
          </table>
        </section>
      )}

      {tab === "ung-vien" && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8 }}>
          <h3>Ứng viên {selectedYC ? `(thuộc yêu cầu ${selectedYC.maYeuCau})` : ""}</h3>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                <th style={th}>Mã ứng viên</th>
                <th style={th}>Họ tên</th>
                <th style={th}>Email</th>
                <th style={th}>Số điện thoại</th>
                <th style={th}>Kinh nghiệm</th>
                <th style={th}>Trạng thái</th>
                <th style={th}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {ungVienList.map((uv) => (
                <tr key={uv.ungVienId} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td}>{uv.maUngVien}</td>
                  <td style={td}>{uv.hoTen}</td>
                  <td style={td}>{uv.email || "-"}</td>
                  <td style={td}>{uv.soDienThoai || "-"}</td>
                  <td style={td}>{uv.soNamKinhNghiem ?? 0} năm</td>
                  <td style={td}><StatusChip status={uv.trangThai} /></td>
                  <td style={td}>
                    <button onClick={() => onSelectUV(uv)} style={btn}>Xem phỏng vấn/quyết định</button>
                  </td>
                </tr>
              ))}
              {ungVienList.length === 0 && (
                <tr><td colSpan={7} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chưa có ứng viên</td></tr>
              )}
            </tbody>
          </table>
        </section>
      )}

      {tab === "phong-van" && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8 }}>
          <h3>Lịch phỏng vấn {selectedUV ? `(${selectedUV.hoTen})` : ""}</h3>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                <th style={th}>Vòng</th>
                <th style={th}>Bắt đầu</th>
                <th style={th}>Kết thúc</th>
                <th style={th}>Địa điểm</th>
                <th style={th}>Hình thức</th>
                <th style={th}>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {lichPVList.map((l) => (
                <tr key={l.lichPvId} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td}>Vòng {l.vongPhongVan}</td>
                  <td style={td}>{l.thoiGianBatDau}</td>
                  <td style={td}>{l.thoiGianKetThuc}</td>
                  <td style={td}>{l.diaDiem || "-"}</td>
                  <td style={td}>{l.hinhThuc}</td>
                  <td style={td}><StatusChip status={l.trangThai} /></td>
                </tr>
              ))}
              {lichPVList.length === 0 && (
                <tr><td colSpan={6} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chưa có lịch phỏng vấn</td></tr>
              )}
            </tbody>
          </table>
        </section>
      )}

      {tab === "quyet-dinh" && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8 }}>
          <h3>Quyết định tuyển dụng {selectedUV ? `(${selectedUV.hoTen})` : ""}</h3>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                <th style={th}>Loại hợp đồng</th>
                <th style={th}>Mức lương</th>
                <th style={th}>Ngày vào làm</th>
                <th style={th}>Phòng ban</th>
                <th style={th}>Chức danh</th>
                <th style={th}>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {quyetDinhList.map((q) => (
                <tr key={q.quyetDinhId} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td}>{q.loaiHopDong}</td>
                  <td style={td}>{q.mucLuongDeNghi?.toLocaleString("vi-VN")} VND</td>
                  <td style={td}>{q.ngayVaoLamDeNghi}</td>
                  <td style={td}>{q.tenPhongBan || q.phongBanId}</td>
                  <td style={td}>{q.chucDanh || "-"}</td>
                  <td style={td}><StatusChip status={q.trangThai} /></td>
                </tr>
              ))}
              {quyetDinhList.length === 0 && (
                <tr><td colSpan={6} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chưa có quyết định</td></tr>
              )}
            </tbody>
          </table>
        </section>
      )}
    </div>
  );
}

function TabBtn({ active, onClick, children }: any) {
  return (
    <button onClick={onClick} style={{
      padding: "8px 16px",
      background: active ? "#0d9488" : "#fff",
      color: active ? "#fff" : "#0f172a",
      border: "1px solid #0d9488",
      borderRadius: 4,
      cursor: "pointer",
    }}>{children}</button>
  );
}

function StatusChip({ status }: { status: string }) {
  const colorMap: Record<string, string> = {
    MOI_TAO: "#94a3b8",
    CHO_PHE_DUYET: "#f59e0b",
    DA_PHE_DUYET: "#10b981",
    DANG_TUYEN: "#3b82f6",
    DA_DONG: "#64748b",
    HUY: "#ef4444",
    MOI_NOP_HO_SO: "#94a3b8",
    CHO_PHONG_VAN_VONG_1: "#f59e0b",
    CHO_PHONG_VAN_VONG_2: "#f59e0b",
    CHO_DANH_GIA: "#3b82f6",
    DE_NGHI_TUYEN: "#10b981",
    TU_CHOI: "#ef4444",
    RUT_HO_SO: "#94a3b8",
    DA_TUYEN: "#10b981",
    CHUA_DIEN_RA: "#94a3b8",
    DANG_DIEN_RA: "#3b82f6",
    HOAN_THANH: "#10b981",
    CHO_PHAN_HOI: "#f59e0b",
    DA_DONG_Y: "#10b981",
  };
  const labelMap: Record<string, string> = {
    MOI_TAO: "Mới tạo",
    CHO_PHE_DUYET: "Chờ phê duyệt",
    DA_PHE_DUYET: "Đã phê duyệt",
    DANG_TUYEN: "Đang tuyển",
    DA_DONG: "Đã đóng",
    HUY: "Hủy",
    MOI_NOP_HO_SO: "Mới nộp hồ sơ",
    CHO_PHONG_VAN_VONG_1: "Chờ phỏng vấn vòng 1",
    CHO_PHONG_VAN_VONG_2: "Chờ phỏng vấn vòng 2",
    CHO_DANH_GIA: "Chờ đánh giá",
    DE_NGHI_TUYEN: "Đề nghị tuyển",
    TU_CHOI: "Từ chối",
    RUT_HO_SO: "Rút hồ sơ",
    DA_TUYEN: "Đã tuyển",
    CHUA_DIEN_RA: "Chưa diễn ra",
    DANG_DIEN_RA: "Đang diễn ra",
    HOAN_THANH: "Hoàn thành",
    CHO_PHAN_HOI: "Chờ phản hồi",
    DA_DONG_Y: "Đã đồng ý",
  };
  const color = colorMap[status] ?? "#64748b";
  return <span style={{ background: color, color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{labelMap[status] ?? status}</span>;
}

const th: React.CSSProperties = { padding: 10, textAlign: "left" };
const td: React.CSSProperties = { padding: 10 };
const btn: React.CSSProperties = { padding: "4px 12px", background: "#0d9488", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 };
