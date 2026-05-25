import type { ResourceInfo } from '../../types';
import { addResource } from '../api/addResource';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export const usePostResource = () => {
  const qc = useQueryClient();
  const qk = ['resource'];

  return useMutation({
    mutationFn: (newName: string) => addResource(newName),
    onMutate: (variables) => {
      qc.cancelQueries({ queryKey: qk });
      const prev = qc.getQueryData(qk);

      qc.setQueryData(qk, (old: ResourceInfo[]) => {
        const newResource = { id: -1, name: variables } as ResourceInfo;
        return old ? [...old, newResource] : [newResource];
      });
      qc.invalidateQueries({ queryKey: qk });

      return { prev };
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: qk });
    },
    onError: (err, _, context) => {
      console.error(err);
      alert('리소스 업로드 중 오류 발생: ' + err.message);

      qc.setQueryData(qk, context?.prev);
    },
    onSettled: () => {
      qc.invalidateQueries({ queryKey: qk });
    },
  });
};
