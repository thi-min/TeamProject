// 목적: 하나의 페이지에서 "self(내 비밀번호 변경)" + "reset(비번찾기 후 재설정)" 모드 분기
// 라우트: /member/update-password  (두 흐름 모두 이 경로 사용)
// 모드 판별:
//   - 우선순위: location.state.mode ('self' | 'reset')
//   - 없으면 resetToken 또는 memberId가 쿼리/state에 있으면 'reset', 아니면 'self'

import React, { useMemo, useState } from "react";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { apiUpdatePasswordSelf, apiResetPassword } from "../services/memberApi";
import BackButton from "../../../common/BackButton";

function validateNewPassword(newPw, newPwCheck) {
  const issues = [];
  if (!newPw || newPw.length < 8)
    issues.push("새 비밀번호는 8자 이상이어야 합니다.");
  if (!/[A-Za-z]/.test(newPw) || !/[0-9]/.test(newPw))
    issues.push("영문과 숫자를 모두 포함해야 합니다.");
  if (newPw !== newPwCheck)
    issues.push("새 비밀번호와 확인이 일치하지 않습니다.");
  return issues;
}

export default function UpdatePasswordPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();

  // state/쿼리에서 값 수집
  const state = location.state ?? {};
  const modeHint = state.mode; // 'self' | 'reset' | undefined
  const resetToken = state.resetToken ?? searchParams.get("token") ?? null;
  const memberId = state.memberId ?? searchParams.get("memberId") ?? "";
  const rawExp =
    state.expiresAt ?? state.expiresAtMs ?? searchParams.get("exp") ?? null;

  // 만료시간(있으면 초/밀리초 모두 수용)
  const expiresAtMs = useMemo(() => {
    if (!rawExp) return null;
    const s = String(rawExp);
    return s.length <= 10 ? Number(s) * 1000 : Number(s);
  }, [rawExp]);

  // 최종 모드 결정
  const mode = useMemo(() => {
    if (modeHint === "self" || modeHint === "reset") return modeHint;
    if (resetToken || memberId) return "reset";
    return "self";
  }, [modeHint, resetToken, memberId]);

  const expired = useMemo(() => {
    if (!expiresAtMs) return false;
    return Date.now() >= expiresAtMs;
  }, [expiresAtMs]);

  // 폼 상태
  const [form, setForm] = useState({
    currentPassword: "",
    newPassword: "",
    newPasswordCheck: "",
  });
  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((p) => ({ ...p, [name]: value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    // 공통 검증
    const pwIssues = validateNewPassword(
      form.newPassword,
      form.newPasswordCheck
    );
    if (pwIssues.length) {
      setErrorMsg(pwIssues[0]);
      return;
    }

    // 모드별 추가 검증
    if (mode === "self") {
      if (!form.currentPassword) {
        setErrorMsg("현재 비밀번호를 입력해 주세요.");
        return;
      }
    } else {
      // reset 모드
      if (!memberId) {
        setErrorMsg(
          "회원 식별 정보가 없습니다. 처음부터 비밀번호 찾기를 진행해 주세요."
        );
        return;
      }
      if (expired) {
        setErrorMsg(
          "변경 가능한 시간이 만료되었습니다. 다시 비밀번호 찾기를 진행해 주세요."
        );
        return;
      }
    }

    setSubmitting(true);
    try {
      if (mode === "self") {
        // 로그인 상태 본인 변경
        await apiUpdatePasswordSelf({
          currentPassword: form.currentPassword,
          newPassword: form.newPassword,
          newPasswordCheck: form.newPasswordCheck,
        });
      } else {
        // 비번찾기 후 재설정
        await apiResetPassword({
          memberId,
          newPassword: form.newPassword,
          newPasswordCheck: form.newPasswordCheck,
          resetToken, // 서버가 필요 없으면 무시
        });
      }

      alert("비밀번호가 변경되었습니다. 다시 로그인해 주세요.");
      navigate("/login", { replace: true });
    } catch (err) {
      // 서버가 상세 메시지를 준다면 우선 표출
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err?.message ||
        "비밀번호 변경 중 오류가 발생했습니다.";
      setErrorMsg(String(msg));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form noValidate onSubmit={onSubmit}>
      <div className="form_top_box">
        <div className="form_top_item">
          <i className="form_icon type1"></i>
          <div className="form_title">
            {mode === "self" ? "비밀번호 변경" : "비밀번호 재설정"}
          </div>
          <div className="form_desc">
            <p>
              {mode === "self"
                ? "현재 비밀번호 확인 후 새 비밀번호로 변경합니다."
                : expired
                ? "재설정 가능 시간이 만료되었습니다. 다시 진행해 주세요."
                : "본인 확인이 완료되었습니다. 새 비밀번호를 설정해 주세요."}
            </p>
          </div>
        </div>
      </div>

      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="float_box clearfix">
            <div className="form_item_box">
              {/* self 모드에서만 현재 비밀번호 노출 */}
              {mode === "self" && (
                <div className="input_item">
                  <div className="from_text">현재 비밀번호</div>
                  <input
                    id="currentPassword"
                    name="currentPassword"
                    type="password"
                    className="form_input"
                    value={form.currentPassword}
                    onChange={onChange}
                    autoComplete="current-password"
                  />
                </div>
              )}

              <div className="input_item">
                <div className="from_text">새 비밀번호</div>
                <input
                  id="newPassword"
                  name="newPassword"
                  type="password"
                  className="form_input"
                  autoComplete="new-password"
                  value={form.newPassword}
                  onChange={onChange}
                />
              </div>

              <div className="input_item">
                <div className="from_text">새 비밀번호 확인</div>
                <input
                  id="newPasswordCheck"
                  name="newPasswordCheck"
                  type="password"
                  className="form_input"
                  autoComplete="new-password"
                  value={form.newPasswordCheck}
                  onChange={onChange}
                />
              </div>

              <div className="form_center_box">
                <div className="temp_btn white md">
                  <BackButton label="이전" className="btn white" />
                </div>
                <div className="temp_btn md">
                  <button
                    type="submit"
                    className="btn"
                    disabled={submitting || (mode === "reset" && expired)}
                    aria-disabled={submitting || (mode === "reset" && expired)}
                  >
                    {submitting ? "변경 중..." : "비밀번호 변경"}
                  </button>
                </div>
              </div>

              {!!errorMsg && (
                <div className="form_error" role="alert" aria-live="assertive">
                  {errorMsg}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </form>
  );
}
