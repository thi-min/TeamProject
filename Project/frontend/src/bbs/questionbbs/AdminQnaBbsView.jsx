// 📁 src/admin/AdminQnaBbsView.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";
import "./qnabbs.css";

export default function AdminQnaBbsView() {
  const { id } = useParams(); // 게시글 번호
  const navigate = useNavigate();
  const token = localStorage.getItem("accessToken");

  const [post, setPost] = useState(null); // { bbs: {}, answer: "" }
  const [answerText, setAnswerText] = useState("");
  const [files, setFiles] = useState([]); // 첨부파일 리스트

  const BASE_URL = "http://127.0.0.1:8090/admin/bbs";

  // ---------------- 게시글 + 답변 조회 ----------------
  const fetchPost = async () => {
    try {
      const res = await api.get(`${BASE_URL}/qna/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log("게시글 조회 결과:", res.data);
      setPost(res.data);
      setAnswerText(res.data.answer || ""); // 기존 답변 불러오기
    } catch (err) {
      console.error("게시글 조회 실패:", err);
      if (err.response?.status === 401) {
        alert("로그인 정보가 만료되었습니다.");
        navigate("/admin/login");
      } else {
        alert("게시글 조회 실패");
      }
    }
  };

  // ---------------- 첨부파일 조회 ----------------
  const fetchFiles = async () => {
    try {
      const res = await api.get(`${BASE_URL}/${id}/files`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log("첨부파일 조회:", res.data);
      setFiles(res.data);
    } catch (err) {
      console.error("첨부파일 조회 실패:", err);
      setFiles([]);
    }
  };

  useEffect(() => {
    fetchPost();
    fetchFiles();
  }, [id]);

  // ---------------- 답변 저장 ----------------
  const handleSaveAnswer = async () => {
    if (!answerText.trim()) {
      alert("답변을 입력해주세요.");
      return;
    }
    try {
      const res = await api.post(
        `${BASE_URL}/qna/${id}/answer`,
        { answer: answerText },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      console.log("답변 저장 성공:", res.data);

      setPost((prev) => ({
        ...prev,
        answer: res.data.answer,
      }));
      setAnswerText(res.data.answer || "");
      alert("답변이 저장되었습니다.");
      navigate("/admin/bbs/qna");
    } catch (err) {
      console.error("답변 저장 실패:", err);
      alert("답변 저장 실패. 다시 시도해주세요.");
    }
  };

  // ---------------- 게시글 삭제 ----------------
  const handleDeletePost = async () => {
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;
    try {
      await api.delete(`${BASE_URL}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("게시글이 삭제되었습니다.");
      navigate("/admin/bbs/qna");
    } catch (err) {
      console.error("게시글 삭제 실패:", err);
      alert(err.response?.data?.message || "게시글 삭제 실패");
    }
  };

  if (!post) return <div>로딩 중...</div>;

  const bbs = post.bbs || {}; // bbs 정보가 들어있는 객체

  return (
    <div className="bbs-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">게시판 관리</div>
        </div>
      </div>
      <table className="table type2 responsive border line_td">
        <colgroup>
          <col style={{ width: "20%" }} />
          <col />
        </colgroup>
        <tbody>
          {/* 제목 */}
          <tr>
            <th scope="row">제목</th>
            <td>{bbs.bbsTitle}</td>
          </tr>

          {/* 본문 (이미지 포함 가능) */}
          <tr>
            <th scope="row">내용</th>
            <td>
              <div
                className="bbs-content"
                dangerouslySetInnerHTML={{ __html: bbs.bbsContent }}
              />
            </td>
          </tr>

          {/* 작성자 */}
          <tr>
            <th scope="row">작성자</th>
            <td>{bbs.memberName || "익명"}</td>
          </tr>

          {/* 작성일 */}
          <tr>
            <th scope="row">작성일</th>
            <td>{new Date(bbs.registDate).toLocaleDateString()}</td>
          </tr>

          {/* 첨부파일 */}
          {files.length > 0 ? (
            files.map((file, idx) => (
              <tr key={file.fileNum}>
                {idx === 0 && (
                  <th scope="row" rowSpan={files.length}>
                    첨부파일
                  </th>
                )}
                <td>
                  <a
                    href={file.fileUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {file.originalName}
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

          {/* 관리자 답변 */}
          <tr>
            <th scope="row">답변</th>
            <td>
              {/* 답변 작성 textarea */}
              <div className="temp_form">
                <textarea
                  value={answerText}
                  className="temp_input text_area_form"
                  onChange={(e) => setAnswerText(e.target.value)}
                  placeholder="답변을 입력하세요"
                  rows={5}
                  style={{ width: "100%", marginTop: "10px" }}
                />
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      {/* 버튼 영역 (테이블 밖) */}
      <div className="form_center_box">
        <div className="temp_btn white md">
          <button className="btn" onClick={() => navigate("/admin/bbs/qna")}>
            목록으로
          </button>
        </div>
        <div className="right_btn_box">
          <div className="temp_btn md">
            <button className="btn" onClick={handleDeletePost}>
              삭제
            </button>
          </div>
          <div className="temp_btn md">
            <button className="btn" onClick={handleSaveAnswer}>
              등록
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
