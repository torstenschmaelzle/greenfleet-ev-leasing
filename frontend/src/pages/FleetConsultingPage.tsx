import { useState, useEffect } from 'react';
import { getRecommendations } from '../api/fleetConsulting';
import type { FleetBundleRecommendation, FleetBundle, BundleVehicle } from '../api/fleetConsulting';

// ─── Unsplash image logic (same as CataloguePage) ──────────────────────────
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

const BUNDLE_META: Record<string, { label: string; icon: string; gradient: string; accent: string; desc: string }> = {
  BALANCED:  { label: 'Balanced',  icon: '⚖️',  gradient: 'linear-gradient(135deg,#1565c0,#1976d2)', accent: '#1565c0', desc: 'Best overall score across range, cost & CO₂' },
  ECO:       { label: 'Eco',       icon: '🌿',  gradient: 'linear-gradient(135deg,#1b5e20,#2e7d32)', accent: '#2e7d32', desc: 'Maximum CO₂ savings, greenest fleet' },
  ECONOMIC:  { label: 'Economic',  icon: '💰',  gradient: 'linear-gradient(135deg,#4a148c,#6a1b9a)', accent: '#6a1b9a', desc: 'Lowest total cost of ownership' },
  CUSTOM:    { label: 'Custom',    icon: '🎯',  gradient: 'linear-gradient(135deg,#bf360c,#e64a19)', accent: '#e64a19', desc: 'Optimised for your personal weight settings' },
};

const WEIGHT_META = [
  { key: 'range', label: 'Range',  color: '#1565c0', desc: 'Prioritise vehicles with the highest km range' },
  { key: 'co2',   label: 'CO₂',   color: '#2e7d32', desc: 'Favour vehicles with the biggest CO₂ savings' },
  { key: 'cost',  label: 'Cost',   color: '#6a1b9a', desc: 'Minimise monthly leasing & charging cost' },
];

function fmtChf(cents: number) {
  return `CHF ${(cents / 100).toLocaleString('de-CH', { minimumFractionDigits: 0, maximumFractionDigits: 0 })}`;
}

function scoreColor(s: number) {
  const n = s * 100;
  if (n >= 70) return '#2e7d32';
  if (n >= 40) return '#e65100';
  return '#c62828';
}

// ─── Sub-components ─────────────────────────────────────────────────────────

