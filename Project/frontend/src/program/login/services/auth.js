// frontend/src/program/auth/api/auth.js
import api from "../../../common/api/axios";

/**
 * ✅ 로그인 API 호출 함수
 * - axios 인스턴스(api) 사용 → baseURL: http://localhost:8090
 * - 응답 바디와 헤더 모두에서 토큰을 추출
 */
export async function loginUser({ memberId, memberPw }) {
  try {
    // axios는 JSON 자동 변환 + 기본 Content-Type 지정
    const res = await api.post("/auth/login", { memberId, memberPw });

    // axios 응답 구조
    // res.data  → 응답 바디(JSON)
    // res.headers → 응답 헤더 객체 (소문자 키)

    const data = res.data;

    // 1) 헤더에서 accessToken 시도: Authorization: Bearer xxx
    const authHeader = res.headers["authorization"];
    const headerAccess = authHeader?.startsWith("Bearer ")
      ? authHeader.replace(/^Bearer\s+/i, "")
      : null;

    // 2) 헤더에서 refreshToken 시도
    const headerRefresh =
      res.headers["x-refresh-token"] || res.headers["X-Refresh-Token"];

    // 3) 바디에서 시도
    const bodyAccess =
      data.accessToken ??
      data.token ??
      data.jwt ??
      data?.member?.accessToken ??
      null;

    const bodyRefresh =
      data.refreshToken ?? data?.member?.refreshToken ?? null;

    // 최종 토큰 결정 (헤더 우선)
    const accessToken = headerAccess ?? bodyAccess ?? null;
    const refreshToken = headerRefresh ?? bodyRefresh ?? null;

    // 디버깅 로그
    console.log("[auth.js] res data:", data);
    console.log("[auth.js] parsed tokens:", { accessToken, refreshToken });

    return { accessToken, refreshToken, data };
  } catch (err) {
    // axios 에러 처리
    const serverMsg =
      err?.response?.data?.message ||
      err?.message ||
      "로그인 요청 실패";
    throw new Error(serverMsg);
  }
}
