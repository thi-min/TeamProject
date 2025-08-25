// 📁 src/router/layoutRoutes.js

import { Route } from "react-router-dom";
import routes from "./router";
import { RequireUserOnMember } from "./RouteGuards";
import { LogoutLink } from "../../program/login/pages/LogoutLink"; //로그아웃
import RequireAdmin from "../../common/components/RequireAdmin";

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

//관리자
import Admin from "../../program/admin/pages/AdminPage"; //관리자 로그인시 출력
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage"; //관리자 비밀번호 변경
import MemberList from "../../program/admin/pages/MemberListPage"; //관리자 전체 회원조회
import MemberDetail from "../../program/admin/pages/MemberDetailPage"; //관리자 회원정보 변경

//카카오
import KakaoCallbackPage from "../../program/login/pages/KakaoCallbackPage"; //카카오 로그인 콜백

// 📌 routes 객체 기반으로 Route 구성
const layoutRoutes = [
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
      <RequireUserOnMember>
        <MyPage />
      </RequireUserOnMember>
    }
  />, //마이페이지
  <Route
    key="memberdata"
    path={routes.member.memberdata.path}
    element={
      <RequireUserOnMember>
        <MemberPage />
      </RequireUserOnMember>
    }
  />, //회원정보
  <Route key="find-id" path={routes.member.findid.path} element={<FindId />} />, //아이디 찾기
  <Route key="find-pw" path={routes.member.findpw.path} element={<FindPw />} />, //비밀번호 찾기
  <Route
    key="update-password"
    path={routes.member.changepw.path}
    element={<ChangePw />}
  />, //비밀번호 변경

  //카카오
  <Route
    key="kakao-collback"
    path={routes.kakao.colback.path}
    element={<KakaoCallbackPage />}
  />, //카카오 로그인 콜백

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
