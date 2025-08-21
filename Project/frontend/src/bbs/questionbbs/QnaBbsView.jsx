import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function QnaBbsView() {
  const { id } = useParams(); // 게시글 번호
  const [post, setPost] = useState(null);
  const [files, setFiles] = useState([]); // 첨부파일 상태
  const navigate = useNavigate();

  const baseUrl = "http://127.0.0.1:8090/bbs";

  useEffect(() => {
    fetchPost();
    fetchFiles(); // 게시글과 별도로 첨부파일 조회
  }, [id]);

  // ---------------- 게시글 단건 조회 ----------------
  const fetchPost = async () => {
    try {
      const res = await axios.get(`${baseUrl}/${id}`);
      console.log("게시글 데이터:", res.data);
      setPost(res.data);
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
      navigate("/bbs/qna"); // 목록 페이지로 이동
    } catch (error) {
      console.error("삭제 오류:", error);
      alert("삭제 실패");
    }
  };

  // ---------------- 게시글 수정 ----------------
  const handleEdit = () => {
    navigate(`/bbs/qna/edit/${id}`);
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-container">
      <h2>{post.bbsTitle}</h2>

      <div className="bbs-content">
        {/* 본문 HTML 그대로 렌더링 */}
        <div dangerouslySetInnerHTML={{ __html: post.bbsContent }} />
        <p>작성일: {new Date(post.registDate).toLocaleDateString()}</p>
      </div>

      {/* 첨부파일 렌더링 */}
      {files.length > 0 && (
        <div className="bbs-files">
          <h4>첨부파일</h4>
          <ul>
            {files.map((file) => (
              <li key={file.fileNum}>
                {file.savedName.match(/\.(jpeg|jpg|gif|png)$/i) ? (
                  <img
                    src={`${baseUrl}/download/${file.fileNum}`} // 이미지 미리보기
                    alt={file.originalName}
                    style={{ maxWidth: "200px" }}
                  />
                ) : (
                  <a
                    href={`${baseUrl}/download/${file.fileNum}`} // 다운로드 링크
                    download={file.originalName}
                  >
                    {file.originalName}
                  </a>
                )}
              </li>
            ))}
          </ul>
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
