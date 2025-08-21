// src/features/phone/PhoneVerificationForm.jsx
// 목적: "회원가입" 모드와 "마이페이지 번호 변경" 모드 모두 처리
// - props.mode: "signup" | "update"
// - props.currentMemberId?: string  // update 모드에서 현재 로그인 회원ID 전달하면, 회원ID input 숨김
// - props.onVerified?: (phoneDigitsOnly: string) => void  // 인증 완료 콜백
//
// 알림 문구 요구사항:
// - 중복 시:     alert("이미 가입되어있는 번호 입니다");
// - OTP 전송 시: alert("인증번호를 전송했습니다.");
// - 회원 없음:   alert("일치하는 회원이 없습니다.");

import React, { useState, useRef } from "react";
import { sendOtpKR } from "./PhoneAuthService";
import { isPhoneTaken, findMemberById } from "./memberRepository";

export default function PhoneVerificationForm({
  mode = "signup",
  currentMemberId = "",
  onVerified = () => {},
  className = "",
}) {
  const [memberId, setMemberId] = useState(currentMemberId || "");
  const [phone, setPhone] = useState(""); // 숫자만 입력 유도
  const [code, setCode] = useState(""); // 6자리 OTP
  const [confirmation, setConfirmation] = useState(null); // sendOtp 결과
  const [sending, setSending] = useState(false);
  const [verifying, setVerifying] = useState(false);

  // 버튼 DOM id: Firebase reCAPTCHA가 이 id를 기준으로 invisible 세팅
  const sendBtnId = useRef(
    `send-otp-button-${Math.random().toString(36).slice(2)}`
  );

  // 입력값 핸들러: 전화번호는 숫자만 유지
  const handlePhoneChange = (e) => {
    const onlyDigits = e.target.value.replace(/\D/g, "");
    setPhone(onlyDigits);
  };

  // 1) OTP 전송
  const handleSend = async () => {
    try {
      // 모드별 선행 체크
      if (mode === "signup") {
        // 회원가입: 전화번호 중복이면 전송 금지
        const taken = await isPhoneTaken(phone);
        if (taken) {
          alert("이미 가입되어있는 번호 입니다");
          return;
        }
        // 통과 → 전송
      } else {
        // update: 회원 존재 여부 체크(회원 없으면 “일치하는 회원이 없습니다.”)
        const targetMemberId = memberId?.trim();
        if (!targetMemberId) {
          alert("일치하는 회원이 없습니다.");
          return;
        }
        const me = await findMemberById(targetMemberId);
        if (!me) {
          alert("일치하는 회원이 없습니다.");
          return;
        }
        // 다른 사람이 쓰는 번호인지 체크(본인 제외)
        const taken = await isPhoneTaken(phone, targetMemberId);
        if (taken) {
          alert("이미 가입되어있는 번호 입니다");
          return;
        }
      }

      setSending(true);
      const confirmationResult = await sendOtpKR({
        phoneRaw: phone,
        recaptchaButtonId: sendBtnId.current,
      });
      setConfirmation(confirmationResult);
      alert("인증번호를 전송했습니다."); // 요구사항 알림
    } catch (err) {
      console.error(err);
      alert(err.message || "인증번호 전송 실패");
    } finally {
      setSending(false);
    }
  };

  // 2) OTP 확인
  const handleVerify = async () => {
    if (!confirmation) {
      alert("먼저 인증번호를 전송해 주세요.");
      return;
    }
    if (!/^\d{6}$/.test(code)) {
      alert("6자리 인증번호를 입력해 주세요.");
      return;
    }
    try {
      setVerifying(true);
      // Firebase가 발송한 '6자리 코드'를 검증
      await confirmation.confirm(code);
      // 성공 → 상위로 '숫자만 전화번호' 전달
      onVerified(phone);
    } catch (err) {
      console.error(err);
      alert("인증번호가 올바르지 않습니다.");
    } finally {
      setVerifying(false);
    }
  };

  return (
    <div className={`phone-verify ${className}`}>
      {/* update 모드에서 currentMemberId가 없으면 회원ID 입력란 노출 → “일치하는 회원이 없습니다.” 사용 가능 */}
      {mode === "update" && !currentMemberId && (
        <div className="temp_form w100">
          <label htmlFor="memberId" className="temp_label">
            회원 아이디
          </label>
          <input
            id="memberId"
            className="temp_input"
            type="text"
            placeholder="회원 아이디 입력"
            value={memberId}
            onChange={(e) => setMemberId(e.target.value)}
          />
        </div>
      )}

      <div className="temp_form w100">
        <label htmlFor="phone" className="temp_label">
          휴대폰 번호
        </label>
        <input
          id="phone"
          className="temp_input"
          type="tel"
          placeholder="숫자만 입력 (예: 01012345678)"
          value={phone}
          onChange={handlePhoneChange}
        />
      </div>

      {/* 이 버튼 id가 reCAPTCHA 컨테이너로 쓰임 (invisible) */}
      <button
        id={sendBtnId.current}
        type="button"
        className="temp_btn type1"
        onClick={handleSend}
        disabled={sending}
      >
        {sending ? "전송 중..." : "인증번호 전송"}
      </button>

      <div className="temp_form w100" style={{ marginTop: 12 }}>
        <label htmlFor="code" className="temp_label">
          인증번호 입력
        </label>
        <input
          id="code"
          className="temp_input"
          type="text"
          inputMode="numeric"
          placeholder="6자리"
          value={code}
          onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))}
          maxLength={6}
        />
      </div>

      <button
        type="button"
        className="temp_btn type2"
        onClick={handleVerify}
        disabled={verifying}
      >
        {verifying ? "확인 중..." : "인증 확인"}
      </button>
    </div>
  );
}
