// 목적: 카카오 콜백 ?code=... 수신 → 백엔드 /kakao/callback 호출 → 분기
// - 회원 없음(signupRequired=true): /join 이동 + 프리필 "모든 필드" 전달
// - 회원 있음(login=true): 토큰 저장 후 /member/mypage 이동
// - StrictMode 중복 호출 가드(같은 code 2회요청 방지)

import React, { useEffect, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import routes from "../../../common/routes/router";

export default function KakaoCallbackPage() {
  const navigate = useNavigate();
  const { search } = useLocation();

  const API_BASE =
    process.env.REACT_APP_API_BASE?.replace(/\/+$/, "") ||
    "http://127.0.0.1:8090";

  const MYPAGE_PATH = routes?.member?.mypage?.path || "/member/mypage";
  const JOIN_PATH = routes?.member?.join?.path || "/join";

  const hasRunRef = useRef(false);

  useEffect(() => {
    const params = new URLSearchParams(search);
    const code = params.get("code");
    if (!code) {
      navigate("/", { replace: true });
      return;
    }

    const USED_KEY = `kakao_code_used:${code}`;
    if (hasRunRef.current) return;
    hasRunRef.current = true;
    if (sessionStorage.getItem(USED_KEY) === "1") return;

    const run = async () => {
      try {
        const url = `${API_BASE}/kakao/callback?code=${encodeURIComponent(
          code
        )}`;
        const res = await axios.get(url, { withCredentials: true });
        const data = res?.data;
        sessionStorage.setItem(USED_KEY, "1");
        console.log("[KakaoCallback] response:", data);

        // 신규: /join으로 이동 + 프리필 전부 전달
        if (data?.signupRequired === true) {
          navigate(JOIN_PATH, {
            replace: true,
            state: {
              via: "kakao",
              kakaoId: data?.kakaoId ?? null,
              email: data?.email ?? null,
              name: data?.name ?? null, // ✅ nickname이 아니라 name
              gender: data?.gender ?? null,
              birthday: data?.birthday ?? null, // "MMDD"
              birthyear: data?.birthyear ?? null, // "YYYY"
              phoneNumber: data?.phoneNumber ?? null,
            },
          });
          return;
        }

        // 기존 회원: 로그인 완료 → 마이페이지
        if (data?.login === true) {
          if (data?.accessToken) {
            try {
              localStorage.setItem("accessToken", data.accessToken);
              if (data?.memberId)
                localStorage.setItem("memberId", data.memberId);
              if (data?.role) localStorage.setItem("role", data.role);
            } catch (e) {
              console.warn("[KakaoCallback] localStorage set failed:", e);
            }
          }
          navigate(MYPAGE_PATH, { replace: true });
          return;
        }

        alert(
          "카카오 로그인 응답을 해석할 수 없습니다. 잠시 후 다시 시도해주세요."
        );
        navigate("/", { replace: true });
      } catch (err) {
        console.error("[KakaoCallback] error:", err);
        sessionStorage.setItem(USED_KEY, "1");
        alert("카카오 로그인 처리 중 오류가 발생했습니다.");
        navigate("/", { replace: true });
      }
    };

    run();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

  return (
    <div className="callback-page">
      <div className="loading">카카오 로그인 처리 중입니다...</div>
    </div>
  );
}
