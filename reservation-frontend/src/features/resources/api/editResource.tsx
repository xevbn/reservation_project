import { apiFetch } from '../../../api/apiFetch';
import type { ResourceInfo } from '../../types';

export const editResource = (id: number, info: ResourceInfo) => {
  return apiFetch(`/resource/${id}/edit`, {
    method: 'PUT',
    credentials: 'include',
    body: JSON.stringify(info),
  });
};
