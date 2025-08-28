// 개선: 사용자 가드와 동일하게 레이스 보정(GRACE_MS) + auth 이벤트 구독
//       토큰이 생기면 즉시 통과, 없으면 잠시 기다렸다가 최종 판정

import React, { useEffect, useMemo, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const GRACE_MS = 400;

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

function tokenHasAdmin(tok) {
  try {
    if (!tok) return false;
    const payload = jwtDecode(tok);
    const raw = payload?.role ?? payload?.roles ?? payload?.authorities ?? "";
    const s = Array.isArray(raw) ? raw.join(",") : String(raw || "");
    return /(^|,)ROLE?_?ADMIN(,|$)/i.test(s);
  } catch {
    return false;
  }
}

const RequireAdmin = ({ children }) => {
  const location = useLocation();

  // 최초 스냅샷
  const initialTok = useMemo(() => getValidAccessToken(), []);
  const initialReady = useMemo(
    () => !!initialTok && tokenHasAdmin(initialTok),
    [initialTok]
  );

  const [ready, setReady] = useState(initialReady);
  const [grace, setGrace] = useState(!initialReady);

  // 로그인/로그아웃 이벤트 구독
  useEffect(() => {
    const recheck = () => {
      const tok = getValidAccessToken();
      setReady(!!tok && tokenHasAdmin(tok));
    };
    window.addEventListener("auth:login", recheck);
    window.addEventListener("auth:logout", recheck);
    return () => {
      window.removeEventListener("auth:login", recheck);
      window.removeEventListener("auth:logout", recheck);
    };
  }, []);

  // 짧은 유예 후 최종 판정
  useEffect(() => {
    if (!grace) return;
    const t = setTimeout(() => {
      setGrace(false);
      const tok = getValidAccessToken();
      setReady(!!tok && tokenHasAdmin(tok));
    }, GRACE_MS);
    return () => clearTimeout(t);
  }, [grace]);

  if (grace) return null;

  if (!ready) {
    // 권한 없음 또는 비로그인 → 로그인 페이지로
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location.pathname || "/" }}
      />
    );
  }

  return children;
};

export default RequireAdmin;
