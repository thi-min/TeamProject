// 📁 src/admin/NormalBbsWrite.jsx
import React, { useState, useEffect, useRef } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "./normalbbs.css";

function NormalBbsWrite() {
  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([{ id: Date.now(), file: null, insertOption: "no-insert" }]);
  const editorRef = useRef(null); // 본문 내용 ref
  const navigate = useNavigate();

  // 로그인 확인 (accessToken 기준)
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("관리자 로그인 후 이용해주세요.");
      navigate("/admin/login");
    }
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

      const reader = new FileReader();
      reader.onload = (e) => {
        const imgTag = `<img src="${e.target.result}" data-id="${id}" style="max-width:600px;" />`;

        if (editorRef.current) {
          editorRef.current.focus();
          const sel = window.getSelection();
          if (!sel.rangeCount) return;
          const range = sel.getRangeAt(0);

          const el = document.createElement("span");
          el.innerHTML = imgTag;
          range.insertNode(el);

          range.setStartAfter(el);
          range.setEndAfter(el);
          sel.removeAllRanges();
          sel.addRange(range);
        }
      };
      reader.readAsDataURL(file);
    } else {
      if (editorRef.current) {
        const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
        imgs.forEach((img) => img.remove());
      }
    }

    setFiles(prev =>
      prev.map(f => (f.id === id ? { ...f, insertOption: option } : f))
    );
  };

  // 파일 input 추가/삭제
  const addFileInput = () => {
    setFiles(prev => [...prev, { id: Date.now(), file: null, insertOption: "no-insert" }]);
  };

  const removeFileInput = (id) => {
    setFiles(prev => prev.filter(f => f.id !== id));
    if (editorRef.current) {
      const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
      imgs.forEach((img) => img.remove());
    }
  };

  // MutationObserver로 이미지 삭제 감지
  useEffect(() => {
    const observer = new MutationObserver(() => {
      setFiles(prevFiles =>
        prevFiles.map(f => {
          if (f.insertOption === "insert") {
            const imgExists = editorRef.current?.querySelector(`img[data-id='${f.id}']`);
            if (!imgExists) return { ...f, insertOption: "no-insert" };
          }
          return f;
        })
      );
    });

    if (editorRef.current) {
      observer.observe(editorRef.current, { childList: true, subtree: true });
    }

    return () => observer.disconnect();
  }, []);

  // 저장
  const handleSubmit = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("관리자 로그인 후 이용해주세요.");
      navigate("/admin/login");
      return;
    }

    const formData = new FormData();
    formData.append("type", "NORMAL");

    const contentHTML = editorRef.current?.innerHTML || "";

    formData.append(
      "bbsDto",
      new Blob([JSON.stringify({ bbsTitle: title, bbsContent: contentHTML })], { type: "application/json" })
    );

    files.forEach((f, index) => {
      if (f.file) {
        formData.append("files", f.file);
        formData.append(`insertOptions[${index}]`, f.insertOption);
      }
    });

    try {
      await api.post("/admin/bbs/bbslist/bbsadd", formData, {
        headers: { 
          "Content-Type": "multipart/form-data",
          "Authorization": `Bearer ${token}`
        },
      });
      alert("공지사항 등록 성공!");
      navigate("/admin/bbs/normal"); // 글 저장 후 목록 이동
    } catch (error) {
      console.error("등록 오류:", error);
      if (error.response?.status === 401) {
        alert("인증 실패: 로그인 정보가 만료되었거나 잘못되었습니다.");
        navigate("/admin/login");
      } else if (error.response?.status === 403) {
        alert("권한이 없습니다.");
      } else {
        alert("등록 실패");
      }
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
        <div
          ref={editorRef}
          contentEditable
          className="bbs-content-input"
          style={{ minHeight: "200px", border: "1px solid #ccc", padding: "10px" }}
        />

        {/* 파일 첨부 */}
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
              ➕ 파일 추가
            </button>
          </div>
        </div>

        {/* 버튼 */}
        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate("/admin/bbs/normal")}
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
