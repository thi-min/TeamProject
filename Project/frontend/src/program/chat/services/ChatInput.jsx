import { useState } from "react";
import chatService from "./ChatService";
import "../style/ChatInput.css";

const ChatInput = ({ chatRoomNum }) => {
  const [message, setMessage] = useState("");

  const handleMessageChange = event => {
    setMessage(event.target.value);
  };

  const handleSendMessage = event => {
    event.preventDefault();
    if (message.trim()) {
      // 1. 백엔드에서 요구하는 JSON 객체 형태로 데이터 구성
      const chatDto = {
        chatRoomNum: chatRoomNum,
        message: message,
      };
      
      // 2. chatService의 sendMessage 함수에 단일 객체(chatDto)를 전달
      chatService.sendMessage(chatDto);
      
      // 메시지 입력창 초기화
      setMessage("");
    }
  };

  return (
    <form className="chat-input-form" onSubmit={handleSendMessage}>
      <input
        type="text"
        className="chat-input-field"
        placeholder="메시지를 입력하세요..."
        value={message}
        onChange={handleMessageChange}
      />
      <button type="submit" className="chat-send-btn">
        전송
      </button>
    </form>
  );
};

export default ChatInput;