// T10 mock: Leave requests (NghiPhep) + Overtime (DangKyOt) + Leave Balance (QuyPhepNam)

const LEAVE_KEY = "hrm_t10_leave";
const OT_KEY = "hrm_t10_ot";
const BALANCE_KEY = "hrm_t10_balance";

function delay(ms = 100) { return new Promise(r => setTimeout(r, ms)); }

function loadLeave() {
  const raw = localStorage.getItem(LEAVE_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "lv-1", nhanVienId: "nv-1", maNv: "NV001", hoTen: "Nguyen Van A", loaiNghiPhep: "PHEP_NAM", tuNgay: "2026-07-14", denNgay: "2026-07-18", soNgayNghi: 5.0, lyDo: "Di du lich", trangThai: "DA_DUYET", fileDinhKemUrl: null, duyetCap1Boi: "nv-5", ngayDuyetCap1: "2026-07-10", duyetCap2Boi: "nv-1", ngayDuyetCap2: "2026-07-10", ghiChuDuyet: "OK" },
    { id: "lv-2", nhanVienId: "nv-2", maNv: "NV002", hoTen: "Tran Thi B", loaiNghiPhep: "OM", tuNgay: "2026-07-03", denNgay: "2026-07-04", soNgayNghi: 2.0, lyDo: "Bi cam cum", trangThai: "DA_DUYET", fileDinhKemUrl: null, duyetCap1Boi: "nv-5", ngayDuyetCap1: "2026-07-03", duyetCap2Boi: "nv-1", ngayDuyetCap2: "2026-07-03", ghiChuDuyet: null },
    { id: "lv-3", nhanVienId: "nv-3", maNv: "NV003", hoTen: "Le Van C", loaiNghiPhep: "PHEP_NAM", tuNgay: "2026-07-21", denNgay: "2026-07-25", soNgayNghi: 5.0, lyDo: "Nghi he mua thu", trangThai: "CHO_DUYET", fileDinhKemUrl: null, duyetCap1Boi: null, ngayDuyetCap1: null, duyetCap2Boi: null, ngayDuyetCap2: null, ghiChuDuyet: null },
    { id: "lv-4", nhanVienId: "nv-4", maNv: "NV004", hoTen: "Pham Thi D", loaiNghiPhep: "VIEC_RIENG_CO_LUONG", tuNgay: "2026-07-07", denNgay: "2026-07-07", soNgayNghi: 1.0, lyDo: "Co viec gia dinh", trangThai: "TU_CHOI", fileDinhKemUrl: null, duyetCap1Boi: "nv-5", ngayDuyetCap1: "2026-07-06", duyetCap2Boi: null, ngayDuyetCap2: null, ghiChuDuyet: "Khong co giay to" },
  ];
  localStorage.setItem(LEAVE_KEY, JSON.stringify(initial));
  return initial;
}

function saveLeave(data: any[]) { localStorage.setItem(LEAVE_KEY, JSON.stringify(data)); }

function loadOT() {
  const raw = localStorage.getItem(OT_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "ot-1", nhanVienId: "nv-1", maNv: "NV001", hoTen: "Nguyen Van A", ngayLamOt: "2026-07-05", gioBatDau: "17:00", gioKetThuc: "20:00", soGioOt: 3.0, heSoOt: "NGAY_THUONG_150", lamDem: false, lyDo: "Doi du lieu", trangThai: "DA_DUYET", duyetCap1Boi: "nv-5", ngayDuyetCap1: "2026-07-05", duyetCap2Boi: "nv-1", ngayDuyetCap2: "2026-07-05" },
    { id: "ot-2", nhanVienId: "nv-2", maNv: "NV002", hoTen: "Tran Thi B", ngayLamOt: "2026-07-08", gioBatDau: "18:00", gioKetThuc: "22:00", soGioOt: 4.0, heSoOt: "NGAY_THUONG_150", lamDem: false, lyDo: "Hoan thanh du an", trangThai: "DUYET_CAP_1", duyetCap1Boi: "nv-5", ngayDuyetCap1: "2026-07-08", duyetCap2Boi: null, ngayDuyetCap2: null },
    { id: "ot-3", nhanVienId: "nv-3", maNv: "NV003", hoTen: "Le Van C", ngayLamOt: "2026-07-12", gioBatDau: "14:00", gioKetThuc: "22:00", soGioOt: 8.0, heSoOt: "NGAY_NGHI_TUAN_200", lamDem: true, lyDo: "Doi he thong may chu", trangThai: "CHO_DUYET", duyetCap1Boi: null, ngayDuyetCap1: null, duyetCap2Boi: null, ngayDuyetCap2: null },
  ];
  localStorage.setItem(OT_KEY, JSON.stringify(initial));
  return initial;
}

