import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import logo from '../assets/logo.png';

function IconCar() {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M5 17H3v-6l2.5-5h13L21 11v6h-2" />
      <path d="M5 17h14" />
      <circle cx="7.5" cy="17" r="2" />
      <circle cx="16.5" cy="17" r="2" />
      <path d="M3 11h18" />
    </svg>
  );
}

function IconChart() {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <line x1="18" y1="20" x2="18" y2="10" />
      <line x1="12" y1="20" x2="12" y2="4" />
      <line x1="6" y1="20" x2="6" y2="14" />
      <line x1="2" y1="20" x2="22" y2="20" />
    </svg>
  );
}


function IconContracts() {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <polyline points="14 2 14 8 20 8" />
      <line x1="16" y1="13" x2="8" y2="13" />
      <line x1="16" y1="17" x2="8" y2="17" />
      <polyline points="10 9 9 9 8 9" />
    </svg>
  );
}

function IconUser() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="8" r="4" />
      <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
    </svg>
  );
}

function IconLogOut() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <polyline points="16 17 21 12 16 7" />
      <line x1="21" y1="12" x2="9" y2="12" />
    </svg>
  );
}

const navLinkBase: React.CSSProperties = {
  display: 'flex', alignItems: 'center', gap: 9,
  padding: '9px 12px', borderRadius: 'var(--radius-sm)', textDecoration: 'none',
  fontSize: 'var(--text-base)', marginBottom: 2,
  transition: 'background var(--transition-fast)',
};

const navStyle = ({ isActive }: { isActive: boolean }): React.CSSProperties => ({
  ...navLinkBase,
  color: isActive ? '#ffffff' : 'var(--color-sidebar-text)',
  fontWeight: isActive ? 600 : 400,
  background: isActive ? 'rgba(22,163,74,0.30)' : 'transparent',
  boxShadow: isActive ? 'inset 2px 0 0 #4ade80' : 'none',
});

export default function DashboardLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div style={{ display: 'flex', height: '100vh' }}>
      {/* Sidebar */}
      <nav style={{
        width: 224,
        flexShrink: 0,
        background: 'var(--color-sidebar-bg)',
        display: 'flex',
        flexDirection: 'column',
        padding: '0 8px 12px',
      }}>
        {/* Brand area */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '16px 10px 18px',
          borderBottom: '1px solid var(--color-sidebar-divider)',
          marginBottom: 12,
        }}>
          <img
            src={logo}
            alt="GreenFleet logo"
            style={{ width: 32, height: 32, objectFit: 'contain', flexShrink: 0 }}
          />
          <span style={{
            color: 'var(--color-sidebar-text)',
            fontWeight: 700,
            fontSize: 'var(--text-md)',
            letterSpacing: -0.3,
          }}>
            GreenFleet
          </span>
        </div>

        {/* Section label */}
        <span style={{
          color: 'var(--color-sidebar-muted)',
          fontSize: 10,
          fontWeight: 700,
          letterSpacing: 1.2,
          padding: '0 10px 6px',
          textTransform: 'uppercase',
        }}>
          Navigation
        </span>

        {user?.role === 'WORKER' && (<>
          <NavLink to="/dashboard/catalogue" style={navStyle}>
            <IconCar />EV Catalogue
          </NavLink>
          <NavLink to="/dashboard/fleet-consulting" style={navStyle}>
            <IconChart />Find Your Fleet
          </NavLink>
          <NavLink to="/dashboard/contracts" style={navStyle}>
            <IconContracts />Contracts
          </NavLink>
        </>)}

        {user?.role === 'CUSTOMER' && (<>
          <NavLink to="/dashboard/fleet-consulting" style={navStyle}>
            <IconChart />Find Your Fleet
          </NavLink>
          <NavLink to="/dashboard/my-contracts" style={navStyle}>
            <IconContracts />My Contracts
          </NavLink>
        </>)}

        {/* Spacer */}
        <div style={{ flex: 1 }} />

        {/* User / Logout footer */}
        <div style={{ borderTop: '1px solid var(--color-sidebar-divider)', paddingTop: 10 }}>
          <div style={{
            display: 'flex', alignItems: 'center', gap: 8,
            color: 'var(--color-sidebar-muted)',
            fontSize: 'var(--text-sm)',
            padding: '6px 10px 4px',
          }}>
            <span style={{
              width: 26, height: 26, borderRadius: '50%',
              background: 'var(--color-sidebar-faint)',
              display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
            }}>
              <IconUser />
            </span>
            <span style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {user?.role === 'CUSTOMER' ? (user.name ?? user.username) : user?.username}
            </span>
          </div>
          <button
            onClick={handleLogout}
            style={{
              width: '100%', textAlign: 'left',
              display: 'flex', alignItems: 'center', gap: 8,
              padding: '7px 10px', borderRadius: 'var(--radius-sm)', border: 'none',
              background: 'transparent', color: 'var(--color-sidebar-muted)',
              fontSize: 'var(--text-sm)', cursor: 'pointer',
              transition: 'background var(--transition-fast)',
            }}
            onMouseEnter={e => (e.currentTarget.style.background = 'var(--color-sidebar-faint)')}
            onMouseLeave={e => (e.currentTarget.style.background = 'transparent')}
          >
            <IconLogOut />
            Log out
          </button>
        </div>
      </nav>

      {/* Page content */}
      <main style={{ flex: 1, overflow: 'auto', background: 'var(--color-bg)' }}>
        <Outlet />
      </main>
    </div>
  );
}
