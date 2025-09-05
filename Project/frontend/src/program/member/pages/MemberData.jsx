// MyPage.jsx
// 목적: 인증 사용자 마이페이지 조회 + 주소 수정(우편번호/도로명 readonly, 상세주소만 수정)
// 특징:
//  - 첫 렌더 시 /member/mypage/memberdata 호출
//  - 401 → 로그인, 403 → 비밀번호 변경으로 안내(로딩 해제 후 네비)
//  - DB의 memberAddress를 파싱하여 postcode/roadAddress/detailAddress에 주입
//  - 주소검색(카카오)로 우편번호/도로명 갱신, "저장" 클릭 시 PUT 호출

import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  apiGetMyPage,
  apiUpdateMyAddress,
  apiUpdateMyPhone,
  apiUpdateSmsAgree,
} from "../services/memberApi";
import "../style/member.css";

import BackButton from "../../../common/BackButton";

// ──────────────────────────────────────
// 주소 유틸: 파싱/조합
// 예: "[13543] 경기 성남시 분당구 대왕판교로 372 123동 456호"
const parseAddress = (addr) => {
  if (!addr) return { postcode: "", roadAddress: "", detailAddress: "" };

  // 1) 우편번호 추출
  let postcode = "";
  let rest = addr.trim();
  const pm = /^\[(\d{5})\]\s*(.*)$/.exec(rest);
  if (pm) {
    postcode = pm[1];
    rest = pm[2].trim();
  }

  // 2) 기본/상세 분리: "기본(숫자로 끝)" + " 공백 " + "상세(숫자 없음) "
  //   예) "... 1239 공중화장실" → 기본: "... 1239", 상세: "공중화장실"
  const m = /^(.*\d)\s+([^\d]+)$/.exec(rest);
  if (m) {
    return { postcode, roadAddress: m[1].trim(), detailAddress: m[2].trim() };
  }
  // 상세가 없거나 숫자가 포함되면 전부 기본주소로
  return { postcode, roadAddress: rest, detailAddress: "" };
};

const composeAddress = ({ postcode, roadAddress, detailAddress }) => {
  const p = (postcode || "").trim();
  const r = (roadAddress || "").trim();
  const d = (detailAddress || "").trim();
  return `${p ? `[${p}] ` : ""}${r}${d ? ` ${d}` : ""}`.trim();
};

// 카카오 주소 스크립트 로더
const loadDaumPostcodeScript = () =>
  new Promise((resolve, reject) => {
    if (window.daum?.Postcode) return resolve();
    const existing = document.querySelector(
      'script[data-daum-postcode="true"]'
    );
    if (existing) {
      existing.addEventListener("load", () => resolve());
      existing.addEventListener("error", reject);
      return;
    }
    const s = document.createElement("script");
    s.src =
      "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    s.async = true;
    s.setAttribute("data-daum-postcode", "true");
    s.onload = () => resolve();
    s.onerror = () => reject(new Error("Daum Postcode script load failed"));
    document.head.appendChild(s);
  });

