// 📁 src/admin/AdminQnaBbs.js
import React, { useEffect, useState } from "react";
import axios from "axios";
import "./qnabbs.css";

function AdminQnaBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [expandedRow, setExpandedRow] = useState(null);
  const [answerText, setAnswerText] = useState("");

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
      console.error("게시글 불러오기 중 오류:", error);
      alert("목록 조회 실패");
    }
  };

  useEffect(() => {
    fetchPosts(page);
  }, [page, searchType, searchKeyword]);

  const handleSearch = () => {
    setPage(0);
    fetchPosts(0);
  };

  const toggleRow = (id, existingAnswer) => {
    if (expandedRow === id) {
      setExpandedRow(null);
    } else {
      setExpandedRow(id);
      setAnswerText(existingAnswer || "");
    }
  };

  const handleSaveAnswer = async (bbsId) => {
    try {
      await axios.post(`/admin/bbs/qna/${bbsId}/answer`, {
        content: answerText
      }, {
        params: { adminId: 1 } // 관리자 ID 필요
      });
      alert("답변이 저장되었습니다.");
      fetchPosts(page);
      setExpandedRow(null);
    } catch (err) {
      console.error(err);
      alert("답변 저장 실패");
    }
  };

  const handleUpdateAnswer = async (qnaId) => {
    try {
      await axios.put(`/admin/bbs/qna/${qnaId}`, {
        content: answerText
      });
      alert("답변이 수정되었습니다.");
      fetchPosts(page);
      setExpandedRow(null);
    } catch (err) {
      console.error(err);
      alert("답변 수정 실패");
    }
  };

  const handleDeleteAnswer = async (qnaId) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await axios.delete(`/admin/bbs/qna/${qnaId}`);
      alert("답변이 삭제되었습니다.");
      fetchPosts(page);
      setExpandedRow(null);
    } catch (err) {
      console.error(err);
      alert("답변 삭제 실패");
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
        </select>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="검색어를 입력하세요"
        />
        <button onClick={handleSearch}>조회</button>
      </div>

      <table className="bbs-table">
        <thead>
          <tr>
            <th style={{ width: "5%" }}>번호</th>
            <th style={{ width: "65%" }}>제목</th>
            <th style={{ width: "15%" }}>작성자</th>
            <th style={{ width: "10%" }}>작성일</th>
            <th style={{ width: "5%" }}>답변</th>
          </tr>
        </thead>
        <tbody>
          {posts.length > 0 ? (
            posts.map((post) => (
              <React.Fragment key={post.bulletinNum}>
                <tr>
                  <td>{post.bulletinNum}</td>
                  <td>{post.bbsTitle}</td>
                  <td>{post.memberName || "익명"}</td>
                  <td>{new Date(post.registDate).toLocaleDateString()}</td>
                  <td>
                    <button onClick={() => toggleRow(post.bulletinNum, post.answerContent)}>V</button>
                  </td>
                </tr>
                {expandedRow === post.bulletinNum && (
                  <tr>
                    <td colSpan="5">
                      <div className="answer-section">
                        {post.answerContent ? (
                          <>
                            <textarea
                              value={answerText}
                              onChange={(e) => setAnswerText(e.target.value)}
                              rows={4}
                            />
                            <div>
                              <button onClick={() => handleUpdateAnswer(post.qnaId)}>수정</button>
                              <button onClick={() => handleDeleteAnswer(post.qnaId)}>삭제</button>
                            </div>
                          </>
                        ) : (
                          <>
                            <textarea
                              value={answerText}
                              onChange={(e) => setAnswerText(e.target.value)}
                              placeholder="답변을 입력하세요"
                              rows={4}
                            />
                            <div>
                              <button onClick={() => handleSaveAnswer(post.bulletinNum)}>저장</button>
                            </div>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))
          ) : (
            <tr>
              <td colSpan="5">등록된 질문이 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>«</button>
        {Array.from({ length: totalPages }, (_, i) => (
          <button key={i} className={page === i ? "active" : ""} onClick={() => setPage(i)}>
            {i + 1}
          </button>
        ))}
        <button disabled={page === totalPages - 1} onClick={() => setPage(page + 1)}>»</button>
      </div>
    </div>
  );
}

export default AdminQnaBbs;
