import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./qnabbs.css";

function QnaBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // 여기로 옮겼음
  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");

  const navigate = useNavigate();

  const fetchPosts = async (pageNumber = 0) => {
    try {
      const params = {
        type: "FAQ",
        page: pageNumber,
        size: 10,
      };

      if (searchType !== "all" && searchKeyword.trim() !== "") {
        params.searchType = searchType;
        params.keyword = searchKeyword.trim();
      }

      const response = await axios.get("/bbs/bbslist", { params });

      setPosts(response.data.content);
      setTotalPages(response.data.totalPages);
      setPage(response.data.number);
    } catch (error) {
      console.error("게시글 불러오기 중 오류 발생:", error);
      alert("목록 조회 실패");
    }
  };

  // 페이지, 검색 조건 바뀔 때마다 다시 불러오기
  useEffect(() => {
    fetchPosts(page);
  }, [page, searchType, searchKeyword]);

  const handleSearch = () => {
    setPage(0);    // 검색 버튼 누르면 1페이지부터 조회
    fetchPosts(0);
  };

  return (
    <div className="bbs-container">
      <h2>❓ Q&A 게시판</h2>

      {/* 검색창 */}
      <div className="search-bar">
        <div className="temp_form_box lg">
          <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
            <option value="all">전체</option>
            <option value="title">제목</option>
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

      <div className="table responsive">
        <table className="bbs-table">
          <colgroup>
            <col style={{ width: "10%" }} />
            <col style={{ width: "70%" }} />
            <col style={{ width: "15%" }} />
            <col style={{ width: "5%" }} />
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
                <tr key={post.bulletinNum}>
                  <td>{post.bulletinNum}</td>
                  <td>
                    <Link to={`/qnabbs/view/${post.bulletinNum}`}>{post.bbsTitle}</Link>
                  </td>
                  <td>{post.memberName || "익명"}</td>
                  <td>{new Date(post.registDate).toLocaleDateString()}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="4">등록된 질문이 없습니다.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* 페이지네이션 */}
      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>
          «
        </button>
        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i}
            className={page === i ? "active" : ""}
            onClick={() => setPage(i)}
          >
            {i + 1}
          </button>
        ))}
        <button disabled={page === totalPages - 1} onClick={() => setPage(page + 1)}>
          »
        </button>
      </div>

      {/* 글쓰기 버튼 */}
      <div className="bbs-actions">
        <button onClick={() => navigate("/qnabbs/write")}>질문 작성</button>
      </div>
    </div>
  );
}

export default QnaBbs;
