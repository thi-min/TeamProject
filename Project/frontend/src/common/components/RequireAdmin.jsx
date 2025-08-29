// src/common/guards/RequireAdmin.jsx
import React, { useMemo } from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { useAuth } from "../context/AuthContext";

// ADMIN 문자열 판정 (ADMIN 또는 ROLE_ADMIN 모두 허용)
const ADMIN_REGEX = /(^|[,\s])(?:ROLE_)?ADMIN(?![A-Za-z_])(?!\w)/i;

function isAdminString(s) {
  if (!s) return false;
  return ADMIN_REGEX.test(String(s));
}

function extractRolesFromClaims(claims) {
  if (!claims) return [];
  const out = [];

  const push = (v) => {
    if (!v) return;
    if (Array.isArray(v)) {
      v.forEach(push);
    } else if (typeof v === "object") {
      // 객체 배열/객체 지원: {authority:"ROLE_ADMIN"}, {role:"ADMIN"}, {name:"ADMIN"}, ...
      push(v.authority || v.role || v.name || v.value);
    } else {
      // 문자열 "ADMIN", "ROLE_ADMIN", "ADMIN,USER" 등
      String(v)
        .split(/[,\s]+/)
        .filter(Boolean)
        .forEach((p) => out.push(p));
    }
  };

  push(claims.role);
  push(claims.roles);
  push(claims.authorities);
  push(claims.auth);
  push(claims.permissions);

  return out;
}

const RequireAdmin = ({ children }) => {
  const { isLogin, role: ctxRole } = useAuth();

  // 1) 컨텍스트 role 최우선
  const ctxIsAdmin = useMemo(() => {
    if (Array.isArray(ctxRole)) return ctxRole.some(isAdminString);
    return isAdminString(ctxRole);
  }, [ctxRole]);

  // 2) LS role 보강(혹시 컨텍스트 초기화 전 첫 렌더 레이스)
  const lsRole = localStorage.getItem("role");
  const lsIsAdmin = isAdminString(lsRole);

  // 3) 토큰 클레임에서 보강
  const token =
    localStorage.getItem("accessToken") ||
    localStorage.getItem("adminAccessToken");

  let jwtIsAdmin = false;
  if (token) {
    try {
      const claims = jwtDecode(token);
      const roles = extractRolesFromClaims(claims);
      jwtIsAdmin = roles.some(isAdminString);
    } catch {
      jwtIsAdmin = false;
    }
  }

  const isAdmin = ctxIsAdmin || lsIsAdmin || jwtIsAdmin;

  // 로그인 자체가 아니면 로그인 페이지로
  if (!isLogin && !token) {
    return <Navigate to="/login" replace />;
  }

  // 로그인은 했지만 관리자 권한이 없으면 차단
  if (!isAdmin) {
    alert("관리자만 접근할 수 있는 페이지입니다.");
    return <Navigate to="/" replace />;
  }

  return children;
};

export default RequireAdmin;