import { apiFetch } from '../../../api/apiFetch';

export async function getResourceList() {
  const res = await apiFetch('/resource/list', {
    method: 'GET',
  });

  return res.resourceList;
}
