// src/common/utils/currentMemberId.js
// 목적:
//  - 현재 로그인한 "회원 ID"를 프론트에서 자동 추론한다.
//  - 로컬/세션 스토리지의 토큰과 보편적인 키 이름(memberId 등)을 광범위하게 검사한다.
//  - JWT가 있으면 jwt-decode로 디코딩하여 sub/memberId/email/userId/username 순으로 추출한다.
// 사용 방법:
//  - import { guessCurrentMemberId, useCurrentMemberId } from "…/currentMemberId";
//  - const id = guessCurrentMemberId(); // 함수형
//  - const id = useCurrentMemberId();   // 리액트 훅형
//
// 주의:
//  - 프로젝트별 토큰/클레임 이름이 다를 수 있어, 아래 candidates / claimCandidates는 필요 시 추가.
//  - jwt-decode v4는 named export(jwtDecode) 사용.
//  - 이 유틸이 null을 반환하면, 실제로 "로그인 상태 아님" 이거나 저장 로직이 다른 곳에 있을 수 있으니 점검 필요.

import { useMemo } from "react";
import { jwtDecode } from "jwt-decode";

// 내부 유틸: 객체에서 "아이디로 쓸 수 있는 필드"를 최대한 뽑아낸다.
function extractIdFromObject(obj) {
  if (!obj || typeof obj !== "object") return null;
  // 흔한 키 후보들 (프로젝트에 맞춰 추가/정리 가능)
  const claimCandidates = [
    "memberId",
    "email",
    "sub",
    "userId",
    "username",
    "id",
    "loginId",
  ];
  for (const key of claimCandidates) {
    const v = obj[key];
    if (typeof v === "string" && v.trim()) {
      return v.trim();
    }
  }
  return null;
}

// 내부 유틸: JSON 파싱을 안전하게 시도
function tryParseJSON(value) {
  try {
    return JSON.parse(value);
  } catch {
    return null;
  }
}

/**
 * 현재 로그인한 회원 ID를 최대한 "동기적으로" 추론한다.
 * 1) localStorage/sessionStorage에서 흔한 키 조회(memberId 등)
 * 2) accessToken/jwt/token 등 토큰을 디코드하여 sub/memberId/email 등 추출
 * 3) JSON으로 저장해 둔 user/auth/profile 같은 객체에서 키 추출
 * 실패 시 null 반환
 */
export function guessCurrentMemberId() {
  // 1) 명시적으로 저장했을 법한 키들 먼저 확인
  const explicitIdKeys = ["memberId", "loginId", "email", "userId", "username"];
  for (const k of explicitIdKeys) {
    const v = localStorage.getItem(k) || sessionStorage.getItem(k);
    if (typeof v === "string" && v.trim()) return v.trim();
  }

  // 2) 토큰 디코드 시도 (주요 키 이름들)
  const tokenKeys = ["accessToken", "jwt", "token", "id_token"];
  for (const k of tokenKeys) {
    const raw = localStorage.getItem(k) || sessionStorage.getItem(k);
    if (!raw) continue;
    try {
      const decoded = jwtDecode(raw);
      const idFromClaims = extractIdFromObject(decoded);
      if (idFromClaims) return idFromClaims;
    } catch {
      // 무시 (다음 후보로 진행)
    }
  }

  // 3) JSON으로 저장된 사용자 정보 객체에서 추출 시도
  const jsonKeys = ["user", "auth", "profile", "currentUser", "member"];
  for (const k of jsonKeys) {
    const raw = localStorage.getItem(k) || sessionStorage.getItem(k);
    if (!raw) continue;
    const parsed = tryParseJSON(raw);
    const idFromObj = extractIdFromObject(parsed);
    if (idFromObj) return idFromObj;
  }

  // 못 찾으면 null
  return null;
}

/**
 * React 훅 버전: 컴포넌트에서 간단히 사용
 * - 최초 마운트 시 한 번 계산하여 고정(로그인 상태가 바뀔 수 있으면 의도적으로 리렌더 트리거 필요)
 */
export function useCurrentMemberId() {
  return useMemo(() => guessCurrentMemberId(), []);
}
