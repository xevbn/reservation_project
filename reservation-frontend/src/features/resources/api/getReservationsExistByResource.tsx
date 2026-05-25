import { apiFetch } from '../../../api/apiFetch';

export const getReservationsExistByResource = async (id: number) => {
  const res = apiFetch(`/resource/${id}/reservations`, {
    method: 'GET',
    credentials: 'include',
  });

  return res;
};
