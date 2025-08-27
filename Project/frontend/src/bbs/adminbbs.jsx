// src/admin/pages/AdminBbs.jsx
import React from "react";
import { Link } from "react-router-dom";
import "../program/admin/style/admin.css";

export default function AdminBbs() {
  return (
    <div className="admin_page">
      <div className="title_box">
        <div className="title">게시판 관리</div>
      </div>
      <div className="admin_area">
        {/* 탭 메뉴 */}
        <ul className="admin_menu">
          <li className="link_item type1">
            <Link to="/admin/bbs/image">이미지 게시판</Link>
          </li>
          <li className="link_item type2">
            <Link to="/admin/bbs/qna">QnA 게시판</Link>
          </li>
          <li className="link_item type3">
            <Link to="/admin/bbs/normal">공지사항 게시판</Link>
          </li>
        </ul>

        {/* 안내 메시지 */}
        <div className="bbs_content">
          게시판을 선택해주세요.
        </div>

        {/* 뒤로가기 */}
        <div className="back_link">
          <Link to="/admin">← 관리자 메인으로</Link>
        </div>
      </div>
    </div>
  );
}
