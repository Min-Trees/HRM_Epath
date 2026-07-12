// Mock layer for T18 - KPI/OKR
const STORAGE_KEY = "kpi_v1";

interface Cycle {
  cycleId: string;
  tenChuKy: string;
  loaiChuKy: string;
  ngayBatDau: string;
  ngayKetThuc: string;
  trangThai: string;
  soMucTieu: number;
  soNvThamGia: number;
}

interface Assign {
  assignmentId: string;
  cycleId: string;
  nhanVienId: string;
  hoTen: string;
  maNv: string;
  tenMucTieu: string;
  loaiMucTieu: string;
  donViDo: string;
  targetValue: number;
  trongSo: number;
  trangThai: string;
  diemTuDanhGia?: number;
  diemManager?: number;
  diemTrungBinh?: number;
  xepLoaiCuoi?: string;
}

interface Store {
  cycles: Cycle[];
  assigns: Assign[];
}

const seed: Store = {
  cycles: [
    { cycleId: "cycle-1", tenChuKy: "Q1-2026", loaiChuKy: "QUARTER", ngayBatDau: "2026-01-01", ngayKetThuc: "2026-03-31", trangThai: "DA_DONG", soMucTieu: 4, soNvThamGia: 2 },
    { cycleId: "cycle-2", tenChuKy: "Q2-2026", loaiChuKy: "QUARTER", ngayBatDau: "2026-04-01", ngayKetThuc: "2026-06-30", trangThai: "DANG_DANH_GIA", soMucTieu: 5, soNvThamGia: 3 },
    { cycleId: "cycle-3", tenChuKy: "Nam 2026", loaiChuKy: "YEAR", ngayBatDau: "2026-01-01", ngayKetThuc: "2026-12-31", trangThai: "MOI_TAO", soMucTieu: 0, soNvThamGia: 0 },
  ],
  assigns: [
    { assignmentId: "a-1", cycleId: "cycle-1", nhanVienId: "nv-1", hoTen: "Nguyen Van A", maNv: "NV-001", tenMucTieu: "Hoan thanh 5 du an", loaiMucTieu: "KPI", donViDo: "du an", targetValue: 5, trongSo: 30, trangThai: "HR_DA_PHE_DUYET", diemTuDanhGia: 90, diemManager: 95, diemTrungBinh: 93, xepLoaiCuoi: "A" },
    { assignmentId: "a-2", cycleId: "cycle-1", nhanVienId: "nv-1", hoTen: "Nguyen Van A", maNv: "NV-001", tenMucTieu: "Tang doanh thu 20%", loaiMucTieu: "OKR", donViDo: "%", targetValue: 20, trongSo: 50, trangThai: "HR_DA_PHE_DUYET", diemTuDanhGia: 85, diemManager: 88, diemTrungBinh: 87, xepLoaiCuoi: "B" },
    { assignmentId: "a-3", cycleId: "cycle-2", nhanVienId: "nv-1", hoTen: "Nguyen Van A", maNv: "NV-001", tenMucTieu: "Phat hanh 3 tinh nang", loaiMucTieu: "KPI", donViDo: "tinh nang", targetValue: 3, trongSo: 40, trangThai: "NV_DA_TU_DANH_GIA", diemTuDanhGia: 85 },
    { assignmentId: "a-4", cycleId: "cycle-2", nhanVienId: "nv-2", hoTen: "Tran Thi B", maNv: "NV-002", tenMucTieu: "Dam bao uptime 99.5%", loaiMucTieu: "KPI", donViDo: "%", targetValue: 99.5, trongSo: 60, trangThai: "MOI_GAN" },
    { assignmentId: "a-5", cycleId: "cycle-2", nhanVienId: "nv-3", hoTen: "Le Van C", maNv: "NV-003", tenMucTieu: "Training 2 khoa hoc", loaiMucTieu: "NHIEM_VU", donViDo: "khoa", targetValue: 2, trongSo: 100, trangThai: "MANAGER_DA_REVIEW", diemTuDanhGia: 75, diemManager: 80, diemTrungBinh: 78 },
  ],
};

function ensure() {
  if (typeof window === "undefined") return;
  if (!window.localStorage.getItem(STORAGE_KEY)) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(seed));
  }
}

function read(): Store {
  ensure();
  return JSON.parse(window.localStorage.getItem(STORAGE_KEY) || JSON.stringify(seed));
}

function write(s: Store) {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(s));
}

function delay<T>(v: T, ms = 80): Promise<T> {
  return new Promise((r) => setTimeout(() => r(v), ms));
}

export const kpiMock = {
  async listCycles() {
    return delay(read().cycles);
  },
  async listAssignments(cycleId: string) {
    return delay(read().assigns.filter((a) => a.cycleId === cycleId));
  },
  async selfAssess(assignmentId: string, payload: { diemTuDanhGia: number; tyLeHoanThanh: number; nhanXetNv: string }) {
    const s = read();
    const a = s.assigns.find((x) => x.assignmentId === assignmentId);
    if (!a) throw new Error("Not found");
    a.diemTuDanhGia = payload.diemTuDanhGia;
    a.trangThai = "NV_DA_TU_DANH_GIA";
    write(s);
    return delay(a);
  },
  async managerReview(assignmentId: string, payload: { diemManager: number; xepLoaiDeXuat: string; nhanXetManager: string }) {
    const s = read();
    const a = s.assigns.find((x) => x.assignmentId === assignmentId);
    if (!a) throw new Error("Not found");
    a.diemManager = payload.diemManager;
    a.diemTrungBinh = (a.diemTuDanhGia || 0) * 0.4 + payload.diemManager * 0.6;
    a.trangThai = "MANAGER_DA_REVIEW";
    write(s);
    return delay(a);
  },
  async hrApprove(assignmentId: string, payload: { diemCuoi: number; xepLoaiCuoi: string; heSoThuong: number }) {
    const s = read();
    const a = s.assigns.find((x) => x.assignmentId === assignmentId);
    if (!a) throw new Error("Not found");
    a.diemTrungBinh = payload.diemCuoi;
    a.xepLoaiCuoi = payload.xepLoaiCuoi;
    a.trangThai = "HR_DA_PHE_DUYET";
    write(s);
    return delay(a);
  },
};