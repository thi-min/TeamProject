// 📁 src/admin/ImgBoard.jsx
import React, { useEffect, useState } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "./Gallery.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faChevronRight } from "@fortawesome/free-solid-svg-icons";

export default function AdminImgBoard() {
  const [posts, setPosts] = useState([]);
  const [repImages, setRepImages] = useState({});
  const [searchKeyword, setSearchKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");
  const [selectedPosts, setSelectedPosts] = useState([]);

  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/admin/bbs/poto";
  const pageSize = 12;

  // ---------------- 게시글 + 대표 이미지 조회 ----------------
  const fetchPosts = async (page = 0, keyword = "") => {
    try {
      const params = { page, size: pageSize };
      if (searchType !== "all" && keyword.trim() !== "") {
        if (searchType === "title") params.bbstitle = keyword.trim();
        if (searchType === "content") params.bbscontent = keyword.trim();
        if (searchType === "writer") params.memberName = keyword.trim();
      }

      const res = await api.get(baseUrl, { params });
      const postsArray = Array.isArray(res.data.list) ? res.data.list : [];
      setPosts(postsArray);

      setTotalPages(res.data.totalPages ?? 1);
      setCurrentPage(res.data.page ?? 0);

      const repMap = {};
      (res.data.representativeImages || []).forEach((img) => {
        if (img.bulletinNum && img.imagePath) {
          repMap[img.bulletinNum] = `http://127.0.0.1:8090${img.imagePath}`;
        }
      });
      setRepImages(repMap);

      setSelectedPosts([]);
    } catch (error) {
      console.error("게시글 조회 실패:", error);
      alert("게시글 조회 실패");
      setPosts([]);
      setTotalPages(0);
      setCurrentPage(0);
    }
  };

  useEffect(() => {
    fetchPosts(currentPage, searchKeyword);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage]);

  // ---------------- 검색 ----------------
  const handleSearch = () => {
    setCurrentPage(0);
    fetchPosts(0, searchKeyword);
  };

  // ---------------- 페이지 변경 ----------------
  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) setCurrentPage(page);
  };

  // ---------------- 전체 선택 / 해제 ----------------
  const handleSelectAll = (checked) => {
    if (checked) {
      setSelectedPosts(posts.map((p) => p.bulletinNum));
    } else {
      setSelectedPosts([]);
    }
  };

  // ---------------- 개별 체크박스 ----------------
  const handleCheckboxChange = (id) => {
    setSelectedPosts((prev) =>
      prev.includes(id) ? prev.filter((pid) => pid !== id) : [...prev, id]
    );
  };

  // ---------------- 선택 삭제 ----------------
  const handleDeleteSelected = async () => {
    if (selectedPosts.length === 0) {
      alert("삭제할 게시글을 선택하세요.");
      return;
    }
    if (!window.confirm("선택한 게시글을 삭제하시겠습니까?")) return;

    try {
      await api.delete("/admin/bbs/delete-multiple", {
        params: { adminId: 1, ids: selectedPosts.join(",") },
      });
      alert("삭제 완료");
      fetchPosts(currentPage, searchKeyword);
    } catch (err) {
      console.error(err);
      alert("삭제 실패");
    }
  };

  return (
    <div className="img-board-container">
      {/* 상단 메뉴 + 전체 선택 체크박스 */}
      <div className="top-bar">
        <div className="right-controls">
          <input
            type="checkbox"
            checked={selectedPosts.length === posts.length && posts.length > 0}
            onChange={(e) => handleSelectAll(e.target.checked)}
          />
          <label>전체 선택</label>
          <button className="delete-btn" onClick={handleDeleteSelected}>
            선택 삭제
          </button>
          <button className="write-btn" onClick={() => navigate("/bbs/image/write")}>
            글쓰기
          </button>
        </div>
      </div>

      {/* 검색창 */}
      <div className="search-bar">
        <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
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

      {/* 게시글 목록 */}
      {posts.length > 0 ? (
        <div className="img-board-grid">
          {posts.map((post) => {
            const repImage = repImages[post.bulletinNum];
            return (
              <div
                className={`img-board-item ${selectedPosts.includes(post.bulletinNum) ? "selected" : ""}`}
                key={post.bulletinNum}
              >
                {/* ✅ 개별 체크박스 - 카드 왼쪽 상단 */}
                <input
                  type="checkbox"
                  className="item-checkbox"
                  checked={selectedPosts.includes(post.bulletinNum)}
                  onChange={() => handleCheckboxChange(post.bulletinNum)}
                />

                {/* 게시글 이미지 + 클릭 이동 */}
                <div
                  className="img-thumb"
                  onClick={() => navigate(`/bbs/admin/image/Detail/${post.bulletinNum}`)} // ✅ 경로 수정
                >
                  {repImage ? (
                    <img src={repImage} alt={post.bbstitle} />
                  ) : (
                    <div className="no-image">🖼️</div>
                  )}
                </div>

                {/* 게시글 정보 */}
                <div className="img-info">
                  <div className="title">{post.bbstitle}</div>
                  <div className="meta">
                    <span>{post.regdate?.substring(0, 10)}</span>
                    <span>조회 {post.readcount}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="no-posts">등록된 게시물이 없습니다.</div>
      )}

      {/* 페이지네이션 */}
      <div className="pagination">
        <button disabled={currentPage === 0} onClick={() => handlePageChange(currentPage - 1)}>
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
