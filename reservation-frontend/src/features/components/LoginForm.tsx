import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

type Props = {
  onSubmit: (username: string, password: string) => void;
  loading: boolean;
  error: string | null;
  css: string | null;
};

export const LoginForm = ({ onSubmit, loading, error, css }: Props) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  return (
    <div className="login-container">
      <style>{css}</style>
      <form
        className="login-form"
        onSubmit={(e) => {
          e.preventDefault();
          onSubmit(username, password);
        }}
      >
        <label>사용자명</label>
        <input
          className="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <label>비밀번호</label>
        <input
          className="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {error && <p>{error}</p>}

        <button className="loading" disabled={loading}>
          {loading ? '로그인 중...' : '로그인'}
        </button>

        <div className="mt-4">
          <p className="mb-2">또는 소셜 로그인</p>
          <a href={`/login/oauth2/code/google`}>구글 로그인</a>
        </div>
        <button className="register-btn" onClick={() => navigate('/register')}>
          회원가입
        </button>
      </form>
    </div>
  );
};
