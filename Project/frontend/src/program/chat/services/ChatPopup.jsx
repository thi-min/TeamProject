import React, { useState, useEffect, useRef } from "react";
import ChatService from './ChatService';
import { api } from "../../../common/api/axios.js";
import "../style/ChatPopup.css";

// 낙관적 업데이트 로직이 제거된 ChatInput 컴포넌트
const ChatInput = ({ chatRoomNum }) => {
    const [message, setMessage] = useState("");
    const [isSending, setIsSending] = useState(false);

    const handleMessageChange = event => {
        setMessage(event.target.value);
    };

    const handleSendMessage = async event => {
        event.preventDefault();
        const trimmedMessage = message.trim();
        
        if (!trimmedMessage || isSending) {
            console.log("메시지가 비어 있거나 이미 전송 중입니다.");
            return;
        }

        setIsSending(true);

        const chatDto = {
            chatRoomNum: chatRoomNum,
            message: trimmedMessage
        };

        try {
            await ChatService.sendMessage(chatDto);
            setMessage(""); // 성공적으로 보낸 후 입력창 초기화
        } catch (error) {
            console.error("메시지 전송 실패:", error);
            // 실패 시 사용자에게 알림을 주는 로직 추가 가능
        } finally {
            setIsSending(false);
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
                disabled={isSending} // isSending이 true일 때만 비활성화
            />
            <button type="submit" className="chat-send-btn" disabled={isSending || !message.trim()}>
                전송
            </button>
        </form>
    );
};

// ChatPopup 컴포넌트
const ChatPopup = ({ onClose }) => {
    const [chatHistory, setChatHistory] = useState([]);
    const [chatRoomNum, setChatRoomNum] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const chatMessagesRef = useRef(null);
    
    // 시간을 보기 좋게 포맷팅하는 함수
    const formatTime = (isoString) => {
        if (!isoString) return "";
        const date = new Date(isoString);
        return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
    };
    
    // 이 함수를 웹소켓 메시지 수신 시 호출하여 상태를 업데이트합니다.
    const handleAddMessage = (message) => {
        setChatHistory((prevHistory) => [...prevHistory, message]);
    };

    // 1. 컴포넌트 마운트 시 채팅방 정보를 초기화하는 로직
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
            } catch (e) {
                console.error("채팅 초기화 오류:", e);
                setError("채팅을 시작할 수 없습니다. 다시 시도해주세요.");
            } finally {
                setIsLoading(false);
            }
        };
        initializeChat();
    }, []);

    // 2. chatRoomNum이 유효해졌을 때 웹소켓에 연결하고 구독하는 로직
    useEffect(() => {
        if (chatRoomNum) { 
            const token = localStorage.getItem("accessToken");
            if (!token) return;

            ChatService.connect(
                token,
                () => {
                    console.log("WebSocket 연결 성공");
                    ChatService.subscribe(chatRoomNum, (message) => {
                        // ⭐ 핵심: 새로운 메시지가 도착하면 handleAddMessage를 호출하여 상태 업데이트
                        handleAddMessage(message); 
                    });
                },
                (err) => {
                    console.error("WebSocket 연결 실패:", err);
                    setError("채팅 서버에 연결할 수 없습니다.");
                }
            );

            return () => {
                ChatService.disconnect();
            };
        }
    }, [chatRoomNum]); 

    // 3. 메시지가 추가될 때마다 스크롤을 맨 아래로 이동
    useEffect(() => {
        if (chatMessagesRef.current) {
            chatMessagesRef.current.scrollTop = chatMessagesRef.current.scrollHeight;
        }
    }, [chatHistory]);

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
                    <div key={index} className={`chat-line ${msg.senderRole === 'MEMBER' || msg.senderRole === 'USER' ? "my-line" : "other-line"}`}>
                        <div className={`chat-message-bubble ${msg.senderRole === 'MEMBER' || msg.senderRole === 'USER' ? "my-bubble" : "other-bubble"}`}>
                            <span className="message-content">{msg.message}</span>
                        </div>
                        <span className="message-timestamp">{formatTime(msg.timestamp)}</span>
                    </div>
                ))}
            </div>
            <div className="chat-input-container">
                <ChatInput chatRoomNum={chatRoomNum} />
            </div>
        </div>
    );
};

export default ChatPopup;