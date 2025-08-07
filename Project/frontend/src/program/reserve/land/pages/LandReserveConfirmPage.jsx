import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import "./../style/LandReserveStyle.css";

const LandReserveConfirmPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();

  if (!state) return <p>예약 정보가 없습니다.</p>;

  const {
    name, phone, landType, animalNumber, guardianNumber, note,
    selectedDate, selectedSlotId, timeSlots
  } = state;
  const timeLabel = timeSlots?.find(
  (slot) => slot.timeSlotId === selectedSlotId
  )?.label || "시간대 정보 없음";

    const basePrice = 2000;

    // 추가 요금 계산
    const additionalDogPrice = animalNumber > 1 ? (animalNumber - 1) * 1000 : 0;
    const guardianPrice = guardianNumber * 1000;

    const additionalPrice = additionalDogPrice + guardianPrice;

    // 총 결제 금액
    const totalAmount = basePrice + additionalPrice;

    // 설명 텍스트
    const basePriceDetail = `기본 (${landType === "SMALL" ? "소형견" : "대형견"} x 1마리)`;
    const extraPriceDetail = `추가반려견 ${animalNumber > 1 ? (animalNumber - 1) : 0}마리, (1,000원 x ${animalNumber > 1 ? (animalNumber - 1 + guardianNumber) : guardianNumber}) → ${additionalPrice.toLocaleString()}원`;

  const handleReserveSubmit = async () => {
    try {
      const requestDto = {
        reserveDto: {
          memberNum: 1,
          reserveType: 1,
          reserveNumber: guardianNumber,
          note,
        },
        landDto: {
          landDate: selectedDate,
          timeSlotId: selectedSlotId,
          landType,
          animalNumber,
        },
        volunteerDto: null,
      };

      const response = await axios.post("/api/reserve", requestDto);
      const reserveCode = response.data.reserveCode;

      navigate("/reserve/land/success", {
        state: {
          reserveCode: "1", 
        },
      });
    } catch (error) {
      console.error("예약 실패", error);
      alert("예약 처리 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="land-confirm-page">
      <h2 className="page-title">놀이터 예약 신청서</h2>

      <section className="info-section">
        <h3>예약 신청서</h3>
        <table className="info-table">
          <tbody>
            <tr><th>신청자명</th><td>{name}</td></tr>
            <tr><th>연락처</th><td>{phone}</td></tr>
            <tr><th>놀이터 유형</th><td>{landType === "SMALL" ? "소형견" : "대형견"}</td></tr>
            <tr><th>반려견 수</th><td>{animalNumber}</td></tr>
            <tr><th>사람 수</th><td>{guardianNumber}</td></tr>
            <tr><th>예약 시간</th><td>{selectedDate} {timeLabel || "시간 정보 없음"}</td></tr>
            <tr><th>비고</th><td>{note}</td></tr>
          </tbody>
        </table>
      </section>

      <section className="payment-section">
        <h3>결제 정보</h3>
        <table className="info-table">
          <tbody>
            <tr><th>총 이용금액</th><td>{totalAmount}원</td></tr>
            <tr><th>기본</th><td>{basePriceDetail} (2,000원 x 1) → 2,000원</td></tr>
            <tr><th>추가</th><td>{extraPriceDetail} (1,000원 x {guardianNumber}) → {guardianNumber * 1000}원</td></tr>
            <tr className="total-row"><th>총 결제금액</th><td><strong>{totalAmount.toLocaleString()}원</strong></td></tr>
          </tbody>
        </table>
      </section>

      <div className="form-action-buttons">
        <button className="prev-button" type="button"  onClick={() => navigate(-1)}>이전</button>
        <button className="next-button" type="button"  onClick={handleReserveSubmit}>다음</button>
      </div>
    </div>
  );
};

export default LandReserveConfirmPage;