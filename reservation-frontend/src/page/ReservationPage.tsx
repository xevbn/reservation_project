import { useState } from 'react';
import { useSlots } from '../features/reservation/hooks/useSlots';
import { useReserve } from '../features/reservation/hooks/useReserve';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useSlotsSSE } from '../features/reservation/hooks/useSlotsSSE';
import { useLogout } from '../features/auth/hooks/useLogout';
import { Calendar } from '../features/components/Calendar';
import dayjs from 'dayjs';
import { SlotModal } from '../features/components/SlotModal';

const css = `.reservation-page {
  max-width: 800px;
  margin: 40px auto;
  padding: 30px;
  background-color: #ffffff;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  text-align: center;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.reservation-page h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 30px;
  text-align: left;
  border-left: 5px solid #0078d4;
  padding-left: 15px;
}

.calendar-div {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.buttons {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s ease;
  margin: 0 8px;
}

.my-reservation {
  background-color: #0078d4;
  color: white;
  border: none;
}

.logout {
  background-color: transparent;
  color: #666;
  border: 1px solid #ccc;
}

.logout:hover {
  background-color: #f5f5f5;
  color: #d83b01;
  border-color: #d83b01;
}

@media (max-width: 600px) {
  .reservation-page {
    margin: 10px;
    padding: 20px;
  }
  
  .reservation-page h2 {
    font-size: 20px;
  }
}`;

export default function ReservationPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const resourceId = searchParams.get('resourceId');

  const [selectedDate, setSelectedDate] = useState<string | null>('');
  const [modalOpen, setModalOpen] = useState(false);

  if (!resourceId) {
    console.log(Error('리소스가 없습니다'));
    navigate('/resourceList');
    throw new Error('resourceId is null');
  }

  const numResourceId = Number.parseInt(resourceId);
  const { data: slots, isLoading } = useSlots(
    selectedDate,
    numResourceId,
    modalOpen,
  );
  useSlotsSSE();
  const createReservation = useReserve();
  const logout = useLogout();

  return (
    <div className="reservation-page">
      <style>{css}</style>
      <h2>예약 페이지</h2>

      <div className="calendar-div">
        <Calendar
          onSelect={(date) => {
            const dateString = dayjs(date).format('YYYY-MM-DD');
            if (!date) return;

            setSelectedDate(dateString);
            setModalOpen(true);
          }}
          list={null}
        />
      </div>

      {modalOpen && selectedDate && (
        <SlotModal
          date={selectedDate}
          slots={slots}
          loading={isLoading}
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          onSelectSlot={(slot) => {
            const isConfirmed = window.confirm('예약하시겠습니까?');

            if (isConfirmed) {
              createReservation.mutate({
                resourceId: numResourceId,
                date: selectedDate,
                startTime: slot.time.split('-')[0],
                endTime: slot.time.split('-')[1],
              });
            }

            setModalOpen(false);
          }}
        />
      )}

      <div className="buttons">
        <button onClick={logout.logout} className="logout">
          로그아웃
        </button>

        <button
          onClick={() => navigate('/reservationList')}
          className="my-reservation"
        >
          내 예약
        </button>
      </div>
    </div>
  );
}
