import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { vehiclesApi } from '../api/vehicles';
import { useAuth } from '../context/AuthContext';
import type { Vehicle } from '../types';

const UNSPLASH_KEY = 'YOUR_UNSPLASH_ACCESS_KEY';

const STATUS_STYLE: Record<string, { bg: string; color: string; label: string }> = {
  AVAILABLE:      { bg: '#e8f5e9', color: '#2e7d32', label: 'Available' },
  RESERVED:       { bg: '#fff3e0', color: '#e65100', label: 'Reserved' },
  UNDER_CONTRACT: { bg: '#e3f2fd', color: '#1565c0', label: 'Under Contract' },
  REMOVED:        { bg: '#f5f5f5', color: '#757575', label: 'Removed' },
};

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
      if (url) {
        imageCache[key] = url;
        return url;
      }
    } catch {
      // try next query
    }
  }

  imageCache[key] = null;
  return null;
}

function VehicleCard({
  v,
  isWorker,
  onEdit,
  onDelete,
}: {
  v: Vehicle;
  isWorker: boolean;
  onEdit: (vin: string) => void;
  onDelete: (vin: string, label: string) => void;
}) {
  const [imgSrc, setImgSrc] = useState<string | null | 'loading'>('loading');
  const [hovered, setHovered] = useState(false);

  useEffect(() => {
    let active = true;
    resolveImage(v.brand, v.model).then(url => {
      if (active) setImgSrc(url);
    });
    return () => { active = false; };
  }, [v.brand, v.model]);

  const s = STATUS_STYLE[v.status] ?? { bg: '#f5f5f5', color: '#555', label: v.status };
  const price = `${v.currency} ${(v.monthlyLeasePriceCents / 100).toFixed(0)}`;

  return (
    <div
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        background: 'white',
        borderRadius: 14,
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
        boxShadow: hovered
          ? '0 12px 32px rgba(57,73,171,0.18)'
          : '0 2px 10px rgba(0,0,0,0.08)',
        transform: hovered ? 'translateY(-5px)' : 'translateY(0)',
        transition: 'transform 0.2s ease, box-shadow 0.2s ease',
      }}
    >
      {/* Image area */}
      <div style={{ position: 'relative', width: '100%', height: 185, background: '#e8eaf6', flexShrink: 0 }}>
        {imgSrc === 'loading' && (
          <div style={{
            position: 'absolute', inset: 0,
            background: 'linear-gradient(135deg, #e8eaf6 0%, #c5cae9 100%)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <span style={{ fontSize: 36, opacity: 0.5 }}>⚡</span>
          </div>
        )}
        {imgSrc && imgSrc !== 'loading' && (
          <img
            src={imgSrc}
            alt={`${v.brand} ${v.model}`}
            style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }}
          />
        )}
        {imgSrc === null && (
          <div style={{
            position: 'absolute', inset: 0,
            background: 'linear-gradient(135deg, #c5cae9 0%, #9fa8da 100%)',
            display: 'flex', flexDirection: 'column',
            alignItems: 'center', justifyContent: 'center', gap: 6,
          }}>
            <span style={{ fontSize: 40 }}>🚗</span>
            <span style={{ color: 'rgba(255,255,255,0.8)', fontSize: 11 }}>No image available</span>
          </div>
        )}

        {/* Status pill */}
        <span style={{
          position: 'absolute', top: 10, right: 10,
          background: s.bg, color: s.color,
          borderRadius: 20, padding: '3px 10px',
          fontSize: 11, fontWeight: 700,
          boxShadow: '0 1px 6px rgba(0,0,0,0.15)',
          backdropFilter: 'blur(4px)',
        }}>
          {s.label}
        </span>

        {/* Type pill */}
        <span style={{
          position: 'absolute', top: 10, left: 10,
          background: 'rgba(26,35,126,0.75)', color: 'white',
          borderRadius: 20, padding: '3px 10px',
          fontSize: 11, fontWeight: 600,
          backdropFilter: 'blur(4px)',
        }}>
          {v.vehicleType}
        </span>
      </div>

      {/* Card body */}
      <div style={{ padding: '14px 16px 16px', display: 'flex', flexDirection: 'column', gap: 0, flex: 1 }}>
        <div style={{ fontWeight: 800, fontSize: 17, color: '#1a237e', lineHeight: 1.2 }}>{v.brand}</div>
        <div style={{ fontWeight: 500, fontSize: 14, color: '#757575', marginBottom: 10 }}>{v.model}</div>

        {/* Specs grid */}
        <div style={{
          display: 'grid', gridTemplateColumns: '1fr 1fr',
          gap: '6px 12px', fontSize: 12, color: '#555', marginBottom: 12,
        }}>
          <SpecItem icon="⚡" label={`${v.rangeKm} km`} />
          <SpecItem icon="🔋" label={`${v.capacityKwh} kWh`} />
          <SpecItem icon="💪" label={`${v.horsePowerHp} HP`} />
          <SpecItem icon="⚙️" label={`${v.accelerationSec}s 0–100`} />
        </div>

        {/* Divider */}
        <div style={{ borderTop: '1px solid #f0f0f0', marginBottom: 12 }} />

        {/* Price + actions */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div>
            <span style={{ fontWeight: 800, fontSize: 16, color: '#3949ab' }}>{price}</span>
            <span style={{ fontSize: 11, color: '#aaa' }}>/month</span>
          </div>
          {isWorker && (
            <div style={{ display: 'flex', gap: 6 }}>
              <ActionBtn
                label="Edit"
                bg="#e8eaf6"
                color="#3949ab"
                onClick={() => onEdit(v.vin)}
              />
              <ActionBtn
                label="Remove"
                bg="#ffebee"
                color="#c62828"
                onClick={() => onDelete(v.vin, `${v.brand} ${v.model}`)}
              />
            </div>
          )}
        </div>

        {/* VIN */}
        <div style={{
          marginTop: 8, fontSize: 10, color: '#bdbdbd',
          fontFamily: 'monospace', letterSpacing: 0.5,
        }}>
          {v.vin}
        </div>
      </div>
    </div>
  );
}

