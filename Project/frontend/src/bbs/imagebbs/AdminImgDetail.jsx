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
    <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">게시판 관리</div>
        </div>
      </div>
    <table className="table type2 responsive border line_td" >
      <colgroup>
        <col style={{ width: "20%" }} />
        <col />
      </colgroup>
      <tbody>
        {/* 제목 */}
        <tr>
          <th scope="row">제목</th>
          <td>{post.bbsTitle}</td>
        </tr>

        {/* 작성일 / 조회수 */}
        <tr>
          <th scope="row">작성일 / 조회수</th>
          <td>
            {post.registDate ? post.registDate.substring(0, 10) : ""} &nbsp;|&nbsp;
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
          files.map((f, idx) => {
            const ext = f.extension?.toLowerCase();
            const isImage = ["jpg", "jpeg", "png"].includes(ext);

            return (
              <tr key={f.fileNum}>
                {/* 첫 번째 파일만 th 출력, rowspan으로 합치기 */}
                {idx === 0 && (
                  <th scope="row" rowSpan={files.length}>
                    첨부파일
                  </th>
                )}
                <td>
                  <a
                    href={`${backendUrl}/admin/bbs/files/${f.fileNum}/download`}
                    download={f.originalName}
                  >
                    {isImage ? (
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
            );
          })
        ) : (
          <tr>
            <th scope="row">첨부파일</th>
            <td>첨부파일이 없습니다.</td>
          </tr>
        )}
      </tbody>
    </table>

    {/* 버튼 영역 (테이블 밖) */}
    <div className="form_center_box">
      <div className="temp_btn white md">
      <button className="btn" onClick={() => navigate("/admin/bbs/image")}>목록으로</button>
      </div>
      <div className="temp_btn md">
      <button className="btn" onClick={handleDelete}>
        삭제
      </button>
      </div>
    </div>
    </div>
  );
}

