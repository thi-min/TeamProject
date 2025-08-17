import axios from 'axios';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../style/Chat.css'; // 이전에 제공된 스타일 파일을 기준으로 생성합니다.

const ChatList = () => {
  const [chatRooms, setChatRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // API를 통해 채팅방 목록을 가져옵니다.
    const fetchChatRooms = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/chat/rooms'); // 백엔드 API 주소로 변경 필요
        setChatRooms(response.data);
      } catch (err) {
        setError('채팅방 목록을 불러오는 데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchChatRooms();
  }, []);

  const handleRowClick = (chatRoomId) => {
    navigate(`/chat/room/${chatRoomId}`);
  };

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="chat-list-container">
      <h2 className="chat-list-title">입양 문의 1:1 채팅 목록</h2>
      <div className="chat-list-header">
        <div className="chat-list-row-header">
          <div className="chat-list-column-header">번호</div>
          <div className="chat-list-column-header">ID</div>
          <div className="chat-list-column-header">이름</div>
          <div className="chat-list-column-header last-chat-header">마지막 채팅</div>
          <div className="chat-list-column-header">최근 작성일자</div>
        </div>
      </div>
      <div className="chat-list-body">
        {chatRooms.length === 0 ? (
          <div className="no-chat-message">채팅방이 없습니다.</div>
        ) : (
          chatRooms.map((room) => (
            <div 
              key={room.chatRoomId}
              className="chat-list-row"
              onClick={() => handleRowClick(room.chatRoomId)}
            >
              <div className="chat-list-cell">{room.chatRoomId}</div>
              <div className="chat-list-cell">{room.member?.memberId || 'N/A'}</div>
              <div className="chat-list-cell">{room.member?.memberName || 'N/A'}</div>
              <div className="chat-list-cell last-chat-content">
                {/* 마지막 채팅 메시지 및 'New' 표시 (백엔드 응답에 따라 수정 필요) */}
                <span>마지막 메시지 내용...</span>
                {/* {room.hasNewMessage && <span className="chat-list-new-tag">New</span>} */}
              </div>
              <div className="chat-list-cell">
                {/* 날짜 형식 변환 필요 */}
                {new Date(room.createAt).toLocaleDateString()}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ChatList;