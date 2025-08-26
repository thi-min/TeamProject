import React from "react";

export default function SearchBar({
  keyword,
  setKeyword,
  onSearch,
  placeholder = "검색어를 입력하세요",
  className = "search_form",
  buttonClassName = "btn",
}) {
  const submit = (e) => {
    e?.preventDefault?.();
    onSearch?.();
  };

  return (
    <form className={className} onSubmit={submit}>
      <input
        type="text"
        className="temp_input"
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        placeholder={placeholder}
      />
      <button type="submit" className={buttonClassName}>
        검색
      </button>
    </form>
  );
}
