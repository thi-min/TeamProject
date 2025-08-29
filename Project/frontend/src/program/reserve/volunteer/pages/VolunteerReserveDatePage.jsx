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
  const [volunteerCounts, setVolunteerCounts] = useState({});
  const [loading, setLoading] = useState(true);

  // ✅ yyyy-MM-dd 포맷 (KST)
  const formatDateKST = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  // ✅ 주말 여부 확인 (토:6, 일:0)
  const isWeekend = (date) => {
    const day = date.getDay();
    return day === 0 || day === 6;
  };

  // ✅ 휴무일 조회
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

  // ✅ 예약 현황 조회 (월 단위)
  const fetchMonthlyCounts = async (year, month) => {
    try {
      const { data } = await api.get("/api/volunteer/timeslots/month", {
        params: { year, month},
      });

      setVolunteerCounts(data);
    } catch (err) {
      console.error("예약 현황(월) 불러오기 실패:", err);
    }
  };

  // ✅ 마감 여부 확인
  const isFullyBooked = (date) => {
  const dateStr = formatDateKST(date);
  const counts = volunteerCounts[dateStr];

  console.log("체크하는 날짜:", dateStr, "counts:", counts);

  if (closedDates.some((cd) => cd.date === dateStr)) return true; // 휴무일
  if (!counts) return false; 

  // 예약이 전혀 없는 경우 → 선택 가능
  const totalReserved = counts.reduce((sum, c) => sum + c.reservedCount, 0);
  if (totalReserved === 0) return false;

  // 모든 슬롯이 정원 마감일 때만 true
  return counts.every((c) => c.reservedCount >= c.capacity);
};

  // ✅ 최초 로딩 시 이번 달 데이터 조회
  useEffect(() => {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;

    const loadInit = async () => {
      setLoading(true);
      await fetchClosedDays(year, month);
      await fetchMonthlyCounts(year, month);
      setLoading(false);
    };

    loadInit();
  }, []);

  // ✅ 다음 버튼 클릭
  const handleNextClick = () => {
    if (!selectedDate) {
      alert("예약 날짜를 선택해주세요.");
      return;
    }
    const formattedDate = formatDateKST(selectedDate);
    navigate("/reserve/volunteer/form", { state: { selectedDate: formattedDate } });
  };

  return (
    <div className="volunteer-date-page">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon vol"></div>
          <div className="form_title">봉사 예약 신청</div>
        </div>
      </div>
      <h3>예약하실 날짜를 선택해주세요 (※ 주말만 가능)</h3>

      <div className="calendar-wrapper">
        {loading ? (
          <p>달력 데이터를 불러오는 중...</p>
        ) : (
          <Calendar
            onChange={setSelectedDate}
            value={selectedDate}
            minDate={new Date()}
            maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
 
            tileDisabled={({ date, view }) => {
              if (view !== "month") return false;  
              const dateStr = formatDateKST(date);
              return (
                !isWeekend(date) || // 평일 불가
                closedDates.some((cd) => cd.date === dateStr) || // 휴무일 불가
                isFullyBooked(date) // 정원 초과 불가
              );
            }}
            tileClassName={({ date, view }) => {
              if (view !== "month") return null;   
              const dateStr = formatDateKST(date);
              if (closedDates.some((cd) => cd.date === dateStr)) return "closed-date";
              if (!isWeekend(date)) return "weekday-disabled";
              if (isFullyBooked(date)) return "full-booked";
              return null;
            }}
            tileContent={({ date, view }) => {
              if (view === "month") {
                const dateStr = formatDateKST(date);
                const closed = closedDates.find((cd) => cd.date === dateStr);

                if (closed) {
                  return (
                    <div>
                      <div>{date.getDate()}일</div>
                      <div className="closed-text">{closed.reason || "휴무일"}</div>
                    </div>
                  );
                }

                if (!isWeekend(date)) {
                  return (
                    <div>
                      <div>{date.getDate()}일</div>
                      <div className="disabled-text">예약불가</div>
                    </div>
                  );
                }

                if (isFullyBooked(date)) {
                  return (
                    <div>
                      <div>{date.getDate()}일</div>
                      <div className="full-text">예약마감</div>
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
              fetchMonthlyCounts(year, month);
            }}
          />
        )}
      </div>

      <div className="my-btn-wrap">
        <div className="form_center_box">
          <div className="temp_btn md">
            <button className="btn" onClick={handleNextClick}>
              다음
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VolunteerReserveDatePage;