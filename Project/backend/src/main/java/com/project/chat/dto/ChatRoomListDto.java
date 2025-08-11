package com.project.adopt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomListDto {
    private Long chatRoomId;
    private Long memberNum;
    private String memberName;
    private String adminId;
    private String lastChatContent;
    private LocalDateTime lastChatTime;
}