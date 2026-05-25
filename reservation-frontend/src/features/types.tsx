//타임테이블 슬롯
export type Slot = {
  id: number;
  time: string;
  isReserved: boolean;
};

export interface reservationInfo {
  id: number;
  date: string;
  startTime: string;
  endTime: string;
  resourceId: number;
  username: string; //이거 필요하냐
}

export type CreateReservationRequest = {
  date: string;
  startTime: string;
  endTime: string;
  resourceId: number;
};

export interface ResourceInfo {
  id: number | null;
  name: string;
}

export interface userInfo {
  id: number;
  role: string;
}
