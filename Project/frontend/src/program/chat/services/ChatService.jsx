import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

let stompClient = null;

const ChatService = {
  /**
   * WebSocket 및 STOMP 연결을 시작합니다.
   * @param {string} token - JWT 토큰
   * @param {function} onConnectCallback - 연결 성공 시 실행될 콜백 함수
   * @param {function} onErrorCallback - 연결 실패 시 실행될 콜백 함수
   */
  connect: (token, onConnectCallback, onErrorCallback) => {
    if (stompClient && stompClient.connected) {
      console.log("STOMP 클라이언트가 이미 연결되어 있습니다.");
      if (onConnectCallback) onConnectCallback();
      return;
    }

    // SockJS를 사용하여 WebSocket 연결
    // const socket = new SockJS(`http://192.168.0.115:8090/ws`);
    // stompClient = Stomp.over(socket);
    stompClient = Stomp.over(() => {
      return new SockJS(`http://127.0.0.1:8090/ws`);
    });
    // 디버그 메시지 비활성화 (필요하다면)
    // stompClient.debug = () => {};

    // stompClient.connect()의 첫 번째 인자로 헤더를 전달
    const headers = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    stompClient.connect(
      headers,
      frame => {
        console.log("STOMP 연결 성공:", frame);
        onConnectCallback();
      },
      error => {
        console.error("STOMP 연결 실패:", error);
        onErrorCallback(error);
      }
    );
  },

  /**
   * 특정 채팅방을 구독하여 실시간 메시지를 수신합니다.
   * @param {string} chatRoomNum - 구독할 채팅방 번호
   * @param {function} onMessageReceived - 메시지 수신 시 실행될 콜백 함수
   */
  subscribe: (chatRoomNum, onMessageReceived) => {
    if (!stompClient || !stompClient.connected) {
      console.error("STOMP 클라이언트가 연결되지 않았습니다.");
      return;
    }
    const destination = `/sub/chat/detail/${chatRoomNum}`;
    stompClient.subscribe(destination, message => {
      onMessageReceived(JSON.parse(message.body));
    });
  },

  /**
   * 메시지를 서버로 전송합니다.
   * @param {Object} chatDto - 전송할 메시지 객체 (chatRoomNum, message, senderNum, senderRole 포함)
   */
  sendMessage: (chatDto) => {
    if (!stompClient || !stompClient.connected) {
      console.error("STOMP 클라이언트가 연결되지 않았습니다.");
      return;
    }
    // 백엔드에서 @MessageMapping("/chat/message")를 사용하는 경우
    const destination = "/pub/chat/message";
    stompClient.send(destination, {}, JSON.stringify(chatDto));
  },

  /**
   * STOMP 연결을 종료합니다.
   */
  disconnect: () => {
    if (stompClient) {
      stompClient.disconnect(() => {
        console.log("STOMP 클라이언트 연결 종료");
      });
    }
  }
};

export default ChatService;