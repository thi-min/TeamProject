// 목적: 이름 + 연락처로 아이디 찾기
// 동작 요구사항:
//  1) 서버 응답이 성공(2xx) → alert("회원님의 ID는 OOOO입니다.") 띄운 뒤 로그인 페이지로 이동
//  2) 서버 응답이 실패(4xx/5xx) → 서버에서 내려온 에러 메시지를 alert로 띄우고, 현재 페이지에 그대로 남기기
//
// 주의:
//  - apiFindMemberId()는 서버에서 2xx면 문자열을 반환하고, 4xx/5xx면 Error를 throw하도록 작성되어 있음.
//  - 따라서 "성공 시 = navigate('/login')" / "실패 시 = alert 후 페이지 유지" 로 분기하면 됨.

import { useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFindMemberId } from "../services/memberApi";

import BackButton from "../../../common/BackButton";

// 숫자만 남기는 유틸(휴대폰 입력 시 사용)
function onlyDigits(v = "") {
  return (v + "").replace(/\D/g, "");
}

// 간단한 유효성 검사
function validate({ memberName, memberPhone }) {
  const errors = {};
  if (!memberName?.trim()) errors.memberName = "이름을 입력하세요.";
  const phone = onlyDigits(memberPhone);
  if (!phone) errors.memberPhone = "휴대폰 번호를 입력하세요.";
  else if (phone.length < 10 || phone.length > 11)
    errors.memberPhone = "휴대폰 번호 형식이 올바르지 않습니다.";
  return errors;
}

export default function FindIdPage() {
  const navigate = useNavigate();

  // ⬇️ 폼 상태 (컨트롤드 인풋)
  const [form, setForm] = useState({
    memberName: "",
    memberPhone: "",
  });

  // ⬇️ 에러/로딩/메시지 상태 (화면 표시용 — 알림은 window.alert 사용)
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // ⬇️ 공통 onChange 핸들러
  const handleChange = useCallback((e) => {
    const { name, value } = e.target;

    if (name === "memberPhone") {
      // 연락처는 숫자만 허용
      setForm((prev) => ({ ...prev, memberPhone: onlyDigits(value) }));
      return;
    }
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  // ⬇️ 제출 시 동작
  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();

      // 1) 프론트 유효성 검사
      const v = validate(form);
      setErrors(v);
      if (Object.keys(v).length > 0) return;

      try {
        setLoading(true);

        // 2) 서버 요청
        //    - 성공 시: 서버가 "회원님의 ID는 OOOO 입니다." 같은 문자열을 반환
        //    - 실패 시: apiFindMemberId 내부에서 Error throw
        const text = await apiFindMemberId({
          memberName: form.memberName,
          memberPhone: form.memberPhone,
        });

        // 3) 성공 → 알림 → 로그인 페이지로 이동
        window.alert(text); // 예: "회원님의 ID는 OOOO 입니다."
        navigate("/login", { replace: true });
      } catch (err) {
        // 4) 실패 → 서버 에러 메시지를 알림으로 표시하고 페이지는 유지
        const msg =
          (err && err.message) || "아이디 찾기 중 오류가 발생했습니다.";
        window.alert(msg);
        // 페이지 유지: navigate 호출 안 함
      } finally {
        setLoading(false);
      }
    },
    [form, navigate]
  );

  // 버튼 비활성화 조건
  const isDisabled = useMemo(
    () => loading || !form.memberName.trim() || !form.memberPhone.trim(),
    [loading, form]
  );
  return (
    <form onSubmit={handleSubmit} noValidate>
      <div className="form_top_box">
        <div className="form_top_item">
          <i className="form_icon type1"></i>
          <div className="form_title">아이디 찾기</div>
          <div className="form_desc">
            <p>이름과, 핸드폰번호를 입력해주세요.</p>
          </div>
        </div>
      </div>
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="float_box clearfix">
            <div className="form_item_box">
              <div className="input_item">
                <div className="from_text">이름</div>
                <input
                  id="memberName"
                  name="memberName"
                  type="text"
                  className="form_input"
                  value={form.memberName}
                  onChange={handleChange}
                  placeholder="이름을 입력하세요"
                  autoComplete="name"
                />
                {errors.memberName && <small>{errors.memberName}</small>}
              </div>
              <div className="input_item">
                <div className="from_text">휴대폰 번호</div>
                <input
                  id="memberPhone"
                  name="memberPhone"
                  type="tel"
                  className="form_input"
                  value={form.memberPhone}
                  onChange={handleChange}
                  placeholder="숫자만 입력"
                  autoComplete="tel"
                  inputMode="numeric"
                />
                {errors.memberPhone && <small>{errors.memberPhone}</small>}
              </div>
              <div className="form_center_box">
                <div className="temp_btn white md">
                  <BackButton label="이전" className="btn white" />
                </div>
                <div className="temp_btn md ">
                  <button type="submit" className="btn" disabled={isDisabled}>
                    {loading ? "조회 중..." : "아이디 찾기"}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </form>
  );
}
