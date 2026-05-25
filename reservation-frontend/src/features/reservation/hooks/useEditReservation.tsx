import { useMutation, useQueryClient } from '@tanstack/react-query';
import { editReservation } from '../api/editReservation';
import type { reservationInfo, Slot } from '../../types';

interface editProps {
  editData: reservationInfo;
  reservationId: number;
}

export function useEditReservation(date: string, resourceId: number | null) {
  const qc = useQueryClient();
  const listKey = ['reservationList'];
  const slotKey = ['slots', date, resourceId];

  return useMutation({
    //해당 mutation의 기본 fetch 함수(일반적으로는?)
    mutationFn: ({ editData, reservationId }: editProps) => {
      console.log(reservationId);
      return editReservation(editData, reservationId);
    },
    //낙관적 업데이트를 위해 사용
    onMutate: async (updateData) => {
      qc.cancelQueries({ queryKey: slotKey });
      qc.cancelQueries({ queryKey: slotKey });

      const prev = qc.getQueryData(slotKey);

      qc.setQueryData(slotKey, (old: Slot[] | undefined) => {
        old?.map((slot) =>
          slot.id === updateData.reservationId
            ? { ...slot, ...updateData }
            : slot,
        );
      });

      return { prev };
    },
    //작동 중 성공 시 발동
    onSuccess: () => {
      //slots에 대한 query는 sse를 통해 지속적인 동기화하므로 해당 mutation에서는 사용 x
      const queryKey = ['reservationList'];

      qc.cancelQueries({ queryKey });
      qc.invalidateQueries({ queryKey });

      alert('예약 변경에 성공했습니다.');
    },
    //작동 중 오류 발생 시 발동
    onError: (err, _, context) => {
      console.log(err);
      qc.setQueryData(slotKey, context?.prev);
    },
    //실패든 성공이든 마지막에 작동 finally와 유사
    onSettled: () => {
      qc.invalidateQueries({ queryKey: listKey });
      qc.invalidateQueries({ queryKey: slotKey });
    },
  });
}
