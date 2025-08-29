import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import PaymentInfo from "../../../../common/components/PaymentInfo";

const LandReserveSuccessPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);

  if (!state) return <p>예약 정보가 없습니다.</p>;

  const { reserveCode } = state;

  const handlePaymentInfo = () => {
    setShowModal(true);
  };

  const handleGoMyReserves = () => {
    navigate("/member/mypage/reserves"); 
  };

  return (
    
    <div className="box reserve_box">
      <div className="reserve_header">
        <h3>예약 신청 완료</h3>
      </div>

      <div className="reserve_code">
        예약번호 &nbsp;<span>{reserveCode}</span>
      </div>

      <div className="reserve_body">
        <p>반려견 놀이터 예약 신청이 완료되었습니다.</p>
        <p>신청일 당일 내 미결제 시 자동 취소되며, 결제까지 진행해주세요.</p>
      </div>

      <div className="btn_group">
        <span className="temp_btn white md">
          <button type="button" className="btn" onClick={handlePaymentInfo}>
            예약 결제하기
          </button>
        </span>
        <span className="temp_btn  md">
          <button type="button" className="btn" onClick={handleGoMyReserves}>
            예약 내역 확인
          </button>
        </span>
      </div>

      {/* ✅ 결제 안내 모달 */}
      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <PaymentInfo />
            <button type="button" className="btn" onClick={() => setShowModal(false)}>닫기</button>
          </div>
        </div>
      )}
    </div>

  );
};

export default LandReserveSuccessPage;