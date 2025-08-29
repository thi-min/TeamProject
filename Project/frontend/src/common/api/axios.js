// frontend/src/common/api/axios.js
// ---------------------------------------------------------
// 목적
// 1) 공용 Axios 인스턴스(api) 생성
// 2) 공개 API(/auth/login, /auth/reissue, /signup, /member/check-*)에는 Authorization 제거
// 3) 보호 API에는 Authorization 자동 부착
//    - /admin/** 요청일 때는 ADMIN 권한이 '실제'로 들어있는 토큰을 우선 선택
// 4) 401 응답 시 /auth/reissue 자동 호출 → 성공 시 원요청 1회 재시도
// 5) refreshToken 없으면 재발급 시도 대신 "로그아웃 처리(토큰 삭제) + /login 이동"
//    - 백엔드가 쿠키 기반 재발급이면(withCredentials) 쿠키로 재발급 시도
// 6) KakaoCallback 등 로그인 직후 레이스 방지:
//    - saveTokens()가 토큰을 즉시 전역 주입 + 'auth:login' 이벤트 발행
//    - 가드(RequireAuth)에서 이 이벤트를 듣고 재판정 가능
// ---------------------------------------------------------

import axios from "axios";
import { jwtDecode } from "jwt-decode";

// ===== 환경 설정 =====
const BASE_URL = "http://127.0.0.1:8090";
const REISSUE_URL = "/auth/reissue"; // 재발급 엔드포인트
const LOGIN_URL = "/auth/login"; // 로그인 엔드포인트
const FRONT_LOGIN_ROUTE = "/login"; // 프론트 로그인 라우트

// 백엔드가 httpOnly 쿠키로 refreshToken을 보관한다면 true
const SUPPORT_COOKIE_REFRESH = true;

// ===== 토큰 키 =====
export const TOKEN_KEYS = {
  access: "accessToken",
  refresh: "refreshToken",
  adminAccess: "adminAccessToken",
};

// ===== 로컬스토리지 유틸 =====
function getLS(key) {
  try {
    return localStorage.getItem(key) || "";
  } catch {
    return "";
  }
}
function setLS(key, val) {
  try {
    localStorage.setItem(key, val);
  } catch {}
}
function removeLS(key) {
  try {
    localStorage.removeItem(key);
  } catch {}
}
function clearTokens() {
  removeLS(TOKEN_KEYS.access);
  removeLS(TOKEN_KEYS.refresh);
  removeLS(TOKEN_KEYS.adminAccess);
}

// ===== 토큰 유틸 =====
function safeDecode(token) {
  try {
    return jwtDecode(token);
  } catch {
    return null;
  }
}
function isExpiredToken(token) {
  const p = safeDecode(token);
  if (!p || !p.exp) return false;
  const now = Math.floor(Date.now() / 1000);
  return p.exp <= now;
}
function hasAdminRole(payload) {
  const raw = payload?.role ?? payload?.roles ?? payload?.authorities ?? "";
  const s = Array.isArray(raw) ? raw.join(",") : String(raw || "");
  // ADMIN 또는 ROLE_ADMIN 허용
  return /(^|,)ROLE?_?ADMIN(,|$)/i.test(s);
}

/**
 * ✅ 현재 저장된 accessToken이 "존재하고, 만료되지 않았는지" 간편 판정
 * - 가드(RequireAuth) 등에서 사용
 */
export function hasValidAccessToken() {
  const t = getLS(TOKEN_KEYS.access);
  return !!t && !isExpiredToken(t);
}

// ===== 공용 인스턴스 =====
export const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  withCredentials: true, // 쿠키 기반 재발급 지원 시 필요(CORS에서 credentials 허용)
});

/**
 * ✅ 런타임에서 토큰을 주입/해제하는 헬퍼
 * - KakaoCallback 등에서 로그인 성공 직후 호출하면
 *   이후 요청에 즉시 Authorization 헤더가 반영됨
 */
