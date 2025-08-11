import { useEffect, useRef, useState } from "react";

export default function ChatConversation({ conversation, onSend }) {
  const [text, setText] = useState("");
  const scrollRef = useRef(null);

  useEffect(() => {
    // 스크롤 맨 아래로 자동 이동
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [conversation]);

  if (!conversation) {
    return <div className="chat-empty">대화를 선택하세요.</div>;
  }

  const handleSend = () => {
    if (!text.trim()) return;
    onSend(conversation.id, text.trim());
    setText("");
  };

  const onKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="chat-conversation">
      <div className="conv-header">
        <div className="conv-title">{conversation.name}</div>
        <div className="conv-email">{conversation.email}</div>
      </div>

      <div className="messages" ref={scrollRef}>
        {conversation.messages.map((m) => (
          <div key={m.id} className={`message-row ${m.from === "me" ? "me" : "other"}`}>
            <div className="bubble">{m.text}</div>
            <div className="time">{m.time}</div>
          </div>
        ))}
      </div>

      <div className="chat-input-area">
        <textarea
          placeholder="메시지를 입력하세요"
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={onKeyDown}
          rows={2}
        />
        <button onClick={handleSend} className="send-btn">전송</button>
      </div>
    </div>
  );
}