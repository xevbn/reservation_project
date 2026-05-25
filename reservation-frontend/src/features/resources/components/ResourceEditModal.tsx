import { useState } from 'react';
import { useEditResource } from '../hook/useEditResource';

interface Props {
  id: number;
  isOpen: boolean;
  onClose: () => void;
}

const css = `
.edit-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1500;
  backdrop-filter: blur(3px); /* 배경 흐림 효과 */
}

.edit-form {
  background-color: #ffffff;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  width: 90%;
  max-width: 380px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  animation: modalSlideUp 0.3s ease-out;
  align-items: center;
}

.edit-name {
  width: 80%;
  padding: 14px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  margin-bottom: 8px;
}

.edit-name:focus {
  border-color: #228be6;
  box-shadow: 0 0 0 3px rgba(34, 139, 230, 0.1);
}

.edit-form button {
  width: 80%;
  padding: 12px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.edit-confirm-btn {
  background-color: #228be6;
  color: white;
}

.edit-confirm-btn:hover {
  background-color: #1c7ed6;
}

.close {
  background-color: #f1f3f5;
  color: #495057;
}

.close:hover {
  background-color: #e9ecef;
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}`;

export const ResourceEditModal = ({ id, isOpen, onClose }: Props) => {
  const edit = useEditResource();
  const [newName, setNewName] = useState<string>('');

  if (!isOpen) return null;

  return (
    <div className="edit-modal">
      <style>{css}</style>
      <div className="edit-form">
        <input
          className="edit-name"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          placeholder="이름 변경"
        />

        <button
          className="edit-confirm-btn"
          type="submit"
          onClick={() => {
            edit.mutate({
              id: id,
              info: { id: -1, name: newName },
            });
            onClose();
          }}
        >
          수정
        </button>

        <button className="close" type="button" onClick={() => onClose()}>
          닫기
        </button>
      </div>
    </div>
  );
};
