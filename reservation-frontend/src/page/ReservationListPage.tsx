import { ReserveList } from '../features/components/ReserveList';

const css = `
.reserveListForm {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Pretendard', sans-serif;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.reserveList {
  display: flex;
  gap: 15px;
  overflow-x: auto;
  padding: 10px 5px;
  scrollbar-width: thin;
}

.reservation {
  flex: 0 0 auto;
  width: 200px;
  padding: 16px;
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  text-align: left;
  white-space: pre-line;
  line-height: 1.6;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}

.reservation:hover:not(:disabled) {
  border-color: #4A90E2;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15);
}

.reservation:disabled {
  background-color: #f5f5f5;
  color: #bbb;
  cursor: not-allowed;
  border-color: #eee;
}

.calendar {
  padding: 20px;
  border-radius: 16px;
  border: 1px solid #eee;
  background-color: #ffffff;
}

.reserveListForm > button:last-child {
  width: 100%;
  max-width: 400px;
  margin: 20px auto;
  padding: 16px;
  background-color: #000;
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.3s;
}

.reserveListForm > button:last-child:hover {
  background-color: #333;
}

.slotModal, .resourceList {
  position: relative;
  z-index: 1000;
  align-items: center;
}

@media (max-width: 768px) {
  .reserveListForm {
    padding: 15px;
  }
  
  .reservation {
    width: 180px;
  }
}
`;

export const ReservationListPage = () => {
  return <ReserveList css={css} />;
};
