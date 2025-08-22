// 📁 src/qna/QnaBbsEdit.jsx
import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./qnabbs.css";

const QnaBbsEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef(null);
  const baseUrl = "http://127.0.0.1:8090/bbs";

  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);

  // 게시글 조회
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await axios.get(`${baseUrl}/${id}`);
        const data = res.data;

        setTitle(data.bbsTitle || "");
        if (editorRef.current) editorRef.current.innerHTML = data.bbsContent || "";

        const existingFiles = data.files || [];
        setFiles(
          existingFiles.map((f) => ({
            id: f.fileNum,
            file: null,
            url: `${baseUrl}/download/${f.fileNum}`,
            name: f.originalName,
            insertOption: "no-insert",
            isDeleted: false,
            isNew: false,
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

  // 파일 선택
  const handleFileChange = (id, newFile) => {
    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, file: newFile, isNew: true, url: null } : f))
    );
  };

  // 본문 삽입
  const handleInsertOptionChange = (id, option) => {
    const fileObj = files.find((f) => f.id === id);
    const file = fileObj?.file;

    if (option === "insert") {
      if (!file && !fileObj.url) {
        alert("먼저 파일을 선택해주세요.");
        return;
      }

      if (file && !["image/jpeg", "image/jpg"].includes(file.type)) {
        alert("본문 삽입은 jpg/jpeg 이미지 파일만 가능합니다.");
        return;
      }

      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          if (editorRef.current) {
            const sel = window.getSelection();
            if (!sel.rangeCount) return;
            const range = sel.getRangeAt(0);
            const imgTag = `<img src="${e.target.result}" data-id="${id}" style="max-width:600px;" />`;
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
      }
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

  // 파일 추가
  const addFileInput = () => {
    setFiles((prev) => [
      ...prev,
      { id: Date.now(), file: null, url: null, name: "", insertOption: "no-insert", isDeleted: false, isNew: true },
    ]);
  };

  // 파일 제거
  const removeFileInput = (id) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
    if (editorRef.current) {
      const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
      imgs.forEach((img) => img.remove());
    }
  };

  // MutationObserver로 contentEditable 변화를 감지
  useEffect(() => {
    const observer = new MutationObserver(() => {
      setFiles((prevFiles) =>
        prevFiles.map((f) => {
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

  // 제출
  const handleSubmit = async (e) => {
    e.preventDefault();
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("회원 로그인 후 이용해주세요.");
      return;
    }

    const contentHTML = editorRef.current?.innerHTML || "";
    const formData = new FormData();
    formData.append("memberNum", memberNum);
    formData.append(
      "bbsDto",
      new Blob([JSON.stringify({ bbsTitle: title, bbsContent: contentHTML })], { type: "application/json" })
    );

    const deletedFileIds = files.filter((f) => f.isDeleted && !f.isNew).map((f) => f.id);
    formData.append("deletedFileIds", JSON.stringify(deletedFileIds));

    files.forEach((f) => {
      if (f.file && f.isNew) formData.append("files", f.file);
      formData.append("insertOptions", f.insertOption);
    });

    try {
      await axios.put(`${baseUrl}/member/${id}?memberNum=${memberNum}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("게시글 수정 성공!");
      navigate(`/bbs/qna/${id}`);
    } catch (error) {
      console.error("수정 오류:", error);
      alert("수정 실패");
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
            onChange={(e) => setTitle(e.target.value)}
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
          <div className="bbs-label">파일 첨부</div>
          <div className="bbs-file-list">
            {files.map((f) => (
              <div className="bbs-file-row" key={f.id} style={{ opacity: f.isDeleted ? 0.5 : 1 }}>
                {f.url && !f.isNew && !f.isDeleted ? (
                  <>
                    <a href={f.url} target="_blank" rel="noreferrer">{f.name}</a>
                    <button type="button" onClick={() => {
                      setFiles(prev => prev.map(file => file.id === f.id ? { ...file, isDeleted: true } : file));
                      if (editorRef.current) {
                        const imgs = editorRef.current.querySelectorAll(`img[data-id='${f.id}']`);
                        imgs.forEach(img => img.remove());
                      }
                    }}>삭제</button>
                  </>
                ) : (
                  <input type="file" onChange={(e) => handleFileChange(f.id, e.target.files[0])} disabled={f.isDeleted} />
                )}

                <div className="bbs-file-options">
                  <label>
                    <input type="radio" name={`insertOption-${f.id}`} value="insert"
                      checked={f.insertOption === "insert"}
                      onChange={() => handleInsertOptionChange(f.id, "insert")} />
                    본문 삽입
                  </label>
                  <label>
                    <input type="radio" name={`insertOption-${f.id}`} value="no-insert"
                      checked={f.insertOption === "no-insert"}
                      onChange={() => handleInsertOptionChange(f.id, "no-insert")} />
                    본문 미삽입
                  </label>
                </div>

                {files.length > 1 && <button type="button" className="bbs-file-remove" onClick={() => removeFileInput(f.id)}>❌</button>}
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
