// src/admin/pages/AdminPasswordUpdatePage.jsx

import React, { useState } from "react";
import { updateAdminPassword } from "../services/adminPwService";

/**
 * 관리자 비밀번호 변경 페이지
 * - 현재 비밀번호, 새 비밀번호, 새 비밀번호 확인 입력
 * - 제출 시 성공/실패 메시지를 항상 "문자열"로만 렌더
 */
export default function AdminPasswordUpdatePage() {
  // ✅ 폼 상태
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [newPasswordCheck, setNewPasswordCheck] = useState("");

  // ✅ 사용자에게 보여줄 피드백 (항상 문자열만!)
  const [feedback, setFeedback] = useState({ type: null, text: "" });
  const [loading, setLoading] = useState(false);

  // ✅ 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!currentPassword || !newPassword || !newPasswordCheck) {
      setFeedback({ type: "error", text: "모든 항목을 입력해주세요." });
      return;
    }
    if (newPassword !== newPasswordCheck) {
      setFeedback({ type: "error", text: "새 비밀번호가 일치하지 않습니다." });
      return;
    }
    try {
      setLoading(true);
      const msg = await updateAdminPassword({
        currentPassword,
        newPassword,
        newPasswordCheck,
      });
      setFeedback({
        type: "success",
        text: msg || "비밀번호가 성공적으로 변경되었습니다.",
      });
      setCurrentPassword("");
      setNewPassword("");
      setNewPasswordCheck("");
    } catch (err) {
      setFeedback({ type: "error", text: err.message || "실패했습니다." });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate>
      <div className="form_top_box">
        <div className="form_top_item">
          <i className="form_icon type1"></i>
          <div className="form_title">관리자 비밀번호 변경</div>
          <div className="form_desc">
            <p>비밀번호를 변경해주세요.</p>
          </div>
        </div>
      </div>
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="float_box clearfix">
            <div className="left_box">
              <div className="id_pw_form">
                <label htmlFor="currentPassword">현재 비밀번호</label>
                <input
                  id="currentPassword"
                  type="password"
                  className="temp_input"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  autoComplete="current-password"
                />
              </div>

              <div className="form_row">
                <label htmlFor="newPassword">새 비밀번호</label>
                <input
                  id="newPassword"
                  type="password"
                  className="temp_input"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  autoComplete="new-password"
                />
              </div>

              <div className="form_row">
                <label htmlFor="newPasswordCheck">새 비밀번호 확인</label>
                <input
                  id="newPasswordCheck"
                  type="password"
                  className="temp_input"
                  value={newPasswordCheck}
                  onChange={(e) => setNewPasswordCheck(e.target.value)}
                  autoComplete="new-password"
                />
              </div>

              <button type="submit" className="btn_primary" disabled={loading}>
                {loading ? "변경 중..." : "비밀번호 변경"}
              </button>

              {/* ✅ 항상 문자열만 렌더링 */}
              {feedback.type && (
                <p
                  className={
                    feedback.type === "success" ? "msg_success" : "msg_error"
                  }
                  // 절대 객체를 넣지 말고, 문자열만!
                >
                  {feedback.text}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </form>
  );
}
