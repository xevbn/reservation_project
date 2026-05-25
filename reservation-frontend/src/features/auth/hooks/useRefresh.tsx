import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../../stores/AuthStore';
import { refreshToken } from '../../../api/apiFetch';
import { useCallback, useState } from 'react';

export const useRefresh = () => {
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const { setAccessToken, clear, isLogin } = useAuthStore();

  const refresh = useCallback(async () => {
    if (!isLogin) return;
    setIsLoading(true);
    try {
      const res = await refreshToken();

      if (res.ok) {
        const data = await res.json();
        const token = data.Authorization;
        setAccessToken(token);
      } else {
        clear();
        navigate('/login');
      }
    } catch (error) {
      console.error('refresh 에러:', error);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, [clear, isLogin, navigate, setAccessToken]);

  return { refresh, isLoading };
};
