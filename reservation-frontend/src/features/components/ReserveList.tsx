import { Calendar } from './Calendar';
import dayjs from 'dayjs';
import { useState } from 'react';
import { useResource } from '../resources/hook/useResource';
import { useNavigate } from 'react-router-dom';
import { ResourceList } from '../resources/components/resourceList';
import { SlotModal } from './SlotModal';
import { useSlots } from '../reservation/hooks/useSlots';
import { useSlotsSSE } from '../reservation/hooks/useSlotsSSE';
import { useGetReservation } from '../reservation/hooks/useGetReservations';
import type { reservationInfo, ResourceInfo } from '../types';
import { DetailModal } from './DetailModal';
import { useCancelReservation } from '../reservation/hooks/useCancelReservation';
import { useEditReservation } from '../reservation/hooks/useEditReservation';
import { CalendarModal } from './CalendarModal';
import { isBefore, startOfDay } from 'date-fns';

interface Props {
  css: string | null;
}

export const ReserveList = ({ css }: Props) => {
  const [selectedReservation, setSelectedReservation] =
    useState<reservationInfo | null>(null);
  const { data: reservationList } = useGetReservation();
  const [resourceListModalOpen, setResourceListModalOpen] =
    useState<boolean>(false);
  const [slotModalOpen, setSlotModalOpen] = useState<boolean>(false);
  const [detailModalOpen, setDetailModalOpen] = useState<boolean>(false);
  const [calendarModalOpen, setCalendarModalOpen] = useState<boolean>(false);
  const { data: resources, loading } = useResource();
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState<string>('');
  const [resourceId, setResourceId] = useState<number | null>(null);
  const { data: slots, isLoading } = useSlots(
    selectedDate,
    resourceId,
    slotModalOpen,
  );
  const cancel = useCancelReservation(selectedDate, resourceId);
  const edit = useEditReservation(selectedDate, resourceId);
  useSlotsSSE();

  const handleEditStart = () => {
    if (!selectedReservation) return;

    setDetailModalOpen(false);
    setCalendarModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await cancel.mutate(id);
      setDetailModalOpen(false);
    } catch (err) {
      alert('예약 취소 중 오류가 발생했습니다.');
      console.log(err);
    }
  };

  return (
    <div className="reserveListForm">
      <style>{css}</style>
      {reservationList && reservationList.length > 0 && (
        <div className="reserveList">
          {reservationList.map((reservation: reservationInfo) =>
            !isBefore(reservation.date, dayjs().format('YYYY-MM-DD')) ? (
              <button
                key={reservation.id}
                className="reservation"
                disabled={isBefore(reservation.date, startOfDay(new Date()))}
                onClick={() => {
                  setSelectedReservation(reservation);
                  setDetailModalOpen(true);
                }}
              >
                {resources.find(
                  (r: ResourceInfo) => r.id === reservation.resourceId,
                ).name +
                  '\n' +
                  '날짜: ' +
                  dayjs(reservation.date).format('YYYY-MM-DD') +
                  '\n' +
                  '시간: ' +
                  reservation.startTime +
                  '-' +
                  reservation.endTime}
              </button>
            ) : null,
          )}
        </div>
      )}

      <div className="calendar">
        <Calendar onSelect={() => {}} list={reservationList} />
      </div>

      <div className="resourceList">
        <ResourceList
          resources={resources}
          isOpen={resourceListModalOpen}
          onSelect={(id) => {
            setResourceId(id);
            setResourceListModalOpen(false);
            setSlotModalOpen(true);
          }}
          isLoading={loading}
          css={`
            div:has(> ul) {
              position: fixed;
              top: 0;
              left: 0;
              width: 100vw;
              height: 100vh;
              background-color: rgba(0, 0, 0, 0.6);
              display: flex;
              justify-content: center;
              align-items: center;
              z-index: 9999;
              backdrop-filter: blur(3px);
            }

            div:has(> ul) ul {
              background-color: #ffffff;
              width: 90%;
              max-width: 400px;
              max-height: 70vh;
              padding: 24px;
              border-radius: 20px;
              list-style: none;
              overflow-y: auto;
              box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
              animation: slideUp 0.3s ease-out;
            }

            div:has(> ul) li {
              background-color: #f8f9fa;
              border: 1px solid #eee;
              border-radius: 12px;
              padding: 18px;
              transition: all 0.2s;
              display: flex;
              justify-content: space-between;
              align-items: center;
            }

            div:has(> ul) li:hover {
              background-color: #fff;
              border-color: #007bff;
              transform: translateY(-2px);
              box-shadow: 0 5px 15px rgba(0, 123, 255, 0.1);
            }

            div:has(> ul) li h3 {
              margin: 0;
              font-size: 16px;
              color: #333;
            }

            @keyframes slideUp {
              from {
                opacity: 0;
                transform: translateY(30px);
              }
              to {
                opacity: 1;
                transform: translateY(0);
              }
            }

            div:has(> ul) ul::-webkit-scrollbar {
              width: 6px;
            }
            div:has(> ul) ul::-webkit-scrollbar-thumb {
              background: #ddd;
              border-radius: 10px;
            }
          `}
        />
      </div>

      <div className="slotModal">
        <SlotModal
          date={selectedDate}
          slots={slots}
          loading={isLoading}
          isOpen={slotModalOpen}
          onClose={() => setSlotModalOpen(false)}
          onSelectSlot={(slot) => {
            if (
              selectedReservation &&
              window.confirm('예약을 변경하시겠습니까?')
            ) {
              const editInfo = {
                id: -1,
                date: selectedDate,
                startTime: slot.time.split('-')[0],
                endTime: slot.time.split('-')[1],
                resourceId: resourceId,
                username: '',
              } as reservationInfo;
              edit.mutate({
                editData: editInfo,
                reservationId: selectedReservation.id,
              });
            }

            setSlotModalOpen(false);
            setSelectedReservation(null);
          }}
        />
      </div>

      <DetailModal
        isOpen={detailModalOpen}
        onClose={() => {
          setDetailModalOpen(false);
        }}
        onDelete={handleDelete}
        onEdit={handleEditStart}
        reservation={selectedReservation}
      />

      <CalendarModal
        isOpen={calendarModalOpen}
        resourceId={resourceId}
        onSelect={(date: Date) => {
          const dateStr = dayjs(date).format('YYYY-MM-DD');
          setSelectedDate(dateStr);
          setResourceListModalOpen(true);
          setCalendarModalOpen(false);
        }}
        onClose={() => {
          console.log('clicked');
          setCalendarModalOpen(false);
        }}
      />

      <button onClick={() => navigate('/resource')}>예약하러 가기</button>
    </div>
  );
};
