// src/bbs/imagebbs/AdminImgEdit.jsx
import React, { useState, useRef, useEffect } from "react";
import api from "../../common/api/axios";
import { useNavigate, useParams } from "react-router-dom";

import { Swiper, SwiperSlide } from "swiper/react";
import {
  Navigation,
  Pagination,
  Autoplay,
  A11y,
  EffectFade,
} from "swiper/modules";
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";
import "swiper/css/effect-fade";

export default function AdminImgEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef(null);

  const baseUrl = "http://127.0.0.1:8090/admin/bbs";

  // ✅ 성공/취소 시 이동할 관리자 상세보기 경로
  const goDetail = () => navigate(`/admin/bbs/image/Detail/${id}`);

  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);

  // ================= 게시글 조회 (관리자 전용) =================
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await api.get(`${baseUrl}/poto/${id}`); // GET /admin/bbs/poto/{id}
        const data = res.data;

        const bbs = data?.bbs || {};
        setTitle(bbs.bbsTitle || "");
        if (editorRef.current)
          editorRef.current.innerHTML = bbs.bbsContent || "";

        const repPath =
          data?.representativeImage?.imagePath ||
          data?.representativeImage?.thumbnailPath ||
          "";

        const existingFiles = (data?.files || []).map((f) => {
          const savedName = f.savedName;
          const path = f.path;
          const isRep =
            (!!repPath && !!savedName && repPath.endsWith(savedName)) ||
            (!!repPath && !!path && repPath === path);

          return {
            id: f.fileNum,
            file: null,
            name: f.originalName,
            url: f.fileUrl || null,
            isRepresentative: isRep,
            isNew: false,
            isDeleted: false,
            overwrite: false,
          };
        });

        if (
          !existingFiles.some((f) => f.isRepresentative) &&
          existingFiles.length > 0
        ) {
          existingFiles[0].isRepresentative = true;
        }

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

  // ================= 파일 선택 (새 파일 또는 덮어쓰기) =================
  const handleFileChange = (id, newFile) => {
    if (
      newFile &&
      !["image/jpeg", "image/jpg"].includes(newFile.type.toLowerCase())
    ) {
      alert("jpg/jpeg 파일만 첨부 가능합니다.");
      return;
    }

    setFiles((prev) =>
      prev.map((f) =>
        f.id === id
          ? {
              ...f,
              file: newFile,
              isNew: f.isNew || true,
              overwrite: true,
              name: newFile.name,
            }
          : f
      )
    );
  };

  // ================= 대표 이미지 선택 =================
  const handleRepresentativeChange = (id) => {
    setFiles((prev) =>
      prev.map((f) => ({ ...f, isRepresentative: f.id === id && !f.isDeleted }))
    );
  };

  // ================= 새 파일 추가 =================
  const addFileInput = () => {
    const tempId = `new_${Date.now()}`;
    setFiles((prev) => [
      ...prev,
      {
        id: tempId,
        file: null,
        name: "",
        url: null,
        isRepresentative: prev.filter((f) => !f.isDeleted).length === 0,
        isNew: true,
        isDeleted: false,
        overwrite: false,
      },
    ]);
  };

  // ================= 삭제 처리 =================
  const removeFileInput = (id) => {
    setFiles((prev) => {
      const updated = prev.map((f) =>
        f.id === id ? { ...f, isDeleted: true, isRepresentative: false } : f
      );
      const aliveFiles = updated.filter((f) => !f.isDeleted);
      if (
        !aliveFiles.some((f) => f.isRepresentative) &&
        aliveFiles.length > 0
      ) {
        aliveFiles[0].isRepresentative = true;
      }
      return [...updated];
    });
  };

  // ================= 유틸: ID 배열 → 콤마 문자열(CSV) =================
  const toCsv = (arr) => (arr && arr.length > 0 ? arr.join(",") : "");

  // ================= 제출 (관리자 전용 API 규격) =================
  const handleSubmit = async (e) => {
    e.preventDefault();

    // ✅ 대표 이미지(관리자 API는 숫자 filenum만 허용)
    const aliveFiles = files.filter((f) => !f.isDeleted);
    const repFile = aliveFiles.find((f) => f.isRepresentative);
    if (!repFile) return alert("대표 이미지는 반드시 1장 선택해야 합니다.");

    // ⚠ 신규 파일은 filenum이 없어 대표 지정 불가(서버 제약)
    if (repFile.isNew) {
      alert(
        "현재 서버 로직상 '신규 업로드 파일'은 대표 지정이 불가합니다.\n" +
          "1) 기존 파일 중 하나를 대표로 지정하거나\n" +
          "2) 먼저 저장 후 다시 대표로 지정해 주세요."
      );
      return;
    }

    const formData = new FormData();

    // @RequestParam 규격
    const contentHTML = editorRef.current?.innerHTML || "";
    formData.append("bbsTitle", title);
    formData.append("bbsContent", contentHTML);

    // 삭제/덮어쓰기(CSV)
    const deletedFileIds = files
      .filter((f) => f.isDeleted && !f.isNew)
      .map((f) => f.id);
    const overwriteFileIds = files
      .filter((f) => f.overwrite && !f.isNew)
      .map((f) => f.id);
    formData.append("deletedFileIds", toCsv(deletedFileIds));
    formData.append("overwriteFileIds", toCsv(overwriteFileIds));

    // 신규 파일 업로드
    const newFiles = files.filter((f) => !f.isDeleted && f.isNew && f.file);
    newFiles.forEach((f) => formData.append("files", f.file));

    // 대표 이미지 ID(숫자 문자열)
    formData.append("isRepresentativeList", String(repFile.id));

    try {
      await api.put(`${baseUrl}/poto/${id}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("수정이 완료되었습니다."); // ✅ 성공 알림
      goDetail(); // ✅ 상세보기로 이동
    } catch (error) {
      console.error("수정 오류:", error);
      const msg =
        error?.response?.data?.error ||
        error?.response?.data?.message ||
        "서버 오류";
      alert("수정 실패: " + msg);
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
            {files.map(
              (f) =>
                !f.isDeleted && (
                  <div className="bbs-file-row" key={f.id}>
                    {f.isNew || f.overwrite ? (
                      <input
                        type="file"
                        accept=".jpg,.jpeg,image/jpeg"
                        onChange={(e) =>
                          handleFileChange(f.id, e.target.files[0])
                        }
                      />
                    ) : (
                      <span>
                        {f.name}{" "}
                        <button
                          type="button"
                          onClick={() =>
                            setFiles((prev) =>
                              prev.map((file) =>
                                file.id === f.id
                                  ? { ...file, overwrite: true }
                                  : file
                              )
                            )
                          }
                        >
                          파일 변경
                        </button>
                      </span>
                    )}

                    {f.url && !f.overwrite && (
                      <a href={f.url} target="_blank" rel="noreferrer">
                        보기
                      </a>
                    )}

                    <label
                      title={
                        f.isNew ? "신규 파일은 대표 지정 불가(서버 제약)" : ""
                      }
                    >
                      <input
                        type="radio"
                        checked={f.isRepresentative}
                        onChange={() => handleRepresentativeChange(f.id)}
                        disabled={f.isNew}
                      />{" "}
                      대표이미지
                    </label>

                    <button type="button" onClick={() => removeFileInput(f.id)}>
                      ❌
                    </button>
                  </div>
                )
            )}
            <button type="button" onClick={addFileInput}>
              ➕ 파일 추가
            </button>
          </div>
        </div>

        <div className="bbs-btn-area">
          <button type="button" onClick={goDetail}>
            취소
          </button>
          <button type="submit">수정</button>
        </div>
      </form>
    </div>
  );
}
