// src/common/paging/usePager.js
import { useCallback, useEffect, useMemo, useState } from "react";

export default function usePager({ fetcher, pageSize = 10, debounceMs = 0 }) {
  const [keyword, setKeyword] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(pageSize);
  const [items, setItems] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const debouncedKeyword = useDebounce(keyword, debounceMs);

  // ✅ fetcher만 의존 (page/size/keyword는 "인자"로 전달)
  const load = useCallback(
    async (p, s, k) => {
      setLoading(true);
      setError("");
      try {
        const {
          items,
          page: cur,
          totalPages,
        } = await fetcher({
          page: p,
          size: s,
          keyword: k,
        });
        setItems(items || []);
        // 서버가 page를 문자열로 줄 수도 있으니 숫자로 보정
        const nextPage = Number.isFinite(cur) ? Number(cur) : p;
        setTotalPages(Number.isFinite(totalPages) ? Number(totalPages) : 0);
        setPage(nextPage);
      } catch (e) {
        console.error(e);
        setError(
          e?.response?.data?.message ||
            e.message ||
            "목록을 불러오지 못했습니다."
        );
        setItems([]);
        setTotalPages(0);
      } finally {
        setLoading(false);
      }
    },
    [fetcher]
  );

  // ✅ 초기 1회 + 키워드/사이즈 변경 시 0페이지부터 로딩
  useEffect(() => {
    load(0, size, debouncedKeyword);
  }, [debouncedKeyword, size, load]);

  // ✅ page 변경 시 로딩
  useEffect(() => {
    load(page, size, debouncedKeyword);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]); // page 변경 "만" 트리거

  const search = useCallback(() => setPage(0), []);
  const handlePageChange = useCallback(
    (newPage) => {
      if (newPage >= 0 && newPage < Math.max(totalPages, 1)) setPage(newPage);
    },
    [totalPages]
  );

  return useMemo(
    () => ({
      keyword,
      setKeyword,
      items,
      page,
      totalPages,
      loading,
      error,
      search,
      handlePageChange,
    }),
    [keyword, items, page, totalPages, loading, error, search, handlePageChange]
  );
}

function useDebounce(value, delay = 0) {
  const [v, setV] = useState(value);
  useEffect(() => {
    if (!delay) {
      setV(value);
      return;
    }
    const id = setTimeout(() => setV(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);
  return v;
}
