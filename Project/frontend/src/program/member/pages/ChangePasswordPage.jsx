// ChangePasswordPage.jsx
// 목적: 비밀번호 변경 폼
// 핵심 처리:
//  - 클라이언트에서만 판단 가능한 것: 새 비번/확인 불일치, 현재 비번과 새 비번 동일 여부, 필수값 누락, 만료
//  - 서버만 판단 가능한 것: "현재 비번이 맞는지" (→ 제출 후 서버 응답으로 처리)
// 시나리오 매핑:
//  1) 현재 비번 틀림 + 새 비번 불일치 → 클라가 '불일치'를 먼저 잡고 alert, 제출 차단
//  2) 현재 비번 맞음 + 새 비번 불일치 → 클라가 '불일치' alert, 제출 차단
//  3) 현재 비번 틀림 + 새 비번 일치 → 제출 진행 → 서버에서 "현재 비번 불일치" 응답 → alert

import React, { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { updatePassword } from "../services/memberApi";
import BackButton from "../../../common/BackButton";

export default function ChangePasswordPage() {
  const location = useLocation();
  const navigate = useNavigate();

  // 비번찾기 성공 페이지에서 넘겨준 상태
  const fromState = location.state ?? {};
  const memberId = fromState.memberId ?? "";

  // 만료 시간 단위 보정 (초 → ms)
  const rawExpires = fromState.expiresAt ?? fromState.expiresAtMs ?? null;
  const expiresAtMs = useMemo(() => {
    if (!rawExpires) return null;
    const digits = String(rawExpires).length;
    return digits <= 10 ? Number(rawExpires) * 1000 : Number(rawExpires);
  }, [rawExpires]);

  const expiredChange = useMemo(() => {
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

  // 공통 onChange
  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // 파생 값: 새 비번/확인 불일치
  const pwMismatch =
    !!form.newPassword &&
    !!form.newPasswordCheck &&
    form.newPassword !== form.newPasswordCheck;

  // 파생 값: 현재 비번과 새 비번 동일 (변경 의미 없음)
  const sameAsCurrent =
    !!form.currentPassword &&
    !!form.newPassword &&
    form.currentPassword === form.newPassword;

  // 제출 가능 여부 (클라 차원)
  const canSubmit =
    !submitting &&
    !expiredChange &&
    !!memberId &&
    !!form.currentPassword &&
    !!form.newPassword &&
    !!form.newPasswordCheck &&
    !pwMismatch &&
    !sameAsCurrent;

  // ★ 기존 onSubmit에서 "pwMismatch 차단" 부분을 제거하고 아래처럼 수정
  const onSubmit = async (e) => {
    e.preventDefault();

    // 1) 기본 가드
    if (expiredChange) {
      alert(
        "변경 가능한 시간이 만료되었습니다. 다시 비밀번호 찾기를 진행해 주세요."
      );
      setErrorMsg("변경 가능한 시간이 만료되었습니다.");
      return;
    }
    if (!memberId) {
      alert("회원 식별 정보가 없습니다. 처음부터 다시 진행해주세요.");
      setErrorMsg("회원 식별 정보가 없습니다.");
      return;
    }
    if (!form.currentPassword) {
      alert("현재 비밀번호를 입력해 주세요.");
      setErrorMsg("현재 비밀번호를 입력해 주세요.");
      return;
    }
    if (!form.newPassword || !form.newPasswordCheck) {
      alert("새 비밀번호를 입력/확인해 주세요.");
      setErrorMsg("새 비밀번호를 입력/확인해 주세요.");
      return;
    }

    // 2) 현재 비번과 새 비번이 동일 → 바로 차단(이건 서버 보낼 필요 없음)
    const sameAsCurrent =
      !!form.currentPassword &&
      !!form.newPassword &&
      form.currentPassword === form.newPassword;
    if (sameAsCurrent) {
      alert(
        "새 비밀번호가 현재 비밀번호와 동일합니다. 다른 비밀번호를 입력해 주세요."
      );
      setErrorMsg("새 비밀번호가 현재 비밀번호와 동일합니다.");
      return;
    }

    // 3) 새 비번 불일치 감지 (하지만 여기서 '즉시 차단'하지 않음!)
    //    → 서버에 보내서 '현재 비번 불일치'와 '불일치'가 동시에 있을 때
    //      서버 메시지로 우선순위를 정한다.
    const clientDetectedMismatch =
      !!form.newPassword &&
      !!form.newPasswordCheck &&
      form.newPassword !== form.newPasswordCheck;

    setErrorMsg("");
    setSubmitting(true);
    try {
      await updatePassword({
        memberId,
        currentPassword: form.currentPassword,
        newPassword: form.newPassword,
        newPasswordCheck: form.newPasswordCheck,
      });

      alert("비밀번호가 변경되었습니다. 다시 로그인해 주세요.");
      navigate("/login");
    } catch (err) {
      // 서버에서 온 에러 메시지 최대한 노출
      const serverMsg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err?.message ||
        "비밀번호 변경 중 오류가 발생했습니다.";
      const msgText = String(serverMsg);

      // 4) 우선순위 처리
      //    ① 서버가 '현재 비밀번호 불일치'라고 하면 → 이 메시지만 보여줌
      //    ② 그 외에 서버가 '새 비번 불일치'라고 하면 → 그거 보여줌
      //    ③ 서버가 두 메시지 모두 안 주는데, 클라가 불일치 감지했다면 → 불일치 안내
      if (msgText.includes("현재 비밀번호가 일치하지 않습니다")) {
        alert("현재 비밀번호가 일치하지 않습니다.");
        setErrorMsg("현재 비밀번호가 일치하지 않습니다.");
      } else if (
        msgText.includes("변경할 비밀번호가 일치하지 않습니다") ||
        msgText.includes("비밀번호가 일치하지 않습니다")
      ) {
        alert("새 비밀번호와 확인이 일치하지 않습니다.");
        setErrorMsg("새 비밀번호와 확인이 일치하지 않습니다.");
      } else if (clientDetectedMismatch) {
        // 서버가 명확히 안 줬어도 클라가 불일치를 감지했으면 안내
        alert("새 비밀번호와 확인이 일치하지 않습니다.");
        setErrorMsg("새 비밀번호와 확인이 일치하지 않습니다.");
      } else {
        alert(msgText);
        setErrorMsg(msgText);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={onSubmit} noValidate>
      <div className="form_top_box">
        <div className="form_top_item">
          <i className="form_icon type1"></i>
          <div className="form_title">비밀번호 변경</div>
          <div className="form_desc">
            <p>
              {expiredChange
                ? "변경 가능한 시간이 만료되었습니다. 다시 비밀번호 찾기를 진행해 주세요."
                : "본인 확인이 완료되었습니다. 새 비밀번호를 설정해 주세요."}
            </p>
          </div>
        </div>
      </div>

      {!expiredChange && (
        <div className="form_item type2">
          <div className="form_login_wrap">
            <div className="float_box clearfix">
              <div className="form_item_box">
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

                <div className="input_item">
                  <div className="from_text">새 비밀번호</div>
                  <input
                    id="newPassword"
                    name="newPassword"
                    type="password"
                    className="form_input"
                    value={form.newPassword}
                    onChange={onChange}
                    autoComplete="new-password"
                  />
                </div>

                <div className="input_item">
                  <div className="from_text">새 비밀번호 확인</div>
                  <input
                    id="newPasswordCheck"
                    name="newPasswordCheck"
                    type="password"
                    className="form_input"
                    value={form.newPasswordCheck}
                    onChange={onChange}
                    autoComplete="new-password"
                  />
                </div>

                {/* 실시간 경고 표시 (시각적) */}
                {pwMismatch && (
                  <div
                    className="form_error"
                    role="alert"
                    aria-live="assertive"
                  >
                    새 비밀번호와 확인이 일치하지 않습니다.
                  </div>
                )}
                {sameAsCurrent && (
                  <div
                    className="form_error"
                    role="alert"
                    aria-live="assertive"
                  >
                    새 비밀번호가 현재 비밀번호와 동일합니다.
                  </div>
                )}
                {!!errorMsg && (
                  <div
                    className="form_error"
                    role="alert"
                    aria-live="assertive"
                  >
                    {errorMsg}
                  </div>
                )}

                <div className="form_center_box">
                  <div className="temp_btn white md">
                    <BackButton label="이전" className="btn white" />
                  </div>
                  <div className="temp_btn md">
                    <button
                      type="submit"
                      className="btn"
                      disabled={!canSubmit}
                      aria-disabled={!canSubmit}
                    >
                      {submitting ? "변경 중..." : "비밀번호 변경"}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </form>
  );
}
