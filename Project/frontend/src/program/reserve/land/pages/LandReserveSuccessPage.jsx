import React from "react";
import { useLocation } from "react-router-dom";

const LandReserveSuccessPage = () => {
  const { state } = useLocation();
  if (!state) return <p>예약 정보가 없습니다.</p>;

  // ✅ 구조 분해
  const { reserveCode} = state;

  return (
    <div className="box reserve_box">
      <div className="reserve_header">
        <h2>예약 신청 완료</h2>
      </div>

      <div className="reserve_code">
        예약번호 <strong>{reserveCode}</strong>
      </div>

      <div className="reserve_body">
        <p>반려견 놀이터 예약 신청이 완료되었습니다.</p>
        <p>신청일 당일 내 미결제 시 자동 취소되며, 결제까지 진행해주세요.</p>
      </div>

      <div className="btn_group">
        <span className="temp_btn white md">
          <button type="button" className="btn">예약 결제하기</button>
        </span>
        <span className="temp_btn white md">
          <button type="button" className="btn">예약 내역 확인</button>
        </span>
      </div>
    </div>
  );
};

export default LandReserveSuccessPage;