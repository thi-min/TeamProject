import api from "../../../../common/api/axios";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import "./../style/VolunteerReserveStyle.css"; // 봉사 예약 스타일

const VolunteerReserveDatePage = () => {
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState(null);
  const [closedDates, setClosedDates] = useState([]); // {date, reason}

  // 휴무일 조회
  const fetchClosedDays = async (year, month) => {
    try {
      const { data } = await api.get("/api/closed-days", {
        params: { year, month },
      });
      setClosedDates(data.map((d) => ({ date: d.closedDate, reason: d.reason })));
    } catch (err) {
      console.error("휴무일 조회 실패:", err);
    }
  };

  useEffect(() => {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;

    fetchClosedDays(year, month);
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
    return !isWeekend(date) || closedDates.some((cd) => cd.date === dateStr);
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
    const closed = closedDates.find((cd) => cd.date === dateStr);

    // 1️⃣ 휴무일 (DB에 등록된 날) → 빨간 글씨
    if (closed) {
      return (
        <div>
          <div>{date.getDate()}일</div>
          <div className="closed-text">{closed.reason || "예약마감"}</div>
        </div>
      );
    }

    // 2️⃣ 평일 (주말이 아님) → 그냥 회색 표시만 (글자는 검정)
    if (!isWeekend(date)) {
      return (
        <div>
          <div>{date.getDate()}일</div>
          <div className="disabled-text">예약불가</div>
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