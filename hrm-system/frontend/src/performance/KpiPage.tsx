import { useEffect, useState } from "react";
import { kpiMock } from "../mock/kpi.mock";

export default function KpiPage() {
  const [cycles, setCycles] = useState<any[]>([]);
  const [selectedCycle, setSelectedCycle] = useState<any>(null);
  const [assignments, setAssignments] = useState<any[]>([]);
  const [selectedAssign, setSelectedAssign] = useState<any>(null);
  const [view, setView] = useState<"assignments" | "self" | "review" | "approve">("assignments");
  const [diemTuDanhGia, setDiemTuDanhGia] = useState("85");
  const [tyLeHT, setTyLeHT] = useState("95");
  const [nhanXetNV, setNhanXetNV] = useState("");
  const [diemManager, setDiemManager] = useState("88");
  const [xepLoaiDeXuat, setXepLoaiDeXuat] = useState("A");
  const [nhanXetManager, setNhanXetManager] = useState("");
  const [diemCuoi, setDiemCuoi] = useState("90");
  const [xepLoaiCuoi, setXepLoaiCuoi] = useState("A");
  const [heSoThuong, setHeSoThuong] = useState("1.5");

  useEffect(() => { kpiMock.listCycles().then(setCycles); }, []);

  const onSelectCycle = async (cycle: any) => {
    setSelectedCycle(cycle);
    const list = await kpiMock.listAssignments(cycle.cycleId);
    setAssignments(list);
    setSelectedAssign(null);
    setView("assignments");
  };

  const submitSelf = async () => {
    if (!selectedAssign) return;
    const updated = await kpiMock.selfAssess(selectedAssign.assignmentId, {
      diemTuDanhGia: Number(diemTuDanhGia),
      tyLeHoanThanh: Number(tyLeHT),
      nhanXetNv: nhanXetNV,
    });
    setAssignments((prev) => prev.map((a) => a.assignmentId === updated.assignmentId ? updated : a));
    setSelectedAssign(updated);
    alert("Tự đánh giá thành công");
  };

  const submitReview = async () => {
    if (!selectedAssign) return;
    const updated = await kpiMock.managerReview(selectedAssign.assignmentId, {
      diemManager: Number(diemManager),
      xepLoaiDeXuat,
      nhanXetManager,
    });
    setAssignments((prev) => prev.map((a) => a.assignmentId === updated.assignmentId ? updated : a));
    setSelectedAssign(updated);
    alert("Review thành công");
  };

  const submitApprove = async () => {
    if (!selectedAssign) return;
    const updated = await kpiMock.hrApprove(selectedAssign.assignmentId, {
      diemCuoi: Number(diemCuoi),
      xepLoaiCuoi,
      heSoThuong: Number(heSoThuong),
    });
    setAssignments((prev) => prev.map((a) => a.assignmentId === updated.assignmentId ? updated : a));
    setSelectedAssign(updated);
    alert("HR phê duyệt thành công");
  };

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <h2>Module Đánh giá hiệu suất (T18)</h2>
      <p style={{ color: "#64748b" }}>KPI/OKR theo chu kỳ: NV tự đánh giá → Manager review → HR phê duyệt xếp loại</p>

      <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
        <h3>Chu kỳ đánh giá</h3>
        <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          {cycles.map((c) => (
            <div key={c.cycleId}
                 onClick={() => onSelectCycle(c)}
                 style={{
                   border: "1px solid #cbd5e1",
                   borderRadius: 8,
                   padding: 12,
                   cursor: "pointer",
                   background: selectedCycle?.cycleId === c.cycleId ? "#ecfeff" : "#fff",
                   minWidth: 220,
                 }}>
              <div style={{ fontWeight: 600 }}>{c.tenChuKy}</div>
              <div style={{ fontSize: 12, color: "#64748b" }}>
                {c.ngayBatDau} → {c.ngayKetThuc}
              </div>
              <div style={{ fontSize: 12, marginTop: 4 }}>
                <StatusChip status={c.trangThai} />
              </div>
              <div style={{ fontSize: 12, marginTop: 4 }}>
                {c.soMucTieu} mục tiêu · {c.soNvThamGia} NV
              </div>
            </div>
          ))}
        </div>
      </section>

      {selectedCycle && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <h3>Mục tiêu KPI - {selectedCycle.tenChuKy}</h3>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                <th style={th}>Mục tiêu</th>
                <th style={th}>Loại</th>
                <th style={th}>Đơn vị</th>
                <th style={th}>Target</th>
                <th style={th}>Trọng số</th>
                <th style={th}>NV</th>
                <th style={th}>Trạng thái</th>
                <th style={th}>Điểm</th>
                <th style={th}>Xếp loại</th>
                <th style={th}>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {assignments.map((a) => (
                <tr key={a.assignmentId} style={{ borderTop: "1px solid #e2e8f0" }}>
                  <td style={td}>{a.tenMucTieu}</td>
                  <td style={td}>{a.loaiMucTieu}</td>
                  <td style={td}>{a.donViDo || "-"}</td>
                  <td style={td}>{a.targetValue}</td>
                  <td style={td}>{a.trongSo}</td>
                  <td style={td}>{a.hoTen || a.nhanVienId}</td>
                  <td style={td}><StatusChip status={a.trangThai} /></td>
                  <td style={td}>{a.diemTrungBinh?.toFixed(2) || "-"}</td>
                  <td style={td}>{a.xepLoaiCuoi || "-"}</td>
                  <td style={td}>
                    <button onClick={() => { setSelectedAssign(a); setView("self"); }} style={btn}>Chi tiết</button>
                  </td>
                </tr>
              ))}
              {assignments.length === 0 && (
                <tr><td colSpan={10} style={{ padding: 24, textAlign: "center", color: "#94a3b8" }}>Chu kỳ chưa có mục tiêu nào</td></tr>
              )}
            </tbody>
          </table>
        </section>
      )}

      {selectedAssign && (
        <section style={{ background: "#fff", padding: 16, borderRadius: 8, marginBottom: 16 }}>
          <h3>Chi tiết: {selectedAssign.tenMucTieu}</h3>
          <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
            <TabBtn active={view === "self"} onClick={() => setView("self")}>NV tự đánh giá</TabBtn>
            <TabBtn active={view === "review"} onClick={() => setView("review")}>Manager review</TabBtn>
            <TabBtn active={view === "approve"} onClick={() => setView("approve")}>HR phê duyệt</TabBtn>
          </div>

          {view === "self" && (
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
              <Field label="Tỷ lệ hoàn thành (%)">
                <input type="number" value={tyLeHT} onChange={(e) => setTyLeHT(e.target.value)} style={inp} />
              </Field>
              <Field label="Điểm tự đánh giá (0-100)">
                <input type="number" value={diemTuDanhGia} onChange={(e) => setDiemTuDanhGia(e.target.value)} style={inp} />
              </Field>
              <Field label="Nhận xét NV" full>
                <textarea value={nhanXetNV} onChange={(e) => setNhanXetNV(e.target.value)} style={{ ...inp, height: 80 }} />
              </Field>
              <div style={{ gridColumn: "span 2" }}>
                <button onClick={submitSelf} style={{ ...btn, background: "#0d9488" }} disabled={selectedAssign.trangThai !== "MOI_GAN"}>
                  Gửi tự đánh giá
                </button>
                {selectedAssign.trangThai !== "MOI_GAN" && (
                  <span style={{ marginLeft: 12, color: "#64748b", fontSize: 13 }}>
                    (Trạng thái hiện tại: {selectedAssign.trangThai})
                  </span>
                )}
              </div>
            </div>
          )}

          {view === "review" && (
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
              <Field label="Điểm Manager (0-100)">
                <input type="number" value={diemManager} onChange={(e) => setDiemManager(e.target.value)} style={inp} />
              </Field>
              <Field label="Đề xuất xếp loại">
                <select value={xepLoaiDeXuat} onChange={(e) => setXepLoaiDeXuat(e.target.value)} style={inp}>
                  <option value="A">A - Xuất sắc</option>
                  <option value="B">B - Tốt</option>
                  <option value="C">C - Trung bình</option>
                  <option value="D">D - Yếu</option>
                </select>
              </Field>
              <Field label="Nhận xét Manager" full>
                <textarea value={nhanXetManager} onChange={(e) => setNhanXetManager(e.target.value)} style={{ ...inp, height: 80 }} />
              </Field>
              <div style={{ gridColumn: "span 2" }}>
                <button onClick={submitReview} style={{ ...btn, background: "#0d9488" }} disabled={selectedAssign.trangThai !== "NV_DA_TU_DANH_GIA"}>
                  Gửi review
                </button>
              </div>
            </div>
          )}

          {view === "approve" && (
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
              <Field label="Điểm cuối (0-100)">
                <input type="number" value={diemCuoi} onChange={(e) => setDiemCuoi(e.target.value)} style={inp} />
              </Field>
              <Field label="Xếp loại cuối">
                <select value={xepLoaiCuoi} onChange={(e) => setXepLoaiCuoi(e.target.value)} style={inp}>
                  <option value="A">A - Xuất sắc</option>
                  <option value="B">B - Tốt</option>
                  <option value="C">C - Trung bình</option>
                  <option value="D">D - Yếu</option>
                </select>
              </Field>
              <Field label="Hệ số thưởng">
                <input type="number" step="0.1" value={heSoThuong} onChange={(e) => setHeSoThuong(e.target.value)} style={inp} />
              </Field>
              <div style={{ gridColumn: "span 2" }}>
                <button onClick={submitApprove} style={{ ...btn, background: "#0d9488" }} disabled={selectedAssign.trangThai !== "MANAGER_DA_REVIEW"}>
                  Phê duyệt HR
                </button>
              </div>
            </div>
          )}

          <div style={{ marginTop: 16, padding: 12, background: "#f8fafc", borderRadius: 6, fontSize: 13 }}>
            <div>Điểm NV tự đánh giá: <strong>{selectedAssign.diemTuDanhGia?.toFixed(2) || "-"}</strong></div>
            <div>Điểm Manager: <strong>{selectedAssign.diemManager?.toFixed(2) || "-"}</strong></div>
            <div>Điểm trung bình: <strong>{selectedAssign.diemTrungBinh?.toFixed(2) || "-"}</strong> (40% NV + 60% Manager)</div>
            <div>Xếp loại cuối: <strong>{selectedAssign.xepLoaiCuoi || "-"}</strong></div>
          </div>
        </section>
      )}
    </div>
  );
}

