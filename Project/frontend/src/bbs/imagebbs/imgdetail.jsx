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
    <div className="form_item type2 bbs_form">
      <div className="img_bbs_wrap">
        <div className="img_slide_box">
          <div className="slide_list">
            <div className="slide_item">슬라이드 아이템</div>
          </div>
        </div>
        <div className="text_box">
          <div className="text_item">
            <span className="t1">제목</span>
            <span className="t2">{post.bbsTitle}</span>
          </div>
          <div className="text_item">
            <span className="t1">내용</span>
            <span
              className="t2"
              dangerouslySetInnerHTML={{ __html: post.bbsContent }}
            />
          </div>
          <div className="text_item">
            <span className="t1">작성일</span>
            <span className="t2">
              {post.registDate ? post.registDate.substring(0, 10) : ""}
            </span>
          </div>
        </div>
      </div>
      <div className="form_center_box ">
        <div className="temp_btn white md">
          <button className="btn" onClick={() => navigate("/bbs/image")}>
            목록보기
          </button>
        </div>
        <div className="right_btn_box">
          <div className="temp_btn white md">
            <button className="btn" onClick={handleEdit}>
              수정
            </button>
          </div>
          <div className="temp_btn md">
            <button className="btn" onClick={handleDelete}>
              삭제
            </button>
          </div>
        </div>
      </div>
    </div>
    // {files.length > 0 ? (
    //   files.map((f, idx) => (
    //     <tr key={f.fileNum}>
    //       {idx === 0 && <th rowSpan={files.length}>첨부파일</th>}
    //       <td>
    //         <a
    //           href={`${backendUrl}/bbs/files/${f.fileNum}/download`}
    //           download={f.originalName}
    //         >
    //           {f.extension?.toLowerCase().match(/(jpeg|jpg|png|gif)$/) ? (
    //             <img
    //               src={
    //                 f.fileUrl.startsWith("http")
    //                   ? f.fileUrl
    //                   : `${backendUrl}${f.fileUrl}`
    //               }
    //               alt={f.originalName}
    //               style={{ maxWidth: "300px" }}
    //             />
    //           ) : (
    //             f.originalName
    //           )}
    //         </a>
    //       </td>
    //     </tr>
    //   ))
    // ) : (
    //   <tr>
    //     <th scope="row">첨부파일</th>
    //     <td>첨부파일이 없습니다.</td>
    //   </tr>
    // )}
  );
}
