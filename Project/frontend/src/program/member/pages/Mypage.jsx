// src/program/member/pages/Mypage.jsx
// 목적:
//  - 내 정보 호출 결과로 "카카오 로그인 회원" 여부를 판별
//  - 카카오 회원이면 "비밀번호 변경" 링크를 클릭 불가로 막고, 클릭 시 알림 표출
//  - 화면에 로그인 유형(카카오/일반)도 함께 노출
//
// 판별 로직:
//  - myInfo.snsYn === true / "Y"
//  - 또는 myInfo.kakaoId 존재
//  - 또는 myInfo.snsType / provider 가 "KAKAO"
//
// ※ 백엔드 응답 필드명이 프로젝트마다 다를 수 있어 다중 키를 안전하게 체크합니다.

import React, { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import MemberDeleteButton from "./MemberDeleteButton";
import { useAuth } from "../../../common/context/AuthContext";
import api from "../../../common/api/axios";

export default function Mypage() {
  // 전역 인증 상태
  const { isLogin } = useAuth();

  // 라우팅
  const nav = useNavigate();

  // 화면 상태
  const [myInfo, setMyInfo] = useState(null); // { memberNum, memberId, memberName, ... }
  const [loading, setLoading] = useState(true);

  /**
   * 1) 비로그인 즉시 "/"로 이동
   */
  useEffect(() => {
    if (!isLogin) {
      nav("/", { replace: true });
    }
  }, [isLogin, nav]);

  /**
   * 2) 로그인 상태에서만 내 정보 요청
   */
  useEffect(() => {
    if (!isLogin) {
      setLoading(false);
      return;
    }

    let cancelled = false;
    setLoading(true);

    (async () => {
      try {
        // Authorization은 axios 인터셉터가 자동 부착
        const res = await api.get("/member/mypage/me");
        if (!cancelled) setMyInfo(res.data);
      } catch (e) {
        console.error(
          "[MyPage] /member/mypage/me 실패:",
          e?.response?.status,
          e?.message
        );
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [isLogin]);

  // ✅ 카카오 회원 여부 판별 (다중 키를 안전하게 체크)
  const isKakaoMember = useMemo(() => {
    if (!myInfo) return false;

    const snsYn =
      myInfo.snsYn === true || String(myInfo.snsYn || "").toUpperCase() === "Y";

    const kakaoId = !!myInfo.kakaoId;

    const provider =
      String(myInfo.provider || myInfo.snsType || "").toUpperCase() === "KAKAO";

    return snsYn || kakaoId || provider;
  }, [myInfo]);

  // 화면용 라벨
  const loginTypeLabel = isKakaoMember ? "카카오 로그인" : "일반 로그인";

  // 카카오 회원의 비밀번호 변경 클릭 차단 + 알림
  const handleBlockedPwChange = (e) => {
    e.preventDefault();
    window.alert("카카오회원은 비밀번호 변경을 사용하실 수 없습니다.");
  };

  // 로딩 중 UI
  if (loading) {
    return <div className="admin_page">로딩중...</div>;
  }

  // 안전 가드
  if (!myInfo) {
    return <div className="admin_page">정보를 불러올 수 없습니다.</div>;
  }

  return (
    <div className="my_page">
      <div className="title_box">
        <div className="title">마이 페이지</div>
        {/* 로그인 유형 뱃지 표출 */}
        <div className="sub_title">
          로그인 유형: <strong>{loginTypeLabel}</strong>
        </div>
      </div>

      <div className="member_area">
        <ul className="my_menu">
          <li className="link_item type1">
            {isKakaoMember ? (
              // 🔒 카카오 회원 → 클릭 시 알림만, 페이지 이동 막음
              <a href="#!" onClick={handleBlockedPwChange}>
                비밀번호 변경
              </a>
            ) : (
              // 🔓 일반 회원 → 정상 이동
              <Link to="/member/update-password" state={{ mode: "self" }}>
                비밀번호 변경
              </Link>
            )}
          </li>

          <li className="link_item type2">
            <Link to="/member/mypage/memberdata">회원 정보</Link>
          </li>
          <li className="link_item type3">
            <Link to="/member/mypage/reserves">예약 내역 조회</Link>
          </li>
          <li className="link_item type4">
            <Link to="/member/adopt/list">입양 내역 조회</Link>
          </li>
          <li className="link_item type5">
            <Link to="/member/funds/list">후원 내역 조회</Link>
          </li>
          <li className="link_item type6">
            <MemberDeleteButton
              memberNum={myInfo?.memberNum}
              className="form_flex"
            />
          </li>
        </ul>

      </div>
    </div>
  );
}
