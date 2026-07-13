// T05 mock: Employee (NhanVien) + Dependents + Work History
// Mirrors API shapes from com.company.hrm.hr.controller.HrController

const STORAGE_KEY = "hrm_t05_employees";
const DEP_KEY = "hrm_t05_dependents";
const WORK_KEY = "hrm_t05_work_history";

function delay(ms = 100) { return new Promise(r => setTimeout(r, ms)); }

function loadEmployees() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { nhanVienId: "nv-1", maNv: "NV001", hoTen: "Nguyen Van A", soCccd: "079201001234", ngayCapCccd: "2020-01-15", noiCapCccd: "TP.HCM", ngaySinh: "1988-03-15", gioiTinh: "NAM", diaChiThuongTru: "123 Nguyen Trai, Q1, TP.HCM", diaChiLienLac: "123 Nguyen Trai, Q1, TP.HCM", soDienThoai: "0901123456", email: "a.nguyen@company.vn", phongBanId: "pb-root-2", phongBan: "Phong Nhan su", ngachBacId: "g1", ngachBac: "NV01 - Nhan vien", ngayVaoLam: "2020-01-01", trangThai: "CHINH_THUC", companyId: "11111111-1111-1111-1111-111111111111", maSoThue: "1234567890", quanLyTrucTiepId: "nv-5" },
    { nhanVienId: "nv-2", maNv: "NV002", hoTen: "Tran Thi B", soCccd: "079202003456", ngayCapCccd: "2019-06-20", noiCapCccd: "Ha Noi", ngaySinh: "1992-07-22", gioiTinh: "NU", diaChiThuongTru: "456 Le loi, Q3, TP.HCM", diaChiLienLac: "456 Le loi, Q3, TP.HCM", soDienThoai: "0902123456", email: "b.tran@company.vn", phongBanId: "pb-root-4", phongBan: "Phong IT", ngachBacId: "g1", ngachBac: "NV01 - Nhan vien", ngayVaoLam: "2021-03-15", trangThai: "CHINH_THUC", companyId: "11111111-1111-1111-1111-111111111111", maSoThue: "2345678901", quanLyTrucTiepId: "nv-5" },
    { nhanVienId: "nv-3", maNv: "NV003", hoTen: "Le Van C", soCccd: "079203005678", ngayCapCccd: "2018-09-10", noiCapCccd: "TP.HCM", ngaySinh: "1990-11-30", gioiTinh: "NAM", diaChiThuongTru: "789 Truong Chinh, Q.Tan Binh, TP.HCM", diaChiLienLac: "789 Truong Chinh, Q.Tan Binh, TP.HCM", soDienThoai: "0903123456", email: "c.le@company.vn", phongBanId: "pb-root-3", phongBan: "Phong Ky thuat", ngachBacId: "g4", ngachBac: "KTV01 - Ky thuat vien", ngayVaoLam: "2019-07-01", trangThai: "CHINH_THUC", companyId: "11111111-1111-1111-1111-111111111111", maSoThue: "3456789012", quanLyTrucTiepId: "nv-5" },
    { nhanVienId: "nv-4", maNv: "NV004", hoTen: "Pham Thi D", soCccd: "079204007890", ngayCapCccd: "2021-02-28", noiCapCccd: "TP.HCM", ngaySinh: "1995-05-18", gioiTinh: "NU", diaChiThuongTru: "321 Vo Van Tien, Q.10, TP.HCM", diaChiLienLac: "321 Vo Van Tien, Q.10, TP.HCM", soDienThoai: "0904123456", email: "d.pham@company.vn", phongBanId: "pb-root-5", phongBan: "Phong Kinh doanh", ngachBacId: "g2", ngachBac: "NV02 - Nhan vien cap II", ngayVaoLam: "2022-01-10", trangThai: "CHINH_THUC", companyId: "11111111-1111-1111-1111-111111111111", maSoThue: "4567890123", quanLyTrucTiepId: "nv-5" },
    { nhanVienId: "nv-5", maNv: "NV005", hoTen: "Hoang Van E", soCccd: "079205009012", ngayCapCccd: "2022-04-15", noiCapCccd: "TP.HCM", ngaySinh: "1985-12-25", gioiTinh: "NAM", diaChiThuongTru: "654 Dien Bien Phu, Q.Binh Thanh, TP.HCM", diaChiLienLac: "654 Dien Bien Phu, Q.Binh Thanh, TP.HCM", soDienThoai: "0905123456", email: "e.hoang@company.vn", phongBanId: "pb-root-1", phongBan: "Ban Giam doc", ngachBacId: "g8", ngachBac: "GD - Giam doc", ngayVaoLam: "2020-06-01", trangThai: "CHINH_THUC", companyId: "11111111-1111-1111-1111-111111111111", maSoThue: "5678901234", quanLyTrucTiepId: null },
  ];
  localStorage.setItem(STORAGE_KEY, JSON.stringify(initial));
  return initial;
}

