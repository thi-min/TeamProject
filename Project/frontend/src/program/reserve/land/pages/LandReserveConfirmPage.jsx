import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import "./../style/LandReserveStyle.css";

const LandReserveConfirmPage = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  const token = localStorage.getItem("accessToken");

  const [detail, setDetail] = useState(null); // 백엔드에서 받아온 상세 정보

  const {
    memberNum,
    name,
    phone,
    landType,
    animalNumber,
    guardianNumber,
    note,
    selectedDate,
    selectedSlotId,
    timeSlots,
  } = state || {};

  const timeLabel =
    timeSlots?.find((slot) => slot.timeSlotId === selectedSlotId)?.label ||
    "시간대 정보 없음";

  // ✅ 서버에서 가격/상세정보 불러오기
  useEffect(() => {
    if (!state) return; // state 없을 때는 API 호출 안 함

    const fetchDetail = async () => {
      try {
        const res = await axios.get(
          `/api/land/detail-temp`, // ← 실제 엔드포인트로 변경
          {
            params: {
              landDate: selectedDate,
              timeSlotId: selectedSlotId,
              landType,
              animalNumber,
              guardianNumber,
            },
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setDetail(res.data);
      } catch (err) {
        console.error("상세 정보 불러오기 실패", err);
        alert("결제 정보를 불러올 수 없습니다.");
      }
    };
    fetchDetail();
  }, [
    state,
    selectedDate,
    selectedSlotId,
    landType,
    animalNumber,
    guardianNumber,
    token,
  ]);

  if (!state) return <p>예약 정보가 없습니다.</p>;
  if (!detail) return <p>결제 정보를 불러오는 중입니다...</p>;

  const handleReserveSubmit = async () => {
    try {
      const requestDto = {
        reserveDto: {
          memberNum,
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

      const response = await axios.post("/api/reserve", requestDto, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const reserveCode = response.data.reserveCode;

      navigate("/reserve/land/success", { state: { reserveCode } });
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
            <tr><th>예약 날짜</th><td>{selectedDate}</td></tr>
            <tr><th>예약 시간</th><td>{timeLabel}</td></tr>
            <tr><th>비고</th><td>{note}</td></tr>
          </tbody>
        </table>
      </section>

      <section className="payment-section">
        <h3>결제 정보</h3>
        <table className="info-table">
          <tbody>
            <tr><th>총 이용금액</th><td>{detail.totalPrice}원</td></tr>
            <tr><th>기본</th><td>{detail.basePriceDetail}</td></tr>
            <tr><th>추가</th><td>{detail.extraPriceDetail} → {detail.additionalPrice}원</td></tr>
            <tr className="total-row"><th>총 결제금액</th><td><strong>{detail.totalPrice.toLocaleString()}원</strong></td></tr>
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

export default LandReserveConfirmPage;