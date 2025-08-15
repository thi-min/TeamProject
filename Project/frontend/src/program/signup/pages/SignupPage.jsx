// src/program/signup/pages/SignupPage.jsx
import React, { useCallback, useMemo, useState, useRef } from "react";
import "../style/signup.css";

/** 비밀번호 유효성 검사 함수 */
function evaluatePassword(password, passwordCheck) {
  const result = {
    valid: true,
    issues: [],
    same: password === passwordCheck,
  };

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
  /** 각 input ref */
  const refs = {
    memberId: useRef(null),
    memberPw: useRef(null),
    memberPwCheck: useRef(null),
    memberName: useRef(null),
    memberBirth: useRef(null),
    memberSex: useRef(null),
    memberPhone: useRef(null),
  };

  /** 회원가입 폼 데이터 상태 */
  const [formData, setFormData] = useState({
    memberId: "",
    memberPw: "",
    memberPwCheck: "",
    memberName: "",
    memberBirth: "",
    memberPhone: "",
    postcode: "",
    memberAddress: "", // 서버 전송용 주소
    roadAddress: "", // 화면 표시용 도로명주소
    detailAddress: "",
    smsAgree: false,
    memberSex: "MAN",
  });

  /** 아이디 중복체크 상태 */
  const [idCheck, setIdCheck] = useState({
    loading: false,
    done: false,
    available: false,
    message: "",
  });

  /** 비밀번호 유효성 상태 */
  const [pwState, setPwState] = useState(() => evaluatePassword("", ""));

  /** input 값 변경 핸들러 */
  const handleChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;

    if (name === "memberPhone") {
      const onlyNums = (value || "").replace(/[^0-9]/g, "");
      setFormData((prev) => ({ ...prev, memberPhone: onlyNums }));
      return;
    }

    const nextValue = type === "checkbox" ? !!checked : value;

    setFormData((prev) => {
      const next = { ...prev, [name]: nextValue };

      if (name === "memberId") {
        setIdCheck({
          loading: false,
          done: false,
          available: false,
          message: "",
        });
      }

      if (name === "memberPw" || name === "memberPwCheck") {
        setPwState(evaluatePassword(next.memberPw, next.memberPwCheck));
      }
      return next;
    });
  }, []);

  /** 아이디 중복체크 실행 */
  const handleCheckDuplicateId = useCallback(async () => {
    const email = formData.memberId.trim();

    if (!email) {
      refs.memberId.current?.focus();
      return setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: "아이디(이메일)를 입력해주세요.",
      });
    }
    if (!isEmail(email)) {
      refs.memberId.current?.focus();
      return setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: "올바른 이메일 형식이 아닙니다.",
      });
    }

    try {
      setIdCheck((s) => ({ ...s, loading: true }));
      await new Promise((r) => setTimeout(r, 500));
      const available = !email.includes("taken");

      setIdCheck({
        loading: false,
        done: true,
        available,
        message: available
          ? "사용 가능한 아이디입니다."
          : "이미 사용 중인 아이디입니다.",
      });
    } catch (err) {
      console.error(err);
      setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: "아이디 확인 중 오류가 발생했습니다.",
      });
    }
  }, [formData.memberId]);

  /** 회원가입 폼 제출 */
  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();

      // 입력값 순서대로 하나씩 검증
      if (!formData.memberId || !isEmail(formData.memberId)) {
        alert("이메일을 입력해주세요.");
        return;
      }
      if (!formData.memberName) {
        alert("이름을 입력해주세요.");
        return;
      }
      if (!formData.memberBirth) {
        alert("생년월일을 입력해주세요.");
        return;
      }
      if (!formData.memberPhone) {
        alert("전화번호를 입력해주세요.");
        return;
      }
      if (!formData.memberAddress) {
        alert("주소를 입력해주세요.");
        return;
      }
      if (!pwState.valid) {
        alert(pwState.issues[0]); // 첫 번째 비밀번호 오류만 표시
        return;
      }

      // 전송 데이터
      const payload = {
        memberId: formData.memberId,
        memberPw: formData.memberPw,
        memberName: formData.memberName,
        memberBirth: formData.memberBirth,
        memberPhone: formData.memberPhone,
        memberAddress: `${formData.postcode ? `[${formData.postcode}] ` : ""}${
          formData.memberAddress
        } ${formData.detailAddress || ""}`.trim(),
        smsAgree: !!formData.smsAgree,
        memberSex: formData.memberSex,
      };

      try {
        const res = await fetch("/signup", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (!res.ok) {
          const errMsg = await res.text();
          throw new Error(errMsg || "회원가입 요청 실패");
        }

        alert("회원가입이 완료되었습니다.");
        // 가입 완료 후 로그인 페이지로 이동 예시
        window.location.href = "/login";
      } catch (err) {
        console.error(err);
        alert(`회원가입 실패: ${err.message}`);
      }
    },
    [formData, pwState]
  );

  /** 카카오 주소 API 스크립트 로드 */
  const loadDaumPostcodeScript = useCallback(() => {
    return new Promise((resolve, reject) => {
      if (window.daum && window.daum.Postcode) return resolve();

      const existing = document.querySelector(
        'script[data-daum-postcode="true"]'
      );
      if (existing) {
        existing.addEventListener("load", () => resolve());
        existing.addEventListener("error", reject);
        return;
      }

      const script = document.createElement("script");
      script.src =
        "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
      script.async = true;
      script.setAttribute("data-daum-postcode", "true");
      script.onload = () => resolve();
      script.onerror = () =>
        reject(new Error("Daum Postcode script load failed"));
      document.head.appendChild(script);
    });
  }, []);

  /** 카카오 주소 검색 팝업 열기 */
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
            roadAddress: roadAddr,
            memberAddress: baseAddress,
          }));
        },
      }).open();
    } catch (e) {
      console.error(e);
      alert("주소 검색 스크립트 로딩에 실패했습니다.");
    }
  }, [loadDaumPostcodeScript]);

  return (
    <div className="signup-container">
      {/* ✅ 브라우저 기본 검증 끔 */}
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
                  {idCheck.message && (
                    <div
                      className={`hint ${
                        idCheck.done ? (idCheck.available ? "ok" : "warn") : ""
                      }`}
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
                      placeholder="숫자만 입력"
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

              {/* 주소 : 우편번호 + 검색 + 도로명 + 상세 */}
              <tr>
                <th scope="row">주소</th>
                <td className="address_form">
                  {/* 우편번호 */}
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

                    {/* 주소검색 버튼 (클래스 유지) */}
                    <div className="temp_btn md">
                      <button
                        type="button"
                        className="btn"
                        onClick={openPostcodePopup}
                      >
                        주소검색
                      </button>
                    </div>
                  </div>

                  {/* 도로명 주소 */}
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

                  {/* 상세주소 입력 */}
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