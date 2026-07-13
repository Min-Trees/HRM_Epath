import React, { useEffect, useState } from "react";
import { payrollRunApi } from "../api";

const STATUS_COLORS: Record<string, string> = {
  CHO_CHAY: "#94a3b8",
  DANG_CHAY: "#3b82f6",
  DA_CHAY: "#0d9488",
  DA_DUYET_CAP_1: "#f59e0b",
  DA_DUYET_CAP_2: "#10b981",
  DA_CHI_TRA: "#059669",
  HUY: "#ef4444",
};

export default function PayrollRunPage() {
  const [runs, setRuns] = useState<any[]>([]);
  const [selected, setSelected] = useState<any>(null);
  const [creating, setCreating] = useState(false);
  const [newThang, setNewThang] = useState(new Date().getMonth() + 1);
  const [newNam, setNewNam] = useState(new Date().getFullYear());

  const refresh = () => payrollRunApi.list().then((r) => setRuns(Array.isArray(r) ? r : r.content || []));

  useEffect(() => { refresh(); }, []);

  const create = async () => {
    try {
      await payrollRunApi.create(newThang, newNam);
      await refresh();
      setCreating(false);
    } catch (e: any) {
      alert(e.message || "Loi");
    }
  };

  const transition = async (action: string) => {
    if (!selected) return;
    switch (action) {
      case "start": await payrollRunApi.start(selected.kyLinhId); break;
      case "approve-cap-1": await payrollRunApi.approveCap1(selected.kyLinhId); break;
      case "approve-cap-2": await payrollRunApi.approveCap2(selected.kyLinhId); break;
      case "pay-paid": await payrollRunApi.markPaid(selected.kyLinhId); break;
      case "cancel": await payrollRunApi.cancel(selected.kyLinhId, "Huy boi nguoi dung"); break;
    }
    await refresh();
    const updated = await payrollRunApi.get(selected.kyLinhId);
    setSelected(updated);
  };

  const downloadPayslip = async (kyLinhId: string, nvId: string, maNv: string) => {
    try {
      const html = await payrollRunApi.getPayslip(kyLinhId, nvId);
      const blob = new Blob([html], { type: "text/html" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `phieu-luong-${maNv}.html`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (e: any) {
      alert(e.message);
    }
  };

  const onSelect = async (r: any) => {
    const full = await payrollRunApi.get(r.kyLinhId);
    setSelected(full);
  };

  const canStart = (s: string) => s === "CHO_CHAY";
  const canApprove1 = (s: string) => s === "DA_CHAY";
  const canApprove2 = (s: string) => s === "DA_DUYET_CAP_1";
  const canPay = (s: string) => s === "DA_DUYET_CAP_2";
  const canCancel = (s: string) => s !== "DA_CHI_TRA" && s !== "HUY";

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <h2>Module Payroll Run (T19)</h2>
      <p style={{ color: "#64748b" }}>
        Kỳ lĩnh lương với workflow: Chạy kỳ → Duyệt cấp 1 → Duyệt cấp 2 → Chi trả.
      </p>

      <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h3>Danh sách kỳ lĩnh</h3>
          <button onClick={() => setCreating(true)} style={{ ...btnPrimary }}>+ Tạo kỳ mới</button>
        </div>
        <table style={{ width: "100%", borderCollapse: "collapse", marginTop: 12 }}>
          <thead>
            <tr style={{ background: "#f1f5f9" }}>
              <th style={th}>Mã kỳ</th>
              <th style={th}>Kỳ</th>
              <th style={th}>Trạng thái</th>
              <th style={th}>SL NV</th>
              <th style={th}>Tổng thực lĩnh</th>
              <th style={th}>BHXH NLĐ</th>
              <th style={th}>Thuế TNCN</th>
              <th style={th}>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {runs.map((r) => (
              <tr key={r.kyLinhId} style={{ borderTop: "1px solid #e2e8f0" }}>
                <td style={td}>{r.maKyLinh}</td>
                <td style={td}>{String(r.thang).padStart(2, "0")}/{r.nam}</td>
                <td style={td}>
                  <span style={{
                    background: STATUS_COLORS[r.trangThai] ?? "#64748b",
                    color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12,
                  }}>{r.trangThai}</span>
                </td>
                <td style={td}>{r.tongNhanVien}</td>
                <td style={td}>{fmtVND(r.tongThucLinh)}</td>
                <td style={td}>{fmtVND(r.tongBhxhNld)}</td>
                <td style={td}>{fmtVND(r.tongThueTncn)}</td>
                <td style={td}>
                  <button onClick={() => onSelect(r)} style={btn}>Chi tiết</button>
                </td>
              </tr>
            ))}
            {runs.length === 0 && (
              <tr><td colSpan={8} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chưa có kỳ lĩnh nào</td></tr>
            )}
          </tbody>
        </table>
      </section>

      {creating && (
        <div style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <h3>Tạo kỳ lĩnh mới</h3>
          <div style={{ display: "flex", gap: 12, alignItems: "flex-end" }}>
            <Field label="Tháng">
              <select value={newThang} onChange={(e) => setNewThang(Number(e.target.value))} style={inp}>
                {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                  <option key={m} value={m}>Tháng {m}</option>
                ))}
              </select>
            </Field>
            <Field label="Năm">
              <input type="number" value={newNam} onChange={(e) => setNewNam(Number(e.target.value))} style={inp} />
            </Field>
            <button onClick={create} style={btnPrimary}>Tạo kỳ</button>
            <button onClick={() => setCreating(false)} style={{ ...btn, background: "#94a3b8" }}>Hủy</button>
          </div>
        </div>
      )}

      {selected && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <h3>Chi tiết kỳ lĩnh {selected.maKyLinh} - {selected.thang}/{selected.nam}</h3>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginBottom: 16 }}>
            <Stat label="Trạng thái" value={selected.trangThai} color={STATUS_COLORS[selected.trangThai]} />
            <Stat label="Người chạy" value={selected.nguoiChayId?.slice(0, 8) || "-"} />
            <Stat label="Ngày chạy" value={selected.ngayChay || "-"} />
            <Stat label="Duyệt cấp 1" value={selected.nguoiDuyetCap1Id?.slice(0, 8) || "-"} />
            <Stat label="Ngày duyệt 1" value={selected.ngayDuyetCap1 || "-"} />
            <Stat label="Duyệt cấp 2" value={selected.nguoiDuyetCap2Id?.slice(0, 8) || "-"} />
          </div>

          <h4>Workflow</h4>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 16 }}>
            <button onClick={() => transition("start")} disabled={!canStart(selected.trangThai)} style={btnPrimary}>▶ Chạy kỳ lương</button>
            <button onClick={() => transition("approve-cap-1")} disabled={!canApprove1(selected.trangThai)} style={{ ...btn, background: "#f59e0b" }}>✓ Duyệt cấp 1</button>
            <button onClick={() => transition("approve-cap-2")} disabled={!canApprove2(selected.trangThai)} style={{ ...btn, background: "#3b82f6" }}>✓ Duyệt cấp 2</button>
            <button onClick={() => transition("pay-paid")} disabled={!canPay(selected.trangThai)} style={{ ...btn, background: "#059669" }}>💰 Chi trả</button>
            <button onClick={() => transition("cancel")} disabled={!canCancel(selected.trangThai)} style={{ ...btn, background: "#ef4444" }}>✕ Hủy kỳ</button>
          </div>

          <h4>Phiếu lương mẫu</h4>
          <p style={{ color: "#64748b", fontSize: 13 }}>
            Nhấn "Tải phiếu lương" để xem payslip HTML cho NV đầu tiên trong hệ thống.
          </p>
          <button onClick={() => downloadPayslip(selected.kyLinhId, "nv-1", "NV-001")}
                  style={{ ...btn, background: "#6366f1", marginTop: 8 }}>
            📄 Tải phiếu lương NV-001
          </button>

          <h4 style={{ marginTop: 24 }}>Audit log</h4>
          <div style={{ background: "#f8fafc", padding: 12, borderRadius: 6, fontSize: 13 }}>
            <div>→ CHAY_KY_LUONG: {selected.ngayChay || "(chua chay)"}</div>
            <div>→ DUYET_CAP_1: {selected.ngayDuyetCap1 || "(chua duyet)"}</div>
            <div>→ DUYET_CAP_2: {selected.ngayDuyetCap2 || "(chua duyet)"}</div>
            <div>→ CHI_TRA: {selected.ngayChiTraThucTe || "(chua chi tra)"}</div>
            {selected.fileZipUrl && <div>📦 File zip: {selected.fileZipUrl}</div>}
          </div>
        </section>
      )}
    </div>
  );
}

function Stat({ label, value, color }: any) {
  return (
    <div style={{ background: "#f8fafc", padding: 12, borderRadius: 6 }}>
      <div style={{ fontSize: 12, color: "#64748b" }}>{label}</div>
      <div style={{ fontWeight: 600, color: color || "#0f172a" }}>{value}</div>
    </div>
  );
}

function Field({ label, children }: any) {
  return (
    <div>
      <label style={lbl}>{label}</label>
      {children}
    </div>
  );
}

function fmtVND(v: any) {
  if (v === null || v === undefined) return "-";
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(v));
}

const lbl: React.CSSProperties = { display: "block", fontSize: 12, color: "#64748b", marginBottom: 4 };
const inp: React.CSSProperties = { padding: 8, border: "1px solid #cbd5e1", borderRadius: 4 };
const btn: React.CSSProperties = { padding: "6px 12px", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 13 };
const btnPrimary: React.CSSProperties = { ...btn, background: "#0d9488" };
const th: React.CSSProperties = { padding: 10, textAlign: "left" };
const td: React.CSSProperties = { padding: 10 };
