import React, { useEffect, useState } from "react";
import { bhxhReportApi } from "../api";

export default function BhxhReportPage() {
  const [reportType, setReportType] = useState<"D02-LT" | "D03-LT">("D02-LT");
  const [tuNgay, setTuNgay] = useState(() => {
    const d = new Date();
    d.setDate(1);
    return d.toISOString().slice(0, 10);
  });
  const [denNgay, setDenNgay] = useState(() => new Date().toISOString().slice(0, 10));
  const [maDonVi, setMaDonVi] = useState("DV-001");
  const [tenDonVi, setTenDonVi] = useState("Cong ty TNHH ABC");
  const [maSoThue, setMaSoThue] = useState("");
  const [report, setReport] = useState<any>(null);
  const [xmlContent, setXmlContent] = useState<string>("");
  const [loading, setLoading] = useState(false);

  const generate = async () => {
    setLoading(true);
    try {
      let r: any;
      let xml: string;
      if (reportType === "D02-LT") {
        r = await bhxhReportApi.generateD02(tuNgay, denNgay, maDonVi, tenDonVi, maSoThue);
        xml = await bhxhReportApi.downloadD02Xml(tuNgay, denNgay, maDonVi, tenDonVi, maSoThue);
      } else {
        r = await bhxhReportApi.generateD03(tuNgay, denNgay, maDonVi, tenDonVi);
        xml = await bhxhReportApi.downloadD03Xml(tuNgay, denNgay, maDonVi, tenDonVi);
      }
      setReport(r);
      setXmlContent(xml);
    } catch (e: any) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  };

  const downloadXml = () => {
    const blob = new Blob([xmlContent], { type: "application/xml" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `${reportType.replace(/-/g, "")}_${maDonVi}_${tuNgay}.xml`;
    a.click();
    URL.revokeObjectURL(url);
  };

  useEffect(() => { setReport(null); setXmlContent(""); }, [reportType]);

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <h2>BHXH Reports (T15)</h2>

      <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16, boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
        <div style={{ display: "flex", gap: 16, flexWrap: "wrap", alignItems: "flex-end" }}>
          <div>
            <label style={lblStyle}>Loai bao cao</label>
            <select value={reportType} onChange={(e) => setReportType(e.target.value as any)} style={inpStyle}>
              <option value="D02-LT">D02-LT (Bao tang/giam LD)</option>
              <option value="D03-LT">D03-LT (Bao cap so BHXH)</option>
            </select>
          </div>
          <div>
            <label style={lblStyle}>Tu ngay</label>
            <input type="date" value={tuNgay} onChange={(e) => setTuNgay(e.target.value)} style={inpStyle} />
          </div>
          <div>
            <label style={lblStyle}>Den ngay</label>
            <input type="date" value={denNgay} onChange={(e) => setDenNgay(e.target.value)} style={inpStyle} />
          </div>
          <div>
            <label style={lblStyle}>Ma don vi BHXH</label>
            <input value={maDonVi} onChange={(e) => setMaDonVi(e.target.value)} style={inpStyle} />
          </div>
          <div>
            <label style={lblStyle}>Ten don vi</label>
            <input value={tenDonVi} onChange={(e) => setTenDonVi(e.target.value)} style={{ ...inpStyle, width: 220 }} />
          </div>
          {reportType === "D02-LT" && (
            <div>
              <label style={lblStyle}>Ma so thue</label>
              <input value={maSoThue} onChange={(e) => setMaSoThue(e.target.value)} style={inpStyle} />
            </div>
          )}
          <button onClick={generate} disabled={loading} style={{ ...btnStyle, background: "#0d9488" }}>
            {loading ? "Dang tao..." : "Tao bao cao"}
          </button>
        </div>
      </section>

      {report && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
            <h3 style={{ margin: 0 }}>{reportType} - Tong so dong: {report.tongSoDong || 0}</h3>
            <button onClick={downloadXml} disabled={!xmlContent} style={{ ...btnStyle, background: "#3b82f6" }}>
              Tai file XML
            </button>
          </div>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                {reportType === "D02-LT" ? (
                  <>
                    <th style={th}>STT</th>
                    <th style={th}>Ma NV</th>
                    <th style={th}>Ho ten</th>
                    <th style={th}>Ma so BHXH</th>
                    <th style={th}>Loai</th>
                    <th style={th}>Ngay PS</th>
                    <th style={th}>Muc luong</th>
                    <th style={th}>Trang thai</th>
                  </>
                ) : (
                  <>
                    <th style={th}>STT</th>
                    <th style={th}>Ma NV</th>
                    <th style={th}>Ho ten</th>
                    <th style={th}>Ma so BHXH</th>
                    <th style={th}>Ngay cap</th>
                    <th style={th}>Loai de nghi</th>
                    <th style={th}>Ly do</th>
                  </>
                )}
              </tr>
            </thead>
            <tbody>
              {!report.rows?.length && (
                <tr>
                  <td colSpan={8} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Khong co du lieu trong ky</td>
                </tr>
              )}
              {report.rows?.map((r: any, idx: number) => (
                <tr key={idx} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td}>{idx + 1}</td>
                  <td style={td}>{r.maNv}</td>
                  <td style={td}>{r.hoTen}</td>
                  <td style={td}>{r.maSoBhxh}</td>
                  {reportType === "D02-LT" ? (
                    <>
                      <td style={td}>{r.loaiBienDong}</td>
                      <td style={td}>{r.ngayPhatSinh}</td>
                      <td style={td}>{r.mucLuongDong ? new Intl.NumberFormat("vi-VN").format(r.mucLuongDong) : "-"}</td>
                      <td style={td}><StatusChip status={r.trangThaiNop} /></td>
                    </>
                  ) : (
                    <>
                      <td style={td}>{r.ngayCapSo}</td>
                      <td style={td}>{r.loaiDeNghi}</td>
                      <td style={td}>{r.lyDo}</td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      )}

      {xmlContent && (
        <section style={{ background: "#1e293b", color: "#e2e8f0", padding: 16, borderRadius: 8 }}>
          <h3 style={{ marginTop: 0 }}>XML Preview ({Math.round(xmlContent.length / 1024)} KB)</h3>
          <pre style={{ maxHeight: 400, overflow: "auto", fontSize: 12, lineHeight: 1.4 }}>{xmlContent.slice(0, 4000)}{xmlContent.length > 4000 ? "\n... (truncated)" : ""}</pre>
        </section>
      )}
    </div>
  );
}

function StatusChip({ status }: { status: string }) {
  const color = status === "DA_NOP" ? "#10b981" : "#f59e0b";
  return <span style={{ background: color, color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{status}</span>;
}

const lblStyle: React.CSSProperties = { display: "block", fontSize: 12, color: "#64748b", marginBottom: 4 };
const inpStyle: React.CSSProperties = { padding: 8, width: 180, border: "1px solid #cbd5e1", borderRadius: 4 };
const btnStyle: React.CSSProperties = { padding: "8px 16px", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer" };
const th: React.CSSProperties = { padding: 10, textAlign: "left" };
const td: React.CSSProperties = { padding: 10 };
