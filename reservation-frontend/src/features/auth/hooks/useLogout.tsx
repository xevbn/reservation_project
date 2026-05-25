import { useNavigate } from 'react-router-dom';
import { logoutApi } from '../api/logoutApi';
import { useState } from 'react';
import { useAuthStore } from '../../../stores/AuthStore';

export function useLogout() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { clear, isLogin, setIsLogin } = useAuthStore();

  const logout = async () => {
    if (!isLogin) return;
    setLoading(true);
    setError(null);

    try {
      const res = await logoutApi();
      if (res.ok) {
        setIsLogin(false);
        clear();
      }

      navigate('/login');
    } catch {
      setError('로그 아웃 실패');
    } finally {
      setLoading(false);
    }
  };

  return { logout, loading, error };
}
