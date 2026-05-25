import { useState } from 'react';
import { checkEmailApi } from '../auth/api/checkEmailApi';

type Props = {
  onSubmit: (v: { email: string; username: string; password: string }) => void;
  loading: boolean;
  error: string | null;
  css: string | null;
};

export const RegisterForm = ({ onSubmit, loading, error, css }: Props) => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [passwordChk, setPasswordChk] = useState('');
  const [isDuplicate, setIsDuplicate] = useState<boolean | null>(null);
  const checkEmail = async () => {
    const available = await checkEmailApi(email);
    setIsDuplicate(!available);
  };

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        if (isDuplicate) {
          alert('이메일 중복을 확인해주세요');
          return;
        } else if (username === '' || password === '') {
          alert('사용자 이름이나 비밀번호가 비어있습니다.');
          return;
        } else if (password !== passwordChk) {
          alert('비밀번호가 일치하지 않습니다.');
          return;
        }
        onSubmit({ email, username, password });
      }}
    >
      <style>{css}</style>
      <input
        type="email"
        value={email}
        onChange={(e) => {
          setEmail(e.target.value);
        }}
        placeholder="이메일 입력"
      />
      <button type="button" onClick={checkEmail} disabled={email === ''}>
        이메일 중복 확인
      </button>
      {isDuplicate === null ? (
        <></>
      ) : isDuplicate ? (
        <p style={{ color: 'red' }}>이미 사용 중인 이메일입니다</p>
      ) : (
        <p style={{ color: 'green' }}>사용 가능한 이메일입니다</p>
      )}

      <input
        value={username}
        onChange={(e) => {
          setUsername(e.target.value);
        }}
        placeholder="닉네임"
      />
      <input
        type="password"
        value={password}
        onChange={(e) => {
          setPassword(e.target.value);
        }}
        placeholder="비밀번호"
      />
      <input
        type="password"
        value={passwordChk}
        onChange={(e) => {
          setPasswordChk(e.target.value);
        }}
        placeholder="비밀번호 확인"
      />

      {error && <p>{error}</p>}
      <button disabled={loading}>{loading ? '처리 중...' : '회원가입'}</button>
    </form>
  );
};
