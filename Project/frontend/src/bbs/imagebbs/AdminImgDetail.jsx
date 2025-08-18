import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Gallery.css";

export default function AdminImgDetail() {
  const { id } = useParams();
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    axios
      .get(`/bbs/${id}`) // 관리자도 동일 엔드포인트 사용, 백엔드에서 권한 체크
      .then((res) => setPost(res.data))
      .catch((err) => {
        console.error(err);
        alert("게시글 조회 실패");
      });
  }, [id]);

  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await axios.delete(`/admin/bbs/${id}`, { params: { adminId: 1 } }); // 관리자 ID
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
        {/* 수정 버튼 제거, 삭제만 가능 */}
        <button onClick={handleDelete}>삭제</button>
      </div>
    </div>
  );
}
