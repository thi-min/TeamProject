// 📁 src/member/MemberNormalBbs.jsx
import React, { useEffect, useState } from "react";
import api from "../../common/api/axios";
import "./normalbbs.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';

function MemberNormalBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");

  const BASE_URL = "http://127.0.0.1:8090"; // 백엔드 서버 주소
  const PAGE_SIZE = 10; // 한 페이지당 글 수

  // 게시판 목록 불러오기
  const fetchNotices = async (pageNumber = 0) => {
    try {
      const params = {
        type: "NORMAL",
        page: pageNumber,
        size: PAGE_SIZE,
      };

      // 검색 조건 반영
      if (searchKeyword.trim() !== "" && searchType !== "all") {
        if (searchType === "title") params.bbstitle = searchKeyword.trim();
        else if (searchType === "writer") params.memberName = searchKeyword.trim();
        else if (searchType === "content") params.bbscontent = searchKeyword.trim();
      }

      const response = await api.get(`${BASE_URL}/bbs/bbslist`, { params });
      const bbsData = response.data.bbsList;

      setPosts(bbsData.content || []);
      setTotalPages(bbsData.totalPages || 0);
      setPage(bbsData.number || 0);

    } catch (error) {
      console.error("공지사항을 불러오는 중 오류 발생:", error);
      alert("공지사항 조회 실패");
    }
  };

  useEffect(() => {
    fetchNotices(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

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
      <h2>📢 공지사항 게시판</h2>

      {/* 검색창 */}
      <div className="search-bar">
        <div className="temp_form_box lg">
          <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
            <option value="writer">작성자</option>
          </select>
        </div>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="검색어를 입력하세요"
        />
        <button onClick={handleSearch}>조회</button>
      </div>

      {/* 게시글 테이블 */}
      <table className="bbs-table">
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
                onClick={() => window.location.href = `/bbs/normal/view/${post.bulletinNum}`} // 수정된 경로
                style={{ cursor: "pointer" }}
              >
                <td>{post.bulletinNum}</td>
                <td>{post.bbsTitle}</td>
                <td>관리자</td>
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

        <button disabled={page === totalPages - 1} onClick={() => handlePageChange(page + 1)}>
          <FontAwesomeIcon icon={faChevronRight} />
        </button>
      </div>
    </div>
  );
}

export default MemberNormalBbs;
