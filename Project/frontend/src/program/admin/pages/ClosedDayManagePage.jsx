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
  const [selectedDate, setSelectedDate] = useState(null);
  const [reason, setReason] = useState("");

  // 날짜 포맷 yyyy-MM-dd
  const formatDateKST = (date) => {
    const offset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - offset);
    return localDate.toISOString().split("T")[0];
  };

  // ✅ 휴무일 조회
  const fetchClosedDays = async (y, m) => {
    try {
      const { data } = await ClosedDayService.getClosedDays(y, m);
      setClosedDays(data.map((d) => ({ date: d.closedDate, reason: d.reason })));
    } catch (err) {
      console.error("휴무일 조회 실패", err);
    }
  };

  useEffect(() => {
    fetchClosedDays(year, month);
  }, [year, month]);

  // ✅ 휴무일 토글 (등록/삭제)
  const toggleClosedDay = async (dateStr, reasonInput) => {
    try {
      const exists = closedDays.some((cd) => cd.date === dateStr);

      if (exists) {
        await ClosedDayService.deleteClosedDay(dateStr);
        setClosedDays((prev) => prev.filter((cd) => cd.date !== dateStr));
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

      setClosedDays((prev) => [...prev, { date: dateStr, reason: trimmed }]);
      setReason(""); // 입력 박스 비우기
    } catch (e) {
      console.error("휴무일 등록/삭제 실패", e);
      alert("작업에 실패했습니다.");
    }
  };

  return (
  <div className="closedday-page">
    <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon clsoedday"></div>
          <div className="form_title">휴무일 관리</div>
        </div>
      </div>
    <h3 className="closedday-title">
      {year}년 {month}월
    </h3>

    <div className="closedday-content">
    {/* 달력 영역 */}
    <div className="card closedday-calendar">

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
            const closed = closedDays.find((cd) => cd.date === dateStr);
            if (closed) return <div className="closed-text">{closed.reason || "예약마감"}</div>;
          }
          return null;
        }}
        tileClassName={({ date, view }) => {
          if (view !== "month") return null;
          const dateStr = formatDateKST(date);
          const isClosed = closedDays.some((cd) => cd.date === dateStr);
          return isClosed ? "disabled-day" : null;
        }}
        tileDisabled={({ date, view }) => {
          if (view === "month") {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            return date < today;
          }
          return false;
        }}
      />
    </div>

    {/* 우측 패널 */}
    <div className="card closedday-sidebar">
      <h3 className="card-subtitle">선택된 날짜</h3>

      {selectedDate ? (
        <>
          <p className="selected-date">{formatDateKST(selectedDate)}</p>

          {closedDays.some((cd) => cd.date === formatDateKST(selectedDate)) ? (
            <>
              {(() => {
                const closed = closedDays.find((cd) => cd.date === formatDateKST(selectedDate));
                return (
                  <>
                    <p className="closed-day-text">현재 휴무일</p>
                    <p className="closed-day-reason">
                      사유: {closed.reason || "사유 없음"}
                    </p>

                    <div className="btn-row">
                      <div className="temp_btn white xsm">
                        <button
                          className="btn"
                          onClick={() => toggleClosedDay(formatDateKST(selectedDate))}
                        >
                          휴무일 해제
                        </button>
                      </div>
                    </div>
                  </>
                );
              })()}
            </>
          ) : (
            <>
              <p className="available-day-text">예약 가능일</p>
              <input
                type="text"
                placeholder="휴무일 사유 입력"
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                className="ui-input"
              />

              <div className="btn-row">
                <div className="temp_btn xsm">
                  <button className="btn" onClick={() => toggleClosedDay(formatDateKST(selectedDate), reason)}>
                    휴무일 등록
                  </button>
                </div>
              </div>
            </>
          )}
        </>
      ) : (
        <p>날짜를 선택하세요</p>
      )}
    </div>
    </div>
  </div>
);
};

export default ClosedDayManagePage;