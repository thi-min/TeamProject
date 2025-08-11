import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./normalbbs.css";

function NormalBbsWrite() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([{ id: Date.now(), file: null }]); // 기본 1개 첨부
  const navigate = useNavigate();

  // 파일 변경
  const handleFileChange = (id, newFile) => {
    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, file: newFile } : f))
    );
  };

  // 파일 입력창 추가
  const addFileInput = () => {
    setFiles((prev) => [...prev, { id: Date.now(), file: null }]);
  };

  // 파일 입력창 삭제
  const removeFileInput = (id) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
  };

  // 저장
  const handleSubmit = async (e) => {
    e.preventDefault();

    const adminId = localStorage.getItem("adminId");
    if (!adminId) {
      alert("관리자 로그인 후 이용해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("adminId", adminId);
    formData.append("type", "NORMAL");
    formData.append(
      "bbsDto",
      new Blob([JSON.stringify({ bbstitle: title, bbscontent: content })], {
        type: "application/json",
      })
    );

    files.forEach((f) => {
      if (f.file) formData.append("files", f.file);
    });

    try {
      await axios.post("/admin/bbs/bbslist/bbsadd", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("공지사항 등록 성공!");
      navigate("/normalbbs");
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
          placeholder="제목을 입력해 주세요"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />

        {/* 내용 */}
        <textarea
          className="bbs-content-input"
          placeholder="내용을 입력해 주세요"
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
                <div className="bbs-file-options">
                  <label>
                   <input type="radio" name={`insertOption-${f.id}`} value="insert" /> 본문 삽입
                  </label>
                  <label>
                    <input type="radio" name={`insertOption-${f.id}`} value="no-insert" /> 본문 미삽입
                  </label>
                </div>
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
            onClick={() => navigate("/admin/notice")}
          >
            취소
          </button>
          <button type="submit" className="bbs-save-btn">
            저장
          </button>
        </div>
      </form>
    </div>
  );
}

export default NormalBbsWrite;
