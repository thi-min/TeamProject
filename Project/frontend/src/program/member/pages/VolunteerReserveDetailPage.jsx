import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import MyReserveService from "../services/MyReserveService";
import "../style/MyReserveStyle.css";

const VolunteerReserveDetailPage = () => {
  const { reserveCode } = useParams();   // /member/mypage/reserves/volunteer/:reserveCode
  const navigate = useNavigate();
  const [detail, setDetail] = useState(null);

  useEffect(() => {
    async function fetchDetail() {
      try {
        const token = localStorage.getItem("accessToken");
        const data = await MyReserveService.getVolunteerReserveDetail(reserveCode, token);
        setDetail(data);
      } catch (err) {
        console.error("상세조회 실패:", err);
      }
    }
    fetchDetail();
  }, [reserveCode]);

  const handleCancel = async () => {
    if (!window.confirm("정말 예약을 취소하시겠습니까?")) return;
        try {
            const token = localStorage.getItem("accessToken");   // 토큰 꺼내기
            console.log("accessToken:", token);
            await MyReserveService.cancelReserve(reserveCode, token); // 토큰 전달
            alert("예약이 취소되었습니다.");
            navigate("/member/mypage/reserves");
        } catch (err) {
            console.error("예약 취소 실패:", err);
            alert("예약 취소 중 오류가 발생했습니다.");
        }
        };

     if (!detail) return <p>로딩 중...</p>;

  return (
    <div className="mypage-reserve-wrapper">
      <h2>봉사 예약 정보</h2>
      <table className="table type2 responsive">
        <colgroup>
          <col className="w20p" />
          <col />
        </colgroup>
        <tbody>
          <tr><th>예약 코드</th><td>{detail.reserveCode}</td></tr>
          <tr><th>신청자명</th><td>{detail.memberName}</td></tr>
          <tr><th>연락처</th><td>{detail.memberPhone}</td></tr>
          <tr><th>생년월일</th><td>{detail.memberBirth}</td></tr>
          <tr><th>봉사일</th><td>{detail.volDate}</td></tr>
          <tr><th>신청 일자</th><td>{detail.applyDate}</td></tr>
          <tr><th>봉사시간</th><td>{detail.label}</td></tr>
          <tr><th>인원수</th><td>{detail.reserveNumber}</td></tr>
          <tr><th>예약 상태</th><td>{detail.reserveState}</td></tr>
          <tr><th>비고</th><td>{detail.note ? detail.note : "-"}</td></tr>
        </tbody>
      </table>

      <div className="reserve-buttons">
        <button onClick={() => navigate(-1)}>이전</button>
        {detail && detail.reserveState !== "CANCEL" && detail.reserveState !== "REJ" && (
        <button onClick={handleCancel}>예약취소</button>)}
      </div>
    </div>
  );
};

export default VolunteerReserveDetailPage;