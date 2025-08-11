import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function QnaBbsView() {
  const { id } = useParams(); // bulletinNum
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchPost();
  }, []);

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
      navigate("/qnabbs");
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
      <button onClick={handleDelete}>삭제</button>
    </div>
  );
}

export default QnaBbsView;