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

    findid: { path: "/find-id", label: "아이디 찾기" },
    findpw: { path: "/find-pw", label: "비밀번호 찾기" },
    changepw: { path: "/update-password", label: "비밀번호 변경" },

    mypage: { path: "/mypage", label: "마이페이지" },
    update: { path: "/mypage/update", label: "회원정보 수정" },
    delete: { path: "/mypage/update/delete", label: "회원 탈퇴" },
    adopt: {
            list: { path: "/member/adopt/list", label: "나의 입양 신청서" },
            detail: (id) => `/member/adopt/detail/${id}`,
        }
  },
  // 후원 경로
  fund: {
    root: { path: "/fund", label: "후원 메인" },
    fundForm: { path: "/fund/donation", label: "후원금 신청" },
    goodsForm: { path: "/fund/goods", label: "후원물품 신청" },
    regularForm: { path: "/fund/regular", label: "정기후원 신청" },
    fundDetails: { path: "/fund/donation-details", label: "후원금 상세" },
    goodsDetails: { path: "/fund/goods-details", label: "후원물품 상세" },
    regularDetails: { path: "/fund/regular-details", label: "정기후원 상세" },
  },

  admin: {
    admin: { path: "/admin", label: "관리자 페이지" },
    password: { path: "/admin/updatePw", label: "관리자 비밀번호 변경" },
    dashboard: { path: "/admin/dashboard", label: "관리자 홈" },
    members: { path: "/admin/members", label: "회원 관리" },
    memberDetail: (id) => `/admin/members/${id}`,
    // 1:1 채팅 경로 추가
    chat: {
      list: { path: "/admin/chat/list", label: "채팅 목록" },
      room: (id) => `/admin/chat/room/:ChatRoomId`,
    },
    
    // 입양 신청서 경로 추가
      adopt: {
            list: { path: "/admin/adopt/list", label: "입양 신청서 관리" },
            detail: (id) => `/admin/adopt/detail/${id}`,
            resist: { path: "/admin/adopt/resist", label: "입양 신청서 작성" },
            update: (id) => `/admin/adopt/update/${id}`,
        },
    // 동물 관리 경로 추가
      animal: {
            list: { path: "/admin/animal/list", label: "동물 정보 관리" },
            detail: (id) => `/admin/animal/detail/${id}`,
            resist: { path: "/admin/animal/resist", label: "동물 정보 등록" },
            update: (id) => `/admin/animal/update/${id}`,
        
      },
  },
  // 맵 관련 경로 추가
  map: {
      root: { path: "/map", label: "지도 검색" },
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