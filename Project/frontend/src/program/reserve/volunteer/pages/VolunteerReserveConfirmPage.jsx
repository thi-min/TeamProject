import React from "react";
import api from "../../../../common/api/axios";
import { useLocation, useNavigate } from "react-router-dom";
import "./../style/VolunteerReserveStyle.css"; // 스타일 경로 확인

const VolunteerReserveConfirmPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();

   if (!state || !state.formData) {
    return <p>예약 신청 정보가 없습니다.</p>;
  }

  const { formData, selectedDate, selectedSlotId, timeSlots } = state;

  const selectedSlot = timeSlots?.find((slot) => slot.timeSlotId === selectedSlotId);
  const reserveNumber = Number(formData.reserveNumber) || 0;
  
  const handleConfirm = async () => {
    try {
      const memberNum = localStorage.getItem("memberNum");
      if (!memberNum) return alert("로그인이 필요합니다.");

      const token = localStorage.getItem("accessToken");

      if (!selectedSlotId) return alert("시간대를 선택해 주세요.");
      if (!Number(formData.reserveNumber)) return alert("신청자 수를 선택해 주세요.");


      // ✅ 서버에 보낼 payload
      const payload = {
        reserveDto: {
          memberNum: Number(memberNum),   // 회원번호
          reserveType: 2,                 // VOLUNTEER
          reserveNumber: Number(formData.reserveNumber),
          note: formData.note || "",
        },
        volunteerDto: {
          volDate: selectedDate,          // "YYYY-MM-DD"
          timeSlotId: selectedSlotId,     // 선택한 슬롯 ID
        },
      };
      console.log("[POST] /api/reserve payload:", payload);

      const { data } = await api.post("/api/reserve", payload, {
        headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      });

      console.log("[POST] /api/reserve response:", data);
      const reserveCode = data.reserveCode;

      // ✅ 응답값을 성공 페이지로 넘기기
      navigate("/reserve/volunteer/success", {
        state: {
          reserveCode,
          formData,
          selectedSlot,
        },
      });
    } catch (error) {
      console.error("예약 실패", error.response?.data || error.message);
      alert("예약 처리 중 오류가 발생했습니다.");
    }
  };

  return (
    <div>
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon land"></div>
          <div className="form_title">봉사 예약 신청</div>
        </div>
      </div>

      <div className="info-section">
      <h3>봉사활동 신청서</h3>
        <table className="table type2 responsive border">
          <colgroup>
            <col className="w30p" />
            <col />
          </colgroup>
          <tbody>
            <tr><th>신청자명</th><td>{formData.name}</td></tr>
            <tr><th>연락처</th><td>{formData.phone}</td></tr>
            <tr><th>생년월일</th><td>{formData.birth}</td></tr>
            <tr><th>신청 인원 수</th><td>{reserveNumber}</td></tr>
            <tr><th>봉사 날짜</th><td>{selectedDate}</td></tr>
            <tr><th>봉사 시간</th><td>{selectedSlot?.label || "-"}</td></tr>
            <tr><th>비고</th><td>{formData.note || "-"}</td></tr>
          </tbody>
        </table>
      </div>

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

export default VolunteerReserveConfirmPage;