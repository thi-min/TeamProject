// src/admin/bbs/imagebbs/AdminImgWrite.jsx
// 목적: "관리자 이미지 게시글 작성 페이지" (레이아웃/클래스는 사용자 작성 페이지와 동일하게 유지)
// 사용 엔드포인트:
//  1) 기본:  POST /admin/bbs/poto               (관리자 전용 생성 API가 준비되어 있을 경우)
//  2) 폴백:  POST /bbs/bbslist/bbsadd           (현재 사용자용 생성 API; 관리자 생성 API가 없을 때 임시 사용)
// 전송 규격(서비스 로직 준수):
//  - memberNum (필수; POTO는 작성 회원 필요)
//  - type = "POTO"
//  - bbsDto(JSON Blob): { bbsTitle, bbsContent, bulletinType: "POTO" }
//  - files[] (jpg/jpeg, image/jpeg, 5MB 이하)
//  - isRepresentative[] (files와 개수/순서 동일, 한 개는 "Y" 나머지는 "N")
// 성공 시 이동: /admin/bbs/image/Detail/{bulletinNum}

import React, { useRef, useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
// 주의: 관리자 폴더에서 common/api까지의 상대경로는 3단계 상위입니다.
import api from "../../common/api/axios";

export default function AdminImgWrite() {
  // 제목/본문
  const [title, setTitle] = useState("");
  const editorRef = useRef(null);

  // 파일 목록 상태
  //  - id: 행 구분용 고유값
  //  - file: 실제 File 객체
  const [files, setFiles] = useState([{ id: Date.now(), file: null }]);

  // 대표 이미지(전역 1개) — 행의 id를 저장
  const [repId, setRepId] = useState(null);

  // 전송 중 중복 제출 방지
  const [submitting, setSubmitting] = useState(false);

  const navigate = useNavigate();

  // ─────────────────────────────────────────────────────────────
  // 유틸: JPEG만 허용 (서비스 정책: jpg/jpeg + image/jpeg + 5MB 이하)
  // ─────────────────────────────────────────────────────────────
  const isAllowedImage = (file) => {
    if (!file) return false;
    const mimeOk = String(file.type || "").toLowerCase() === "image/jpeg";
    const name = String(file.name || "");
    const ext = name.includes(".") ? name.split(".").pop().toLowerCase() : "";
    const extOk = ext === "jpg" || ext === "jpeg";
    const sizeOk = file.size <= 5 * 1024 * 1024;
    return mimeOk && extOk && sizeOk;
  };

  // ─────────────────────────────────────────────────────────────
  // 파일 핸들러
  // ─────────────────────────────────────────────────────────────
  const handleFileChange = (id, file) => {
    if (file && !isAllowedImage(file)) {
      alert("jpg/jpeg 형식, 5MB 이하만 첨부 가능합니다.");
      return;
    }
    setFiles((prev) => prev.map((f) => (f.id === id ? { ...f, file } : f)));
    // 대표 미설정 상태에서 첫 파일이 들어오면 자동 대표로 선정(UX 보조)
    if (!repId && file) setRepId(id);
  };

  const addFileInput = () =>
    setFiles((prev) => [...prev, { id: Date.now(), file: null }]);

  const removeFileInput = (id) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
    // 대표로 선택된 행을 지우면 대표 해제
    if (repId === id) setRepId(null);
  };

  // 현재 유효(파일이 실제 선택된)한 행만 필터링
  const validFiles = useMemo(() => files.filter((f) => !!f.file), [files]);

  // 라디오(전역 1개 그룹) — 해당 행을 대표로 지정
  const chooseRepresentative = (id) => setRepId(id);

  // ─────────────────────────────────────────────────────────────
  // 제출
  // ─────────────────────────────────────────────────────────────
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (submitting) return;

    if (!title.trim()) {
      alert("제목을 입력하세요.");
      return;
    }
    if (validFiles.length === 0) {
      alert("최소 1장 이상의 jpg/jpeg 이미지를 첨부하세요.");
      return;
    }

    // 대표 이미지 보정: 미선택이면 첫 유효 파일을 자동 대표로 지정
    const finalRepId = repId ?? validFiles[0]?.id ?? null;

    // FormData 구성
    const formData = new FormData();
    formData.append("type", "POTO"); // 관례적으로 함께 보냄(서버에서 쓰지 않아도 무해)

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

    // files & isRepresentative (순서/개수 동일하게)
    validFiles.forEach((f) => {
      formData.append("files", f.file);
      formData.append("isRepresentative", f.id === finalRepId ? "Y" : "N");
    });

    // 엔드포인트:
    const ADMIN_CREATE_URL = "/admin/bbs/imgadd";

    try {
      setSubmitting(true);

      // 관리자 전용 생성 호출(라우트가 생겼으므로 단일 호출)
      const res = await api.post(ADMIN_CREATE_URL, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      // 생성된 bulletinNum 파싱(컨트롤러/서비스 반환 형태 대응)
      const data = res?.data || {};
      const newId =
        data?.bulletinNum ??
        data?.id ??
        data?.bbs?.bulletinNum ??
        data?.bbs?.id ??
        null;

      alert("게시글이 등록되었습니다.");
      navigate("/admin/bbs/image");
    } catch (error) {
      // 서비스 예외 메시지 가급적 그대로 노출
      const msg =
        error?.response?.data?.error ||
        error?.response?.data?.message ||
        "서버 오류";
      alert(`등록 실패: ${msg}`);
      console.error("등록 실패:", error);
    } finally {
      setSubmitting(false);
    }
  };

  // ─────────────────────────────────────────────────────────────
  // 렌더 (레이아웃/클래스 구조는 사용자 페이지와 동일)
  //  - .bbs-write-container
  //  - .bbs-write-form
  //  - .bbs-row / .bbs-label / .bbs-title-input / .bbs-content-input
  //  - .bbs-file-list / .bbs-file-row / .bbs-file-options / .bbs-file-remove
  //  - .bbs-btn-area / .bbs-cancel-btn / .bbs-save-btn
  // ─────────────────────────────────────────────────────────────
  return (
    <div className="bbs-write-container">
      <form className="bbs-write-form" onSubmit={handleSubmit}>
        <div className="bbs-row">
          <div className="bbs-label">이름</div>
          <input
            type="text"
            className="bbs-title-input"
            placeholder="이름을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="bbs-row">
          <div className="bbs-label">소개</div>
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
          {/* 백엔드에서 썸네일(300x300)을 생성합니다. 본문에는 /DATA 경로가 그대로 저장됩니다. */}
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
                  {/* 전역 1개 라디오 그룹: name을 동일하게 두어 한 번에 하나만 선택되게 함 */}
                  <label>
                    <input
                      type="radio"
                      name="repOption" // 동일 그룹명(전역 하나)
                      checked={repId === f.id}
                      onChange={() => chooseRepresentative(f.id)}
                    />{" "}
                    대표이미지 삽입
                  </label>

                  <label>
                    <input
                      type="radio"
                      name="repOption"
                      checked={repId !== f.id}
                      onChange={() => setRepId(null)}
                    />{" "}
                    대표이미지 미삽입
                  </label>
                </div>

                {files.length > 1 && (
                  <button
                    type="button"
                    className="bbs-file-remove"
                    onClick={() => removeFileInput(f.id)}
                    title="이 행 삭제"
                  >
                    ❌
                  </button>
                )}
              </div>
            ))}
            <p className="em_b_red">
              * jpg/jpeg 형식, 5MB 이하만 업로드 가능. 대표 이미지는 1개만
              선택됩니다.
            </p>
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
            onClick={() => navigate("/admin/bbs/image")}
            disabled={submitting}
          >
            취소
          </button>
          <button type="submit" className="bbs-save-btn" disabled={submitting}>
            {submitting ? "등록 중..." : "등록"}
          </button>
        </div>
      </form>
    </div>
  );
}
