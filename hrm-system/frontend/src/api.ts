// =============================================================================
// HRM_Epath — API Client (connects to Spring Boot backend at http://localhost:8080)
// All 140 endpoints across 12 modules. Replace mock data with these real calls.
// =============================================================================

const BASE = "http://localhost:8080/api/v1";

function getToken(): string {
  return localStorage.getItem("hrm_token") || "";
}

async function request(
  method: string,
  path: string,
  body?: any,
  params?: Record<string, string>
): Promise<any> {
  const url = new URL(BASE + path, window.location.href);
  if (params) {
    Object.entries(params).forEach(([k, v]) => url.searchParams.set(k, v));
  }

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };
  const token = getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const res = await fetch(url.toString(), {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401) {
    window.location.hash = "#/login";
    throw new Error("Unauthorized");
  }
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`API ${method} ${path} failed: ${res.status} ${text}`);
  }
  const ct = res.headers.get("Content-Type") || "";
  if (ct.includes("xml")) return res.text();
  return res.json();
}

const get = (path: string, params?: Record<string, string>) =>
  request("GET", path, undefined, params);
const post = (path: string, body?: any) => request("POST", path, body);
const put = (path: string, body?: any) => request("PUT", path, body);
const patch = (path: string, body?: any) => request("PATCH", path, body);
const del = (path: string) => request("DELETE", path);

// =============================================================================
// AUTH
// =============================================================================
export const authApi = {
  login: (username: string, password: string) =>
    post("/auth/login", { username, password }),
  me: () => get("/auth/me"),
};

// =============================================================================
// SYSTEM — Companies
// =============================================================================
export const companyApi = {
  create: (data: any) => post("/system/companies", data),
  list: (status?: string) =>
    get("/system/companies", status ? { status } : undefined),
  get: (id: string) => get(`/system/companies/${id}`),
  update: (id: string, data: any) =>
    put(`/system/companies/${id}`, data),
  updateStatus: (id: string, status: string) =>
    post(`/system/companies/${id}/status?status=${status}`),
};

// =============================================================================
// SYSTEM — Users
// =============================================================================
export const userApi = {
  create: (data: any) => post("/system/users", data),
  list: (companyId: string) =>
    get("/system/users", { companyId }),
  get: (id: string) => get(`/system/users/${id}`),
  update: (id: string, data: any) =>
    put(`/system/users/${id}`, data),
  lock: (id: string) => post(`/system/users/${id}/lock`),
  unlock: (id: string) => post(`/system/users/${id}/unlock`),
  resetPassword: (id: string, newPassword: string) =>
    post(`/system/users/${id}/reset-password?newPassword=${newPassword}`),
};

// =============================================================================
// HR — Departments
// =============================================================================
export const departmentApi = {
  list: (view: "flat" | "tree" = "flat") =>
    get("/hr/departments", { view }),
  get: (id: string) => get(`/hr/departments/${id}`),
  create: (data: any) => post("/hr/departments", data),
  update: (id: string, data: any) =>
    put(`/hr/departments/${id}`, data),
  close: (id: string) => patch(`/hr/departments/${id}/close`),
  assignManager: (id: string, nhanVienId: string) =>
    put(`/hr/departments/${id}/manager`, { nhanVienId }),
};

// =============================================================================
// HR — Salary Grades
// =============================================================================
export const salaryGradeApi = {
  list: (page = 0, size = 20) =>
    get("/hr/salary-grades", { page: String(page), size: String(size) }),
  create: (data: any) => post("/hr/salary-grades", data),
  update: (id: string, data: any) =>
    put(`/hr/salary-grades/${id}`, data),
  close: (id: string) => patch(`/hr/salary-grades/${id}/close`),
};

