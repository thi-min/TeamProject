// 📁 src/admin/NormalBbsEdit.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";
import "./normalbbs.css";

const NormalBbsEdit = () => {
  const { id } = useParams(); // 수정할 게시글 번호
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]); // 기존 파일과 새 파일 관리
  const [insertOptions, setInsertOptions] = useState([]); // 이미지 insert 여부
  const token = localStorage.getItem("accessToken"); // JWT 토큰
  const API_BASE = "http://127.0.0.1:8090/admin/bbs";

  // ---------------- 게시글 단건 조회 ----------------
  useEffect(() => {
    const fetchPost = async () => {
      if (!token) {
        alert("관리자 로그인 후 이용해주세요.");
        navigate("/admin/login");
        return;
      }
      try {
        const res = await api.get(`${API_BASE}/normal/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setTitle(res.data.bbsTitle);
        setContent(res.data.bbsContent);

        const existingFiles = res.data.files || [];
        setFiles(
          existingFiles.map((f) => ({
            id: f.fileNum,
            file: null,
            url: f.fileUrl,
            name: f.originalName,
            isDeleted: false,
          }))
        );

        setInsertOptions(existingFiles.map(() => "insert")); // 기본값 insert
      } catch (error) {
        console.error("게시글 조회 오류:", error);
        alert("게시글 불러오기 실패");
        navigate("/admin/bbs/normal");
      }
    };
    fetchPost();
  }, [id, token, navigate]);

  // ---------------- 첨부파일 관리 ----------------
  const handleFileChange = (index, newFile) => {
    setFiles((prev) => {
      const newFiles = [...prev];
      newFiles[index] = { ...newFiles[index], file: newFile };
      return newFiles;
    });

    setInsertOptions((prev) => {
      const newOptions = [...prev];
      newOptions[index] = "insert";
      return newOptions;
    });
  };

  const addFileInput = () => {
    setFiles((prev) => [
      ...prev,
      { id: Date.now(), file: null, url: null, name: "", isDeleted: false },
    ]);
    setInsertOptions((prev) => [...prev, "insert"]);
  };

  const toggleFileDelete = (index) => {
    setFiles((prev) => {
      const newFiles = [...prev];
      newFiles[index].isDeleted = !newFiles[index].isDeleted;
      return newFiles;
    });

    setInsertOptions((prev) => {
      const newOptions = [...prev];
      newOptions[index] = "no-insert";
      return newOptions;
    });
  };

  // ---------------- 수정 제출 ----------------
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!token) {
      alert("관리자 로그인 후 이용해주세요.");
      navigate("/admin/login");
      return;
    }

    const formData = new FormData();
    formData.append(
      "bbsDto",
      new Blob(
        [JSON.stringify({ bbsTitle: title, bbsContent: content })],
        { type: "application/json" }
      )
    );

    const deletedFileIds = files
      .filter((f) => f.isDeleted && typeof f.id === "number")
      .map((f) => f.id);
    formData.append("deletedFileIds", deletedFileIds.join(","));

    files.forEach((f) => {
      if (f.file) formData.append("files", f.file);
    });

    insertOptions.forEach((opt) => formData.append("insertOptions", opt));

    try {
      await api.put(`${API_BASE}/admin/${id}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });
      alert("게시글 수정 성공!");
      navigate(`/admin/bbs/normal/${id}`);
    } catch (error) {
      console.error("수정 오류:", error.response || error);
      if (error.response?.status === 403) {
        alert("관리자 권한이 없습니다.");
      } else if (error.response?.status === 401) {
        alert("로그인 후 다시 시도해주세요.");
      } else {
        alert("게시글 수정 실패");
      }
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

        {/* 첨부파일 섹션 */}
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

        {/* 버튼 영역 */}
        <div className="bbs-btn-area">
          <button
            type="button"
            className="bbs-cancel-btn"
            onClick={() => navigate(`/admin/bbs/normal/${id}`)}
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
