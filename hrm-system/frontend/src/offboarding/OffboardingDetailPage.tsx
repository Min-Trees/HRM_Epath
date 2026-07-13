import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { offboardingApi } from "../api";

export default function OffboardingDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [caseData, setCaseData] = useState<any>(null);
  const [tasks, setTasks] = useState<any[]>([]);
  const [severance, setSeverance] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    Promise.all([
      offboardingApi.get(id),
      offboardingApi.listTasks(id),
      offboardingApi.getSeverance(id),
    ]).then(([c, t, s]) => {
      setCaseData(c);
      setTasks(Array.isArray(t) ? t : []);
      setSeverance(s);
      setLoading(false);
    });
  }, [id]);

  if (loading) return <div style={{ padding: 24 }}>?ang t?i...</div>;
  if (!caseData) return <div style={{ padding: 24 }}>Không tìm th?y h? s?.</div>;

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif", maxWidth: 1100, margin: "0 auto" }}>
      <button onClick={() => navigate("/offboarding")} style={{ marginBottom: 16, color: "#0d9488", background: "none", border: "none", cursor: "pointer" }}>
        ? Quay l?i danh sách
      </button>
      <h2>H? s? ngh? vi?c · {caseData.soQuyetDinh}</h2>

      <section style={cardStyle}>
        <h3 style={cardTitle}>Thông tin chung</h3>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
          <Field label="Mã NV" value={caseData.maNv} />
          <Field label="H? tên" value={caseData.hoTen} />
          <Field label="S? quy?t ??nh" value={caseData.soQuyetDinh} />
          <Field label="Ngày quy?t ??nh" value={caseData.ngayQuyetDinh} />
          <Field label="Ngày ngh? vi?c cu?i" value={caseData.ngayNghiViecCuoi} />
          <Field label="Ngày chính th?c ngh?" value={caseData.ngayChinhThucNghi} />
          <Field label="Lý do" value={caseData.lyDo} />
          <Field label="Tr?ng thái" value={caseData.trangThai} />
          <Field label="Ng??i t?o" value={caseData.nguoiTaoId} />
          <Field label="Ng??i duy?t" value={caseData.nguoiDuyetId || "—"} />
        </div>
        {caseData.lyDoChiTiet && (
          <div style={{ marginTop: 12 }}>
            <strong>Lý do chi ti?t:</strong>
            <div style={{ padding: 8, background: "#f8fafc", borderRadius: 4, marginTop: 4 }}>{caseData.lyDoChiTiet}</div>
          </div>
        )}
      </section>

      <section style={cardStyle}>
        <h3 style={cardTitle}>
          Checklist ({tasks.filter((t) => t.trangThai === "HOAN_THANH" || t.trangThai === "KHONG_AP_DUNG").length}/{tasks.length})
        </h3>
        {tasks.length === 0 && <div style={{ color: "#94a3b8" }}>Ch?a có checklist.</div>}
        {tasks.map((t, idx) => (
          <div key={t.taskId} style={{ display: "flex", alignItems: "center", padding: "10px 8px", borderBottom: "1px solid #e2e8f0", gap: 12 }}>
            <input
              type="checkbox"
              checked={t.trangThai === "HOAN_THANH"}
              onChange={(e) => updateTask(t, e.target.checked ? "HOAN_THANH" : "CHUA_LAM", idx)}
            />
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 500 }}>{idx + 1}. {t.loaiTask}</div>
              {t.moTa && <div style={{ color: "#64748b", fontSize: 13 }}>{t.moTa}</div>}
            </div>
            <select
              value={t.trangThai}
              onChange={(e) => updateTask(t, e.target.value, idx)}
              style={{ padding: 4 }}
            >
              <option value="CHUA_LAM">Ch?a làm</option>
              <option value="DANG_LAM">?ang làm</option>
              <option value="HOAN_THANH">Hoàn thành</option>
              <option value="KHONG_AP_DUNG">Không áp d?ng</option>
            </select>
            {t.hanHoanThanh && (
              <span style={{ color: "#64748b", fontSize: 13 }}>H?n: {t.hanHoanThanh}</span>
            )}
          </div>
        ))}
      </section>

      <section style={cardStyle}>
        <h3 style={cardTitle}>Tr? c?p thôi vi?c (Severance)</h3>
        {severance ? (
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <Field label="Th?i gian làm vi?c" value={`${severance.thoiGianLamViecThang} tháng`} />
            <Field label="S? n?m thâm niên" value={severance.soNamThamNien} />
            <Field label="L??ng bình quân 6 tháng" value={fmtVND(severance.luongThoiViecBinhQuan)} />
            <Field label="H? s?" value={severance.heSo} />
            <Field label="S? ti?n tr? c?p" value={fmtVND(severance.soTienTroCap)} highlight />
            <Field label="Ng??i tính" value={severance.nguoiTinhId || "—"} />
          </div>
        ) : (
          <div style={{ color: "#64748b" }}>
            Ch?a có phép tính. S? d?ng API <code>POST /api/v1/hr/offboarding/severance/preview</code> ?? tính tr??c.
          </div>
        )}
      </section>
    </div>
  );

  function updateTask(t: any, newStatus: string, idx: number) {
    if (!id) return;
    const updated = [...tasks];
    updated[idx] = { ...t, trangThai: newStatus };
    setTasks(updated);
    offboardingApi.updateTask(t.taskId, newStatus).then(() => {
      offboardingApi.get(id).then(setCaseData);
    });
  }
}

function Field({ label, value, highlight }: { label: string; value: any; highlight?: boolean }) {
  return (
    <div>
      <div style={{ color: "#64748b", fontSize: 12 }}>{label}</div>
      <div style={{ fontWeight: 500, color: highlight ? "#0d9488" : undefined }}>{value}</div>
    </div>
  );
}

function fmtVND(v: any): string {
  if (!v) return "—";
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(v));
}

const cardStyle: React.CSSProperties = {
  background: "#fff",
  padding: 16,
  marginBottom: 16,
  borderRadius: 8,
  boxShadow: "0 1px 3px rgba(0,0,0,0.08)",
};
const cardTitle: React.CSSProperties = { marginTop: 0, marginBottom: 12, color: "#0f172a" };
