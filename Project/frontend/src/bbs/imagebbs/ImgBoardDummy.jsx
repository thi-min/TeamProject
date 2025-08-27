import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Gallery.css";

export default function ImgBoardDummy() {
  const navigate = useNavigate();

  // ✅ 더미 데이터 (120개)
  const [allPosts] = useState(
    Array.from({ length: 140 }, (_, i) => ({
      bulletinNum: i + 1,
      bbstitle: `더미 게시글 ${i + 1}`,
      bbscontent: `이것은 더미 게시글 ${i + 1}의 내용입니다.`,
      writer: `작성자${(i % 5) + 1}`,
      imagePath: `https://via.placeholder.com/400x300.png?text=Post+${i + 1}`,
      regdate: `2025-08-${String((i % 30) + 1).padStart(2, "0")}`,
      readcount: Math.floor(Math.random() * 1000),
    }))
  );

  // ✅ 검색 상태
  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");

  // ✅ 현재 검색 조건에 맞는 데이터 필터링
  const filteredPosts = allPosts.filter((post) => {
    if (!searchKeyword) return true;
    if (searchType === "title") {
      return post.bbstitle.includes(searchKeyword);
    } else if (searchType === "content") {
      return post.bbscontent.includes(searchKeyword);
    } else if (searchType === "writer") {
      return post.writer.includes(searchKeyword);
    }
    return (
      post.bbstitle.includes(searchKeyword) ||
      post.bbscontent.includes(searchKeyword) ||
      post.writer.includes(searchKeyword)
    );
  });

  // ✅ 페이지네이션 상태
  const [currentPage, setCurrentPage] = useState(0);
  const postsPerPage = 12;
  const totalPages = Math.ceil(filteredPosts.length / postsPerPage);

  // ✅ 10개 단위로 페이지네이션 묶기
  const pageGroupSize = 10;
  const currentGroup = Math.floor(currentPage / pageGroupSize);
  const startPage = currentGroup * pageGroupSize;
  const endPage = Math.min(startPage + pageGroupSize, totalPages);

  // ✅ 현재 페이지 게시글 slice
  const paginatedPosts = filteredPosts.slice(
    currentPage * postsPerPage,
    (currentPage + 1) * postsPerPage
  );

  // ✅ 검색 실행 시 첫 페이지로
  const handleSearch = () => {
    setCurrentPage(0);
  };

  return (
    <div className="img-board-container">
      {/* ✅ 상단 버튼 */}
      <div className="top-bar">
        <button
          className="write-btn"
          onClick={() => navigate("/bbs/image/write")}
        >
          글쓰기
        </button>
      </div>

      {/* ✅ 검색창 */}
      <div className="search-bar">
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
        >
          <option value="all">전체</option>
          <option value="title">제목</option>
          <option value="content">내용</option>
          <option value="writer">작성자</option>
        </select>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="검색어를 입력하세요"
        />
        <button onClick={handleSearch}>조회</button>
      </div>

      {/* ✅ 게시글 목록 */}
      <div className="img-board-grid">
        {paginatedPosts.length > 0 ? (
          paginatedPosts.map((post) => (
            <div
              className="img-board-item"
              key={post.bulletinNum}
              onClick={() => alert(`${post.bbstitle} 클릭됨`)}
            >
              <div className="img-thumb">
                {post.imagePath ? (
                  <img src={post.imagePath} alt={post.bbstitle} />
                ) : (
                  <div className="no-image">이미지 없음</div>
                )}
              </div>
              <div className="img-info">
                <div className="title">{post.bbstitle}</div>
                <div className="meta">
                  <span>{post.regdate}</span>
                  <span>조회 {post.readcount}</span>
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="no-result">검색 결과가 없습니다.</div>
        )}
      </div>

      {/* ✅ 페이지네이션 */}
      <div className="pagination">
        {/* 이전 그룹 버튼 */}
        {currentGroup > 0 && (
          <button onClick={() => setCurrentPage(startPage - 1)}>이전</button>
        )}

        {/* 현재 그룹 페이지 번호 */}
        {Array.from({ length: endPage - startPage }, (_, idx) => (
          <button
            key={startPage + idx}
            className={currentPage === startPage + idx ? "active" : ""}
            onClick={() => setCurrentPage(startPage + idx)}
          >
            {startPage + idx + 1}
          </button>
        ))}

        {/* 다음 그룹 버튼 */}
        {endPage < totalPages && (
          <button onClick={() => setCurrentPage(endPage)}>다음</button>
        )}
      </div>
    </div>
  );
}
