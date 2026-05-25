import type { Slot } from '../types';

interface SlotModalProps {
  date: string;
  slots?: Slot[] | undefined;
  loading: boolean;
  isOpen: boolean;
  onClose: () => void;
  onSelectSlot: (slot: Slot) => void;
}

export const SlotModal = ({
  date,
  slots,
  loading,
  isOpen,
  onClose,
  onSelectSlot,
}: SlotModalProps) => {
  const css = `
        .modal-backdrop {
            position: fixed;
            top: 0;
            left: 0;
            width: 100vw;
            height: 100vh;
            background-color: rgba(0, 0, 0, 0.5); /* 어두운 배경 */
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000; /* 최상단에 위치 */
        }
        .modal {
            background-color: white;
            padding: 2rem;
            border-radius: 12px;
            width: 90%;
            max-width: 400px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            animation: slideUp 0.3s ease-out;
        }

        @keyframes slideUp {
            from { transform: translateY(20px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        .slot-list {
            display: flex;
            flex-direction: column;
            align-items: center;
            text-align: center;
            gap: 10px;
            margin: 1.5rem 0;
        }

        .slot-time {
            width: 40%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 6px;
            cursor: pointer;
        }

        .slot-time:disabled {
            background-color: #f0f0f0;
            color: grey;
            cursor: not-allowed;
        }
        
        .date {
            color: black;
        }
    `;

  if (!isOpen) return null;

  return (
    <div>
      <style>{css}</style>
      <div className="modal-backdrop">
        <div className="modal">
          <h2 className="date">{date} 예약 가능한 시간</h2>

          {loading && <p>로딩 중...</p>}

          {!loading && slots && slots.length > 0 && (
            <div className="slot-list">
              {slots?.map((slot) => (
                <button
                  key={`${slot.id}-${slot.isReserved}`}
                  onClick={() => onSelectSlot(slot)}
                  className="slot-time"
                  disabled={slot.isReserved}
                >
                  {slot.time}
                </button>
              ))}
            </div>
          )}

          <button className="close" onClick={onClose}>
            닫기
          </button>
        </div>
      </div>
    </div>
  );
};
