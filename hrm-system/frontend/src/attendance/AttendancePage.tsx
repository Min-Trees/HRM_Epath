// T08 + T09: Shift management + Attendance page
import { useState, useEffect } from "react";
import { PageHeader } from "../components/SharedComponents";
import { shiftMock } from "../mock/shift.mock";
import { attendanceMock } from "../mock/attendance.mock";
import { employeeMock } from "../mock/employee.mock";

const LOAI_CA: Record<string, string> = {
  HANH_CHINH: "Hanh chinh", CA_KIP: "Ca kip", FLEXIBLE: "Linh hoat",
};

const LOAI_NGOAI_LE: Record<string, { bg: string; color: string; label: string }> = {
  KHONG_NGOAI_LE: { bg: "#dcfce7", color: "#166534", label: "Khong ngoai le" },
  DI_TRE: { bg: "#fef3c7", color: "#92400e", label: "Di tre" },
  VE_SOM: { bg: "#fef3c7", color: "#92400e", label: "Ve som" },
  THIEU_CONG: { bg: "#fee2e2", color: "#991b1b", label: "Thieu cong" },
  LAM_NGOAI_CA: { bg: "#dbeafe", color: "#1e40af", label: "Lam ngoai ca" },
};

function Chip({ bg, color, children }: any) {
  return <span style={{ background: bg, color, padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{children}</span>;
}

export default function AttendancePage() {
  const [tab, setTab] = useState<"shifts" | "assign" | "timelog">("shifts");
  const [shifts, setShifts] = useState<any[]>([]);
  const [assignments, setAssignments] = useState<any[]>([]);
  const [timelogs, setTimelogs] = useState<any[]>([]);
  const [employees, setEmployees] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showShiftForm, setShowShiftForm] = useState(false);
  const [showAssignForm, setShowAssignForm] = useState(false);
  const [editShift, setEditShift] = useState<any>(null);
  const [shiftForm, setShiftForm] = useState({ maCa: "", tenCa: "", loaiCa: "HANH_CHINH", gioBatDau: "08:00", gioKetThuc: "17:00", soGioChuan: 8.0, quaNgay: false });
  const [assignForm, setAssignForm] = useState({ nhanVienIds: [] as string[], caId: "", tuNgay: "", denNgay: "", mode: "bulk" as "bulk" | "single" });
  const [filterMonth, setFilterMonth] = useState(() => {
    const d = new Date(); return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}`;
  });
  const [msg, setMsg] = useState<{ type: string; text: string } | null>(null);

  useEffect(() => { loadShifts(); loadEmployees(); }, []);
  useEffect(() => { if (tab === "assign") loadAssignments(); }, [tab]);
  useEffect(() => { if (tab === "timelog") loadTimelogs(); }, [tab, filterMonth]);

  async function loadShifts() {
    setLoading(true);
    try { setShifts(await shiftMock.listShifts(true)); } finally { setLoading(false); }
  }

  async function loadEmployees() {
    try { const r = await employeeMock.list({ page: 0, size: 100 }); setEmployees(r.content); } catch {}
  }

  async function loadAssignments() {
    const [y, m] = filterMonth.split("-");
    const days = new Date(parseInt(y), parseInt(m), 0).getDate();
    const from = `${y}-${m}-01`;
    const to = `${y}-${m}-${String(days).padStart(2, "0")}`;
    try {
      const all = await shiftMock.listAssignments("", from, to);
      setAssignments(all.slice(0, 50));
    } catch {}
  }

  async function loadTimelogs() {
    const [y, m] = filterMonth.split("-");
    const days = new Date(parseInt(y), parseInt(m), 0).getDate();
    const from = `${y}-${m}-01`;
    const to = `${y}-${m}-${String(days).padStart(2, "0")}`;
    try { setTimelogs(await attendanceMock.list({ from, to })); } catch {}
  }

  function showMsg(type: string, text: string) {
    setMsg({ type, text }); setTimeout(() => setMsg(null), 3000);
  }

  async function handleSaveShift() {
    try {
      if (editShift) await shiftMock.updateShift(editShift.id, shiftForm);
      else await shiftMock.createShift(shiftForm);
      setShowShiftForm(false); setEditShift(null);
      setShiftForm({ maCa: "", tenCa: "", loaiCa: "HANH_CHINH", gioBatDau: "08:00", gioKetThuc: "17:00", soGioChuan: 8.0, quaNgay: false });
      loadShifts();
      showMsg("ok", "Luu thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleCloseShift(id: string) {
    try { await shiftMock.closeShift(id); loadShifts(); showMsg("ok", "Dong ca thanh cong"); } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleAssign() {
    try {
      if (assignForm.mode === "bulk") {
        if (!assignForm.caId || !assignForm.tuNgay || !assignForm.denNgay) { showMsg("err", "Dien day du thong tin"); return; }
        await shiftMock.assignBulk({ nhanVienIds: assignForm.nhanVienIds, caId: assignForm.caId, tuNgay: assignForm.tuNgay, denNgay: assignForm.denNgay });
      } else {
        if (!assignForm.nhanVienIds[0] || !assignForm.caId || !assignForm.tuNgay) { showMsg("err", "Dien day du thong tin"); return; }
        await shiftMock.assignSingle({ nhanVienId: assignForm.nhanVienIds[0], caId: assignForm.caId, ngayApDung: assignForm.tuNgay });
      }
      setShowAssignForm(false); setAssignForm({ nhanVienIds: [], caId: "", tuNgay: "", denNgay: "", mode: "bulk" });
      loadAssignments();
      showMsg("ok", "Phan ca thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  const cardStyle: any = { background: "#fff", borderRadius: 12, padding: 24, width: 480 };
  const inputStyle: any = { width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14, boxSizing: "border-box", marginBottom: 12 };

  return (
    <div>
      <PageHeader title="Cham cong" subtitle="T08 — Ca lam viec & Phan ca | T09 — Cham cong chi tiet" />
      <div style={{ padding: "16px 24px" }}>
        {msg && <div style={{ padding: "8px 16px", background: msg.type === "ok" ? "#dcfce7" : "#fee2e2", color: msg.type === "ok" ? "#166534" : "#991b1b", borderRadius: 6, marginBottom: 12 }}>{msg.text}</div>}

        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          {[
            { key: "shifts", label: "Ca lam viec", color: "#3b82f6" },
            { key: "assign", label: "Phan ca", color: "#8b5cf6" },
            { key: "timelog", label: "Cham cong", color: "#10b981" },
          ].map(t => (
            <button key={t.key} onClick={() => setTab(t.key as any)} style={{ padding: "8px 16px", background: tab === t.key ? t.color : "#e2e8f0", color: tab === t.key ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>
              {t.label}
            </button>
          ))}
          <input type="month" value={filterMonth} onChange={e => setFilterMonth(e.target.value)} style={{ ...inputStyle, width: 160, marginBottom: 0, marginLeft: 8 }} />
        </div>

        {tab === "shifts" && (
          <div>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: 12 }}>
              <button onClick={() => { setEditShift(null); setShiftForm({ maCa: "", tenCa: "", loaiCa: "HANH_CHINH", gioBatDau: "08:00", gioKetThuc: "17:00", soGioChuan: 8.0, quaNgay: false }); setShowShiftForm(true); }} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Them ca</button>
            </div>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Dang tai...</div> : (
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Ma ca</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Ten ca</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Loai</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Gio BD</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Gio KT</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>So gio</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Qua ngay</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Trang thai</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Hanh dong</th>
                </tr></thead>
                <tbody>
                  {shifts.map(s => (
                    <tr key={s.id} style={{ borderTop: "1px solid #e2e8f0" }}>
                      <td style={{ padding: "10px 16px", fontFamily: "monospace", fontWeight: 700 }}>{s.maCa}</td>
                      <td style={{ padding: "10px 8px" }}>{s.tenCa}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>{LOAI_CA[s.loaiCa] || s.loaiCa}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{s.gioBatDau}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{s.gioKetThuc}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>{s.soGioChuan}h</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>{s.quaNgay ? "Co" : "-"}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}><Chip bg={s.active ? "#dcfce7" : "#fee2e2"} color={s.active ? "#166534" : "#991b1b"}>{s.active ? "Hoat dong" : "Dong"}</Chip></td>
                      <td style={{ padding: "10px 8px" }}>
                        <button onClick={() => { setEditShift(s); setShiftForm({ maCa: s.maCa, tenCa: s.tenCa, loaiCa: s.loaiCa, gioBatDau: s.gioBatDau, gioKetThuc: s.gioKetThuc, soGioChuan: s.soGioChuan, quaNgay: s.quaNgay }); setShowShiftForm(true); }} style={{ marginRight: 4, padding: "4px 8px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Sua</button>
                        {s.active && <button onClick={() => handleCloseShift(s.id)} style={{ padding: "4px 8px", background: "#ef4444", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Dong</button>}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {tab === "assign" && (
          <div>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: 12 }}>
              <button onClick={() => setShowAssignForm(true)} style={{ padding: "8px 16px", background: "#8b5cf6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Phan ca</button>
            </div>
            <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
              <div style={{ padding: "8px 16px", background: "#f8fafc", borderBottom: "1px solid #e2e8f0", fontSize: 13, color: "#64748b" }}>Lich phan ca thang {filterMonth} ({assignments.length} ban ghi)</div>
              {assignments.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Chua co phan ca</div>}
              {assignments.map((a, i) => (
                <div key={a.id} style={{ padding: "10px 16px", borderBottom: i < assignments.length - 1 ? "1px solid #f1f5f9" : "none", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div>
                    <span style={{ fontFamily: "monospace", fontWeight: 700, marginRight: 8 }}>{a.maNv}</span>
                    <span>{a.hoTen}</span>
                  </div>
                  <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
                    <span style={{ fontSize: 13, fontFamily: "monospace" }}>{a.ngayApDung}</span>
                    <Chip bg="#dbeafe" color="#1e40af">{a.tenCa}</Chip>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {tab === "timelog" && (
          <div>
            <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
              {timelogs.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Chua co du lieu cham cong</div>}
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Ma NV</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Ho ten</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Ngay</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Gio vao</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Gio ra</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Nguon</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Ngoai le</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>So gio</th>
                </tr></thead>
                <tbody>
                  {timelogs.map((t, i) => {
                    const exc = LOAI_NGOAI_LE[t.loaiNgoaiLe] || LOAI_NGOAI_LE.KHONG_NGOAI_LE;
                    return (
                      <tr key={t.id} style={{ borderTop: "1px solid #f1f5f9", background: t.loaiNgoaiLe !== "KHONG_NGOAI_LE" ? exc.bg + "22" : "transparent" }}>
                        <td style={{ padding: "8px 16px", fontFamily: "monospace", fontWeight: 700, fontSize: 13 }}>{t.maNv}</td>
                        <td style={{ padding: "8px 8px", fontSize: 13 }}>{t.hoTen}</td>
                        <td style={{ padding: "8px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{t.ngayChamCong}</td>
                        <td style={{ padding: "8px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{t.gioVao || "-"}</td>
                        <td style={{ padding: "8px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{t.gioRa || "-"}</td>
                        <td style={{ padding: "8px 8px", textAlign: "center", fontSize: 12, color: "#64748b" }}>{t.nguon}</td>
                        <td style={{ padding: "8px 8px", textAlign: "center" }}><Chip bg={exc.bg} color={exc.color}>{exc.label}</Chip></td>
                        <td style={{ padding: "8px 8px", textAlign: "center", fontSize: 13, fontWeight: 600 }}>{t.soGioCong || "-"}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {showShiftForm && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowShiftForm(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>{editShift ? "Sua ca lam viec" : "Them ca lam viec"}</h3>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ma ca *</label>
            <input style={inputStyle} value={shiftForm.maCa} onChange={e => setShiftForm(f => ({ ...f, maCa: e.target.value }))} placeholder="VD: HC1" />
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ten ca *</label>
            <input style={inputStyle} value={shiftForm.tenCa} onChange={e => setShiftForm(f => ({ ...f, tenCa: e.target.value }))} placeholder="VD: Hanh chinh 1" />
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Loai ca</label>
            <select style={inputStyle} value={shiftForm.loaiCa} onChange={e => setShiftForm(f => ({ ...f, loaiCa: e.target.value }))}>
              <option value="HANH_CHINH">Hanh chinh</option><option value="CA_KIP">Ca kip</option><option value="FLEXIBLE">Linh hoat</option>
            </select>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Gio bat dau</label><input style={inputStyle} type="time" value={shiftForm.gioBatDau} onChange={e => setShiftForm(f => ({ ...f, gioBatDau: e.target.value }))} /></div>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Gio ket thuc</label><input style={inputStyle} type="time" value={shiftForm.gioKetThuc} onChange={e => setShiftForm(f => ({ ...f, gioKetThuc: e.target.value }))} /></div>
            </div>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>So gio chuan</label>
            <input style={inputStyle} type="number" step="0.5" value={shiftForm.soGioChuan} onChange={e => setShiftForm(f => ({ ...f, soGioChuan: parseFloat(e.target.value) || 0 }))} />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowShiftForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huy</button>
              <button onClick={handleSaveShift} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Luu</button>
            </div>
          </div>
        </div>
      )}

      {showAssignForm && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowAssignForm(false)}>
          <div style={{ ...cardStyle, width: 520 }} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Phan ca</h3>
            <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
              <button onClick={() => setAssignForm(f => ({ ...f, mode: "bulk" }))} style={{ padding: "6px 12px", background: assignForm.mode === "bulk" ? "#8b5cf6" : "#e2e8f0", color: assignForm.mode === "bulk" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 13 }}>Nhieu NV</button>
              <button onClick={() => setAssignForm(f => ({ ...f, mode: "single" }))} style={{ padding: "6px 12px", background: assignForm.mode === "single" ? "#8b5cf6" : "#e2e8f0", color: assignForm.mode === "single" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 13 }}>Mot NV</button>
            </div>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Nhan vien</label>
            {assignForm.mode === "bulk" ? (
              <div style={{ maxHeight: 120, overflowY: "auto", border: "1px solid #cbd5e1", borderRadius: 6, padding: 8, marginBottom: 12 }}>
                {employees.map(e => (
                  <label key={e.nhanVienId} style={{ display: "flex", gap: 8, alignItems: "center", marginBottom: 4, cursor: "pointer" }}>
                    <input type="checkbox" checked={assignForm.nhanVienIds.includes(e.nhanVienId)} onChange={ev => {
                      setAssignForm(f => ({ ...f, nhanVienIds: ev.target.checked ? [...f.nhanVienIds, e.nhanVienId] : f.nhanVienIds.filter(id => id !== e.nhanVienId) }));
                    }} />
                    <span style={{ fontSize: 13 }}>{e.maNv} — {e.hoTen}</span>
                  </label>
                ))}
              </div>
            ) : (
              <select style={inputStyle} value={assignForm.nhanVienIds[0] || ""} onChange={e => setAssignForm(f => ({ ...f, nhanVienIds: [e.target.value] }))}>
                <option value="">Chon nhan vien</option>
                {employees.map(e => <option key={e.nhanVienId} value={e.nhanVienId}>{e.maNv} — {e.hoTen}</option>)}
              </select>
            )}
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ca lam viec</label>
            <select style={inputStyle} value={assignForm.caId} onChange={e => setAssignForm(f => ({ ...f, caId: e.target.value }))}>
              <option value="">Chon ca</option>
              {shifts.filter(s => s.active).map(s => <option key={s.id} value={s.id}>{s.maCa} — {s.tenCa}</option>)}
            </select>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>{assignForm.mode === "bulk" ? "Tu ngay" : "Ngay ap dung"}</label><input style={inputStyle} type="date" value={assignForm.tuNgay} onChange={e => setAssignForm(f => ({ ...f, tuNgay: e.target.value }))} /></div>
              {assignForm.mode === "bulk" && <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Den ngay</label><input style={inputStyle} type="date" value={assignForm.denNgay} onChange={e => setAssignForm(f => ({ ...f, denNgay: e.target.value }))} /></div>}
            </div>
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowAssignForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huy</button>
              <button onClick={handleAssign} style={{ padding: "8px 16px", background: "#8b5cf6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Phan ca</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
