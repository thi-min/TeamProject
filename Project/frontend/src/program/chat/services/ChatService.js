// src/program/chat/services/ChatService.js

const API_BASE_URL = 'http://localhost:8080/api/chat';

/**
 * 특정 회원의 채팅방을 조회하거나 새로 생성합니다.
 * 백엔드의 POST /api/chat/room/{memberNum} 엔드포인트와 통신합니다.
 * @param {number} memberNum - 회원의 고유 번호
 * @returns {Promise<Object>} 채팅방 정보를 담은 객체
 */
export const findOrCreateChatRoom = async (memberNum) => {
    try {
        const response = await fetch(`${API_BASE_URL}/room/${memberNum}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        if (!response.ok) {
            throw new Error('네트워크 응답이 올바르지 않습니다.');
        }
        return await response.json();
    } catch (error) {
        console.error('채팅방 조회 또는 생성 중 오류 발생:', error);
        return null;
    }
};

/**
 * 특정 채팅방의 메시지 목록을 조회합니다.
 * 백엔드의 GET /api/chat/messages/{roomNum} 엔드포인트와 통신합니다.
 * @param {number} roomNum - 채팅방의 고유 번호
 * @returns {Promise<Array<Object>>} 메시지 목록을 담은 배열
 */
export const getChatMessages = async (roomNum) => {
    try {
        const response = await fetch(`${API_BASE_URL}/messages/${roomNum}`);
        if (!response.ok) {
            throw new Error('네트워크 응답이 올바르지 않습니다.');
        }
        return await response.json();
    } catch (error) {
        console.error('메시지 목록 조회 중 오류 발생:', error);
        return [];
    }
};