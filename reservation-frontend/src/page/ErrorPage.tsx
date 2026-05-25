import { useNavigate } from 'react-router-dom';

export const ErrorPage = (error: Error) => {
  const navigate = useNavigate();

  return (
    <div>
      <h1>오류가 발생했습니다</h1>
      <p>페이지를 표시할 수 없습니다. 잠시 후 시도해주세요.</p>

      {error && (
        <div
          style={{
            marginBottom: '20px',
            backgroundColor: '#e9ecef',
            padding: '15px',
            borderRadius: '5px',
          }}
        >
          <h3>오류 상세 정보:</h3>
          <p style={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap' }}>
            {error.message || JSON.stringify(error, null, 2)}
          </p>
        </div>
      )}
      <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
    </div>
  );
};
