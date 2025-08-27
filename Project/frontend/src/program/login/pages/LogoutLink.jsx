import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../common/context/AuthContext";
import api from "../../../common/api/axios";

export function LogoutLink({
  className = "user_item logout",
  children = "로그아웃",
}) {
  const { logout } = useAuth();
  const nav = useNavigate();
  const [loading, setLoading] = useState(false);

  const onLogout = async () => {
    if (loading) return;

    try {
      setLoading(true);

      // ✅ 가드 알림 억제 플래그 세팅 (이동 과정에서 '로그인 해주세요' 뜨는 것 방지)
      sessionStorage.setItem("logoutJustNow", "1");

      // 서버의 httpOnly refresh 쿠키 제거 (실패해도 진행)
      try {
        await api.post("/auth/logout");
      } catch {}

      // 전역 인증 상태 해제 (로컬 토큰/상태 초기화)
      logout();

      // ✅ 사용자에게 먼저 알림
      alert("로그아웃되었습니다");

      // 홈으로 이동
      nav("/", { replace: true });
    } finally {
      setLoading(false);
    }
  };

  return (
    <button
      type="button"
      className={className}
      onClick={onLogout}
      disabled={loading}
    >
      {loading ? "로그아웃 중..." : children}
    </button>
  );
}
