// src/bbs/qnabbs/QnaBbs.jsx
import React, { useState } from 'react';
import './qnabbs.css';

function QnaBbs() {
  const [openIndex, setOpenIndex] = useState(null);

  const dummyQnA = [
    {
      question: "회원가입 아이디 찾기는 어디서 하나요?",
      answer: "로그인/회원가입/아이디·비번 찾기에서 찾을 수 있습니다. (서비스 오픈 예정: 2024년 3월 29일)"
    },
    {
      question: "펫 사진은 어떻게 등록하나요?",
      answer: "이미지 게시판에서 사진 업로드 버튼을 통해 등록할 수 있습니다."
    }
  ];

  return (
    <div className="bbs-container">
      <h2>❓ 자주 묻는 질문 (Q&A)</h2>
      <div className="qna-list">
        {dummyQnA.map((item, idx) => (
          <div key={idx} className="qna-item">
            <div className="qna-question" onClick={() => setOpenIndex(openIndex === idx ? null : idx)}>
              <strong>Q.</strong> {item.question}
            </div>
            {openIndex === idx && (
              <div className="qna-answer">
                <strong>A.</strong> {item.answer}
              </div>
            )}
          </div>
        ))}
      </div>

      <div className="pagination">
        <button disabled>«</button>
        <button className="active">1</button>
        <button>2</button>
        <button>3</button>
        <button>»</button>
      </div>
    </div>
  );
}

export default QnaBbs;
