// T04 mock: Department + Salary Grade (PhongBan + NgachBacLuong)
// Mirrors API shapes from com.company.hrm.hr.controller.HrController

const STORAGE_KEY = "hrm_t04_departments";
const GRADE_KEY = "hrm_t04_salary_grades";

const COMPANY_ID = "11111111-1111-1111-1111-111111111111";

function delay(ms = 100) { return new Promise(r => setTimeout(r, ms)); }

function loadDept() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { phongBanId: "pb-root-1", maPhongBan: "PGD", tenPhongBan: "Ban Giam doc", phongBanChaId: null, capDo: 1, dinhBien: 3, active: true },
    { phongBanId: "pb-root-2", maPhongBan: "PNS", tenPhongBan: "Phong Nhan su", phongBanChaId: null, capDo: 1, dinhBien: 5, active: true },
    { phongBanId: "pb-root-3", maPhongBan: "PKT", tenPhongBan: "Phong Ky thuat", phongBanChaId: null, capDo: 1, dinhBien: 8, active: true },
    { phongBanId: "pb-root-4", maPhongBan: "PIT", tenPhongBan: "Phong IT", phongBanChaId: null, capDo: 1, dinhBien: 4, active: true },
    { phongBanId: "pb-root-5", maPhongBan: "PKD", tenPhongBan: "Phong Kinh doanh", phongBanChaId: null, capDo: 1, dinhBien: 10, active: true },
    { phongBanId: "pb-1", maPhongBan: "PNS_TD", tenPhongBan: "Phong Tuyen dung", phongBanChaId: "pb-root-2", capDo: 2, dinhBien: 2, active: true },
    { phongBanId: "pb-2", maPhongBan: "PIT_DEV", tenPhongBan: "Phong Phat trien", phongBanChaId: "pb-root-4", capDo: 2, dinhBien: 3, active: true },
  ];
  saveDept(initial);
  return initial;
}

function saveDept(data: any[]) { localStorage.setItem(STORAGE_KEY, JSON.stringify(data)); }

function loadGrades() {
  const raw = localStorage.getItem(GRADE_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "g1", maNgach: "NV01", tenNgach: "Nhan vien", soBac: 5, luongCoSo: 2340000, heSo: 1.0, active: true },
    { id: "g2", maNgach: "NV02", tenNgach: "Nhan vien cap II", soBac: 5, luongCoSo: 2340000, heSo: 1.2, active: true },
    { id: "g3", maNgach: "NV03", tenNgach: "Nhan vien cap III", soBac: 5, luongCoSo: 2340000, heSo: 1.4, active: true },
    { id: "g4", maNgach: "KTV01", tenNgach: "Ky thuat vien", soBac: 5, luongCoSo: 2700000, heSo: 1.0, active: true },
    { id: "g5", maNgach: "KTV02", tenNgach: "Ky thuat vien cap II", soBac: 5, luongCoSo: 2700000, heSo: 1.2, active: true },
    { id: "g6", maNgach: "TP", tenNgach: "Truong phong", soBac: 3, luongCoSo: 5000000, heSo: 1.0, active: true },
    { id: "g7", maNgach: "PP", tenNgach: "Pho phong", soBac: 3, luongCoSo: 4200000, heSo: 1.0, active: true },
    { id: "g8", maNgach: "GD", tenNgach: "Giam doc", soBac: 1, luongCoSo: 15000000, heSo: 1.0, active: true },
  ];
  saveGrades(initial);
  return initial;
}

function saveGrades(data: any[]) { localStorage.setItem(GRADE_KEY, JSON.stringify(data)); }

