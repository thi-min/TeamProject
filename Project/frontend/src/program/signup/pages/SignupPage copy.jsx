/**
 * 목적: /join/signup 페이지
 * - 카카오 콜백 → /join → 약관동의 후 진입 시, 프리필 정보를 화면 인풋에 그대로 표출
 * - 비밀번호 행은 카카오 유입(via === "kakao")이면 숨김(.kakao-hidden)
 * - 제출 시 카카오 가입은 비밀번호 없이 진행(백엔드에서 null 처리)
 *
 * UI는 "현재 적용된 코드" 원형 유지, 로직/검증/제출은 "기존 로직" 이식
 */

import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { useLocation } from "react-router-dom";
import "../style/signup.css";

import api from "../../../common/api/axios";
import { apiCheckDuplicateId } from "../../member/services/memberApi";

// KakaoCallback/Join과 동일 세션 키
const KAKAO_PREFILL_KEY = "kakao_prefill_v1";
const KAKAO_FLOW_FLAG  = "kakao_flow";

/** 비밀번호 유효성 검사(기존 로직) */
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

/** 간단 이메일 형식 체크(기존 로직) */
const isEmail = (v) => /\S+@\S+\.\S+/.test(v || "");

/** 카카오 성별 → 프로젝트 Enum 변환 (MAN/WOMAN) */
const normalizeSexEnum = (v) => {
  if (!v) return "";
  const s = String(v).toUpperCase();
  if (s === "M" || s === "MALE") return "MAN";
  if (s === "F" || s === "FEMALE") return "WOMAN";
  return s; // 이미 MAN/WOMAN이면 그대로
};

/** +82 국제번호 → 국내 010 숫자만(전송용) */
const e164ToLocalDigits = (p) => {
  if (!p) return "";
  let digits = String(p).replace(/[^0-9]/g, "");
  if (digits.startsWith("82")) digits = "0" + digits.slice(2);
  return digits;
};