// =============================================================================
// HR — Employees
// =============================================================================
export const employeeApi = {
  create: (data: any) => post("/hr/employees", data),
  list: (q = "", phongBanId?: string, trangThai?: string, page = 0, size = 20) => {
    const params: Record<string, string> = { q, page: String(page), size: String(size) };
    if (phongBanId) params["phongBanId"] = phongBanId;
    if (trangThai) params["trangThai"] = trangThai;
    return get("/hr/employees", params);
  },
  get: (id: string) => get(`/hr/employees/${id}`),
  update: (id: string, data: any) =>
    put(`/hr/employees/${id}`, data),
  // Dependents
  listDependents: (id: string) =>
    get(`/hr/employees/${id}/dependents`),
  addDependent: (id: string, data: any) =>
    post(`/hr/employees/${id}/dependents`, data),
  updateDependent: (id: string, depId: string, data: any) =>
    put(`/hr/employees/${id}/dependents/${depId}`, data),
  deleteDependent: (id: string, depId: string) =>
    del(`/hr/employees/${id}/dependents/${depId}`),
  // Work History
  listWorkHistory: (id: string) =>
    get(`/hr/employees/${id}/work-history`),
  addWorkHistory: (id: string, data: any) =>
    post(`/hr/employees/${id}/work-history`, data),
  updateWorkHistory: (id: string, wid: string, data: any) =>
    put(`/hr/employees/${id}/work-history/${wid}`, data),
  deleteWorkHistory: (id: string, wid: string) =>
    del(`/hr/employees/${id}/work-history/${wid}`),
  // Movements
  listMovements: (id: string) =>
    get(`/hr/employees/${id}/movements`),
  getStatus: (id: string, date?: string) =>
    get(`/hr/employees/${id}/status`, date ? { date } : undefined),
};

// =============================================================================
// HR — Contracts
// =============================================================================
export const contractApi = {
  create: (nhanVienId: string, data: any) =>
    post(`/hr/employees/${nhanVienId}/contracts`, data),
  listByEmployee: (nhanVienId: string) =>
    get(`/hr/employees/${nhanVienId}/contracts`),
  get: (id: string) => get(`/hr/contracts/${id}`),
  update: (id: string, data: any) =>
    put(`/hr/contracts/${id}`, data),
  addAddendum: (contractId: string, data: any) =>
    post(`/hr/contracts/${contractId}/addendum`, data),
  listExpiring: (fromDays = 30, toDays = 45) =>
    get("/hr/contracts/expiring", {
      fromDays: String(fromDays),
      toDays: String(toDays),
    }),
};

// =============================================================================
// HR — Personnel Movements
// =============================================================================
export const bienDongApi = {
  create: (data: any) => post("/hr/movements", data),
};

// =============================================================================
// ATTENDANCE — Shifts
// =============================================================================
export const shiftApi = {
  list: (active?: boolean) =>
    get("/attendance/shifts", active !== undefined ? { active: String(active) } : undefined),
  get: (id: string) => get(`/attendance/shifts/${id}`),
  create: (data: any) => post("/attendance/shifts", data),
  update: (id: string, data: any) =>
    put(`/attendance/shifts/${id}`, data),
  close: (id: string) => patch(`/attendance/shifts/${id}/close`),
};

// =============================================================================
// ATTENDANCE — Shift Assignments
// =============================================================================
export const shiftAssignmentApi = {
  assign: (data: any) => post("/attendance/shift-assignments", data),
  list: (employeeId: string, from: string, to: string) =>
    get("/attendance/shift-assignments", { employeeId, from, to }),
  get: (id: string) => get(`/attendance/shift-assignments/${id}`),
  delete: (id: string) => del(`/attendance/shift-assignments/${id}`),
  getStandardShift: (employeeId: string, date?: string) =>
    get(`/attendance/employees/${employeeId}/standard-shift`,
      date ? { date } : undefined),
};

// =============================================================================
// ATTENDANCE — Time Logs
// =============================================================================
export const timeLogApi = {
  record: (data: any) => post("/attendance/time-logs", data),
  batchSync: (data: any) => post("/attendance/time-logs/batch", data),
  list: (employeeId: string, from: string, to: string) =>
    get("/attendance/time-logs", { employeeId, from, to }),
  get: (id: string) => get(`/attendance/time-logs/${id}`),
  submitExplanation: (id: string, giaiTrinh: string) =>
    post(`/attendance/time-logs/${id}/explanation`, { giaiTrinh }),
  approve: (id: string, duyet: boolean, ghiChu?: string) =>
    post(`/attendance/time-logs/${id}/approve`, { duyet, ghiChu }),
  getMonthlySummary: (employeeId: string, month: number, year: number) =>
    get(`/attendance/employees/${employeeId}/summary`, {
      month: String(month),
      year: String(year),
    }),
};

