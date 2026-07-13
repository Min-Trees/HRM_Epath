// T05: Employee management page
import { useState, useEffect } from "react";
import { PageHeader } from "../components/SharedComponents";
import { employeeMock } from "../mock/employee.mock";
import { deptMock } from "../mock/department.mock";

const TRANG_THAI_COLORS: Record<string, { bg: string; color: string }> = {
  UNG_VIEN: { bg: "#dbeafe", color: "#1e40af" },
  THU_VIEC: { bg: "#fef3c7", color: "#92400e" },
  CHINH_THUC: { bg: "#dcfce7", color: "#166534" },
  TAM_HOAN_HDLD: { bg: "#fee2e2", color: "#991b1b" },
  DA_NGHI_VIEC: { bg: "#f3f4f6", color: "#6b7280" },
  DA_NGHI_HUU: { bg: "#f3f4f6", color: "#6b7280" },
  LUU_TRU: { bg: "#f3f4f6", color: "#6b7280" },
};

const TRANG_THAI_LABEL: Record<string, string> = {
  UNG_VIEN: "Ung vien",
  THU_VIEC: "Thu viec",
  CHINH_THUC: "Chinh thuc",
  TAM_HOAN_HDLD: "Tam hoan HDLD",
  DA_NGHI_VIEC: "Da nghi viec",
  DA_NGHI_HUU: "Da nghi huu",
  LUU_TRU: "Luu tru",
};

