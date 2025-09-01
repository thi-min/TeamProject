import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./qnabbs.css";

function AdminQnaBbsDummy() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [searchType, setSearchType] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [selectedPosts, setSelectedPosts] = useState([]);
  const [expandedPosts, setExpandedPosts] = useState([]); // 답글 펼침 상태
  const navigate = useNavigate();

  // 더미 데이터
  const dummyData = [
    {
      bulletinNum: 1,
      bbsTitle: "더미 질문 1",
      bbsContent: "내용 1",
      memberName: "홍길동",
      registDate: "2025-08-17T12:00:00",
      answerContent: "답변 1"
    },
    {
      bulletinNum: 2,
      bbsTitle: "더미 질문 2",
      bbsContent: "내용 2",
      memberName: "익명",
      registDate: "2025-08-16T14:30:00",
      answerContent: ""
    },
    {
      bulletinNum: 3,
      bbsTitle: "더미 질문 3",
      bbsContent: "내용 3",
      memberName: "김철수",
      registDate: "2025-08-15T09:20:00",
      answerContent: "답변 3"
    }
  ];

  const fetchPosts = (pageNumber = 0) => {
    let filtered = dummyData;
    if (searchType !== "all" && searchKeyword.trim() !== "") {
      const keyword = searchKeyword.trim().toLowerCase();
      if (searchType === "title") {
        filtered = dummyData.filter(post => post.bbsTitle.toLowerCase().includes(keyword));
      } else if (searchType === "writer") {
        filtered = dummyData.filter(post => post.memberName.toLowerCase().includes(keyword));
      }
    }
    setPosts(filtered);
    setPage(pageNumber);
  };

  useEffect(() => {
    fetchPosts(page);
  }, [page, searchType, searchKeyword]);

  const handleSearch = () => {
    fetchPosts(0);
  };

  const handleCheckboxChange = (id) => {
    setSelectedPosts(prev =>
      prev.includes(id) ? prev.filter(pid => pid !== id) : [...prev, id]
    );
  };

  const toggleAnswer = (id) => {
    setExpandedPosts(prev =>
      prev.includes(id) ? prev.filter(pid => pid !== id) : [...prev, id]
    );
  };

  return (
    <div className="bbs-container">
      <h2>📌 Q&A 게시판 (관리자) - 더미데이터</h2>

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
            <th>선택</th>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>답글</th>
          </tr>
        </thead>
        <tbody>
          {posts.length > 0 ? (
            posts.map(post => (
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
                    onClick={() => alert(`상세보기: ${post.bbsTitle}`)}
                  >
                    {post.bbsTitle}
                  </td>
                  <td>{post.memberName}</td>
                  <td>{new Date(post.registDate).toLocaleDateString()}</td>
                  <td>
                    {post.answerContent ? (
                      <button onClick={() => toggleAnswer(post.bulletinNum)}>
                        {expandedPosts.includes(post.bulletinNum) ? "숨기기" : "보기"}
                      </button>
                    ) : (
                      "-"
                    )}
                  </td>
                </tr>
                {/* 답글 표시 */}
                {expandedPosts.includes(post.bulletinNum) && (
                  <tr className="answer-row">
                    <td colSpan="6" style={{ background: "#f9f9f9", padding: "10px" }}>
                      <strong>답변:</strong> {post.answerContent}
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))
          ) : (
            <tr>
              <td colSpan="6">등록된 질문이 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default AdminQnaBbsDummy;