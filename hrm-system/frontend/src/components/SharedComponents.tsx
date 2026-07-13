// Shared UI components

export function PageHeader({ title, subtitle }: { title: string; subtitle?: string }) {
  return (
    <div style={{
      background: "white",
      borderBottom: "1px solid #e2e8f0",
      padding: "16px 24px",
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
    }}>
      <div>
        <h2 style={{ margin: 0, fontSize: 18, fontWeight: 600, color: "#0f172a" }}>{title}</h2>
        {subtitle && <p style={{ margin: "2px 0 0", fontSize: 13, color: "#64748b" }}>{subtitle}</p>}
      </div>
    </div>
  );
}

export function StatusChip({ bg, color, children }: { bg: string; color: string; children: React.ReactNode }) {
  return <span style={{ background: bg, color, padding: "2px 8px", borderRadius: 12, fontSize: 12 }}>{children}</span>;
}
