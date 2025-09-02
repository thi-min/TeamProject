import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";

function MemberNormalBbsView() {
  const { id } = useParams(); // 게시글 번호
  const [post, setPost] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      const res = await api.get(`/bbs/${id}`); // 회원용 API
      setPost(res.data);
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      alert("게시글 조회 실패");
    }
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="bbs-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">공지사항</div>
        </div>
      </div>
      <div className="bbs-table">
        {/* line_td - td 텍스트 왼쪽정렬 */}
        {/* img_td - 이미지 + 텍스트 내용 정렬 */}
        <table className="table type2 responsive border line_td" >
          <colgroup>
            <col className="w20p" />
            <col />
          </colgroup>
          <tbody>
            {/* 제목 */}
            <tr>
              <th scope="row">제목</th>
              <td>
                {post.bbs?.bbsTitle}
              </td>
            </tr>

            {/* 내용 */}
            <tr>
              <th>내용</th>
              <td className="img_td">
                <div
                  dangerouslySetInnerHTML={{ __html: post.bbs?.bbsContent }}
                ></div>
              </td>
            </tr>

            {/* 작성일 */}
            <tr>
              <th>작성일</th>
              <td>
                {post.bbs?.registDate
                  ? new Date(post.bbs.registDate).toLocaleDateString()
                  : ""}
              </td>
            </tr>

            {/* 첨부파일 */}
            {post.files && post.files.length > 0 && (
              <tr>
                <th>첨부파일</th>
                <td>
                  {post.files.map((file, index) => (
                    <div className="all_day">
                      <span className="file_name">{file.originalName}</span>
                      <a className="text_btn ico_down" href={file.fileUrl} download>
                        다운로드
                      </a>
                    </div>
                  ))}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      <div className="temp_btn white md">
            <button type="button" className="btn" onClick={() => navigate("/bbs/normal/")}>
            뒤로
            </button>
          </div>
    </div>
  );
}

export default MemberNormalBbsView;