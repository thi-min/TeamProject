import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";
import "./Gallery.css";

export default function ImgDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [files, setFiles] = useState([]);
  const [repImage, setRepImage] = useState(null);
  const backendUrl = "http://127.0.0.1:8090";

  // 게시글 상세 조회
  const fetchPost = async () => {
    try {
      const res = await api.get(`${backendUrl}/bbs/${id}`);
      const bbs = res.data.bbs || res.data;

      setPost(bbs);
      setFiles(res.data.files || []);
      setRepImage(res.data.representativeImage || null);
    } catch (error) {
      console.error("상세 조회 실패:", error);
      alert("게시글 상세 조회 실패");
    }
  };

  useEffect(() => {
    fetchPost();
  }, [id]);

  // 게시글 삭제
  const handleDelete = async () => {
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인이 필요합니다.");
      return;
    }
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await api.delete(`${backendUrl}/bbs/${id}?memberNum=${memberNum}`);
      alert("게시글 삭제 성공");
      navigate("/bbs/image");
    } catch (error) {
      console.error("삭제 실패:", error);
      alert("게시글 삭제 실패");
    }
  };

  // 게시글 수정 페이지 이동
  const handleEdit = () => {
    navigate(`/bbs/image/edit/${id}`);
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-container">
      {/* 대표 이미지 */}
      {repImage && repImage.imagePath && (
        <div className="bbs-rep-image">
          {repImage.fileNum ? (
            <a
              href={`${backendUrl}/bbs/files/${repImage.fileNum}/download`}
              download={repImage.originalName || "대표이미지"}
            >
              <img
                src={repImage.imagePath.startsWith("http") ? repImage.imagePath : `${backendUrl}${repImage.imagePath}`}
                alt={post.bbsTitle}
                style={{ maxWidth: "500px", marginBottom: "20px" }}
              />
            </a>
          ) : (
            <img
              src={repImage.imagePath.startsWith("http") ? repImage.imagePath : `${backendUrl}${repImage.imagePath}`}
              alt={post.bbsTitle}
              style={{ maxWidth: "500px", marginBottom: "20px" }}
            />
          )}
        </div>
      )}

      {/* 제목 */}
      <h2>{post.bbsTitle}</h2>

      {/* 작성일 및 조회수 */}
      <div className="bbs-detail-meta">
        <span>{post.registDate ? post.registDate.substring(0, 10) : ""}</span>
        <span>조회 {post.readCount ?? 0}</span>
      </div>

      {/* 내용 */}
      <div
        className="bbs-detail-content"
        dangerouslySetInnerHTML={{ __html: post.bbsContent }}
      />

      {/* 첨부파일 */}
      <div className="bbs-detail-files">
        {files.length > 0 ? (
          files.map((f) => {
            const ext = f.extension?.toLowerCase();
            const isDownloadable = ext === "jpg" || ext === "jpeg"; // 백엔드 허용
            return (
              <div key={f.fileNum} style={{ marginBottom: "10px" }}>
                {isDownloadable ? (
                  <a href={`${backendUrl}/bbs/files/${f.fileNum}/download`} download={f.originalName}>
                    {ext.match(/\.(jpeg|jpg)$/i) ? (
                      <img
                        src={f.fileUrl.startsWith("http") ? f.fileUrl : `${backendUrl}${f.fileUrl}`}
                        alt={f.originalName}
                        style={{ maxWidth: "300px" }}
                      />
                    ) : (
                      f.originalName
                    )}
                  </a>
                ) : (
                  <span>{f.originalName} (다운로드 불가)</span>
                )}
              </div>
            );
          })
        ) : (
          <div>첨부파일이 없습니다.</div>
        )}
      </div>

      {/* 버튼 영역 */}
      <div style={{ marginTop: "20px" }}>
        <button onClick={() => navigate("/bbs/image")}>목록으로</button>
        <button onClick={handleEdit} style={{ marginLeft: "10px" }}>수정</button>
        <button onClick={handleDelete} style={{ marginLeft: "10px" }}>삭제</button>
      </div>
    </div>
  );
}