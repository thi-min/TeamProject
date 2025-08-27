// 목적: 카카오 인증 code 수신 → 백엔드 교환 → 결과에 따라
//  - 기존회원: 토큰 저장 후 마이페이지(or 홈) 이동
//  - 신규회원: 프리필을 sessionStorage에 저장하고 /join(약관) → /join/sigup 로 이어짐
//
// 개선점:
//  1) ✅ React 18 개발모드의 useEffect 중복 실행 완전 차단 (ranRef)
//  2) ✅ 카카오 code 재사용으로 400(KOE320)일 때, 인가 페이지로 1회 자동 재시도 (retry guard)
//  3) ✅ 백엔드 응답 포맷을 "LOGIN/SIGNUP"과 "EXISTING/NEW" 모두 호환 처리
//  4) ✅ .env 키 이름 혼용 지원: REACT_APP_API_BASE_URL, VITE_API_BASE_URL 등
//
// 필요 env(.env):
//   REACT_APP_API_BASE_URL=http://127.0.0.1:8090
//   REACT_APP_KAKAO_REST_API_KEY=카카오REST키
//   REACT_APP_KAKAO_REDIRECT_URI=http://127.0.0.1:3000/oauth/kakao/callback

import React, { useEffect, useMemo, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const API_BASE =
  process.env.REACT_APP_API_BASE_URL ||
  process.env.REACT_APP_API_BASE ||
  (import.meta?.env?.VITE_API_BASE_URL ?? "http://127.0.0.1:8090");

const KAKAO_CLIENT_ID =
  process.env.REACT_APP_KAKAO_REST_API_KEY ||
  import.meta?.env?.VITE_KAKAO_REST_KEY ||
  "";

const KAKAO_REDIRECT_URI =
  process.env.REACT_APP_KAKAO_REDIRECT_URI ||
  import.meta?.env?.VITE_KAKAO_REDIRECT_URI ||
  "http://127.0.0.1:3000/oauth/kakao/callback";

const AUTH_URL = "https://kauth.kakao.com/oauth/authorize";
const SCOPES = [
  "account_email",
  "profile_nickname",
  "phone_number",
  "gender",
  "birthday",
  "birthyear",
];

const PREFILL_KEY = "kakao_prefill_v1";
const RETRY_ONCE_KEY = "kakao_retry_once";

function buildAuthorizeUrl() {
  const state = Math.random().toString(36).slice(2) + Date.now().toString(36);
  try {
    sessionStorage.setItem("kakao_oauth_state", state);
  } catch {}
  const p = new URLSearchParams({
    response_type: "code",
    client_id: KAKAO_CLIENT_ID,
    redirect_uri: KAKAO_REDIRECT_URI,
    scope: SCOPES.join(" "),
    prompt: "select_account",
    state,
  });
  return `${AUTH_URL}?${p.toString()}`;
}

export default function KakaoCallbackPage() {
  const navigate = useNavigate();
  const { search } = useLocation();
  const [msg, setMsg] = useState("카카오 로그인 처리 중...");
  const ranRef = useRef(false); // ⛔️ 중복 실행 가드

  // code 추출
  const code = useMemo(() => {
    const sp = new URLSearchParams(search);
    return sp.get("code");
  }, [search]);

  // 디버그
  useEffect(() => {
    // 한 번만 찍자
    console.log("[KakaoCallback] API_BASE =", API_BASE);
  }, []);

  useEffect(() => {
    if (ranRef.current) return;
    ranRef.current = true;

    if (!code) {
      setMsg("카카오 인증 코드가 없습니다.");
      const t = setTimeout(() => navigate("/login", { replace: true }), 1200);
      return () => clearTimeout(t);
    }

    const run = async () => {
      try {
        const url = `${API_BASE}/kakao/callback?code=${encodeURIComponent(
          code
        )}`;

        const res = await fetch(url, {
          method: "GET",
          headers: { Accept: "application/json" },
        });

        if (!res.ok) {
          const txt = await res.text().catch(() => "");
          // 🔁 invalid_grant(KOE320) → code 재발급 1회 자동 재시도
          if (res.status === 400 && /invalid_grant|KOE320/i.test(txt || "")) {
            const retried = sessionStorage.getItem(RETRY_ONCE_KEY) === "1";
            if (!retried) {
              try {
                sessionStorage.setItem(RETRY_ONCE_KEY, "1");
                sessionStorage.removeItem(PREFILL_KEY);
              } catch {}
              window.location.replace(buildAuthorizeUrl());
              return;
            }
          }
          // 폴백: 약관으로 넘기되 프리필은 없음(사용자가 수동 입력)
          console.warn("[KakaoCallback] backend error:", res.status, txt);
          navigate("/join", {
            replace: true,
            state: { from: "kakao", kakaoAuthCode: code },
          });
          return;
        }

        // ✅ 성공 응답 파싱 (두 포맷 모두 허용)
        const data = await res.json();

        // --- 포맷 A: 우리가 설계한 형태 ---
        // { status:"LOGIN"|"SIGNUP", accessToken, refreshToken, kakaoId, prefill:{memberName, memberId, memberPhone, birth, sex} }
        if (data?.status === "LOGIN") {
          try {
            if (data.accessToken)
              localStorage.setItem("accessToken", data.accessToken);
            if (data.refreshToken)
              localStorage.setItem("refreshToken", data.refreshToken);
            if (data.memberId) localStorage.setItem("memberId", data.memberId);
            if (data.memberName)
              localStorage.setItem("memberName", data.memberName);
          } catch {}
          navigate("/member/mypage", { replace: true });
          return;
        }
        if (data?.status === "SIGNUP") {
          const payload = {
            kakaoId: data?.kakaoId || "",
            prefill: {
              memberName: data?.prefill?.memberName || "",
              memberId: (data?.prefill?.memberId || "").toLowerCase(),
              memberPhone: data?.prefill?.memberPhone || "",
              memberBirth: data?.prefill?.birth || "",
              memberSex: data?.prefill?.sex || "",
            },
            via: "kakao",
          };
          try {
            sessionStorage.setItem(PREFILL_KEY, JSON.stringify(payload));
            sessionStorage.removeItem(RETRY_ONCE_KEY);
          } catch {}
          navigate("/join", { replace: true });
          return;
        }

        // --- 포맷 B: 네 현재 파일에 적힌 형태 ---
        // { status:"EXISTING"|"NEW", accessToken, refreshToken, member:{ memberId, kakaoId, nickname, email } }
        if (data?.status === "EXISTING") {
          try {
            if (data.accessToken)
              localStorage.setItem("accessToken", data.accessToken);
            if (data.refreshToken)
              localStorage.setItem("refreshToken", data.refreshToken);
            if (data.member?.memberId)
              localStorage.setItem("memberId", data.member.memberId);
            if (data.member?.nickname)
              localStorage.setItem("memberName", data.member.nickname);
          } catch {}
          navigate("/member/mypage", { replace: true });
          return;
        }
        if (data?.status === "NEW") {
          const payload = {
            kakaoId: data?.member?.kakaoId || "",
            prefill: {
              memberId: (
                data?.member?.email ||
                data?.member?.kakaoId ||
                ""
              ).toLowerCase(),
              memberName: data?.member?.nickname || "",
              memberPhone: "",
              memberBirth: "",
              memberSex: "",
            },
            via: "kakao",
          };
          try {
            sessionStorage.setItem(PREFILL_KEY, JSON.stringify(payload));
            sessionStorage.removeItem(RETRY_ONCE_KEY);
          } catch {}
          navigate("/join", { replace: true });
          return;
        }

        // 알 수 없는 포맷 → 약관으로만 이동
        navigate("/join", { replace: true, state: { from: "kakao" } });
      } catch (e) {
        console.error("[KakaoCallback] error:", e);
        navigate("/join", {
          replace: true,
          state: { from: "kakao", kakaoAuthCode: code },
        });
      }
    };

    run();
  }, [code, navigate]);

  return (
    <div className="oauth_callback page_center">
      <p className="text_md">{msg}</p>
    </div>
  );
}
