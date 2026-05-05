import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { vehiclesApi } from '../api/vehicles';
import type { UpdateVehicleRequest, Vehicle } from '../types';
import PageHeader from '../components/PageHeader';

const VEHICLE_TYPES = ['SUV', 'KOMBI', 'LIMOUSINE', 'VAN'];
const CURRENCIES = ['CHF', 'USD', 'EUR', 'GBP'];

export default function UpdateVehiclePage() {
  const { vin } = useParams<{ vin: string }>();
  const navigate = useNavigate();
  const [vehicle, setVehicle] = useState<Vehicle | null>(null);
  const [form, setForm] = useState<UpdateVehicleRequest | null>(null);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [submitHov, setSubmitHov] = useState(false);

  useEffect(() => {
    if (!vin) return;
    vehiclesApi.get(vin).then(v => {
      setVehicle(v);
      setForm({
        brand: v.brand, model: v.model, rangeKm: v.rangeKm,
        vehicleType: v.vehicleType, consumptionKwhPer100km: v.consumptionKwhPer100km,
        capacityKwh: v.capacityKwh, voltageV: v.voltageV, maxChargeSpeedKw: v.maxChargeSpeedKw,
        monthlyLeasePriceCents: v.monthlyLeasePriceCents, currency: v.currency,
        newStateOfHealth: v.stateOfHealth, newMeasuredRangeKm: v.measuredRangeKm,
      });
    }).catch(() => { setError('Vehicle not found'); navigate('/dashboard/catalogue'); });
  }, [vin, navigate]);

  const set = (key: keyof UpdateVehicleRequest, value: string | number) =>
    setForm(f => f ? { ...f, [key]: value } : f);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!vin || !form) return;
    setError('');
    setSaving(true);
    try {
      await vehiclesApi.update(vin, form);
      navigate('/dashboard/catalogue');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to update vehicle');
    } finally {
      setSaving(false);
    }
  };

  if (!form) return <div style={{ padding: 32, color: 'var(--color-text-muted)' }}>Loading…</div>;

  return (
    <div style={{ padding: 'var(--space-6)' }}>
      <PageHeader
        kicker="EV Catalogue"
        title="Update Vehicle"
        subtitle={`Editing: ${vehicle?.brand ?? ''} ${vehicle?.model ?? ''} · ${vin}`}
        action={
          <button
            onClick={() => navigate('/dashboard/catalogue')}
            style={{
              background: 'transparent', color: 'var(--color-text-secondary)',
              border: '1px solid var(--color-border)', borderRadius: 'var(--radius-md)',
              padding: '7px 14px', fontWeight: 500, fontSize: 'var(--text-sm)', cursor: 'pointer',
              transition: 'border-color var(--transition-fast)',
            }}
            onMouseEnter={e => (e.currentTarget.style.borderColor = 'var(--color-brand)')}
            onMouseLeave={e => (e.currentTarget.style.borderColor = 'var(--color-border)')}
          >
            ← Back
          </button>
        }
      />

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
        {error && (
          <div style={{
            background: 'var(--color-error-bg)', color: 'var(--color-error)',
            borderRadius: 'var(--radius-md)', padding: '10px 14px',
            fontSize: 'var(--text-sm)', fontWeight: 500,
            border: '1px solid rgba(185,28,28,0.2)',
          }}>
            {error}
          </div>
        )}

        <FormCard title="Vehicle Identity">
          <Row label="VIN">
            <input
              style={inputReadOnly}
              value={vin}
              readOnly
            />
            <span style={{
              fontSize: 10, color: 'var(--color-text-faint)', marginTop: 3,
              fontWeight: 500, letterSpacing: 0.3,
            }}>
              The VIN cannot be changed after registration.
            </span>
          </Row>
        </FormCard>

        <FormCard title="Vehicle Specification">
          <Grid2>
            <Row label="Brand">
              <input style={input} value={form.brand} onChange={e => set('brand', e.target.value)} required />
            </Row>
            <Row label="Model">
              <input style={input} value={form.model} onChange={e => set('model', e.target.value)} required />
            </Row>
            <Row label="Vehicle Type">
              <select style={input} value={form.vehicleType} onChange={e => set('vehicleType', e.target.value)}>
                {VEHICLE_TYPES.map(t => <option key={t}>{t}</option>)}
              </select>
            </Row>
            <Row label="Range (km)">
              <input style={input} type="number" value={form.rangeKm}
                onChange={e => set('rangeKm', +e.target.value)} required min={1} />
            </Row>
            <Row label="Consumption (kWh / 100 km)">
              <input style={input} type="number" step="0.1" value={form.consumptionKwhPer100km}
                onChange={e => set('consumptionKwhPer100km', +e.target.value)} required min={0.1} />
            </Row>
          </Grid2>
        </FormCard>

        <FormCard title="Battery Specification">
          <Grid2>
            <Row label="Capacity (kWh)">
              <input style={input} type="number" step="0.1" value={form.capacityKwh}
                onChange={e => set('capacityKwh', +e.target.value)} required min={0.1} />
            </Row>
            <Row label="Voltage (V)">
              <input style={input} type="number" step="0.1" value={form.voltageV}
                onChange={e => set('voltageV', +e.target.value)} required min={0.1} />
            </Row>
            <Row label="Max Charge Speed (kW)">
              <input style={input} type="number" step="0.1" value={form.maxChargeSpeedKw}
                onChange={e => set('maxChargeSpeedKw', +e.target.value)} required min={0.1} />
            </Row>
          </Grid2>
        </FormCard>

        <FormCard title="Pricing">
          <Grid2>
            <Row label="Purchase Price (cents)">
              <input style={input} type="number" value={form.monthlyLeasePriceCents}
                onChange={e => set('monthlyLeasePriceCents', +e.target.value)} required min={1} />
            </Row>
            <Row label="Currency">
              <select style={input} value={form.currency} onChange={e => set('currency', e.target.value)}>
                {CURRENCIES.map(c => <option key={c}>{c}</option>)}
              </select>
            </Row>
          </Grid2>
        </FormCard>

        <FormCard title="Battery Health — New Measurement">
          <Grid2>
            <Row label="State of Health (%)">
              <input style={input} type="number" step="0.1" value={form.newStateOfHealth}
                onChange={e => set('newStateOfHealth', +e.target.value)} required min={0} max={100} />
            </Row>
            <Row label="Measured Range (km)">
              <input style={input} type="number" value={form.newMeasuredRangeKm}
                onChange={e => set('newMeasuredRangeKm', +e.target.value)} required min={1} />
            </Row>
          </Grid2>
        </FormCard>

        <div>
          <button
            type="submit"
            disabled={saving}
            onMouseEnter={() => setSubmitHov(true)}
            onMouseLeave={() => setSubmitHov(false)}
            style={{
              background: submitHov && !saving ? 'var(--color-brand-dark)' : 'var(--color-brand)',
              color: 'white',
              border: 'none',
              borderRadius: 'var(--radius-md)',
              padding: '12px 32px',
              fontSize: 'var(--text-base)',
              fontWeight: 700,
              cursor: saving ? 'not-allowed' : 'pointer',
              opacity: saving ? 0.75 : 1,
              transition: 'background var(--transition-fast)',
              boxShadow: 'var(--shadow-brand)',
            }}
          >
            {saving ? 'Saving…' : 'Save Changes'}
          </button>
        </div>
      </form>
    </div>
  );
}