function saveOT(data: any[]) { localStorage.setItem(OT_KEY, JSON.stringify(data)); }

function loadBalance() {
  const raw = localStorage.getItem(BALANCE_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { nhanVienId: "nv-1", nam: 2026, soNgayDuocHuong: 14.0, soNgayDaDung: 5.0, soNgayConLai: 9.0 },
    { nhanVienId: "nv-2", nam: 2026, soNgayDuocHuong: 12.0, soNgayDaDung: 2.0, soNgayConLai: 10.0 },
    { nhanVienId: "nv-3", nam: 2026, soNgayDuocHuong: 13.0, soNgayDaDung: 0.0, soNgayConLai: 13.0 },
    { nhanVienId: "nv-4", nam: 2026, soNgayDuocHuong: 12.0, soNgayDaDung: 0.0, soNgayConLai: 12.0 },
    { nhanVienId: "nv-5", nam: 2026, soNgayDuocHuong: 14.0, soNgayDaDung: 0.0, soNgayConLai: 14.0 },
  ];
  localStorage.setItem(BALANCE_KEY, JSON.stringify(initial));
  return initial;
}

function saveBalance(data: any[]) { localStorage.setItem(BALANCE_KEY, JSON.stringify(data)); }

const LOAI_PHEP_OPTIONS = [
  { value: "PHEP_NAM", label: "Phep nam" },
  { value: "OM", label: "Om" },
  { value: "VIEC_RIENG_CO_LUONG", label: "Viec rieng co luong" },
  { value: "VIEC_RIENG_KHONG_LUONG", label: "Viec rieng khong luong" },
  { value: "THAI_SAN", label: "Thai san" },
  { value: "KHAC", label: "Khac" },
];

const TRANG_THAI_LABEL: Record<string, string> = {
  CHO_DUYET: "Cho duyet",
  DUYET_CAP_1: "Da duyet cap 1",
  DA_DUYET: "Da duyet",
  TU_CHOI: "Tu choi",
  HUY: "Huy",
};

