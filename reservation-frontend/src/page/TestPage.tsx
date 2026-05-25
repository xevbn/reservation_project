import { getOwnReservation } from '../features/reservation/api/getOwnReservation';

export default function TestPage() {
  const list = getOwnReservation();
  console.log(list);

  return (
    <div>
      <h1>테스트 페이지</h1>
    </div>
  );
}