// =============================================================================
// ATTENDANCE — Exceptions
// =============================================================================
export const attendanceExceptionApi = {
  list: (from: string, to: string, status?: string) => {
    const params: Record<string, string> = { from, to };
    if (status) params["status"] = status;
    return get("/attendance/exceptions", params);
  },
};

// =============================================================================
// ATTENDANCE — Leave Requests
// =============================================================================
export const leaveRequestApi = {
  create: (data: any) => post("/attendance/leave-requests", data),
  list: (employeeId?: string, status?: string) => {
    const params: Record<string, string> = {};
    if (employeeId) params["employeeId"] = employeeId;
    if (status) params["status"] = status;
    return get("/attendance/leave-requests", params);
  },
  get: (id: string) => get(`/attendance/leave-requests/${id}`),
  approveCap1: (id: string, duyet: boolean, ghiChu?: string) =>
    post(`/attendance/leave-requests/${id}/approve-cap1`, { duyet, ghiChu }),
  approveCap2: (id: string, duyet: boolean, ghiChu?: string) =>
    post(`/attendance/leave-requests/${id}/approve-cap2`, { duyet, ghiChu }),
  cancel: (id: string) =>
    post(`/attendance/leave-requests/${id}/cancel`),
};

// =============================================================================
// ATTENDANCE — Overtime Requests
// =============================================================================
export const otRequestApi = {
  create: (data: any) => post("/attendance/overtime-requests", data),
  list: (employeeId?: string, status?: string) => {
    const params: Record<string, string> = {};
    if (employeeId) params["employeeId"] = employeeId;
    if (status) params["status"] = status;
    return get("/attendance/overtime-requests", params);
  },
  get: (id: string) => get(`/attendance/overtime-requests/${id}`),
  approveCap1: (id: string, duyet: boolean, ghiChu?: string) =>
    post(`/attendance/overtime-requests/${id}/approve-cap1`, { duyet, ghiChu }),
  approveCap2: (id: string, duyet: boolean, ghiChu?: string) =>
    post(`/attendance/overtime-requests/${id}/approve-cap2`, { duyet, ghiChu }),
  cancel: (id: string) =>
    post(`/attendance/overtime-requests/${id}/cancel`),
  getMonthly: (employeeId: string, month: number, year: number) =>
    get(`/attendance/overtime/employees/${employeeId}/monthly`, {
      month: String(month),
      year: String(year),
    }),
};

// =============================================================================
// ATTENDANCE — Leave Balance
// =============================================================================
export const leaveBalanceApi = {
  get: (employeeId: string, year: number) =>
    get("/attendance/leave-balance", { employeeId, year: String(year) }),
  init: (data: any) => post("/attendance/leave-balance/init", data),
};

// =============================================================================
// OFFBOARDING
// =============================================================================
export const offboardingApi = {
  list: (page = 0, size = 20) =>
    get("/hr/offboarding/cases", { page: String(page), size: String(size) }),
  get: (id: string) => get(`/hr/offboarding/cases/${id}`),
  create: (data: any) => post("/hr/offboarding/cases", data),
  updateStatus: (id: string, status: string) =>
    patch(`/hr/offboarding/cases/${id}/status?status=${status}`),
  listTasks: (caseId: string) =>
    get(`/hr/offboarding/cases/${caseId}/tasks`),
  updateTask: (taskId: string, trangThai: string, fileDinhKemUrl?: string, ghiChu?: string) =>
    patch(`/hr/offboarding/tasks/${taskId}`,
      { trangThai, ...(fileDinhKemUrl && { fileDinhKemUrl }), ...(ghiChu && { ghiChu }) }),
  previewSeverance: (nhanVienId: string, ngayNghiViec: string, luongBinhQuan6Thang: number, lyDo: string) =>
    post("/hr/offboarding/severance/preview", undefined),
  getSeverance: (caseId: string) =>
    get(`/hr/offboarding/cases/${caseId}/severance`),
  calculateSeverance: (caseId: string, luongBinhQuan6Thang: number) =>
    post(`/hr/offboarding/cases/${caseId}/severance?luongBinhQuan6Thang=${luongBinhQuan6Thang}`),
};

