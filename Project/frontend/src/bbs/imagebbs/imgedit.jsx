import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Gallery.css";

export default function GalleryEdit() {
  const { id } = useParams();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]); // 기존 첨부파일 목록
  const [representativeImage, setRepresentativeImage] = useState(null);
  const [newRepImage, setNewRepImage] = useState(null); // 교체용 대표 이미지
  const navigate = useNavigate();

  useEffect(() => {
    axios.get(`/bbs/${id}`).then((res) => {
      setTitle(res.data.bbstitle);
      setContent(res.data.bbscontent);
      setFiles(res.data.files || []);
      setRepresentativeImage(res.data.representativeImageUrl);
    });
  }, [id]);

  /** 게시글 기본정보 수정 */
  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    const bbsDto = { bbstitle: title, bbscontent: content };
    formData.append(
      "bbsDto",
      new Blob([JSON.stringify(bbsDto)], { type: "application/json" })
    );
    formData.append("memberNum", 1);

    await axios.put(`/bbs/${id}`, formData, {
      headers: { "Content-Type": "multipart/form-data" }
    });

    alert("게시글 정보 수정 완료");
  };

  /** 대표 이미지 변경 */
  const handleRepImageUpdate = async () => {
    if (!newRepImage) {
      alert("변경할 이미지를 선택하세요.");
      return;
    }

    const formData = new FormData();
    const imageDto = { bulletinNum: id };
    formData.append(
      "imageDto",
      new Blob([JSON.stringify(imageDto)], { type: "application/json" })
    );
    formData.append("imageFile", newRepImage);

    await axios.put(`/bbs/image/${id}`, formData, {
      headers: { "Content-Type": "multipart/form-data" }
    });

    alert("대표 이미지 변경 완료");
    window.location.reload();
  };

  /** 첨부파일 교체 */
  const handleFileReplace = async (fileId, file) => {
    if (!file) return;

    const formData = new FormData();
    const fileDto = { fileId: fileId };
    formData.append(
      "fileDto",
      new Blob([JSON.stringify(fileDto)], { type: "application/json" })
    );
    formData.append("file", file);

    await axios.put(`/bbs/file/${fileId}`, formData, {
      headers: { "Content-Type": "multipart/form-data" }
    });

    alert("첨부파일 교체 완료");
    window.location.reload();
  };

  return (
    <div className="form-container">
      <h2>게시글 수정</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="제목"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <textarea
          placeholder="내용"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        />
        <button type="submit">게시글 저장</button>
      </form>

      {/* 대표 이미지 변경 */}
      <div className="edit-section">
        <h3>대표 이미지 변경</h3>
        {representativeImage && (
          <img
            src={representativeImage}
            alt="대표 이미지"
            className="detail-image"
          />
        )}
        <input
          type="file"
          onChange={(e) => setNewRepImage(e.target.files[0])}
        />
        <button onClick={handleRepImageUpdate}>대표 이미지 교체</button>
      </div>

      {/* 첨부파일 교체 */}
      <div className="edit-section">
        <h3>첨부파일 교체</h3>
        {files.length > 0 ? (
          files.map((f) => (
            <div key={f.fileId} className="file-replace-item">
              <a href={f.downloadUrl} target="_blank" rel="noreferrer">
                {f.originalFileName}
              </a>
              <input
                type="file"
                onChange={(e) =>
                  handleFileReplace(f.fileId, e.target.files[0])
                }
              />
            </div>
          ))
        ) : (
          <p>첨부파일 없음</p>
        )}
      </div>

      <div className="detail-actions">
        <button onClick={() => navigate(`/bbs/${id}`)}>돌아가기</button>
      </div>
    </div>
  );
}