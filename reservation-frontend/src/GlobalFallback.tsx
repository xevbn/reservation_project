import type { FallbackProps } from 'react-error-boundary';

export function GlobalErrorFallback({
  error,
  resetErrorBoundary,
}: FallbackProps) {
  const err = error as Error;
  return (
    <div style={{ padding: '50px', textAlign: 'center' }}>
      <h1>예상치 못한 문제가 발생했습니다.</h1>
      <p style={{ color: 'red' }}>{err.message}</p>
      <button
        onClick={resetErrorBoundary}
        style={{ padding: '10px 20px', cursor: 'pointer' }}
      >
        다시 시도하기
      </button>
      <button onClick={() => (window.location.href = '/')}>홈으로 이동</button>
    </div>
  );
}
