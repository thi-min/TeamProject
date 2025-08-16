package com.project.chat.dto;

import java.time.LocalDateTime;

import com.project.alarm.entity.CheckState;

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
public class ChatMessageResponseDto {
    private Long chatMessageId;
    private Long chatRoomId;
    private Long memberNum;
    private String adminId;
    private LocalDateTime sendTime;
    private String chatCont;
    private CheckState chatCheck;
}