import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function QnaBbsView() {
  const { id } = useParams();
  const [post, setPost] = useState(null); // 게시글 + 답변 포함
  const [files, setFiles] = useState([]);
  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/bbs";

  useEffect(() => {
    fetchPost();
    fetchFiles();
  }, [id]);

  // ---------------- 게시글 조회 ----------------
  const fetchPost = async () => {
    try {
      const res = await axios.get(`${baseUrl}/${id}`);
      console.log("게시글 데이터:", res.data);
      setPost(res.data); // { bbs: {...}, answer: "관리자 답변" }
    } catch (error) {
      console.error("게시글 조회 오류:", error);
      alert("게시글 조회 실패");
    }
  };

  // ---------------- 첨부파일 조회 ----------------
  const fetchFiles = async () => {
    try {
      const res = await axios.get(`${baseUrl}/${id}/files`);
      console.log("첨부파일 데이터:", res.data);
      setFiles(res.data);
    } catch (error) {
      console.error("첨부파일 조회 오류:", error);
    }
  };

  // ---------------- 게시글 삭제 ----------------
  const handleDelete = async () => {
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인이 필요합니다.");
      return;
    }
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`${baseUrl}/${id}?memberNum=${memberNum}`);
      alert("삭제되었습니다.");
      navigate("/bbs/qna");
    } catch (error) {
      console.error("삭제 오류:", error);
      alert("삭제 실패");
    }
  };

  // ---------------- 게시글 수정 이동 ----------------
  const handleEdit = () => {
    navigate(`/bbs/qna/edit/${id}`);
  };

  if (!post) return <div>로딩 중...</div>;

  const bbs = post.bbs || {};

  return (
    <div className="bbs-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">Q&A 게시판</div>
        </div>


        <div className="bbs-table">
          <table className="table type2 responsive border line_td" >
            <colgroup>
              <col className="w20p" />
              <col />
            </colgroup>
            <tbody>
              <tr>
                <th scope="row">제목</th>
                <td>
                  {bbs.bbsTitle}
                </td>
              </tr>
              <tr>
                <th>작성일</th>
                <td>{bbs.registDate ? new Date(bbs.registDate).toLocaleDateString() : ""}</td>
              </tr>
              <tr>
                <th>질문</th>
                <td>
                  <div dangerouslySetInnerHTML={{ __html: bbs.bbsContent }} />
                </td>
              </tr>
              {files.length > 0 && (
                <tr>
                  <th>첨부파일</th>
                  <td>
                    {files.map((file) => (
                      <div key={file.fileNum}>
                        <a
                          href={`${baseUrl}/files/${file.fileNum}/download`}
                          download={file.originalName}
                        >
                          {file.originalName}
                        </a>
                      </div>
                    ))}
                  </td>
                </tr>
              )}

              {/* 관리자 답변 */}
              {post.answer && (
                <tr>
                  <th>관리자 답변</th>
                  <td>{post.answer}</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="form_center_box ">
          <div className="temp_btn white md">
            <button className="btn" onClick={() => navigate("/bbs/qna")}>목록보기</button>
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
    </div>
  );
}

export default QnaBbsView;
