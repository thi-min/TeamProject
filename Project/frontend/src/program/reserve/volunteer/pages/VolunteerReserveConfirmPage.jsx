import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import "./../style/VolunteerReserveStyle.css"; // 스타일 경로 확인

const VolunteerReserveConfirmPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();

  if (!state) return <p>예약 정보가 없습니다.</p>;

  const {
    name, phone, birth, reserveNumber, note,
    selectedDate, selectedSlotId, timeSlots
  } = state;

  const timeLabel = timeSlots?.find(
    (slot) => slot.timeSlotId === selectedSlotId
  )?.label || "시간대 정보 없음";

  const handleReserveSubmit = async () => {
    try {
      const requestDto = {
        reserveDto: {
          memberNum: 1, // TODO: 로그인 연동 후 변경
          reserveType: 2,
          reserveNumber: reserveNumber,
          note,
        },
        landDto: null,
        volunteerDto: {
          volDate: selectedDate,
          timeSlotId: selectedSlotId,
          memberBirth: birth,
        },
      };

      const response = await axios.post("/api/reserve", requestDto);
      const reserveCode = response.data.reserveCode;

      navigate("/reserve/volunteer/success", {
        state: {
          reserveCode: "2"
        },
      });
    } catch (error) {
      console.error("예약 실패", error);
      alert("예약 처리 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="volunteer-confirm-page">
      <h2 className="page-title">봉사활동 신청서</h2>

      <section className="info-section">
        <h3>예약 신청서</h3>
        <table className="info-table">
          <tbody>
            <tr><th>신청자명</th><td>{name}</td></tr>
            <tr><th>연락처</th><td>{phone}</td></tr>
            <tr><th>생년월일</th><td>{birth}</td></tr>
            <tr><th>신청 인원 수</th><td>{reserveNumber}</td></tr>
            <tr><th>봉사 날짜</th><td>{selectedDate}</td></tr>
            <tr><th>봉사 시간</th><td>{timeLabel}</td></tr>
            <tr><th>비고</th><td>{note || "-"}</td></tr>
          </tbody>
        </table>
      </section>

      <div className="form-action-buttons">
        <button className="prev-button" type="button" onClick={() => navigate(-1)}>이전</button>
        <button className="next-button" type="button" onClick={handleReserveSubmit}>다음</button>
      </div>
    </div>
  );
};

export default VolunteerReserveConfirmPage;