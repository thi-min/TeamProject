// 📁 src/admin/AdminQnaBbs.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./qnabbs.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';

function AdminQnaBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [expandedRow, setExpandedRow] = useState(null);
  const [selectedPosts, setSelectedPosts] = useState([]);
  const navigate = useNavigate();

  const BASE_URL = "http://127.0.0.1:8090"; // 백엔드 서버 주소

  const fetchPosts = async (pageNumber = 0) => {
    try {
      const params = { type: "FAQ", page: pageNumber, size: 10 };
      if (searchType !== "all" && searchKeyword.trim() !== "") {
        if (searchType === "title") params.bbstitle = searchKeyword.trim();
        else if (searchType === "writer") params.memberName = searchKeyword.trim();
        else if (searchType === "content") params.bbscontent = searchKeyword.trim();
      }
      const response = await axios.get(`${BASE_URL}/bbs/bbslist`, { params });
      setPosts(response.data.content);
      setTotalPages(response.data.totalPages);
      setPage(response.data.number);
    } catch (error) {
      console.error("게시글 불러오기 중 오류:", error);
      alert("목록 조회 실패");
    }
  };

  useEffect(() => {
    fetchPosts(page);
  }, [page]);

  const handleSearch = () => {
    setPage(0);
    fetchPosts(0);
  };

  const handleCheckboxChange = (id) => {
    setSelectedPosts(prev =>
      prev.includes(id) ? prev.filter(pid => pid !== id) : [...prev, id]
    );
  };

  const handleDeleteSelected = async () => {
    if (selectedPosts.length === 0) {
      alert("삭제할 게시글을 선택하세요.");
      return;
    }
    if (!window.confirm("선택한 게시글을 정말 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`${BASE_URL}/admin/bbs/delete-multiple`, {
        data: { ids: selectedPosts },
        params: { adminId: 1 }
      });
      alert("선택한 게시글이 삭제되었습니다.");
      setSelectedPosts([]);
      fetchPosts(page);
    } catch (err) {
      console.error(err);
      alert("삭제 실패");
    }
  };

  const toggleRow = (id) => {
    setExpandedRow(expandedRow === id ? null : id);
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
    }
  };

  return (
    <div className="bbs-container">
      <h2>📌 Q&A 게시판 (관리자)</h2>

      {/* 검색창 */}
      <div className="search-bar">
        <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
          <option value="all">전체</option>
          <option value="title">제목</option>
          <option value="writer">작성자</option>
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

      {/* 다중 삭제 버튼 */}
      <div style={{ margin: "10px 0" }}>
        <button onClick={handleDeleteSelected}>선택 삭제</button>
      </div>

      {/* 게시글 테이블 */}
      <table className="bbs-table">
        <thead>
          <tr>
            <th style={{ width: "3%" }}>선택</th>
            <th style={{ width: "5%" }}>번호</th>
            <th style={{ width: "60%" }}>제목</th>
            <th style={{ width: "15%" }}>작성자</th>
            <th style={{ width: "10%" }}>작성일</th>
            <th style={{ width: "7%" }}>답변</th>
          </tr>
        </thead>
        <tbody>
          {posts.length > 0 ? (
            posts.map((post) => (
              <React.Fragment key={post.bulletinNum}>
                <tr>
                  <td>
                    <input
                      type="checkbox"
                      checked={selectedPosts.includes(post.bulletinNum)}
                      onChange={() => handleCheckboxChange(post.bulletinNum)}
                    />
                  </td>
                  <td>{post.bulletinNum}</td>
                  <td
                    style={{ cursor: "pointer", color: "blue" }}
                    onClick={() => navigate(`bbs/admin/qna/view/${post.bulletinNum}`)}
                  >
                    {post.bbsTitle}
                  </td>
                  <td>{post.memberName || "익명"}</td>
                  <td>{new Date(post.registDate).toLocaleDateString()}</td>
                  <td>
                    {post.answerContent && (
                      <button onClick={() => toggleRow(post.bulletinNum)}>
                        {expandedRow === post.bulletinNum ? "접기" : "보기"}
                      </button>
                    )}
                  </td>
                </tr>
                {expandedRow === post.bulletinNum && post.answerContent && (
                  <tr>
                    <td colSpan="6">
                      <div className="answer-section">
                        <strong>답변:</strong>
                        <p>{post.answerContent}</p>
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))
          ) : (
            <tr>
              <td colSpan="6" style={{ textAlign: "center", padding: "50px 0" }}>
                등록된 질문이 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      <div className="pagination">
        <button disabled={page === 0} onClick={() => handlePageChange(page - 1)}>
          <FontAwesomeIcon icon={faChevronLeft} />
        </button>

        {Array.from({ length: Math.max(totalPages, 1) }, (_, i) => (
          <button
            key={i}
            className={page === i ? "active" : ""}
            onClick={() => handlePageChange(i)}
          >
            {i + 1}
          </button>
        ))}

        <button disabled={page === Math.max(totalPages, 1) - 1} onClick={() => handlePageChange(page + 1)}>
          <FontAwesomeIcon icon={faChevronRight} />
        </button>
      </div>
    </div>
  );
}

export default AdminQnaBbs;
