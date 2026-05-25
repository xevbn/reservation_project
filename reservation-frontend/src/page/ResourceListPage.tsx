import { useNavigate } from 'react-router-dom';
import { useResource } from '../features/resources/hook/useResource';
import { ResourceList } from '../features/resources/components/resourceList';

const css = `
div {
  max-width: 500px;
  margin: 0 auto;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

li {
  background-color: #ffffff;
  border: 1px solid #e1e4e8;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

li:hover {
  border-color: #007bff;
  background-color: #f8fbff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.1);
}

li:active {
  transform: translateY(0);
  background-color: #f0f5ff;
}

li h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  letter-spacing: -0.02em;
}

ul:empty::after {
  content: "사용 가능한 리소스가 없습니다.";
  display: block;
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 14px;
}`;

export default function ResourceListPage() {
  const navigate = useNavigate();
  const { data, loading, error } = useResource();

  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>예약 가능한 리소스</h1>
      <ResourceList
        isOpen={true}
        resources={data}
        onSelect={(id) => navigate(`/reservation?resourceId=${id}`)}
        isLoading={loading}
        css={css}
      />

      <button
        onClick={() => {
          navigate('/reservationList');
        }}
      >
        내 예약
      </button>
    </div>
  );
}
