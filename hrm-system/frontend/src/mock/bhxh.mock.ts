// Mock layer for T15 - BHXH Reports
// Generates D02-LT / D03-LT report + XML preview, mô phỏng backend behavior.

const STORAGE_KEY = "bhxh_reports_v1";

interface D02Row {
  nhanVienId: string;
  maNv: string;
  hoTen: string;
  soCmnd: string;
  ngaySinh: string;
  gioiTinh: string;
  maSoBhxh: string;
  loaiBienDong: string;
  lyDoBienDong: string;
  ngayPhatSinh: string;
  mucLuongDong: number;
  tyLeNld: number;
  tyLeDn: number;
  trangThaiNop: string;
}

interface D03Row {
  nhanVienId: string;
  maNv: string;
  hoTen: string;
  ngaySinh: string;
  soCmnd: string;
  maSoBhxh: string;
  ngayCapSo: string;
  loaiDeNghi: string;
  lyDo: string;
  trangThaiNop: string;
}

const SEED_D02: D02Row[] = [
  { nhanVienId: "nv-1", maNv: "NV-001", hoTen: "Nguyen Van A", soCmnd: "0123456789", ngaySinh: "1990-01-15", gioiTinh: "NAM", maSoBhxh: "BH00001", loaiBienDong: "TANG", lyDoBienDong: "MOI_TUYEN_DUNG", ngayPhatSinh: "2026-01-10", mucLuongDong: 10_000_000, tyLeNld: 10.5, tyLeDn: 21.5, trangThaiNop: "CHUA_NOP" },
  { nhanVienId: "nv-2", maNv: "NV-002", hoTen: "Tran Thi B", soCmnd: "0234567890", ngaySinh: "1992-05-20", gioiTinh: "NU", maSoBhxh: "BH00002", loaiBienDong: "GIAM", lyDoBienDong: "NGHI_VIEC", ngayPhatSinh: "2026-01-15", mucLuongDong: 12_000_000, tyLeNld: 10.5, tyLeDn: 21.5, trangThaiNop: "CHUA_NOP" },
  { nhanVienId: "nv-3", maNv: "NV-003", hoTen: "Le Van C", soCmnd: "0345678901", ngaySinh: "1988-12-05", gioiTinh: "NAM", maSoBhxh: "BH00003", loaiBienDong: "GIAM", lyDoBienDong: "HET_HAN_HDLD", ngayPhatSinh: "2026-01-20", mucLuongDong: 8_500_000, tyLeNld: 10.5, tyLeDn: 21.5, trangThaiNop: "DA_NOP" },
];

const SEED_D03: D03Row[] = [
  { nhanVienId: "nv-1", maNv: "NV-001", hoTen: "Nguyen Van A", ngaySinh: "1990-01-15", soCmnd: "0123456789", maSoBhxh: "BH00001", ngayCapSo: "2026-01-10", loaiDeNghi: "CAP_MOI", lyDo: "Tuyen dung moi", trangThaiNop: "CHUA_NOP" },
];

function delay<T>(value: T, ms = 100): Promise<T> {
  return new Promise((r) => setTimeout(() => r(value), ms));
}

function buildD02Xml(report: any): string {
  const sb: string[] = [];
  sb.push('<?xml version="1.0" encoding="UTF-8"?>');
  sb.push("<BAO_CAO_D02_LT xmlns=\"http://baohiemxahoi.gov.vn/schema/d02-lt\">");
  sb.push("  <THONG_TIN_CHUNG>");
  kv(sb, "MA_DON_VI", report.maDonViBHXH);
  kv(sb, "TEN_DON_VI", report.tenDonVi);
  kv(sb, "TU_NGAY", report.tuNgay);
  kv(sb, "DEN_NGAY", report.denNgay);
  sb.push(`    <TONG_SO_DONG>${report.tongSoDong}</TONG_SO_DONG>`);
  sb.push("  </THONG_TIN_CHUNG>");
  sb.push("  <DANH_SACH>");
  (report.rows || []).forEach((r: any, idx: number) => {
    sb.push(`    <ROWDATA STT="${idx + 1}">`);
    kv(sb, "MA_NV", r.maNv);
    kv(sb, "HO_TEN", r.hoTen);
    kv(sb, "SO_CMND", r.soCmnd);
    kv(sb, "NGAY_SINH", r.ngaySinh);
    kv(sb, "GIOI_TINH", r.gioiTinh);
    kv(sb, "MA_SO_BHXH", r.maSoBhxh);
    kv(sb, "LOAI_BAO", r.loaiBienDong);
    kv(sb, "LY_DO", r.lyDoBienDong);
    kv(sb, "NGAY_PHAT_SINH", r.ngayPhatSinh);
    kv(sb, "MUC_LUONG_DONG", r.mucLuongDong?.toString());
    kv(sb, "TY_LE_NLD", r.tyLeNld?.toString());
    kv(sb, "TY_LE_DN", r.tyLeDn?.toString());
    kv(sb, "TRANG_THAI_NOP", r.trangThaiNop);
    sb.push("    </ROWDATA>");
  });
  sb.push("  </DANH_SACH>");
  sb.push("</BAO_CAO_D02_LT>");
  return sb.join("\n");
}

