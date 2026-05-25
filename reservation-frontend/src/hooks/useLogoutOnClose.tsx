import { useEffect } from 'react';
import { logoutApi } from '../features/auth/api/logoutApi';
import { useAuthStore } from '../stores/AuthStore';

export function useLogoutOnClose() {
  const { isLogin, setIsLogin, clear } = useAuthStore();
  useEffect(() => {
    const handleBeforeUnload = () => {
      if (!isLogin) return;
      logoutApi();
      clear();
      setIsLogin(false);
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [clear, isLogin, setIsLogin]);
}
