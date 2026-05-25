import { LoginForm } from '../features/components/LoginForm';
import { useLogin } from '../features/auth/hooks/useLogin';

const css = `
/* 컨테이너: 화면 정중앙 배치 */
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
}

/* 폼 스타일 */
.login-form {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 380px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 레이블 (text 태그) */
.login-form label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

/* 입력창 공통 */
.username, .password {
  width: 100%;
  padding: 12px;
  margin-bottom: 20px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.username:focus, .password:focus {
  border-color: #007bff;
  outline: none;
}

/* 에러 메시지 */
.login-form p {
  color: #dc3545;
  font-size: 13px;
  margin-bottom: 15px;
}

/* 로그인 버튼 (로딩 상태 포함) */
.loading {
  width: 100%;
  padding: 14px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.loading:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 소셜 로그인 섹션 */
.mt-4 {
  margin-top: 24px;
  text-align: center;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.mt-4 p {
  color: #888;
  font-size: 14px;
  margin-bottom: 12px;
}

.mt-4 a {
  display: block;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  text-decoration: none;
  color: #333;
  font-size: 14px;
  transition: background 0.2s;
}

.mt-4 a:hover {
  background-color: #f1f1f1;
}

/* 회원가입 버튼 (폼 외부) */
.register-btn {
  margin-top: 20px;
  background: none;
  border: none;
  color: #666;
  text-decoration: underline;
  cursor: pointer;
  font-size: 14px;
}
`;

export function LoginPage() {
  const login = useLogin();

  return (
    <div>
      <LoginForm
        onSubmit={login.login}
        loading={login.loading}
        error={login.error}
        css={css}
      />
    </div>
  );
}
