import React, { useState } from "react";
import "./normalbbs.css";

function AdminNormalBbsdummy() {
  const [posts] = useState(
    Array.from({ length: 25 }, (_, i) => ({
      bulletinNum: i + 1,
      bbstitle: `공지사항 (관리자) ${i + 1}`,
      writer: "관리자",
      createdAt: new Date(2025, 7, (i % 28) + 1).toISOString(),
    }))
  );

  const [page, setPage] = useState(0);
  const postsPerPage = 10;
  const totalPages = Math.ceil(posts.length / postsPerPage);

  const paginatedPosts = posts.slice(
    page * postsPerPage,
    (page + 1) * postsPerPage
  );

  return (
    <div className="bbs-container">
      <h2>📢 공지사항 (관리자)</h2>

      <div className="notice-top-btns">
        <button onClick={() => alert("글쓰기 페이지로 이동")}>글쓰기</button>
      </div>

      <div className="table responsive">
        <table className="bbs-table">
          <thead>
            <tr>
              <th>번호</th>
              <th>제목</th>
              <th>작성자</th>
              <th>작성일</th>
            </tr>
          </thead>
          <tbody>
            {paginatedPosts.map((post) => (
              <tr
                key={post.bulletinNum}
                onClick={() => alert(`관리자: ${post.bbstitle} 클릭됨`)}
                style={{ cursor: "pointer" }}
              >
                <td>{post.bulletinNum}</td>
                <td>{post.bbstitle}</td>
                <td>{post.writer}</td>
                <td>{new Date(post.createdAt).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* 페이지네이션 */}
      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>
          «
        </button>
        {Array.from({ length: totalPages }, (_, idx) => (
          <button
            key={idx}
            onClick={() => setPage(idx)}
            className={idx === page ? "active" : ""}
          >
            {idx + 1}
          </button>
        ))}
        <button
          disabled={page === totalPages - 1}
          onClick={() => setPage(page + 1)}
        >
          »
        </button>
      </div>
    </div>
  );
}

export default AdminNormalBbsdummy;