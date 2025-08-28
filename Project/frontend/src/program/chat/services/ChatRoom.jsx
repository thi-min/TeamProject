import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';
import { FaPaperPlane, FaTimes } from 'react-icons/fa';
import '../style/Chat.css';

const ChatRoom = ({ memberNum, adminId, chatRoomId, onClose }) => {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const { roomId } = useParams();
  const messagesEndRef = useRef(null);

  const currentChatRoomId = chatRoomId || roomId;
  const isMember = !!memberNum;
  const isConnectedRef = useRef(false);

  useEffect(() => {
    // chatRoomId가 없으면 빈 배열로 초기화하고 함수 종료
    if (!currentChatRoomId) {
      setMessages([]);
      return;
    }

    const fetchMessages = async () => {
      try {
        const response = await axios.get(`/chat/list/${currentChatRoomId}/messages`);
        setMessages(response.data);
      } catch (error) {
        console.error('Failed to fetch messages', error);
      }
    };
    fetchMessages();

    // WebSocket 연결
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      onConnect: () => {
        console.log('Connected to WebSocket');
        isConnectedRef.current = true;
        // 특정 채팅방 토픽 구독
        client.subscribe(`/topic/chat/${currentChatRoomId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, receivedMessage]);
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error:', frame.headers['message']);
        console.error('Additional details:', frame.body);
        isConnectedRef.current = false;
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });
    
    client.webSocketFactory = () => {
      return new SockJS('http://localhost:8080/ws');
    };
    
    client.activate();
    setStompClient(client);

    return () => {
      if (client.active) {
        client.deactivate();
      }
    };
  }, [currentChatRoomId]);

  useEffect(() => {
    // 새 메시지 수신 시 스크롤을 맨 아래로 이동
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  const handleSendMessage = () => {
    if (inputMessage.trim() === '' || !isConnectedRef.current) return;

    const chatMessage = {
      chatRoomId: currentChatRoomId,
      senderId: isMember ? memberNum : adminId,
      message: inputMessage,
      chatType: isMember ? 'TALK' : 'REPLY'
    };

    stompClient.publish({
      destination: `/app/chat.send`,
      body: JSON.stringify(chatMessage)
    });

    setInputMessage('');
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSendMessage();
    }
  };

  return (
    <div className="chat-room-modal">
      <div className="chat-header">
        <span className="chat-title">1:1 채팅</span>
        <button className="close-button" onClick={onClose}>
          <FaTimes />
        </button>
      </div>
      <div className="chat-messages">
        {messages.map((msg, index) => (
          <div 
            key={index} 
            className={`message-bubble ${isMember && msg.chatType === 'TALK' || !isMember && msg.chatType === 'REPLY' ? 'my-message' : 'other-message'}`}
          >
            <div className="message-content">{msg.message}</div>
            <div className="message-time">{new Date(msg.chatTime).toLocaleTimeString()}</div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
      <div className="chat-input-container">
        <input
          type="text"
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="메시지를 입력하세요..."
        />
        <button className="send-button" onClick={handleSendMessage}>
          <FaPaperPlane />
        </button>
      </div>
    </div>
  );
};

export default ChatRoom;