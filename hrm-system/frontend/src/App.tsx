import { HashRouter, Routes, Route, useLocation, useNavigate, Outlet } from "react-router-dom";
import React, { useState } from "react";
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
// DANH SÁCH ĐIỀU HƯỚNG
// ============================================================================
interface NavItem {
  path: string;
  label: string;
  task: string;
  color: string;
  icon: string;
}

const NAV_ITEMS: NavItem[] = [
  { path: "/", label: "Trang chủ", task: "", color: "#64748b", icon: "🏠" },
  { path: "/hr/departments", label: "Cơ cấu tổ chức", task: "T04", color: "#3b82f6", icon: "🏢" },
  { path: "/hr/employees", label: "Nhân sự", task: "T05", color: "#10b981", icon: "👤" },
  { path: "/hr/contracts", label: "Hợp đồng", task: "T06", color: "#8b5cf6", icon: "📄" },
  { path: "/attendance", label: "Chấm công", task: "T08+T09", color: "#06b6d4", icon: "🕐" },
  { path: "/leave", label: "Nghỉ phép & OT", task: "T10", color: "#f59e0b", icon: "🏖" },
  { path: "/system", label: "Quản trị hệ thống", task: "T11", color: "#64748b", icon: "⚙" },
  { path: "/offboarding", label: "Hồ sơ nghỉ việc", task: "T14", color: "#ef4444", icon: "📋" },
  { path: "/bhxh/reports", label: "Báo cáo BHXH", task: "T15", color: "#f59e0b", icon: "🏥" },
  { path: "/tax/qtt", label: "Quyết toán thuế TNCN", task: "T16", color: "#3b82f6", icon: "📊" },
  { path: "/recruitment", label: "Tuyển dụng", task: "T17", color: "#8b5cf6", icon: "👥" },
  { path: "/performance/kpi", label: "KPI / OKR", task: "T18", color: "#06b6d4", icon: "🎯" },
  { path: "/payroll/run", label: "Payroll Run", task: "T19", color: "#10b981", icon: "💰" },
  { path: "/training", label: "Đào tạo", task: "T20", color: "#ec4899", icon: "🎓" },
];

// ============================================================================
// LAYOUT — bao bọc mọi trang bằng sidebar (sử dụng <Outlet /> cho nội dung)
// ============================================================================
function AppLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  const isActive = (path: string) => {
    if (path === "/") return location.pathname === "/";
    return location.pathname.startsWith(path);
  };

  return (
    <div style={{ display: "flex", minHeight: "100vh", fontFamily: "system-ui, sans-serif", background: "#f8fafc" }}>
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

      <main className="hr-scroll" style={{ flex: 1, overflow: "auto", minWidth: 0 }}>
        <Outlet />
      </main>
    </div>
  );
}

// ============================================================================
// TRANG CHỦ — Bảng điều khiển tổng quan
// ============================================================================
function Dashboard() {
  const modules = [
    { path: "#/hr/departments", label: "Cơ cấu tổ chức", task: "T04", color: "#3b82f6" },
    { path: "#/hr/employees", label: "Hồ sơ nhân viên", task: "T05", color: "#10b981" },
    { path: "#/hr/contracts", label: "Hợp đồng lao động", task: "T06", color: "#8b5cf6" },
    { path: "#/attendance", label: "Chấm công & Ca làm việc", task: "T08+T09", color: "#06b6d4" },
    { path: "#/leave", label: "Nghỉ phép & Tăng ca", task: "T10", color: "#f59e0b" },
    { path: "#/system", label: "Quản trị hệ thống", task: "T11", color: "#64748b" },
    { path: "#/offboarding", label: "Hồ sơ nghỉ việc", task: "T14", color: "#ef4444" },
    { path: "#/bhxh/reports", label: "Báo cáo BHXH D02/D03", task: "T15", color: "#f97316" },
    { path: "#/tax/qtt", label: "Quyết toán thuế TNCN", task: "T16", color: "#3b82f6" },
    { path: "#/recruitment", label: "Tuyển dụng", task: "T17", color: "#8b5cf6" },
    { path: "#/performance/kpi", label: "Đánh giá KPI/OKR", task: "T18", color: "#06b6d4" },
    { path: "#/payroll/run", label: "Payroll Run - Kỳ lĩnh lương", task: "T19", color: "#10b981" },
    { path: "#/training", label: "Đào tạo", task: "T20", color: "#ec4899" },
  ];

  return (
    <div style={{ padding: 0 }}>
      <div style={{
        background: "linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%)",
        color: "white",
        padding: "40px 48px",
      }}>
        <h1 style={{ margin: 0, fontSize: 28, fontWeight: 700 }}>HRM_Epath</h1>
        <p style={{ margin: "8px 0 0", opacity: 0.85, fontSize: 15 }}>
          Hệ thống quản lý nhân sự · V13 · {new Date().getFullYear()}
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

      <div style={{ padding: "0 48px", marginTop: 32, marginBottom: 24 }}>
        <div style={{ background: "#fffbeb", border: "1px solid #fde68a", borderRadius: 12, padding: "16px 24px" }}>
          <strong style={{ color: "#92400e" }}>Tài khoản test</strong>
          <span style={{ color: "#78350f" }}> (mật khẩu: </span>
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

      <div style={{ padding: "0 48px", marginBottom: 32 }}>
        <h2 style={{ fontSize: 18, fontWeight: 600, color: "#1e293b", marginBottom: 16 }}>
          Các module ({modules.length} modules)
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

      <div style={{ padding: "0 48px 48px" }}>
        <div style={{ background: "#f0fdf4", border: "1px solid #bbf7d0", borderRadius: 12, padding: "20px 24px" }}>
          <h3 style={{ margin: "0 0 12px", fontSize: 15, color: "#166534" }}>Dữ liệu mẫu đã nạp</h3>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))", gap: 8 }}>
            {[
              ["Nhân viên", "5 NV (NV001–NV005) + NV006 (đã nghỉ)"],
              ["Phòng ban", "5 phòng ban (PGD, PNS, PKT, PIT, PKD)"],
              ["Bảng công", "Tháng 5/2026 — 5 NV — trạng thái ĐÃ CHỐT"],
              ["Bảng lương", "5 bảng lương — trạng thái ĐÃ DUYỆT"],
              ["Kỳ lĩnh lương", "2 kỳ (1 ĐÃ CHI TRẢ, 1 ĐÃ DUYỆT CẤP 2)"],
              ["BHXH", "5 NV — quá trình ĐANG ĐÓNG"],
              ["Cam kết 08", "5 NV — ỦY QUYỀN QTT"],
              ["Tuyển dụng", "2 yêu cầu (MỚI TẠO + ĐANG TUYỂN)"],
              ["KPI", "1 chu kỳ Q2/2026 — 4 phân công"],
              ["Đào tạo", "3 CT, 2 lớp, 6 đăng ký, 4 điểm danh"],
              ["Offboarding", "1 hồ sơ NV006 — HOÀN THÀNH"],
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
// APP — Gốc với HashRouter + layout route pattern
// ============================================================================
function App() {
  return (
    <HashRouter>
      <Routes>
        <Route element={<AppLayout />}>
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
        </Route>
      </Routes>
    </HashRouter>
  );
}

export default App;