// ─── Shared form helpers ──────────────────────────────────────────────────────

function FormCard({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div style={{
      background: 'var(--color-surface)',
      borderRadius: 'var(--radius-lg)',
      border: '1px solid var(--color-border)',
      borderTop: '2px solid rgba(22,163,74,0.25)',
      padding: '20px 24px',
      boxShadow: 'var(--shadow-sm)',
    }}>
      <div style={{
        fontWeight: 700,
        fontSize: 'var(--text-sm)',
        letterSpacing: 0.6,
        textTransform: 'uppercase' as const,
        color: 'var(--color-text-muted)',
        marginBottom: 16,
        paddingBottom: 10,
        borderBottom: '1px solid var(--color-border-light)',
      }}>
        {title}
      </div>
      {children}
    </div>
  );
}

function Grid2({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px 24px' }}>
      {children}
    </div>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
      <label style={{
        fontSize: 'var(--text-sm)',
        fontWeight: 600,
        color: 'var(--color-text-secondary)',
      }}>
        {label}
      </label>
      {children}
    </div>
  );
}

const inputBase: React.CSSProperties = {
  padding: '9px 12px',
  borderRadius: 'var(--radius-md)',
  border: '1px solid var(--color-border)',
  fontSize: 'var(--text-base)',
  width: '100%',
  transition: 'border-color 0.12s ease, box-shadow 0.12s ease',
};

const input: React.CSSProperties = {
  ...inputBase,
  color: 'var(--color-text)',
  background: 'var(--color-surface)',
};

const inputReadOnly: React.CSSProperties = {
  ...inputBase,
  color: 'var(--color-text-faint)',
  background: 'var(--color-border-light)',
  fontFamily: 'ui-monospace, SFMono-Regular, monospace',
  letterSpacing: 0.5,
  cursor: 'default',
};
