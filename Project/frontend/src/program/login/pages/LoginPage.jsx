// src/pages/LoginPage.jsx
// ✅ 로그인 페이지 (InputFieId 컴포넌트 사용 X, 순수 input 태그 사용)
// - UI: temp_form/md 래퍼 + temp_input 클래스로 스타일 적용
// - 로직: 전역 AuthContext의 login() 호출, loginUser API 연동
// - 접근성/사용성: autoComplete, required, Enter 제출, 로딩 중 버튼 비활성화
// - 보강: 토큰 추출 형태 다양성 대응, refreshToken 저장, role 기반 이동, 관리자 토큰 호환 저장

import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../services/auth"; // 로그인 API (백엔드: /auth/login)
import { useAuth } from "../../../common/context/AuthContext"; // 전역 로그인 상태
import { jwtDecode } from "jwt-decode"; // role fallback 용
import "../style/login.css";

const LoginPage = () => {
  // ✅ 폼 상태
  const [form, setForm] = useState({ memberId: "", memberPw: "" });
  // ✅ 에러 메시지 상태
  const [error, setError] = useState("");
  // ✅ 로딩 상태 (중복 제출 방지/버튼 비활성화)
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth(); // ✅ 전역 로그인 처리(토큰 저장 등 컨텍스트 호출)

  // ✅ 입력 변경 핸들러 (name으로 분기)
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // ✅ 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (loading) return; // 로딩 중 중복 제출 방지
    setError("");
    setLoading(true);

    try {
      // ⛳ 공용 로그인 API 호출 (/auth/login)
      //  - loginUser가 axios로 호출하여 { data } 형태를 반환한다고 가정
      const result = await loginUser({
        memberId: form.memberId, // ⚠️ 서버 DTO 키명과 반드시 일치
        memberPw: form.memberPw, // 예: username/password 라면 여기도 맞춰 변경
      });

      // axios 응답 안전 처리
      const data = result?.data ?? result ?? {};

      // ✅ 다양한 응답 포맷을 대비하여 토큰을 안전하게 추출
      const accessToken =
        data.accessToken ?? data.token ?? data.member?.accessToken ?? null;

      const refreshToken =
        data.refreshToken ?? data.member?.refreshToken ?? null;

      if (!accessToken) {
        // 토큰이 없다면 인증 실패로 처리
        throw new Error("토큰이 응답에 없습니다.");
      }

      // ✅ role은 응답 바디에서 우선, 없으면 JWT 디코드로 fallback
      let role =
        data.role ??
        data.member?.role ??
        (() => {
          try {
            const payload = jwtDecode(accessToken);
            return payload?.role ?? "USER";
          } catch {
            return "USER";
          }
        })();

      // 대소문자/접두어 정규화
      const upperRole = String(role).toUpperCase();

      // ✅ 로컬스토리지 저장 (axios 인터셉터에서 이 키를 사용)
      localStorage.setItem("accessToken", accessToken);
      if (refreshToken) localStorage.setItem("refreshToken", refreshToken);

      // + memberNum 불러와서 로컬 스트로지에 저장
      if (data.memberNum) {
        localStorage.setItem("memberNum", data.memberNum);
      }
      // 📌 호환용: 예전 코드가 adminAccessToken을 참조할 수 있어 ADMIN이면 같이 저장
      if (upperRole === "ADMIN" || upperRole === "ROLE_ADMIN") {
        localStorage.setItem("adminAccessToken", accessToken);
      } else {
        localStorage.removeItem("adminAccessToken");
      }

      // ✅ 전역 컨텍스트에도 반영 (컨텍스트 구현에 맞춰 전달)
      login({ accessToken, refreshToken, role: upperRole });

      alert("로그인 성공");

      // ✅ role 기반 라우팅: 관리자면 /admin, 아니면 /
      if (upperRole === "ADMIN" || upperRole === "ROLE_ADMIN") {
        navigate("/admin");
      } else {
        navigate("/");
      }
    } catch (err) {
      console.error("❌ 로그인 실패:", err);
      // 서버 메시지가 있으면 우선 표시
      const msg =
        err?.response?.data?.message ||
        err?.message ||
        "로그인 실패: 아이디 또는 비밀번호를 확인하세요.";
      setError(msg);
      alert("로그인 실패");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="login_wrap" onSubmit={handleSubmit}>
      <div className="form_top_box">
        <div className="form_top_item">
          <i className="form_icon type1"></i>
          <div className="form_title">로그인</div>
          <div className="form_desc">
            <p>함께마당에 오신것을 환영합니다.</p>
            <p>로그인 하셔서 다양한 서비스를 이용하세요!</p>
          </div>
        </div>
      </div>
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="float_box clearfix">
            <div className="left_box">
              <div className="left_from">
                <div className="from_text">아이디</div>
                <input
                  className="id_color form_input"
                  type="text"
                  name="memberId"
                  value={form.memberId}
                  onChange={handleChange}
                  placeholder="아이디 입력"
                  autoComplete="username" // ✅ 브라우저 자동완성
                  required // ✅ 빈값 제출 방지
                />
              </div>
              <div className="left_from">
                <span className="from_text">비밀번호</span>
                <input
                  id="userPasswd"
                  className="form_input"
                  type="password"
                  name="memberPw"
                  value={form.memberPw}
                  onChange={handleChange}
                  placeholder="비밀번호 입력"
                  autoComplete="current-password"
                  required
                />
              </div>
            </div>
            <div className="right_box">
              <div className="login_btn_box bth_item">
                <button
                  type="submit"
                  className="login_btn type3"
                  disabled={loading}
                >
                  {loading ? "로그인 중..." : "로그인"}
                </button>
              </div>
            </div>
          </div>
          <div className="form_btn_box">
            <div className="login_more">
              <div className="id_find bth_item">
                <Link href="" className="login_btn type1">
                  <span>아이디 찾기</span>
                </Link>
              </div>
              <div className="pw_find bth_item">
                <Link href="/findPassword" className="login_btn type1">
                  <span>비밀번호 찾기</span>
                </Link>
              </div>
              <div className="signup bth_item">
                <a href="/signup" className="login_btn type2">
                  <span>회원가입</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </form>
  );
};

export default LoginPage;
