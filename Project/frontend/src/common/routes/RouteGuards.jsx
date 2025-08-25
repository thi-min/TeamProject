// 목적: 경로의 첫 세그먼트가 'member'인 경우, 비로그인이면
//      1) "로그인 해주세요" 알림을 1회만 띄우고
//      2) 즉시 "/"로 리디렉트하여 접근 자체를 차단한다.

import React, { useRef } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// 경로가 /member 또는 /member/** 인지 판별
function isMemberRoot(pathname) {
  if (!pathname) return false;
  return pathname === "/member" || pathname.startsWith("/member/");
}

export function RequireUserOnMember({ children }) {
  const { isLogin } = useAuth();
  const location = useLocation();
  const alerted = useRef(false); // 알림 중복 방지

  // 비로그인이고, /member 진입 시도라면 즉시 차단
  if (!isLogin && isMemberRoot(location.pathname)) {
    // ✅ 로그아웃 버튼에 의해 방금 로그아웃된 경우 알림 억제
    const justLoggedOut = sessionStorage.getItem("logoutJustNow") === "1";

    if (!justLoggedOut && !alerted.current) {
      alerted.current = true;
      // 렌더 중 alert 호출을 안전하게 하기 위해 microtask로 밀어둠
      queueMicrotask(() => window.alert("로그인 해주세요"));
    }
    // 플래그는 바로 제거(다음 내비게이션에 잔존하지 않게)
    queueMicrotask(() => sessionStorage.removeItem("logoutJustNow"));

    // 페이지 컴포넌트는 전혀 렌더되지 않음 → 접근 자체 차단
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
