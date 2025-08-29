// Project/frontend/src/program/chat/ChatDetail.jsx

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

// 메시지 입력 컴포넌트 (내부 정의)
const ChatInput = ({ chatRoomNum, onAddMessage, userRole }) => {
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
                senderRole: userRole // 동적으로 사용자 역할을 할당
            };
            
            try {
                await ChatService.sendMessage(chatDto);
                onAddMessage({
                    ...chatDto,
                    timestamp: new Date().toISOString()
                });
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


const ChatDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const chatRoomNum = id;
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const chatMessagesRef = useRef(null);
    const [userRole, setUserRole] = useState("MEMBER"); // 기본값 설정

    const authAxios = api.create({
        baseURL: 'http://localhost:8090/',
        headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    });

    // 역할을 표시 이름으로 매핑하는 객체
    const roleDisplayNames = {
        'USER': '회원',
        
        'MEMBER': '관리자' 
    };

    // 시간을 보기 좋게 포맷팅하는 함수 추가
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

    const handleAddMessage = (message) => {
        setMessages(prevMessages => [...prevMessages, message]);
    };
    
    // 컴포넌트 마운트 시 대화 기록 조회 및 웹소켓 연결
    useEffect(() => {
        fetchChatHistory();

        const token = localStorage.getItem("accessToken");
        if (token) {
            // 토큰에서 사용자 역할 추출
            const decodedToken = decodeJwt(token);
            if (decodedToken && decodedToken.auth) {
                const role = decodedToken.auth;
                setUserRole(role);
            }

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
        }

        return () => {
            ChatService.disconnect();
        };
    }, [chatRoomNum]);

    // 메시지가 추가될 때마다 스크롤을 맨 아래로 이동
    useEffect(() => {
        if (chatMessagesRef.current) {
            chatMessagesRef.current.scrollTop = chatMessagesRef.current.scrollHeight;
        }
    }, [messages]);


    if (loading) return <div>로딩 중...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div className="chat-detail-container">
            <h1>채팅방 {chatRoomNum} 대화 내역</h1>
            <div className="message-list" ref={chatMessagesRef}>
                {messages.length > 0 ? (
                    messages.map((msg, index) => (
                        <div key={index} className="message-item">
                            <div className="message-content-wrapper">
                                {/* roleDisplayNames 객체를 사용하여 역할 이름을 변환 */}
                                <strong>{roleDisplayNames[msg.senderRole] || msg.senderRole}</strong>: {msg.message}
                                <span className="message-timestamp">{formatTime(msg.timestamp)}</span>
                            </div>
                        </div>
                    ))
                ) : (
                    <div>대화 기록이 없습니다.</div>
                )}
            </div>
            
            <div className="chat-input-container">
                <ChatInput chatRoomNum={chatRoomNum} onAddMessage={handleAddMessage} userRole={userRole} />
            </div>

            <button onClick={() => navigate(-1)}>이전으로</button>
        </div>
    );
};

export default ChatDetail;