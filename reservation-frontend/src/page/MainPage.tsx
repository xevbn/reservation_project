import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/AuthStore';
import { apiFetch } from '../api/apiFetch';

export default function MainPage() {
  const navigate = useNavigate();
  const clear = useAuthStore((s) => s.clear);

  const logout = async () => {
    apiFetch('/logout', {
      method: 'GET',
      credentials: 'include',
    });

    clear();
    navigate('/login');
  };

  return (
    <div style={{ padding: 24 }}>
      <header style={{ display: 'flex', justifyContent: 'space-between' }}>
        <h1>예약 시스템</h1>
        <button onClick={logout}>로그아웃</button>
      </header>

      <main style={{ marginTop: 32 }}>
        <section style={{ marginBottom: 24 }}>
          <h2>예약</h2>
          <p>리소스를 선택하고 날짜를 선택해 예약을 진행하세요.</p>
          <button onClick={() => navigate('resources')}>예약하러 가기</button>
        </section>

        <section>
          <h2>내 예약</h2>
          <button onClick={() => navigate('/my-reservation')}>
            내 예약 보기
          </button>
        </section>
      </main>
    </div>
  );
}
