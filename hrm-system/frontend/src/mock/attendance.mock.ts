// T09 mock: Attendance / Time logs (ChamCongChiTiet)

const TIME_LOG_KEY = "hrm_t09_timelogs";

function delay(ms = 100) { return new Promise(r => setTimeout(r, ms)); }

function loadTimeLogs() {
  const raw = localStorage.getItem(TIME_LOG_KEY);
  if (raw) return JSON.parse(raw);

  // Generate sample data for the current month
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1;
  const daysInMonth = new Date(year, month, 0).getDate();

  const logs: any[] = [];
  const employees = [
    { id: "nv-1", maNv: "NV001", hoTen: "Nguyen Van A" },
    { id: "nv-2", maNv: "NV002", hoTen: "Tran Thi B" },
    { id: "nv-3", maNv: "NV003", hoTen: "Le Van C" },
    { id: "nv-4", maNv: "NV004", hoTen: "Pham Thi D" },
    { id: "nv-5", maNv: "NV005", hoTen: "Hoang Van E" },
  ];

  let logId = 1;
  for (const emp of employees) {
    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(year, month - 1, day);
      const dow = date.getDay();
      if (dow === 0) continue; // skip Sunday

      const isWeekend = dow === 6;
      if (isWeekend) continue; // skip Saturday for now

      const baseHour = 8;
      const baseMin = 5;
      const log: any = {
        id: "tl-" + (logId++),
        nhanVienId: emp.id,
        maNv: emp.maNv,
        hoTen: emp.hoTen,
        ngayChamCong: `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`,
        gioVao: `${String(baseHour + Math.floor(Math.random() * 3)).padStart(2, "0")}:${String(baseMin + Math.floor(Math.random() * 50)).padStart(2, "0")}`,
        gioRa: `${String(17 + Math.floor(Math.random() * 2)).padStart(2, "0")}:${String(Math.floor(Math.random() * 60)).padStart(2, "0")}`,
        nguon: "VAN_TAY",
        loaiNgoaiLe: "KHONG_NGOAI_LE",
        soGioCong: 8.0,
        canGiaiTrinh: false,
        giaiTrinhNoiDung: null,
        giaiTrinhTrangThai: null,
        duyetBoi: null,
        duyetLuc: null,
      };

      // Add some exceptions for realism
      if (day === 5 || day === 12) { log.loaiNgoaiLe = "DI_TRE"; log.canGiaiTrinh = true; log.gioVao = "09:30"; }
      if (day === 19) { log.loaiNgoaiLe = "VE_SOM"; log.canGiaiTrinh = true; log.gioRa = "15:30"; }

      logs.push(log);
    }
  }

  localStorage.setItem(TIME_LOG_KEY, JSON.stringify(logs));
  return logs;
}

function saveTimeLogs(data: any[]) { localStorage.setItem(TIME_LOG_KEY, JSON.stringify(data)); }

let nextLogId = 1000;
export const attendanceMock = {
  async list(params: { employeeId?: string; from?: string; to?: string }) {
    await delay();
    const logs = loadTimeLogs();
    return logs.filter(l => {
      if (params.employeeId && l.nhanVienId !== params.employeeId) return false;
      if (params.from && l.ngayChamCong < params.from) return false;
      if (params.to && l.ngayChamCong > params.to) return false;
      return true;
    }).sort((a, b) => b.ngayChamCong.localeCompare(a.ngayChamCong));
  },

  async record(form: any) {
    await delay();
    const logs = loadTimeLogs();
    const existing = logs.find(l => l.nhanVienId === form.nhanVienId && l.ngayChamCong === form.ngayChamCong);
    if (existing) throw new Error("Da co du lieu cham cong cho ngay nay");
    const newLog = { id: "tl-" + (nextLogId++), ...form, loaiNgoaiLe: form.loaiNgoaiLe || "KHONG_NGOAI_LE", canGiaiTrinh: false };
    logs.push(newLog);
    saveTimeLogs(logs);
    return newLog;
  },

  async submitExplanation(id: string, noiDung: string) {
    await delay();
    const logs = loadTimeLogs();
    const idx = logs.findIndex(l => l.id === id);
    if (idx < 0) throw new Error("Khong tim thay ban ghi cham cong");
    logs[idx].canGiaiTrinh = false;
    logs[idx].giaiTrinhNoiDung = noiDung;
    logs[idx].giaiTrinhTrangThai = "CHO_DUYET";
    saveTimeLogs(logs);
    return logs[idx];
  },

  async approve(id: string, approve: boolean, approverId: string, ghiChu: string) {
    await delay();
    const logs = loadTimeLogs();
    const idx = logs.findIndex(l => l.id === id);
    if (idx < 0) throw new Error("Khong tim thay ban ghi cham cong");
    logs[idx].giaiTrinhTrangThai = approve ? "DA_DUYET" : "TU_CHOI";
    logs[idx].duyetBoi = approverId;
    logs[idx].duyetLuc = new Date().toISOString();
    if (ghiChu) logs[idx].ghiChuDuyet = ghiChu;
    saveTimeLogs(logs);
    return logs[idx];
  },

  async summary(employeeId: string, thang: number, nam: number) {
    await delay();
    const logs = loadTimeLogs().filter(l => {
      if (l.nhanVienId !== employeeId) return false;
      const d = new Date(l.ngayChamCong);
      return d.getMonth() + 1 === thang && d.getFullYear() === nam;
    });

    const daysInMonth = new Date(nam, thang, 0).getDate();
    const soNgayCong = logs.filter(l => l.loaiNgoaiLe !== "THIEU_CONG").length;
    const exceptions = logs.filter(l => l.loaiNgoaiLe !== "KHONG_NGOAI_LE" && l.loaiNgoaiLe !== "THIEU_CONG").length;
    const tongGio = logs.reduce((s, l) => s + (parseFloat(l.soGioCong) || 0), 0);

    return {
      nhanVienId: employeeId,
      thang,
      nam,
      soNgayCong,
      soNgayNghi: daysInMonth - soNgayCong,
      soNgayNgoaiLe: exceptions,
      tongGioCong: tongGio.toFixed(2),
      chiTiet: logs,
    };
  },
};
