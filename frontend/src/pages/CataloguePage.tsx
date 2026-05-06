import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { vehiclesApi } from '../api/vehicles';
import { useAuth } from '../context/AuthContext';
import type { Vehicle } from '../types';
import PageHeader from '../components/PageHeader';

const UNSPLASH_KEY = 'YOUR_UNSPLASH_ACCESS_KEY';

const STATUS_STYLE: Record<string, { bg: string; color: string; label: string; sectionAccent: string }> = {
  AVAILABLE:      { bg: '#dcfce7', color: '#15803d', label: 'Available',      sectionAccent: '#16a34a' },
  RESERVED:       { bg: '#fff7ed', color: '#c2410c', label: 'Reserved',       sectionAccent: '#ea580c' },
  UNDER_CONTRACT: { bg: '#e0f2fe', color: '#0369a1', label: 'Under Contract', sectionAccent: '#0284c7' },
  REMOVED:        { bg: '#f1f5f9', color: '#64748b', label: 'Removed',        sectionAccent: '#94a3b8' },
};

const STATUS_ORDER = ['AVAILABLE', 'RESERVED', 'UNDER_CONTRACT', 'REMOVED'];

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

// ─── SVG icons ────────────────────────────────────────────────────────────────

function IconRange() {
  return (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
    </svg>
  );
}

function IconBattery() {
  return (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="7" width="16" height="10" rx="2" />
      <line x1="22" y1="11" x2="22" y2="13" />
    </svg>
  );
}

function IconPower() {
  return (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M18.36 6.64a9 9 0 1 1-12.73 0" />
      <line x1="12" y1="2" x2="12" y2="12" />
    </svg>
  );
}

function IconClock() {
  return (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" />
      <polyline points="12 6 12 12 16 14" />
    </svg>
  );
}

function IconSearch() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="11" cy="11" r="8" />
      <path d="M21 21l-4.35-4.35" />
    </svg>
  );
}

function IconRefresh() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" />
      <path d="M3 3v5h5" />
    </svg>
  );
}

function IconCar({ size = 40, opacity = 0.22 }: { size?: number; opacity?: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"
      style={{ opacity }}>
      <path d="M5 11L7 6h10l2 5" />
      <rect x="2" y="11" width="20" height="6" rx="2" />
      <circle cx="7" cy="17" r="1.5" />
      <circle cx="17" cy="17" r="1.5" />
    </svg>
  );
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

function formatPrice(cents: number, currency: string): string {
  const amount = Math.round(cents / 100);
  return `${currency}\u00a0${amount.toLocaleString('de-CH')}`;
}

// ─── Sub-components ───────────────────────────────────────────────────────────

function SpecItem({ icon, label }: { icon: React.ReactNode; label: string }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
      <span style={{ color: 'var(--color-brand)', display: 'flex', flexShrink: 0 }}>{icon}</span>
      <span style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>{label}</span>
    </div>
  );
}

function ActionBtn({
  label, variant, onClick,
}: {
  label: string;
  variant: 'edit' | 'remove';
  onClick: () => void;
}) {
  const [hov, setHov] = useState(false);

  const edit   = { idle: { border: '1px solid rgba(22,163,74,0.35)', color: 'var(--color-brand-dark)',  bg: 'transparent' },
                   hov:  { border: '1px solid var(--color-brand)',   color: 'white',                    bg: 'var(--color-brand)' } };
  const remove = { idle: { border: '1px solid rgba(185,28,28,0.30)', color: 'var(--color-error)',        bg: 'transparent' },
                   hov:  { border: '1px solid var(--color-error)',   color: 'white',                    bg: 'var(--color-error)' } };

  const t = variant === 'edit' ? edit : remove;
  const s = hov ? t.hov : t.idle;

  return (
    <button
      onClick={onClick}
      onMouseEnter={() => setHov(true)}
      onMouseLeave={() => setHov(false)}
      style={{
        border: s.border,
        background: s.bg,
        color: s.color,
        borderRadius: 'var(--radius-sm)',
        padding: '5px 12px',
        fontSize: 'var(--text-sm)',
        fontWeight: 600,
        cursor: 'pointer',
        transition: 'all var(--transition-fast)',
        lineHeight: 1,
      }}
    >
      {label}
    </button>
  );
}

