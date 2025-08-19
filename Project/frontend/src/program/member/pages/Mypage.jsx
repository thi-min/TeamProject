// MyPage.jsx
// 목적: 인증된 사용자의 마이페이지 조회/표시
// 포인트:
//  - 첫 렌더 시 /mypage 호출
//  - 401이면 로그인으로 안내
//  - 403(비번 만료)이면 비번 변경 페이지로 유도
//  - 서버 메시지는 최대한 사용자에게 친숙하게 노출
//  - UI 클래스는 기존 프로젝트 스타일 유지(필요한 최소 마크업만 사용)

import React, { useEffect, useState, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { apiGetMyPage } from "../services/memberApi";
import BackButton from "../../../common/BackButton";

export default function MyPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const [data, setData] = useState(null); // 서버 응답 DTO
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [error, setError] = useState(""); // 사용자 노출용 에러 메시지

  // enum → 표시 문자열
  const sexLabel = useMemo(() => {
    if (!data?.memberSex) return "";
    // 서버 enum이 MALE/FEMALE 로 온다고 가정
    return data.memberSex === "MALE"
      ? "남성"
      : data.memberSex === "FEMALE"
      ? "여성"
      : data.memberSex;
  }, [data]);

  useEffect(() => {
    let mounted = true;

    (async () => {
      setLoading(true);
      setError("");
      try {
        const res = await apiGetMyPage();
        if (!mounted) return;
        setData(res.data);
      } catch (err) {
        if (!mounted) return;

        const status = err?.response?.status;
        const msg =
          err?.response?.data?.message ||
          err?.response?.data ||
          err?.message ||
          "마이페이지 조회 중 오류가 발생했습니다.";

        // ✅ 401: 로그인 필요
        if (status === 401) {
          alert("로그인이 필요합니다. 로그인 후 다시 시도해주세요.");
          navigate("/login", { replace: true, state: { from: location } });
          return;
        }

        // ✅ 403: 비밀번호 만료
        if (status === 403 && String(msg).includes("비밀번호가 만료")) {
          alert("비밀번호가 만료되었습니다. 비밀번호를 변경해 주세요.");
          // 비번 변경 페이지로 이동 (필요 시 state에 memberId/만료시각 전달)
          navigate("/change-password", {
            replace: true,
            state: {
              // memberId는 보통 토큰에서 복원 가능. 필요 시 저장소/컨텍스트에서 주입
              // memberId: decoded.memberId,
              // expiresAt: ... (없으면 생략)
            },
          });
          return;
        }

        // 그 외 오류
        setError(String(msg));
      } finally {
        if (mounted) setLoading(false);
      }
    })();

    return () => {
      mounted = false;
    };
  }, [navigate, location]);

  if (loading) {
    return (
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="form_item_box">
            <div className="from_text">마이페이지를 불러오는 중입니다...</div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="form_item_box">
            <div className="form_error" role="alert" aria-live="assertive">
              {error}
            </div>
            <div className="form_center_box">
              <BackButton label="이전" className="btn white" />
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!data) {
    return null;
  }

  return (
    <div className="form_item type2">
      <div className="form_top_box">
        <div className="form_top_item">
          <i className="form_icon type1"></i>
          <div className="form_title">마이페이지</div>
          <div className="form_desc">
            <p>회원님의 기본 정보를 확인할 수 있습니다.</p>
          </div>
        </div>
      </div>

      <div className="form_login_wrap">
        <div className="float_box clearfix">
          <div className="form_item_box">
            <div className="input_item">
              <div className="from_text">이름</div>
              <div className="form_value">{data.memberName ?? "-"}</div>
            </div>

            <div className="input_item">
              <div className="from_text">아이디(이메일)</div>
              <div className="form_value">{data.memberId ?? "-"}</div>
            </div>

            <div className="input_item">
              <div className="from_text">생년월일</div>
              <div className="form_value">{data.memberBirth ?? "-"}</div>
            </div>

            <div className="input_item">
              <div className="from_text">성별</div>
              <div className="form_value">{sexLabel || "-"}</div>
            </div>

            <div className="input_item">
              <div className="from_text">주소</div>
              <div className="form_value">{data.memberAddress ?? "-"}</div>
            </div>

            <div className="input_item">
              <div className="from_text">휴대전화</div>
              <div className="form_value">{data.memberPhone ?? "-"}</div>
            </div>

            <div className="input_item">
              <div className="from_text">카카오 ID</div>
              <div className="form_value">{data.kakaoId ?? "-"}</div>
            </div>

            <div className="input_item">
              <div className="from_text">SMS 수신동의</div>
              <div className="form_value">
                {data.smsAgree ? "동의" : "미동의"}
              </div>
            </div>

            <div className="form_center_box">
              <div className="temp_btn white md">
                <BackButton label="이전" className="btn white" />
              </div>
              {/* 필요 시 비밀번호 변경/정보 수정 버튼 추가 */}
              {/* <div className="temp_btn md">
                <button type="button" className="btn" onClick={() => navigate("/member/edit")}>
                  정보 수정
                </button>
              </div> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
