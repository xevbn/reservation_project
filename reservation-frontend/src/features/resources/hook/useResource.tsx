import { getResourceList } from '../api/getResourceList';
import { useQuery } from '@tanstack/react-query';

export function useResource() {
  const queryKey = ['resource'];

  const {
    data = [],
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: queryKey,
    queryFn: getResourceList,
    staleTime: 1000 * 60 * 60,
  });

  return {
    data,
    loading: isLoading,
    error: isError
      ? '리소스 목록을 불러오지 못했습니다:' + error.message
      : null,
  };
}
