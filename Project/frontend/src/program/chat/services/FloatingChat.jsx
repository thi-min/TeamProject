import React, { useState } from "react";
import ChatPopup from "./ChatPopup";
import "../style/FloatingChat.css";

const FloatingChat = () => {
  // 팝업 열림/닫힘 상태만 관리
  const [isPopupOpen, setIsPopupOpen] = useState(false);

  const togglePopup = () => {
    setIsPopupOpen(!isPopupOpen);
  };

  return (
    <>
      {/* 플로팅 배너 버튼: 회원 여부와 관계없이 항상 렌더링 */}
      <button className="floating-chat-btn" onClick={togglePopup}>
        💬
      </button>

      {/* 팝업 채팅창 (조건부 렌더링) */}
      {isPopupOpen && <ChatPopup onClose={togglePopup} />}
    </>
  );
};

export default FloatingChat;
