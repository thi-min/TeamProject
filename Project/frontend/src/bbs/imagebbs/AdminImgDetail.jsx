// 📁 src/admin/AdminImgDetail.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";
import "./Gallery.css";

export default function AdminImgDetail() {
  const { id } = useParams();
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  // 🔹 관리자 이미지 게시글 상세 조회 URL
  const baseUrl = "http://127.0.0.1:8090/admin/bbs/poto";

  useEffect(() => {
    api
      .get(`${baseUrl}/${id}`) // 관리자 상세 조회용 엔드포인트
      .then((res) => setPost(res.data))
      .catch((err) => {
        console.error(err);
        alert("게시글 조회 실패");
      });
  }, [id]);

  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await api.delete(`/admin/bbs/${id}`, { params: { adminId: 1 } }); // 관리자 ID
      alert("삭제 완료");
      navigate("/admin/imgboard"); // 관리자 목록 페이지로 이동
    } catch (err) {
      console.error(err);
      alert("삭제 실패");
    }
  };

  if (!post) return <div>로딩중...</div>;

  return (
    <div className="detail-container">
      <img
        className="detail-image"
        src={post.representativeImageUrl}
        alt={post.bbstitle}
      />
      <h3>{post.bbstitle}</h3>
      <p>{post.bbscontent}</p>

      <div className="detail-files">
        <h4>첨부파일</h4>
        {post.files?.length ? (
          post.files.map((f, idx) => (
            <div key={idx}>
              <a href={f.downloadUrl} target="_blank" rel="noreferrer">
                {f.originalFileName}
              </a>
            </div>
          ))
        ) : (
          <p>첨부파일 없음</p>
        )}
      </div>

      <div className="detail-actions">
        <button onClick={handleDelete}>삭제</button>
      </div>
    </div>
  );
}
