// 📁 src/router/layoutRoutes.js
import { Route } from "react-router-dom";
import routes from "./router";
import { LogoutLink } from "../../program/login/pages/LogoutLink"; //로그아웃

//권한 부여
import { RequireMember } from "../components/RequireUser"; //사용자
import RequireAdmin from "../components/RequireAdmin"; //관리자

//메인페이지
import Main from "../../main/pages/MainPage";

// 관리자 - 시간관리 + 휴무일 관리 + 배너관리
import TimeSlotManagePage from "../../program/admin/pages/TimeSlotManagePage";
import ClosedDayManagePage from "../../program/admin/pages/ClosedDayManagePage";
import BannerListPage from "../../program/admin/pages/BannerListPage";
import BannerCreatePage from "../../program/admin/pages/BannerCreatePage";
import BannerDetailPage from "../../program/admin/pages/BannerDetailPage";

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
//회원
import LoginPage from "../../program/login/pages/LoginPage"; //로그인
import Join from "../../program/signup/pages/JoinPage"; //회원가입 약관
import PhoneVerifyPage from "../../program/signup/pages/PhoneVerifyPage"; //휴대폰 인증 페이지
import Signup from "../../program/signup/pages/SignupPage"; //회원가입
import FindId from "../../program/member/pages/FindIdPage"; //아이디 찾기
import FindPw from "../../program/member/pages/FindPasswordPage"; //비밀번호 찾기
import ChangePw from "../../program/member/pages/UpdatePasswordPage"; //비밀번호 변경
import MyPage from "../../program/member/pages/Mypage"; //마이페이지
import MemberPage from "../../program/member/pages/MemberData"; //회원정보
import MyReserveListPage from "../../program/member/pages/MyReserveListPage"; //예약내역조회
import LandReserveDetailPage from "../../program/member/pages/LandReserveDetailPage"; //놀이터예약 상세보기
import VolunteerReserveDetailPage from "../../program/member/pages/VolunteerReserveDetailPage"; //봉사예약 상세보기
import AdminLandReservePage from "../../program/admin/pages/AdminLandReservePage"; //놀이터예약 관리
import AdminVolunteerReservePage from "../../program/admin/pages/AdminVolunteerReservePage";  //봉사예약 관리
import AdminLandReserveDetailPage from "../../program/admin/pages/AdminLandReserveDetailPage"; //관리자 놀이터예약 상태 변경
import AdminVolunteerReserveDetailPage from "../../program/admin/pages/AdminVolunteerReserveDetailPage"; //관리자 봉사예약 상태 변경

//관리자
import Admin from "../../program/admin/pages/AdminPage"; //관리자 로그인시 출력
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage"; //관리자 비밀번호 변경
import MemberList from "../../program/admin/pages/MemberListPage"; //관리자 전체 회원조회
import MemberDetail from "../../program/admin/pages/MemberDetailPage"; //관리자 회원정보 변경

//카카오
import KakaoCallbackPage from "../../program/login/pages/KakaoCallbackPage"; //카카오 로그인 콜백

