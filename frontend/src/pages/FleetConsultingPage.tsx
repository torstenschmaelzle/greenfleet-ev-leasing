import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRecommendations } from '../api/fleetConsulting';
import type { FleetBundleRecommendation, FleetBundle, BundleVehicle } from '../api/fleetConsulting';
import { reserveFleetBundle } from '../api/sales';
import PageHeader from '../components/PageHeader';

// ─── Unsplash image logic (unchanged) ───────────────────────────────────────
const UNSPLASH_KEY = 'YOUR_UNSPLASH_ACCESS_KEY';
const imageCache: Record<string, string | null> = {};

async function resolveImage(brand: string, model: string): Promise<string | null> {
  const key = `${brand}::${model}`;
  if (key in imageCache) return imageCache[key];
  const queries = [
    `${brand} ${model} electric car`,
    `${brand} electric car`,
    'electric vehicle car road',
    'modern car exterior',
  ];
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

// ─── Constants ──────────────────────────────────────────────────────────────
const CHARGING_TYPES = ['COMPANY_CHARGER', 'PUBLIC_STATION', 'MIXED'];
const VEHICLE_TYPES  = ['', 'SUV', 'SEDAN', 'VAN', 'COMPACT', 'ESTATE'];

const BUNDLE_META: Record<string, {
  label: string; accent: string; lightBg: string; border: string; desc: string; abbr: string;
}> = {
  BALANCED: { label: 'Balanced',  accent: '#0891b2', lightBg: '#ecfeff', border: '#a5f3fc', desc: 'Best overall score across range, cost & CO₂',          abbr: 'B' },
  ECO:      { label: 'Eco',       accent: '#16a34a', lightBg: '#f0fdf4', border: '#86efac', desc: 'Maximum CO₂ savings, greenest fleet',                   abbr: 'E' },
  ECONOMIC: { label: 'Economic',  accent: '#6d28d9', lightBg: '#f5f3ff', border: '#c4b5fd', desc: 'Lowest total cost of ownership',                        abbr: '$' },
  PREMIUM:  { label: 'Premium',   accent: '#b45309', lightBg: '#fefce8', border: '#fcd34d', desc: 'Maximum range & sustainability — cost not a constraint', abbr: 'P' },
  CUSTOM:   { label: 'Custom',    accent: '#475569', lightBg: '#f8fafc', border: '#cbd5e1', desc: 'Optimised for your personal weight settings',            abbr: 'C' },
};

// Intentional per-dimension accent colors for the scoring weight panel
const WEIGHT_META = [
  { key: 'range', label: 'Range', color: '#2563eb', desc: 'Prioritise vehicles with the highest km range' },
  { key: 'co2',   label: 'CO₂',  color: '#16a34a', desc: 'Favour vehicles with the biggest CO₂ savings' },
  { key: 'cost',  label: 'Cost',  color: '#7c3aed', desc: 'Minimise monthly leasing & charging cost' },
];

// ─── Helpers ─────────────────────────────────────────────────────────────────
function fmtChf(cents: number) {
  return `CHF ${(cents / 100).toLocaleString('de-CH', { minimumFractionDigits: 0, maximumFractionDigits: 0 })}`;
}

// ─── Inline SVG icons ────────────────────────────────────────────────────────

function IconChevron({ size = 16, color = 'var(--color-text-faint)' }: { size?: number; color?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 16 16" fill="none">
      <path d="M4 6l4 4 4-4" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

// ─── Sub-components ──────────────────────────────────────────────────────────

function VehicleImageCard({ v }: { v: BundleVehicle }) {
  const [img, setImg] = useState<string | null | 'loading'>('loading');
  useEffect(() => {
    let alive = true;
    resolveImage(v.brand, v.model).then(url => { if (alive) setImg(url); });
    return () => { alive = false; };
  }, [v.brand, v.model]);

  return (
    <div style={{
      background: 'var(--color-surface)',
      borderRadius: 'var(--radius-md)',
      border: '1px solid var(--color-border)',
      overflow: 'hidden',
      display: 'flex',
      flexDirection: 'column',
    }}>
      {/* Image */}
      <div style={{ position: 'relative', height: 105, background: 'var(--color-border-light)', flexShrink: 0 }}>
        {img === 'loading' && (
          <div style={{ position: 'absolute', inset: 0, background: 'var(--color-border)', opacity: 0.5 }} />
        )}
        {img && img !== 'loading' && (
          <img src={img} alt={`${v.brand} ${v.model}`}
            style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }} />
        )}
        {img === null && (
          <div style={{
            position: 'absolute', inset: 0, background: 'var(--color-bg)',
            display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 4,
          }}>
            <span style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-faint)', letterSpacing: 1.5 }}>EV</span>
            <span style={{ color: 'var(--color-text-faint)', fontSize: 'var(--text-xs)' }}>No image</span>
          </div>
        )}
        <span style={{
          position: 'absolute', top: 7, left: 7,
          background: 'rgba(15,23,42,0.72)', color: 'white',
          borderRadius: 4, padding: '2px 6px', fontSize: 10, fontWeight: 600,
          backdropFilter: 'blur(4px)', letterSpacing: 0.4,
        }}>{v.vehicleType}</span>
      </div>

      {/* Body */}
      <div style={{ padding: '11px 12px 13px', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div style={{ fontWeight: 700, fontSize: 'var(--text-sm)', color: 'var(--color-text)', lineHeight: 1.2 }}>{v.brand}</div>
        <div style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-muted)', marginBottom: 10 }}>{v.model}</div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 4, marginBottom: 11 }}>
          {[
            { k: 'Range',    v: `${v.rangeKm} km`,                          c: 'var(--color-text-secondary)' },
            { k: 'Monthly',  v: `${fmtChf(v.monthlyInstallmentCents)}`,      c: 'var(--color-text-secondary)' },
            { k: 'CO₂ saved', v: `${v.co2SavingsKgPerYear.toFixed(0)} kg/yr`, c: '#16a34a' },
          ].map(({ k, v: val, c }) => (
            <div key={k} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 'var(--text-xs)' }}>
              <span style={{ color: 'var(--color-text-faint)' }}>{k}</span>
              <span style={{ fontWeight: 600, color: c }}>{val}</span>
            </div>
          ))}
        </div>

        {/* Score bars */}
        <div style={{ marginTop: 'auto' }}>
          <div style={{
            fontSize: 9, fontWeight: 600, color: 'var(--color-text-faint)',
            textTransform: 'uppercase' as const, letterSpacing: 0.6, marginBottom: 6,
          }}>Scores</div>
          <div style={{ display: 'flex', gap: 5 }}>
            {[
              { label: 'Range', val: v.score.rangeScore, color: '#2563eb' },
              { label: 'CO₂',   val: v.score.co2Score,   color: '#16a34a' },
              { label: 'Cost',  val: v.score.costScore,   color: '#7c3aed' },
            ].map(({ label, val, color }) => (
              <div key={label} style={{ flex: 1 }}>
                <div style={{ height: 4, borderRadius: 2, background: 'var(--color-border-light)', overflow: 'hidden', marginBottom: 3 }}>
                  <div style={{ height: '100%', width: `${val * 100}%`, background: color, borderRadius: 2 }} />
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ fontSize: 9, color: 'var(--color-text-faint)' }}>{label}</span>
                  <span style={{ fontSize: 9, fontWeight: 700, color }}>{(val * 100).toFixed(0)}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ marginTop: 9, fontSize: 9, color: 'var(--color-text-disabled)', fontFamily: 'monospace', letterSpacing: 0.4 }}>
          VIN …{v.vin.slice(-6)}
        </div>
      </div>
    </div>
  );
}

