// src/admin/pages/AdminPage.jsx

import React from "react";
import { Link } from "react-router-dom";

// ✅ 관리자 메인 허브 페이지
// - 로그인 후 진입하는 첫 페이지
// - 회원관리, 예약관리, 비밀번호 변경 등 각 기능 페이지로 이동하는 링크 제공
export default function AdminPage() {
  return (
    <div className="admin-page">
      <h1>관리자 메인 페이지</h1>
      <p>원하는 관리 기능을 선택하세요.</p>

      <ul className="admin-menu">
        {/* 비밀번호 변경 */}
        <li>
          <Link to="/admin/updatePw">비밀번호 변경</Link>
        </li>

        {/* 회원 관리 */}
        <li>
          <Link to="/admin/members">회원 관리</Link>
        </li>

        {/* 예약 관리 */}
        <li>
          <Link to="/admin/reservations">예약 관리</Link>
        </li>
      </ul>
    </div>
  );
}
