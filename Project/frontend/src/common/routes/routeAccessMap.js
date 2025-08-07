// 권한에 따라 접근 가능한 메뉴 경로 정의

export const routeAccessMap = [
  // 누구나 접근 가능한 페이지
  {
    title: '홈',
    path: '/',
    access: 'ALL',
  },
  {
    title: '공지사항',
    path: '/board/notice',
    access: 'ALL',
  },

  // 회원만 접근 가능한 페이지
  {
    title: '마이페이지',
    path: '/mypage',
    access: 'USER',
  },
  {
    title: '놀이터 예약',
    path: '/land/reserve',
    access: 'USER',
  },
  {
    title: '봉사 신청',
    path: '/volunteer/apply',
    access: 'USER',
  },

  // 관리자만 접근 가능한 페이지
  {
    title: '회원 관리',
    path: '/admin/members',
    access: 'ADMIN',
  },
  {
    title: '예약 현황',
    path: '/admin/reservations',
    access: 'ADMIN',
  },
];
