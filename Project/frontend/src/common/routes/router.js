// 🛣 모든 경로를 중앙에서 관리하는 라우터 모음

const routes = {
  // 메인/홈
  home: { path: "/", label: "홈" },

  // 센터 소개
  about: {
    root: { path: "/about/signup", label: "센터소개" },
    greeting: { path: "/about/greeting", label: "인사말" },
    facility: { path: "/about/facility", label: "시설 소개" },
    location: { path: "/about/location", label: "오시는 길" },
  },

  // 입양 소식
  adoption: {
    list: { path: "/about/list", label: "센터 아이들" },
    process: { path: "/about/process", label: "입양 절차 안내" },
    review: { path: "/about/review", label: "입양 후기" },
  },

  // 동물 놀이터
  land: {
    root: { path: "/land", label: "동물 놀이터" },
    info: { path: "/land/info", label: "놀이터 소개" },
    gallery: { path: "/land/gallery", label: "놀이터 둘러보기" },
    reserve: { path: "/land/reserve", label: "예약하기" }, // USER 전용
  },

  // 게시판
  board: {
    root: { path: "/board", label: "게시판" },
    notice: { path: "/board/notice", label: "공지사항" },
    qna: { path: "/board/qna", label: "질문 게시판" },
    photo: { path: "/board/photo", label: "포토 게시판" },
  },

  // 회원 관련
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
    reserves: { path: "/member/mypage/reserves", label: "예약 내역 조회" },
    landReserveDetail: { path: "/member/mypage/reserves/land/:reserveCode", label: "놀이터 예약 상세보기" },
    volunteerReserveDetail: { path: "/member/mypage/reserves/volunteer/:reserveCode", label: "봉사 예약 상세보기"},
    memberdata: { path: "/member/mypage/memberdata", label: "회원정보" },
    update: {
      path: "/member/mypage/memberdata/update",
      label: "회원정보 수정",
    },
    delete: { path: "/member/mypage/update/delete", label: "회원 탈퇴" },

  },

  // 관리자
  admin: {
    admin: { path: "/admin", label: "관리자 페이지" },
    password: { path: "/admin/updatePw", label: "관리자 비밀번호 변경" },
    membersList: { path: "/admin/membersList", label: "회원 목록" },
    memberDetail: {
      path: "/admin/membersList/:memberNum",
      build: (id) => `/admin/membersList/${id}`,
      label: "회원 상세",
    },
    timeSlotManage: { path: "/admin/timeslots", label: "시간대 관리" },
    closedDayManage: { path: "/admin/closedday", label: "휴무일 관리"}, 
    landReserveManage: { path: "/admin/reserve/land", label: "놀이터 예약 관리" },
    volunteerReserveManage: { path: "/admin/reserve/volunteer", label: "봉사 예약 관리" },
    landReserveDetail: { path: "/admin/reserve/land/:reserveCode", label: "놀이터 예약 상세보기" },
    volunteerReserveDetail: { path: "/admin/reserve/volunteer/:reserveCode", label: "봉사 예약 상세보기" }, 
    bannerManage: { path: "/admin/banner", label: "배너 관리"},
    bannerCreate: { path: "/admin/banner/create", label: "배너 생성"},
    bannerDetail: { path: "/admin/banner/:bannerId", label: "배너 수정 및 삭제"}

  },

  //예약
  reserve: {
    root:   { path: "/reserve", label: "예약하기" },
    land: {
      date:    { path: "/reserve/land/date",    label: "날짜/시간 선택" }, 
      form:    { path: "/reserve/land/form",    label: "놀이터 예약" },
      confirm: { path: "/reserve/land/confirm", label: "예약 확인" },
      success: { path: "/reserve/land/success", label: "예약 완료" },
    },
    volunteer: {
      date:    { path: "/reserve/volunteer/date",    label: "날짜/시간 선택" },
      form:    { path: "/reserve/volunteer/form",    label: "봉사 신청" },
      confirm: { path: "/reserve/volunteer/confirm", label: "신청 확인" },
      success: { path: "/reserve/volunteer/success", label: "신청 완료" },
    },
  },
  // 봉사활동
  volunteer: {
    list: { path: "/volunteer/list", label: "봉사 목록" },
    detail: (id) => `/volunteer/${id}`,
    apply: { path: "/volunteer/apply", label: "봉사 신청" },
  },

  // 404 등 기타
  common: {
    notFound: { path: "*", label: "페이지를 찾을 수 없습니다" },
  },
};

export default routes;
