import { useEffect, useState } from "react";
import { thueApi } from "../api";

export default function QuyetToanThuePage() {
  const [tab, setTab] = useState<"02" | "05">("02");
  const [nam, setNam] = useState(new Date().getFullYear() - 1);
  const [maDonVi, setMaDonVi] = useState("DV-001");
  const [tenDonVi, setTenDonVi] = useState("Công ty TNHH ABC");
  const [maSoThue, setMaSoThue] = useState("0123456789");
  const [report, setReport] = useState<any>(null);
  const [xmlContent, setXmlContent] = useState<string>("");
  const [loading, setLoading] = useState(false);

  const generate = async () => {
    setLoading(true);
    try {
      if (tab === "02") {
        const r = await thueApi.generate02(nam, maDonVi, tenDonVi, maSoThue);
        setReport(r);
        setXmlContent(await thueApi.download02Xml(nam, maDonVi, tenDonVi, maSoThue));
      } else {
        const r = await thueApi.generate05(nam, maNvFor05(), tenDonVi, maSoThue);
        setReport(r);
        setXmlContent(await thueApi.download05Xml(nam, maNvFor05(), tenDonVi, maSoThue));
      }
    } catch (e: any) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  };

  const maNvFor05 = () => {
    return (report?.maNv) || "NV-001";
  };

  const downloadXml = () => {
    const blob = new Blob([xmlContent], { type: "application/xml" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `${tab}-QTT_${maDonVi}_${nam}.xml`;
    a.click();
    URL.revokeObjectURL(url);
  };

  useEffect(() => { setReport(null); setXmlContent(""); }, [tab]);

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <h2>Quyết toán thuế TNCN (T16)</h2>
      <p style={{ color: "#64748b" }}>Theo Thông tư 92/2015/TT-BTC. Cam kết 08/CK-TNCN đăng ký trước ngày 31/3 năm sau.</p>

      <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
        <button onClick={() => setTab("02")} style={tabBtn(tab === "02")}>Mẫu 02/QTT (Tổng hợp doanh nghiệp)</button>
        <button onClick={() => setTab("05")} style={tabBtn(tab === "05")}>Mẫu 05/QTT (Chi tiết nhân viên)</button>
      </div>

      <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
        <div style={{ display: "flex", gap: 16, flexWrap: "wrap", alignItems: "flex-end" }}>
          <div>
            <label style={lbl}>Năm quyết toán</label>
            <input type="number" value={nam} onChange={(e) => setNam(Number(e.target.value))} style={inp} />
          </div>
          <div>
            <label style={lbl}>Mã đơn vị</label>
            <input value={maDonVi} onChange={(e) => setMaDonVi(e.target.value)} style={inp} />
          </div>
          <div>
            <label style={lbl}>Tên đơn vị</label>
            <input value={tenDonVi} onChange={(e) => setTenDonVi(e.target.value)} style={{ ...inp, width: 220 }} />
          </div>
          <div>
            <label style={lbl}>MST đơn vị</label>
            <input value={maSoThue} onChange={(e) => setMaSoThue(e.target.value)} style={inp} />
          </div>
          {tab === "05" && (
            <div>
              <label style={lbl}>Mã nhân viên</label>
              <input value={report?.maNv || "NV-001"} onChange={(e) => setReport({ ...(report || {}), maNv: e.target.value })} style={inp} />
            </div>
          )}
          <button onClick={generate} disabled={loading} style={{ ...btn, background: "#0d9488" }}>
            {loading ? "Đang tạo..." : "Tạo báo cáo"}
          </button>
        </div>
      </section>

      {report && tab === "02" && <Mau02View report={report} />}
      {report && tab === "05" && <Mau05View report={report} />}

      {xmlContent && (
        <section style={{ background: "#1e293b", color: "#e2e8f0", padding: 16, borderRadius: 8, marginTop: 16 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
            <h3 style={{ margin: 0 }}>Xem trước XML</h3>
            <button onClick={downloadXml} style={{ ...btn, background: "#3b82f6" }}>Tải XML</button>
          </div>
          <pre style={{ maxHeight: 400, overflow: "auto", fontSize: 12, lineHeight: 1.4 }}>
            {xmlContent.slice(0, 4000)}
            {xmlContent.length > 4000 ? "\n... (đã rút gọn)" : ""}
          </pre>
        </section>
      )}
    </div>
  );
}

function Mau02View({ report }: { report: any }) {
  return (
    <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
      <h3>Mẫu 02/QTT-TNCN - Năm {report.nam}</h3>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12 }}>
        <StatBox label="Tổng số nhân viên" value={report.tongSoNhanVien} />
        <StatBox label="Nhân viên ủy quyền" value={report.tongNhanVienUyQuyen} highlight />
        <StatBox label="Nhân viên tự quyết toán thuế" value={report.tongNhanVienTuQtt} />
        <StatBox label="Tổng thu nhập chịu thuế" value={fmtVND(report.tongThuNhapChiuThue)} />
        <StatBox label="Tổng giảm trừ bản thân" value={fmtVND(report.tongGiamTruBanThan)} />
        <StatBox label="Tổng giảm trừ người phụ thuộc" value={fmtVND(report.tongGiamTruNguoiPhuThuoc)} />
        <StatBox label="Tổng thuế đã khấu trừ" value={fmtVND(report.tongThueDaKhauTru)} highlight />
        <StatBox label="Tổng thuế phải nộp thêm" value={fmtVND(report.tongThuePhaiNopThem)} />
        <StatBox label="Tổng thuế được hoàn" value={fmtVND(report.tongThueDuocHoan)} />
      </div>

      <h4 style={{ marginTop: 24 }}>Nhân viên có mức thuế cao nhất</h4>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ background: "#f1f5f9" }}>
            <th style={th}>STT</th>
            <th style={th}>Mã nhân viên</th>
            <th style={th}>Họ tên</th>
            <th style={th}>MST</th>
            <th style={th}>Thu nhập chịu thuế</th>
            <th style={th}>Thuế đã khấu trừ</th>
            <th style={th}>Thuế phải nộp</th>
          </tr>
        </thead>
        <tbody>
          {(report.top10NhanVienThueCao || []).map((r: any, i: number) => (
            <tr key={i} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td}>{i + 1}</td>
              <td style={td}>{r.maNv}</td>
              <td style={td}>{r.hoTen}</td>
              <td style={td}>{r.maSoThue || "-"}</td>
              <td style={td}>{fmtVND(r.thuNhapChiuThue)}</td>
              <td style={td}>{fmtVND(r.thueDaKhauTru)}</td>
              <td style={td}>{fmtVND(r.thuePhaiNop)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

function Mau05View({ report }: { report: any }) {
  return (
    <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
      <h3>Mẫu 05/QTT-TNCN - {report.hoTen} ({report.maNv}) - Năm {report.nam}</h3>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginBottom: 16 }}>
        <StatBox label="MST" value={report.maSoThue || "-"} />
        <StatBox label="CCCD" value={report.cmnd || "-"} />
        <StatBox label="Cam kết 08/CK-TNCN" value={report.loaiCamKet08} highlight />
        <StatBox label="Số người phụ thuộc" value={report.soNguoiPhuThuoc} />
        <StatBox label="Tổng thu nhập cả năm" value={fmtVND(report.tongThuNhapCaNam)} />
        <StatBox label="Tổng thu nhập chịu thuế" value={fmtVND(report.tongThuNhapChiuThue)} />
        <StatBox label="Tổng giảm trừ bản thân" value={fmtVND(report.giamTruBanThan)} />
        <StatBox label="Tổng giảm trừ người phụ thuộc" value={fmtVND(report.giamTruNguoiPhuThuoc)} />
        <StatBox label="Tổng thuế đã khấu trừ" value={fmtVND(report.tongThueDaKhauTru)} highlight />
      </div>

      <h4>Chi tiết theo tháng</h4>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ background: "#f1f5f9" }}>
            <th style={th}>Tháng</th>
            <th style={th}>Thu nhập chịu thuế</th>
            <th style={th}>Giảm trừ bản thân</th>
            <th style={th}>Giảm trừ người phụ thuộc</th>
            <th style={th}>Thuế đã khấu trừ</th>
          </tr>
        </thead>
        <tbody>
          {(report.chiTietThang || []).map((m: any) => (
            <tr key={m.thang} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td}>Tháng {m.thang}</td>
              <td style={td}>{fmtVND(m.thuNhapChiuThue)}</td>
              <td style={td}>{fmtVND(m.giamTruBanThan)}</td>
              <td style={td}>{fmtVND(m.giamTruNguoiPhuThuoc)}</td>
              <td style={td}>{fmtVND(m.thueDaKhauTru)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

function StatBox({ label, value, highlight }: { label: string; value: any; highlight?: boolean }) {
  return (
    <div style={{ background: highlight ? "#ecfeff" : "#f8fafc", padding: 12, borderRadius: 6 }}>
      <div style={{ fontSize: 12, color: "#64748b" }}>{label}</div>
      <div style={{ fontWeight: 600, fontSize: 16, color: highlight ? "#0e7490" : undefined }}>
        {value === null || value === undefined ? "-" : value}
      </div>
    </div>
  );
}

function fmtVND(v: any): string {
  if (!v) return "-";
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(v));
}

const tabBtn = (active: boolean): React.CSSProperties => ({
  padding: "8px 16px",
  background: active ? "#0d9488" : "#fff",
  color: active ? "#fff" : "#0f172a",
  border: "1px solid #0d9488",
  borderRadius: 4,
  cursor: "pointer",
});
const lbl: React.CSSProperties = { display: "block", fontSize: 12, color: "#64748b", marginBottom: 4 };
const inp: React.CSSProperties = { padding: 8, width: 180, border: "1px solid #cbd5e1", borderRadius: 4 };
const btn: React.CSSProperties = { padding: "8px 16px", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer" };
const th: React.CSSProperties = { padding: 10, textAlign: "left" };
const td: React.CSSProperties = { padding: 10 };