/** 화면 표시용 한국 전화번호 포맷(010-XXXX-XXXX 등) */
const formatPhoneKR = (raw) => {
  if (!raw) return "";
  let d = String(raw).replace(/\D/g, "");
  if (d.startsWith("82")) d = "0" + d.slice(2);
  if (d.startsWith("010") && d.length >= 11) {
    return `010-${d.slice(3, 7)}-${d.slice(7, 11)}`;
  }
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

  /** 포커스 이동용 refs */
  const refs = {
    memberId: useRef(null),
    memberPw: useRef(null),
    memberPwCheck: useRef(null),
    memberName: useRef(null),
    memberBirth: useRef(null),
    memberSex: useRef(null), // 첫 라디오에만 ref
    memberPhone: useRef(null),
  };


  const kakaoPrefill = useMemo(() => {
    const s =
      location.state && typeof location.state === "object"
        ? location.state
        : null;
    if (s?.via === "kakao" || s?.kakaoId) return s; // 명시적으로 카카오 플로우
    try {
      // ⬇️ 세션 플래그가 있을 때만 세션 프리필 사용 (일반 가입은 무시)
      if (sessionStorage.getItem(KAKAO_FLOW_FLAG) === "1") {
        const raw = sessionStorage.getItem(KAKAO_PREFILL_KEY);
        return raw ? JSON.parse(raw) : null;
      }
    } catch {}
    return null;
  }, [location.state]);

  // 일반 가입으로 들어온 경우, 남아있던 카카오 흔적 제거
useEffect(() => {
  const s =
    location.state && typeof location.state === "object"
      ? location.state
      : null;
  const isExplicitKakao = s?.via === "kakao" || s?.kakaoId;
  const kakaoFlow = sessionStorage.getItem(KAKAO_FLOW_FLAG) === "1";
  if (!isExplicitKakao && !kakaoFlow) {
    // 일반 가입: 세션 프리필/플래그 제거 via/kakaoId 초기화
    try {
      sessionStorage.removeItem(KAKAO_PREFILL_KEY);
      sessionStorage.removeItem(KAKAO_FLOW_FLAG);
    } catch {}
    setFormData((prev) => ({ ...prev, via: "", kakaoId: "" }));
  }
}, [location.state]);

  // =========================
  // 화면 상태 (컨트롤드 인풋)
  // =========================
  const [formData, setFormData] = useState({
    via: "", // "" | "kakao"
    kakaoId: "", // 카카오 고유 ID (서버 연동용)
    memberId: "", // 이메일(ID)
    memberPw: "",
    memberPwCheck: "",
    memberName: "",
    memberBirth: "", // "YYYY-MM-DD"
    memberSex: "", // "MAN" | "WOMAN" | ""
    memberPhone: "", // 화면 표시: 010-XXXX-XXXX
    smsAgree: false,
    postcode: "",
    roadAddress: "", // 화면 표시용
    detailAddress: "",
    memberAddress: "", // 서버 전송용(기본주소)
  });

const isKakao = formData.via === "kakao";

  // =========================
  // 카카오 프리필 → 화면에 표출 (전화번호는 하이픈 포맷)
  // =========================
  useEffect(() => {
    if (!kakaoPrefill) return;

    // 생년월일 가공
    const yyyy = kakaoPrefill.birthyear || "";
    const mmdd = kakaoPrefill.birthday || ""; // "MMDD"
    const mm = mmdd?.slice(0, 2) || "";
    const dd = mmdd?.slice(2, 4) || "";
    const isoBirth = yyyy && mm && dd ? `${yyyy}-${mm}-${dd}` : "";

    // 성별 매핑
    const sex = normalizeSexEnum(kakaoPrefill.gender);

    setFormData((prev) => ({
      ...prev,
      via: "kakao",
      kakaoId: kakaoPrefill.kakaoId || "",
      memberId: (kakaoPrefill.email || "").toLowerCase(), // 현재 규칙: 이메일 사용
      memberName: kakaoPrefill.name || "",
      memberBirth: isoBirth,
      memberSex: sex,
      memberPhone: formatPhoneKR(kakaoPrefill.phoneNumber) || "", // 화면 포맷: 010-XXXX-XXXX
      // 주소는 팝업에서 선택
    }));

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
    lastCheckedId: "",
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
  const normalizeDate = useCallback(
    (d) => (d ? String(d).slice(0, 10) : ""),
    []
  );

  // =========================
  // 이벤트 핸들러
  // =========================
  const handleChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;

    if (name === "memberPhone") {
      // 화면 입력 시에도 하이픈 포맷 유지(표시는 포맷, 전송 시 숫자만)
      const display = formatPhoneKR(value);
      setFormData((prev) => ({ ...prev, memberPhone: display }));
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
          lastCheckedId: "",
        });
      }
      return next;
    });
  }, []);

  // =========================
  // 아이디 중복체크(기존 로직 이식)
  // =========================
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
          (available
            ? "사용 가능한 아이디입니다."
            : "이미 사용 중인 아이디입니다."),
        lastCheckedId: email,
      });
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

  /** 카카오(다음) 우편번호 스크립트 1회 로드(현재 코드 유지) */
  const loadDaumPostcodeScript = useCallback(() => {
    return new Promise((resolve, reject) => {
      if (window.daum && window.daum.Postcode) return resolve();

      const existing = document.querySelector(
        'script[data-daum-postcode="true"]'
      );
      if (existing) {
        existing.addEventListener("load", () => resolve());
        existing.addEventListener("error", (e) => reject(e));
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

  /** 주소검색 팝업(현재 코드 유지 서버전송용 memberAddress 설정) */
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
            roadAddress: roadAddr, // 화면표시용
            memberAddress: baseAddress, // 서버전송용(기본주소)
          }));
        },
      }).open();
    } catch (e) {
      console.error(e);
      alert("주소 검색 스크립트 로딩에 실패했습니다.");
    }
  }, [loadDaumPostcodeScript]);

  // =========================
  // 제출(기존 로직 이식)
  // =========================
  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();

      const kakaoFlow = formData.via === "kakao";

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

      // 비밀번호 검증: 카카오는 스킵, 일반 가입만 검사
      if (!kakaoFlow) {
        if (!pwState.valid) {
          alert(pwState.issues[0]);
          refs.memberPw.current?.focus();
          return;
        }
      }

      // 전화번호: 전송 시 숫자만
      const phoneDigits = onlyDigits(e164ToLocalDigits(formData.memberPhone));
      if (phoneDigits.length < 10 || phoneDigits.length > 11) {
        alert("전화번호는 10~11자리 숫자만 입력해주세요.");
        refs.memberPhone.current?.focus();
        return;
      }

      // 주소 합본(기본+상세, 길이 방어)
      const safeAddress = `${formData.memberAddress || ""} ${
        formData.detailAddress || ""
      }`
        .trim()
        .slice(0, 100);

      // 서버 전송 payload (카카오 연동 키 포함)
      const payload = {
        memberId: normalizedId, // 로그인 ID(이메일)
        memberPw: kakaoFlow ? null : (formData.memberPw || "").trim(),
        memberName: (formData.memberName || "").trim(),
        memberBirth: normalizeDate(formData.memberBirth), // yyyy-MM-dd
        memberPhone: phoneDigits, // 숫자만
        memberAddress: safeAddress,
        smsAgree: !!formData.smsAgree,
        memberSex: formData.memberSex, // "MAN" | "WOMAN"
        kakaoId: formData.kakaoId || null, // ⬅ 중요: 연동키
        via: formData.via || "", // 서버 분기 참고용
      };

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

  // =========================
  // 렌더 (현재 UI 유지)
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

              {/* 전화번호 문자 수신동의 */}
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
