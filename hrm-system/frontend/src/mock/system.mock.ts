// T11 mock: System Admin (Company, Role, User, Audit)

const COMPANY_KEY = "hrm_t11_companies";
const USER_KEY = "hrm_t11_users";
const AUDIT_KEY = "hrm_t11_audit";

function delay(ms = 100) { return new Promise(r => setTimeout(r, ms)); }

function loadCompanies() {
  const raw = localStorage.getItem(COMPANY_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { companyId: "11111111-1111-1111-1111-111111111111", maSoThue: "0123456789", tenCongTy: "Cong ty TNHH Epath Viet Nam", diaChi: "123 Nguyen Hue, Q.1, TP.HCM", dienThoai: "02812345678", email: "info@epath.vn", trangThai: "ACTIVE", ngayThanhLap: "2018-01-15" },
    { companyId: "22222222-2222-2222-2222-222222222222", maSoThue: "9876543210", tenCongTy: "Cong ty TNHH ABC", diaChi: "456 Le Duan, Q.3, TP.HCM", dienThoai: "02887654321", email: "info@abc.vn", trangThai: "ACTIVE", ngayThanhLap: "2020-05-20" },
  ];
  localStorage.setItem(COMPANY_KEY, JSON.stringify(initial));
  return initial;
}

function saveCompanies(data: any[]) { localStorage.setItem(COMPANY_KEY, JSON.stringify(data)); }

function loadUsers() {
  const raw = localStorage.getItem(USER_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { userId: "u-1", username: "admin", email: "admin@epath.vn", roleCodes: ["SYSTEM_ADMIN"], trangThai: "ACTIVE", companyId: "11111111-1111-1111-1111-111111111111", employeeId: null, lastLogin: "2026-07-10T08:00:00" },
    { userId: "u-2", username: "cadmin", email: "cadmin@epath.vn", roleCodes: ["COMPANY_ADMIN", "HR_MANAGER"], trangThai: "ACTIVE", companyId: "11111111-1111-1111-1111-111111111111", employeeId: null, lastLogin: "2026-07-09T17:00:00" },
    { userId: "u-3", username: "a.nguyen", email: "a.nguyen@company.vn", roleCodes: ["HR", "HR_MANAGER"], trangThai: "ACTIVE", companyId: "11111111-1111-1111-1111-111111111111", employeeId: "nv-1", lastLogin: "2026-07-12T09:00:00" },
    { userId: "u-4", username: "b.tran", email: "b.tran@company.vn", roleCodes: ["EMPLOYEE"], trangThai: "ACTIVE", companyId: "11111111-1111-1111-1111-111111111111", employeeId: "nv-2", lastLogin: "2026-07-12T08:30:00" },
    { userId: "u-5", username: "c.le", email: "c.le@company.vn", roleCodes: ["MANAGER"], trangThai: "ACTIVE", companyId: "11111111-1111-1111-1111-111111111111", employeeId: "nv-3", lastLogin: "2026-07-11T16:00:00" },
    { userId: "u-6", username: "d.pham", email: "d.pham@company.vn", roleCodes: ["ACCOUNTANT", "PAYROLL_ACCOUNTANT"], trangThai: "ACTIVE", companyId: "11111111-1111-1111-1111-111111111111", employeeId: "nv-4", lastLogin: "2026-07-10T14:00:00" },
    { userId: "u-7", username: "e.hoang", email: "e.hoang@company.vn", roleCodes: ["COMPANY_ADMIN", "HR_MANAGER"], trangThai: "LOCKED", companyId: "11111111-1111-1111-1111-111111111111", employeeId: "nv-5", lastLogin: "2026-07-01T10:00:00" },
  ];
  localStorage.setItem(USER_KEY, JSON.stringify(initial));
  return initial;
}

function saveUsers(data: any[]) { localStorage.setItem(USER_KEY, JSON.stringify(data)); }

function loadAudit() {
  const raw = localStorage.getItem(AUDIT_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "aud-1", module: "hr", action: "CREATE_EMPLOYEE", entityType: "NhanVien", entityId: "nv-1", userId: "u-3", companyId: "11111111-1111-1111-1111-111111111111", timestamp: "2026-07-12T09:00:00", ipAddress: "192.168.1.10", oldValue: null, newValue: '{"hoTen":"Nguyen Van A","maNv":"NV001"}' },
    { id: "aud-2", module: "payroll", action: "APPROVE_PAYROLL", entityType: "BangLuongThang", entityId: "bl-1", userId: "u-6", companyId: "11111111-1111-1111-1111-111111111111", timestamp: "2026-07-10T17:00:00", ipAddress: "192.168.1.15", oldValue: '{"trangThai":"CHO_DUYET"}', newValue: '{"trangThai":"DA_DUYET"}' },
    { id: "aud-3", module: "system", action: "LOGIN", entityType: "UserAccount", entityId: "u-3", userId: "u-3", companyId: "11111111-1111-1111-1111-111111111111", timestamp: "2026-07-12T09:00:00", ipAddress: "192.168.1.10", oldValue: null, newValue: '{"username":"a.nguyen"}' },
    { id: "aud-4", module: "hr", action: "UPDATE_EMPLOYEE", entityType: "NhanVien", entityId: "nv-2", userId: "u-3", companyId: "11111111-1111-1111-1111-111111111111", timestamp: "2026-07-11T14:30:00", ipAddress: "192.168.1.10", oldValue: '{"soDienThoai":"0902000000"}', newValue: '{"soDienThoai":"0902123456"}' },
  ];
  localStorage.setItem(AUDIT_KEY, JSON.stringify(initial));
  return initial;
}

