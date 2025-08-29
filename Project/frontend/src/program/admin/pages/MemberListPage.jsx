// 목적: 관리자 회원 목록 (공용 페이지네이션/검색 컴포넌트 사용)
// 사용법 요약:
//  1) fetchers.js에서 이 페이지가 쓸 fetcher를 import 한다.
//  2) <PaginatedTable>에 fetcher와 columns만 넘긴다.
//  3) 검색 입력제한이 필요하면 searchFilter로 가공(예: 숫자만 허용)

import React, { useState, useCallback } from "react";
import { Link } from "react-router-dom";
import PaginatedTable from "../../../common/paging/PaginatedTable";
import SearchBar from "../../../common/paging/SearchBar";
import { fetchAdminMembersByNum } from "./fetchers";

export default function MemberListPage() {
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState(""); // "" | "ACTIVE" | "REST" | "OUT"
  const onSearch = useCallback(() => {
    // fetcher에 status를 함께 넘기도록 래핑해서 사용
    // fetchAdminMembersByNum({ page:0, size:10, keyword, status })
  }, [keyword, status]);
  return (
    <div className="signup-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon type2"></div>
          <div className="form_title">회원 관리</div>
          <div className="form_desc">
            <p>관리자 기준 회원 목록을 조회합니다.</p>
          </div>
        </div>
      </div>
      <SearchBar
        keyword={keyword}
        setKeyword={setKeyword}
        onSearch={onSearch}
        placeholder="검색어를 입력해주세요"
        // 🔽 select 활성화
        selectEnabled
        selectValue={status}
        setSelectValue={setStatus}
        selectOptions={[
          { value: "", label: "전체" },
          { value: "ACTIVE", label: "이름" },
          { value: "REST", label: "아이디" },
        ]}
        selectName="memberState"
        selectPosition="before" // or "after"
        // selectClassName="temp_select" // 필요시 커스텀
      />
      <PaginatedTable
        fetcher={fetchAdminMembersByNum} // ✅ 이 페이지만의 데이터 호출 함수
        pageSize={10}
        debounceMs={0} // 즉시 검색. 실시간이면 300 추천
        columns={[
          { label: "번호", render: (r) => r.memberNum },
          { label: "아이디", render: (r) => r.memberId },
          { label: "이름", render: (r) => r.memberName },
          { label: "가입일", render: (r) => r.memberDay || "-" },
          { label: "상태", render: (r) => r.memberState },
          { label: "잠금", render: (r) => (r.memberLock ? "잠금" : "정상") },
          {
            label: "관리",
            render: (r) => (
              <div className="temp_btn sm">
                <Link className="btn" to={`/admin/membersList/${r.memberNum}`}>
                  상세보기
                </Link>
              </div>
            ),
          },
        ]}
      />
    </div>
  );
}
