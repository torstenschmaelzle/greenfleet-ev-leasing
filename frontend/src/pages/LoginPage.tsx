import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login, user } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (user) { navigate('/dashboard/catalogue'); return null; }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(username, password);
      navigate('/dashboard/catalogue');
    } catch {
      setError('Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex',
      alignItems: 'center', justifyContent: 'center',
      background: 'var(--color-bg)',
    }}>
      <div style={{
        width: 380, borderRadius: 'var(--radius-lg)',
        boxShadow: 'var(--shadow-lg)',
        overflow: 'hidden', background: 'var(--color-surface)',
      }}>
        {/* Header — green brand */}
        <div style={{
          background: 'linear-gradient(135deg, var(--color-sidebar-bg) 0%, var(--color-brand-darker) 60%, var(--color-brand-dark) 100%)',
          padding: '24px 32px',
          display: 'flex', alignItems: 'center', gap: 'var(--space-4)',
        }}>
          <div style={{
            width: 48, height: 48, borderRadius: '50%',
            background: 'rgba(255,255,255,0.12)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 22, flexShrink: 0,
          }}>🌿</div>
          <div>
            <div style={{ color: 'white', fontWeight: 700, fontSize: 'var(--text-xl)' }}>GreenFleet</div>
            <div style={{ color: 'rgba(255,255,255,0.65)', fontSize: 'var(--text-sm)' }}>Sign in to continue</div>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} style={{ padding: '28px 32px', display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-1)' }}>
            <label style={{ fontSize: 'var(--text-sm)', fontWeight: 500, color: 'var(--color-text-secondary)' }}>Username</label>
            <input
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="worker or customer"
              required
              style={{
                padding: '9px 12px', borderRadius: 'var(--radius-sm)', fontSize: 'var(--text-base)',
                border: '1px solid var(--color-border)', outline: 'none',
                transition: 'border-color var(--transition-fast)',
              }}
              onFocus={e => e.target.style.borderColor = 'var(--color-brand)'}
              onBlur={e => e.target.style.borderColor = 'var(--color-border)'}
            />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-1)' }}>
            <label style={{ fontSize: 'var(--text-sm)', fontWeight: 500, color: 'var(--color-text-secondary)' }}>Password</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              style={{
                padding: '9px 12px', borderRadius: 'var(--radius-sm)', fontSize: 'var(--text-base)',
                border: '1px solid var(--color-border)', outline: 'none',
                transition: 'border-color var(--transition-fast)',
              }}
              onFocus={e => e.target.style.borderColor = 'var(--color-brand)'}
              onBlur={e => e.target.style.borderColor = 'var(--color-border)'}
            />
          </div>
          {error && (
            <div style={{
              background: 'var(--color-error-bg)', color: 'var(--color-error)',
              borderRadius: 'var(--radius-sm)',
              padding: '8px 12px', fontSize: 'var(--text-sm)',
            }}>
              {error}
            </div>
          )}
          <button
            type="submit"
            disabled={loading}
            style={{
              background: 'var(--color-brand)', color: 'white', border: 'none',
              borderRadius: 'var(--radius-sm)', padding: '11px', fontSize: 'var(--text-base)',
              fontWeight: 600, marginTop: 4,
              opacity: loading ? 0.7 : 1,
              transition: 'background var(--transition-fast)',
              cursor: loading ? 'not-allowed' : 'pointer',
            }}
            onMouseEnter={e => { if (!loading) (e.currentTarget as HTMLButtonElement).style.background = 'var(--color-brand-dark)'; }}
            onMouseLeave={e => { (e.currentTarget as HTMLButtonElement).style.background = 'var(--color-brand)'; }}
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  );
}
