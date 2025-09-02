import React, { useState, useRef, useEffect } from "react";
import api from "../../common/api/axios";
import { useNavigate, useParams } from "react-router-dom";

//swiper
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

export default function GalleryEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef(null);
  const baseUrl = "http://127.0.0.1:8090/bbs";

  const [title, setTitle] = useState("");
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);
  // ================= 게시글 조회 =================
  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await api.get(`${baseUrl}/${id}`);
        const data = res.data;

        setTitle(data.bbs.bbsTitle || "");
        if (editorRef.current)
          editorRef.current.innerHTML = data.bbs.bbsContent || "";

        const existingFiles = (data.files || []).map((f) => ({
          id: f.fileNum, // 기존 파일은 DB filenum
          file: null,
          name: f.originalName,
          url: f.fileUrl || null,
          isRepresentative: f.isRepresentative === "Y",
          isNew: false,
          isDeleted: false,
          overwrite: false,
        }));

        // 대표 이미지 없으면 첫번째 파일을 대표로 설정
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

  // ================= 제출 =================
  // 제출
  const handleSubmit = async (e) => {
    e.preventDefault();
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) return alert("로그인이 필요합니다.");

    const aliveFiles = files.filter((f) => !f.isDeleted);
    const repFile = aliveFiles.find((f) => f.isRepresentative);
    if (!repFile) return alert("대표 이미지는 반드시 1장 선택해야 합니다.");

    const formData = new FormData();

    // ✅ bbsDto는 JSON + Blob
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

    // ✅ memberNum은 그냥 문자열 (Blob ❌)
    formData.append("memberNum", memberNum);

    // ✅ 삭제된 기존 파일 (JSON.stringify)
    const deletedFileIds = files
      .filter((f) => f.isDeleted && !f.isNew)
      .map((f) => f.id);
    formData.append("deletedFileIds", JSON.stringify(deletedFileIds));

    // ✅ 덮어쓰기 파일 (JSON.stringify)
    const overwriteFileIds = files
      .filter((f) => f.overwrite && !f.isNew)
      .map((f) => f.id);
    formData.append("overwriteFileIds", JSON.stringify(overwriteFileIds));

    // ✅ 새 파일 업로드
    const newFiles = files.filter((f) => !f.isDeleted && f.isNew && f.file);
    newFiles.forEach((f) => formData.append("files", f.file));

    // ✅ 대표 이미지 ID (문자열 그대로)
    formData.append("isRepresentativeList", repFile.id.toString());

    try {
      await api.put(`${baseUrl}/member/${id}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("게시글이 수정되었습니다.");
      navigate(`/bbs/image/${id}`);
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

                    <label>
                      <input
                        type="radio"
                        checked={f.isRepresentative}
                        onChange={() => handleRepresentativeChange(f.id)}
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
          <button type="button" onClick={() => navigate(`/bbs/image/${id}`)}>
            취소
          </button>
          <button type="submit">수정</button>
        </div>
      </form>
    </div>
  );
}
