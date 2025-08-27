// 📁 src/router/layoutRoutes.js
import React from "react";
import { Route } from "react-router-dom";
import routes from "./router";
import { LogoutLink } from "../../program/login/pages/LogoutLink"; //로그아웃

//권한 부여
import { RequireMember } from "../components/RequireUser"; //사용자
import RequireAdmin from "../components/RequireAdmin"; //관리자

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


// 📌 layoutRoutes 정의

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


// 게시판 관련
import AdminBbs from "../../bbs/adminbbs";

import Normal from "../../bbs/normalbbs/Normal";
import NormalBbsView from "../../bbs/normalbbs/NormalBbsview";
import NormalBbsWrite from "../../bbs/normalbbs/NormalBbsWrite";
import NormalBbsEdit from "../../bbs/normalbbs/NormalBbsEdit";
import MemberNormalBbs from "../../bbs/normalbbs/MemberNormalBbs";
import MemberNormalBbsView from "../../bbs/normalbbs/MemberNormalBbsView";
import MemberNormalBbsDummy from "../../bbs/normalbbs/MemberNormalBbsdummy";
import AdminNormalBbsdummy from "../../bbs/normalbbs/AdminNormalBbsdummy";

import QnaBbs from "../../bbs/questionbbs/QnaBbs";
import QnaBbsWrite from "../../bbs/questionbbs/QnaBbsWrite";
import QnaBbsView from "../../bbs/questionbbs/QnaBbsView";
import QnaBbsEdit from "../../bbs/questionbbs/QnaBbsEdit";
import AdminQnaBbs from "../../bbs/questionbbs/AdminQnaBbs";
import AdminQnaBbsView from "../../bbs/questionbbs/AdminQnaBbsView";
import AdminQnaBbsDummy from "../../bbs/questionbbs/AdminQnaBbsDummy";


import ImgList from "../../bbs/imagebbs/imgList";
import ImgDetail from "../../bbs/imagebbs/imgdetail";
import ImgEdit from "../../bbs/imagebbs/imgedit";
import ImgWrite from "../../bbs/imagebbs/imgwrite";
import AdminImgBoard from "../../bbs/imagebbs/AdminImgBoard";
import AdminImgDetail from "../../bbs/imagebbs/AdminImgDetail";
import ImgBoardDummy from "../../bbs/imagebbs/ImgBoardDummy";

// 📌 routes 객체 기반으로 Route 구성
const layoutRoutes = [
  <Route key="adminbbstab" path="/admin/bbs" element={<AdminBbs />} />,

  // 일반 게시판
  <Route key="normal" path="/admin/bbs/normal" element={<Normal />} />,
  <Route key="normal-bbs-view" path="/admin/bbs/normal/:id" element={<NormalBbsView />} />,
  <Route key="normal-bbs-write" path="/admin/bbs/normal/write" element={<NormalBbsWrite />} />,
  <Route key="normal-bbs-edit" path="/bbs/normal/edit" element={<NormalBbsEdit />} />,
  <Route key="Membernormal" path="/bbs/normal" element={<MemberNormalBbs />} />,
  <Route key="Membernormal-view" path="/bbs/normal/view/:id" element={<MemberNormalBbsView />} />,
  <Route key="Membernormal-dummy" path="/bbs/member/normal/dummy" element={<MemberNormalBbsDummy/>} />,
  <Route key="Adminnormal-dummy" path="/bbs/Admin/normal/dummy" element={<AdminNormalBbsdummy/>} />,

  // QnA 게시판
  <Route key="qna-bbs" path="/bbs/qna" element={<QnaBbs />} />,
  <Route key="qna-bbs-write" path="/bbs/qna/write" element={<QnaBbsWrite />} />,
  <Route key="qna-bbs-view" path="/bbs/qna/:id" element={<QnaBbsView />} />,
  <Route key="qna-bbs-edit" path="/bbs/qna/edit/:id" element={<QnaBbsEdit />} />,
  <Route key="qna-bbs-admin" path="/admin/bbs/qna" element={ <RequireAdmin><AdminQnaBbs /></RequireAdmin>} />,
  <Route key="qna-bbs-admin-view" path="/admin/qna/view/:id" element={ <RequireAdmin><AdminQnaBbsView /></RequireAdmin>} />,
  <Route key="qna-bbs-admin-dummy" path="/bbs/admin/qna/dummy" element={<AdminQnaBbsDummy />} />,


  // 이미지 게시판
  <Route key="img-list" path="/bbs/image" element={<ImgList />} />,
  <Route key="img-detail" path="/bbs/image/:id" element={<ImgDetail />} />,
  <Route key="img-edit" path="/bbs/image/edit/:id" element={<ImgEdit />} />,
  <Route key="img-write" path="/bbs/image/write" element={<ImgWrite />} />,
  <Route key="img-Admin-ImgBoard" path="/admin/bbs/image" element={<AdminImgBoard />} />,
  <Route key="img-Admin-ImgDetail" path="/admin/bbs/image/Detail/:id" element={<AdminImgDetail />} />,
  <Route key="img-Admin-ImgDummy" path="/bbs/admin/image/Dummy" element={<ImgBoardDummy />} />,

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
  //<Route key="chatList" path={routes.admin.chat.list.path} element={<ChatList />} />,
  //<Route key="chatRoom" path={routes.admin.chat.room(':roomId')} element={<ChatRoom />} />,

  // 입양 신청서 route 추가
    //<Route key="adoptForm" path={routes.admin.adopt.form(':memberNum')} element={<AdoptApplicationForm />} />,
  // 동물 정보 기입 route 추가
    //<Route key="animalRegister" path={routes.admin.animal.register.path} element={<AnimalForm />} />,

  // 맵 관련 route 추가
    //<Route key="mapPage" path={routes.mapdata.map.path} element={<MapPage />} />,
   // <Route key="mapRegister" path={routes.mapdata.register.path} element={<MapForm />} />,


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
