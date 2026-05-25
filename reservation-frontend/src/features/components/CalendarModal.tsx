import { Calendar } from './Calendar';

interface CalendarModalProps {
  isOpen: boolean;
  resourceId: number | null;
  onSelect: (date: Date) => void;
  onClose: () => void;
}

const css = `
.modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 450px;
  height: 400px;
  z-index: 2000;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.modal-backdrop {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(2px);
}

.close {
  position: absolute;
  top: 5px;
  right: 5px;
  background: none;
  border: none;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  z-index: 9999;
}

.close:hover {
  color: #d83b01;
}

@keyframes modalPop {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
`;

export const CalendarModal = ({
  isOpen,
  onSelect,
  onClose,
}: CalendarModalProps) => {
  if (!isOpen) return null;

  return (
    <div className="modal">
      <style>{css}</style>
      <div className="modal-backdrop">
        <button className="close" onClick={onClose}>
          닫기
        </button>
        <Calendar onSelect={onSelect} list={null} />
      </div>
    </div>
  );
};
