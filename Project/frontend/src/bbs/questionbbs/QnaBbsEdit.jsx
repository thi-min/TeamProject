// 📁 src/qna/QnaBbsEdit.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./qnabbs.css";

const QnaBbsEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/bbs";

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]);
  const [previewImages, setPreviewImages] = useState({});
  const [loading, setLoading] = useState(true);

  // 게시글 조회
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await axios.get(`${baseUrl}/${id}`);
        const data = res.data;

        setTitle(data.bbsTitle || "");
        setContent(data.bbsContent || "");

        // 기존 첨부파일 세팅
        const existingFiles = data.files || [];
        setFiles(
          existingFiles.map((f) => ({
            id: f.fileNum,
            file: null,
            url: `${baseUrl}/download/${f.fileNum}`,
            name: f.originalName,
            isDeleted: false,
            isNew: false,
            insertOption: "no-insert",
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
      prev.map((f) => (f.id === id ? { ...f, file: newFile, isNew: true } : f))
    );

    if (newFile && ["image/jpeg", "image/jpg"].includes(newFile.type)) {
      const reader = new FileReader();
      reader.onload = (e) =>
        setPreviewImages((prev) => ({ ...prev, [id]: e.target.result }));
      reader.readAsDataURL(newFile);
    } else {
      setPreviewImages((prev) => {
        const updated = { ...prev };
        delete updated[id];
        return updated;
      });
    }
  };

  // 본문 삽입 옵션
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
        reader.onload = (e) =>
          setPreviewImages((prev) => ({ ...prev, [id]: e.target.result }));
        reader.readAsDataURL(file);
      }
    } else {
      setPreviewImages((prev) => {
        const updated = { ...prev };
        delete updated[id];
        return updated;
      });
    }

    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, insertOption: option } : f))
    );
  };

  // 파일 추가
  const addFileInput = () => {
    setFiles((prev) => [
      ...prev,
      {
        id: Date.now(),
        file: null,
        url: null,
        name: "",
        isDeleted: false,
        isNew: true,
        insertOption: "no-insert",
      },
    ]);
  };

  // 파일 제거
  const removeFileInput = (id) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
    setPreviewImages((prev) => {
      const updated = { ...prev };
      delete updated[id];
      return updated;
    });
  };

  // 기존 파일 삭제 토글
  const toggleFileDelete = (index) => {
    setFiles((prev) => {
      const newFiles = [...prev];
      newFiles[index].isDeleted = !newFiles[index].isDeleted;
      return newFiles;
    });
  };

  // 제출
  const handleSubmit = async (e) => {
    e.preventDefault();
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("회원 로그인 후 이용해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("memberNum", memberNum);
    formData.append(
      "bbsDto",
      new Blob([JSON.stringify({ bbsTitle: title, bbsContent: content })], {
        type: "application/json",
      })
    );

    const deletedFileIds = files
      .filter((f) => f.isDeleted && !f.isNew)
      .map((f) => f.id);
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
        />

        {/* 이미지 미리보기 */}
        {Object.keys(previewImages).length > 0 && (
          <div className="bbs-preview-area">
            <div className="bbs-label">본문 삽입 이미지 미리보기</div>
            <div className="bbs-preview-list">
              {Object.entries(previewImages).map(([id, src]) => (
                <img
                  key={id}
                  src={src}
                  alt={`preview-${id}`}
                  style={{ maxWidth: "300px", margin: "10px" }}
                />
              ))}
            </div>
          </div>
        )}

        {/* 첨부파일 */}
        <div className="bbs-file-section">
          <div className="bbs-file-label">첨부파일</div>
          {files.map((f, idx) => (
            <div
              className="bbs-file-row"
              key={f.id}
              style={{ opacity: f.isDeleted ? 0.5 : 1 }}
            >
              {!f.isNew && f.url && !f.isDeleted ? (
                <>
                  <a href={f.url} target="_blank" rel="noreferrer">
                    {f.name}
                  </a>
                  <button
                    type="button"
                    onClick={() => toggleFileDelete(idx)}
                    style={{ marginLeft: 10 }}
                  >
                    삭제
                  </button>
                </>
              ) : (
                <input
                  type="file"
                  onChange={(e) => handleFileChange(f.id, e.target.files[0])}
                  disabled={f.isDeleted}
                />
              )}

              <div className="bbs-file-options">
                <label>
                  <input
                    type="radio"
                    name={`insertOption-${f.id}`}
                    value="insert"
                    checked={f.insertOption === "insert"}
                    onChange={() => handleInsertOptionChange(f.id, "insert")}
                  />
                  본문 삽입
                </label>
                <label>
                  <input
                    type="radio"
                    name={`insertOption-${f.id}`}
                    value="no-insert"
                    checked={f.insertOption === "no-insert"}
                    onChange={() => handleInsertOptionChange(f.id, "no-insert")}
                  />
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
              {f.isDeleted && (
                <span style={{ marginLeft: 8, color: "red" }}>삭제됨</span>
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

        {/* 버튼 */}
        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate(`/bbs/qna/${id}`)}
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
};

export default QnaBbsEdit;
