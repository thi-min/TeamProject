/**
 * ✅ AuthContext: 로그인 상태/토큰을 전역으로 관리하는 파일
 * - 아주 기초부터 차근차근: Context 만들기 → Provider로 감싸기 → useAuth 훅으로 꺼내쓰기
 * - 초보자도 한눈에 이해할 수 있도록 주석을 자세히 달아둠
 */

import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  useCallback,
} from "react";

// ⚠️ 주의: jwt-decode v4부터는 named export만 제공 → {}로 감싸서 import
// 설치가 안 돼 있다면: npm i jwt-decode
import { jwtDecode } from "jwt-decode";

if (!window.__AUTH_CTX_ID__) {
  window.__AUTH_CTX_ID__ = Math.random().toString(36).slice(2);
  console.log("[AuthContext] instance id =", window.__AUTH_CTX_ID__);
}

// 1) Context(빈 그릇) 만들기 — 나중에 Provider로 "값"을 채워서 자식들이 꺼내 씀
const AuthContext = createContext(null);

// 2) 공통 유틸 함수: 토큰을 해석(decode)하고 만료 여부 체크
function decodeAndCheck(accessToken) {
  if (!accessToken) {
    return { isValid: false, payload: null };
  }

  try {
    const payload = jwtDecode(accessToken); // { sub, role, exp, ... }
    const nowSec = Math.floor(Date.now() / 1000); // 현재 시간(초)
    const isValid = !!payload.exp && payload.exp > nowSec; // exp가 있고, 현재보다 미래면 유효
    return { isValid, payload };
  } catch (e) {
    console.error("[Auth] 토큰 해석 실패:", e);
    return { isValid: false, payload: null };
  }
}

// 로컬스토리지 키(이름) — 오타 방지용 상수
const ACCESS_KEY = "accessToken";
const REFRESH_KEY = "refreshToken";
const ADMIN_ACCESS_TOKEN = "adminAccessToken";
const MEMBER_NUM = "memberNum";

/**
 * 3) Provider 컴포넌트
 * - 앱의 루트를 이 컴포넌트로 감싸면, 하위 컴포넌트 어디에서든 로그인 정보를 꺼낼 수 있음
 */