function SpecItem({ icon, label }: { icon: string; label: string }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
      <span style={{ fontSize: 13 }}>{icon}</span>
      <span>{label}</span>
    </div>
  );
}

function ActionBtn({
  label, bg, color, onClick,
}: {
  label: string; bg: string; color: string; onClick: () => void;
}) {
  const [hovered, setHovered] = useState(false);
  return (
    <button
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        border: 'none', background: hovered ? color : bg, color: hovered ? 'white' : color,
        borderRadius: 7, padding: '5px 11px', fontSize: 12, fontWeight: 600,
        cursor: 'pointer', transition: 'background 0.15s, color 0.15s',
      }}
    >
      {label}
    </button>
  );
}

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
      v.model.toLowerCase().includes(q),
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
    <div style={{ padding: 24, minHeight: '100%', background: '#f7f8fc' }}>
      {/* Header */}
      <div style={{
        background: 'linear-gradient(135deg, #1a237e, #3949ab)',
        borderRadius: 14, padding: '20px 28px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        marginBottom: 20,
        boxShadow: '0 4px 20px rgba(26,35,126,0.25)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{
            width: 48, height: 48, borderRadius: '50%',
            background: 'rgba(255,255,255,0.15)',
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 22,
          }}>⚡</div>
          <div>
            <div style={{ color: 'white', fontWeight: 800, fontSize: 20 }}>EV Catalogue</div>
            <div style={{ color: 'rgba(255,255,255,0.65)', fontSize: 12 }}>GreenFleet electric vehicle inventory</div>
          </div>
        </div>
        {isWorker && (
          <button
            onClick={() => navigate('/dashboard/add-vehicle')}
            style={{
              background: 'rgba(255,255,255,0.15)', color: 'white',
              border: '1px solid rgba(255,255,255,0.3)', borderRadius: 8,
              padding: '9px 18px', fontWeight: 700, fontSize: 13, cursor: 'pointer',
            }}
          >
            + Add Vehicle
          </button>
        )}
      </div>

      {/* Search bar */}
      <div style={{
        background: 'white', borderRadius: 10, padding: '10px 16px',
        marginBottom: 24, boxShadow: '0 1px 4px rgba(0,0,0,0.07)',
        display: 'flex', alignItems: 'center', gap: 8,
      }}>
        <span style={{ fontSize: 15 }}>🔍</span>
        <input
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search by brand, model or VIN…"
          style={{ flex: 1, border: 'none', outline: 'none', fontSize: 14, background: 'transparent' }}
        />
        <button
          onClick={load}
          style={{ border: 'none', background: 'none', fontSize: 18, cursor: 'pointer', color: '#999' }}
          title="Reload"
        >↺</button>
      </div>

      {error && <div style={{ color: '#c62828', marginBottom: 16, fontWeight: 500 }}>{error}</div>}

      {/* Card grid */}
      {filtered.length === 0 ? (
        <div style={{
          textAlign: 'center', color: '#bbb', padding: 64,
          background: 'white', borderRadius: 12,
        }}>
          No vehicles found.
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(270px, 1fr))',
          gap: 22,
        }}>
          {filtered.map(v => (
            <VehicleCard
              key={v.vin}
              v={v}
              isWorker={isWorker}
              onEdit={vin => navigate(`/dashboard/update-vehicle/${vin}`)}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}
    </div>
  );
}