function VehicleCard({
  v, isWorker, onEdit, onDelete,
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

  const s = STATUS_STYLE[v.status] ?? { bg: '#f1f5f9', color: '#64748b', label: v.status };
  const price = formatPrice(v.monthlyLeasePriceCents, v.currency);

  return (
    <div
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        background: 'var(--color-surface)',
        borderRadius: 'var(--radius-lg)',
        border: hovered ? '1px solid rgba(22,163,74,0.38)' : '1px solid var(--color-border)',
        borderTop: hovered ? '2px solid var(--color-brand)' : '2px solid rgba(22,163,74,0.35)',
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
        boxShadow: hovered
          ? '0 8px 28px rgba(22,163,74,0.13), 0 2px 8px rgba(0,0,0,0.06)'
          : 'var(--shadow-sm)',
        transform: hovered ? 'translateY(-3px)' : 'translateY(0)',
        transition: 'transform var(--transition-normal), box-shadow var(--transition-normal), border-color var(--transition-fast)',
      }}
    >
      {/* Image */}
      <div style={{ position: 'relative', width: '100%', height: 180, flexShrink: 0, overflow: 'hidden' }}>

        {/* Loading shimmer */}
        {imgSrc === 'loading' && (
          <div style={{
            position: 'absolute', inset: 0,
            background: 'linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'var(--color-brand)',
          }}>
            <IconCar size={52} opacity={0.18} />
          </div>
        )}

        {/* Loaded image */}
        {imgSrc && imgSrc !== 'loading' && (
          <img
            src={imgSrc}
            alt={`${v.brand} ${v.model}`}
            style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }}
          />
        )}

        {/* Fallback — no image returned */}
        {imgSrc === null && (
          <div style={{
            position: 'absolute', inset: 0,
            background: 'linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%)',
            display: 'flex', flexDirection: 'column',
            alignItems: 'center', justifyContent: 'center', gap: 8,
            color: 'var(--color-brand)',
          }}>
            <IconCar size={52} opacity={0.25} />
            <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-faint)', fontWeight: 500 }}>
              No image available
            </span>
          </div>
        )}

        {/* Dark gradient for badge readability */}
        <div style={{
          position: 'absolute', inset: 0,
          background: 'linear-gradient(to bottom, rgba(0,0,0,0.28) 0%, transparent 45%)',
          pointerEvents: 'none',
        }} />

        {/* Type badge — top left */}
        <span style={{
          position: 'absolute', top: 10, left: 10,
          background: 'rgba(15,30,20,0.68)',
          color: 'rgba(255,255,255,0.92)',
          borderRadius: 'var(--radius-sm)',
          padding: '3px 8px',
          fontSize: 10,
          fontWeight: 700,
          letterSpacing: 0.6,
          textTransform: 'uppercase' as const,
          backdropFilter: 'blur(6px)',
        }}>
          {v.vehicleType}
        </span>

        {/* Status badge — top right */}
        <span style={{
          position: 'absolute', top: 10, right: 10,
          background: s.bg,
          color: s.color,
          borderRadius: 'var(--radius-sm)',
          padding: '3px 8px',
          fontSize: 10,
          fontWeight: 700,
          letterSpacing: 0.4,
          boxShadow: 'var(--shadow-xs)',
        }}>
          {s.label}
        </span>
      </div>

      {/* Card body */}
      <div style={{ padding: '14px 16px 16px', display: 'flex', flexDirection: 'column', flex: 1, gap: 0 }}>

        {/* Brand + model */}
        <div style={{ fontWeight: 800, fontSize: 'var(--text-lg)', color: 'var(--color-text)', lineHeight: 1.15 }}>
          {v.brand}
        </div>
        <div style={{ fontWeight: 500, fontSize: 'var(--text-base)', color: 'var(--color-text-muted)', marginBottom: 12 }}>
          {v.model}
        </div>

        {/* Specs 2×2 grid */}
        <div style={{
          display: 'grid', gridTemplateColumns: '1fr 1fr',
          gap: '7px 8px',
          marginBottom: 14,
        }}>
          <SpecItem icon={<IconRange />}   label={`${v.rangeKm} km`} />
          <SpecItem icon={<IconBattery />} label={`${v.capacityKwh} kWh`} />
          <SpecItem icon={<IconPower />}   label={`${v.horsePowerHp} HP`} />
          <SpecItem icon={<IconClock />}   label={`${v.accelerationSec}s 0–100`} />
        </div>

        <div style={{ borderTop: '1px solid var(--color-border-light)', marginBottom: 12 }} />

        {/* Price + actions */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8 }}>
          <div style={{ minWidth: 0 }}>
            <div style={{
              fontSize: 10,
              fontWeight: 600,
              color: 'var(--color-text-faint)',
              letterSpacing: 0.5,
              textTransform: 'uppercase' as const,
              marginBottom: 2,
            }}>
              Purchase price
            </div>
            <span style={{
              fontWeight: 800,
              fontSize: 'var(--text-md)',
              color: 'var(--color-brand-dark)',
              whiteSpace: 'nowrap',
            }}>
              {price}
            </span>
          </div>

          {isWorker && (
            <div style={{ display: 'flex', gap: 6, flexShrink: 0 }}>
              <ActionBtn label="Edit"   variant="edit"   onClick={() => onEdit(v.vin)} />
              <ActionBtn label="Remove" variant="remove" onClick={() => onDelete(v.vin, `${v.brand} ${v.model}`)} />
            </div>
          )}
        </div>

        {/* VIN */}
        <div style={{
          marginTop: 10,
          paddingTop: 8,
          borderTop: '1px solid var(--color-border-light)',
          fontSize: 10,
          color: 'var(--color-text-faint)',
          fontFamily: 'ui-monospace, SFMono-Regular, monospace',
          letterSpacing: 0.6,
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
        }}>
          {v.vin}
        </div>
      </div>
    </div>
  );
}

