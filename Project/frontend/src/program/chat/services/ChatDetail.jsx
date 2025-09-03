import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { api } from "../../../common/api/axios.js";
import ChatService from './ChatService';
import "../style/Chat.css";

// JWT 토큰 디코딩 함수 (payload만 가져옴)
const decodeJwt = (token) => {
    try {
        const payload = token.split('.')[1];
        const decoded = atob(payload);
        return JSON.parse(decoded);
    } catch (e) {
        return null;
    }
};

// 수정된 ChatInput 컴포넌트 (클래스명 적용)
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
            return;
        }

        setIsSending(true);

        const chatDto = {
            chatRoomNum: chatRoomNum,
            message: trimmedMessage
        };

        try {
            await ChatService.sendMessage(chatDto);
            setMessage("");
        } catch (error) {
            console.error("메시지 전송 실패:", error);
        } finally {
            setIsSending(false);
        }
    };

    return (
        <form className="chat-input-form" onSubmit={handleSendMessage}>
            <input
                type="text"
                className="ui-input" // 클래스명 통일
                placeholder="메시지를 입력하세요..."
                value={message}
                onChange={handleMessageChange}
                disabled={isSending}
            />
            <div className="temp_btn md">
                <button type="submit" className="btn" disabled={isSending || !message.trim()}>
                    {isSending ? "전송 중..." : "전송"}
                </button>
            </div>
        </form>
    );
};

const ChatDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const chatRoomNum = id;
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const chatMessagesRef = useRef(null);
    const [userRole, setUserRole] = useState(null);

    const authAxios = api.create({
        baseURL: 'http://127.0.0.1:8090/',
        headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    });

    const roleDisplayNames = {
        'ADMIN': '관리자',
        'MEMBER': '회원' 
    };

    const formatTime = (isoString) => {
        if (!isoString) return "";
        const date = new Date(isoString);
        return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
    };

    const fetchChatHistory = async () => {
        setLoading(true);
        try {
            const response = await authAxios.get(`/api/chat/detail/${chatRoomNum}`);
            setMessages(response.data);
            setError(null);
        } catch (err) {
            console.error("채팅 기록 조회 실패:", err);
            setError("채팅 기록을 불러올 수 없습니다. 권한을 확인해주세요.");
            setMessages([]);
        } finally {
            setLoading(false);
        }
    };

    // WebSocket 메시지를 받아 상태에 추가하는 함수
    const handleAddMessage = (newMessage) => {
        setMessages(prevMessages => [...prevMessages, newMessage]);
    };
    
    useEffect(() => {
        fetchChatHistory();

        const token = localStorage.getItem("accessToken");
        if (token) {
            const decodedToken = decodeJwt(token);
            if (decodedToken && decodedToken.auth) {
                const role = decodedToken.auth;
                setUserRole(role);
            } else {
                setUserRole("MEMBER");
            }
        } else {
            setUserRole("MEMBER");
        }
    }, [chatRoomNum]);

    useEffect(() => {
        if (chatRoomNum) {
            const token = localStorage.getItem("accessToken");
            if (!token) return;

            ChatService.connect(
                token,
                () => {
                    console.log("WebSocket 연결 성공");
                    ChatService.subscribe(chatRoomNum, (message) => {
                        handleAddMessage(message);
                    });
                },
                (err) => {
                    console.error("WebSocket 연결 실패:", err);
                }
            );

            return () => {
                ChatService.disconnect();
            };
        }
    }, [chatRoomNum]);

    useEffect(() => {
        if (chatMessagesRef.current) {
            chatMessagesRef.current.scrollTop = chatMessagesRef.current.scrollHeight;
        }
    }, [messages]);

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div>
            <h3>채팅방 {chatRoomNum} 대화 내역</h3>
            <div className="box" style={{ height: "400px", overflowY: "auto" }}> {/* 메시지 목록 영역 */}
                {messages.length > 0 ? (
                    messages.map((msg, index) => (
                        <div key={index} className={`message-item ${msg.senderRole === userRole ? "my-message" : "other-message"}`}>
                            <div className="message-content-wrapper">
                                <strong>{roleDisplayNames[msg.senderRole] || msg.senderRole}</strong>: {msg.message}
                                <span className="message-timestamp">{formatTime(msg.timestamp)}</span>
                            </div>
                        </div>
                    ))
                ) : (
                    <div>대화 기록이 없습니다.</div>
                )}
            </div>
            <div className="temp_input"> {/* 입력 필드 및 버튼 영역 */}
                <ChatInput chatRoomNum={chatRoomNum} />
            </div>
            
                <div className="temp_btn white md">
                    <button type="button" className="btn" onClick={() => navigate(-1)}>
                        목록보기
                    </button>
                </div>
            
        </div>
    );
};

export default ChatDetail;