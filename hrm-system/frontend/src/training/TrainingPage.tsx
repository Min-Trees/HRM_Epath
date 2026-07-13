import React, { useEffect, useState } from "react";
import { trainingMock } from "../mock/training.mock";

const STATUS_COLORS: Record<string, string> = {
  // Chuong trinh
  NHAP: "#94a3b8", CONG_BO: "#0d9488", NGUNG: "#ef4444",
  // Lop
  MO_DANG_KY: "#10b981", DONG_DANG_KY: "#f59e0b", DANG_DIEN_RA: "#3b82f6", HOAN_THANH: "#059669", HUY: "#ef4444",
  // Dang ky
  CHO_DUYET: "#94a3b8", DA_CHAP_NHAN: "#10b981", TU_CHOI: "#ef4444",
  // Diem danh
  CHUA_DI_HOC: "#94a3b8", CO_MAT: "#10b981", VANG: "#ef4444", VANG_CO_PHEP: "#f59e0b",
  // Ket qua
  XUAT_SAC: "#059669", TOT: "#10b981", TRUNG_BINH: "#3b82f6", YEU: "#ef4444", KHONG_DANH_GIA: "#94a3b8",
};

const LOAI_OPTIONS = ["KY_NANG_MEMM","KY_NANG_CHUYEN_MON","AN_TOAN_LAO_DONG","LANG_DAO_VAN_HOA","QUAN_LY","CHUNG_CHI_BAT_BUOC","KHAC"];

