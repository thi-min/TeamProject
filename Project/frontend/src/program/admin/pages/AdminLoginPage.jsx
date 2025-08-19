//기존 관리자, 회원 로그인 분리 합병으로 변경
// 프론트: 관리자 로그인 페이지도 /login 으로 요청 보냄 → 토큰은 accessToken 하나만 저장
// 인터셉터: /admin/** 라고 해서 별도 키(adminAccessToken) 찾지 않고 항상 accessToken만 붙임
// 백엔드: /login은 permitAll, /admin/**는 ADMIN 권한 필요(기존과 동일). 토큰 role이 ADMIN(또는 ROLE_ADMIN) 이어야 통과
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../../common/api/axios";
import { jwtDecode } from "jwt-decode"; // 역할 분기용(리다이렉트 판단)

const AdminLoginPage = () => {
  const [form, setForm] = useState({ adminId: "", adminPw: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // ✅ 핵심: /admin/login → /login 으로 통합
  //  - 백엔드는 관리자 계정이면 role=ADMIN(또는 ROLE_ADMIN)으로 토큰 발급
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (loading) return;
    setError("");
    setLoading(true);

    try {
      const res = await api.post("/login", {
        // 백엔드 파라미터명이 userId/password 라면 맞춰주세요.
        // 여기선 기존 관리자 폼 이름을 그대로 보내지만, 서버쪽 DTO와 일치해야 합니다.
        memberId: form.adminId,   // ⬅ 서버가 adminId를 받는다면 adminId로 바꾸세요
        memberPw: form.adminPw,   // ⬅ 서버가 password를 받는다면 password로 바꾸세요
      });

      const data = res.data || {};
      const accessToken = data.accessToken ?? null;
      const refreshToken = data.refreshToken ?? null;

      if (!accessToken) {
        setError("토큰이 응답에 없습니다.");
        return;
      }

      // ✅ 통합: 하나의 키로만 저장
      localStorage.setItem("accessToken", accessToken);
      if (refreshToken) localStorage.setItem("refreshToken", refreshToken);

      // 역할에 따라 이동 (ADMIN이면 관리자 대시보드로)
      const payload = jwtDecode(accessToken);
      const role = payload?.role; // 서버가 넣어주는 클레임 이름에 맞추세요
      if (role === "ADMIN" || role === "ROLE_ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/"); // 일반 사용자 홈 등
      }
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.message ||
        "로그인 실패: 아이디/비밀번호를 확인하세요.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="login_wrap" onSubmit={handleSubmit}>
      <div className="form_top_box">
        <div className="form_top_item">
          <i className="form_icon type1"></i>
          <div className="form_title">관리자 로그인</div>
          <div className="form_desc">
            <p>관리자 페이지에 오신 것을 환영합니다.</p>
            <p>아이디와 비밀번호를 입력해주세요.</p>
          </div>
        </div>
      </div>

      {/* 상단 에러 메시지 블록 (UX 맞춤) */}
      {error && (
        <div className="server_error" role="alert">
          {error}
        </div>
      )}

      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="float_box clearfix">
            <div className="left_box">
              {/* 관리자 아이디 */}
              <div className="id_pw_form">
                <div className="id_pw_text">아이디</div>
                <input
                  className="id_color form_input"
                  type="text"
                  name="adminId"
                  value={form.adminId}
                  onChange={handleChange}
                  placeholder="관리자 아이디 입력"
                  autoComplete="username" // ✅ 자동완성
                  required
                />
              </div>

              {/* 비밀번호 */}
              <div className="id_pw_form">
                <span className="id_pw_text">비밀번호</span>
                <input
                  id="adminPasswd"
                  className="form_input"
                  type="password"
                  name="adminPw"
                  value={form.adminPw}
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
              {/* 필요하면 관리자용 링크로 교체/숨김 처리 */}
              <div className="id_find bth_item">
                <Link to="#" className="login_btn type1">
                  <span>아이디 찾기</span>
                </Link>
              </div>
              <div className="pw_find bth_item">
                <Link to="#" className="login_btn type1">
                  <span>비밀번호 찾기</span>
                </Link>
              </div>
              <div className="signup bth_item">
                <Link to="/signup" className="login_btn type2">
                  <span>회원가입</span>
                </Link>
              </div>
            </div>
          </div>

          {/* 하단 에러 메시지 블록 (디자인에 따라 위/아래 중 택1 가능) */}
          {error && (
            <div className="server_error" role="alert" style={{ marginTop: 8 }}>
              {error}
            </div>
          )}
        </div>
      </div>
    </form>
  );
};

export default AdminLoginPage;
