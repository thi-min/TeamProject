
export const routeAccessMap = [
  {
    title: '센터소개',
    path: '/about',
    access: 'ALL',
    children: [
      { title: '인사말', path: '/about/greeting', access: 'ALL' },
      { title: '시설 소개', path: '/about/facility', access: 'ALL' },
      { title: '오시는 길', path: '/about/location', access: 'ALL' },
    ],
  },
  {
    title: '입양 소식',
    path: '/about',
    access: 'ALL',
    children: [
      { title: '센터 아이들', path: '/about/list', access: 'ALL' },
      { title: '입양 절차 안내', path: '/about/process', access: 'ALL' },
      { title: '입양 후기', path: '/about/review', access: 'ALL' },
    ],
  },
  {
    title: '동물 놀이터',
    path: '/land',
    access: 'ALL',
    children: [
      { title: '놀이터 소개', path: '/land/info', access: 'ALL' },
      { title: '놀이터 둘러보기', path: '/land/gallery', access: 'ALL' },
      { title: '예약하기', path: '/land/reserve', access: 'USER' },
    ],
  },
  {
    title: '게시판',
    path: '/board',
    access: 'ALL',
    children: [
      { title: '공지사항', path: '/board/notice', access: 'ALL' },
      { title: '질문 게시판', path: '/board/qna', access: 'ALL' },
      { title: '포토 게시판', path: '/board/photo', access: 'ALL' },
    ],
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
];
