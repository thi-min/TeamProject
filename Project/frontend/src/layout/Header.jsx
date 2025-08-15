import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../common/context/AuthContext';
import NavLinks from '../common/routes/NavLinks';

console.log('[Header] sees AuthContext id =', window.__AUTH_CTX_ID__);

const Header = () => {
  // ✅ AuthContext가 제공하는 실제 키: isLogin, userRole, login, logout
    const { isLogin, userId, role, logout } = useAuth();
    console.log('isLogin:', isLogin, 'userId:', userId, 'role:', role);

    return (
    <header id="header">
        {/* 헤더 상단 로그인/회원가입 링크 */}
        <div className="header_top_box">
            <div className="admin_box">
                <Link to="/admin/login" className="admin_link">관리자</Link>
            </div>
            <div className="info_box">
                {/* 날짜 */}
                <div className="today_box">
                    <span className="today">2025년 7월 9일</span>
                </div>
                <div className="weather_inner">
                    <span className="icon"></span>
                    <span className="temperature">
                        <span className="">33
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
            <img src="/common/images/dp_logo.png" alt="로고" className="logo_image" />
            <span className="logo_text">함께마당</span>
          </Link>
        </h1>
        <div className="nav_box">
            <NavLinks />
            <div className="top_link_list">
                <div className="link_item">
                  {isLogin ? (
                      <>
                        <button type="button" className="user_item logout" onClick={logout}>로그아웃</button>
                      </>
                    ) : (
                      <Link to="/login" className="user_item login">로그인</Link>
                    )
                  }
                </div>
                {/* 로그인 했을때 숨김 */}
                {!isLogin && (
                <div className="link_item">
                    <Link to="/signup">회원가입</Link>
                </div>
                )}
                {/* 로그인 했을때 표출 */}
                {isLogin && (
                  <div className="link_item">
                    <Link to="/mypage">마이페이지</Link>
                </div>
                )}
            </div>
        </div>
      </div>
    </header>
    
  );
};

export default Header;