export function AuthProvider({ children }) {
  // (A) 토큰 원본을 보관
  const [accessToken, setAccessToken] = useState(() =>
    localStorage.getItem(ACCESS_KEY)
  );
  const [refreshToken, setRefreshToken] = useState(() =>
    localStorage.getItem(REFRESH_KEY)
  );

  // (B) 토큰에서 뽑아낸 "파생 상태" (보는 사람 입장에서 더 직관적)
  const [isLogin, setIsLogin] = useState(false);
  const [role, setRole] = useState(null); // 예: 'USER' / 'ADMIN'
  const [userId, setUserId] = useState(null); // 일반적으로 JWT의 sub 사용

  // (C) accessToken이 바뀔 때마다 → 해석해서(isLogin/role/userId) 갱신
  useEffect(() => {
    const { isValid, payload } = decodeAndCheck(accessToken);
    setIsLogin(isValid);
    setRole(payload?.role ?? null);
    setUserId(payload?.sub ?? null);
  }, [accessToken]);

  // (D) 로그인 함수 — 서버에서 받은 토큰을 저장
  //const login = useCallback(({ accessToken: at, refreshToken: rt }) => {
  const login = useCallback((raw) => {
    // 1) 인자가 없을 수도 있으니 기본값 방어
    const input = raw ?? {};

    // 2) 응답 스키마가 제각각일 때를 대비해 토큰을 "정규화"해서 추출
    //    (프로젝트 백엔드 실제 키에 맞춰 위쪽 줄부터 채택됨)
    const at =
      input.accessToken ??
      input.token ??
      input.jwt ??
      input?.data?.accessToken ??
      input?.member?.accessToken ??
      null;

    const rt =
      input.refreshToken ??
      input?.data?.refreshToken ??
      input?.member?.refreshToken ??
      null;
    if (!at && !rt) {
      console.error(
        "[Auth] login()에 유효한 토큰이 없습니다. 받은 값:",
        JSON.parse(JSON.stringify(input))
      );
      return; // 토큰 없으면 종료
    }

    if (at) {
      localStorage.setItem(ACCESS_KEY, at);
      setAccessToken(at);

      // 🔹 여기서 즉시 로그인 상태 반영
      try {
        const { payload } = decodeAndCheck(at); // 토큰 해석
        setIsLogin(true); // 바로 true로
        setUserId(payload?.sub ?? null); // JWT sub → 사용자 ID
        setRole(payload?.role ?? null); // JWT role → 역할
      } catch (e) {
        console.error("[Auth] 토큰 해석 오류", e);
        //해석실패시 로그인 상태 true로 할지
        setIsLogin(!!at);
      }
    }
    if (rt) {
      localStorage.setItem(REFRESH_KEY, rt);
      setRefreshToken(rt);
    }
  }, []);

  // (E) 로그아웃 함수 — 저장된 토큰을 지움
  const logout = useCallback(() => {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(ADMIN_ACCESS_TOKEN);
    localStorage.removeItem(MEMBER_NUM);
    sessionStorage.clear();
    setAccessToken(null);
    setRefreshToken(null);
    // 파생 상태도 초기화
    setIsLogin(false);
    setRole(null);
    setUserId(null);
  }, []);

  // (F) 토큰 재발급(자동 로그인용) — 필요할 때 호출
  //    실제 API 주소/응답 형태는 프로젝트에 맞게 바꿔주세요.
  const refresh = useCallback(async () => {
    if (!refreshToken) return false;

    try {
      const res = await fetch("/", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken }),
      });

      if (!res.ok) throw new Error("토큰 재발급 실패");

      const data = await res.json(); // { accessToken, refreshToken } 라고 가정
      if (data.accessToken) {
        localStorage.setItem(ACCESS_KEY, data.accessToken);
        setAccessToken(data.accessToken);
      }
      if (data.refreshToken) {
        localStorage.setItem(REFRESH_KEY, data.refreshToken);
        setRefreshToken(data.refreshToken);
      }
      return true;
    } catch (e) {
      console.error("[Auth] 재발급 에러:", e);
      logout(); // 재발급 실패 → 강제 로그아웃
      return false;
    }
  }, [refreshToken, logout]);

  // (G) 토큰 만료 60초 전에 자동 재발급 시도 — 선택사항이지만 편함
  useEffect(() => {
    if (!accessToken) return;

    const { payload } = decodeAndCheck(accessToken);
    const exp = payload?.exp; // 만료시간(초)
    if (!exp) return;

    const nowSec = Math.floor(Date.now() / 1000);
    const msUntilRefresh = Math.max((exp - 60 - nowSec) * 1000, 0); // 60초 전에 미리 시도

    const timer = setTimeout(async () => {
      const ok = await refresh();
      if (!ok) logout();
    }, msUntilRefresh);

    return () => clearTimeout(timer);
  }, [accessToken, refresh, logout]);

  // (H) 하위 컴포넌트에서 꺼내 쓸 값들
  const value = {
    // 읽기 전용 상태
    isLogin,
    role,
    userId,
    accessToken,
    refreshToken,
    // 조작 함수
    login,
    logout,
    refresh,
  };

  // Provider가 실제로 "값"을 담아서 자식한테 내려줌
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * 4) useAuth 훅
 * - 어디서든 간단히: const { isLogin, login, logout } = useAuth();
 * - 단, <AuthProvider>로 앱이 감싸져 있어야 함!
 */
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    // Provider로 감싸지 않은 곳에서 사용하면 에러로 알려줌 (디버깅 편하게)
    throw new Error("useAuth는 <AuthProvider> 내부에서만 사용해야 합니다.");
  }
  return ctx;
}
