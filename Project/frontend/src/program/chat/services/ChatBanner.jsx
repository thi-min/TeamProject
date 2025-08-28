import React, { useState } from 'react';
import { Link } from "react-router-dom";
import { useAuth } from "../../../common/context/AuthContext";
import "../style/Chat.css";
import Chat from "./Chat";

const ChatBanner = () => {
    const { isLogin, userId } = useAuth();
    const [isChatOpen, setIsChatOpen] = useState(false);

    if (!isLogin) {
        return null;
    }

    const handleChatToggle = () => {
        setIsChatOpen(prev => !prev);
    };
    
    return (
        <div className="chat-banner">
            <button className="chat-button" onClick={handleChatToggle}>
                💬
            </button>
            {isChatOpen && (
                <div className="chat-content">
                    <Chat memberNum={userId} />
                    <button className="close-chat-btn" onClick={() => setIsChatOpen(false)}>닫기</button>
                </div>
            )}
        </div>
    );
};

export default ChatBanner;