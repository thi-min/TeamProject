// 📁 src/admin/NoticeBbs.jsx
import React, { useEffect, useState } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "./normalbbs.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";

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
      };

      if (searchKeyword.trim() !== "" && searchType !== "all") {
        if (searchType === "title") params.bbstitle = searchKeyword.trim();
        else if (searchType === "writer")
          params.memberName = searchKeyword.trim();
        else if (searchType === "content")
          params.bbscontent = searchKeyword.trim();
      }

      // 🔹 공지사항 전용 API 호출
      const response = await api.get(`${BASE_URL}/admin/bbs/notices`, {
        params,
      });

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

  const handleWrite = () => {
    navigate("/admin/bbs/normal/write"); // 글쓰기 페이지로 이동
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
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">게시판 관리</div>
        </div>
      </div>
      <h3>공지사항</h3>

      {/* 🔍 검색창 */}
      <div className="search_bar_box">
        <div className="temp_form_box md">
          <select className="temp_select"  value={searchType} onChange={(e) => setSearchType(e.target.value)}>
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
            <option value="writer">작성자</option>
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

      {/* 📄 게시글 테이블 */}
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
                {/* 제목 클릭 시 상세 페이지로 이동 */}
                <td
                  style={{ cursor: "pointer", color: "#007bff" }}
                  onClick={() => navigate(`/admin/bbs/normal/${post.bulletinNum}`)}
                >
                  {post.bbsTitle}
                </td>
                <td>관리자</td>
                <td>{new Date(post.registDate).toLocaleDateString()}</td>
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
      {/* ✍ 글쓰기 버튼 */}
      {isAdmin && (
        <div className="form_center_box solo">
        <div className="temp_btn md">
          <button className="btn" onClick={handleWrite}>
            글쓰기
          </button>
        </div>
      </div>
      )}
    </div>
  );
}

export default NoticeBbs;