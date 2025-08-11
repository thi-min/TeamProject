import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Gallery.css";

export default function Imgwrite() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([{ id: Date.now(), file: null }]);
  const navigate = useNavigate();

  const handleFileChange = (id, newFile) => {
    setFiles(prev =>
      prev.map(f => (f.id === id ? { ...f, file: newFile } : f))
    );
  };

  const addFileInput = () => {
    setFiles(prev => [...prev, { id: Date.now(), file: null }]);
  };

  const removeFileInput = (id) => {
    setFiles(prev => prev.filter(f => f.id !== id));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인이 필요합니다.");
      return;
    }

    const formData = new FormData();
    formData.append(
      "bbsDto",
      new Blob([JSON.stringify({ bbstitle: title, bbscontent: content })], {
        type: "application/json",
      })
    );
    formData.append("memberNum", memberNum);
    formData.append("type", "POTO");

    files.forEach(f => {
      if (f.file) formData.append("files", f.file);
    });

    try {
      await axios.post("/bbs/bbslist/bbsadd", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("등록 완료");
      navigate("/imgbbs");
    } catch (error) {
      console.error("등록 오류:", error);
      alert("등록 실패");
    }
  };

  return (
    <div className="bbs-write-container">
      <form className="bbs-write-form" onSubmit={handleSubmit}>
        {/* 제목 */}
        <input
          type="text"
          className="bbs-title-input"
          placeholder="제목을 입력하세요"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />

        {/* 내용 */}
        <textarea
          className="bbs-content-input"
          placeholder="내용을 입력하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        ></textarea>

        {/* 파일 첨부 */}
        <div className="bbs-file-section">
          <div className="bbs-file-label">파일 첨부</div>
          <div className="bbs-file-list">
            {files.map((f) => (
              <div className="bbs-file-row" key={f.id}>
                <input
                  type="file"
                  onChange={(e) => handleFileChange(f.id, e.target.files[0])}
                />
                {files.length > 1 && (
                  <button
                    type="button"
                    className="bbs-file-remove"
                    onClick={() => removeFileInput(f.id)}
                  >
                    ❌
                  </button>
                )}
              </div>
            ))}
            <button
              type="button"
              className="bbs-file-add"
              onClick={addFileInput}
            >
              ➕
            </button>
          </div>
        </div>

        {/* 버튼 */}
        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate("/imgbbs")}
          >
            취소
          </button>
          <button type="submit" className="bbs-save-btn">
            등록
          </button>
        </div>
      </form>
    </div>
  );
}

