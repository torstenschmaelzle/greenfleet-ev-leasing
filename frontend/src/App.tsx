import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import CataloguePage from './pages/CataloguePage';
import AddVehiclePage from './pages/AddVehiclePage';
import UpdateVehiclePage from './pages/UpdateVehiclePage';
import DashboardLayout from './components/DashboardLayout';

function RequireAuth({ children }: { children: JSX.Element }) {
  const { user, loading } = useAuth();
  if (loading) return <div style={{ padding: 32 }}>Loading…</div>;
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

function RequireWorker({ children }: { children: JSX.Element }) {
  const { user } = useAuth();
  if (user?.role !== 'WORKER') return <Navigate to="/dashboard/catalogue" replace />;
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route
            path="/dashboard"
            element={
              <RequireAuth>
                <DashboardLayout />
              </RequireAuth>
            }
          >
            <Route index element={<Navigate to="catalogue" replace />} />
            <Route path="catalogue" element={<CataloguePage />} />
            <Route
              path="add-vehicle"
              element={
                <RequireWorker>
                  <AddVehiclePage />
                </RequireWorker>
              }
            />
            <Route
              path="update-vehicle/:vin"
              element={
                <RequireWorker>
                  <UpdateVehiclePage />
                </RequireWorker>
              }
            />
          </Route>
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
