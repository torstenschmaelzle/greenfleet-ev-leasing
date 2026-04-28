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
      background: '#f0f4f8',
    }}>
      <div style={{
        width: 380, borderRadius: 12,
        boxShadow: '0 4px 20px rgba(0,0,0,0.12)',
        overflow: 'hidden', background: 'white',
      }}>
        {/* Header */}
        <div style={{
          background: 'linear-gradient(135deg, #1a237e 0%, #283593 60%, #3949ab 100%)',
          padding: '24px 32px',
          display: 'flex', alignItems: 'center', gap: 16,
        }}>
          <div style={{
            width: 48, height: 48, borderRadius: '50%',
            background: 'rgba(255,255,255,0.15)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 22, flexShrink: 0,
          }}>⚡</div>
          <div>
            <div style={{ color: 'white', fontWeight: 700, fontSize: 20 }}>GreenFleet</div>
            <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 13 }}>Sign in to continue</div>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} style={{ padding: '28px 32px', display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
            <label style={{ fontSize: 13, fontWeight: 500 }}>Username</label>
            <input
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="worker or customer"
              required
              style={{
                padding: '9px 12px', borderRadius: 6, fontSize: 14,
                border: '1px solid #d0d5dd', outline: 'none',
              }}
            />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
            <label style={{ fontSize: 13, fontWeight: 500 }}>Password</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              style={{
                padding: '9px 12px', borderRadius: 6, fontSize: 14,
                border: '1px solid #d0d5dd', outline: 'none',
              }}
            />
          </div>
          {error && (
            <div style={{
              background: '#fef2f2', color: '#b91c1c', borderRadius: 6,
              padding: '8px 12px', fontSize: 13,
            }}>
              {error}
            </div>
          )}
          <button
            type="submit"
            disabled={loading}
            style={{
              background: '#1a237e', color: 'white', border: 'none',
              borderRadius: 6, padding: '11px', fontSize: 14,
              fontWeight: 600, marginTop: 4,
              opacity: loading ? 0.7 : 1,
            }}
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  );
}
