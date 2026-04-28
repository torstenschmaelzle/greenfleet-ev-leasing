import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { vehiclesApi } from '../api/vehicles';
import type { AddVehicleRequest } from '../types';

const VEHICLE_TYPES = ['SUV', 'KOMBI', 'LIMOUSINE', 'VAN'];
const CURRENCIES = ['CHF', 'USD', 'EUR', 'GBP'];

const empty: AddVehicleRequest = {
  vin: '', brand: '', model: '', rangeKm: 0, vehicleType: 'SUV',
  consumptionKwhPer100km: 0, capacityKwh: 0, voltageV: 0, maxChargeSpeedKw: 0,
  horsePowerHp: 0, accelerationSec: 0, topSpeedKmh: 0,
  monthlyLeasePriceCents: 0, currency: 'CHF',
  initialStateOfHealth: 100, initialMeasuredRangeKm: 0,
};

export default function AddVehiclePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<AddVehicleRequest>(empty);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const set = (key: keyof AddVehicleRequest, value: string | number) =>
    setForm(f => ({ ...f, [key]: value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSaving(true);
    try {
      await vehiclesApi.add(form);
      navigate('/dashboard/catalogue');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to add vehicle');
    } finally {
      setSaving(false);
    }
  };

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
          <div style={{ color: 'white', fontWeight: 700, fontSize: 18 }}>Add New Vehicle</div>
          <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>Register an electric vehicle in GreenFleet</div>
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
          <Row label="VIN">
            <input style={input} value={form.vin} onChange={e => set('vin', e.target.value)} required placeholder="e.g. WBA3A5G59DNP26082" />
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
            <Row label="Range (km)"><input style={input} type="number" value={form.rangeKm || ''} onChange={e => set('rangeKm', +e.target.value)} required min={1} /></Row>
            <Row label="Consumption (kWh/100km)"><input style={input} type="number" step="0.1" value={form.consumptionKwhPer100km || ''} onChange={e => set('consumptionKwhPer100km', +e.target.value)} required min={0.1} /></Row>
          </Grid2>
        </Card>

        <Card title="Battery Specification">
          <Grid2>
            <Row label="Capacity (kWh)"><input style={input} type="number" step="0.1" value={form.capacityKwh || ''} onChange={e => set('capacityKwh', +e.target.value)} required min={0.1} /></Row>
            <Row label="Voltage (V)"><input style={input} type="number" step="0.1" value={form.voltageV || ''} onChange={e => set('voltageV', +e.target.value)} required min={0.1} /></Row>
            <Row label="Max Charge Speed (kW)"><input style={input} type="number" step="0.1" value={form.maxChargeSpeedKw || ''} onChange={e => set('maxChargeSpeedKw', +e.target.value)} required min={0.1} /></Row>
          </Grid2>
        </Card>

        <Card title="Performance">
          <Grid2>
            <Row label="Horse Power (HP)"><input style={input} type="number" value={form.horsePowerHp || ''} onChange={e => set('horsePowerHp', +e.target.value)} required min={1} /></Row>
            <Row label="0–100 km/h (sec)"><input style={input} type="number" step="0.1" value={form.accelerationSec || ''} onChange={e => set('accelerationSec', +e.target.value)} required min={0.1} /></Row>
            <Row label="Top Speed (km/h)"><input style={input} type="number" value={form.topSpeedKmh || ''} onChange={e => set('topSpeedKmh', +e.target.value)} required min={1} /></Row>
          </Grid2>
        </Card>

        <Card title="Pricing">
          <Grid2>
            <Row label="Monthly Lease (cents)"><input style={input} type="number" value={form.monthlyLeasePriceCents || ''} onChange={e => set('monthlyLeasePriceCents', +e.target.value)} required min={1} /></Row>
            <Row label="Currency">
              <select style={input} value={form.currency} onChange={e => set('currency', e.target.value)}>
                {CURRENCIES.map(c => <option key={c}>{c}</option>)}
              </select>
            </Row>
          </Grid2>
        </Card>

        <Card title="Battery Health">
          <Grid2>
            <Row label="State of Health (%)"><input style={input} type="number" step="0.1" value={form.initialStateOfHealth || ''} onChange={e => set('initialStateOfHealth', +e.target.value)} required min={0} max={100} /></Row>
            <Row label="Measured Range (km)"><input style={input} type="number" value={form.initialMeasuredRangeKm || ''} onChange={e => set('initialMeasuredRangeKm', +e.target.value)} required min={1} /></Row>
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
          {saving ? 'Saving…' : '+ Add Vehicle to Catalogue'}
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