export function applyAuthToken(accessToken, opts = { alsoAdmin: true }) {
  if (accessToken) {
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    setLS(TOKEN_KEYS.access, accessToken);
    if (opts?.alsoAdmin) setLS(TOKEN_KEYS.adminAccess, accessToken);
  } else {
    delete api.defaults.headers.common["Authorization"];
    removeLS(TOKEN_KEYS.access);
    if (opts?.alsoAdmin) removeLS(TOKEN_KEYS.adminAccess);
  }
}

/**
 * ✅ 로그인/재발급 직후 토큰 저장 + 전역 주입 + 이벤트 발행
 * - accessToken을 axios 기본 헤더에 즉시 반영
 * - localStorage에 저장
 * - 'auth:login' 이벤트를 window에 발행 → SPA 가드가 즉시 재판정 가능
 */
export function saveTokens({ accessToken, refreshToken, alsoAdmin = true }) {
  if (accessToken) {
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    setLS(TOKEN_KEYS.access, accessToken);
    if (alsoAdmin) setLS(TOKEN_KEYS.adminAccess, accessToken);
  }
  if (refreshToken) setLS(TOKEN_KEYS.refresh, refreshToken);

  // 🔔 로그인 직후 가드/초기 API와의 레이스를 줄이기 위해 이벤트 발행
  try {
    window.dispatchEvent(new Event("auth:login"));
  } catch {}
}

/** (선택) 클라이언트 로그아웃용 유틸 */
export function logoutLocal() {
  delete api.defaults.headers.common["Authorization"];
  clearTokens();
  try {
    window.dispatchEvent(new Event("auth:logout"));
  } catch {}
}

// 앱 시작 시, 저장된 accessToken을 기본 헤더로 복구
try {
  const bootTok = getLS(TOKEN_KEYS.access);
  if (bootTok && !isExpiredToken(bootTok)) {
    api.defaults.headers.common["Authorization"] = `Bearer ${bootTok}`;
  }
} catch {}

// ===== 요청 인터셉터 =====
api.interceptors.request.use((config) => {
  const url = config.url || "";

  // 🔓 공개 API: Authorization 제거
  const isPublic =
    url.startsWith(LOGIN_URL) ||
    url.startsWith(REISSUE_URL) ||
    url.startsWith("/signup") ||
    url.startsWith("/member/check-id") ||
    url.startsWith("/member/check-phone");

  // 🔐 /admin/** (로그인 제외)
  const isAdminApi =
    url.startsWith("/admin") && !url.startsWith("/admin/login");

  // 후보 토큰 로드
  const adminTok = getLS(TOKEN_KEYS.adminAccess);
  const userTok = getLS(TOKEN_KEYS.access);

  // 유효성/권한 판정
  const aValid = adminTok && !isExpiredToken(adminTok);
  const uValid = userTok && !isExpiredToken(userTok);
  const aAdmin = aValid && hasAdminRole(safeDecode(adminTok));
  const uAdmin = uValid && hasAdminRole(safeDecode(userTok));

  // ✅ 핵심 선택 로직
  let tokenToUse = "";
  if (isAdminApi) {
    // /admin/** 에서는 ADMIN 권한이 실제로 들어있는 토큰을 우선
    if (aAdmin) tokenToUse = adminTok;
    else if (uAdmin) tokenToUse = userTok;
    else tokenToUse = aValid ? adminTok : uValid ? userTok : "";
    // (권한이 없다면 유효한 토큰이라도 붙여서 서버가 403 판단하도록 둠)
  } else {
    // 일반 API는 accessToken 우선, 없으면 adminAccessToken
    tokenToUse = uValid ? userTok : aValid ? adminTok : "";
  }

  if (!isPublic && tokenToUse) {
    config.headers["Authorization"] = `Bearer ${tokenToUse}`;
  } else {
    delete config.headers["Authorization"];
  }

  // 🔁 방어적 백업:
  // - 콜백 직후 saveTokens가 localStorage만 먼저 갱신했고
  //   인메모리 기본헤더/인터셉터 주입 타이밍 차가 있을 때 대비
  // - 단, 이미 위에서 토큰을 정해 넣었으면 덮어씌우지 않도록 '없을 때만' 보정
  const lsToken = getLS(TOKEN_KEYS.access);
  if (!isPublic && lsToken && !config.headers["Authorization"]) {
    config.headers["Authorization"] = `Bearer ${lsToken}`;
  }

  return config;
});

