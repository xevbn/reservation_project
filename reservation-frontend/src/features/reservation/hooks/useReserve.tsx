import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createReservation } from '../api/postReservation';
import type { CreateReservationRequest, Slot } from '../../types';

//예약 생성
export function useReserve() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (reservData: CreateReservationRequest) =>
      createReservation(reservData),
    onSuccess: (data, variables) => {
      const myListKey = ['reservationList'];
      const queryKey = ['slots', variables.date, variables.resourceId];

      qc.setQueryData(queryKey, (old: Slot[] | undefined) => {
        return old?.map((slot: Slot) =>
          slot.time === data.startTime + '-' + data.endTime ? data : old,
        );
      });
      qc.invalidateQueries({ queryKey: myListKey });
    },
    onError: (err) => {
      console.log(err.message);
      alert('예약에 실패했습니다: ' + err.message);
    },
    onSettled: (_data, _error, variables) => {
      //마지막으로 작동해야할 로직이 있는가
      qc.invalidateQueries({
        queryKey: ['slots', variables.date, variables.resourceId],
      });
    },
  });
}
