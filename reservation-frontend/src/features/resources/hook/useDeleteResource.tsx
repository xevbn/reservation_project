import { deleteResource } from '../api/deleteResourceApi';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { ResourceInfo } from '../../types';

export const useDeleteResource = () => {
  const qc = useQueryClient();
  const qk = ['resource'];

  return useMutation({
    mutationFn: (id: number) => deleteResource(id),
    onMutate: (variables) => {
      qc.cancelQueries({ queryKey: qk });
      const prev = qc.getQueryData(qk);

      qc.setQueryData(qk, (old: ResourceInfo[]) => {
        return old?.filter(
          (resource: ResourceInfo) => resource.id !== variables,
        );
      });

      return { prev };
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: qk });
    },
    onError: (err, _, context) => {
      qc.setQueryData(qk, context?.prev);
      console.error(err);
      alert('리소스 삭제 중 오류 발생: ' + err.message);
    },
    onSettled: () => {
      qc.invalidateQueries({ queryKey: qk });
    },
  });
};
