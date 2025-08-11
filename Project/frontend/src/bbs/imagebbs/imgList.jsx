import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Gallery.css";

export default function ImgBoard() {
  const [posts, setPosts] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();

   // 검색 상태
  const [searchType, setSearchType] = useState("all");

  const fetchPosts = async (page = 0, keyword = "") => {
    try {
      const res = await axios.get("/bbs/bbslist", {
        params: {
          searchType: "title",
          bbstitle: keyword || undefined,
          type: "POTO",
          page,
          size: 12, // 4 x 3
        },
      });
      setPosts(res.data.content);
      setTotalPages(res.data.totalPages);
      setCurrentPage(res.data.number);
    } catch (error) {
      console.error("데이터 불러오기 오류:", error);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  const handleSearch = () => {
    fetchPosts(0, searchKeyword);
  };

  const handlePageChange = (page) => {
    fetchPosts(page, searchKeyword);
  };

  return (
    <div className="img-board-container">
      {/* 검색창 */}
      <div className="search-bar">
        <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
          <option value="all">전체</option>
          <option value="title">제목</option>
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

      {/* 게시글 목록 */}
      <div className="img-board-grid">
        {posts.map((post) => (
          <div
            className="img-board-item"
            key={post.bulletinNum}
            onClick={() => navigate(`/imgbbs/${post.bulletinNum}`)}
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
                <span>{post.regdate?.substring(0, 10)}</span>
                <span>조회 {post.readcount}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* 페이지네이션 */}
      <div className="pagination">
        {Array.from({ length: totalPages }, (_, idx) => (
          <button
            key={idx}
            className={idx === currentPage ? "active" : ""}
            onClick={() => handlePageChange(idx)}
          >
            {idx + 1}
          </button>
        ))}
      </div>
    </div>
  );
}