function saveEmployees(data: any[]) { localStorage.setItem(STORAGE_KEY, JSON.stringify(data)); }

function loadDependents() {
  const raw = localStorage.getItem(DEP_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "dep-1", nhanVienId: "nv-1", hoTen: "Nguyen Thi X", quanHe: "VO", ngaySinh: "1988-05-10", soCccd: "079201991234", ngheNghiep: "Khong", tuNgay: "2020-01-01", denNgay: null, active: true },
    { id: "dep-2", nhanVienId: "nv-1", hoTen: "Nguyen Van Y", quanHe: "CON", ngaySinh: "2010-03-20", soCccd: "079201991235", ngheNghiep: "Hoc sinh", tuNgay: "2010-03-20", denNgay: null, active: true },
    { id: "dep-3", nhanVienId: "nv-4", hoTen: "Pham Van Z", quanHe: "CHONG", ngaySinh: "1993-08-15", soCccd: "079204991234", ngheNghiep: "Lai xe", tuNgay: "2022-01-10", denNgay: null, active: true },
  ];
  localStorage.setItem(DEP_KEY, JSON.stringify(initial));
  return initial;
}

function saveDependents(data: any[]) { localStorage.setItem(DEP_KEY, JSON.stringify(data)); }

function loadWorkHistory() {
  const raw = localStorage.getItem(WORK_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "wh-1", nhanVienId: "nv-3", tuNgay: "2019-07-01", denNgay: "2022-06-30", phongBan: "Phong Kinh doanh", chucDanh: "Nhan vien kinh doanh", ghiChu: "Chuyen sang PKT" },
    { id: "wh-2", nhanVienId: "nv-2", tuNgay: "2021-03-15", denNgay: null, phongBan: "Phong IT", chucDanh: "Lap trinh vien", ghiChu: null },
  ];
  localStorage.setItem(WORK_KEY, JSON.stringify(initial));
  return initial;
}

function saveWorkHistory(data: any[]) { localStorage.setItem(WORK_KEY, JSON.stringify(data)); }

let nextId = 100;
function newId() { return "emp-" + (nextId++); }

