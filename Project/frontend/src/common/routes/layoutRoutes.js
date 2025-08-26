
import { Route } from "react-router-dom";
import routes from "./router";
import { RequireUserOnMember } from "./RouteGuards";
import { LogoutLink } from "../../program/login/pages/LogoutLink"; // 로그아웃

// ==============================
// 회원 관련 페이지
// ==============================
import LoginPage from "../../program/login/pages/LoginPage";
import Join from "../../program/signup/pages/JoinPage";
import PhoneVerifyPage from "../../program/signup/pages/PhoneVerifyPage";
import Signup from "../../program/signup/pages/SignupPage";
import FindId from "../../program/member/pages/FindIdPage";
import FindPw from "../../program/member/pages/FindPasswordPage";
import ChangePw from "../../program/member/pages/UpdatePasswordPage";
import MyPage from "../../program/member/pages/Mypage";
import MemberPage from "../../program/member/pages/MemberData";

// ==============================
// 후원(Fund) 관련 페이지
// ==============================
import {
  FundApplicationDetails,
  FundApplicationForm,
  FundMainPage,
  GoodsApplicationDetails,
  GoodsApplicationForm,
  RegularApplicationDetails,
  RegularApplicationForm,
  MemberFundList,
  MemberFundDetail
} from "../../program/fund/services/FundPage";

// ==============================
// 1:1 채팅 관련 페이지
// ==============================
import ChatList from "../../program/chat/services/ChatList";
import ChatRoom from "../../program/chat/services/ChatRoom";

// ==============================
// 입양 신청서 관련 페이지
// ==============================
import AdoptApplicationForm from "../../program/adopt/services/AdoptApplicationForm";

// ==============================
// 동물 정보 페이지 (Animal)
// ==============================
import AnimalForm from "../../program/animal/services/AnimalForm";

// ==============================
// 지도 관련 페이지
// ==============================
import MapForm from '../../program/mapdata/services/MapForm.jsx';

// ==============================
// 관리자 페이지
// ==============================
import Admin from "../../program/admin/pages/AdminPage";
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage";
import MemberList from "../../program/admin/pages/MemberListPage";
import MemberDetail from "../../program/admin/pages/MemberDetailPage";

// ==============================
// 📌 routes 객체 기반 Route 구성
// ==============================
const layoutRoutes = [

  // ------------------------------
  // 후원(Fund) 관련 Route
  // ------------------------------
  <Route key="fundMainPage" path={routes.fund.root.path} element={<FundMainPage />} />,
  <Route key="fundForm" path={routes.fund.fundForm.path} element={<FundApplicationForm />} />,
  <Route key="goodsForm" path={routes.fund.goodsForm.path} element={<GoodsApplicationForm />} />,
  <Route key="regularForm" path={routes.fund.regularForm.path} element={<RegularApplicationForm />} />,
  <Route key="fundDetails" path={routes.fund.fundDetails.path} element={<FundApplicationDetails />} />,
  <Route key="goodsDetails" path={routes.fund.goodsDetails.path} element={<GoodsApplicationDetails />} />,
  <Route key="regularDetails" path={routes.fund.regularDetails.path} element={<RegularApplicationDetails />} />,
  <Route key="MemberFundList" path={routes.member.fund.list.path} element={<MemberFundList />} />,
  <Route key="memberFundDetail" path={routes.member.fund.detail(`:id`)} element={<MemberFundDetail/>} />,
  // ------------------------------
  // 1:1 채팅 Route
  // ------------------------------
  <Route key="chatList" path={routes.admin.chat.list.path} element={<ChatList />} />,
  <Route key="chatRoom" path={routes.admin.chat.room(':roomId')} element={<ChatRoom />} />,

  // ------------------------------
  // 입양 신청서 Route
  // ------------------------------
  <Route key="memberAdoptList" path={routes.member.adopt.list.path} element={<AdoptApplicationForm />} />,
  <Route key="memberAdoptDetail" path={routes.member.adopt.detail(':id')} element={<AdoptApplicationForm />} />,
  <Route key="adminAdoptList" path={routes.admin.adopt.list.path} element={<AdoptApplicationForm />} />,
  <Route key="adminAdoptDetail" path={routes.admin.adopt.detail(':id')} element={<AdoptApplicationForm />} />,
  <Route key="adminAdoptRegist" path={routes.admin.adopt.regist.path} element={<AdoptApplicationForm />} />,
  <Route key="adminAdoptUpdate" path={routes.admin.adopt.update(':id')} element={<AdoptApplicationForm />} />,

  // ------------------------------
  // 동물 정보 Route (관리자)
  // ------------------------------
  <Route key="adminAnimalList" path={routes.admin.animal.list.path} element={<AnimalForm />} />,
  <Route key="adminAnimalDetail" path={routes.admin.animal.detail(':id')} element={<AnimalForm />} />,
  <Route key="adminAnimalRegist" path={routes.admin.animal.regist.path} element={<AnimalForm />} />,
  <Route key="adminAnimalUpdate" path={routes.admin.animal.update(':id')} element={<AnimalForm />} />,

  // ------------------------------
  // 지도 관련 Route
  // ------------------------------
  <Route key="map" path={routes.map.root.path} element={<MapForm />} />,

  // ------------------------------
  // 사용자 관련 Route
  // ------------------------------
  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />,
  <Route key="logout" path={routes.member.logout.path} element={<LogoutLink />} />,
  <Route key="join" path={routes.member.join.path} element={<Join />} />,
  <Route key="phonetest" path={routes.member.phone.path} element={<PhoneVerifyPage />} />,
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />,
  <Route
    key="mypage"
    path={routes.member.mypage.path}
    element={
      <RequireUserOnMember>
        <MyPage />
      </RequireUserOnMember>
    }
  />,
  <Route
    key="memberdata"
    path={routes.member.update.path} // 수정: memberdata 대신 update
    element={
      <RequireUserOnMember>
        <MemberPage />
      </RequireUserOnMember>
    }
  />,
  <Route key="find-id" path={routes.member.findid.path} element={<FindId />} />,
  <Route key="find-pw" path={routes.member.findpw.path} element={<FindPw />} />,
  <Route key="update-password" path={routes.member.changepw.path} element={<ChangePw />} />,

  // ------------------------------
  // 관리자 관련 Route
  // ------------------------------
  <Route key="admin" path={routes.admin.admin.path} element={<Admin />} />,
  <Route
    key="updatePw"
    path={routes.admin.password.path}
    element={
      <RequireUserOnMember>
        <AdminPw />
      </RequireUserOnMember>
    }
  />,
  <Route key="memberList" path={routes.admin.members.path} element={<MemberList />} />,
  <Route key="memberDetail" path={routes.admin.memberDetail(':id')} element={<MemberDetail />} />,
];

export default layoutRoutes;