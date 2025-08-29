import React from "react";
import api from "../../../../common/api/axios";
import { useLocation, useNavigate } from "react-router-dom";
import "./../style/LandReserveStyle.css";

const LandReserveConfirmPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();

  if (!state || !state.formData) {
    return <p>예약 신청 정보가 없습니다.</p>;
  }

  const { formData, selectedDate, selectedSlotId, timeSlots } = state;

  const selectedSlot = timeSlots?.find((slot) => slot.timeSlotId === selectedSlotId);

  // ✅ 프론트에서 금액 계산
  const basePrice = 2000; // 반려견 1마리 기준
  const animalNumber = Number(formData.animalNumber) || 0;
  const reserveNumber = Number(formData.reserveNumber) || 0;

  const additionalPrice =
    (animalNumber > 1 ? (animalNumber - 1) * 1000 : 0) + reserveNumber * 1000;
  const totalPrice = basePrice + additionalPrice;

  const basePriceDetail = `${basePrice}원 (반려견 1마리 기준)`;
  const extraPriceDetail = `추가 반려견 수 : ${Math.max(animalNumber - 1, 0)}(마리) x 1000원 + 보호자 수 : ${reserveNumber}(명) x 1000원`;

  const handleConfirm = async () => {
  try {
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) return alert("로그인이 필요합니다.");

    // ✅ 토큰도 같이 가져오기
    const token = localStorage.getItem("accessToken");

    // 1) 프론트에서 한번 더 유효성 체크
    if (!selectedSlotId) return alert("시간대를 선택해 주세요.");
    if (!Number(formData.reserveNumber)) return alert("보호자 수를 입력해 주세요.");

    // 2) 서버에 보낼 payload (로그로 확인)
    const payload = {
      reserveDto: {
        memberNum: Number(memberNum),         // 회원번호
        reserveType: 1,                        // LAND
        reserveNumber: Number(formData.reserveNumber), // 보호자 수
        note: formData.note || "",
      },
      landDto: {
        landType: formData.landType,           // "SMALL" | "LARGE"
        animalNumber: Number(formData.animalNumber),
        landDate: selectedDate,                // "YYYY-MM-DD"
        timeSlotId: selectedSlotId,            // 선택한 슬롯 ID
      },
    };
    console.log("[POST] /api/reserve payload:", payload);

    const { data } = await api.post("/api/reserve", payload, {
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    });

    console.log("[POST] /api/reserve response:", data);
    const reserveCode = data.reserveCode;

    navigate("/reserve/land/success", {
      state: {
        reserveCode,
        formData,
        selectedSlot,
        calculated: { basePrice, additionalPrice, totalPrice, basePriceDetail, extraPriceDetail },
      },
    });
  } catch (err) {
      console.error("예약 생성 실패", err.response?.data || err.message);
      alert("예약 생성에 실패했습니다. 다시 시도해주세요.");
    }
  };

  return (
    <div>
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon land"></div>
          <div className="form_title">놀이터 예약 신청</div>
        </div>
      </div>

      {/* 예약 신청서 */}
      <div className="info-section">
        <h3>예약 신청서</h3>
        <table className="table type2 responsive border">
          <colgroup>
            <col className="w30p" />
            <col />
          </colgroup>
          <tbody>
            <tr><th scope="row">신청자명</th><td>{formData.name}</td></tr>
            <tr><th scope="row">연락처</th><td>{formData.phone}</td></tr>
            <tr><th scope="row">놀이터 유형</th><td>{formData.landType === "SMALL" ? "소형견" : "대형견"}</td></tr>
            <tr><th scope="row">반려견 수</th><td>{animalNumber}</td></tr>
            <tr><th scope="row">보호자 수</th><td>{reserveNumber}</td></tr>
            <tr><th scope="row">예약 날짜</th><td>{selectedDate}</td></tr>
            <tr><th scope="row">예약 시간</th><td>{selectedSlot?.label || "-"}</td></tr>
            <tr><th scope="row">비고</th><td>{formData.note || "-"}</td></tr>
          </tbody>
        </table>
      </div>

      {/* 결제 정보 */}
      <div className="payment-section">
        <h3>결제 정보</h3>
        <table className="table type2 responsive border">
          <colgroup>
            <col className="w30p" />
            <col />
          </colgroup>
          <tbody>
            <tr><th>기본금액</th><td>{basePriceDetail}</td></tr>
            <tr><th>추가금액</th><td>{extraPriceDetail} → {additionalPrice}원</td></tr>
            <tr className="total-row"><th>총 결제금액</th><td><strong>{totalPrice.toLocaleString()}원</strong></td></tr>
          </tbody>
        </table>
      </div>

      {/* 버튼 */}
      <div className="form_center_box">

          <div className="temp_btn white md">
            <button type="button" className="btn" onClick={() => navigate(-1)}>
              이전
            </button>
          </div>
          <div className="temp_btn md">
            <button type="submit" className="btn" onClick={handleConfirm} >
             예약하기
            </button>
          </div>
      </div>
    </div>
  );
};

export default LandReserveConfirmPage;