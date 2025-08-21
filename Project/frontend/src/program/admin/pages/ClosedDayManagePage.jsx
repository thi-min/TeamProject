import React, { useEffect, useState } from "react";
import ClosedDayService from "../services/ClosedDayService";

const ClosedDayManagePage = () => {
  const today = new Date();
  const [year, setYear] = useState(today.getFullYear());
  const [month, setMonth] = useState(today.getMonth() + 1);
  const [closedDays, setClosedDays] = useState([]);

  // 해당 월 휴무일 조회
  const fetchClosedDays = async () => {
    try {
      const { data } = await ClosedDayService.getClosedDays(year, month);
      setClosedDays(data.map((d) => d.closedDate));
    } catch (err) {
      console.error("휴무일 조회 실패", err);
    }
  };

  // 휴무일 토글 (등록/삭제)
  const toggleClosedDay = async (day) => {
    const formattedDate = `${year}-${String(month).padStart(2, "0")}-${String(
      day
    ).padStart(2, "0")}`;

    try {
      if (closedDays.includes(formattedDate)) {
        // 삭제
        await ClosedDayService.deleteClosedDay(formattedDate);
        setClosedDays(closedDays.filter((d) => d !== formattedDate));
      } else {
        // 등록
        await ClosedDayService.setClosedDay(formattedDate);
        setClosedDays([...closedDays, formattedDate]);
      }
    } catch (err) {
      console.error("휴무일 등록/삭제 실패", err);
    }
  };

  useEffect(() => {
    fetchClosedDays();
  }, [year, month]);

  // 단순 달력 렌더링 (테이블 형태)
  const daysInMonth = new Date(year, month, 0).getDate();
  const days = Array.from({ length: daysInMonth }, (_, i) => i + 1);

  return (
    <div style={{ padding: "20px" }}>
      <h2>
        휴무일 관리 - {year}년 {month}월
      </h2>

      <div style={{ marginBottom: "10px" }}>
        <button onClick={() => setMonth((m) => (m > 1 ? m - 1 : 12))}>
          이전 달
        </button>
        <button onClick={() => setMonth((m) => (m < 12 ? m + 1 : 1))}>
          다음 달
        </button>
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(7, 1fr)",
          gap: "5px",
        }}
      >
        {days.map((day) => {
          const dateStr = `${year}-${String(month).padStart(2, "0")}-${String(
            day
          ).padStart(2, "0")}`;
          const isClosed = closedDays.includes(dateStr);
          return (
            <div
              key={day}
              onClick={() => toggleClosedDay(day)}
              style={{
                padding: "10px",
                textAlign: "center",
                border: "1px solid #ccc",
                borderRadius: "6px",
                cursor: "pointer",
                backgroundColor: isClosed ? "#ffcccc" : "#ccffcc",
              }}
            >
              {day}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ClosedDayManagePage;