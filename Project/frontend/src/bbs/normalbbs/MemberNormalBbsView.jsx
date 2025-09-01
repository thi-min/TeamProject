import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../../common/api/axios";

function MemberNormalBbsView() {
  const { id } = useParams(); // 게시글 번호
  const [post, setPost] = useState(null);

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      const res = await api.get(`/bbs/${id}`); // 회원용 API
      setPost(res.data);
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      alert("게시글 조회 실패");
    }
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-container">
      <h2>{post.bbs?.bbsTitle}</h2>

      <div className="bbs-content">
        {/* HTML 렌더링 적용 */}
        <div
          dangerouslySetInnerHTML={{ __html: post.bbs?.bbsContent }}
        ></div>

        <p>
          작성일:{" "}
          {post.bbs?.registDate
            ? new Date(post.bbs.registDate).toLocaleDateString()
            : ""}
        </p>
      </div>

      {/* 첨부파일 */}
      {post.files && post.files.length > 0 && (
        <div className="bbs-files">
          <h4>첨부파일</h4>
          <ul>
            {post.files.map((file) => (
              <li key={file.fileNum}>
                {file.fileUrl.match(/\.(jpeg|jpg|gif|png)$/i) ? (
                  <img
                    src={file.fileUrl} // 이미 백엔드에서 BASE_URL 포함됨
                    alt={file.originalName}
                    style={{ maxWidth: "200px" }}
                  />
                ) : (
                  <a href={file.fileUrl} download>
                    {file.originalName}
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