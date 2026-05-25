import { apiFetch } from '../../../api/apiFetch';
import type { Slot } from '../../types';

export async function getSlots(date: string | null, resourceId: number | null) {
  if (!date || !resourceId) {
    throw new Error('date or resourceId is null');
  }

  const res = await apiFetch(`/reservation/${date}?resourceId=${resourceId}`);

  const formatted = Object.entries(res).map(([time, isReserved], index) => ({
    id: index,
    time: time,
    isReserved: isReserved,
  })) as Slot[];

  return formatted;
}
