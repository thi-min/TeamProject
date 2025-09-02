// 📁 src/admin/AdminImgDetail.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";

export default function AdminImgDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [files, setFiles] = useState([]);
  const [repImage, setRepImage] = useState(null);

  const baseUrl = "http://127.0.0.1:8090/admin/bbs/poto";
  const backendUrl = "http://127.0.0.1:8090";

  // 게시글 상세 조회
  const fetchPost = async () => {
    try {
      const res = await api.get(`${baseUrl}/${id}`);
      const bbs = res.data.bbs || res.data;

      setPost(bbs);
      setRepImage(res.data.representativeImage || null);

      // 첨부파일 조회
      const fileRes = await api.get(`/admin/bbs/${id}/files`);
      setFiles(fileRes.data || []);
    } catch (err) {
      console.error("상세 조회 실패:", err);
      alert("게시글 조회 실패");
    }
  };

  useEffect(() => {
    fetchPost();
  }, [id]);

  // 게시글 삭제
  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await api.delete(`/admin/bbs/${id}`, { params: { adminId: 1 } });
      alert("삭제 완료");
      navigate("/admin/bbs/image");
    } catch (err) {
      console.error("삭제 실패:", err);
      alert("삭제 실패");
    }
  };

  if (!post) return <div>로딩중...</div>;

  return (
    <div className="bbs-container">
      {/* ✅ 대표 이미지 따로 표시 */}
      {repImage && repImage.imagePath && (
        <div className="bbs-rep-image">
          <a
            href={`${backendUrl}/bbs/files/${repImage.fileNum}/download`}
            download={repImage.originalName || "대표이미지"}
          >
            <img
              src={
                repImage.imagePath.startsWith("http")
                  ? repImage.imagePath
                  : `${backendUrl}${repImage.imagePath}`
              }
              alt={post.bbsTitle}
              style={{ maxWidth: "500px", marginBottom: "20px" }}
            />
          </a>
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

      {/* 첨부파일 (대표이미지 포함 전체 파일 다운로드 가능) */}
      <div className="bbs-detail-files">
        {files.length > 0 ? (
          files.map((f) => {
            const ext = f.extension?.toLowerCase();
            const isImage = ["jpg", "jpeg", "png"].includes(ext);

            return (
              <div key={f.fileNum} style={{ marginBottom: "10px" }}>
                {isImage ? (
                  <a
                    href={`${backendUrl}/admin/bbs/files/${f.fileNum}/download`}
                    download={f.originalName}
                  >
                    <img
                      src={
                        f.fileUrl.startsWith("http")
                          ? f.fileUrl
                          : `${backendUrl}${f.fileUrl}`
                      }
                      alt={f.originalName}
                      style={{ maxWidth: "300px" }}
                    />
                  </a>
                ) : (
                  <a
                    href={`${backendUrl}/admin/bbs/files/${f.fileNum}/download`}
                    download={f.originalName}
                  >
                    {f.originalName}
                  </a>
                )}
              </div>
            );
          })
        ) : (
          <div>첨부파일이 없습니다.</div>
        )}
      </div>

      {/* 버튼 */}
      <div className="detail-actions" style={{ marginTop: "20px" }}>
        <button onClick={() => navigate("/admin/bbs/image")}>목록으로</button>
        <button onClick={handleDelete} style={{ marginLeft: "10px" }}>
          삭제
        </button>
      </div>
    </div>
  );
}
