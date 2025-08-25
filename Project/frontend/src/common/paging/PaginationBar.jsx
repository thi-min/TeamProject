import React from "react";
import "../style/pagin.css";

export default function PaginationBar({
  page,
  totalPages,
  onChange,
  //className = "pagination",
}) {
  const total = Math.max(totalPages, 1);
  return (
    <div className="pagination_box">
      <button
        className="page_btn prev"
        disabled={page <= 0}
        onClick={() => onChange(page - 1)}
      >
        이전
      </button>
      <div className="page_btn_box">
        {Array.from({ length: total }, (_, i) => (
          <button
            key={i}
            className={page === i ? "page active" : "page"}
            onClick={() => onChange(i)}
          >
            {i + 1}
          </button>
        ))}
      </div>

      <button
        className="next page_btn"
        disabled={page >= total - 1}
        onClick={() => onChange(page + 1)}
      >
        다음
      </button>
    </div>
  );
}
