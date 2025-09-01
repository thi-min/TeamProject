import React, { useState, useEffect } from "react";
import MyReserveService from "./../services/MyReserveService";
import { useLocation, useNavigate } from "react-router-dom";
import "./../style/MyReserveStyle.css";
import PaymentInfo from "../../../common/components/PaymentInfo";

function MyReserveListPage() {
  const [selectedType, setSelectedType] = useState("LAND"); // 기본값 LAND
  const [reserves, setReserves] = useState([]);
  const location = useLocation();
  const navigate = useNavigate();
  const memberNum = localStorage.getItem("memberNum"); // 로그인된 사용자 정보
  
  useEffect(() => {
  fetchReserves();
}, [selectedType]);

  useEffect(() => {
    if (location.state?.selectedType) {
      setSelectedType(location.state.selectedType);
    }
  }, [location.state]);

  const fetchReserves = async () => {
    try {
      const type = selectedType === "LAND" ? 1 : 2;
      const data = await MyReserveService.getMyReservesByType(memberNum, type);
      setReserves(data);
    } catch (error) {
      console.error("예약 목록 조회 실패", error);
      setReserves([]);
    }
  };

  const handleRowClick = (reserveCode) => {
    if (selectedType === "LAND") {
      navigate(`/member/mypage/reserves/land/${reserveCode}`);
    } else {
      navigate(`/member/mypage/reserves/volunteer/${reserveCode}`);
    }
  };

  return (
    <div className="mypage-reserve-wrapper">
      <div className="reserve-header">
        <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon reserve"></div>
          <div className="form_title">예약 내역 조회</div>
        </div>
      </div>
        <h3>예약 내역</h3>
      </div>
      <div className="reserve-tabs">
        <div className={`temp_btn md ${selectedType === "LAND" ? "" : "white"}`}>
          <button
            className="btn"
            onClick={() => setSelectedType("LAND")}
          >
            놀이터예약
          </button>
        </div>
        <div className={`temp_btn md ${selectedType === "VOLUNTEER" ? "" : "white"}`}>
          <button
            className="btn"
            onClick={() => setSelectedType("VOLUNTEER")}
          >
            봉사예약
          </button>
        </div>
      </div>

      <table className="table type2 responsive">
        <thead>
          <tr>
            <th>예약코드</th>
            <th>신청일</th>
            <th>예약일</th>
            <th>예약유형</th>
            <th>예약상태</th>
          </tr>
        </thead>
        <tbody className="text_center">
          {reserves.length === 0 ? (
            <tr>
              <td colSpan="5">조회된 예약 내역이 없습니다.</td>
            </tr>
          ) : (
            reserves.map((item) => (
              <tr
                key={item.reserveCode}
                onClick={() => handleRowClick(item.reserveCode)}
                style={{ cursor: "pointer" }}
              >
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
      {selectedType === "LAND" && <PaymentInfo />}
    </div>
  );
}

export default MyReserveListPage;