import React, { useEffect, useState } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";

export default function ImgBoard() {
  const [posts, setPosts] = useState([]);
  const [repImages, setRepImages] = useState({});
  const [searchKeyword, setSearchKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");

  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/bbs/bbslist";
  const backendUrl = "http://127.0.0.1:8090"; // ⚠️ /DATA 경로에는 붙이지 않음

  // /DATA 또는 http(s)로 시작하면 그대로, 그 외만 backendUrl prefix
  const resolveSrc = (raw) => {
    if (!raw) return null;
    const s = String(raw);
    if (s.startsWith("/DATA") || s.startsWith("http")) return s;
    return `${backendUrl}${s}`;
  };

  // 게시글 + 대표 이미지 조회
  const fetchPosts = async (page = 0, keyword = "") => {
    try {
      const params = { type: "POTO", page, size: 12 };

      // 검색 파라미터
      if (searchType !== "all" && keyword.trim() !== "") {
        params.searchType = searchType;
        if (searchType === "title") params.bbstitle = keyword.trim();
        if (searchType === "content") params.bbscontent = keyword.trim();
      }

      const res = await api.get(baseUrl, { params });

      const pageData = res.data.bbsList || {};
      setPosts(pageData.content || []);
      setTotalPages(pageData.totalPages || 0);
      setCurrentPage(pageData.number || 0);

      // 대표 이미지 맵 구성
      const repMap = {};
      const repImagesFromBack = res.data.representativeImages || {};
      for (const [key, value] of Object.entries(repImagesFromBack)) {
        if (value) {
          // thumbnailPath 우선, 없으면 imagePath 사용
          const raw = value.thumbnailPath || value.imagePath || null;
          repMap[key] = raw ? { ...value, imagePath: resolveSrc(raw) } : null;
        } else {
          repMap[key] = null;
        }
      }
      setRepImages(repMap);
    } catch (error) {
      console.error("데이터 불러오기 오류:", error);
      alert("게시글 조회 실패");
    }
  };

  useEffect(() => {
    fetchPosts(currentPage, searchKeyword);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage]);

  const handleSearch = () => {
    setCurrentPage(0);
    fetchPosts(0, searchKeyword);
  };

  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) setCurrentPage(page);
  };

  const formatDate = (iso) => {
    if (!iso) return "";
    // registDate가 ISO 문자열이면 앞 10자리, 객체면 적절히 변환
    try {
      const s = String(iso);
      return s.length >= 10 ? s.slice(0, 10) : s;
    } catch {
      return "";
    }
  };

  return (
    <div className="img-board-container">
      <div className="top-bar">
        <button
          className="write-btn"
          onClick={() => navigate("/bbs/image/write")}
        >
          글쓰기
        </button>
      </div>

      <div className="search-bar">
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
        >
          <option value="all">전체</option>
          <option value="title">제목</option>
          <option value="content">내용</option>
        </select>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="검색어를 입력하세요"
        />
        <button onClick={handleSearch}>조회</button>
      </div>

      {posts.length > 0 ? (
        <div className="img-board-grid">
          {posts.map((post) => {
            // key는 bulletinNum 문자열 키로 저장되어 있음
            const repImage = repImages[String(post.bulletinNum)];
            const thumbSrc = repImage?.imagePath || null;

            return (
              <div
                className="img-board-item"
                key={post.bulletinNum}
                onClick={() => navigate(`/bbs/image/${post.bulletinNum}`)}
              >
                <div className="img-thumb">
                  {thumbSrc ? (
                    <img src={thumbSrc} alt={post.bbsTitle} />
                  ) : (
                    <div className="no-image">🖼️</div>
                  )}
                </div>
                <div className="img-info">
                  {/* ✅ 백엔드 DTO 필드명과 일치 */}
                  <div className="title">{post.bbsTitle}</div>
                  <div className="meta">
                    <span>{formatDate(post.registDate)}</span>
                    <span>조회 {post.viewers ?? 0}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="no-posts">등록된 게시물이 없습니다.</div>
      )}

      <div className="pagination">
        <button
          disabled={currentPage === 0}
          onClick={() => handlePageChange(currentPage - 1)}
        >
          <FontAwesomeIcon icon={faChevronLeft} />
        </button>
        {Array.from({ length: Math.max(totalPages, 1) }, (_, idx) => (
          <button
            key={idx}
            className={idx === currentPage ? "active" : ""}
            onClick={() => handlePageChange(idx)}
          >
            {idx + 1}
          </button>
        ))}
        <button
          disabled={currentPage === Math.max(totalPages, 1) - 1}
          onClick={() => handlePageChange(currentPage + 1)}
        >
          <FontAwesomeIcon icon={faChevronRight} />
        </button>
      </div>
    </div>
  );
}
