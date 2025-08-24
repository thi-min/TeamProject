import axios from 'axios';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../style/Chat.css'; // 이전에 제공된 스타일 파일을 기준으로 생성합니다.


const ChatList = () => {
    // 1. 상태 관리
    const [chatRooms, setChatRooms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    // 2. 컴포넌트 마운트 시 데이터 불러오기
    useEffect(() => {
        const fetchChatRooms = async () => {
            try {
                // 백엔드 API 엔드포인트 호출
                // ChatListResponseDto 리스트를 반환하는 /rooms 엔드포인트를 호출합니다.
                const response = await axios.get('http://localhost:3000/rooms');
                console.log('API 응답:', response.data);
                
                // 데이터 상태 업데이트
                setChatRooms(response.data);
            } catch (err) {
                console.error("채팅 목록을 불러오는 중 오류가 발생했습니다:", err);
                // 오류 상태 업데이트
                setError("채팅 목록을 불러오는 데 실패했습니다.");
            } finally {
                // 로딩 상태 해제
                setLoading(false);
            }
        };

        fetchChatRooms();
    }, []); // 빈 배열은 컴포넌트가 처음 렌더링될 때만 실행됨을 의미

    // 3. 채팅방 클릭 이벤트 핸들러
    const handleChatRoomClick = (chatRoomId) => {
        // 클릭 시 해당 채팅방 상세 페이지로 이동
        // URL 경로는 프로젝트 라우팅 설정에 맞게 변경해야 합니다.
        navigate(`/admin/chat/detail/${chatRoomId}`);
    };

    // 4. 조건부 렌더링
    if (loading) {
        return <div className="loading">채팅 목록을 불러오는 중입니다...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

    // 5. 컴포넌트 렌더링
    return (
        <div className="chat-list-container">
            <h1 className="chat-list-title">채팅방 목록</h1>
            {chatRooms.length === 0 ? (
                <div className="no-chat-message">개설된 채팅방이 없습니다.</div>
            ) : (
                <ul className="chat-list">
                    {chatRooms.map((room) => (
                        <li 
                            key={room.chatRoomId} 
                            className="chat-list-item"
                            onClick={() => handleChatRoomClick(room.chatRoomId)}
                        >
                            <div className="chat-info">
                                <div className="member-name">
                                    {room.memberName}
                                    {/* chatcheck가 N(미확인)인 경우 new 표시 */}
                                    {room.hasNewMessage && <span className="new-badge">new</span>}
                                </div>
                                <div className="last-message">{room.lastMessageContent}</div>
                            </div>
                            <div className="chat-meta">
                                <div className="last-message-time">{new Date(room.lastMessageTime).toLocaleString()}</div>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ChatList;