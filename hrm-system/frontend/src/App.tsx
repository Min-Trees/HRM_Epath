import { HashRouter, Routes, Route, useLocation, useNavigate, useParams } from "react-router-dom";
import React, { useState, ReactNode, useEffect } from "react";
import DepartmentPage from "./department/DepartmentPage";
import EmployeePage from "./employee/EmployeePage";
import ContractPage from "./contract/ContractPage";
import AttendancePage from "./attendance/AttendancePage";
import LeavePage from "./leave/LeavePage";
import SystemAdminPage from "./system/SystemAdminPage";
import OffboardingListPage from "./offboarding/OffboardingListPage";
import OffboardingDetailPage from "./offboarding/OffboardingDetailPage";
import BhxhReportPage from "./bhxh/BhxhReportPage";
import QuyetToanThuePage from "./tax/QuyetToanThuePage";
import RecruitmentListPage from "./recruitment/RecruitmentListPage";
import KpiPage from "./performance/KpiPage";
import PayrollRunPage from "./payroll/PayrollRunPage";
import TrainingPage from "./training/TrainingPage";

// ============================================================================
// SHARED NAVIGATION ITEMS
// ============================================================================
interface NavItem {
  path: string;
  label: string;
  task: string;
  color: string;
  icon: string;
}

const NAV_ITEMS: NavItem[] = [
  { path: "/", label: "Trang chu", task: "", color: "#64748b", icon: "🏠" },
  { path: "/hr/departments", label: "Co cau to chuc", task: "T04", color: "#3b82f6", icon: "🏢" },
  { path: "/hr/employees", label: "Nhan su", task: "T05", color: "#10b981", icon: "👤" },
  { path: "/hr/contracts", label: "Hop dong", task: "T06", color: "#8b5cf6", icon: "📄" },
  { path: "/attendance", label: "Cham cong", task: "T08+T09", color: "#06b6d4", icon: "🕐" },
  { path: "/leave", label: "Nghi phep & OT", task: "T10", color: "#f59e0b", icon: "🏖" },
  { path: "/system", label: "Quan tri he thong", task: "T11", color: "#64748b", icon: "⚙" },
  { path: "/offboarding", label: "Ho so nghi viec", task: "T14", color: "#ef4444", icon: "📋" },
  { path: "/bhxh/reports", label: "Bao cao BHXH", task: "T15", color: "#f59e0b", icon: "🏥" },
  { path: "/tax/qtt", label: "Quyet toan thue TNCN", task: "T16", color: "#3b82f6", icon: "📊" },
  { path: "/recruitment", label: "Tuyen dung", task: "T17", color: "#8b5cf6", icon: "👥" },
  { path: "/performance/kpi", label: "KPI / OKR", task: "T18", color: "#06b6d4", icon: "🎯" },
  { path: "/payroll/run", label: "Payroll Run", task: "T19", color: "#10b981", icon: "💰" },
  { path: "/training", label: "Dao tao", task: "T20", color: "#ec4899", icon: "🎓" },
];

// ============================================================================
// APP LAYOUT — wraps every page with sidebar
// ============================================================================
function AppLayout({ children }: { children: ReactNode }) {
  const location = useLocation();
  const navigate = useNavigate();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  const isActive = (path: string) => {
    if (path === "/") return location.pathname === "/";
    return location.pathname.startsWith(path);
  };

  return (
    <div style={{ display: "flex", minHeight: "100vh", fontFamily: "system-ui, sans-serif", background: "#f8fafc" }}>
      {/* ── Sidebar ── */}
      <aside style={{
        width: sidebarCollapsed ? 64 : 240,
        background: "#1e293b",
        color: "#e2e8f0",
        display: "flex",
        flexDirection: "column",
        transition: "width 0.2s",
        flexShrink: 0,
        position: "sticky",
        top: 0,
        height: "100vh",
        overflow: "hidden",
      }}>
        {/* Logo */}
        <div style={{
          padding: sidebarCollapsed ? "16px 8px" : "16px 20px",
          borderBottom: "1px solid #334155",
          display: "flex",
          alignItems: "center",
          gap: 12,
          minHeight: 60,
        }}>
          <span style={{ fontSize: 22 }}>🏢</span>
          {!sidebarCollapsed && (
            <div>
              <div style={{ fontWeight: 700, fontSize: 15, color: "#fff", lineHeight: 1.2 }}>HRM_Epath</div>
              <div style={{ fontSize: 11, color: "#94a3b8" }}>V13 · 2026</div>
            </div>
          )}
        </div>

        {/* Nav items */}
        <nav style={{ flex: 1, overflowY: "auto", padding: "8px 0" }}>
          {NAV_ITEMS.map((item) => {
            const active = isActive(item.path);
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                title={sidebarCollapsed ? item.label : ""}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: 10,
                  width: "100%",
                  padding: sidebarCollapsed ? "10px 0" : "10px 16px",
                  justifyContent: sidebarCollapsed ? "center" : "flex-start",
                  background: active ? "#2563eb" : "transparent",
                  color: active ? "#fff" : "#94a3b8",
                  border: "none",
                  cursor: "pointer",
                  textAlign: "left",
                  fontSize: 13,
                  fontWeight: active ? 600 : 400,
                  transition: "all 0.15s",
                  borderRadius: 0,
                }}
                onMouseEnter={(e) => {
                  if (!active) {
                    e.currentTarget.style.background = "#334155";
                    e.currentTarget.style.color = "#e2e8f0";
                  }
                }}
                onMouseLeave={(e) => {
                  if (!active) {
                    e.currentTarget.style.background = "transparent";
                    e.currentTarget.style.color = "#94a3b8";
                  }
                }}
              >
                <span style={{ fontSize: 16, flexShrink: 0 }}>{item.icon}</span>
                {!sidebarCollapsed && (
                  <span style={{ flex: 1, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                    {item.label}
                  </span>
                )}
                {!sidebarCollapsed && item.task && (
                  <span style={{
                    fontSize: 10,
                    background: item.color + "33",
                    color: item.color,
                    padding: "1px 5px",
                    borderRadius: 4,
                    fontWeight: 600,
                    flexShrink: 0,
                  }}>
                    {item.task}
                  </span>
                )}
              </button>
            );
          })}
        </nav>

        {/* Collapse toggle */}
        <button
          onClick={() => setSidebarCollapsed((c) => !c)}
          style={{
            padding: "12px",
            background: "transparent",
            border: "none",
            borderTop: "1px solid #334155",
            color: "#64748b",
            cursor: "pointer",
            display: "flex",
            justifyContent: "center",
            fontSize: 16,
          }}
        >
          {sidebarCollapsed ? "▶" : "◀"}
        </button>
      </aside>

      {/* ── Main content ── */}
      <main style={{ flex: 1, overflow: "auto", minWidth: 0 }}>
        {children}
      </main>
    </div>
  );
}

