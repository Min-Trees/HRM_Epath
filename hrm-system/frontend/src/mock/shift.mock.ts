// T08 mock: Shift management (CaLamViec + PhanCa)

const SHIFT_KEY = "hrm_t08_shifts";
const ASSIGN_KEY = "hrm_t08_assignments";

function delay(ms = 100) { return new Promise(r => setTimeout(r, ms)); }

function loadShifts() {
  const raw = localStorage.getItem(SHIFT_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "sh-1", maCa: "HC1", tenCa: "Hanh chinh 1", loaiCa: "HANH_CHINH", gioBatDau: "08:00", gioKetThuc: "17:00", soGioChuan: 8.0, quaNgay: false, active: true },
    { id: "sh-2", maCa: "HC_FLEX", tenCa: "Hanh chinh linh hoat", loaiCa: "FLEXIBLE", gioBatDau: "08:00", gioKetThuc: "18:00", soGioChuan: 8.0, quaNgay: false, active: true },
    { id: "sh-3", maCa: "CA1", tenCa: "Ca sang", loaiCa: "CA_KIP", gioBatDau: "06:00", gioKetThuc: "14:00", soGioChuan: 8.0, quaNgay: false, active: true },
    { id: "sh-4", maCa: "CA2", tenCa: "Ca chieu", loaiCa: "CA_KIP", gioBatDau: "14:00", gioKetThuc: "22:00", soGioChuan: 8.0, quaNgay: false, active: true },
    { id: "sh-5", maCa: "CA3", tenCa: "Ca dem", loaiCa: "CA_KIP", gioBatDau: "22:00", gioKetThuc: "06:00", soGioChuan: 8.0, quaNgay: true, active: true },
  ];
  localStorage.setItem(SHIFT_KEY, JSON.stringify(initial));
  return initial;
}

function saveShifts(data: any[]) { localStorage.setItem(SHIFT_KEY, JSON.stringify(data)); }

function loadAssignments() {
  const raw = localStorage.getItem(ASSIGN_KEY);
  if (raw) return JSON.parse(raw);
  const today = new Date();
  const getDate = (offset: number) => {
    const d = new Date(today);
    d.setDate(d.getDate() + offset);
    return d.toISOString().split("T")[0];
  };
  const initial: any[] = [
    { id: "pa-1", nhanVienId: "nv-1", maNv: "NV001", hoTen: "Nguyen Van A", caId: "sh-1", tenCa: "Hanh chinh 1", ngayApDung: getDate(-30), ghiChu: "Mac dinh" },
    { id: "pa-2", nhanVienId: "nv-2", maNv: "NV002", hoTen: "Tran Thi B", caId: "sh-1", tenCa: "Hanh chinh 1", ngayApDung: getDate(-30), ghiChu: "Mac dinh" },
    { id: "pa-3", nhanVienId: "nv-3", maNv: "NV003", hoTen: "Le Van C", caId: "sh-3", tenCa: "Ca sang", ngayApDung: getDate(-30), ghiChu: "Ca kip" },
    { id: "pa-4", nhanVienId: "nv-4", maNv: "NV004", hoTen: "Pham Thi D", caId: "sh-1", tenCa: "Hanh chinh 1", ngayApDung: getDate(-30), ghiChu: "Mac dinh" },
    { id: "pa-5", nhanVienId: "nv-5", maNv: "NV005", hoTen: "Hoang Van E", caId: "sh-1", tenCa: "Hanh chinh 1", ngayApDung: getDate(-30), ghiChu: "Mac dinh" },
  ];
  localStorage.setItem(ASSIGN_KEY, JSON.stringify(initial));
  return initial;
}

function saveAssignments(data: any[]) { localStorage.setItem(ASSIGN_KEY, JSON.stringify(data)); }

