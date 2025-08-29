import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { findOrCreateChatRoom, getChatMessages } from './ChatService';
import "../style/Chat.css";

const Chat = ({ memberNum }) => {
    const [messages, setMessages] = useState([]);
    const [inputMessage, setInputMessage] = useState('');
    const [chatRoom, setChatRoom] = useState(null);
    const client = useRef(null);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        const setupChat = async () => {
            const room = await findOrCreateChatRoom(memberNum);
            if (room) {
                setChatRoom(room);
                
                const existingMessages = await getChatMessages(room.roomNum);
                setMessages(existingMessages);
                
                connectWebSocket(room.roomNum);
            }
        };
        
        setupChat();
        
        return () => {
            if (client.current) {
                client.current.deactivate();
            }
        };
    }, [memberNum]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const connectWebSocket = (roomNum) => {
        client.current = new Client({
            webSocketFactory: () => new SockJS(`http://localhost:8080/ws`),
            connectHeaders: {},
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            
            onConnect: () => {
                console.log("WebSocket 연결 성공");
                
                client.current.subscribe(`/sub/chat/room/${roomNum}`, (message) => {
                    const newMessage = JSON.parse(message.body);
                    setMessages(prev => [...prev, newMessage]);
                });
            },
            
            onStompError: (frame) => {
                console.error("Broker 오류:", frame);
            },
        });
        
        client.current.activate();
    };

    const sendMessage = () => {
        if (client.current && client.current.connected && inputMessage) {
            // ⭐ 이 부분을 수정했습니다.
            const chatMessage = {
                senderId: memberNum,
                isMemberSender: true,
                messageContent: inputMessage
            };
            
            client.current.publish({
                destination: `/pub/chat/${chatRoom.roomNum}`, // ⭐ pub 경로에 roomNum 추가
                body: JSON.stringify(chatMessage),
            });
            
            setInputMessage('');
        }
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    return (
        <div className="chat-window">
            <div className="chat-messages">
                {messages.map((msg, index) => (
                    // ⭐ msg 객체의 속성 이름 수정 (백엔드 DTO에 맞게)
                    <div key={index} className={`chat-message ${msg.isMemberSender ? 'my-message' : 'other-message'}`}>
                        <div className="message-content">{msg.messageContent}</div>
                    </div>
                ))}
                <div ref={messagesEndRef} />
            </div>
            <div className="chat-input-area">
                <input 
                    type="text" 
                    placeholder="메시지를 입력하세요"
                    value={inputMessage} 
                    onChange={(e) => setInputMessage(e.target.value)} 
                    onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                            sendMessage();
                        }
                    }}
                />
                <button onClick={sendMessage}>보내기</button>
            </div>
        </div>
    );
};

export default Chat;