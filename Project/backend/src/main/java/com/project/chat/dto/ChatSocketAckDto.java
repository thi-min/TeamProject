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
public class ChatSocketAckDto {
    private Long chatMessageId;
    private Long chatRoomId;
    private Long memberNum;
    private String adminId;
    private String content;
    private LocalDateTime sendTime;
    private boolean delivered;
}