// =============================================================================
// PAYROLL — Ky Linh Luong
// =============================================================================
export const payrollRunApi = {
  list: () => get("/payroll/run/ky-luong"),
  get: (id: string) => get(`/payroll/run/ky-luong/${id}`),
  create: (thang: number, nam: number) =>
    post("/payroll/run/ky-luong", { thang, nam }),
  start: (id: string) => post(`/payroll/run/ky-luong/${id}/start`),
  approveCap1: (id: string) =>
    post(`/payroll/run/ky-luong/${id}/approve-cap-1`),
  approveCap2: (id: string) =>
    post(`/payroll/run/ky-luong/${id}/approve-cap-2`),
  markPaid: (id: string, fileZipUrl?: string) =>
    post(`/payroll/run/ky-luong/${id}/pay-paid`,
      fileZipUrl ? { fileZipUrl } : undefined),
  cancel: (id: string, lyDo: string) =>
    post(`/payroll/run/ky-luong/${id}/cancel?lyDo=${lyDo}`),
  getPayslip: (kyLinhId: string, nhanVienId: string) =>
    get(`/payroll/run/ky-luong/${kyLinhId}/payslip/${nhanVienId}`),
};

// =============================================================================
// PAYROLL — Thue TNCN
// =============================================================================
export const thueApi = {
  generate02: (nam: number, maDonVi: string, tenDonVi: string, maSoThue: string) =>
    get("/payroll/tax/qtt/02", {
      nam: String(nam),
      maDonVi,
      tenDonVi,
      maSoThue,
    }),
  download02Xml: (nam: number, maDonVi: string, tenDonVi: string, maSoThue: string) =>
    get("/payroll/tax/qtt/02.xml", {
      nam: String(nam),
      maDonVi,
      tenDonVi,
      maSoThue,
    }),
  generate05: (nam: number, nhanVienId: string, tenDonVi: string, maSoThue: string) =>
    get("/payroll/tax/qtt/05", {
      nam: String(nam),
      nhanVienId,
      tenDonVi,
      maSoThueDonVi: maSoThue,
    }),
  download05Xml: (nam: number, nhanVienId: string, tenDonVi: string, maSoThue: string) =>
    get("/payroll/tax/qtt/05.xml", {
      nam: String(nam),
      nhanVienId,
      tenDonVi,
      maSoThueDonVi: maSoThue,
    }),
  upsertCamKet08: (data: any) =>
    post("/payroll/tax/cam-ket-08", data),
};

// =============================================================================
// BHXH Reports
// =============================================================================
export const bhxhReportApi = {
  generateD02: (tuNgay: string, denNgay: string, maDonViBHXH: string, tenDonVi: string, maSoThue: string) =>
    get("/bhxh/reports/d02-lt", { tuNgay, denNgay, maDonViBHXH, tenDonVi, maSoThueDonVi: maSoThue }),
  downloadD02Xml: (tuNgay: string, denNgay: string, maDonViBHXH: string, tenDonVi: string, maSoThue: string) =>
    get("/bhxh/reports/d02-lt.xml", { tuNgay, denNgay, maDonViBHXH, tenDonVi, maSoThueDonVi: maSoThue }),
  generateD03: (tuNgay: string, denNgay: string, maDonViBHXH: string, tenDonVi: string) =>
    get("/bhxh/reports/d03-lt", { tuNgay, denNgay, maDonViBHXH, tenDonVi }),
  downloadD03Xml: (tuNgay: string, denNgay: string, maDonViBHXH: string, tenDonVi: string) =>
    get("/bhxh/reports/d03-lt.xml", { tuNgay, denNgay, maDonViBHXH, tenDonVi }),
};

