import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";

function MemberNormalBbsView() {
  const { id } = useParams(); // 게시글 번호
  const [post, setPost] = useState(null);

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      const res = await axios.get(`/bbs/${id}`); // 회원용 API
      setPost(res.data);
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      alert("게시글 조회 실패");
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
    </div>
  );
}

export default MemberNormalBbsView;