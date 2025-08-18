import { useEffect, useState } from "react";
import "./Chat.css";
import ChatConversation from "./ChatConversation";
import sampleConversations from "./chatData";
import ChatList from "./ChatList";

const STORAGE_KEY = "chat_read_status_v1";

export default function ChatPage() {
  const [conversations, setConversations] = useState([]);
  const [selectedId, setSelectedId] = useState(null);

  // 초기 로드: 샘플 데이터 + localStorage에서 read 상태 반영
  useEffect(() => {
    const persisted = JSON.parse(localStorage.getItem(STORAGE_KEY) || "{}");
    const initialized = sampleConversations.map((c) => ({
      ...c,
      new: persisted[c.id] === false ? false : c.new,
    }));
    setConversations(initialized);
    if (initialized.length > 0) setSelectedId(initialized[0].id);
  }, []);

  useEffect(() => {
    // 대화 선택시 new 배지 제거(로컬에 저장)
    if (selectedId == null) return;
    setConversations((prev) =>
      prev.map((c) => (c.id === selectedId ? { ...c, new: false } : c))
    );
    const persisted = JSON.parse(localStorage.getItem(STORAGE_KEY) || "{}");
    persisted[selectedId] = false;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(persisted));
  }, [selectedId]);

  const handleSelect = (id) => {
    setSelectedId(id);
  };

  const handleSend = (conversationId, text) => {
    // 간단하게 새로운 메시지 추가(서버 통신 대신)
    setConversations((prev) =>
      prev.map((c) => {
        if (c.id !== conversationId) return c;
        const now = new Date();
        const hhmm = `${String(now.getHours()).padStart(2, "0")}:${String(
          now.getMinutes()
        ).padStart(2, "0")}`;
        const newMsg = { id: `m${Date.now()}`, from: "me", text, time: hhmm };
        return {
          ...c,
          messages: [...c.messages, newMsg],
          lastMessage: text,
          lastDate: now.toISOString().slice(0, 10),
        };
      })
    );
    // (옵션) 서버로 전송할 시 이 부분에서 fetch/send
  };

  const selectedConv = conversations.find((c) => c.id === selectedId) || null;

  return (
    <div className="chat-page">
      <div className="left-pane">
        <div className="controls">
          <button onClick={() => alert("채팅하기 버튼(임시) 클릭")}>채팅하기</button>
        </div>
        <ChatList
          conversations={conversations}
          selectedId={selectedId}
          onSelect={handleSelect}
        />
      </div>
      <div className="right-pane">
        <ChatConversation conversation={selectedConv} onSend={handleSend} />
      </div>
    </div>
  );
}