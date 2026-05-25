import { apiFetch } from '../../../api/apiFetch';

export const deleteResource = (id: number) => {
  return apiFetch(`/resource/${id}/delete`, {
    method: 'DELETE',
    credentials: 'include',
  });
};
