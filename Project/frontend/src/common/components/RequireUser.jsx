// 목적: 경로의 첫 세그먼트가 'member'인 경우, 비로그인이면
//      1) "로그인 해주세요" 알림(1회) 후
//      2) "/"로 리다이렉트.
// 보강: isLogin이 false여도 로컬 토큰이 유효하면 "로그인 중"으로 간주(초기 타이밍 이슈 완화)

import React, { useRef } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// 간단 JWT exp 검증
function safeDecode(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1] || ""));
    return payload || null;
  } catch {
    return null;
  }
}
function isExpiredToken(tok) {
  const p = safeDecode(tok);
  if (!p || !p.exp) return false;
  const nowSec = Math.floor(Date.now() / 1000);
  return p.exp <= nowSec;
}
function hasValidLocalToken() {
  try {
    const t =
      localStorage.getItem("accessToken") ||
      localStorage.getItem("adminAccessToken");
    return !!t && !isExpiredToken(t);
  } catch {
    return false;
  }
}

// 경로가 /member 또는 /member/** 인지 판별
function isMemberRoot(pathname) {
  if (!pathname) return false;

  return (
    pathname.startsWith("/member") ||
    pathname.startsWith("/reserve") ||
    pathname.startsWith("/funds") 
  );

}

export function RequireMember({ children }) {
  const { isLogin } = useAuth();
  const location = useLocation();
  const alerted = useRef(false); // 알림 중복 방지

  // 🔐 최종 로그인 판정: 컨텍스트 OR 로컬 유효 토큰
  const loggedIn = isLogin || hasValidLocalToken();

  if (!loggedIn && isMemberRoot(location.pathname)) {
    const justLoggedOut = sessionStorage.getItem("logoutJustNow") === "1";

    if (!justLoggedOut && !alerted.current) {
      alerted.current = true;
      queueMicrotask(() => window.alert("로그인 해주세요"));
    }
    queueMicrotask(() => sessionStorage.removeItem("logoutJustNow"));

    // 기존 정책 유지: "/"로 이동
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}