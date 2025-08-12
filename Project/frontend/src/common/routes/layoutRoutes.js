// 📁 src/router/layoutRoutes.js
import React from "react";
import { Route } from "react-router-dom";
import routes from "./router";

// 📌 각 페이지 컴포넌트
import LoginPage from "../../program/login/pages/LoginPage";
import LogoutLink from "../../program/login/pages/LogoutLink";
import Signup from "../../program/signup/pages/SignupPage";

// 게시판 관련
import NormalBbsView from "../../bbs/normalbbs/NormalBbsview";
import NormalBbsWrite from "../../bbs/normalbbs/NormalBbsWrite";
import Normal from "../../bbs/normalbbs/Normal";

import QnaBbs from "../../bbs/questionbbs/QnaBbs";
import QnaBbsWrite from "../../bbs/questionbbs/QnaBbsWrite";
import QnaBbsView from "../../bbs/questionbbs/QnaBbsView";
import AdminQnaBbs from "../../bbs/questionbbs/AdminQnaBbs";


import ImgList from "../../bbs/imagebbs/imgList";
import ImgDetail from "../../bbs/imagebbs/imgdetail";
import ImgEdit from "../../bbs/imagebbs/imgedit";
import ImgWrite from "../../bbs/imagebbs/imgwrite";

// 📌 layoutRoutes 정의
const layoutRoutes = [
  // 로그인/회원 관련
  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />,
  <Route key="logout" path={routes.member.logout.path} element={<LogoutLink />} />,
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />,

  // 일반 게시판
  <Route key="normal-bbs-view" path="/bbs/normal/:id" element={<NormalBbsView />} />,
  <Route key="normal-bbs-write" path="/bbs/normal/write" element={<NormalBbsWrite />} />,
  <Route key="normal" path="/bbs/normal/" element={<Normal />} />,

  // QnA 게시판
  <Route key="qna-bbs" path="/bbs/qna" element={<QnaBbs />} />,
  <Route key="qna-bbs-write" path="/bbs/qna/write" element={<QnaBbsWrite />} />,
  <Route key="qna-bbs-view" path="/bbs/qna/:id" element={<QnaBbsView />} />,
  <Route key="qna-bbs-admin" path="/bbs/admin/qna" element={<AdminQnaBbs />} />,

  // 이미지 게시판
  <Route key="img-list" path="/bbs/image" element={<ImgList />} />,
  <Route key="img-detail" path="/bbs/image/:id" element={<ImgDetail />} />,
  <Route key="img-edit" path="/bbs/image/:id/edit" element={<ImgEdit />} />,
  <Route key="img-write" path="/bbs/image/write" element={<ImgWrite />} />,
];

export default layoutRoutes;
