import { apiFetch } from '../../../api/apiFetch';

//로그인 요청
export async function loginApi(username: string, password: string) {
  try {
    const res = await apiFetch('/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });

    return res;
  } catch (e) {
    console.log(e);
  }
}
