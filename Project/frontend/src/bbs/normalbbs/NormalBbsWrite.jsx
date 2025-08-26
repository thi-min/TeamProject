// 📁 src/admin/NormalBbsWrite.jsx
import React, { useState, useEffect } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "./normalbbs.css";

function NormalBbsWrite() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([{ id: Date.now(), file: null, insertOption: "no-insert" }]);
  const navigate = useNavigate();

  // 로그인 확인: 서버에 확인
  useEffect(() => {
    const checkLogin = async () => {
      try {
        await api.get("/admin/check-login"); // 로그인 확인용 엔드포인트 필요
      } catch (err) {
        alert("관리자 로그인 후 이용해주세요.");
        navigate("/admin/login");
      }
    };
    checkLogin();
  }, [navigate]);

  // 파일 변경
  const handleFileChange = (id, newFile) => {
    setFiles(prev =>
      prev.map(f => (f.id === id ? { ...f, file: newFile } : f))
    );

    if (newFile && !["image/jpeg", "image/jpg"].includes(newFile.type)) {
      setFiles(prev =>
        prev.map(f => (f.id === id ? { ...f, insertOption: "no-insert" } : f))
      );
    }
  };

  // 본문 삽입 옵션 변경
  const handleInsertOptionChange = (id, option) => {
    const file = files.find(f => f.id === id)?.file;
    if (option === "insert") {
      if (!file) {
        alert("먼저 파일을 선택해주세요.");
        return;
      }
      if (!["image/jpeg", "image/jpg"].includes(file.type)) {
        alert("본문 삽입은 jpg/jpeg 이미지 파일만 가능합니다.");
        return;
      }
    }
    setFiles(prev =>
      prev.map(f => (f.id === id ? { ...f, insertOption: option } : f))
    );
  };

  // 파일 입력창 추가
  const addFileInput = () => {
    setFiles(prev => [...prev, { id: Date.now(), file: null, insertOption: "no-insert" }]);
  };

  // 파일 입력창 삭제
  const removeFileInput = (id) => {
    setFiles(prev => prev.filter(f => f.id !== id));
  };

  // 저장
  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("type", "NORMAL");
    formData.append(
      "bbsDto",
      new Blob([JSON.stringify({ bbstitle: title, bbscontent: content })], { type: "application/json" })
    );

    files.forEach((f, index) => {
      if (f.file) {
        formData.append("files", f.file);
        formData.append(`insertOptions[${index}]`, f.insertOption);
      }
    });

    try {
      await api.post("/admin/bbs/bbslist/bbsadd", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("공지사항 등록 성공!");
      navigate("/admin/notice");
    } catch (error) {
      console.error("등록 오류:", error);
      alert("등록 실패");
    }
  };

  return (
    <div className="bbs-write-container">
      <form className="bbs-write-form" onSubmit={handleSubmit}>
        <input
          type="text"
          className="bbs-title-input"
          placeholder="제목을 입력해 주세요"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />

        <textarea
          className="bbs-content-input"
          placeholder="내용을 입력해 주세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        ></textarea>

        <div className="bbs-file-section">
          <div className="bbs-file-label">파일 첨부</div>
          <div className="bbs-file-list">
            {files.map((f) => (
              <div className="bbs-file-row" key={f.id}>
                <input
                  type="file"
                  accept=".jpg,.jpeg"
                  onChange={(e) => handleFileChange(f.id, e.target.files[0])}
                />
                <div className="bbs-file-options">
                  <label>
                    <input
                      type="radio"
                      name={`insertOption-${f.id}`}
                      value="insert"
                      checked={f.insertOption === "insert"}
                      onChange={() => handleInsertOptionChange(f.id, "insert")}
                    /> 본문 삽입
                  </label>
                  <label>
                    <input
                      type="radio"
                      name={`insertOption-${f.id}`}
                      value="no-insert"
                      checked={f.insertOption === "no-insert"}
                      onChange={() => handleInsertOptionChange(f.id, "no-insert")}
                    /> 본문 미삽입
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
            <button type="button" className="bbs-file-add" onClick={addFileInput}>
              ➕
            </button>
          </div>
        </div>

        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate("/admin/notice")}
          >
            취소
          </button>
          <button type="submit" className="bbs-save-btn">저장</button>
        </div>
      </form>
    </div>
  );
}

export default NormalBbsWrite;
