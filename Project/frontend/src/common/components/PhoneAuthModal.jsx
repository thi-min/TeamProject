// src/common/components/PhoneAuthModal.jsx
// 목적: "팝업 버튼 제외" "버튼 눌렀을 때 나오는 팝업 안쪽 내용"만 구현
//  제공된 마크업(.popup_box, .popup_item, .item_inner, .pop_close_btn, .pop_title_box, .pop_text_box, .pop_btn_box, .back_screen) 그대로 사용
//  jQuery 동작: body.pop_not_scroll 잠금/해제, 닫기 버튼 포커스, ESC 닫기, .pop_text_box tabindex 처리 재현
//  기능: 휴대폰 중복체크 → Firebase 인증번호 전송/검증
//
// 사용(부모):
//   <button type="button" className="phone_btn" onClick={()=>setOpen(true)}>휴대폰 인증</button>
//   <PhoneAuthModal open={open} onClose={()=>setOpen(false)} onVerified={(p)=>{/* { phone, firebaseUid } */}} mode="signup" />
//
// 백엔드(예시):
//   GET /api/members/exists/phone?phone={E.164} → { "exists": true/false }
//
// Firebase auth 인스턴스 경로는 프로젝트에 맞게 조정하세요.

import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
 navigate
 // [삭제] navigate: React가 export하지 않음. 페이지 전환은 부모가 담당하거나 useNavigate를 써야 함.
} from "react";
import { RecaptchaVerifier, signInWithPhoneNumber } from "firebase/auth";
import { auth } from "../firebase/firebase";
import "../style/common.css";

// ✅ 프로젝트에 맞게 수정 가능
const MEMBER_PHONE_EXISTS_API = "/api/members/exists/phone?phone=";

// --------유틸: KR 번호 → E.164(+82...) ----------
function formatToE164KR(input) {
  const digits = (input || "").replace(/[^0-9]/g, "");
  if (!digits) return "";
  if (digits.startsWith("0")) return `+82${digits.slice(1)}`; // 010 → +8210...
  if (digits.startsWith("82")) return `+${digits}`;
  if ((input || "").startsWith("+")) return input;
  return digits;
}

// --------중복체크(true=이미가입) ----------
async function checkPhoneDuplicate(phoneE164) {
  try {
    const res = await fetch(
      `${MEMBER_PHONE_EXISTS_API}${encodeURIComponent(phoneE164)}`,
      { method: "GET", headers: { Accept: "application/json" } }
    );
    if (!res.ok) return false; // UX상 막지 않음
    const json = await res.json();
    return Boolean(json?.exists);
  } catch {
    return false;
  }
}

// --------body 스크롤 잠금 ----------
function useBodyScrollLock(locked) {
  useEffect(() => {
    if (locked) document.body.classList.add("pop_not_scroll");
    else document.body.classList.remove("pop_not_scroll");
    return () => document.body.classList.remove("pop_not_scroll");
  }, [locked]);
}

export default function PhoneAuthModal({
  open, // 부모에서 제어
  onClose, // 닫기 핸들러
  onVerified, // 인증 성공 콜백: ({ phone, firebaseUid })
  mode = "signup", // 'signup'이면 중복번호 차단
  initialPhone = "",
  title = "휴대폰 번호 인증",
  dataPopup = "con_popup1", // data-popup 값 커스터마이즈 필요 시
}) {
  const [phone, setPhone] = useState(initialPhone);
  const [sending, setSending] = useState(false);
  const [sent, setSent] = useState(false);
  const [code, setCode] = useState("");
  const [verifying, setVerifying] = useState(false);
  const [message, setMessage] = useState("");
  const confirmationRef = useRef(null);
  const closeBtnRef = useRef(null);
  const textBoxRef = useRef(null);

  // open 시 초기화 닫기 버튼 포커스
  useEffect(() => {
    if (open) {
      setPhone((v) => v || initialPhone || "");
      setSending(false);
      setSent(false);
      setCode("");
      setVerifying(false);
      setMessage("");
      confirmationRef.current = null;
      // 닫기 버튼 포커스 (jQuery: .pop_close_btn.focus())
      setTimeout(() => closeBtnRef.current?.focus(), 0);
    }
  }, [open, initialPhone]);

  // body 스크롤 잠금 (jQuery: body.pop_not_scroll)
  useBodyScrollLock(open);

  // ESC 닫기
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => e.key === "Escape" && onClose?.();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, onClose]);

  // .pop_text_box tabindex 처리 (jQuery 로직 치환)
  useLayoutEffect(() => {
    if (!open || !textBoxRef.current) return;
    const el = textBoxRef.current;
    const style = window.getComputedStyle(el);
    const maxH = parseInt(style.maxHeight || "0", 10);
    const innerH = el.clientHeight;
    if (maxH && maxH === innerH) el.setAttribute("tabindex", "0");
    else el.removeAttribute("tabindex");
  }, [open, sent, code, phone, message]);

