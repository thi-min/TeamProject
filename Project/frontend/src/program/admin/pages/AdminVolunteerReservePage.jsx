import React, { useEffect, useState } from "react";
import axios from "axios";
import "../style/ReserveManage.css";

const AdminVolunteerReservePage = () => {
  const [reservations, setReservations] = useState([]);
  const [filters, setFilters] = useState({
    startDate: "",
    endDate: "",
    memberName: "",
    reserveState: "",
  });

  // 봉사 예약 목록 조회
  const fetchReservations = async () => {
    try {
      const { data } = await axios.get("/api/admin/reserve/volunteer");
      setReservations(data);
    } catch (err) {
      console.error("봉사 예약 목록 조회 실패", err);
    }
  };

  // 봉사 예약 검색 실행 (조건부)
  const handleSearch = async () => {
    try {
      const payload = {};
      if (filters.startDate) payload.startDate = filters.startDate;
      if (filters.endDate) payload.endDate = filters.endDate;
      if (filters.memberName) payload.memberName = filters.memberName;
      if (filters.reserveState) payload.reserveState = filters.reserveState;

      if (Object.keys(payload).length === 0) {
        fetchReservations();
        return;
      }

      const { data } = await axios.post(
        "/api/admin/reserve/volunteer/search",
        payload
      );
      setReservations(data);
    } catch (err) {
      console.error("봉사 예약 검색 실패", err);
    }
  };

  // 검색 초기화
  const handleReset = () => {
    setFilters({
      startDate: "",
      endDate: "",
      memberName: "",
      reserveState: "",
    });
    fetchReservations();
  };

  useEffect(() => {
    fetchReservations();
  }, []);

  return (
    <div className="admin-reserve-page">
      <h2>봉사 예약 관리</h2>

      {/* 검색 영역 */}
      <div className="search-bar">
        <input
          type="date"
          value={filters.startDate}
          onChange={(e) => setFilters({ ...filters, startDate: e.target.value })}
        />
        <input
          type="date"
          value={filters.endDate}
          onChange={(e) => setFilters({ ...filters, endDate: e.target.value })}
        />
        <input
          type="text"
          placeholder="회원 이름"
          value={filters.memberName}
          onChange={(e) =>
            setFilters({ ...filters, memberName: e.target.value })
          }
        />
        <select
          value={filters.reserveState}
          onChange={(e) =>
            setFilters({ ...filters, reserveState: e.target.value })
          }
        >
          <option value="">상태 전체</option>
          <option value="ING">진행중</option>
          <option value="DONE">완료</option>
          <option value="CANCEL">취소</option>
        </select>
        <button onClick={handleSearch}>검색</button>
        <button onClick={handleReset} className="reset-btn">초기화</button>
      </div>

      {/* 결과 테이블 */}
      <table className="reservation-table">
        <thead>
          <tr>
            <th>예약코드</th>
            <th>회원명</th>
            <th>프로그램명</th>
            <th>예약일자</th>
            <th>상태</th>
          </tr>
        </thead>
        <tbody>
          {reservations.map((r) => (
            <tr key={r.reserveCode}>
              <td>{r.reserveCode}</td>
              <td>{r.memberName}</td>
              <td>{r.programName}</td>
              <td>{r.reserveDate}</td>
              <td>{r.reserveState}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminVolunteerReservePage;