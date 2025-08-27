/**
 * 목적: /join/signup 페이지
 * - 카카오 콜백 → /join → 약관동의 후 진입 시, 프리필 정보를 화면 인풋에 그대로 표출
 * - 비밀번호 행은 카카오 유입(via === "kakao")이면 숨김(.kakao-hidden)
 * - 제출 시 카카오 가입은 비밀번호 없이 진행(백엔드에서 null 처리)
 *
 * 입력 양식: 당신이 준 테이블 기반 마크업을 그대로 사용
 *  - 숨김 필드: via, kakaoId
 *  - 아이디(=email type), 비밀번호/확인, 이름, 생년월일(date), 성별(라디오), 전화번호(+수신동의),
 *    주소(우편번호/도로명/상세)
 *
 * 연결 포인트:
 *  - 아이디 중복체크: handleCheckDuplicateId() 내부 TODO 지점에 기존 API 연결
 *  - 주소검색 팝업: openPostcodePopup() 내부 TODO 지점에 기존 함수 연결
 *  - 최종 회원가입 제출: handleSubmit() 내부 TODO 지점에 기존 API 연결
 */

import React, { useEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import "../style/signup.css";
// KakaoCallback/Join과 동일 세션 키
const KAKAO_PREFILL_KEY = "kakao_prefill_v1";

const formatPhoneKR = (raw) => {
  if (!raw) return "";
  let d = String(raw).replace(/\D/g, ""); // 숫자만
  if (d.startsWith("82")) d = "0" + d.slice(2); // +82 → 0
  // 010 휴대폰 기본 11자리
  if (d.startsWith("010") && d.length >= 11) {
    return `010-${d.slice(3, 7)}-${d.slice(7, 11)}`;
  }
  // 011/016/017/018/019 등 10~11자리 대응
  if (/^01[1-9]/.test(d)) {
    if (d.length === 10)
      return `${d.slice(0, 3)}-${d.slice(3, 6)}-${d.slice(6, 10)}`;
    if (d.length >= 11)
      return `${d.slice(0, 3)}-${d.slice(3, 7)}-${d.slice(7, 11)}`;
  }
  return d;
};

export default function SignupPage() {
  const location = useLocation();

  // =========================
  // Refs (포커스/검증 시 사용)
  // =========================
  const refs = {
    memberId: useRef(null),
    memberPw: useRef(null),
    memberPwCheck: useRef(null),
    memberName: useRef(null),
    memberBirth: useRef(null),
    memberSex: useRef(null), // 첫 라디오에만 ref
    memberPhone: useRef(null),
  };

  // =========================
  // 프리필 로딩 (state > session)
  // =========================
  const kakaoPrefill = useMemo(() => {
    const s =
      location.state && typeof location.state === "object"
        ? location.state
        : null;
    if (s?.via === "kakao" || s?.kakaoId) return s;
    try {
      const raw = sessionStorage.getItem(KAKAO_PREFILL_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }, [location.state]);

  // =========================
  // 화면 상태 (컨트롤드 인풋)
  // =========================
  const [formData, setFormData] = useState({
    via: "", // "" | "kakao"
    kakaoId: "", // 카카오 고유 ID(=memberId로 사용)
    memberId: "", // 이메일(ID)
    memberPw: "",
    memberPwCheck: "",
    memberName: "",
    memberBirth: "", // "YYYY-MM-DD"
    memberSex: "", // "MAN" | "WOMAN" | ""
    memberPhone: "",
    smsAgree: false,
    postcode: "",
    roadAddress: "",
    detailAddress: "",
  });

  const isKakao = formData.via === "kakao" || !!formData.kakaoId;

  // =========================
  // 카카오 프리필 → 화면에 표출
  // =========================
  useEffect(() => {
    if (!kakaoPrefill) return;

    // 생년월일 가공
    const yyyy = kakaoPrefill.birthyear || "";
    const mmdd = kakaoPrefill.birthday || ""; // "MMDD"
    const mm = mmdd?.slice(0, 2) || "";
    const dd = mmdd?.slice(2, 4) || "";
    const isoBirth = yyyy && mm && dd ? `${yyyy}-${mm}-${dd}` : "";

    // 성별 매핑: male/female -> MAN/WOMAN
    const sex =
      kakaoPrefill.gender === "male"
        ? "MAN"
        : kakaoPrefill.gender === "female"
        ? "WOMAN"
        : "";

    setFormData((prev) => ({
      ...prev,
      via: "kakao",
      memberId: kakaoPrefill.email || "", // 규칙: kakaoId = email
      memberName: kakaoPrefill.name || "", // nickname X, name O
      memberBirth: isoBirth, // YYYY-MM-DD
      memberSex: sex,
      memberPhone: formatPhoneKR(kakaoPrefill.phoneNumber) || "",
      // 나머지(주소 등)는 그대로 유지
    }));

    // 새로고침에도 유지
    try {
      sessionStorage.setItem(KAKAO_PREFILL_KEY, JSON.stringify(kakaoPrefill));
    } catch {}
  }, [kakaoPrefill]);

  // =========================
  // 유틸/상태
  // =========================
  const [idCheck, setIdCheck] = useState({
    loading: false,
    done: false,
    available: false,
    message: "",
  });

  const pwState = useMemo(() => {
    const same =
      formData.memberPw && formData.memberPw === formData.memberPwCheck;
    return { same };
  }, [formData.memberPw, formData.memberPwCheck]);

  // =========================
  // 이벤트 핸들러
  // =========================
  const handleChange = (e) => {
    const { name, type } = e.target;
    const value = type === "checkbox" ? e.target.checked : e.target.value;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCheckDuplicateId = async () => {
    // ✅ TODO: 여기서 "기존 아이디 중복체크 API"를 호출하세요.
    //   - 예: const { available, message } = await api.checkId(formData.memberId)
    // 지금은 더미 로직(이메일 형식이면 사용가능)으로만 처리해 둡니다.
    try {
      setIdCheck({ loading: true, done: false, available: false, message: "" });
      const ok = /\S+@\S+\.\S+/.test(formData.memberId);
      await new Promise((r) => setTimeout(r, 400));
      setIdCheck({
        loading: false,
        done: true,
        available: ok,
        message: ok
          ? "사용 가능한 아이디입니다."
          : "아이디 형식을 확인해주세요.",
      });
      if (!ok && refs.memberId.current) refs.memberId.current.focus();
    } catch (e) {
      setIdCheck({
        loading: false,
        done: true,
        available: false,
        message: "중복 확인 중 오류가 발생했습니다.",
      });
    }
  };

  const openPostcodePopup = () => {
    // ✅ TODO: 기존 주소검색(다음 우편번호 등) 팝업 연동
    // 연동되어 있다면 해당 함수 호출만 남기세요.
    // 아래는 임시 안내:
    alert("주소검색 팝업을 기존 구현에 연결하세요.");
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // 카카오 가입은 비밀번호 없이 진행 → 서버에서 null/무시 처리
    const payload = {
      via: formData.via || "",
      kakaoId: formData.kakaoId || null,

      memberId: formData.memberId.trim(),
      memberPw: isKakao ? null : formData.memberPw || null,
      memberPwCheck: isKakao ? null : formData.memberPwCheck || null,

      memberName: formData.memberName.trim(),
      memberBirth: formData.memberBirth || null, // "YYYY-MM-DD"
      memberSex: formData.memberSex || null, // "MAN"/"WOMAN"

      memberPhone: formData.memberPhone || null,
      smsAgree: !!formData.smsAgree,

      postcode: formData.postcode || null,
      roadAddress: formData.roadAddress || null,
      detailAddress: formData.detailAddress || null,

      // (선택) 서버가 받는다면 SNS 플래그도 함께
      snsYn: isKakao ? true : false,
      snsType: isKakao ? "KAKAO" : null,
    };

    // ✅ TODO: 기존 "회원가입 API" 호출 연결
    //  - 예: return signUp(payload).then(...).catch(...)
    console.log("[Signup] submit payload:", payload);
    alert("제출 로직은 기존 API에 연결하세요. (콘솔에 payload 확인 가능)");
  };

  // =========================
  // 렌더
  // =========================
  return (
    <div className="signup-container">
      {/* 숨김 필드 (서버/디버깅용) */}
      <form noValidate onSubmit={handleSubmit}>
        <input type="hidden" name="via" value={formData.via || ""} />
        <input type="hidden" name="kakaoId" value={formData.kakaoId || ""} />

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
                      readOnly={isKakao} // 카카오 유입 시 수정 잠금(원하면 제거)
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
                      placeholder={
                        isKakao
                          ? "카카오 가입은 비밀번호 없이 진행됩니다(선택 입력)."
                          : ""
                      }
                      autoComplete="new-password"
                    />
                  </div>
                  <span className="form_winning">
                    비밀번호는 8~20자 영문, 숫자로 구성되어있어야 합니다.
                  </span>
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
                      placeholder={
                        isKakao
                          ? "카카오 가입은 비밀번호 확인이 필요하지 않습니다."
                          : ""
                      }
                      autoComplete="new-password"
                    />
                  </div>
                  {formData.memberPwCheck && !isKakao && (
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

              {/* 생년월일 (date) */}
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

              {/* 성별 (라디오) */}
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
                      readOnly // 카카오 유입이면 서버 값 그대로 사용(원하면 편집 허용)
                    />
                  </div>
                  <span className="temp_form">
                    <input
                      type="checkbox"
                      id="smsYn"
                      name="smsAgree"
                      className="temp_check"
                      checked={!!formData.smsAgree}
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
                      <button
                        type="button"
                        className="btn"
                        onClick={openPostcodePopup}
                      >
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
