package com.project.chat.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private Long chatRoomId;
    private String memberName; // 회원이름
    private String lastMessageContent; // 마지막 채팅 내용
    private LocalDateTime lastMessageTime; // 마지막 채팅 시간
    private boolean hasNewMessage; // 채팅방에 미확인 메시지가 있는지 여부 (ChatCheck.N)
}