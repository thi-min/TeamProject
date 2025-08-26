import React from "react";

/**
 * 공용 검색바 + (옵션) 셀렉트박스 1개
 *
 * Props
 * - keyword, setKeyword, onSearch, placeholder, filter: 기존 동일
 * - selectEnabled?: boolean               // 셀렉트 사용 여부 (기본 false)
 * - selectValue?: string                  // 셀렉트 현재 값 (페이지에서 상태 관리)
 * - setSelectValue?: (v: string)=>void    // 셀렉트 변경 핸들러 (페이지에서 내려줌)
 * - selectOptions?: Array<{value:string,label:string}> // 셀렉트 옵션
 * - selectName?: string                   // name 속성
 * - selectPosition?: "before" | "after"   // 인풋 앞/뒤 배치 (기본 "before")
 * - selectClassName?: string              // 클래스 커스터마이즈 (기본 "temp_select")
 */

import "../style/pagin.css";

export default function SearchBar({
  keyword,
  setKeyword,
  onSearch,
  placeholder = "검색어를 입력하세요",
  filter, // (val)=>string | undefined

  // 🔽 optional select props
  selectEnabled = false,
  selectValue = "",
  setSelectValue,
  selectOptions = [],
  selectName,
  selectPosition = "before",
}) {
  const SelectBox = selectEnabled ? (
    <div className="temp_form_box md">
      <select
        name={selectName}
        className="temp_select"
        value={selectValue}
        onChange={(e) => setSelectValue?.(e.target.value)}
      >
        {selectOptions.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  ) : null;

  return (
    <form
      className="search_bar_box"
      onSubmit={(e) => {
        e.preventDefault();
        onSearch?.();
      }}
    >
      {/* select 앞 배치 */}
      {selectPosition === "before" && SelectBox}

      <div className="temp_form md w30p">
        <input
          className="temp_input"
          value={keyword}
          onChange={(e) => {
            const next = filter ? filter(e.target.value) : e.target.value;
            setKeyword(next);
          }}
          placeholder={placeholder}
        />
      </div>

      {/* select 뒤 배치 */}
      {selectPosition === "after" && SelectBox}

      <div className="temp_btn md">
        <button className="btn" type="submit">
          검색
        </button>
      </div>
    </form>
  );
}
