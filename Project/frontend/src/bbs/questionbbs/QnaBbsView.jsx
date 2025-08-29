import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function QnaBbsView() {
  const { id } = useParams();
  const [post, setPost] = useState(null);   // 게시글 + 답변 포함
  const [files, setFiles] = useState([]);
  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/bbs";

  useEffect(() => {
    fetchPost();
    fetchFiles();
  }, [id]);

  // ---------------- 게시글 조회 ----------------
  const fetchPost = async () => {
    try {
      const res = await axios.get(`${baseUrl}/${id}`);
      console.log("게시글 데이터:", res.data);
      setPost(res.data); // { bbs: {...}, answer: "관리자 답변" }
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      alert("게시글 조회 실패");
    }
  };

  // ---------------- 첨부파일 조회 ----------------
  const fetchFiles = async () => {
    try {
      const res = await axios.get(`${baseUrl}/${id}/files`);
      console.log("첨부파일 데이터:", res.data);
      setFiles(res.data);
    } catch (error) {
      console.error("첨부파일 조회 오류:", error);
    }
  };

  // ---------------- 게시글 삭제 ----------------
  const handleDelete = async () => {
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인이 필요합니다.");
      return;
    }
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`${baseUrl}/${id}?memberNum=${memberNum}`);
      alert("삭제되었습니다.");
      navigate("/bbs/qna");
    } catch (error) {
      console.error("삭제 오류:", error);
      alert("삭제 실패");
    }
  };

  // ---------------- 게시글 수정 이동 ----------------
  const handleEdit = () => {
    navigate(`/bbs/qna/edit/${id}`);
  };

  if (!post) return <div>로딩 중...</div>;

  const bbs = post.bbs || {};

  return (
    <div className="bbs-container">
      <h2>{bbs.bbsTitle}</h2>

      <div className="bbs-content">
        <div dangerouslySetInnerHTML={{ __html: bbs.bbsContent }} />
        <p>
          작성일: {bbs.registDate ? new Date(bbs.registDate).toLocaleDateString() : ""}
        </p>
      </div>

      {/* 첨부파일 */}
      {files.length > 0 && (
        <div className="bbs-files">
          <h4>첨부파일</h4>
          <ul>
            {files.map((file) => (
              <li key={file.fileNum}>
                <a
                  href={`${baseUrl}/files/${file.fileNum}/download`}
                  download={file.originalName}
                >
                  {file.originalName}
                </a>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* 관리자 답변 표시 */}
      {post.answer && (
        <div className="answer-section">
          <h4>관리자 답변</h4>
          <p>{post.answer}</p>
        </div>
      )}

      <div className="bbs-btn-area">
        <button onClick={handleEdit}>수정</button>
        <button onClick={handleDelete}>삭제</button>
      </div>
    </div>
  );
}

export default QnaBbsView;
