import { useState, useEffect } from 'react';
import { getRecommendations } from '../api/fleetConsulting';
import type { FleetBundleRecommendation, FleetBundle, BundleVehicle } from '../api/fleetConsulting';

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
  BALANCED: { label: 'Balanced',  accent: '#2563eb', lightBg: '#eff6ff', border: '#bfdbfe', desc: 'Best overall score across range, cost & CO₂',          abbr: 'B' },
  ECO:      { label: 'Eco',       accent: '#16a34a', lightBg: '#f0fdf4', border: '#bbf7d0', desc: 'Maximum CO₂ savings, greenest fleet',                   abbr: 'E' },
  ECONOMIC: { label: 'Economic',  accent: '#7c3aed', lightBg: '#faf5ff', border: '#ddd6fe', desc: 'Lowest total cost of ownership',                        abbr: '$' },
  PREMIUM:  { label: 'Premium',   accent: '#b45309', lightBg: '#fefce8', border: '#fde68a', desc: 'Maximum range & sustainability — cost not a constraint', abbr: 'P' },
  CUSTOM:   { label: 'Custom',    accent: '#d97706', lightBg: '#fffbeb', border: '#fde68a', desc: 'Optimised for your personal weight settings',            abbr: 'C' },
};

const WEIGHT_META = [
  { key: 'range', label: 'Range', color: '#2563eb', desc: 'Prioritise vehicles with the highest km range' },
  { key: 'co2',   label: 'CO₂',  color: '#16a34a', desc: 'Favour vehicles with the biggest CO₂ savings' },
  { key: 'cost',  label: 'Cost',  color: '#7c3aed', desc: 'Minimise monthly leasing & charging cost' },
];

// ─── Helpers ─────────────────────────────────────────────────────────────────
function fmtChf(cents: number) {
  return `CHF ${(cents / 100).toLocaleString('de-CH', { minimumFractionDigits: 0, maximumFractionDigits: 0 })}`;
}

function scoreColor(s: number) {
  const n = s * 100;
  if (n >= 70) return '#16a34a';
  if (n >= 40) return '#d97706';
  return '#dc2626';
}

// ─── Inline SVG icons ────────────────────────────────────────────────────────
function IconFleet({ size = 20, color = '#16a34a' }: { size?: number; color?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
      stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="1" y="3" width="15" height="13" rx="2" />
      <path d="M16 8h4l3 3v5h-7V8z" />
      <circle cx="5.5" cy="18.5" r="2.5" />
      <circle cx="18.5" cy="18.5" r="2.5" />
    </svg>
  );
}

function IconCheck({ size = 20, color = '#16a34a' }: { size?: number; color?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
      stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 11l3 3L22 4" />
      <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11" />
    </svg>
  );
}

