import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import CataloguePage from './pages/CataloguePage';
import AddVehiclePage from './pages/AddVehiclePage';
import UpdateVehiclePage from './pages/UpdateVehiclePage';
import DashboardLayout from './components/DashboardLayout';
import FleetConsultingPage from './pages/FleetConsultingPage';
import SalesPage from './pages/SalesPage';
import ContractsPage from './pages/ContractsPage';
import MyContractsPage from './pages/MyContractsPage';

function RequireAuth({ children }: { children: JSX.Element }) {
  const { user, loading } = useAuth();
  if (loading) return <div style={{ padding: 32 }}>Loading…</div>;
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

function RequireWorker({ children }: { children: JSX.Element }) {
  const { user } = useAuth();
  if (user?.role !== 'WORKER') return <Navigate to="/dashboard/fleet-consulting" replace />;
  return children;
}

function RequireCustomer({ children }: { children: JSX.Element }) {
  const { user } = useAuth();
  if (user?.role !== 'CUSTOMER') return <Navigate to="/dashboard/catalogue" replace />;
  return children;
}

function DashboardIndex() {
  const { user } = useAuth();
  return <Navigate to={user?.role === 'WORKER' ? 'catalogue' : 'fleet-consulting'} replace />;
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
            <Route index element={<DashboardIndex />} />
            <Route
              path="catalogue"
              element={
                <RequireWorker>
                  <CataloguePage />
                </RequireWorker>
              }
            />
            <Route path="fleet-consulting" element={<FleetConsultingPage />} />
            <Route path="sales" element={<SalesPage />} />
            <Route path="my-contracts" element={<RequireCustomer><MyContractsPage /></RequireCustomer>} />
            <Route
              path="contracts"
              element={
                <RequireWorker>
                  <ContractsPage />
                </RequireWorker>
              }
            />
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
