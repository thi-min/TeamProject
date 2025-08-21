import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../common/context/AuthContext";
import NavLinks from "../common/routes/NavLinks";
import { LogoutLink } from "../program/login/pages/LogoutLink";

const Header = () => {
  // ✅ AuthContext가 제공하는 실제 키: isLogin, userRole, login
  const { isLogin, userId, role } = useAuth();
  console.log("isLogin:", isLogin, "userId:", userId, "role:", role);

  return (
    <header id="header">
      {/* 헤더 상단 로그인/회원가입 링크 */}
      <div className="header_top_box">
        <div className="info_box">
          {/* 날짜 */}
          <div className="today_box">
            <span className="today">2025년 7월 9일</span>
          </div>
          <div className="weather_inner">
            <span className="icon"></span>
            <span className="temperature">
              <span className="">
                33
                <span>℃</span>
              </span>
            </span>
            <span className="weather">맑음</span>
          </div>
        </div>
      </div>

      {/* 헤더 내부 영역 */}
      <div className="header_inner">
        <h1 className="logo">
          <Link to="/">
            <img
              src="/common/images/dp_logo.png"
              alt="로고"
              className="logo_image"
            />
            <span className="logo_text">함께마당</span>
          </Link>
        </h1>
        <div className="nav_box">
          <NavLinks />
          <div className="top_link_list">
            <div className="link_item">
              {isLogin ? (
                <LogoutLink className="user_item logout" />
              ) : (
                <Link to="/login" className="user_item login">
                  로그인
                </Link>
              )}
            </div>

            {/* 로그인 했을 때만 노출 */}
            {isLogin && (
              <div className="link_item">
                <Link to="/member/mypage">마이페이지</Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
