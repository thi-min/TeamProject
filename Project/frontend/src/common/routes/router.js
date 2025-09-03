// 🛣 모든 경로를 중앙에서 관리하는 라우터 모음

const routes = {
  // 메인/홈
  main: { path: "/", label: "메인페이지" },

  // ==============================
  // 센터 소개 관련
  // ==============================
  about: {
    root: { path: "/about", label: "센터소개" },
    greeting: { path: "/about/greeting", label: "인사말" },
    facility: { path: "/about/facility", label: "시설 소개" },
    location: { path: "/about/location", label: "오시는 길" },
  },

  // ==============================
  // 입양 관련
  // ==============================
  adoption: {
    list: { path: "/adoption/list", label: "센터 아이들" },
    review: { path: "/adoption/review", label: "입양 후기" },
  },

  // ==============================
  // 동물 놀이터 관련
  // ==============================
  land: {
    root: { path: "/land", label: "동물 놀이터" },
    info: { path: "/land/info", label: "놀이터 소개" },
  },

  // ==============================
  // 게시판 관련
  // ==============================
  board: {
    root: { path: "/bbs", label: "게시판" },
    notice: { path: "/bbs/normalbbs", label: "공지사항" },
    qna: { path: "/bbs/questionbbs", label: "질문 게시판" },
    photo: { path: "/bbs/imagebbs", label: "포토 게시판" },
  },

  // ==============================
  // 회원 관련
  // ==============================
  member: {
    join: { path: "/join", label: "회원 약관" },
    phone: { path: "/phonetest", label: "휴대폰 인증" },
    signup: { path: "/join/signup", label: "회원가입" },
    login: { path: "/login", label: "로그인" },
    logout: { path: "/logout", label: "로그아웃" },
    findid: { path: "/find-id", label: "아이디 찾기" },
    findpw: { path: "/find-pw", label: "비밀번호 찾기" },

    changepw: { path: "/member/update-password", label: "비밀번호 변경" },
    mypage: { path: "/member/mypage", label: "마이페이지" },
    memberdata: { path: "/member/mypage/memberdata", label: "회원정보" },
    update: {
      path: "/member/mypage/memberdata/update",
      label: "회원정보 수정",
    },
    delete: { path: "/member/mypage/update/delete", label: "회원 탈퇴" },

    reserves: { path: "/member/mypage/reserves", label: "예약 내역 조회" },
    landReserveDetail: {
      path: "/member/mypage/reserves/land/:reserveCode",
      label: "놀이터 예약 상세보기",
    },
    volunteerReserveDetail: {
      path: "/member/mypage/reserves/volunteer/:reserveCode",
      label: "봉사 예약 상세보기",
    },

    // 개인 입양 신청서
    adopt: {
      list: { path: "/member/adopt/list", label: "나의 입양 신청서" },
      detail: (id) => `/member/adopt/detail/${id}`,
    },

    // 나의 후원
    fund: {
      list: { path: "/member/funds/list", label: "나의 후원 목록" },
      detail: (id) => `/member/funds/${id}`,
    },
  },

  //카카오 관련
  kakao: {
    callback: { path: "/oauth/kakao/callback", label: "카카오 콜백" },
  },

  // ==============================
  // 후원(Fund) 관련
  // ==============================
  fund: {
    root: { path: "/funds", label: "후원 메인" },
    fundForm: { path: "/funds/donation", label: "후원금 신청" },
    goodsForm: { path: "/funds/goods", label: "후원물품 신청" },
    regularForm: { path: "/funds/regular", label: "정기후원 신청" },
    fundDetails: { path: "/funds/donation-details", label: "후원금 상세" },
    goodsDetails: { path: "/funds/goods-details", label: "후원물품 상세" },
    regularDetails: { path: "/funds/regular-details", label: "정기후원 상세" },
  },

  // ==============================
  // 관리자 관련
  // ==============================
  admin: {
    admin: { path: "/admin", label: "관리자 페이지" },
    password: { path: "/admin/updatePw", label: "관리자 비밀번호 변경" },

    dashboard: { path: "/admin/dashboard", label: "관리자 홈" },
    members: { path: "/admin/members", label: "회원 관리" },
    memberDetail: (id) => `/admin/members/${id}`,

    // 1:1 채팅
    chat: {
      path: "/admin/chat/list",
      label: "채팅 관리",
    },
    chatDetail: (id) => `/admin/chat/detail/${id}`,

    // 입양 신청서 관리
    adopt: {
      list: { path: "/admin/adopt/list", label: "입양 신청서 관리" },
      detail: (id) => `/admin/adopt/detail/${id}`,
      regist: { path: "/admin/adopt/regist", label: "입양 신청서 작성" },
      update: (id) => `/admin/adopt/update/${id}`,
    },

    membersList: { path: "/admin/membersList", label: "회원 목록" },
    memberDetail: {
      path: "/admin/membersList/:memberNum",
      build: (id) => `/admin/membersList/${id}`,
      label: "회원 상세",
    },

    // 동물 관리
    animal: {
      list: { path: "/admin/animal/list", label: "동물 정보 관리" },
      detail: (id) => `/admin/animal/detail/${id}`,
      regist: { path: "/admin/animal/regist", label: "동물 정보 등록" },
      update: (id) => `/admin/animal/update/${id}`,
    },

    fund: {
      list: { path: "/admin/funds/list", label: " 후원 정보 관리" },
      detail: (id) => `/admin/funds/detail/${id}`,
    },

    // 예약 관리
    timeSlotManage: { path: "/admin/timeslots", label: "시간대 관리" },
    closedDayManage: { path: "/admin/closedday", label: "휴무일 관리" },
    landReserveManage: {
      path: "/admin/reserve/land",
      label: "놀이터 예약 관리",
    },
    volunteerReserveManage: {
      path: "/admin/reserve/volunteer",
      label: "봉사 예약 관리",
    },
    landReserveDetail: {
      path: "/admin/reserve/land/:reserveCode",
      label: "놀이터 예약 상세보기",
    },

    volunteerReserveDetail: {
      path: "/admin/reserve/volunteer/:reserveCode",
      label: "봉사 예약 상세보기",
    },
    bannerManage: { path: "/admin/banner", label: "배너 관리" },
    bannerCreate: { path: "/admin/banner/create", label: "배너 생성" },
    bannerDetail: {
      path: "/admin/banner/:bannerId",
      label: "배너 수정 및 삭제",
    },
  },

  map: {
    root: { path: "/map", label: "지도 검색" },
  },

  //예약

  reserve: {
    root: { path: "/reserve", label: "예약하기" },
    land: {
      date: { path: "/reserve/land/date", label: "날짜/시간 선택" },
      form: { path: "/reserve/land/form", label: "놀이터 예약" },
      confirm: { path: "/reserve/land/confirm", label: "예약 확인" },
      success: { path: "/reserve/land/success", label: "예약 완료" },
    },
    volunteer: {
      date: { path: "/reserve/volunteer/date", label: "날짜/시간 선택" },
      form: { path: "/reserve/volunteer/form", label: "봉사 신청" },
      confirm: { path: "/reserve/volunteer/confirm", label: "신청 확인" },
      success: { path: "/reserve/volunteer/success", label: "신청 완료" },
    },
  },

  volunteer: {
    list: { path: "/volunteer/list", label: "봉사 목록" },
    info: { path: "/volunteer/info", label: "봉사 소개" },
    detail: (id) => `/volunteer/${id}`,
  },

  // ==============================
  // 공통
  // ==============================
  common: {
    notFound: { path: "*", label: "페이지를 찾을 수 없습니다" },
  },
};

export default routes;
