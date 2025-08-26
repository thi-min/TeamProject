// 목적: 카카오 인증 code 수신 → 백엔드에 교환 요청 → 결과에 따라
// - 신규: /join 으로 이동 (state에 kakao 관련 값 전달)
// - 기존: 토큰 저장 후 홈("/") 이동
//
// 의존: react-router-dom, localStorage (또는 프로젝트의 토큰 저장 유틸)
// 백엔드 기대 응답(예시):
//  200 OK, JSON:
//   {
//     "status": "NEW" | "EXISTING",
//     "accessToken": "xxx" | null,
//     "refreshToken": "yyy" | null,
//     "member": {
//        "memberId": "email@example.com" | null,
//        "kakaoId": "1234567890",
//        "nickname": "홍길동" | null,
//        "email": "email@example.com" | null
//     }
//   }
//
// 백엔드가 아직 준비 전이라면 에러가 나더라도 "/join"으로 code만 들고 이동하게 폴백 처리함.

import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const API_BASE = process.env.REACT_APP_API_BASE || "http://127.0.0.1:8090";

export default function KakaoCallbackPage() {
  const navigate = useNavigate();
  const { search } = useLocation();
  const [msg, setMsg] = useState("카카오 로그인 처리 중...");

  // URL 파라미터에서 code 추출
  const code = useMemo(() => {
    const sp = new URLSearchParams(search);
    return sp.get("code");
  }, [search]);

  useEffect(() => {
    if (!code) {
      setMsg("카카오 인증 코드가 없습니다.");
      // 안전하게 로그인 페이지 또는 메인으로 돌려보냄
      const timer = setTimeout(
        () => navigate("/login", { replace: true }),
        1200
      );
      return () => clearTimeout(timer);
    }

    const run = async () => {
      try {
        // 1) 백엔드로 교환 요청
        const finalUrl = `${API_BASE}/kakao/callback?code=${encodeURIComponent(
          code
        )}`;
        // 필요한 경우 credentials 포함 (백엔드 세션/쿠키 사용 시)
        const res = await fetch(finalUrl, {
          method: "GET",
          headers: { Accept: "application/json" },
        });

        // 2) 백엔드 응답 분기
        if (!res.ok) {
          // 백엔드 준비 전이거나 오류 시 → 폴백: /join으로 code만 넘김
          console.warn("[KakaoCallback] backend error status:", res.status);
          navigate("/join", {
            replace: true,
            state: {
              from: "kakao",
              // 프론트에서 /join에서 교환하도록 하거나, 사용자가 가입 진행하도록 유도
              kakaoAuthCode: code,
              prefill: { snsYn: true },
            },
          });
          return;
        }

        const data = await res.json();
        // 기대 구조 참고:
        // const { status, accessToken, refreshToken, member } = data;

        if (data.status === "EXISTING") {
          // 3-A) 기존 회원: 토큰 저장 후 홈 이동
          if (data.accessToken)
            localStorage.setItem("accessToken", data.accessToken);
          if (data.refreshToken)
            localStorage.setItem("refreshToken", data.refreshToken);

          // 필요 시 사용자 정보 저장(프로젝트 공통 스토리지 규약에 맞게)
          if (data.member) {
            localStorage.setItem("memberId", data.member.memberId ?? "");
            localStorage.setItem("memberName", data.member.nickname ?? "");
          }

          setMsg("로그인 완료! 이동합니다...");
          navigate("/", { replace: true });
          return;
        }

        // 3-B) 신규 회원: /join 으로 이동하면서 카카오 정보 전달
        // - memberId로 이메일을 쓸지, kakaoId를 쓸지는 /join에서 정책 적용
        // - 기본적으로 snsYn(true) 설정
        const kakaoId = data?.member?.kakaoId ?? null;
        const email = data?.member?.email ?? null;
        const nickname = data?.member?.nickname ?? null;

        navigate("/join", {
          replace: true,
          state: {
            from: "kakao",
            kakaoId,
            email,
            nickname,
            prefill: {
              memberId: email ?? kakaoId ?? "", // 선호 정책: 이메일 우선, 없으면 kakaoId
              memberName: nickname ?? "",
              snsYn: true, // 문자 수신 동의 기본값(요청사항 반영)
            },
          },
        });
      } catch (err) {
        // 네트워크 오류 등 → 폴백: /join으로 code만 넘김
        console.error("[KakaoCallback] error:", err);
        navigate("/join", {
          replace: true,
          state: {
            from: "kakao",
            kakaoAuthCode: code,
            prefill: { snsYn: true },
          },
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