export const leaveMock = {
  LOAI_PHEP_OPTIONS,
  TRANG_THAI_LABEL,

  async list(params: { employeeId?: string; statuses?: string[] }) {
    await delay();
    const leaves = loadLeave();
    return leaves.filter(l => {
      if (params.employeeId && l.nhanVienId !== params.employeeId) return false;
      if (params.statuses?.length && !params.statuses.includes(l.trangThai)) return false;
      return true;
    });
  },

  async create(form: any) {
    await delay();
    const leaves = loadLeave();
    const newLeave = { id: "lv-" + Date.now(), ...form, trangThai: "CHO_DUYET" };
    leaves.push(newLeave);
    saveLeave(leaves);
    return newLeave;
  },

  async approveCap1(id: string, approve: boolean, approverId: string, ghiChu: string) {
    await delay();
    const leaves = loadLeave();
    const idx = leaves.findIndex(l => l.id === id);
    if (idx < 0) throw new Error("Khong tim thay don nghi phep");
    leaves[idx].trangThai = approve ? "DUYET_CAP_1" : "TU_CHOI";
    leaves[idx].duyetCap1Boi = approverId;
    leaves[idx].ngayDuyetCap1 = new Date().toISOString().split("T")[0];
    if (ghiChu) leaves[idx].ghiChuDuyet = ghiChu;
    saveLeave(leaves);
    return leaves[idx];
  },

  async approveCap2(id: string, approve: boolean, approverId: string, ghiChu: string) {
    await delay();
    const leaves = loadLeave();
    const idx = leaves.findIndex(l => l.id === id);
    if (idx < 0) throw new Error("Khong tim thay don nghi phep");
    if (leaves[idx].duyetCap1Boi === approverId) throw new Error("Khong duoc cung nguoi duyet 2 cap");
    leaves[idx].trangThai = approve ? "DA_DUYET" : "TU_CHOI";
    leaves[idx].duyetCap2Boi = approverId;
    leaves[idx].ngayDuyetCap2 = new Date().toISOString().split("T")[0];
    if (ghiChu) leaves[idx].ghiChuDuyet = ghiChu;
    // Update balance
    if (approve && leaves[idx].loaiNghiPhep === "PHEP_NAM") {
      const bal = loadBalance();
      const bIdx = bal.findIndex(b => b.nhanVienId === leaves[idx].nhanVienId && b.nam === new Date().getFullYear());
      if (bIdx >= 0) {
        bal[bIdx].soNgayDaDung += leaves[idx].soNgayNghi;
        bal[bIdx].soNgayConLai -= leaves[idx].soNgayNghi;
        saveBalance(bal);
      }
    }
    saveLeave(leaves);
    return leaves[idx];
  },

  async cancel(id: string) {
    await delay();
    const leaves = loadLeave();
    const idx = leaves.findIndex(l => l.id === id);
    if (idx < 0) throw new Error("Khong tim thay don nghi phep");
    if (leaves[idx].trangThai === "DA_DUYET" && leaves[idx].loaiNghiPhep === "PHEP_NAM") {
      const bal = loadBalance();
      const bIdx = bal.findIndex(b => b.nhanVienId === leaves[idx].nhanVienId && b.nam === new Date().getFullYear());
      if (bIdx >= 0) {
        bal[bIdx].soNgayDaDung -= leaves[idx].soNgayNghi;
        bal[bIdx].soNgayConLai += leaves[idx].soNgayNghi;
        saveBalance(bal);
      }
    }
    leaves[idx].trangThai = "HUY";
    saveLeave(leaves);
    return leaves[idx];
  },
};

export const otMock = {
  async list(params: { employeeId?: string; statuses?: string[] }) {
    await delay();
    const ots = loadOT();
    return ots.filter(o => {
      if (params.employeeId && o.nhanVienId !== params.employeeId) return false;
      if (params.statuses?.length && !params.statuses.includes(o.trangThai)) return false;
      return true;
    });
  },

  async create(form: any) {
    await delay();
    const ots = loadOT();
    const newOT = { id: "ot-" + Date.now(), ...form, trangThai: "CHO_DUYET" };
    ots.push(newOT);
    saveOT(ots);
    return newOT;
  },

  async approveCap1(id: string, approve: boolean, approverId: string) {
    await delay();
    const ots = loadOT();
    const idx = ots.findIndex(o => o.id === id);
    if (idx < 0) throw new Error("Khong tim thay don OT");
    ots[idx].trangThai = approve ? "DUYET_CAP_1" : "TU_CHOI";
    ots[idx].duyetCap1Boi = approverId;
    ots[idx].ngayDuyetCap1 = new Date().toISOString().split("T")[0];
    saveOT(ots);
    return ots[idx];
  },

  async approveCap2(id: string, approve: boolean, approverId: string) {
    await delay();
    const ots = loadOT();
    const idx = ots.findIndex(o => o.id === id);
    if (idx < 0) throw new Error("Khong tim thay don OT");
    if (ots[idx].duyetCap1Boi === approverId) throw new Error("Khong duoc cung nguoi duyet 2 cap");
    ots[idx].trangThai = approve ? "DA_DUYET" : "TU_CHOI";
    ots[idx].duyetCap2Boi = approverId;
    ots[idx].ngayDuyetCap2 = new Date().toISOString().split("T")[0];
    saveOT(ots);
    return ots[idx];
  },
};

export const balanceMock = {
  async get(nhanVienId: string, nam: number) {
    await delay();
    const bal = loadBalance().find(b => b.nhanVienId === nhanVienId && b.nam === nam);
    return bal || { nhanVienId, nam, soNgayDuocHuong: 0, soNgayDaDung: 0, soNgayConLai: 0 };
  },

  async listByYear(nam: number) {
    await delay();
    return loadBalance().filter(b => b.nam === nam);
  },
};
