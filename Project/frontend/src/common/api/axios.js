// frontend/src/common/api/axios.js
// ---------------------------------------------------------
// 목적
// 1) 공용 Axios 인스턴스(api) 생성
// 2) 공개 API(/auth/login, /auth/reissue, /signup, /member/check-*)에는 Authorization 제거
// 3) 보호 API에는 Authorization 자동 부착 (adminAccessToken 우선)
// 4) 401 응답 시 /auth/reissue 자동 호출 → 성공 시 원요청 1회 재시도
// 5) refreshToken 없으면 재발급 시도 대신 "로그아웃 처리(토큰 삭제) + /login 이동"
//    - 백엔드가 쿠키 기반 재발급이면, 쿠키로도 재발급 시도를 해봄(withCredentials)
// ---------------------------------------------------------

import axios from "axios";
import { jwtDecode } from "jwt-decode";

// ===== 환경 설정 =====
const BASE_URL = "http://localhost:8090";
const REISSUE_URL = "/auth/reissue";    // 백엔드 재발급 엔드포인트
const LOGIN_URL = "/auth/login";        // 로그인 엔드포인트(백엔드 기준 경로에 맞춤)
const FRONT_LOGIN_ROUTE = "/login";     // 프론트 라우터 로그인 경로(필요 시 수정)

// 백엔드가 "쿠키 기반(refreshToken을 httpOnly 쿠키로 보관)"이라면 true 권장
const SUPPORT_COOKIE_REFRESH = true;

// ===== 토큰 키 =====
const TOKEN_KEYS = {
  access: "accessToken",
  refresh: "refreshToken",
  adminAccess: "adminAccessToken",
};

function getLS(key) {
  try { return localStorage.getItem(key) || ""; } catch { return ""; }
}
function setLS(key, val) {
  try { localStorage.setItem(key, val); } catch {}
}
function removeLS(key) {
  try { localStorage.removeItem(key); } catch {}
}
function clearTokens() {
  removeLS(TOKEN_KEYS.access);
  removeLS(TOKEN_KEYS.refresh);
  removeLS(TOKEN_KEYS.adminAccess);
}

function safeDecode(token) {
  try { return jwtDecode(token); } catch { return null; }
}
function isExpiredToken(token) {
  const p = safeDecode(token);
  if (!p || !p.exp) return false;
  const now = Math.floor(Date.now() / 1000);
  return p.exp <= now;
}

// ===== 공용 인스턴스 =====
export const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  withCredentials: true, // ✅ 쿠키 기반 재발급을 지원하려면 true (CORS에서 credentials 허용 필요)
});

// ===== 요청 인터셉터 =====
api.interceptors.request.use((config) => {
  const url = config.url || "";

  // 🔓 공개 API: 토큰 제거
  const isPublic =
    url.startsWith(LOGIN_URL) ||
    url.startsWith(REISSUE_URL) ||
    url.startsWith("/signup") ||
    url.startsWith("/member/check-id") ||
    url.startsWith("/member/check-phone");

  // 🔐 /admin/** (로그인 제외)
  const isAdminApi = url.startsWith("/admin") && !url.startsWith("/admin/login");

  // 토큰 후보
  const adminAccess = getLS(TOKEN_KEYS.adminAccess);
  const userAccess  = getLS(TOKEN_KEYS.access);

  // ✅ 있는 토큰은 우선순위대로 부착 (권한 부족은 서버가 403으로 판단)
  let tokenToUse = "";
  if (isAdminApi) tokenToUse = adminAccess || userAccess;
  else tokenToUse = userAccess || adminAccess;

  if (!isPublic && tokenToUse && !isExpiredToken(tokenToUse)) {
    config.headers["Authorization"] = `Bearer ${tokenToUse}`;
  } else {
    delete config.headers["Authorization"];
  }

  return config;
});

// ===== 401 자동 재발급 =====
let isRefreshing = false;
let refreshPromise = null;

/**
 * refreshToken으로 토큰 재발급
 * - 로컬스토리지에 refreshToken이 있으면 바디로 전송
 * - 없더라도 쿠키 기반을 지원하면(withCredentials) 바디 없이 호출 시도
 * - 성공: { accessToken, refreshToken? } 저장 + 반환
 * - 실패: 예외 throw
 */
async function reissueTokens() {
  const refreshToken = getLS(TOKEN_KEYS.refresh);

  const client = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    withCredentials: true, // 쿠키 기반 재발급 지원
  });

  // 1) 로컬 저장된 refreshToken이 있으면 바디로 전송
  if (refreshToken) {
    const res = await client.post(REISSUE_URL, { refreshToken });
    const data = res.data || {};
    const newAccess  = data.accessToken || "";
    const newRefresh = data.refreshToken || refreshToken;

    if (!newAccess) throw new Error("REISSUE_NO_ACCESS");
    setLS(TOKEN_KEYS.access, newAccess);
    setLS(TOKEN_KEYS.refresh, newRefresh);
    // 관리자 흐름에서도 동일 액세스를 쓰도록 동기화
    setLS(TOKEN_KEYS.adminAccess, newAccess);
    return { accessToken: newAccess, refreshToken: newRefresh };
  }

  // 2) 로컬에 없지만, 쿠키 기반을 지원한다면 바디 없이 호출해봄
  if (SUPPORT_COOKIE_REFRESH) {
    const res = await client.post(REISSUE_URL, {}); // 쿠키로 인증
    const data = res.data || {};
    const newAccess  = data.accessToken || "";
    const newRefresh = data.refreshToken || "";

    if (!newAccess) throw new Error("REISSUE_NO_ACCESS");
    setLS(TOKEN_KEYS.access, newAccess);
    if (newRefresh) setLS(TOKEN_KEYS.refresh, newRefresh);
    setLS(TOKEN_KEYS.adminAccess, newAccess);
    return { accessToken: newAccess, refreshToken: newRefresh };
  }

  // 3) 둘 다 못하면 실패
  throw new Error("NO_REFRESH_TOKEN");
}

// ===== 응답 인터셉터 =====
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const { config, response } = error;
    const original = config || {};

    // 무한 루프 방지 플래그
    if (!original.__retry) original.__retry = { tried401: false };

    // 401만 자동 처리
    if (response && response.status === 401 && !original.__retry.tried401) {
      original.__retry.tried401 = true;

      try {
        // 싱글 플라이트: 동시에 많은 401 발생 시 한 번만 재발급
        if (!isRefreshing) {
          isRefreshing = true;
          refreshPromise = reissueTokens()
            .catch((e) => {
              // 재발급 실패 → 토큰 삭제 + 로그인 이동
              clearTokens();
              // 프론트 라우터 기준 로그인 화면으로 이동
              try { window.location.href = FRONT_LOGIN_ROUTE; } catch {}
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
        // 최종 실패: 그대로 에러 반환
        return Promise.reject(e);
      }
    }

    // 그 외 상태코드는 그대로
    return Promise.reject(error);
  }
);

// ✅ default export + named export 둘 다 제공
export default api;
