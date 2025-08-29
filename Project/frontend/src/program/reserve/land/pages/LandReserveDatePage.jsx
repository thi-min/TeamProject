import api from "../../../../common/api/axios";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import "./../style/LandReserveStyle.css";

const LandReserveDatePage = () => {
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState(null);
  const [closedDays, setClosedDays] = useState([]); // 휴무일 API
  const [landCounts, setLandCounts] = useState({}); // 월별 예약 현황 데이터
  const [loading, setLoading] = useState(true);

  // ✅ yyyy-MM-dd 포맷
  const formatDateKST = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

  // 휴무일 조회 API
  const fetchClosedDays = async (year, month) => {
    try {
      const { data } = await api.get("/api/closed-days", {
        params: { year, month },
      });
      setClosedDays(data.map((d) => ({ date: d.closedDate, reason: d.reason })));
    } catch (err) {
      console.error("휴무일 조회 실패:", err);
    }
  };

  // 놀이터 예약 현황 (월 단위)
  const fetchLandMonthSlots = async (year, month) => {
    try {
      const { data } = await api.get("/api/land/timeslots/month", {
        params: { year, month },
      });

      setLandCounts(data); // { "2025-09-06": [ {landType, reservedCount, capacity, ...}, ... ] }
    } catch (err) {
      console.error("월별 슬롯 조회 실패:", err);
    }
  };

  // 페이지 진입 시 이번 달 데이터 로드
  useEffect(() => {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;

    const loadInit = async () => {
      setLoading(true);
      await fetchClosedDays(year, month);
      await fetchLandMonthSlots(year, month);
      setLoading(false);
    };

    loadInit();
  }, []);

  // 날짜 선택 핸들러
  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  // 해당 날짜가 마감(예약불가)인지 판단
  const isDateClosed = (date) => {
  const dateStr = formatDateKST(date);

  // 1) 휴무일 우선
  if (closedDays.some((cd) => cd.date === dateStr)) {
    return true;
  }

  // 2) 예약 현황 확인
  const slots = landCounts[dateStr];
  if (!slots) return false;

  // ✅ localStorage 규칙 불러오기
  const rules = JSON.parse(localStorage.getItem("landRules") || "{}");
  const allowedSmall = rules.SMALL || [];
  const allowedLarge = rules.LARGE || [];

  // 소형견 규칙 슬롯만 체크
  const smallSlots = slots.filter(
    (s) => s.landType === "SMALL" && allowedSmall.includes(s.timeSlotId)
  );
  const smallClosed =
    smallSlots.length > 0 &&
    smallSlots.every((s) => s.reservedCount >= s.capacity);

  // 대형견 규칙 슬롯만 체크
  const largeSlots = slots.filter(
    (s) => s.landType === "LARGE" && allowedLarge.includes(s.timeSlotId)
  );
  const largeClosed =
    largeSlots.length > 0 &&
    largeSlots.every((s) => s.reservedCount >= s.capacity);

  // 소형, 대형 둘 다 규칙 슬롯이 다 찼을 때만 달력에서 마감 처리
  return smallClosed && largeClosed;
};

  // 다음 버튼 클릭 시 이동
  const handleNextClick = () => {
    if (!selectedDate) {
      alert("예약 날짜를 선택해주세요.");
      return;
    }
    const formattedDate = formatDateKST(selectedDate);
    navigate("/reserve/land/form", { state: { selectedDate: formattedDate } });
  };

  return (
    <div className="land-date-page">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon land"></div>
          <div className="form_title">놀이터 예약 신청</div>
        </div>
      </div>
      <h3>예약하실 날짜를 선택해주세요</h3>

      <div className="calendar-wrapper">
        <Calendar
          onChange={handleDateSelect}
          value={selectedDate}
          minDate={new Date()}
          maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
          // 예약불가(휴무일 + 완전 마감) 처리
          tileDisabled={({ date, view }) => {
            if (view !== "month") return false;
            const dateStr = formatDateKST(date);

            return (
              closedDays.some((cd) => cd.date === dateStr) || // 휴무일
              isDateClosed(date) // 정원 마감
            );
          }}
          tileClassName={({ date, view }) => {
            if (view !== "month") return null;
            const dateStr = formatDateKST(date);

            if (closedDays.some((cd) => cd.date === dateStr)) return "closed-date";
            if (isDateClosed(date)) return "full-booked";
            return null;
          }}
          tileContent={({ date, view }) => {
            if (view === "month") {
              const dateStr = formatDateKST(date);
              const closed = closedDays.find((cd) => cd.date === dateStr);

              if (closed) {
                return (
                  <div>
                    <div>{date.getDate()}일</div>
                    <div className="closed-text">{closed.reason || "휴무일"}</div>
                  </div>
                );
              }

              if (isDateClosed(date)) {
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
          // ✅ 달(month) 바뀔 때마다 API 다시 불러오기
          onActiveStartDateChange={({ activeStartDate }) => {
            const year = activeStartDate.getFullYear();
            const month = activeStartDate.getMonth() + 1;
            fetchClosedDays(year, month);
            fetchLandMonthSlots(year, month);
          }}
        />
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

export default LandReserveDatePage;