export const employeeMock = {
  async list(params?: { q?: string; phongBanId?: string; trangThai?: string; page?: number; size?: number }) {
    await delay();
    const emps = loadEmployees();
    let filtered = emps;
    if (params?.q) {
      const q = params.q.toLowerCase();
      filtered = filtered.filter(e => e.hoTen?.toLowerCase().includes(q) || e.maNv?.toLowerCase().includes(q));
    }
    if (params?.phongBanId) filtered = filtered.filter(e => e.phongBanId === params.phongBanId);
    if (params?.trangThai) filtered = filtered.filter(e => e.trangThai === params.trangThai);
    const page = params?.page || 0;
    const size = params?.size || 20;
    const content = filtered.slice(page * size, (page + 1) * size);
    return { content, totalElements: filtered.length, totalPages: Math.ceil(filtered.length / size), page, size };
  },

  async get(id: string) {
    await delay();
    const emps = loadEmployees();
    const emp = emps.find(e => e.nhanVienId === id);
    if (!emp) throw new Error("Khong tim thay nhan vien");
    const deps = loadDependents().filter(d => d.nhanVienId === id && d.active);
    const wh = loadWorkHistory().filter(w => w.nhanVienId === id);
    return { ...emp, dependents: deps, workHistory: wh };
  },

  async create(form: any) {
    await delay();
    const emps = loadEmployees();
    const existing = emps.find(e => e.soCccd === form.soCccd);
    if (existing) throw new Error("CCCD da ton tai");
    const maxMa = emps.reduce((m, e) => {
      const n = parseInt(e.maNv.replace("NV", ""));
      return n > m ? n : m;
    }, 0);
    const newEmp = {
      nhanVienId: newId(),
      maNv: "NV" + String(maxMa + 1).padStart(4, "0"),
      hoTen: form.hoTen,
      soCccd: form.soCccd,
      ngayCapCccd: form.ngayCapCccd,
      noiCapCccd: form.noiCapCccd,
      ngaySinh: form.ngaySinh,
      gioiTinh: form.gioiTinh,
      diaChiThuongTru: form.diaChiThuongTru,
      diaChiLienLac: form.diaChiLienLac || form.diaChiThuongTru,
      soDienThoai: form.soDienThoai,
      email: form.email,
      phongBanId: form.phongBanId,
      phongBan: form.phongBan,
      ngachBacId: form.ngachBacId,
      ngachBac: form.ngachBac,
      ngayVaoLam: form.ngayVaoLam,
      trangThai: "UNG_VIEN",
      companyId: "11111111-1111-1111-1111-111111111111",
      maSoThue: form.maSoThue || null,
      quanLyTrucTiepId: form.quanLyTrucTiepId || null,
    };
    emps.push(newEmp);
    saveEmployees(emps);
    return newEmp;
  },

  async updateInfo(id: string, form: any) {
    await delay();
    const emps = loadEmployees();
    const idx = emps.findIndex(e => e.nhanVienId === id);
    if (idx < 0) throw new Error("Khong tim thay nhan vien");
    // Only update info fields
    emps[idx] = { ...emps[idx], ...form };
    saveEmployees(emps);
    return emps[idx];
  },

  // Dependents
  async listDependents(nvId: string) {
    await delay();
    return loadDependents().filter(d => d.nhanVienId === nvId && d.active);
  },

  async createDependent(nvId: string, form: any) {
    await delay();
    const deps = loadDependents();
    const newDep = { id: "dep-" + Date.now(), nhanVienId: nvId, ...form, active: true };
    deps.push(newDep);
    saveDependents(deps);
    return newDep;
  },

  async deleteDependent(nvId: string, depId: string) {
    await delay();
    const deps = loadDependents();
    const idx = deps.findIndex(d => d.id === depId && d.nhanVienId === nvId);
    if (idx >= 0) { deps[idx].active = false; saveDependents(deps); }
    return { ok: true };
  },

  // Work History
  async listWorkHistory(nvId: string) {
    await delay();
    return loadWorkHistory().filter(w => w.nhanVienId === nvId);
  },

  async createWorkHistory(nvId: string, form: any) {
    await delay();
    const whs = loadWorkHistory();
    const newWh = { id: "wh-" + Date.now(), nhanVienId: nvId, ...form };
    whs.push(newWh);
    saveWorkHistory(whs);
    return newWh;
  },

  async deleteWorkHistory(nvId: string, whId: string) {
    await delay();
    const whs = loadWorkHistory();
    const filtered = whs.filter(w => !(w.id === whId && w.nhanVienId === nvId));
    saveWorkHistory(filtered);
    return { ok: true };
  },
};