// [추가] reCAPTCHA 정리 함수(Timeout/만료 후 재생성 대비)
  function clearRecaptcha() {
    try {
      if (window.recaptchaVerifier?.clear) {
        window.recaptchaVerifier.clear();
      }
    } catch {}
    window.recaptchaVerifier = null;
  }

  // reCAPTCHA 준비: 존재하면 재사용 시도, 실패/만료면 새로 생성
  async function setupRecaptcha({ forceNew = false } = {}) {
    if (forceNew) clearRecaptcha();

    if (window.recaptchaVerifier) {
      // 위젯이 살아있는지 확인 (render가 한 번이라도 되었는지)
      try {
        await window.recaptchaVerifier.render();
        return window.recaptchaVerifier;
      } catch {
        // 재사용 실패 → 새로 생성
        clearRecaptcha();
      }
    }

    // 새로 생성 (invisible)
    window.recaptchaVerifier = new RecaptchaVerifier(auth, "recaptcha-container", {
      size: "invisible",
      callback: () => {
        // 토큰 발급 시 자동 호출
      },
      "expired-callback": () => {
        setMessage("인증 시간이 만료되었습니다. 다시 시도해 주세요.");
      },
    });

    await window.recaptchaVerifier.render();
    return window.recaptchaVerifier;
  }

  // 인증번호 전송
  const handleSendCode = useCallback(async () => {
    setMessage("");
    const formatted = formatToE164KR(phone);
    if (!formatted || formatted.length < 10) {
      setMessage("유효한 휴대폰 번호를 입력해 주세요.");
      return;
    }

    setSending(true);
    try {
      if (mode === "signup") {
        const exists = await checkPhoneDuplicate(formatted);
        if (exists) {
          setMessage("이미 가입되어있는 번호 입니다.");
          return;
        }
      }

      const verifier = await setupRecaptcha();
      const result = await signInWithPhoneNumber(auth, formatted, verifier);
      confirmationRef.current = result;
      setSent(true);
      setMessage("인증번호를 전송했습니다.");
    } catch (err) {
    // [추가] 원인 파악/회복을 위한 메시지/재생성 처리
      const raw = String(err?.message || "");
      let msg = "인증번호 전송 중 오류가 발생했습니다.";
      if (raw.includes("auth/invalid-phone-number")) {
        msg = "휴대폰 번호 형식이 올바르지 않습니다.";
      } else if (raw.includes("auth/too-many-requests") || raw.includes("quota")) {
        msg = "요청이 많습니다. 잠시 후 다시 시도해 주세요.";
      } else if (raw.toLowerCase().includes("timeout")) {
        msg = "인증 대기시간이 초과되었습니다. 다시 전송해 주세요.";
        clearRecaptcha();
        await setupRecaptcha({ forceNew: true });
      }
    console.error("[phone auth] send error:", err?.code, err?.message); // [추가] 디버깅용
      setMessage(msg);
    } finally {
      setSending(false);
    }
 }, [mode, phone]); // [수정] setupRecaptcha는 동일 스코프 함수라 deps에서 제외(불필요 재생성 방지)

  // 인증번호 검증
  const handleVerifyCode = useCallback(async () => {
    setMessage("");
    if (!code || code.length < 6) {
      setMessage("6자리 인증번호를 입력해 주세요.");
      return;
    }
    if (!confirmationRef.current) {
      setMessage("인증 세션이 없습니다. 다시 전송해 주세요.");
      return;
    }
    try {
      setVerifying(true);
      const cred = await confirmationRef.current.confirm(code);
      const phoneE164 = formatToE164KR(phone);
      // ✅ (중요) 회원가입 페이지에서 읽을 수 있도록 세션 저장
      sessionStorage.setItem("verifiedPhone", phoneE164);
      sessionStorage.setItem("phoneVerified", "true");
      // ✅ (중요) 회원가입 페이지에서 읽을 수 있도록 세션 저장
      onVerified?.({ phone: phoneE164, firebaseUid: cred?.user?.uid || null });
      onClose?.();
    } catch (err) {
      let msg = "인증번호가 올바르지 않습니다.";
      const m = String(err?.message || "");
      if (m.includes("auth/code-expired"))
        msg = "인증번호가 만료되었습니다. 다시 전송해 주세요.";
    console.error("[phone auth] verify error:", err?.code, err?.message); // [추가]
      setMessage(msg);
    } finally {
      setVerifying(false);
    }
  }, [code, onClose, onVerified, phone]);

