import dayjs from 'dayjs';
import type { reservationInfo, ResourceInfo } from '../types';
import { useQueryClient } from '@tanstack/react-query';

interface DetailModalProps {
  isOpen: boolean;
  reservation: reservationInfo | null;
  onClose: () => void;
  onEdit: () => void;
  onDelete: (id: number) => void;
}

const css = `
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.detail-modal {
  background-color: #fff;
  width: 90%;
  max-width: 450px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.modal-header {
  padding: 12px 24px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 50px;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  color: #111;
}

.close-icon {
  background: none;
  border: none;
  font-size: 24px;
  line-height: 1;
  color: #999;
  cursor: pointer;
}

.modal-content {
  padding: 20px 24px;
}

.info-group {
  margin-bottom: 20px;
}

.info-group label {
  display: block;
  font-size: 13px;
  color: #888;
  margin-bottom: 6px;
}

.info-group p {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.time-highlight {
  display: inline-block;
  margin-top: 8px !important;
  padding: 4px 10px;
  background-color: #f0f7ff;
  color: #007bff !important;
  border-radius: 6px;
  font-weight: 700 !important;
}

.modal-footer {
  padding: 16px 24px;
  background-color: #f9f9f9;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.modal-footer button {
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-edit {
  background-color: #007bff;
  color: white;
}

.btn-edit:hover { background-color: #0056b3; }

.btn-delete {
  background-color: #fff;
  color: #dc3545;
  border: 1px solid #dc3545 !important;
}

.btn-delete:hover {
  background-color: #fff5f5;
}
`;

export const DetailModal = ({
  isOpen,
  reservation,
  onClose,
  onEdit,
  onDelete,
}: DetailModalProps) => {
  const qc = useQueryClient();
  if (!isOpen || !reservation) return null;
  const resourceList = qc.getQueryData(['resource']) as ResourceInfo[];

  return (
    <div className="modal">
      <style>{css}</style>
      <div className="modal-backdrop">
        <div className="modal detail-modal">
          <div className="modal-header">
            <h3>예약 상세 내역</h3>
            <button className="close-icon" onClick={onClose}>
              &times;
            </button>
          </div>

          <div className="modal-content">
            <div className="info-group">
              <label>예약 리소스</label>
              <p>{`${resourceList.find((r) => r.id === reservation.resourceId)?.name}`}</p>
            </div>

            <div className="info-group">
              <label>예약 일시</label>
              <p>{dayjs(reservation.date).format('YYYY년 MM월 DD일')}</p>
              <p className="time-highlight">
                {reservation.startTime + '-' + reservation.endTime}
              </p>
            </div>
          </div>

          <div className="modal-footer">
            <button className="btn-edit" onClick={onEdit}>
              시간 변경
            </button>

            <button
              className="btn-delete"
              onClick={() => {
                if (window.confirm('정말 예약을 취소하시겠습니까?')) {
                  onDelete(reservation.id);
                }
              }}
            >
              예약 취소
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
