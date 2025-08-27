import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Gallery.css";

export default function ImgDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [files, setFiles] = useState([]);
  const [repImage, setRepImage] = useState(null); // 대표 이미지
  const backendUrl = "http://127.0.0.1:8090";

  // 게시글 조회
  const fetchPost = async () => {
    try {
      const res = await axios.get(`${backendUrl}/bbs/${id}`);
      // 백엔드 반환 데이터 구조 확인 후 맞춤
      setPost(res.data.bbs || res.data); 
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
      await axios.delete(`${backendUrl}/bbs/${id}?memberNum=${memberNum}`);
      alert("게시글이 삭제되었습니다.");
      navigate("/imgbbs"); // 목록 페이지 이동
    } catch (error) {
      console.error("삭제 오류:", error);
      alert("게시글 삭제 실패");
    }
  };

  // 게시글 수정 페이지 이동
  const handleEdit = () => {
    navigate(`/bbs/image/edit/${id}`);
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-detail-container">
      {/* 대표 이미지 */}
      {repImage && repImage.imagePath && (
        <div className="bbs-rep-image">
          <img
            src={`${backendUrl}${repImage.imagePath}`} // 절대 경로 사용
            alt="대표 이미지"
            style={{ maxWidth: "500px", marginBottom: "20px" }}
          />
        </div>
      )}

      {/* 제목 */}
      <h2>{post.bbsTitle}</h2>

      {/* 작성일 및 조회수 */}
      <div className="bbs-detail-meta">
        <span>{post.regdate?.substring(0, 10)}</span>
        <span>조회 {post.readcount}</span>
      </div>

      {/* 내용 */}
      <div
        className="bbs-detail-content"
        dangerouslySetInnerHTML={{ __html: post.bbsContent }}
      />

      {/* 첨부파일 */}
      <div className="bbs-detail-files">
        {files.length > 0 ? (
          files.map((f) => (
            <div key={f.fileNum} style={{ marginBottom: "10px" }}>
              {f.fileUrl.match(/\.(jpeg|jpg|gif|png)$/i) ? (
                <img
                  src={`${backendUrl}${f.fileUrl}`}
                  alt={f.originalName}
                  style={{ maxWidth: "300px" }}
                />
              ) : (
                <a
                  href={`${backendUrl}${f.fileUrl}`}
                  download={f.originalName}
                >
                  {f.originalName}
                </a>
              )}
            </div>
          ))
        ) : (
          <div>첨부파일이 없습니다.</div>
        )}
      </div>

      {/* 버튼 영역 */}
      <div style={{ marginTop: "20px" }}>
        <button onClick={() => navigate("/imgbbs")}>목록으로</button>
        <button onClick={handleEdit} style={{ marginLeft: "10px" }}>
          수정
        </button>
        <button onClick={handleDelete} style={{ marginLeft: "10px" }}>
          삭제
        </button>
      </div>
    </div>
  );
}
