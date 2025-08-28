// 목적: 경로의 첫 세그먼트가 'member'인 경우, 비로그인이면
//      1) "로그인 해주세요" 알림을 1회만 띄우고
//      2) 즉시 "/"로 리디렉트하여 접근 자체를 차단한다.
// 개선: 카카오 콜백 직후 토큰 저장/주입 타이밍과의 레이스를 줄이기 위해
//      - 짧은 유예시간(GRACE_MS) 동안 재확인
//      - 'auth:login' / 'auth:logout' 커스텀 이벤트 구독 (콜백/로그아웃 시점 즉시 반영)

import React, { useEffect, useMemo, useRef, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

// 경로가 /member 또는 /member/** 인지 판별
function isMemberRoot(pathname) {
  if (!pathname) return false;
  return pathname === "/member" || pathname.startsWith("/member/");
}

// localStorage 토큰 즉시 유효성 판정(가드가 Context 갱신을 기다리지 않도록)
function getValidAccessToken() {
  try {
    const tok =
      localStorage.getItem("accessToken") ||
      localStorage.getItem("adminAccessToken") ||
      "";
    if (!tok) return "";
    const p = jwtDecode(tok);
    const now = Math.floor(Date.now() / 1000);
    if (p?.exp && p.exp > now) return tok;
    return "";
  } catch {
    return "";
  }
}

const GRACE_MS = 400; // 카카오 콜백 직후 토큰 반영 레이스 보정용

export function RequireMember({ children }) {
  const location = useLocation();
  const alerted = useRef(false); // 알림 중복 방지

  // 최초 토큰 스냅샷
  const initialHasToken = useMemo(() => !!getValidAccessToken(), []);
  const [hasToken, setHasToken] = useState(initialHasToken);
  const [grace, setGrace] = useState(!initialHasToken); // 최초에 없으면 잠깐 기다림

  // 로그인/로그아웃 커스텀 이벤트 반영 (콜백/로그아웃 시 즉시 재판정)
  useEffect(() => {
    const onLogin = () => setHasToken(!!getValidAccessToken());
    const onLogout = () => setHasToken(false);
    window.addEventListener("auth:login", onLogin);
    window.addEventListener("auth:logout", onLogout);
    return () => {
      window.removeEventListener("auth:login", onLogin);
      window.removeEventListener("auth:logout", onLogout);
    };
  }, []);

  // 짧은 유예 후 한 번 더 판정(콜백 직후 레이스 방지)
  useEffect(() => {
    if (!grace) return;
    const t = setTimeout(() => {
      setGrace(false);
      setHasToken(!!getValidAccessToken());
    }, GRACE_MS);
    return () => clearTimeout(t);
  }, [grace]);

  // 1) 유예 중이면 아무것도 렌더하지 않음(깜박임/불필요 알림 방지)
  if (grace) return null;

  // 2) /member 진입인데 토큰이 최종적으로도 없으면 차단
  if (!hasToken && isMemberRoot(location.pathname)) {
    const justLoggedOut = sessionStorage.getItem("logoutJustNow") === "1";

    if (!justLoggedOut && !alerted.current) {
      alerted.current = true;
      queueMicrotask(() => window.alert("로그인 해주세요"));
    }
    queueMicrotask(() => sessionStorage.removeItem("logoutJustNow"));

    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}

export default RequireMember;
