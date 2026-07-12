// Mock layer for T19 - Payroll Run
const STORAGE_KEY = "payroll_run_v1";

interface KyLinh {
  kyLinhId: string;
  maKyLinh: string;
  thang: number;
  nam: number;
  trangThai: string;
  tongNhanVien: number;
  tongThucLinh: number;
  tongBhxhNld: number;
  tongThueTncn: number;
  nguoiChayId?: string;
  nguoiDuyetCap1Id?: string;
  nguoiDuyetCap2Id?: string;
  ngayChay?: string;
  ngayDuyetCap1?: string;
  ngayDuyetCap2?: string;
  ngayChiTraThucTe?: string;
  fileZipUrl?: string;
  ghiChu?: string;
}

interface Store {
  kyLinh: KyLinh[];
}

const seed: Store = {
  kyLinh: [
    {
      kyLinhId: "kl-1", maKyLinh: "KL-00001", thang: 4, nam: 2026,
      trangThai: "DA_CHI_TRA",
      tongNhanVien: 100, tongThucLinh: 1_425_000_000, tongBhxhNld: 178_500_000, tongThueTncn: 50_000_000,
      nguoiChayId: "u-1", nguoiDuyetCap1Id: "u-2", nguoiDuyetCap2Id: "u-3",
      ngayChay: "2026-04-25T10:00", ngayDuyetCap1: "2026-04-26T09:00",
      ngayDuyetCap2: "2026-04-27T11:00", ngayChiTraThucTe: "2026-04-30T14:00",
      fileZipUrl: "https://example.com/transfer-042026.zip",
    },
    {
      kyLinhId: "kl-2", maKyLinh: "KL-00002", thang: 5, nam: 2026,
      trangThai: "DA_DUYET_CAP_2",
      tongNhanVien: 102, tongThucLinh: 1_480_000_000, tongBhxhNld: 184_500_000, tongThueTncn: 53_000_000,
      nguoiChayId: "u-1", nguoiDuyetCap1Id: "u-2", nguoiDuyetCap2Id: "u-3",
      ngayChay: "2026-05-25T10:00", ngayDuyetCap1: "2026-05-26T09:00",
      ngayDuyetCap2: "2026-05-27T11:00",
    },
  ],
};

function ensure() { if (typeof window !== "undefined" && !window.localStorage.getItem(STORAGE_KEY)) window.localStorage.setItem(STORAGE_KEY, JSON.stringify(seed)); }
function read(): Store { ensure(); return JSON.parse(window.localStorage.getItem(STORAGE_KEY) || JSON.stringify(seed)); }
function write(s: Store) { window.localStorage.setItem(STORAGE_KEY, JSON.stringify(s)); }
function delay<T>(v: T, ms = 80) { return new Promise((r) => setTimeout(() => r(v), ms)); }

export const payrollRunMock = {
  async list() { return delay(read().kyLinh); },
  async findById(id: string) { return delay(read().kyLinh.find((k) => k.kyLinhId === id)); },
  async create(thang: number, nam: number) {
    const s = read();
    if (s.kyLinh.find((k) => k.thang === thang && k.nam === nam)) {
      throw new Error(`Ky luong ${thang}/${nam} da ton tai`);
    }
    const k: KyLinh = {
      kyLinhId: `kl-${Date.now()}`,
      maKyLinh: `KL-${String(Date.now()).slice(-5)}`,
      thang, nam,
      trangThai: "CHO_CHAY",
      tongNhanVien: 0, tongThucLinh: 0, tongBhxhNld: 0, tongThueTncn: 0,
    };
    s.kyLinh.push(k);
    write(s);
    return delay(k);
  },
  async transition(id: string, action: string) {
    const s = read();
    const k = s.kyLinh.find((x) => x.kyLinhId === id);
    if (!k) throw new Error("Not found");
    const now = new Date().toISOString();
    const transitions: Record<string, [string, string, string?]> = {
      "start": ["DA_CHAY", "ngayChay"],
      "approve-cap-1": ["DA_DUYET_CAP_1", "ngayDuyetCap1"],
      "approve-cap-2": ["DA_DUYET_CAP_2", "ngayDuyetCap2"],
      "pay-paid": ["DA_CHI_TRA", "ngayChiTraThucTe"],
      "cancel": ["HUY", ""],
    };
    const t = transitions[action];
    if (!t) throw new Error("Unknown action");
    const [newState, tsField] = t;
    k.trangThai = newState;
    if (tsField) (k as any)[tsField] = now;
    if (action === "start") {
      // gia lap tinh toan
      k.tongNhanVien = 102;
      k.tongThucLinh = 1_480_000_000;
      k.tongBhxhNld = 184_500_000;
      k.tongThueTncn = 53_000_000;
    }
    if (action === "pay-paid") {
      k.fileZipUrl = `https://example.com/transfer-${k.thang}${k.nam}.zip`;
    }
    write(s);
    return delay(k);
  },
  renderPayslipHtml(kyLinhId: string, nvId: string): string {
    const s = read();
    const k = s.kyLinh.find((x) => x.kyLinhId === kyLinhId);
    if (!k) return "<p>Not found</p>";
    return `<html><head><meta charset="UTF-8"><title>Phieu luong</title>
      <style>body{font-family:sans-serif;padding:30px;max-width:760px;margin:auto;}
      h1{text-align:center;}table{width:100%;border-collapse:collapse;}
      th,td{border:1px solid #cbd5e1;padding:8px;text-align:left;}
      th{background:#f1f5f9;}.right{text-align:right;}
      .total{font-weight:bold;background:#fef9c3;}</style></head><body>
      <h1>PHIEU LUONG (Mock)</h1>
      <p>Ky: ${String(k.thang).padStart(2, "0")}/${k.nam} - NV: ${nvId}</p>
      <p>Trang thai ky: <b>${k.trangThai}</b></p>
      <h3>I. Cac khoan thu</h3>
      <table><tr><th>Khoan</th><th class="right">So tien (VND)</th></tr>
      <tr><td>Luong co ban</td><td class="right">15,000,000</td></tr>
      <tr><td>Phu cap</td><td class="right">2,000,000</td></tr>
      <tr><td>OT</td><td class="right">1,500,000</td></tr>
      <tr class="total"><td>Tong thu</td><td class="right">18,500,000</td></tr></table>
      <h3>II. Cac khoan tru</h3>
      <table><tr><th>Khoan</th><th class="right">So tien (VND)</th></tr>
      <tr><td>BHXH NLD (10.5%)</td><td class="right">1,785,000</td></tr>
      <tr><td>Thue TNCN</td><td class="right">500,000</td></tr>
      <tr><td>Tam ung</td><td class="right">2,000,000</td></tr>
      <tr class="total"><td>Tong tru</td><td class="right">4,285,000</td></tr></table>
      <h3 style="color:#dc2626">THUC LINH: 14,215,000 VND</h3>
      <p style="margin-top:40px;font-size:12px;color:#64748b">Mock payslip - thay the se sinh tu bang_luong_thang thuc te.</p>
      </body></html>`;
  },
};