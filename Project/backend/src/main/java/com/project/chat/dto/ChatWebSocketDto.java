package com.project.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatWebSocketDto {
    
    private Long chatRoomNum; // 채팅방 번호
    private Long senderNum;
    private String senderRole;
    private String message;
}