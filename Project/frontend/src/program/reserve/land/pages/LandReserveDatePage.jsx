import axios from "axios";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import "./../style/LandReserveStyle.css"; // 필요 시 사용자 정의 스타일 추가
import ClosedDayService from "../../../admin/services/ClosedDayService";

const LandReserveDatePage = () => {
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

  const fetchClosedDays = async () => {
    try {
      const { data } = await axios.get("/api/closed-days", {
        params: { year, month },
      });
      setClosedDates(data.map((d) => d.closedDate));
    } catch (err) {
      console.error("휴무일 조회 실패:", err);
    }
  };

  fetchClosedDays();
}, []);


  // 날짜 선택 핸들러
  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  // yyyy-MM-dd 포맷 함수 (로컬 기준)
  const formatDateKST = (date) => {
    const offset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - offset);
      return localDate.toISOString().split("T")[0];
  };

// 예약 마감 날짜인지 여부 확인
const isDateClosed = (date) => {
  const dateStr = formatDateKST(date);
    return closedDates.includes(dateStr);
};

  // 다음 버튼 클릭 핸들러
  const handleNextClick = () => {
    if (!selectedDate) {
      alert("예약 날짜를 선택해주세요.");
      return;
    }

  const formattedDate = formatDateKST(selectedDate); 
  navigate("/reserve/land/form", {
    state: { selectedDate: formattedDate }, 
  });
  };


  return (
    <div className="land-date-page">
      <h2>놀이터 예약 신청</h2>
      <p>예약하실 날짜를 선택해주세요</p>

      <div className="calendar-wrapper">
        <Calendar
          onChange={setSelectedDate}
          value={selectedDate}
          minDate={new Date()}
          maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
          tileDisabled={({ date }) => isDateClosed(date)}
          tileClassName={({ date }) =>
            isDateClosed(date) ? "closed-date" : null
          }
          // 👉 여기 tileContent 추가
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
              if (isDateClosed(date)) {
                return (
                  <div className="closed-text">
                    <div>{date.getDate()}일</div>
                    <div>예약마감</div>
                  </div>
                );
              }
            }
            return null;
          }}
          // ✅ 달(month) 바뀔 때마다 API 다시 불러오기
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

export default LandReserveDatePage;
