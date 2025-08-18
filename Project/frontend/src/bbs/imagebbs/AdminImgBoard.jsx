import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Gallery.css";

export default function AdminImgBoard() {
  const [posts, setPosts] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedPosts, setSelectedPosts] = useState([]); // ✅ 선택된 게시글
  const [searchType, setSearchType] = useState("all");
  const navigate = useNavigate();

  const fetchPosts = async (page = 0, keyword = "") => {
    try {
      const res = await axios.get("/bbs/bbslist", {
        params: {
          searchType: searchType !== "all" ? searchType : undefined,
          bbstitle: searchType === "title" ? keyword : undefined,
          bbscontent: searchType === "content" ? keyword : undefined,
          type: "POTO",
          page,
          size: 12,
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

  // ✅ 체크박스 선택
  const handleCheckboxChange = (id) => {
    setSelectedPosts((prev) =>
      prev.includes(id) ? prev.filter((pid) => pid !== id) : [...prev, id]
    );
  };

  // ✅ 선택 삭제
  const handleDeleteSelected = async () => {
    if (selectedPosts.length === 0) {
      alert("삭제할 게시글을 선택하세요.");
      return;
    }
    if (!window.confirm("선택한 게시글을 삭제하시겠습니까?")) return;

    try {
      await axios.delete("/admin/bbs/delete-multiple", {
        data: selectedPosts,
        params: { adminId: 1 }, // 관리자 ID (임시)
      });
      alert("삭제 완료");
      setSelectedPosts([]);
      fetchPosts(currentPage, searchKeyword);
    } catch (err) {
      console.error(err);
      alert("삭제 실패");
    }
  };

  return (
    <div className="img-board-container">
      {/* 상단 메뉴 (❌ 글쓰기 제거, ✅ 삭제만 가능) */}
      <div className="top-bar">
        <button className="delete-btn" onClick={handleDeleteSelected}>
          선택 삭제
        </button>
      </div>

      {/* 검색창 */}
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
          placeholder="검색어 입력"
        />
        <button onClick={handleSearch}>조회</button>
      </div>

      {/* 게시글 목록 */}
      <div className="img-board-grid">
        {posts.map((post) => (
          <div
            className={`img-board-item ${
              selectedPosts.includes(post.bulletinNum) ? "selected" : ""
            }`}
            key={post.bulletinNum}
          >
            {/* ✅ 체크박스 */}
            <input
              type="checkbox"
              className="select-checkbox"
              checked={selectedPosts.includes(post.bulletinNum)}
              onChange={() => handleCheckboxChange(post.bulletinNum)}
            />

            {/* ✅ 관리자도 게시글 상세 조회는 가능 → navigate 유지 */}
            <div
              className="img-thumb"
              onClick={() => navigate(`/imgbbs/${post.bulletinNum}`)}
            >
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
