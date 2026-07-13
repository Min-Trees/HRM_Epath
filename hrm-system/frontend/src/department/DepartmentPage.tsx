// T04: Department (PhongBan) + Salary Grade (NgachBacLuong) Page
import { useState, useEffect } from "react";
import { PageHeader } from "../components/SharedComponents";
import { deptMock, salaryGradeMock } from "../mock/department.mock";

interface Dept {
  phongBanId: string;
  maPhongBan: string;
  tenPhongBan: string;
  phongBanChaId: string | null;
  capDo: number;
  dinhBien: number;
  active: boolean;
  truongBoPhanId?: string;
  children?: Dept[];
}

interface Grade {
  id: string;
  maNgach: string;
  tenNgach: string;
  soBac: number;
  luongCoSo: number;
  heSo: number;
  active: boolean;
}

function fmtVND(v: number) {
  if (!v) return "-";
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(v);
}

const statusBadge = (active: boolean) => ({
  background: active ? "#dcfce7" : "#fee2e2",
  color: active ? "#166534" : "#991b1b",
  padding: "2px 8px", borderRadius: 12, fontSize: 12,
});

function DeptRow({ dept, onEdit, onClose }: { dept: Dept; onEdit: (d: Dept) => void; onClose: (id: string) => void }) {
  const [open, setOpen] = useState(false);
  const indent = (dept.capDo - 1) * 24;
  return (
    <>
      <tr style={{ borderTop: "1px solid #e2e8f0" }}>
        <td style={{ paddingLeft: 16 + indent, padding: "10px 8px" }}>
          <button onClick={() => setOpen(o => !o)} style={{ background: "none", border: "none", cursor: "pointer", padding: "0 4px", fontSize: 12 }}>
            {open ? "▼" : "▶"}
          </button>
          <span style={{ fontFamily: "monospace", fontWeight: 600 }}>{dept.maPhongBan}</span>
        </td>
        <td style={{ padding: "10px 8px" }}>{dept.tenPhongBan}</td>
        <td style={{ padding: "10px 8px", textAlign: "center" }}>{dept.capDo}</td>
        <td style={{ padding: "10px 8px", textAlign: "center" }}>{dept.dinhBien}</td>
        <td style={{ padding: "10px 8px", textAlign: "center" }}>
          <span style={statusBadge(dept.active)}>{dept.active ? "Hoat dong" : "Dong"}</span>
        </td>
        <td style={{ padding: "10px 8px" }}>
          <button onClick={() => onEdit(dept)} style={{ marginRight: 4, padding: "4px 8px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Sua</button>
          {dept.active && (
            <button onClick={() => onClose(dept.phongBanId)} style={{ padding: "4px 8px", background: "#ef4444", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Dong</button>
          )}
        </td>
      </tr>
      {open && dept.children?.map(child => (
        <DeptRow key={child.phongBanId} dept={child} onEdit={onEdit} onClose={onClose} />
      ))}
    </>
  );
}

export default function DepartmentPage() {
  const [tab, setTab] = useState<"dept" | "grade">("dept");
  const [depts, setDepts] = useState<any[]>([]);
  const [grades, setGrades] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showDeptForm, setShowDeptForm] = useState(false);
  const [showGradeForm, setShowGradeForm] = useState(false);
  const [editDept, setEditDept] = useState<Dept | null>(null);
  const [editGrade, setEditGrade] = useState<Grade | null>(null);
  const [deptForm, setDeptForm] = useState({ maPhongBan: "", tenPhongBan: "", phongBanChaId: "", dinhBien: 0 });
  const [gradeForm, setGradeForm] = useState({ maNgach: "", tenNgach: "", soBac: 5, luongCoSo: 0, heSo: 1.0 });
  const [msg, setMsg] = useState<{ type: "ok" | "err"; text: string } | null>(null);

  useEffect(() => { loadData(); }, [tab]);

  async function loadData() {
    setLoading(true);
    try {
      if (tab === "dept") {
        const data = await deptMock.listDept("flat");
        setDepts(data);
      } else {
        const data = await salaryGradeMock.listGrades();
        setGrades(Array.isArray(data) ? data : data.content || []);
      }
    } finally { setLoading(false); }
  }

  function showMsg(type: "ok" | "err", text: string) {
    setMsg({ type, text });
    setTimeout(() => setMsg(null), 3000);
  }

  async function handleSaveDept() {
    try {
      if (editDept) {
        await deptMock.updateDept(editDept.phongBanId, deptForm);
      } else {
        await deptMock.createDept(deptForm);
      }
      setShowDeptForm(false); setEditDept(null); setDeptForm({ maPhongBan: "", tenPhongBan: "", phongBanChaId: "", dinhBien: 0 });
      loadData();
      showMsg("ok", "Luu thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleCloseDept(id: string) {
    try {
      await deptMock.closeDept(id);
      loadData();
      showMsg("ok", "Dong phong ban thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleSaveGrade() {
    try {
      if (editGrade) {
        await salaryGradeMock.updateGrade(editGrade.id, gradeForm);
      } else {
        await salaryGradeMock.createGrade(gradeForm);
      }
      setShowGradeForm(false); setEditGrade(null); setGradeForm({ maNgach: "", tenNgach: "", soBac: 5, luongCoSo: 0, heSo: 1.0 });
      loadData();
      showMsg("ok", "Luu thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleCloseGrade(id: string) {
    try {
      await salaryGradeMock.closeGrade(id);
      loadData();
      showMsg("ok", "Dong ngach thanh cong");
    } catch (e: any) { showMsg("err", e.message); }
  }

  function openEditDept(d: Dept) {
    setEditDept(d); setDeptForm({ maPhongBan: d.maPhongBan, tenPhongBan: d.tenPhongBan, phongBanChaId: d.phongBanChaId || "", dinhBien: d.dinhBien }); setShowDeptForm(true);
  }

  function openEditGrade(g: Grade) {
    setEditGrade(g); setGradeForm({ maNgach: g.maNgach, tenNgach: g.tenNgach, soBac: g.soBac, luongCoSo: g.luongCoSo, heSo: g.heSo }); setShowGradeForm(true);
  }

  const formStyle: any = { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 };
  const cardStyle: any = { background: "#fff", borderRadius: 12, padding: 24, width: 480, maxHeight: "80vh", overflowY: "auto" };
  const inputStyle: any = { width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14, boxSizing: "border-box", marginBottom: 12 };

  return (
    <div>
      <PageHeader title="Co cau to chuc" subtitle="T04 — Phong ban & Ngach bac luong" />
      <div style={{ padding: "16px 24px" }}>
        {msg && <div style={{ padding: "8px 16px", background: msg.type === "ok" ? "#dcfce7" : "#fee2e2", color: msg.type === "ok" ? "#166534" : "#991b1b", borderRadius: 6, marginBottom: 12 }}>{msg.text}</div>}

        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          <button onClick={() => setTab("dept")} style={{ padding: "8px 16px", background: tab === "dept" ? "#3b82f6" : "#e2e8f0", color: tab === "dept" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>Phong ban</button>
          <button onClick={() => setTab("grade")} style={{ padding: "8px 16px", background: tab === "grade" ? "#10b981" : "#e2e8f0", color: tab === "grade" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>Ngach bac luong</button>
        </div>

        {tab === "dept" && (
          <div>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: 12 }}>
              <button onClick={() => { setEditDept(null); setDeptForm({ maPhongBan: "", tenPhongBan: "", phongBanChaId: "", dinhBien: 0 }); setShowDeptForm(true); }} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 14 }}>+ Them phong ban</button>
            </div>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Dang tai...</div> : (
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Ma PB</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Ten phong ban</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Cap do</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Dinh bien</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Trang thai</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Hanh dong</th>
                </tr></thead>
                <tbody>
                  {depts.map(d => <DeptRow key={d.phongBanId} dept={d} onEdit={openEditDept} onClose={handleCloseDept} />)}
                </tbody>
              </table>
            )}
          </div>
        )}

        {tab === "grade" && (
          <div>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: 12 }}>
              <button onClick={() => { setEditGrade(null); setGradeForm({ maNgach: "", tenNgach: "", soBac: 5, luongCoSo: 0, heSo: 1.0 }); setShowGradeForm(true); }} style={{ padding: "8px 16px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 14 }}>+ Them ngach bac</button>
            </div>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Dang tai...</div> : (
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Ma ngach</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Ten ngach</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>So bac</th>
                  <th style={{ padding: "10px 8px", textAlign: "right", fontSize: 13 }}>Luong co so</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>He so</th>
                  <th style={{ padding: "10px 8px", textAlign: "right", fontSize: 13 }}>Luong bac 1</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Trang thai</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Hanh dong</th>
                </tr></thead>
                <tbody>
                  {grades.map(g => (
                    <tr key={g.id} style={{ borderTop: "1px solid #e2e8f0" }}>
                      <td style={{ padding: "10px 16px", fontFamily: "monospace", fontWeight: 600 }}>{g.maNgach}</td>
                      <td style={{ padding: "10px 8px" }}>{g.tenNgach}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}>{g.soBac}</td>
                      <td style={{ padding: "10px 8px", textAlign: "right" }}>{fmtVND(g.luongCoSo)}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}>{g.heSo}x</td>
                      <td style={{ padding: "10px 8px", textAlign: "right", fontWeight: 600 }}>{fmtVND(g.luongCoSo * g.heSo)}</td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}><span style={statusBadge(g.active)}>{g.active ? "Hoat dong" : "Dong"}</span></td>
                      <td style={{ padding: "10px 8px" }}>
                        <button onClick={() => openEditGrade(g)} style={{ marginRight: 4, padding: "4px 8px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Sua</button>
                        {g.active && <button onClick={() => handleCloseGrade(g.id)} style={{ padding: "4px 8px", background: "#ef4444", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Dong</button>}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>

      {showDeptForm && (
        <div style={formStyle} onClick={() => setShowDeptForm(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px", fontSize: 16 }}>{editDept ? "Sua phong ban" : "Them phong ban"}</h3>
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>Ma phong ban</label>
            <input style={inputStyle} value={deptForm.maPhongBan} onChange={e => setDeptForm(f => ({ ...f, maPhongBan: e.target.value }))} placeholder="VD: PGD, PNS" />
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>Ten phong ban</label>
            <input style={inputStyle} value={deptForm.tenPhongBan} onChange={e => setDeptForm(f => ({ ...f, tenPhongBan: e.target.value }))} placeholder="VD: Phong Nhan su" />
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>Phong ban cha</label>
            <select style={inputStyle} value={deptForm.phongBanChaId} onChange={e => setDeptForm(f => ({ ...f, phongBanChaId: e.target.value }))}>
              <option value="">Khong co (Root)</option>
              {depts.filter(d => d.phongBanId !== editDept?.phongBanId).map(d => <option key={d.phongBanId} value={d.phongBanId}>{d.maPhongBan} — {d.tenPhongBan}</option>)}
            </select>
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>Dinh bien</label>
            <input style={inputStyle} type="number" value={deptForm.dinhBien} onChange={e => setDeptForm(f => ({ ...f, dinhBien: parseInt(e.target.value) || 0 }))} />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
              <button onClick={() => setShowDeptForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", color: "#334", border: "none", borderRadius: 6, cursor: "pointer" }}>Huy</button>
              <button onClick={handleSaveDept} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Luu</button>
            </div>
          </div>
        </div>
      )}

      {showGradeForm && (
        <div style={formStyle} onClick={() => setShowGradeForm(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px", fontSize: 16 }}>{editGrade ? "Sua ngach bac" : "Them ngach bac"}</h3>
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>Ma ngach</label>
            <input style={inputStyle} value={gradeForm.maNgach} onChange={e => setGradeForm(f => ({ ...f, maNgach: e.target.value }))} placeholder="VD: NV01" />
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>Ten ngach</label>
            <input style={inputStyle} value={gradeForm.tenNgach} onChange={e => setGradeForm(f => ({ ...f, tenNgach: e.target.value }))} placeholder="VD: Nhan vien" />
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>So bac</label>
            <input style={inputStyle} type="number" value={gradeForm.soBac} onChange={e => setGradeForm(f => ({ ...f, soBac: parseInt(e.target.value) || 1 }))} />
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>Luong co so (VND)</label>
            <input style={inputStyle} type="number" value={gradeForm.luongCoSo} onChange={e => setGradeForm(f => ({ ...f, luongCoSo: parseInt(e.target.value) || 0 }))} placeholder="VD: 2340000" />
            <label style={{ fontSize: 13, color: "#64748b", marginBottom: 4, display: "block" }}>He so bac 1</label>
            <input style={inputStyle} type="number" step="0.1" value={gradeForm.heSo} onChange={e => setGradeForm(f => ({ ...f, heSo: parseFloat(e.target.value) || 1 }))} />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
              <button onClick={() => setShowGradeForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", color: "#334", border: "none", borderRadius: 6, cursor: "pointer" }}>Huy</button>
              <button onClick={handleSaveGrade} style={{ padding: "8px 16px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Luu</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
