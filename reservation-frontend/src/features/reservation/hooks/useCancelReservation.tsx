import { useMutation, useQueryClient } from '@tanstack/react-query';
import { cancelReservationApi } from '../api/cancelReservation';
import type { Slot } from '../../types';

//예약 취소
export function useCancelReservation(date: string, resourceId: number | null) {
  const qc = useQueryClient();
  const slotKey = ['slots', date, resourceId];
  const listKey = ['reservationList'];

  return useMutation({
    mutationFn: (reservationId: number) => cancelReservationApi(reservationId),
    onMutate: (variables) => {
      qc.cancelQueries({ queryKey: slotKey });
      qc.cancelQueries({ queryKey: listKey });

      const previousList = qc.getQueryData(slotKey);

      qc.setQueryData(slotKey, (old: Slot[] | undefined) => {
        return old?.filter((res) => res.id !== variables);
      });

      return { previousList };
    },
    onSuccess: () => {
      //variables는 reservationId 하나뿐임
      qc.invalidateQueries({ queryKey: listKey });
    },
    onError: (err, _, context) => {
      console.log(err);
      if (context?.previousList) {
        qc.setQueryData(slotKey, context.previousList);
        alert('삭제에 실패했습니다. 다시 시도해주세요');
      }
    },
    onSettled: () => {
      qc.invalidateQueries({ queryKey: slotKey });
      qc.invalidateQueries({ queryKey: listKey });
    },
  });
}
