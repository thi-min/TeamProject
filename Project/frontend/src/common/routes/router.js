// src/common/routes/router.js
// 모든 경로를 중앙에서 관리하는 라우터 모음

const routes = {
  home: { path: "/", label: "홈" },

  about: {
    root: { path: "/about/signup", label: "센터소개" },
    greeting: { path: "/about/greeting", label: "인사말" },
    facility: { path: "/about/facility", label: "시설 소개" },
    location: { path: "/about/location", label: "오시는 길" },
  },

  adoption: {
    list: { path: "/about/list", label: "센터 아이들" },
    process: { path: "/about/process", label: "입양 절차 안내" },
    review: { path: "/about/review", label: "입양 후기" },
  },

  land: {
    root: { path: "/land", label: "동물 놀이터" },
    info: { path: "/land/info", label: "놀이터 소개" },
    gallery: { path: "/land/gallery", label: "놀이터 둘러보기" },
    reserve: { path: "/land/reserve", label: "예약하기" },
  },

  board: {
    root: { path: "/board", label: "게시판" },
    notice: { path: "/board/notice", label: "공지사항" },
    qna: { path: "/board/qna", label: "질문 게시판" },
    photo: { path: "/board/photo", label: "포토 게시판" },
  },

  member: {
    signup: { path: "/signup", label: "회원가입" },
    login: { path: "/login", label: "로그인" },
    logout: { path: "/logout", label: "로그아웃" },
    mypage: { path: "/mypage", label: "마이페이지" },
    update: { path: "/mypage/update", label: "회원정보 수정" },
    delete: { path: "/mypage/update/delete", label: "회원 탈퇴" },

    // 1:1 채팅 관련 경로 추가
    chatList: { path: "/member/chat", label: "1:1 채팅 목록" },
    chatPage: { path: "/member/chat/:id", label: "1:1 채팅" },
  },

  admin: {
    login: { path: "/admin/login", label: "관리자 로그인" },
    dashboard: { path: "/admin/dashboard", label: "관리자 홈" },
    members: { path: "/admin/members", label: "회원 관리" },
    memberDetail: (id) => `/admin/members/${id}`,
  },

  reservation: {
    list: { path: "/reservation/list", label: "예약 목록" },
    create: { path: "/reservation/create", label: "예약 생성" },
    detail: (id) => `/reservation/${id}`,
  },

  volunteer: {
    list: { path: "/volunteer/list", label: "봉사 목록" },
    detail: (id) => `/volunteer/${id}`,
    apply: { path: "/volunteer/apply", label: "봉사 신청" },
  },

  common: {
    notFound: { path: "*", label: "페이지를 찾을 수 없습니다" },
  },
};

export default routes;