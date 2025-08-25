// 파일: frontend/src/program/login/components/KakaoLoginButton.jsx
// 목적: "카카오로 로그인" 버튼을 누르면 Kakao OAuth 인가 페이지(kauth.kakao.com)로 이동시킨다.
// 사용 위치 예시: 로그인 페이지 내 <KakaoLoginButton /> 렌더링
//
// 📌 전략(간단/안전):
//  1) 프론트 → 카카오 인가 페이지로 이동(사용자 동의)
//  2) 카카오 → 프론트의 /oauth/kakao/callback?code=... 로 리다이렉트
//  3) 프론트 콜백 페이지에서 code를 백엔드(/kakao/callback)로 전달(다음 파일에서 구현)
//
import React, { useCallback } from "react";

export default function KakaoLoginButton({ children }) {
  const KAKAO_CLIENT_ID = process.env.REACT_APP_KAKAO_REST_API_KEY;
  const REDIRECT_URI = process.env.REACT_APP_KAKAO_REDIRECT_URI;

  // 동의 요청할 scope 목록(필요한 것만 추가)
  // - phone_number는 별도 심사/동의 항목. 콘솔에서 권한 미승인 상태면 내려오지 않을 수 있음.
  const SCOPE = [
    "profile_nickname",
    "account_email",
    "gender",
    "birthday",
    "birthyear",
    "phone_number",
  ].join(" ");

  const handleKakaoLogin = useCallback(() => {
    if (!KAKAO_CLIENT_ID || !REDIRECT_URI) {
      alert(
        "카카오 설정 값이 없습니다. .env에 REACT_APP_KAKAO_REST_API_KEY, REACT_APP_KAKAO_REDIRECT_URI를 확인하세요."
      );
      return;
    }

    // Kakao OAuth2 Authorize URL
    const authorizeUrl = new URL("https://kauth.kakao.com/oauth/authorize");
    authorizeUrl.searchParams.set("response_type", "code");
    authorizeUrl.searchParams.set("client_id", KAKAO_CLIENT_ID);
    authorizeUrl.searchParams.set("redirect_uri", REDIRECT_URI);
    authorizeUrl.searchParams.set("scope", SCOPE);
    // 사용자에게 매번 계정 선택/동의창을 강제하려면 prompt=login 유지(선택)
    authorizeUrl.searchParams.set("prompt", "login");

    // 인가 페이지로 이동
    window.location.href = authorizeUrl.toString();
  }, [KAKAO_CLIENT_ID, REDIRECT_URI]);

  return (
    <div className="kakao_login bth_item">
      <button
        type="button"
        className="login_btn kakao_btn"
        onClick={handleKakaoLogin}
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 20 20"
          fill="none"
          class="login-v2-button__item__logo"
        >
          <title>kakao 로고</title>
          <path
            fill-rule="evenodd"
            clip-rule="evenodd"
            d="M9.96052 3C5.83983 3 2.5 5.59377 2.5 8.79351C2.5 10.783 3.79233 12.537 5.75942 13.5807L4.9313 16.6204C4.85835 16.8882 5.1634 17.1029 5.39883 16.9479L9.02712 14.5398C9.33301 14.5704 9.64386 14.587 9.96052 14.587C14.0812 14.587 17.421 11.9932 17.421 8.79351C17.421 5.59377 14.0812 3 9.96052 3Z"
            fill="black"
          ></path>
        </svg>
        <span>{children ?? "카카오 로그인"}</span>
      </button>
    </div>
  );
}
