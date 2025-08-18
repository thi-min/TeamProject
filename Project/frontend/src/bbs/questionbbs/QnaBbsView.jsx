import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function QnaBbsView() {
  const { id } = useParams(); // 게시글 번호 (bulletinNum)
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchPost();
  }, [id]); // id가 바뀔 때도 다시 호출하도록 변경

  const fetchPost = async () => {
    try {
      const res = await axios.get(`/bbs/${id}`);
      setPost(res.data);
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      alert("조회 실패");
    }
  };

  const handleDelete = async () => {
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인이 필요합니다.");
      return;
    }

    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`/bbs/${id}?memberNum=${memberNum}`);
      alert("삭제되었습니다.");
      navigate("/bbs/qna"); // 목록 페이지 경로 맞춰 수정
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

      {/* 첨부파일이 있을 때만 보여주기 */}
      {post.files && post.files.length > 0 && (
        <div className="bbs-files">
          <h4>첨부파일</h4>
          <ul>
            {post.files.map((file) => (
              <li key={file.id}>
                {/* 이미지 파일이라면 미리보기 */}
                {file.url.match(/\.(jpeg|jpg|gif|png)$/) ? (
                  <img src={file.url} alt={file.name} style={{ maxWidth: "200px" }} />
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
    </div>
  );
}

export default QnaBbsView;