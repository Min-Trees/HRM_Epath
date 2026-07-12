import { HashRouter, Routes, Route } from "react-router-dom";
import OffboardingListPage from "./offboarding/OffboardingListPage";
import OffboardingDetailPage from "./offboarding/OffboardingDetailPage";
import BhxhReportPage from "./bhxh/BhxhReportPage";

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
      </ul>
    </div>
  );
}

export default App;
