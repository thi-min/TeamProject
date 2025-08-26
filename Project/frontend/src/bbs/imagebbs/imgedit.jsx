// 📁 src/gallery/GalleryEdit.jsx
import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import "./Gallery.css";

export default function GalleryEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef(null);

  const baseUrl = "http://127.0.0.1:8090/bbs";

  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);

  // ---------------- 게시글 조회 ----------------
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await axios.get(`${baseUrl}/${id}`);
        const data = res.data;

        setTitle(data.bbs.bbsTitle || "");
        if (editorRef.current) editorRef.current.innerHTML = data.bbs.bbsContent || "";

        // 기존 파일 처리
        const existingFiles = (data.files || []).map(f => ({
          id: f.fileNum,
          file: null,
          name: f.originalName,
          url: f.fileUrl ? `${baseUrl}${f.fileUrl}` : null,
          isRepresentative: f.isRepresentative === "Y",
          isNew: false,
          isDeleted: false
        }));
        setFiles(existingFiles);

      } catch (error) {
        console.error(error);
        alert("게시글 불러오기 실패");
      } finally {
        setLoading(false);
      }
    };
    fetchPost();
  }, [id]);

  // ---------------- 파일 선택 ----------------
  const handleFileChange = (id, newFile) => {
    if (newFile && !["image/jpeg", "image/jpg"].includes(newFile.type.toLowerCase())) {
      alert("jpg/jpeg 파일만 첨부 가능합니다.");
      return;
    }
    setFiles(prev =>
      prev.map(f => (f.id === id ? { ...f, file: newFile, isNew: true } : f))
    );
  };

  // ---------------- 대표 이미지 선택 ----------------
  const handleRepresentativeChange = (id, value) => {
    if (value) {
      const alreadyRep = files.find(f => f.isRepresentative && f.id !== id);
      if (alreadyRep) {
        alert("대표이미지는 하나만 선택할 수 있습니다.");
        return;
      }
    }
    setFiles(prev =>
      prev.map(f => ({ ...f, isRepresentative: f.id === id ? value : f.isRepresentative }))
    );
  };

  // ---------------- 파일 추가/삭제 ----------------
  const addFileInput = () => setFiles(prev => [...prev, { id: Date.now(), file: null, isRepresentative: false, isNew: true, isDeleted: false }]);
  const removeFileInput = (id) => setFiles(prev => prev.map(f => f.id === id ? { ...f, isDeleted: true } : f));

  // ---------------- 게시글 수정 ----------------
  const handleSubmit = async (e) => {
    e.preventDefault();
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) return alert("로그인이 필요합니다.");

    const formData = new FormData();
    formData.append("memberNum", memberNum);

    const contentHTML = editorRef.current?.innerHTML || "";
    const bbsDtoPayload = { bbsTitle: title, bbsContent: contentHTML, bulletinType: "POTO" };
    formData.append("bbsDto", new Blob([JSON.stringify(bbsDtoPayload)], { type: "application/json" }));

    const deletedFileIds = files.filter(f => f.isDeleted && !f.isNew).map(f => f.id);
    formData.append("deletedFileIds", JSON.stringify(deletedFileIds));

    files.forEach(f => {
      if (f.file && !f.isDeleted) {
        formData.append("files", f.file);
        formData.append("isRepresentative", f.isRepresentative ? "Y" : "N");
      }
    });

    try {
      await axios.put(`${baseUrl}/member/${id}`, formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      alert("게시글이 수정되었습니다.");
      navigate("/imgbbs");
    } catch (error) {
      console.error("수정 오류:", error);
      alert("수정 실패: " + (error.response?.data?.message || "서버 오류"));
    }
  };

  if (loading) return <div>로딩 중...</div>;

  return (
    <div className="bbs-write-container">
      <form className="bbs-write-form" onSubmit={handleSubmit}>
        <div className="bbs-row">
          <div className="bbs-label">제목</div>
          <input
            type="text"
            className="bbs-title-input"
            placeholder="제목을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="bbs-row">
          <div className="bbs-label">내용</div>
          <div
            ref={editorRef}
            contentEditable
            className="bbs-content-input"
            style={{ minHeight: "200px", border: "1px solid #ccc", padding: "10px", whiteSpace: "pre-wrap" }}
          />
        </div>

        <div className="bbs-row">
          <div className="bbs-label">파일 첨부</div>
          <div className="bbs-file-list">
            {files.map(f => !f.isDeleted && (
              <div className="bbs-file-row" key={f.id}>
                <input
                  type="file"
                  accept=".jpg,.jpeg,image/jpeg"
                  onChange={e => handleFileChange(f.id, e.target.files[0])}
                />
                {f.url && !f.file && <a href={f.url} target="_blank" rel="noreferrer">{f.name}</a>}
                <div className="bbs-file-options">
                  <label>
                    <input
                      type="radio"
                      name={`repOption-${f.id}`}
                      checked={f.isRepresentative}
                      onChange={() => handleRepresentativeChange(f.id, true)}
                    /> 대표이미지 삽입
                  </label>
                  <label>
                    <input
                      type="radio"
                      name={`repOption-${f.id}`}
                      checked={!f.isRepresentative}
                      onChange={() => handleRepresentativeChange(f.id, false)}
                    /> 대표이미지 미삽입
                  </label>
                </div>
                {files.length > 1 && (
                  <button type="button" className="bbs-file-remove" onClick={() => removeFileInput(f.id)}>❌</button>
                )}
              </div>
            ))}
            <button type="button" className="bbs-file-add" onClick={addFileInput}>➕ 파일 추가</button>
          </div>
        </div>

        <div className="bbs-btn-area">
          <button type="button" className="bbs-cancel-btn" onClick={() => navigate("/imgbbs")}>취소</button>
          <button type="submit" className="bbs-save-btn">수정</button>
        </div>
      </form>
    </div>
  );
}
