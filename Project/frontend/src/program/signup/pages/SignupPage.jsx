// /program/signup/pages/SignupPage.jsx
/**
 * 일반가입과 카카오가입 엄격 분리
 * - 카카오: state.via === 'kakao' && state.kakaoId 존재할 때만 프리필/잠금/비번숨김
 * - 일반: 모든 필드 편집 가능, 비밀번호/확인 노출
 */

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import "../style/signup.css";

import api from "../../../common/api/axios";
import { apiCheckDuplicateId } from "../../member/services/memberApi";

const KAKAO_PREFILL_KEY = "kakao_prefill_v1";

function evaluatePassword(password, passwordCheck) {
  const result = { valid: true, issues: [], same: password === passwordCheck };
  if (!password || password.length < 8) {
    result.valid = false; result.issues.push("비밀번호는 8자 이상이어야 합니다.");
  }
  if (!/[A-Za-z]/.test(password) || !/[0-9]/.test(password)) {
    result.valid = false; result.issues.push("영문과 숫자를 모두 포함해야 합니다.");
  }
  if (!result.same) {
    result.valid = false; result.issues.push("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
  }
  return result;
}
const isEmail = (v) => /\S+@\S+\.\S+/.test(v || "");

const normalizeSexEnum = (v) => {
  if (!v) return "";
  const s = String(v).toUpperCase();
  if (s === "M" || s === "MALE") return "MAN";
  if (s === "F" || s === "FEMALE") return "WOMAN";
  return s; // 이미 MAN/WOMAN이면 그대로
};
const e164ToLocalDigits = (p) => {
  if (!p) return "";
  let digits = String(p).replace(/[^0-9]/g, "");
  if (digits.startsWith("82")) digits = "0" + digits.slice(2);
  return digits;
};
const formatPhoneKR = (raw) => {
  if (!raw) return "";
  let d = String(raw).replace(/\D/g, "");
  if (d.startsWith("82")) d = "0" + d.slice(2);
  if (d.startsWith("010") && d.length >= 11) return `010-${d.slice(3,7)}-${d.slice(7,11)}`;
  if (/^01[1-9]/.test(d)) {
    if (d.length === 10) return `${d.slice(0,3)}-${d.slice(3,6)}-${d.slice(6,10)}`;
    if (d.length >= 11) return `${d.slice(0,3)}-${d.slice(3,7)}-${d.slice(7,11)}`;
  }
  return d;
};

export default function SignupPage() {
  const location = useLocation();

  const refs = {
    memberId: useRef(null),
    memberPw: useRef(null),
    memberPwCheck: useRef(null),
    memberName: useRef(null),
    memberBirth: useRef(null),
    memberSex: useRef(null),
    memberPhone: useRef(null),
  };

  const [formData, setFormData] = useState({
    via: "", kakaoId: "",
    memberId: "", memberPw: "", memberPwCheck: "",
    memberName: "", memberBirth: "", memberSex: "",
    memberPhone: "", smsAgree: false,
    postcode: "", roadAddress: "", detailAddress: "", memberAddress: "",
  });

  /**
   * 진입 시 1회:
   * - 일반 진입: 카카오 흔적 제거 + via/kakaoId 초기화
   * - 카카오 진입(via==='kakao' && kakaoId): 프리필 세팅
   */
  useEffect(() => {
    const state = (location.state && typeof location.state === "object") ? location.state : null;
    const isKakaoState = !!(state && state.via === "kakao" && state.kakaoId);

    if (!isKakaoState) {
      // ✅ 일반 가입 모드
      try { sessionStorage.removeItem(KAKAO_PREFILL_KEY); } catch {}
      setFormData((p) => ({ 
        ...p,
        via: "",
        kakaoId: "",
        // 일반 모드에서는 입력 전부 가능해야 하므로 값도 비워 시작
        memberId: "",
        memberPhone: "",
      }));
      return;
    }

    // ✅ 카카오 모드: state 우선, 없으면 세션 프리필(백업) 사용
    let prefill = state;
    if (!prefill) {
      try {
        const raw = sessionStorage.getItem(KAKAO_PREFILL_KEY);
        prefill = raw ? JSON.parse(raw) : null;
      } catch { /* ignore */ }
    }
    if (!prefill) return; // 안전빵

    const yyyy = prefill.birthyear || "";
    const mmdd = prefill.birthday || "";
    const mm = mmdd?.slice(0, 2) || "";
    const dd = mmdd?.slice(2, 4) || "";
    const isoBirth = yyyy && mm && dd ? `${yyyy}-${mm}-${dd}` : "";
    const sex = normalizeSexEnum(prefill.gender);

    setFormData((prev) => ({
      ...prev,
      via: "kakao",
      kakaoId: prefill.kakaoId || "",
      memberId: (prefill.email || "").toLowerCase(),
      memberName: prefill.name || "",
      memberBirth: isoBirth,
      memberSex: sex,
      memberPhone: formatPhoneKR(prefill.phoneNumber) || "",
    }));

    try { sessionStorage.setItem(KAKAO_PREFILL_KEY, JSON.stringify(prefill)); } catch {}
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location.key]);

  /** isKakao = 파생값(진짜 카카오 가입일 때만 true) */
  const isKakao = formData.via === "kakao" && !!formData.kakaoId;

  const [idCheck, setIdCheck] = useState({
    loading: false, done: false, available: false, message: "", lastCheckedId: "",
  });
  const pwState = useMemo(
    () => evaluatePassword(formData.memberPw, formData.memberPwCheck),
    [formData.memberPw, formData.memberPwCheck]
  );
  const normalizedId = useMemo(
    () => (formData.memberId || "").trim().toLowerCase(),
    [formData.memberId]
  );
  const onlyDigits = useCallback((v) => (v || "").replace(/[^0-9]/g, ""), []);
  const normalizeDate = useCallback((d) => (d ? String(d).slice(0, 10) : ""), []);

  const handleChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;

    if (name === "memberPhone") {
      const display = formatPhoneKR(value);
      setFormData((prev) => ({ ...prev, memberPhone: display }));
      return;
    }
    const nextValue = type === "checkbox" ? !!checked : value;
    setFormData((prev) => {
      const next = { ...prev, [name]: nextValue };
      if (name === "memberId") {
        setIdCheck({ loading: false, done: false, available: false, message: "", lastCheckedId: "" });
      }
      return next;
    });
  }, []);

  const handleCheckDuplicateId = useCallback(async () => {
    const email = normalizedId;
    if (!email) {
      refs.memberId.current?.focus();
      return setIdCheck({ loading: false, done: true, available: false, message: "아이디(이메일)를 입력해주세요.", lastCheckedId: "" });
    }
    if (!isEmail(email)) {
      refs.memberId.current?.focus();
      return setIdCheck({ loading: false, done: true, available: false, message: "올바른 이메일 형식이 아닙니다.", lastCheckedId: "" });
    }
    try {
      setIdCheck((s) => ({ ...s, loading: true, message: "" }));
      const { available, message } = await apiCheckDuplicateId(email);
      setIdCheck({
        loading: false, done: true, available,
        message: message || (available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."),
        lastCheckedId: email,
      });
      alert(message || (available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."));
    } catch (err) {
      console.error(err);
      setIdCheck({
        loading: false, done: true, available: false,
        message: err.message || "아이디 확인 중 오류가 발생했습니다.",
        lastCheckedId: email,
      });
      alert(err.message || "아이디 확인 중 오류가 발생했습니다.");
    }
  }, [normalizedId]);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    const kakaoFlow = isKakao;

    if (!formData.memberId || !isEmail(formData.memberId)) {
      alert("이메일을 입력해주세요."); refs.memberId.current?.focus(); return;
    }
    if (!idCheck.done) { alert("아이디 중복체크를 완료해 주세요."); return; }
    if (!idCheck.available) { alert(idCheck.message || "이미 사용 중인 아이디입니다."); return; }
    if (idCheck.lastCheckedId !== normalizedId) { alert("아이디가 변경되었습니다. 다시 중복체크를 진행해 주세요."); return; }

    if (!formData.memberName) { alert("이름을 입력해주세요."); refs.memberName.current?.focus(); return; }
    if (!formData.memberBirth) { alert("생년월일을 입력해주세요."); refs.memberBirth.current?.focus(); return; }
    if (!formData.memberPhone) { alert("전화번호를 입력해주세요."); refs.memberPhone.current?.focus(); return; }
    if (!formData.memberAddress) { alert("주소를 입력해주세요."); return; }

    if (!kakaoFlow && !pwState.valid) {
      alert(pwState.issues[0]); refs.memberPw.current?.focus(); return;
    }

    const phoneDigits = onlyDigits(e164ToLocalDigits(formData.memberPhone));
    if (phoneDigits.length < 10 || phoneDigits.length > 11) {
      alert("전화번호는 10~11자리 숫자만 입력해주세요."); refs.memberPhone.current?.focus(); return;
    }

    const safeAddress = `${formData.memberAddress || ""} ${formData.detailAddress || ""}`.trim().slice(0, 100);

    const payload = {
      memberId: normalizedId,
      memberPw: kakaoFlow ? null : (formData.memberPw || "").trim(),
      memberName: (formData.memberName || "").trim(),
      memberBirth: normalizeDate(formData.memberBirth),
      memberPhone: phoneDigits,
      memberAddress: safeAddress,
      smsAgree: !!formData.smsAgree,
      memberSex: formData.memberSex,
      kakaoId: kakaoFlow ? (formData.kakaoId || null) : null,
      via: kakaoFlow ? "kakao" : "",
    };

    try {
      await api.post("/join/signup", payload, {
        headers: { "Content-Type": "application/json" },
        withCredentials: true, timeout: 10000,
      });
      alert("회원가입이 완료되었습니다.");
      window.location.href = "/login";
    } catch (err) {
      const data = err?.response?.data;
      const serverMsg = data?.message || data?.error || (typeof data === "string" ? data : "") || err.message || "회원가입 중 오류가 발생했습니다.";
      console.error("SIGNUP FAIL:", err?.response?.status, data || err.message);
      alert(serverMsg);
    }
  }, [formData, idCheck, normalizedId, pwState, onlyDigits, normalizeDate, isKakao]);

  return (
    <div className="signup-container">
      <form noValidate onSubmit={handleSubmit}>
        {/* 서버 디버깅용 히든 */}
        <input type="hidden" name="via" value={isKakao ? "kakao" : ""} />
        <input type="hidden" name="kakaoId" value={isKakao ? (formData.kakaoId || "") : ""} />

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
              {/* 아이디 */}
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
                      readOnly={isKakao}  // 일반모드면 입력 가능
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
                    <div className={`hint ${idCheck.available ? "ok" : "warn"}`} style={{ marginTop: 8 }}>
                      {idCheck.message}
                    </div>
                  )}
                </td>
              </tr>

              {/* 비밀번호 (카카오면 숨김) */}
              <tr className={isKakao ? "kakao-hidden" : ""}>
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
                      placeholder={isKakao ? "카카오 가입은 비밀번호 없이 진행됩니다(선택 입력)." : ""}
                      autoComplete="new-password"
                    />
                  </div>
                  <span className="form_winning">비밀번호는 8~20자 영문, 숫자로 구성되어있어야 합니다.</span>
                </td>
              </tr>

              {/* 비밀번호 확인 (카카오면 숨김) */}
              <tr className={isKakao ? "kakao-hidden" : ""}>
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
                      placeholder={isKakao ? "카카오 가입은 비밀번호 확인이 필요하지 않습니다." : ""}
                      autoComplete="new-password"
                    />
                  </div>
                </td>
              </tr>

              {/* 이름 */}
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

              {/* 생년월일 */}
              <tr>
                <th scope="row">생년월일</th>
                <td>
                  <div className="temp_form md w40p">
                    <input
                      ref={refs.memberBirth}
                      className="temp_input"
                      type="date"
                      name="memberBirth"
                      value={formData.memberBirth || ""}
                      onChange={handleChange}
                    />
                  </div>
                </td>
              </tr>

              {/* 성별 */}
              <tr>
                <th scope="row">성별</th>
                <td>
                  <span className="temp_form md">
                    <input
                      type="radio" id="sex-man" name="memberSex" value="MAN"
                      className="temp_radio" checked={formData.memberSex === "MAN"} onChange={handleChange}
                      ref={formData.memberSex === "" ? refs.memberSex : null}
                    />
                    <label htmlFor="sex-man">남</label>
                  </span>
                  <span className="temp_form md">
                    <input
                      type="radio" id="sex-woman" name="memberSex" value="WOMAN"
                      className="temp_radio" checked={formData.memberSex === "WOMAN"} onChange={handleChange}
                    />
                    <label htmlFor="sex-woman">여</label>
                  </span>
                </td>
              </tr>

              {/* 전화번호 + 수신동의 */}
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
                      readOnly={isKakao}    // 일반모드면 입력 가능
                      placeholder="예: 010-1234-5678"
                    />
                  </div>
                  <span className="temp_form">
                    <input
                      type="checkbox" id="smsYn" name="smsAgree"
                      className="temp_check" checked={!!formData.smsAgree} onChange={handleChange}
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
                      <input className="temp_input" type="text" name="postcode" value={formData.postcode || ""} readOnly placeholder="우편번호" />
                    </div>
                    <div className="temp_btn md">
                      <button
                        type="button"
                        className="btn"
                        onClick={async () => {
                          try {
                            if (!(window.daum && window.daum.Postcode)) {
                              await new Promise((resolve, reject) => {
                                const s = document.createElement("script");
                                s.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
                                s.async = true; s.onload = resolve; s.onerror = reject;
                                document.head.appendChild(s);
                              });
                            }
                            new window.daum.Postcode({
                              oncomplete: (data) => {
                                const zonecode = data.zonecode || "";
                                const roadAddr = (data.roadAddress || "").trim();
                                const jibunAddr = (data.jibunAddress || "").trim();
                                const baseAddress = roadAddr || jibunAddr;
                                setFormData((prev) => ({
                                  ...prev, postcode: zonecode, roadAddress: roadAddr, memberAddress: baseAddress,
                                }));
                              },
                            }).open();
                          } catch {
                            alert("주소 검색 스크립트 로딩에 실패했습니다.");
                          }
                        }}
                      >
                        주소검색
                      </button>
                    </div>
                  </div>

                  <div className="temp_form lg w100p">
                    <input className="temp_input" type="text" name="roadAddress" value={formData.roadAddress || ""} readOnly placeholder="기본주소(도로명)" />
                  </div>

                  <div className="temp_form lg w100p">
                    <input
                      className="temp_input" type="text" name="detailAddress"
                      value={formData.detailAddress || ""}
                      onChange={(e) => setFormData((prev) => ({ ...prev, detailAddress: e.target.value }))}
                      placeholder="상세주소"
                    />
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <div className="form_btn_box">
            <div className="temp_btn md">
              <button type="submit" className="btn">회원가입</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}
