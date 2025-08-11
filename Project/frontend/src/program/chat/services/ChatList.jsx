
export default function ChatList({ conversations, selectedId, onSelect }) {
  return (
    <div className="chat-list">
      <table className="chat-list-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>ID</th>
            <th>이름</th>
            <th>마지막 채팅</th>
            <th>최근 작성일자</th>
          </tr>
        </thead>
        <tbody>
          {conversations.map((c, idx) => (
            <tr
              key={c.id}
              className={c.id === selectedId ? "selected" : ""}
              onClick={() => onSelect(c.id)}
            >
              <td>{idx + 1}</td>
              <td>{c.email}</td>
              <td>
                {c.name} {c.new ? <span className="badge">New</span> : null}
              </td>
              <td className="last-msg">{c.lastMessage}</td>
              <td>{c.lastDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}