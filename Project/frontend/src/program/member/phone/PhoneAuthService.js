// src/features/phone/PhoneAuthService.js
// 목적: Firebase Phone Auth 래퍼(OTP 전송/확인). 6자리 코드는 Firebase가 생성/검증함.
// 주의: 발신번호 지정 불가(플랫폼 정책)

import { auth, buildInvisibleRecaptcha } from "../../firebase/firebase";
import { signInWithPhoneNumber } from "firebase/auth";

// 한국 번호 표준화: 010-xxxx-xxxx → "+82 10xxxxxxxx" 형태로 변환
export function toE164KR(raw) {
  // 숫자만 남기기
  const digits = (raw || "").replace(/\D/g, "");
  // 010xxxxxxxx(=11자리)만 허용(필요시 011/016 등 확장 가능)
  if (!/^010\d{8}$/.test(digits)) return null;
  // E.164 변환: +82 10xxxxxxxx
  return "+82 " + digits.slice(1); // 010 제거 후 앞에 +82
}

// OTP 전송 (결과로 confirmationResult 반환)
export async function sendOtpKR({
  phoneRaw,
  recaptchaButtonId = "send-otp-button",
}) {
  const e164 = toE164KR(phoneRaw);
  if (!e164) {
    throw new Error("전화번호 형식이 올바르지 않습니다. (예: 01012345678)");
  }
  // invisible reCAPTCHA 생성(버튼 id 기준으로 연결)
  const verifier = buildInvisibleRecaptcha(recaptchaButtonId);
  const confirmationResult = await signInWithPhoneNumber(auth, e164, verifier);
  return confirmationResult; // 이후 confirmationResult.confirm(code)로 검증
}
