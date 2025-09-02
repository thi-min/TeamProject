// import React, { useState, useRef, useEffect } from "react";
// import axios from "axios";
// import { useNavigate } from "react-router-dom";

// function QnaBbsWrite() {
//   const [bbstitle, setBbstitle] = useState("");
//   const [files, setFiles] = useState([{ id: Date.now(), file: null, insertOption: "no-insert" }]);
//   const editorRef = useRef(null);
//   const navigate = useNavigate();
//   const baseUrl = "http://127.0.0.1:8090/bbs/bbslist/bbsadd";

//   // 파일 선택
//   const handleFileChange = (id, newFile) => {
//     setFiles(prev =>
//       prev.map(f => (f.id === id ? { ...f, file: newFile } : f))
//     );

//     if (newFile && !["image/jpeg", "image/jpg"].includes(newFile.type)) {
//       setFiles(prev =>
//         prev.map(f => (f.id === id ? { ...f, insertOption: "no-insert" } : f))
//       );
//     }
//   };

//   // 본문 삽입 옵션 변경
//   const handleInsertOptionChange = (id, option) => {
//     const fileObj = files.find(f => f.id === id);
//     const file = fileObj?.file;

//     if (option === "insert") {
//       if (!file) return alert("먼저 파일을 선택해주세요.");
//       if (!["image/jpeg", "image/jpg"].includes(file.type)) {
//         return alert("본문 삽입은 jpg/jpeg 이미지 파일만 가능합니다.");
//       }

//       const alreadyInserted = editorRef.current?.querySelector(`img[data-id='${id}']`);
//       if (alreadyInserted) return;

//       const reader = new FileReader();
//       reader.onload = e => {
//         const imgTag = `<img src="${e.target.result}" data-id="${id}" style="max-width:600px;" />`;

//         if (editorRef.current) {
//           editorRef.current.focus();
//           const sel = window.getSelection();
//           if (!sel.rangeCount) return;
//           const range = sel.getRangeAt(0);

//           const el = document.createElement("span");
//           el.innerHTML = imgTag;
//           range.insertNode(el);

//           range.setStartAfter(el);
//           range.setEndAfter(el);
//           sel.removeAllRanges();
//           sel.addRange(range);
//         }
//       };
//       reader.readAsDataURL(file);
//     } else {
//       if (editorRef.current) {
//         const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
//         imgs.forEach(img => img.remove());
//       }
//     }

//     setFiles(prev =>
//       prev.map(f => (f.id === id ? { ...f, insertOption: option } : f))
//     );
//   };

//   // 파일 추가
//   const addFileInput = () => {
//     setFiles(prev => [...prev, { id: Date.now(), file: null, insertOption: "no-insert" }]);
//   };

//   // 파일 삭제
//   const removeFileInput = id => {
//     setFiles(prev => prev.filter(f => f.id !== id));
//     if (editorRef.current) {
//       const imgs = editorRef.current.querySelectorAll(`img[data-id='${id}']`);
//       imgs.forEach(img => img.remove());
//     }
//   };

//   // 본문 삽입 이미지 삭제 감지
//   useEffect(() => {
//     const observer = new MutationObserver(() => {
//       setFiles(prevFiles =>
//         prevFiles.map(f => {
//           if (f.insertOption === "insert") {
//             const imgExists = editorRef.current?.querySelector(`img[data-id='${f.id}']`);
//             if (!imgExists) return { ...f, insertOption: "no-insert" };
//           }
//           return f;
//         })
//       );
//     });

//     if (editorRef.current) {
//       observer.observe(editorRef.current, { childList: true, subtree: true });
//     }

//     return () => observer.disconnect();
//   }, []);

//   // 제출
//   const handleSubmit = async e => {
//     e.preventDefault();
//     const memberNum = localStorage.getItem("memberNum");
//     if (!memberNum) return alert("로그인이 필요합니다.");

//     const formData = new FormData();
//     formData.append("memberNum", memberNum);
//     formData.append("type", "FAQ");

//     // 본문 HTML (insert 선택된 이미지 포함)
//     const contentHTML = editorRef.current?.innerHTML || "";
//     formData.append(
//       "bbsDto",
//       new Blob(
//         [
//           JSON.stringify({
//             bbsTitle: bbstitle,
//             bbsContent: contentHTML,
//             bulletinType: "FAQ",
//           }),
//         ],
//         { type: "application/json" }
//       )
//     );

//     // 모든 파일 첨부 (insert 여부 상관없이)
//     files.forEach(f => {
//       if (f.file) {
//         formData.append("files", f.file);
//       }
//     });

//     try {
//       await axios.post(baseUrl, formData, {
//         headers: { "Content-Type": "multipart/form-data" },
//       });
//       alert("질문이 등록되었습니다.");
//       navigate("/bbs/qna");
//     } catch (error) {
//       console.error("질문 등록 오류:", error);
//       alert("등록 실패: 서버 연결이나 데이터 확인 필요");
//     }
//   };

//   return (
//     <div className="bbs-write-container">
//       <form className="bbs-write-form" onSubmit={handleSubmit}>
//         <div className="bbs-row">
//           <div className="bbs-label">제목</div>
//           <input
//             type="text"
//             className="bbs-title-input"
//             placeholder="제목을 입력해 주세요"
//             value={bbstitle}
//             onChange={e => setBbstitle(e.target.value)}
//             required
//           />
//         </div>

//         <div className="bbs-row">
//           <div className="bbs-label">내용</div>
//           <div
//             ref={editorRef}
//             contentEditable
//             className="bbs-content-input"
//             style={{ minHeight: "200px", border: "1px solid #ccc", padding: "10px" }}
//           />
//         </div>

//         <div className="bbs-row">
//           <div className="bbs-label">파일 첨부</div>
//           <div className="bbs-file-list">
//             {files.map(f => (
//               <div className="bbs-file-row" key={f.id}>
//                 <input type="file" onChange={e => handleFileChange(f.id, e.target.files[0])} />
//                 <div className="bbs-file-options">
//                   <label>
//                     <input
//                       type="radio"
//                       name={`insertOption-${f.id}`}
//                       value="insert"
//                       checked={f.insertOption === "insert"}
//                       onChange={() => handleInsertOptionChange(f.id, "insert")}
//                     />
//                     본문 삽입
//                   </label>
//                   <label>
//                     <input
//                       type="radio"
//                       name={`insertOption-${f.id}`}
//                       value="no-insert"
//                       checked={f.insertOption === "no-insert"}
//                       onChange={() => handleInsertOptionChange(f.id, "no-insert")}
//                     />
//                     본문 미삽입
//                   </label>
//                 </div>
//                 {files.length > 1 && (
//                   <button type="button" className="bbs-file-remove" onClick={() => removeFileInput(f.id)}>
//                     ❌
//                   </button>
//                 )}
//               </div>
//             ))}
//             <button type="button" className="bbs-file-add" onClick={addFileInput}>
//               ➕ 파일 추가
//             </button>
//           </div>
//         </div>

//         <div className="bbs-btn-area">
//           <button type="button" className="bbs-cancel-btn" onClick={() => navigate("/bbs/qna")}>
//             취소
//           </button>
//           <button type="submit" className="bbs-save-btn">
//             저장
//           </button>
//         </div>
//       </form>
//     </div>
//   );
// }

// export default QnaBbsWrite;