export default function TrainingPage() {
  const [tab, setTab] = useState<"ct" | "lop" | "dk" | "dg">("ct");
  const [chuongTrinh, setChuongTrinh] = useState<any[]>([]);
  const [lopHoc, setLopHoc] = useState<any[]>([]);
  const [dangKy, setDangKy] = useState<any[]>([]);
  const [showCtForm, setShowCtForm] = useState(false);
  const [showLopForm, setShowLopForm] = useState(false);
  const [showDkForm, setShowDkForm] = useState(false);
  const [showDgForm, setShowDgForm] = useState<string | null>(null);
  const [selectedCt, setSelectedCt] = useState<any>(null);
  const [selectedLop, setSelectedLop] = useState<any>(null);

  const refresh = async () => {
    const [ct, lop, dk] = await Promise.all([
      trainingMock.listChuongTrinh() as Promise<any[]>,
      trainingMock.listLop() as Promise<any[]>,
      trainingMock.listDangKy() as Promise<any[]>,
    ]);
    setChuongTrinh(ct);
    setLopHoc(lop);
    setDangKy(dk);
  };
  useEffect(() => { refresh(); }, []);

  const ctCreate = async (form: any) => {
    await trainingMock.createChuongTrinh(form);
    setShowCtForm(false); await refresh();
  };
  const lopCreate = async (form: any) => {
    await trainingMock.createLop(form);
    setShowLopForm(false); await refresh();
  };
  const ctPublish = async (id: string) => {
    await trainingMock.congBoChuongTrinh(id);
    await refresh();
  };
  const lopTransition = async (id: string, s: string) => {
    await trainingMock.lopTransition(id, s);
    await refresh();
  };
  const dkCreate = async (form: any) => {
    try {
      await trainingMock.dangKy(form);
      setShowDkForm(false); await refresh();
    } catch (e: any) { alert(e.message); }
  };
  const dkDuyet = async (id: string, q: "DA_CHAP_NHAN" | "TU_CHOI") => {
    await trainingMock.duyetDangKy(id, q);
    await refresh();
  };
  const dgSubmit = async (dkId: string, diem: any) => {
    try {
      await trainingMock.danhGia(dkId, diem);
      setShowDgForm(null); await refresh();
    } catch (e: any) { alert(e.message); }
  };

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <h2>Module Đào tạo (T20)</h2>
      <p style={{ color: "#64748b" }}>Chương trình → Lớp học → Đăng ký → Điểm danh → Đánh giá.</p>

      <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
        <Tab active={tab === "ct"} onClick={() => setTab("ct")}>Chương trình ({chuongTrinh.length})</Tab>
        <Tab active={tab === "lop"} onClick={() => setTab("lop")}>Lớp học ({lopHoc.length})</Tab>
        <Tab active={tab === "dk"} onClick={() => setTab("dk")}>Đăng ký ({dangKy.length})</Tab>
        <Tab active={tab === "dg"} onClick={() => setTab("dg")}>Đánh giá</Tab>
      </div>

      {tab === "ct" && (
        <Section>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
            <h3>Danh sách chương trình</h3>
            <button onClick={() => setShowCtForm(true)} style={btnPrimary}>+ Tạo chương trình</button>
          </div>
          <table style={table}>
            <thead>
              <tr style={thead}>
                <th style={th}>Mã</th>
                <th style={th}>Tên chương trình</th>
                <th style={th}>Loại</th>
                <th style={th}>Giờ</th>
                <th style={th}>Ngưỡng đậu</th>
                <th style={th}>Trạng thái</th>
                <th style={th}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {chuongTrinh.map((c) => (
                <tr key={c.id} style={tr}>
                  <td style={td}>{c.maChuongTrinh}</td>
                  <td style={td}>{c.tenChuongTrinh}</td>
                  <td style={td}>{c.loaiChuongTrinh}</td>
                  <td style={td}>{c.thoiLuongGio}h</td>
                  <td style={td}>{c.diemDanhGiaToiThieu}</td>
                  <td style={td}>
                    <span style={{ ...chip, background: STATUS_COLORS[c.trangThai] }}>{c.trangThai}</span>
                  </td>
                  <td style={td}>
                    {c.trangThai === "NHAP" && <button onClick={() => ctPublish(c.id)} style={{ ...btn, background: "#0d9488" }}>📣 Công bố</button>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {showCtForm && <ChuongTrinhForm onSubmit={ctCreate} onCancel={() => setShowCtForm(false)} />}
        </Section>
      )}

      {tab === "lop" && (
        <Section>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
            <h3>Danh sách lớp học</h3>
            <button onClick={() => setShowLopForm(true)} disabled={chuongTrinh.filter((c) => c.trangThai === "CONG_BO").length === 0}
                    style={btnPrimary}>+ Tạo lớp</button>
          </div>
          <table style={table}>
            <thead><tr style={thead}>
              <th style={th}>Mã lớp</th><th style={th}>Tên lớp</th><th style={th}>Chương trình</th>
              <th style={th}>Thời gian</th><th style={th}>Sĩ số</th><th style={th}>Trạng thái</th><th style={th}>Hành động</th>
            </tr></thead>
            <tbody>
              {lopHoc.map((l) => {
                const ct = chuongTrinh.find((c) => c.id === l.chuongTrinhId);
                return (
                  <tr key={l.id} style={tr}>
                    <td style={td}>{l.maLop}</td>
                    <td style={td}>{l.tenLop}</td>
                    <td style={td}>{ct?.tenChuongTrinh || "?"}</td>
                    <td style={td}>{l.ngayBatDau} → {l.ngayKetThuc}</td>
                    <td style={td}>{l.soChoToiDa}</td>
                    <td style={td}><span style={{ ...chip, background: STATUS_COLORS[l.trangThai] }}>{l.trangThai}</span></td>
                    <td style={td}>{renderLopActions(l, lopTransition)}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {showLopForm && <LopForm chuongTrinh={chuongTrinh.filter((c) => c.trangThai === "CONG_BO")} onSubmit={lopCreate} onCancel={() => setShowLopForm(false)} />}
        </Section>
      )}

      {tab === "dk" && (
        <Section>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
            <h3>Danh sách đăng ký</h3>
            <button onClick={() => setShowDkForm(true)} disabled={lopHoc.filter((l) => l.trangThai === "MO_DANG_KY").length === 0}
                    style={btnPrimary}>+ Đăng ký mới</button>
          </div>
          <table style={table}>
            <thead><tr style={thead}>
              <th style={th}>NV</th><th style={th}>Lớp</th><th style={th}>Ngày đăng ký</th>
              <th style={th}>Trạng thái</th><th style={th}>Điểm</th><th style={th}>CC</th><th style={th}>Hành động</th>
            </tr></thead>
            <tbody>
              {dangKy.map((d) => {
                const lop = lopHoc.find((l) => l.id === d.lopHocId);
                return (
                  <tr key={d.id} style={tr}>
                    <td style={td}>{d.nhanVienId.slice(0, 8)}</td>
                    <td style={td}>{lop?.tenLop || "?"}</td>
                    <td style={td}>{d.ngayDangKy?.slice(0, 10)}</td>
                    <td style={td}><span style={{ ...chip, background: STATUS_COLORS[d.trangThai] }}>{d.trangThai}</span></td>
                    <td style={td}>{d.diemTongKet ?? "-"}</td>
                    <td style={td}>{d.chungChiCap ?? "-"}</td>
                    <td style={td}>
                      {d.trangThai === "CHO_DUYET" && <>
                        <button onClick={() => dkDuyet(d.id, "DA_CHAP_NHAN")} style={{ ...btn, background: "#10b981" }}>✓ Duyệt</button>
                        <button onClick={() => dkDuyet(d.id, "TU_CHOI")} style={{ ...btn, background: "#ef4444", marginLeft: 4 }}>✕ Từ chối</button>
                      </>}
                      {d.trangThai === "DA_CHAP_NHAN" && (
                        <button onClick={() => setShowDgForm(d.id)} style={{ ...btn, background: "#6366f1" }}>📝 Đánh giá</button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {showDkForm && <DangKyForm lopHoc={lopHoc.filter((l) => l.trangThai === "MO_DANG_KY")} onSubmit={dkCreate} onCancel={() => setShowDkForm(false)} />}
          {showDgForm && <DanhGiaForm dangKyId={showDgForm} onSubmit={(d) => dgSubmit(showDgForm, d)} onCancel={() => setShowDgForm(null)} />}
        </Section>
      )}

      {tab === "dg" && (
        <Section>
          <h3>Đánh giá sau đào tạo</h3>
          <p style={{ color: "#64748b" }}>Vào tab "Đăng ký" và nhấn nút "📝 Đánh giá" trên một đăng ký đã được chấp nhận để mở form đánh giá.</p>
          <div style={{ background: "#f8fafc", padding: 12, borderRadius: 6, marginTop: 12 }}>
            <strong>Tổng kết:</strong>
            <ul>
              <li>Số chương trình: {chuongTrinh.length}</li>
              <li>Số lớp học: {lopHoc.length}</li>
              <li>Số đăng ký: {dangKy.length}</li>
              <li>Đã chấp nhận: {dangKy.filter((d) => d.trangThai === "DA_CHAP_NHAN").length}</li>
              <li>Đã đánh giá: {dangKy.filter((d) => d.diemTongKet).length}</li>
              <li>Cấp chứng chỉ: {dangKy.filter((d) => d.chungChiCap).length}</li>
            </ul>
          </div>
        </Section>
      )}
    </div>
  );
}

function renderLopActions(l: any, lopTransition: (id: string, s: string) => void) {
  const buttons: any[] = [];
  if (l.trangThai === "MO_DANG_KY") buttons.push(<button key="dong" onClick={() => lopTransition(l.id, "DONG_DANG_KY")} style={{ ...btn, background: "#f59e0b" }}>🔒 Đóng ĐK</button>);
  if (l.trangThai === "DONG_DANG_KY") buttons.push(<button key="batdau" onClick={() => lopTransition(l.id, "DANG_DIEN_RA")} style={{ ...btn, background: "#3b82f6" }}>▶ Bắt đầu</button>);
  if (l.trangThai === "DANG_DIEN_RA") buttons.push(<button key="ht" onClick={() => lopTransition(l.id, "HOAN_THANH")} style={{ ...btn, background: "#059669" }}>✓ Hoàn thành</button>);
  if (["MO_DANG_KY","DONG_DANG_KY","DANG_DIEN_RA"].includes(l.trangThai)) buttons.push(<button key="huy" onClick={() => lopTransition(l.id, "HUY")} style={{ ...btn, background: "#ef4444", marginLeft: 4 }}>✕</button>);
  return <>{buttons}</>;
}

function ChuongTrinhForm({ onSubmit, onCancel }: any) {
  const [form, setForm] = useState({ maChuongTrinh: "", tenChuongTrinh: "", loaiChuongTrinh: "KY_NANG_CHUYEN_MON", thoiLuongGio: 8, diemDanhGiaToiThieu: 60, chungChi: "" });
  return <Modal onClose={onCancel} title="Tạo chương trình đào tạo">
    <Field label="Mã chương trình"><input value={form.maChuongTrinh} onChange={(e) => setForm({...form, maChuongTrinh: e.target.value})} style={inp} /></Field>
    <Field label="Tên chương trình"><input value={form.tenChuongTrinh} onChange={(e) => setForm({...form, tenChuongTrinh: e.target.value})} style={inp} /></Field>
    <Field label="Loại"><select value={form.loaiChuongTrinh} onChange={(e) => setForm({...form, loaiChuongTrinh: e.target.value})} style={inp}>{LOAI_OPTIONS.map((l) => <option key={l} value={l}>{l}</option>)}</select></Field>
    <Field label="Thời lượng (giờ)"><input type="number" value={form.thoiLuongGio} onChange={(e) => setForm({...form, thoiLuongGio: Number(e.target.value)})} style={inp} /></Field>
    <Field label="Điểm đậu tối thiểu"><input type="number" value={form.diemDanhGiaToiThieu} onChange={(e) => setForm({...form, diemDanhGiaToiThieu: Number(e.target.value)})} style={inp} /></Field>
    <Field label="Chứng chỉ cấp"><input value={form.chungChi} onChange={(e) => setForm({...form, chungChi: e.target.value})} style={inp} /></Field>
    <div style={{ marginTop: 12, display: "flex", gap: 8 }}>
      <button onClick={() => onSubmit(form)} style={btnPrimary}>Lưu</button>
      <button onClick={onCancel} style={{ ...btn, background: "#94a3b8" }}>Hủy</button>
    </div>
  </Modal>;
}

function LopForm({ chuongTrinh, onSubmit, onCancel }: any) {
  const [form, setForm] = useState<any>({
    maLop: "", chuongTrinhId: chuongTrinh[0]?.id, tenLop: "",
    ngayBatDau: new Date().toISOString().slice(0, 10),
    ngayKetThuc: new Date(Date.now() + 7 * 86400000).toISOString().slice(0, 10),
    soBuoi: 5, soChoToiDa: 30, diaDiem: "", giangVien: "",
  });
  return <Modal onClose={onCancel} title="Tạo lớp học">
    <Field label="Mã lớp"><input value={form.maLop} onChange={(e) => setForm({...form, maLop: e.target.value})} style={inp} /></Field>
    <Field label="Tên lớp"><input value={form.tenLop} onChange={(e) => setForm({...form, tenLop: e.target.value})} style={inp} /></Field>
    <Field label="Chương trình"><select value={form.chuongTrinhId} onChange={(e) => setForm({...form, chuongTrinhId: e.target.value})} style={inp}>{chuongTrinh.map((c: any) => <option key={c.id} value={c.id}>{c.tenChuongTrinh}</option>)}</select></Field>
    <Field label="Ngày bắt đầu"><input type="date" value={form.ngayBatDau} onChange={(e) => setForm({...form, ngayBatDau: e.target.value})} style={inp} /></Field>
    <Field label="Ngày kết thúc"><input type="date" value={form.ngayKetThuc} onChange={(e) => setForm({...form, ngayKetThuc: e.target.value})} style={inp} /></Field>
    <Field label="Số buổi"><input type="number" value={form.soBuoi} onChange={(e) => setForm({...form, soBuoi: Number(e.target.value)})} style={inp} /></Field>
    <Field label="Sĩ số tối đa"><input type="number" value={form.soChoToiDa} onChange={(e) => setForm({...form, soChoToiDa: Number(e.target.value)})} style={inp} /></Field>
    <Field label="Địa điểm"><input value={form.diaDiem} onChange={(e) => setForm({...form, diaDiem: e.target.value})} style={inp} /></Field>
    <Field label="Giảng viên"><input value={form.giangVien} onChange={(e) => setForm({...form, giangVien: e.target.value})} style={inp} /></Field>
    <div style={{ marginTop: 12, display: "flex", gap: 8 }}>
      <button onClick={() => onSubmit(form)} style={btnPrimary}>Lưu</button>
      <button onClick={onCancel} style={{ ...btn, background: "#94a3b8" }}>Hủy</button>
    </div>
  </Modal>;
}

function DangKyForm({ lopHoc, onSubmit, onCancel }: any) {
  const [form, setForm] = useState({ lopHocId: lopHoc[0]?.id, nhanVienId: "nv-1", lyDoDangKy: "" });
  return <Modal onClose={onCancel} title="Đăng ký lớp học">
    <Field label="Lớp học"><select value={form.lopHocId} onChange={(e) => setForm({...form, lopHocId: e.target.value})} style={inp}>{lopHoc.map((l: any) => <option key={l.id} value={l.id}>{l.tenLop} ({l.maLop})</option>)}</select></Field>
    <Field label="Mã NV"><input value={form.nhanVienId} onChange={(e) => setForm({...form, nhanVienId: e.target.value})} style={inp} /></Field>
    <Field label="Lý do đăng ký"><textarea value={form.lyDoDangKy} onChange={(e) => setForm({...form, lyDoDangKy: e.target.value})} style={{...inp, height: 60}} /></Field>
    <div style={{ marginTop: 12, display: "flex", gap: 8 }}>
      <button onClick={() => onSubmit(form)} style={btnPrimary}>Đăng ký</button>
      <button onClick={onCancel} style={{ ...btn, background: "#94a3b8" }}>Hủy</button>
    </div>
  </Modal>;
}

function DanhGiaForm({ dangKyId, onSubmit, onCancel }: any) {
  const [form, setForm] = useState({ diemNoiDung: 80, diemGiangVien: 80, diemThucHanh: 80, yKienNguoiHoc: "" });
  return <Modal onClose={onCancel} title={`Đánh giá - ĐK ${dangKyId.slice(0, 8)}`}>
    <p style={{ color: "#64748b", fontSize: 13 }}>Điểm 0-100, trọng số: 40% nội dung + 30% giảng viên + 30% thực hành.</p>
    <Field label="Điểm nội dung"><input type="number" min={0} max={100} value={form.diemNoiDung} onChange={(e) => setForm({...form, diemNoiDung: Number(e.target.value)})} style={inp} /></Field>
    <Field label="Điểm giảng viên"><input type="number" min={0} max={100} value={form.diemGiangVien} onChange={(e) => setForm({...form, diemGiangVien: Number(e.target.value)})} style={inp} /></Field>
    <Field label="Điểm thực hành"><input type="number" min={0} max={100} value={form.diemThucHanh} onChange={(e) => setForm({...form, diemThucHanh: Number(e.target.value)})} style={inp} /></Field>
    <Field label="Ý kiến người học"><textarea value={form.yKienNguoiHoc} onChange={(e) => setForm({...form, yKienNguoiHoc: e.target.value})} style={{...inp, height: 60}} /></Field>
    <div style={{ marginTop: 12, display: "flex", gap: 8 }}>
      <button onClick={() => onSubmit(form)} style={btnPrimary}>Lưu đánh giá</button>
      <button onClick={onCancel} style={{ ...btn, background: "#94a3b8" }}>Hủy</button>
    </div>
  </Modal>;
}

function Tab({ active, onClick, children }: any) {
  return <button onClick={onClick} style={{ padding: "8px 16px", background: active ? "#0d9488" : "#e2e8f0", color: active ? "#fff" : "#0f172a", border: "none", borderRadius: 6, cursor: "pointer", fontWeight: 600 }}>{children}</button>;
}
function Section({ children }: any) { return <div style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>{children}</div>; }
function Field({ label, children }: any) { return <div style={{ marginBottom: 8 }}><label style={lbl}>{label}</label>{children}</div>; }
function Modal({ title, children, onClose }: any) {
  return <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 100 }}>
    <div style={{ background: "#fff", padding: 24, borderRadius: 8, width: 480, maxHeight: "90vh", overflow: "auto" }}>
      <h3 style={{ marginTop: 0 }}>{title}</h3>
      {children}
      <button onClick={onClose} style={{ position: "absolute", top: 12, right: 12, background: "none", border: "none", fontSize: 20 }}>×</button>
    </div>
  </div>;
}

const lbl: React.CSSProperties = { display: "block", fontSize: 12, color: "#64748b", marginBottom: 4 };
const inp: React.CSSProperties = { padding: 8, border: "1px solid #cbd5e1", borderRadius: 4, width: "100%", boxSizing: "border-box" };
const btn: React.CSSProperties = { padding: "6px 12px", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer", fontSize: 13 };
const btnPrimary: React.CSSProperties = { ...btn, background: "#0d9488" };
const table: React.CSSProperties = { width: "100%", borderCollapse: "collapse" };
const thead: React.CSSProperties = { background: "#f1f5f9" };
const tr: React.CSSProperties = { borderTop: "1px solid #e2e8f0" };
const th: React.CSSProperties = { padding: 10, textAlign: "left" };
const td: React.CSSProperties = { padding: 10 };
const chip: React.CSSProperties = { color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 };