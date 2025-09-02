import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";

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
    <table className="table type2 responsive border line_td" >
      <colgroup>
        <col style={{ width: "20%" }} />
        <col />
      </colgroup>
      <tbody>
        {/* 대표 이미지 */}
        {repImage && repImage.imagePath && (
          <tr>
            <th scope="row">대표 이미지</th>
            <td>
              {repImage.fileNum ? (
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
              ) : (
                <img
                  src={
                    repImage.imagePath.startsWith("http")
                      ? repImage.imagePath
                      : `${backendUrl}${repImage.imagePath}`
                  }
                  alt={post.bbsTitle}
                  style={{ maxWidth: "500px", marginBottom: "20px" }}
                />
              )}
            </td>
          </tr>
        )}

        {/* 제목 */}
        <tr>
          <th scope="row">제목</th>
          <td>{post.bbsTitle}</td>
        </tr>

        {/* 작성일 및 조회수 */}
        <tr>
          <th scope="row">작성일 / 조회수</th>
          <td>
            {post.registDate ? post.registDate.substring(0, 10) : ""} &nbsp; | &nbsp;
            조회 {post.readCount ?? 0}
          </td>
        </tr>

        {/* 내용 */}
        <tr>
          <th scope="row">내용</th>
          <td>
            <div
              className="bbs-detail-content"
              dangerouslySetInnerHTML={{ __html: post.bbsContent }}
            />
          </td>
        </tr>

        {/* 첨부파일 */}
        {files.length > 0 ? (
          files.map((f, idx) => (
            <tr key={f.fileNum}>
              {idx === 0 && <th rowSpan={files.length}>첨부파일</th>}
              <td>
                <a
                  href={`${backendUrl}/bbs/files/${f.fileNum}/download`}
                  download={f.originalName}
                >
                  {f.extension?.toLowerCase().match(/(jpeg|jpg|png|gif)$/) ? (
                    <img
                      src={
                        f.fileUrl.startsWith("http")
                          ? f.fileUrl
                          : `${backendUrl}${f.fileUrl}`
                      }
                      alt={f.originalName}
                      style={{ maxWidth: "300px" }}
                    />
                  ) : (
                    f.originalName
                  )}
                </a>
              </td>
            </tr>
          ))
        ) : (
          <tr>
            <th scope="row">첨부파일</th>
            <td>첨부파일이 없습니다.</td>
          </tr>
        )}
      </tbody>
    </table>

    {/* 버튼 영역 */}
    <div className="form_center_box ">
          <div className="temp_btn white md">
            <button className="btn" onClick={() => navigate("/bbs/image")}>목록보기</button>
          </div>
          <div className="right_btn_box">
            <div className="temp_btn white md">
              <button className="btn" onClick={handleEdit}>수정</button>
            </div>
            <div className="temp_btn md">
              <button className="btn" onClick={handleDelete}>삭제</button>
            </div>
          </div>
        </div>
  </div>
);
}