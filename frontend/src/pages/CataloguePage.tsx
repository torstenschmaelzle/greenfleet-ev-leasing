import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { vehiclesApi } from '../api/vehicles';
import { useAuth } from '../context/AuthContext';
import type { Vehicle } from '../types';

const STATUS_STYLE: Record<string, { bg: string; color: string }> = {
  AVAILABLE:      { bg: '#e8f5e9', color: '#2e7d32' },
  RESERVED:       { bg: '#fff3e0', color: '#e65100' },
  UNDER_CONTRACT: { bg: '#e3f2fd', color: '#1565c0' },
  REMOVED:        { bg: '#fafafa', color: '#757575' },
};

export default function CataloguePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isWorker = user?.role === 'WORKER';

  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [filtered, setFiltered] = useState<Vehicle[]>([]);
  const [search, setSearch] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const data = await vehiclesApi.list();
      setVehicles(data);
      setFiltered(data);
    } catch (_e) {
      setError('Failed to load vehicles');
    }
  };

  useEffect(() => { load(); }, []);

  useEffect(() => {
    const q = search.toLowerCase();
    setFiltered(vehicles.filter(v =>
      v.vin.toLowerCase().includes(q) ||
      v.brand.toLowerCase().includes(q) ||
      v.model.toLowerCase().includes(q)
    ));
  }, [search, vehicles]);

  const handleDelete = async (vin: string, label: string) => {
    if (!window.confirm(`Remove ${label}? This cannot be undone.`)) return;
    try {
      await vehiclesApi.remove(vin);
      load();
    } catch (e: unknown) {
      alert(e instanceof Error ? e.message : 'Failed to remove vehicle');
    }
  };

  return (
    <div style={{ padding: 24 }}>
      {/* Header */}
      <div style={{
        background: 'linear-gradient(135deg, #1a237e, #3949ab)',
        borderRadius: 12, padding: '20px 28px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        marginBottom: 16,
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{
            width: 48, height: 48, borderRadius: '50%',
            background: 'rgba(255,255,255,0.15)',
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 22,
          }}>⚡</div>
          <div>
            <div style={{ color: 'white', fontWeight: 700, fontSize: 18 }}>EV Catalogue</div>
            <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>GreenFleet electric vehicle inventory</div>
          </div>
        </div>
        {isWorker && (
          <button
            onClick={() => navigate('/dashboard/add-vehicle')}
            style={{
              background: 'rgba(255,255,255,0.15)', color: 'white',
              border: '1px solid rgba(255,255,255,0.3)', borderRadius: 6,
              padding: '8px 16px', fontWeight: 600, fontSize: 13,
            }}
          >
            + Add Vehicle
          </button>
        )}
      </div>

      {/* Search bar */}
      <div style={{
        background: 'white', borderRadius: 10, padding: '10px 16px',
        marginBottom: 12, boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
        display: 'flex', alignItems: 'center', gap: 8,
      }}>
        <span>🔍</span>
        <input
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search by brand, model or VIN…"
          style={{ flex: 1, border: 'none', outline: 'none', fontSize: 14 }}
        />
        <button onClick={load} style={{ border: 'none', background: 'none', fontSize: 16 }}>↺</button>
      </div>

      {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}

      {/* Grid */}
      <div style={{
        background: 'white', borderRadius: 10,
        boxShadow: '0 1px 4px rgba(0,0,0,0.08)', overflow: 'auto',
      }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
          <thead>
            <tr style={{ borderBottom: '2px solid #f0f0f0', color: '#888' }}>
              <th style={th}>Image</th>
              <th style={th}>VIN</th>
              <th style={th}>Brand</th>
              <th style={th}>Model</th>
              <th style={th}>Type</th>
              <th style={th}>Range</th>
              <th style={th}>Battery</th>
              <th style={th}>Power</th>
              <th style={th}>Price / mo</th>
              <th style={th}>Status</th>
              {isWorker && <th style={th}></th>}
            </tr>
          </thead>
          <tbody>
            {filtered.map((v, i) => {
              const s = STATUS_STYLE[v.status] ?? { bg: '#f5f5f5', color: '#555' };
              return (
                <tr key={v.vin} style={{ borderBottom: '1px solid #f5f5f5', background: i % 2 === 1 ? '#fafafa' : 'white' }}>
                  <td style={td}>
                    <img
                      src={`https://source.unsplash.com/80x50/?${v.brand},${v.model},electric-car`}
                      alt={`${v.brand} ${v.model}`}
                      style={{ width: 80, height: 50, objectFit: 'cover', borderRadius: 4 }}
                    />
                  </td>
                  <td style={{ ...td, fontFamily: 'monospace', fontSize: 11 }}>{v.vin}</td>
                  <td style={td}>{v.brand}</td>
                  <td style={{ ...td, color: '#555' }}>{v.model}</td>
                  <td style={td}>
                    <span style={badge('#f0f0f0', '#666')}>{v.vehicleType}</span>
                  </td>
                  <td style={td}>{v.rangeKm} km</td>
                  <td style={td}>{v.capacityKwh} kWh</td>
                  <td style={td}>{v.horsePowerHp} HP</td>
                  <td style={td}>{v.currency} {(v.monthlyLeasePriceCents / 100).toFixed(2)}</td>
                  <td style={td}>
                    <span style={badge(s.bg, s.color)}>{v.status.replace('_', ' ')}</span>
                  </td>
                  {isWorker && (
                    <td style={td}>
                      <button
                        onClick={() => navigate(`/dashboard/update-vehicle/${v.vin}`)}
                        style={{ border: 'none', background: 'none', fontSize: 16, marginRight: 4 }}
                        title="Edit"
                      >✏️</button>
                      <button
                        onClick={() => handleDelete(v.vin, `${v.brand} ${v.model}`)}
                        style={{ border: 'none', background: 'none', fontSize: 16 }}
                        title="Delete"
                      >🗑️</button>
                    </td>
                  )}
                </tr>
              );
            })}
            {filtered.length === 0 && (
              <tr>
                <td colSpan={isWorker ? 11 : 10} style={{ ...td, textAlign: 'center', color: '#999', padding: 32 }}>
                  No vehicles found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

const th: React.CSSProperties = { padding: '10px 12px', textAlign: 'left', fontWeight: 600 };
const td: React.CSSProperties = { padding: '8px 12px' };
const badge = (bg: string, color: string): React.CSSProperties => ({
  background: bg, color, borderRadius: 12,
  padding: '2px 8px', fontSize: 11, fontWeight: 600, whiteSpace: 'nowrap',
  display: 'inline-block',
});
