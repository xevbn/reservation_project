import { useEffect } from 'react';
import { useAuthStore } from '../../../stores/AuthStore';

//예약 정보를 받아오는 sse
export function useSlotsSSE() {
  const { setEventSource, userInfo } = useAuthStore();

  useEffect(() => {
    setEventSource(`api/reservation/sse/subscribe?userId=${userInfo?.id}`);
  }, []);
}
