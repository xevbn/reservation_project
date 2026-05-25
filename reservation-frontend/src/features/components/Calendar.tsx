import { isBefore, isSameMonth, startOfDay } from 'date-fns';
import { useState } from 'react';
import { DayPicker } from 'react-day-picker';
import type { reservationInfo } from '../types';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import 'react-day-picker/dist/style.css';

interface CalendarProps {
  onSelect: (date: Date) => void;
  list: reservationInfo[] | null;
}

const css = `
.grid-calendar.rdp {
  --rdp-cell-size: 45px;
  --rdp-accent-color: #0078d4;
  margin: 0;
}

.grid-calendar .rdp-head_cell {
  border: 1px solid #eee;
  text-transform: none;
  font-size: 13px;
  color: #1a1a1a;
}

.grid-calendar .rdp-day:not(.rdp-day_today) {
  border-radius: 0 !important;
  border: 0.5px solid #eee;
  margin: 0 !important;
  color: black;
}

.grid-calendar .rdp-day_today {
  font-weight: bold;
  color: #0078d4;
  background-color: #f0f7ff;
}

.grid-calendar .rdp-caption_label {
  color: #1a1a1a;
  font-size: 24px;
  font-weight: 700;
}
`;

export const Calendar = ({ onSelect }: CalendarProps) => {
  const [selected] = useState<Date | undefined>();
  const [month, setMonth] = useState(new Date());

  const disableDays = (date: Date) => {
    const today = new Date();
    const isPastDay = isBefore(date, startOfDay(today));
    return isPastDay || !isSameMonth(date, month);
  };

  return (
    <div>
      <style>{css}</style>
      <DayPicker
        mode="single"
        selected={selected}
        onSelect={onSelect}
        required
        month={month}
        onMonthChange={setMonth}
        formatters={{
          formatWeekdayName: (date) => format(date, 'EEE', { locale: ko }),
          formatCaption: (date) => format(date, 'yyyy년 MM월', { locale: ko }),
        }}
        showOutsideDays
        disabled={disableDays}
        className="grid-calendar"
      />
    </div>
  );
};
