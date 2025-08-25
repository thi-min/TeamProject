// 파일: frontend/src/program/login/pages/KakaoCallbackPage.jsx
// 목적: 카카오 인증 후 리다이렉트된 콜백 페이지.
//  - URL 의 ?code=... 를 읽음
//  - 백엔드 /kakao/callback 으로 code 전달
//  - 응답이 requireSignup=false 이면: 토큰 저장 후 로그인 완료 처리
//  - 응답이 requireSignup=true  이면: 카카오 정보(pre-fill) 들고 회원가입 페이지로 이동
//
// 연동 규칙(백엔드와 합의한 응답 형태 가정 - 프로젝트 주석 기반):
//  MemberLoginResponseDto {
//    String memberId;        // 기존회원이면 일반 ID, 카카오면 kakaoId가 들어올 수 있음
//    String memberName;
//    String accessToken;
//    String refreshToken;
//    Boolean requireSignup;   // 신규면 true
//    // 신규일 때만 내려올 수 있는 프리필용 필드들 (아래 키들은 백엔드 주석을 따라 대응)
//    String kakaoId;
//    String gender;           // "male" | "female"
//    String birth;            // "yyyy-mm-dd"
//    String phone;            // "010-0000-0000"
//  }
//
// 동작 개요:
//  1) 쿼리스트링에서 code 추출
//  2) GET /kakao/callback?code=... 호출
//  3) requireSignup=false → localStorage에 access/refresh 저장 → 메인/마이페이지로 이동
//  4) requireSignup=true  → /signup 로 이동하면서 state에 프리필 데이터 전달
//
// 환경 변수(.env - 프론트):
//  - 없음(이 파일은 백엔드로만 호출)
//
// 주의 사항:
//  - 프로젝트의 토큰 저장 키 이름은 기존 AuthContext/로직에 맞춘다. (예: 'accessToken', 'refreshToken')
//  - 리다이렉트 경로는 프로젝트 정책에 맞게 수정 가능(아래 DEFAULT_REDIRECT, SIGNUP_PATH 상수)
//
// 에러 처리:
//  - code 없거나 API 실패 시 사용자에게 안내 및 로그인 페이지로 돌아가는 버튼 노출.

import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const DEFAULT_REDIRECT = "/"; // 로그인 성공 후 보낼 기본 경로(예: 메인 or /member/mypage)
const SIGNUP_PATH = "/signup"; // 신규회원일 때 보낼 경로(프로젝트의 회원가입 라우트에 맞춰 조정)

// 백엔드 베이스 경로가 프록시(CRA proxy)라면 상대경로 사용 가능: "/kakao/callback"
// 프록시가 없다면 절대경로로 수정: "http://127.0.0.1:8090/kakao/callback"
const KAKAO_CALLBACK_API = "/kakao/callback";

export default function KakaoCallbackPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const [status, setStatus] = useState("LOADING"); // LOADING | SUCCESS | SIGNUP | ERROR
  const [errorMsg, setErrorMsg] = useState("");

  // URLSearchParams에서 code 추출
  const code = useMemo(() => {
    const search = new URLSearchParams(location.search);
    return search.get("code");
  }, [location.search]);

  useEffect(() => {
    // code가 없는 경우 예외 처리
    if (!code) {
      setStatus("ERROR");
      setErrorMsg("카카오 인가 코드를 찾을 수 없습니다. (code 파라미터 누락)");
      return;
    }

    // 백엔드로 code 전달 → 로그인/신규 분기
    const fetchTokenOrSignup = async () => {
      try {
        // GET /kakao/callback?code=...
        const url = new URL(KAKAO_CALLBACK_API, window.location.origin);
        url.searchParams.set("code", code);

        const res = await fetch(url.toString(), {
          method: "GET",
          headers: {
            Accept: "application/json",
          },
          credentials: "include", // 필요 시 (쿠키 사용 등). 토큰은 보통 바디로 받으니 없어도 OK
        });

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || `서버 응답 오류(${res.status})`);
        }

        const data = await res.json();

        // 분기: 기존회원 로그인
        if (data && data.requireSignup === false) {
          // 토큰 저장 (프로젝트의 컨벤션에 맞춤)
          if (data.accessToken) {
            localStorage.setItem("accessToken", data.accessToken);
          }
          if (data.refreshToken) {
            localStorage.setItem("refreshToken", data.refreshToken);
          }

          setStatus("SUCCESS");

          // 이후 경로는 정책에 맞게 조정 (마이페이지 등)
          navigate(DEFAULT_REDIRECT, { replace: true });
          return;
        }

        // 분기: 신규 회원가입 필요
        // 회원가입 페이지로 이동하면서 state로 프리필 데이터 전달
        // 백엔드 주석 기준으로 들어올 수 있는 필드들을 가공
        const prefill = {
          kakaoId: data?.kakaoId ?? data?.memberId ?? "",
          memberName: data?.memberName ?? "",
          // "yyyy-mm-dd" → 이미 포맷된 값이라고 가정. 없으면 빈 값.
          memberBirth: data?.birth ?? "",
          // "010-0000-0000" → 하이픈 제거는 회원가입 폼에서 처리하거나 여기서 처리
          memberPhone: (data?.phone ?? "").replace(/[^0-9]/g, ""),
          // gender: "male"|"female" → 프로젝트 enum에 맞게 상응값으로 매핑은 폼에서 처리
          gender: data?.gender ?? "",
          // 필요 시 email, nickname 등 추가
        };

        setStatus("SIGNUP");
        navigate(SIGNUP_PATH, {
          replace: true,
          state: { mode: "kakao", prefill },
        });
      } catch (err) {
        console.error(err);
        setStatus("ERROR");
        setErrorMsg(
          err.message || "카카오 로그인 처리 중 오류가 발생했습니다."
        );
      }
    };

    fetchTokenOrSignup();
  }, [code, navigate]);

  if (status === "LOADING") {
    return (
      <div className="kakao_callback loading">
        <div className="title_box">
          <div className="title">카카오 로그인 처리중…</div>
        </div>
        <div className="desc">잠시만 기다려주세요.</div>
      </div>
    );
  }

  if (status === "ERROR") {
    return (
      <div className="kakao_callback error">
        <div className="title_box">
          <div className="title">카카오 로그인 실패</div>
        </div>
        <p className="desc">{errorMsg}</p>
        <button
          type="button"
          className="temp_btn"
          onClick={() => navigate("/login", { replace: true })}
        >
          로그인 페이지로 돌아가기
        </button>
      </div>
    );
  }

  // SUCCESS, SIGNUP 등은 useEffect에서 즉시 navigate 하므로
  // 사용자에게 보일 일은 거의 없음(안전빵으로 빈 레이아웃)
  return <div className="kakao_callback done" />;
}
