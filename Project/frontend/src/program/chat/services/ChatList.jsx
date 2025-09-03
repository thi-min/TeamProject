// Project/frontend/src/program/chat/ChatList.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../../../common/api/axios.js";
import "../style/Chat.css";

const ChatList = () => {
    const navigate = useNavigate();
    const [chatRooms, setChatRooms] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    const authAxios = api.create({
        baseURL: 'http://127.0.0.1:8090/',
        headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    });

    const fetchChatRooms = async () => {
        setLoading(true);
        try {
            const response = await authAxios.get("/api/chat/admin/list", {
                params: { page: 0, size: 20 }
            });
            setChatRooms(response.data.content);
        } catch (e) {
            console.error("채팅방 목록 로딩 실패:", e);
            setMessage("채팅방 목록을 불러오는 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchChatRooms();
    }, []);

    if (loading) return <div>로딩 중...</div>;
    if (message) return <div>{message}</div>;

    return (
        <div>
            <h3>채팅방 관리</h3>
            <table className="table type2 responsive border">
                <thead>
                    <tr>
                        <th>채팅방 번호</th>
                        <th>입양자명</th>
                        <th>마지막 메시지</th>
                        <th>마지막 메시지 시간</th>
                    </tr>
                </thead>
                <tbody className="text_center">
                    {chatRooms.length > 0 ? (
                        chatRooms.map((room) => (
                            <tr
                                key={room.chatRoomNum}
                                onClick={() => navigate(`/admin/chat/detail/${room.chatRoomNum}`)}
                                style={{ cursor: 'pointer' }}
                            >
                                <td>{room.chatRoomNum}</td>
                                <td>{room.memberName}</td>
                                <td>{room.lastMessage}</td>
                                <td>{new Date(room.lastMessageTime).toLocaleString()}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="4" className="text-center">채팅방이 없습니다.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>

    );
};

export default ChatList;