export default function MyPage() {
  const navigate = useNavigate();
  const location = useLocation();

  // 원본 DTO + 상태
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [phone, setPhone] = useState("");
  const [smsAgree, setSmsAgree] = useState(false);

  // 주소 폼 상태
  const [formData, setFormData] = useState({
    postcode: "",
    roadAddress: "",
    detailAddress: "",
  });

  // 성별 표시
  const sexLabel = useMemo(() => {
    if (!data?.memberSex) return "-";
    if (data.memberSex === "MALE" || data.memberSex === "MAN") return "남성";
    if (data.memberSex === "FEMALE" || data.memberSex === "WOMAN")
      return "여성";
    return String(data.memberSex);
  }, [data]);

  // ✅ 휴대폰: 초기값(숫자만) 백업용 ref + dirty 플래그
  const initialPhoneDigitsRef = React.useRef(""); // 최초 로드 시의 "숫자만" 저장
  const [dirtyPhone, setDirtyPhone] = useState(false);

  // 최초 로드
  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      setError("");
      try {
        const res = await apiGetMyPage();
        if (cancelled) return;
        setData(res.data);
        setSmsAgree(!!res.data.smsAgree);

        // ✅ 휴대폰 초기 세팅
        const displayPhone = res.data.memberPhone || ""; // 화면용(하이픈 포함)
        const initialDigits = displayPhone.replace(/[^0-9]/g, ""); // 숫자만
        setPhone(displayPhone);
        initialPhoneDigitsRef.current = initialDigits; // ✅ 백업
        setDirtyPhone(false);
        const legacy = parseAddress(res.data?.memberAddress || "");
        setFormData({
          postcode: res.data.memberPostcode || legacy.postcode,
          roadAddress: res.data.memberRoadAddress || legacy.roadAddress,
          detailAddress: res.data.memberDetailAddress || legacy.detailAddress,
        });
      } catch (err) {
        if (cancelled) return;
        const status = err?.response?.status;
        const msg =
          err?.response?.data?.message ||
          err?.response?.data ||
          err?.message ||
          "마이페이지 조회 중 오류가 발생했습니다.";

        if (status === 401) {
          setLoading(false);
          alert("로그인이 필요합니다.");
          navigate("/login", { replace: true, state: { from: location } });
          return;
        }
        if (status === 403) {
          setLoading(false);
          alert("비밀번호가 만료되었습니다. 비밀번호를 변경해 주세요.");
          navigate("/update-password", { replace: true });
          return;
        }
        setError(String(msg));
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [navigate, location]);
  // 주소검색 버튼
  const openPostcodePopup = async () => {
    try {
      await loadDaumPostcodeScript();
      new window.daum.Postcode({
        oncomplete: (addr) => {
          const zonecode = addr.zonecode || "";
          const roadAddr =
            (addr.roadAddress || "").trim() || (addr.jibunAddress || "").trim();
          setFormData((prev) => ({
            ...prev,
            postcode: zonecode,
            roadAddress: roadAddr,
          }));
        },
      }).open();
    } catch (e) {
      alert("주소 검색 스크립트 로딩에 실패했습니다.");
    }
  };

  // 상세주소 입력
  const onChangeDetail = (e) => {
    setFormData((prev) => ({ ...prev, detailAddress: e.target.value }));
  };

  // 휴대폰 입력(숫자만 유지 → 표시용 하이픈)
  const formatPhoneDisplay = (digits) => {
    if (digits.length === 11)
      return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
    if (digits.length === 10) {
      if (digits.startsWith("02"))
        return `02-${digits.slice(2, 6)}-${digits.slice(6)}`;
      return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(6)}`;
    }
    return digits;
  };

  const [newPhone, setNewPhone] = useState("");

  const onChangePhone = (e) => {
    const digits = (e.target.value || "").replace(/[^0-9]/g, "").slice(0, 11);
    setPhone(formatPhoneDisplay(digits));
    setDirtyPhone(true); // ✅ 사용자가 손댐
  };

  // ✅ "수정" 버튼: 주소 + 휴대폰 + SMS 동의 변경을 한 번에 저장
  const onSubmit = async (e) => {
    e.preventDefault();
    if (!data) return;

    // ── 1) 변경 여부 판정 ───────────────────────────────
    const norm = (s) => (s ?? "").trim();

    const addrChanged =
      norm(formData.postcode) !== norm(data.memberPostcode) ||
      norm(formData.roadAddress) !== norm(data.memberRoadAddress) ||
      norm(formData.detailAddress) !== norm(data.memberDetailAddress);

    const newDigits = (phone || "").replace(/[^0-9]/g, ""); // 현재 입력(숫자만)
    const oldDigits = initialPhoneDigitsRef.current; // 최초 로드 값(숫자만)
    const phoneChanged = dirtyPhone && newDigits !== oldDigits; // 사용자가 수정했고 값이 달라야 변경으로 인정

    const smsChanged = smsAgree !== !!data.smsAgree;

    if (!addrChanged && !phoneChanged && !smsChanged) {
      alert("변경된 내용이 없습니다.");
      return;
    }

    // ── 2) 유효성 검증 ───────────────────────────────────
    if (addrChanged && !formData.roadAddress.trim()) {
      alert("기본주소(도로명)를 먼저 선택해 주세요.");
      return;
    }
    if (phoneChanged && (newDigits.length < 10 || newDigits.length > 11)) {
      alert("휴대전화 번호 형식이 올바르지 않습니다.");
      return;
    }

    // ── 3) 저장(변경된 것만) + 마지막에 한 번만 재조회 ──
    try {
      setSaving(true);

      if (addrChanged) {
        await apiUpdateMyAddress({
          postcode: formData.postcode,
          roadAddress: formData.roadAddress,
          detailAddress: formData.detailAddress,
        });
      }

      if (phoneChanged) {
        // 서버에서 숫자만 추출/검증/저장(NO-OP/중복은 서버에서 처리)
        await apiUpdateMyPhone(phone);
      }

      if (smsChanged) {
        await apiUpdateSmsAgree(smsAgree);
      }

      // ── 4) 최신 데이터 재조회 → 화면 상태 동기화 ────────
      const refreshed = await apiGetMyPage();
      const d = refreshed.data;

      setData(d);
      setFormData({
        postcode: d.memberPostcode || "",
        roadAddress: d.memberRoadAddress || "",
        detailAddress: d.memberDetailAddress || "",
      });

      const dispPhone = d.memberPhone || "";
      setPhone(dispPhone);
      initialPhoneDigitsRef.current = dispPhone.replace(/[^0-9]/g, ""); // ✅ 기준값 갱신
      setDirtyPhone(false);

      setSmsAgree(!!d.smsAgree);

      alert("수정이 완료되었습니다.");
    } catch (err) {
      const status = err?.response?.status;
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err?.message ||
        "수정 중 오류가 발생했습니다.";

      // 휴대폰 중복 등 서버에서 409로 내려줄 때
      if (status === 409) {
        alert("이미 등록된 휴대전화 번호입니다.");
      } else {
        alert(msg);
      }
    } finally {
      setSaving(false);
    }
  };

  // 로딩/에러 처리
  if (loading) {
    return (
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="form_item_box">
            <div className="form_name">마이페이지를 불러오는 중입니다...</div>
          </div>
        </div>
      </div>
    );
  }
  if (error) {
    return (
      <div className="form_item type2">
        <div className="form_login_wrap">
          <div className="form_item_box">
            <div className="form_error" role="alert" aria-live="assertive">
              {error}
            </div>
            <div style={{ marginTop: 12 }}>
              <button
                type="button"
                className="btn"
                onClick={() => navigate(-1)}
              >
                이전
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }
  if (!data) return null;

  // 본문
  return (
    <form noValidate onSubmit={onSubmit}>
      <div className="form_item type2 member_wrap">
        <div className="form_top_box">
          <div className="form_top_item">
            <i className="form_icon type1"></i>
            <div className="form_title">마이페이지</div>
            <div className="form_desc">
              <p>회원님의 기본 정보를 확인할 수 있습니다.</p>
            </div>
          </div>
        </div>

        <div className="input_form_box">
          <div className="length_box clearfix">
            <div className="form_title">회원 정보</div>
            <div className="form_cnt">
              <div className="item_sec">
                <div className="form_name">이름</div>
                <div className="temp_form md">
                  <input
                    type="text"
                    value={data.memberName ?? ""}
                    className="temp_input"
                    readOnly
                  />
                </div>
              </div>

              <div className="item_sec">
                <div className="form_name">아이디(이메일)</div>
                <div className="temp_form md">
                  <input
                    type="text"
                    value={data.memberId ?? ""}
                    className="temp_input"
                    readOnly
                  />
                </div>
              </div>

              <div className="item_sec">
                <div className="form_name">생년월일</div>
                <div className="temp_form md w40p">
                  <input
                    type="date"
                    value={data.memberBirth || ""}
                    className="temp_input"
                    readOnly
                  />
                </div>
              </div>

              <div className="item_sec">
                <div className="form_name">성별</div>
                <div className="temp_form md">
                  <div className="temp_input">{sexLabel}</div>
                </div>
              </div>

              {/* 주소 수정 블록 */}
              <div className="item_sec">
                <div className="form_name">주소</div>
                <div className="address_form temp_form md">
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

                  <div className="temp_form lg">
                    <input
                      className="temp_input"
                      type="text"
                      name="roadAddress"
                      value={formData.roadAddress || ""}
                      readOnly
                      placeholder="기본주소(도로명)"
                    />
                  </div>

                  <div className="temp_form lg">
                    <input
                      className="temp_input"
                      type="text"
                      name="detailAddress"
                      value={formData.detailAddress || ""}
                      onChange={onChangeDetail}
                      placeholder="상세주소"
                    />
                  </div>
                </div>
              </div>

              <div className="item_sec">
                <div className="form_name">휴대전화</div>
                <div className="form_flex">
                  <div className="temp_form md w50p">
                    <input
                      type="tel"
                      value={phone}
                      className="temp_input"
                      onChange={onChangePhone}
                      placeholder="예: 01012345678"
                    />
                  </div>
                  {/* <div className="temp_btn md">
                    <button type="button" className="btn">
                      휴대폰 인증
                    </button>
                  </div> */}
                </div>
              </div>

              <div className="item_sec">
                <div className="form_name">카카오 ID</div>
                <div className="temp_form md">
                  <input
                    type="text"
                    value={data.kakaoId ?? ""}
                    className="temp_input"
                    readOnly
                  />
                </div>
              </div>
              <div className="item_sec">
                <div className="form_name">SMS 수신동의</div>
                <div className="temp_form md">
                  <input
                    type="checkbox"
                    id="smsYn"
                    className="temp_check"
                    checked={smsAgree}
                    onChange={(e) => setSmsAgree(e.target.checked)}
                  />
                  <label htmlFor="smsYn">수신동의</label>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="form_center_box">
          <div className="temp_btn white md">
            <BackButton label="이전" className="btn white" />
          </div>
          <div className="temp_btn md">
            <button type="submit" className="btn" disabled={saving}>
              수정
            </button>
          </div>
        </div>
      </div>
    </form>
  );
}
