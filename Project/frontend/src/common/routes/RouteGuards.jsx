// 목적: /member/** 모든 경로에 대해 "로그인 상태가 아니면 즉시 '/'"로 강제 이동시키는 가드
// 사용처: router.js 또는 layoutRoutes.js에서 /member 섹션을 이 가드로 감싸기

import React, { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * RequireUserOnMember
 * - /member/** 전용 가드
 * - isLogin === false 이거나 accessToken이 사라지면 즉시 "/"로 리디렉트
 * - children: 실제 /member/** 페이지들
 */
function isProtectedMemberPath(pathname) {
  if (!pathname) return false;

  // 첫 세그먼트가 'member'인지 확인
  // pathname이 "/member/xxx" 또는 "/member" 형태일 때만 true
  return pathname === "/member" || pathname.startsWith("/member/");
}

export function RequireUserOnMember({ children }) {
  const { isLogin } = useAuth();
  const nav = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (!isLogin && isProtectedMemberPath(location.pathname)) {
      nav("/", { replace: true }); // ✅ 로그인 안된 상태면 "/"로 강제 이동
    }
  }, [isLogin, location.pathname, nav]);

  return <>{children}</>;
}