function IconChevron({ size = 16, color = '#94a3b8' }: { size?: number; color?: string }) {
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
      background: 'white',
      borderRadius: 8,
      border: '1px solid #e2e8f0',
      overflow: 'hidden',
      display: 'flex',
      flexDirection: 'column',
    }}>
      {/* Image area */}
      <div style={{ position: 'relative', height: 110, background: '#f1f5f9', flexShrink: 0 }}>
        {img === 'loading' && (
          <div style={{ position: 'absolute', inset: 0, background: '#e2e8f0' }} />
        )}
        {img && img !== 'loading' && (
          <img src={img} alt={`${v.brand} ${v.model}`}
            style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }} />
        )}
        {img === null && (
          <div style={{
            position: 'absolute', inset: 0, background: '#f8fafc',
            display: 'flex', flexDirection: 'column',
            alignItems: 'center', justifyContent: 'center', gap: 4,
          }}>
            <div style={{
              width: 38, height: 24, border: '1.5px solid #cbd5e1',
              borderRadius: 4, display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <span style={{ fontSize: 10, color: '#94a3b8', fontWeight: 700 }}>EV</span>
            </div>
            <span style={{ color: '#94a3b8', fontSize: 10 }}>No image</span>
          </div>
        )}
        <span style={{
          position: 'absolute', top: 7, left: 7,
          background: 'rgba(15,23,42,0.65)', color: 'white',
          borderRadius: 4, padding: '2px 7px', fontSize: 10, fontWeight: 600,
          backdropFilter: 'blur(4px)', letterSpacing: 0.3,
        }}>{v.vehicleType}</span>
      </div>

      {/* Body */}
      <div style={{ padding: '10px 12px 12px' }}>
        <div style={{ fontWeight: 700, fontSize: 13, color: '#0f172a' }}>{v.brand}</div>
        <div style={{ fontSize: 12, color: '#64748b', marginBottom: 8 }}>{v.model}</div>

        <div style={{
          display: 'grid', gridTemplateColumns: '1fr 1fr',
          gap: '3px 8px', fontSize: 11, marginBottom: 10,
        }}>
          <span style={{ color: '#475569' }}>{v.rangeKm} km range</span>
          <span style={{ color: '#475569' }}>{fmtChf(v.monthlyInstallmentCents)}/mo</span>
          <span style={{ color: '#475569' }}>{v.co2SavingsKgPerYear.toFixed(0)} kg CO₂/yr</span>
          <span style={{ fontFamily: 'monospace', fontSize: 9.5, color: '#94a3b8' }}>{v.vin.slice(-6)}</span>
        </div>

        {/* Score breakdown */}
        <div style={{
          fontSize: 10, color: '#94a3b8', marginBottom: 5,
          fontWeight: 500, textTransform: 'uppercase', letterSpacing: 0.4,
        }}>Score breakdown</div>
        <div style={{ display: 'flex', gap: 4 }}>
          {[
            { label: 'Range', val: v.score.rangeScore, color: '#2563eb' },
            { label: 'CO₂',   val: v.score.co2Score,   color: '#16a34a' },
            { label: 'Cost',  val: v.score.costScore,   color: '#7c3aed' },
          ].map(({ label, val, color }) => (
            <div key={label} style={{ flex: 1, textAlign: 'center' }}>
              <div style={{ fontSize: 9, color: '#94a3b8', marginBottom: 3 }}>{label}</div>
              <div style={{ height: 3, borderRadius: 2, background: '#f1f5f9', overflow: 'hidden' }}>
                <div style={{ height: '100%', width: `${val * 100}%`, background: color, borderRadius: 2 }} />
              </div>
              <div style={{ fontSize: 10, fontWeight: 700, color, marginTop: 2 }}>{(val * 100).toFixed(0)}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function BundleCard({ bundle, expanded, onToggle }: {
  bundle: FleetBundle;
  expanded: boolean;
  onToggle: () => void;
}) {
  const meta = BUNDLE_META[bundle.bundleType] ?? {
    label: bundle.bundleType, accent: '#475569', lightBg: '#f8fafc',
    border: '#e2e8f0', desc: '', abbr: '?',
  };
  const pct = Math.round(bundle.bundleScore * 100);
  const scoreFill = scoreColor(bundle.bundleScore);

  return (
    <div style={{
      borderRadius: 10,
      border: '1px solid #e2e8f0',
      borderLeft: `4px solid ${meta.accent}`,
      overflow: 'hidden',
      boxShadow: expanded
        ? '0 4px 20px rgba(0,0,0,0.09)'
        : '0 1px 5px rgba(0,0,0,0.05)',
      transition: 'box-shadow 0.2s',
      background: 'white',
    }}>
      {/* Card header */}
      <div style={{
        padding: '16px 20px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        borderBottom: '1px solid #f1f5f9',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          {/* Type monogram */}
          <div style={{
            width: 40, height: 40, borderRadius: 8,
            background: meta.lightBg,
            border: `1px solid ${meta.border}`,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontWeight: 800, fontSize: 15, color: meta.accent, letterSpacing: -0.5,
            flexShrink: 0,
          }}>{meta.abbr}</div>
          <div>
            <div style={{ fontWeight: 700, fontSize: 15, color: '#0f172a' }}>{meta.label} Bundle</div>
            <div style={{ fontSize: 12, color: '#64748b', marginTop: 1 }}>{meta.desc}</div>
          </div>
        </div>

        {/* Score KPI badge */}
        <div style={{
          background: '#f8fafc',
          border: `1.5px solid ${scoreFill}33`,
          borderRadius: 8, padding: '6px 16px',
          display: 'flex', flexDirection: 'column', alignItems: 'center',
          minWidth: 60,
        }}>
          <span style={{ fontWeight: 800, fontSize: 20, color: scoreFill, lineHeight: 1 }}>{pct}</span>
          <span style={{ fontSize: 9, color: '#94a3b8', textTransform: 'uppercase', letterSpacing: 0.6, marginTop: 2 }}>Score</span>
        </div>
      </div>

      {/* Metrics row */}
      <div style={{
        display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)',
        borderBottom: '1px solid #f1f5f9',
        background: '#fafbfc',
      }}>
        {[
          { label: 'Monthly total',  val: fmtChf(bundle.tco.monthlyTotalCents),          sub: 'all vehicles' },
          { label: 'Annual total',   val: fmtChf(bundle.tco.annualTotalCents),            sub: 'incl. charging' },
          { label: 'Lease / month',  val: fmtChf(bundle.tco.monthlyLeaseCostCents),       sub: 'fleet total' },
          { label: 'CO₂ saved / yr', val: `${bundle.totalCo2SavingsKgPerYear.toFixed(0)} kg`, sub: 'vs. ICE fleet' },
        ].map(({ label, val, sub }, idx, arr) => (
          <div key={label} style={{
            padding: '14px 16px', textAlign: 'center',
            borderRight: idx < arr.length - 1 ? '1px solid #f1f5f9' : 'none',
          }}>
            <div style={{ fontSize: 11, color: '#94a3b8', marginBottom: 5, fontWeight: 500 }}>{label}</div>
            <div style={{ fontWeight: 700, fontSize: 14, color: '#0f172a' }}>{val}</div>
            <div style={{ fontSize: 10, color: '#cbd5e1', marginTop: 2 }}>{sub}</div>
          </div>
        ))}
      </div>

      {/* Expand toggle */}
      <button
        onClick={onToggle}
        style={{
          width: '100%', background: 'none', border: 'none',
          padding: '10px 20px', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          borderBottom: expanded ? '1px solid #f1f5f9' : 'none',
          transition: 'background 0.15s',
        }}
        onMouseEnter={e => (e.currentTarget.style.background = '#f8fafc')}
        onMouseLeave={e => (e.currentTarget.style.background = 'none')}
      >
        <span style={{ color: meta.accent, fontWeight: 600, fontSize: 13 }}>
          {expanded
            ? 'Collapse vehicles'
            : `View ${bundle.vehicles.length} vehicle${bundle.vehicles.length !== 1 ? 's' : ''}`}
        </span>
        <span style={{
          display: 'inline-block',
          transform: expanded ? 'rotate(180deg)' : 'none',
          transition: 'transform 0.2s',
        }}>
          <IconChevron />
        </span>
      </button>

      {/* Vehicles grid */}
      {expanded && (
        <div style={{
          padding: '16px 18px 20px',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(190px, 1fr))',
          gap: 12,
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
      background: 'white',
      borderRadius: 10,
      border: '1px solid #e2e8f0',
      boxShadow: '0 1px 6px rgba(0,0,0,0.05)',
      padding: '22px 24px',
      height: '100%',
      boxSizing: 'border-box',
      display: 'flex',
      flexDirection: 'column',
    }}>
      <div style={{ fontWeight: 700, fontSize: 14, color: '#0f172a', marginBottom: 2 }}>Scoring Weights</div>
      <div style={{ fontSize: 12, color: '#64748b', marginBottom: 24 }}>
        Adjust how bundles are ranked. Weights always sum to 100%.
      </div>

      {WEIGHT_META.map(({ key, label, color, desc }, i) => (
        <div key={key} style={{ marginBottom: 20 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 7 }}>
            <div>
              <span style={{ fontWeight: 600, fontSize: 13, color: '#0f172a' }}>{label}</span>
              <div style={{ fontSize: 11, color: '#94a3b8', marginTop: 1 }}>{desc}</div>
            </div>
            <span style={{ fontWeight: 700, fontSize: 18, color, minWidth: 48, textAlign: 'right' }}>
              {weights[i]}%
            </span>
          </div>
          <input
            type="range" min={0} max={100} value={weights[i]}
            onChange={e => update(i, Number(e.target.value))}
            style={{ width: '100%', accentColor: color, cursor: 'pointer' }}
          />
        </div>
      ))}

      {/* Allocation stacked bar */}
      <div style={{ marginTop: 'auto', paddingTop: 8 }}>
        <div style={{
          fontSize: 10, color: '#94a3b8', marginBottom: 7,
          fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5,
        }}>Allocation</div>
        <div style={{ display: 'flex', height: 8, borderRadius: 4, overflow: 'hidden', gap: 1 }}>
          {WEIGHT_META.map(({ color }, i) => (
            <div key={i} style={{
              background: color, width: `${weights[i]}%`,
              transition: 'width 0.15s',
            }} />
          ))}
        </div>
        <div style={{ display: 'flex', gap: 14, marginTop: 8 }}>
          {WEIGHT_META.map(({ label, color }, i) => (
            <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 5, fontSize: 11, color: '#64748b' }}>
              <div style={{ width: 8, height: 8, borderRadius: 2, background: color }} />
              <span>{label}</span>
              <span style={{ color, fontWeight: 600 }}>{weights[i]}%</span>
            </div>
          ))}
        </div>
      </div>

      {/* Bundle type legend */}
      <div style={{ marginTop: 24, borderTop: '1px solid #f1f5f9', paddingTop: 18 }}>
        <div style={{
          fontSize: 10, fontWeight: 600, color: '#94a3b8', marginBottom: 14,
          textTransform: 'uppercase', letterSpacing: 0.5,
        }}>Generated bundle types</div>
        {Object.entries(BUNDLE_META).map(([type, m]) => (
          <div key={type} style={{ display: 'flex', alignItems: 'flex-start', gap: 10, marginBottom: 10 }}>
            <div style={{
              width: 24, height: 24, borderRadius: 5,
              background: m.lightBg, border: `1px solid ${m.border}`,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontWeight: 800, fontSize: 11, color: m.accent, flexShrink: 0,
            }}>{m.abbr}</div>
            <div>
              <span style={{ fontWeight: 600, fontSize: 12, color: m.accent }}>{m.label}</span>
              <div style={{ fontSize: 11, color: '#94a3b8', marginTop: 1, lineHeight: 1.35 }}>{m.desc}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ─── Form field wrapper ──────────────────────────────────────────────────────
function Field({ label, hint, children }: { label: string; hint?: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 14 }}>
      <label style={{
        display: 'block', fontSize: 11, fontWeight: 600, color: '#475569',
        marginBottom: 5, textTransform: 'uppercase', letterSpacing: 0.5,
      }}>
        {label}
      </label>
      {children}
      {hint && <div style={{ fontSize: 11, color: '#94a3b8', marginTop: 3 }}>{hint}</div>}
    </div>
  );
}

const inputStyle: React.CSSProperties = {
  width: '100%', boxSizing: 'border-box',
  padding: '9px 12px', fontSize: 13,
  border: '1px solid #d1d5db', borderRadius: 6,
  outline: 'none', background: '#ffffff',
  color: '#111827', fontFamily: 'inherit',
  transition: 'border-color 0.15s, box-shadow 0.15s',
};

// ─── Main page ───────────────────────────────────────────────────────────────
export default function FleetConsultingPage() {
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
      <div style={{ padding: '24px 28px', minHeight: '100%', background: '#f1f5f9' }}>

        {/* Page header */}
        <div style={{
          background: 'white',
          borderRadius: 10,
          border: '1px solid #e2e8f0',
          padding: '16px 22px',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          marginBottom: 24,
          boxShadow: '0 1px 4px rgba(0,0,0,0.05)',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
            <div style={{
              width: 40, height: 40, borderRadius: 8,
              background: '#f0fdf4', border: '1px solid #bbf7d0',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              flexShrink: 0,
            }}>
              <IconFleet />
            </div>
            <div>
              <div style={{ fontWeight: 700, fontSize: 17, color: '#0f172a', letterSpacing: -0.2 }}>
                Fleet Consulting
              </div>
              <div style={{ fontSize: 12, color: '#64748b', marginTop: 1 }}>
                Configure your fleet requirements and scoring preferences to generate optimised EV bundles.
              </div>
            </div>
          </div>
          <div style={{
            background: '#f0fdf4', border: '1px solid #bbf7d0',
            borderRadius: 6, padding: '4px 12px',
            fontSize: 11, fontWeight: 600, color: '#16a34a',
            letterSpacing: 0.3, textTransform: 'uppercase', whiteSpace: 'nowrap',
          }}>
            EV Advisory
          </div>
        </div>

        <form onSubmit={handleGenerate}>
          <div style={{ display: 'grid', gridTemplateColumns: '380px 1fr', gap: 20, alignItems: 'start' }}>

            {/* Left: Requirements card */}
            <div style={{
              background: 'white',
              borderRadius: 10,
              border: '1px solid #e2e8f0',
              boxShadow: '0 1px 6px rgba(0,0,0,0.05)',
              padding: '22px 24px',
            }}>
              <div style={{ fontWeight: 700, fontSize: 14, color: '#0f172a', marginBottom: 2 }}>
                Fleet Requirements
              </div>
              <div style={{ fontSize: 11, color: '#94a3b8', marginBottom: 20 }}>
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
              <div style={{ borderTop: '1px solid #f1f5f9', marginTop: 18, paddingTop: 18 }}>
                <div style={{
                  fontSize: 10, fontWeight: 600, color: '#94a3b8',
                  textTransform: 'uppercase', letterSpacing: 0.5,
                  marginBottom: 14,
                  display: 'flex', alignItems: 'center', gap: 10,
                }}>
                  <span style={{ whiteSpace: 'nowrap' }}>Optional filters</span>
                  <div style={{ flex: 1, height: 1, background: '#f1f5f9' }} />
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

              {error && (
                <div style={{
                  background: '#fef2f2', border: '1px solid #fecaca',
                  borderRadius: 6, padding: '9px 12px',
                  color: '#dc2626', fontSize: 13, marginTop: 14, marginBottom: 2,
                }}>
                  {error}
                </div>
              )}

              <button type="submit" disabled={loading} style={{
                width: '100%', padding: '11px 0', marginTop: 18,
                background: loading ? '#86efac' : '#16a34a',
                color: 'white', border: 'none', borderRadius: 7,
                fontWeight: 600, fontSize: 14, letterSpacing: 0.2,
                cursor: loading ? 'default' : 'pointer',
                boxShadow: loading ? 'none' : '0 2px 10px rgba(22,163,74,0.28)',
                transition: 'background 0.15s, box-shadow 0.15s',
              }}>
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
    <div style={{ padding: '24px 28px', minHeight: '100%', background: '#f1f5f9' }}>

      {/* Results header */}
      <div style={{
        background: 'white',
        borderRadius: 10,
        border: '1px solid #e2e8f0',
        padding: '16px 22px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        marginBottom: 20,
        boxShadow: '0 1px 4px rgba(0,0,0,0.05)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
          <div style={{
            width: 40, height: 40, borderRadius: 8,
            background: '#f0fdf4', border: '1px solid #bbf7d0',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            flexShrink: 0,
          }}>
            <IconCheck />
          </div>
          <div>
            <div style={{ fontWeight: 700, fontSize: 16, color: '#0f172a' }}>Bundle Recommendations</div>
            <div style={{ fontSize: 12, color: '#64748b', marginTop: 1 }}>
              {result?.numberOfVehicles} vehicles &middot; {result?.leaseTermMonths}-month lease &middot;{' '}
              {bundles.length} bundle{bundles.length !== 1 ? 's' : ''} generated
            </div>
          </div>
        </div>
        <button
          onClick={() => setStep(0)}
          style={{
            background: 'white', color: '#475569',
            border: '1px solid #d1d5db', borderRadius: 7,
            padding: '8px 16px', fontWeight: 600, fontSize: 13, cursor: 'pointer',
            display: 'flex', alignItems: 'center', gap: 6,
            transition: 'background 0.15s',
          }}
          onMouseEnter={e => (e.currentTarget.style.background = '#f8fafc')}
          onMouseLeave={e => (e.currentTarget.style.background = 'white')}
        >
          ← Modify requirements
        </button>
      </div>

      {/* Request summary + weights bar */}
      <div style={{
        background: 'white', borderRadius: 10, border: '1px solid #e2e8f0',
        padding: '13px 20px', marginBottom: 20,
        boxShadow: '0 1px 4px rgba(0,0,0,0.04)',
        display: 'flex', alignItems: 'center', gap: 0, flexWrap: 'wrap',
      }}>
        {/* Summary chips */}
        <div style={{ display: 'flex', gap: 7, flexWrap: 'wrap', flex: 1 }}>
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
              background: '#f8fafc', borderRadius: 5, padding: '4px 10px',
              fontSize: 11, border: '1px solid #e2e8f0',
              display: 'flex', alignItems: 'center', gap: 4,
            }}>
              <span style={{ color: '#94a3b8', fontWeight: 500 }}>{label}:</span>
              <span style={{ fontWeight: 600, color: '#374151' }}>{val}</span>
            </div>
          ))}
        </div>

        {/* Divider */}
        <div style={{ width: 1, height: 30, background: '#e2e8f0', margin: '0 18px', flexShrink: 0 }} />

        {/* Weights */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexShrink: 0 }}>
          <span style={{
            fontSize: 10, color: '#94a3b8', fontWeight: 600,
            textTransform: 'uppercase', letterSpacing: 0.5, whiteSpace: 'nowrap',
          }}>Weights</span>
          {WEIGHT_META.map(({ label, color }, i) => (
            <div key={label} style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 11 }}>
              <div style={{ width: 8, height: 8, borderRadius: 2, background: color }} />
              <span style={{ color: '#64748b' }}>{label}</span>
              <span style={{ fontWeight: 700, color }}>{weights[i]}%</span>
            </div>
          ))}
        </div>
      </div>

      {/* Bundle cards */}
      {bundles.length === 0 ? (
        <div style={{
          background: 'white', borderRadius: 10, border: '1px solid #e2e8f0',
          padding: 48, textAlign: 'center', color: '#94a3b8', fontSize: 14,
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
            />
          ))}
        </div>
      )}
    </div>
  );
}
