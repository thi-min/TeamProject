import React, { useState, useEffect, useRef } from "react";
import ChatService from './ChatService';
import { api } from "../../../common/api/axios.js";
import "../style/ChatPopup.css";
import "../style/ChatInput.css";

// ChatInput 컴포넌트를 이 파일 내부에 정의
const ChatInput = ({ chatRoomNum, onAddMessage }) => {
  const [message, setMessage] = useState("");

  const handleMessageChange = event => {
    setMessage(event.target.value);
  };

  const handleSendMessage = async event => {
    event.preventDefault();
    const trimmedMessage = message.trim();
    
    if (trimmedMessage && chatRoomNum) {
      const chatDto = {
        chatRoomNum: chatRoomNum,
        message: trimmedMessage,
      };
      
      try {
        await ChatService.sendMessage(chatDto);
        setMessage("");
        
      } catch (error) {
        console.error("메시지 전송 실패:", error);
        alert("메시지 전송에 실패했습니다. 다시 시도해주세요.");
      }
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


const ChatPopup = ({ onClose }) => {
  const [chatHistory, setChatHistory] = useState([]);
  const [chatRoomNum, setChatRoomNum] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const chatMessagesRef = useRef(null);

  const handleAddMessage = (message) => {
    setChatHistory((prevHistory) => {
        const isDuplicate = prevHistory.some(msg => 
            msg.timestamp === message.timestamp && 
            msg.message === message.message
        );
        if (isDuplicate) {
            return prevHistory;
        }
        return [...prevHistory, message];
    });
  };

  useEffect(() => {
    const initializeChat = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        if (!token) {
          setError("로그인이 필요합니다.");
          setIsLoading(false);
          return;
        }

        const response = await api.post("/api/chat/detail", {}, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const newChatRoomNum = response.data.chatRoomNum;
        setChatRoomNum(newChatRoomNum);

        const historyResponse = await api.get(`/api/chat/detail/${newChatRoomNum}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setChatHistory(historyResponse.data);

        ChatService.connect(
          token,
          () => {
            console.log("WebSocket 연결 성공");
            ChatService.subscribe(newChatRoomNum, (message) => {
              handleAddMessage(message);
            });
          },
          (err) => {
            console.error("WebSocket 연결 실패:", err);
            setError("채팅 서버에 연결할 수 없습니다.");
          }
        );
      } catch (e) {
        console.error("채팅 초기화 오류:", e);
        if (e.response && e.response.status === 401) {
          setError("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
        } else {
          setError("채팅을 시작할 수 없습니다. 다시 시도해주세요.");
        }
      } finally {
        setIsLoading(false);
      }
    };

    initializeChat();

    return () => {
      ChatService.disconnect();
    };
  }, []);

  useEffect(() => {
    if (chatMessagesRef.current) {
      chatMessagesRef.current.scrollTop = chatMessagesRef.current.scrollHeight;
    }
  }, [chatHistory]);

  const formatTime = (isoString) => {
    if (!isoString) return "";
    const date = new Date(isoString);
    return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="chat-popup">
      <div className="chat-header">
        <span className="chat-title">그룹 채팅</span> 
        <button className="close-btn" onClick={onClose}>
          &times;
        </button>
      </div>

      <div className="chat-messages" ref={chatMessagesRef}>
        {isLoading && <p className="loading-message">채팅을 불러오는 중...</p>}
        {error && <p className="error-message">{error}</p>}
        {!isLoading && !error && chatHistory.map((msg, index) => (
          // ⭐ DB의 senderRole 값에 따라 위치를 결정
            <div key={index} className={`chat-line ${msg.senderRole === 'member' ? "my-line" : "other-line"}`}>
            <div className={`chat-message-bubble ${msg.senderRole === 'member' ? "my-bubble" : "other-bubble"}`}>
                <span className="message-content">{msg.message}</span>
            </div>
                <span className="message-timestamp">{formatTime(msg.timestamp)}</span>
            </div>
        ))}
      </div>

      <div className="chat-input-container">
        <ChatInput chatRoomNum={chatRoomNum} onAddMessage={handleAddMessage} />
      </div>
    </div>
  );
};

export default ChatPopup;