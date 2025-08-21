import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function QnaBbsWrite() {
  const [bbstitle, setBbstitle] = useState("");
  const [bbscontent, setBbscontent] = useState("");
  const [files, setFiles] = useState([
    { id: Date.now(), file: null, insertOption: "no-insert" },
  ]);
  const [previewImages, setPreviewImages] = useState({}); // { id: base64string }
  const navigate = useNavigate();

  const baseUrl = "http://127.0.0.1:8090/bbs/bbslist/bbsadd";

  // 파일 선택
  const handleFileChange = (id, newFile) => {
    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, file: newFile } : f))
    );

    if (newFile && !["image/jpeg", "image/jpg"].includes(newFile.type)) {
      setFiles((prev) =>
        prev.map((f) => (f.id === id ? { ...f, insertOption: "no-insert" } : f))
      );
      setPreviewImages((prev) => {
        const updated = { ...prev };
        delete updated[id];
        return updated;
      });
    }
  };

  // 본문 삽입 옵션 변경
  const handleInsertOptionChange = (id, option) => {
    const fileObj = files.find((f) => f.id === id);
    const file = fileObj?.file;

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
        setPreviewImages((prev) => ({ ...prev, [id]: e.target.result }));
      };
      reader.readAsDataURL(file);
    } else if (option === "no-insert") {
      setPreviewImages((prev) => {
        const updated = { ...prev };
        delete updated[id];
        return updated;
      });
    }

    // 옵션 상태 업데이트
    setFiles((prev) =>
      prev.map((f) => (f.id === id ? { ...f, insertOption: option } : f))
    );
  };

  const addFileInput = () => {
    setFiles((prev) => [
      ...prev,
      { id: Date.now(), file: null, insertOption: "no-insert" },
    ]);
  };

  const removeFileInput = (id) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
    setPreviewImages((prev) => {
      const updated = { ...prev };
      delete updated[id];
      return updated;
    });
  };

  // 제출
  const handleSubmit = async (e) => {
    e.preventDefault();
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인이 필요합니다.");
      return;
    }

    const formData = new FormData();
    formData.append("memberNum", memberNum);
    formData.append("type", "FAQ");

    formData.append(
      "bbsDto",
      new Blob(
        [
          JSON.stringify({
            bbsTitle: bbstitle,
            bbsContent: bbscontent,
            bulletinType: "FAQ",
          }),
        ],
        { type: "application/json" }
      )
    );

    files.forEach((f) => {
      if (f.file) {
        formData.append("files", f.file);
        formData.append("insertOptions", f.insertOption);
      }
    });

    try {
      await axios.post(baseUrl, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("질문이 등록되었습니다.");
      navigate("/bbs/qna");
    } catch (error) {
      console.error("질문 등록 오류:", error);
      alert("등록 실패: 서버 연결이나 데이터 확인 필요");
    }
  };

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
            value={bbstitle}
            onChange={(e) => setBbstitle(e.target.value)}
            required
          />
        </div>

        {/* 내용 */}
        <div className="bbs-row">
          <div className="bbs-label">내용</div>
          <textarea
            className="bbs-content-input"
            placeholder="내용을 입력해 주세요"
            value={bbscontent}
            onChange={(e) => setBbscontent(e.target.value)}
            required
          />
        </div>

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

        {/* 파일 첨부 */}
        <div className="bbs-row">
          <div className="bbs-label">파일 첨부</div>
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
                      onChange={() =>
                        handleInsertOptionChange(f.id, "no-insert")
                      }
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

        {/* 버튼 */}
        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate("/bbs/qna")}
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

export default QnaBbsWrite;
