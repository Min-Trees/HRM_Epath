// Mock layer for T17 - Recruitment
const STORAGE_KEY = "recruitment_v1";

interface YC {
  yeuCauId: string;
  maYeuCau: string;
  tieuDe: string;
  phongBanId: string;
  nguoiYeuCauId: string;
  soLuongCan: number;
  trangThai: string;
  soUngVien: number;
}

interface UV {
  ungVienId: string;
  maUngVien: string;
  hoTen: string;
  email: string;
  soDienThoai: string;
  soNamKinhNghiem: number;
  trangThai: string;
  yeuCauId?: string;
}

interface LichPV {
  lichPvId: string;
  ungVienId: string;
  vongPhongVan: number;
  thoiGianBatDau: string;
  thoiGianKetThuc: string;
  diaDiem: string;
  hinhThuc: string;
  trangThai: string;
}

interface QD {
  quyetDinhId: string;
  ungVienId: string;
  loaiHopDong: string;
  mucLuongDeNghi: number;
  ngayVaoLamDeNghi: string;
  phongBanId: string;
  chucDanh: string;
  trangThai: string;
}

interface Store {
  yeuCau: YC[];
  ungVien: UV[];
  lichPV: LichPV[];
  quyetDinh: QD[];
}

const seed: Store = {
  yeuCau: [
    { yeuCauId: "yc-1", maYeuCau: "YC-00001", tieuDe: "Tuyen Senior Backend Dev", phongBanId: "pb-1", nguoiYeuCauId: "nv-1", soLuongCan: 2, trangThai: "DANG_TUYEN", soUngVien: 3 },
    { yeuCauId: "yc-2", maYeuCau: "YC-00002", tieuDe: "Tuyen HR Executive", phongBanId: "pb-2", nguoiYeuCauId: "nv-2", soLuongCan: 1, trangThai: "CHO_PHE_DUYET", soUngVien: 0 },
    { yeuCauId: "yc-3", maYeuCau: "YC-00003", tieuDe: "Tuyen Marketing Intern", phongBanId: "pb-3", nguoiYeuCauId: "nv-1", soLuongCan: 1, trangThai: "DA_DONG", soUngVien: 1 },
  ],
  ungVien: [
    { ungVienId: "uv-1", maUngVien: "UV-00001", hoTen: "Nguyen Van A", email: "a@gmail.com", soDienThoai: "0901234567", soNamKinhNghiem: 5, trangThai: "DE_NGHI_TUYEN", yeuCauId: "yc-1" },
    { ungVienId: "uv-2", maUngVien: "UV-00002", hoTen: "Tran Thi B", email: "b@gmail.com", soDienThoai: "0902345678", soNamKinhNghiem: 3, trangThai: "CHO_PHONG_VAN_VONG_2", yeuCauId: "yc-1" },
    { ungVienId: "uv-3", maUngVien: "UV-00003", hoTen: "Le Van C", email: "c@gmail.com", soDienThoai: "0903456789", soNamKinhNghiem: 2, trangThai: "TU_CHOI", yeuCauId: "yc-1" },
    { ungVienId: "uv-4", maUngVien: "UV-00004", hoTen: "Pham Thi D", email: "d@gmail.com", soDienThoai: "0904567890", soNamKinhNghiem: 1, trangThai: "DA_TUYEN", yeuCauId: "yc-3" },
  ],
  lichPV: [
    { lichPvId: "lpv-1", ungVienId: "uv-1", vongPhongVan: 1, thoiGianBatDau: "2026-03-15T09:00", thoiGianKetThuc: "2026-03-15T10:00", diaDiem: "Phong hop A", hinhThuc: "TRUC_TIEP", trangThai: "HOAN_THANH" },
    { lichPvId: "lpv-2", ungVienId: "uv-1", vongPhongVan: 2, thoiGianBatDau: "2026-03-20T14:00", thoiGianKetThuc: "2026-03-20T15:00", diaDiem: "Phong hop B", hinhThuc: "ONLINE", trangThai: "HOAN_THANH" },
    { lichPvId: "lpv-3", ungVienId: "uv-2", vongPhongVan: 1, thoiGianBatDau: "2026-03-15T10:30", thoiGianKetThuc: "2026-03-15T11:30", diaDiem: "Phong hop A", hinhThuc: "TRUC_TIEP", trangThai: "HOAN_THANH" },
    { lichPvId: "lpv-4", ungVienId: "uv-2", vongPhongVan: 2, thoiGianBatDau: "2026-03-22T09:00", thoiGianKetThuc: "2026-03-22T10:00", diaDiem: "Phong hop B", hinhThuc: "TRUC_TIEP", trangThai: "CHUA_DIEN_RA" },
  ],
  quyetDinh: [
    { quyetDinhId: "qd-1", ungVienId: "uv-1", loaiHopDong: "CHINH_THUC", mucLuongDeNghi: 25_000_000, ngayVaoLamDeNghi: "2026-04-01", phongBanId: "pb-1", chucDanh: "Senior Backend", trangThai: "DA_DONG_Y" },
    { quyetDinhId: "qd-2", ungVienId: "uv-4", loaiHopDong: "CONG_TAC_VIEN", mucLuongDeNghi: 5_000_000, ngayVaoLamDeNghi: "2026-03-01", phongBanId: "pb-3", chucDanh: "Marketing Intern", trangThai: "DA_DONG_Y" },
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

function delay<T>(v: T, ms = 80): Promise<T> {
  return new Promise((r) => setTimeout(() => r(v), ms));
}

export const recruitmentMock = {
  async listYeuCau() {
    return delay(read().yeuCau);
  },
  async listUngVien() {
    return delay(read().ungVien);
  },
  async listUngVienTheoYeuCau(yeuCauId: string) {
    return delay(read().ungVien.filter((u) => u.yeuCauId === yeuCauId));
  },
  async listLichPV(ungVienId: string) {
    return delay(read().lichPV.filter((l) => l.ungVienId === ungVienId));
  },
  async listQuyetDinh(ungVienId: string) {
    return delay(read().quyetDinh.filter((q) => q.ungVienId === ungVienId));
  },
};