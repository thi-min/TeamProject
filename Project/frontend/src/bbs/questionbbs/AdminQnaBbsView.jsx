// 📁 src/admin/AdminQnaBbsView.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./qnabbs.css";

function AdminQnaBbsView() {
  const { id } = useParams(); // 게시글 번호
  const [post, setPost] = useState(null);
  const [answerText, setAnswerText] = useState(""); // 답변 내용
  const navigate = useNavigate();

  // ✅ 관리자 게시판 API 기본 URL
  const BASE_URL = "http://127.0.0.1:8090/admin/bbs";

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/bbslist/${id}`);
      setPost(res.data);
      setAnswerText(res.data.answerContent || ""); // 기존 답변 불러오기
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      alert("조회 실패");
    }
  };

  // 답변 저장 (새 답변)
  const handleSaveAnswer = async () => {
    try {
      await axios.post(`${BASE_URL}/qna/${id}/answer`, { content: answerText }, { params: { adminId: 1 } });
      alert("답변이 저장되었습니다.");
      fetchPost();
    } catch (error) {
      console.error("답변 저장 실패:", error);
      alert("답변 저장 실패");
    }
  };

  // 답변 수정
  const handleUpdateAnswer = async () => {
    try {
      await axios.put(`${BASE_URL}/qna/${post.qnaId}`, { content: answerText });
      alert("답변이 수정되었습니다.");
      fetchPost();
    } catch (error) {
      console.error("답변 수정 실패:", error);
      alert("답변 수정 실패");
    }
  };

  // 답변 삭제
  const handleDeleteAnswer = async () => {
    if (!window.confirm("답변을 삭제하시겠습니까?")) return;
    try {
      await axios.delete(`${BASE_URL}/qna/${post.qnaId}`);
      alert("답변이 삭제되었습니다.");
      setAnswerText("");
      fetchPost();
    } catch (error) {
      console.error("답변 삭제 실패:", error);
      alert("답변 삭제 실패");
    }
  };

  // 게시글 삭제
  const handleDeletePost = async () => {
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;
    try {
      await axios.delete(`${BASE_URL}/${id}`, { params: { adminId: 1 } });
      alert("게시글이 삭제되었습니다.");
      navigate("/admin/bbs");
    } catch (error) {
      console.error("삭제 오류:", error);
      alert("삭제 실패");
    }
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-container">
      <h2>{post.bbsTitle}</h2>
      <div className="bbs-content">
        <p>{post.bbsContent}</p>
        <p>작성자: {post.memberName || "익명"}</p>
        <p>작성일: {new Date(post.registDate).toLocaleDateString()}</p>
      </div>

      {/* 첨부파일 */}
      {post.files && post.files.length > 0 && (
        <div className="bbs-files">
          <h4>첨부파일</h4>
          <ul>
            {post.files.map(file => (
              <li key={file.id}>
                {file.url.match(/\.(jpeg|jpg|gif|png)$/) ? (
                  <img src={file.url} alt={file.name} style={{ maxWidth: "200px" }} />
                ) : (
                  <a href={file.url} download>{file.name}</a>
                )}
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* 답변 섹션 */}
      <div className="answer-section">
        <h4>답변</h4>
        <textarea
          value={answerText}
          onChange={(e) => setAnswerText(e.target.value)}
          placeholder="답변을 입력하세요"
          rows={5}
          style={{ width: "100%" }}
        />
        <div style={{ marginTop: "5px" }}>
          {post.answerContent ? (
            <>
              <button onClick={handleUpdateAnswer}>수정</button>
              <button onClick={handleDeleteAnswer}>삭제</button>
            </>
          ) : (
            <button onClick={handleSaveAnswer}>저장</button>
          )}
        </div>
      </div>

      {/* 버튼 */}
      <div style={{ marginTop: "10px" }}>
        <button onClick={handleDeletePost}>게시글 삭제</button>
        <button onClick={() => navigate("/admin/bbs")}>목록으로</button>
      </div>
    </div>
  );
}

export default AdminQnaBbsView;
