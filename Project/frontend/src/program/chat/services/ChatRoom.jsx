import { Client } from '@stomp/stompjs';
import axios from 'axios';
import { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';
import '../style/Chat.css'; // 스타일시트 경로는 프로젝트 구조에 맞게 수정해주세요.

const ChatRoom = () => {
    const { chatRoomId } = useParams(); // URL에서 채팅방 ID 추출
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);

    // 메시지 목록의 최하단으로 스크롤
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    // WebSocket 연결 및 API 호출
    useEffect(() => {
        // 1. 초기 메시지 목록 불러오기
        const fetchMessages = async () => {
            try {
                const response = await axios.get(`http://localhost:3000/rooms/${chatRoomId}/messages`);
                setMessages(response.data);
            } catch (err) {
                console.error("메시지 목록을 불러오는 중 오류 발생:", err);
                setError("메시지 목록을 불러오는 데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };
    // 2. WebSocket 연결
    const socket = new SockJS('http://localhost:3000/ws');
    // Stomp.over 대신 new Client({ webSocketFactory: () => socket }) 사용
    const client = new Client({
        webSocketFactory: () => socket
    });

    client.onConnect = (frame) => {
        console.log('Connected: ' + frame);
        // 구독: 메시지가 도착하면 목록에 추가
        client.subscribe(`/topic/chat/${chatRoomId}`, (message) => {
            const receivedMessage = JSON.parse(message.body);
            setMessages(prevMessages => [...prevMessages, receivedMessage]);
        });
    };
    
    // ⭐ 연결 시도
    client.activate();
    stompClient.current = client;

    fetchMessages();
    
    // 컴포넌트 언마운트 시 WebSocket 연결 해제
    return () => {
        if (stompClient.current) {
            stompClient.current.deactivate(); // disconnect() 대신 deactivate() 사용
        }
    };
}, [chatRoomId]);

    // messages 상태가 업데이트될 때마다 스크롤
    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    // 메시지 전송 핸들러
    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (newMessage.trim() === '') return;

        // 백엔드에 보낼 메시지 DTO
        const messagePayload = {
            chatRoomId: chatRoomId,
            adminId: 'AdminId', // ⭐ 현재 로그인된 관리자 ID로 변경해야 합니다.
            chatCont: newMessage
        };

        try {
            await axios.post(`http://localhost:3000/rooms/${chatRoomId}/messages`, messagePayload);
            setNewMessage(''); // 전송 후 입력창 비우기
        } catch (err) {
            console.error("메시지 전송 중 오류 발생:", err);
            // 오류 처리: 사용자에게 메시지를 보여주는 등의 로직 추가
        }
    };

    if (loading) {
        return <div className="loading">채팅 내역을 불러오는 중입니다...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

    return (
        <div className="chat-container">
            <h1 className="chat-header">1:1 채팅방 (관리자)</h1>
            <div className="messages-container">
                {messages.map((msg) => (
                    <div 
                        key={msg.chatMessageId} 
                        className={`message ${msg.adminId ? 'admin-message' : 'member-message'}`}
                    >
                        <div className="message-content">
                            {msg.chatCont}
                        </div>
                        <div className="message-time">
                            {new Date(msg.sendTime).toLocaleTimeString()}
                        </div>
                    </div>
                ))}
                <div ref={messagesEndRef} />
            </div>
            <form className="message-form" onSubmit={handleSendMessage}>
                <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="메시지를 입력하세요..."
                    className="message-input"
                />
                <button type="submit" className="send-button">전송</button>
            </form>
        </div>
    );
};

export default ChatRoom;