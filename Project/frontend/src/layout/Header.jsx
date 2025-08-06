import React from 'react';

import { Link } from 'react-router-dom'; // 페이지 이동용

const Header = () => {
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
            <nav id="depth">
                <ul className="depth_list clearfix">
                    <li className="depth1_item">
                    <Link to="#">센터소개</Link>
                    <ul className="depth2_list">
                        <li className="depth2_item"><Link to="#">인사말</Link></li>
                        <li className="depth2_item"><Link to="#">시설 소개</Link></li>
                        <li className="depth2_item"><Link to="#">오시는 길</Link></li>
                    </ul>
                    </li>
                    <li className="depth1_item">
                    <Link to="#">입양 소식</Link>
                    <ul className="depth2_list">
                        <li className="depth2_item"><Link to="#">센터 아이들</Link></li>
                        <li className="depth2_item"><Link to="#">입양 절차 안내</Link></li>
                        <li className="depth2_item"><Link to="#">입양 후기</Link></li>
                    </ul>
                    </li>
                    <li className="depth1_item">
                    <Link to="#">동물 놀이터</Link>
                    <ul className="depth2_list">
                        <li className="depth2_item"><Link to="#">놀이터 소개</Link></li>
                        <li className="depth2_item"><Link to="#">놀이터 둘러보기</Link></li>
                        <li className="depth2_item"><Link to="#">예약하기</Link></li>
                    </ul>
                    </li>
                    <li className="depth1_item">
                    <Link to="#">봉사 프로그램</Link>
                    <ul className="depth2_list">
                        <li className="depth2_item"><Link to="#">봉사 소개</Link></li>
                        <li className="depth2_item"><Link to="#">봉사 신청하기</Link></li>
                    </ul>
                    </li>
                    <li className="depth1_item">
                    <Link to="#">게시판</Link>
                    <ul className="depth2_list">
                        <li className="depth2_item"><Link to="#">공지사항</Link></li>
                        <li className="depth2_item"><Link to="#">질문 게시판</Link></li>
                        <li className="depth2_item"><Link to="#">포토 게시판</Link></li>
                    </ul>
                    </li>
                    <li className="depth1_item">
                    <Link to="#">후원하기</Link>
                    <ul className="depth2_list">
                        <li className="depth2_item"><Link to="#">후원안내</Link></li>
                        <li className="depth2_item"><Link to="#">후원금 사용내역(통계)</Link></li>
                    </ul>
                    </li>
                </ul>
            </nav>
            </div>
            <div className="top_link_list">
                {/* 로그인 안되어 있을때 표츌 */}
                <div className="link_item">
                    <Link to="/login" className="user_item login">로그인</Link>
                </div>
                {/* 로그인 했을때 버튼 표출 */}
                <div className="link_item">
                    <Link to="/logout" className="user_item logout">로그아웃</Link>
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
