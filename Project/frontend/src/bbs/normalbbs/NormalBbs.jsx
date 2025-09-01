import React, { useEffect, useState } from "react";
import axios from "axios";
import "./normalbbs.css"; // 스타일 따로 관리

function NoticeBbs() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isAdmin, setIsAdmin] = useState(false); // 기본은 false

  // ✅ 개발용 임시 관리자 설정
  useEffect(() => {
    const storedAdminId = localStorage.getItem("adminId");

    if (storedAdminId) {
      setIsAdmin(true);
    } else {
      // 개발자 테스트용 임시 adminId 설정
      localStorage.setItem("adminId", "test-admin");
      setIsAdmin(true);
    }

    fetchNotices();
  }, []);

  // 🔹 게시판 목록 불러오기
  const fetchNotices = async (pageNumber = 0) => {
    try {
      const response = await axios.get("/admin/bbs/bbslist", {
        params: {
          type: "NORMAL",         // 공지사항 게시판
          searchType: null,
          bbstitle: null,
          bbscontent: null,
          page: pageNumber,
          size: 10
        }
      });

      setPosts(response.data.content);
      setTotalPages(response.data.totalPages);
      setPage(response.data.number);
    } catch (error) {
      console.error("공지사항을 불러오는 중 오류 발생:", error);
    }
  };

  // 🔹 페이지 이동
  const handlePageChange = (newPage) => {
    fetchNotices(newPage);
  };

  // 🔹 글쓰기 버튼 클릭 시 이동
  const handleWrite = () => {
    window.location.href = "/admin/notice/write";
  };

  return (
    <div className="notice-container">
      <h2>📢 공지사항 게시판</h2>

      {isAdmin && (
        <div className="notice-top-btns">
          <button onClick={handleWrite}>글쓰기</button>
        </div>
      )}

      <table className="notice-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {posts.length > 0 ? (
            posts.map((post) => (
              <tr
                key={post.bulletinNum}
                onClick={() => window.location.href = `/admin/notice/view/${post.bulletinNum}`}
                style={{ cursor: "pointer" }}
              >
                <td>{post.bulletinNum}</td>
                <td>{post.bbstitle}</td>
                <td>{new Date(post.createdAt).toLocaleDateString()}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="3">등록된 공지사항이 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>

      {/* 🔹 페이지네이션 */}
      <div className="pagination">
        {Array.from({ length: totalPages }, (_, idx) => (
          <button
            key={idx}
            onClick={() => handlePageChange(idx)}
            className={idx === page ? "active" : ""}
          >
            {idx + 1}
          </button>
        ))}
      </div>
    </div>
  );
}

export default NoticeBbs;