// src/program/admin/pages/MemberDetailPage.jsx
// 목적: 관리자용 회원 상세 조회 + 상태/잠금 수정
// - 백엔드:
//    GET /membersList/{memberNum}    → 상세 조회
//    PUT /membersList/{memberNum}?memberState=...&memberLock=... → 상태/잠금 수정
// - 상세 응답 DTO에 없는 필드는 null/undefined 일 수 있으므로 안전하게 표시

import React, { useEffect, useState, useCallback } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  fetchAdminMemberDetail,
  updateAdminMemberState,
} from "../services/adminApi";

const STATE_OPTIONS = [
  { value: "ACTIVE", label: "정상(ACTIVE)" },
  { value: "REST", label: "휴면(REST)" },
  { value: "OUT", label: "탈퇴(OUT)" },
];

export default function MemberDetailPage() {
  const { memberNum } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [detail, setDetail] = useState(null);
  const [error, setError] = useState("");

  // 수정 폼 상태 (초기값은 상세 데이터 로드 후 반영)
  const [memberState, setMemberState] = useState("ACTIVE");
  const [memberLock, setMemberLock] = useState(false);

  const loadDetail = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await fetchAdminMemberDetail(memberNum);
      setDetail(data || null);
      // 상세에 상태/잠금이 포함되어 있으면 초기값 반영
      if (data?.memberState) setMemberState(String(data.memberState));
      if (typeof data?.memberLock === "boolean") setMemberLock(!!data.memberLock);
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || e.message || "상세 정보를 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  }, [memberNum]);

  useEffect(() => {
    loadDetail();
  }, [loadDetail]);

  // 수정 요청
  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      const res = await updateAdminMemberState(memberNum, {
        memberState,
        memberLock,
      });
      alert(res?.message || "수정이 완료되었습니다.");
      // 성공 후 상세 정보 갱신
      await loadDetail();
    } catch (e) {
      console.error(e);
      alert(e?.response?.data?.message || e.message || "수정 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="signup-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon type2"></div>
          <div className="form_title">회원 상세</div>
          <div className="form_desc">
            <p>회원 번호: {memberNum}</p>
          </div>
        </div>
      </div>

      <div className="temp_btn md" style={{ marginBottom: 12 }}>
        <Link className="btn" to="/admin/members">목록으로</Link>
      </div>

      {error && <div className="hint warn" style={{ marginBottom: 8 }}>{error}</div>}

      {!detail || loading ? (
        <div className="form_wrap">불러오는 중...</div>
      ) : (
        <>
          {/* 상세 정보 */}
          <div className="form_wrap" style={{ marginBottom: 24 }}>
            <table className="table type2 responsive">
              <tbody>
                <tr>
                  <th scope="row">회원번호</th>
                  <td>{detail.memberNum}</td>
                </tr>
                <tr>
                  <th scope="row">아이디(이메일)</th>
                  <td>{detail.memberId || "-"}</td>
                </tr>
                <tr>
                  <th scope="row">이름</th>
                  <td>{detail.memberName || "-"}</td>
                </tr>
                <tr>
                  <th scope="row">생년월일</th>
                  <td>{detail.memberBirth || "-"}</td>
                </tr>
                <tr>
                  <th scope="row">핸드폰</th>
                  <td>{detail.memberPhone || "-"}</td>
                </tr>
                <tr>
                  <th scope="row">주소</th>
                  <td>{detail.memberAddress || "-"}</td>
                </tr>
                <tr>
                  <th scope="row">가입일</th>
                  <td>{detail.memberDay || "-"}</td>
                </tr>
                <tr>
                  <th scope="row">성별</th>
                  <td>{detail.memberSex || "-"}</td>
                </tr>
                <tr>
                  <th scope="row">문자 수신</th>
                  <td>{detail.smsAgree ? "동의" : "미동의"}</td>
                </tr>
                <tr>
                  <th scope="row">계정 잠금</th>
                  <td>{detail.memberLock ? "잠금" : "정상"}</td>
                </tr>
                <tr>
                  <th scope="row">상태</th>
                  <td>{detail.memberState || "-"}</td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* 상태/잠금 수정 폼 */}
          <form className="form_wrap" onSubmit={handleUpdate}>
            <table className="table type2 responsive">
              <tbody>
                <tr>
                  <th scope="row">회원 상태 변경</th>
                  <td>
                    <div className="temp_form md w40p">
                      <select
                        className="temp_input"
                        value={memberState}
                        onChange={(e) => setMemberState(e.target.value)}
                      >
                        {STATE_OPTIONS.map((o) => (
                          <option key={o.value} value={o.value}>{o.label}</option>
                        ))}
                      </select>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row">계정 잠금</th>
                  <td>
                    <div className="temp_form md">
                      <input
                        id="lockCheck"
                        type="checkbox"
                        className="temp_check"
                        checked={memberLock}
                        onChange={(e) => setMemberLock(e.target.checked)}
                      />
                      <label htmlFor="lockCheck">잠금 설정</label>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>

            <div className="form_btn_box">
              <div className="temp_btn md">
                <button type="submit" className="btn">저장</button>
              </div>
              <div className="temp_btn md white">
                <button type="button" className="btn" onClick={() => navigate(-1)}>
                  뒤로
                </button>
              </div>
            </div>
          </form>
        </>
      )}
    </div>
  );
}
