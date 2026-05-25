import { Navigate } from 'react-router-dom';
import { useAuthStore } from './stores/AuthStore';

export const AdminRoute = ({ children }: { children: React.ReactNode }) => {
  const { userInfo } = useAuthStore();

  if (!userInfo) return <Navigate to="/login" replace />;

  if (userInfo.role !== 'ADMIN') {
    alert('관리자 권한이 없습니다.');
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};
