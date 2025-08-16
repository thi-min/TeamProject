// 📁 src/router/layoutRoutes.js

import { Route } from "react-router-dom";
import routes from "./router";

// 📌 각 페이지 컴포넌트 import
import LoginPage from "../../program/login/pages/LoginPage";
import LogoutLink from "../../program/login/pages/LogoutLink";
import Signup from "../../program/signup/pages/SignupPage";

// 1:1 채팅 페이지 import
import ChatList from "../../program/chat/services/ChatList";
import ChatPage from "../../program/chat/services/ChatPage";

// 📌 routes 객체 기반으로 Route 구성
const layoutRoutes = [
  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />,
  <Route key="logout" path={routes.member.logout.path} element={<LogoutLink />} />,
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />,

  // 1:1 채팅 route
  <Route key="chatList" path={routes.member.chatList.path} element={<ChatList />} />,
  <Route key="chatPage" path={routes.member.chatPage.path} element={<ChatPage />} />,
];

export default layoutRoutes;
