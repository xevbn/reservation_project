import { apiFetch } from '../../../api/apiFetch';
import type { reservationInfo } from '../../types';

export async function editReservation(
  data: reservationInfo,
  reservationId: number,
) {
  const reservationReq = {
    id: data.id,
    date: data.date,
    startTime: data.startTime,
    endTime: data.endTime,
    resourceId: data.resourceId,
  };

  const res = await apiFetch(`/reservation/${reservationId}/detail`, {
    method: 'PUT',
    credentials: 'include',
    body: JSON.stringify(reservationReq),
  });

  return res;
}