// =============================================================================
// RECRUITMENT — Recruitment Requests
// =============================================================================
export const recruitmentApi = {
  // Yeu cau
  listYeuCau: () => get("/recruitment/yeu-cau"),
  getYeuCau: (id: string) => get(`/recruitment/yeu-cau/${id}`),
  createYeuCau: (data: any) => post("/recruitment/yeu-cau", data),
  submitYeuCau: (id: string) => post(`/recruitment/yeu-cau/${id}/submit`),
  approveYeuCau: (id: string) => post(`/recruitment/yeu-cau/${id}/approve`),
  startRecruiting: (id: string) => post(`/recruitment/yeu-cau/${id}/start-recruiting`),
  closeYeuCau: (id: string) => post(`/recruitment/yeu-cau/${id}/close`),
  // Ung vien
  listUngVien: () => get("/recruitment/ung-vien"),
  getUngVien: (id: string) => get(`/recruitment/ung-vien/${id}`),
  listUngVienByYeuCau: (yeuCauId: string) =>
    get(`/recruitment/yeu-cau/${yeuCauId}/ung-vien`),
  createUngVien: (data: any) => post("/recruitment/ung-vien", data),
  updateUngVienStatus: (id: string, status: string) =>
    post(`/recruitment/ung-vien/${id}/status?status=${status}`),
  // Lich phong van
  listLichPV: (ungVienId: string) =>
    get(`/recruitment/ung-vien/${ungVienId}/lich-phong-van`),
  schedulePV: (data: any) => post("/recruitment/lich-phong-van", data),
  updateLichPVStatus: (id: string, status: string) =>
    post(`/recruitment/lich-phong-van/${id}/status?status=${status}`),
  listDanhGiaPV: (lichPvId: string) =>
    get(`/recruitment/lich-phong-van/${lichPvId}/danh-gia`),
  submitDanhGiaPV: (data: any) =>
    post("/recruitment/danh-gia", data),
  // Quyet dinh tuyen
  listQuyetDinh: (ungVienId: string) =>
    get(`/recruitment/ung-vien/${ungVienId}/quyet-dinh`),
  createQuyetDinh: (data: any) =>
    post("/recruitment/quyet-dinh", data),
  respondQuyetDinh: (id: string, dongY: boolean) =>
    post(`/recruitment/quyet-dinh/${id}/ung-vien-phan-hoi?dongY=${dongY}`),
};

// =============================================================================
// PERFORMANCE — KPI / OKR
// =============================================================================
export const kpiApi = {
  listCycles: () => get("/performance/cycles"),
  createCycle: (data: any) => post("/performance/cycles", data),
  startCycle: (id: string) => post(`/performance/cycles/${id}/start`),
  closeCycle: (id: string) => post(`/performance/cycles/${id}/close`),
  listAssignments: (cycleId: string) =>
    get("/performance/assignments", { cycleId }),
  listByEmployee: (nhanVienId: string, cycleId: string) =>
    get(`/performance/nhan-vien/${nhanVienId}/assignments`, { cycleId }),
  createAssignment: (data: any) =>
    post("/performance/assignments", data),
  selfAssess: (assignmentId: string, data: any) =>
    post(`/performance/assignments/${assignmentId}/self-assess`, data),
  managerReview: (assignmentId: string, data: any) =>
    post(`/performance/assignments/${assignmentId}/manager-review`, data),
  hrApprove: (assignmentId: string, data: any) =>
    post(`/performance/assignments/${assignmentId}/hr-approve`, data),
};

// =============================================================================
// TRAINING
// =============================================================================
export const trainingApi = {
  // Chuong trinh
  listChuongTrinh: () => get("/training/chuong-trinh"),
  createChuongTrinh: (data: any) =>
    post("/training/chuong-trinh", data),
  congBoChuongTrinh: (id: string) =>
    post(`/training/chuong-trinh/${id}/cong-bo`),
  // Lop hoc
  listLop: () => get("/training/lop"),
  createLop: (data: any) => post("/training/lop", data),
  lopTransition: (id: string, newState: string) =>
    post(`/training/lop/${id}/transition?newState=${newState}`),
  // Dang ky
  dangKy: (data: any) => post("/training/dang-ky", data),
  duyetDangKy: (id: string, quyetDinh: "DA_CHAP_NHAN" | "TU_CHOI", ghiChu?: string) =>
    post(`/training/dang-ky/${id}/duyet?quyetDinh=${quyetDinh}`,
      ghiChu ? { ghiChu } : undefined),
  listDangKyByLop: (lopId: string) =>
    get(`/training/dang-ky/by-lop/${lopId}`),
  listDangKyByNV: (nvId: string) =>
    get(`/training/dang-ky/by-nv/${nvId}`),
  // Diem danh & Danh gia
  diemDanh: (data: any) => post("/training/diem-danh", data),
  danhGia: (data: any) => post("/training/danh-gia", data),
};
