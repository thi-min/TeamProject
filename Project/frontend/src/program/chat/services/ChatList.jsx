// src/program/chat/services/ChatList.jsx

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import '../style/Chat.css';

const ChatList = ({ isAdmin }) => {
  const [chatRooms, setChatRooms] = useState([]); // Hook call 1

  useEffect(() => { // Hook call 2
    if (!isAdmin) {
      // Don't fetch data if the user is not an admin
      return; 
    }
    
    const fetchChatRooms = async () => {
      try {
        const response = await axios.get('/chat/list');
        setChatRooms(response.data);
      } catch (error) {
        console.error('Failed to fetch chat rooms', error);
      }
    };

    fetchChatRooms();
  }, [isAdmin]); // Add isAdmin to the dependency array

  // Conditional return must be placed after all hook calls
  if (!isAdmin) {
    return <div>관리자만 접근할 수 있는 페이지입니다.</div>;
  }

  return (
    <div className="chat-list-container">
      <h2>채팅방 목록</h2>
      <ul className="chat-room-list">
        {chatRooms.map((room) => (
          <li key={room.chatRoomId} className="chat-room-item">
            <Link to={`/admin/chat/detail/${room.chatRoomId}`}>
              <div className="chat-room-info">
                <span className="member-name">{room.memberName}</span>
                {room.hasNewMessage && (
                  <span className="new-message-indicator">새 메시지!</span>
                )}
              </div>
              <p className="last-message-content">{room.lastMessageContent}</p>
              <span className="last-message-time">
                {new Date(room.lastMessageTime).toLocaleString()}
              </span>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ChatList;