// 📁 src/router/layoutRoutes.js

import { Route } from "react-router-dom";
import routes from "./router";

// 📌 각 페이지 컴포넌트 import
import LoginPage from "../../program/login/pages/LoginPage";
import LogoutLink from "../../program/login/pages/LogoutLink";
import Signup from "../../program/signup/pages/SignupPage";
import Admin from "../../program/admin/pages/AdminPage";
import AdminPw from "../../program/admin/pages/AdminPasswordUpdatePage";

// 📌 routes 객체 기반으로 Route 구성
const layoutRoutes = [
  <Route key="login" path={routes.member.login.path} element={<LoginPage />} />,
  <Route key="logout" path={routes.member.logout.path} element={<LogoutLink />} />,
  <Route key="signup" path={routes.member.signup.path} element={<Signup />} />,
  
  <Route key="admin" path={routes.admin.admin.path} element={<Admin />} />,
  <Route key="updatePw" path={routes.admin.password.path} element={<AdminPw />} />,
];

export default layoutRoutes;
