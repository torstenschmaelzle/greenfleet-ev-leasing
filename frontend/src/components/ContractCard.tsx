import type { FleetContractResponse } from '../api/sales';

const BUNDLE_LABELS: Record<string, string> = {
  BALANCED: 'Balanced', ECO: 'Eco', ECONOMIC: 'Economic', PREMIUM: 'Premium', CUSTOM: 'Custom',
};

const BUNDLE_COLORS: Record<string, { bg: string; color: string }> = {
  PREMIUM:  { bg: 'rgba(124,58,237,0.10)',  color: '#7c3aed' },
  BALANCED: { bg: 'rgba(8,145,178,0.10)',   color: '#0891b2' },
  ECO:      { bg: 'rgba(22,163,74,0.10)',   color: '#16a34a' },
  ECONOMIC: { bg: 'rgba(234,179,8,0.12)',   color: '#a16207' },
  CUSTOM:   { bg: 'rgba(100,116,139,0.10)', color: '#475569' },
};

function fmt(cents: number, currency = 'CHF') {
  return (cents / 100).toLocaleString('de-CH', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

interface ContractCardProps {
  contract: FleetContractResponse;
  showCustomer?: boolean;
  onView: () => void;
}

export default function ContractCard({ contract, showCustomer = false, onView }: ContractCardProps) {
  const signed = contract.signed;
  const bundleColor = BUNDLE_COLORS[contract.bundleType] ?? BUNDLE_COLORS.CUSTOM;

  return (
    <div style={{
      background: 'var(--color-surface)',
      borderRadius: 'var(--radius-lg)',
      border: '1px solid var(--color-border)',
      boxShadow: 'var(--shadow-sm)',
      overflow: 'hidden',
      display: 'flex',
      transition: 'box-shadow 0.15s, transform 0.15s',
    }}
      onMouseEnter={e => {
        (e.currentTarget as HTMLDivElement).style.boxShadow = 'var(--shadow-md)';
        (e.currentTarget as HTMLDivElement).style.transform = 'translateY(-1px)';
      }}
      onMouseLeave={e => {
        (e.currentTarget as HTMLDivElement).style.boxShadow = 'var(--shadow-sm)';
        (e.currentTarget as HTMLDivElement).style.transform = 'translateY(0)';
      }}
    >
      {/* Left accent bar */}
      <div style={{
        width: 4, flexShrink: 0,
        background: signed ? '#16a34a' : '#ca8a04',
      }} />

      <div style={{ flex: 1, padding: '20px 20px 16px 20px' }}>

        {/* Top row: bundle + status */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
          <span style={{
            padding: '3px 10px', borderRadius: 6, fontSize: 12, fontWeight: 700,
            background: bundleColor.bg, color: bundleColor.color,
            letterSpacing: 0.3,
          }}>
            {BUNDLE_LABELS[contract.bundleType] ?? contract.bundleType}
          </span>
          <span style={{
            display: 'flex', alignItems: 'center', gap: 5,
            padding: '3px 10px', borderRadius: 20, fontSize: 12, fontWeight: 700,
            ...(signed
              ? { background: '#f0fdf4', color: '#15803d', border: '1px solid #86efac' }
              : { background: '#fefce8', color: '#a16207', border: '1px solid #fde68a' }),
          }}>
            <span style={{ width: 6, height: 6, borderRadius: '50%', background: 'currentColor', display: 'inline-block' }} />
            {signed ? 'Signed' : 'Pending signature'}
          </span>
        </div>

        {/* Customer name — admin only */}
        {showCustomer && (
          <div style={{ marginBottom: 12 }}>
            <div style={{ fontWeight: 700, fontSize: 15, color: 'var(--color-text)' }}>
              {contract.customerName}
            </div>
            <div style={{ fontSize: 12, color: 'var(--color-text-muted)', marginTop: 1 }}>
              {contract.customerCompany} · {contract.customerEmail}
            </div>
          </div>
        )}

        {/* Monthly rate — main focus */}
        <div style={{ marginBottom: 14 }}>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 4 }}>
            <span style={{ fontSize: 11, fontWeight: 600, color: 'var(--color-text-faint)', marginRight: 2 }}>
              {contract.currency}
            </span>
            <span style={{ fontSize: 28, fontWeight: 800, color: 'var(--color-text)', letterSpacing: -1, lineHeight: 1 }}>
              {fmt(contract.totalMonthlyInstallmentCents, contract.currency)}
            </span>
            <span style={{ fontSize: 13, color: 'var(--color-text-muted)', fontWeight: 400 }}>/mo</span>
          </div>
          <div style={{ fontSize: 12, color: 'var(--color-text-faint)', marginTop: 3 }}>
            {contract.currency} {fmt(contract.totalMonthlyInstallmentCents * contract.leaseTermMonths, contract.currency)} total over {contract.leaseTermMonths} months
          </div>
        </div>

        {/* Stats row + button */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12 }}>
          <div style={{ display: 'flex', gap: 16 }}>
            <Stat label="Vehicles" value={String(contract.vehicles.length)} />
            <Stat label="Lease term" value={`${contract.leaseTermMonths} mo`} />
          </div>

          <button onClick={onView} style={{
            padding: '7px 16px', fontSize: 13, fontWeight: 600, cursor: 'pointer',
            background: 'var(--color-bg)', color: 'var(--color-text)',
            border: '1px solid var(--color-border)', borderRadius: 'var(--radius-sm)',
            transition: 'all 0.15s', whiteSpace: 'nowrap',
            display: 'flex', alignItems: 'center', gap: 6,
          }}
            onMouseEnter={e => {
              e.currentTarget.style.background = 'var(--color-brand)';
              e.currentTarget.style.color = 'white';
              e.currentTarget.style.borderColor = 'var(--color-brand)';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.background = 'var(--color-bg)';
              e.currentTarget.style.color = 'var(--color-text)';
              e.currentTarget.style.borderColor = 'var(--color-border)';
            }}
          >
            View Contract
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M5 12h14M12 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <div style={{ fontSize: 10, fontWeight: 700, color: 'var(--color-text-faint)', textTransform: 'uppercase', letterSpacing: 0.6 }}>
        {label}
      </div>
      <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--color-text)', marginTop: 2 }}>
        {value}
      </div>
    </div>
  );
}
