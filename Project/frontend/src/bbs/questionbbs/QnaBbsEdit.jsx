// 📁 src/qna/QnaBbsEdit.jsx
import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./qnabbs.css";

const BACKEND_URL = "http://127.0.0.1:8090"; // 파일 절대경로용

const QnaBbsEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef(null);
  const baseUrl = `${BACKEND_URL}/bbs`;

  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);

  // ---------------- 게시글 조회 ----------------
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await axios.get(`${baseUrl}/${id}`);
        const data = res.data;
        const bbs = data.bbs;
        const existingFiles = data.files || [];

        setTitle(bbs.bbsTitle || "");
        if (editorRef.current) editorRef.current.innerHTML = bbs.bbsContent || "";

        setFiles(
          existingFiles.map(f => ({
            id: f.fileNum,
            file: null,
            name: f.originalName,
            url: `${BACKEND_URL}/bbs/files/${f.fileNum}/download`,
            insertOption: "no-insert",
            isDeleted: false,
            isNew: false
          }))
        );
      } catch (error) {
        console.error("게시글 조회 오류:", error);
        alert("게시글 불러오기 실패");
      } finally {
        setLoading(false);
      }
    };
    fetchPost();
  }, [id]);

  // ---------------- 파일 선택 ----------------
  const handleFileChange = (id, newFile) => {
    setFiles(prev =>
      prev.map(f =>
        f.id === id ? { ...f, file: newFile, isNew: true, url: null, isDeleted: false } : f
      )
    );
  };

  // ---------------- 본문 삽입 옵션 ----------------
  const handleInsertOptionChange = (id, option) => {
    const fileObj = files.find(f => f.id === id);
    const file = fileObj?.file;

    if (option === "insert") {
      if (!file && !fileObj.url) return alert("먼저 파일을 선택해주세요.");
      if (file && !["image/jpeg", "image/jpg"].includes(file.type))
        return alert("본문 삽입은 jpg/jpeg 이미지 파일만 가능합니다.");

      if (editorRef.current) {
        editorRef.current.focus();

        // 커서 위치가 없으면 끝으로 이동
        const sel = window.getSelection();
        if (!sel.rangeCount) {
          const range = document.createRange();
          range.selectNodeContents(editorRef.current);
          range.collapse(false);
          sel.removeAllRanges();
          sel.addRange(range);
        }

        const img = document.createElement("img");
        img.src = file ? URL.createObjectURL(file) : fileObj.url;
        img.dataset.id = id;
        img.style.maxWidth = "600px";

        const range = sel.getRangeAt(0);
        range.insertNode(img);
        range.setStartAfter(img);
        range.setEndAfter(img);
        sel.removeAllRanges();
        sel.addRange(range);

        if (file) URL.revokeObjectURL(img.src);
      }
    } else {
      if (editorRef.current) {
        const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
        imgs.forEach(img => img.remove());
      }
    }

    setFiles(prev =>
      prev.map(f => (f.id === id ? { ...f, insertOption: option } : f))
    );
  };

  // ---------------- 파일 추가/삭제 ----------------
  const addFileInput = () => {
    setFiles(prev => [
      ...prev,
      { id: Date.now(), file: null, name: "", url: null, insertOption: "no-insert", isDeleted: false, isNew: true }
    ]);
  };

  const removeFileInput = id => {
    setFiles(prev =>
      prev.map(f => (f.id === id ? { ...f, isDeleted: true } : f))
    );
    if (editorRef.current) {
      const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
      imgs.forEach(img => img.remove());
    }
  };

  // ---------------- MutationObserver ----------------
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
    if (editorRef.current) observer.observe(editorRef.current, { childList: true, subtree: true });
    return () => observer.disconnect();
  }, []);

  // ---------------- 제출 ----------------
  const handleSubmit = async e => {
    e.preventDefault();
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) return alert("회원 로그인 후 이용해주세요.");

    const contentHTML = editorRef.current?.innerHTML || "";
    const formData = new FormData();

    formData.append("memberNum", new Blob([JSON.stringify(memberNum)], { type: "application/json" }));
    formData.append("bbsDto", new Blob([JSON.stringify({ bbsTitle: title, bbsContent: contentHTML, bulletinType: "FAQ" })], { type: "application/json" }));

    // 삭제된 파일
    const deletedFileIds = files.filter(f => f.isDeleted && !f.isNew).map(f => f.id);
    formData.append("deletedFileIds", new Blob([JSON.stringify(deletedFileIds)], { type: "application/json" }));

    // insertOption 상태
    const insertOptionsList = files.map(f => f.insertOption);
    formData.append("insertOptions", new Blob([JSON.stringify(insertOptionsList)], { type: "application/json" }));

    // 새 파일 첨부
    files.forEach(f => {
      if (f.file && f.isNew && !f.isDeleted) formData.append("files", f.file);
    });

    try {
      await axios.put(`${baseUrl}/member/${id}`, formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      alert("게시글 수정 완료!");
      navigate(`/bbs/qna/${id}`);
    } catch (error) {
      console.error("수정 오류:", error);
      alert("게시글 수정 실패");
    }
  };

  if (loading) return <div>로딩 중...</div>;

  return (
    <div className="bbs-write-container">
      <form className="bbs-write-form" onSubmit={handleSubmit}>
        {/* 제목 */}
        <div className="bbs-row">
          <div className="bbs-label">제목</div>
          <input
            type="text"
            className="bbs-title-input"
            placeholder="제목을 입력해 주세요"
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
        </div>

        {/* 내용 */}
        <div className="bbs-row">
          <div className="bbs-label">내용</div>
          <div
            ref={editorRef}
            contentEditable
            className="bbs-content-input"
            style={{ minHeight: "200px", border: "1px solid #ccc", padding: "10px", whiteSpace: "pre-wrap" }}
          />
        </div>

        {/* 첨부파일 */}
        <div className="bbs-row">
          <div className="bbs-label">첨부파일</div>
          <div className="bbs-file-list">
            {files.map(f => (
              <div key={f.id} className="bbs-file-row" style={{ opacity: f.isDeleted ? 0.5 : 1 }}>
                {!f.isDeleted && f.url && !f.isNew ? (
                  <>
                    <a href={f.url} target="_blank" rel="noreferrer">{f.name}</a>
                    <button type="button" onClick={() => removeFileInput(f.id)}>삭제</button>
                  </>
                ) : (
                  !f.isDeleted && <input type="file" onChange={e => handleFileChange(f.id, e.target.files[0])} />
                )}

                {!f.isDeleted && (
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
                )}

                {f.isDeleted && <span style={{ marginLeft: 8, color: "red" }}>삭제됨</span>}
              </div>
            ))}
            <button type="button" className="bbs-file-add" onClick={addFileInput}>➕ 파일 추가</button>
          </div>
        </div>

        {/* 버튼 */}
        <div className="bbs-btn-area">
          <button type="button" className="bbs-cancel-btn" onClick={() => navigate(`/bbs/qna/${id}`)}>취소</button>
          <button type="submit" className="bbs-save-btn">저장</button>
        </div>
      </form>
    </div>
  );
};

export default QnaBbsEdit;