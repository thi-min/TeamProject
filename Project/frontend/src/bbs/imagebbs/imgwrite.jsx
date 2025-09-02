import React, { useState, useRef } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";

export default function ImgWrite() {
  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([
    { id: Date.now(), file: null, isRepresentative: false },
  ]);
  const editorRef = useRef(null);
  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/bbs/bbslist/bbsadd";

  const handleFileChange = (id, newFile) => {
    if (
      newFile &&
      !["image/jpeg", "image/jpg"].includes(newFile.type.toLowerCase())
    ) {
      alert("jpg/jpeg 파일만 첨부 가능합니다.");
      return;
    }
    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, file: newFile } : f))
    );
  };

  const handleRepresentativeChange = (id, value) => {
    if (value) {
      const alreadyRep = files.find((f) => f.isRepresentative && f.id !== id);
      if (alreadyRep) {
        alert("대표이미지는 하나만 선택할 수 있습니다.");
        return;
      }
    }
    setFiles((prev) =>
      prev.map((f) => ({
        ...f,
        isRepresentative: f.id === id ? value : f.isRepresentative,
      }))
    );
  };

  const addFileInput = () =>
    setFiles((prev) => [
      ...prev,
      { id: Date.now(), file: null, isRepresentative: false },
    ]);
  const removeFileInput = (id) =>
    setFiles((prev) => prev.filter((f) => f.id !== id));

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("handleSubmit 실행됨"); // 테스트
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) return alert("로그인이 필요합니다.");

    if (files.filter((f) => f.file !== null).length === 0)
      return alert("최소 1장 이상의 jpg/jpeg 이미지를 첨부해야 합니다.");

    const formData = new FormData();
    formData.append("memberNum", memberNum);
    formData.append("type", "POTO");

    const contentHTML = editorRef.current?.innerHTML || "";
    const bbsDtoPayload = {
      bbsTitle: title,
      bbsContent: contentHTML,
      bulletinType: "POTO",
    };

    formData.append(
      "bbsDto",
      new Blob([JSON.stringify(bbsDtoPayload)], { type: "application/json" })
    );

    files.forEach((f) => {
      if (f.file) {
        formData.append("files", f.file);
        formData.append("isRepresentative", f.isRepresentative ? "Y" : "N");
      }
    });

    try {
      await api.post(baseUrl, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("게시글이 등록되었습니다.");
      console.log("리스트로 이동 시도");
      navigate("/bbs/imagebbs");
    } catch (error) {
      console.error("등록 오류:", error);
      alert("등록 실패: " + (error.response?.data?.message || "서버 오류"));
    }
  };

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
            style={{
              minHeight: "200px",
              border: "1px solid #ccc",
              padding: "10px",
              whiteSpace: "pre-wrap",
            }}
          />
        </div>

        <div className="bbs-row">
          <div className="bbs-label">파일 첨부</div>
          <div className="bbs-file-list">
            {files.map((f) => (
              <div className="bbs-file-row" key={f.id}>
                <input
                  type="file"
                  accept=".jpg,.jpeg,image/jpeg"
                  onChange={(e) => handleFileChange(f.id, e.target.files[0])}
                />
                <div className="bbs-file-options">
                  <label>
                    <input
                      type="radio"
                      name={`repOption-${f.id}`}
                      checked={f.isRepresentative}
                      onChange={() => handleRepresentativeChange(f.id, true)}
                    />{" "}
                    대표이미지 삽입
                  </label>
                  <label>
                    <input
                      type="radio"
                      name={`repOption-${f.id}`}
                      checked={!f.isRepresentative}
                      onChange={() => handleRepresentativeChange(f.id, false)}
                    />{" "}
                    대표이미지 미삽입
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
            onClick={() => navigate("/bbs/image")}
          >
            취소
          </button>
          <button
            type="submit"
            className="bbs-save-btn"
            onClick={(e) => console.log("등록 버튼 클릭됨")}
          >
            등록
          </button>
        </div>
      </form>
    </div>
  );
}