function VehicleImageCard({ v }: { v: BundleVehicle }) {
  const [img, setImg] = useState<string | null | 'loading'>('loading');
  useEffect(() => {
    let alive = true;
    resolveImage(v.brand, v.model).then(url => { if (alive) setImg(url); });
    return () => { alive = false; };
  }, [v.brand, v.model]);

  return (
    <div style={{
      background: 'white', borderRadius: 12, overflow: 'hidden',
      boxShadow: '0 2px 12px rgba(0,0,0,0.09)',
      display: 'flex', flexDirection: 'column',
    }}>
      {/* Image */}
      <div style={{ position: 'relative', height: 120, background: '#e8eaf6', flexShrink: 0 }}>
        {img === 'loading' && (
          <div style={{
            position: 'absolute', inset: 0,
            background: 'linear-gradient(135deg,#e8eaf6,#c5cae9)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <span style={{ fontSize: 28, opacity: 0.4 }}>⚡</span>
          </div>
        )}
        {img && img !== 'loading' && (
          <img src={img} alt={`${v.brand} ${v.model}`}
            style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }} />
        )}
        {img === null && (
          <div style={{
            position: 'absolute', inset: 0,
            background: 'linear-gradient(135deg,#c5cae9,#9fa8da)',
            display: 'flex', flexDirection: 'column',
            alignItems: 'center', justifyContent: 'center', gap: 4,
          }}>
            <span style={{ fontSize: 28 }}>🚗</span>
            <span style={{ color: 'rgba(255,255,255,0.8)', fontSize: 10 }}>No image</span>
          </div>
        )}
        <span style={{
          position: 'absolute', top: 7, left: 7,
          background: 'rgba(26,35,126,0.8)', color: 'white',
          borderRadius: 20, padding: '2px 8px', fontSize: 10, fontWeight: 600,
          backdropFilter: 'blur(4px)',
        }}>{v.vehicleType}</span>
      </div>

      {/* Body */}
      <div style={{ padding: '10px 12px 12px' }}>
        <div style={{ fontWeight: 800, fontSize: 14, color: '#1a237e' }}>{v.brand}</div>
        <div style={{ fontSize: 12, color: '#757575', marginBottom: 8 }}>{v.model}</div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '4px 8px', fontSize: 11, color: '#555', marginBottom: 10 }}>
          <span>⚡ {v.rangeKm} km</span>
          <span>💰 {fmtChf(v.monthlyInstallmentCents)}/mo</span>
          <span>🌿 {v.co2SavingsKgPerYear.toFixed(0)} kg/yr</span>
          <span style={{ fontFamily: 'monospace', fontSize: 9.5, color: '#bbb' }}>{v.vin.slice(-6)}</span>
        </div>

        {/* Score bar */}
        <div style={{ fontSize: 10, color: '#888', marginBottom: 3 }}>Score breakdown</div>
        <div style={{ display: 'flex', gap: 4 }}>
          {[
            { label: 'Range', val: v.score.rangeScore, color: '#1565c0' },
            { label: 'CO₂',   val: v.score.co2Score,   color: '#2e7d32' },
            { label: 'Cost',  val: v.score.costScore,   color: '#6a1b9a' },
          ].map(({ label, val, color }) => (
            <div key={label} style={{ flex: 1, textAlign: 'center' }}>
              <div style={{ fontSize: 9, color: '#999', marginBottom: 2 }}>{label}</div>
              <div style={{
                height: 4, borderRadius: 2, background: '#f0f0f0', overflow: 'hidden',
              }}>
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
    label: bundle.bundleType, icon: '📦',
    gradient: 'linear-gradient(135deg,#37474f,#546e7a)', accent: '#546e7a',
    desc: '',
  };
  const pct = Math.round(bundle.bundleScore * 100);

  return (
    <div style={{
      borderRadius: 16, overflow: 'hidden',
      boxShadow: expanded
        ? '0 8px 32px rgba(0,0,0,0.15)'
        : '0 2px 12px rgba(0,0,0,0.08)',
      transition: 'box-shadow 0.2s',
      background: 'white',
    }}>
      {/* Header */}
      <div style={{
        background: meta.gradient, padding: '18px 22px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{
            width: 44, height: 44, borderRadius: '50%',
            background: 'rgba(255,255,255,0.18)',
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20,
          }}>{meta.icon}</div>
          <div>
            <div style={{ color: 'white', fontWeight: 800, fontSize: 17 }}>{meta.label} Bundle</div>
            <div style={{ color: 'rgba(255,255,255,0.75)', fontSize: 11, marginTop: 1 }}>{meta.desc}</div>
          </div>
        </div>
        {/* Score ring */}
        <div style={{ textAlign: 'center' }}>
          <div style={{
            width: 52, height: 52, borderRadius: '50%',
            background: 'rgba(255,255,255,0.2)',
            border: '3px solid rgba(255,255,255,0.5)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            flexDirection: 'column',
          }}>
            <span style={{ color: 'white', fontWeight: 800, fontSize: 16, lineHeight: 1 }}>{pct}</span>
            <span style={{ color: 'rgba(255,255,255,0.7)', fontSize: 9 }}>score</span>
          </div>
        </div>
      </div>

      {/* Stats row */}
      <div style={{
        display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)',
        borderBottom: '1px solid #f0f0f0',
      }}>
        {[
          { icon: '💶', label: 'Monthly total',    val: fmtChf(bundle.tco.monthlyTotalCents) },
          { icon: '📅', label: 'Annual total',     val: fmtChf(bundle.tco.annualTotalCents) },
          { icon: '⚡', label: 'Lease / month',    val: fmtChf(bundle.tco.monthlyLeaseCostCents) },
          { icon: '🌿', label: 'CO₂ saved / yr',  val: `${bundle.totalCo2SavingsKgPerYear.toFixed(0)} kg` },
        ].map(({ icon, label, val }) => (
          <div key={label} style={{ padding: '14px 16px', textAlign: 'center', borderRight: '1px solid #f5f5f5' }}>
            <div style={{ fontSize: 18, marginBottom: 2 }}>{icon}</div>
            <div style={{ fontSize: 11, color: '#999', marginBottom: 2 }}>{label}</div>
            <div style={{ fontWeight: 700, fontSize: 13, color: '#222' }}>{val}</div>
          </div>
        ))}
      </div>

      {/* Expand toggle */}
      <button
        onClick={onToggle}
        style={{
          width: '100%', background: 'none', border: 'none',
          padding: '11px 22px', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          color: meta.accent, fontWeight: 600, fontSize: 13,
          borderBottom: expanded ? '1px solid #f0f0f0' : 'none',
        }}
      >
        <span>{expanded ? 'Hide vehicles' : `Show ${bundle.vehicles.length} vehicle${bundle.vehicles.length !== 1 ? 's' : ''}`}</span>
        <span style={{ fontSize: 16, transition: 'transform 0.2s', transform: expanded ? 'rotate(180deg)' : 'none' }}>›</span>
      </button>

      {/* Vehicles grid */}
      {expanded && (
        <div style={{
          padding: '16px 18px 20px',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(190px, 1fr))',
          gap: 14,
        }}>
          {bundle.vehicles.map(v => (
            <VehicleImageCard key={v.vin} v={v} />
          ))}
        </div>
      )}
    </div>
  );
}

