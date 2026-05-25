import { useNavigate, useSearchParams } from 'react-router-dom';
import { useEffect } from 'react';
import { useAuthStore } from '../stores/AuthStore';

export default function OAuthRedirectPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('accessToken');
  const userInfoStr = searchParams.get('userInfo');
  const userInfo = userInfoStr !== null ? JSON.parse(userInfoStr) : null;
  const { setAccessToken, setUserInfo, isLogin, setIsLogin } = useAuthStore();

  useEffect(() => {
    if (token != null && userInfo != null && !isLogin) {
      setAccessToken(token);
      setUserInfo(userInfo);
      setIsLogin(true);
      navigate('/resourceList', { replace: true });
    }
  }, [
    isLogin,
    navigate,
    setAccessToken,
    setIsLogin,
    setUserInfo,
    token,
    userInfo,
  ]);

  return (
    <div style={{ padding: 32 }}>
      <h2>로그인 처리 중...</h2>
    </div>
  );
}
