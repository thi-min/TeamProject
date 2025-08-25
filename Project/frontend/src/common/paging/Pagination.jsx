import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";

export default function Pagination({
  page = 0,
  totalPages = 0,
  onChange, // (newPage) => void
  className = "pagination",
}) {
  const pages = Array.from({ length: Math.max(totalPages, 1) }, (_, i) => i);

  return (
    <div className={className}>
      <button
        disabled={page <= 0}
        onClick={() => onChange?.(page - 1)}
        aria-label="이전 페이지"
      >
        <FontAwesomeIcon icon={faChevronLeft} />
      </button>

      {pages.map((i) => (
        <button
          key={i}
          className={page === i ? "active" : ""}
          onClick={() => onChange?.(i)}
          aria-current={page === i ? "page" : undefined}
        >
          {i + 1}
        </button>
      ))}

      <button
        disabled={page >= Math.max(totalPages, 1) - 1}
        onClick={() => onChange?.(page + 1)}
        aria-label="다음 페이지"
      >
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </div>
  );
}
