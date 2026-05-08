import { useEffect, useRef, useState } from 'react';
import PageHeader from '../components/PageHeader';
import ContractCard from '../components/ContractCard';
import { listFleetContracts, getFleetContract } from '../api/sales';
import { vehiclesApi } from '../api/vehicles';
import type { FleetContractResponse } from '../api/sales';
import type { Vehicle } from '../types';

const BUNDLE_LABELS: Record<string, string> = {
  BALANCED: 'Balanced', ECO: 'Eco', ECONOMIC: 'Economic', PREMIUM: 'Premium', CUSTOM: 'Custom',
};

function fmt(cents: number, currency = 'CHF') {
  return `${currency} ${(cents / 100).toLocaleString('de-CH', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function today() {
  return new Date().toLocaleDateString('en-GB', { day: 'numeric', month: 'long', year: 'numeric' });
}

// ─── Icons ────────────────────────────────────────────────────────────────────

function IconRefresh() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" />
      <path d="M3 3v5h5" />
    </svg>
  );
}

function IconFile() {
  return (
    <svg width="40" height="40" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"
      style={{ opacity: 0.22 }}>
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <polyline points="14 2 14 8 20 8" />
      <line x1="16" y1="13" x2="8" y2="13" />
      <line x1="16" y1="17" x2="8" y2="17" />
      <polyline points="10 9 9 9 8 9" />
    </svg>
  );
}

function IconPrint() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="6 9 6 2 18 2 18 9" />
      <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2" />
      <rect x="6" y="14" width="12" height="8" />
    </svg>
  );
}

// ─── Contract document (modal) ────────────────────────────────────────────────

const bodyText: React.CSSProperties = {
  fontSize: 13, color: '#475569', lineHeight: 1.7, margin: '0 0 4px', fontFamily: 'sans-serif',
};

function DocSection({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 28, paddingBottom: 28, borderBottom: '1px solid #f1f5f9' }}>
      <div style={{ fontWeight: 700, fontSize: 15, color: '#0f172a', marginBottom: 12, fontFamily: 'sans-serif' }}>
        {title}
      </div>
      {children}
    </div>
  );
}

function PartyBox({ title, lines }: { title: string; lines: string[] }) {
  return (
    <div style={{ background: '#f8fafc', borderRadius: 8, padding: '14px 16px', border: '1px solid #e2e8f0' }}>
      <div style={{ fontSize: 10, fontFamily: 'sans-serif', fontWeight: 700, color: '#94a3b8',
        textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 8 }}>{title}</div>
      {lines.map((l, i) => (
        <div key={i} style={{ fontSize: i === 0 ? 14 : 12, fontWeight: i === 0 ? 700 : 400,
          color: i === 0 ? '#0f172a' : '#64748b', fontFamily: 'sans-serif', lineHeight: 1.6 }}>{l}</div>
      ))}
    </div>
  );
}

function ContractDocument({ contract, vehicleMap }: {
  contract: FleetContractResponse;
  vehicleMap: Record<string, Vehicle>;
}) {
  const totalAnnual = contract.totalMonthlyInstallmentCents * 12;
  const totalLease  = contract.totalMonthlyInstallmentCents * contract.leaseTermMonths;

  return (
    <div style={{ background: 'white', borderRadius: 10, border: '1px solid #e2e8f0',
      boxShadow: '0 4px 24px rgba(0,0,0,0.08)', overflow: 'hidden', fontFamily: 'Georgia, serif' }}>

      {/* Header */}
      <div style={{ background: 'linear-gradient(135deg, #0f172a 0%, #1e3a5f 100%)',
        padding: '32px 40px', color: 'white' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <div style={{ fontSize: 11, fontFamily: 'sans-serif', fontWeight: 600, letterSpacing: 2,
              color: '#94a3b8', textTransform: 'uppercase', marginBottom: 8 }}>GreenFleet Mobility AG</div>
            <div style={{ fontSize: 26, fontWeight: 700, letterSpacing: -0.5 }}>Fleet Leasing Agreement</div>
            <div style={{ fontSize: 14, color: '#94a3b8', marginTop: 6 }}>
              {BUNDLE_LABELS[contract.bundleType] ?? contract.bundleType} Bundle · {contract.leaseTermMonths}-Month Term
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: 11, fontFamily: 'sans-serif', color: '#64748b', letterSpacing: 0.5 }}>CONTRACT REF.</div>
            <div style={{ fontSize: 13, fontFamily: 'monospace', color: '#cbd5e1', marginTop: 2 }}>{contract.number}</div>
            <div style={{ fontSize: 11, fontFamily: 'sans-serif', color: '#64748b', marginTop: 8 }}>DATE</div>
            <div style={{ fontSize: 13, fontFamily: 'sans-serif', color: '#cbd5e1', marginTop: 2 }}>{today()}</div>
          </div>
        </div>
      </div>

      <div style={{ padding: '32px 40px' }}>

        {/* Parties */}
        <DocSection title="1. Parties to the Agreement">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
            <PartyBox title="Lessor" lines={['GreenFleet Mobility AG', 'Bahnhofstrasse 42, 8001 Zürich', 'Switzerland', 'CHE-123.456.789 MwSt']} />
            <PartyBox title="Lessee" lines={[contract.customerName, contract.customerCompany, contract.customerEmail]} />
          </div>
        </DocSection>

        {/* Lease summary */}
        <DocSection title="2. Lease Summary">
          <p style={bodyText}>
            GreenFleet Mobility AG (the "Lessor") hereby agrees to lease to {contract.customerCompany} (the
            "Lessee") a fleet of {contract.vehicles.length} fully electric vehicle{contract.vehicles.length !== 1 ? 's' : ''} as
            described in Section 3 below. The lease commences on the date of signature and runs for a fixed
            term of <strong>{contract.leaseTermMonths} months</strong>, after which the vehicles are to be returned
            to the Lessor in a condition consistent with normal wear and tear.
          </p>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12, marginTop: 16 }}>
            {[
              { label: 'Monthly instalment', value: fmt(contract.totalMonthlyInstallmentCents, contract.currency) },
              { label: 'Annual total',        value: fmt(totalAnnual, contract.currency) },
              { label: 'Total lease value',   value: fmt(totalLease, contract.currency) },
            ].map(({ label, value }) => (
              <div key={label} style={{ background: '#f8fafc', borderRadius: 8, padding: '12px 14px',
                border: '1px solid #e2e8f0', textAlign: 'center' }}>
                <div style={{ fontSize: 10, fontFamily: 'sans-serif', fontWeight: 600, color: '#94a3b8',
                  textTransform: 'uppercase', letterSpacing: 0.5 }}>{label}</div>
                <div style={{ fontSize: 16, fontWeight: 700, color: '#0f172a', marginTop: 4, fontFamily: 'sans-serif' }}>{value}</div>
              </div>
            ))}
          </div>
        </DocSection>

        {/* Vehicle schedule */}
        <DocSection title="3. Vehicle Schedule">
          <p style={bodyText}>
            The following electric vehicles are covered under this agreement. Each vehicle is supplied
            with a full battery health certification and all applicable warranties as detailed in the
            GreenFleet Standard Vehicle Specification.
          </p>
          <div style={{ marginTop: 12, border: '1px solid #e2e8f0', borderRadius: 8, overflow: 'hidden' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontFamily: 'sans-serif', fontSize: 13 }}>
              <thead>
                <tr style={{ background: '#f1f5f9' }}>
                  {['#', 'Vehicle', 'Type', 'Range', 'Monthly'].map(h => (
                    <th key={h} style={{ padding: '10px 14px', textAlign: 'left', fontWeight: 600,
                      fontSize: 11, color: '#64748b', textTransform: 'uppercase', letterSpacing: 0.4,
                      borderBottom: '1px solid #e2e8f0' }}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {contract.vehicles.map((v, i) => {
                  const info = vehicleMap[v.vin];
                  return (
                    <tr key={v.vin} style={{ borderBottom: i < contract.vehicles.length - 1 ? '1px solid #f1f5f9' : 'none' }}>
                      <td style={{ padding: '10px 14px', color: '#94a3b8', fontFamily: 'monospace', fontSize: 11 }}>{i + 1}</td>
                      <td style={{ padding: '10px 14px' }}>
                        <div style={{ fontWeight: 600, color: '#0f172a' }}>
                          {info ? `${info.brand} ${info.model}` : '—'}
                        </div>
                        <div style={{ fontSize: 10, color: '#94a3b8', fontFamily: 'monospace' }}>VIN {v.vin}</div>
                      </td>
                      <td style={{ padding: '10px 14px', color: '#475569' }}>{info?.vehicleType ?? '—'}</td>
                      <td style={{ padding: '10px 14px', color: '#2563eb', fontWeight: 600 }}>
                        {info ? `${info.rangeKm} km` : '—'}
                      </td>
                      <td style={{ padding: '10px 14px', fontWeight: 700, color: '#0891b2' }}>
                        {fmt(v.monthlyInstallmentCents, v.currency)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </DocSection>

        {/* Signatures */}
        <DocSection title="4. Signatures">
          <p style={bodyText}>
            By signing below, both parties confirm they have read, understood, and agreed to all terms
            and conditions set forth in this Fleet Leasing Agreement.
          </p>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 32, marginTop: 20 }}>
            <div>
              <div style={{ fontSize: 11, fontFamily: 'sans-serif', fontWeight: 600, color: '#94a3b8',
                textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 8 }}>For the Lessor</div>
              <div style={{ borderBottom: '1.5px solid #334155', paddingBottom: 6, minHeight: 48,
                display: 'flex', alignItems: 'flex-end' }}>
                <span style={{ fontSize: 28, fontFamily: 'Brush Script MT, cursive', color: '#1e293b', lineHeight: 1 }}>
                  A. Müller
                </span>
              </div>
              <div style={{ marginTop: 6, fontSize: 12, fontFamily: 'sans-serif', color: '#475569' }}>
                Andreas Müller, Managing Director<br />GreenFleet Mobility AG · {today()}
              </div>
            </div>
            <div>
              <div style={{ fontSize: 11, fontFamily: 'sans-serif', fontWeight: 600, color: '#94a3b8',
                textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 8 }}>For the Lessee</div>
              <div style={{ borderBottom: '1.5px solid #334155', paddingBottom: 6, minHeight: 48,
                display: 'flex', alignItems: 'flex-end' }}>
                {contract.signed
                  ? <span style={{ fontSize: 13, fontFamily: 'sans-serif', color: '#15803d', fontStyle: 'italic' }}>
                      Signed digitally via GreenFleet platform
                    </span>
                  : <span style={{ fontSize: 12, fontFamily: 'sans-serif', color: '#94a3b8', fontStyle: 'italic' }}>
                      Awaiting signature
                    </span>
                }
              </div>
              <div style={{ marginTop: 6, fontSize: 12, fontFamily: 'sans-serif', color: '#475569' }}>
                {contract.customerName}<br />{contract.customerCompany}
              </div>
            </div>
          </div>
        </DocSection>

        {/* Footer */}
        <div style={{ marginTop: 8, paddingTop: 16, borderTop: '1px solid #e2e8f0',
          fontSize: 11, fontFamily: 'sans-serif', color: '#94a3b8', textAlign: 'center', lineHeight: 1.6 }}>
          GreenFleet Mobility AG · Bahnhofstrasse 42, 8001 Zürich · +41 44 000 00 00 · legal@greenfleet.ch<br />
          This document constitutes a legally binding agreement under Swiss contract law (OR Art. 184 ff.).
        </div>
      </div>
    </div>
  );
}

// ─── Modal ────────────────────────────────────────────────────────────────────

function ContractModal({ number, onClose }: { number: string; onClose: () => void }) {
  const [contract, setContract]   = useState<FleetContractResponse | null>(null);
  const [vehicleMap, setVehicleMap] = useState<Record<string, Vehicle>>({});
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState('');
  const printRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    let alive = true;
    Promise.all([getFleetContract(number), vehiclesApi.list()])
      .then(([c, vehicles]) => {
        if (!alive) return;
        const map: Record<string, Vehicle> = {};
        for (const v of vehicles) map[v.vin] = v;
        setContract(c);
        setVehicleMap(map);
      })
      .catch(e => { if (alive) setError(e instanceof Error ? e.message : 'Failed to load contract'); })
      .finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [number]);

  const handlePrint = () => {
    const content = printRef.current;
    if (!content) return;
    const win = window.open('', '_blank', 'width=900,height=700');
    if (!win) return;
    win.document.write(`
      <html>
        <head>
          <title>Contract ${number}</title>
          <style>
            * { box-sizing: border-box; margin: 0; padding: 0; }
            body { font-family: Georgia, serif; background: white; }
            @media print { body { -webkit-print-color-adjust: exact; print-color-adjust: exact; } }
          </style>
        </head>
        <body>${content.innerHTML}</body>
      </html>
    `);
    win.document.close();
    win.focus();
    setTimeout(() => { win.print(); }, 400);
  };

  // Close on Escape
  useEffect(() => {
    const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [onClose]);

  return (
    <div
      onClick={e => { if (e.target === e.currentTarget) onClose(); }}
      style={{
        position: 'fixed', inset: 0, zIndex: 1000,
        background: 'rgba(0,0,0,0.6)',
        display: 'flex', alignItems: 'flex-start', justifyContent: 'center',
        padding: '24px 16px', overflowY: 'auto',
      }}
    >
      <div style={{ width: '100%', maxWidth: 860, display: 'flex', flexDirection: 'column', gap: 12 }}>

        {/* Toolbar */}
        <div style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          background: 'white', borderRadius: 10, padding: '12px 18px',
          boxShadow: '0 4px 20px rgba(0,0,0,0.2)',
        }}>
          <div>
            <div style={{ fontWeight: 700, fontSize: 15, color: '#0f172a' }}>Fleet Leasing Agreement</div>
            <div style={{ fontSize: 11, fontFamily: 'monospace', color: '#94a3b8', marginTop: 2 }}>{number}</div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <button
              onClick={handlePrint}
              disabled={loading || !!error}
              style={{
                display: 'flex', alignItems: 'center', gap: 7,
                padding: '8px 16px', fontSize: 13, fontWeight: 600,
                background: '#0f172a', color: 'white', border: 'none', borderRadius: 7,
                cursor: loading || error ? 'not-allowed' : 'pointer',
                opacity: loading || error ? 0.5 : 1,
              }}
            >
              <IconPrint /> Print / Save as PDF
            </button>
            <button
              onClick={onClose}
              style={{
                padding: '8px 14px', fontSize: 13, fontWeight: 600,
                background: 'none', color: '#64748b',
                border: '1px solid #e2e8f0', borderRadius: 7, cursor: 'pointer',
              }}
            >
              Close ×
            </button>
          </div>
        </div>

        {/* Body */}
        {loading && (
          <div style={{ background: 'white', borderRadius: 10, padding: '64px 0',
            textAlign: 'center', color: '#64748b', fontSize: 14 }}>
            Loading contract…
          </div>
        )}

        {error && (
          <div style={{ background: 'white', borderRadius: 10, padding: '24px',
            color: '#dc2626', fontSize: 14, textAlign: 'center' }}>
            {error}
          </div>
        )}

        {!loading && !error && contract && (
          <div ref={printRef}>
            <ContractDocument contract={contract} vehicleMap={vehicleMap} />
          </div>
        )}
      </div>
    </div>
  );
}

// ─── Main page ────────────────────────────────────────────────────────────────

export default function ContractsPage() {
  const [contracts, setContracts]       = useState<FleetContractResponse[]>([]);
  const [search, setSearch]             = useState('');
  const [loading, setLoading]           = useState(true);
  const [error, setError]               = useState('');
  const [viewingNumber, setViewingNumber] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      setContracts(await listFleetContracts());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load contracts');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const q = search.toLowerCase();
  const filtered = contracts.filter(c =>
    c.number.toLowerCase().includes(q) ||
    c.customerName.toLowerCase().includes(q) ||
    c.customerCompany.toLowerCase().includes(q),
  );

  const signed   = contracts.filter(c => c.signed).length;
  const unsigned = contracts.length - signed;

  return (
    <div style={{ padding: 'var(--space-6)', minHeight: '100%', background: 'var(--color-bg)' }}>

      {viewingNumber && (
        <ContractModal number={viewingNumber} onClose={() => setViewingNumber(null)} />
      )}

      <PageHeader
        kicker="Admin · Sales"
        title="Fleet Contracts"
        subtitle="All fleet leasing agreements created in the system"
        badge={loading ? undefined : `${contracts.length} total`}
        action={
          <button
            onClick={load}
            style={{
              display: 'flex', alignItems: 'center', gap: 6,
              border: '1px solid var(--color-border)', background: 'var(--color-surface)',
              color: 'var(--color-text-muted)', borderRadius: 'var(--radius-sm)',
              padding: '6px 12px', fontSize: 13, cursor: 'pointer',
              transition: 'all var(--transition-fast)',
            }}
            onMouseEnter={e => { e.currentTarget.style.color = 'var(--color-brand)'; e.currentTarget.style.borderColor = 'var(--color-brand)'; }}
            onMouseLeave={e => { e.currentTarget.style.color = 'var(--color-text-muted)'; e.currentTarget.style.borderColor = 'var(--color-border)'; }}
          >
            <IconRefresh /> Refresh
          </button>
        }
      />

      {/* Summary chips */}
      {!loading && contracts.length > 0 && (
        <div style={{ display: 'flex', gap: 10, marginBottom: 'var(--space-6)' }}>
          {[
            { label: 'Signed',  value: signed,   bg: '#f0fdf4', color: '#15803d', border: '#86efac' },
            { label: 'Pending', value: unsigned, bg: '#fefce8', color: '#a16207', border: '#fde68a' },
          ].map(({ label, value, bg, color, border }) => (
            <div key={label} style={{
              padding: '6px 14px', borderRadius: 20, fontSize: 13, fontWeight: 600,
              background: bg, color, border: `1px solid ${border}`,
            }}>
              {value} {label}
            </div>
          ))}
        </div>
      )}

      {/* Search bar */}
      <div style={{
        background: 'var(--color-surface)', borderRadius: 'var(--radius-md)',
        padding: '9px 14px', marginBottom: 'var(--space-6)',
        display: 'flex', alignItems: 'center', gap: 10,
        border: '1px solid var(--color-border)', boxShadow: 'var(--shadow-xs)',
      }}>
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor"
          strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
          style={{ color: 'var(--color-text-faint)', flexShrink: 0 }}>
          <circle cx="11" cy="11" r="8" /><path d="M21 21l-4.35-4.35" />
        </svg>
        <input
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search by contract number, customer or company…"
          style={{
            flex: 1, border: 'none', outline: 'none',
            fontSize: 'var(--text-base)', background: 'transparent', color: 'var(--color-text)',
          }}
        />
        {search && (
          <button onClick={() => setSearch('')} style={{
            border: 'none', background: 'none', cursor: 'pointer',
            color: 'var(--color-text-faint)', fontSize: 18, lineHeight: 1, padding: 0,
          }}>×</button>
        )}
      </div>

      {error && (
        <div style={{
          marginBottom: 16, padding: '10px 14px', background: '#fef2f2',
          border: '1px solid #fca5a5', borderRadius: 6, color: '#dc2626', fontSize: 14,
        }}>
          {error}
        </div>
      )}

      {loading ? (
        <div style={{ padding: '48px 0', textAlign: 'center', color: 'var(--color-text-muted)', fontSize: 14 }}>
          Loading contracts…
        </div>
      ) : filtered.length === 0 ? (
        <div style={{
          display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
          gap: 12, padding: '72px 32px',
          background: 'var(--color-surface)', borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--color-border)', boxShadow: 'var(--shadow-sm)',
        }}>
          <IconFile />
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontWeight: 600, fontSize: 'var(--text-md)', color: 'var(--color-text-muted)', marginBottom: 4 }}>
              {search ? 'No contracts match your search' : 'No contracts yet'}
            </div>
            <div style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-faint)' }}>
              {search ? 'Try a different contract number or customer name.' : 'Fleet contracts will appear here once created.'}
            </div>
          </div>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: 16 }}>
          {filtered.map(c => (
            <ContractCard key={c.number} contract={c} showCustomer onView={() => setViewingNumber(c.number)} />
          ))}
        </div>
      )}
    </div>
  );
}
