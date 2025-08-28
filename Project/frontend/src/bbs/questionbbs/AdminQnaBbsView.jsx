// 📁 src/admin/AdminQnaBbsView.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";
import "./qnabbs.css";

export default function AdminQnaBbsView() {
  const { id } = useParams(); // 게시글 번호
  const navigate = useNavigate();
  const token = localStorage.getItem("accessToken");

  const [post, setPost] = useState(null);       // { bbs: {}, answer: "" }
  const [answerText, setAnswerText] = useState("");
  const [files, setFiles] = useState([]);       // 첨부파일 리스트

  const BASE_URL = "http://127.0.0.1:8090/admin/bbs";

  // ---------------- 게시글 + 답변 조회 ----------------
  const fetchPost = async () => {
    try {
      const res = await api.get(`${BASE_URL}/qna/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log("게시글 조회 결과:", res.data);
      setPost(res.data);
      setAnswerText(res.data.answer || ""); // 기존 답변 불러오기
    } catch (err) {
      console.error("게시글 조회 실패:", err);
      if (err.response?.status === 401) {
        alert("로그인 정보가 만료되었습니다.");
        navigate("/admin/login");
      } else {
        alert("게시글 조회 실패");
      }
    }
  };

  // ---------------- 첨부파일 조회 ----------------
  const fetchFiles = async () => {
    try {
      const res = await api.get(`${BASE_URL}/${id}/files`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log("첨부파일 조회:", res.data);
      setFiles(res.data);
    } catch (err) {
      console.error("첨부파일 조회 실패:", err);
      setFiles([]);
    }
  };

  useEffect(() => {
    fetchPost();
    fetchFiles();
  }, [id]);

  // ---------------- 답변 저장 ----------------
  const handleSaveAnswer = async () => {
    if (!answerText.trim()) {
      alert("답변을 입력해주세요.");
      return;
    }
    try {
      const res = await api.post(
        `${BASE_URL}/qna/${id}/answer`,
        { answer: answerText },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      console.log("답변 저장 성공:", res.data);

      setPost((prev) => ({
        ...prev,
        answer: res.data.answer,
      }));
      setAnswerText(res.data.answer || "");
      alert("답변이 저장되었습니다.");
      navigate("/admin/bbs/qna");
    } catch (err) {
      console.error("답변 저장 실패:", err);
      alert("답변 저장 실패. 다시 시도해주세요.");
    }
  };

  // ---------------- 게시글 삭제 ----------------
  const handleDeletePost = async () => {
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;
    try {
      await api.delete(`${BASE_URL}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("게시글이 삭제되었습니다.");
      navigate("/admin/bbs/qna");
    } catch (err) {
      console.error("게시글 삭제 실패:", err);
      alert(err.response?.data?.message || "게시글 삭제 실패");
    }
  };

  if (!post) return <div>로딩 중...</div>;

  const bbs = post.bbs || {}; // bbs 정보가 들어있는 객체

  return (
    <div className="bbs-container">
      <h2>{bbs.bbsTitle}</h2>

      <div className="bbs-content">
        {/* 본문 HTML 렌더링 (이미지 포함 가능) */}
        <div
          dangerouslySetInnerHTML={{ __html: bbs.bbsContent }}
        />
        <p>작성자: {bbs.memberName || "익명"}</p>
        <p>작성일: {new Date(bbs.registDate).toLocaleDateString()}</p>
      </div>

      {/* 첨부파일 섹션 */}
      {files.length > 0 && (
        <div className="file-section">
          <h4>첨부파일</h4>
          <ul>
            {files.map((file) => (
              <li key={file.fileNum}>
                <a href={file.fileUrl} target="_blank" rel="noopener noreferrer">
                  {file.originalName}
                </a>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* 답변 섹션 */}
      <div className="answer-section">
        <h4>답변</h4>
        {post.answer && (
          <div className="existing-answer">
            <strong>현재 답변:</strong>
            <p>{post.answer}</p>
          </div>
        )}
        <textarea
          value={answerText}
          onChange={(e) => setAnswerText(e.target.value)}
          placeholder="답변을 입력하세요"
          rows={5}
          style={{ width: "100%" }}
        />
        <div style={{ marginTop: "5px" }}>
          <button onClick={handleSaveAnswer}>저장</button>
        </div>
      </div>

      <div style={{ marginTop: "10px" }}>
        <button onClick={handleDeletePost}>게시글 삭제</button>
        <button onClick={() => navigate("/admin/bbs/qna")}>목록으로</button>
      </div>
    </div>
  );
}
