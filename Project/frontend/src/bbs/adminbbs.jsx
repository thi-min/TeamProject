// src/admin/pages/AdminBbs.jsx
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "../program/admin/style/admin.css";

export default function AdminBbs() {
  const navigate = useNavigate();
  return (
    <div className="admin_page">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">게시판 관리</div>
        </div>
      </div>
      {/* 안내 메시지 */}
      <div className="bbs_content">게시판을 선택해주세요.</div>
      <div className="admin_area">
        <ul className="my_menu">
          <li className="link_item type1">
            <Link to="/admin/bbs/image">센터 아이들</Link>
          </li>
          <li className="link_item type2">
            <Link to="/admin/bbs/qna">QnA 게시판</Link>
          </li>
          <li className="link_item type3">
            <Link to="/admin/bbs/normal">공지사항 게시판</Link>
          </li>
        </ul>

        {/* 뒤로가기 */}
        <div className="adminbbs-btn">
          <div className="temp_btn white md">
            <button type="button" className="btn" onClick={() => navigate(-1)}>
              뒤로
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