function StatusChip({ status }: { status: string }) {
  const s = TRANG_THAI_COLORS[status] || { bg: "#f3f4f6", color: "#6b7280" };
  return <span style={{ background: s.bg, color: s.color, padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{TRANG_THAI_LABEL[status] || status}</span>;
}

export default function EmployeePage() {
  const [list, setList] = useState<any[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState("");
  const [phongBanFilter, setPhongBanFilter] = useState("");
  const [trangThaiFilter, setTrangThaiFilter] = useState("");
  const [depts, setDepts] = useState<any[]>([]);
  const [selected, setSelected] = useState<any | null>(null);
  const [empTab, setEmpTab] = useState<"info" | "dep" | "wh">("info");
  const [showCreate, setShowCreate] = useState(false);
  const [form, setForm] = useState({
    hoTen: "", soCccd: "", ngayCapCccd: "", noiCapCccd: "", ngaySinh: "", gioiTinh: "NAM",
    diaChiThuongTru: "", diaChiLienLac: "", soDienThoai: "", email: "",
    phongBanId: "", phongBan: "", ngachBacId: "", ngachBac: "",
    ngayVaoLam: "", maSoThue: "", quanLyTrucTiepId: "",
  });
  const [depForm, setDepForm] = useState({ hoTen: "", quanHe: "", ngaySinh: "", soCccd: "", ngheNghiep: "", tuNgay: "" });
  const [whForm, setWhForm] = useState({ tuNgay: "", denNgay: "", phongBan: "", chucDanh: "", ghiChu: "" });
  const [msg, setMsg] = useState<{ type: string; text: string } | null>(null);

  useEffect(() => { loadList(); loadDepts(); }, []);

  async function loadDepts() {
    try {
      const data = await deptMock.listDept("flat");
      setDepts(data);
    } catch {}
  }

  async function loadList() {
    setLoading(true);
    try {
      const params: any = { page: 0, size: 50 };
      if (q) params.q = q;
      if (phongBanFilter) params.phongBanId = phongBanFilter;
      if (trangThaiFilter) params.trangThai = trangThaiFilter;
      const r = await employeeMock.list(params);
      setList(r.content); setTotal(r.totalElements);
    } finally { setLoading(false); }
  }

  async function selectEmp(id: string) {
    try {
      const emp = await employeeMock.get(id);
      setSelected(emp); setEmpTab("info");
    } catch {}
  }

  function showMsg(type: string, text: string) {
    setMsg({ type, text }); setTimeout(() => setMsg(null), 3000);
  }

  async function handleCreate() {
    const dept = depts.find(d => d.phongBanId === form.phongBanId);
    try {
      const created = await employeeMock.create({ ...form, phongBan: dept?.tenPhongBan || "" });
      showMsg("ok", "Tao nhan vien thanh cong");
      setShowCreate(false);
      setForm({ hoTen: "", soCccd: "", ngayCapCccd: "", noiCapCccd: "", ngaySinh: "", gioiTinh: "NAM", diaChiThuongTru: "", diaChiLienLac: "", soDienThoai: "", email: "", phongBanId: "", phongBan: "", ngachBacId: "", ngachBac: "", ngayVaoLam: "", maSoThue: "", quanLyTrucTiepId: "" });
      loadList();
      selectEmp(created.nhanVienId);
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleAddDep() {
    if (!selected) return;
    try {
      await employeeMock.createDependent(selected.nhanVienId, depForm);
      setDepForm({ hoTen: "", quanHe: "", ngaySinh: "", soCccd: "", ngheNghiep: "", tuNgay: "" });
      selectEmp(selected.nhanVienId);
      showMsg("ok", "Them nguoi phu thuoc thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleDelDep(depId: string) {
    if (!selected) return;
    try {
      await employeeMock.deleteDependent(selected.nhanVienId, depId);
      selectEmp(selected.nhanVienId);
      showMsg("ok", "Xoa thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleAddWH() {
    if (!selected) return;
    try {
      await employeeMock.createWorkHistory(selected.nhanVienId, whForm);
      setWhForm({ tuNgay: "", denNgay: "", phongBan: "", chucDanh: "", ghiChu: "" });
      selectEmp(selected.nhanVienId);
      showMsg("ok", "Them qua trinh cong tac thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleDelWH(whId: string) {
    if (!selected) return;
    try {
      await employeeMock.deleteWorkHistory(selected.nhanVienId, whId);
      selectEmp(selected.nhanVienId);
    } catch {}
  }

  const formStyle: any = { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 };
  const cardStyle: any = { background: "#fff", borderRadius: 12, padding: 24, width: 640, maxHeight: "85vh", overflowY: "auto" };
  const inputStyle: any = { width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14, boxSizing: "border-box", marginBottom: 10 };
  const labelStyle: any = { fontSize: 13, color: "#64748b", marginBottom: 2, display: "block" };
  const grid2: any = { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" };

  return (
    <div>
      <PageHeader title="Ho so nhan vien" subtitle="T05 — Nhan su, nguoi phu thuoc, qua trinh cong tac" />
      <div style={{ padding: "16px 24px" }}>
        {msg && <div style={{ padding: "8px 16px", background: msg.type === "ok" ? "#dcfce7" : "#fee2e2", color: msg.type === "ok" ? "#166534" : "#991b1b", borderRadius: 6, marginBottom: 12 }}>{msg.text}</div>}

        {/* Filter bar */}
        <div style={{ display: "flex", gap: 8, marginBottom: 16, flexWrap: "wrap" }}>
          <input style={{ ...inputStyle, width: 240, marginBottom: 0 }} value={q} onChange={e => setQ(e.target.value)} onKeyDown={e => e.key === "Enter" && loadList()} placeholder="Tim ho ten, ma NV..." />
          <select style={{ ...inputStyle, width: 180, marginBottom: 0 }} value={phongBanFilter} onChange={e => { setPhongBanFilter(e.target.value); loadList(); }}>
            <option value="">Tat ca phong ban</option>
            {depts.map(d => <option key={d.phongBanId} value={d.phongBanId}>{d.tenPhongBan}</option>)}
          </select>
          <select style={{ ...inputStyle, width: 160, marginBottom: 0 }} value={trangThaiFilter} onChange={e => { setTrangThaiFilter(e.target.value); loadList(); }}>
            <option value="">Tat ca trang thai</option>
            {Object.entries(TRANG_THAI_LABEL).map(([v, l]) => <option key={v} value={v}>{l}</option>)}
          </select>
          <button onClick={loadList} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 14 }}>Tim kiem</button>
          <div style={{ flex: 1 }} />
          <button onClick={() => setShowCreate(true)} style={{ padding: "8px 16px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 14 }}>+ Them nhan vien</button>
        </div>

        <div style={{ display: "flex", gap: 16, alignItems: "flex-start" }}>
          {/* Left: employee list */}
          <div style={{ flex: "0 0 400px" }}>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Dang tai...</div> : (
              <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                <div style={{ padding: "8px 12px", background: "#f8fafc", borderBottom: "1px solid #e2e8f0", fontSize: 13, color: "#64748b" }}>Tong: {total} nhan vien</div>
                <div style={{ maxHeight: "calc(100vh - 280px)", overflowY: "auto" }}>
                  {list.map(emp => (
                    <div key={emp.nhanVienId} onClick={() => selectEmp(emp.nhanVienId)} style={{
                      padding: "10px 12px", cursor: "pointer", borderBottom: "1px solid #f1f5f9",
                      background: selected?.nhanVienId === emp.nhanVienId ? "#eff6ff" : "transparent",
                    }}>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <span style={{ fontFamily: "monospace", fontWeight: 700, fontSize: 13 }}>{emp.maNv}</span>
                        <StatusChip status={emp.trangThai} />
                      </div>
                      <div style={{ fontSize: 14, fontWeight: 500, marginTop: 2 }}>{emp.hoTen}</div>
                      <div style={{ fontSize: 12, color: "#64748b", marginTop: 2 }}>{emp.phongBan || "-"} — {emp.email}</div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Right: detail panel */}
          <div style={{ flex: 1 }}>
            {selected ? (
              <div style={{ background: "#fff", borderRadius: 8, boxShadow: "0 1px 3px rgba(0,0,0,0.08)", overflow: "hidden" }}>
                <div style={{ padding: "16px 20px", borderBottom: "1px solid #e2e8f0", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: 18 }}>{selected.hoTen}</div>
                    <div style={{ color: "#64748b", fontSize: 13, marginTop: 2 }}>{selected.maNv} · {selected.phongBan}</div>
                  </div>
                  <StatusChip status={selected.trangThai} />
                </div>
                <div style={{ display: "flex", borderBottom: "1px solid #e2e8f0" }}>
                  {(["info", "dep", "wh"] as const).map(t => (
                    <button key={t} onClick={() => setEmpTab(t)} style={{ padding: "10px 16px", background: "none", border: "none", borderBottom: empTab === t ? "2px solid #3b82f6" : "2px solid transparent", color: empTab === t ? "#3b82f6" : "#64748b", cursor: "pointer", fontSize: 14, fontWeight: empTab === t ? 600 : 400 }}>
                      {t === "info" ? "Thong tin" : t === "dep" ? "Nguoi phu thuoc" : "Qua trinh"}
                    </button>
                  ))}
                </div>
                <div style={{ padding: 20 }}>
                  {empTab === "info" && (
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "12px 24px" }}>
                      {[
                        ["Ho va ten", selected.hoTen],
                        ["Ma NV", selected.maNv],
                        ["So CCCD", selected.soCccd],
                        ["Ngay cap CCCD", selected.ngayCapCccd],
                        ["Noi cap CCCD", selected.noiCapCccd],
                        ["Ngay sinh", selected.ngaySinh],
                        ["Gioi tinh", selected.gioiTinh === "NAM" ? "Nam" : selected.gioiTinh === "NU" ? "Nu" : "Khac"],
                        ["So DT", selected.soDienThoai],
                        ["Email", selected.email],
                        ["Phong ban", selected.phongBan],
                        ["Ngach bac", selected.ngachBac],
                        ["Ngay vao lam", selected.ngayVaoLam],
                        ["Ma so thue", selected.maSoThue || "-"],
                      ].map(([label, value]) => (
                        <div key={String(label)}>
                          <div style={{ fontSize: 12, color: "#94a3b8", marginBottom: 2 }}>{String(label)}</div>
                          <div style={{ fontSize: 14, fontWeight: 500 }}>{String(value || "-")}</div>
                        </div>
                      ))}
                    </div>
                  )}
                  {empTab === "dep" && (
                    <div>
                      <div style={{ marginBottom: 12, display: "flex", gap: 8, flexWrap: "wrap" }}>
                        <input style={{ ...inputStyle, width: 160, marginBottom: 0 }} value={depForm.hoTen} onChange={e => setDepForm(f => ({ ...f, hoTen: e.target.value }))} placeholder="Ho ten" />
                        <input style={{ ...inputStyle, width: 120, marginBottom: 0 }} value={depForm.quanHe} onChange={e => setDepForm(f => ({ ...f, quanHe: e.target.value }))} placeholder="Quan he" />
                        <input style={{ ...inputStyle, width: 120, marginBottom: 0 }} type="date" value={depForm.ngaySinh} onChange={e => setDepForm(f => ({ ...f, ngaySinh: e.target.value }))} />
                        <button onClick={handleAddDep} style={{ padding: "8px 12px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 13 }}>Them</button>
                      </div>
                      {selected.dependents?.length === 0 && <div style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co nguoi phu thuoc</div>}
                      {selected.dependents?.map((d: any) => (
                        <div key={d.id} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "8px 0", borderBottom: "1px solid #f1f5f9" }}>
                          <div>
                            <div style={{ fontWeight: 500 }}>{d.hoTen}</div>
                            <div style={{ fontSize: 12, color: "#64748b" }}>{d.quanHe} · Sinh: {d.ngaySinh}</div>
                          </div>
                          <button onClick={() => handleDelDep(d.id)} style={{ padding: "4px 8px", background: "#fee2e2", color: "#991b1b", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Xoa</button>
                        </div>
                      ))}
                    </div>
                  )}
                  {empTab === "wh" && (
                    <div>
                      <div style={{ marginBottom: 12, display: "flex", gap: 8, flexWrap: "wrap", alignItems: "center" }}>
                        <input style={{ ...inputStyle, width: 100, marginBottom: 0 }} type="date" value={whForm.tuNgay} onChange={e => setWhForm(f => ({ ...f, tuNgay: e.target.value }))} placeholder="Tu ngay" />
                        <input style={{ ...inputStyle, width: 100, marginBottom: 0 }} type="date" value={whForm.denNgay} onChange={e => setWhForm(f => ({ ...f, denNgay: e.target.value }))} placeholder="Den ngay" />
                        <input style={{ ...inputStyle, width: 150, marginBottom: 0 }} value={whForm.phongBan} onChange={e => setWhForm(f => ({ ...f, phongBan: e.target.value }))} placeholder="Phong ban" />
                        <input style={{ ...inputStyle, width: 130, marginBottom: 0 }} value={whForm.chucDanh} onChange={e => setWhForm(f => ({ ...f, chucDanh: e.target.value }))} placeholder="Chuc danh" />
                        <button onClick={handleAddWH} style={{ padding: "8px 12px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 13 }}>Them</button>
                      </div>
                      {selected.workHistory?.length === 0 && <div style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co qua trinh cong tac</div>}
                      {selected.workHistory?.map((w: any) => (
                        <div key={w.id} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "8px 0", borderBottom: "1px solid #f1f5f9" }}>
                          <div>
                            <div style={{ fontWeight: 500 }}>{w.phongBan} — {w.chucDanh}</div>
                            <div style={{ fontSize: 12, color: "#64748b" }}>{w.tuNgay} {w.denNgay ? `→ ${w.denNgay}` : "→ hien tai"} {w.ghiChu ? `· ${w.ghiChu}` : ""}</div>
                          </div>
                          <button onClick={() => handleDelWH(w.id)} style={{ padding: "4px 8px", background: "#fee2e2", color: "#991b1b", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Xoa</button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ) : (
              <div style={{ padding: 48, textAlign: "center", background: "#fff", borderRadius: 8, color: "#94a3b8", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                Chon nhan vien de xem chi tiet
              </div>
            )}
          </div>
        </div>
      </div>

      {showCreate && (
        <div style={formStyle} onClick={() => setShowCreate(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px", fontSize: 16 }}>Them nhan vien</h3>
            <div style={grid2}>
              {[
                ["hoTen", "Ho va ten *"], ["soCccd", "So CCCD *"], ["ngayCapCccd", "Ngay cap CCCD", "date"],
                ["noiCapCccd", "Noi cap CCCD"], ["ngaySinh", "Ngay sinh", "date"], ["soDienThoai", "So dien thoai"],
                ["email", "Email"], ["diaChiThuongTru", "Dia chi thuong tru"], ["maSoThue", "Ma so thue"],
              ].map(([field, label, type]) => (
                <div key={String(field)}>
                  <label style={labelStyle}>{String(label)}</label>
                  <input style={inputStyle} type={type === "date" ? "date" : "text"} value={(form as any)[String(field)]} onChange={e => setForm(f => ({ ...f, [String(field)]: e.target.value }))} />
                </div>
              ))}
              <div>
                <label style={labelStyle}>Phong ban *</label>
                <select style={inputStyle} value={form.phongBanId} onChange={e => { const d = depts.find(x => x.phongBanId === e.target.value); setForm(f => ({ ...f, phongBanId: e.target.value, phongBan: d?.tenPhongBan || "" })); }}>
                  <option value="">Chon phong ban</option>
                  {depts.map(d => <option key={d.phongBanId} value={d.phongBanId}>{d.tenPhongBan}</option>)}
                </select>
              </div>
              <div>
                <label style={labelStyle}>Ngay vao lam *</label>
                <input style={inputStyle} type="date" value={form.ngayVaoLam} onChange={e => setForm(f => ({ ...f, ngayVaoLam: e.target.value }))} />
              </div>
            </div>
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 16 }}>
              <button onClick={() => setShowCreate(false)} style={{ padding: "8px 16px", background: "#e2e8f0", color: "#334", border: "none", borderRadius: 6, cursor: "pointer" }}>Huy</button>
              <button onClick={handleCreate} style={{ padding: "8px 16px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Tao nhan vien</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
