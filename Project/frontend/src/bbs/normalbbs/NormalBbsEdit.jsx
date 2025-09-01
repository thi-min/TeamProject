// 📁 src/admin/NormalBbsEdit.jsx
import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";
import "./normalbbs.css";

const BACKEND_URL = "http://127.0.0.1:8090";
const API_BASE = `${BACKEND_URL}/admin/bbs`;
const FILE_DOWNLOAD = (fileNum) => `${BACKEND_URL}/bbs/files/${fileNum}/download`;

const allowedExtensions = ["jpg", "jpeg", "png", "pdf", "ppt", "pptx", "doc", "docx"];
const allowedMimeTypes = [
  "image/jpeg", "image/png", "application/pdf",
  "application/vnd.ms-powerpoint",
  "application/vnd.openxmlformats-officedocument.presentationml.presentation",
  "application/msword",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
];
const imageMimeTypes = ["image/jpeg", "image/jpg", "image/png"];

const NormalBbsEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef(null);

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);

  const token = localStorage.getItem("accessToken");

  // ---------------- 게시글 + 파일 조회 ----------------
  useEffect(() => {
    if (!token) {
      alert("관리자 로그인 후 이용해주세요.");
      navigate("/admin/login");
      return;
    }

    const fetchData = async () => {
      try {
        const res = await api.get(`${API_BASE}/normal/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const bbsData = res.data.bbs || res.data;
        setTitle(bbsData?.bbsTitle ?? "");
        setContent(bbsData?.bbsContent ?? "");

        const fileRes = await api.get(`${API_BASE}/normal/${id}/files`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const loadedFiles = (fileRes.data || []).map((f) => {
          const fileUrl = FILE_DOWNLOAD(f.fileNum);
          const isInserted = bbsData.bbsContent?.includes(fileUrl);
          return {
            id: f.id ?? f.fileNum,
            file: null,
            name: f.originalName,
            url: fileUrl,
            isDeleted: false,
            isNew: false,
            insertOption: isInserted ? "insert" : f.insertOption ?? "no-insert",
          };
        });
        setFiles(loadedFiles);

      } catch (err) {
        console.error(err);
        alert("게시글 또는 파일 로딩 실패");
        navigate("/admin/bbs/normal");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, token, navigate]);

  // ---------------- contentEditable 초기값 세팅 ----------------
  useEffect(() => {
    if (editorRef.current) editorRef.current.innerHTML = content;
  }, [loading]);

  // ---------------- 본문 내 이미지 삭제 감지 ----------------
  useEffect(() => {
    const editor = editorRef.current;
    if (!editor) return;

    const observer = new MutationObserver(() => {
      setFiles((prev) =>
        prev.map((f) => {
          const imgExists = editor.querySelector(`img[data-id='${f.id}']`);
          if (!imgExists && f.insertOption === "insert") return { ...f, insertOption: "no-insert" };
          return f;
        })
      );
      setContent(editor.innerHTML);
    });

    observer.observe(editor, { childList: true, subtree: true });
    return () => observer.disconnect();
  }, []);

  // ---------------- 첨부파일 관리 ----------------
  const handleFileChange = (fileId, newFile) => {
    if (newFile) {
      const ext = newFile.name.split(".").pop().toLowerCase();
      if (!allowedExtensions.includes(ext) || !allowedMimeTypes.includes(newFile.type)) {
        alert("첨부 불가한 파일입니다.");
        return;
      }
    }

    setFiles((prev) =>
      prev.map((f) =>
        f.id === fileId
          ? {
              ...f,
              file: newFile,
              url: null,
              isDeleted: false,
              isNew: true,
              name: newFile ? newFile.name : f.name,
              insertOption: newFile && !imageMimeTypes.includes(newFile.type) ? "no-insert" : f.insertOption,
            }
          : f
      )
    );
  };

  const addFileInput = () => {
    const newId = Date.now();
    setFiles((prev) => [
      ...prev,
      { id: newId, file: null, url: null, name: "", isDeleted: false, isNew: true, insertOption: "no-insert" },
    ]);
  };

  const toggleFileDelete = (fileId) => {
    setFiles((prev) =>
      prev.map((f) => {
        if (f.id === fileId) {
          if (editorRef.current) {
            const imgs = editorRef.current.querySelectorAll(`img[data-id='${fileId}']`);
            imgs.forEach((img) => img.remove());
            setContent(editorRef.current.innerHTML);
          }
          return { ...f, isDeleted: !f.isDeleted, insertOption: "no-insert" };
        }
        return f;
      })
    );
  };

  const handleInsertOptionChange = (fileId, option) => {
    const fileObj = files.find((f) => f.id === fileId);
    if (!fileObj) return;

    if (option === "insert") {
      if ((!fileObj.file && !fileObj.url) || fileObj.isDeleted) {
        alert("파일을 먼저 선택하거나 삭제되지 않은 상태여야 합니다.");
        return;
      }
      if (fileObj.file && !imageMimeTypes.includes(fileObj.file.type)) {
        alert("본문 삽입은 이미지 파일만 가능합니다.");
        return;
      }

      if (!editorRef.current) return;
      const alreadyInserted = editorRef.current.querySelector(`img[data-id='${fileId}']`);
      if (alreadyInserted) return;

      const insertImgNode = (src) => {
        const img = document.createElement("img");
        img.src = src;
        img.dataset.id = fileId;
        img.style.maxWidth = "600px";
        editorRef.current.appendChild(img);
        setContent(editorRef.current.innerHTML);
      };

      if (fileObj.file) {
        const reader = new FileReader();
        reader.onload = (e) => insertImgNode(e.target.result);
        reader.readAsDataURL(fileObj.file);
      } else if (fileObj.url) {
        insertImgNode(fileObj.url);
      }
    } else {
      if (editorRef.current) {
        const imgs = editorRef.current.querySelectorAll(`img[data-id='${fileId}']`);
        imgs.forEach((img) => img.remove());
        setContent(editorRef.current.innerHTML);
      }
    }

    setFiles((prev) => prev.map((f) => (f.id === fileId ? { ...f, insertOption: option } : f)));
  };

  // ---------------- 제출 ----------------
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!token) {
      alert("관리자 로그인 후 이용해주세요.");
      navigate("/admin/login");
      return;
    }

    const contentHTML = editorRef.current?.innerHTML || "";
    const formData = new FormData();

    formData.append("bbsTitle", title);
    formData.append("bbsContent", contentHTML);

    const deletedIds = files.filter(f => f.isDeleted && !f.isNew).map(f => f.id);
    if (deletedIds.length > 0) formData.append("deletedFileIds", deletedIds.join(","));

    const insertOptions = files.filter(f => !f.isDeleted).map(f => f.insertOption);
    formData.append("insertOptions", insertOptions.join(","));

    files.forEach(f => {
      if (f.isNew && !f.isDeleted && f.file) formData.append("files", f.file);
    });

    try {
      await api.put(`${API_BASE}/normal/${id}`, formData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("게시글 수정 성공!");
      navigate(`/admin/bbs/normal/${id}`);
    } catch (error) {
      console.error(error?.response || error);
      if (error?.response?.status === 403) alert("관리자 권한이 없습니다.");
      else if (error?.response?.status === 401) alert("로그인 후 다시 시도해주세요.");
      else alert("게시글 수정 실패");
    }
  };

  if (loading) return <div>로딩 중...</div>;

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
          className="bbs-content-input"
          style={{ minHeight: "200px", border: "1px solid #ccc", padding: "10px", whiteSpace: "pre-wrap" }}
          onInput={(e) => setContent(e.currentTarget.innerHTML)}
        />
        <div className="bbs-file-section">
          <div className="bbs-file-label">첨부파일</div>
          <div className="bbs-file-list">
            {files.map((f) => (
              <div key={f.id} className="bbs-file-row" style={{ opacity: f.isDeleted ? 0.5 : 1 }}>
                {!f.isDeleted ? (
                  <>
                    {f.name ? (
                      <a href={f.url || "#"} target="_blank" rel="noreferrer">{f.name}</a>
                    ) : (
                      <input type="file" onChange={(e) => handleFileChange(f.id, e.target.files[0])} />
                    )}
                    <button type="button" onClick={() => toggleFileDelete(f.id)} style={{ marginLeft: "10px" }}>
                      {f.isDeleted ? "복원" : "삭제"}
                    </button>
                    <div className="bbs-file-options">
                      <label>
                        <input
                          type="radio"
                          name={`insertOption-${f.id}`}
                          value="insert"
                          checked={f.insertOption === "insert"}
                          onChange={() => handleInsertOptionChange(f.id, "insert")}
                          disabled={!imageMimeTypes.some(m => f.name.toLowerCase().endsWith(m.split('/')[1])) && !f.file}
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
                  </>
                ) : (
                  <span style={{ marginLeft: 8, color: "red" }}>삭제됨</span>
                )}
              </div>
            ))}
            <button type="button" className="bbs-file-add" onClick={addFileInput}>➕ 파일 추가</button>
          </div>
        </div>
        <div className="bbs-btn-area">
          <button type="button" className="bbs-cancel-btn" onClick={() => navigate(`/admin/bbs/normal/${id}`)}>취소</button>
          <button type="submit" className="bbs-save-btn">저장</button>
        </div>
      </form>
    </div>
  );
};

export default NormalBbsEdit;