function buildD03Xml(report: any): string {
  const sb: string[] = [];
  sb.push('<?xml version="1.0" encoding="UTF-8"?>');
  sb.push("<BAO_CAO_D03_LT xmlns=\"http://baohiemxahoi.gov.vn/schema/d03-lt\">");
  sb.push("  <THONG_TIN_CHUNG>");
  kv(sb, "MA_DON_VI", report.maDonViBHXH);
  kv(sb, "TEN_DON_VI", report.tenDonVi);
  kv(sb, "TU_NGAY", report.tuNgay);
  kv(sb, "DEN_NGAY", report.denNgay);
  sb.push(`    <TONG_SO_DONG>${report.tongSoDong}</TONG_SO_DONG>`);
  sb.push("  </THONG_TIN_CHUNG>");
  sb.push("  <DANH_SACH>");
  (report.rows || []).forEach((r: any, idx: number) => {
    sb.push(`    <ROWDATA STT="${idx + 1}">`);
    kv(sb, "MA_NV", r.maNv);
    kv(sb, "HO_TEN", r.hoTen);
    kv(sb, "NGAY_SINH", r.ngaySinh);
    kv(sb, "MA_SO_BHXH", r.maSoBhxh);
    kv(sb, "NGAY_CAP_SO", r.ngayCapSo);
    kv(sb, "LOAI_DE_NGHI", r.loaiDeNghi);
    kv(sb, "LY_DO", r.lyDo);
    sb.push("    </ROWDATA>");
  });
  sb.push("  </DANH_SACH>");
  sb.push("</BAO_CAO_D03_LT>");
  return sb.join("\n");
}

function kv(sb: string[], key: string, value: string | null | undefined) {
  sb.push(`    <${key}>${xmlEsc(value)}</${key}>`);
}

function xmlEsc(s: any): string {
  if (s === null || s === undefined) return "";
  return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

function ensureSeed() {
  if (typeof window === "undefined") return;
  if (!window.localStorage.getItem(STORAGE_KEY)) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify({ d02: SEED_D02, d03: SEED_D03 }));
  }
}

export const bhxhReportMock = {
  async generateD02LT(tuNgay: string, denNgay: string, maDonViBHXH: string, tenDonVi: string) {
    ensureSeed();
    const cached = typeof window !== "undefined" ? JSON.parse(window.localStorage.getItem(STORAGE_KEY) || "{}") : { d02: SEED_D02 };
    const rows = (cached.d02 || SEED_D02).filter((r: D02Row) => r.ngayPhatSinh >= tuNgay && r.ngayPhatSinh <= denNgay);
    return delay({
      tuNgay, denNgay, maDonViBHXH, tenDonVi,
      nguoiLap: "HR-Admin",
      ngayLap: new Date().toISOString(),
      tongSoDong: rows.length,
      rows,
    });
  },

  async generateD03LT(tuNgay: string, denNgay: string, maDonViBHXH: string, tenDonVi: string) {
    ensureSeed();
    const cached = typeof window !== "undefined" ? JSON.parse(window.localStorage.getItem(STORAGE_KEY) || "{}") : { d03: SEED_D03 };
    const rows = (cached.d03 || SEED_D03).filter((r: D03Row) => r.ngayCapSo >= tuNgay && r.ngayCapSo <= denNgay);
    return delay({
      tuNgay, denNgay, maDonViBHXH, tenDonVi,
      ngayLap: new Date().toISOString(),
      tongSoDong: rows.length,
      rows,
    });
  },

  async exportXmlD02LT(tuNgay: string, denNgay: string, maDonVi: string, tenDonVi: string) {
    const r = await this.generateD02LT(tuNgay, denNgay, maDonVi, tenDonVi);
    return delay(buildD02Xml(r));
  },

  async exportXmlD03LT(tuNgay: string, denNgay: string, maDonVi: string, tenDonVi: string) {
    const r = await this.generateD03LT(tuNgay, denNgay, maDonVi, tenDonVi);
    return delay(buildD03Xml(r));
  },
};
