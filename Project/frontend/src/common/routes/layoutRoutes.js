// 📁 src/router/layoutRoutes.js

import { Route } from "react-router-dom";
import routes from "./router";
import { RequireUserOnMember } from "./RouteGuards";
import { LogoutLink } from "../../program/login/pages/LogoutLink"; //로그아웃

// 📌 각 페이지 컴포넌트 import
<<<<<<< HEAD
import Admin from "../../program/admin/pages/AdminPage"; //관리자 로그인시 출력
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage"; //관리자 비밀번호 변경
import LoginPage from "../../program/login/pages/LoginPage"; //로그인
import LogoutLink from "../../program/login/pages/LogoutLink"; //로그아웃
import ChangePw from "../../program/member/pages/ChangePasswordPage"; //비밀번호 변경
=======
//회원
import LoginPage from "../../program/login/pages/LoginPage"; //로그인
import Join from "../../program/signup/pages/JoinPage"; //회원가입 약관
import PhoneVerifyPage from "../../program/signup/pages/PhoneVerifyPage"; //휴대폰 인증 페이지
import Signup from "../../program/signup/pages/SignupPage"; //회원가입
>>>>>>> refs/remotes/origin/ahj0808
import FindId from "../../program/member/pages/FindIdPage"; //아이디 찾기
import FindPw from "../../program/member/pages/FindPasswordPage"; //비밀번호 찾기
<<<<<<< HEAD
import Mypage from "../../program/member/pages/Mypage"; //마이페이지
import Signup from "../../program/signup/pages/SignupPage"; //회원가입


// FundPage.js에서 기본 내보내기가 없으므로, FundMainPage를 이름으로 불러옵니다.
// 이제 FundPage.js에서 내보내는 모든 컴포넌트를 불러옵니다.
import {
  FundApplicationDetails,
  FundApplicationForm,
  FundMainPage,
  GoodsApplicationDetails,
  GoodsApplicationForm,
  RegularApplicationDetails,
  RegularApplicationForm
} from "../../program/fund/services/FundPage";

// 1:1 채팅 컴포넌트 import
import ChatList from "../../program/chat/services/ChatList";
import ChatRoom from "../../program/chat/services/ChatRoom";

// 입양 신청서 컴포넌트 import
import AdoptApplicationForm from "../../program/adopt/services/AdoptApplicationForm";

// AnimalForm 컴포넌트 import
import AnimalForm from "../../program/animal/services/AnimalForm";

// mapform컴포넌트
import MapForm from '../../program/mapdata/services/MapForm.jsx';
=======
import ChangePw from "../../program/member/pages/UpdatePasswordPage"; //비밀번호 변경
import MyPage from "../../program/member/pages/Mypage"; //마이페이지
import MemberPage from "../../program/member/pages/MemberData"; //회원정보

//관리자
import Admin from "../../program/admin/pages/AdminPage"; //관리자 로그인시 출력
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage"; //관리자 비밀번호 변경
import MemberList from "../../program/admin/pages/MemberListPage"; //관리자 전체 회원조회
import MemberDetail from "../../program/admin/pages/MemberDetailPage"; //관리자 회원정보 변경

>>>>>>> refs/remotes/origin/ahj0808

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
  <Route key="regularForm" path={routes.fund.regularForm.path} element={<RegularApplicationForm />} />,
  <Route key="fundDetails" path={routes.fund.fundDetails.path} element={<FundApplicationDetails />} />,
  <Route key="goodsDetails" path={routes.fund.goodsDetails.path} element={<GoodsApplicationDetails />} />,
  <Route key="regularDetails" path={routes.fund.regularDetails.path} element={<RegularApplicationDetails />} />,

  // 1:1 채팅 route 추가
  <Route key={routes.admin.chat.list.path} path={routes.admin.chat.list.path} element={<ChatList />} />,
  <Route key={routes.admin.chat.room(':roomId')} path={routes.admin.chat.room(':roomId')} element={<ChatRoom />} />,
  
  
  // 입양 신청서 route 추가
  <Route key="memberAdoptList" path={routes.member.adopt.list.path} element={<AdoptApplicationForm />} />,
  <Route key="memberAdoptDetail" path={routes.member.adopt.detail(':id')} element={<AdoptApplicationForm />} />,
  
  // 관리자: 목록 조회, 상세 조회, 작성, 수정
  <Route key="adminAdoptList" path={routes.admin.adopt.list.path} element={<AdoptApplicationForm />} />,
  <Route key="adminAdoptDetail" path={routes.admin.adopt.detail(':id')} element={<AdoptApplicationForm />} />,
  <Route key="adminAdoptResist" path={routes.admin.adopt.resist.path} element={<AdoptApplicationForm />} />,
  <Route key="adminAdoptUpdate" path={routes.admin.adopt.update(':id')} element={<AdoptApplicationForm />} />,
  
  // 동물 정보 Route (관리자)
  <Route key="adminAnimalList" path={routes.admin.animal.list.path} element={<AnimalForm />} />,
  <Route key="adminAnimalDetail" path={routes.admin.animal.detail(':id')} element={<AnimalForm />} />,
  <Route key="adminAnimalResist" path={routes.admin.animal.resist.path} element={<AnimalForm />} />,
  <Route key="adminAnimalUpdate" path={routes.admin.animal.update(':id')} element={<AnimalForm />} />,

  // 맵 관련 route 추가
  <Route key="map" path={routes.map.root.path} element={<MapForm />} />,


    

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

  //관리자
  <Route key="admin" path={routes.admin.admin.path} element={<Admin />} />, //관리자 로그인시 출력
  <Route
<<<<<<< HEAD
    key="updatePw"
    path={routes.admin.password.path}
    element={<AdminPw />}
  />, //관리자 비밀번호 변경

=======
  key="updatePw"
  path={routes.admin.password.path}
  element={
    <RequireUserOnMember>
        <AdminPw />
      </RequireUserOnMember>
    }
    />, //관리자 비밀번호 변경
  <Route key="memberList" path={routes.admin.membersList.path} element={<MemberList />} />, //관리자 회원목록
  <Route
    key="memberDetail"
    path={routes.admin.memberDetail.path}
    element={<MemberDetail />}
  />,//관리자 회원상세보기
>>>>>>> refs/remotes/origin/ahj0808
];

export default layoutRoutes;
