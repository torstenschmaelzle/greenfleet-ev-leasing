import { useState } from 'react';
import { getRecommendations } from '../api/fleetConsulting';
import type { VehicleRecommendation } from '../types';

const CHARGING_TYPES = ['COMPANY_CHARGER', 'PUBLIC_STATION', 'MIXED'];
const SIZE_OPTIONS = ['', 'SUV', 'SEDAN', 'VAN', 'COMPACT', 'ESTATE'];
const WEIGHT_COLORS = ['#1565c0', '#2e7d32', '#6a1b9a'];
const WEIGHT_LABELS = ['Range', 'Cost', 'CO₂'];

function scoreColor(score: number) {
  if (score >= 70) return '#2e7d32';
  if (score >= 40) return '#e65100';
  return '#c62828';
}

function fmtAmount(cents: number, currency: string) {
  return `${currency} ${(cents / 100).toFixed(2)}`;
}

export default function FleetConsultingPage() {
  const [dailyKm, setDailyKm] = useState(100);
  const [drivers, setDrivers] = useState(3);
  const [chargingType, setChargingType] = useState('COMPANY_CHARGER');
  const [electricityRate, setElectricityRate] = useState(0.28);
  const [budget, setBudget] = useState(1500);
  const [sizePreference, setSizePreference] = useState('');
  const [weights, setWeights] = useState([40, 40, 20]);

  const [results, setResults] = useState<VehicleRecommendation[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const updateWeight = (idx: number, newVal: number) => {
    newVal = Math.max(0, Math.min(100, newVal));
    const next = [...weights];
    const j = (idx + 1) % 3;
    const k = (idx + 2) % 3;
    const remaining = 100 - newVal;
    const sumJK = next[j] + next[k];
    if (sumJK === 0) {
      next[j] = Math.floor(remaining / 2);
      next[k] = remaining - next[j];
    } else {
      next[j] = Math.round(remaining * next[j] / sumJK);
      next[k] = remaining - next[j];
    }
    next[idx] = newVal;
    setWeights(next);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await getRecommendations({
        daily_route_km: dailyKm,
        number_of_drivers: drivers,
        charging_type: chargingType,
        electricity_rate_cents: Math.round(electricityRate * 100),
        currency: 'CHF',
        budget_monthly_max_cents: Math.round(budget * 100),
        size_preference: sizePreference || undefined,
        range_weight: weights[0],
        cost_weight: weights[1],
        co2_weight: weights[2],
      });
      setResults(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Request failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24, minHeight: '100%' }}>
      {/* Header */}
      <div style={{
        background: 'linear-gradient(135deg, #1b5e20, #2e7d32, #388e3c)',
        borderRadius: 12, padding: '20px 28px',
        display: 'flex', alignItems: 'center', gap: 16,
        marginBottom: 16,
      }}>
        <div style={{
          width: 48, height: 48, borderRadius: '50%',
          background: 'rgba(255,255,255,0.15)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 22,
        }}>🚌</div>
        <div>
          <div style={{ color: 'white', fontWeight: 700, fontSize: 18 }}>Fleet Consulting</div>
          <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>
            Enter your requirements and find the best EV for your fleet
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', gap: 16, alignItems: 'flex-start' }}>
        {/* Form card */}
        <form onSubmit={handleSubmit} style={{
          width: 360, flexShrink: 0,
          background: 'white', borderRadius: 10,
          boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
          padding: 20,
        }}>
          <div style={{ fontWeight: 700, fontSize: 15, marginBottom: 14 }}>Fleet Requirements</div>

          <Field label="Daily route (km)">
            <input type="number" min={1} value={dailyKm}
              onChange={e => setDailyKm(Number(e.target.value))}
              style={inputStyle} />
          </Field>

          <Field label="Number of drivers">
            <input type="number" min={1} value={drivers}
              onChange={e => setDrivers(Number(e.target.value))}
              style={inputStyle} />
          </Field>

          <Field label="Charging type">
            <select value={chargingType} onChange={e => setChargingType(e.target.value)} style={inputStyle}>
              {CHARGING_TYPES.map(t => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}
            </select>
          </Field>

          <Field label="Electricity rate (CHF/kWh)">
            <input type="number" min={0.01} step={0.01} value={electricityRate}
              onChange={e => setElectricityRate(Number(e.target.value))}
              style={inputStyle} />
          </Field>

          <Field label="Monthly budget (CHF)">
            <input type="number" min={1} value={budget}
              onChange={e => setBudget(Number(e.target.value))}
              style={inputStyle} />
          </Field>

          <Field label="Size preference (optional)">
            <select value={sizePreference} onChange={e => setSizePreference(e.target.value)} style={inputStyle}>
              {SIZE_OPTIONS.map(s => <option key={s} value={s}>{s || '— any —'}</option>)}
            </select>
          </Field>

          {/* Weight sliders */}
          <div style={{
            border: '1px solid #e0e0e0', borderRadius: 8,
            padding: 12, marginTop: 8, marginBottom: 14,
          }}>
            <div style={{ fontSize: 12, fontWeight: 600, color: '#666', marginBottom: 10 }}>Scoring Weights</div>
            {WEIGHT_LABELS.map((label, i) => (
              <div key={label} style={{ marginBottom: 8 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 2 }}>
                  <span style={{ fontSize: 13, fontWeight: 500 }}>{label}</span>
                  <span style={{ fontSize: 13, fontWeight: 700, color: WEIGHT_COLORS[i] }}>{weights[i]}%</span>
                </div>
                <input type="range" min={0} max={100} value={weights[i]}
                  onChange={e => updateWeight(i, Number(e.target.value))}
                  style={{ width: '100%', accentColor: WEIGHT_COLORS[i] }} />
              </div>
            ))}
            {/* Bar visualisation */}
            <div style={{ display: 'flex', height: 8, borderRadius: 4, overflow: 'hidden', marginTop: 4 }}>
              {WEIGHT_COLORS.map((color, i) => (
                <div key={i} style={{ background: color, width: `${weights[i]}%`, transition: 'width 0.1s' }} />
              ))}
            </div>
          </div>

          {error && <div style={{ color: '#c62828', fontSize: 13, marginBottom: 8 }}>{error}</div>}

          <button type="submit" disabled={loading} style={{
            width: '100%', padding: '10px 0',
            background: loading ? '#a5d6a7' : '#2e7d32',
            color: 'white', border: 'none', borderRadius: 6,
            fontWeight: 600, fontSize: 14, cursor: loading ? 'default' : 'pointer',
          }}>
            {loading ? 'Loading…' : '🔍 Get Recommendations'}
          </button>
        </form>

        {/* Results */}
        <div style={{
          flex: 1, background: 'white', borderRadius: 10,
          boxShadow: '0 1px 4px rgba(0,0,0,0.08)', overflow: 'hidden',
        }}>
          <div style={{ fontWeight: 700, fontSize: 15, padding: '16px 20px 0' }}>Recommendations</div>
          {results === null ? (
            <div style={{ padding: 40, textAlign: 'center', color: '#999', fontSize: 14 }}>
              Enter your fleet requirements and click "Get Recommendations" to see results.
            </div>
          ) : results.length === 0 ? (
            <div style={{ padding: 40, textAlign: 'center', color: '#999', fontSize: 14 }}>
              No vehicles matched your requirements.
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid #f0f0f0', color: '#888' }}>
                    {['#', 'Score', 'Vehicle', 'Type', 'Range', 'Range OK', 'Lease/mo', 'TCO/mo', 'TCO/yr', 'CO₂ saved/yr'].map(h => (
                      <th key={h} style={th}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {results.map((rec, i) => (
                    <tr key={i} style={{ borderBottom: '1px solid #f5f5f5', background: i % 2 === 1 ? '#fafafa' : 'white' }}>
                      <td style={td}>#{i + 1}</td>
                      <td style={td}>
                        <span style={{ fontWeight: 700, fontSize: 16, color: scoreColor(rec.suitabilityScore.score) }}>
                          {rec.suitabilityScore.score}
                        </span>
                      </td>
                      <td style={td}>{rec.brand} {rec.model}</td>
                      <td style={td}>
                        <span style={badge('#f0f0f0', '#666')}>{rec.vehicleType}</span>
                      </td>
                      <td style={td}>{rec.rangeKm.value} km</td>
                      <td style={td}>
                        <span style={badge(rec.rangeFit.sufficient ? '#e8f5e9' : '#ffebee', rec.rangeFit.sufficient ? '#2e7d32' : '#c62828')}>
                          {rec.rangeFit.sufficient ? 'Yes' : 'No'}
                        </span>
                      </td>
                      <td style={td}>{fmtAmount(rec.monthlyLeasePrice.amountInCents, rec.monthlyLeasePrice.currency)}</td>
                      <td style={td}>{fmtAmount(rec.tco.monthlyTotal.amountInCents, rec.tco.monthlyTotal.currency)}</td>
                      <td style={td}>{fmtAmount(rec.tco.annualTotal.amountInCents, rec.tco.annualTotal.currency)}</td>
                      <td style={td}>{Number(rec.co2Impact.annualSavingsKg).toFixed(0)} kg</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 10 }}>
      <label style={{ display: 'block', fontSize: 12, fontWeight: 600, color: '#555', marginBottom: 4 }}>
        {label}
      </label>
      {children}
    </div>
  );
}

const inputStyle: React.CSSProperties = {
  width: '100%', boxSizing: 'border-box',
  padding: '7px 10px', fontSize: 13,
  border: '1px solid #ddd', borderRadius: 6, outline: 'none',
};

const th: React.CSSProperties = { padding: '10px 12px', textAlign: 'left', fontWeight: 600, whiteSpace: 'nowrap' };
const td: React.CSSProperties = { padding: '8px 12px', whiteSpace: 'nowrap' };
const badge = (bg: string, color: string): React.CSSProperties => ({
  background: bg, color, borderRadius: 12,
  padding: '2px 8px', fontSize: 11, fontWeight: 600,
  display: 'inline-block',
});
