// MyPage.jsx
// 목적: 인증 사용자 마이페이지 조회 + 주소 수정(우편번호/도로명 readonly, 상세주소만 수정)
// 특징:
//  - 첫 렌더 시 /member/mypage 호출
//  - 401 → 로그인, 403 → 비밀번호 변경으로 안내(로딩 해제 후 네비)
//  - DB의 memberAddress를 파싱하여 postcode/roadAddress/detailAddress에 주입
//  - 주소검색(카카오)로 우편번호/도로명 갱신, "저장" 클릭 시 PUT 호출

import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { apiGetMyPage, apiUpdateMyAddress } from "../services/memberApi";
import "../style/member.css";

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

  //   // 저장 버튼
  //   const onSaveAddress = async () => {
  //     if (!formData.roadAddress.trim()) {
  //       alert("기본주소(도로명)를 먼저 선택해 주세요.");
  //       return;
  //     }
  //     try {
  //       setSaving(true);
  //       await apiUpdateMyAddress(formData); // 내부에서 compose 전송
  //       setData((prev) => ({ ...prev, memberAddress: composeAddress(formData) }));
  //       alert("주소가 저장되었습니다.");
  //     } catch (err) {
  //       const msg =
  //         err?.response?.data?.message ||
  //         err?.response?.data ||
  //         err?.message ||
  //         "주소 저장 중 오류가 발생했습니다.";
  //       alert(msg);
  //     } finally {
  //       setSaving(false);
  //     }
  //   };

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
              <div className="temp_form md">
                <input
                  type="text"
                  value={data.memberPhone ?? ""}
                  className="temp_input"
                  readOnly
                />
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
                  type="text"
                  value={data.smsAgree ? "동의" : "미동의"}
                  className="temp_input"
                  readOnly
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
