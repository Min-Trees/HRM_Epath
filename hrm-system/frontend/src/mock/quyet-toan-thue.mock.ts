// Mock layer for T16 - Quyet toan thue TNCN
const STORAGE_KEY = "qtt_2026_v1";

interface MonthlyRow {
  thang: number;
  thuNhapChiuThue: number;
  thueDaKhauTru: number;
  giamTruBanThan: number;
  giamTruNguoiPhuThuoc: number;
}

const SEED: Record<string, MonthlyRow[]> = {
  "NV-001": Array.from({ length: 12 }, (_, i) => ({
    thang: i + 1,
    thuNhapChiuThue: 7_000_000 + i * 100_000,
    thueDaKhauTru: 350_000 + i * 10_000,
    giamTruBanThan: 11_000_000,
    giamTruNguoiPhuThuoc: 0,
  })),
  "NV-002": Array.from({ length: 12 }, (_, i) => ({
    thang: i + 1,
    thuNhapChiuThue: 15_000_000 + i * 200_000,
    thueDaKhauTru: 1_400_000 + i * 25_000,
    giamTruBanThan: 11_000_000,
    giamTruNguoiPhuThuoc: 4_400_000,
  })),
};

function delay<T>(v: T, ms = 100): Promise<T> {
  return new Promise((r) => setTimeout(() => r(v), ms));
}

function ensure() {
  if (typeof window === "undefined") return;
  if (!window.localStorage.getItem(STORAGE_KEY)) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(SEED));
  }
}

function build02Xml(r: any): string {
  const sb: string[] = [];
  sb.push('<?xml version="1.0" encoding="UTF-8"?>');
  sb.push('<QUYET_TOAN_02_QTT xmlns="http://gdt.gov.vn/schema/02qtt">');
  sb.push("  <THONG_TIN_CHUNG>");
  kv(sb, "NAM", String(r.nam));
  kv(sb, "MA_DON_VI", r.maDonVi);
  kv(sb, "TEN_DON_VI", r.tenDonVi);
  kv(sb, "MA_SO_THUE", r.maSoThue);
  sb.push("  </THONG_TIN_CHUNG>");
  sb.push("  <TONG_HOP>");
  kv(sb, "TONG_SO_NHAN_VIEN", String(r.tongSoNhanVien));
  kv(sb, "TONG_NV_UY_QUYEN", String(r.tongNhanVienUyQuyen));
  kv(sb, "TONG_NV_TU_QTT", String(r.tongNhanVienTuQtt));
  kv(sb, "TONG_THU_NHAP_CHIU_THUE", num(r.tongThuNhapChiuThue));
  kv(sb, "TONG_THUE_DA_KHAU_TRU", num(r.tongThueDaKhauTru));
  kv(sb, "TONG_THUE_PHAI_NOP", num(r.tongThuePhaiNop));
  sb.push("  </TONG_HOP>");
  sb.push("</QUYET_TOAN_02_QTT>");
  return sb.join("\n");
}

function build05Xml(r: any): string {
  const sb: string[] = [];
  sb.push('<?xml version="1.0" encoding="UTF-8"?>');
  sb.push('<QUYET_TOAN_05_QTT xmlns="http://gdt.gov.vn/schema/05qtt">');
  sb.push("  <THONG_TIN_NHAN_VIEN>");
  kv(sb, "NAM", String(r.nam));
  kv(sb, "MA_NV", r.maNv);
  kv(sb, "HO_TEN", r.hoTen);
  kv(sb, "MA_SO_THUE", r.maSoThue || "");
  kv(sb, "CMND", r.cmnd || "");
  kv(sb, "LOAI_CAM_KET_08", r.loaiCamKet08 || "CHUA_CO");
  kv(sb, "SO_NGUOI_PHU_THUOC", String(r.soNguoiPhuThuoc || 0));
  kv(sb, "GIAM_TRU_BAN_THAN", num(r.giamTruBanThan));
  kv(sb, "GIAM_TRU_NGUOI_PHU_THUOC", num(r.giamTruNguoiPhuThuoc));
  sb.push("  </THONG_TIN_NHAN_VIEN>");
  sb.push("  <CHI_TIET_THANG>");
  (r.chiTietThang || []).forEach((m: MonthlyRow) => {
    sb.push(`    <THANG THANG_TT="${m.thang}">`);
    kv(sb, "THU_NHAP_CHIU_THUE", num(m.thuNhapChiuThue));
    kv(sb, "THUE_DA_KHAU_TRU", num(m.thueDaKhauTru));
    sb.push("    </THANG>");
  });
  sb.push("  </CHI_TIET_THANG>");
  sb.push("  <TONG_KET>");
  kv(sb, "TONG_THU_NHAP_CA_NAM", num(r.tongThuNhapCaNam));
  kv(sb, "TONG_THUE_DA_KHAU_TRU", num(r.tongThueDaKhauTru));
  kv(sb, "TONG_THUE_PHAI_NOP", num(r.tongThuePhaiNop));
  sb.push("  </TONG_KET>");
  sb.push("</QUYET_TOAN_05_QTT>");
  return sb.join("\n");
}

