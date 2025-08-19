// 📁 src/router/layoutRoutes.js

import { Route } from "react-router-dom";
import routes from "./router";

// 📌 각 페이지 컴포넌트 import
import LoginPage from "../../program/login/pages/LoginPage"; //로그인
import LogoutLink from "../../program/login/pages/LogoutLink"; //로그아웃
import Signup from "../../program/signup/pages/SignupPage"; //회원가입
import Admin from "../../program/admin/pages/AdminPage"; //관리자 로그인시 출력
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage"; //관리자 비밀번호 변경
import FindId from "../../program/member/pages/FindIdPage"; //아이디 찾기
import FindPw from "../../program/member/pages/FindPasswordPage"; //비밀번호 찾기
import ChangePw from "../../program/member/pages/ChangePasswordPage"; //비밀번호 변경
import Mypage from "../../program/member/pages/Mypage"; //마이페이지


// FundPage.js에서 기본 내보내기가 없으므로, FundMainPage를 이름으로 불러옵니다.
// 이제 FundPage.js에서 내보내는 모든 컴포넌트를 불러옵니다.
import {
  FundApplicationDetails,
  FundApplicationForm,
  FundMainPage,
  GoodsApplicationDetails,
  GoodsApplicationForm,
  RecurringApplicationDetails,
  RecurringApplicationForm
} from "../../program/fund/services/FundPage";

// 1:1 채팅 컴포넌트 import
import ChatList from "../../program/chat/services/ChatList";
import ChatRoom from "../../program/chat/services/ChatRoom";

// 입양 신청서 컴포넌트 import
import AdoptApplicationForm from "../../program/adopt/services/AdoptApplicationForm";

// AnimalForm 컴포넌트 import
import AnimalForm from "../../program/animal/services/AnimalForm";

// mapdata 컴포넌트
import MapForm from "../../program/mapdata/services/MapForm";
import MapPage from "../../program/mapdata/services/MapPage";

// 📌 routes 객체 기반으로 Route 구성
const layoutRoutes = [

  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />,
  <Route key="logout" path={routes.member.logout.path} element={<LogoutLink />} />,
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />,
  


  // 후원 route
  // 각 후원 페이지를 별도의 경로로 라우팅합니다.
  <Route key="fundMainPage" path={routes.fund.root.path} element={<FundMainPage />} />,
  <Route key="fundForm" path={routes.fund.fundForm.path} element={<FundApplicationForm />} />,
  <Route key="goodsForm" path={routes.fund.goodsForm.path} element={<GoodsApplicationForm />} />,
  <Route key="recurringForm" path={routes.fund.recurringForm.path} element={<RecurringApplicationForm />} />,
  <Route key="fundDetails" path={routes.fund.fundDetails.path} element={<FundApplicationDetails />} />,
  <Route key="goodsDetails" path={routes.fund.goodsDetails.path} element={<GoodsApplicationDetails />} />,
  <Route key="recurringDetails" path={routes.fund.recurringDetails.path} element={<RecurringApplicationDetails />} />,

  // 1:1 채팅 route 추가
  <Route key="chatList" path={routes.admin.chat.list.path} element={<ChatList />} />,
  <Route key="chatRoom" path={routes.admin.chat.room(':roomId')} element={<ChatRoom />} />,

  // 입양 신청서 route 추가
    <Route key="adoptForm" path={routes.admin.adopt.form(':memberNum')} element={<AdoptApplicationForm />} />,
  // 동물 정보 기입 route 추가
    <Route key="animalRegister" path={routes.admin.animal.register.path} element={<AnimalForm />} />,

  // 맵 관련 route 추가
    <Route key="mapPage" path={routes.mapdata.map.path} element={<MapPage />} />,
    <Route key="mapRegister" path={routes.mapdata.register.path} element={<MapForm />} />,

    

  //사용자
  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />, //로그인
  <Route
    key="logout"
    path={routes.member.logout.path}
    element={<LogoutLink />}
  />, //로그아웃
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />, //회원가입
  <Route key="find-pw" path={routes.member.mypage.path} element={<Mypage />} />, //마이페이지
  <Route key="find-id" path={routes.member.findid.path} element={<FindId />} />, //아이디 찾기
  <Route key="find-pw" path={routes.member.findpw.path} element={<FindPw />} />, //비밀번호 찾기
  <Route
    key="update-password"
    path={routes.member.changepw.path}
    element={<ChangePw />}
  />, //비밀번호 변경

  //관리자
  <Route key="admin" path={routes.admin.admin.path} element={<Admin />} />, //관리자 로그인시 출력
  <Route
    key="updatePw"
    path={routes.admin.password.path}
    element={<AdminPw />}
  />, //관리자 비밀번호 변경

];

export default layoutRoutes;
