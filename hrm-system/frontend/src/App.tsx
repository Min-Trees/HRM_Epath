import { HashRouter, Routes, Route } from "react-router-dom";
import OffboardingListPage from "./offboarding/OffboardingListPage";
import OffboardingDetailPage from "./offboarding/OffboardingDetailPage";
import BhxhReportPage from "./bhxh/BhxhReportPage";
import QuyetToanThuePage from "./tax/QuyetToanThuePage";
import RecruitmentListPage from "./recruitment/RecruitmentListPage";
import KpiPage from "./performance/KpiPage";
import PayrollRunPage from "./payroll/PayrollRunPage";
import TrainingPage from "./training/TrainingPage";

function App() {
  return (
    <HashRouter>
      <div style={{ fontFamily: "system-ui, sans-serif", minHeight: "100vh", background: "#f8fafc" }}>
        <Routes>
          <Route path="/" element={<Dashboard />} />
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
      </div>
    </HashRouter>
  );
}

function Dashboard() {
  return (
    <div style={{ padding: 24 }}>
      <h1>HRM</h1>
      <p>Quản lý nhân sự · Chấm công · BHXH · Tính lương</p>
      <ul>
        <li><a href="#/offboarding">Hồ sơ nghỉ việc (T14)</a></li>
        <li><a href="#/bhxh/reports">BHXH Reports - D02-LT, D03-LT (T15)</a></li>
        <li><a href="#/tax/qtt">Quyết toán thuế TNCN - 02/QTT, 05/QTT (T16)</a></li>
        <li><a href="#/recruitment">Tuyển dụng (T17)</a></li>
        <li><a href="#/performance/kpi">Đánh giá hiệu suất KPI/OKR (T18)</a></li>
        <li><a href="#/payroll/run">Payroll Run - Workflow kỳ lĩnh (T19)</a></li>
        <li><a href="#/training">Đào tạo - Training (T20)</a></li>
      </ul>
    </div>
  );
}

export default App;