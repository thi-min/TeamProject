// src/pages/LoginPage.jsx
// ✅ 로그인 페이지 (InputFieId 컴포넌트 사용 X, 순수 input 태그 사용)
// - UI: temp_form/md 래퍼 + temp_input 클래스로 스타일 적용
// - 로직: 전역 AuthContext의 login() 호출, loginUser API 연동
// - 접근성/사용성: autoComplete, required, Enter 제출, 로딩 중 버튼 비활성화
import { Link } from "react-router-dom";
import React, { useState } from "react";
import { loginUser } from "../services/auth"; // 로그인 API
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../common/context/AuthContext"; // 전역 로그인 상태
import "../style/login.css";
// (선택) 디버깅용 로깅
// console.log('[LoginPage] sees AuthContext id =', window.__AUTH_CTX_ID__);

const LoginPage = () => {
  // ✅ 폼 상태
  const [form, setForm] = useState({ memberId: "", memberPw: "" });
  // ✅ 에러 메시지 상태
  const [error, setError] = useState("");
  // ✅ 로딩 상태 (중복 제출 방지/버튼 비활성화)
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth(); // 전역 로그인 처리 (토큰 저장 등)

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
      // ⛳ 로그인 API 호출
      const result = await loginUser(form);

      // ⛳ 전역 로그인 처리 (컨텍스트에 토큰 전달)
      login({
        accessToken: result.accessToken,
        refreshToken: result.refreshToken, // 응답에 있으면 함께 저장
      });

      alert("로그인 성공");
      navigate("/"); // 메인 페이지로 이동
    } catch (err) {
      console.error("❌ 로그인 실패:", err);
      setError("로그인 실패: 아이디 또는 비밀번호를 확인하세요.");
      alert("로그인 실패");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="login_wrap" onSubmit={handleSubmit}>
      <div class="form_top_box">
        <div class="form_top_item">
          <i class="form_icon type1"></i>
          <div class="form_title">로그인</div>
          <div class="form_desc">
            <p>함께마당에 오신것을 환영합니다.</p>
            <p>로그인 하셔서 다양한 서비스를 이용하세요!</p>
          </div>
        </div>
      </div>
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="float_box clearfix">
            <div className="left_box">
              <div className="id_pw_form">
                <div className="id_pw_text">아이디</div>
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
              <div className="id_pw_form">
                <span className="id_pw_text">비밀번호</span>
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
                <Link
                  href="./findUserPswd.do?key=284"
                  className="login_btn type1"
                >
                  <span>비밀번호 찾기</span>
                </Link>
              </div>
              <div className="signup bth_item">
                <a
                  href="./joinStep1Form.do?key=285"
                  className="login_btn type2"
                >
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
