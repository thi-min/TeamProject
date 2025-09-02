import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./qnabbs.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";

function QnaBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");

  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/bbs/bbslist";

  const fetchPosts = async (pageNumber = 0) => {
    try {
      const params = { type: "FAQ", page: pageNumber, size: 10 };

      if (searchType !== "all" && searchKeyword.trim() !== "") {
        params.searchType = searchType;
        if (searchType === "title") params.bbstitle = searchKeyword.trim();
        if (searchType === "content") params.bbscontent = searchKeyword.trim();
      }

      const response = await axios.get(baseUrl, { params });
      const content = response.data?.bbsList?.content || []; // 안전하게 가져오기
      setPosts(content);
      setTotalPages(response.data?.bbsList?.totalPages || 0);
      setPage(response.data?.bbsList?.number || 0);
    } catch (error) {
      console.error("게시글 불러오기 중 오류 발생:", error);
      alert("목록 조회 실패");
      setPosts([]);
      setTotalPages(0);
      setPage(0);
    }
  };

  useEffect(() => {
    fetchPosts(page);
  }, [page]);

  const handleSearch = () => {
    setPage(0);
    fetchPosts(0);
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) setPage(newPage);
  };

  const handleWrite = () => {
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인 후 글쓰기가 가능합니다.");
      return;
    }
    navigate("/bbs/qna/write");
  };

  return (
    <div className="bbs-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">Q&A 게시판</div>
        </div>
      </div>

      {/* 검색창 */}
      <div className="search_bar_box">
        <div className="temp_form_box md">
          <select className="temp_select" value={searchType} onChange={(e) => setSearchType(e.target.value)}>
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
          </select>
        </div>
        <div className="temp_form md w30p">
          <input
            type="text"
            className="temp_input"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="검색어를 입력하세요"
          />
        </div>
        <div className="temp_btn md">
          <button className="btn" onClick={handleSearch}>조회</button>
        </div>
      </div>

      {/* 게시판 테이블 */}
      <table className="table responsive border">
        <colgroup>
          <col style={{ width: "10%" }} />
          <col style={{ width: "65%" }} />
          <col style={{ width: "10%" }} />
          <col style={{ width: "15%" }} />
        </colgroup>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody className="text_center">
          {posts.length > 0 ? (
            posts.map((post) => (
              <tr key={post.bulletinNum}>
                <td>{post.bulletinNum}</td>
                <td>
                  <Link style={{ width: "100%", height: "100%" }} to={`/bbs/qna/${post.bulletinNum}`}>{post.bbsTitle}</Link>
                </td>
                <td>{post.memberName || "익명"}</td>
                <td>{post.registDate ? new Date(post.registDate).toLocaleDateString() : ""}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={4} style={{ textAlign: "center", padding: "90px 0" }}>
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
        <button
          disabled={page === Math.max(totalPages, 1) - 1}
          onClick={() => handlePageChange(page + 1)}
        >
          <FontAwesomeIcon icon={faChevronRight} />
        </button>
      </div>

      <div className="form_center_box solo">
        <div className="temp_btn md">
          <button className="btn" onClick={handleWrite}>
            글쓰기
          </button>
        </div>
      </div>
    </div>
  );
}

export default QnaBbs;