// ===== 401 자동 재발급 =====
let isRefreshing = false;
let refreshPromise = null;

async function reissueTokens() {
  const refreshToken = getLS(TOKEN_KEYS.refresh);

  const client = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    withCredentials: true,
  });

  // 1) 로컬에 refreshToken 있으면 바디로 전송
  if (refreshToken) {
    const res = await client.post(REISSUE_URL, { refreshToken });
    const data = res.data || {};
    const newAccess = data.accessToken || "";
    const newRefresh = data.refreshToken || refreshToken;

    if (!newAccess) throw new Error("REISSUE_NO_ACCESS");
    setLS(TOKEN_KEYS.access, newAccess);
    setLS(TOKEN_KEYS.refresh, newRefresh);
    // 재발급 후에도 관리자 흐름과 호환되도록 동기화(관리자/회원 공용 페이지 고려)
    setLS(TOKEN_KEYS.adminAccess, newAccess);
    // 전역 헤더도 갱신
    api.defaults.headers.common["Authorization"] = `Bearer ${newAccess}`;
    return { accessToken: newAccess, refreshToken: newRefresh };
  }

  // 2) 로컬에 없지만 쿠키 기반이면 쿠키로 재발급 시도
  if (SUPPORT_COOKIE_REFRESH) {
    const res = await client.post(REISSUE_URL, {});
    const data = res.data || {};
    const newAccess = data.accessToken || "";
    const newRefresh = data.refreshToken || "";

    if (!newAccess) throw new Error("REISSUE_NO_ACCESS");
    setLS(TOKEN_KEYS.access, newAccess);
    if (newRefresh) setLS(TOKEN_KEYS.refresh, newRefresh);
    setLS(TOKEN_KEYS.adminAccess, newAccess);
    api.defaults.headers.common["Authorization"] = `Bearer ${newAccess}`;
    return { accessToken: newAccess, refreshToken: newRefresh };
  }

  // 3) 둘 다 실패
  throw new Error("NO_REFRESH_TOKEN");
}

// ===== 응답 인터셉터 =====
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const { config, response } = error;
    const original = config || {};

    // 무한루프 방지 플래그
    if (!original.__retry) original.__retry = { tried401: false };

    // 401 자동 처리
    if (response && response.status === 401 && !original.__retry.tried401) {
      original.__retry.tried401 = true;

      try {
        // 싱글 플라이트: 동시에 여러 401이 와도 한 번만 재발급
        if (!isRefreshing) {
          isRefreshing = true;
          refreshPromise = reissueTokens()
            .catch((e) => {
              // 재발급 실패 → 토큰 삭제 후 로그인으로
              clearTokens();
              try {
                window.location.href = FRONT_LOGIN_ROUTE;
              } catch {}
              throw e;
            })
            .finally(() => {
              isRefreshing = false;
            });
        }

        const { accessToken } = await refreshPromise;

        // 원요청 Authorization 갱신 후 재시도
        original.headers = original.headers || {};
        original.headers["Authorization"] = `Bearer ${accessToken}`;
        return api.request(original);
      } catch (e) {
        return Promise.reject(e);
      }
    }

    // 그 외 상태코드는 그대로 전달
    return Promise.reject(error);
  }
);

// ✅ default + named export 제공
export default api;
