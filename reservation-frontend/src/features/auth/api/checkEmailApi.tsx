import { apiFetch } from '../../../api/apiFetch';

export type checkEmailResponse = {
  message: string;
  available: boolean;
};

export async function checkEmailApi(email: string): Promise<boolean> {
  const res = await apiFetch('/check_email', {
    method: 'POST',
    body: JSON.stringify({ email }),
  });

  console.log(res);

  return res.available;
}
