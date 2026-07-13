// T11: System Admin page (Company, Role, User, Audit)
import { useState, useEffect } from "react";
import { PageHeader } from "../components/SharedComponents";
import { companyApi, userApi } from "../api";

function Chip({ bg, color, children }: any) {
  return <span style={{ background: bg, color, padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{children}</span>;
}

function StatusChip({ status }: { status: string }) {
  const COLORS: Record<string, { bg: string; color: string }> = {
    ACTIVE: { bg: "#dcfce7", color: "#166534" },
    LOCKED: { bg: "#fee2e2", color: "#991b1b" },
    PENDING: { bg: "#fef3c7", color: "#92400e" },
  };
  const c = COLORS[status] || { bg: "#f3f4f6", color: "#6b7280" };
  const L: Record<string, string> = { ACTIVE: "Active", LOCKED: "Bi khoa", PENDING: "Cho kich hoat" };
  return <Chip bg={c.bg} color={c.color}>{L[status] || status}</Chip>;
}

function fmtDate(iso: string | null) {
  if (!iso) return "-";
  return new Date(iso).toLocaleString("vi-VN");
}

const ROLES = [
  { code: "SYSTEM_ADMIN", name: "Quan tri he thong" },
  { code: "COMPANY_ADMIN", name: "Quan tri cong ty" },
  { code: "HR_MANAGER", name: "Quan ly Nhan su" },
  { code: "ACCOUNTANT", name: "Ke toan" },
  { code: "MANAGER", name: "Quan ly" },
  { code: "EMPLOYEE", name: "Nhan vien" },
];

export default function SystemAdminPage() {
  const [tab, setTab] = useState<"company" | "user" | "audit">("company");
  const [companies, setCompanies] = useState<any[]>([]);
  const [users, setUsers] = useState<any[]>([]);
  const [auditPage, setAuditPage] = useState<any>(null);
  const [auditFilter, setAuditFilter] = useState({ module: "" });
  const [loading, setLoading] = useState(true);
  const [showCoForm, setShowCoForm] = useState(false);
  const [showUserForm, setShowUserForm] = useState(false);
  const [coForm, setCoForm] = useState({ maSoThue: "", tenCongTy: "", diaChi: "", dienThoai: "", email: "", ngayThanhLap: "" });
  const [userForm, setUserForm] = useState({ username: "", email: "", roleCodes: [] as string[], employeeId: "" });
  const [msg, setMsg] = useState<{ type: string; text: string } | null>(null);

  useEffect(() => { loadData(); }, [tab]);

  async function loadData() {
    setLoading(true);
    try {
      if (tab === "company") {
        const data = await companyApi.list();
        setCompanies(Array.isArray(data) ? data : data.content || []);
      }
      else if (tab === "user") {
        const data = await userApi.list("");
        setUsers(Array.isArray(data) ? data : data.content || []);
      }
      else if (tab === "audit") {
        // audit not yet in api.ts; use empty for now
        setAuditPage({ content: [], totalElements: 0 });
      }
    } finally { setLoading(false); }
  }

  function showMsg(type: string, text: string) {
    setMsg({ type, text }); setTimeout(() => setMsg(null), 3000);
  }

  async function handleCreateCompany() {
    try { await companyApi.create(coForm); setShowCoForm(false); loadData(); showMsg("ok", "Tao cong ty thanh cong"); }
    catch (e: any) { showMsg("err", e.message); }
  }

  async function handleCreateUser() {
    try { await userApi.create(userForm); setShowUserForm(false); loadData(); showMsg("ok", "Tao tai khoan thanh cong"); }
    catch (e: any) { showMsg("err", e.message); }
  }

  async function handleLockUnlock(userId: string, lock: boolean) {
    try { lock ? await userApi.lock(userId) : await userApi.unlock(userId); loadData(); showMsg("ok", lock ? "Khoa tai khoan" : "Mo khoa tai khoan"); }
    catch (e: any) { showMsg("err", e.message); }
  }

  const cardStyle: any = { background: "#fff", borderRadius: 12, padding: 24, width: 480 };
  const inputStyle: any = { width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14, boxSizing: "border-box", marginBottom: 12 };

  return (
    <div>
      <PageHeader title="Quan tri he thong" subtitle="T11 — Cong ty, tai khoan, phan quyen, lich su kiem toan" />
      <div style={{ padding: "16px 24px" }}>
        {msg && <div style={{ padding: "8px 16px", background: msg.type === "ok" ? "#dcfce7" : "#fee2e2", color: msg.type === "ok" ? "#166534" : "#991b1b", borderRadius: 6, marginBottom: 12 }}>{msg.text}</div>}

        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          {[
            { key: "company", label: "Cong ty", color: "#3b82f6" },
            { key: "user", label: "Tai khoan", color: "#10b981" },
            { key: "audit", label: "Lich su kiem toan", color: "#6b7280" },
          ].map(t => (
            <button key={t.key} onClick={() => setTab(t.key as any)} style={{ padding: "8px 16px", background: tab === t.key ? t.color : "#e2e8f0", color: tab === t.key ? "#fff" : "#334", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>
              {t.label}
            </button>
          ))}
        </div>

        {/* COMPANIES */}
        {tab === "company" && (
          <div>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: 12 }}>
              <button onClick={() => { setCoForm({ maSoThue: "", tenCongTy: "", diaChi: "", dienThoai: "", email: "", ngayThanhLap: "" }); setShowCoForm(true); }} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Them cong ty</button>
            </div>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Dang tai...</div> : (
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(320px, 1fr))", gap: 16 }}>
                {companies.map(co => (
                  <div key={co.companyId} style={{ background: "#fff", borderRadius: 12, padding: 20, boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 12 }}>
                      <div>
                        <div style={{ fontWeight: 700, fontSize: 15, marginBottom: 4 }}>{co.tenCongTy}</div>
                        <div style={{ fontSize: 13, color: "#64748b" }}>{co.maSoThue}</div>
                      </div>
                      <StatusChip status={co.trangThai} />
                    </div>
                    <div style={{ fontSize: 13, color: "#475569", display: "grid", gap: 4 }}>
                      <div>Dia chi: <strong>{co.diaChi}</strong></div>
                      <div>DT: <strong>{co.dienThoai}</strong> · Email: <strong>{co.email}</strong></div>
                      <div>Thanh lap: <strong>{co.ngayThanhLap}</strong></div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* USERS */}
        {tab === "user" && (
          <div>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: 12 }}>
              <button onClick={() => { setUserForm({ username: "", email: "", roleCodes: [], employeeId: "" }); setShowUserForm(true); }} style={{ padding: "8px 16px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>+ Them tai khoan</button>
            </div>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Dang tai...</div> : (
              <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                <thead><tr style={{ background: "#f8fafc" }}>
                  <th style={{ padding: "10px 16px", textAlign: "left", fontSize: 13 }}>Username</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Email</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Vai tro</th>
                  <th style={{ padding: "10px 8px", textAlign: "center", fontSize: 13 }}>Trang thai</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Dang nhap cuoi</th>
                  <th style={{ padding: "10px 8px", textAlign: "left", fontSize: 13 }}>Hanh dong</th>
                </tr></thead>
                <tbody>
                  {users.map(u => (
                    <tr key={u.userId} style={{ borderTop: "1px solid #e2e8f0" }}>
                      <td style={{ padding: "10px 16px", fontWeight: 600 }}>{u.username}</td>
                      <td style={{ padding: "10px 8px", fontSize: 13, color: "#475569" }}>{u.email}</td>
                      <td style={{ padding: "10px 8px" }}>
                        <div style={{ display: "flex", gap: 4, flexWrap: "wrap" }}>
                          {(u.roleCodes || []).map((r: string) => <Chip key={r} bg="#ede9fe" color="#5b21b6">{r}</Chip>)}
                        </div>
                      </td>
                      <td style={{ padding: "10px 8px", textAlign: "center" }}><StatusChip status={u.trangThai} /></td>
                      <td style={{ padding: "10px 8px", fontSize: 12, color: "#64748b" }}>{fmtDate(u.lastLogin)}</td>
                      <td style={{ padding: "10px 8px" }}>
                        {u.trangThai === "LOCKED" ? (
                          <button onClick={() => handleLockUnlock(u.userId, false)} style={{ padding: "4px 8px", background: "#dcfce7", color: "#166534", border: "1px solid #86efac", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Mo khoa</button>
                        ) : (
                          <button onClick={() => handleLockUnlock(u.userId, true)} style={{ padding: "4px 8px", background: "#fee2e2", color: "#991b1b", border: "1px solid #fca5a5", borderRadius: 4, cursor: "pointer", fontSize: 12 }}>Khoa</button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {/* AUDIT */}
        {tab === "audit" && (
          <div>
            <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
              <select value={auditFilter.module} onChange={e => { setAuditFilter({ module: e.target.value }); }} style={{ ...inputStyle, width: 180, marginBottom: 0 }}>
                <option value="">Tat ca module</option>
                <option value="hr">HR</option>
                <option value="payroll">Payroll</option>
                <option value="system">System</option>
                <option value="timekeeping">Timekeeping</option>
                <option value="social_ins">Social Ins</option>
              </select>
              <button onClick={loadData} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Loc</button>
            </div>
            {loading ? <div style={{ padding: 24, color: "#64748b" }}>Dang tai...</div> : (
              <div>
                <div style={{ fontSize: 13, color: "#64748b", marginBottom: 8 }}>Tong: {auditPage?.totalElements || 0} ban ghi</div>
                <div style={{ background: "#fff", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
                  {auditPage?.content?.length === 0 && <div style={{ padding: 32, textAlign: "center", color: "#94a3b8" }}>Khong co du lieu</div>}
                  {(auditPage?.content || []).map((log: any, i: number) => (
                    <div key={log.id} style={{ padding: "12px 20px", borderBottom: i < (auditPage.content?.length || 0) - 1 ? "1px solid #f1f5f9" : "none" }}>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 4 }}>
                        <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
                          <Chip bg="#dbeafe" color="#1e40af">{log.module}</Chip>
                          <span style={{ fontWeight: 600, fontSize: 14 }}>{log.action}</span>
                        </div>
                        <span style={{ fontSize: 12, color: "#94a3b8" }}>{fmtDate(log.timestamp)}</span>
                      </div>
                      <div style={{ fontSize: 12, color: "#64748b" }}>
                        Entity: <strong>{log.entityType}</strong> ({log.entityId}) · User: <strong>{log.userId}</strong>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Company form */}
      {showCoForm && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowCoForm(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Them cong ty</h3>
            {[["maSoThue", "Ma so thue *"], ["tenCongTy", "Ten cong ty *"], ["diaChi", "Dia chi"], ["dienThoai", "Dien thoai"], ["email", "Email"]].map(([f, l]) => (
              <div key={String(f)}>
                <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>{String(l)}</label>
                <input style={inputStyle} value={(coForm as any)[String(f)]} onChange={e => setCoForm(f => ({ ...f, [String(f)]: e.target.value }))} />
              </div>
            ))}
            <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Ngay thanh lap</label>
            <input style={inputStyle} type="date" value={coForm.ngayThanhLap} onChange={e => setCoForm(f => ({ ...f, ngayThanhLap: e.target.value }))} />
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 8 }}>
              <button onClick={() => setShowCoForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huy</button>
              <button onClick={handleCreateCompany} style={{ padding: "8px 16px", background: "#3b82f6", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Tao</button>
            </div>
          </div>
        </div>
      )}

      {/* User form */}
      {showUserForm && (
        <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }} onClick={() => setShowUserForm(false)}>
          <div style={cardStyle} onClick={e => e.stopPropagation()}>
            <h3 style={{ margin: "0 0 16px" }}>Them tai khoan</h3>
            <div>
              <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Username *</label>
              <input style={inputStyle} value={userForm.username} onChange={e => setUserForm(f => ({ ...f, username: e.target.value }))} />
            </div>
            <div>
              <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 4 }}>Email *</label>
              <input style={inputStyle} value={userForm.email} onChange={e => setUserForm(f => ({ ...f, email: e.target.value }))} />
            </div>
            <div>
              <label style={{ fontSize: 13, color: "#64748b", display: "block", marginBottom: 8 }}>Vai tro</label>
              <div style={{ display: "flex", flexDirection: "column", gap: 6, maxHeight: 160, overflowY: "auto" }}>
                {ROLES.map(role => (
                  <label key={role.code} style={{ display: "flex", gap: 8, alignItems: "center", cursor: "pointer" }}>
                    <input type="checkbox" checked={userForm.roleCodes.includes(role.code)} onChange={ev => setUserForm(f => ({ ...f, roleCodes: ev.target.checked ? [...f.roleCodes, role.code] : f.roleCodes.filter(c => c !== role.code) }))} />
                    <span style={{ fontSize: 13 }}>{role.code} — {role.name}</span>
                  </label>
                ))}
              </div>
            </div>
            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 16 }}>
              <button onClick={() => setShowUserForm(false)} style={{ padding: "8px 16px", background: "#e2e8f0", border: "none", borderRadius: 6, cursor: "pointer" }}>Huy</button>
              <button onClick={handleCreateUser} style={{ padding: "8px 16px", background: "#10b981", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}>Tao</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
