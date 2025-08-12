import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./normalbbs.css";

const NormalBbsEdit = () => {
  const { id } = useParams(); // 수정할 게시글 번호
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]); // 기존 파일과 새 파일 관리

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await axios.get(`/admin/bbs/${id}`);
        setTitle(res.data.bbstitle);
        setContent(res.data.bbscontent);
        const existingFiles = res.data.files || [];
        setFiles(
          existingFiles.map((f) => ({
            id: f.id,
            file: null,
            url: f.url,
            name: f.name,
            isDeleted: false,
          }))
        );
      } catch (error) {
        console.error("게시글 조회 오류:", error);
        alert("게시글 불러오기 실패");
      }
    };
    fetchPost();
  }, [id]);

  const handleFileChange = (index, newFile) => {
    setFiles((prev) => {
      const newFiles = [...prev];
      newFiles[index] = { ...newFiles[index], file: newFile };
      return newFiles;
    });
  };

  const addFileInput = () => {
    setFiles((prev) => [
      ...prev,
      { id: Date.now(), file: null, url: null, name: "", isDeleted: false },
    ]);
  };

  const toggleFileDelete = (index) => {
    setFiles((prev) => {
      const newFiles = [...prev];
      newFiles[index].isDeleted = !newFiles[index].isDeleted;
      return newFiles;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const adminId = localStorage.getItem("adminId");
    if (!adminId) {
      alert("관리자 로그인 후 이용해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("adminId", adminId);
    formData.append(
      "bbsDto",
      new Blob(
        [JSON.stringify({ bbstitle: title, bbscontent: content })],
        { type: "application/json" }
      )
    );

    const deletedFileIds = files
      .filter((f) => f.isDeleted && f.id && typeof f.id === "number")
      .map((f) => f.id);
    formData.append("deletedFileIds", JSON.stringify(deletedFileIds));

    files.forEach((f) => {
      if (f.file) formData.append("files", f.file);
    });

    try {
      await axios.put(`/admin/bbs/${id}?adminId=${adminId}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("공지사항 수정 성공!");
      navigate(`/admin/notice/${id}`);
    } catch (error) {
      console.error("수정 오류:", error);
      alert("수정 실패");
    }
  };

  if (!title && !content) return <div>로딩 중...</div>;

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
          <div className="bbs-file-label">첨부파일</div>

          {files.map((f, idx) => (
            <div
              className="bbs-file-row"
              key={f.id || idx}
              style={{ opacity: f.isDeleted ? 0.5 : 1 }}
            >
              {f.url && !f.isDeleted ? (
                <>
                  <a href={f.url} target="_blank" rel="noreferrer">
                    {f.name}
                  </a>
                  <button
                    type="button"
                    onClick={() => toggleFileDelete(idx)}
                    style={{ marginLeft: "10px" }}
                  >
                    삭제
                  </button>
                </>
              ) : (
                <input
                  type="file"
                  onChange={(e) => handleFileChange(idx, e.target.files[0])}
                  disabled={f.isDeleted}
                />
              )}
            </div>
          ))}

          <button type="button" className="bbs-file-add" onClick={addFileInput}>
            ➕ 파일 추가
          </button>
        </div>

        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate(`/admin/notice/${id}`)}
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

export default NormalBbsEdit;
