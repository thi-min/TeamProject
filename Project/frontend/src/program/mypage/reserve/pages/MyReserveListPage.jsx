import React, { useState, useEffect } from "react";
import MyReserveService from "../services/MyReserveService";
import { useNavigate } from "react-router-dom";
import "./../style/MyReserveStyle.css";

function MyReserveListPage() {
  const [selectedType, setSelectedType] = useState("LAND"); // 기본값 LAND
  const [reserves, setReserves] = useState([]);
  const navigate = useNavigate();
  const memberNum = localStorage.getItem("memberNum"); // 로그인된 사용자 정보

  const mockLandReserves = [
    {
      reserveCode: "L00001",
      applyDate: "2025-08-01",
      reserveDate: "2025-08-15",
      reserveTypeName: "놀이터예약",
      reserveState: "예약완료",
    },
    {
      reserveCode: "L00002",
      applyDate: "2025-08-02",
      reserveDate: "2025-08-20",
      reserveTypeName: "놀이터예약",
      reserveState: "이용완료",
    },
  ];

  const mockVolunteerReserves = [
    {
      reserveCode: "V00001",
      applyDate: "2025-08-03",
      reserveDate: "2025-08-18",
      reserveTypeName: "봉사예약",
      reserveState: "예약완료",
    },
    {
      reserveCode: "V00002",
      applyDate: "2025-08-05",
      reserveDate: "2025-08-25",
      reserveTypeName: "봉사예약",
      reserveState: "취소됨",
    },
  ];

  useEffect(() => {
    fetchReserves();
  }, [selectedType]);

  const fetchReserves = async () => {
    try {
      const type = selectedType === "LAND" ? 1 : 2;
      const data = await MyReserveService.getMyReservesByType(memberNum, type);
      setReserves(data);
    } catch (error) {
      console.error("예약 목록 조회 실패", error);
    }
  };

  return (
    <div className="mypage-reserve-wrapper">
      <h2>마이페이지 - 예약내역 조회</h2>
      <div className="reserve-tabs">
        <button
          className={`tab-button ${selectedType === "LAND" ? "active" : ""}`}
          onClick={() => setSelectedType("LAND")}
        >
          놀이터예약
        </button>
        <button
          className={`tab-button ${selectedType === "VOLUNTEER" ? "active" : ""}`}
          onClick={() => setSelectedType("VOLUNTEER")}
        >
          봉사예약
        </button>
      </div>

      <table className="reserve-table">
        <thead>
          <tr>
            <th>예약코드</th>
            <th>신청일</th>
            <th>예약일</th>
            <th>예약유형</th>
            <th>예약상태</th>
          </tr>
        </thead>
        <tbody>
          {reserves.length === 0 ? (
            <tr>
              <td colSpan="5">조회된 예약 내역이 없습니다.</td>
            </tr>
          ) : (
            reserves.map((item) => (
              <tr key={item.reserveCode}>
                <td>{item.reserveCode}</td>
                <td>{item.applyDate}</td>
                <td>{item.reserveDate}</td>
                <td>{item.reserveTypeName}</td>
                <td>{item.reserveState}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default MyReserveListPage;