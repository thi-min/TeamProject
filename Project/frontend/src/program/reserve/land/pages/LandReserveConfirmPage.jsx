import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./../style/LandReserveStyle.css";

const LandReserveConfirmPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  
  if (!state || !state.formData) {
    return <p>예약 신청 정보가 없습니다.</p>;
  }

  const { formData, selectedSlot } = state;

  return (
    <div className="land-confirm-page">
      <h2 className="page-title">놀이터 예약 신청서</h2>

      {/* 예약 신청서 */}
      <section className="info-section">
        <h3>예약 신청서</h3>
        <table className="info-table">
          <tbody>
            <tr><th>신청자명</th><td>{formData.name}</td></tr>
            <tr><th>연락처</th><td>{formData.phone}</td></tr>
            <tr><th>놀이터 유형</th><td>{formData.landType === "SMALL" ? "소형견" : "대형견"}</td></tr>
            <tr><th>반려견 수</th><td>{formData.animalNumber}</td></tr>
            <tr><th>사람 수</th><td>{formData.reserveNumber}</td></tr>
            <tr><th>예약 날짜</th><td>{formData.selectedDate}</td></tr>
            <tr><th>예약 시간</th><td>{selectedSlot?.label}</td></tr>
            <tr><th>비고</th><td>{formData.note || "-"}</td></tr>
          </tbody>
        </table>
      </section>

      {/* 결제 정보 */}
      <section className="payment-section">
        <h3>결제 정보</h3>
        <table className="info-table">
          <tbody>
            <tr><th>총 이용금액</th><td>{formData.totalPrice}원</td></tr>
            <tr><th>기본</th><td>{formData.basePriceDetail}</td></tr>
            <tr><th>추가</th><td>{formData.extraPriceDetail} → {formData.additionalPrice}원</td></tr>
            <tr className="total-row"><th>총 결제금액</th><td><strong>{formData.totalPrice.toLocaleString()}원</strong></td></tr>
          </tbody>
        </table>
      </section>

      {/* 버튼 */}
      <div className="form-action-buttons">
        <button
          className="prev-button"
          type="button"
          onClick={() => navigate(-1)} // 이전 페이지로
        >
          이전
        </button>
        <button
          className="next-button"
          type="button"
          onClick={() =>
            navigate("/reserve/land/success", {
              state: { formData, selectedSlot },
            })
          }
        >
          다음
        </button>
      </div>
    </div>
  );
};

export default LandReserveConfirmPage;