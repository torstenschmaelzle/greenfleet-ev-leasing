import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const inputStyle: React.CSSProperties = {
  padding: '9px 12px', borderRadius: 6, fontSize: 14,
  border: '1px solid var(--color-border)', outline: 'none',
  transition: 'border-color 0.15s',
  background: 'var(--color-surface)', color: 'var(--color-text)',
};

export default function LoginPage() {
  const { login, customerLogin, user } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState<'worker' | 'customer'>('customer');

  // Worker form
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  // Customer form
  const [name,    setName]    = useState('');
  const [email,   setEmail]   = useState('');
  const [company, setCompany] = useState('');

  const [error,   setError]   = useState('');
  const [loading, setLoading] = useState(false);

  if (user) {
    navigate(user.role === 'WORKER' ? '/dashboard/catalogue' : '/dashboard/fleet-consulting');
    return null;
  }

  const handleWorkerSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(''); setLoading(true);
    try {
      await login(username, password);
      navigate('/dashboard/catalogue');
    } catch {
      setError('Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  const handleCustomerSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(''); setLoading(true);
    try {
      await customerLogin(name.trim(), email.trim(), company.trim());
      navigate('/dashboard/fleet-consulting');
    } catch {
      setError('Please check your details and try again');
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
        width: 400, borderRadius: 'var(--radius-lg)',
        boxShadow: 'var(--shadow-lg)',
        overflow: 'hidden', background: 'var(--color-surface)',
      }}>
        {/* Brand header */}
        <div style={{
          background: 'linear-gradient(135deg, var(--color-sidebar-bg) 0%, var(--color-brand-darker) 60%, var(--color-brand-dark) 100%)',
          padding: '24px 32px',
          display: 'flex', alignItems: 'center', gap: 12,
        }}>
          <div style={{
            width: 48, height: 48, borderRadius: '50%',
            background: 'rgba(255,255,255,0.12)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 22, flexShrink: 0,
          }}>🌿</div>
          <div>
            <div style={{ color: 'white', fontWeight: 700, fontSize: 18 }}>GreenFleet</div>
            <div style={{ color: 'rgba(255,255,255,0.65)', fontSize: 13 }}>Sign in to continue</div>
          </div>
        </div>

        {/* Tabs */}
        <div style={{ display: 'flex', borderBottom: '1px solid var(--color-border)' }}>
          {(['customer', 'worker'] as const).map(t => (
            <button key={t} onClick={() => { setTab(t); setError(''); }}
              style={{
                flex: 1, padding: '12px', fontSize: 14, fontWeight: tab === t ? 700 : 400,
                border: 'none', cursor: 'pointer',
                background: tab === t ? 'var(--color-surface)' : 'var(--color-bg)',
                color: tab === t ? 'var(--color-brand)' : 'var(--color-text-muted)',
                borderBottom: tab === t ? '2px solid var(--color-brand)' : '2px solid transparent',
                transition: 'all 0.15s',
              }}>
              {t === 'customer' ? 'Customer' : 'Worker'}
            </button>
          ))}
        </div>

        <div style={{ padding: '28px 32px', display: 'flex', flexDirection: 'column', gap: 16 }}>

          {/* Customer tab */}
          {tab === 'customer' && (
            <>
              <div style={{ fontSize: 13, color: 'var(--color-text-muted)', lineHeight: 1.5 }}>
                Enter your details to access fleet consulting and your contracts.
                First time? A profile will be created automatically.
              </div>
              <form onSubmit={handleCustomerSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                {[
                  { label: 'Full name',    value: name,    set: setName,    type: 'text',  placeholder: 'Jane Smith' },
                  { label: 'Business email', value: email, set: setEmail,   type: 'email', placeholder: 'jane@acme.com' },
                  { label: 'Company name', value: company, set: setCompany, type: 'text',  placeholder: 'Acme AG' },
                ].map(({ label, value, set, type, placeholder }) => (
                  <div key={label} style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
                    <label style={{ fontSize: 12, fontWeight: 600, color: 'var(--color-text-secondary)', textTransform: 'uppercase', letterSpacing: 0.5 }}>
                      {label}
                    </label>
                    <input required type={type} value={value} onChange={e => set(e.target.value)}
                      placeholder={placeholder} style={inputStyle}
                      onFocus={e => e.target.style.borderColor = 'var(--color-brand)'}
                      onBlur={e => e.target.style.borderColor = 'var(--color-border)'}
                    />
                  </div>
                ))}
                {error && <ErrorMsg msg={error} />}
                <SubmitBtn loading={loading} label="Find Your Fleet" />
              </form>
            </>
          )}

          {/* Worker tab */}
          {tab === 'worker' && (
            <form onSubmit={handleWorkerSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              {[
                { label: 'Username', value: username, set: setUsername, type: 'text',     placeholder: 'worker' },
                { label: 'Password', value: password, set: setPassword, type: 'password', placeholder: '' },
              ].map(({ label, value, set, type, placeholder }) => (
                <div key={label} style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
                  <label style={{ fontSize: 12, fontWeight: 600, color: 'var(--color-text-secondary)', textTransform: 'uppercase', letterSpacing: 0.5 }}>
                    {label}
                  </label>
                  <input required type={type} value={value} onChange={e => set(e.target.value)}
                    placeholder={placeholder} style={inputStyle}
                    onFocus={e => e.target.style.borderColor = 'var(--color-brand)'}
                    onBlur={e => e.target.style.borderColor = 'var(--color-border)'}
                  />
                </div>
              ))}
              {error && <ErrorMsg msg={error} />}
              <SubmitBtn loading={loading} label="Sign in" />
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

function ErrorMsg({ msg }: { msg: string }) {
  return (
    <div style={{
      background: 'var(--color-error-bg)', color: 'var(--color-error)',
      borderRadius: 6, padding: '8px 12px', fontSize: 13,
    }}>{msg}</div>
  );
}

function SubmitBtn({ loading, label }: { loading: boolean; label: string }) {
  return (
    <button type="submit" disabled={loading} style={{
      background: 'var(--color-brand)', color: 'white', border: 'none',
      borderRadius: 6, padding: '11px', fontSize: 14, fontWeight: 600,
      marginTop: 4, opacity: loading ? 0.7 : 1,
      cursor: loading ? 'not-allowed' : 'pointer',
      transition: 'background 0.15s',
    }}
      onMouseEnter={e => { if (!loading) (e.currentTarget as HTMLButtonElement).style.background = 'var(--color-brand-dark)'; }}
      onMouseLeave={e => { (e.currentTarget as HTMLButtonElement).style.background = 'var(--color-brand)'; }}
    >
      {loading ? 'Loading…' : label}
    </button>
  );
}
