import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../common/api/axios";

export default function LatestNotice() {
  const [posts, setPosts] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/bbs/latest") // ✅ 백엔드 API
      .then((res) => {
        setPosts(res.data);
      })
      .catch((err) => {
        console.error(err);
        setError("공지사항을 불러오는 중 오류가 발생했습니다.");
      });
  }, []);

  if (error) return <div>{error}</div>;

  return (
    <div className="bbs_box">
      <div className="bbs_title_box">
        <span>공지사항</span>
        <Link to="/bbs/normal" className="bbs_move_btn">
          더보기
        </Link>
      </div>
      <div className="bbs_text_list">
        {posts.map((post) => (
          <div className="bbs_text_item" key={post.bulletinNum}>
            <Link
              to={`/bbs/normal/view/${post.bulletinNum}`}
              className="notice_title"
            >
              <span className="link_title">{post.bbstitle}</span>
              <span className="link_day">{post.registdate}</span>
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}
