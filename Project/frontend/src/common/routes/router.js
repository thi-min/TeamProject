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

  // 게시판
   board: {
    root: { path: "/bbs", label: "게시판" },
    notice: { path: "/bbs/normalbbs", label: "공지사항" },
    qna: { path: "/bbs/questionbbs", label: "질문 게시판" },
    photo: { path: "/bbs/imagebbs", label: "포토 게시판" },
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
  },
  // 후원 경로
  fund: {
    root: { path: "/fund", label: "후원 메인" },
    fundForm: { path: "/fund/money", label: "후원금 신청" },
    goodsForm: { path: "/fund/goods", label: "후원물품 신청" },
    recurringForm: { path: "/fund/recurring", label: "정기후원 신청" },
    fundDetails: { path: "/fund/money-details", label: "후원금 상세" },
    goodsDetails: { path: "/fund/goods-details", label: "후원물품 상세" },
    recurringDetails: { path: "/fund/recurring-details", label: "정기후원 상세" },
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
      room: (id) => `/admin/chat/room/${id}`,
    },
    // 입양 신청서 경로 추가
      adopt: {
          form: (memberNum) => `/admin/adopt/request/${memberNum}`,
          label: "입양 신청서"
      },
    // 동물 관리 경로 추가
      animal: {
          register: { path: "/admin/animal/register", label: "동물 정보 기입" },
      },
  },

  // frontend/src/common/routes/router.js


  mapdata:{
    root: { path: "/map", label: "동물 놀이터" },
    info: { path: "/map/info", label: "놀이터 소개" },
    gallery: { path: "/map/gallery", label: "놀이터 둘러보기" },
    reserve: { path: "/map/reserve", label: "예약하기" },
    // '놀이터 위치' 경로를 /map으로 변경
    map: { path: "/map", label: "놀이터 위치" },
    register: { path: "/map/map/register", label: "장소 등록" },
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