function BundleCard({ bundle, expanded, onToggle, onReserve, reserving, reserveError }: {
  bundle: FleetBundle;
  expanded: boolean;
  onToggle: () => void;
  onReserve: () => void;
  reserving: boolean;
  reserveError: string;
}) {
  const meta = BUNDLE_META[bundle.bundleType] ?? {
    label: bundle.bundleType, accent: 'var(--color-text-muted)', lightBg: 'var(--color-bg)',
    border: 'var(--color-border)', desc: '', abbr: '?',
  };

  return (
    <div style={{
      borderRadius: 'var(--radius-lg)',
      border: '1px solid var(--color-border)',
      borderTop: `3px solid ${meta.accent}`,
      overflow: 'hidden',
      boxShadow: expanded ? 'var(--shadow-md)' : 'var(--shadow-xs)',
      transition: 'box-shadow var(--transition-normal)',
      background: 'var(--color-surface)',
    }}>
      {/* Card header */}
      <div style={{
        padding: '18px 22px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        borderBottom: '1px solid var(--color-border-light)',
        background: `linear-gradient(135deg, ${meta.lightBg} 0%, var(--color-surface) 55%)`,
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
          <div style={{
            width: 44, height: 44, borderRadius: 10,
            background: 'var(--color-surface)',
            border: `1.5px solid ${meta.border}`,
            boxShadow: `0 0 0 3px ${meta.lightBg}`,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontWeight: 800, fontSize: 16, color: meta.accent,
            flexShrink: 0,
          }}>{meta.abbr}</div>
          <div>
            <div style={{ fontWeight: 700, fontSize: 16, color: 'var(--color-text)', lineHeight: 1.2 }}>
              {meta.label} Bundle
            </div>
            <div style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-muted)', marginTop: 3 }}>
              {meta.desc}
            </div>
          </div>
        </div>

        {/* Vehicle count chip */}
        <div style={{
          background: meta.lightBg,
          border: `1px solid ${meta.border}`,
          borderRadius: 'var(--radius-md)',
          padding: '7px 16px',
          display: 'flex', alignItems: 'baseline', gap: 5,
          flexShrink: 0,
        }}>
          <span style={{ fontWeight: 800, fontSize: 18, color: meta.accent, lineHeight: 1 }}>
            {bundle.vehicles.length}
          </span>
          <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-muted)', fontWeight: 500 }}>
            EV{bundle.vehicles.length !== 1 ? 's' : ''}
          </span>
        </div>
      </div>

      {/* Metrics row */}
      <div style={{
        display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)',
        borderBottom: '1px solid var(--color-border-light)',
        background: 'var(--color-bg)',
      }}>
        {[
          { label: 'Monthly total',  val: fmtChf(bundle.tco.monthlyTotalCents),               sub: 'all vehicles' },
          { label: 'Annual total',   val: fmtChf(bundle.tco.annualTotalCents),                 sub: 'incl. charging' },
          { label: 'Lease / month',  val: fmtChf(bundle.tco.monthlyLeaseCostCents),            sub: 'fleet total' },
          { label: 'CO₂ saved / yr', val: `${bundle.totalCo2SavingsKgPerYear.toFixed(0)} kg`,  sub: 'vs. ICE fleet' },
        ].map(({ label, val, sub }, idx, arr) => (
          <div key={label} style={{
            padding: '14px 18px', textAlign: 'center' as const,
            borderRight: idx < arr.length - 1 ? '1px solid var(--color-border-light)' : 'none',
          }}>
            <div style={{
              fontSize: 10, fontWeight: 600, color: 'var(--color-text-faint)',
              textTransform: 'uppercase' as const, letterSpacing: 0.5, marginBottom: 6,
            }}>{label}</div>
            <div style={{ fontWeight: 700, fontSize: 'var(--text-base)', color: 'var(--color-text)', letterSpacing: -0.3 }}>{val}</div>
            <div style={{ fontSize: 10, color: 'var(--color-text-disabled)', marginTop: 3 }}>{sub}</div>
          </div>
        ))}
      </div>

      {/* Reserve button */}
      <div style={{ padding: '14px 22px', borderBottom: '1px solid var(--color-border-light)', display: 'flex', alignItems: 'center', gap: 14 }}>
        <button
          onClick={onReserve}
          disabled={reserving}
          style={{
            padding: '10px 24px', fontSize: 14, fontWeight: 600,
            cursor: reserving ? 'default' : 'pointer',
            background: reserving ? 'var(--color-brand-muted)' : meta.accent,
            color: 'white', border: 'none', borderRadius: 6,
            opacity: reserving ? 0.7 : 1,
            transition: 'opacity var(--transition-fast)',
          }}
        >
          {reserving ? 'Reserving vehicles…' : 'Reserve this bundle'}
        </button>
        {reserveError && (
          <span style={{ fontSize: 'var(--text-sm)', color: 'var(--color-error)', fontWeight: 500 }}>
            {reserveError}
          </span>
        )}
      </div>

      {/* Expand toggle */}
      <button
        onClick={onToggle}
        style={{
          width: '100%', background: 'none', border: 'none',
          padding: '11px 22px', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          borderBottom: expanded ? '1px solid var(--color-border-light)' : 'none',
          transition: 'background var(--transition-fast)',
        }}
        onMouseEnter={e => (e.currentTarget.style.background = 'var(--color-bg)')}
        onMouseLeave={e => (e.currentTarget.style.background = 'none')}
      >
        <span style={{ color: meta.accent, fontWeight: 600, fontSize: 'var(--text-sm)' }}>
          {expanded
            ? 'Collapse vehicles'
            : `View ${bundle.vehicles.length} vehicle${bundle.vehicles.length !== 1 ? 's' : ''}`}
        </span>
        <span style={{
          display: 'inline-block',
          transform: expanded ? 'rotate(180deg)' : 'none',
          transition: 'transform var(--transition-normal)',
        }}>
          <IconChevron color={meta.accent} />
        </span>
      </button>

      {/* Vehicles grid */}
      {expanded && (
        <div style={{
          padding: '18px 20px 22px',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(195px, 1fr))',
          gap: 14,
          background: 'var(--color-bg)',
        }}>
          {bundle.vehicles.map(v => (
            <VehicleImageCard key={v.vin} v={v} />
          ))}
        </div>
      )}
    </div>
  );
}

