import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./normalbbs.css"; // 스타일 따로 관리
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';

function NoticeBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isAdmin, setIsAdmin] = useState(true); // 관리자 여부 (임시)

  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");

  const navigate = useNavigate();

  // 게시판 목록 불러오기
  const fetchNotices = async (pageNumber = 0) => {
    try {
      const params = {
        type: "NORMAL",
        page: pageNumber,
        size: 10,
      };

      if (searchType !== "all" && searchKeyword.trim() !== "") {
        params.searchType = searchType;
        params.keyword = searchKeyword.trim();
      }

      const response = await axios.get("/admin/bbs/bbslist", { params });
      setPosts(response.data.content);
      setTotalPages(response.data.totalPages);
      setPage(response.data.number);
    } catch (error) {
      console.error("공지사항을 불러오는 중 오류 발생:", error);
    }
  };

  useEffect(() => {
    fetchNotices();
  }, []);

  // 페이지 이동
  const handlePageChange = (newPage) => {
    fetchNotices(newPage);
  };

  // 글쓰기 버튼 클릭
  const handleWrite = () => {
    navigate("/bbs/normal/write"); // SPA 방식으로 이동
  };

  // 검색 실행
  const handleSearch = () => {
    fetchNotices(0);
  };

  return (
    <div className="bbs-container">
      <h2>📢 공지사항 게시판</h2>

      {/* 검색창 */}
      <div className="search-bar">
        <div className="temp_form_box lg">
          <select  className="temp_select"  value={searchType} onChange={(e) => setSearchType(e.target.value)}>
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="writer">작성자</option>
          </select>
        </div>
        <div class ="temp_form_box lg">
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="검색어를 입력하세요"
          style={{ height: "100%" }}
        />
        </div>
        <button onClick={handleSearch}>조회</button>
      </div>

      {/* 글쓰기 버튼 */}
      {isAdmin && (
        <div className="top-bar" style={{ margin: "10px 0" }}>
          <button className="write-btn" onClick={handleWrite}>
            글쓰기
          </button>
        </div>
      )}

      {/* 게시판 테이블 */}
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
              <th scope="col">번호</th>
              <th scope="col">제목</th>
              <th scope="col">작성자</th>
              <th scope="col">작성일</th>
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
                  <td>{post.writer}</td>
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

      {/* 페이지네이션 */}
      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>
          <FontAwesomeIcon icon={faChevronLeft} />
        </button>

        {Array.from({ length: Math.max(totalPages, 1) }, (_, i) => (
          <button
            key={i}
            className={page === i ? "active" : ""}
            onClick={() => setPage(i)}
          >
            {i + 1}
          </button>
        ))}

        <button disabled={page === Math.max(totalPages, 1) - 1} onClick={() => setPage(page + 1)}>
          <FontAwesomeIcon icon={faChevronRight} />
        </button>
      </div>
    </div>
  );
}

export default NoticeBbs;
