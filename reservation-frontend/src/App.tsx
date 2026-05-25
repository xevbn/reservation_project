import './App.css';
import { Route, Routes } from 'react-router-dom';
import ProtectedRoute from './features/components/ProtectedRoute';
import OAuthRedirectPage from './page/OAuthRedirectPage';
import ResourceListPage from './page/ResourceListPage';
import { LoginPage } from './page/LoginPage';
import { SignupPage } from './page/SignupPage';
import ReservationPage from './page/ReservationPage';
import { useLogoutOnClose } from './hooks/useLogoutOnClose';
import { useEffect } from 'react';
import { useRefresh } from './features/auth/hooks/useRefresh';
import { useAuthStore } from './stores/AuthStore';
import TestPage from './page/TestPage';
import { ReservationListPage } from './page/ReservationListPage';
import { ResourceEditPage } from './page/ResourceEditPage';
import { AdminRoute } from './AdminRoute';

function HomePage() {
  return <div>Home</div>;
}

function App() {
  const { accessToken } = useAuthStore();
  const { refresh } = useRefresh();

  useEffect(() => {
    const init = async () => {
      await refresh();
    };

    const isPublicPath =
      window.location.pathname === '/register' ||
      window.location.pathname === '/login';

    if (!isPublicPath && !accessToken) init();
  }, [accessToken, refresh]);
  useLogoutOnClose();

  //  if (isLoading) return <LoadingSpinnerPage />;

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/oauth/success" element={<OAuthRedirectPage />} />
      <Route path="/resource" element={<ResourceListPage />} />
      <Route path="/register" element={<SignupPage />} />
      <Route path="/reservation" element={<ReservationPage />} />
      <Route path="/resourceList" element={<ResourceListPage />} />
      <Route path="/test" element={<TestPage />} />
      <Route path="/reservationList" element={<ReservationListPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <HomePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin"
        element={
          <AdminRoute>
            <ResourceEditPage />
          </AdminRoute>
        }
      />
    </Routes>
  );
}

export default App;
