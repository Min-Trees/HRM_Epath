// Mock API for T14 - Offboarding module.
// Dùng localStorage làm "DB" ?? dev frontend không ph? thu?c backend.

const STORAGE_KEY = "offboarding_cases_v1";
const SEVERANCE_KEY = "offboarding_severance_v1";

interface OffboardingCaseDTO {
  caseId: string;
  nhanVienId: string;
  maNv: string;
  hoTen: string;
  soQuyetDinh: string;
  ngayQuyetDinh: string;
  ngayNghiViecCuoi: string;
  ngayChinhThucNghi: string;
  lyDo: string;
  lyDoChiTiet?: string;
  trangThai: string;
  ghiChu?: string;
  tongTask: number;
  taskHoanThanh: number;
  tienDoPhanTram: number;
}

interface TaskDTO {
  taskId: string;
  caseId: string;
  loaiTask: string;
  moTa?: string;
  nguoiPhuTrachId?: string;
  hanHoanThanh?: string;
  trangThai: string;
  thuTu: number;
}

interface SeveranceDTO {
  severanceId: string;
  caseId: string;
  nhanVienId: string;
  thoiGianLamViecThang: number;
  soNamThamNien: number;
  luongThoiViecBinhQuan: number;
  heSo: number;
  soTienTroCap: number;
  hoTen?: string;
  maNv?: string;
  ngayVaoLam?: string;
  ngayNghiViecCuoi?: string;
}

const SEED_CASES: OffboardingCaseDTO[] = [
  {
    caseId: "c-001",
    nhanVienId: "nv-101",
    maNv: "NV-0101",
    hoTen: "Nguyen Van A",
    soQuyetDinh: "QDD-2026-001",
    ngayQuyetDinh: "2026-06-15",
    ngayNghiViecCuoi: "2026-06-30",
    ngayChinhThucNghi: "2026-07-01",
    lyDo: "NGHI_VIEC_TU_NGUYEN",
    lyDoChiTiet: "Chuyen cong tac moi",
    trangThai: "DANG_THUC_HIEN",
    ghiChu: "Da ban giao mot phan",
    tongTask: 10,
    taskHoanThanh: 6,
    tienDoPhanTram: 60,
  },
  {
    caseId: "c-002",
    nhanVienId: "nv-102",
    maNv: "NV-0102",
    hoTen: "Tran Thi B",
    soQuyetDinh: "QDD-2026-002",
    ngayQuyetDinh: "2026-06-20",
    ngayNghiViecCuoi: "2026-07-31",
    ngayChinhThucNghi: "2026-08-01",
    lyDo: "HET_HAN_HDLD",
    trangThai: "MOI_TAO",
    tongTask: 10,
    taskHoanThanh: 0,
    tienDoPhanTram: 0,
  },
];

const SEED_TASKS: Record<string, TaskDTO[]> = {
  "c-001": [
    { taskId: "t-01", caseId: "c-001", loaiTask: "TRA_TAI_SAN", moTa: "Tra laptop Dell 5420", trangThai: "HOAN_THANH", thuTu: 1 },
    { taskId: "t-02", caseId: "c-001", loaiTask: "THU_HOI_QUYEN_TRUY_CAP", moTa: "Vo hieu email", trangThai: "HOAN_THANH", thuTu: 3 },
    { taskId: "t-03", caseId: "c-001", loaiTask: "BAN_GIAO_CONG_VIEC", moTa: "Ban giao cho NV-0201", trangThai: "HOAN_THANH", thuTu: 2 },
    { taskId: "t-04", caseId: "c-001", loaiTask: "BAO_GIAM_BHXH", trangThai: "DANG_LAM", thuTu: 7 },
    { taskId: "t-05", caseId: "c-001", loaiTask: "QUYET_TOAN_THUE_TNCN", trangThai: "CHUA_LAM", thuTu: 6 },
    { taskId: "t-06", caseId: "c-001", loaiTask: "PHONG_VAN_THAM_PHONG", trangThai: "HOAN_THANH", thuTu: 9 },
    { taskId: "t-07", caseId: "c-001", loaiTask: "PHEP_NAM_CON_DU", trangThai: "HOAN_THANH", thuTu: 4 },
    { taskId: "t-08", caseId: "c-001", loaiTask: "KY_VAN_BANG_LUONG", trangThai: "CHUA_LAM", thuTu: 5 },
    { taskId: "t-09", caseId: "c-001", loaiTask: "CHOT_SO_BHXH_D07", trangThai: "KHONG_AP_DUNG", thuTu: 8 },
    { taskId: "t-10", caseId: "c-001", loaiTask: "XAC_NHAN_KHONG_NO", trangThai: "HOAN_THANH", thuTu: 10 },
  ],
  "c-002": [],
};

