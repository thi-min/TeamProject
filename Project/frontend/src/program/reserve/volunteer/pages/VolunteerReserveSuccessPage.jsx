import React from "react";
import { useLocation } from "react-router-dom";

const VolunteerReserveSuccessPage = () => {
  const { state } = useLocation();

  if (!state) return <p>예약 정보가 없습니다.</p>;

  return (
    <div className="volunteer-success-page">
      <h2>예약 신청 완료</h2>
      <div className="success-box">
        <p className="reserve-code">예약번호 <strong>{state.reserveCode}</strong></p>
        <p>봉사활동 예약 신청이 완료되었습니다.</p>
        <p>신청일 당일 내 미결제 시 자동 취소되며, 결제까지 진행해주세요.</p>
        <button className="btn-payment">예약 결제하기</button>
      </div>
    </div>
  );
};

export default VolunteerReserveSuccessPage;