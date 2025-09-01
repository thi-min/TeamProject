// 📁 src/admin/NormalBbsWrite.jsx
import React, { useState, useEffect, useRef } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "./normalbbs.css";

function NormalBbsWrite() {
  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([{ id: Date.now(), file: null, insertOption: "no-insert" }]);
  const editorRef = useRef(null);
  const navigate = useNavigate();

  const allowedExtensions = ["jpg", "jpeg", "png", "pdf", "ppt", "pptx", "doc", "docx"];
  const allowedMimeTypes = [
    "image/jpeg",
    "image/png",
    "application/pdf",
    "application/vnd.ms-powerpoint",
    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  ];
  const imageMimeTypes = ["image/jpeg", "image/jpg", "image/png"];

  // 로그인 확인
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("관리자 로그인 후 이용해주세요.");
      navigate("/admin/login");
    }
  }, [navigate]);

  // contentEditable placeholder 처리
  useEffect(() => {
    const editor = editorRef.current;
    const placeholder = "내용을 입력해주세요";

    const handleFocus = () => {
      if (editor.innerHTML === placeholder) {
        editor.innerHTML = "";
        editor.classList.remove("placeholder");
      }
    };
    const handleBlur = () => {
      if (editor.innerHTML.trim() === "") {
        editor.innerHTML = placeholder;
        editor.classList.add("placeholder");
      }
    };

    if (editor.innerHTML.trim() === "") {
      editor.innerHTML = placeholder;
      editor.classList.add("placeholder");
    }

    editor.addEventListener("focus", handleFocus);
    editor.addEventListener("blur", handleBlur);

    return () => {
      editor.removeEventListener("focus", handleFocus);
      editor.removeEventListener("blur", handleBlur);
    };
  }, []);

  // 파일 변경
  const handleFileChange = (id, newFile) => {
    if (newFile) {
      const ext = newFile.name.split(".").pop().toLowerCase();
      if (!allowedExtensions.includes(ext) || !allowedMimeTypes.includes(newFile.type)) {
        alert("첨부 불가한 파일입니다.");
        return;
      }
    }

    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, file: newFile } : f))
    );

    // 본문 삽입은 imageMimeTypes만 허용
    if (newFile && !imageMimeTypes.includes(newFile.type)) {
      setFiles((prev) =>
        prev.map((f) =>
          f.id === id && f.insertOption === "insert"
            ? { ...f, insertOption: "no-insert" }
            : f
        )
      );
    }
  };

  // 본문 삽입 옵션 변경
  const handleInsertOptionChange = (id, option) => {
    const file = files.find((f) => f.id === id)?.file;

    if (option === "insert") {
      if (!file) {
        alert("먼저 파일을 선택해주세요.");
        return;
      }
      if (!imageMimeTypes.includes(file.type)) {
        alert("본문 삽입은 jpg, jpeg, png 이미지 파일만 가능합니다.");
        return;
      }

      const alreadyInserted = editorRef.current?.querySelector(`img[data-id='${id}']`);
      if (alreadyInserted) return;

      const reader = new FileReader();
      reader.onload = (e) => {
        const imgTag = `<img src="${e.target.result}" data-id="${id}" style="max-width:600px;" />`;
        if (editorRef.current) {
          const range = document.createRange();
          range.selectNodeContents(editorRef.current);
          range.collapse(false);
          const el = document.createElement("span");
          el.innerHTML = imgTag;
          range.insertNode(el);
          const sel = window.getSelection();
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

    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, insertOption: option } : f))
    );
  };

  // 파일 추가/삭제
  const addFileInput = () => {
    setFiles((prev) => [
      ...prev,
      { id: Date.now(), file: null, insertOption: "no-insert" }
    ]);
  };

  const removeFileInput = (id) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
    if (editorRef.current) {
      const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
      imgs.forEach((img) => img.remove());
    }
  };

  // 제출
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

    // editorRef에 있는 최종 HTML 저장, placeholder 제거
    const editorHTML =
      editorRef.current?.innerHTML === "내용을 입력해주세요"
        ? ""
        : editorRef.current?.innerHTML || "";

    formData.append(
      "bbsDto",
      new Blob(
        [JSON.stringify({ bbsTitle: title, bbsContent: editorHTML })],
        { type: "application/json" }
      )
    );

    // 모든 파일 첨부 (본문 삽입 여부와 무관)
    files.forEach((f) => {
      if (f.file) formData.append("files", f.file);
    });

    try {
      await api.post("/admin/bbs/bbslist/bbsadd", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${token}`
        }
      });
      alert("공지사항 등록 성공!");
      navigate("/admin/bbs/normal");
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
        <input
          type="text"
          className="bbs-title-input"
          placeholder="제목을 입력해 주세요"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />

        <div
          ref={editorRef}
          contentEditable
          className="bbs-content-input placeholder"
          style={{ minHeight: "200px", border: "1px solid #ccc", padding: "10px" }}
        />

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
                    <input
                      type="radio"
                      name={`insertOption-${f.id}`}
                      value="insert"
                      checked={f.insertOption === "insert"}
                      onChange={() => handleInsertOptionChange(f.id, "insert")}
                    />{" "}
                    본문 삽입
                  </label>
                  <label>
                    <input
                      type="radio"
                      name={`insertOption-${f.id}`}
                      value="no-insert"
                      checked={f.insertOption === "no-insert"}
                      onChange={() => handleInsertOptionChange(f.id, "no-insert")}
                    />{" "}
                    본문 미삽입
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
              ➕ 파일 추가
            </button>
          </div>
        </div>

        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate("/admin/bbs/normal")}
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