function kv(sb: string[], key: string, value: string) {
  sb.push(`    <${key}>${xmlEsc(value)}</${key}>`);
}
function num(v: any): string {
  if (v === null || v === undefined) return "0";
  return String(v);
}
function xmlEsc(s: string): string {
  return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

function sum(arr: MonthlyRow[], key: keyof MonthlyRow): number {
  return arr.reduce((acc, m) => acc + (m[key] as number), 0);
}

export const quyetToanThueMock = {
  async generate02QTT(nam: number, maDonVi: string, tenDonVi: string, maSoThue: string) {
    ensure();
    const data: Record<string, MonthlyRow[]> = JSON.parse(window.localStorage.getItem(STORAGE_KEY) || "{}");
    const allEmployees = Object.keys(data);

    const top10: any[] = [];
    let tongTNCT = 0;
    let tongThue = 0;
    let tongGiamBanThan = 0;
    let tongGiamNPT = 0;

    for (const maNv of allEmployees) {
      const rows = data[maNv];
      const tnct = sum(rows, "thuNhapChiuThue");
      const thue = sum(rows, "thueDaKhauTru");
      const gtbt = sum(rows, "giamTruBanThan");
      const gtnpt = sum(rows, "giamTruNguoiPhuThuoc");

      tongTNCT += tnct;
      tongThue += thue;
      tongGiamBanThan += gtbt;
      tongGiamNPT += gtnpt;

      top10.push({
        maNv,
        hoTen: maNv === "NV-001" ? "Nguyen Van A" : "Tran Thi B",
        maSoThue: maNv === "NV-001" ? "0123456789-001" : "0123456789-002",
        thuNhapChiuThue: tnct,
        thueDaKhauTru: thue,
        thuePhaiNop: thue,
      });
    }
    top10.sort((a, b) => b.thuNhapChiuThue - a.thuNhapChiuThue);

    return delay({
      nam,
      maDonVi,
      tenDonVi,
      maSoThue,
      nguoiLap: "HR-Admin",
      ngayLap: new Date().toISOString(),
      tongSoNhanVien: allEmployees.length,
      tongNhanVienUyQuyen: allEmployees.length,
      tongNhanVienTuQtt: 0,
      tongThuNhapChiuThue: tongTNCT,
      tongGiamTruBanThan: tongGiamBanThan,
      tongGiamTruNguoiPhuThuoc: tongGiamNPT,
      tongThueDaKhauTru: tongThue,
      tongThuePhaiNopThem: 0,
      tongThueDuocHoan: 0,
      tongThuePhaiNop: tongThue,
      top10NhanVienThueCao: top10.slice(0, 10),
    });
  },

  async generate05QTT(nam: number, maNv: string) {
    ensure();
    const data: Record<string, MonthlyRow[]> = JSON.parse(window.localStorage.getItem(STORAGE_KEY) || "{}");
    const rows = data[maNv] || [];

    const tnct = sum(rows, "thuNhapChiuThue");
    const thue = sum(rows, "thueDaKhauTru");
    const gtbt = sum(rows, "giamTruBanThan");
    const gtnpt = sum(rows, "giamTruNguoiPhuThuoc");

    return delay({
      nam,
      maNv,
      hoTen: maNv === "NV-001" ? "Nguyen Van A" : "Tran Thi B",
      maSoThue: maNv === "NV-001" ? "0123456789-001" : "0123456789-002",
      cmnd: "0123456789",
      diaChi: "Ha Noi",
      ngaySinh: "1990-01-15",
      soSoBHXH: "BH00001",
      loaiCamKet08: "UY_QUYEN_QTT",
      soNguoiPhuThuoc: maNv === "NV-002" ? 1 : 0,
      giamTruBanThan: gtbt,
      giamTruNguoiPhuThuoc: gtnpt,
      tenDonVi: "Cong ty TNHH ABC",
      maSoThueDonVi: "0123456789",
      chiTietThang: rows,
      tongThuNhapCaNam: tnct + gtbt + gtnpt,
      tongThuNhapChiuThue: tnct,
      tongThueDaKhauTru: thue,
      tongThuePhaiNop: thue,
      thueDuocHoan: 0,
    });
  },

  async exportXml02QTT(nam: number, maDonVi: string, tenDonVi: string, maSoThue: string) {
    const r = await this.generate02QTT(nam, maDonVi, tenDonVi, maSoThue);
    return delay(build02Xml(r));
  },

  async exportXml05QTT(nam: number, maNv: string) {
    const r = await this.generate05QTT(nam, maNv);
    return delay(build05Xml(r));
  },
};