package com.project.adopt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket을 통해 주고받는 메시지 페이로드 표준형
 * - client -> server: content, chatRoomId, sender info
 * - server -> client: content, chatRoomId, sender info, sendTime
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSocketPayload {
    private Long chatRoomId;
    private Long memberNum;    // 전송자(회원) id (null 가능)
    private String adminId;    // 전송자(관리자) id (null 가능)
    private String content;
    private LocalDateTime sendTime; // 서버에서 채팅을 저장할 때 채워짐
}