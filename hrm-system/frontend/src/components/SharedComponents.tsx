// Shared UI components

export function PageHeader({ title, subtitle, right }: { title: string; subtitle?: string; right?: React.ReactNode }) {
  return (
    <div className="hr-page-header">
      <div>
        <h2>{title}</h2>
        {subtitle && <p className="hr-subtitle">{subtitle}</p>}
      </div>
      {right && <div style={{ display: "flex", gap: 8 }}>{right}</div>}
    </div>
  );
}

export function StatusChip({ bg, color, children }: { bg: string; color: string; children: React.ReactNode }) {
  return <span className="hr-chip" style={{ background: bg, color }}>{children}</span>;
}
