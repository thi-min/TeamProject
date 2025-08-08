// 📁 src/router/menuRoutes.js
// 👉 메뉴 렌더링 전용 route 구성 (access 포함)

const menuRoutes = [
  {
    title: "센터소개",
    access: "ALL",
    children: [
      { title: "인사말", path: "/about/greeting", access: "ALL" },
      { title: "시설 소개", path: "/about/facility", access: "ALL" },
      { title: "오시는 길", path: "/about/location", access: "ALL" },
    ],
  },
  {
    title: "입양 소식",
    access: "ALL",
    children: [
      { title: "센터 아이들", path: "/about/list", access: "ALL" },
      { title: "입양 절차 안내", path: "/about/process", access: "ALL" },
      { title: "입양 후기", path: "/about/review", access: "ALL" },
    ],
  },
  {
    title: "동물 놀이터",
    access: "ALL",
    children: [
      { title: "놀이터 소개", path: "/land/info", access: "ALL" },
      { title: "놀이터 둘러보기", path: "/land/gallery", access: "ALL" },
      { title: "예약하기", path: "/land/reserve", access: "USER" },
    ],
  },
  {
    title: "게시판",
    access: "ALL",
    children: [
      { title: "공지사항", path: "/board/notice", access: "ALL" },
      { title: "질문 게시판", path: "/board/qna", access: "ALL" },
      { title: "포토 게시판", path: "/board/photo", access: "ALL" },
    ],
  },
  {
    title: "후원하기",
    access: "ALL",
    children: [
      { title: "후원안내", path: "/fund/info", access: "ALL" },
      { title: "후원금 사용내역", path: "/fund/allfund", access: "ALL" },
    ],
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
