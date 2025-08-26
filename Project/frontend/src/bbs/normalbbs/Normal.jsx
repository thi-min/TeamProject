// 📁 src/admin/NoticeBbs.jsx
import React, { useEffect, useState } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "./normalbbs.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';

function NoticeBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isAdmin, setIsAdmin] = useState(true);

  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");

  const navigate = useNavigate();
  const BASE_URL = "http://127.0.0.1:8090"; // 관리자 백엔드 서버 주소

  // 📌 공지사항 목록 불러오기
  const fetchNotices = async (pageNumber = 0) => {
    try {
      const params = {
        page: pageNumber,
        size: 10,
        type: "NORMAL",
      };

      if (searchKeyword.trim() !== "" && searchType !== "all") {
        if (searchType === "title") params.bbstitle = searchKeyword.trim();
        else if (searchType === "writer") params.memberName = searchKeyword.trim();
        else if (searchType === "content") params.bbscontent = searchKeyword.trim();
      }

      // 🔹 공지사항 전용 API 호출
      const response = await api.get(`${BASE_URL}/admin/bbs/notices`, { params });

      // 🔑 백엔드 구조에 맞게 처리
      const data = response.data;
      setPosts(data.list || []);
      setTotalPages(Math.ceil((data.total || 0) / (data.size || 10)));
      setPage(data.page || 0);
    } catch (error) {
      console.error("공지사항 불러오기 오류:", error);
      alert("목록 조회 실패");
    }
  };

  useEffect(() => {
    fetchNotices(page);
  }, [page]);

  // ✍ 글쓰기 버튼 클릭 시
  const handleWrite = () => {
    navigate("/admin/bbs/normal/write"); // Route에 맞춰 수정
  };

  const handleSearch = () => {
    setPage(0);
    fetchNotices(0);
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
    }
  };

  return (
    <div className="bbs-container">
      <h2>📢 공지사항 게시판 (관리자)</h2>

      {/* 🔍 검색창 */}
      <div className="search-bar">
        <div className="temp_form_box lg">
          <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
            <option value="writer">작성자</option>
          </select>
        </div>
        <div className="temp_form_box lg">
          <input
            type="text"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="검색어를 입력하세요"
          />
        </div>
        <button onClick={handleSearch}>조회</button>
      </div>

      {/* ✍ 글쓰기 버튼 */}
      {isAdmin && (
        <div className="top-bar" style={{ margin: "10px 0" }}>
          <button className="write-btn" onClick={handleWrite}>
            글쓰기
          </button>
        </div>
      )}

      {/* 📄 게시글 테이블 */}
      <table className="bbs-table">
        <div className="table responsive">
          <colgroup>
            <col style={{ width: "10%" }} />
            <col style={{ width: "70%" }} />
            <col style={{ width: "10%" }} />
            <col style={{ width: "10%" }} />
          </colgroup>
          <thead>
            <tr>
              <th>번호</th>
              <th>제목</th>
              <th>작성자</th>
              <th>작성일</th>
            </tr>
          </thead>
          <tbody>
            {posts.length > 0 ? (
              posts.map((post) => (
                <tr
                  key={post.bulletinNum}
                  onClick={() => navigate(`/admin/notice/view/${post.bulletinNum}`)}
                  style={{ cursor: "pointer" }}
                >
                  <td>{post.bulletinNum}</td>
                  <td>{post.bbstitle}</td>
                  <td>{post.memberName}</td>
                  <td>{new Date(post.createdAt).toLocaleDateString()}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} style={{ textAlign: "center", padding: "90px 0" }}>
                  등록된 공지가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </div>
      </table>

      {/* 📌 페이지네이션 */}
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

export default NoticeBbs;
