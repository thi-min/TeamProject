import React, { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../common/context/AuthContext";
import NavLinks from "../common/routes/NavLinks";
import { LogoutLink } from "../program/login/pages/LogoutLink";
import AlarmBanner from "../program/alarm/services/AlarmBanner";

//공용css
import "../contents/styles/contents1.css";
import "../contents/styles/contents2.css";
import "../bbs/style/bbsTemplate.css";

const Header = () => {
  const { isLogin, userId, role } = useAuth();
  console.log("isLogin:", isLogin, "userId:", userId, "role:", role);

  const [isAlarmOpen, setIsAlarmOpen] = useState(false);

  const handleAlarmToggle = () => {
    setIsAlarmOpen((prev) => !prev);
  };

  return (
    <header id="header">
      {/* 헤더 상단 로그인/회원가입 링크 */}
      <div className="header_top_box">
        <div className="info_box">
          {/* 로그인 했을 때만 노출 */}
          {isLogin && (
            <div className="link_item">
              {role === "ADMIN" ? (
                <Link to="/admin">관리자페이지</Link>
              ) : (
                <Link to="/member/mypage">마이페이지</Link>
              )}
            </div>
          )}
          {/* 알람 버튼 추가 */}
          <button className="alarm-button" onClick={handleAlarmToggle}>
            🔔
          </button>
          <AlarmBanner
            isOpen={isAlarmOpen}
            onClose={() => setIsAlarmOpen(false)}
          />
          {/* 날짜 */}
          <div className="today_box">
            <span className="today">2025년 9월 9일</span>
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
                <LogoutLink className="user_item logout">로그아웃</LogoutLink>
              ) : (
                <Link to="/login" className="user_item login">
                  로그인
                </Link>
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
