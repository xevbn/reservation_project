import type { ReactNode } from 'react';
import { useAuthStore } from '../../stores/AuthStore';
import { Navigate } from 'react-router-dom';

export default function ProtectedRoute({ children }: { children: ReactNode }) {
  const token = useAuthStore((s) => s.accessToken);
  if (!token) return <Navigate to="/login" />;
  return <>{children}</>;
}
