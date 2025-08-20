// 📁 src/router/layoutRoutes.js

import { Route } from "react-router-dom";
import routes from "./router";

// 관리자 - 시간관리 페이지
import TimeSlotManagePage from "../../program/admin/pages/TimeSlotManagePage";

// ✅ Land 예약 플로우 페이지
import LandReserveDatePage from "../../program/reserve/land/pages/LandReserveDatePage";
import LandReserveFormPage from "../../program/reserve/land/pages/LandReserveFormPage";
import LandReserveConfirmPage from "../../program/reserve/land/pages/LandReserveConfirmPage";
import LandReserveSuccessPage from "../../program/reserve/land/pages/LandReserveSuccessPage";

// ✅ Volunteer 예약 플로우
import VolunteerReserveDatePage from "../../program/reserve/volunteer/pages/VolunteerReserveDatePage";
import VolunteerReserveFormPage from "../../program/reserve/volunteer/pages/VolunteerReserveFormPage";
import VolunteerReserveConfirmPage from "../../program/reserve/volunteer/pages/VolunteerReserveConfirmPage";
import VolunteerReserveSuccessPage from "../../program/reserve/volunteer/pages/VolunteerReserveSuccessPage";

// 📌 각 페이지 컴포넌트 import
import LoginPage from "../../program/login/pages/LoginPage"; //로그인
import LogoutLink from "../../program/login/pages/LogoutLink"; //로그아웃
import Signup from "../../program/signup/pages/SignupPage"; //회원가입
import Admin from "../../program/admin/pages/AdminPage"; //관리자 로그인시 출력
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage"; //관리자 비밀번호 변경
import FindId from "../../program/member/pages/FindIdPage"; //아이디 찾기
import FindPw from "../../program/member/pages/FindPasswordPage"; //비밀번호 찾기
import ChangePw from "../../program/member/pages/ChangePasswordPage"; //비밀번호 변경
import MyPage from "../../program/member/pages/Mypage"; //마이페이지
import MyReserveListPage from "../../program/member/pages/MyReserveListPage"; //예약내역조회
import LandReserveDetailPage from "../../program/member/pages/LandReserveDetailPage";
import VolunteerReserveDetailPage from "../../program/member/pages/VolunteerReserveDetailPage";

// 📌 routes 객체 기반으로 Route 구성
const layoutRoutes = [

  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />,
  <Route key="logout" path={routes.member.logout.path} element={<LogoutLink />} />,
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />,

   // 🔹 Land 예약: 날짜 → 폼 → 확인 → 완료
  <Route key="reserve-land-date" path={routes.reserve.land.date.path} element={<LandReserveDatePage />} />,
  <Route key="reserve-land-form" path={routes.reserve.land.form.path} element={<LandReserveFormPage />} />,
  <Route key="reserve-land-confirm" path={routes.reserve.land.confirm.path} element={<LandReserveConfirmPage />} />,
  <Route key="reserve-land-success" path={routes.reserve.land.success.path} element={<LandReserveSuccessPage />} />,

  // 🔹 Volunteer 예약: 날짜 → 폼 → 확인 → 완료
  <Route key="reserve-volunteer-date" path={routes.reserve.volunteer.date.path} element={<VolunteerReserveDatePage />} />,
  <Route key="reserve-volunteer-form" path={routes.reserve.volunteer.form.path} element={<VolunteerReserveFormPage />} />,
  <Route key="reserve-volunteer-confirm" path={routes.reserve.volunteer.confirm.path} element={<VolunteerReserveConfirmPage />} />,
  <Route key="reserve-volunteer-success" path={routes.reserve.volunteer.success.path} element={<VolunteerReserveSuccessPage />} />,

  // 🔹 관리자 페이지 - 시간대 관리
  <Route key="admin-timeslot-manage" path={routes.admin.timeSlotManage.path} element={<TimeSlotManagePage />} />,
  <Route key="admin" path={routes.admin.admin.path} element={<Admin />} />,
  <Route key="updatePw" path={routes.admin.password.path} element={<AdminPw />} />,

  //사용자
  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />, //로그인
  <Route
    key="logout"
    path={routes.member.logout.path}
    element={<LogoutLink />}
  />, //로그아웃
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />, //회원가입
  <Route key="mypage" path={routes.member.mypage.path} element={<MyPage />} />, //마이페이지
  <Route key="find-id" path={routes.member.findid.path} element={<FindId />} />, //아이디 찾기
  <Route key="find-pw" path={routes.member.findpw.path} element={<FindPw />} />, //비밀번호 찾기
  <Route
    key="update-password"
    path={routes.member.changepw.path}
    element={<ChangePw />}
  />, //비밀번호 변경

  // 마이페이지 예약내역조회
  <Route key="mypage-reserves" path={routes.member.reserves.path} element={<MyReserveListPage />} />,
  // 놀이터 예약 상세보기
  <Route key="mypage-reserve-land-detail" path={routes.member.landReserveDetail.path} element={<LandReserveDetailPage />} />,
  // 봉사 예약 상세보기
  <Route key="mypage-reserve-volunteer-detail" path={routes.member.volunteerReserveDetail.path} element={<VolunteerReserveDetailPage />} />,

  //관리자
  <Route key="admin" path={routes.admin.admin.path} element={<Admin />} />, //관리자 로그인시 출력
  <Route
    key="updatePw"
    path={routes.admin.password.path}
    element={<AdminPw />}
  />, //관리자 비밀번호 변경
];

export default layoutRoutes;
