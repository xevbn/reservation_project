import { apiFetch } from '../../../api/apiFetch';

export async function logoutApi() {
  try {
    const res = apiFetch('/logout', {
      credentials: 'include',
      keepalive: true,
    });

    return res;
  } catch (err) {
    console.log('로그아웃 요청 실패', err);
    return err;
  }
}