export const shiftMock = {
  async listShifts(active?: boolean) {
    await delay();
    const shifts = loadShifts();
    if (active !== undefined) return shifts.filter(s => s.active === active);
    return shifts;
  },

  async getShift(id: string) {
    await delay();
    const s = loadShifts().find(x => x.id === id);
    if (!s) throw new Error("Khong tim thay ca lam viec");
    return s;
  },

  async createShift(form: any) {
    await delay();
    const shifts = loadShifts();
    const existing = shifts.find(s => s.maCa === form.maCa);
    if (existing) throw new Error("Ma ca da ton tai");
    const newS = { id: "sh-" + Date.now(), ...form, active: true };
    shifts.push(newS);
    saveShifts(shifts);
    return newS;
  },

  async updateShift(id: string, form: any) {
    await delay();
    const shifts = loadShifts();
    const idx = shifts.findIndex(s => s.id === id);
    if (idx < 0) throw new Error("Khong tim thay ca lam viec");
    shifts[idx] = { ...shifts[idx], ...form };
    saveShifts(shifts);
    return shifts[idx];
  },

  async closeShift(id: string) {
    await delay();
    const shifts = loadShifts();
    const idx = shifts.findIndex(s => s.id === id);
    if (idx < 0) throw new Error("Khong tim thay ca lam viec");
    shifts[idx].active = false;
    saveShifts(shifts);
    return shifts[idx];
  },

  async assignSingle(form: { nhanVienId: string; caId: string; ngayApDung: string; ghiChu?: string }) {
    await delay();
    const assignments = loadAssignments();
    const shift = loadShifts().find(s => s.id === form.caId);
    const emp = loadEmployees().find(e => e.nhanVienId === form.nhanVienId);
    if (!emp) throw new Error("Nhan vien khong ton tai");
    if (!shift) throw new Error("Ca lam viec khong ton tai");
    const existing = assignments.find(a => a.nhanVienId === form.nhanVienId && a.ngayApDung === form.ngayApDung);
    if (existing) {
      const idx = assignments.indexOf(existing);
      assignments[idx] = { ...existing, caId: form.caId, tenCa: shift.tenCa };
    } else {
      assignments.push({ id: "pa-" + Date.now(), nhanVienId: form.nhanVienId, maNv: emp.maNv, hoTen: emp.hoTen, caId: form.caId, tenCa: shift.tenCa, ngayApDung: form.ngayApDung, ghiChu: form.ghiChu || "" });
    }
    saveAssignments(assignments);
    return { ok: true };
  },

  async assignBulk(form: { nhanVienIds: string[]; caId: string; tuNgay: string; denNgay: string; ghiChu?: string }) {
    await delay();
    const assignments = loadAssignments();
    const shift = loadShifts().find(s => s.id === form.caId);
    let created = 0;
    for (const nvId of form.nhanVienIds) {
      const emp = loadEmployees().find(e => e.nhanVienId === nvId);
      if (!emp) continue;
      const start = new Date(form.tuNgay);
      const end = new Date(form.denNgay);
      for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
        const dateStr = d.toISOString().split("T")[0];
        const existing = assignments.find(a => a.nhanVienId === nvId && a.ngayApDung === dateStr);
        if (!existing) {
          assignments.push({ id: "pa-" + Date.now() + "-" + Math.random(), nhanVienId: nvId, maNv: emp.maNv, hoTen: emp.hoTen, caId: form.caId, tenCa: shift?.tenCa || "", ngayApDung: dateStr, ghiChu: form.ghiChu || "" });
          created++;
        }
      }
    }
    saveAssignments(assignments);
    return { created, skipped: 0 };
  },

  async listAssignments(employeeId: string, from: string, to: string) {
    await delay();
    const assignments = loadAssignments().filter(a => {
      if (employeeId && a.nhanVienId !== employeeId) return false;
      if (a.ngayApDung >= from && a.ngayApDung <= to) return true;
      return false;
    });
    return assignments.sort((a, b) => a.ngayApDung.localeCompare(b.ngayApDung));
  },
};

// Need employee reference
function loadEmployees() {
  const raw = localStorage.getItem("hrm_t05_employees");
  if (!raw) return [];
  return JSON.parse(raw);
}