// 📌 routes 객체 기반으로 Route 구성
const layoutRoutes = [

  //메인페이지
  <Route key="main" path={routes.main.path} element={<Main />} />, //메인

   // 🔹 Land 예약: 날짜 → 폼 → 확인 → 완료
  <Route key="reserve-land-date" path={routes.reserve.land.date.path} element={<RequireMember><LandReserveDatePage /></RequireMember>} />,
  <Route key="reserve-land-form" path={routes.reserve.land.form.path} element={<RequireMember><LandReserveFormPage /></RequireMember>} />,
  <Route key="reserve-land-confirm" path={routes.reserve.land.confirm.path} element={<RequireMember><LandReserveConfirmPage /></RequireMember>} />,
  <Route key="reserve-land-success" path={routes.reserve.land.success.path} element={<RequireMember><LandReserveSuccessPage /></RequireMember>} />,

  // 🔹 Volunteer 예약: 날짜 → 폼 → 확인 → 완료
  <Route key="reserve-volunteer-date" path={routes.reserve.volunteer.date.path} element={<RequireMember><VolunteerReserveDatePage /></RequireMember>} />,
  <Route key="reserve-volunteer-form" path={routes.reserve.volunteer.form.path} element={<RequireMember><VolunteerReserveFormPage /></RequireMember>} />,
  <Route key="reserve-volunteer-confirm" path={routes.reserve.volunteer.confirm.path} element={<RequireMember><VolunteerReserveConfirmPage /></RequireMember>} />,
  <Route key="reserve-volunteer-success" path={routes.reserve.volunteer.success.path} element={<RequireMember><VolunteerReserveSuccessPage /></RequireMember>} />,

  // 🔹 관리자 페이지 - 
  <Route key="admin-timeslot-manage" path={routes.admin.timeSlotManage.path} element={<RequireAdmin><TimeSlotManagePage /></RequireAdmin>} />,
  <Route key="admin" path={routes.admin.admin.path} element={<RequireAdmin><Admin /></RequireAdmin>} />,
  <Route key="updatePw" path={routes.admin.password.path} element={<RequireAdmin><AdminPw /></RequireAdmin>} />,
  <Route key="admin-closedday-manage" path={routes.admin.closedDayManage.path} element={<RequireAdmin><ClosedDayManagePage /></RequireAdmin>} />,
  <Route key="admin-land-manage" path={routes.admin.landReserveManage.path} element={<RequireAdmin><AdminLandReservePage /></RequireAdmin>} />,
  <Route key="admin-volunteer-manage" path={routes.admin.volunteerReserveManage.path} element={<RequireAdmin><AdminVolunteerReservePage /></RequireAdmin>} />,
  <Route key="admin-land-detail" path={routes.admin.landReserveDetail.path} element={<RequireAdmin><AdminLandReserveDetailPage /></RequireAdmin>} />,
  <Route key="admin-volunteer-detail" path={routes.admin.volunteerReserveDetail.path} element={<RequireAdmin><AdminVolunteerReserveDetailPage /></RequireAdmin>} />,
  <Route key="admin-banner-manage" path={routes.admin.bannerManage.path} element={<RequireAdmin><BannerListPage /></RequireAdmin >} />,
  <Route key="admin-banner-create" path={routes.admin.bannerCreate.path} element={<RequireAdmin><BannerCreatePage /></RequireAdmin >} />,
  <Route key="admin-banner-detail" path={routes.admin.bannerDetail.path} element={<RequireAdmin><BannerDetailPage /></RequireAdmin >} />,

  //사용자
  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />, //로그인
  <Route
    key="logout"
    path={routes.member.logout.path}
    element={<LogoutLink />}
  />, //로그아웃
  <Route key="join" path={routes.member.join.path} element={<Join />} />, //회원 약관
  <Route
    key="phonetest"
    path={routes.member.phone.path}
    element={<PhoneVerifyPage />}
  />, //회원 약관
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />, //회원가입
  <Route
    key="mypage"
    path={routes.member.mypage.path}
    element={
      <RequireMember>
        <MyPage />
      </RequireMember>
    }
  />, //마이페이지
  <Route
    key="memberdata"
    path={routes.member.memberdata.path}
    element={
      <RequireMember>
        <MemberPage />
      </RequireMember>
    }
  />, //회원정보
  <Route key="find-id" path={routes.member.findid.path} element={<FindId />} />, //아이디 찾기
  <Route key="find-pw" path={routes.member.findpw.path} element={<FindPw />} />, //비밀번호 찾기
  <Route
    key="update-password"
    path={routes.member.changepw.path}
    element={
      <RequireMember>
        <ChangePw />
      </RequireMember>
    }
  />, //비밀번호 변경

  //카카오
  <Route
    key="kakao-callback"
    path={routes.kakao.callback.path}
    element={<KakaoCallbackPage />}
  />, //콜백 연결

  // 마이페이지 예약내역조회
  <Route key="mypage-reserves" path={routes.member.reserves.path} element={<RequireMember><MyReserveListPage /></RequireMember>} />,
  // 놀이터 예약 상세보기
  <Route key="mypage-reserve-land-detail" path={routes.member.landReserveDetail.path} element={<RequireMember><LandReserveDetailPage /></RequireMember>} />,
  // 봉사 예약 상세보기
  <Route key="mypage-reserve-volunteer-detail" path={routes.member.volunteerReserveDetail.path} element={<RequireMember><VolunteerReserveDetailPage /></RequireMember>} />,

  //관리자
  <Route
    key="admin"
    path={routes.admin.admin.path}
    element={
      <RequireAdmin>
        <Admin />
      </RequireAdmin>
    }
  />, //관리자 로그인시 출력
  <Route
    key="updatePw"
    path={routes.admin.password.path}
    element={
      <RequireAdmin>
        <AdminPw />
      </RequireAdmin>
    }
  />, //관리자 비밀번호 변경
  <Route
    key="memberList"
    path={routes.admin.membersList.path}
    element={
      <RequireAdmin>
        <MemberList />
      </RequireAdmin>
    }
  />, //관리자 회원목록
  <Route
    key="memberDetail"
    path={routes.admin.memberDetail.path}
    element={
      <RequireAdmin>
        <MemberDetail />
      </RequireAdmin>
    }
  />, //관리자 회원상세보기
];

export default layoutRoutes;
