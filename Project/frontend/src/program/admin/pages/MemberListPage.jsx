// src/program/admin/pages/MemberListPage.jsx
// 목적: 관리자 회원 목록 (페이지네이션 제거 버전)
// - 검색어 입력 후 "검색" 누를 때만 서버 호출.
// - 목록은 한 번에 받아와 테이블에 그대로 표시.
// - 상세보기 링크는 routes 중앙관리의 build 사용.

import React, { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { fetchAdminMembersNoPaging } from "../services/adminApi";

export default function MemberListPage() {
  const [keyword, setKeyword] = useState("");   // 서버 검색용 키워드
  const [loading, setLoading] = useState(false);
  const [members, setMembers] = useState([]);   // 실제 테이블에 뿌릴 데이터
  const [error, setError] = useState("");

  // 목록 로드 (검색 포함)
  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const list = await fetchAdminMembersNoPaging({ keyword });
      setMembers(list);
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || e.message || "목록을 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  }, [keyword]);

  // 최초 1회 로드
  useEffect(() => {
    load();
  }, [load]);

  // 검색 버튼
  const handleSearch = (e) => {
    e?.preventDefault?.();
    load();
  };

  return (
    <div className="signup-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon type2"></div>
          <div className="form_title">회원 관리</div>
          <div className="form_desc">
            <p>관리자 기준 회원 목록을 조회합니다.</p>
            <p>페이지네이션은 추후 공용 컴포넌트로 교체 예정</p>
          </div>
        </div>
      </div>

      {/* 검색바 (서버 검색: 이름 부분일치) */}
      <form className="form_flex" onSubmit={handleSearch} style={{ gap: 8, marginBottom: 12 }}>
        <div className="temp_form md">
          <input
            className="temp_input"
            type="text"
            placeholder="이름 검색 (부분일치)"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
        </div>
        <div className="temp_btn md">
          <button type="submit" className="btn">검색</button>
        </div>
      </form>

      {/* 에러 메시지 */}
      {error && <div className="hint warn" style={{ marginBottom: 8 }}>{error}</div>}

      {/* 목록 테이블 */}
      <div className="form_wrap">
        <table className="table type2 responsive">
          <thead>
            <tr>
              <th>번호</th>
              <th>아이디</th>
              <th>이름</th>
              <th>가입일</th>
              <th>상태</th>
              <th>잠금</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={7}>불러오는 중...</td></tr>
            ) : members.length === 0 ? (
              <tr><td colSpan={7}>데이터가 없습니다.</td></tr>
            ) : (
              members.map((m) => (
                <tr key={m.memberNum}>
                  <td>{m.memberNum}</td>
                  <td>{m.memberId}</td>
                  <td>{m.memberName}</td>
                  <td>{m.memberDay || "-"}</td>
                  <td>{m.memberState}</td>
                  <td>{m.memberLock ? "잠금" : "정상"}</td>
                  <td>
                    <div className="temp_btn sm">
                      <Link className="btn">
                        상세보기
                      </Link>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
