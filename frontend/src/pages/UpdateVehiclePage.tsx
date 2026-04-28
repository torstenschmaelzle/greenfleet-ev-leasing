import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { vehiclesApi } from '../api/vehicles';
import type { UpdateVehicleRequest, Vehicle } from '../types';

const VEHICLE_TYPES = ['SUV', 'KOMBI', 'LIMOUSINE', 'VAN'];
const CURRENCIES = ['CHF', 'USD', 'EUR', 'GBP'];

export default function UpdateVehiclePage() {
  const { vin } = useParams<{ vin: string }>();
  const navigate = useNavigate();
  const [vehicle, setVehicle] = useState<Vehicle | null>(null);
  const [form, setForm] = useState<UpdateVehicleRequest | null>(null);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

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

  if (!form) return <div style={{ padding: 32 }}>Loading…</div>;

  return (
    <div style={{ padding: 24 }}>
      {/* Header */}
      <div style={{
        background: 'linear-gradient(135deg, #1a237e, #3949ab)',
        borderRadius: 12, padding: '20px 28px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        marginBottom: 20,
      }}>
        <div>
          <div style={{ color: 'white', fontWeight: 700, fontSize: 18 }}>Update Vehicle</div>
          <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>
            Editing: {vehicle?.brand} {vehicle?.model} · {vin}
          </div>
        </div>
        <button
          onClick={() => navigate('/dashboard/catalogue')}
          style={{
            background: 'rgba(255,255,255,0.15)', color: 'white',
            border: '1px solid rgba(255,255,255,0.3)', borderRadius: 6,
            padding: '8px 16px', fontWeight: 600, fontSize: 13,
          }}
        >
          ← Back
        </button>
      </div>

      <form onSubmit={handleSubmit} style={{ maxWidth: 840, display: 'flex', flexDirection: 'column', gap: 16 }}>
        {error && <div style={{ background: '#fef2f2', color: '#b91c1c', borderRadius: 8, padding: '10px 14px' }}>{error}</div>}

        <Card title="Vehicle Identity">
          <Row label="VIN (read-only)">
            <input style={{ ...input, background: '#f5f5f5', color: '#888' }} value={vin} readOnly />
          </Row>
        </Card>

        <Card title="Vehicle Specification">
          <Grid2>
            <Row label="Brand"><input style={input} value={form.brand} onChange={e => set('brand', e.target.value)} required /></Row>
            <Row label="Model"><input style={input} value={form.model} onChange={e => set('model', e.target.value)} required /></Row>
            <Row label="Vehicle Type">
              <select style={input} value={form.vehicleType} onChange={e => set('vehicleType', e.target.value)}>
                {VEHICLE_TYPES.map(t => <option key={t}>{t}</option>)}
              </select>
            </Row>
            <Row label="Range (km)"><input style={input} type="number" value={form.rangeKm} onChange={e => set('rangeKm', +e.target.value)} required min={1} /></Row>
            <Row label="Consumption (kWh/100km)"><input style={input} type="number" step="0.1" value={form.consumptionKwhPer100km} onChange={e => set('consumptionKwhPer100km', +e.target.value)} required min={0.1} /></Row>
          </Grid2>
        </Card>

        <Card title="Battery Specification">
          <Grid2>
            <Row label="Capacity (kWh)"><input style={input} type="number" step="0.1" value={form.capacityKwh} onChange={e => set('capacityKwh', +e.target.value)} required min={0.1} /></Row>
            <Row label="Voltage (V)"><input style={input} type="number" step="0.1" value={form.voltageV} onChange={e => set('voltageV', +e.target.value)} required min={0.1} /></Row>
            <Row label="Max Charge Speed (kW)"><input style={input} type="number" step="0.1" value={form.maxChargeSpeedKw} onChange={e => set('maxChargeSpeedKw', +e.target.value)} required min={0.1} /></Row>
          </Grid2>
        </Card>

        <Card title="Pricing">
          <Grid2>
            <Row label="Monthly Lease (cents)"><input style={input} type="number" value={form.monthlyLeasePriceCents} onChange={e => set('monthlyLeasePriceCents', +e.target.value)} required min={1} /></Row>
            <Row label="Currency">
              <select style={input} value={form.currency} onChange={e => set('currency', e.target.value)}>
                {CURRENCIES.map(c => <option key={c}>{c}</option>)}
              </select>
            </Row>
          </Grid2>
        </Card>

        <Card title="Battery Health (new measurement)">
          <Grid2>
            <Row label="State of Health (%)"><input style={input} type="number" step="0.1" value={form.newStateOfHealth} onChange={e => set('newStateOfHealth', +e.target.value)} required min={0} max={100} /></Row>
            <Row label="Measured Range (km)"><input style={input} type="number" value={form.newMeasuredRangeKm} onChange={e => set('newMeasuredRangeKm', +e.target.value)} required min={1} /></Row>
          </Grid2>
        </Card>

        <button
          type="submit"
          disabled={saving}
          style={{
            background: '#1a237e', color: 'white', border: 'none',
            borderRadius: 8, padding: '14px', fontSize: 15,
            fontWeight: 600, opacity: saving ? 0.7 : 1,
          }}
        >
          {saving ? 'Saving…' : '✓ Save Changes'}
        </button>
      </form>
    </div>
  );
}

function Card({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div style={{ background: 'white', borderRadius: 10, padding: '18px 20px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' }}>
      <div style={{ fontWeight: 600, fontSize: 14, marginBottom: 14, color: '#1a237e' }}>{title}</div>
      {children}
    </div>
  );
}

function Grid2({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px 20px' }}>
      {children}
    </div>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
      <label style={{ fontSize: 12, fontWeight: 500, color: '#555' }}>{label}</label>
      {children}
    </div>
  );
}

const input: React.CSSProperties = {
  padding: '8px 10px', borderRadius: 6, border: '1px solid #d0d5dd',
  fontSize: 13, outline: 'none', width: '100%',
};
