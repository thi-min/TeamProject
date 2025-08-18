import React from "react";
import { useLocation } from "react-router-dom";

const VolunteerReserveSuccessPage = () => {
  const { state } = useLocation();
  if (!state) return <p>예약 정보가 없습니다.</p>;

  // ✅ 구조 분해
  const { reserveCode /*, formData, selectedSlot, selectedDate */ } = state;

  return (
    <div className="box reserve_box">
      <div className="reserve_header">
        <h2>봉사 신청 완료</h2>
      </div>

      <div className="reserve_code">
        예약번호 <strong>{reserveCode}</strong>
      </div>

      <div className="reserve_body">
        <p>봉사활동 예약 신청이 완료되었습니다.</p>
        <p>신청일 당일 시간을 준수해주세요. 감사합니다.</p>
      </div>

      <div className="btn_group">
        <span className="temp_btn white md">
          <button type="button" className="btn">봉사 신청내역 확인</button>
        </span>
      </div>
    </div>
  );
};

export default VolunteerReserveSuccessPage;