// src/admin/pages/AdminPage.jsx

import React from "react";
import { Link } from "react-router-dom";
import "../style/admin.css";

// ✅ 관리자 메인 허브 페이지
// - 로그인 후 진입하는 첫 페이지
// - 회원관리, 예약관리, 비밀번호 변경 등 각 기능 페이지로 이동하는 링크 제공
export default function AdminPage() {
  return (
    <div className="my_page">
      <div className="title_box">
        <div className="title">관리자 페이지</div>
      </div>
      <div className="admin_area">
        <ul className="my_menu">
          <li className="link_item type1">
            <Link to="/admin/updatePw">비밀번호 변경</Link>
          </li>
          <li className="link_item type2">
            <Link to="/admin/membersList">회원 관리</Link>
          </li>
          <li className="link_item type3">
            <Link to="/admin/reserve/land">놀이터 예약 관리</Link>
          </li>
          <li className="link_item type4">
            <Link to="/admin/reserve/volunteer">봉사 예약 관리</Link>
          </li>
          <li className="link_item type5">
            <Link to="/admin/bbs">게시판 관리</Link>
          </li>
          <li className="link_item type6">
            <Link to="/admin/timeslots">예약 시간대 관리</Link>
          </li>
          <li className="link_item type7">
            <Link to="/admin/closedday">휴무일 관리</Link>
          </li>
          <li className="link_item type8">
            <Link to="/admin/banner">배너 관리</Link>
          </li>
          <li className="link_item type9">
            <Link to="/admin/chat/list">채팅 관리</Link>
          </li>
          <li className="link_item type10">
            <Link to="/admin/funds/list">후원 관리</Link>
          </li>
          <li className="link_item type11">
            <Link to="/admin/adopt/list">입양 관리</Link>
          </li>
          {/* <li className="link_item type11">
            <Link to="/admin/animal/list">동물 관리</Link>
          </li> */}
        </ul>
      </div>
    </div>
  );
}
