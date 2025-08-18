// frontend/src/program/admin/api/adminAuth.js
// ✅ axios 인스턴스(api) 사용으로 통일
import api from "../../../common/api/axios";

/**
 * 관리자 로그인
 * @param {{ adminId: string, adminPw: string }} params
 * @returns {Promise<{ accessToken: string|null, refreshToken: string|null, data: any }>}
 */
export async function adminLogin({ adminId, adminPw }) {
  try {
    // 최종 요청: http://localhost:8090/admin/login (axios baseURL 기준)
    const res = await api.post("/admin/login", { adminId, adminPw });

    const data = res.data;

    // 헤더에서 토큰을 내려주는 경우 대비 (대부분 바디로 내려줌)
    const authHeader = res.headers["authorization"];
    const headerAccess = authHeader?.startsWith("Bearer ")
      ? authHeader.replace(/^Bearer\s+/i, "")
      : null;
    const headerRefresh = res.headers["x-refresh-token"];

    // 바디에서 토큰
    const bodyAccess  = data?.accessToken ?? data?.token ?? null;
    const bodyRefresh = data?.refreshToken ?? null;

    const accessToken  = headerAccess ?? bodyAccess ?? null;
    const refreshToken = headerRefresh ?? bodyRefresh ?? null;

    return { accessToken, refreshToken, data };
  } catch (err) {
    const serverMsg =
      err?.response?.data?.message ||
      err?.message ||
      "관리자 로그인 요청 실패";
    throw new Error(serverMsg);
  }
}
