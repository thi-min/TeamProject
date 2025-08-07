import React from 'react';
import {isLogin, useAuth } from '../common/context/AuthContext';
import LogoutLink from '../program/login/pages/LogoutLink';
import { Link } from 'react-router-dom'; // 페이지 이동용

export const routeAccessMap = [
  // 공통 (로그인 여부와 무관한 공개 메뉴)
  {
    title: '홈',
    path: '/',
    access: 'ALL', // 누구나 접근 가능
  },
  {
    title: '공지사항',
    path: '/board/notice',
    access: 'ALL',
  },

  // 회원(Member)만 접근 가능
  {
    title: '마이페이지',
    path: '/mypage',
    access: 'USER',
  },
  {
    title: '놀이터 예약',
    path: '/playground/reserve',
    access: 'USER',
  },
  {
    title: '봉사 신청',
    path: '/volunteer/apply',
    access: 'USER',
  },

  // 관리자만 접근 가능
  {
    title: '회원 관리',
    path: '/admin/members',
    access: 'ADMIN',
  },
  {
    title: '게시글 관리',
    path: '/admin/board',
    access: 'ADMIN',
  },
  {
    title: '예약 현황',
    path: '/admin/reservations',
    access: 'ADMIN',
  },
];


const Header = () => {

    const navData = [
        {
            title: '센터소개',
            path: '/about',
            links: [
            { name: '인사말', path: '/about/greeting' },
            { name: '시설 소개', path: '/about/facility' },
            { name: '오시는 길', path: '/about/location' },
            ],
        },
        {
            title: '입양 소식',
            path: '/about',
            links: [
            { name: '센터 아이들', path: '' },
            { name: '입양 절차 안내', path: '/about/process' },
            { name: '입양 후기', path: '/about/review' },
            ],
        },
        {
            title: '동물 놀이터',
            path: '/land',
            links: [
            { name: '놀이터 소개', path: '/land/info' },
            { name: '놀이터 둘러보기', path: '/land/gallery' },
            { name: '예약하기', path: '/land/reserve' },
            ],
        },
        {
            title: '봉사 프로그램',
            path: '/volunteer',
            links: [
            { name: '봉사 소개', path: '/volunteer/info' },
            { name: '봉사 신청하기', path: '/volunteer/reserve' },
            ],
        },
        {
            title: '게시판',
            path: '/board',
            links: [
            { name: '공지사항', path: '/board/notice' },
            { name: '질문 게시판', path: '/board/qna' },
            { name: '포토 게시판', path: '/board/photo' },
            ],
        },
        {
            title: '후원하기',
            path: '/fund',
            links: [
            { name: '후원안내', path: '/fund/info' },
            { name: '후원금 사용내역', path: '/fund/statistics' },
            ],
        },
    ];

    //const isLogin = !!localStorage.getItem('accessToken');
    const {isLogin} = useAuth();
    const {userRole} = useAuth();
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
            {routeAccessMap
                .filter(route =>
                route.access === 'ALL' ||
                route.access === userRole)
                .map((route, index) => (
                <li key={index}>
                    <Link to={route.path}>{route.title}</Link>
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