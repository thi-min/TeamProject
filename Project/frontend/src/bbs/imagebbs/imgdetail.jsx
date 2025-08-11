import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Gallery.css";

export default function Imgdetail() {
  const { id } = useParams();
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get(`/bbs/${id}`).then((res) => setPost(res.data));
  }, [id]);

  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    await axios.delete(`/bbs/${id}`, { params: { memberNum: 1 } });
    alert("삭제 완료");
    navigate("/");
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
        <button onClick={() => navigate(`/bbs/edit/${id}`)}>수정</button>
        <button onClick={handleDelete}>삭제</button>
      </div>
    </div>
  );
}
