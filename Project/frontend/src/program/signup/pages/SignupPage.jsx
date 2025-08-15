// ✅ 핵심 변경 요약
// 1) <form noValidate onSubmit={handleSubmit}>  ← 브라우저 기본 검증 off
// 2) required 제거해도 되고, 남겨둬도 noValidate면 막지 않음
// 3) 첫 누락 필드로 focus 이동(각 input에 ref 부여)
// 4) "입력하지 않으셨습니다" 알림은 커스텀 로직에서 처리

import React, { useMemo, useRef, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../style/signup.css";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || "http://localhost:8090",
  withCredentials: false,
});

export default function Signup() {
  const navigate = useNavigate();

  // ---------- 상태 ----------
  const [formData, setFormData] = useState({
    memberId: "",
    memberPw: "",
    memberPwCheck: "",
    memberName: "",
    memberBirth: "",
    memberPhone: "",
    memberAddress: "",
    memberSex: "",
    smsAgree: false,
  });

  const [idCheck, setIdCheck] = useState({
    loading: false,
    done: false,
    available: false,
    message: "",
  });

  const [pwState, setPwState] = useState({
    lengthOk: false,
    hasLetter: false,
    hasNumber: false,
    noSpace: true,
    ok: false,
    matched: false,
  });

  // ---------- refs: 첫 누락 필드 포커스를 위해 ----------
  const refs = {
    memberId: useRef(null),
    memberPw: useRef(null),
    memberPwCheck: useRef(null),
    memberName: useRef(null),
    memberBirth: useRef(null),
    memberPhone: useRef(null),
    memberAddress: useRef(null),
    memberSex: useRef(null), // 라디오 그룹 포커스 용
  };

  // ---------- 유틸 ----------
  const evaluatePassword = (password, passwordCheck) => {
    const lengthOk = password.length >= 8 && password.length <= 20;
    const hasLetter = /[A-Za-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const noSpace = !/\s/.test(password);
    const ok = lengthOk && hasLetter && hasNumber && noSpace;
    const matched = password.length > 0 && password === passwordCheck;
    return { lengthOk, hasLetter, hasNumber, noSpace, ok, matched };
  };

  const isEmailValid = useMemo(() => {
    const v = formData.memberId.trim();
    if (!v) return false;
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
  }, [formData.memberId]);

  // ---------- 입력 핸들러 ----------
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    if (name === "memberId") {
      setIdCheck({
        loading: false,
        done: false,
        available: false,
        message: "",
      });
    }

    // ✅ type별 값 결정: checkbox / radio / 그 외
    const nextValue =
      type === "checkbox" ? checked : type === "radio" ? value : value;

    setFormData((prev) => {
      const next = { ...prev, [name]: nextValue };

      // ✅ 비번/확인은 정책 재계산
      if (name === "memberPw" || name === "memberPwCheck") {
        const memberPw = name === "memberPw" ? next.memberPw : prev.memberPw;
        const memberPwCheck =
          name === "memberPwCheck" ? next.memberPwCheck : prev.memberPwCheck;
        setPwState(evaluatePassword(memberPw, memberPwCheck));
      }
      return next;
    });

    //핸드폰번호 숫자만
    if (name === "memberPhone") {
      // 숫자만 남기기
      const onlyNums = value.replace(/[^0-9]/g, "");
      setFormData((prev) => ({ ...prev, [name]: onlyNums }));
      return; // 아래 로직은 건너뜀
    }
  };

  // ---------- 중복 체크 ----------
  const handleCheckDuplicateId = async () => {
    const email = formData.memberId.trim();

    if (!email) {
      const msg = "아이디(이메일)를 입력하세요.";
      setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: msg,
      });
      alert(msg);
      refs.memberId.current?.focus();
      return;
    }
    if (!isEmailValid) {
      const msg = "아이디(이메일) 형식이 올바르지 않습니다.";
      setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: msg,
      });
      alert(msg);
      refs.memberId.current?.focus();
      return;
    }

    try {
      setIdCheck((p) => ({ ...p, loading: true }));
      const res = await api.get("/check-id", {
        params: { memberId: email },
        validateStatus: (s) => s === 200 || s === 409 || s === 400,
      });

      if (res.status === 200) {
        const msg = res.data?.message ?? "사용 가능한 아이디입니다.";
        setIdCheck({
          loading: false,
          done: true,
          available: true,
          message: msg,
        });
        alert(msg);
        return;
      }
      if (res.status === 409) {
        const msg = res.data?.message ?? "이미 사용 중인 아이디입니다.";
        setIdCheck({
          loading: false,
          done: true,
          available: false,
          message: msg,
        });
        alert(msg);
        refs.memberId.current?.focus();
        return;
      }
      if (res.status === 400) {
        const msg = res.data?.message ?? "아이디(이메일)를 입력하세요.";
        setIdCheck({
          loading: false,
          done: true,
          available: false,
          message: msg,
        });
        alert(msg);
        refs.memberId.current?.focus();
        return;
      }
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.message ||
        "아이디 확인 중 오류가 발생했습니다.";
      setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: msg,
      });
      alert(msg);
      refs.memberId.current?.focus();
    }
  };

  // ---------- 커스텀 필수값 검사 + 제출 ----------
  const handleSubmit = async (e) => {
    e.preventDefault(); // ✅ 우리는 커스텀 검증만 사용

    // 1) 필수 필드 목록과 라벨(경고문구용) 정의
    const required = [
      { name: "memberId", label: "아이디(이메일)" },
      { name: "memberPw", label: "비밀번호" },
      { name: "memberPwCheck", label: "비밀번호 확인" },
      { name: "memberName", label: "이름" },
      { name: "memberBirth", label: "생년월일" },
      { name: "memberSex", label: "성별" }, // 라디오
      { name: "memberPhone", label: "전화번호" },
      { name: "memberAddress", label: "주소" },
    ];

    // 2) 첫 번째 누락 항목을 찾아 알림 + 포커스
    for (const f of required) {
      // 성별은 라디오 특성상 빈 문자열이 유지될 수 있으니 별도 체크
      const value =
        f.name === "memberSex"
          ? formData.memberSex
          : String(formData[f.name] ?? "").trim();

      if (!value) {
        alert(`${f.label}을(를) 입력하지 않으셨습니다.`);
        // 해당 입력으로 포커스
        refs[f.name]?.current?.focus();
        return;
      }
    }

    // 3) 이메일 형식 검사(추가)
    if (!isEmailValid) {
      alert("아이디(이메일) 형식이 올바르지 않습니다.");
      refs.memberId.current?.focus();
      return;
    }

    // 4) 아이디 중복체크 선행
    if (!idCheck.done || !idCheck.available) {
      alert("아이디 중복체크를 먼저 완료해주세요.");
      refs.memberId.current?.focus();
      return;
    }

    // 5) 비밀번호 정책 + 일치
    if (!pwState.ok) {
      alert("비밀번호는 8~20자, 영문+숫자 조합이며 공백이 없어야 합니다.");
      refs.memberPw.current?.focus();
      return;
    }
    if (!pwState.matched) {
      alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      refs.memberPwCheck.current?.focus();
      return;
    }

    // 6) 제출
    try {
      await api.post("/signup", {
        memberId: formData.memberId.trim(),
        memberPw: formData.memberPw,
        memberName: formData.memberName,
        memberBirth: formData.memberBirth,
        memberPhone: formData.memberPhone,
        memberAddress: formData.memberAddress,
        smsAgree: formData.smsAgree,
        memberSex: formData.memberSex,
      });
      alert("회원가입이 완료되었습니다.");
      navigate("/login");
    } catch (err) {
      console.error(err);
      alert("회원가입 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="signup-container">
      <h3>회원가입</h3>

      {/* ✅ 브라우저 기본 검증 끔 */}
      <form noValidate onSubmit={handleSubmit}>
        <table className="table type2 responsive">
          <tbody>
            <tr>
              <th scope="row">아이디</th>
              <td>
                <span className="temp_form md">
                  <input
                    ref={refs.memberId} // ✅ 포커스 대상
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
                <div className="temp_form md">
                  <input
                    ref={refs.memberPw} // ✅ 포커스 대상
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
                <div className="temp_form md">
                  <input
                    ref={refs.memberPwCheck} // ✅ 포커스 대상
                    className="temp_input"
                    type="password"
                    name="memberPwCheck"
                    value={formData.memberPwCheck}
                    onChange={handleChange}
                  />
                </div>
                {formData.memberPwCheck && (
                  <div
                    className={`hint ${pwState.matched ? "ok" : "error"}`}
                    style={{ marginTop: 8 }}
                  >
                    {pwState.matched
                      ? "비밀번호가 일치합니다."
                      : "비밀번호가 일치하지 않습니다."}
                  </div>
                )}
              </td>
            </tr>

            <tr>
              <th scope="row">이름</th>
              <td>
                <div className="temp_form md">
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
                <div className="temp_form md">
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
                    ref={formData.memberSex === "" ? refs.memberSex : null} // 선택 전엔 그룹 포커스용
                  />
                  <label htmlFor="sex-man">남</label>
                </span>
                <span className="temp_form md" style={{ marginLeft: 12 }}>
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
              <td>
                <div className="temp_form md">
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
              </td>
            </tr>

            <tr>
              <th scope="row">주소</th>
              <td>
                <div className="temp_form md">
                  <input
                    ref={refs.memberAddress}
                    className="temp_input"
                    type="text"
                    name="memberAddress"
                    value={formData.memberAddress}
                    onChange={handleChange}
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
      </form>
    </div>
  );
}
