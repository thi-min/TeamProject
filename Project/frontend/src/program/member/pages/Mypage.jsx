import React, { Link, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import MemberDeleteButton from "./MemberDeleteButton";
import { useAuth } from "../../../common/context/AuthContext";
import api from "../../../common/api/axios";

export default function Mypage() {
  // 전역 인증 상태
  const { isLogin } = useAuth();

  // 라우팅
  const nav = useNavigate();

  // 화면 상태
  const [myInfo, setMyInfo] = useState(null); // { memberNum, memberId, memberName, memberState }
  const [loading, setLoading] = useState(true);

  /**
   * 1) 비로그인 즉시 "/"로 이동
   * - 버튼 로그아웃, 토큰 만료, 재발급 실패 등으로 isLogin=false가 되면 즉시 홈으로
   * - replace:true로 히스토리에 남기지 않음
   */
  useEffect(() => {
    if (!isLogin) {
      nav("/", { replace: true });
    }
  }, [isLogin, nav]);

  /**
   * 2) 로그인 상태에서만 내 정보 요청
   * - 비로그인이라면 요청 자체를 skip (401/재시도/알림 중복 방지)
   * - StrictMode(개발)로 인한 효과 2회 실행에 대비해 cancelled 플래그 사용
   */
  useEffect(() => {
    // 로그인 아니면 네트워크 호출 생략하고 로딩 종료
    if (!isLogin) {
      setLoading(false);
      return;
    }

    let cancelled = false;
    setLoading(true);

    (async () => {
      try {
        const res = await api.get("/member/mypage/me"); // Authorization은 인터셉터가 자동 부착
        if (!cancelled) setMyInfo(res.data);
      } catch (e) {
        // 401/403 → 인터셉터가 재발급 시도, 실패 시 isLogin=false가 되어 위 useEffect가 "/"로 이동 처리
        // 여기서 알림을 띄우면 두 번 뜰 수 있으니 콘솔만.
        console.error(
          "[MyPage] /member/mypage/me 실패:",
          e?.response?.status,
          e?.message
        );
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    // 언마운트/재마운트 시 setState 방지
    return () => {
      cancelled = true;
    };
  }, [isLogin]);

  // 로딩 중 UI
  if (loading) {
    return <div className="admin_page">로딩중...</div>;
  }

  // (안전) 정보 없을 때의 가드 — 보통은 isLogin 가드가 먼저 작동하여 "/"로 이동함
  if (!myInfo) {
    return <div className="admin_page">정보를 불러올 수 없습니다.</div>;
  }

  return (
    <div className="my_page">
      <div className="title_box">
        <div className="title">마이 페이지</div>
      </div>
      <div className="member_area">
        <ul className="my_menu">
          <li className="link_item type1">
            <Link to="/member/update-password" state={{ mode: "self" }}>
              비밀번호 변경
            </Link>
          </li>
          <li className="link_item type2">
            <Link to="/member/mypage/memberdata">회원 정보</Link>
          </li>
          <li className="link_item type3">
            <Link to="/member/mypage/reserves">예약 내역 조회</Link>
          </li>
          <li className="link_item type6">
            <Link to="">예약 시간대 관리</Link>
          </li>
          <li className="link_item type5">
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
