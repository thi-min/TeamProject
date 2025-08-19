// 목적: 아이디 + 이름 + 연락처로 "비밀번호 찾기(본인확인)"
// 동작:
//  1) 성공(2xx): 서버가 문자열 응답 → alert(문구) → 비밀번호 변경 페이지로 이동(/update-password)
//                이 때 memberId를 location.state로 넘겨 재설정 화면에서 활용
//  2) 실패(4xx/5xx): 서버 에러 메시지를 alert로 표시하고 현재 페이지 유지
//
// 주의:
//  - API 호출은 공용 axios 인스턴스(api) 기반의 apiFindMemberPw() 사용
//  - 휴대폰 입력은 숫자만 허용
//  - CSS 제외(기존 클래스명 유지)

import { useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFindMemberPw } from "../services/memberApi";

import BackButton from "../../../common/BackButton";

function onlyDigits(v = "") {
  return (v + "").replace(/\D/g, "");
}

function validate({ memberId, memberName, memberPhone }) {
  const errors = {};
  if (!memberId?.trim()) errors.memberId = "아이디(이메일)를 입력하세요.";
  if (!memberName?.trim()) errors.memberName = "이름을 입력하세요.";
  const phone = onlyDigits(memberPhone);
  if (!phone) errors.memberPhone = "휴대폰 번호를 입력하세요.";
  else if (phone.length < 10 || phone.length > 11)
    errors.memberPhone = "휴대폰 번호 형식이 올바르지 않습니다.";
  return errors;
}

export default function FindPasswordPage() {
  const navigate = useNavigate();

  // ⬇️ 컨트롤드 폼 상태
  const [form, setForm] = useState({
    memberId: "",
    memberName: "",
    memberPhone: "",
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // ⬇️ 공통 onChange
  const handleChange = useCallback((e) => {
    const { name, value } = e.target;

    if (name === "memberPhone") {
      // 연락처는 숫자만
      setForm((prev) => ({ ...prev, memberPhone: onlyDigits(value) }));
      return;
    }
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  // ⬇️ 제출
  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();

      const v = validate(form);
      setErrors(v);
      if (Object.keys(v).length > 0) return;

      try {
        setLoading(true);

        // ✅ 본인확인 요청 (성공 시 서버가 안내 문자열 반환)
        const text = await apiFindMemberPw({
          memberId: form.memberId,
          memberName: form.memberName,
          memberPhone: form.memberPhone,
        });

        // ✅ 성공: 안내 → 비밀번호 변경 화면으로 이동
        //    - update-password 라우트로 이동하며 memberId를 함께 전달
        window.alert(
          text || "본인 확인이 완료되었습니다. 비밀번호를 재설정 해주세요."
        );
        navigate("/update-password", {
          replace: true,
          state: { memberId: form.memberId }, // 재설정 화면에서 사용
        });
      } catch (err) {
        // ❌ 실패: 메시지 표시 후 현재 페이지 유지
        const msg =
          (err && err.message) || "비밀번호 찾기 중 오류가 발생했습니다.";
        window.alert(msg);
      } finally {
        setLoading(false);
      }
    },
    [form, navigate]
  );

  const isDisabled = useMemo(
    () =>
      loading ||
      !form.memberId.trim() ||
      !form.memberName.trim() ||
      !form.memberPhone.trim(),
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
                <div className="from_text">아이디(이메일)</div>
                <input
                  id="memberId"
                  name="memberId"
                  type="email"
                  className="form_input"
                  value={form.memberId}
                  onChange={handleChange}
                  placeholder="example@domain.com"
                  autoComplete="email"
                />
                {errors.memberId && <small>{errors.memberId}</small>}
              </div>
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
                    {loading ? "확인 중..." : "본인 확인"}
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
