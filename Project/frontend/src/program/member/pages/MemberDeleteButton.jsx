// 설명: 마이페이지 등에서 재활용 가능한 탈퇴 버튼.
// - confirm 모달 → 사유 입력(선택) → DELETE 호출
// - 성공 시 토큰/세션 정리 후 홈으로 이동

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

import { useAuth } from "../../../common/context/AuthContext";
import api from "../../../common/api/axios";

export default function MemberDeleteButton({
  memberNum,
  className = "link_item type2",
}) {
  const nav = useNavigate();
  const { logout } = useAuth();
  const [loading, setLoading] = useState(false);
  const [reason, setReason] = useState("");

  const handleDelete = async () => {
    if (loading) return;
    if (!memberNum) {
      alert("회원번호를 확인할 수 없습니다. 다시 로그인해 주세요.");
      return;
    }

    const ok = window.confirm(
      "정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다."
    );
    if (!ok) return;

    try {
      setLoading(true);
      const query = reason ? `?message=${encodeURIComponent(reason)}` : "";
      // ✅ 공용 api 인스턴스 사용
      const res = await api.delete(`/member/mypage/del/${memberNum}${query}`);
      const data = res.data; // { memberNum, memberName, message }

      alert(data?.message || "탈퇴가 완료되었습니다.");

      // ✅ 1) 서버의 httpOnly refresh 쿠키 제거
      try {
        await api.post("/auth/logout"); // 서버가 쿠키 clear 하도록
      } catch (e) {
        // 로그아웃 API 실패해도 아래 logout()으로 클라이언트 상태는 끊음
        console.warn("logout API failed:", e);
      }

      // ✅ 2) 전역 인증상태도 반드시 해제
      logout(); // ← 헤더의 isLogin, role 등 메모리 상태 초기화

      //   // 토큰/세션 정리
      //   localStorage.removeItem("accessToken");
      //   localStorage.removeItem("refreshToken");
      //   sessionStorage.clear();

      nav("/");
    } catch (err) {
      console.error(err);
      // axios 에러 응답 처리
      if (err.response) {
        alert(err.response.data?.message || `에러: ${err.response.status}`);
      } else {
        alert(err.message || "탈퇴 중 오류가 발생했습니다.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={className}>
      {/* 사유 입력(선택) — 디자인에 맞게 숨기거나 위치 조정 가능
      <div className="temp_form md w40p" style={{ marginRight: 8 }}>
        <input
          className="temp_input"
          type="text"
          placeholder="탈퇴 사유(선택)"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
        />
      </div> */}

      <button
        type="button"
        onClick={handleDelete}
        disabled={loading || !memberNum}
      >
        {loading ? "처리 중..." : "회원 탈퇴"}
      </button>
    </div>
  );
}
