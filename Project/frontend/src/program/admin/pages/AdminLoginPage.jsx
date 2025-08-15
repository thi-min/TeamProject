// frontend/src/program/admin/pages/AdminLoginPage.jsx
// ✅ 관리자 로그인 페이지 (일반 로그인 페이지와 동일한 구조/스타일/동작)
// - 순수 input 태그 사용 + 기존 클래스명 유지(login_wrap, form_input 등)
// - 에러 메시지/로딩 상태 처리 동일
// - axios 인스턴스(api) 직접 사용 (요청대로 통일)
// - 성공 시 토큰 저장 후 관리자 대시보드로 이동

import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../../common/api/axios"; // ✅ 공통 axios 인스턴스

const AdminLoginPage = () => {
  // ✅ 폼 상태 (사용자 페이지와 동일한 패턴)
  const [form, setForm] = useState({ adminId: "", adminPw: "" });
  // ✅ 에러 메시지 상태 (상단/아래 노출)
  const [error, setError] = useState("");
  // ✅ 로딩 상태 (중복 제출 방지 및 버튼 비활성화)
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  // ✅ 입력 변경 핸들러 (name으로 값 바인딩)
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // ✅ 제출 핸들러 (사용자 로그인 페이지와 동일한 흐름)
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (loading) return;
    setError("");
    setLoading(true);

    try {
      // ⛳ 관리자 로그인 API 호출 (axios 인스턴스 통일)
      //    최종 요청: http://localhost:8090/admin/login
      const res = await api.post("/admin/login", {
        adminId: form.adminId,
        adminPw: form.adminPw,
      });

      // 서버 응답(JSON)에서 토큰 꺼내기
      const data = res.data || {};
      const accessToken = data.accessToken ?? null;
      const refreshToken = data.refreshToken ?? null;

      // (선택) 헤더 기반 토큰도 지원하려면 아래처럼 병합 가능
      // const authHeader = res.headers?.authorization;
      // const headerAccess = authHeader?.startsWith("Bearer ")
      //   ? authHeader.replace(/^Bearer\s+/i, "")
      //   : null;
      // const headerRefresh = res.headers?.["x-refresh-token"];
      // const finalAccess = headerAccess ?? accessToken;
      // const finalRefresh = headerRefresh ?? refreshToken;

      // ⛳ 토큰 저장 (전역 컨텍스트가 있다면 거기에 위임해도 됨)
      if (accessToken) localStorage.setItem("accessToken", accessToken);
      if (refreshToken) localStorage.setItem("refreshToken", refreshToken);

      alert("관리자 로그인 성공");

      // ⛳ 관리자 대시보드로 이동 (경로는 프로젝트에 맞게 조정)
      navigate("/admin/dashboard");
    } catch (err) {
      console.error("❌ 관리자 로그인 실패:", err);
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
