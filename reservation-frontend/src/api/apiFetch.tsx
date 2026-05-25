import { useAuthStore } from '../stores/AuthStore';

//기본 베이스 url
export const API_URL = 'api';

//토큰 만료 시 재발급 요청
export async function refreshToken() {
  const res = await fetch(`${API_URL}/auth/refresh`, {
    method: 'POST',
    credentials: 'include',
  });

  return res;
}

//기본 fetch용 함수
export async function apiFetch(input: string, init?: RequestInit) {
  const authStore = useAuthStore.getState();
  const token = authStore.accessToken;

  const headers = {
    ...(init?.headers || {}),
    ...(token && { Authorization: `Bearer ${token}` }),
    'Content-Type': 'application/json',
  };

  let res = await fetch(`${API_URL}${input}`, {
    ...init,
    headers,
    credentials: 'include',
  });

  //401 unauthorized 오류 시
  if (res.status === 401) {
    try {
      const refreshResult = await refreshToken();
      if (!refreshResult.ok) throw new Error('refresh token expired');
      const json = await refreshResult.json();
      const newToken = json.Authorization;

      authStore.setAccessToken(newToken);

      const retryHeaders = {
        ...headers,
        Authorization: `Bearer ${newToken}`,
      };

      //재시도
      res = await fetch(`${API_URL}${input}`, {
        ...init,
        headers: retryHeaders,
        credentials: 'include',
      });
    } catch {
      authStore.clear();
      throw new Error('Unauthorized');
    }
  } else if (res.status === 409) {
    return res.json();
  }

  if (!res.ok) throw new Error(await res.text());

  if (res.status === 204) {
    return 'edit / delete success';
  }
  return res.json();
}
