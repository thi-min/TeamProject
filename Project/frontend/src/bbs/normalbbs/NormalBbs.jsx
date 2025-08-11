import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function NormalBbsWrite() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    const adminId = localStorage.getItem("adminId");

    const formData = new FormData();
    formData.append("adminId", adminId);
    formData.append("type", "NORMAL");
    formData.append("bbsDto", new Blob([JSON.stringify({ bbstitle: title, bbscontent: content })], {
      type: "application/json",
    }));

    try {
      await axios.post("/admin/bbs/bbslist/bbsadd", formData);
      alert("공지사항 등록 성공!");
      navigate("/normalbbs");
    } catch (error) {
      console.error("등록 오류:", error);
      alert("등록 실패");
    }
  };

  return (
    <div className="notice-container">
      <h2>📄 공지사항 작성</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <input
            type="text"
            placeholder="제목을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        <div>
          <textarea
            placeholder="내용을 입력하세요"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
          ></textarea>
        </div>
        <button type="submit">등록</button>
      </form>
    </div>
  );
}

export default NormalBbsWrite;
