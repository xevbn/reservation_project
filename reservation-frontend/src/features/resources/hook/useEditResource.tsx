import type { ResourceInfo } from '../../types';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { editResource } from '../api/editResource';

interface Props {
  id: number;
  info: ResourceInfo;
}

export const useEditResource = () => {
  const qc = useQueryClient();
  const qk = ['resource'];

  return useMutation({
    mutationFn: ({ id, info }: Props) => editResource(id, info),
    onMutate: (variables) => {
      qc.cancelQueries({ queryKey: qk });
      const prev = qc.getQueryData(qk);

      qc.setQueryData(qk, (old: ResourceInfo[]) => {
        old?.map((resource) =>
          resource.id === variables.id
            ? { ...resource, ...variables }
            : resource,
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
      alert('리소스 수정 중 오류 발생: ' + err.message);
    },
    onSettled: () => {
      qc.invalidateQueries({ queryKey: qk });
    },
  });
};
