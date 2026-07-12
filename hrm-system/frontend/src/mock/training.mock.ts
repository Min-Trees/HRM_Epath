// Mock layer for T20 - Training
const STORAGE_KEY = "training_v1";

interface Store {
  chuongTrinh: any[];
  lop: any[];
  dangKy: any[];
  danhGia: any[];
}

const seed: Store = {
  chuongTrinh: [
    { id: "ct-1", maChuongTrinh: "CT-001", tenChuongTrinh: "An toan lao dong", loaiChuongTrinh: "AN_TOAN_LAO_DONG",
      thoiLuongGio: 8, diemDanhGiaToiThieu: 60, chungChi: "ATLD", trangThai: "CONG_BO", moTa: "Dao tao bat buoc", mucTieu: "Trang bi kien thuc ATLD", nguoiTaoId: "u-1" },
    { id: "ct-2", maChuongTrinh: "CT-002", tenChuongTrinh: "Excel nang cao", loaiChuongTrinh: "KY_NANG_CHUYEN_MON",
      thoiLuongGio: 16, diemDanhGiaToiThieu: 70, chungChi: "", trangThai: "CONG_BO", moTa: "Pivot, VLOOKUP", mucTieu: "Excel chuyen nghiep", nguoiTaoId: "u-1" },
    { id: "ct-3", maChuongTrinh: "CT-003", tenChuongTrinh: "Ky nang lanh dao", loaiChuongTrinh: "QUAN_LY",
      thoiLuongGio: 24, diemDanhGiaToiThieu: 75, chungChi: "", trangThai: "NHAP", moTa: "Leadership", mucTieu: "Lanh dao cap trung", nguoiTaoId: "u-1" },
  ],
  lop: [
    { id: "lop-1", maLop: "L-001", chuongTrinhId: "ct-1", tenLop: "ATLD Khoi 5/2026",
      ngayBatDau: "2026-05-15", ngayKetThuc: "2026-05-16", soBuoi: 2, soChoToiDa: 30, diaDiem: "Phong 301",
      giangVien: "Nguyen Van A", chiPhiMoiNv: 500000, trangThai: "MO_DANG_KY", ghiChu: "", nguoiPhuTrachId: "u-1" },
    { id: "lop-2", maLop: "L-002", chuongTrinhId: "ct-2", tenLop: "Excel Thang 6",
      ngayBatDau: "2026-06-01", ngayKetThuc: "2026-06-05", soBuoi: 5, soChoToiDa: 20, diaDiem: "Phong 202",
      giangVien: "Tran Thi B", chiPhiMoiNv: 1500000, trangThai: "DANG_DIEN_RA", ghiChu: "", nguoiPhuTrachId: "u-2" },
  ],
  dangKy: [
    { id: "dk-1", lopHocId: "lop-1", nhanVienId: "nv-1", ngayDangKy: "2026-05-01T09:00", trangThai: "DA_CHAP_NHAN", lyDoDangKy: "Bat buoc theo quy dinh" },
    { id: "dk-2", lopHocId: "lop-1", nhanVienId: "nv-2", ngayDangKy: "2026-05-02T10:00", trangThai: "CHO_DUYET", lyDoDangKy: "Nang cao kien thuc" },
    { id: "dk-3", lopHocId: "lop-2", nhanVienId: "nv-3", ngayDangKy: "2026-05-25T08:30", trangThai: "DA_CHAP_NHAN", lyDoDangKy: "" },
  ],
  danhGia: [],
};

function ensure() { if (typeof window !== "undefined" && !window.localStorage.getItem(STORAGE_KEY)) window.localStorage.setItem(STORAGE_KEY, JSON.stringify(seed)); }
function read(): Store { ensure(); return JSON.parse(window.localStorage.getItem(STORAGE_KEY) || JSON.stringify(seed)); }
function write(s: Store) { window.localStorage.setItem(STORAGE_KEY, JSON.stringify(s)); }
function delay<T>(v: T, ms = 80) { return new Promise((r) => setTimeout(() => r(v), ms)); }

