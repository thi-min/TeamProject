import { Stomp } from '@stomp/stompjs';
import axios from 'axios';
import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';
import '../style/Chat.css'; // 이전에 제공된 스타일 파일을 기준으로 생성합니다.

const ChatRoom = () => {
  const { roomId } = useParams();
  const navigate = useNavigate();
  const [messages, setMessages] = useState([]);
  const [messageInput, setMessageInput] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const messagesEndRef = useRef(null);

  const MEMBER_NUM = 1; // 실제로는 로그인한 회원의 번호를 사용해야 합니다.
  const ADMIN_ID = 'admin1'; // 실제로는 로그인한 관리자 ID를 사용해야 합니다.
  const CHAT_ROOM_ID = roomId;

  useEffect(() => {
    // 기존 메시지 불러오기
    const fetchMessages = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/chat/rooms/${CHAT_ROOM_ID}/messages`);
        setMessages(response.data);
      } catch (error) {
        console.error('기존 메시지를 불러오는 데 실패했습니다.', error);
      }
    };
    
    fetchMessages();

    // STOMP 클라이언트 설정 및 연결
    const socket = new SockJS('http://localhost:8080/stomp-websocket'); // WebSocket 엔드포인트
    const client = Stomp.over(socket);
    
    client.connect({}, () => {
      setIsConnected(true);
      setStompClient(client);

      // 메시지 수신용 토픽 구독
      client.subscribe(`/topic/chat/${CHAT_ROOM_ID}`, (message) => {
        const receivedMessage = JSON.parse(message.body);
        setMessages(prevMessages => [...prevMessages, receivedMessage]);
      });
    }, (error) => {
      console.error('STOMP 연결 실패:', error);
      setIsConnected(false);
    });

    // 컴포넌트 언마운트 시 연결 해제
    return () => {
      if (client.connected) {
        client.disconnect();
      }
    };
  }, [CHAT_ROOM_ID]);

  useEffect(() => {
    // 새 메시지 수신 시 스크롤을 맨 아래로 이동
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSendMessage = () => {
    if (messageInput.trim() && isConnected) {
      const messageDto = {
        chatRoomId: CHAT_ROOM_ID,
        memberNum: null, // 관리자가 보낼 때는 memberNum이 null
        adminId: ADMIN_ID,
        chatCont: messageInput
      };
      
      stompClient.send(`/app/chat.send`, {}, JSON.stringify(messageDto));
      setMessageInput('');
    }
  };

  const formatTime = (isoString) => {
    const date = new Date(isoString);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  };

  return (
    <div className="chat-room-container">
      <div className="chat-room-header">
        <h2 className="chat-room-title">1:1 대화</h2>
      </div>
      <div className="chat-messages-area">
        <div className="chat-messages">
          {messages.length === 0 ? (
            <div className="no-messages">대화 내용이 없습니다.</div>
          ) : (
            messages.map((msg, index) => (
              <div 
                key={index} 
                className={`chat-message-row ${msg.adminId ? 'admin-message' : 'member-message'}`}
              >
                <div className="chat-message-bubble">
                  <div className="chat-message-content">{msg.chatCont}</div>
                  <div className="chat-message-time">{formatTime(msg.sendTime)}</div>
                </div>
              </div>
            ))
          )}
          <div ref={messagesEndRef} />
        </div>
      </div>
      <div className="chat-input-area">
        <input
          type="text"
          value={messageInput}
          onChange={(e) => setMessageInput(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
          className="chat-input"
          placeholder="메시지를 입력하세요."
        />
        <button 
          onClick={handleSendMessage}
          className="chat-send-button"
        >
          채팅하기
        </button>
      </div>
    </div>
  );
};

export default ChatRoom;