import { apiFetch } from '../../../api/apiFetch';

export async function getOwnReservation() {
  const res = await apiFetch(`/reservation/detail`, {
    method: 'GET',
    credentials: 'include',
  });

  return res;
}
