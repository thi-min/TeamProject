import axios from "axios";
import React, { useState, useEffect} from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import "./../style/VolunteerReserveStyle.css"; // 봉사 예약 스타일

const VolunteerReserveDatePage = () => {
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState(null);
  const [closedDates, setClosedDates] = useState([]);
  const [holidays, setHolidays] = useState([]);

  const fetchClosedDays = async (year, month) => {
    try {
      const { data } = await axios.get("/api/closed-days", {
        params: { year, month },
      });
      setClosedDates(data.map((d) => d.closedDate));
      setHolidays(
        data
          .filter((d) => d.holidayName)
          .map((d) => ({ date: d.closedDate, name: d.holidayName }))
      );
    } catch (err) {
      console.error("휴무일 조회 실패:", err);
    }
  };

  useEffect(() => {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;

    fetchClosedDays(year, month)
  }, []);

    // 날짜 선택 핸들러
  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  // 주말 여부 확인 (토: 6, 일: 0)
  const isWeekend = (date) => {
    const day = date.getDay();
    return day === 0 || day === 6;
  };

  // yyyy-MM-dd 포맷 함수 (KST 기준)
  const formatDateKST = (date) => {
    const offset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - offset);
    return localDate.toISOString().split("T")[0];
  };

  // 휴무일 또는 평일 제외
  const isDateDisabled = (date) => {
    const dateStr = formatDateKST(date);
    return !isWeekend(date) || closedDates.includes(dateStr);
  };

  // 다음 버튼 클릭 시
  const handleNextClick = () => {
    if (!selectedDate) {
      alert("예약 날짜를 선택해주세요.");
      return;
    }

    const formattedDate = formatDateKST(selectedDate);
    navigate("/reserve/volunteer/form", {
      state: { selectedDate: formattedDate },
    });
  };

  return (
    <div className="volunteer-date-page">
      <h2>봉사활동 예약 신청</h2>
      <p>예약하실 날짜를 선택해주세요 (※ 주말만 가능)</p>

      <div className="calendar-wrapper">
        <Calendar
          onChange={handleDateSelect}
          value={selectedDate}
          minDate={new Date()}
          maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
          tileDisabled={({ date }) => isDateDisabled(date)}
          tileClassName={({ date }) =>
            isDateDisabled(date) ? "closed-date" : null
          }
          tileContent={({ date, view }) => {
            if (view === "month") {
              const dateStr = formatDateKST(date);
              const holiday = holidays.find((h) => h.date === dateStr);

              if (holiday) {
                return (
                  <div className="holiday-text">
                    <div>{date.getDate()}일</div>
                    <div>{holiday.name}</div>
                  </div>
                );
              }

              if (isDateDisabled(date)) {
                return (
                  <div className="closed-text">
                    <div>{date.getDate()}일</div>
                    <div>{!isWeekend(date) ? "예약불가" : "예약마감"}</div>
                  </div>
                );
              }
            }
            return null;
          }}

          onActiveStartDateChange={({ activeStartDate }) => {
            const year = activeStartDate.getFullYear();
            const month = activeStartDate.getMonth() + 1;
            fetchClosedDays(year, month);
          }}
        />
      </div>

      <div className="form-action-buttons">
        <button className="next-button" onClick={handleNextClick}>
          다음
        </button>
      </div>
    </div>
  );
};

export default VolunteerReserveDatePage;