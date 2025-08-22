import React, { useEffect, useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import ClosedDayService from "../services/ClosedDayService";
import "../style/ClosedDayManage.css";

const ClosedDayManagePage = () => {
  const today = new Date();
  const [year, setYear] = useState(today.getFullYear());
  const [month, setMonth] = useState(today.getMonth() + 1);
  const [closedDays, setClosedDays] = useState([]); // {date, reason}
  const [holidays, setHolidays] = useState([]);     // {date, name}
  const [selectedDate, setSelectedDate] = useState(null);
  const [reason, setReason] = useState("");

  // 날짜 포맷 yyyy-MM-dd
  const formatDateKST = (date) => {
    const offset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - offset);
    return localDate.toISOString().split("T")[0];
  };

  // ✅ 직접 등록 휴무일 조회
  const fetchClosedDays = async (y, m) => {
    try {
      const { data } = await ClosedDayService.getClosedDays(y, m);
      setClosedDays(data.map((d) => ({ date: d.closedDate, reason: d.reason })));
    } catch (err) {
      console.error("휴무일 조회 실패", err);
    }
  };

  // ✅ 공휴일 조회
  const fetchHolidays = async (y) => {
    try {
      const { data } = await ClosedDayService.getHolidaysByYear(y);
      setHolidays(data.map((d) => ({ date: d.date, name: d.name })));
    } catch (err) {
      console.error("공휴일 조회 실패", err);
    }
  };

  useEffect(() => {
    fetchClosedDays(year, month);
    fetchHolidays(year);
  }, [year, month]);

  // ✅ 휴무일 토글 (등록/삭제)
   const toggleClosedDay = async (dateStr, reasonInput) => {
    try {
      const exists = closedDays.some(cd => cd.date === dateStr);

      if (exists) {
        await ClosedDayService.deleteClosedDay(dateStr);
        setClosedDays(prev => prev.filter(cd => cd.date !== dateStr));
        if (formatDateKST(selectedDate ?? new Date()) === dateStr) setReason("");
         alert("휴무일이 해제되었습니다.");
        return;
      }

      // 새 등록
      const trimmed = (reasonInput ?? "").trim();
      if (!trimmed) {
        alert("휴무일 사유를 입력하세요.");
        return;
      }

      await ClosedDayService.setClosedDay({
        closedDate: dateStr,
        reason: trimmed,
        isClosed: true,
      });
      alert("휴무일이 등록되었습니다.");

      setClosedDays(prev => [...prev, { date: dateStr, reason: trimmed }]);
      setReason(""); // 입력 박스 비우기
    } catch (e) {
      console.error("휴무일 등록/삭제 실패", e);
      alert("작업에 실패했습니다.");
    }
  };

  return (
    <div style={{ display: "flex", padding: "20px", gap: "20px" }}>
      {/* 달력 */}
      <div style={{ flex: 2, maxWidth: "900px" }}>
        <h2>
          휴무일 관리 - {year}년 {month}월
        </h2>
        <Calendar
          value={selectedDate}
          onClickDay={setSelectedDate}
          onActiveStartDateChange={({ activeStartDate }) => {
            const y = activeStartDate.getFullYear();
            const m = activeStartDate.getMonth() + 1;
            setYear(y);
            setMonth(m);
            fetchClosedDays(y, m);
          }}
          tileContent={({ date, view }) => {
            if (view === "month") {
              const dateStr = formatDateKST(date);

              // 1️⃣ 공휴일
              const holiday = holidays.find((h) => h.date === dateStr);
              if (holiday) {
                return (
                  <div style={{ color: "orange", fontSize: "0.7rem" }}>
                    {holiday.name}
                  </div>
                );
              }

              // 2️⃣ 직접 등록 휴무일
              const closed = closedDays.find((cd) => cd.date === dateStr);
              if (closed) {
                return (
                  <div style={{ color: "red", fontSize: "0.7rem" }}>
                    {closed.reason || "예약마감"}
                  </div>
                );
              }
            }
            return null;
          }}
          tileClassName={({ date, view }) => {
            if (view !== "month") return null;
            const dateStr = formatDateKST(date);
            const isHoliday = holidays.some(h => h.date === dateStr);
            const isClosed = closedDays.some(cd => cd.date === dateStr);
            return (isHoliday || isClosed) ? "disabled-day" : null;
          }}
        />
      </div>

      {/* 우측 패널 */}
      <div
        style={{
          flex: 1,
          minWidth: "250px",
          border: "1px solid #ccc",
          borderRadius: "8px",
          padding: "15px",
        }}
      >
        <h3>선택된 날짜</h3>
        {selectedDate ? (
          <>
            <p>{formatDateKST(selectedDate)}</p>
            {closedDays.some((cd) => cd.date === formatDateKST(selectedDate)) ? (
              <>
                <p style={{ color: "red" }}>현재 휴무일</p>
                <button onClick={() => toggleClosedDay(formatDateKST(selectedDate))}>
                  휴무일 해제
                </button>
              </>
            ) : (
              <>
                <p style={{ color: "green" }}>예약 가능일</p>
                <input
                  type="text"
                  placeholder="휴무일 사유 입력"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  style={{ width: "100%", marginBottom: 8 }}
                /> <br />
                <button onClick={() => toggleClosedDay(formatDateKST(selectedDate), reason)}>
                  휴무일 등록
                </button>
              </>
            )}
          </>
        ) : (
          <p>날짜를 선택하세요</p>
        )}
      </div>
    </div>
  );
};

export default ClosedDayManagePage;