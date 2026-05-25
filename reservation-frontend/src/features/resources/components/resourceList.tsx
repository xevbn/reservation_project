import type { ResourceInfo } from '../../types';

type Props = {
  resources: ResourceInfo[];
  onSelect: (id: number) => void;
  isOpen: boolean;
  isLoading: boolean;
  css: string | null;
};

export const ResourceList = ({
  resources,
  onSelect,
  isOpen,
  isLoading,
  css,
}: Props) => {
  if (!isOpen) return null;
  if (isLoading) return <p>로딩 중...</p>;

  return (
    <div>
      <style>{css}</style>
      <ul>
        {resources.map((r) => (
          <li
            key={r?.id}
            onClick={() => {
              if (r.id === null) {
                alert('리소스 아이디가 없습니다.');
              } else onSelect(r.id);
            }}
            style={{ cursor: 'pointer', marginBottom: 12 }}
          >
            <h3>{r.name}</h3>
          </li>
        ))}
      </ul>
    </div>
  );
};
