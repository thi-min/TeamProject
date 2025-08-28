// src/common/guards/RequireAdmin.jsx
import React, { useMemo } from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { useAuth } from "../context/AuthContext";

/** 다양한 형태의 권한 클레임을 안전하게 파싱해서 ADMIN 여부 판단 */
function hasAdminFromClaims(claims) {
  if (!claims) return false;

  // 후보군 수집: 문자열/배열/객체배열 모두 대응
  const bag = [];

  const push = (v) => {
    if (!v) return;
    if (Array.isArray(v)) {
      for (const el of v) push(el);
    } else if (typeof v === "string") {
      // 콤마/공백 구분
      v.split(/[,\s]+/).forEach((s) => s && bag.push(s));
    } else if (typeof v === "object") {
      // {authority:"ROLE_ADMIN"} | {role:"ADMIN"} | {name:"ADMIN"}
      bag.push(v.authority || v.role || v.name);
    }
  };

  push(claims.role);
  push(claims.roles);
  push(claims.authorities);
  push(claims.auth);
  push(claims.permissions);

  // 최종 문자열로 합쳐서 ADMIN 패턴 검사
  const joined = bag.filter(Boolean).join(",");
  return /(^|,|\s)ROLE?_?ADMIN(,|\s|$)/i.test(joined);
}

const RequireAdmin = ({ children }) => {
  const { isLogin, role: ctxRole } = useAuth();

  // 1) 컨텍스트의 role을 최우선 사용 (로그인 직후에도 신뢰 가능)
  const ctxIsAdmin = useMemo(() => {
    if (!ctxRole) return false;
    return /(^|,|\s)ROLE?_?ADMIN(,|\s|$)/i.test(String(ctxRole).toUpperCase());
  }, [ctxRole]);

  // 2) 토큰으로 보강 판정 (컨텍스트가 아직 초기화 중인 첫 렌더 레이스 등 대비)
  const token =
    localStorage.getItem("accessToken") ||
    localStorage.getItem("adminAccessToken");

  let jwtIsAdmin = false;
  if (token) {
    try {
      const claims = jwtDecode(token);
      jwtIsAdmin = hasAdminFromClaims(claims);
    } catch {
      jwtIsAdmin = false;
    }
  }

  const isAdmin = ctxIsAdmin || jwtIsAdmin;

  // 로그인 자체가 아니면 → 로그인 페이지로
  if (!isLogin && !token) {
    return <Navigate to="/login" replace />;
  }

  // 로그인은 했지만 관리자 권한이 없다면 경고 후 홈으로
  if (!isAdmin) {
    alert("관리자만 접근할 수 있는 페이지입니다.");
    return <Navigate to="/" replace />;
  }

  return children;
};

export default RequireAdmin;
