import { useQuery } from '@tanstack/react-query';
import { getOwnReservation } from '../api/getOwnReservation';
import { useNavigate } from 'react-router-dom';

export const useGetReservation = () => {
  const navigate = useNavigate();

  return useQuery({
    queryKey: ['reservationList'],
    queryFn: async () => await getOwnReservation(),
    throwOnError: (error) => {
      if (error.message === 'UNAUTHORIZED') navigate('/login');

      console.log('내 예약 리스트를 불러오는 중 오류 발생 : ', error.message);
      throw Error(error.message);
    },
  });
};