// 언마운트 시 reCAPTCHA 정리
function clearRecaptcha() {
try {
  if (window.recaptchaVerifier?.clear) {
    window.recaptchaVerifier.clear();
  }
} catch {}
window.recaptchaVerifier = null;
}
// [삭제] 중복 정의된 clearRecaptcha를 위로 올려 통합(한 곳에서만 정의)

// [삭제] 내비게이션은 부모 컴포넌트(페이지)에서 책임짐.
//        이 컴포넌트는 onVerified 콜백만 호출해 단일 책임 유지.

  // === 제공한 "팝업 내부" 마크업 그대로 ===
  return (
    <div className="con_popup_box phone">
      {/* <div
        className={`popup_box ${open ? "active" : ""}`}
        data-popup={dataPopup}
        role="dialog"
        aria-modal="true"
        aria-label="휴대폰 인증 팝업"
        aria-hidden={!open}
        // inert: 숨김 상태에서 포커스/탭 방지 (브라우저에 따라 폴리필 필요할 수 있음)
        {...(!open ? { inert: "" } : {})}
        style={{ display: open ? "block" : "none" }}
      > */}
      <div
        className={`popup_box ${open ? "active" : ""}`}
        data-popup={dataPopup}
        role="dialog"
        aria-modal="true"
        aria-label="휴대폰 인증 팝업"
        // ✅ 불리언으로 전달 (닫혀 있을 때만 inert=true).
        inert={!open}
        // 접근성 보조: 화면리더에 숨김
        aria-hidden={!open}
        // 포인터 이벤트 차단(닫힘 상태에서 클릭 막기)
        style={{ display: open ? "block" : "none", pointerEvents: open ? "auto" : "none" }}
      >
        <div className="popup_item">
          <div className="item_inner" onClick={(e) => e.stopPropagation()}>
            {/* 닫기 버튼 */}
            <button
              type="button"
              className="pop_close_btn"
              onClick={onClose}
              ref={closeBtnRef}
              title="닫기"
            >
              닫기
            </button>

            {/* 타이틀 */}
            <div className="pop_title_box">
              {mode === "mypage" ? "휴대폰 번호 변경 인증" : title}
            </div>

            {/* 본문 영역(.pop_text_box) */}
            <div className="pop_text_box" ref={textBoxRef}>
              {/* 안내/에러 메시지 */}
              {message && <p className="temp_help margin_b_15">{message}</p>}
              <div className="pop_inner">
                {/* 휴대폰 번호 입력 */}
                <div className="temp_form md">
                  <label className="temp_label" htmlFor="phoneInput">
                    휴대폰 번호
                  </label>
                  <input
                    id="phoneInput"
                    className="temp_input"
                    type="tel"
                    placeholder="숫자만 입력 (예: 01012345678)"
                    value={phone}
                    onChange={(e) =>
                      setPhone(e.target.value.replace(/[^0-9]/g, ""))
                    }
                    disabled={sent}
                  />
                </div>

                {/* 인증번호 전송 버튼 */}
                <div className="pop_btn_box">
                  <span className="temp_btn md">
                    <button
                      type="button"
                      className="btn"
                      onClick={handleSendCode}
                      disabled={sending || sent}
                      title="인증번호 전송"
                    >
                      {sending ? "전송 중..." : sent ? "전송됨" : "인증번호 전송"}
                    </button>
                  </span>
                </div>
              </div>

              {/* 인증번호 입력/확인 (전송 후 표시) */}
              {sent && (
                <>
                  <div className="pop_inner">
                    <div className="temp_form md">
                      <label className="temp_label" htmlFor="codeInput">
                        인증번호(6자리)
                      </label>
                      <input
                        id="codeInput"
                        className="temp_input"
                        type="text"
                        inputMode="numeric"
                        maxLength={6}
                        placeholder="예: 123456"
                        value={code}
                        onChange={(e) =>
                          setCode(e.target.value.replace(/[^0-9]/g, ""))
                        }
                      />
                    </div>

                    <div className="pop_btn_box">
                      <span className="temp_btn md white">
                        <button
                          type="button"
                          className="btn chk_close_btn"
                          onClick={handleVerifyCode}
                          disabled={verifying}
                          title="인증확인"
                        >
                          {verifying ? "확인 중..." : "인증확인"}
                        </button>
                      </span>
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>

          {/* 배경(백스크린) */}
          <div className="back_screen" onClick={onClose} />
        </div>

        {/* Invisible reCAPTCHA target (size: 'invisible'이므로 표시 안 됨) */}
        <div id="recaptcha-container" />
      </div>
    </div>
  );
}