function saveAudit(data: any[]) { localStorage.setItem(AUDIT_KEY, JSON.stringify(data)); }

const ROLES = [
  { code: "SYSTEM_ADMIN", name: "Quan tri he thong", permissions: 50, description: "Toan quyen he thong" },
  { code: "COMPANY_ADMIN", name: "Quan tri cong ty", permissions: 49, description: "Quan tri toan bo cong ty" },
  { code: "HR_MANAGER", name: "Quan ly Nhan su", permissions: 35, description: "Quan ly nhan su va cham cong" },
  { code: "ACCOUNTANT", name: "Ke toan", permissions: 25, description: "Ke toan va tinh luong" },
  { code: "MANAGER", name: "Quan ly", permissions: 15, description: "Quan ly cap duoi" },
  { code: "EMPLOYEE", name: "Nhan vien", permissions: 5, description: "Nhan vien thong thuong" },
];

const PERMISSIONS = [
  { code: "hr.employee.create", name: "Tao nhan vien", module: "hr" },
  { code: "hr.employee.read", name: "Xem nhan vien", module: "hr" },
  { code: "hr.employee.update", name: "Sua nhan vien", module: "hr" },
  { code: "payroll.read", name: "Xem bang luong", module: "payroll" },
  { code: "payroll.approve", name: "Duyet bang luong", module: "payroll" },
  { code: "timekeeping.leave.create", name: "Tao don nghi phep", module: "timekeeping" },
  { code: "timekeeping.leave.approve_cap1", name: "Duyet cap 1 nghi phep", module: "timekeeping" },
];

export const systemMock = {
  ROLES,
  PERMISSIONS,

  // Companies
  async listCompanies() {
    await delay();
    return loadCompanies();
  },

  async createCompany(form: any) {
    await delay();
    const companies = loadCompanies();
    const existing = companies.find(c => c.maSoThue === form.maSoThue);
    if (existing) throw new Error("Ma so thue da ton tai");
    const newCo = { companyId: "c-" + Date.now(), ...form, trangThai: "ACTIVE" };
    companies.push(newCo);
    saveCompanies(companies);
    return newCo;
  },

  async updateCompany(id: string, form: any) {
    await delay();
    const companies = loadCompanies();
    const idx = companies.findIndex(c => c.companyId === id);
    if (idx < 0) throw new Error("Khong tim thay cong ty");
    companies[idx] = { ...companies[idx], ...form };
    saveCompanies(companies);
    return companies[idx];
  },

  // Users
  async listUsers(companyId?: string) {
    await delay();
    const users = loadUsers();
    if (companyId) return users.filter(u => u.companyId === companyId);
    return users;
  },

  async getUser(id: string) {
    await delay();
    const u = loadUsers().find(x => x.userId === id);
    if (!u) throw new Error("Khong tim thay tai khoan");
    return u;
  },

  async createUser(form: any) {
    await delay();
    const users = loadUsers();
    const existing = users.find(u => u.username === form.username);
    if (existing) throw new Error("Username da ton tai");
    const newU = { userId: "u-" + Date.now(), ...form, trangThai: "ACTIVE", lastLogin: null };
    users.push(newU);
    saveUsers(users);
    return newU;
  },

  async updateUser(id: string, form: any) {
    await delay();
    const users = loadUsers();
    const idx = users.findIndex(u => u.userId === id);
    if (idx < 0) throw new Error("Khong tim thay tai khoan");
    users[idx] = { ...users[idx], ...form };
    saveUsers(users);
    return users[idx];
  },

  async lockUser(id: string) {
    await delay();
    const users = loadUsers();
    const idx = users.findIndex(u => u.userId === id);
    if (idx < 0) throw new Error("Khong tim thay tai khoan");
    users[idx].trangThai = "LOCKED";
    saveUsers(users);
    return users[idx];
  },

  async unlockUser(id: string) {
    await delay();
    const users = loadUsers();
    const idx = users.findIndex(u => u.userId === id);
    if (idx < 0) throw new Error("Khong tim thay tai khoan");
    users[idx].trangThai = "ACTIVE";
    saveUsers(users);
    return users[idx];
  },

  // Audit
  async listAudit(params?: { module?: string; from?: string; to?: string; page?: number; size?: number }) {
    await delay();
    const logs = loadAudit();
    let filtered = logs;
    if (params?.module) filtered = filtered.filter(l => l.module === params.module);
    if (params?.from) filtered = filtered.filter(l => l.timestamp >= params.from);
    if (params?.to) filtered = filtered.filter(l => l.timestamp <= params.to);
    const page = params?.page || 0;
    const size = params?.size || 20;
    return {
      content: filtered.slice(page * size, (page + 1) * size),
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
      page, size,
    };
  },
};