// ============================================================================
// DASHBOARD — full landing page
// ============================================================================
function Dashboard() {
  const modules = [
    { path: "#/hr/departments", label: "Co cau to chuc", task: "T04", color: "#3b82f6" },
    { path: "#/hr/employees", label: "Ho so nhan vien", task: "T05", color: "#10b981" },
    { path: "#/hr/contracts", label: "Hop dong lao dong", task: "T06", color: "#8b5cf6" },
    { path: "#/attendance", label: "Cham cong & Ca lam viec", task: "T08+T09", color: "#06b6d4" },
    { path: "#/leave", label: "Nghi phep & Tang ca", task: "T10", color: "#f59e0b" },
    { path: "#/system", label: "Quan tri he thong", task: "T11", color: "#64748b" },
    { path: "#/offboarding", label: "Ho so nghi viec", task: "T14", color: "#ef4444" },
    { path: "#/bhxh/reports", label: "Bao cao BHXH D02/D03", task: "T15", color: "#f97316" },
    { path: "#/tax/qtt", label: "Quyet toan thue TNCN", task: "T16", color: "#3b82f6" },
    { path: "#/recruitment", label: "Tuyen dung", task: "T17", color: "#8b5cf6" },
    { path: "#/performance/kpi", label: "Danh gia KPI/OKR", task: "T18", color: "#06b6d4" },
    { path: "#/payroll/run", label: "Payroll Run - Ky linh luong", task: "T19", color: "#10b981" },
    { path: "#/training", label: "Dao tao", task: "T20", color: "#ec4899" },
  ];

  return (
    <div style={{ padding: 0 }}>
      {/* Header banner */}
      <div style={{
        background: "linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%)",
        color: "white",
        padding: "40px 48px",
      }}>
        <h1 style={{ margin: 0, fontSize: 28, fontWeight: 700 }}>HRM_Epath</h1>
        <p style={{ margin: "8px 0 0", opacity: 0.85, fontSize: 15 }}>
          He thong quan ly nhan su · V13 · {new Date().getFullYear()}
        </p>
        <div style={{ marginTop: 12, display: "flex", gap: 16, flexWrap: "wrap" }}>
          <span style={{ background: "rgba(255,255,255,0.15)", padding: "4px 12px", borderRadius: 20, fontSize: 13 }}>
            Backend: http://localhost:8080
          </span>
          <span style={{ background: "rgba(255,255,255,0.15)", padding: "4px 12px", borderRadius: 20, fontSize: 13 }}>
            Frontend: http://localhost:5173
          </span>
        </div>
      </div>

      {/* Login info */}
      <div style={{ padding: "0 48px", marginTop: 32, marginBottom: 24 }}>
        <div style={{ background: "#fffbeb", border: "1px solid #fde68a", borderRadius: 12, padding: "16px 24px" }}>
          <strong style={{ color: "#92400e" }}>Tai khoan test</strong>
          <span style={{ color: "#78350f" }}> (password: </span>
          <code style={{ background: "#fef3c7", padding: "1px 6px", borderRadius: 4, color: "#92400e" }}>123456</code>
          <span style={{ color: "#78350f" }}>)</span>
          <div style={{ marginTop: 8, display: "flex", gap: 8, flexWrap: "wrap" }}>
            {[
              { user: "a.nguyen", roles: "HR_MANAGER + HR" },
              { user: "b.tran", roles: "EMPLOYEE" },
              { user: "c.le", roles: "MANAGER" },
              { user: "d.pham", roles: "ACCOUNTANT + PAYROLL_ACCOUNTANT" },
              { user: "e.hoang", roles: "COMPANY_ADMIN + HR_MANAGER" },
            ].map((u) => (
              <code key={u.user} style={{ background: "#fef9c3", padding: "2px 10px", borderRadius: 6, fontSize: 13, color: "#713f12" }}>
                {u.user} / {u.roles}
              </code>
            ))}
          </div>
        </div>
      </div>

      {/* Modules grid */}
      <div style={{ padding: "0 48px", marginBottom: 32 }}>
        <h2 style={{ fontSize: 18, fontWeight: 600, color: "#1e293b", marginBottom: 16 }}>
          Cac module ({modules.length} modules)
        </h2>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))", gap: 16 }}>
          {modules.map((m) => (
            <a key={m.path} href={m.path} style={{
              display: "block",
              background: "white",
              border: "1px solid #e2e8f0",
              borderRadius: 12,
              padding: "20px 24px",
              textDecoration: "none",
              color: "#1e293b",
              transition: "all 0.15s",
              boxShadow: "0 1px 3px rgba(0,0,0,0.06)",
            }}
              onMouseEnter={(e) => {
                e.currentTarget.style.borderColor = m.color;
                e.currentTarget.style.boxShadow = `0 4px 12px ${m.color}33`;
                e.currentTarget.style.transform = "translateY(-2px)";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.borderColor = "#e2e8f0";
                e.currentTarget.style.boxShadow = "0 1px 3px rgba(0,0,0,0.06)";
                e.currentTarget.style.transform = "none";
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
                <span style={{
                  background: m.color + "20", color: m.color,
                  fontSize: 11, fontWeight: 600, padding: "2px 8px", borderRadius: 99
                }}>
                  {m.task}
                </span>
                <span style={{ fontSize: 20, color: "#cbd5e1" }}>→</span>
              </div>
              <div style={{ fontWeight: 600, fontSize: 15 }}>{m.label}</div>
            </a>
          ))}
        </div>
      </div>

      {/* Seed data info */}
      <div style={{ padding: "0 48px 48px" }}>
        <div style={{ background: "#f0fdf4", border: "1px solid #bbf7d0", borderRadius: 12, padding: "20px 24px" }}>
          <h3 style={{ margin: "0 0 12px", fontSize: 15, color: "#166534" }}>Seed Data da nap</h3>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))", gap: 8 }}>
            {[
              ["Nhan vien", "5 NV (NV001–NV005) + NV006 (da nghi)"],
              ["Phong ban", "5 phong ban (PGD, PNS, PKT, PIT, PKD)"],
              ["Bang cong", "Thang 5/2026 — 5 NV — trang thai DA_CHOT"],
              ["Bang luong", "5 bang luong — trang thai DA_DUYET"],
              ["Ky linh luong", "2 ky (1 DA_CHI_TRA, 1 DA_DUYET_CAP_2)"],
              ["BHXH", "5 NV — qua trinh DANG_DONG"],
              ["Cam ket 08", "5 NV — UY_QUYEN_QTT"],
              ["Tuyen dung", "2 yeu cau (MOI_TAO + DANG_TUYEN)"],
              ["KPI", "1 cycle Q2/2026 — 4 assignments"],
              ["Dao tao", "3 CT, 2 lop, 6 dang ky, 4 diem danh"],
              ["Offboarding", "1 case NV006 — HOAN_THANH"],
            ].map(([k, v]) => (
              <div key={k} style={{ fontSize: 13 }}>
                <strong style={{ color: "#166534" }}>{k}:</strong>{" "}
                <span style={{ color: "#15803d" }}>{v}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// PAGE WRAPPER — adds consistent header to each page
// ============================================================================
function PageHeader({ title, subtitle }: { title: string; subtitle?: string }) {
  return (
    <div style={{
      background: "white",
      borderBottom: "1px solid #e2e8f0",
      padding: "16px 24px",
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
    }}>
      <div>
        <h2 style={{ margin: 0, fontSize: 18, fontWeight: 600, color: "#0f172a" }}>{title}</h2>
        {subtitle && <p style={{ margin: "2px 0 0", fontSize: 13, color: "#64748b" }}>{subtitle}</p>}
      </div>
    </div>
  );
}

// ============================================================================
// OFFBOARDING LIST — with fixed Vietnamese encoding
// ============================================================================
import { offboardingApi } from "./mock/offboarding.mock";
import { bhxhReportMock } from "./mock/bhxh.mock";
import { quyetToanThueMock } from "./mock/quyet-toan-thue.mock";
import { recruitmentMock } from "./mock/recruitment.mock";
import { kpiMock } from "./mock/kpi.mock";
import { payrollRunMock } from "./mock/payroll-run.mock";
import { trainingMock } from "./mock/training.mock";

// Offboarding List (fixed encoding)
function OffboardingList() {
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

  if (loading) return <div style={{ padding: 24, color: "#64748b" }}>Đang tai...</div>;

  const items = (data?.content || []).filter((c: any) =>
    !filter ||
    c.maNv?.toLowerCase().includes(filter.toLowerCase()) ||
    c.hoTen?.toLowerCase().includes(filter.toLowerCase()) ||
    c.soQuyetDinh?.toLowerCase().includes(filter.toLowerCase())
  );

  return (
    <div>
      <PageHeader title="Ho so nghi viec (Offboarding)" subtitle="T14 — Quan ly ho so, checklist, severance" />
      <div style={{ padding: 24 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
          <input
            type="text"
            placeholder="Tim theo ma NV, ho ten hoac so quyet dinh..."
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            style={{ padding: "8px 12px", width: 360, border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14 }}
          />
          <button
            onClick={() => navigate("/offboarding/new")}
            style={{ padding: "8px 16px", background: "#0d9488", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 14 }}
          >
            + Tao ho so
          </button>
        </div>

        {items.length === 0 && (
          <div style={{ padding: 48, textAlign: "center", color: "#94a3b8", background: "white", borderRadius: 8 }}>
            Khong co du lieu
          </div>
        )}
        {items.length > 0 && (
          <table style={{ width: "100%", borderCollapse: "collapse", background: "white", borderRadius: 8, overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
            <thead>
              <tr style={{ background: "#f8fafc" }}>
                <th style={th}>Ma NV</th>
                <th style={th}>Ho ten</th>
                <th style={th}>So QD</th>
                <th style={th}>Ngay nghi</th>
                <th style={th}>Ly do</th>
                <th style={th}>Trang thai</th>
                <th style={th}>Tien do</th>
                <th style={th}></th>
              </tr>
            </thead>
            <tbody>
              {items.map((c: any) => (
                <tr key={c.caseId} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td}>{c.maNv}</td>
                  <td style={td}>{c.hoTen}</td>
                  <td style={td}>{c.soQuyetDinh}</td>
                  <td style={td}>{c.ngayNghiViecCuoi}</td>
                  <td style={td}>{c.lyDo}</td>
                  <td style={td}><StatusChip status={c.trangThai} map={OFFB_STATUS} /></td>
                  <td style={td}><ProgressBar pct={c.tienDoPhanTram || 0} /></td>
                  <td style={td}>
                    <button onClick={() => navigate(`/offboarding/${c.caseId}`)} style={{ color: "#0d9488", background: "none", border: "none", cursor: "pointer", textDecoration: "underline", fontSize: 13 }}>
                      Chi tiet
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <div style={{ marginTop: 8, color: "#64748b", fontSize: 13 }}>
          Tong: {data?.totalElements || 0} ho so
        </div>
      </div>
    </div>
  );
}

// Offboarding Detail
function OffboardingDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [caseData, setCaseData] = useState<any>(null);
  const [tasks, setTasks] = useState<any[]>([]);
  const [severance, setSeverance] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) { setLoading(false); return; }
    Promise.all([
      offboardingApi.get(id),
      offboardingApi.listTasks(id),
      offboardingApi.getSeverance(id),
    ]).then(([c, t, s]) => {
      setCaseData(c);
      setTasks(t);
      setSeverance(s);
      setLoading(false);
    });
  }, [id]);

  if (loading) return <div style={{ padding: 24, color: "#64748b" }}>Đang tai...</div>;

  return (
    <div>
      <PageHeader title={`Ho so nghi viec ${caseData ? `· ${caseData.soQuyetDinh}` : ""}`} />
      <div style={{ padding: 24, maxWidth: 1100, margin: "0 auto" }}>
        <button onClick={() => navigate("/offboarding")} style={{ marginBottom: 16, color: "#0d9488", background: "none", border: "none", cursor: "pointer", fontSize: 14 }}>
          ← Quay lai danh sach
        </button>

        <section style={card}>
          <h3 style={cardTitle}>Thong tin chung</h3>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <Field label="Ma NV" value={caseData?.maNv} />
            <Field label="Ho ten" value={caseData?.hoTen} />
            <Field label="So quyet dinh" value={caseData?.soQuyetDinh} />
            <Field label="Ngay quyet dinh" value={caseData?.ngayQuyetDinh} />
            <Field label="Ngay nghi viec cuoi" value={caseData?.ngayNghiViecCuoi} />
            <Field label="Ngay chinh thuc nghi" value={caseData?.ngayChinhThucNghi} />
            <Field label="Ly do" value={caseData?.lyDo} />
            <Field label="Trang thai" value={caseData?.trangThai ? <StatusChip status={caseData.trangThai} map={OFFB_STATUS} /> : null} />
            <Field label="Nguoi tao" value={caseData?.nguoiTaoId} />
            <Field label="Nguoi duyet" value={caseData?.nguoiDuyetId || "—"} />
          </div>
        </section>

        <section style={card}>
          <h3 style={cardTitle}>
            Checklist ({tasks.filter((t) => t.trangThai === "HOAN_THANH" || t.trangThai === "KHONG_AP_DUNG").length}/{tasks.length})
          </h3>
          {tasks.length === 0 && <div style={{ color: "#94a3b8" }}>Chua co checklist.</div>}
          {tasks.map((t, idx) => (
            <div key={t.taskId} style={{ display: "flex", alignItems: "center", padding: "10px 8px", borderBottom: "1px solid #e2e8f0", gap: 12 }}>
              <input type="checkbox" checked={t.trangThai === "HOAN_THANH"} onChange={(e) => updateTask(t, e.target.checked ? "HOAN_THANH" : "CHUA_LAM", idx)} />
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 500 }}>{idx + 1}. {t.loaiTask}</div>
                {t.moTa && <div style={{ color: "#64748b", fontSize: 13 }}>{t.moTa}</div>}
              </div>
              <select value={t.trangThai} onChange={(e) => updateTask(t, e.target.value, idx)} style={{ padding: 4 }}>
                <option value="CHUA_LAM">Chua lam</option>
                <option value="DANG_LAM">Đang lam</option>
                <option value="HOAN_THANH">Hoan thanh</option>
                <option value="KHONG_AP_DUNG">Khong ap dung</option>
              </select>
            </div>
          ))}
        </section>

        <section style={card}>
          <h3 style={cardTitle}>Tro cap thoi viec (Severance)</h3>
          {severance ? (
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
              <Field label="Thoi gian lam viec" value={`${severance.thoiGianLamViecThang} thang`} />
              <Field label="So nam tham nien" value={severance.soNamThamNien} />
              <Field label="Luong binh quan 6 thang" value={fmtVND(severance.luongThoiViecBinhQuan)} />
              <Field label="He so" value={severance.heSo} />
              <Field label="So tien tro cap" value={fmtVND(severance.soTienTroCap)} highlight />
              <Field label="Nguoi tinh" value={severance.nguoiTinhId || "—"} />
            </div>
          ) : (
            <div style={{ color: "#64748b" }}>
              Chua co phep tinh. Su dung API <code>POST /api/v1/hr/offboarding/severance/preview</code> de tinh truoc.
            </div>
          )}
        </section>
      </div>
    </div>
  );

  function updateTask(t: any, newStatus: string, idx: number) {
    if (!id) return;
    const updated = [...tasks];
    updated[idx] = { ...t, trangThai: newStatus };
    setTasks(updated);
    offboardingApi.updateTask(t.taskId, newStatus).then(() => { offboardingApi.get(id).then(setCaseData); });
  }
}

// Offboarding new
function OffboardingNew() {
  const navigate = useNavigate();
  return (
    <div>
      <PageHeader title="Tao ho so nghi viec" />
      <div style={{ padding: 24, maxWidth: 600 }}>
        <button onClick={() => navigate("/offboarding")} style={{ marginBottom: 16, color: "#0d9488", background: "none", border: "none", cursor: "pointer" }}>
          ← Quay lai
        </button>
        <div style={{ background: "white", padding: 24, borderRadius: 8, boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
          <p style={{ color: "#64748b" }}>Form tao ho so nghi viec moi. Ket noi backend that de su dung.</p>
          <p style={{ color: "#64748b", fontSize: 13 }}>Endpoint: <code>POST /api/v1/hr/offboarding</code></p>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// BHXH REPORT PAGE
// ============================================================================
function BhxhReport() {
  const [reportType, setReportType] = useState<"D02-LT" | "D03-LT">("D02-LT");
  const [tuNgay, setTuNgay] = useState(() => { const d = new Date(); d.setDate(1); return d.toISOString().slice(0, 10); });
  const [denNgay, setDenNgay] = useState(new Date().toISOString().slice(0, 10));
  const [maDonVi, setMaDonVi] = useState("DV-001");
  const [tenDonVi, setTenDonVi] = useState("Cong ty TNHH ABC");
  const [report, setReport] = useState<any>(null);
  const [xmlContent, setXmlContent] = useState<string>("");
  const [loading, setLoading] = useState(false);

  const generate = async () => {
    setLoading(true);
    const r = reportType === "D02-LT"
      ? await bhxhReportMock.generateD02LT(tuNgay, denNgay, maDonVi, tenDonVi)
      : await bhxhReportMock.generateD03LT(tuNgay, denNgay, maDonVi, tenDonVi);
    setReport(r);
    const xml = reportType === "D02-LT"
      ? await bhxhReportMock.exportXmlD02LT(tuNgay, denNgay, maDonVi, tenDonVi)
      : await bhxhReportMock.exportXmlD03LT(tuNgay, denNgay, maDonVi, tenDonVi);
    setXmlContent(xml);
    setLoading(false);
  };

  const downloadXml = () => {
    const blob = new Blob([xmlContent], { type: "application/xml" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `${reportType.replace(/-/g, "")}_${maDonVi}_${tuNgay}.xml`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div>
      <PageHeader title="Bao cao BHXH" subtitle="T15 — D02-LT (tang/giam LD) · D03-LT (bien dong qua trinh)" />
      <div style={{ padding: 24 }}>
        <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16, boxShadow: "0 1px 3px rgba(0,0,0,0.06)" }}>
          <div style={{ display: "flex", gap: 16, flexWrap: "wrap", alignItems: "flex-end" }}>
            <div>
              <label style={lblStyle}>Loai bao cao</label>
              <select value={reportType} onChange={(e) => setReportType(e.target.value as any)} style={inpStyle}>
                <option value="D02-LT">D02-LT (Bao tang/giam LD)</option>
                <option value="D03-LT">D03-LT (Bao cap so BHXH)</option>
              </select>
            </div>
            <div><label style={lblStyle}>Tu ngay</label><input type="date" value={tuNgay} onChange={(e) => setTuNgay(e.target.value)} style={inpStyle} /></div>
            <div><label style={lblStyle}>Den ngay</label><input type="date" value={denNgay} onChange={(e) => setDenNgay(e.target.value)} style={inpStyle} /></div>
            <div><label style={lblStyle}>Ma don vi BHXH</label><input value={maDonVi} onChange={(e) => setMaDonVi(e.target.value)} style={inpStyle} /></div>
            <div><label style={lblStyle}>Ten don vi</label><input value={tenDonVi} onChange={(e) => setTenDonVi(e.target.value)} style={{ ...inpStyle, width: 200 }} /></div>
            <button onClick={generate} disabled={loading} style={{ ...btnStyle, background: "#0d9488" }}>
              {loading ? "Dang tao..." : "Tao bao cao"}
            </button>
          </div>
        </div>

        {report && (
          <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
              <h3 style={{ margin: 0 }}>{reportType} — Tong so dong: {report.tongSoDong || 0}</h3>
              <button onClick={downloadXml} disabled={!xmlContent} style={{ ...btnStyle, background: "#3b82f6" }}>Tai file XML</button>
            </div>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead><tr style={{ background: "#f8fafc" }}>{BHXH_COLS(reportType)}</tr></thead>
              <tbody>
                {!report.rows?.length && <tr><td colSpan={8} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Khong co du lieu trong ky</td></tr>}
                {report.rows?.map((r: any, idx: number) => (
                  <tr key={idx} style={{ borderTop: "1px solid #e2e8f0" }}>
                    <td style={td2}>{idx + 1}</td>
                    <td style={td2}>{r.maNv}</td>
                    <td style={td2}>{r.hoTen}</td>
                    <td style={td2}>{r.maSoBhxh}</td>
                    {reportType === "D02-LT" ? (
                      <><td style={td2}>{r.loaiBienDong}</td><td style={td2}>{r.ngayPhatSinh}</td><td style={td2}>{r.mucLuongDong ? new Intl.NumberFormat("vi-VN").format(r.mucLuongDong) : "-"}</td><td style={td2}><BhxhStatusChip status={r.trangThaiNop} /></td></>
                    ) : (
                      <><td style={td2}>{r.ngayCapSo}</td><td style={td2}>{r.loaiDeNghi}</td><td style={td2}>{r.lyDo}</td></>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {xmlContent && (
          <div style={{ background: "#1e293b", color: "#e2e8f0", padding: 16, borderRadius: 8 }}>
            <h3 style={{ marginTop: 0 }}>XML Preview ({Math.round(xmlContent.length / 1024)} KB)</h3>
            <pre style={{ maxHeight: 400, overflow: "auto", fontSize: 12, lineHeight: 1.4 }}>{xmlContent.slice(0, 4000)}{xmlContent.length > 4000 ? "\n... (truncated)" : ""}</pre>
          </div>
        )}
      </div>
    </div>
  );
}

// ============================================================================
// QUYET TOAN THUE PAGE
// ============================================================================
function QuyetToanThue() {
  const [tab, setTab] = useState<"02" | "05">("02");
  const [nam, setNam] = useState(new Date().getFullYear() - 1);
  const [maDonVi, setMaDonVi] = useState("DV-001");
  const [tenDonVi, setTenDonVi] = useState("Cong ty TNHH ABC");
  const [maSoThue, setMaSoThue] = useState("0123456789");
  const [report, setReport] = useState<any>(null);
  const [xmlContent, setXmlContent] = useState<string>("");
  const [loading, setLoading] = useState(false);

  const generate = async () => {
    setLoading(true);
    if (tab === "02") {
      const r = await quyetToanThueMock.generate02QTT(nam, maDonVi, tenDonVi, maSoThue);
      setReport(r);
      setXmlContent(await quyetToanThueMock.exportXml02QTT(nam, maDonVi, tenDonVi, maSoThue));
    } else {
      const r = await quyetToanThueMock.generate05QTT(nam, "NV-001");
      setReport(r);
      setXmlContent(await quyetToanThueMock.exportXml05QTT(nam, "NV-001"));
    }
    setLoading(false);
  };

  const downloadXml = () => {
    const blob = new Blob([xmlContent], { type: "application/xml" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `${tab}-QTT_${maDonVi}_${nam}.xml`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div>
      <PageHeader title="Quyet toan thue TNCN" subtitle="T16 — Theo Thong tu 92/2015/TT-BTC · Cam ket 08" />
      <div style={{ padding: 24 }}>
        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          <button onClick={() => setTab("02")} style={tabBtn(tab === "02")}>Mau 02/QTT (Tong hop DN)</button>
          <button onClick={() => setTab("05")} style={tabBtn(tab === "05")}>Mau 05/QTT (Chi tiet NV)</button>
        </div>

        <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <div style={{ display: "flex", gap: 16, flexWrap: "wrap", alignItems: "flex-end" }}>
            <div><label style={lblStyle}>Nam quyet toan</label><input type="number" value={nam} onChange={(e) => setNam(Number(e.target.value))} style={inpStyle} /></div>
            <div><label style={lblStyle}>Ma don vi</label><input value={maDonVi} onChange={(e) => setMaDonVi(e.target.value)} style={inpStyle} /></div>
            <div><label style={lblStyle}>Ten don vi</label><input value={tenDonVi} onChange={(e) => setTenDonVi(e.target.value)} style={{ ...inpStyle, width: 200 }} /></div>
            <div><label style={lblStyle}>MST don vi</label><input value={maSoThue} onChange={(e) => setMaSoThue(e.target.value)} style={inpStyle} /></div>
            <button onClick={generate} disabled={loading} style={{ ...btnStyle, background: "#0d9488" }}>{loading ? "Dang tao..." : "Tao bao cao"}</button>
          </div>
        </div>

        {report && tab === "02" && <Mau02View report={report} />}
        {report && tab === "05" && <Mau05View report={report} />}

        {xmlContent && (
          <div style={{ background: "#1e293b", color: "#e2e8f0", padding: 16, borderRadius: 8, marginTop: 16 }}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 8 }}>
              <h3 style={{ margin: 0 }}>XML Preview</h3>
              <button onClick={downloadXml} style={{ ...btnStyle, background: "#3b82f6" }}>Tai XML</button>
            </div>
            <pre style={{ maxHeight: 400, overflow: "auto", fontSize: 12, lineHeight: 1.4 }}>
              {xmlContent.slice(0, 4000)}{xmlContent.length > 4000 ? "\n... (truncated)" : ""}
            </pre>
          </div>
        )}
      </div>
    </div>
  );
}

// ============================================================================
// RECRUITMENT PAGE
// ============================================================================
function Recruitment() {
  const [tab, setTab] = useState<"yeu-cau" | "ung-vien" | "phong-van" | "quyet-dinh">("yeu-cau");
  const [yeuCauList, setYeuCauList] = useState<any[]>([]);
  const [ungVienList, setUngVienList] = useState<any[]>([]);
  const [lichPVList, setLichPVList] = useState<any[]>([]);
  const [quyetDinhList, setQuyetDinhList] = useState<any[]>([]);

  useEffect(() => {
    recruitmentMock.listYeuCau().then(setYeuCauList);
    recruitmentMock.listUngVien().then(setUngVienList);
  }, []);

  return (
    <div>
      <PageHeader title="Tuyen dung" subtitle="T17 — Yeu cau · Ung vien · Phong van · Quyet dinh" />
      <div style={{ padding: 24 }}>
        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          {(["yeu-cau","ung-vien","phong-van","quyet-dinh"] as const).map((t) => (
            <button key={t} onClick={() => setTab(t)} style={tabBtn(tab === t)}>
              {TAB_LABELS[t]} ({t === "yeu-cau" ? yeuCauList.length : t === "ung-vien" ? ungVienList.length : t === "phong-van" ? lichPVList.length : quyetDinhList.length})
            </button>
          ))}
        </div>

        <div style={{ background: "white", padding: 16, borderRadius: 8 }}>
          {tab === "yeu-cau" && <YeuCauTab list={yeuCauList} onSelect={(uv) => { setUngVienList(uv ? [uv] : []); }} />}
          {tab === "ung-vien" && <UngVienTab list={ungVienList} onSelect={(uv) => { setLichPVList([]); setQuyetDinhList([]); }} />}
          {tab === "phong-van" && <LichPVTab list={lichPVList} />}
          {tab === "quyet-dinh" && <QuyetDinhTab list={quyetDinhList} />}
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// KPI PAGE
// ============================================================================
function Kpi() {
  const [cycles, setCycles] = useState<any[]>([]);
  const [selectedCycle, setSelectedCycle] = useState<any>(null);
  const [assignments, setAssignments] = useState<any[]>([]);
  const [selectedAssign, setSelectedAssign] = useState<any>(null);
  const [view, setView] = useState<"assignments" | "self" | "review" | "approve">("assignments");
  const [diemTuDanhGia, setDiemTuDanhGia] = useState("85");
  const [tyLeHT, setTyLeHT] = useState("95");
  const [nhanXetNV, setNhanXetNV] = useState("");
  const [diemManager, setDiemManager] = useState("88");
  const [xepLoaiDeXuat, setXepLoaiDeXuat] = useState("A");
  const [nhanXetManager, setNhanXetManager] = useState("");
  const [diemCuoi, setDiemCuoi] = useState("90");
  const [xepLoaiCuoi, setXepLoaiCuoi] = useState("A");
  const [heSoThuong, setHeSoThuong] = useState("1.5");

  useEffect(() => { kpiMock.listCycles().then(setCycles); }, []);

  const onSelectCycle = async (cycle: any) => {
    setSelectedCycle(cycle);
    const list = await kpiMock.listAssignments(cycle.cycleId);
    setAssignments(list);
    setSelectedAssign(null);
    setView("assignments");
  };

  const submitSelf = async () => {
    if (!selectedAssign) return;
    const updated = await kpiMock.selfAssess(selectedAssign.assignmentId, { diemTuDanhGia: Number(diemTuDanhGia), tyLeHoanThanh: Number(tyLeHT), nhanXetNv: nhanXetNV });
    setAssignments((prev) => prev.map((a) => a.assignmentId === updated.assignmentId ? updated : a));
    setSelectedAssign(updated);
    alert("Tu danh gia thanh cong");
  };

  const submitReview = async () => {
    if (!selectedAssign) return;
    const updated = await kpiMock.managerReview(selectedAssign.assignmentId, { diemManager: Number(diemManager), xepLoaiDeXuat, nhanXetManager });
    setAssignments((prev) => prev.map((a) => a.assignmentId === updated.assignmentId ? updated : a));
    setSelectedAssign(updated);
    alert("Review thanh cong");
  };

  const submitApprove = async () => {
    if (!selectedAssign) return;
    const updated = await kpiMock.hrApprove(selectedAssign.assignmentId, { diemCuoi: Number(diemCuoi), xepLoaiCuoi, heSoThuong: Number(heSoThuong) });
    setAssignments((prev) => prev.map((a) => a.assignmentId === updated.assignmentId ? updated : a));
    setSelectedAssign(updated);
    alert("HR phe duyet thanh cong");
  };

  return (
    <div>
      <PageHeader title="Danh gia hieu suat KPI / OKR" subtitle="T18 — Chu ky · Muc tieu · Tu danh gia · Manager review · HR phe duyet" />
      <div style={{ padding: 24 }}>
        <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <h3 style={{ margin: "0 0 12px" }}>Chu ky danh gia</h3>
          <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
            {cycles.map((c) => (
              <div key={c.cycleId} onClick={() => onSelectCycle(c)} style={{
                border: "1px solid #cbd5e1", borderRadius: 8, padding: 12, cursor: "pointer",
                background: selectedCycle?.cycleId === c.cycleId ? "#ecfeff" : "#fff", minWidth: 220,
              }}>
                <div style={{ fontWeight: 600 }}>{c.tenChuKy}</div>
                <div style={{ fontSize: 12, color: "#64748b" }}>{c.ngayBatDau} → {c.ngayKetThuc}</div>
                <div style={{ fontSize: 12, marginTop: 4 }}><StatusChip status={c.trangThai} map={KPI_CYCLE_STATUS} /></div>
              </div>
            ))}
          </div>
        </div>

        {selectedCycle && (
          <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
            <h3>Muc tieu KPI — {selectedCycle.tenChuKy}</h3>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead><tr style={{ background: "#f8fafc" }}>
                <th style={th}>Muc tieu</th><th style={th}>Loai</th><th style={th}>Don vi</th>
                <th style={th}>Target</th><th style={th}>Trong so</th><th style={th}>NV</th>
                <th style={th}>Trang thai</th><th style={th}>Diem</th><th style={th}>Hang loai</th><th style={th}></th>
              </tr></thead>
              <tbody>
                {assignments.map((a) => (
                  <tr key={a.assignmentId} style={{ borderTop: "1px solid #e2e8f0" }}>
                    <td style={td2}>{a.tenMucTieu}</td>
                    <td style={td2}>{a.loaiMucTieu}</td>
                    <td style={td2}>{a.donViDo || "-"}</td>
                    <td style={td2}>{a.targetValue}</td>
                    <td style={td2}>{a.trongSo}</td>
                    <td style={td2}>{a.hoTen || a.nhanVienId}</td>
                    <td style={td2}><StatusChip status={a.trangThai} map={KPI_ASSIGN_STATUS} /></td>
                    <td style={td2}>{a.diemTrungBinh?.toFixed(2) || "-"}</td>
                    <td style={td2}>{a.xepLoaiCuoi || "-"}</td>
                    <td style={td2}>
                      <button onClick={() => { setSelectedAssign(a); setView("self"); }} style={{ ...btnStyleSmall, background: "#0d9488" }}>Chi tiet</button>
                    </td>
                  </tr>
                ))}
                {assignments.length === 0 && <tr><td colSpan={10} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co muc tieu nao</td></tr>}
              </tbody>
            </table>
          </div>
        )}

        {selectedAssign && (
          <div style={{ background: "white", padding: 16, borderRadius: 8 }}>
            <h3>Chi tiet: {selectedAssign.tenMucTieu}</h3>
            <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
              {(["self","review","approve"] as const).map((v) => (
                <button key={v} onClick={() => setView(v)} style={tabBtn(view === v)}>
                  {v === "self" ? "NV tu danh gia" : v === "review" ? "Manager review" : "HR phe duyet"}
                </button>
              ))}
            </div>

            {view === "self" && (
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                <Field label="Ty le hoan thanh (%)"><input type="number" value={tyLeHT} onChange={(e) => setTyLeHT(e.target.value)} style={inpStyle} /></Field>
                <Field label="Diem tu danh gia (0–100)"><input type="number" value={diemTuDanhGia} onChange={(e) => setDiemTuDanhGia(e.target.value)} style={inpStyle} /></Field>
                <Field label="Nhan xet NV" full><textarea value={nhanXetNV} onChange={(e) => setNhanXetNV(e.target.value)} style={{ ...inpStyle, height: 80 }} /></Field>
                <div style={{ gridColumn: "span 2" }}>
                  <button onClick={submitSelf} style={{ ...btnStyle, background: "#0d9488" }} disabled={selectedAssign.trangThai !== "MOI_GAN"}>Gui tu danh gia</button>
                </div>
              </div>
            )}

            {view === "review" && (
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                <Field label="Diem Manager (0–100)"><input type="number" value={diemManager} onChange={(e) => setDiemManager(e.target.value)} style={inpStyle} /></Field>
                <Field label="De xuat xep loai">
                  <select value={xepLoaiDeXuat} onChange={(e) => setXepLoaiDeXuat(e.target.value)} style={inpStyle}>
                    <option value="A">A — Xuat sac</option><option value="B">B — Tot</option><option value="C">C — Trung binh</option><option value="D">D — Yeu</option>
                  </select>
                </Field>
                <Field label="Nhan xet Manager" full><textarea value={nhanXetManager} onChange={(e) => setNhanXetManager(e.target.value)} style={{ ...inpStyle, height: 80 }} /></Field>
                <div style={{ gridColumn: "span 2" }}>
                  <button onClick={submitReview} style={{ ...btnStyle, background: "#0d9488" }} disabled={selectedAssign.trangThai !== "NV_DA_TU_DANH_GIA"}>Gui review</button>
                </div>
              </div>
            )}

            {view === "approve" && (
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                <Field label="Diem cuoi (0–100)"><input type="number" value={diemCuoi} onChange={(e) => setDiemCuoi(e.target.value)} style={inpStyle} /></Field>
                <Field label="Xep loai cuoi">
                  <select value={xepLoaiCuoi} onChange={(e) => setXepLoaiCuoi(e.target.value)} style={inpStyle}>
                    <option value="A">A — Xuat sac</option><option value="B">B — Tot</option><option value="C">C — Trung binh</option><option value="D">D — Yeu</option>
                  </select>
                </Field>
                <Field label="He so thuong"><input type="number" step="0.1" value={heSoThuong} onChange={(e) => setHeSoThuong(e.target.value)} style={inpStyle} /></Field>
                <div style={{ gridColumn: "span 2" }}>
                  <button onClick={submitApprove} style={{ ...btnStyle, background: "#0d9488" }} disabled={selectedAssign.trangThai !== "MANAGER_DA_REVIEW"}>Phe duyet HR</button>
                </div>
              </div>
            )}

            <div style={{ marginTop: 16, padding: 12, background: "#f8fafc", borderRadius: 6, fontSize: 13 }}>
              <div>Diem NV tu danh gia: <strong>{selectedAssign.diemTuDanhGia?.toFixed(2) || "-"}</strong></div>
              <div>Diem Manager: <strong>{selectedAssign.diemManager?.toFixed(2) || "-"}</strong></div>
              <div>Diem trung binh: <strong>{selectedAssign.diemTrungBinh?.toFixed(2) || "-"}</strong> (40% NV + 60% Manager)</div>
              <div>Xep loai cuoi: <strong>{selectedAssign.xepLoaiCuoi || "-"}</strong></div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// ============================================================================
// PAYROLL RUN PAGE
// ============================================================================
function PayrollRun() {
  const [runs, setRuns] = useState<any[]>([]);
  const [selected, setSelected] = useState<any>(null);
  const [creating, setCreating] = useState(false);
  const [newThang, setNewThang] = useState(new Date().getMonth() + 1);
  const [newNam, setNewNam] = useState(new Date().getFullYear());

  const refresh = () => payrollRunMock.list().then(setRuns);
  useEffect(() => { refresh(); }, []);

  const create = async () => {
    try { await payrollRunMock.create(newThang, newNam); await refresh(); setCreating(false); }
    catch (e: any) { alert(e.message || "Loi"); }
  };

  const transition = async (action: string) => {
    if (!selected) return;
    await payrollRunMock.transition(selected.kyLinhId, action);
    await refresh();
    const updated = await payrollRunMock.findById(selected.kyLinhId);
    setSelected(updated);
  };

  const downloadPayslip = (kyLinhId: string, nvId: string, maNv: string) => {
    const html = payrollRunMock.renderPayslipHtml(kyLinhId, nvId);
    const blob = new Blob([html], { type: "text/html" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url; a.download = `phieu-luong-${maNv}.html`; a.click();
    URL.revokeObjectURL(url);
  };

  const onSelect = async (r: any) => {
    const full = await payrollRunMock.findById(r.kyLinhId);
    setSelected(full);
  };

  const canStart = (s: string) => s === "CHO_CHAY";
  const canApprove1 = (s: string) => s === "DA_CHAY";
  const canApprove2 = (s: string) => s === "DA_DUYET_CAP_1";
  const canPay = (s: string) => s === "DA_DUYET_CAP_2";
  const canCancel = (s: string) => s !== "DA_CHI_TRA" && s !== "HUY";

  return (
    <div>
      <PageHeader title="Payroll Run" subtitle="T19 — Ky linh luong · Workflow: Chay ky → Duyet cap 1 → Duyet cap 2 → Chi tra" />
      <div style={{ padding: 24 }}>
        <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
            <h3 style={{ margin: 0 }}>Danh sach ky linh</h3>
            <button onClick={() => setCreating(true)} style={{ ...btnStyle, background: "#0d9488" }}>+ Tao ky moi</button>
          </div>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr style={{ background: "#f8fafc" }}>
              <th style={th}>Ma ky</th><th style={th}>Ky</th><th style={th}>Trang thai</th>
              <th style={th}>SL NV</th><th style={th}>Tong thuc linh</th><th style={th}>BHXH NLD</th><th style={th}>Thue TNCN</th><th style={th}></th>
            </tr></thead>
            <tbody>
              {runs.map((r) => (
                <tr key={r.kyLinhId} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td2}>{r.maKyLinh}</td>
                  <td style={td2}>{String(r.thang).padStart(2,"0")}/{r.nam}</td>
                  <td style={td2}><span style={{ background: PAYROLL_STATUS[r.trangThai] || "#64748b", color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{r.trangThai}</span></td>
                  <td style={td2}>{r.tongNhanVien}</td>
                  <td style={td2}>{fmtVND(r.tongThucLinh)}</td>
                  <td style={td2}>{fmtVND(r.tongBhxhNld)}</td>
                  <td style={td2}>{fmtVND(r.tongThueTncn)}</td>
                  <td style={td2}><button onClick={() => onSelect(r)} style={{ ...btnStyleSmall, background: "#0d9488" }}>Chi tiet</button></td>
                </tr>
              ))}
              {runs.length === 0 && <tr><td colSpan={8} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co ky linh nao</td></tr>}
            </tbody>
          </table>
        </div>

        {creating && (
          <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
            <h3>Tao ky linh moi</h3>
            <div style={{ display: "flex", gap: 12, alignItems: "flex-end" }}>
              <div><label style={lblStyle}>Thang</label>
                <select value={newThang} onChange={(e) => setNewThang(Number(e.target.value))} style={inpStyle}>
                  {Array.from({length: 12},(_,i)=>i+1).map((m)=><option key={m} value={m}>Thang {m}</option>)}
                </select>
              </div>
              <div><label style={lblStyle}>Nam</label><input type="number" value={newNam} onChange={(e) => setNewNam(Number(e.target.value))} style={inpStyle} /></div>
              <button onClick={create} style={{ ...btnStyle, background: "#0d9488" }}>Tao ky</button>
              <button onClick={() => setCreating(false)} style={{ ...btnStyle, background: "#94a3b8" }}>Huy</button>
            </div>
          </div>
        )}

        {selected && (
          <div style={{ background: "white", padding: 16, borderRadius: 8 }}>
            <h3>Chi tiet ky linh {selected.maKyLinh} — {selected.thang}/{selected.nam}</h3>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginBottom: 16 }}>
              <Stat label="Trang thai" value={selected.trangThai} color={PAYROLL_STATUS[selected.trangThai]} />
              <Stat label="Nguoi chay" value={selected.nguoiChayId?.slice(0,8)||"-"} />
              <Stat label="Ngay chay" value={selected.ngayChay||"-"} />
              <Stat label="Duyet cap 1" value={selected.nguoiDuyetCap1Id?.slice(0,8)||"-"} />
              <Stat label="Ngay duyet 1" value={selected.ngayDuyetCap1||"-"} />
              <Stat label="Duyet cap 2" value={selected.nguoiDuyetCap2Id?.slice(0,8)||"-"} />
            </div>
            <h4>Workflow</h4>
            <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 16 }}>
              <button onClick={() => transition("start")} disabled={!canStart(selected.trangThai)} style={{ ...btnStyle, background: "#0d9488" }}>▶ Chay ky luong</button>
              <button onClick={() => transition("approve-cap-1")} disabled={!canApprove1(selected.trangThai)} style={{ ...btnStyle, background: "#f59e0b" }}>✓ Duyet cap 1</button>
              <button onClick={() => transition("approve-cap-2")} disabled={!canApprove2(selected.trangThai)} style={{ ...btnStyle, background: "#3b82f6" }}>✓ Duyet cap 2</button>
              <button onClick={() => transition("pay-paid")} disabled={!canPay(selected.trangThai)} style={{ ...btnStyle, background: "#059669" }}>💰 Chi tra</button>
              <button onClick={() => transition("cancel")} disabled={!canCancel(selected.trangThai)} style={{ ...btnStyle, background: "#ef4444" }}>✕ Huy ky</button>
            </div>
            <button onClick={() => downloadPayslip(selected.kyLinhId, "nv-1", "NV-001")} style={{ ...btnStyle, background: "#6366f1", marginTop: 8 }}>📄 Tai phieu luong NV-001</button>
            <h4 style={{ marginTop: 24 }}>Audit log</h4>
            <div style={{ background: "#f8fafc", padding: 12, borderRadius: 6, fontSize: 13 }}>
              <div>→ CHAY_KY_LUONG: {selected.ngayChay || "(chua chay)"}</div>
              <div>→ DUYET_CAP_1: {selected.ngayDuyetCap1 || "(chua duyet)"}</div>
              <div>→ DUYET_CAP_2: {selected.ngayDuyetCap2 || "(chua duyet)"}</div>
              <div>→ CHI_TRA: {selected.ngayChiTraThucTe || "(chua chi tra)"}</div>
              {selected.fileZipUrl && <div>📦 File zip: {selected.fileZipUrl}</div>}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// ============================================================================
// TRAINING PAGE
// ============================================================================
function Training() {
  const [tab, setTab] = useState<"ct" | "lop" | "dk" | "dg">("ct");
  const [chuongTrinh, setChuongTrinh] = useState<any[]>([]);
  const [lopHoc, setLopHoc] = useState<any[]>([]);
  const [dangKy, setDangKy] = useState<any[]>([]);
  const [showCtForm, setShowCtForm] = useState(false);
  const [showLopForm, setShowLopForm] = useState(false);
  const [showDkForm, setShowDkForm] = useState(false);
  const [showDgForm, setShowDgForm] = useState<string | null>(null);

  const refresh = async () => {
    const [ct, lop, dk] = await Promise.all([
      trainingMock.listChuongTrinh() as Promise<any[]>,
      trainingMock.listLop() as Promise<any[]>,
      trainingMock.listDangKy() as Promise<any[]>,
    ]);
    setChuongTrinh(ct);
    setLopHoc(lop);
    setDangKy(dk);
  };
  useEffect(() => { refresh(); }, []);

  const ctCreate = async (form: any) => { await trainingMock.createChuongTrinh(form); setShowCtForm(false); await refresh(); };
  const lopCreate = async (form: any) => { await trainingMock.createLop(form); setShowLopForm(false); await refresh(); };
  const ctPublish = async (id: string) => { await trainingMock.congBoChuongTrinh(id); await refresh(); };
  const lopTransition = async (id: string, s: string) => { await trainingMock.lopTransition(id, s); await refresh(); };
  const dkCreate = async (form: any) => { try { await trainingMock.dangKy(form); setShowDkForm(false); await refresh(); } catch (e: any) { alert(e.message); } };
  const dkDuyet = async (id: string, q: "DA_CHAP_NHAN" | "TU_CHOI") => { await trainingMock.duyetDangKy(id, q); await refresh(); };
  const dgSubmit = async (dkId: string, diem: any) => { try { await trainingMock.danhGia(dkId, diem); setShowDgForm(null); await refresh(); } catch (e: any) { alert(e.message); } };

  return (
    <div>
      <PageHeader title="Dao tao" subtitle="T20 — Chuong trinh · Lop hoc · Dang ky · Diem danh · Danh gia" />
      <div style={{ padding: 24 }}>
        <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          {(["ct","lop","dk","dg"] as const).map((t) => (
            <button key={t} onClick={() => setTab(t)} style={tabBtn(tab === t)}>
              {TRAIN_TABS[t]} ({t === "ct" ? chuongTrinh.length : t === "lop" ? lopHoc.length : t === "dk" ? dangKy.length : ""})
            </button>
          ))}
        </div>

        {tab === "ct" && (
          <div style={{ background: "white", padding: 16, borderRadius: 8 }}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
              <h3 style={{ margin: 0 }}>Danh sach chuong trinh</h3>
              <button onClick={() => setShowCtForm(true)} style={{ ...btnStyle, background: "#0d9488" }}>+ Tao chuong trinh</button>
            </div>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead><tr style={{ background: "#f8fafc" }}>
                <th style={th}>Ma</th><th style={th}>Ten chuong trinh</th><th style={th}>Loai</th><th style={th}>Gio</th><th style={th}>Nguong dau</th><th style={th}>Trang thai</th><th style={th}></th>
              </tr></thead>
              <tbody>
                {chuongTrinh.map((c) => (
                  <tr key={c.id} style={{ borderTop: "1px solid #e2e8f0" }}>
                    <td style={td2}>{c.maChuongTrinh}</td><td style={td2}>{c.tenChuongTrinh}</td><td style={td2}>{c.loaiChuongTrinh}</td>
                    <td style={td2}>{c.thoiLuongGio}h</td><td style={td2}>{c.diemDanhGiaToiThieu}</td>
                    <td style={td2}><span style={{ background: TRAIN_STATUS[c.trangThai] || "#64748b", color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{c.trangThai}</span></td>
                    <td style={td2}>{c.trangThai === "NHAP" && <button onClick={() => ctPublish(c.id)} style={{ ...btnStyleSmall, background: "#0d9488" }}>📣 Cong bo</button>}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {showCtForm && <TrainingForm onSubmit={ctCreate} onCancel={() => setShowCtForm(false)} mode="ct" />}
          </div>
        )}

        {tab === "lop" && (
          <div style={{ background: "white", padding: 16, borderRadius: 8 }}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
              <h3 style={{ margin: 0 }}>Danh sach lop hoc</h3>
              <button onClick={() => setShowLopForm(true)} disabled={!chuongTrinh.find((c) => c.trangThai === "CONG_BO")} style={{ ...btnStyle, background: "#0d9488" }}>+ Tao lop</button>
            </div>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead><tr style={{ background: "#f8fafc" }}>
                <th style={th}>Ma lop</th><th style={th}>Ten lop</th><th style={th}>Chuong trinh</th><th style={th}>Thoi gian</th><th style={th}>Si so</th><th style={th}>Trang thai</th><th style={th}></th>
              </tr></thead>
              <tbody>
                {lopHoc.map((l) => {
                  const ct = chuongTrinh.find((c) => c.id === l.chuongTrinhId);
                  return (
                    <tr key={l.id} style={{ borderTop: "1px solid #e2e8f0" }}>
                      <td style={td2}>{l.maLop}</td><td style={td2}>{l.tenLop}</td><td style={td2}>{ct?.tenChuongTrinh || "?"}</td>
                      <td style={td2}>{l.ngayBatDau} → {l.ngayKetThuc}</td><td style={td2}>{l.soChoToiDa}</td>
                      <td style={td2}><span style={{ background: TRAIN_STATUS[l.trangThai] || "#64748b", color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{l.trangThai}</span></td>
                      <td style={td2}>{RenderLopActions(l, lopTransition)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            {showLopForm && <TrainingForm onSubmit={lopCreate} onCancel={() => setShowLopForm(false)} mode="lop" chuongTrinh={chuongTrinh.filter((c) => c.trangThai === "CONG_BO")} />}
          </div>
        )}

        {tab === "dk" && (
          <div style={{ background: "white", padding: 16, borderRadius: 8 }}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
              <h3 style={{ margin: 0 }}>Danh sach dang ky</h3>
              <button onClick={() => setShowDkForm(true)} disabled={!lopHoc.find((l) => l.trangThai === "MO_DANG_KY")} style={{ ...btnStyle, background: "#0d9488" }}>+ Dang ky moi</button>
            </div>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead><tr style={{ background: "#f8fafc" }}>
                <th style={th}>NV</th><th style={th}>Lop</th><th style={th}>Ngay DK</th><th style={th}>Trang thai</th><th style={th}>Diem</th><th style={th}>CC</th><th style={th}></th>
              </tr></thead>
              <tbody>
                {dangKy.map((d) => {
                  const lop = lopHoc.find((l) => l.id === d.lopHocId);
                  return (
                    <tr key={d.id} style={{ borderTop: "1px solid #e2e8f0" }}>
                      <td style={td2}>{d.nhanVienId.slice(0, 8)}</td><td style={td2}>{lop?.tenLop || "?"}</td>
                      <td style={td2}>{d.ngayDangKy?.slice(0, 10)}</td>
                      <td style={td2}><span style={{ background: TRAIN_STATUS[d.trangThai] || "#64748b", color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{d.trangThai}</span></td>
                      <td style={td2}>{d.diemTongKet ?? "-"}</td><td style={td2}>{d.chungChiCap ?? "-"}</td>
                      <td style={td2}>
                        {d.trangThai === "CHO_DUYET" && <>
                          <button onClick={() => dkDuyet(d.id, "DA_CHAP_NHAN")} style={{ ...btnStyleSmall, background: "#10b981" }}>✓ Duyet</button>
                          <button onClick={() => dkDuyet(d.id, "TU_CHOI")} style={{ ...btnStyleSmall, background: "#ef4444", marginLeft: 4 }}>✕ Tu choi</button>
                        </>}
                        {d.trangThai === "DA_CHAP_NHAN" && <button onClick={() => setShowDgForm(d.id)} style={{ ...btnStyleSmall, background: "#6366f1" }}>📝 Danh gia</button>}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            {showDkForm && <TrainingForm onSubmit={dkCreate} onCancel={() => setShowDkForm(false)} mode="dk" lopHoc={lopHoc.filter((l) => l.trangThai === "MO_DANG_KY")} />}
            {showDgForm && <TrainingForm onSubmit={(d) => dgSubmit(showDgForm, d)} onCancel={() => setShowDgForm(null)} mode="dg" />}
          </div>
        )}

        {tab === "dg" && (
          <div style={{ background: "white", padding: 16, borderRadius: 8 }}>
            <h3>Danh gia sau dao tao</h3>
            <p style={{ color: "#64748b" }}>Vao tab "Dang ky" va nhan nut "📝 Danh gia" tren mot dang ky da duoc chap nhan.</p>
            <div style={{ background: "#f8fafc", padding: 12, borderRadius: 6, marginTop: 12 }}>
              <strong>Tong ket:</strong>
              <ul style={{ margin: "4px 0 0 16px" }}>
                <li>So chuong trinh: {chuongTrinh.length}</li>
                <li>So lop hoc: {lopHoc.length}</li>
                <li>So dang ky: {dangKy.length}</li>
                <li>Da chap nhan: {dangKy.filter((d) => d.trangThai === "DA_CHAP_NHAN").length}</li>
                <li>Da danh gia: {dangKy.filter((d) => d.diemTongKet).length}</li>
                <li>Cap chung chi: {dangKy.filter((d) => d.chungChiCap).length}</li>
              </ul>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// ============================================================================
// SHARED SUB-COMPONENTS
// ============================================================================

// Training inline forms
function TrainingForm({ onSubmit, onCancel, mode, chuongTrinh, lopHoc }: any) {
  const [form, setForm] = useState<any>(mode === "ct" ? { maChuongTrinh: "", tenChuongTrinh: "", loaiChuongTrinh: "KY_NANG_CHUYEN_MON", thoiLuongGio: 8, diemDanhGiaToiThieu: 60, chungChi: "" }
    : mode === "lop" ? { maLop: "", chuongTrinhId: chuongTrinh?.[0]?.id, tenLop: "", ngayBatDau: new Date().toISOString().slice(0,10), ngayKetThuc: new Date(Date.now()+7*86400000).toISOString().slice(0,10), soBuoi: 5, soChoToiDa: 30, diaDiem: "", giangVien: "" }
    : mode === "dk" ? { lopHocId: lopHoc?.[0]?.id, nhanVienId: "nv-1", lyDoDangKy: "" }
    : { diemNoiDung: 80, diemGiangVien: 80, diemThucHanh: 80, yKienNguoiHoc: "" });

  return (
    <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 100 }}>
      <div style={{ background: "#fff", padding: 24, borderRadius: 8, width: 480, maxHeight: "90vh", overflow: "auto" }}>
        <h3 style={{ marginTop: 0 }}>
          {mode === "ct" ? "Tao chuong trinh dao tao" : mode === "lop" ? "Tao lop hoc" : mode === "dk" ? "Dang ky lop hoc" : "Danh gia dao tao"}
        </h3>
        {mode === "ct" && <>
          <Field label="Ma chuong trinh"><input value={form.maChuongTrinh} onChange={(e) => setForm({...form, maChuongTrinh: e.target.value})} style={inpStyle} /></Field>
          <Field label="Ten chuong trinh"><input value={form.tenChuongTrinh} onChange={(e) => setForm({...form, tenChuongTrinh: e.target.value})} style={inpStyle} /></Field>
          <Field label="Thoi luong (gio)"><input type="number" value={form.thoiLuongGio} onChange={(e) => setForm({...form, thoiLuongGio: Number(e.target.value)})} style={inpStyle} /></Field>
          <Field label="Diem dau toi thieu"><input type="number" value={form.diemDanhGiaToiThieu} onChange={(e) => setForm({...form, diemDanhGiaToiThieu: Number(e.target.value)})} style={inpStyle} /></Field>
        </>}
        {mode === "lop" && chuongTrinh && <>
          <Field label="Chuong trinh"><select value={form.chuongTrinhId} onChange={(e) => setForm({...form, chuongTrinhId: e.target.value})} style={inpStyle}>{chuongTrinh.map((c: any) => <option key={c.id} value={c.id}>{c.tenChuongTrinh}</option>)}</select></Field>
          <Field label="Ten lop"><input value={form.tenLop} onChange={(e) => setForm({...form, tenLop: e.target.value})} style={inpStyle} /></Field>
          <Field label="Ngay bat dau"><input type="date" value={form.ngayBatDau} onChange={(e) => setForm({...form, ngayBatDau: e.target.value})} style={inpStyle} /></Field>
          <Field label="Ngay ket thuc"><input type="date" value={form.ngayKetThuc} onChange={(e) => setForm({...form, ngayKetThuc: e.target.value})} style={inpStyle} /></Field>
          <Field label="Si so toi da"><input type="number" value={form.soChoToiDa} onChange={(e) => setForm({...form, soChoToiDa: Number(e.target.value)})} style={inpStyle} /></Field>
          <Field label="Dia diem"><input value={form.diaDiem} onChange={(e) => setForm({...form, diaDiem: e.target.value})} style={inpStyle} /></Field>
          <Field label="Giang vien"><input value={form.giangVien} onChange={(e) => setForm({...form, giangVien: e.target.value})} style={inpStyle} /></Field>
        </>}
        {mode === "dk" && lopHoc && <>
          <Field label="Lop hoc"><select value={form.lopHocId} onChange={(e) => setForm({...form, lopHocId: e.target.value})} style={inpStyle}>{lopHoc.map((l: any) => <option key={l.id} value={l.id}>{l.tenLop} ({l.maLop})</option>)}</select></Field>
          <Field label="Ma NV"><input value={form.nhanVienId} onChange={(e) => setForm({...form, nhanVienId: e.target.value})} style={inpStyle} /></Field>
          <Field label="Ly do dang ky"><textarea value={form.lyDoDangKy} onChange={(e) => setForm({...form, lyDoDangKy: e.target.value})} style={{...inpStyle, height: 60}} /></Field>
        </>}
        {mode === "dg" && <>
          <p style={{ color: "#64748b", fontSize: 13 }}>Diem 0–100, trong so: 40% noi dung + 30% giang vien + 30% thuc hanh.</p>
          <Field label="Diem noi dung"><input type="number" min={0} max={100} value={form.diemNoiDung} onChange={(e) => setForm({...form, diemNoiDung: Number(e.target.value)})} style={inpStyle} /></Field>
          <Field label="Diem giang vien"><input type="number" min={0} max={100} value={form.diemGiangVien} onChange={(e) => setForm({...form, diemGiangVien: Number(e.target.value)})} style={inpStyle} /></Field>
          <Field label="Diem thuc hanh"><input type="number" min={0} max={100} value={form.diemThucHanh} onChange={(e) => setForm({...form, diemThucHanh: Number(e.target.value)})} style={inpStyle} /></Field>
          <Field label="Y kien nguoi hoc"><textarea value={form.yKienNguoiHoc} onChange={(e) => setForm({...form, yKienNguoiHoc: e.target.value})} style={{...inpStyle, height: 60}} /></Field>
        </>}
        <div style={{ marginTop: 12, display: "flex", gap: 8 }}>
          <button onClick={() => onSubmit(form)} style={{ ...btnStyle, background: "#0d9488" }}>Luu</button>
          <button onClick={onCancel} style={{ ...btnStyle, background: "#94a3b8" }}>Huy</button>
        </div>
      </div>
    </div>
  );
}

function RenderLopActions(l: any, lopTransition: any) {
  const btns: any[] = [];
  if (l.trangThai === "MO_DANG_KY") btns.push(<button key="dong" onClick={() => lopTransition(l.id, "DONG_DANG_KY")} style={{ ...btnStyleSmall, background: "#f59e0b" }}>🔒 Dong DK</button>);
  if (l.trangThai === "DONG_DANG_KY") btns.push(<button key="batdau" onClick={() => lopTransition(l.id, "DANG_DIEN_RA")} style={{ ...btnStyleSmall, background: "#3b82f6" }}>▶ Bat dau</button>);
  if (l.trangThai === "DANG_DIEN_RA") btns.push(<button key="ht" onClick={() => lopTransition(l.id, "HOAN_THANH")} style={{ ...btnStyleSmall, background: "#059669" }}>✓ Hoan thanh</button>);
  if (["MO_DANG_KY","DONG_DANG_KY","DANG_DIEN_RA"].includes(l.trangThai)) btns.push(<button key="huy" onClick={() => lopTransition(l.id, "HUY")} style={{ ...btnStyleSmall, background: "#ef4444", marginLeft: 4 }}>✕</button>);
  return <>{btns}</>;
}

// Mau 02 / 05 views
function Mau02View({ report }: { report: any }) {
  return (
    <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
      <h3>Mau 02/QTT-TNCN — Nam {report.nam}</h3>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12 }}>
        <Stat label="Tong so NV" value={report.tongSoNhanVien} />
        <Stat label="NV uy quyen" value={report.tongNhanVienUyQuyen} highlight />
        <Stat label="NV tu QTT" value={report.tongNhanVienTuQtt} />
        <Stat label="Tong TN chiu thue" value={fmtVND(report.tongThuNhapChiuThue)} />
        <Stat label="Tong giam tru ban than" value={fmtVND(report.tongGiamTruBanThan)} />
        <Stat label="Tong giam tru NPT" value={fmtVND(report.tongGiamTruNguoiPhuThuoc)} />
        <Stat label="Tong thue da khau tru" value={fmtVND(report.tongThueDaKhauTru)} highlight />
        <Stat label="Tong thue phai them" value={fmtVND(report.tongThuePhaiNopThem)} />
        <Stat label="Tong thue duoc hoan" value={fmtVND(report.tongThueDuocHoan)} />
      </div>
      <h4 style={{ marginTop: 24 }}>Top NV co thue cao</h4>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead><tr style={{ background: "#f8fafc" }}>
          <th style={th}>STT</th><th style={th}>Ma NV</th><th style={th}>Ho ten</th><th style={th}>MST</th><th style={th}>TN chiu thue</th><th style={th}>Thue da khau tru</th><th style={th}>Thue phai nop</th>
        </tr></thead>
        <tbody>
          {(report.top10NhanVienThueCao || []).map((r: any, i: number) => (
            <tr key={i} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td2}>{i+1}</td><td style={td2}>{r.maNv}</td><td style={td2}>{r.hoTen}</td><td style={td2}>{r.maSoThue || "-"}</td>
              <td style={td2}>{fmtVND(r.thuNhapChiuThue)}</td><td style={td2}>{fmtVND(r.thueDaKhauTru)}</td><td style={td2}>{fmtVND(r.thuePhaiNop)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function Mau05View({ report }: { report: any }) {
  return (
    <div style={{ background: "white", padding: 16, borderRadius: 8, marginBottom: 16 }}>
      <h3>Mau 05/QTT-TNCN — {report.hoTen} ({report.maNv}) — Nam {report.nam}</h3>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginBottom: 16 }}>
        <Stat label="MST" value={report.maSoThue || "-"} />
        <Stat label="So NPT" value={report.soNguoiPhuThuoc} />
        <Stat label="Tong TN ca nam" value={fmtVND(report.tongThuNhapCaNam)} />
        <Stat label="Tong TN chiu thue" value={fmtVND(report.tongThuNhapChiuThue)} />
        <Stat label="Giam tru ban than" value={fmtVND(report.giamTruBanThan)} />
        <Stat label="Giam tru NPT" value={fmtVND(report.giamTruNguoiPhuThuoc)} />
        <Stat label="Thue da khau tru" value={fmtVND(report.tongThueDaKhauTru)} highlight />
      </div>
      <h4>Chi tiet theo thang</h4>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead><tr style={{ background: "#f8fafc" }}>
          <th style={th}>Thang</th><th style={th}>TN chiu thue</th><th style={th}>Giam tru ban than</th><th style={th}>Giam tru NPT</th><th style={th}>Thue da khau tru</th>
        </tr></thead>
        <tbody>
          {(report.chiTietThang || []).map((m: any) => (
            <tr key={m.thang} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td2}>Thang {m.thang}</td><td style={td2}>{fmtVND(m.thuNhapChiuThue)}</td>
              <td style={td2}>{fmtVND(m.giamTruBanThan)}</td><td style={td2}>{fmtVND(m.giamTruNguoiPhuThuoc)}</td><td style={td2}>{fmtVND(m.thueDaKhauTru)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// Recruit sub-tabs
function YeuCauTab({ list, onSelect }: { list: any[]; onSelect: (uv: any) => void }) {
  return (
    <>
      <h3 style={{ margin: "0 0 12px" }}>Yeu cau tuyen dung</h3>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead><tr style={{ background: "#f8fafc" }}>
          <th style={th}>Ma</th><th style={th}>Tieu de</th><th style={th}>SL can</th><th style={th}>Trang thai</th><th style={th}>SL UV</th><th style={th}></th>
        </tr></thead>
        <tbody>
          {list.map((yc) => (
            <tr key={yc.yeuCauId} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td2}>{yc.maYeuCau}</td><td style={td2}>{yc.tieuDe}</td><td style={td2}>{yc.soLuongCan}</td>
              <td style={td2}><StatusChip status={yc.trangThai} map={RECRUIT_STATUS} /></td>
              <td style={td2}>{yc.soUngVien}</td>
              <td style={td2}><button onClick={() => onSelect(yc)} style={{ ...btnStyleSmall, background: "#0d9488" }}>Xem UV</button></td>
            </tr>
          ))}
          {list.length === 0 && <tr><td colSpan={6} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co yeu cau nao</td></tr>}
        </tbody>
      </table>
    </>
  );
}

function UngVienTab({ list, onSelect }: { list: any[]; onSelect: (uv: any) => void }) {
  return (
    <>
      <h3 style={{ margin: "0 0 12px" }}>Ung vien</h3>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead><tr style={{ background: "#f8fafc" }}>
          <th style={th}>Ma UV</th><th style={th}>Ho ten</th><th style={th}>Email</th><th style={th}>ĐT</th><th style={th}>Kinh nghiem</th><th style={th}>Trang thai</th><th style={th}></th>
        </tr></thead>
        <tbody>
          {list.map((uv) => (
            <tr key={uv.ungVienId} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td2}>{uv.maUngVien}</td><td style={td2}>{uv.hoTen}</td><td style={td2}>{uv.email || "-"}</td>
              <td style={td2}>{uv.soDienThoai || "-"}</td><td style={td2}>{uv.soNamKinhNghiem ?? 0} nam</td>
              <td style={td2}><StatusChip status={uv.trangThai} map={RECRUIT_STATUS} /></td>
              <td style={td2}><button onClick={() => onSelect(uv)} style={{ ...btnStyleSmall, background: "#0d9488" }}>Xem PV/QĐ</button></td>
            </tr>
          ))}
          {list.length === 0 && <tr><td colSpan={7} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co ung vien</td></tr>}
        </tbody>
      </table>
    </>
  );
}

function LichPVTab({ list }: { list: any[] }) {
  return (
    <>
      <h3 style={{ margin: "0 0 12px" }}>Lich phong van</h3>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead><tr style={{ background: "#f8fafc" }}>
          <th style={th}>Vong</th><th style={th}>Bat dau</th><th style={th}>Ket thuc</th><th style={th}>Dia diem</th><th style={th}>Hinh thuc</th><th style={th}>Trang thai</th>
        </tr></thead>
        <tbody>
          {list.map((l) => (
            <tr key={l.lichPvId} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td2}>Vong {l.vongPhongVan}</td><td style={td2}>{l.thoiGianBatDau}</td><td style={td2}>{l.thoiGianKetThuc}</td>
              <td style={td2}>{l.diaDiem || "-"}</td><td style={td2}>{l.hinhThuc}</td>
              <td style={td2}><StatusChip status={l.trangThai} map={RECRUIT_STATUS} /></td>
            </tr>
          ))}
          {list.length === 0 && <tr><td colSpan={6} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co lich phong van</td></tr>}
        </tbody>
      </table>
    </>
  );
}

function QuyetDinhTab({ list }: { list: any[] }) {
  return (
    <>
      <h3 style={{ margin: "0 0 12px" }}>Quyet dinh tuyen</h3>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead><tr style={{ background: "#f8fafc" }}>
          <th style={th}>Loai HD</th><th style={th}>Muc luong</th><th style={th}>Ngay vao lam</th><th style={th}>Phong ban</th><th style={th}>Trang thai</th>
        </tr></thead>
        <tbody>
          {list.map((q) => (
            <tr key={q.quyetDinhId} style={{ borderTop: "1px solid #e2e8f0" }}>
              <td style={td2}>{q.loaiHopDong}</td><td style={td2}>{q.mucLuongDeNghi?.toLocaleString("vi-VN")} VND</td>
              <td style={td2}>{q.ngayVaoLamDeNghi}</td><td style={td2}>{q.tenPhongBan || q.phongBanId}</td>
              <td style={td2}><StatusChip status={q.trangThai} map={RECRUIT_STATUS} /></td>
            </tr>
          ))}
          {list.length === 0 && <tr><td colSpan={5} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chua co quyet dinh</td></tr>}
        </tbody>
      </table>
    </>
  );
}

// Helper components
function Field({ label, value, highlight, full, children }: { label: string; value?: any; highlight?: boolean; full?: boolean; children?: ReactNode }) {
  return (
    <div style={{ gridColumn: full ? "span 2" : undefined }}>
      <div style={{ color: "#64748b", fontSize: 12, marginBottom: 4 }}>{label}</div>
      {children || <div style={{ fontWeight: 500, color: highlight ? "#0d9488" : undefined }}>{value ?? "-"}</div>}
    </div>
  );
}

function Stat({ label, value, color, highlight }: { label: string; value: any; color?: string; highlight?: boolean }) {
  return (
    <div style={{ background: "#f8fafc", padding: 12, borderRadius: 6, border: highlight ? "2px solid #3b82f6" : undefined }}>
      <div style={{ fontSize: 12, color: "#64748b" }}>{label}</div>
      <div style={{ fontWeight: 600, fontSize: 16, color: color || (highlight ? "#3b82f6" : undefined) }}>{value ?? "-"}</div>
    </div>
  );
}

function StatusChip({ status, map }: { status: string; map: Record<string, string> }) {
  return (
    <span style={{ background: map[status] || "#64748b", color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>
      {status}
    </span>
  );
}

function BhxhStatusChip({ status }: { status: string }) {
  const color = status === "DA_NOP" ? "#10b981" : "#f59e0b";
  return <span style={{ background: color, color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{status}</span>;
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

function fmtVND(v: any) {
  if (v === null || v === undefined || v === 0) return "-";
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(v));
}

// Status maps
const OFFB_STATUS: Record<string, string> = { MOI_TAO: "#94a3b8", CHO_DUYET: "#f59e0b", DANG_THUC_HIEN: "#3b82f6", CHO_QUYET_TOAN: "#8b5cf6", HOAN_THANH: "#10b981", HUY: "#ef4444" };
const KPI_CYCLE_STATUS: Record<string, string> = { MOI_TAO: "#94a3b8", DANG_DANH_GIA: "#3b82f6", DA_DONG: "#10b981", HUY: "#ef4444" };
const KPI_ASSIGN_STATUS: Record<string, string> = { MOI_GAN: "#94a3b8", NV_DA_TU_DANH_GIA: "#f59e0b", MANAGER_DA_REVIEW: "#3b82f6", HR_DA_PHE_DUYET: "#10b981", TU_CHOI: "#ef4444" };
const PAYROLL_STATUS: Record<string, string> = { CHO_CHAY: "#94a3b8", DANG_CHAY: "#3b82f6", DA_CHAY: "#0d9488", DA_DUYET_CAP_1: "#f59e0b", DA_DUYET_CAP_2: "#10b981", DA_CHI_TRA: "#059669", HUY: "#ef4444" };
const TRAIN_STATUS: Record<string, string> = { NHAP: "#94a3b8", CONG_BO: "#0d9488", NGUNG: "#ef4444", MO_DANG_KY: "#10b981", DONG_DANG_KY: "#f59e0b", DANG_DIEN_RA: "#3b82f6", HOAN_THANH: "#059669", CHO_DUYET: "#94a3b8", DA_CHAP_NHAN: "#10b981", TU_CHOI: "#ef4449" };
const RECRUIT_STATUS: Record<string, string> = { MOI_TAO: "#94a3b8", CHO_PHE_DUYET: "#f59e0b", DA_PHE_DUYET: "#10b981", DANG_TUYEN: "#3b82f6", DA_DONG: "#64748b", HUY: "#ef4444", MOI_NOP_HO_SO: "#94a3b8", CHO_PHONG_VAN_VONG_1: "#f59e0b", CHO_DANH_GIA: "#3b82f6", DE_NGHI_TUYEN: "#10b981", TU_CHOI: "#ef4444", RUT_HO_SO: "#94a3b8", DA_TUYEN: "#10b981", CHUA_DIEN_RA: "#94a3b8", CHO_PHAN_HOI: "#f59e0b", DA_DONG_Y: "#10b981" };

const TAB_LABELS: Record<string, string> = { "yeu-cau": "Yeu cau TD", "ung-vien": "Ung vien", "phong-van": "Phong van", "quyet-dinh": "Quyet dinh" };
const TRAIN_TABS: Record<string, string> = { ct: "Chuong trinh", lop: "Lop hoc", dk: "Dang ky", dg: "Danh gia" };

// BHXH columns helper
function BHXH_COLS(type: string) {
  if (type === "D02-LT") return <><th style={th}>STT</th><th style={th}>Ma NV</th><th style={th}>Ho ten</th><th style={th}>Ma so BHXH</th><th style={th}>Loai</th><th style={th}>Ngay PS</th><th style={th}>Muc luong</th><th style={th}>Trang thai</th></>;
  return <><th style={th}>STT</th><th style={th}>Ma NV</th><th style={th}>Ho ten</th><th style={th}>Ma so BHXH</th><th style={th}>Ngay cap</th><th style={th}>Loai de nghi</th><th style={th}>Ly do</th></>;
}

// Common styles
const card: React.CSSProperties = { background: "#fff", padding: 16, marginBottom: 16, borderRadius: 8, boxShadow: "0 1px 3px rgba(0,0,0,0.08)" };
const cardTitle: React.CSSProperties = { marginTop: 0, marginBottom: 12, color: "#0f172a" };
const th: React.CSSProperties = { padding: 10, textAlign: "left", fontWeight: 600, fontSize: 13 };
const td: React.CSSProperties = { padding: 10, fontSize: 13 };
const td2: React.CSSProperties = td;
const lblStyle: React.CSSProperties = { display: "block", fontSize: 12, color: "#64748b", marginBottom: 4 };
const inpStyle: React.CSSProperties = { padding: 8, width: 180, border: "1px solid #cbd5e1", borderRadius: 4 };
const btnStyle: React.CSSProperties = { padding: "8px 16px", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer" };
const btnStyleSmall: React.CSSProperties = { padding: "4px 10px", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 12 };
const tabBtn = (active: boolean): React.CSSProperties => ({ padding: "8px 16px", background: active ? "#0d9488" : "#fff", color: active ? "#fff" : "#0f172a", border: "1px solid #0d9488", borderRadius: 4, cursor: "pointer", fontSize: 13 });

// ============================================================================
// APP — root with HashRouter + AppLayout
// ============================================================================
function App() {
  return (
    <HashRouter>
      <AppLayout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/hr/departments" element={<DepartmentPage />} />
          <Route path="/hr/employees" element={<EmployeePage />} />
          <Route path="/hr/contracts" element={<ContractPage />} />
          <Route path="/attendance" element={<AttendancePage />} />
          <Route path="/leave" element={<LeavePage />} />
          <Route path="/system" element={<SystemAdminPage />} />
          <Route path="/offboarding" element={<OffboardingListPage />} />
          <Route path="/offboarding/new" element={<OffboardingDetailPage />} />
          <Route path="/offboarding/:id" element={<OffboardingDetailPage />} />
          <Route path="/bhxh/reports" element={<BhxhReportPage />} />
          <Route path="/tax/qtt" element={<QuyetToanThuePage />} />
          <Route path="/recruitment" element={<RecruitmentListPage />} />
          <Route path="/performance/kpi" element={<KpiPage />} />
          <Route path="/payroll/run" element={<PayrollRunPage />} />
          <Route path="/training" element={<TrainingPage />} />
        </Routes>
      </AppLayout>
    </HashRouter>
  );
}

export default App;