// ─── Weight sliders ──────────────────────────────────────────────────────────
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
      background: 'white', borderRadius: 14,
      boxShadow: '0 2px 12px rgba(0,0,0,0.07)',
      padding: '22px 24px', height: '100%', boxSizing: 'border-box',
      display: 'flex', flexDirection: 'column',
    }}>
      <div style={{ fontWeight: 800, fontSize: 16, color: '#1a237e', marginBottom: 4 }}>Scoring Weights</div>
      <div style={{ fontSize: 12, color: '#999', marginBottom: 20 }}>
        Adjust how bundles are ranked. Weights always sum to 100%.
      </div>

      {WEIGHT_META.map(({ key, label, color, desc }, i) => (
        <div key={key} style={{ marginBottom: 22 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 4 }}>
            <div>
              <span style={{ fontWeight: 700, fontSize: 14, color: '#222' }}>{label}</span>
              <div style={{ fontSize: 11, color: '#aaa', marginTop: 1 }}>{desc}</div>
            </div>
            <span style={{
              fontWeight: 800, fontSize: 20, color,
              minWidth: 48, textAlign: 'right',
            }}>{weights[i]}%</span>
          </div>
          <input
            type="range" min={0} max={100} value={weights[i]}
            onChange={e => update(i, Number(e.target.value))}
            style={{ width: '100%', accentColor: color, cursor: 'pointer' }}
          />
        </div>
      ))}

      {/* Stacked bar */}
      <div style={{ marginTop: 'auto' }}>
        <div style={{ fontSize: 11, color: '#bbb', marginBottom: 6 }}>Weight distribution</div>
        <div style={{ display: 'flex', height: 10, borderRadius: 5, overflow: 'hidden', gap: 1 }}>
          {WEIGHT_META.map(({ color }, i) => (
            <div key={i} style={{
              background: color, width: `${weights[i]}%`,
              transition: 'width 0.15s',
            }} />
          ))}
        </div>
        <div style={{ display: 'flex', gap: 12, marginTop: 8 }}>
          {WEIGHT_META.map(({ label, color }, i) => (
            <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 11, color: '#666' }}>
              <div style={{ width: 8, height: 8, borderRadius: 2, background: color }} />
              {label}
            </div>
          ))}
        </div>
      </div>

      {/* Bundle legend */}
      <div style={{ marginTop: 24, borderTop: '1px solid #f0f0f0', paddingTop: 18 }}>
        <div style={{ fontSize: 12, fontWeight: 700, color: '#666', marginBottom: 10 }}>Generated bundles</div>
        {Object.entries(BUNDLE_META).map(([type, m]) => (
          <div key={type} style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 7 }}>
            <span style={{ fontSize: 14 }}>{m.icon}</span>
            <div>
              <span style={{ fontWeight: 700, fontSize: 12, color: m.accent }}>{m.label}</span>
              <span style={{ fontSize: 11, color: '#aaa', marginLeft: 6 }}>{m.desc}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ─── Form field helper ───────────────────────────────────────────────────────
function Field({ label, hint, children }: { label: string; hint?: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 14 }}>
      <label style={{ display: 'block', fontSize: 11, fontWeight: 700, color: '#555', marginBottom: 4, textTransform: 'uppercase', letterSpacing: 0.4 }}>
        {label}
      </label>
      {children}
      {hint && <div style={{ fontSize: 10.5, color: '#bbb', marginTop: 3 }}>{hint}</div>}
    </div>
  );
}

