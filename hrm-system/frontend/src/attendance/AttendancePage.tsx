// T08 + T09: Trang quản lý ca làm việc & chấm công
import { useState, useEffect } from "react";
import { PageHeader } from "../components/SharedComponents";
import { shiftApi, shiftAssignmentApi, timeLogApi } from "../api";
import { employeeApi } from "../api";

const LOAI_CA: Record<string, string> = {
  HANH_CHINH: "Hành chính", CA_KIP: "Ca kíp", FLEXIBLE: "Linh hoạt",
};

const LOAI_NGOAI_LE: Record<string, { bg: string; color: string; label: string }> = {
  KHONG_NGOAI_LE: { bg: "#dcfce7", color: "#166534", label: "Không ngoại lệ" },
  DI_TRE: { bg: "#fef3c7", color: "#92400e", label: "Đi trễ" },
  VE_SOM: { bg: "#fef3c7", color: "#92400e", label: "Về sớm" },
  THIEU_CONG: { bg: "#fee2e2", color: "#991b1b", label: "Thiếu công" },
  LAM_NGOAI_CA: { bg: "#dbeafe", color: "#1e40af", label: "Làm ngoài ca" },
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
    try {
      const data = await shiftApi.list(true);
      setShifts(Array.isArray(data) ? data : data.content || []);
    } finally { setLoading(false); }
  }

  async function loadEmployees() {
    try { const r = await employeeApi.list("", undefined, undefined, 0, 100); setEmployees(r.content); } catch {}
  }

  async function loadAssignments() {
    const [y, m] = filterMonth.split("-");
    const days = new Date(parseInt(y), parseInt(m), 0).getDate();
    const from = `${y}-${m}-01`;
    const to = `${y}-${m}-${String(days).padStart(2, "0")}`;
    try {
      const data = await shiftAssignmentApi.list("", from, to);
      setAssignments(Array.isArray(data) ? data.slice(0, 50) : (data.content || []).slice(0, 50));
    } catch {}
  }

  async function loadTimelogs() {
    const [y, m] = filterMonth.split("-");
    const days = new Date(parseInt(y), parseInt(m), 0).getDate();
    const from = `${y}-${m}-01`;
    const to = `${y}-${m}-${String(days).padStart(2, "0")}`;
    try {
      const data = await timeLogApi.list("", from, to);
      setTimelogs(Array.isArray(data) ? data : data.content || []);
    } catch {}
  }

  function showMsg(type: string, text: string) {
    setMsg({ type, text }); setTimeout(() => setMsg(null), 3000);
  }

  async function handleSaveShift() {
    try {
      if (editShift) await shiftApi.update(editShift.id, shiftForm);
      else await shiftApi.create(shiftForm);
      setShowShiftForm(false); setEditShift(null);
      setShiftForm({ maCa: "", tenCa: "", loaiCa: "HANH_CHINH", gioBatDau: "08:00", gioKetThuc: "17:00", soGioChuan: 8.0, quaNgay: false });
      loadShifts();
      showMsg("ok", "Lưu thành công");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleCloseShift(id: string) {
    try { await shiftApi.close(id); loadShifts(); showMsg("ok", "Đóng ca thành công"); } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleAssign() {
    try {
      if (assignForm.mode === "bulk") {
        if (!assignForm.caId || !assignForm.tuNgay || !assignForm.denNgay) { showMsg("err", "Điền đầy đủ thông tin"); return; }
        await shiftAssignmentApi.assign({ nhanVienIds: assignForm.nhanVienIds, caId: assignForm.caId, tuNgay: assignForm.tuNgay, denNgay: assignForm.denNgay });
      } else {
        if (!assignForm.nhanVienIds[0] || !assignForm.caId || !assignForm.tuNgay) { showMsg("err", "Điền đầy đủ thông tin"); return; }
        await shiftAssignmentApi.assign({ nhanVienId: assignForm.nhanVienIds[0], caId: assignForm.caId, ngayApDung: assignForm.tuNgay });
      }
      setShowAssignForm(false); setAssignForm({ nhanVienIds: [], caId: "", tuNgay: "", denNgay: "", mode: "bulk" });
      loadAssignments();
      showMsg("ok", "Phân ca thành công");
    } catch (e: any) { showMsg("err", e.message); }
  }

  const cardStyle: any = { background: "#fff", borderRadius: 12, padding: 24, width: 480 };
  const inputStyle: any = { width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14, boxSizing: "border-box", marginBottom: 12 };

  return (
    <div>
      <PageHeader title="Chấm công" subtitle="T08 — Ca làm việc & Phân ca | T09 — Chấm công chi tiết" />
      <div style={{ padding: "16px 24px" }}>
        {msg && <div style={{ padding: "8px 16px", background: msg.type === "ok" ? "#dcfce7" : "#fee2e2", color: msg.type === "ok" ? "#166534" : "#991b1b", borderRadius: 6, marginBottom: 12 }}>{msg.text}</div>}

        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          {[
            { key: "shifts", label: "Ca làm việc", color: "#3b82f6" },
            { key: "assign", label: "Phân ca", color: "#8b5cf6" },
            { key: "timelog", label: "Chấm công", color: "#10b981" },
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
              <button onClick={() => { setEditShift(null); setShiftForm({ maCa: "", tenCa: "", loaiCa: "HANH_CHINH", gioBatDau: "08:00", gioKetThuc: "17:00", soGioChuan: 8.0, quaNgay: false }); setShowShiftForm(true); }} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Thêm ca</button>
            </div>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Đang tải...</div> : (
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Mã ca</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Tên ca</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Loại</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Giờ BĐ</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Giờ KT</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Số giờ</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Qua ngày</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Trạng thái</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Hành động</th>
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
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>{s.quaNgay ? "Có" : "-"}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}><Chip bg={s.active ? "#dcfce7" : "#fee2e2"} color={s.active ? "#166534" : "#991b1b"}>{s.active ? "Hoạt động" : "Đóng"}</Chip></td>
                      <td style={{ padding: "10px 8px" }}>
                        <button onClick={() => { setEditShift(s); setShiftForm({ maCa: s.maCa, tenCa: s.tenCa, loaiCa: s.loaiCa, gioBatDau: s.gioBatDau, gioKetThuc: s.gioKetThuc, soGioChuan: s.soGioChuan, quaNgay: s.quaNgay }); setShowShiftForm(true); }} style={{ marginRight: 4, padding: "4px 8px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Sửa</button>
                        {s.active && <button onClick={() => handleCloseShift(s.id)} style={{ padding: "4px 8px", background: "#ef4444", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Đóng</button>}
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
              <button onClick={() => setShowAssignForm(true)} style={{ padding: "8px 16px", background: "#8b5cf6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Phân ca</button>
            </div>
            <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
              <div style={{ padding: "8px 16px", background: "#f8fafc", borderBottom: "1px solid #e2e8f0", fontSize: 13, color: "#64748b" }}>Lịch phân ca tháng {filterMonth} ({assignments.length} bản ghi)</div>
              {assignments.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Chưa có phân ca</div>}
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
              {timelogs.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Chưa có dữ liệu chấm công</div>}
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Mã NV</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Họ tên</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Ngày</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Giờ vào</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Giờ ra</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Nguồn</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Ngoại lệ</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Số giờ</th>
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
            <h3 style={{ margin: "0 0 16px" }}>{editShift ? "Sửa ca làm việc" : "Thêm ca làm việc"}</h3>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Mã ca *</label>
            <input style={inputStyle} value={shiftForm.maCa} onChange={e => setShiftForm(f => ({ ...f, maCa: e.target.value }))} placeholder="VD: HC1" />
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Tên ca *</label>
            <input style={inputStyle} value={shiftForm.tenCa} onChange={e => setShiftForm(f => ({ ...f, tenCa: e.target.value }))} placeholder="VD: Hành chính 1" />
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Loại ca</label>
            <select style={inputStyle} value={shiftForm.loaiCa} onChange={e => setShiftForm(f => ({ ...f, loaiCa: e.target.value }))}>
              <option value="HANH_CHINH">Hành chính</option><option value="CA_KIP">Ca kíp</option><option value="FLEXIBLE">Linh hoạt</option>
            </select>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Giờ bắt đầu</label><input style={inputStyle} type="time" value={shiftForm.gioBatDau} onChange={e => setShiftForm(f => ({ ...f, gioBatDau: e.target.value }))} /></div>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Giờ kết thúc</label><input style={inputStyle} type="time" value={shiftForm.gioKetThuc} onChange={e => setShiftForm(f => ({ ...f, gioKetThuc: e.target.value }))} /></div>
            </div>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Số giờ chuẩn</label>
            <input style={inputStyle} type="number" step="0.5" value={shiftForm.soGioChuan} onChange={e => setShiftForm(f => ({ ...f, soGioChuan: parseFloat(e.target.value) || 0 }))} />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowShiftForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huỷ</button>
              <button onClick={handleSaveShift} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Lưu</button>
            </div>
          </div>
        </div>
      )}

      {showAssignForm && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowAssignForm(false)}>
          <div style={{ ...cardStyle, width: 520 }} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Phân ca</h3>
            <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
              <button onClick={() => setAssignForm(f => ({ ...f, mode: "bulk" }))} style={{ padding: "6px 12px", background: assignForm.mode === "bulk" ? "#8b5cf6" : "#e2e8f0", color: assignForm.mode === "bulk" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 13 }}>Nhiều NV</button>
              <button onClick={() => setAssignForm(f => ({ ...f, mode: "single" }))} style={{ padding: "6px 12px", background: assignForm.mode === "single" ? "#8b5cf6" : "#e2e8f0", color: assignForm.mode === "single" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 13 }}>Một NV</button>
            </div>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Nhân viên</label>
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
                <option value="">Chọn nhân viên</option>
                {employees.map(e => <option key={e.nhanVienId} value={e.nhanVienId}>{e.maNv} — {e.hoTen}</option>)}
              </select>
            )}
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ca làm việc</label>
            <select style={inputStyle} value={assignForm.caId} onChange={e => setAssignForm(f => ({ ...f, caId: e.target.value }))}>
              <option value="">Chọn ca</option>
              {shifts.filter(s => s.active).map(s => <option key={s.id} value={s.id}>{s.maCa} — {s.tenCa}</option>)}
            </select>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>{assignForm.mode === "bulk" ? "Từ ngày" : "Ngày áp dụng"}</label><input style={inputStyle} type="date" value={assignForm.tuNgay} onChange={e => setAssignForm(f => ({ ...f, tuNgay: e.target.value }))} /></div>
              {assignForm.mode === "bulk" && <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Đến ngày</label><input style={inputStyle} type="date" value={assignForm.denNgay} onChange={e => setAssignForm(f => ({ ...f, denNgay: e.target.value }))} /></div>}
            </div>
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowAssignForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huỷ</button>
              <button onClick={handleAssign} style={{ padding: "8px 16px", background: "#8b5cf6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Phân ca</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
