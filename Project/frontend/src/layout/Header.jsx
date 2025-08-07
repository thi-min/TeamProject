import React from 'react';
import {isLogin, useAuth } from '../common/context/AuthContext';
import LogoutLink from '../program/login/pages/LogoutLink';
import { routeAccessMap } from '../common/routes/router';
import { Link } from 'react-router-dom'; // 페이지 이동용


const Header = () => {

    //const isLogin = !!localStorage.getItem('accessToken');
    const {isLogin} = useAuth();
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
                        <span className="ondo">33
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
            <img src="/common/image/dp_logo.png" alt="로고" className="logo_image" />
            <span className="logo_text">함께마당</span>
          </Link>
        </h1>
        <div className="nav_box">
            <div className="depth_area">
            {/* 사이드 메뉴 렌더링 */}
            {routeAccessMap.map(menu => (
              <li key={menu.path}>
                <Link to={menu.path}>{menu.title}</Link>
                {menu.children && (
                  <ul>
                    {menu.children.map(sub => (
                      <li key={sub.path}>
                        <Link to={sub.path}>{sub.title}</Link>
                      </li>
                    ))}
                  </ul>
                )}
              </li>
            ))}
            </div>
            <div className="top_link_list">
                <div className="link_item">
                    {isLogin ? (<LogoutLink />) : (<Link to="/login" className="user_item login">로그인</Link>)}
                </div>
                {/* 로그인 했을때 숨김 */}
                <div className="link_item">
                    <Link to="/signup">회원가입</Link>
                </div>
                {/* 로그인 했을때 표출 */}
                <div className="link_item">
                    <Link to="/mypage">마이페이지</Link>
                </div>
            </div>
        </div>
      </div>
    </header>
  );
};

export default Header;