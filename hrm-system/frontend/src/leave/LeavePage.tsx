// T10: Trang nghỉ phép & tăng ca
import { useState, useEffect } from "react";
import { PageHeader } from "../components/SharedComponents";
import { leaveRequestApi, otRequestApi, leaveBalanceApi } from "../api";
import { employeeApi } from "../api";

function Chip({ bg, color, children }: any) {
  return <span style={{ background: bg, color, padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{children}</span>;
}

function StatusChip({ status }: { status: string }) {
  const COLORS: Record<string, { bg: string; color: string }> = {
    CHO_DUYET: { bg: "#fef3c7", color: "#92400e" },
    DUYET_CAP_1: { bg: "#dbeafe", color: "#1e40af" },
    DA_DUYET: { bg: "#dcfce7", color: "#166534" },
    TU_CHOI: { bg: "#fee2e2", color: "#991b1b" },
    HUY: { bg: "#f3f4f6", color: "#6b7280" },
  };
  const c = COLORS[status] || { bg: "#f3f4f6", color: "#6b7280" };
  const L: Record<string, string> = { CHO_DUYET: "Chờ duyệt", DUYET_CAP_1: "Đã duyệt cấp 1", DA_DUYET: "Đã duyệt", TU_CHOI: "Từ chối", HUY: "Huỷ" };
  return <Chip bg={c.bg} color={c.color}>{L[status] || status}</Chip>;
}

const LOAI_PHEP_OPTIONS = [
  { value: "PHEP_NAM", label: "Phép năm" },
  { value: "PHEP_BU", label: "Phép bù" },
  { value: "OM", label: "Ốm" },
  { value: "THAI_SAN", label: "Thai sản" },
  { value: "KHONG_HUONG_LUONG", label: "Không hưởng lương" },
  { value: "CONG_TAC", label: "Công tác" },
  { value: "KHAC", label: "Khác" },
];

export default function LeavePage() {
  const [tab, setTab] = useState<"leave" | "ot" | "balance">("leave");
  const [leaveList, setLeaveList] = useState<any[]>([]);
  const [otList, setOtList] = useState<any[]>([]);
  const [balance, setBalance] = useState<any[]>([]);
  const [employees, setEmployees] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showLeaveForm, setShowLeaveForm] = useState(false);
  const [showOtForm, setShowOtForm] = useState(false);
  const [selectedNv, setSelectedNv] = useState("");
  const [leaveForm, setLeaveForm] = useState({ nhanVienId: "", loaiNghiPhep: "PHEP_NAM", tuNgay: "", denNgay: "", soNgayNghi: 1, lyDo: "" });
  const [otForm, setOtForm] = useState({ nhanVienId: "", ngayLamOt: "", gioBatDau: "17:00", gioKetThuc: "20:00", soGioOt: 3, heSoOt: "NGAY_THUONG_150", lamDem: false, lyDo: "" });
  const [msg, setMsg] = useState<{ type: string; text: string } | null>(null);

  useEffect(() => { loadEmployees(); }, []);
  useEffect(() => { if (tab === "leave") loadLeave(); else if (tab === "ot") loadOT(); else loadBalance(); }, [tab]);

  async function loadEmployees() {
    try { const r = await employeeApi.list("", undefined, undefined, 0, 100); setEmployees(r.content); } catch {}
  }

  async function loadLeave() {
    setLoading(true);
    try {
      const data = await leaveRequestApi.list();
      setLeaveList(Array.isArray(data) ? data : data.content || []);
    } finally { setLoading(false); }
  }

  async function loadOT() {
    setLoading(true);
    try {
      const data = await otRequestApi.list();
      setOtList(Array.isArray(data) ? data : data.content || []);
    } finally { setLoading(false); }
  }

  async function loadBalance() {
    setLoading(true);
    try {
      const allBalance: any[] = [];
      for (const emp of employees.slice(0, 20)) {
        try {
          const b = await leaveBalanceApi.get(emp.nhanVienId, new Date().getFullYear());
          if (b) allBalance.push({ ...b, nhanVienId: emp.nhanVienId });
        } catch {}
      }
      setBalance(allBalance);
    } finally { setLoading(false); }
  }

  function showMsg(type: string, text: string) {
    setMsg({ type, text }); setTimeout(() => setMsg(null), 3000);
  }

  async function handleCreateLeave() {
    try {
      await leaveRequestApi.create(leaveForm);
      setShowLeaveForm(false);
      loadLeave();
      showMsg("ok", "Tạo đơn nghỉ phép thành công");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleCreateOT() {
    try {
      await otRequestApi.create(otForm);
      setShowOtForm(false);
      loadOT();
      showMsg("ok", "Tạo đơn OT thành công");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleApprove(id: string, approve: boolean) {
    try {
      await leaveRequestApi.approveCap1(id, approve, "");
      loadLeave();
      showMsg("ok", approve ? "Duyệt thành công" : "Từ chối thành công");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleApproveCap2(id: string, approve: boolean) {
    try {
      await leaveRequestApi.approveCap2(id, approve, "");
      loadLeave();
      showMsg("ok", approve ? "Duyệt cấp 2 thành công" : "Từ chối");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleApproveOTCap1(id: string, approve: boolean) {
    try {
      await otRequestApi.approveCap1(id, approve, "");
      loadOT();
      showMsg("ok", approve ? "Duyệt OT cấp 1" : "Từ chối OT");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleApproveOTCap2(id: string, approve: boolean) {
    try {
      await otRequestApi.approveCap2(id, approve, "");
      loadOT();
      showMsg("ok", approve ? "Duyệt OT cấp 2" : "Từ chối OT");
    } catch (e: any) { showMsg("err", e.message); }
  }

  const cardStyle: any = { background: "#fff", borderRadius: 12, padding: 24, width: 480 };
  const inputStyle: any = { width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14, boxSizing: "border-box", marginBottom: 12 };

  return (
    <div>
      <PageHeader title="Nghỉ phép & Tăng ca" subtitle="T10 — Đơn nghỉ phép, đăng ký OT, số phép năm" />
      <div style={{ padding: "16px 24px" }}>
        {msg && <div style={{ padding: "8px 16px", background: msg.type === "ok" ? "#dcfce7" : "#fee2e2", color: msg.type === "ok" ? "#166534" : "#991b1b", borderRadius: 6, marginBottom: 12 }}>{msg.text}</div>}

        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          {[
            { key: "leave", label: "Đơn nghỉ phép", color: "#3b82f6" },
            { key: "ot", label: "Đăng ký OT", color: "#f59e0b" },
            { key: "balance", label: "Số phép năm", color: "#10b981" },
          ].map(t => (
            <button key={t.key} onClick={() => setTab(t.key as any)} style={{ padding: "8px 16px", background: tab === t.key ? t.color : "#e2e8f0", color: tab === t.key ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>
              {t.label}
            </button>
          ))}
          <div style={{ flex: 1 }} />
          {tab === "leave" && <button onClick={() => setShowLeaveForm(true)} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Tạo đơn</button>}
          {tab === "ot" && <button onClick={() => setShowOtForm(true)} style={{ padding: "8px 16px", background: "#f59e0b", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Đăng ký OT</button>}
        </div>

        {tab === "leave" && (
          <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
            {loading && <div style={{ padding: 24, color: "#64748b" }}>Đang tải...</div>}
            {!loading && leaveList.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Chưa có đơn nghỉ phép</div>}
            {!loading && leaveList.length > 0 && (
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Mã NV</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Họ tên</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Loại</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Từ ngày</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Đến ngày</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Số ngày</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Trạng thái</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Hành động</th>
                </tr></thead>
                <tbody>
                  {leaveList.map((l, i) => (
                    <tr key={l.id} style={{ borderTop: "1px solid #f1f5f9" }}>
                      <td style={{ padding: "10px 16px", fontFamily: "monospace", fontWeight: 700, fontSize: 13 }}>{l.maNv}</td>
                      <td style={{ padding: "10px 8px", fontSize: 13 }}>{l.hoTen}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>{LOAI_PHEP_OPTIONS.find(o => o.value === l.loaiNghiPhep)?.label || l.loaiNghiPhep}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{l.tuNgay}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{l.denNgay}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>{l.soNgayNghi}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}><StatusChip status={l.trangThai} /></td>
                      <td style={{ padding: "10px 8px" }}>
                        {l.trangThai === "CHO_DUYET" && (
                          <>
                            <button onClick={() => handleApprove(l.id, true)} style={{ marginRight: 4, padding: "3px 8px", background: "#dcfce7", color: "#166534", border: "1px solid #86efac", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Duyệt</button>
                            <button onClick={() => handleApprove(l.id, false)} style={{ padding: "3px 8px", background: "#fee2e2", color: "#991b1b", border: "1px solid #fca5a5", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Từ chối</button>
                          </>
                        )}
                        {l.trangThai === "DUYET_CAP_1" && (
                          <>
                            <button onClick={() => handleApproveCap2(l.id, true)} style={{ marginRight: 4, padding: "3px 8px", background: "#dcfce7", color: "#166534", border: "1px solid #86efac", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Duyệt cấp 2</button>
                            <button onClick={() => handleApproveCap2(l.id, false)} style={{ padding: "3px 8px", background: "#fee2e2", color: "#991b1b", border: "1px solid #fca5a5", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Từ chối</button>
                          </>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {tab === "ot" && (
          <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
            {loading && <div style={{ padding: 24, color: "#64748b" }}>Đang tải...</div>}
            {!loading && otList.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Chưa có đơn OT</div>}
            {!loading && otList.length > 0 && (
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Mã NV</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Họ tên</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Ngày</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Giờ</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Số giờ</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Hệ số</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Trạng thái</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Hành động</th>
                </tr></thead>
                <tbody>
                  {otList.map((o, i) => (
                    <tr key={o.id} style={{ borderTop: "1px solid #f1f5f9" }}>
                      <td style={{ padding: "10px 16px", fontFamily: "monospace", fontWeight: 700, fontSize: 13 }}>{o.maNv}</td>
                      <td style={{ padding: "10px 8px", fontSize: 13 }}>{o.hoTen}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{o.ngayLamOt}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13, fontFamily: "monospace" }}>{o.gioBatDau}–{o.gioKetThuc}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13, fontWeight: 600 }}>{o.soGioOt}h</td>
                      <td style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>
                        <Chip bg="#fef3c7" color="#92400e">{o.heSoOt === "NGAY_THUONG_150" ? "150%" : o.heSoOt === "NGAY_NGHI_TUAN_200" ? "200%" : "300%"}</Chip>
                      </td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}><StatusChip status={o.trangThai} /></td>
                      <td style={{ padding: "10px 8px" }}>
                        {o.trangThai === "CHO_DUYET" && (
                          <>
                            <button onClick={() => handleApproveOTCap1(o.id, true)} style={{ marginRight: 4, padding: "3px 8px", background: "#dcfce7", color: "#166534", border: "1px solid #86efac", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Duyệt</button>
                            <button onClick={() => handleApproveOTCap1(o.id, false)} style={{ padding: "3px 8px", background: "#fee2e2", color: "#991b1b", border: "1px solid #fca5a5", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Từ chối</button>
                          </>
                        )}
                        {o.trangThai === "DUYET_CAP_1" && (
                          <>
                            <button onClick={() => handleApproveOTCap2(o.id, true)} style={{ marginRight: 4, padding: "3px 8px", background: "#dcfce7", color: "#166534", border: "1px solid #86efac", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Duyệt cấp 2</button>
                            <button onClick={() => handleApproveOTCap2(o.id, false)} style={{ padding: "3px 8px", background: "#fee2e2", color: "#991b1b", border: "1px solid #fca5a5", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Từ chối</button>
                          </>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {tab === "balance" && (
          <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
            {loading && <div style={{ padding: 24, color: "#64748b" }}>Đang tải...</div>}
            {!loading && balance.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Chưa có số phép năm</div>}
            {!loading && balance.length > 0 && (
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Nhân viên</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Được hưởng</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Đã dùng</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Còn lại</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Tỷ lệ</th>
                </tr></thead>
                <tbody>
                  {balance.map((b, i) => {
                    const emp = employees.find(e => e.nhanVienId === b.nhanVienId);
                    const pct = b.soNgayDuocHuong > 0 ? Math.round((b.soNgayConLai / b.soNgayDuocHuong) * 100) : 0;
                    return (
                      <tr key={b.nhanVienId} style={{ borderTop: "1px solid #f1f5f9" }}>
                        <td style={{ padding: "10px 16px" }}>
                          <div style={{ fontWeight: 600 }}>{emp?.hoTen || b.nhanVienId}</div>
                          <div style={{ fontSize: 12, color: "#64748b", fontFamily: "monospace" }}>{emp?.maNv || "-"}</div>
                        </td>
                        <td style={{ padding: "10px 8px", textAlign: "center", fontWeight: 600 }}>{b.soNgayDuocHuong}</td>
                        <td style={{ padding: "10px 8px", textAlign: "center", color: "#dc2626" }}>{b.soNgayDaDung}</td>
                        <td style={{ padding: "10px 8px", textAlign: "center", fontWeight: 700, color: "#166534", fontSize: 16 }}>{b.soNgayConLai}</td>
                        <td style={{ padding: "10px 8px", textAlign: "center" }}>
                          <div style={{ width: 80, height: 8, background: "#e2e8f0", borderRadius: 4, overflow: "hidden", margin: "0 auto" }}>
                            <div style={{ width: `${pct}%`, height: "100%", background: pct > 50 ? "#10b981" : pct > 20 ? "#f59e0b" : "#ef4444", borderRadius: 4 }} />
                          </div>
                          <div style={{ fontSize: 11, color: "#64748b", marginTop: 2 }}>{pct}%</div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>

      {showLeaveForm && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowLeaveForm(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Tạo đơn nghỉ phép</h3>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Nhân viên</label>
            <select style={inputStyle} value={leaveForm.nhanVienId} onChange={e => setLeaveForm(f => ({ ...f, nhanVienId: e.target.value }))}>
              <option value="">Chọn nhân viên</option>
              {employees.map(emp => <option key={emp.nhanVienId} value={emp.nhanVienId}>{emp.maNv} — {emp.hoTen}</option>)}
            </select>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Loại nghỉ phép</label>
            <select style={inputStyle} value={leaveForm.loaiNghiPhep} onChange={e => setLeaveForm(f => ({ ...f, loaiNghiPhep: e.target.value }))}>
              {LOAI_PHEP_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
            </select>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Từ ngày</label><input style={inputStyle} type="date" value={leaveForm.tuNgay} onChange={e => setLeaveForm(f => ({ ...f, tuNgay: e.target.value }))} /></div>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Đến ngày</label><input style={inputStyle} type="date" value={leaveForm.denNgay} onChange={e => setLeaveForm(f => ({ ...f, denNgay: e.target.value }))} /></div>
            </div>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Số ngày nghỉ</label>
            <input style={inputStyle} type="number" step="0.5" value={leaveForm.soNgayNghi} onChange={e => setLeaveForm(f => ({ ...f, soNgayNghi: parseFloat(e.target.value) || 1 }))} />
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Lý do</label>
            <input style={inputStyle} value={leaveForm.lyDo} onChange={e => setLeaveForm(f => ({ ...f, lyDo: e.target.value }))} placeholder="Lý do nghỉ phép..." />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowLeaveForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huỷ</button>
              <button onClick={handleCreateLeave} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Tạo</button>
            </div>
          </div>
        </div>
      )}

      {showOtForm && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowOtForm(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Đăng ký tăng ca</h3>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Nhân viên</label>
            <select style={inputStyle} value={otForm.nhanVienId} onChange={e => setOtForm(f => ({ ...f, nhanVienId: e.target.value }))}>
              <option value="">Chọn nhân viên</option>
              {employees.map(emp => <option key={emp.nhanVienId} value={emp.nhanVienId}>{emp.maNv} — {emp.hoTen}</option>)}
            </select>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ngày làm OT</label>
            <input style={inputStyle} type="date" value={otForm.ngayLamOt} onChange={e => setOtForm(f => ({ ...f, ngayLamOt: e.target.value }))} />
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Giờ bắt đầu</label><input style={inputStyle} type="time" value={otForm.gioBatDau} onChange={e => setOtForm(f => ({ ...f, gioBatDau: e.target.value }))} /></div>
              <div><label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Giờ kết thúc</label><input style={inputStyle} type="time" value={otForm.gioKetThuc} onChange={e => setOtForm(f => ({ ...f, gioKetThuc: e.target.value }))} /></div>
            </div>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Hệ số</label>
            <select style={inputStyle} value={otForm.heSoOt} onChange={e => setOtForm(f => ({ ...f, heSoOt: e.target.value }))}>
              <option value="NGAY_THUONG_150">Ngày thường 150%</option>
              <option value="NGAY_NGHI_TUAN_200">Ngày nghỉ tuần 200%</option>
              <option value="NGAY_LE_300">Ngày lễ 300%</option>
            </select>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Lý do</label>
            <input style={inputStyle} value={otForm.lyDo} onChange={e => setOtForm(f => ({ ...f, lyDo: e.target.value }))} placeholder="Lý do OT..." />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowOtForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huỷ</button>
              <button onClick={handleCreateOT} style={{ padding: "8px 16px", background: "#f59e0b", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Đăng ký</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
