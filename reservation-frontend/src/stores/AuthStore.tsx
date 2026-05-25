import { create } from 'zustand';
import type { userInfo } from '../features/types';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { queryClient } from './queryClient';

export type AuthState = {
  accessToken: string | null;
  setAccessToken: (token: string | null) => void;
  userInfo: userInfo | null;
  setUserInfo: (userInfo: userInfo | null) => void;
  isLogin: boolean;
  setIsLogin: (isLogin: boolean) => void;
  eventSource: EventSource | null;
  setEventSource: (url: string) => void;
  retryCount: number;
  clear: () => void;
};

//액세스 토큰 저장
export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: null,
  userInfo: null,
  isLogin: false,
  eventSource: null,
  retryCount: 0,
  setAccessToken: (token) => set({ accessToken: token }),
  setUserInfo: (info) => set({ userInfo: info }),
  setIsLogin: (isLogin) => set({ isLogin: isLogin }),
  setEventSource: (url) => {
    const currentSource = get().eventSource;
    if (currentSource) currentSource.close();

    const newSource = new EventSourcePolyfill(url, {
      headers: {
        Authorization: `Bearer ${get().accessToken}`,
      },
    });
    set({ eventSource: newSource });

    newSource.onopen = () => {
      set({ retryCount: 0 });
    };

    newSource.onmessage = (event) => {
      console.log('event arrived');
      const { type, date, resourceId } = event.data;

      if (type === 'SLOT_UPDATE') {
        queryClient.refetchQueries({
          queryKey: ['slots', date, resourceId],
          exact: true,
        });
      }
    };

    newSource.onerror = () => {
      newSource.close();
      const { retryCount } = get();

      if (retryCount < 5) {
        const delay = Math.pow(2, retryCount) * 1000;
        console.log(`${delay}ms 후 재연결 시도`);

        setTimeout(() => {
          set({ retryCount: retryCount + 1 });
          get().setEventSource(url);
        }, delay);
      } else {
        console.error('sse 재연결 횟수 초과. 연결 중단');
      }
    };

    return () => {
      console.log('sse closed');
      newSource.close();
    };
  },
  clear: () => {
    get().eventSource?.close();
    set({ accessToken: null, userInfo: null, eventSource: null });
  },
}));
