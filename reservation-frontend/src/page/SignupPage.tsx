import { RegisterForm } from '../features/components/RegisterForm';
import { useRegister } from '../features/auth/hooks/useRegister';

export function SignupPage() {
  const register = useRegister();
  const css = `
    /* 전체 폼 스타일 */
form {
  background: #ffffff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 12px; /* 요소 사이의 간격 */
  align-items: center;
}

/* 입력 필드 공통 */
input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s ease;
}

input:focus {
  border-color: #4A90E2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
}

/* 이메일 입력창과 중복 확인 버튼 나란히 배치 */
input[type="email"] {
  flex: 1; /* 이메일 입력창이 남은 공간 차지 */
  margin-bottom: 0;
}

/* "이메일 중복 확인" 버튼 전용 스타일 */
button[type="button"] {
  width: auto;
  padding: 0 15px;
  height: 46px; /* input 높이와 맞춤 */
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap; /* 텍스트 줄바꿈 방지 */
  transition: background 0.2s;
}

button[type="button"]:hover:not(:disabled) {
  background-color: #e5e5e5;
}

button[type="button"]:disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* 중복 확인 메시지 (p 태그) */
p {
  font-size: 13px;
  margin: -4px 0 8px 4px; /* 입력창 바로 아래 붙게 설정 */
}

/* 회원가입 버튼 (메인 액션) */
button:not([type="button"]) {
  width: 100%;
  padding: 14px;
  background-color: #4A90E2;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 10px;
  transition: background 0.3s;
}

button:not([type="button"]):hover:not(:disabled) {
  background-color: #357ABD;
}

button:not([type="button"]):disabled {
  background-color: #cbd5e0;
  cursor: not-allowed;
}
`;

  return (
    <div>
      <h3>회원가입</h3>
      <RegisterForm
        onSubmit={({ email, username, password }) => {
          register.register({ email, username, password });
          window.alert('회원가입에 성공했습니다.');
        }}
        loading={register.loading}
        error={register.error}
        css={css}
      />
    </div>
  );
}
