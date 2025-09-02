import React, { useEffect, useState } from "react";
import api from "../../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "../style/ReserveManage.css";
import "../../../common/styles/pagin.css";
import AdminReserveService from "../services/AdminReserveService";

const AdminLandReservePage = () => {
  const [reservations, setReservations] = useState([]);
  const [filters, setFilters] = useState({
    startDate: "",
    endDate: "",
    memberName: "",
    reserveState: "",
  });
  const navigate = useNavigate();

  // 목록 조회
  const fetchReservations = async () => {
  try {
    const { data } = await AdminReserveService.getLandReservations();
    setReservations(data);
  } catch (err) {
    console.error("예약 목록 조회 실패", err);
  }
};
//페이지네이션 상태
const [currentPage, setCurrentPage] = useState(1);
const itemsPerPage = 10;
const totalPages = Math.ceil(reservations.length / itemsPerPage);

const paginatedReservations = reservations.slice(
  (currentPage - 1) * itemsPerPage,
  currentPage * itemsPerPage
);


// 검색 실행 (조건부)
const handleSearch = async () => {
  try {
    // 검색 조건 null 필드 제거
    const payload = {};
    if (filters.startDate) payload.startDate = filters.startDate;
    if (filters.endDate) payload.endDate = filters.endDate;
    if (filters.memberName) payload.memberName = filters.memberName;
    if (filters.reserveState) payload.reserveState = filters.reserveState;

    // 아무 조건도 없을 때는 전체 조회
    if (Object.keys(payload).length === 0) {
      fetchReservations();
      return;
    }

    const { data } = await AdminReserveService.searchLandReservations(payload);
    setReservations(data);
  } catch (err) {
    console.error("예약 검색 실패", err);
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
  fetchReservations(); // 전체 목록 다시 불러오기
};

// 페이지 로드시 전체 예약 조회
useEffect(() => {
  fetchReservations();
}, []);



  return (
    <div className="admin-reserve-page">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon land"></div>
          <div className="form_title">놀이터 예약 관리</div>
        </div>
      </div>
      <h3>놀이터 예약 목록</h3>

      {/* 검색 영역 */}
      <div className="reserve-search-bar">
        <div class="temp_form md w15p">
          <input
            type="date"
            class="temp_input"
            value={filters.startDate}
            onChange={(e) => setFilters({ ...filters, startDate: e.target.value })}
          />
        </div>
        <div class="temp_form md w15p">
        <input
          type="date"
          class="temp_input"
          value={filters.endDate}
          onChange={(e) => setFilters({ ...filters, endDate: e.target.value })}
        />
        </div>
        <div class="temp_form md w10p">
        <input
          type="text"
          class="temp_input"
          placeholder="회원 이름"
          value={filters.memberName}
          onChange={(e) => setFilters({ ...filters, memberName: e.target.value })}
        />
        </div>
        <div class="temp_form_box md">
        <select
          class="temp_select"
          value={filters.reserveState}
          onChange={(e) => setFilters({ ...filters, reserveState: e.target.value })}
        >
          <option value="">상태</option>
          <option value="ING">대기중</option>
          <option value="REJ">거절</option>
          <option value="DONE">승인</option>
          <option value="CANCEL">취소</option>
        </select>
        </div>
        <button onClick={handleSearch}>검색</button>
        <button onClick={handleReset} className="reset-btn">초기화</button>
      </div>

      {/* 결과 테이블 */}
      <table className="table type2 responsive border">
        <thead>
          <tr>
            <th>예약코드</th>
            <th>회원명</th>
            <th>프로그램명</th>
            <th>예약일자</th>
            <th>상태</th>
          </tr>
        </thead>
        <tbody className="text_center">
          {paginatedReservations.map((r) => (
            <tr
              key={r.reserveCode}
              onClick={() => navigate(`/admin/reserve/land/${r.reserveCode}`)}
              style={{ cursor: "pointer" }} >
              <td>{r.reserveCode}</td>
              <td>{r.memberName}</td>
              <td>{r.programName}</td>
              <td>{r.reserveDate}</td>
              <td>{r.reserveState}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="pagination_box">
        <button
          className="page_btn prev"
          disabled={currentPage === 1}
          onClick={() => setCurrentPage(currentPage - 1)}
        >
          이전
        </button>

        <div className="page_btn_box">
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i + 1}
              className={`page ${currentPage === i + 1 ? "active" : ""}`}
              onClick={() => setCurrentPage(i + 1)}
            >
              {i + 1}
            </button>
          ))}
        </div>

        <button
          className="page_btn next"
          disabled={currentPage === totalPages}
          onClick={() => setCurrentPage(currentPage + 1)}
        >
          다음
        </button>
      </div>
            
    </div>
  );
};

export default AdminLandReservePage;