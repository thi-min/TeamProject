// src/program/signup/pages/SignupPage.jsx
// 목적: 회원가입 폼 (휴대폰번호 + 문자 수신동의 + 카카오 주소 팝업)
// - 휴대폰 인증 팝업에서 세션에 저장한 번호(+82...)를 010 숫자만으로 1회 주입
// - 이미 사용자가 입력한 값이 있으면 덮어쓰지 않음(안전)
// - 성별은 "MAN"/"WOMAN" 그대로 서버로 전송

import React, { useCallback, useMemo, useState, useRef, useEffect } from "react";
import "../style/signup.css";
import api from "../../../common/api/axios";
import { apiCheckDuplicateId } from "../../member/services/memberApi";

/** 비밀번호 유효성 검사 */
function evaluatePassword(password, passwordCheck) {
  const result = { valid: true, issues: [], same: password === passwordCheck };
  if (!password || password.length < 8) {
    result.valid = false;
    result.issues.push("비밀번호는 8자 이상이어야 합니다.");
  }
  if (!/[A-Za-z]/.test(password) || !/[0-9]/.test(password)) {
    result.valid = false;
    result.issues.push("영문과 숫자를 모두 포함해야 합니다.");
  }
  if (!result.same) {
    result.valid = false;
    result.issues.push("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
  }
  return result;
}

/** 간단 이메일 형식 체크 */
const isEmail = (v) => /\S+@\S+\.\S+/.test(v || "");

export default function SignupPage() {
  /** 포커스 이동용 refs */
  const refs = {
    memberId: useRef(null),
    memberPw: useRef(null),
    memberPwCheck: useRef(null),
    memberName: useRef(null),
    memberBirth: useRef(null),
    memberSex: useRef(null),
    memberPhone: useRef(null),
  };

  /** 폼 상태 */
  const [formData, setFormData] = useState({
    memberId: "",
    memberPw: "",
    memberPwCheck: "",
    memberName: "",
    memberBirth: "",
    memberPhone: "",     // ✅ 여기에 인증된 번호를 1회 주입
    postcode: "",
    memberAddress: "",
    roadAddress: "",
    detailAddress: "",
    smsAgree: false,     // ✅ 문자 수신동의 체크박스
    memberSex: "MAN",    // ✅ "MAN"/"WOMAN" 그대로 전송
  });

  /** 아이디(이메일) 중복체크 상태 */
  const [idCheck, setIdCheck] = useState({
    loading: false,
    done: false,
    available: false,
    message: "",
    lastCheckedId: "",
  });

  /** 비밀번호 유효성 */
  const [pwState, setPwState] = useState(() => evaluatePassword("", ""));

  /** 이메일 정규화 */
  const normalizedId = useMemo(
    () => (formData.memberId || "").trim().toLowerCase(),
    [formData.memberId]
  );

  /** 유틸 */
  const onlyDigits = useCallback((v) => (v || "").replace(/[^0-9]/g, ""), []);
  const normalizeDate = useCallback((d) => (d ? String(d).slice(0, 10) : ""), []);

  /** ✅ +82(국제) → 010(국내) 변환 */
  const e164ToLocal = useCallback((p) => {
    if (!p) return "";
    const m = String(p).trim().match(/^\+82(\d{9,10})$/);
    return m ? `0${m[1]}` : p; // 예: +821012345678 → 01012345678
  }, []);

  /** ✅ [추가] 인증된 번호 세션에서 1회 주입 */
  useEffect(() => {
    try {
      const verified = sessionStorage.getItem("phoneVerified") === "true";
      const e164 = sessionStorage.getItem("verifiedPhone"); // 예: +8210...
      if (!verified || !e164) return;

      setFormData((prev) => {
        // 이미 사용자가 입력해둔 값이 있으면 덮어쓰지 않음
        if (prev.memberPhone && prev.memberPhone.trim().length > 0) return prev;
        const local = e164ToLocal(e164);
        const digits = onlyDigits(local);
        // 10~11자리일 때만 주입
        if (digits.length >= 10 && digits.length <= 11) {
          return { ...prev, memberPhone: digits };
        }
        return prev;
      });

      // 주입 후 플래그를 비워 반복 주입 방지(선택)
      // sessionStorage.removeItem("phoneVerified");
      // sessionStorage.removeItem("verifiedPhone");
    } catch {}
  }, [e164ToLocal, onlyDigits]);

  /** input 변경 핸들러 */
  const handleChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;

    if (name === "memberPhone") {
      // 전화번호는 숫자만 허용
      const onlyNums = (value || "").replace(/[^0-9]/g, "");
      setFormData((prev) => ({ ...prev, memberPhone: onlyNums }));
      return;
    }

    const nextValue = type === "checkbox" ? !!checked : value;

    setFormData((prev) => {
      const next = { ...prev, [name]: nextValue };

      // 아이디가 바뀌면 중복체크 결과 무효화
      if (name === "memberId") {
        setIdCheck({
          loading: false,
          done: false,
          available: false,
          message: "",
          lastCheckedId: "",
        });
      }

      // 비밀번호 유효성 재평가
      if (name === "memberPw" || name === "memberPwCheck") {
        setPwState(evaluatePassword(next.memberPw, next.memberPwCheck));
      }
      return next;
    });
  }, []);

  /** 아이디 중복체크 */
  const handleCheckDuplicateId = useCallback(async () => {
    const email = normalizedId;

    if (!email) {
      refs.memberId.current?.focus();
      return setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: "아이디(이메일)를 입력해주세요.",
        lastCheckedId: "",
      });
    }
    if (!isEmail(email)) {
      refs.memberId.current?.focus();
      return setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: "올바른 이메일 형식이 아닙니다.",
        lastCheckedId: "",
      });
    }

    try {
      setIdCheck((s) => ({ ...s, loading: true, message: "" }));
      const { available, message } = await apiCheckDuplicateId(email);
      setIdCheck({
        loading: false,
        done: true,
        available,
        message:
          message ||
          (available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."),
        lastCheckedId: email,
      });
      alert(message || (available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."));
    } catch (err) {
      console.error(err);
      setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: err.message || "아이디 확인 중 오류가 발생했습니다.",
        lastCheckedId: email,
      });
      alert(err.message || "아이디 확인 중 오류가 발생했습니다.");
    }
  }, [normalizedId]);

  /** 카카오(다음) 우편번호 스크립트 1회 로드 */
  const loadDaumPostcodeScript = useCallback(() => {
    return new Promise((resolve, reject) => {
      if (window.daum && window.daum.Postcode) return resolve();

      const existing = document.querySelector('script[data-daum-postcode="true"]');
      if (existing) {
        existing.addEventListener("load", () => resolve());
        existing.addEventListener("error", (e) => reject(e));
        return;
      }

      const script = document.createElement("script");
      script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
      script.async = true;
      script.setAttribute("data-daum-postcode", "true");
      script.onload = () => resolve();
      script.onerror = () => reject(new Error("Daum Postcode script load failed"));
      document.head.appendChild(script);
    });
  }, []);

  /** 카카오 주소검색 팝업 */
  const openPostcodePopup = useCallback(async () => {
    try {
      await loadDaumPostcodeScript();
      new window.daum.Postcode({
        oncomplete: (data) => {
          const zonecode = data.zonecode || "";
          const roadAddr = (data.roadAddress || "").trim();
          const jibunAddr = (data.jibunAddress || "").trim();
          const baseAddress = roadAddr || jibunAddr;

          setFormData((prev) => ({
            ...prev,
            postcode: zonecode,
            roadAddress: roadAddr,      // 화면표시용
            memberAddress: baseAddress, // 서버전송용
          }));
        },
      }).open();
    } catch (e) {
      console.error(e);
      alert("주소 검색 스크립트 로딩에 실패했습니다.");
    }
  }, [loadDaumPostcodeScript]);

  /** 제출 */
  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();

      // 기본 검증
      if (!formData.memberId || !isEmail(formData.memberId)) {
        alert("이메일을 입력해주세요.");
        refs.memberId.current?.focus();
        return;
      }
      if (!idCheck.done) {
        alert("아이디 중복체크를 완료해 주세요.");
        return;
      }
      if (!idCheck.available) {
        alert(idCheck.message || "이미 사용 중인 아이디입니다.");
        return;
      }
      if (idCheck.lastCheckedId !== normalizedId) {
        alert("아이디가 변경되었습니다. 다시 중복체크를 진행해 주세요.");
        return;
      }
      if (!formData.memberName) {
        alert("이름을 입력해주세요.");
        refs.memberName.current?.focus();
        return;
      }
      if (!formData.memberBirth) {
        alert("생년월일을 입력해주세요.");
        refs.memberBirth.current?.focus();
        return;
      }
      if (!formData.memberPhone) {
        alert("전화번호를 입력해주세요.");
        refs.memberPhone.current?.focus();
        return;
      }
      if (!formData.memberAddress) {
        alert("주소를 입력해주세요.");
        return;
      }
      if (!pwState.valid) {
        alert(pwState.issues[0]);
        refs.memberPw.current?.focus();
        return;
      }

      // 전화번호 숫자/길이 검증
      const phoneDigits = onlyDigits(formData.memberPhone);
      if (phoneDigits.length < 10 || phoneDigits.length > 11) {
        alert("전화번호는 10~11자리 숫자만 입력해주세요.");
        refs.memberPhone.current?.focus();
        return;
      }

      // 주소 길이 방어(필요 시 길이 조정)
      const safeAddress = `${formData.memberAddress || ""} ${formData.detailAddress || ""}`
        .trim()
        .slice(0, 100);

      // 서버 전송 payload
      const payload = {
        memberId: normalizedId,
        memberPw: (formData.memberPw || "").trim(),
        memberName: (formData.memberName || "").trim(),
        memberBirth: normalizeDate(formData.memberBirth), // yyyy-MM-dd
        memberPhone: phoneDigits,                         // 010 숫자만
        memberAddress: safeAddress,
        smsAgree: !!formData.smsAgree,                    // boolean
        memberSex: formData.memberSex,                    // "MAN" | "WOMAN"
      };

      console.log("[SIGNUP payload]", payload);

      try {
        await api.post("/join/signup", payload, {
          headers: { "Content-Type": "application/json" },
          withCredentials: true,
          timeout: 10000,
        });
        alert("회원가입이 완료되었습니다.");
        window.location.href = "/login";
      } catch (err) {
        const status = err?.response?.status;
        const data = err?.response?.data;
        console.error("SIGNUP FAIL:", status, data || err.message);
        const serverMsg =
          data?.message ||
          data?.error ||
          (typeof data === "string" ? data : "") ||
          err.message ||
          "회원가입 중 오류가 발생했습니다. 콘솔 로그를 확인해 주세요.";
        alert(serverMsg);
      }
    },
    [formData, idCheck, normalizedId, onlyDigits, normalizeDate, pwState]
  );

  return (
    <div className="signup-container">
      <form noValidate onSubmit={handleSubmit}>
        <div className="form_top_box">
          <div className="form_top_item">
            <div className="form_icon type2"></div>
            <div className="form_title">회원가입</div>
            <div className="form_desc">
              <p>회원가입을 환영합니다.</p>
              <p>아래의 입력칸을 모두 입력해주시기 바랍니다.</p>
            </div>
          </div>
        </div>

        <div className="form_wrap">
          <table className="table type2 responsive">
            <tbody>
              <tr>
                <th scope="row">아이디</th>
                <td>
                  <span className="temp_form md">
                    <input
                      ref={refs.memberId}
                      type="email"
                      name="memberId"
                      className="temp_input"
                      value={formData.memberId}
                      onChange={handleChange}
                      placeholder="예: user@example.com"
                    />
                  </span>
                  <span className="temp_btn md id_check_btn">
                    <button
                      type="button"
                      className="btn"
                      onClick={handleCheckDuplicateId}
                      disabled={idCheck.loading || !formData.memberId.trim()}
                    >
                      {idCheck.loading ? "확인 중..." : "중복체크"}
                    </button>
                  </span>
                  {idCheck.done && (
                    <div
                      className={`hint ${idCheck.available ? "ok" : "warn"}`}
                      style={{ marginTop: 8 }}
                    >
                      {idCheck.message}
                    </div>
                  )}
                </td>
              </tr>

              <tr>
                <th scope="row">비밀번호</th>
                <td>
                  <div className="temp_form md w40p">
                    <input
                      ref={refs.memberPw}
                      className="temp_input"
                      type="password"
                      name="memberPw"
                      value={formData.memberPw}
                      onChange={handleChange}
                    />
                  </div>
                  <span className="form_winning">
                    비밀번호는 8~20자 영문, 숫자로 구성되어있어야 합니다.
                  </span>
                </td>
              </tr>

              <tr>
                <th scope="row">비밀번호 확인</th>
                <td>
                  <div className="temp_form md w40p">
                    <input
                      ref={refs.memberPwCheck}
                      className="temp_input"
                      type="password"
                      name="memberPwCheck"
                      value={formData.memberPwCheck}
                      onChange={handleChange}
                    />
                  </div>
                  {formData.memberPwCheck && (
                    <div
                      className={`hint ${pwState.same ? "ok" : "error"}`}
                      style={{ marginTop: 8 }}
                    >
                      {pwState.same
                        ? "비밀번호가 일치합니다."
                        : "비밀번호가 일치하지 않습니다."}
                    </div>
                  )}
                </td>
              </tr>

              <tr>
                <th scope="row">이름</th>
                <td>
                  <div className="temp_form md w40p">
                    <input
                      ref={refs.memberName}
                      className="temp_input"
                      type="text"
                      name="memberName"
                      value={formData.memberName}
                      onChange={handleChange}
                      placeholder="이름 입력"
                    />
                  </div>
                </td>
              </tr>

              <tr>
                <th scope="row">생년월일</th>
                <td>
                  <div className="temp_form md w40p">
                    <input
                      ref={refs.memberBirth}
                      className="temp_input"
                      type="date"
                      name="memberBirth"
                      value={formData.memberBirth}
                      onChange={handleChange}
                    />
                  </div>
                </td>
              </tr>

              <tr>
                <th scope="row">성별</th>
                <td>
                  <span className="temp_form md">
                    <input
                      type="radio"
                      id="sex-man"
                      name="memberSex"
                      value="MAN"
                      className="temp_radio"
                      checked={formData.memberSex === "MAN"}
                      onChange={handleChange}
                      ref={formData.memberSex === "" ? refs.memberSex : null}
                    />
                    <label htmlFor="sex-man">남</label>
                  </span>
                  <span className="temp_form md">
                    <input
                      type="radio"
                      id="sex-woman"
                      name="memberSex"
                      value="WOMAN"
                      className="temp_radio"
                      checked={formData.memberSex === "WOMAN"}
                      onChange={handleChange}
                    />
                    <label htmlFor="sex-woman">여</label>
                  </span>
                </td>
              </tr>

              {/* 전화번호 + 문자 수신동의 */}
              <tr>
                <th scope="row">전화번호</th>
                <td className="form_flex">
                  <div className="temp_form md form_4">
                    <input
                      ref={refs.memberPhone}
                      className="temp_input"
                      type="tel"
                      name="memberPhone"
                      value={formData.memberPhone}
                      onChange={handleChange}
                      readOnly
                    />
                  </div>
                  <span className="temp_form">
                    <input
                      type="checkbox"
                      id="smsYn"
                      name="smsAgree"
                      className="temp_check"
                      checked={formData.smsAgree}
                      onChange={handleChange}
                    />
                    <label htmlFor="smsYn">수신동의</label>
                  </span>
                </td>
              </tr>

              {/* 주소 */}
              <tr>
                <th scope="row">주소</th>
                <td className="address_form">
                  <div className="form_flex">
                    <div className="temp_form md w20p">
                      <input
                        className="temp_input"
                        type="text"
                        name="postcode"
                        value={formData.postcode || ""}
                        readOnly
                        placeholder="우편번호"
                      />
                    </div>
                    <div className="temp_btn md">
                      <button type="button" className="btn" onClick={openPostcodePopup}>
                        주소검색
                      </button>
                    </div>
                  </div>

                  <div className="temp_form lg w100p">
                    <input
                      className="temp_input"
                      type="text"
                      name="roadAddress"
                      value={formData.roadAddress || ""}
                      readOnly
                      placeholder="기본주소(도로명)"
                    />
                  </div>

                  <div className="temp_form lg w100p">
                    <input
                      className="temp_input"
                      type="text"
                      name="detailAddress"
                      value={formData.detailAddress || ""}
                      onChange={(e) =>
                        setFormData((prev) => ({
                          ...prev,
                          detailAddress: e.target.value,
                        }))
                      }
                      placeholder="상세주소"
                    />
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <div className="form_btn_box">
            <div className="temp_btn md">
              <button type="submit" className="btn">
                회원가입
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}