const inputStyle: React.CSSProperties = {
  width: '100%', boxSizing: 'border-box',
  padding: '8px 11px', fontSize: 13,
  border: '1.5px solid #e0e0e0', borderRadius: 8,
  outline: 'none', background: '#fafafa',
  transition: 'border-color 0.15s',
};

// ─── Main page ───────────────────────────────────────────────────────────────
export default function FleetConsultingPage() {
  // Form state
  const [dailyKm,       setDailyKm]       = useState(100);
  const [vehicles,      setVehicles]       = useState(3);
  const [leaseTerm,     setLeaseTerm]      = useState(36);
  const [chargingType,  setChargingType]   = useState('COMPANY_CHARGER');
  const [rateChf,       setRateChf]        = useState(0.28);
  const [budget,        setBudget]         = useState('');
  const [brand,         setBrand]          = useState('');
  const [vehicleType,   setVehicleType]    = useState('');
  const [weights,       setWeights]        = useState<[number, number, number]>([34, 33, 33]);

  // Results state
  const [step,          setStep]           = useState<0 | 1>(0);
  const [result,        setResult]         = useState<FleetBundleRecommendation | null>(null);
  const [loading,       setLoading]        = useState(false);
  const [error,         setError]          = useState('');
  const [expanded,      setExpanded]       = useState<Record<string, boolean>>({});

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await getRecommendations({
        daily_route_km:        dailyKm,
        number_of_vehicles:    vehicles,
        lease_term_months:     leaseTerm,
        charging_type:         chargingType,
        electricity_rate_cents: Math.round(rateChf * 100),
        currency:              'CHF',
        budget_monthly_max_cents: budget ? Math.round(Number(budget) * 100) : undefined,
        brand:                 brand || undefined,
        vehicle_type:          vehicleType || undefined,
        range_weight:          weights[0],
        co2_weight:            weights[1],
        cost_weight:           weights[2],
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

  // ── STEP 0: Requirements ─────────────────────────────────────────────────
  if (step === 0) {
    return (
      <div style={{ padding: 24, minHeight: '100%', background: '#f0f2f8' }}>
        {/* Header */}
        <div style={{
          background: 'linear-gradient(135deg,#1b5e20,#2e7d32,#388e3c)',
          borderRadius: 14, padding: '22px 30px',
          display: 'flex', alignItems: 'center', gap: 16,
          marginBottom: 24,
          boxShadow: '0 4px 20px rgba(27,94,32,0.3)',
        }}>
          <div style={{
            width: 52, height: 52, borderRadius: '50%',
            background: 'rgba(255,255,255,0.18)',
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 24,
          }}>🚌</div>
          <div>
            <div style={{ color: 'white', fontWeight: 800, fontSize: 20 }}>Fleet Consulting</div>
            <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12, marginTop: 2 }}>
              Configure your fleet requirements and scoring preferences, then generate optimised EV bundles.
            </div>
          </div>
        </div>

        <form onSubmit={handleGenerate}>
          <div style={{ display: 'grid', gridTemplateColumns: '380px 1fr', gap: 20, alignItems: 'start' }}>

            {/* ── Left: Requirements form ─────────────────────────── */}
            <div style={{
              background: 'white', borderRadius: 14,
              boxShadow: '0 2px 12px rgba(0,0,0,0.07)',
              padding: '22px 24px',
            }}>
              <div style={{ fontWeight: 800, fontSize: 16, color: '#1a237e', marginBottom: 18 }}>
                Fleet Requirements
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

              <Field label="Lease term (months)">
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

              <div style={{
                borderTop: '1px solid #f0f0f0', margin: '16px 0',
                paddingTop: 16,
              }}>
                <div style={{ fontSize: 11, fontWeight: 700, color: '#888', textTransform: 'uppercase', letterSpacing: 0.4, marginBottom: 12 }}>
                  Optional filters
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
                  background: '#ffebee', border: '1px solid #ffcdd2',
                  borderRadius: 8, padding: '9px 12px',
                  color: '#c62828', fontSize: 13, marginBottom: 14,
                }}>
                  {error}
                </div>
              )}

              <button type="submit" disabled={loading} style={{
                width: '100%', padding: '12px 0', marginTop: 4,
                background: loading
                  ? '#a5d6a7'
                  : 'linear-gradient(135deg,#1b5e20,#2e7d32)',
                color: 'white', border: 'none', borderRadius: 8,
                fontWeight: 700, fontSize: 15,
                cursor: loading ? 'default' : 'pointer',
                boxShadow: loading ? 'none' : '0 4px 14px rgba(27,94,32,0.35)',
                transition: 'box-shadow 0.2s',
                letterSpacing: 0.3,
              }}>
                {loading ? '⏳  Generating bundles…' : '✨  Generate Bundles'}
              </button>
            </div>

            {/* ── Right: Weight sliders ──────────────────────────── */}
            <WeightPanel weights={weights} onChange={setWeights} />
          </div>
        </form>
      </div>
    );
  }

  // ── STEP 1: Results ──────────────────────────────────────────────────────
  const bundles = result?.bundles ?? [];

  return (
    <div style={{ padding: 24, minHeight: '100%', background: '#f0f2f8' }}>
      {/* Header */}
      <div style={{
        background: 'linear-gradient(135deg,#1b5e20,#2e7d32,#388e3c)',
        borderRadius: 14, padding: '18px 24px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        marginBottom: 22,
        boxShadow: '0 4px 20px rgba(27,94,32,0.3)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
          <div style={{
            width: 46, height: 46, borderRadius: '50%',
            background: 'rgba(255,255,255,0.18)',
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 22,
          }}>🚌</div>
          <div>
            <div style={{ color: 'white', fontWeight: 800, fontSize: 19 }}>Bundle Recommendations</div>
            <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12, marginTop: 1 }}>
              {result?.numberOfVehicles} vehicles · {result?.leaseTermMonths} month lease · {bundles.length} bundle{bundles.length !== 1 ? 's' : ''} found
            </div>
          </div>
        </div>
        <button
          onClick={() => setStep(0)}
          style={{
            background: 'rgba(255,255,255,0.15)', color: 'white',
            border: '1.5px solid rgba(255,255,255,0.35)', borderRadius: 8,
            padding: '8px 18px', fontWeight: 700, fontSize: 13, cursor: 'pointer',
          }}
        >
          ← Back
        </button>
      </div>

      {/* Summary pills */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 22, flexWrap: 'wrap' }}>
        {[
          { icon: '🛣️', label: `${dailyKm} km/day` },
          { icon: '🚗', label: `${vehicles} vehicles` },
          { icon: '📅', label: `${leaseTerm} mo lease` },
          { icon: '⚡', label: `CHF ${rateChf}/kWh` },
          ...(budget ? [{ icon: '💰', label: `Budget: CHF ${budget}/mo` }] : []),
          ...(brand ? [{ icon: '🏷️', label: brand }] : []),
          ...(vehicleType ? [{ icon: '🚙', label: vehicleType }] : []),
        ].map(({ icon, label }) => (
          <div key={label} style={{
            background: 'white', borderRadius: 20, padding: '5px 14px',
            fontSize: 12, fontWeight: 600, color: '#444',
            boxShadow: '0 1px 4px rgba(0,0,0,0.07)',
            display: 'flex', alignItems: 'center', gap: 5,
          }}>
            <span>{icon}</span>{label}
          </div>
        ))}
      </div>

      {/* Weights summary */}
      <div style={{
        background: 'white', borderRadius: 10, padding: '12px 18px',
        marginBottom: 22, boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
        display: 'flex', alignItems: 'center', gap: 16,
      }}>
        <span style={{ fontSize: 12, fontWeight: 700, color: '#888', textTransform: 'uppercase', letterSpacing: 0.4 }}>Custom weights</span>
        <div style={{ display: 'flex', gap: 18 }}>
          {WEIGHT_META.map(({ label, color }, i) => (
            <div key={label} style={{ display: 'flex', alignItems: 'center', gap: 5, fontSize: 13 }}>
              <div style={{ width: 10, height: 10, borderRadius: 2, background: color }} />
              <span style={{ color: '#555' }}>{label}:</span>
              <span style={{ fontWeight: 700, color }}>{weights[i]}%</span>
            </div>
          ))}
        </div>
      </div>

      {/* Bundle cards */}
      {bundles.length === 0 ? (
        <div style={{
          background: 'white', borderRadius: 14, padding: 48,
          textAlign: 'center', color: '#bbb', fontSize: 14,
          boxShadow: '0 1px 4px rgba(0,0,0,0.07)',
        }}>
          No bundles matched your requirements. Try relaxing the budget or filters.
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
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
