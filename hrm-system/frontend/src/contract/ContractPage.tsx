// T06: Trang quản lý hợp đồng lao động
import { useState, useEffect } from "react";
import { PageHeader } from "../components/SharedComponents";
import { contractApi } from "../api";
import { employeeApi } from "../api";

const LOAI_HD: Record<string, string> = {
  THU_VIEC: "Thử việc", XAC_DINH_THOI_HAN: "Xác định thời hạn",
  KHONG_XAC_DINH_THOI_HAN: "Không xác định thời hạn", PHU_LUC: "Phụ lục",
};

const TT_HD: Record<string, { bg: string; color: string }> = {
  HIEU_LUC: { bg: "#dcfce7", color: "#166534" },
  HET_HIEU_LUC: { bg: "#fee2e2", color: "#991b1b" },
  DA_THANH_LY: { bg: "#f3f4f6", color: "#6b7280" },
  HUY: { bg: "#f3f4f6", color: "#6b7280" },
};

function StatusChip({ status }: { status: string }) {
  const s = TT_HD[status] || { bg: "#f3f4f6", color: "#6b7280" };
  const label: Record<string, string> = { HIEU_LUC: "Hiệu lực", HET_HIEU_LUC: "Hết hiệu lực", DA_THANH_LY: "Đã thanh lý", HUY: "Huỷ" };
  return <span style={{ background: s.bg, color: s.color, padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{label[status] || status}</span>;
}

export default function ContractPage() {
  const [tab, setTab] = useState<"list" | "expiring">("list");
  const [contracts, setContracts] = useState<any[]>([]);
  const [expiring, setExpiring] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedNv, setSelectedNv] = useState("");
  const [employees, setEmployees] = useState<any[]>([]);
  const [empContracts, setEmpContracts] = useState<any[]>([]);
  const [selectedEmp, setSelectedEmp] = useState<any>(null);
  const [showCreate, setShowCreate] = useState(false);
  const [showAddendum, setShowAddendum] = useState<string | null>(null);
  const [form, setForm] = useState({ soHopDong: "", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "", ngayHetHieuLuc: "", mucLuongThoaThuan: 0, phuCapCoDinh: {} as Record<string, number> });
  const [addForm, setAddForm] = useState({ ngayHieuLuc: "", ngayHetHieuLuc: "" });
  const [msg, setMsg] = useState<{ type: string; text: string } | null>(null);

  useEffect(() => { loadEmployees(); loadExpiring(); }, []);

  async function loadEmployees() {
    try {
      const r = await employeeApi.list("", undefined, undefined, 0, 100);
      setEmployees(r.content);
    } catch {}
  }

  async function loadExpiring() {
    try {
      const data = await contractApi.listExpiring(1, 90);
      setExpiring(Array.isArray(data) ? data : []);
    } catch {}
  }

  async function selectEmployee(nvId: string) {
    setSelectedNv(nvId);
    if (!nvId) { setSelectedEmp(null); setEmpContracts([]); return; }
    const nv = employees.find(e => e.nhanVienId === nvId);
    setSelectedEmp(nv);
    try {
      const data = await contractApi.listByEmployee(nvId);
      setEmpContracts(Array.isArray(data) ? data : data.content || []);
    } catch {}
  }

  function showMsg(type: string, text: string) {
    setMsg({ type, text }); setTimeout(() => setMsg(null), 3000);
  }

  async function handleCreate() {
    if (!selectedNv) return;
    try {
      await contractApi.create(selectedNv, form);
      setShowCreate(false);
      selectEmployee(selectedNv);
      showMsg("ok", "Tạo hợp đồng thành công");
    } catch (e: any) { showMsg("err", e.message); }
  }

  async function handleAddendum(gocId: string) {
    if (!selectedNv) return;
    try {
      await contractApi.addAddendum(gocId, addForm);
      setShowAddendum(null);
      setAddForm({ ngayHieuLuc: "", ngayHetHieuLuc: "" });
      selectEmployee(selectedNv);
      showMsg("ok", "Tạo phụ lục thành công");
    } catch (e: any) { showMsg("err", e.message); }
  }

  const fmtVND = (v: number) => !v ? "-" : new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(v);
  const cardStyle: any = { background: "#fff", borderRadius: 12, padding: 24, width: 480 };
  const inputStyle: any = { width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14, boxSizing: "border-box", marginBottom: 12 };

  return (
    <div>
      <PageHeader title="Hợp đồng lao động" subtitle="T06 — Quản lý hợp đồng, phụ lục, cảnh báo hết hạn" />
      <div style={{ padding: "16px 24px" }}>
        {msg && <div style={{ padding: "8px 16px", background: msg.type === "ok" ? "#dcfce7" : "#fee2e2", color: msg.type === "ok" ? "#166534" : "#991b1b", borderRadius: 6, marginBottom: 12 }}>{msg.text}</div>}

        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          <button onClick={() => setTab("list")} style={{ padding: "8px 16px", background: tab === "list" ? "#3b82f6" : "#e2e8f0", color: tab === "list" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>Danh sách HĐ</button>
          <button onClick={() => setTab("expiring")} style={{ padding: "8px 16px", background: tab === "expiring" ? "#f59e0b" : "#e2e8f0", color: tab === "expiring" ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>
            Sắp hết hạn ({expiring.length})
          </button>
        </div>

        {tab === "list" && (
          <div style={{ display: "flex", gap: 16 }}>
            <div style={{ flex: "0 0 280px" }}>
              <select value={selectedNv} onChange={e => selectEmployee(e.target.value)} style={{ ...inputStyle, marginBottom: 12, width: "100%" }}>
                <option value="">Chọn nhân viên...</option>
                {employees.map(emp => <option key={emp.nhanVienId} value={emp.nhanVienId}>{emp.maNv} — {emp.hoTen}</option>)}
              </select>
              {selectedEmp && (
                <div style={{ background: "#fff", borderRadius: 8, padding: 16, boxShadow: "0 1px 3px rgba(0,0,0,0.08)", marginBottom: 12 }}>
                  <div style={{ fontWeight: 700, fontSize: 15 }}>{selectedEmp.hoTen}</div>
                  <div style={{ fontSize: 13, color: "#64748b", marginTop: 4 }}>{selectedEmp.maNv} — {selectedEmp.phongBan}</div>
                  <div style={{ fontSize: 13, color: "#64748b" }}>{selectedEmp.email}</div>
                  <button onClick={() => { setShowCreate(true); setForm({ soHopDong: "", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "", ngayHetHieuLuc: "", mucLuongThoaThuan: 0, phuCapCoDinh: {} }); }} style={{ marginTop: 12, padding: "8px 12px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 13, width: "100%" }}>+ Tạo hợp đồng</button>
                </div>
              )}
            </div>

            <div style={{ flex: 1 }}>
              {!selectedNv && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8", background: "#fff", borderRadius: 8 }}>Chọn nhân viên để xem hợp đồng</div>}
              {selectedNv && empContracts.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8", background: "#fff", borderRadius: 8 }}>Chưa có hợp đồng</div>}
              {empContracts.length > 0 && (
                <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                  {empContracts.map((c, i) => (
                    <div key={c.id} style={{ padding: "16px 20px", borderBottom: i < empContracts.length - 1 ? "1px solid #e2e8f0" : "none" }}>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
                        <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
                          <span style={{ fontFamily: "monospace", fontWeight: 700 }}>{c.soHopDong}</span>
                          <span style={{ fontSize: 13, color: "#64748b" }}>{LOAI_HD[c.loaiHopDong] || c.loaiHopDong}</span>
                          {c.hopDongGocId && <span style={{ fontSize: 11, background: "#fef3c7", color: "#92400e", padding: "1px 6px", borderRadius: 4 }}>Phụ lục</span>}
                        </div>
                        <StatusChip status={c.trangThai} />
                      </div>
                      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 8, fontSize: 13, color: "#475569" }}>
                        <div>Hiệu lực: <strong>{c.ngayHieuLuc}</strong></div>
                        <div>Hết: <strong>{c.ngayHetHieuLuc || "Không có"}</strong></div>
                        <div>Lương: <strong style={{ color: "#166534" }}>{fmtVND(c.mucLuongThoaThuan)}</strong></div>
                      </div>
                      {c.phuCapCoDinh && Object.keys(c.phuCapCoDinh).length > 0 && (
                        <div style={{ marginTop: 8, display: "flex", gap: 8, flexWrap: "wrap" }}>
                          {Object.entries(c.phuCapCoDinh).map(([k, v]) => (
                            <span key={k} style={{ fontSize: 12, background: "#f0fdf4", color: "#166534", padding: "2px 8px", borderRadius: 4 }}>{k}: {fmtVND(v as number)}</span>
                          ))}
                        </div>
                      )}
                      {c.trangThai === "HIEU_LUC" && (
                        <button onClick={() => setShowAddendum(c.id)} style={{ marginTop: 8, padding: "4px 10px", background: "#fef3c7", color: "#92400e", border: "1px solid #fde68a", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>+ Tạo phụ lục</button>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {tab === "expiring" && (
          <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
            {expiring.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Không có hợp đồng sắp hết hạn trong 90 ngày</div>}
            {expiring.map((c, i) => (
              <div key={c.id} style={{ padding: "14px 20px", borderBottom: i < expiring.length - 1 ? "1px solid #e2e8f0" : "none", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <div>
                  <div style={{ fontWeight: 600 }}>{c.hoTen} — <span style={{ fontFamily: "monospace" }}>{c.soHopDong}</span></div>
                  <div style={{ fontSize: 13, color: "#64748b", marginTop: 2 }}>{LOAI_HD[c.loaiHopDong] || c.loaiHopDong} · Hết: {c.ngayHetHieuLuc}</div>
                </div>
                <div style={{ textAlign: "right" }}>
                  <div style={{ fontSize: 13, fontWeight: 700, color: c.soNgayConLai <= 30 ? "#991b1b" : "#d97706" }}>{c.soNgayConLai} ngày</div>
                  <div style={{ fontSize: 12, color: "#64748b" }}>còn lại</div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {showCreate && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowCreate(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Tạo hợp đồng</h3>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Số hợp đồng *</label>
            <input style={inputStyle} value={form.soHopDong} onChange={e => setForm(f => ({ ...f, soHopDong: e.target.value }))} placeholder="VD: HD-2026-001" />
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Loại hợp đồng</label>
            <select style={inputStyle} value={form.loaiHopDong} onChange={e => setForm(f => ({ ...f, loaiHopDong: e.target.value }))}>
              <option value="XAC_DINH_THOI_HAN">Xác định thời hạn</option>
              <option value="KHONG_XAC_DINH_THOI_HAN">Không xác định thời hạn</option>
              <option value="THU_VIEC">Thử việc</option>
            </select>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0 16px" }}>
              <div>
                <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ngày hiệu lực *</label>
                <input style={inputStyle} type="date" value={form.ngayHieuLuc} onChange={e => setForm(f => ({ ...f, ngayHieuLuc: e.target.value }))} />
              </div>
              <div>
                <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ngày hết hạn</label>
                <input style={inputStyle} type="date" value={form.ngayHetHieuLuc} onChange={e => setForm(f => ({ ...f, ngayHetHieuLuc: e.target.value }))} />
              </div>
            </div>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Mức lương thoả thuận (VND)</label>
            <input style={inputStyle} type="number" value={form.mucLuongThoaThuan} onChange={e => setForm(f => ({ ...f, mucLuongThoaThuan: parseFloat(e.target.value) || 0 }))} />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowCreate(false)} style={{ padding: "8px 16px", background: "#e2e8f0", color: "#334", border: "none", borderRadius: 6, cursor: "pointer" }}>Huỷ</button>
              <button onClick={handleCreate} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Lưu</button>
            </div>
          </div>
        </div>
      )}

      {showAddendum && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowAddendum(null)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Tạo phụ lục hợp đồng</h3>
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ngày hiệu lực</label>
            <input style={inputStyle} type="date" value={addForm.ngayHieuLuc} onChange={e => setAddForm(f => ({ ...f, ngayHieuLuc: e.target.value }))} />
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ngày hết hạn mới</label>
            <input style={inputStyle} type="date" value={addForm.ngayHetHieuLuc} onChange={e => setAddForm(f => ({ ...f, ngayHetHieuLuc: e.target.value }))} />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
              <button onClick={() => setShowAddendum(null)} style={{ padding: "8px 16px", background: "#e2e8f0", color: "#334", border: "none", borderRadius: 6, cursor: "pointer" }}>Huỷ</button>
              <button onClick={() => handleAddendum(showAddendum)} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Lưu</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
