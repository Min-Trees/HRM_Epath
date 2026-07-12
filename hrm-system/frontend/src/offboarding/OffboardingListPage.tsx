import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { offboardingApi } from "../mock/offboarding.mock";

export default function OffboardingListPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("");

  useEffect(() => {
    offboardingApi.list({ page: 0, size: 50 }).then((r) => {
      setData(r);
      setLoading(false);
    });
  }, []);

  if (loading) return <div style={{ padding: 24 }}>?ang t?i...</div>;

  const items = (data?.content || []).filter((c: any) =>
    !filter ||
    c.maNv?.toLowerCase().includes(filter.toLowerCase()) ||
    c.hoTen?.toLowerCase().includes(filter.toLowerCase()) ||
    c.soQuyetDinh?.toLowerCase().includes(filter.toLowerCase())
  );

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h2>H? s? ngh? vi?c (Offboarding)</h2>
        <button
          onClick={() => navigate("/offboarding/new")}
          style={{ padding: "8px 16px", background: "#0d9488", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer" }}
        >
          + T?o h? s?
        </button>
      </div>
      <input
        type="text"
        placeholder="Tìm theo mã NV, h? tên ho?c s? Q?..."
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
        style={{ padding: 8, width: "40%", marginBottom: 16, border: "1px solid #cbd5e1", borderRadius: 4 }}
      />
      <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
        <thead>
          <tr style={{ background: "#f1f5f9" }}>
            <th style={th}>Mã NV</th>
            <th style={th}>H? tên</th>
            <th style={th}>S? Q?</th>
            <th style={th}>Ngày ngh?</th>
            <th style={th}>Lý do</th>
            <th style={th}>Tr?ng thái</th>
            <th style={th}>Ti?n ??</th>
            <th style={th}></th>
          </tr>
        </thead>
        <tbody>
          {items.length === 0 && (
            <tr>
              <td colSpan={8} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>
                Không có d? li?u
              </td>
            </tr>
          )}
          {items.map((c: any) => (
            <tr key={c.caseId} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td}>{c.maNv}</td>
              <td style={td}>{c.hoTen}</td>
              <td style={td}>{c.soQuyetDinh}</td>
              <td style={td}>{c.ngayNghiViecCuoi}</td>
              <td style={td}>{c.lyDo}</td>
              <td style={td}>
                <StatusChip status={c.trangThai} />
              </td>
              <td style={td}>
                <ProgressBar pct={c.tienDoPhanTram || 0} />
              </td>
              <td style={td}>
                <button
                  onClick={() => navigate(`/offboarding/${c.caseId}`)}
                  style={{ color: "#0d9488", background: "none", border: "none", cursor: "pointer", textDecoration: "underline" }}
                >
                  Chi ti?t
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div style={{ marginTop: 8, color: "#64748b", fontSize: 13 }}>
        T?ng: {data?.totalElements || 0} h? s?
      </div>
    </div>
  );
}

const th: React.CSSProperties = { padding: 12, textAlign: "left", fontWeight: 600 };
const td: React.CSSProperties = { padding: 12 };

function StatusChip({ status }: { status: string }) {
  const colors: Record<string, string> = {
    MOI_TAO: "#94a3b8",
    CHO_DUYET: "#f59e0b",
    DANG_THUC_HIEN: "#3b82f6",
    CHO_QUYET_TOAN: "#8b5cf6",
    HOAN_THANH: "#10b981",
    HUY: "#ef4444",
  };
  return (
    <span
      style={{
        background: colors[status] || "#94a3b8",
        color: "#fff",
        padding: "2px 8px",
        borderRadius: 12,
        fontSize: 12,
      }}
    >
      {status}
    </span>
  );
}

function ProgressBar({ pct }: { pct: number }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
      <div style={{ width: 80, height: 8, background: "#e2e8f0", borderRadius: 4, overflow: "hidden" }}>
        <div style={{ width: `${pct}%`, height: "100%", background: "#10b981" }} />
      </div>
      <span style={{ fontSize: 12 }}>{pct.toFixed(0)}%</span>
    </div>
  );
}