export const deptMock = {
  async listDept(view = "tree") {
    await delay();
    const depts = loadDept();
    if (view === "flat") return depts.filter(d => d.active);
    // Build tree
    const map = new Map<string, any>();
    const roots: any[] = [];
    depts.filter(d => d.active).forEach(d => { map.set(d.phongBanId, { ...d, children: [] }); });
    map.forEach(d => {
      if (d.phongBanChaId && map.has(d.phongBanChaId)) {
        map.get(d.phongBanChaId).children.push(d);
      } else if (!d.phongBanChaId) {
        roots.push(d);
      }
    });
    return roots;
  },

  async createDept(form: any) {
    await delay();
    const depts = loadDept();
    const existing = depts.find(d => d.maPhongBan === form.maPhongBan && d.active);
    if (existing) throw new Error("Ma phong ban da ton tai");
    const parent = form.phongBanChaId ? depts.find(d => d.phongBanId === form.phongBanChaId) : null;
    const newDept = {
      phongBanId: "pb-" + Date.now(),
      maPhongBan: form.maPhongBan,
      tenPhongBan: form.tenPhongBan,
      phongBanChaId: form.phongBanChaId || null,
      capDo: parent ? parent.capDo + 1 : 1,
      dinhBien: form.dinhBien || 0,
      active: true,
    };
    depts.push(newDept);
    saveDept(depts);
    return newDept;
  },

  async updateDept(id: string, form: any) {
    await delay();
    const depts = loadDept();
    const idx = depts.findIndex(d => d.phongBanId === id);
    if (idx < 0) throw new Error("Khong tim thay phong ban");
    depts[idx] = { ...depts[idx], ...form };
    saveDept(depts);
    return depts[idx];
  },

  async closeDept(id: string) {
    await delay();
    const depts = loadDept();
    const idx = depts.findIndex(d => d.phongBanId === id);
    if (idx < 0) throw new Error("Khong tim thay phong ban");
    // Check children
    const hasChildren = depts.some(d => d.phongBanChaId === id && d.active);
    if (hasChildren) throw new Error("Phong ban con con nhan vien, khong the dong");
    depts[idx].active = false;
    saveDept(depts);
    return depts[idx];
  },

  async assignManager(id: string, nhanVienId: string) {
    await delay();
    const depts = loadDept();
    const idx = depts.findIndex(d => d.phongBanId === id);
    if (idx < 0) throw new Error("Khong tim thay phong ban");
    depts[idx] = { ...depts[idx], truongBoPhanId: nhanVienId };
    saveDept(depts);
    return depts[idx];
  },
};

export const salaryGradeMock = {
  async listGrades(params?: any) {
    await delay();
    const grades = loadGrades().filter(g => g.active);
    if (!params?.page && !params?.size) return grades;
    const page = params?.page || 0;
    const size = params?.size || 20;
    return {
      content: grades.slice(page * size, (page + 1) * size),
      totalElements: grades.length,
      totalPages: Math.ceil(grades.length / size),
      page, size,
    };
  },

  async createGrade(form: any) {
    await delay();
    const grades = loadGrades();
    const existing = grades.find(g => g.maNgach === form.maNgach && g.active);
    if (existing) throw new Error("Ma ngach da ton tai");
    const newGrade = {
      id: "g-" + Date.now(),
      maNgach: form.maNgach,
      tenNgach: form.tenNgach,
      soBac: form.soBac,
      luongCoSo: form.luongCoSo,
      heSo: form.heSo,
      active: true,
    };
    grades.push(newGrade);
    saveGrades(grades);
    return newGrade;
  },

  async updateGrade(id: string, form: any) {
    await delay();
    const grades = loadGrades();
    const idx = grades.findIndex(g => g.id === id);
    if (idx < 0) throw new Error("Khong tim thay ngach bac");
    grades[idx] = { ...grades[idx], ...form };
    saveGrades(grades);
    return grades[idx];
  },

  async closeGrade(id: string) {
    await delay();
    const grades = loadGrades();
    const idx = grades.findIndex(g => g.id === id);
    if (idx < 0) throw new Error("Khong tim thay ngach bac");
    grades[idx].active = false;
    saveGrades(grades);
    return grades[idx];
  },
};