export const trainingMock = {
  async listChuongTrinh() { return delay(read().chuongTrinh); },
  async listLop() { return delay(read().lop); },
  async listDangKy() {
    const s = read();
    return delay(s.dangKy.map((d) => {
      const dg = s.danhGia.find((x) => x.dangKyId === d.id);
      return dg ? { ...d, diemTongKet: dg.diemTrungBinh, chungChiCap: dg.chungChiCap } : d;
    }));
  },

  async createChuongTrinh(form: any) {
    const s = read();
    if (s.chuongTrinh.find((c) => c.maChuongTrinh === form.maChuongTrinh)) throw new Error("Ma trung");
    s.chuongTrinh.push({ ...form, id: `ct-${Date.now()}`, trangThai: "NHAP", ngayTao: new Date().toISOString() });
    write(s);
    return delay(null);
  },
  async congBoChuongTrinh(id: string) {
    const s = read();
    const c = s.chuongTrinh.find((x) => x.id === id);
    if (c) c.trangThai = "CONG_BO";
    write(s); return delay(null);
  },

  async createLop(form: any) {
    const s = read();
    s.lop.push({ ...form, id: `lop-${Date.now()}`, trangThai: "MO_DANG_KY" });
    write(s); return delay(null);
  },
  async lopTransition(id: string, newState: string) {
    const s = read();
    const l = s.lop.find((x) => x.id === id);
    if (l) l.trangThai = newState;
    write(s); return delay(null);
  },

  async dangKy(form: any) {
    const s = read();
    if (s.dangKy.find((d) => d.lopHocId === form.lopHocId && d.nhanVienId === form.nhanVienId && d.trangThai !== "HUY")) {
      throw new Error("NV da dang ky lop nay");
    }
    const lop = s.lop.find((l) => l.id === form.lopHocId);
    if (lop && lop.trangThai !== "MO_DANG_KY") throw new Error("Lop khong con mo dang ky");
    s.dangKy.push({
      ...form, id: `dk-${Date.now()}`, trangThai: "CHO_DUYET", ngayDangKy: new Date().toISOString(),
    });
    write(s); return delay(null);
  },
  async duyetDangKy(id: string, quyetDinh: "DA_CHAP_NHAN" | "TU_CHOI") {
    const s = read();
    const d = s.dangKy.find((x) => x.id === id);
    if (d) {
      d.trangThai = quyetDinh;
      d.ngayDuyet = new Date().toISOString();
    }
    write(s); return delay(null);
  },

  async danhGia(dangKyId: string, diem: any) {
    const s = read();
    const dk = s.dangKy.find((d) => d.id === dangKyId);
    if (!dk) throw new Error("Dang ky khong ton tai");
    const lop = s.lop.find((l) => l.id === dk.lopHocId);
    const ct = lop ? s.chuongTrinh.find((c) => c.id === lop.chuongTrinhId) : null;
    const diemTB = (Number(diem.diemNoiDung) * 0.4) + (Number(diem.diemGiangVien) * 0.3) + (Number(diem.diemThucHanh) * 0.3);
    const nguong = ct?.diemDanhGiaToiThieu ?? 60;
    let ketQua = "TRUNG_BINH";
    if (diemTB >= 90) ketQua = "XUAT_SAC";
    else if (diemTB >= 75) ketQua = "TOT";
    else if (diemTB < nguong) ketQua = "YEU";

    s.danhGia.push({
      id: `dg-${Date.now()}`, dangKyId,
      ...diem, diemTrungBinh: diemTB, ketQua,
      chungChiCap: diemTB >= nguong && ct?.chungChi ? ct.chungChi : null,
      ngayDanhGia: new Date().toISOString(),
    });
    dk.diemTongKet = diemTB;
    if (diemTB >= nguong && ct?.chungChi) dk.chungChiCap = ct.chungChi;
    write(s); return delay(null);
  },
};