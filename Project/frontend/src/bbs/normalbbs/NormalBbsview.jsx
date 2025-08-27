// 📁 src/admin/NormalBbsView.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";

function NormalBbsView() {
  const { id } = useParams(); // 게시글 ID
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  const token = localStorage.getItem("accessToken"); // JWT token
  const apiBase = "http://127.0.0.1:8090/admin/bbs/normal"; // Normal 게시글 전용

  // 게시글 조회
  useEffect(() => {
    if (!token) {
      alert("관리자 로그인 후 이용해주세요.");
      navigate("/admin/login");
      return;
    }
    fetchPost();
  }, [id, token, navigate]);

  const fetchPost = async () => {
    try {
      const res = await api.get(`${apiBase}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPost(res.data);
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
        navigate("/admin/login");
      } else if (error.response?.status === 403) {
        alert("권한이 없습니다.");
      } else if (error.response?.status === 404) {
        alert("게시글을 찾을 수 없습니다.");
        navigate("/admin/bbs/normal");
      } else {
        alert("게시글 조회 실패");
      }
    }
  };

  // 게시글 삭제
  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await api.delete(`${apiBase}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("삭제되었습니다.");
      navigate("/admin/bbs/normal"); // 목록 페이지로 이동
    } catch (error) {
      console.error("삭제 오류:", error);
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
        navigate("/admin/login");
      } else if (error.response?.status === 403) {
        alert("권한이 없습니다.");
      } else {
        alert("삭제 실패");
      }
    }
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-container">
      <h2>{post.bbsTitle}</h2>

      <div
        className="bbs-content"
        dangerouslySetInnerHTML={{ __html: post.bbsContent }}
      />

      <p>작성일: {new Date(post.createdAt).toLocaleDateString()}</p>

      {/* 첨부파일 */}
      {post.files && post.files.length > 0 && (
        <div className="bbs-files">
          <h4>첨부파일</h4>
          <ul>
            {post.files.map((file) => (
              <li key={file.id}>
                {file.extension.match(/(jpeg|jpg|gif|png)/i) ? (
                  <img
                    src={`http://127.0.0.1:8090/admin/bbs/files/${file.id}/download`}
                    alt={file.originalName}
                    style={{ maxWidth: "200px" }}
                  />
                ) : (
                  <a
                    href={`http://127.0.0.1:8090/admin/bbs/files/${file.id}/download`}
                    download
                  >
                    {file.originalName}
                  </a>
                )}
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* 삭제 / 수정 버튼 */}
      <div style={{ marginTop: "20px" }}>
        <button onClick={handleDelete}>삭제</button>
        <button
          onClick={() => navigate(`/admin/bbs/normal/edit/${id}`)}
          style={{ marginLeft: "10px" }}
        >
          수정
        </button>
      </div>
    </div>
  );
}

export default NormalBbsView;
