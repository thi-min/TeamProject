package com.project.adopt.dto;

import com.project.common.enums.CheckState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long chatMessageId;
    private Long chatRoomId;
    private Long memberNum;
    private String memberName;
    private String adminId;
    private LocalDateTime sendTime;
    private String chatCont;
    private CheckState chatCheck;
}