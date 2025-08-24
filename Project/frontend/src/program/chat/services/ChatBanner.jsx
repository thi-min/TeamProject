import axios from 'axios';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import chatIcon from '../images/chat-icon.png'; // 챗 아이콘 이미지 경로 (예시)
import '../style/ChatBanner.css';

const ChatBanner = () => {
    const navigate = useNavigate();
    const [hasUnread, setHasUnread] = useState(false);
    const [chatRoomId, setChatRoomId] = useState(null);

    // JWT 토큰에서 회원 번호 추출
    const getMemberNumFromToken = () => {
        const token = localStorage.getItem('accessToken');
        if (!token) return null;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.memberNum;
        } catch (e) {
            console.error("JWT 토큰 파싱 실패:", e);
            return null;
        }
    };
    
    // 미확인 메시지 여부 및 채팅방 ID 조회
    useEffect(() => {
        const fetchChatStatus = async () => {
            const memberNum = getMemberNumFromToken();
            if (!memberNum) {
                // 로그인하지 않은 상태면 아무것도 하지 않음
                return;
            }

            try {
                // 백엔드 API: 미확인 메시지 및 채팅방 ID 조회
                const response = await axios.get(`http://localhost:3000/api/chat/status?memberNum=${memberNum}`);
                const data = response.data;
                
                setHasUnread(data.hasUnreadMessages);
                setChatRoomId(data.chatRoomId);
            } catch (error) {
                console.error("채팅 상태 조회 실패:", error);
            }
        };

        fetchChatStatus();
        // 10초마다 상태를 주기적으로 업데이트
        const interval = setInterval(fetchChatStatus, 10000); 
        return () => clearInterval(interval);
    }, []);

    const handleBannerClick = async () => {
        const memberNum = getMemberNumFromToken();
        
        if (!memberNum) {
            alert("로그인 후 이용 가능합니다.");
            navigate('/login');
            return;
        }

        if (chatRoomId) {
            // 기존 채팅방이 있으면 해당 채팅방으로 이동
            navigate(`/chat/room/${chatRoomId}`);
        } else {
            // 채팅방이 없으면 새로운 채팅방을 생성하고 이동
            try {
                const response = await axios.post(
                    'http://localhost:3000/api/chat/create-room', 
                    null,
                    { 
                        params: { memberNum: memberNum },
                        headers: {
                            Authorization: `Bearer ${localStorage.getItem('accessToken')}`
                        }
                    }
                );
                const newChatRoomId = response.data.chatRoomId;
                navigate(`/chat/room/${newChatRoomId}`);
            } catch (error) {
                console.error("채팅방 생성 실패:", error);
                alert("채팅방을 여는 데 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
        }
    };

    return (
        <div className="chat-banner" onClick={handleBannerClick}>
            <img src={chatIcon} alt="Chat Icon" className="chat-icon" />
            {hasUnread && <span className="unread-dot"></span>}
        </div>
    );
};

export default ChatBanner;