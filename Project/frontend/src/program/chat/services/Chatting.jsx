import { useState, useEffect, useRef } from 'react'
import * as StompJs from '@stomp/stompjs'

function Chatting() {
  const [chatList, setChatList] = useState([])
  const [newChat, setNewChat] = useState('')
  const clientRef = useRef(null)
  const [token, setToken] = useState(sessionStorage.getItem('token'))

  useEffect(() => {
    // 1. 클라이언트 객체 생성
    const client = new StompJs.Client({
      brokerURL: 'ws://localhost:3000/ws',
      connectHeaders: {
        Authorization: `${token}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })

    // 2. 클라이언트 활성화 로직
    client.onConnect = function (frame) {
      const callback = function (message) {
        if (message.body) {
          const body = JSON.parse(message.body)
          setChatList(prev => [...prev, body])
        }
      }
      // 백엔드 설정에 맞춰 '/sub' 접두사 추가
      client.subscribe('/sub/queue/test', callback) 
    }

    // 3. 브로커에서 에러 발생 시 호출되는 함수
    client.onStompError = function (frame) {
      console.log(`Broker reported error: ${frame.headers.message}`)
      console.log(`Additional details: ${frame.body}`)
      if (
        frame.headers.message ===
        'Failed to send message to ExecutorSubscribableChannel[clientInboundChannel]'
      ) {
        // 가지고 있던 리프레시 토큰으로 새 엑세스 토큰을 발급받아
        // 세션 스토리지에 저장하고,
        // setToken으로 token 상태 업데이트.
      }
    }

    // 클라이언트 활성화
    client.activate()
    clientRef.current = client

    // 4. 컴포넌트 언마운트 시 클라이언트 비활성화
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate()
      }
    }
  }, [token])

  const handleChange = event => {
    setNewChat(event.target.value)
  }

  const handleSubmit = event => {
    event.preventDefault()
    if (newChat.trim() !== '') {
      const newChatObj = { message: newChat }
      const msg = JSON.stringify(newChatObj)
      // 5. 메시지 보내기(퍼블리시)
      if (clientRef.current) {
        // 백엔드 설정에 맞춰 '/pub' 접두사 추가
        clientRef.current.publish({
          destination: '/pub/topic/general', 
          headers: {
            Authorization: `${token}`,
          },
          body: msg,
        })
      }
    }
    setNewChat('')
    event.target.reset()
  }

  return (
    <>
      <div>
        {chatList.map(chat => (
          <div key={crypto.randomUUID()}>
            <span>{chat.message}</span>
          </div>
        ))}
      </div>
      <form onSubmit={handleSubmit}>
        <input
          type='text'
          placeholder='메시지를 입력하세요'
          value={newChat}
          onChange={handleChange}
        />
        <button type='submit'>send</button>
      </form>
    </>
  )
}

export default Chatting