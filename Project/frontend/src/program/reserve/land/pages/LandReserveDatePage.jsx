import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import "./../style/LandReserveStyle.css"; // 필요 시 사용자 정의 스타일 추가

const LandReserveDatePage = () => {
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState(null);

  const closedDates = ["2025-08-08", "2025-08-10"]; // 휴무일 서버에서 가져오면 삭제필요

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
          onChange={handleDateSelect}
          value={selectedDate}
          minDate={new Date()} // 오늘부터
          maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
          tileDisabled={({ date }) => isDateClosed(date)}
        tileClassName={({ date }) =>
          isDateClosed(date) ? "closed-date" : null
        }
          tileContent={({ date, view }) =>
            view === "month" && isDateClosed(date) ? (
              <div className="closed-text">
                <div>{date.getDate()}일</div>
                <div>예약마감</div>
              </div>
            ) : null
          }
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
