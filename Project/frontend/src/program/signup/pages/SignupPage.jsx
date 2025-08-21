// src/program/signup/pages/SignupPage.jsx
import React, { useCallback, useMemo, useState, useRef } from "react";
import "../style/signup.css";
// ✅ 백엔드로 요청을 보내는 axios 인스턴스 (baseURL: http://localhost:8090)
import api from "../../../common/api/axios";
// ✅ 아이디 중복체크 API (서버 응답을 표준화해서 available 여부/메시지 반환)
import { apiCheckDuplicateId } from "../../member/services/memberApi";

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

  /**
   * 아이디 중복체크 상태
   * - lastCheckedId: "검사 당시" 아이디(소문자/trim 정규화된 값). 입력값이 바뀌면 검사 무효화
   */
  const [idCheck, setIdCheck] = useState({
    loading: false,
    done: false,
    available: false,
    message: "",
    lastCheckedId: "", // ✅ 추가: 검사 당시 아이디 저장
  });

  /** 비밀번호 유효성 상태 */
  const [pwState, setPwState] = useState(() => evaluatePassword("", ""));

  /** 휴대폰 인증 */
  const [phoneVerifyOpen, setPhoneVerifyOpen] = useState(false);
  const [phoneVerified, setPhoneVerified] = useState(false);

  /** 현재 입력된 아이디(이메일)를 소문자/trim으로 정규화 */
  const normalizedId = useMemo(
    () => (formData.memberId || "").trim().toLowerCase(),
    [formData.memberId]
  );

  /** input 값 변경 핸들러 */
  const handleChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;

    if (name === "memberPhone") {
      // 숫자만 유지
      const onlyNums = (value || "").replace(/[^0-9]/g, "");
      setFormData((prev) => ({ ...prev, memberPhone: onlyNums }));
      return;
    }

    const nextValue = type === "checkbox" ? !!checked : value;

    setFormData((prev) => {
      const next = { ...prev, [name]: nextValue };

      // 아이디가 바뀌면 → 중복체크 결과 무효화
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

  /** 아이디 중복체크 실행 */
  const handleCheckDuplicateId = useCallback(async () => {
    const email = normalizedId; // 이메일은 보통 대소문자 구분 X → 소문자 정규화

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
      // ✅ 실제 API 호출 (서버 응답 포맷은 apiCheckDuplicateId에서 표준화)
      const { available, message } = await apiCheckDuplicateId(email);

      setIdCheck({
        loading: false,
        done: true,
        available,
        message:
          message ||
          (available
            ? "사용 가능한 아이디입니다."
            : "이미 사용 중인 아이디입니다."),
        lastCheckedId: email, // ✅ 검사 당시 아이디 저장
      });

      // 즉시 사용자 알림
      alert(
        message ||
          (available
            ? "사용 가능한 아이디입니다."
            : "이미 사용 중인 아이디입니다.")
      );
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

  /** 회원가입 폼 제출 */
  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();

      // 0) 아이디 형식/중복체크 선검증
      if (!formData.memberId || !isEmail(formData.memberId)) {
        alert("이메일을 입력해주세요.");
        refs.memberId.current?.focus();
        return;
      }
      // - 중복체크가 아직 안 되었거나( done=false )
      // - 검사 결과가 '사용 불가'이거나
      // - 검사 이후에 아이디가 변경됨(lastCheckedId !== normalizedId)
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

      // 1) 기본 입력값 검증
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

      // 2) 비밀번호 유효성
      if (!pwState.valid) {
        alert(pwState.issues[0]); // 첫 번째 비밀번호 오류만 표시
        refs.memberPw.current?.focus();
        return;
      }

      // 3) 전송 데이터 (아이디는 서버 대소문자 무시 정책이라면 소문자 전송 권장)
      const payload = {
        memberId: normalizedId, // ← 서버가 대소문자 구분하지 않는다면 소문자로 통일 전송
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
        // 최종 요청 = http://localhost:8090/signup
        // (컨트롤러에 @RequestMapping("/auth")가 있다면 "/auth/signup"으로 변경)
        await api.post("/signup", payload);

        alert("회원가입이 완료되었습니다.");
        window.location.href = "/login";
      } catch (err) {
        // 서버에서 {message: "..."} 내려주면 우선 표시
        const serverMsg =
          err?.response?.data?.message ||
          err?.response?.data?.error ||
          err?.message ||
          "회원가입 요청 실패";
        console.error(err);
        alert(`회원가입 실패: ${serverMsg}`);
      }
    },
    [formData, pwState, idCheck, normalizedId]
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

  /** 휴대폰 인증 콜백 폼 데이터에 반영  */
  function handlePhoneVerified({ phone }) {
    // 예: formData.memberPhone 에 E.164(+82...) 저장하고, 표시용은 010 형태로 변환해서 보여줘도 됨
    // setFormData((prev) => ({ ...prev, memberPhone: phone }));
    setPhoneVerified(true);
  }
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
                  {/* 검사 결과/안내 */}
                  {idCheck.done && (
                    <div
                      className={`hint ${idCheck.available ? "ok" : "warn"}`}
                      style={{ marginTop: 8 }}
                    >
                      {idCheck.message}
                      {/* 아이디가 검사 이후에 변경되었으면 재검사 안내 */}
                      {/* {idCheck.lastCheckedId !== normalizedId && (
                        <span>
                          {" "}
                          (아이디가 변경되어 다시 중복체크가 필요합니다.)
                        </span>
                      )} */}
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
                  <div className="temp_btn md">
                    <button
                      type="button"
                      className="btn"
                      onClick={() => setPhoneVerifyOpen(true)}
                    >
                      휴대폰 인증
                    </button>
                    {phoneVerified && (
                      <span className="temp_help success">인증 완료</span>
                    )}
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