// ─── Empty state ──────────────────────────────────────────────────────────────

function EmptyState({ hasSearch }: { hasSearch: boolean }) {
  return (
    <div style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
      gap: 14, padding: '72px 32px',
      background: 'var(--color-surface)', borderRadius: 'var(--radius-lg)',
      border: '1px solid var(--color-border)', boxShadow: 'var(--shadow-sm)',
    }}>
      <div style={{ color: 'var(--color-text-faint)' }}>
        <IconCar size={48} opacity={0.25} />
      </div>
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontWeight: 600, fontSize: 'var(--text-md)', color: 'var(--color-text-muted)', marginBottom: 4 }}>
          {hasSearch ? 'No vehicles match your search' : 'No vehicles in the catalogue'}
        </div>
        <div style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-faint)' }}>
          {hasSearch ? 'Try a different brand, model or VIN.' : 'Add the first vehicle to get started.'}
        </div>
      </div>
    </div>
  );
}

// ─── Page ─────────────────────────────────────────────────────────────────────

export default function CataloguePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isWorker = user?.role === 'WORKER';

  const [vehicles, setVehicles]     = useState<Vehicle[]>([]);
  const [search, setSearch]         = useState('');
  const [error, setError]           = useState('');
  const [searchFocused, setSearchFocused] = useState(false);
  const loadRef = useRef<() => void>(() => {});

  const load = async () => {
    try {
      const data = await vehiclesApi.list();
      setVehicles(data);
    } catch (_e) {
      setError('Failed to load vehicles');
    }
  };

  loadRef.current = load;

  useEffect(() => { load(); }, []);

  // SSE: auto-refresh when any vehicle status changes
  useEffect(() => {
    const es = new EventSource('/api/catalogue/vehicles/events');
    es.addEventListener('vehicle-status-changed', () => { loadRef.current(); });
    return () => es.close();
  }, []);

  const q = search.toLowerCase();
  const filtered = vehicles.filter(v =>
    v.vin.toLowerCase().includes(q) ||
    v.brand.toLowerCase().includes(q) ||
    v.model.toLowerCase().includes(q),
  );

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
    <div style={{ padding: 'var(--space-6)', minHeight: '100%', background: 'var(--color-bg)' }}>
      <PageHeader
        kicker="Fleet Management"
        title="EV Catalogue"
        subtitle="Browse and manage your electric vehicle inventory"
        action={isWorker ? (
          <button
            onClick={() => navigate('/dashboard/add-vehicle')}
            style={{
              background: 'var(--color-brand)', color: 'white',
              border: 'none', borderRadius: 'var(--radius-md)',
              padding: '8px 16px', fontWeight: 600, fontSize: 'var(--text-sm)', cursor: 'pointer',
              transition: 'background var(--transition-fast)',
            }}
            onMouseEnter={e => (e.currentTarget.style.background = 'var(--color-brand-dark)')}
            onMouseLeave={e => (e.currentTarget.style.background = 'var(--color-brand)')}
          >
            + Add Vehicle
          </button>
        ) : undefined}
      />

      {/* Search bar */}
      <div style={{
        background: 'var(--color-surface)',
        borderRadius: 'var(--radius-md)',
        padding: '9px 14px',
        marginBottom: 'var(--space-6)',
        display: 'flex', alignItems: 'center', gap: 10,
        border: searchFocused ? '1px solid rgba(22,163,74,0.55)' : '1px solid var(--color-border)',
        boxShadow: searchFocused
          ? '0 0 0 3px rgba(22,163,74,0.10), var(--shadow-xs)'
          : 'var(--shadow-xs)',
        transition: 'border-color var(--transition-fast), box-shadow var(--transition-fast)',
      }}>
        <span style={{ color: searchFocused ? 'var(--color-brand)' : 'var(--color-text-faint)', display: 'flex', transition: 'color var(--transition-fast)' }}>
          <IconSearch />
        </span>
        <input
          value={search}
          onChange={e => setSearch(e.target.value)}
          onFocus={() => setSearchFocused(true)}
          onBlur={() => setSearchFocused(false)}
          placeholder="Search by brand, model or VIN…"
          style={{
            flex: 1, border: 'none', outline: 'none',
            fontSize: 'var(--text-base)', background: 'transparent',
            color: 'var(--color-text)',
          }}
        />
        <button
          onClick={load}
          style={{
            border: 'none', background: 'none', cursor: 'pointer',
            color: 'var(--color-text-faint)', display: 'flex', padding: 2,
            borderRadius: 'var(--radius-sm)',
            transition: 'color var(--transition-fast)',
          }}
          title="Reload"
          onMouseEnter={e => (e.currentTarget.style.color = 'var(--color-brand)')}
          onMouseLeave={e => (e.currentTarget.style.color = 'var(--color-text-faint)')}
        >
          <IconRefresh />
        </button>
      </div>

      {error && (
        <div style={{
          color: 'var(--color-error)', marginBottom: 16, fontWeight: 500,
          background: 'var(--color-error-bg)', borderRadius: 'var(--radius-md)',
          padding: '10px 14px', fontSize: 'var(--text-sm)',
        }}>
          {error}
        </div>
      )}

      {/* Grouped sections or empty state */}
      {filtered.length === 0 ? (
        <EmptyState hasSearch={search.length > 0} />
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 36 }}>
          {STATUS_ORDER.map(status => {
            const group = filtered.filter(v => v.status === status);
            if (group.length === 0) return null;
            const style = STATUS_STYLE[status] ?? { bg: '#f1f5f9', color: '#64748b', label: status, sectionAccent: '#94a3b8' };
            return (
              <section key={status}>
                {/* Section header */}
                <div style={{
                  display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16,
                  paddingBottom: 10, borderBottom: `2px solid ${style.sectionAccent}22`,
                }}>
                  <span style={{
                    display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                    width: 10, height: 10, borderRadius: '50%',
                    background: style.sectionAccent, flexShrink: 0,
                  }} />
                  <span style={{
                    fontWeight: 700, fontSize: 'var(--text-base)',
                    color: style.sectionAccent, letterSpacing: 0.2,
                  }}>
                    {style.label}
                  </span>
                  <span style={{
                    background: `${style.sectionAccent}18`,
                    color: style.sectionAccent,
                    borderRadius: 20, padding: '1px 9px',
                    fontSize: 'var(--text-sm)', fontWeight: 600,
                  }}>
                    {group.length}
                  </span>
                </div>
                {/* Cards */}
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fill, minmax(272px, 1fr))',
                  gap: 22,
                }}>
                  {group.map(v => (
                    <VehicleCard
                      key={v.vin}
                      v={v}
                      isWorker={isWorker}
                      onEdit={vin => navigate(`/dashboard/update-vehicle/${vin}`)}
                      onDelete={handleDelete}
                    />
                  ))}
                </div>
              </section>
            );
          })}
        </div>
      )}
    </div>
  );
}
