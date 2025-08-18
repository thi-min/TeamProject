// src/admin/pages/AdminPage.jsx

import React from "react";
import { Link } from "react-router-dom";
import "../style/admin.css";

// ✅ 관리자 메인 허브 페이지
// - 로그인 후 진입하는 첫 페이지
// - 회원관리, 예약관리, 비밀번호 변경 등 각 기능 페이지로 이동하는 링크 제공
export default function AdminPage() {
  return (
    <div className="admin_page">
      <div className="title_box">
        <div className="title">관리자 페이지</div>
      </div>
      <div className="admin_area">
        <ul className="admin_menu">
          <li className="link_item">
            <Link to="/admin/updatePw">비밀번호 변경</Link>
          </li>
          <li className="link_item">
            <Link to="/admin/memberList">회원 관리</Link>
          </li>
          <li className="link_item">
            <Link to="/admin/">놀이터 관리</Link>
          </li>
          <li className="link_item">
            <Link to="/admin/">봉사 관리</Link>
          </li>
          <li className="link_item">
            <Link to="">게시판관리</Link>
          </li>
          <li className="link_item">
            <Link to="">시간대관리</Link>
          </li>
        </ul>
      </div>
    </div>
  );
}
