import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

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
        width: 220,
        flexShrink: 0,
        background: 'linear-gradient(180deg, #1a237e 0%, #283593 60%, #3949ab 100%)',
        display: 'flex',
        flexDirection: 'column',
        padding: '16px 8px',
      }}>
        {/* Logo */}
        <div style={{
          color: 'white', fontWeight: 700, fontSize: 15,
          display: 'flex', alignItems: 'center', gap: 8,
          padding: '8px 8px 20px',
        }}>
          ⚡ GreenFleet
        </div>

        {/* Section label */}
        <span style={{
          color: 'rgba(255,255,255,0.5)', fontSize: 10,
          fontWeight: 700, letterSpacing: 1,
          padding: '4px 10px 8px',
        }}>
          SECTIONS
        </span>

        {/* EV Catalogue */}
        <NavLink
          to="/dashboard/catalogue"
          style={({ isActive }) => ({
            display: 'flex', alignItems: 'center', gap: 8,
            padding: '9px 12px', borderRadius: 6, textDecoration: 'none',
            color: 'white', fontWeight: isActive ? 600 : 400,
            background: isActive ? 'rgba(255,255,255,0.18)' : 'transparent',
            marginBottom: 2,
          })}
        >
          🚗 EV Catalogue
        </NavLink>

        {/* Fleet Consulting — greyed out */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          padding: '9px 12px', borderRadius: 6,
          color: 'rgba(255,255,255,0.4)',
        }}>
          📊 Fleet Consulting
          <span style={{
            marginLeft: 'auto', fontSize: 9,
            background: 'rgba(255,255,255,0.12)',
            padding: '2px 6px', borderRadius: 4,
          }}>soon</span>
        </div>

        {/* Sales — greyed out */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          padding: '9px 12px', borderRadius: 6,
          color: 'rgba(255,255,255,0.4)',
        }}>
          💼 Sales
          <span style={{
            marginLeft: 'auto', fontSize: 9,
            background: 'rgba(255,255,255,0.12)',
            padding: '2px 6px', borderRadius: 4,
          }}>soon</span>
        </div>

        {/* Spacer */}
        <div style={{ flex: 1 }} />

        {/* Footer */}
        <div style={{ borderTop: '1px solid rgba(255,255,255,0.12)', paddingTop: 12 }}>
          <div style={{
            color: 'rgba(255,255,255,0.55)', fontSize: 12,
            padding: '4px 10px',
          }}>
            👤 {user?.username}
          </div>
          <button
            onClick={handleLogout}
            style={{
              width: '100%', textAlign: 'left',
              display: 'flex', alignItems: 'center', gap: 6,
              padding: '8px 10px', borderRadius: 6, border: 'none',
              background: 'transparent', color: 'rgba(255,255,255,0.65)',
              fontSize: 13,
            }}
          >
            🚪 Logout
          </button>
        </div>
      </nav>

      {/* Page content */}
      <main style={{ flex: 1, overflow: 'auto', background: '#f0f4f8' }}>
        <Outlet />
      </main>
    </div>
  );
}
