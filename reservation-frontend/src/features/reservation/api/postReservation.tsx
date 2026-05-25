import { apiFetch } from '../../../api/apiFetch';
import type { CreateReservationRequest } from '../../types';

type CreateReservationResponse = {
  reservationId: number;
  date: string;
  startTime: string;
  endTime: string;
  status: string;
};

export async function createReservation(body: CreateReservationRequest) {
  const res = apiFetch(`/reservation/${body.date}`, {
    method: 'POST',
    body: JSON.stringify(body),
    credentials: 'include',
  }) as Promise<CreateReservationResponse>;

  return res;
}