const SEED_SEVERANCE: SeveranceDTO = {
  severanceId: "sc-001",
  caseId: "c-001",
  nhanVienId: "nv-101",
  thoiGianLamViecThang: 60,
  soNamThamNien: 5.0,
  luongThoiViecBinhQuan: 18000000,
  heSo: 0.5,
  soTienTroCap: 45000000,
  hoTen: "Nguyen Van A",
  maNv: "NV-0101",
  ngayVaoLam: "2021-07-01",
  ngayNghiViecCuoi: "2026-06-30",
};

function load(key: string, fallback: any) {
  if (typeof window === "undefined") return fallback;
  try {
    const raw = window.localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

function save(key: string, value: any) {
  if (typeof window === "undefined") return;
  window.localStorage.setItem(key, JSON.stringify(value));
}

function delay<T>(value: T, ms = 120): Promise<T> {
  return new Promise((r) => setTimeout(() => r(value), ms));
}

function ensureSeed() {
  if (!load(STORAGE_KEY, null)) save(STORAGE_KEY, SEED_CASES);
  if (!load(SEVERANCE_KEY, null)) save(SEVERANCE_KEY, SEED_SEVERANCE);
}

function recomputeProgress(caseId: string) {
  const tasks: TaskDTO[] = load(`tasks_${caseId}`, null) ?? SEED_TASKS[caseId] ?? [];
  save(`tasks_${caseId}`, tasks);
  const cases = load(STORAGE_KEY, []);
  const idx = cases.findIndex((c: OffboardingCaseDTO) => c.caseId === caseId);
  if (idx >= 0) {
    cases[idx].tongTask = tasks.length;
    cases[idx].taskHoanThanh = tasks.filter(
      (t) => t.trangThai === "HOAN_THANH" || t.trangThai === "KHONG_AP_DUNG"
    ).length;
    cases[idx].tienDoPhanTram =
      cases[idx].tongTask === 0
        ? 0
        : Math.round((cases[idx].taskHoanThanh * 10000) / cases[idx].tongTask) / 100;
    save(STORAGE_KEY, cases);
  }
}

export const offboardingApi = {
  async list({ page = 0, size = 20 }: { page?: number; size?: number } = {}) {
    ensureSeed();
    const cases: OffboardingCaseDTO[] = load(STORAGE_KEY, []);
    const content = cases.slice(page * size, page * size + size);
    return delay({ content, totalElements: cases.length, totalPages: Math.ceil(cases.length / size), number: page, size });
  },

  async get(id: string) {
    ensureSeed();
    const cases: OffboardingCaseDTO[] = load(STORAGE_KEY, []);
    const found = cases.find((c) => c.caseId === id) ?? null;
    return delay(found);
  },

  async listTasks(caseId: string) {
    ensureSeed();
    const tasks: TaskDTO[] = load(`tasks_${caseId}`, null) ?? SEED_TASKS[caseId] ?? [];
    save(`tasks_${caseId}`, tasks);
    return delay(tasks);
  },

  async updateTask(taskId: string, trangThai: string) {
    ensureSeed();
    const cases: OffboardingCaseDTO[] = load(STORAGE_KEY, []);
    for (const c of cases) {
      const tasks: TaskDTO[] = load(`tasks_${c.caseId}`, null) ?? [];
      const idx = tasks.findIndex((t) => t.taskId === taskId);
      if (idx >= 0) {
        tasks[idx].trangThai = trangThai;
        save(`tasks_${c.caseId}`, tasks);
        recomputeProgress(c.caseId);
        break;
      }
    }
    return delay({ ok: true });
  },

  async getSeverance(caseId: string) {
    ensureSeed();
    const sev: SeveranceDTO = load(SEVERANCE_KEY, null);
    return delay(sev?.caseId === caseId ? sev : null);
  },

  async create(input: Partial<OffboardingCaseDTO>) {
    ensureSeed();
    const cases: OffboardingCaseDTO[] = load(STORAGE_KEY, []);
    const newCase: OffboardingCaseDTO = {
      caseId: `c-${Date.now()}`,
      nhanVienId: input.nhanVienId ?? "nv-x",
      maNv: input.maNv ?? "NV-XXXX",
      hoTen: input.hoTen ?? "...",
      soQuyetDinh: input.soQuyetDinh ?? `QDD-${Date.now()}`,
      ngayQuyetDinh: input.ngayQuyetDinh ?? new Date().toISOString().slice(0, 10),
      ngayNghiViecCuoi: input.ngayNghiViecCuoi ?? new Date().toISOString().slice(0, 10),
      ngayChinhThucNghi: input.ngayChinhThucNghi ?? new Date().toISOString().slice(0, 10),
      lyDo: input.lyDo ?? "KHAC",
      lyDoChiTiet: input.lyDoChiTiet,
      trangThai: "MOI_TAO",
      tongTask: 0,
      taskHoanThanh: 0,
      tienDoPhanTram: 0,
    };
    cases.unshift(newCase);
    save(STORAGE_KEY, cases);
    return delay(newCase);
  },
};
