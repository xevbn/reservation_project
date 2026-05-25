import { apiFetch } from '../../../api/apiFetch';

export const addResource = (name: string) => {
  const res = apiFetch('/resource/add', {
    method: 'POST',
    credentials: 'include',
    body: name,
  });

  return res;
};
