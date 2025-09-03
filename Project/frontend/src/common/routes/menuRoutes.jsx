// 📁 src/router/menuRoutes.js
// 👉 메뉴 렌더링 전용 route 구성 (access 포함)

const menuRoutes = [
  {
    title: "센터소개",
    access: "ALL",
    children: [
      { title: "인사말", path: "/contents/1", access: "ALL" },
      { title: "연혁", path: "/contents/6", access: "ALL" },
      { title: "시설 소개", path: "/contents/2", access: "ALL" },
      { title: "오시는 길", path: "/contents/3", access: "ALL" },
    ],
  },
  {
    title: "입양 소식",
    access: "ALL",
    children: [
      { title: "센터 아이들", path: "/bbs/image", access: "ALL" },
      { title: "입양 절차 안내", path: "/contents/7", access: "ALL" },
    ],
  },
  {
    title: "동물 놀이터",
    access: "ALL",
    children: [
      { title: "놀이터 소개", path: "/contents/4", access: "ALL" },
      { title: "예약하기", path: "/reserve/land/date", access: "ALL" },
    ],
  },
  {
    title: "봉사활동",
    access: "ALL",
    children: [
      { title: "봉사 활동 안내", path: "/contents/5", access: "ALL" },
      {
        title: "봉사 신청하기",
        path: "/reserve/volunteer/date",
        access: "ALL",
      },
    ],
  },
  {
    title: "게시판",
    access: "ALL",
    children: [
      { title: "공지사항", path: "/bbs/normal", access: "ALL" },
      { title: "질문 게시판", path: "/bbs/qna", access: "ALL" },
    ],
  },
  {
    title: "후원하기",
    access: "ALL",
    children: [{ title: "후원안내", path: "/funds", access: "ALL" }],
  },
  {
    title: "관리자 메뉴",
    access: "ADMIN",
    children: [
      { title: "회원 관리", path: "/admin/members", access: "ADMIN" },
      { title: "관리자 홈", path: "/admin/dashboard", access: "ADMIN" },
    ],
  },
];

export default menuRoutes;