// ─── Scoring weight panel ────────────────────────────────────────────────────
function WeightPanel({ weights, onChange }: {
  weights: [number, number, number];
  onChange: (next: [number, number, number]) => void;
}) {
  const update = (idx: number, raw: number) => {
    const val = Math.max(0, Math.min(100, raw));
    const next = [...weights] as [number, number, number];
    const j = (idx + 1) % 3;
    const k = (idx + 2) % 3;
    const remaining = 100 - val;
    const sumJK = next[j] + next[k];
    if (sumJK === 0) {
      next[j] = Math.floor(remaining / 2);
      next[k] = remaining - next[j];
    } else {
      next[j] = Math.round(remaining * next[j] / sumJK);
      next[k] = remaining - next[j];
    }
    next[idx] = val;
    onChange(next);
  };

  return (
    <div style={{
      background: 'var(--color-surface)',
      borderRadius: 'var(--radius-lg)',
      border: '1px solid var(--color-border)',
      borderTop: '2px solid rgba(22,163,74,0.25)',
      boxShadow: 'var(--shadow-sm)',
      padding: '22px 24px',
      height: '100%',
      boxSizing: 'border-box' as const,
      display: 'flex',
      flexDirection: 'column' as const,
    }}>
      <div style={{ fontWeight: 700, fontSize: 'var(--text-base)', color: 'var(--color-text)', marginBottom: 2 }}>Scoring Weights</div>
      <div style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-muted)', marginBottom: 24 }}>
        Adjust how bundles are ranked. Weights always sum to 100%.
      </div>

      {WEIGHT_META.map(({ key, label, color, desc }, i) => (
        <div key={key} style={{
          marginBottom: 10, padding: '11px 13px',
          background: 'var(--color-bg)', borderRadius: 'var(--radius-md)',
          border: '1px solid var(--color-border-light)',
          borderLeft: `3px solid ${color}`,
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 9 }}>
            <div>
              <span style={{ fontWeight: 600, fontSize: 'var(--text-sm)', color: 'var(--color-text)' }}>{label}</span>
              <div style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-faint)', marginTop: 2, lineHeight: 1.4 }}>{desc}</div>
            </div>
            <div style={{
              display: 'flex', flexDirection: 'column' as const, alignItems: 'flex-end', flexShrink: 0, marginLeft: 12,
            }}>
              <span style={{ fontWeight: 700, fontSize: 16, color, lineHeight: 1 }}>{weights[i]}</span>
              <span style={{ fontSize: 'var(--text-xs)', color, opacity: 0.7, lineHeight: 1, marginTop: 1 }}>%</span>
            </div>
          </div>
          <input
            type="range" min={0} max={100} value={weights[i]}
            onChange={e => update(i, Number(e.target.value))}
            style={{
              width: '100%', cursor: 'pointer', color,
              background: `linear-gradient(to right, ${color} 0%, ${color} ${weights[i]}%, var(--color-border) ${weights[i]}%, var(--color-border) 100%)`,
            }}
          />
        </div>
      ))}

      {/* Allocation stacked bar */}
      <div style={{ marginTop: 'auto', paddingTop: 8 }}>
        <div style={{
          fontSize: 'var(--text-xs)', color: 'var(--color-text-faint)', marginBottom: 7,
          fontWeight: 600, textTransform: 'uppercase' as const, letterSpacing: 0.5,
        }}>Allocation</div>
        <div style={{ display: 'flex', height: 10, borderRadius: 5, overflow: 'hidden', gap: 1 }}>
          {WEIGHT_META.map(({ color }, i) => (
            <div key={i} style={{
              background: color, width: `${weights[i]}%`,
              transition: 'width var(--transition-fast)',
            }} />
          ))}
        </div>
        <div style={{ display: 'flex', gap: 14, marginTop: 8 }}>
          {WEIGHT_META.map(({ label, color }, i) => (
            <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 5, fontSize: 'var(--text-xs)', color: 'var(--color-text-muted)' }}>
              <div style={{ width: 8, height: 8, borderRadius: 2, background: color }} />
              <span>{label}</span>
              <span style={{ color, fontWeight: 600 }}>{weights[i]}%</span>
            </div>
          ))}
        </div>
      </div>

      {/* Bundle type legend */}
      <div style={{ marginTop: 20, borderTop: '1px solid var(--color-border-light)', paddingTop: 16 }}>
        <div style={{
          fontSize: 'var(--text-xs)', fontWeight: 600, color: 'var(--color-text-faint)', marginBottom: 10,
          textTransform: 'uppercase' as const, letterSpacing: 0.6,
        }}>Generated bundle types</div>
        <div style={{ display: 'flex', flexDirection: 'column' as const, gap: 6 }}>
          {Object.entries(BUNDLE_META).map(([type, m]) => (
            <div key={type} style={{
              display: 'flex', alignItems: 'center', gap: 10,
              padding: '8px 11px',
              borderRadius: 'var(--radius-md)',
              background: m.lightBg,
              border: `1px solid ${m.border}`,
              borderLeft: `3px solid ${m.accent}`,
              minHeight: 44,
            }}>
              <div style={{
                width: 26, height: 26, borderRadius: 6, flexShrink: 0,
                background: 'white',
                border: `1.5px solid ${m.border}`,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontWeight: 800, fontSize: 11, color: m.accent,
              }}>{m.abbr}</div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontWeight: 700, fontSize: 'var(--text-sm)', color: m.accent, lineHeight: 1.2 }}>{m.label}</div>
                <div style={{
                  fontSize: 'var(--text-xs)', color: 'var(--color-text-secondary)',
                  marginTop: 2, lineHeight: 1.4,
                  whiteSpace: 'nowrap' as const, overflow: 'hidden', textOverflow: 'ellipsis',
                }}>{m.desc}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

// ─── Form field wrapper ──────────────────────────────────────────────────────
function Field({ label, hint, children }: { label: string; hint?: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 14 }}>
      <label style={{
        display: 'block', fontSize: 'var(--text-xs)', fontWeight: 600, color: 'var(--color-text-secondary)',
        marginBottom: 5, textTransform: 'uppercase' as const, letterSpacing: 0.5,
      }}>
        {label}
      </label>
      {children}
      {hint && <div style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-faint)', marginTop: 3 }}>{hint}</div>}
    </div>
  );
}

