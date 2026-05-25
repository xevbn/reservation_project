import { useQuery } from '@tanstack/react-query';
import type { Slot } from '../../types';
import { getSlots } from '../api/getSlots';

export function useSlots(
  date: string | null,
  resourceId: number | null,
  isOpen: boolean,
) {
  return useQuery<Slot[]>({
    queryKey: ['slots', date, resourceId],
    queryFn: () => getSlots(date, resourceId),
    enabled: !!date && !!resourceId && isOpen,
    refetchInterval: false,
  });
}
