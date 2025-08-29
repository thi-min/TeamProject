import React, { useState, useCallback } from "react";
import usePager from "./usePager";
import PaginationBar from "./PaginationBar";
import SearchBar from "./SearchBar";

/**
 * columns: [{ label: string, render: (row)=>ReactNode }]
 * fetcher: async ({ page, size, keyword }) => { items, page, totalPages }
 */
export default function PaginatedTable({
  title,
  fetcher,
  columns,
  pageSize = 10,
  debounceMs = 0,
  searchPlaceholder = "검색어를 입력하세요",
  searchFilter, // (val)=>string
}) {
  const { items, page, totalPages, loading, error, search, handlePageChange } =
    usePager({ fetcher, pageSize, debounceMs });

  return (
    <div className="paginated_table_page">
      {title && <h3 className="title">{title}</h3>}

      {loading && <div className="loading">로딩중…</div>}
      {error && <div className="error">{error}</div>}

      <table className="table border">
        <thead>
          <tr>
            {columns.map((c, i) => (
              <th key={i}>{c.label}</th>
            ))}
          </tr>
        </thead>
        <tbody className="text_center">
          {items.length === 0 ? (
            <tr>
              <td colSpan={columns.length}>데이터가 없습니다.</td>
            </tr>
          ) : (
            items.map((row, ridx) => (
              <tr key={row.id ?? row.key ?? ridx}>
                {columns.map((c, cidx) => (
                  <td key={cidx}>{c.render(row)}</td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>

      <PaginationBar
        page={page}
        totalPages={totalPages}
        onChange={handlePageChange}
      />
    </div>
  );
}
