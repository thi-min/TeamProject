import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const RequireAdmin = ({ children }) => {
  const token =
    localStorage.getItem("accessToken") ||
    localStorage.getItem("adminAccessToken");

  if (!token) {
    // 토큰 없으면 로그인 페이지로 이동
    return <Navigate to="/login" replace />;
  }

  try {
    const payload = jwtDecode(token);
    const role = payload.role || payload.roles || payload.authorities;

    // role 값이 ADMIN 이 아니면 접근 불가
    if (!role || !String(role).includes("ADMIN")) {
      alert("관리자만 접근할 수 있는 페이지입니다.");
      return <Navigate to="/" replace />;
    }
  } catch (err) {
    console.error("토큰 검증 실패:", err);
    return <Navigate to="/login" replace />;
  }

  // 조건 통과 → 관리자 페이지 보여줌
  return children;
};

export default RequireAdmin;