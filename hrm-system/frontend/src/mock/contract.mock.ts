// T06 mock: Contract management (HopDongLaoDong)

const STORAGE_KEY = "hrm_t06_contracts";

function delay(ms = 100) { return new Promise(r => setTimeout(r, ms)); }

function loadContracts() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (raw) return JSON.parse(raw);
  const initial: any[] = [
    { id: "hd-1", nhanVienId: "nv-1", maNv: "NV001", hoTen: "Nguyen Van A", soHopDong: "HD-2020-001", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "2020-01-01", ngayHetHieuLuc: "2023-12-31", mucLuongThoaThuan: 9000000, phuCapCoDinh: { "chuc_vu": 1500000, "an_trua": 1000000 }, trangThai: "HET_HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
    { id: "hd-2", nhanVienId: "nv-1", maNv: "NV001", hoTen: "Nguyen Van A", soHopDong: "HD-2024-001", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "2024-01-01", ngayHetHieuLuc: "2026-12-31", mucLuongThoaThuan: 12000000, phuCapCoDinh: { "chuc_vu": 2000000, "an_trua": 1500000, "xang_xe": 500000 }, trangThai: "HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
    { id: "hd-3", nhanVienId: "nv-2", maNv: "NV002", hoTen: "Tran Thi B", soHopDong: "HD-2021-002", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "2021-03-15", ngayHetHieuLuc: "2024-03-14", mucLuongThoaThuan: 8500000, phuCapCoDinh: { "an_trua": 800000 }, trangThai: "HET_HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
    { id: "hd-4", nhanVienId: "nv-2", maNv: "NV002", hoTen: "Tran Thi B", soHopDong: "HD-2024-002", loaiHopDong: "KHONG_XAC_DINH_THOI_HAN", ngayHieuLuc: "2024-03-15", ngayHetHieuLuc: null, mucLuongThoaThuan: 9000000, phuCapCoDinh: { "an_trua": 1000000 }, trangThai: "HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
    { id: "hd-5", nhanVienId: "nv-3", maNv: "NV003", hoTen: "Le Van C", soHopDong: "HD-2019-003", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "2019-07-01", ngayHetHieuLuc: "2024-06-30", mucLuongThoaThuan: 10000000, phuCapCoDinh: { "chuc_vu": 2500000 }, trangThai: "HET_HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
    { id: "hd-6", nhanVienId: "nv-3", maNv: "NV003", hoTen: "Le Van C", soHopDong: "HD-2024-003", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "2024-07-01", ngayHetHieuLuc: "2027-06-30", mucLuongThoaThuan: 13000000, phuCapCoDinh: { "chuc_vu": 3000000, "an_trua": 1200000 }, trangThai: "HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
    { id: "hd-7", nhanVienId: "nv-4", maNv: "NV004", hoTen: "Pham Thi D", soHopDong: "HD-2022-004", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "2022-01-10", ngayHetHieuLuc: "2025-01-09", mucLuongThoaThuan: 8000000, phuCapCoDinh: { "an_trua": 700000 }, trangThai: "HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
    { id: "hd-8", nhanVienId: "nv-5", maNv: "NV005", hoTen: "Hoang Van E", soHopDong: "HD-2020-005", loaiHopDong: "XAC_DINH_THOI_HAN", ngayHieuLuc: "2020-06-01", ngayHetHieuLuc: "2025-05-31", mucLuongThoaThuan: 18000000, phuCapCoDinh: { "chuc_vu": 5000000, "an_trua": 2000000, "xang_xe": 1000000 }, trangThai: "HIEU_LUC", hopDongGocId: null, companyId: "11111111-1111-1111-1111-111111111111", fileDinhKemUrl: null },
  ];
  localStorage.setItem(STORAGE_KEY, JSON.stringify(initial));
  return initial;
}

function saveContracts(data: any[]) { localStorage.setItem(STORAGE_KEY, JSON.stringify(data)); }

function fmtVND(v: number) {
  if (!v) return "-";
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(v);
}

let nextHd = 9;
export const contractMock = {
  async listByEmployee(nvId: string) {
    await delay();
    return loadContracts().filter(c => c.nhanVienId === nvId).sort((a, b) => b.ngayHieuLuc.localeCompare(a.ngayHieuLuc));
  },

  async get(id: string) {
    await delay();
    const c = loadContracts().find(x => x.id === id);
    if (!c) throw new Error("Khong tim thay hop dong");
    return c;
  },

  async create(nvId: string, form: any) {
    await delay();
    const contracts = loadContracts();
    const existing = contracts.find(c => c.soHopDong === form.soHopDong);
    if (existing) throw new Error("So hop dong da ton tai");
    const emp = loadContracts().find(c => c.nhanVienId === nvId) || {};
    const newC = {
      id: "hd-" + (nextHd++),
      nhanVienId: nvId,
      maNv: emp.maNv || "",
      hoTen: emp.hoTen || "",
      soHopDong: form.soHopDong,
      loaiHopDong: form.loaiHopDong,
      ngayHieuLuc: form.ngayHieuLuc,
      ngayHetHieuLuc: form.loaiHopDong === "KHONG_XAC_DINH_THOI_HAN" ? null : form.ngayHetHieuLuc,
      mucLuongThoaThuan: form.mucLuongThoaThuan,
      phuCapCoDinh: form.phuCapCoDinh || {},
      trangThai: "HIEU_LUC",
      hopDongGocId: null,
      companyId: "11111111-1111-1111-1111-111111111111",
      fileDinhKemUrl: null,
    };
    contracts.push(newC);
    saveContracts(contracts);
    return newC;
  },

  async createAddendum(gocId: string, form: any) {
    await delay();
    const contracts = loadContracts();
    const goc = contracts.find(c => c.id === gocId);
    if (!goc) throw new Error("Khong tim thay hop dong goc");
    const newC = {
      id: "hd-" + (nextHd++),
      nhanVienId: goc.nhanVienId,
      maNv: goc.maNv,
      hoTen: goc.hoTen,
      soHopDong: goc.soHopDong + "-PL1",
      loaiHopDong: "PHU_LUC",
      ngayHieuLuc: form.ngayHieuLuc,
      ngayHetHieuLuc: form.ngayHetHieuLuc,
      mucLuongThoaThuan: goc.mucLuongThoaThuan,
      phuCapCoDinh: goc.phuCapCoDinh,
      trangThai: "HIEU_LUC",
      hopDongGocId: gocId,
      companyId: "11111111-1111-1111-1111-111111111111",
      fileDinhKemUrl: null,
    };
    contracts.push(newC);
    saveContracts(contracts);
    return newC;
  },

  async expiring(fromDays: number, toDays: number) {
    await delay();
    const contracts = loadContracts().filter(c => c.trangThai === "HIEU_LUC" && c.ngayHetHieuLuc);
    const today = new Date();
    return contracts
      .map(c => {
        const exp = new Date(c.ngayHetHieuLuc);
        const daysLeft = Math.ceil((exp.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
        return { ...c, soNgayConLai: daysLeft };
      })
      .filter(c => c.soNgayConLai >= fromDays && c.soNgayConLai <= toDays)
      .sort((a, b) => a.soNgayConLai - b.soNgayConLai);
  },

  fmtVND,
};
