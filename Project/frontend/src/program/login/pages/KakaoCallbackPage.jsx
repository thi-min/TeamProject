// 목적: 카카오 콜백 ?code=... 수신 → 백엔드 /kakao/callback 호출 → 분기
// - 회원 있음: { action:"signin_ok", redirect:"/member/mypage", accessToken, refreshToken, ... }
//   → 토큰 저장(axios 전역 주입) 후 "카카오 계정으로 로그인 되었습니다" 알림 → redirect 이동
// - 회원 없음: { action:"go_join", via:"kakao", kakaoId, prefill:{...} }
//   → "가입된 정보가 없습니다 회원가입을 진행해주세요" 알림 → /join 이동(state+sessionStorage 백업)
// - 레거시 포맷도 지원: { login:true } / { signupRequired:true }
//
// ⚠ React.StrictMode로 콜백이 2번 실행되는 경우를 막기 위해
//   같은 code에 대해 1회만 실행하도록 세션키를 사용한다.
// ⚠ 일반 가입과 카카오 가입을 구분하기 위해 kakao_flow 플래그를 세션에 관리한다.

import React, { useEffect, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api, { saveTokens } from "../../../common/api/axios";
import routes from "../../../common/routes/router";

const KAKAO_PREFILL_KEY = "kakao_prefill_v1";
const KAKAO_FLOW_FLAG = "kakao_flow";

export default function KakaoCallbackPage() {
  const navigate = useNavigate();
  const { search } = useLocation();

  const API_BASE = (
    process.env.REACT_APP_API_BASE || "http://127.0.0.1:8090"
  ).replace(/\/+$/, "");

  const MYPAGE_PATH = routes?.member?.mypage?.path || "/member/mypage";
  const JOIN_PATH = routes?.member?.join?.path || "/join";

  // StrictMode 가드
  const hasRunRef = useRef(false);

  useEffect(() => {
    const params = new URLSearchParams(search);
    const code = params.get("code");

    if (!code) {
      navigate("/", { replace: true });
      return;
    }

    // 같은 code 두번 처리 방지
    const USED_KEY = `kakao_code_used:${code}`;
    if (hasRunRef.current) return;
    hasRunRef.current = true;
    if (sessionStorage.getItem(USED_KEY) === "1") return;

    const run = async () => {
      try {
        const url = `${API_BASE}/kakao/callback?code=${encodeURIComponent(
          code
        )}`;

        // 공용 api 인스턴스 사용(헤더/쿠키 일관성)
        const res = await api.get(url);
        const data = res?.data;
        console.log("[KakaoCallback] response:", data);

        // 재실행 방지 플래그 저장(성공/실패 모두)
        sessionStorage.setItem(USED_KEY, "1");

        // ─────────────────────────────────────────────
        // ✅ 회원 존재 → 로그인 완료
        // ─────────────────────────────────────────────
        if (data?.action === "signin_ok" || data?.login === true) {
          // 토큰 전역 주입(axios 기본헤더 + localStorage 동기화)
          if (data?.accessToken) {
            saveTokens({
              accessToken: data.accessToken,
              refreshToken: data.refreshToken,
              alsoAdmin: true,
            });
            if (data?.memberId) localStorage.setItem("memberId", data.memberId);
            if (data?.role) localStorage.setItem("role", data.role);
            // 가드 레이스 방지 이벤트
            try {
              window.dispatchEvent(new Event("auth:login"));
            } catch {}
          }

          // ✅ 카카오 플로우 흔적 제거(일반 가입에 영향 방지)
          try {
            sessionStorage.removeItem(KAKAO_FLOW_FLAG);
            sessionStorage.removeItem(KAKAO_PREFILL_KEY);
          } catch {}

          // ✅ 사용자 알림 후 이동
          alert("카카오 계정으로 로그인 되었습니다.");
          // 하드 리다이렉트로 새 부팅 → 초기 API/가드가 토큰을 확실히 인식
          const to = data?.redirect || MYPAGE_PATH;
          window.location.replace(to);
          return;
        }

        // ─────────────────────────────────────────────
        // ✅ 회원 없음 → 약관/회원가입으로
        // ─────────────────────────────────────────────
        if (data?.action === "go_join" || data?.signupRequired === true) {
          const pf = data?.prefill || {};
          // Join/Signup에서 기대하는 키로 정규화
          const joinState = {
            via: "kakao",
            kakaoId: data?.kakaoId ?? null,
            email: data?.email ?? pf.email ?? pf.memberId ?? null,
            name: data?.name ?? pf.name ?? pf.memberName ?? null, // nickname X, name O
            gender: data?.gender ?? pf.gender ?? pf.memberSex ?? null, // male/female or MAN/WOMAN
            birthday: data?.birthday ?? pf.birthday ?? null, // "MMDD"
            birthyear: data?.birthyear ?? pf.birthyear ?? null, // "YYYY"
            phoneNumber:
              data?.phoneNumber ?? pf.memberPhone ?? pf.phoneNumber ?? null,
          };

          // 새로고침 대비 백업 + 카카오 플로우 플래그 세팅
          try {
            sessionStorage.setItem(
              KAKAO_PREFILL_KEY,
              JSON.stringify(joinState)
            );
            sessionStorage.setItem(KAKAO_FLOW_FLAG, "1");
          } catch {}

          // ✅ 사용자 알림 후 이동
          alert("가입된 정보가 없습니다 회원가입을 진행해주세요");
          navigate(JOIN_PATH, { replace: true, state: joinState });
          return;
        }

        // ─────────────────────────────────────────────
        // 해석 불가
        // ─────────────────────────────────────────────
        alert("카카오 로그인을 해석할 수 없습니다. 잠시후 다시 실행해주세요");
        navigate("/", { replace: true });
      } catch (err) {
        console.error("[KakaoCallback] error:", err);
        sessionStorage.setItem(`kakao_code_used:${code}`, "1");
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
