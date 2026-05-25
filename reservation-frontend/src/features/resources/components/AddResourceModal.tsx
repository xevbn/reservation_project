import { useState } from 'react';
import { usePostResource } from '../hook/usePostResource';

interface Props {
  isOpen: boolean;
  onClose: () => void;
}

const css = `
.resource-modal {
  background-color: #ffffff;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  max-width: 360px;
  margin: 0 auto;
  border: 1px solid #f0f0f0;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  align-items: center;
}

.resource-name {
  width: 80%;
  padding: 14px 16px;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  font-size: 16px;
  color: #333;
  outline: none;
  transition: all 0.2s ease;
}

.resource-name:focus {
  border-color: #228be6;
  background-color: #f8fbff;
}

.resource-name::placeholder {
  color: #adb5bd;
}

.add-btn {
  width: 80%;
  padding: 14px;
  background-color: #228be6;
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s;
}

.add-btn:hover {
  background-color: #1c7ed6;
}

.add-btn:active {
  transform: scale(0.98);
}
  
.close-icon {
  background: none;
  border: none;
  font-size: 24px;
  line-height: 1;
  color: #999;
  cursor: pointer;
  position: absolute;
  top: 5px;
  right: 5px;
  padding: 5px;
}`;

export const AddResourceModal = ({ isOpen, onClose }: Props) => {
  const post = usePostResource();
  const [resourceName, setResourceName] = useState<string>('');

  if (!isOpen) return null;

  return (
    <div className="resource-modal">
      <style>{css}</style>
      <button className="close-icon" onClick={onClose}>
        &times;
      </button>
      <input
        className="resource-name"
        value={resourceName}
        onChange={(e) => setResourceName(e.target.value)}
        placeholder="새 리소스 이름"
      />

      <button
        className="add-btn"
        onClick={async () => {
          try {
            await post.mutate(resourceName);
          } catch (err) {
            console.error(err);
            alert('리소스 추가에 실패했습니다. ' + err);
          } finally {
            isOpen = false;
            onClose();
          }
        }}
      >
        리소스 추가
      </button>
    </div>
  );
};
