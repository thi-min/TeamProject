// frontend/src/common/api/axios.js
// ---------------------------------------------------------
// 공개 API(아이디/휴대폰 중복체크)는 Authorization 헤더 없이 호출.
// 보호 API만 토큰을 붙이도록 인터셉터에서 분기 처리한다.
// ---------------------------------------------------------
import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8090", // 백엔드 주소
});

// 요청 인터셉터: 보호 API만 Authorization 헤더를 붙임
api.interceptors.request.use((config) => {
  const access = localStorage.getItem("accessToken");

  // 🔓 공개 API(중복체크)는 헤더 제거
  const isPublic =
    config.url?.startsWith("/member/check-id") ||
    config.url?.startsWith("/member/check-phone");

  if (!isPublic && access) {
    config.headers["Authorization"] = `Bearer ${access}`;
  } else {
    delete config.headers["Authorization"];
  }
  return config;
});

export default api;