const inputStyle: React.CSSProperties = {
  width: '100%', boxSizing: 'border-box',
  padding: '9px 12px', fontSize: 'var(--text-sm)' as string,
  border: '1px solid var(--color-border)', borderRadius: 'var(--radius-md)' as string,
  background: 'var(--color-surface)',
  color: 'var(--color-text)', fontFamily: 'inherit',
  transition: 'border-color var(--transition-fast), box-shadow var(--transition-fast)',
};

// ─── Main page ───────────────────────────────────────────────────────────────
export default function FleetConsultingPage() {
  const navigate = useNavigate();

  // Form state — unchanged
  const [dailyKm,      setDailyKm]      = useState(100);
  const [vehicles,     setVehicles]     = useState(3);
  const [leaseTerm,    setLeaseTerm]    = useState(36);
  const [chargingType, setChargingType] = useState('COMPANY_CHARGER');
  const [rateChf,      setRateChf]      = useState(0.28);
  const [budget,       setBudget]       = useState('');
  const [brand,        setBrand]        = useState('');
  const [vehicleType,  setVehicleType]  = useState('');
  const [weights,      setWeights]      = useState<[number, number, number]>([34, 33, 33]);

  // Results state — unchanged
  const [step,     setStep]     = useState<0 | 1>(0);
  const [result,   setResult]   = useState<FleetBundleRecommendation | null>(null);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState('');
  const [expanded, setExpanded] = useState<Record<string, boolean>>({});
  const [reservingBundle, setReservingBundle] = useState<string | null>(null);
  const [reserveErrors, setReserveErrors] = useState<Record<string, string>>({});
  const [submitHov, setSubmitHov] = useState(false);

  // API call — unchanged
  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await getRecommendations({
        daily_route_km:           dailyKm,
        number_of_vehicles:       vehicles,
        lease_term_months:        leaseTerm,
        charging_type:            chargingType,
        electricity_rate_cents:   Math.round(rateChf * 100),
        currency:                 'CHF',
        budget_monthly_max_cents: budget ? Math.round(Number(budget) * 100) : undefined,
        brand:                    brand || undefined,
        vehicle_type:             vehicleType || undefined,
        range_weight:             weights[0],
        co2_weight:               weights[1],
        cost_weight:              weights[2],
      });
      setResult(data);
      setExpanded({});
      setStep(1);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Request failed');
    } finally {
      setLoading(false);
    }
  };

  const toggleBundle = (type: string) =>
    setExpanded(prev => ({ ...prev, [type]: !prev[type] }));

  // ── STEP 0: Requirements form ────────────────────────────────────────────
  if (step === 0) {
    return (
      <div style={{ padding: '24px 28px', minHeight: '100%', background: 'var(--color-bg)' }}>

        <PageHeader
          kicker="EV Advisory"
          title="Fleet Consulting"
          subtitle="Configure requirements and scoring preferences to generate optimised EV bundles."
        />

        <form onSubmit={handleGenerate}>
          <div style={{ display: 'grid', gridTemplateColumns: '380px 1fr', gap: 20, alignItems: 'stretch' }}>

            {/* Left: Requirements card */}
            <div style={{
              background: 'var(--color-surface)',
              borderRadius: 'var(--radius-lg)',
              border: '1px solid var(--color-border)',
              borderTop: '2px solid rgba(22,163,74,0.25)',
              boxShadow: 'var(--shadow-sm)',
              padding: '22px 24px',
              height: '100%',
              boxSizing: 'border-box',
              display: 'flex',
              flexDirection: 'column',
            }}>
              <div style={{ fontWeight: 700, fontSize: 'var(--text-base)', color: 'var(--color-text)', marginBottom: 2 }}>
                Fleet Requirements
              </div>
              <div style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-faint)', marginBottom: 20 }}>
                Core parameters for bundle generation
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 14px' }}>
                <Field label="Daily route" hint="km per vehicle per day">
                  <input type="number" min={1} value={dailyKm}
                    onChange={e => setDailyKm(Number(e.target.value))}
                    style={inputStyle} />
                </Field>
                <Field label="Number of vehicles">
                  <input type="number" min={1} value={vehicles}
                    onChange={e => setVehicles(Number(e.target.value))}
                    style={inputStyle} />
                </Field>
              </div>

              <Field label="Lease term">
                <select value={leaseTerm} onChange={e => setLeaseTerm(Number(e.target.value))} style={inputStyle}>
                  {[12, 24, 36, 48, 60].map(m => (
                    <option key={m} value={m}>{m} months ({m / 12} yr)</option>
                  ))}
                </select>
              </Field>

              <Field label="Charging type">
                <select value={chargingType} onChange={e => setChargingType(e.target.value)} style={inputStyle}>
                  {CHARGING_TYPES.map(t => (
                    <option key={t} value={t}>{t.replace(/_/g, ' ')}</option>
                  ))}
                </select>
              </Field>

              <Field label="Electricity rate" hint="CHF per kWh">
                <input type="number" min={0.01} step={0.01} value={rateChf}
                  onChange={e => setRateChf(Number(e.target.value))}
                  style={inputStyle} />
              </Field>

              {/* Optional filters section */}
              <div style={{ borderTop: '1px solid var(--color-border-light)', marginTop: 18, paddingTop: 18 }}>
                <div style={{
                  fontSize: 'var(--text-xs)', fontWeight: 600, color: 'var(--color-text-faint)',
                  textTransform: 'uppercase' as const, letterSpacing: 0.5,
                  marginBottom: 14,
                  display: 'flex', alignItems: 'center', gap: 10,
                }}>
                  <span style={{ whiteSpace: 'nowrap' }}>Optional filters</span>
                  <div style={{ flex: 1, height: 1, background: 'var(--color-border-light)' }} />
                </div>

                <Field label="Max monthly budget" hint="CHF — leave blank for no limit">
                  <input type="number" min={1} value={budget}
                    onChange={e => setBudget(e.target.value)}
                    placeholder="e.g. 4500"
                    style={inputStyle} />
                </Field>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 14px' }}>
                  <Field label="Brand">
                    <input type="text" value={brand}
                      onChange={e => setBrand(e.target.value)}
                      placeholder="e.g. Tesla"
                      style={inputStyle} />
                  </Field>
                  <Field label="Vehicle type">
                    <select value={vehicleType} onChange={e => setVehicleType(e.target.value)} style={inputStyle}>
                      {VEHICLE_TYPES.map(t => (
                        <option key={t} value={t}>{t || '— any —'}</option>
                      ))}
                    </select>
                  </Field>
                </div>
              </div>

              {/* Spacer pushes CTA to bottom */}
              <div style={{ flex: 1, minHeight: 20 }} />

              {/* Advisory note */}
              <div style={{
                background: 'var(--color-bg)',
                border: '1px solid var(--color-border-light)',
                borderLeft: '3px solid rgba(22,163,74,0.45)',
                borderRadius: 'var(--radius-md)',
                padding: '11px 13px',
                marginBottom: 18,
              }}>
                <div style={{
                  fontSize: 'var(--text-xs)', fontWeight: 600,
                  color: 'var(--color-text-secondary)', marginBottom: 4,
                }}>How it works</div>
                <div style={{
                  fontSize: 'var(--text-xs)', color: 'var(--color-text-faint)', lineHeight: 1.6,
                }}>
                  Bundles are generated from your route distance, fleet size, charging profile and budget.
                  Use the scoring weights to control how results are ranked across range, CO₂ and cost.
                </div>
              </div>

              {error && (
                <div style={{
                  background: 'var(--color-error-bg)', border: '1px solid #fecaca',
                  borderRadius: 'var(--radius-sm)', padding: '9px 12px',
                  color: 'var(--color-error)', fontSize: 'var(--text-sm)', marginBottom: 14,
                }}>
                  {error}
                </div>
              )}

              <button
                type="submit"
                disabled={loading}
                onMouseEnter={() => setSubmitHov(true)}
                onMouseLeave={() => setSubmitHov(false)}
                style={{
                  width: '100%', padding: '11px 0', marginTop: 0,
                  background: loading ? 'var(--color-brand-muted)' : submitHov ? 'var(--color-brand-dark)' : 'var(--color-brand)',
                  color: 'white', border: 'none', borderRadius: 'var(--radius-md)',
                  fontWeight: 600, fontSize: 'var(--text-base)', letterSpacing: 0.2,
                  cursor: loading ? 'default' : 'pointer',
                  boxShadow: loading ? 'none' : 'var(--shadow-brand)',
                  transition: 'background var(--transition-fast), box-shadow var(--transition-fast)',
                }}
              >
                {loading ? 'Generating bundles…' : 'Generate Bundles'}
              </button>
            </div>

            {/* Right: Scoring weights */}
            <WeightPanel weights={weights} onChange={setWeights} />
          </div>
        </form>
      </div>
    );
  }

  // ── STEP 1: Bundle results ───────────────────────────────────────────────
  const bundles = result?.bundles ?? [];

  return (
    <div style={{ padding: '24px 28px', minHeight: '100%', background: 'var(--color-bg)' }}>

      <PageHeader
        kicker="EV Advisory"
        title="Bundle Recommendations"
        subtitle={`${result?.numberOfVehicles} vehicles · ${result?.leaseTermMonths}-month lease · ${bundles.length} bundle${bundles.length !== 1 ? 's' : ''} generated`}
        action={
          <button
            onClick={() => setStep(0)}
            style={{
              background: 'transparent', color: 'var(--color-text-secondary)',
              border: '1px solid var(--color-border)', borderRadius: 'var(--radius-md)',
              padding: '7px 14px', fontWeight: 500, fontSize: 'var(--text-sm)', cursor: 'pointer',
              transition: 'border-color var(--transition-fast)',
            }}
            onMouseEnter={e => (e.currentTarget.style.borderColor = 'var(--color-brand)')}
            onMouseLeave={e => (e.currentTarget.style.borderColor = 'var(--color-border)')}
          >
            ← Modify requirements
          </button>
        }
      />

      {/* Request summary + weights bar */}
      <div style={{
        background: 'var(--color-surface)',
        borderRadius: 'var(--radius-lg)',
        border: '1px solid var(--color-border)',
        borderTop: '2px solid rgba(22,163,74,0.18)',
        padding: '12px 20px',
        marginBottom: 20,
        boxShadow: 'var(--shadow-xs)',
        display: 'flex', alignItems: 'center', flexWrap: 'wrap' as const, gap: 0,
      }}>
        {/* Summary chips */}
        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' as const, flex: 1, alignItems: 'center' }}>
          <span style={{
            fontSize: 10, fontWeight: 600, color: 'var(--color-text-faint)',
            textTransform: 'uppercase' as const, letterSpacing: 0.6, marginRight: 4, whiteSpace: 'nowrap',
          }}>Parameters</span>
          {[
            { label: 'Route',  val: `${dailyKm} km/day` },
            { label: 'Fleet',  val: `${vehicles} vehicles` },
            { label: 'Lease',  val: `${leaseTerm} months` },
            { label: 'Rate',   val: `CHF ${rateChf}/kWh` },
            ...(budget      ? [{ label: 'Budget', val: `CHF ${budget}/mo` }]  : []),
            ...(brand       ? [{ label: 'Brand',  val: brand }]               : []),
            ...(vehicleType ? [{ label: 'Type',   val: vehicleType }]          : []),
          ].map(({ label, val }) => (
            <div key={label} style={{
              background: 'var(--color-bg)', borderRadius: 5, padding: '3px 9px',
              fontSize: 'var(--text-xs)', border: '1px solid var(--color-border)',
              display: 'flex', alignItems: 'center', gap: 4,
            }}>
              <span style={{ color: 'var(--color-text-faint)', fontWeight: 500 }}>{label}:</span>
              <span style={{ fontWeight: 600, color: 'var(--color-text-secondary)' }}>{val}</span>
            </div>
          ))}
        </div>

        {/* Divider */}
        <div style={{ width: 1, height: 28, background: 'var(--color-border-light)', margin: '0 18px', flexShrink: 0 }} />

        {/* Weights */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexShrink: 0 }}>
          <span style={{
            fontSize: 10, color: 'var(--color-text-faint)', fontWeight: 600,
            textTransform: 'uppercase' as const, letterSpacing: 0.6, whiteSpace: 'nowrap',
          }}>Weights</span>
          {WEIGHT_META.map(({ label, color }, i) => (
            <div key={label} style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 'var(--text-xs)' }}>
              <div style={{ width: 7, height: 7, borderRadius: 2, background: color }} />
              <span style={{ color: 'var(--color-text-muted)' }}>{label}</span>
              <span style={{ fontWeight: 700, color }}>{weights[i]}%</span>
            </div>
          ))}
        </div>
      </div>

      {/* Bundle cards */}
      {bundles.length === 0 ? (
        <div style={{
          background: 'var(--color-surface)', borderRadius: 'var(--radius-lg)', border: '1px solid var(--color-border)',
          padding: 48, textAlign: 'center' as const, color: 'var(--color-text-faint)', fontSize: 'var(--text-base)',
        }}>
          No bundles matched your requirements. Try relaxing the budget or filters.
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {bundles.map(bundle => (
            <BundleCard
              key={bundle.bundleType}
              bundle={bundle}
              expanded={!!expanded[bundle.bundleType]}
              onToggle={() => toggleBundle(bundle.bundleType)}
              reserving={reservingBundle === bundle.bundleType}
              reserveError={reserveErrors[bundle.bundleType] ?? ''}
              onReserve={async () => {
                setReservingBundle(bundle.bundleType);
                setReserveErrors(prev => ({ ...prev, [bundle.bundleType]: '' }));
                try {
                  await reserveFleetBundle(bundle.vehicles.map(v => v.vin));
                  navigate('/dashboard/sales', {
                    state: {
                      bundleType: bundle.bundleType,
                      leaseTermMonths: result!.leaseTermMonths,
                      vehicles: bundle.vehicles.map(v => ({
                        vin: v.vin,
                        brand: v.brand,
                        model: v.model,
                        vehicleType: v.vehicleType,
                        rangeKm: v.rangeKm,
                        co2SavingsKgPerYear: v.co2SavingsKgPerYear,
                        monthlyInstallmentCents: v.monthlyInstallmentCents,
                        currency: v.currency,
                      })),
                    },
                  });
                } catch (err) {
                  setReserveErrors(prev => ({
                    ...prev,
                    [bundle.bundleType]: err instanceof Error ? err.message : 'Reservation failed',
                  }));
                } finally {
                  setReservingBundle(null);
                }
              }}
            />
          ))}
        </div>
      )}
    </div>
  );
}
