import { apiFetch } from '../../../api/apiFetch';

export async function cancelReservationApi(reservationId: number) {
  return apiFetch(`/reservation/${reservationId}/detail`, {
    method: 'DELETE',
    credentials: 'include',
  });
}
