import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../../stores/AuthStore';
import { useState } from 'react';
import { loginApi } from '../api/loginApi';

export function useLogin() {
  const navigate = useNavigate();
  const { setAccessToken, setUserInfo, isLogin, setIsLogin } = useAuthStore();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (username: string, password: string) => {
    if (isLogin) {
      console.log('로그인 오류: 이미 로그인이 되어있다고 인식 중');
    }
    setLoading(true);
    setError(null);

    try {
      const { Authorization, userInfo } = await loginApi(username, password);
      setAccessToken(Authorization);
      setUserInfo(userInfo);
      setIsLogin(true);

      if (userInfo.role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/resourceList');
      }
    } catch {
      setError('유저 이름 또는 비밀번호가 올바르지 않습니다.');
    } finally {
      setLoading(false);
    }
  };

  return { login, loading, error };
}