function Field({ label, children, full }: any) {
  return (
    <div style={{ gridColumn: full ? "span 2" : undefined }}>
      <label style={lbl}>{label}</label>
      {children}
    </div>
  );
}

function TabBtn({ active, onClick, children }: any) {
  return (
    <button onClick={onClick} style={{
      padding: "8px 16px",
      background: active ? "#0d9488" : "#fff",
      color: active ? "#fff" : "#0f172a",
      border: "1px solid #0d9488",
      borderRadius: 4,
      cursor: "pointer",
    }}>{children}</button>
  );
}

function StatusChip({ status }: { status: string }) {
  const colorMap: Record<string, string> = {
    MOI_TAO: "#94a3b8", DANG_DANH_GIA: "#3b82f6", DA_DONG: "#64748b", HUY: "#ef4444",
    MOI_GAN: "#94a3b8", NV_DA_TU_DANH_GIA: "#f59e0b", MANAGER_DA_REVIEW: "#3b82f6",
    HR_DA_PHE_DUYET: "#10b981", TU_CHOI: "#ef4444",
  };
  const color = colorMap[status] ?? "#64748b";
  return <span style={{ background: color, color: "#fff", padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{status}</span>;
}

const lbl: React.CSSProperties = { display: "block", fontSize: 12, color: "#64748b", marginBottom: 4 };
const inp: React.CSSProperties = { padding: 8, width: "100%", border: "1px solid #cbd5e1", borderRadius: 4 };
const btn: React.CSSProperties = { padding: "8px 16px", color: "#fff", border: "none", borderRadius: 4, cursor: "pointer" };
const th: React.CSSProperties = { padding: 10, textAlign: "left" };
const td: React.CSSProperties = { padding: 10 };