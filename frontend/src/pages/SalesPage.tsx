import { useState, useEffect, useRef, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import PageHeader from '../components/PageHeader';
import { createFleetContract, getFleetContract, signFleetContract, releaseFleetBundle } from '../api/sales';
import type { FleetContractResponse } from '../api/sales';

// ─── Unsplash image resolution ────────────────────────────────────────────────
const UNSPLASH_KEY = 'YOUR_UNSPLASH_ACCESS_KEY';
const imageCache: Record<string, string | null> = {};
async function resolveImage(brand: string, model: string): Promise<string | null> {
  const key = `${brand}::${model}`;
  if (key in imageCache) return imageCache[key];
  const queries = [`${brand} ${model} electric car`, `${brand} electric car`, 'electric vehicle car road'];
  for (const q of queries) {
    try {
      const res = await fetch(
        `https://api.unsplash.com/search/photos?query=${encodeURIComponent(q)}&client_id=${UNSPLASH_KEY}&per_page=1`,
      );
      if (!res.ok) continue;
      const data = await res.json();
      const url: string | undefined = data.results?.[0]?.urls?.small;
      if (url) { imageCache[key] = url; return url; }
    } catch { /* try next */ }
  }
  imageCache[key] = null;
  return null;
}

// ─── Types ────────────────────────────────────────────────────────────────────
interface SalesVehicle {
  vin: string;
  brand: string;
  model: string;
  vehicleType: string;
  rangeKm: number;
  co2SavingsKgPerYear: number;
  monthlyInstallmentCents: number;
  currency: string;
}
interface SalesState {
  bundleType: string;
  leaseTermMonths: number;
  vehicles: SalesVehicle[];
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
const BUNDLE_LABELS: Record<string, string> = {
  BALANCED: 'Balanced', ECO: 'Eco', ECONOMIC: 'Economic', PREMIUM: 'Premium', CUSTOM: 'Custom',
};
function fmt(cents: number, currency = 'CHF') {
  return `${currency} ${(cents / 100).toLocaleString('de-CH', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}
function today() {
  return new Date().toLocaleDateString('en-GB', { day: 'numeric', month: 'long', year: 'numeric' });
}

const inputStyle: React.CSSProperties = {
  width: '100%', boxSizing: 'border-box', padding: '9px 12px', fontSize: 14,
  border: '1px solid var(--color-border)', borderRadius: 6,
  background: 'var(--color-surface)', color: 'var(--color-text)', fontFamily: 'inherit',
};

// ─── Vehicle image thumbnail ──────────────────────────────────────────────────
function VehicleThumb({ brand, model, size = 64 }: { brand: string; model: string; size?: number }) {
  const [img, setImg] = useState<string | null | 'loading'>('loading');
  useEffect(() => {
    let alive = true;
    resolveImage(brand, model).then(url => { if (alive) setImg(url); });
    return () => { alive = false; };
  }, [brand, model]);
  return (
    <div style={{ width: size, height: Math.round(size * 0.65), borderRadius: 6, overflow: 'hidden',
      flexShrink: 0, background: 'var(--color-border-light)', position: 'relative' }}>
      {img === 'loading' && <div style={{ position: 'absolute', inset: 0, background: 'var(--color-border)', opacity: 0.4 }} />}
      {img && img !== 'loading' && <img src={img} alt={`${brand} ${model}`} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />}
      {img === null && <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center',
        justifyContent: 'center', fontSize: 10, fontWeight: 700, color: 'var(--color-text-faint)', letterSpacing: 1 }}>EV</div>}
    </div>
  );
}

// ─── Vehicle stat card ────────────────────────────────────────────────────────
function VehicleCard({ v, index }: { v: SalesVehicle; index: number }) {
  return (
    <div style={{ display: 'flex', gap: 14, padding: '14px 20px',
      borderBottom: '1px solid var(--color-border-light)', alignItems: 'flex-start' }}>
      <VehicleThumb brand={v.brand} model={v.model} size={80} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
          <div>
            <span style={{ fontWeight: 700, fontSize: 14, color: 'var(--color-text)' }}>{v.brand} {v.model}</span>
            <span style={{ marginLeft: 8, fontSize: 11, color: 'var(--color-text-faint)',
              background: 'var(--color-bg)', border: '1px solid var(--color-border)', borderRadius: 4,
              padding: '1px 6px', fontWeight: 500 }}>{v.vehicleType}</span>
          </div>
          <div style={{ fontWeight: 700, fontSize: 14, color: '#0891b2', flexShrink: 0, marginLeft: 8 }}>
            {fmt(v.monthlyInstallmentCents, v.currency)}<span style={{ fontWeight: 400, fontSize: 11, color: 'var(--color-text-muted)' }}> /mo</span>
          </div>
        </div>
        <div style={{ fontSize: 11, fontFamily: 'monospace', color: 'var(--color-text-disabled)', marginTop: 2 }}>
          Vehicle {index + 1} · VIN {v.vin}
        </div>
        <div style={{ display: 'flex', gap: 16, marginTop: 8 }}>
          {[
            { label: 'Range',     value: `${v.rangeKm} km`,                         color: '#2563eb' },
            { label: 'CO₂ saved', value: `${v.co2SavingsKgPerYear.toFixed(0)} kg/yr`, color: '#16a34a' },
            { label: 'Powertrain', value: 'Battery Electric',                         color: '#7c3aed' },
          ].map(({ label, value, color }) => (
            <div key={label}>
              <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--color-text-faint)',
                textTransform: 'uppercase', letterSpacing: 0.5 }}>{label}</div>
              <div style={{ fontSize: 12, fontWeight: 600, color, marginTop: 1 }}>{value}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

// ─── Signature canvas modal ───────────────────────────────────────────────────
function SignatureModal({ onConfirm, onCancel }: {
  onConfirm: (dataUrl: string) => void;
  onCancel: () => void;
}) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const drawing = useRef(false);
  const hasStrokes = useRef(false);

  const getPos = (e: React.MouseEvent | React.TouchEvent, canvas: HTMLCanvasElement) => {
    const rect = canvas.getBoundingClientRect();
    if ('touches' in e) {
      return { x: e.touches[0].clientX - rect.left, y: e.touches[0].clientY - rect.top };
    }
    return { x: (e as React.MouseEvent).clientX - rect.left, y: (e as React.MouseEvent).clientY - rect.top };
  };

  const startDraw = useCallback((e: React.MouseEvent | React.TouchEvent) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d')!;
    const pos = getPos(e, canvas);
    ctx.beginPath();
    ctx.moveTo(pos.x, pos.y);
    drawing.current = true;
    hasStrokes.current = true;
    e.preventDefault();
  }, []);

  const draw = useCallback((e: React.MouseEvent | React.TouchEvent) => {
    if (!drawing.current) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d')!;
    ctx.strokeStyle = '#1e293b';
    ctx.lineWidth = 2;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    const pos = getPos(e, canvas);
    ctx.lineTo(pos.x, pos.y);
    ctx.stroke();
    e.preventDefault();
  }, []);

  const stopDraw = useCallback(() => { drawing.current = false; }, []);

  const clear = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    canvas.getContext('2d')!.clearRect(0, 0, canvas.width, canvas.height);
    hasStrokes.current = false;
  };

  const confirm = () => {
    if (!hasStrokes.current) return;
    onConfirm(canvasRef.current!.toDataURL('image/png'));
  };

  return (
    <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)', zIndex: 1000,
      display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{ background: 'white', borderRadius: 12, padding: 28, width: 480, boxShadow: '0 20px 60px rgba(0,0,0,0.3)' }}>
        <div style={{ fontWeight: 700, fontSize: 18, marginBottom: 4, color: '#0f172a' }}>Sign the Contract</div>
        <div style={{ fontSize: 13, color: '#64748b', marginBottom: 18, lineHeight: 1.5 }}>
          Draw your signature below. By signing, you confirm acceptance of all terms and conditions
          of this fleet leasing agreement.
        </div>

        <canvas
          ref={canvasRef}
          width={424}
          height={160}
          onMouseDown={startDraw}
          onMouseMove={draw}
          onMouseUp={stopDraw}
          onMouseLeave={stopDraw}
          onTouchStart={startDraw}
          onTouchMove={draw}
          onTouchEnd={stopDraw}
          style={{ display: 'block', width: '100%', border: '1.5px solid #cbd5e1',
            borderRadius: 8, cursor: 'crosshair', background: '#f8fafc',
            touchAction: 'none' }}
        />
        <div style={{ fontSize: 11, color: '#94a3b8', marginTop: 6, textAlign: 'center' }}>
          Sign above
        </div>

        <div style={{ display: 'flex', gap: 10, marginTop: 20 }}>
          <button onClick={clear} style={{ flex: 1, padding: '10px', fontSize: 14, cursor: 'pointer',
            background: 'none', border: '1px solid #cbd5e1', borderRadius: 6, color: '#475569' }}>
            Clear
          </button>
          <button onClick={onCancel} style={{ flex: 1, padding: '10px', fontSize: 14, cursor: 'pointer',
            background: 'none', border: '1px solid #cbd5e1', borderRadius: 6, color: '#475569' }}>
            Cancel
          </button>
          <button onClick={confirm} style={{ flex: 2, padding: '10px', fontSize: 14, fontWeight: 600,
            cursor: 'pointer', background: '#16a34a', color: 'white', border: 'none', borderRadius: 6 }}>
            Confirm Signature
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Main page ────────────────────────────────────────────────────────────────
export default function SalesPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const state = location.state as SalesState | null;

  // Form state
  const [name,    setName]    = useState('');
  const [company, setCompany] = useState('');
  const [email,   setEmail]   = useState('');
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  // Contract state
  const [contract,      setContract]      = useState<FleetContractResponse | null>(null);
  const [showSigModal,  setShowSigModal]  = useState(false);
  const [signing,       setSigning]       = useState(false);
  const [signatureImg,  setSignatureImg]  = useState<string | null>(null);
  const [signed,        setSigned]        = useState(false);

  if (!state) {
    return (
      <div style={{ padding: 40 }}>
        <p style={{ color: 'var(--color-text-muted)' }}>No bundle selected. Go to Fleet Consulting first.</p>
        <button onClick={() => navigate('/dashboard/fleet-consulting')}
          style={{ marginTop: 16, padding: '8px 16px', cursor: 'pointer' }}>
          Back to Fleet Consulting
        </button>
      </div>
    );
  }

  const vehicleInfo: Record<string, SalesVehicle> = {};
  for (const v of state.vehicles) vehicleInfo[v.vin] = v;

  const totalInstallment = state.vehicles.reduce((s, v) => s + v.monthlyInstallmentCents, 0);
  const currency = state.vehicles[0]?.currency ?? 'CHF';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true); setError('');
    try {
      const number = await createFleetContract({
        customerName: name, customerCompany: company, customerEmail: email,
        leaseTermMonths: state.leaseTermMonths, bundleType: state.bundleType,
        vehicles: state.vehicles.map(v => ({ vin: v.vin, monthlyInstallmentCents: v.monthlyInstallmentCents, currency: v.currency })),
      });
      setContract(await getFleetContract(number));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Request failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSignConfirm = async (dataUrl: string) => {
    setShowSigModal(false);
    setSigning(true); setError('');
    try {
      await signFleetContract(contract!.number);
      setSignatureImg(dataUrl);
      setSigned(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Sign failed');
    } finally {
      setSigning(false);
    }
  };

  // ── Signed confirmation ───────────────────────────────────────────────────
  if (signed && contract) {
    return (
      <div style={{ padding: '24px 28px', background: 'var(--color-bg)', minHeight: '100%' }}>
        {showSigModal && <SignatureModal onConfirm={handleSignConfirm} onCancel={() => setShowSigModal(false)} />}
        <PageHeader kicker="Sales · Executed" title="Contract Signed" subtitle={`Agreement ${contract.number} is fully executed and binding.`} />

        <div style={{ maxWidth: 760 }}>
          {/* Confirmation banner */}
          <div style={{ marginBottom: 24, padding: '16px 20px', background: '#f0fdf4',
            border: '1px solid #86efac', borderRadius: 10, display: 'flex', gap: 14, alignItems: 'center' }}>
            <div style={{ width: 36, height: 36, borderRadius: '50%', background: '#16a34a',
              display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="20 6 9 17 4 12" />
              </svg>
            </div>
            <div>
              <div style={{ fontWeight: 700, color: '#15803d', fontSize: 15 }}>Fleet leasing agreement executed</div>
              <div style={{ fontSize: 13, color: '#166534', marginTop: 2 }}>
                Signed on {today()} · Contract reference {contract.number}
              </div>
            </div>
          </div>

          <ContractDocument contract={contract} vehicleInfo={vehicleInfo} signatureImg={signatureImg} />
        </div>
      </div>
    );
  }

  // ── Contract review (pre-sign) ────────────────────────────────────────────
  if (contract) {
    return (
      <div style={{ padding: '24px 28px', background: 'var(--color-bg)', minHeight: '100%' }}>
        {showSigModal && <SignatureModal onConfirm={handleSignConfirm} onCancel={() => setShowSigModal(false)} />}
        <PageHeader kicker="Sales" title="Review & Sign" subtitle="Review the fleet leasing agreement below. Once satisfied, add your signature to execute." />

        {error && <ErrorBanner msg={error} />}

        <div style={{ maxWidth: 760 }}>
          <ContractDocument contract={contract} vehicleInfo={vehicleInfo} signatureImg={null} />

          <div style={{ marginTop: 24, display: 'flex', alignItems: 'center', gap: 16 }}>
            <button onClick={() => setShowSigModal(true)} disabled={signing}
              style={{ padding: '13px 32px', fontSize: 15, fontWeight: 700, cursor: signing ? 'wait' : 'pointer',
                background: '#16a34a', color: 'white', border: 'none', borderRadius: 8,
                boxShadow: '0 2px 8px rgba(22,163,74,0.35)', opacity: signing ? 0.7 : 1 }}>
              {signing ? 'Processing…' : 'Sign Contract'}
            </button>
            <span style={{ fontSize: 13, color: 'var(--color-text-faint)' }}>
              A signature pad will appear for your handwritten signature.
            </span>
          </div>
        </div>
      </div>
    );
  }

  // ── Customer details form ─────────────────────────────────────────────────
  return (
    <div style={{ padding: '24px 28px', background: 'var(--color-bg)', minHeight: '100%' }}>
      <PageHeader
        kicker="Sales"
        title="New Fleet Contract"
        subtitle={`${BUNDLE_LABELS[state.bundleType] ?? state.bundleType} bundle · ${state.vehicles.length} vehicle${state.vehicles.length !== 1 ? 's' : ''} · ${state.leaseTermMonths}-month lease`}
      />

      {error && <ErrorBanner msg={error} />}

      <div style={{ display: 'grid', gridTemplateColumns: '400px 1fr', gap: 24, maxWidth: 900 }}>
        {/* Left: form */}
        <div>
          <div style={{ background: 'var(--color-surface)', borderRadius: 10,
            border: '1px solid var(--color-border)', padding: '24px', marginBottom: 16 }}>
            <div style={{ fontWeight: 700, fontSize: 16, marginBottom: 4 }}>Lessee Details</div>
            <div style={{ fontSize: 13, color: 'var(--color-text-muted)', marginBottom: 20, lineHeight: 1.5 }}>
              Enter the details of the company and contact person who will be entering into this fleet leasing agreement.
            </div>
            <form onSubmit={handleSubmit}>
              {[
                { label: 'Contact name',    value: name,    set: setName,    type: 'text',  placeholder: 'Jane Smith' },
                { label: 'Company name',    value: company, set: setCompany, type: 'text',  placeholder: 'Acme AG' },
                { label: 'Business email',  value: email,   set: setEmail,   type: 'email', placeholder: 'jane@acme.com' },
              ].map(({ label, value, set, type, placeholder }) => (
                <div key={label} style={{ marginBottom: 14 }}>
                  <label style={{ display: 'block', fontSize: 12, fontWeight: 600, color: 'var(--color-text-secondary)',
                    marginBottom: 5, textTransform: 'uppercase', letterSpacing: 0.5 }}>{label}</label>
                  <input required type={type} value={value} onChange={e => set(e.target.value)}
                    placeholder={placeholder} style={inputStyle} />
                </div>
              ))}
              <div style={{ marginTop: 20, display: 'flex', flexDirection: 'column', gap: 8 }}>
                <button type="submit" disabled={loading}
                  style={{ width: '100%', padding: '12px', fontSize: 15, fontWeight: 700,
                    cursor: loading ? 'wait' : 'pointer', background: '#0891b2', color: 'white',
                    border: 'none', borderRadius: 8, opacity: loading ? 0.7 : 1,
                    boxShadow: '0 2px 8px rgba(8,145,178,0.3)' }}>
                  {loading ? 'Creating contract…' : 'Add Details to Contract'}
                </button>
                <button type="button" disabled={loading}
                  onClick={async () => {
                    if (!confirm('Cancel this reservation? The vehicles will be released back to the catalogue.')) return;
                    await releaseFleetBundle(state.vehicles.map(v => v.vin));
                    navigate('/dashboard/fleet-consulting');
                  }}
                  style={{ width: '100%', padding: '10px', fontSize: 13, fontWeight: 500,
                    cursor: 'pointer', background: 'none', color: 'var(--color-text-muted)',
                    border: '1px solid var(--color-border)', borderRadius: 8 }}>
                  Cancel reservation
                </button>
              </div>
            </form>
          </div>

          {/* Terms note */}
          <div style={{ fontSize: 12, color: 'var(--color-text-faint)', lineHeight: 1.6, padding: '0 4px' }}>
            By proceeding you confirm that the lessee has been informed of and accepts the general terms
            and conditions of GreenFleet's EV fleet leasing programme. All contracts are subject to
            final credit approval by the risk management team.
          </div>
        </div>

        {/* Right: vehicle preview */}
        <div style={{ background: 'var(--color-surface)', borderRadius: 10,
          border: '1px solid var(--color-border)', overflow: 'hidden' }}>
          <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--color-border-light)',
            display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 12, fontWeight: 700, color: 'var(--color-text-faint)',
              textTransform: 'uppercase', letterSpacing: 0.6 }}>Selected Fleet</span>
            <span style={{ fontSize: 13, fontWeight: 600, color: '#0891b2' }}>
              {fmt(totalInstallment, currency)}<span style={{ fontWeight: 400, color: 'var(--color-text-muted)', fontSize: 11 }}> /mo total</span>
            </span>
          </div>
          {state.vehicles.map((v, i) => <VehicleCard key={v.vin} v={v} index={i} />)}
          <div style={{ padding: '14px 20px', background: 'var(--color-bg)',
            display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
            {[
              { label: 'Total CO₂ saved', value: `${state.vehicles.reduce((s, v) => s + v.co2SavingsKgPerYear, 0).toFixed(0)} kg/yr`, color: '#16a34a' },
              { label: 'Lease term',      value: `${state.leaseTermMonths} months`,  color: '#0891b2' },
              { label: 'Bundle type',     value: BUNDLE_LABELS[state.bundleType] ?? state.bundleType, color: '#7c3aed' },
            ].map(({ label, value, color }) => (
              <div key={label} style={{ textAlign: 'center', padding: '8px', background: 'var(--color-surface)',
                borderRadius: 6, border: '1px solid var(--color-border-light)' }}>
                <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--color-text-faint)',
                  textTransform: 'uppercase', letterSpacing: 0.5 }}>{label}</div>
                <div style={{ fontWeight: 700, fontSize: 13, color, marginTop: 3 }}>{value}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

// ─── Contract document ─────────────────────────────────────────────────────────
function ContractDocument({ contract, vehicleInfo, signatureImg }: {
  contract: FleetContractResponse;
  vehicleInfo: Record<string, SalesVehicle>;
  signatureImg: string | null;
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
        <Section title="1. Parties to the Agreement">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
            <PartyBox title="Lessor" lines={['GreenFleet Mobility AG', 'Bahnhofstrasse 42, 8001 Zürich', 'Switzerland', 'CHE-123.456.789 MwSt']} />
            <PartyBox title="Lessee" lines={[contract.customerName, contract.customerCompany, contract.customerEmail]} />
          </div>
        </Section>

        {/* Lease summary */}
        <Section title="2. Lease Summary">
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
        </Section>

        {/* Vehicle schedule */}
        <Section title="3. Vehicle Schedule">
          <p style={bodyText}>
            The following electric vehicles are covered under this agreement. Each vehicle is supplied
            with a full battery health certification and all applicable warranties as detailed in the
            GreenFleet Standard Vehicle Specification.
          </p>
          <div style={{ marginTop: 12, border: '1px solid #e2e8f0', borderRadius: 8, overflow: 'hidden' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontFamily: 'sans-serif', fontSize: 13 }}>
              <thead>
                <tr style={{ background: '#f1f5f9' }}>
                  {['#', 'Vehicle', 'Type', 'Range', 'CO₂ saved / yr', 'Monthly'].map(h => (
                    <th key={h} style={{ padding: '10px 14px', textAlign: 'left', fontWeight: 600,
                      fontSize: 11, color: '#64748b', textTransform: 'uppercase', letterSpacing: 0.4,
                      borderBottom: '1px solid #e2e8f0' }}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {contract.vehicles.map((v, i) => {
                  const info = vehicleInfo[v.vin];
                  return (
                    <tr key={v.vin} style={{ borderBottom: i < contract.vehicles.length - 1 ? '1px solid #f1f5f9' : 'none' }}>
                      <td style={{ padding: '10px 14px', color: '#94a3b8', fontFamily: 'monospace', fontSize: 11 }}>{i + 1}</td>
                      <td style={{ padding: '10px 14px' }}>
                        <div style={{ fontWeight: 600, color: '#0f172a' }}>{info?.brand ?? ''} {info?.model ?? ''}</div>
                        <div style={{ fontSize: 10, color: '#94a3b8', fontFamily: 'monospace' }}>VIN {v.vin}</div>
                      </td>
                      <td style={{ padding: '10px 14px', color: '#475569' }}>{info?.vehicleType ?? '—'}</td>
                      <td style={{ padding: '10px 14px', color: '#2563eb', fontWeight: 600 }}>{info?.rangeKm ?? '—'} km</td>
                      <td style={{ padding: '10px 14px', color: '#16a34a', fontWeight: 600 }}>{info?.co2SavingsKgPerYear?.toFixed(0) ?? '—'} kg</td>
                      <td style={{ padding: '10px 14px', fontWeight: 700, color: '#0891b2' }}>{fmt(v.monthlyInstallmentCents, v.currency)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Section>

        {/* Signature block */}
        <Section title="4. Signatures">
          <p style={bodyText}>
            By signing below, both parties confirm they have read, understood, and agreed to all terms
            and conditions set forth in this Fleet Leasing Agreement.
          </p>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 32, marginTop: 20 }}>
            {/* Lessor signature — pre-filled */}
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

            {/* Lessee signature */}
            <div>
              <div style={{ fontSize: 11, fontFamily: 'sans-serif', fontWeight: 600, color: '#94a3b8',
                textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 8 }}>For the Lessee</div>
              <div style={{ borderBottom: '1.5px solid #334155', paddingBottom: 6, minHeight: 48,
                display: 'flex', alignItems: 'flex-end' }}>
                {signatureImg
                  ? <img src={signatureImg} alt="Lessee signature" style={{ maxHeight: 48, maxWidth: '100%' }} />
                  : <span style={{ fontSize: 12, fontFamily: 'sans-serif', color: '#94a3b8', fontStyle: 'italic' }}>
                      Awaiting signature
                    </span>
                }
              </div>
              <div style={{ marginTop: 6, fontSize: 12, fontFamily: 'sans-serif', color: '#475569' }}>
                {contract.customerName}<br />{contract.customerCompany}{signatureImg ? ` · ${today()}` : ''}
              </div>
            </div>
          </div>
        </Section>

        {/* Collapsible T&C */}
        <CollapsibleTnC />

        {/* Footer */}
        <div style={{ marginTop: 32, paddingTop: 16, borderTop: '1px solid #e2e8f0',
          fontSize: 11, fontFamily: 'sans-serif', color: '#94a3b8', textAlign: 'center', lineHeight: 1.6 }}>
          GreenFleet Mobility AG · Bahnhofstrasse 42, 8001 Zürich · +41 44 000 00 00 · legal@greenfleet.ch<br />
          This document constitutes a legally binding agreement under Swiss contract law (OR Art. 184 ff.).
        </div>
      </div>
    </div>
  );
}

// ─── Collapsible T&C ──────────────────────────────────────────────────────────
function CollapsibleTnC() {
  const [open, setOpen] = useState(false);
  const clauses = [
    ['5.1 Maintenance', 'The Lessor is responsible for scheduled maintenance and battery health checks at 12-month intervals. Tyres, windscreens, and consumables are the responsibility of the Lessee.'],
    ['5.2 Insurance',   'The Lessee shall maintain comprehensive motor insurance for each vehicle throughout the lease term. Proof of insurance must be provided to the Lessor within 7 days of contract execution.'],
    ['5.3 Usage',       'The vehicles shall be used solely for lawful business purposes within Switzerland and the Schengen Area. Cross-border usage outside these regions requires prior written consent from the Lessor.'],
    ['5.4 Early termination', 'Early termination is subject to a fee equivalent to 3 monthly instalments per vehicle, plus the outstanding balance of the remaining lease period.'],
    ["5.5 Sustainability", "As part of GreenFleet's commitment to sustainable mobility, the Lessee agrees to charge vehicles primarily using renewable energy sources and to report annual mileage upon request."],
  ];
  return (
    <div style={{ marginBottom: 28, fontFamily: 'sans-serif' }}>
      <button
        onClick={() => setOpen(o => !o)}
        style={{
          display: 'flex', alignItems: 'center', gap: 8, background: 'none', border: 'none',
          cursor: 'pointer', padding: '10px 0', width: '100%', textAlign: 'left',
          borderTop: '1px solid #f1f5f9', borderBottom: open ? 'none' : '1px solid #f1f5f9',
        }}
      >
        <span style={{ fontWeight: 700, fontSize: 15, color: '#0f172a' }}>5. General Terms &amp; Conditions</span>
        <span style={{ marginLeft: 'auto', fontSize: 12, color: '#94a3b8', fontWeight: 500 }}>
          {open ? 'Hide ▲' : 'Show ▼'}
        </span>
      </button>
      {open && (
        <div style={{ paddingTop: 12, paddingBottom: 20, borderBottom: '1px solid #f1f5f9',
          display: 'flex', flexDirection: 'column', gap: 12 }}>
          {clauses.map(([title, text]) => (
            <div key={title}>
              <div style={{ fontWeight: 700, fontSize: 13, color: '#1e293b', marginBottom: 3 }}>{title}</div>
              <p style={{ fontSize: 13, color: '#475569', lineHeight: 1.7, margin: 0, fontFamily: 'sans-serif' }}>{text}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

// ─── Small reusable pieces ────────────────────────────────────────────────────
const bodyText: React.CSSProperties = {
  fontSize: 13, color: '#475569', lineHeight: 1.7, margin: '0 0 4px', fontFamily: 'sans-serif',
};

function Section({ title, children }: { title: string; children: React.ReactNode }) {
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

function ErrorBanner({ msg }: { msg: string }) {
  return (
    <div style={{ marginBottom: 16, padding: '10px 14px', background: '#fef2f2',
      border: '1px solid #fca5a5', borderRadius: 6, color: '#dc2626', fontSize: 14 }}>
      {msg}
    </div>
  );
}
