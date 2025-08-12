import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function NormalBbsView() {
  const { id } = useParams(); // 공지사항 게시글 번호
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      const res = await axios.get(`/admin/bbs/${id}`); // API 주소는 백엔드에 맞게 조정하세요
      setPost(res.data);
    } catch (error) {
      console.error("공지사항 조회 오류:", error);
      alert("공지사항 조회 실패");
    }
  };

  const handleDelete = async () => {
    const adminId = localStorage.getItem("adminId");
    if (!adminId) {
      alert("관리자 로그인 후 이용해주세요.");
      return;
    }

    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`/admin/bbs/${id}?adminId=${adminId}`);
      alert("삭제되었습니다.");
      navigate("/admin/notice"); // 목록 페이지 경로에 맞게 수정하세요
    } catch (error) {
      console.error("삭제 오류:", error);
      alert("삭제 실패");
    }
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-container">
      <h2>{post.bbstitle}</h2>

      <div className="bbs-content">
        <p>{post.bbscontent}</p>
        <p>작성일: {new Date(post.createdAt).toLocaleDateString()}</p>
      </div>

      {/* 첨부파일 */}
      {post.files && post.files.length > 0 && (
        <div className="bbs-files">
          <h4>첨부파일</h4>
          <ul>
            {post.files.map((file) => (
              <li key={file.id}>
                {file.url.match(/\.(jpeg|jpg|gif|png)$/i) ? (
                  <img
                    src={file.url}
                    alt={file.name}
                    style={{ maxWidth: "200px" }}
                  />
                ) : (
                  <a href={file.url} download>
                    {file.name}
                  </a>
                )}
              </li>
            ))}
          </ul>
        </div>
      )}

      <button onClick={handleDelete}>삭제</button>

      {/* 수정 버튼 추가 */}
      <button onClick={() => navigate(`/admin/notice/edit/${id}`)} style={{ marginLeft: "10px" }}>
        수정
      </button>
    </div>
  );
}

export default NormalBbsView;
